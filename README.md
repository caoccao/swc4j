# swc4j

[![Maven Central](https://img.shields.io/maven-central/v/com.caoccao.javet/swc4j?style=for-the-badge)](https://central.sonatype.com/search?q=g:com.caoccao.javet.swc4j) [![Discord](https://img.shields.io/discord/870518906115211305?label=join%20our%20Discord&style=for-the-badge)](https://discord.gg/R4vvKU96gw)

[![swc4j Build](https://github.com/caoccao/swc4j/actions/workflows/swc4j_build.yml/badge.svg)](https://github.com/caoccao/swc4j/actions/workflows/swc4j_build.yml)

[swc4j](https://github.com/caoccao/swc4j) ([SWC](https://github.com/swc-project/swc) for Java) is an ultra-fast JavaScript and TypeScript compilation and bundling tool on JVM. It is part of the [Javet](https://github.com/caoccao/Javet) portfolio serving the processing of JavaScript and TypeScript code before the code is executed in Node.js or V8 on JVM.

<img src="https://github.com/caoccao/swc4j/assets/17514279/5ddddfca-91fc-45dc-83fe-ee7731564b90" alt="swc4j and Javet" width="400"/>

## Features

* Linux + MacOS + Windows
* JavaScript, TypeScript, JSX, TSX, etc.
* Parse
  * AST
  * Comments
  * Tokens
* Transform
  * Minify
  * Multiple Target ES Version
  * Source Map
* Transpile
  * TS → JS
  * JSX → JS
  * TSX → JS
  * Source Map

## Quick Start

* Add the following dependency to your project or download a snapshot build from the [Actions](https://github.com/caoccao/swc4j/actions).

```xml
<!-- Maven -->
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j</artifactId>
    <version>0.5.0</version>
</dependency>
```

```kotlin
// Gradle Kotlin DSL
implementation("com.caoccao.javet:swc4j:0.5.0")
```

```groovy
// Gradle Groovy DSL
implementation 'com.caoccao.javet:swc4j:0.5.0'
```

* Run the following Java code.

```java
// Prepare a simple TypeScript code snippet.
String code = "function add(a:number, b:number) { return a+b; }";
// Prepare a script name.
URL specifier = new URL("file:///abc.ts");
// Prepare an option with script name and media type.
Swc4jTranspileOptions options = new Swc4jTranspileOptions()
        .setSpecifier(specifier)
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

## Docs

* [Tutorials](docs/tutorials/)
* [Release Notes](docs/RELEASE_NOTES.md)

## Blog

* [Run TypeScript Directly in Java](https://blog.caoccao.com/run-typescript-directly-in-java-82b7003b44b8)
* [Hello Swc4j, Goodbye Antlr](https://blog.caoccao.com/hello-swc4j-goodbye-antlr-f9a63e45a3d4)

## License

[APACHE LICENSE, VERSION 2.0](LICENSE)
