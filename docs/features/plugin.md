# Plugin

swc4j provides a plugin system for manipulating the AST during `parse()`, `transform()` or `transpile()` similar to what SWC or Babel plugin system does.

In SWC, a plugin is usually written in Rust and built into Wasm. In swc4j, we can write plugins in pure Java.

## Plugin Host

swc4j plugin host is an interface that takes the AST program as the input. After the plugin host is set to the parse options, transform options, or transpile options, `parse()`, `transform()` or `transpile()` will call the plugin host to make changes to the AST and generate the output based on the new AST.

```java
public interface ISwc4jPluginHost {
    boolean process(ISwc4jAstProgram<?> program);
}
```

The plugins can be visitor based or any other forms depending on the use cases.

## Visitor Plugin

There is a built-in plugin `Swc4jPluginVisitors` which holds a list of `ISwc4jAstVisitor`. It provides a similar development experience that the Babel gives. By writing our own visitors, we are able to manipulate the AST down to arbitrary AST node level without getting involved in other details.

## Built-in Plugins

### ES2015

#### Transform Spread

Transform Spread plugin allows applications to use the spread syntax `...`, which is a feature introduced in ES2015 (ECMAScript 2015), in environments that don't natively support it. This typically applies to older browsers that haven't been updated to support the latest JavaScript standards.

E.g. `[...a, 1, 2, ...b]` to `a.concat([1, 2], b)`.

### JsFuck

#### JsFuck Decoder

JsFuck Decoder plugin allows applications to decode JsFuck encoded scripts to their original form.

E.g. `[+!+[]]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]])+[])[!+[]+!+[]]+[+!+[]]` to `1+1`.
