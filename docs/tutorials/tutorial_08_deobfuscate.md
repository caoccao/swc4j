# Tutorial 08: Deobfuscate

In this tutorial, we are going to learn the following.

* How to write a JavaScript deobfuscate plugin.
* How to evaluate values from AST nodes.

## Is it possible to decode obfuscated JavaScript code with swc4j?

That is a challenging question. The answer is YES.

Let's take a try against the famous obfuscator [JSFK](https://jsfuck.com/) that is an esoteric and educational programming style based on the atomic parts of JavaScript. It uses only six different characters `[]()+!` to write and execute code.

## Preparation

### Script

* Visit [JSFK](https://jsfuck.com/).
* Uncheck `Eval Source`.
* Input `1+1` in the first textbox.
* Click button `Encode`.
* We get the obfuscated JavaScript code `[+!+[]]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]])+[])[!+[]+!+[]]+[+!+[]]`.

### Options

As usual, the following code is written for preparing an options.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a JavaScript code snippet.
String code = "[+!+[]]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]])+[])[!+[]+!+[]]+[+!+[]]"; // 1+1
// Prepare a script name.
URL specifier = new URL("file:///abc.ts");
// Prepare a transform options.
Swc4jTransformOptions options = new Swc4jTransformOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.JavaScript)
        .setOmitLastSemi(true)
        .setInlineSources(false)
        .setSourceMap(Swc4jSourceMapOption.None);
```

### Plugin Host

Let's create a simple plugin host that takes a visitor, and processes the program as follows. It'll help simplify the test.

```java
public class Swc4jPluginHostDeobfuscator implements ISwc4jPluginHost {
    private final ISwc4jAstVisitor visitor;

    public Swc4jPluginHostDeobfuscator(ISwc4jAstVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public boolean process(ISwc4jAstProgram<?> program) {
        program.visit(visitor);
        return true;
    }
}
```

### Method Deobfuscate

As the test is going to be iterative, let's create a method `deobfuscate()` which will be reused in those iterations.

`deobfuscate()`  calls `transform()` repeatedly until the transformed code is stable (not changed any more), and evaluates the final transformed code in [Javet](https://github.com/caoccao/Javet) which is a JavaScript V8 engine embedded in JVM.

```java
private static void deobfuscate(
        Swc4j swc4j,
        Swc4jTransformOptions options,
        ISwc4jPluginHost pluginHost,
        String code) throws Swc4jCoreException, JavetException {
    options.setPluginHost(pluginHost);
    System.out.println("/*********************************************");
    System.out.println("       The transformed code is as follows.");
    System.out.println("*********************************************/");
    // Transform the code.
    Swc4jTransformOutput output;
    while (true) {
        output = swc4j.transform(code, options);
        if (code.equals(output.getCode())) {
            break;
        }
        code = output.getCode();
        System.out.println(code);
    }
    System.out.println("/*********************************************");
    System.out.println("       The evaluated result in V8.");
    System.out.println("*********************************************/");
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        System.out.println(v8Runtime.getExecutor(output.getCode()).executeString());
    }
}
```

## Version 1: Unary Expression

JSFK relies on 2 unary operators `!` and `+` to transform JavaScript code to unary expressions. E.g.

```js
false       =>  ![]
true        =>  !![]
0           =>  +[]
```

Let's write a visitor V1 that evaluates the unary expression as follows.

```java
public static class DeobfuscatorVisitorV1 extends Swc4jAstVisitor {
    @Override
    public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
        ISwc4jAstExpr arg = node.getArg().unParenExpr();
        Optional<ISwc4jAst> newNode = Optional.empty();
        switch (node.getOp()) {
            case Bang:
                switch (arg.getType()) {
                    case ArrayLit:
                    case ObjectLit:
                        // ![] => false, !{} => false
                        newNode = Optional.of(Swc4jAstBool.create(false));
                        break;
                    case Bool:
                    case Number:
                    case Str:
                        // !true => false, !false => true, !0 => true, !0.1 => false, !'' => true, !'false' => false
                        newNode = Optional.of(Swc4jAstBool.create(!arg.as(ISwc4jAstCoercionPrimitive.class).asBoolean()));
                        break;
                    default:
                        break;
                }
                break;
            case Plus:
                switch (arg.getType()) {
                    case ArrayLit:
                        // +[] => 0, +[1] => 1, +[1,2] => NaN
                        newNode = Optional.of(Swc4jAstNumber.create(arg.as(Swc4jAstArrayLit.class).asDouble()));
                        break;
                    case Bool:
                        // +true => 1, +false => 0
                        newNode = Optional.of(Swc4jAstNumber.create(arg.as(ISwc4jAstCoercionPrimitive.class).asInt()));
                        break;
                    case Ident:
                        if (arg.isNaN() || arg.isUndefined()) {
                            // +NaN => NaN, +undefined => NaN
                            newNode = Optional.of(Swc4jAstNumber.createNaN());
                        } else if (arg.isInfinity()) {
                            // +Infinity => Infinity
                            newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                        }
                        break;
                    case Number:
                        // +1 => 1, +1e22 => 1e+22
                        Swc4jAstNumber number = arg.as(Swc4jAstNumber.class);
                        newNode = Optional.of(Swc4jAstNumber.create(number.getValue(), number.getRaw().orElse(null)));
                        break;
                    case ObjectLit:
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                        break;
                    case Str:
                        // +'  ' => 0, +' 0.1 ' => 0.1, +'a' => NaN
                        String stringValue = arg.as(Swc4jAstStr.class).getValue().trim();
                        double doubleValue;
                        if (StringUtils.isEmpty(stringValue)) {
                            doubleValue = 0;
                        } else {
                            try {
                                doubleValue = Double.parseDouble(stringValue);
                            } catch (Throwable t) {
                                doubleValue = Double.NaN;
                            }
                        }
                        newNode = Optional.of(Swc4jAstNumber.create(doubleValue));
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        newNode.ifPresent(n -> node.getParent().replaceNode(node, n));
        return super.visitUnaryExpr(node);
    }
}
```

The implementation might be quite a bit complicated. Actually it strictly follows the JavaScript specification, acting as a script execution engine that evaluates the code at AST level.

Let's run the V1.

```java
deobfuscate(swc4j, options, new Swc4jPluginHostDeobfuscator(new DeobfuscatorVisitorV1()), code);
```

The output is as follows.

```js
/*********************************************
       The transformed code is as follows.
*********************************************/
[+!0]+(+(+!0+(!0+[])[!0+!0+!0]+[+!0]+[0]+[0])+[])[!0+!0]+[+!0]
[+true]+(+(+true+(true+[])[true+true+true]+[+true]+[0]+[0])+[])[true+true]+[+true]
[1]+(+(1+(true+[])[true+true+true]+[1]+[0]+[0])+[])[true+true]+[1]
/*********************************************
       The evaluated result in V8.
*********************************************/
1+1
```

It shows there are 3 iterations til the transformed code is stable.

Let's focus on the first 7 characters `[+!+[]]` to see how visitor V1 works.

```js
[+!+[]] // Original code
[+!0]   // Iteration 1
[+true] // Iteration 2
[1]     // Iteration 3
```

In the end of the transformation, there are no unary expression any more, but a lot of binary expressions.

## Version 2: Binary Expression

JSFK only relies on 1 binary operator `+` in the transformation. E.g.

```js
1           =>  +!+[]
2           =>  !+[]+!+[]
10          =>  [+!+[]]+[+[]]
```

Let's write a visitor V2 that inherits the visitor V1 and evaluates the binary expression as follows.

```java
public static class DeobfuscatorVisitorV2 extends DeobfuscatorVisitorV1 {
    @Override
    public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
        Optional<ISwc4jAst> newNode = Optional.empty();
        ISwc4jAstExpr left = node.getLeft().unParenExpr();
        ISwc4jAstExpr right = node.getRight().unParenExpr();
        Swc4jAstType leftType = left.getType();
        Swc4jAstType rightType = right.getType();
        switch (node.getOp()) {
            case Add:
                if (leftType.isArrayLit() && !left.as(Swc4jAstArrayLit.class).isAllPrimitive()) {
                    break;
                }
                if (rightType.isArrayLit() && !right.as(Swc4jAstArrayLit.class).isAllPrimitive()) {
                    break;
                }
                if ((leftType.isBool() && rightType.isNumber()) ||
                        (leftType.isBool() && rightType.isBool()) ||
                        (leftType.isNumber() && rightType.isBool()) ||
                        (leftType.isNumber() && rightType.isNumber())) {
                    // true+0 => 1, 0+false => 0, true+true => 2, 1+1 => 2
                    double value = left.as(ISwc4jAstCoercionPrimitive.class).asDouble()
                            + right.as(ISwc4jAstCoercionPrimitive.class).asDouble();
                    newNode = Optional.of(Swc4jAstNumber.create(value));
                } else if ((leftType.isBool() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isBool()) ||
                        (leftType.isArrayLit() && rightType.isArrayLit()) ||
                        (leftType.isStr() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isStr()) ||
                        (leftType.isStr() && rightType.isStr()) ||
                        (leftType.isRegex() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isStr()) ||
                        (leftType.isStr() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isBool()) ||
                        (leftType.isBool() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isNumber()) ||
                        (leftType.isNumber() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isRegex()) ||
                        (leftType.isStr() && rightType.isNumber()) ||
                        (leftType.isStr() && rightType.isBool()) ||
                        (leftType.isBool() && rightType.isStr()) ||
                        (leftType.isArrayLit() && rightType.isNumber()) ||
                        (leftType.isNumber() && rightType.isStr()) ||
                        (leftType.isNumber() && rightType.isArrayLit())) {
                    // true+[] => 'true', true+'' => 'true', [0]+/a/i => '0/a/i'
                    String value = left.as(ISwc4jAstCoercionPrimitive.class).asString()
                            + right.as(ISwc4jAstCoercionPrimitive.class).asString();
                    newNode = Optional.of(Swc4jAstStr.create(value));
                } else if (left.isNaN()) {
                    if (rightType.isNumber() || rightType.isBool()) {
                        // NaN+0 => NaN, NaN+true => NaN
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    } else if (rightType.isStr() || rightType.isArrayLit()) {
                        // NaN+'a' => 'NaNa', NaN+['a','b'] => 'NaNa,b'
                        newNode = Optional.of(Swc4jAstStr.create(ISwc4jConstants.NAN + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                    } else if (rightType.isMemberExpr()) {
                        newNode = right.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(rightString -> Swc4jAstStr.create(ISwc4jConstants.NAN + rightString));
                    } else if (right.isNaN() || right.isUndefined() || right.isInfinity()) {
                        // NaN+NaN => NaN, NaN+undefined => NaN, NaN+Infinity => NaN
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (left.isInfinity()) {
                    if (rightType.isNumber() || rightType.isBool()) {
                        // Infinity+0 => Infinity, Infinity+true => Infinity
                        newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (rightType.isStr() || rightType.isArrayLit()) {
                        // Infinity+'a' => 'Infinitya', Infinity+[0] => 'Infinity0'
                        newNode = Optional.of(Swc4jAstStr.create(ISwc4jConstants.INFINITY + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                    } else if (rightType.isMemberExpr()) {
                        newNode = right.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(rightString -> Swc4jAstStr.create(ISwc4jConstants.INFINITY + rightString));
                    } else if (right.isInfinity()) {
                        // Infinity+Infinity => Infinity
                        newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (right.isNaN() || right.isUndefined()) {
                        // Infinity+NaN => NaN, Infinity+undefined => NaN
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (left.isUndefined()) {
                    if (rightType.isNumber() || rightType.isBool()) {
                        // undefined+0 => NaN, undefined+true => NaN
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    } else if (rightType.isStr() || rightType.isArrayLit()) {
                        // undefined+'a' = 'undefineda', undefined+[0] => 'undefined0'
                        newNode = Optional.of(Swc4jAstStr.create(ISwc4jConstants.UNDEFINED + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                    } else if (right.isNaN() || right.isUndefined() || right.isInfinity()) {
                        // undefined+NaN => NaN, undefined+undefined => NaN, undefined+Infinity => NaN
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (right.isNaN()) {
                    if (leftType.isNumber() || leftType.isBool()) {
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    } else if (leftType.isStr() || leftType.isArrayLit()) {
                        newNode = Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.NAN));
                    } else if (leftType.isMemberExpr()) {
                        newNode = left.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(leftString -> Swc4jAstStr.create(leftString + ISwc4jConstants.NAN));
                    } else if (left.isNaN() || left.isUndefined() || left.isInfinity()) {
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (right.isInfinity()) {
                    if (leftType.isNumber() || leftType.isBool()) {
                        newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (leftType.isStr() || leftType.isArrayLit()) {
                        newNode = Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.INFINITY));
                    } else if (leftType.isMemberExpr()) {
                        newNode = left.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(leftString -> Swc4jAstStr.create(leftString + ISwc4jConstants.INFINITY));
                    } else if (left.isInfinity()) {
                        newNode = Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (left.isNaN() || left.isUndefined()) {
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (right.isUndefined()) {
                    if (leftType.isNumber() || leftType.isBool()) {
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    } else if (leftType.isStr() || leftType.isArrayLit()) {
                        newNode = Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.UNDEFINED));
                    } else if (left.isNaN() || left.isUndefined() || left.isInfinity()) {
                        newNode = Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isCallExpr()) {
                    newNode = right.as(Swc4jAstCallExpr.class).eval()
                            .map(n -> left.as(ISwc4jAstCoercionPrimitive.class).asString() + n)
                            .map(Swc4jAstStr::create);
                } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isCallExpr()) {
                    newNode = left.as(Swc4jAstCallExpr.class).eval()
                            .map(n -> n + right.as(ISwc4jAstCoercionPrimitive.class).asString())
                            .map(Swc4jAstStr::create);
                } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isMemberExpr()) {
                    newNode = right.as(Swc4jAstMemberExpr.class).evalAsString()
                            .map(rightString -> left.as(ISwc4jAstCoercionPrimitive.class).asString() + rightString)
                            .map(Swc4jAstStr::create);
                } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isMemberExpr()) {
                    newNode = left.as(Swc4jAstMemberExpr.class).evalAsString()
                            .map(leftString -> leftString + right.as(ISwc4jAstCoercionPrimitive.class).asString())
                            .map(Swc4jAstStr::create);
                }
                break;
            default:
                break;
        }
        newNode.ifPresent(n -> node.getParent().replaceNode(node, n));
        return super.visitBinExpr(node);
    }
}
```

The implementation might be quite verbose and boring. Well, that conveys the 20+ years of history that we have to respect.

Let's run the V2.

```java
deobfuscate(swc4j, options, new Swc4jPluginHostDeobfuscator(new DeobfuscatorVisitorV2()), code);
```

The output is as follows.

```js
/*********************************************
       The transformed code is as follows.
*********************************************/
[+!0]+(+(+!0+(!0+[])[!0+!0+!0]+[+!0]+[0]+[0])+[])[!0+!0]+[+!0]
[+true]+(+(+true+(true+[])[true+true+true]+[+true]+[0]+[0])+[])[true+true]+[+true]
[1]+(+(1+("true")[2+true]+[1]+[0]+[0])+[])[2]+[1]
[1]+(+(1+("true")[3]+[1]+[0]+[0])+[])[2]+[1]
```

It shows there are 4 iterations til the transformed code is stable.

Let's focus on `[!+[]+!+[]+!+[]]` to see how visitor V2 works.

```js
[!+[]+!+[]+!+[]] // Original code
[!0+!0+!0]       // Iteration 1
[true+true+true] // Iteration 2
[2+true]         // Iteration 3
[3]              // Iteration 4
```

In the end of the transformation, there are still a lot of binary expressions, but the deobfuscation is blocked by `("true")[3]` which is a member expression we haven't visited yet.

## Version 3: Member Expression

JSFK relies on member expression to extract particular characters from strings. E.g.

```js
'function forEach() { [native code] }'[0]      => 'f'
'function forEach() { [native code] }'['0']    => 'f'
```

Let's write a visitor V3 that inherits the visitor V2 and evaluates the member expression as follows.

```java
public static class DeobfuscatorVisitorV3 extends DeobfuscatorVisitorV2 {
    @Override
    public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
        Optional<ISwc4jAst> newNode = Optional.empty();
        ISwc4jAstExpr obj = node.getObj().unParenExpr();
        ISwc4jAstMemberProp prop = node.getProp();
        switch (obj.getType()) {
            case Str:
                if (prop instanceof Swc4jAstComputedPropName) {
                    Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
                    ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                    String value = obj.as(Swc4jAstStr.class).getValue();
                    switch (expr.getType()) {
                        case Number: {
                            // 'string'[0] => 's'
                            int index = expr.as(Swc4jAstNumber.class).asInt();
                            if (index >= 0 && index < value.length()) {
                                value = value.substring(index, index + 1);
                                newNode = Optional.of(Swc4jAstStr.create(value));
                            }
                            break;
                        }
                        case Str: {
                            // 'string'['0'] => 's'
                            Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                            if (StringUtils.isDigit(str.getValue())) {
                                int index = str.asInt();
                                if (index >= 0 && index < value.length()) {
                                    value = value.substring(index, index + 1);
                                    newNode = Optional.of(Swc4jAstStr.create(value));
                                }
                            }
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
        newNode.ifPresent(n -> node.getParent().replaceNode(node, n));
        return super.visitMemberExpr(node);
    }
}
```

The implementation is not that long this time. Basically, it works as [String.prototype.at()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/at) at AST level.

Let's run the V3.

```java
deobfuscate(swc4j, options, new Swc4jPluginHostDeobfuscator(new DeobfuscatorVisitorV3()), code);
```

The output is as follows.

```js
/*********************************************
       The transformed code is as follows.
*********************************************/
[+!0]+(+(+!0+(!0+[])[!0+!0+!0]+[+!0]+[0]+[0])+[])[!0+!0]+[+!0]
[+true]+(+(+true+(true+[])[true+true+true]+[+true]+[0]+[0])+[])[true+true]+[+true]
[1]+(+(1+("true")[2+true]+[1]+[0]+[0])+[])[2]+[1]
[1]+(+(1+("true")[3]+[1]+[0]+[0])+[])[2]+[1]
[1]+(+(1+"e"+[1]+[0]+[0])+[])[2]+[1]
[1]+(+("1e"+[1]+[0]+[0])+[])[2]+[1]
[1]+(+("1e1"+[0]+[0])+[])[2]+[1]
[1]+(+("1e10"+[0])+[])[2]+[1]
[1]+(+("1e100")+[])[2]+[1]
[1]+(1e100+[])[2]+[1]
[1]+("1e+100")[2]+[1]
[1]+"+"+[1]
"1+"+[1]
"1+1"
/*********************************************
       The evaluated result in V8.
*********************************************/
1+1
```

Amazing! It takes 14 iterations and finally works.

## Final Version

The V3 is able to handle some simple cases like `1+1`, but still cannot handle many cases as follows.

```js
([]["entries"]()+"")[2]                                         => 'b'
Function("return escape")()(("")["italics"]())[2]               => 'C'
(NaN+Object()["to"+String["name"]]["call"]())[11]               => 'U'
```

Those cases rely on call expression and `Function()` to evaluate a string as code.

Can swc4j handle all those cases? Yes, it is supported by the built-in `Swc4jPluginHostJsFuckDecoder`. The complete implementation is very long and not suitable for this document.

Let's run the final version.

```java
deobfuscate(swc4j, options, new Swc4jPluginHostJsFuckDecoder(), code);
```

The output is as follows.

```js
/*********************************************
       The transformed code is as follows.
*********************************************/
"1+1"
/*********************************************
       The evaluated result in V8.
*********************************************/
1+1
```

As we can see, `Swc4jPluginHostJsFuckDecoder` delivers the transformed code in 1 pass which means there is no intermediate iterations any more so that the performance is much better.

It calls `Swc4jPluginVisitorJsFuckDecoder` internally. And the source code of `Swc4jPluginVisitorJsFuckDecoder` is very simple as follows.

```java
public class Swc4jPluginVisitorJsFuckDecoder extends Swc4jAstVisitor {
    protected AtomicInteger counter;

    public Swc4jPluginVisitorJsFuckDecoder() {
        counter = new AtomicInteger();
    }

    public int getCount() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }

    @Override
    public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitBinExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitCallExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitMemberExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
        node.eval().ifPresent(n -> {
            counter.incrementAndGet();
            node.getParent().replaceNode(node, n);
        });
        return super.visitUnaryExpr(node);
    }
}
```

There are just 4 visit methods with exactly the same body, and the rest of evaluation features are built in swc4j.

## Conclusion

In this tutorial we've learned how to write a JavaScript deobfuscate plugin. It's possible to implement all kinds of JavaScript obfuscators or deobfuscators with the power of swc4j plugin system.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial08Deobfuscate.java).
