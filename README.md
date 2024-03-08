# swc4j

[![swc4j Build](https://github.com/caoccao/swc4j/actions/workflows/swc4j_build.yml/badge.svg)](https://github.com/caoccao/swc4j/actions/workflows/swc4j_build.yml)

[swc4j](https://github.com/caoccao/swc4j) ([SWC](https://github.com/swc-project/swc) for Java) is an ultra-fast JavaScript and TypeScript compilation and bundling tool on JVM. It is part of the [Javet](https://github.com/caoccao/Javet) portfolio serving the processing of JavaScript and TypeScript code before the code is executed in Node.js or V8 on JVM.

## Features

* JavaScript, TypeScript, JSX, TSX, etc.
* Parse (TODO)
* Transpile (Partially Completed)
* AST (TODO)

## Quick Start

* Download a snapshot build from the [Actions](https://github.com/caoccao/swc4j/actions).
* Run the following Java code.

```java
// Prepare a simple TypeScript code snippet.
String code = "function add(a:number, b:number) { return a+b; }";
// Prepare a script name.
String fileName = "abc.ts";
// Prepare an option with script name and media type set.
Swc4jTranspileOptions options = new Swc4jTranspileOptions()
        .setFileName(fileName)
        .setMediaType(Swc4jMediaType.TypeScript);
// Transpile the code.
Swc4jTranspileOutput output = new Swc4j().transpile(code, options);
// Print the transpiled code.
System.out.println(output.getCode());
```

* The transpiled code and inline source map are as follows.

```js
function add(a, b) {
  return a + b;
}
//# sourceMappingURL=data:application/json;base64,...
```

## License

[APACHE LICENSE, VERSION 2.0](LICENSE)
