# swc4j

[![Maven Central](https://img.shields.io/maven-central/v/com.caoccao.javet/swc4j?style=for-the-badge)](https://central.sonatype.com/artifact/com.caoccao.javet/swc4j) [![Discord](https://img.shields.io/discord/870518906115211305?label=join%20our%20Discord&style=for-the-badge)](https://discord.gg/R4vvKU96gw)

[![swc4j Build](https://github.com/caoccao/swc4j/actions/workflows/swc4j_build.yml/badge.svg)](https://github.com/caoccao/swc4j/actions/workflows/swc4j_build.yml)

[swc4j](https://github.com/caoccao/swc4j) ([SWC](https://github.com/swc-project/swc) for Java) is an ultra-fast JavaScript and TypeScript compilation and bundling tool on JVM. It is part of the [Javet](https://github.com/caoccao/Javet) portfolio serving the processing of JavaScript and TypeScript code before the code is executed in Node.js or V8 on JVM.

<img src="https://github.com/caoccao/swc4j/assets/17514279/5ddddfca-91fc-45dc-83fe-ee7731564b90" alt="swc4j and Javet" width="400"/>

## Features

* Android + Linux + MacOS + Windows
* JavaScript, TypeScript, JSX, TSX, etc.
* [Parse](docs/parse.md)
  * [AST](docs/features/ast.md)
    * [Visitor](docs/features/ast_visitor.md)
    * [Plugin](docs/features/plugin.md)
  * Comments
  * Tokens
* [Transform](docs/transform.md)
  * Minify
  * Multiple Target ES Version
  * [Source Map](docs/features/source_map.md)
* [Transpile](docs/transpile.md)
  * TS → JS
  * JSX → JS
  * TSX → JS
  * Source Map
* [Sanitizer](docs/sanitizer.md)
  * [Built-in Object Protection](docs/features/built_in_object_protection.md)
  * [Keyword Restriction](docs/features/keyword_restriction.md)
  * [Function Restriction](docs/features/function_restriction.md)
  * [Identifier Restriction](docs/features/identifier_restriction.md)
  * [Identifier Deletion](docs/features/identifier_deletion.md)
  * [Identifier Freeze](docs/features/identifier_freeze.md)
  * [Identifier Naming Convention](docs/features/identifier_naming_convention.md)

## Quick Start

### Dependency

#### Maven

```xml
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j</artifactId> <!-- Must Have -->
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-android-arm</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-android-arm64</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-android-x86</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-android-x86_64</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-linux-arm64</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-linux-x86_64</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-macos-arm64</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-macos-x86_64</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-windows-arm64</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-windows-x86_64</artifactId>
    <version>1.8.0</version>
</dependency>
```

#### Gradle Kotlin DSL

```kotlin
implementation("com.caoccao.javet:swc4j:1.8.0") // Must Have
implementation("com.caoccao.javet:swc4j-android-arm:1.8.0")
implementation("com.caoccao.javet:swc4j-android-arm64:1.8.0")
implementation("com.caoccao.javet:swc4j-android-x86:1.8.0")
implementation("com.caoccao.javet:swc4j-android-x86_64:1.8.0")
implementation("com.caoccao.javet:swc4j-linux-arm64:1.8.0")
implementation("com.caoccao.javet:swc4j-linux-x86_64:1.8.0")
implementation("com.caoccao.javet:swc4j-macos-arm64:1.8.0")
implementation("com.caoccao.javet:swc4j-macos-x86_64:1.8.0")
implementation("com.caoccao.javet:swc4j-windows-arm64:1.8.0")
implementation("com.caoccao.javet:swc4j-windows-x86_64:1.8.0")
```

#### Gradle Groovy DSL

```groovy
implementation 'com.caoccao.javet:swc4j:1.8.0' // Must Have
implementation 'com.caoccao.javet:swc4j-android-arm:1.8.0'
implementation 'com.caoccao.javet:swc4j-android-arm64:1.8.0'
implementation 'com.caoccao.javet:swc4j-android-x86:1.8.0'
implementation 'com.caoccao.javet:swc4j-android-x86_64:1.8.0'
implementation 'com.caoccao.javet:swc4j-linux-arm64:1.8.0'
implementation 'com.caoccao.javet:swc4j-linux-x86_64:1.8.0'
implementation 'com.caoccao.javet:swc4j-macos-arm64:1.8.0'
implementation 'com.caoccao.javet:swc4j-macos-x86_64:1.8.0'
implementation 'com.caoccao.javet:swc4j-windows-arm64:1.8.0'
implementation 'com.caoccao.javet:swc4j-windows-x86_64:1.8.0'
```

### Transpile

* Run the following Java code to transpile TypeScript to JavaScript.

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

* The transpiled JavaScript code and inline source map are as follows.

```js
function add(a, b) {
  return a + b;
}
//# sourceMappingURL=data:application/json;base64,...
```

### Sanitize

* Run the following Java code to sanitize the JavaScript code.

```java
JavetSanitizerStatementListChecker checker = new JavetSanitizerStatementListChecker();

// 1. Check if keyword const can be used.
String codeString = "const a = 1;";
checker.check(codeString);
System.out.println("1. " + codeString + " // Valid.");

// 2. Check if keyword var can be used.
codeString = "var a = 1;";
try {
    checker.check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("2. " + codeString + " // Invalid: " + e.getMessage());
}

// 3. Check if Object is mutable.
codeString = "Object = {};";
try {
    checker.check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("3. " + codeString + " // Invalid: " + e.getMessage());
}
```

* The output is as follows.

```js
1. const a = 1; // Valid.
2. var a = 1; // Invalid: Keyword var is not allowed.
3. Object = {}; // Invalid: Identifier Object is not allowed.
```

## Docs

* [Tutorials](docs/tutorials/)
* [Release Notes](docs/release_notes.md)

## Blog

* [Run TypeScript Directly in Java](https://blog.caoccao.com/run-typescript-directly-in-java-82b7003b44b8)
* [Hello Swc4j, Goodbye Antlr](https://blog.caoccao.com/hello-swc4j-goodbye-antlr-f9a63e45a3d4)
* [How to Compromise V8 on JVM](https://blog.caoccao.com/how-to-compromise-v8-on-jvm-ceb385572461)
* Write SWC Plugins in Java: [Part 1](https://blog.caoccao.com/write-swc-plugins-in-java-part-1-d48139c6c675), [Part 2](https://blog.caoccao.com/write-swc-plugins-in-java-part-2-039d54611863), [Part 3](https://blog.caoccao.com/write-swc-plugins-in-java-part-3-b82c8bea4069)

## License

[APACHE LICENSE, VERSION 2.0](LICENSE)
