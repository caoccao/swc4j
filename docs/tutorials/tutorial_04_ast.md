# Tutorial 04: AST

In this tutorial, we are going to learn the follows.

* Parse a TypeScript code snippet.
* Create an AST visitor.
* Visit the AST.

## Parse TypeScript

* Create a email validation class in TypeScript as follows. Please make sure capture AST is set to true. Then parse the code and get the output.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a TypeScript code snippet.
String code = "import { Validator } from './Validator';\n" +
        "\n" +
        "class EmailValidator implements Validator {\n" +
        "    isValid(s: string): boolean {\n" +
        "        const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;\n" +
        "        return emailRegex.test(s);\n" +
        "    }\n" +
        "}\n" +
        "\n" +
        "export { EmailValidator };";
// Prepare a script name.
String specifier = "file:///abc.ts";
// Prepare an option with script name and media type.
Swc4jTranspileOptions options = new Swc4jTranspileOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.TypeScript)
        // Set capture ast.
        .setCaptureAst(true)
        .setParseMode(Swc4jParseMode.Module);
// Parse the code.
Swc4jParseOutput output = swc4j.parse(code, options);
// Print the tokens.
System.out.println("/*********************************************");
System.out.println("         The ast is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getProgram().toDebugString());
```

* The AST in the output is printed as follows.

```js
/*********************************************
         The ast is as follows.
*********************************************/
Module (0,247,1,1)
  body[0] ImportDecl (0,40,1,1)
    specifiers[0] ImportNamedSpecifier (9,18,1,10)
      imported? = null
      local Ident (9,18,1,10)
        optional = false
        sym = Validator
      typeOnly = false
    src Str (26,39,1,27)
      raw? = './Validator'
      value = ./Validator
    typeOnly = false
    with? = null
  body[1] ClassDecl (42,219,3,1)
    clazz Class (42,219,3,1)
      _abstract = false
      _implements[0] TsExprWithTypeArgs (74,83,3,33)
        expr Ident (74,83,3,33)
          optional = false
          sym = Validator
        typeArgs? = null
      body[0] ClassMethod (90,217,4,5)
        _abstract = false
        _override = false
        _static = false
        accessibility? = null
        function Function (90,217,4,5)
          _async = false
          body BlockStmt (118,217,4,33)
            stmts[0] VarDecl (128,176,5,9)
              declare = false
              decls[0] VarDeclarator (134,175,5,15)
                definite = false
                init Regex (147,175,5,28)
                  exp = ^[^\s@]+@[^\s@]+\.[^\s@]+$
                  flags = 
                name BindingIdent (134,144,5,15)
                  id Ident (134,144,5,15)
                    optional = false
                    sym = emailRegex
                  typeAnn? = null
              kind = Const
            stmts[1] ReturnStmt (185,211,6,9)
              arg CallExpr (192,210,6,16)
                args[0] ExprOrSpread (208,209,6,32)
                  expr Ident (208,209,6,32)
                    optional = false
                    sym = s
                  spread? = null
                callee MemberExpr (192,207,6,16)
                  obj Ident (192,202,6,16)
                    optional = false
                    sym = emailRegex
                  prop Ident (203,207,6,27)
                    optional = false
                    sym = test
                typeArgs? = null
          generator = false
          params[0] Param (98,107,4,13)
            pat BindingIdent (98,107,4,13)
              id Ident (98,107,4,13)
                optional = false
                sym = s
              typeAnn TsTypeAnn (99,107,4,14)
                typeAnn TsKeywordType (101,107,4,16)
                  kind = TsStringKeyword
          returnType TsTypeAnn (108,117,4,23)
            typeAnn TsKeywordType (110,117,4,25)
              kind = TsBooleanKeyword
          typeParams? = null
        key Ident (90,97,4,5)
          optional = false
          sym = isValid
        kind = Method
        optional = false
      superClass? = null
      superTypeParams? = null
      typeParams? = null
    declare = false
    ident Ident (48,62,3,7)
      optional = false
      sym = EmailValidator
  body[2] NamedExport (221,247,10,1)
    specifiers[0] ExportNamedSpecifier (230,244,10,10)
      exported? = null
      orig Ident (230,244,10,10)
        optional = false
        sym = EmailValidator
      typeOnly = false
    src? = null
    typeOnly = false
    with? = null
  shebang? = null
```

## Create a Visitor

In order to visit the AST, we need to create a Visitor as follows. There are 170+ types of AST nodes. This sample visitor only selectively visit 3 types of AST nodes.

* Subclass the built-in `Swc4jAstVisitor` or implement interface `ISwc4jAstVisitor` from scratch.

```java
public class Visitor extends Swc4jAstVisitor {
    @Override
    public Swc4jAstVisitorResponse visitClassDecl(Swc4jAstClassDecl node) {
        System.out.println("Class name is " + node.getIdent());
        node.getClazz().getImplements().forEach(impl ->
                System.out.println("  Implements " + impl.getExpr()));
        return super.visitClassDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitClassMethod(Swc4jAstClassMethod node) {
        System.out.println("Method name is " + node.getKey());
        return super.visitClassMethod(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitRegex(Swc4jAstRegex node) {
        System.out.println("Regex is " + node);
        return super.visitRegex(node);
    }
}
```

## Visit the AST

* Call `output.getProgram().visit()` with an instance of the `Visitor`.

```java
System.out.println("/*********************************************");
System.out.println("      The visitor output is as follows.");
System.out.println("*********************************************/");
output.getProgram().visit(new Visitor());
```

* The console output of the `Visitor` is as follows. As you can see, the AST can be easily manipulated.

```js
/*********************************************
      The visitor output is as follows.
*********************************************/
Class name is EmailValidator
  Implements Validator
Method name is isValid
Regex is /^[^\s@]+@[^\s@]+\.[^\s@]+$/
```

## Conclusion

In this tutorial we've learned how to parse the code and process the AST. The `parse()` provides a light-weighted insight over the scripts without paying the extra performance overhead on transpiling the scripts.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial04Ast.java).
