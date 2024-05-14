# Javet Sanitizer

Javet Sanitizer is a sanitizer framework for parsing and validating JavaScript, TypeScript code on JVM. It is built on top of [swc4j](https://github.com/caoccao/swc4j).

Javet Sanitizer provides a set of rich checkers at AST level for [Javet](https://github.com/caoccao/Javet) so that applications can address and eliminate the potential threats before the JavaScript code is executed.

It was formerly built on top of [antlr4](https://github.com/antlr/antlr4) and [grammars-v4](https://github.com/antlr/grammars-v4) at https://github.com/caoccao/JavetSanitizer.

## Features

* [Built-in Object Protection](features/built_in_object_protection.md)
* [Keyword Restriction](features/keyword_restriction.md)
* [Function Restriction](features/function_restriction.md)
* [Identifier Restriction](features/identifier_restriction.md)
* [Identifier Deletion](features/identifier_deletion.md)
* [Identifier Freeze](features/identifier_freeze.md)
* [Identifier Naming Convention](features/identifier_naming_convention.md)

## Checkers

### Anonymous Function Checker

Anonymous function checker provides the following checks to validate if a script is a valid anonymous function.

1. Whether `shebang` exists or not per options.
2. `body` has only 1 node.
3. The only 1 node is an `ArrowExpr`.

### Module Checker

Module checker provides the following checks to validate if a script is a valid module.

1. Whether `shebang` exists or not per options.
2. `body` is not empty.
3. `export` is allowed or not per options.
4. `import` is allowed or not per options.

### Module Function Checker

Module function checker provides the following checks to validate if a script is a valid module and only contains function declarations.

1. Whether `shebang` exists or not per options.
2. `body` is not empty.
3. `export` is allowed or not per options.
4. `import` is allowed or not per options.
5. Other nodes are all `FnDecl`.

### Single Statement Checker

Single statement checker provides the following checks to validate if a script only contains one statement.

1. Whether `shebang` exists or not per options.
2. `body` has only 1 node.
3. The only 1 node is an `Stmt`.

### Statement List Checker

Statement list checker provides the following checks to validate if a script has at least one statement.

1. Whether `shebang` exists or not per options.
2. `body` has at least 1 node.
3. The nodes are all `Stmt`.

## Error Codes

| Error                | Code | Message                                                                                |
|----------------------|-----:|----------------------------------------------------------------------------------------|
| UnknownError         |    1 | Unknown error: ${message}                                                              |
| EmptyCodeString      |    2 | The code string is empty.                                                              |
| VisitorNotFound      |    3 | Visitor ${name} is not found.                                                          |
| ParsingError         |    4 | ${message}                                                                             |
| IdentifierNotAllowed |  100 | Identifier ${identifier} is not allowed.                                               |
| KeywordNotAllowed    |  101 | Keyword ${keyword} is not allowed.                                                     |
| InvalidNode          |  200 | ${actualNode} is unexpected. Expecting ${expectedNode} in ${nodeName}.                 |
| NodeCountMismatch    |  220 | AST node count ${actualCount} mismatches the expected AST node count ${expectedCount}. |
| NodeCountTooSmall    |  221 | AST node count ${actualCount} is less than the minimal AST node count ${minCount}.     |
| NodeCountTooLarge    |  222 | AST node count ${actualCount} is greater than the maximal AST node count ${maxCount}.  |
| FunctionNotFound     |  300 | Function ${name} is not found.                                                         |

## Swc4j vs. Antlr

### Antlr is Not a Unified Parser

In the modern scripting world, the scripts are no longer merely JavaScript anymore, but a mixture of JS, TS, JSX, TSX, etc. In Antlr, supporting all those programming languages means we have to generate all individual parsers from their corresponding grammars, and those parsers barely share a common ground. That's N times of duplicated work which tends to be a frustrating burden to the developers.

Swc4j provides a unified parser and AST visitor for 10+ formats: .js, .ts, .jsx, .tsx, .cjs, .mjs, .mts, .cts, .d.ts, .d.mts, .d.cts, .json, .tsbuildinfo, wasm, and source map. That maximizes the reuse of code and simplifies the applications.

### Antlr Grammars are Far from Ideal

I have been using [grammars-v4](https://github.com/antlr/grammars-v4), raising issues and patching it for years. I have to say the quality of the JavaScript grammar is far from ideal. Certain expressions and statements might be off the spec, or completely missing. That seriously impacts the quality of the parser.

As the grammar is crowd-sourced, it's hard to find a go-to person (usually called maintainer on github). At least, I failed to find anyone who is in charge of the JavaScript grammar. I even doubt there are not many users using the JavaScript grammar.

SWC is one of the **de facto** and popular solutions in the JavaScript and TypeScript world.

* SWC is maintained well.
* SWC is up-to-date.
* SWC supports all modern features, even features in staging.
* SWC is ultra-fast.

All those benefits from SWC are available in Swc4j.

### Antlr doesn't Support Transpilation

Antlr is just a parser whereas in the real world we sometimes need a transpiler, e.g. transpile from JSX to JS, from TSX to JS.

Swc4j supports the transpilation out-of-box.

### Antlr Version Conflict is Annoying

There are many versions of Antlr. Those versions are referenced by many common Java libraries, even Spring depends on a particular version of Antlr. E.g. [Spring Data JPA introduces query parser](https://spring.io/blog/2023/03/21/spring-data-jpa-introduces-query-parser). That restricts the downstream applications in choosing another version of Antlr and grammar.

Swc4j is a Java library with zero external dependencies. Applications don't need to step in the dependency hell while using Swc4j.

### Native Side of Swc4j

The only leading feature I could find from Antlr is: Antlr is a pure Java library. There is a native library embedded in Swc4j as the following architectural diagram shows. All SWC features are in a native library written in Rust for Swc4j to call via JNI. But, why should I care? I don't care because it's transparent to the applications at all. Also, if the scripts are executed in [Javet](https://github.com/caoccao/Javet) which has a native library for Node.js or V8 too, there is no difference between 1 and 2 native libraries.
