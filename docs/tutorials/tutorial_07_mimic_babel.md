# Tutorial 07: Mimic Babel

In this tutorial, we are going to learn the following.

* How to write a simplified [Arrow Expression Transformation](https://babeljs.io/docs/babel-plugin-transform-arrow-functions) plugin.
  * Arrow expression
  * Block statement
  * Return statement
  * Function
  * Function expression
* Visualize the AST structure with [SWC Playground](https://swc.rs/playground).

## Is it easy to mimic Babel?

Yes, it's easy to mimic the major features that Babel has by implementing similar visitor-based plugins. Wait, not all features? That's right. Because Babel relies on some additional JavaScript / TypeScript libraries to complete the transformation. swc4j doesn't ship such libraries, so there are a few edge cases not covered. But we may borrow those Babel libraries in the applications and have the swc4j plugins rely on them to achieve 100% compatibility with Babel.

## Arrow Expression Transformation

Arrow expression was introduced in ES6 and has to be transformed to anonymous function for ES5. There is a Babel [plugin](https://babeljs.io/docs/babel-plugin-transform-arrow-functions) dedicated to that. Basically, it does the following:

```js
// From:
(a, b) => a + b
// To:
function(a, b) { return a + b; }

// From:
(a, b) => { return a * b; }
// To:
function(a, b) { return a * b; }
```

As there are ~170 AST types in SWC, it could be quite hard to manipulate the AST types at the beginning. No worry, we can visit the [SWC Playground](https://swc.rs/playground) to analyze the source and target AST structures. After getting a general understanding of the AST structure, we may proceed with the plugin development.

* Create an arrow expression visitor.

```java
// Create a plugin visitors and add an arrow expression visitor.
Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(new Swc4jAstVisitor() {
    @Override
    public Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node) {
        // TODO
        return super.visitArrowExpr(node);
    }
});
```

* According to the SWC playground, we are supposed to create a function expression whose constructor signature is as follows. Obviously, it is an anonymous function in our case, so the first argument `ident` is `null`. We only need to create the second argument.

```java
public Swc4jAstFnExpr(
        @Jni2RustParam(optional = true) Swc4jAstIdent ident,
        Swc4jAstFunction function,
        Swc4jSpan span)
```

* The function constructor signature is as follows. There are quite a few arguments to be prepared. No worry, let's handle them one by one.

```java
public Swc4jAstFunction(
        List<Swc4jAstParam> params,
        List<Swc4jAstDecorator> decorators,
        @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
        @Jni2RustParam(name = "is_generator") boolean generator,
        @Jni2RustParam(name = "is_async") boolean _async,
        @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
        @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn returnType,
        Swc4jSpan span)
```

* `params` is a list of `Swc4jAstParam` whose constructor signature is as follows. `decorators` is an experimental TypeScript feature that we can ignore.

```java
public Swc4jAstParam(
        List<Swc4jAstDecorator> decorators,
        ISwc4jAstPat pat,
        Swc4jSpan span)
```

* `pat` is an interface `ISwc4jAstPat` which is exactly the component type of the `params` of `Swc4jAstArrowExpr`. So, the conversion is as follows.

```java
// Transform the params.
List<Swc4jAstParam> params = node.getParams().stream()
        .map(param -> new Swc4jAstParam(new ArrayList<>(), param, Swc4jSpan.DUMMY))
        .collect(Collectors.toList());
```

* The function `body` has to be a `Swc4jAstBlockStmt` whereas the arrow expression `body` is an interface `ISwc4jAstBlockStmtOrExpr` whose definition is as follows.

```java
@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "BlockStmt", type = Swc4jAstBlockStmt.class),
                @Jni2RustEnumMapping(name = "Expr", type = ISwc4jAstExpr.class, box = true),
        }
)
public interface ISwc4jAstBlockStmtOrExpr extends ISwc4jAst {
}
```

* So, it can be either a block statement or an expression. The conversion is as follows.

```java
// Transform the block statement.
Swc4jAstBlockStmt blockStmt;
ISwc4jAstBlockStmtOrExpr body = node.getBody();
if (body instanceof Swc4jAstBlockStmt) {
    // If the body is a block statement, convert the body directly.
    blockStmt = body.as(Swc4jAstBlockStmt.class);
} else {
    // If the body is an expression, put that expression in a return statement
    // and add that return statement to the block statement.
    List<ISwc4jAstStmt> stmts = new ArrayList<>();
    Swc4jAstReturnStmt returnStmt = new Swc4jAstReturnStmt(body.as(ISwc4jAstExpr.class), Swc4jSpan.DUMMY);
    stmts.add(returnStmt);
    blockStmt = new Swc4jAstBlockStmt(stmts, Swc4jSpan.DUMMY);
}
```

* As the rest of the arguments are trivial, we can create the function and function expression as follows.

```java
// Create the function.
Swc4jAstFunction fn = new Swc4jAstFunction(
        params,
        new ArrayList<>(),
        blockStmt,
        node.isGenerator(),
        node.isAsync(),
        node.getTypeParams().orElse(null),
        node.getReturnType().orElse(null),
        Swc4jSpan.DUMMY);
// Create the function expression.
Swc4jAstFnExpr fnExpr = new Swc4jAstFnExpr(null, fn, Swc4jSpan.DUMMY);
```

* Once we have the function expression ready, we need to call the parent of the arrow expression to replace the arrow expression with the newly created function expression.

```java
// Replace the arrow expression with the function expression.
node.getParent().replaceNode(node, fnExpr);
```

* Well, the plugin is completed as follows.

```java
@Override
public Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node) {
    // Transform the params.
    List<Swc4jAstParam> params = node.getParams().stream()
            .map(param -> new Swc4jAstParam(new ArrayList<>(), param, Swc4jSpan.DUMMY))
            .collect(Collectors.toList());
    // Transform the block statement.
    Swc4jAstBlockStmt blockStmt;
    ISwc4jAstBlockStmtOrExpr body = node.getBody();
    if (body instanceof Swc4jAstBlockStmt) {
        // If the body is a block statement, convert the body directly.
        blockStmt = body.as(Swc4jAstBlockStmt.class);
    } else {
        // If the body is an expression, put that expression in a return statement
        // and add that return statement to the block statement.
        List<ISwc4jAstStmt> stmts = new ArrayList<>();
        Swc4jAstReturnStmt returnStmt = new Swc4jAstReturnStmt(body.as(ISwc4jAstExpr.class), Swc4jSpan.DUMMY);
        stmts.add(returnStmt);
        blockStmt = new Swc4jAstBlockStmt(stmts, Swc4jSpan.DUMMY);
    }
    // Create the function.
    Swc4jAstFunction fn = new Swc4jAstFunction(
            params,
            new ArrayList<>(),
            blockStmt,
            node.isGenerator(),
            node.isAsync(),
            node.getTypeParams().orElse(null),
            node.getReturnType().orElse(null),
            Swc4jSpan.DUMMY);
    // Create the function expression.
    Swc4jAstFnExpr fnExpr = new Swc4jAstFnExpr(null, fn, Swc4jSpan.DUMMY);
    // Replace the arrow expression with the function expression.
    node.getParent().replaceNode(node, fnExpr);
    return super.visitArrowExpr(node);
}
```

* Let's put them together and have a test.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a JavaScript code snippet.
String code = "const add = (a, b) => a + b; const multiply = (a, b) => { return a * b; }";
// Prepare a script name.
URL specifier = new URL("file:///abc.ts");
// Create a plugin host and add the plugin visitors.
Swc4jPluginHost pluginHost = new Swc4jPluginHost().add(pluginVisitors);
// Prepare an option with script name and media type.
Swc4jTransformOptions options = new Swc4jTransformOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.JavaScript)
        .setMinify(false)
        .setInlineSources(false)
        .setPluginHost(pluginHost)
        .setSourceMap(Swc4jSourceMapOption.None);
// Transform the code.
Swc4jTransformOutput output = swc4j.transform(code, options);
// Print the transformed code.
System.out.println("/*********************************************");
System.out.println("       The transformed code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
```

* The output is as follows. Well, it works as we expected.

```js
/*********************************************
       The transformed code is as follows.
*********************************************/
const add = function(a, b) {
  return a + b;
};
const multiply = function(a, b) {
  return a * b;
};
```

## Why is it a simplified one?

Because there are quite some edge cases not covered.

* `this` from the parent scope is not handled.
* `arguments` from the parent scope is not handled.
* The default argument is not handled.
* ...

## Conclusion

In this tutorial we've learned how to write a Babel flavored plugin, how to analyze the AST structure with the SWC playground and swc4j AST types.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial07MimicBabel.java).
