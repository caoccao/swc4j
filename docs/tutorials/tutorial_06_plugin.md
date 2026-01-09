# Tutorial 06: Plugin

In this tutorial, we are going to learn the following.

* What is the plugin system
* What is a plugin host
* What is a visitor plugin
* Write a plugin that swaps the left and right identifiers of an assign expression
* Write a plugin that swaps the conditions and alternative conditions of an if statement

## What is the plugin system

swc4j provides a plugin system for manipulating the AST during `parse()`, `transform()` or `transpile()` similar to what SWC or Babel plugin system does.

In SWC, a plugin is usually written in Rust and built into Wasm. In swc4j, we can write plugins in pure Java.

In Babel, a plugin is usually written in JavaScript and there are huge room for Babel to improve its performance. In swc4j, the similar visitor-based plugin development experience is provided with a much better performance.

## What is a plugin host

swc4j plugin host is an interface that takes the AST program as the input. After the plugin host is set to the parse options, transform options, or transpile options, `parse()`, `transform()` or `transpile()` will call the plugin host to make changes to the AST and generate the output based on the new AST.

```java
public interface ISwc4jPluginHost {
    boolean process(ISwc4jAstProgram<?> program);
}
```

There is a built-in `Swc4jPluginHost` which holds a list of `ISwc4jPlugin`. If the built-in plugin host is not ideal, custom plugin hosts are encouraged.

## What is a visitor plugin

There is a built-in plugin `Swc4jPluginVisitors` which holds a list of `ISwc4jAstVisitor`. It provides a similar development experience that the Babel gives. By writing our own visitors, we are able to manipulate the AST down to arbitrary AST node level without getting involved in other details.

## Write a plugin for Assign Expression

* Create a plugin host, plugin, and define a visitor that swaps the left and right identifiers of an assign expression.

```java
// Create a plugin visitors and add an assign expression visitor.
Swc4jPluginVisitors pluginVisitors = new Swc4jPluginVisitors().add(new Swc4jAstVisitor() {
    @Override
    public Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node) {
        Swc4jAstBindingIdent leftBindingIdent = node.getLeft().as(Swc4jAstBindingIdent.class);
        Swc4jAstIdent leftIdent = leftBindingIdent.getId().as(Swc4jAstIdent.class);
        Swc4jAstIdent rightIdent = node.getRight().as(Swc4jAstIdent.class);
        leftBindingIdent.setId(rightIdent);
        node.setRight(leftIdent);
        return super.visitAssignExpr(node);
    }
});
// Create a plugin host and add the plugin visitors.
Swc4jPluginHost pluginHost = new Swc4jPluginHost().add(pluginVisitors);
```

* Create 2 assign expressions in JavaScript as follows.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a JavaScript code snippet.
String code = "a = b; c = d;";
// Prepare a script name.
URL specifier = URI.create("file:///abc.ts").toURL();
```

* Transpile the code snippet as follows.

```java
// Prepare an option with script name and media type.
Swc4jTranspileOptions options = new Swc4jTranspileOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.JavaScript)
        .setInlineSources(false)
        // Add the plugin host
        .setPluginHost(pluginHost)
        .setSourceMap(Swc4jSourceMapOption.None);
// Transpile the code.
Swc4jTranspileOutput output = swc4j.transpile(code, options);
// Print the transpiled code.
System.out.println("/*********************************************");
System.out.println("       The transpiled code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
```

* The transpiled code in the output is printed as follows. As we can see, `a = b` becomes `b = a`, `c = d` becomes `d = c`.

```js
/*********************************************
       The transpiled code is as follows.
*********************************************/
b = a;
d = c;
```

* Minify the code snippet as follows.

```java
// Prepare an option with script name and media type.
// Minify is turned on by default.
Swc4jTransformOptions options = new Swc4jTransformOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.JavaScript)
        .setInlineSources(false)
        // Add the plugin host
        .setPluginHost(pluginHost)
        .setSourceMap(Swc4jSourceMapOption.None);
// Transform the code.
Swc4jTransformOutput output = swc4j.transform(code, options);
// Print the minified code.
System.out.println("/*********************************************");
System.out.println("       The minified code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
```

* The minified code in the output is printed as follows. As we can see, `a = b` becomes `b=a`, `c = d` becomes `d=c`.

```js
/*********************************************
       The minified code is as follows.
*********************************************/
b=a;d=c;
```

## Write a plugin for If Statement

* Define a visitor that swaps the conditions and alternative conditions of an if statement.

```java
// Add an if statement visitor to the plugin visitors.
pluginVisitors.add(new Swc4jAstVisitor() {
    @Override
    public Swc4jAstVisitorResponse visitIfStmt(Swc4jAstIfStmt node) {
        ISwc4jAstStmt cons = node.getCons().as(ISwc4jAstStmt.class);
        ISwc4jAstStmt alt = node.getAlt().get().as(ISwc4jAstStmt.class);
        node.setCons(alt);
        node.setAlt(cons);
        return super.visitIfStmt(node);
    }
});
```

* Create an if statement in JavaScript as follows.

```java
// Prepare a JavaScript code snippet.
String codeForIfStmt = "if (a) { b; } else { c; }";
```

* Call `transpile()` and the transpiled code in the output is printed as follows. As we can see, `{ b; }` becomes `{ c; }`, `{ c; }` becomes `{ b; }`.

```js
/*********************************************
       The transpiled code is as follows.
*********************************************/
if (a) {
  c;
} else {
  b;
}
```

* Call `transform()` and the minified code in the output is printed as follows. As we can see, `{ b; }` becomes `{c;}`, `{ c; }` becomes `{b;}`.

```js
/*********************************************
       The minified code is as follows.
*********************************************/
if(a){c;}else{b;}
```

## Conclusion

In this tutorial we've learned what is the plugin system, plugin host, visitor plugin, and how to write plugins to manipulate the AST.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial06Plugin.java).
