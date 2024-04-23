# Tutorial 04: AST

In this tutorial, we are going to learn the following.

* Parse a TypeScript code snippet.
* Create an AST visitor.
* Visit the AST.
* Fetch the comments.

## Parse TypeScript

* Create an email validation class in TypeScript as follows. Please make sure capture AST is set to true. Then parse the code and get the output.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a TypeScript code snippet.
String code = "import { Validator } from './Validator';\n" +
        "\n" +
        "class EmailValidator implements Validator {\n" +
        "    isValid(s: string): boolean {\n" +
        "        // This is a regex for email validation.\n" +
        "        const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;\n" +
        "        return emailRegex.test(s);\n" +
        "    }\n" +
        "}\n" +
        "\n" +
        "export { EmailValidator };";
// Prepare a script name.
URL specifier = new URL("file://abc.ts");
// Prepare an option with script name and media type.
Swc4jParseOptions options = new Swc4jParseOptions()
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
Module (0,296,1,1)
  body[0] ImportDecl (0,40,1,1)
    phase = Evaluation
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
  body[1] ClassDecl (42,268,3,1)
    clazz Class (42,268,3,1)
      _abstract = false
      _implements[0] TsExprWithTypeArgs (74,83,3,33)
        expr Ident (74,83,3,33)
          optional = false
          sym = Validator
        typeArgs? = null
      body[0] ClassMethod (90,266,4,5)
        _abstract = false
        _override = false
        _static = false
        accessibility? = null
        function Function (90,266,4,5)
          _async = false
          body BlockStmt (118,266,4,33)
            stmts[0] VarDecl (177,225,6,9)
              declare = false
              decls[0] VarDeclarator (183,224,6,15)
                definite = false
                init Regex (196,224,6,28)
                  exp = ^[^\s@]+@[^\s@]+\.[^\s@]+$
                  flags = 
                name BindingIdent (183,193,6,15)
                  id Ident (183,193,6,15)
                    optional = false
                    sym = emailRegex
                  typeAnn? = null
              kind = Const
            stmts[1] ReturnStmt (234,260,7,9)
              arg CallExpr (241,259,7,16)
                args[0] ExprOrSpread (257,258,7,32)
                  expr Ident (257,258,7,32)
                    optional = false
                    sym = s
                  spread? = null
                callee MemberExpr (241,256,7,16)
                  obj Ident (241,251,7,16)
                    optional = false
                    sym = emailRegex
                  prop Ident (252,256,7,27)
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
  body[2] NamedExport (270,296,11,1)
    specifiers[0] ExportNamedSpecifier (279,293,11,10)
      exported? = null
      orig Ident (279,293,11,10)
        optional = false
        sym = EmailValidator
      typeOnly = false
    src? = null
    typeOnly = false
    with? = null
  shebang? = null
```

## Create a Visitor

In order to visit the AST, we need to create a Visitor as follows. There are 170+ types of AST nodes. This sample visitor only selectively visits 3 types of AST nodes.

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

* The console output of the `Visitor` is as follows. As we can see, the AST can be easily manipulated.

```js
/*********************************************
      The visitor output is as follows.
*********************************************/
Class name is EmailValidator
  Implements Validator
Method name is isValid
Regex is /^[^\s@]+@[^\s@]+\.[^\s@]+$/
```

## Fetch the Comments

As the AST shows there are no comments. What if we want to fetch the comments? Yes, that's supported.

* Turn on the comments and parse the code again as follows.

```java
// Turn on the comments.
options.setCaptureComments(true);
// Parse the code again.
output = swc4j.parse(code, options);
```

* Upgrade the visitor by introducing a private property `comments` and its corresponding constructor.

```java
private final Swc4jComments comments;

public Visitor(Swc4jComments comments) {
    this.comments = comments;
}
```

* As the comment is in front of a VarDecl `const emailRegex = /.../;`, just add `visitVarDecl()` to the visitor as follows.

```java
@Override
public Swc4jAstVisitorResponse visitVarDecl(Swc4jAstVarDecl node) {
    if (comments != null) {
        Swc4jComment comment = comments.getLeading(node.getSpan()).get(0);
        System.out.println("Leading comment is a " + comment.getKind().name() + " comment.");
        System.out.println("  And the comment text is '" + comment.getText() + "'");
    }
    return super.visitVarDecl(node);
}
```

* Run the following code again with the comments as an argument for the visitor.

```java
System.out.println("/*********************************************");
System.out.println("      The visitor output is as follows.");
System.out.println("*********************************************/");
output.getProgram().visit(new Visitor(output.getComments()));
```

* The console output of the `Visitor` is as follows. As we can see, the comment is captured and printed successfully.

```js
/*********************************************
      The visitor output is as follows.
*********************************************/
Class name is EmailValidator
  Implements Validator
Method name is isValid
Leading comment is a Line comment.
  And the comment text is ' This is a regex for email validation.'
Regex is /^[^\s@]+@[^\s@]+\.[^\s@]+$/
```

* All comments are stored in the output as leading and trailing comments both of which are `Map<String, List<Swc4jComment>>`. The following code explains what a leading and trailing comment is.

```js
// This is a leading comment. 
// The key of the leading map is the start position of an AST node.
// In this case, the key is 0.
const a = 1;
// This is a trailing comment.
// The key of the trailing map is the end position of an AST node.
// In this case, the key is 12.
```

## Conclusion

In this tutorial we've learned how to parse the code, process the AST and comments. The `parse()` provides a light-weighted insight over the scripts without paying the extra performance overhead on transpiling the scripts.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial04Ast.java).
