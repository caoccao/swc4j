# swc4j Documentation

Welcome to the swc4j documentation! swc4j is an ultra-fast JavaScript and TypeScript compilation and bundling tool on JVM, bridging the SWC (Speedy Web Compiler) Rust library with Java via JNI.

## Table of Contents

- [Getting Started](#getting-started)
- [Core Operations](#core-operations)
- [Features](#features)
- [Tutorials](#tutorials)
- [Release Notes](#release-notes)

## Getting Started

swc4j provides three main operations for working with JavaScript and TypeScript code:

### Core Operations

| Document                  | Description                                                 |
| ------------------------- | ----------------------------------------------------------- |
| [Parse](parse.md)         | Parse JavaScript/TypeScript into AST (Abstract Syntax Tree) |
| [Transform](transform.md) | Transform and minify JavaScript/TypeScript code             |
| [Transpile](transpile.md) | Convert TypeScript/JSX to JavaScript                        |

### Quick Examples

**Parse TypeScript:**

```java
Swc4j swc4j = new Swc4j();
Swc4jParseOutput output = swc4j.parse("const x: number = 42;", options);
```

**Transform JavaScript:**

```java
Swc4jTransformOutput output = swc4j.transform(code, transformOptions);
String minified = output.getCode();
```

**Transpile TypeScript:**

```java
Swc4jTranspileOutput output = swc4j.transpile(tsCode, transpileOptions);
String jsCode = output.getCode();
```

## Features

Explore advanced capabilities in the [features](features/) directory:

### 🔍 Code Analysis & AST

- **[AST](features/ast.md)** - Understanding the Abstract Syntax Tree structure
- **[AST Visitor](features/ast_visitor.md)** - Implement custom code analysis and transformation
- **[Plugin](features/plugin.md)** - Create reusable plugins for code processing

### 🔒 Security & Sanitization

swc4j provides comprehensive security features to create safe JavaScript execution environments. See [Sanitizer](sanitizer.md) for an overview.

**Protection Mechanisms:**

- **[Built-in Object Protection](features/built_in_object_protection.md)** - Prevent access to dangerous built-in objects
- **[Identifier Restriction](features/identifier_restriction.md)** - Block specific variable/function names
- **[Identifier Freeze](features/identifier_freeze.md)** - Make identifiers immutable
- **[Identifier Deletion](features/identifier_deletion.md)** - Remove dangerous identifiers
- **[Function Restriction](features/function_restriction.md)** - Control which functions can be called
- **[Keyword Restriction](features/keyword_restriction.md)** - Limit JavaScript keywords usage
- **[Naming Convention](features/identifier_naming_convention.md)** - Enforce identifier naming rules

### ⚙️ Configuration & Deployment

- **[Custom Library Loading](features/custom_library_loading.md)** - Control native library deployment
  - Custom deployment paths
  - System library integration
  - Docker/Kubernetes support
  - Classloader error suppression

## Tutorials

Step-by-step guides for common use cases in [tutorials](tutorials/):

## Common Use Cases

### JavaScript/TypeScript Compilation

1. **Parse** code to validate syntax
2. **Transform** to minify and optimize
3. **Transpile** TypeScript/JSX to JavaScript

See: [Parse](parse.md), [Transform](transform.md), [Transpile](transpile.md)

### Code Security & Sanitization

1. Review [Sanitizer](sanitizer.md) overview
2. Choose appropriate protection mechanisms
3. Apply restrictions to create safe environments

See: [features](features/) - Security section

### AST-based Code Analysis

1. **Parse** code into AST
2. Implement custom **Visitor** for analysis
3. Extract insights or transform code

See: [AST](features/ast.md), [AST Visitor](features/ast_visitor.md)

### Custom Deployment

1. Configure library loading strategy
2. Set system properties or custom listener
3. Deploy to production environment

See: [Custom Library Loading](features/custom_library_loading.md)

## Release Notes

See [release_notes.md](release_notes.md) for version history, new features, and breaking changes.

## Getting Help

1. **Check the documentation** - Most common questions are covered here
2. **Review tutorials** - Step-by-step examples for common scenarios
3. **Explore features** - Detailed documentation for specific capabilities
4. **Check release notes** - Recent changes and migration guides
