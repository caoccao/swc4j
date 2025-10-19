# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

swc4j (SWC for Java) is an ultra-fast JavaScript and TypeScript compilation and bundling tool on JVM. It bridges the SWC (Speedy Web Compiler) Rust library with Java via JNI (Java Native Interface), providing parsing, transformation, transpilation, and sanitization capabilities for JavaScript/TypeScript code.

## Build System

**Primary Build Tool**: Gradle with Kotlin DSL

### Common Build Commands

```bash
# Build the Java project
./gradlew build

# Run tests (excludes performance tests)
./gradlew test

# Run performance tests
./gradlew performanceTest

# Clean build artifacts
./gradlew clean

# Build JNI headers (generates C headers from Java native methods)
./gradlew buildJNIHeaders

# Generate JAR with sources and javadoc
./gradlew jar sourcesJar javadocJar

# Generate POM file for Maven publishing
./gradlew generatePomFileForGeneratePomPublication
```

### Running Single Tests

```bash
# Run a specific test class
./gradlew test --tests "com.caoccao.javet.swc4j.TestSwc4j"

# Run a specific test method
./gradlew test --tests "com.caoccao.javet.swc4j.TestSwc4j.testGetVersion"
```

## Rust Native Library

The native Rust code is located in `rust/` directory and uses Cargo for building:

```bash
cd rust
cargo build                    # Debug build
cargo build --release          # Release build
cargo test                     # Run Rust tests
```

The Rust library uses `deno_ast` v0.50.0 for the core SWC functionality and exposes JNI bindings to Java.

## Architecture

### High-Level Structure

swc4j follows a **JNI bridge architecture** with three main layers:

1. **Java API Layer** (`src/main/java/`): Public API exposed to users
2. **JNI Bridge Layer** (`Swc4jNative.java` + `rust/src/lib.rs`): Connects Java to Rust
3. **Rust Core Layer** (`rust/src/`): Implements SWC functionality using `deno_ast`

### Key Components

#### 1. Core Entry Points (Java)
- `Swc4j.java`: Main API class with three core operations:
  - `parse()` - Parse JS/TS code into AST
  - `transform()` - Transform/minify code with various options
  - `transpile()` - Transpile TS/JSX to JS
- `Swc4jNative.java`: JNI native method declarations

#### 2. JNI Bridge (Rust)
- `rust/src/lib.rs`: JNI entry points that delegate to core implementations
  - `Java_com_caoccao_javet_swc4j_Swc4jNative_coreParse`
  - `Java_com_caoccao_javet_swc4j_Swc4jNative_coreTransform`
  - `Java_com_caoccao_javet_swc4j_Swc4jNative_coreTranspile`
- `rust/src/jni_utils.rs`: JNI conversion utilities
- `rust/src/core.rs`: Core SWC operations implementation

#### 3. AST System
The AST (Abstract Syntax Tree) implementation follows SWC's structure:
- `ast/`: Java representations of AST nodes
  - `ast/clazz/`: Class-related nodes (functions, classes, methods)
  - `ast/expr/`: Expression nodes (binary, unary, call expressions)
  - `ast/expr/lit/`: Literal nodes (strings, numbers, booleans)
  - `ast/stmt/`: Statement nodes (if, return, var declarations)
  - `ast/program/`: Program/module nodes
  - `ast/enums/`: AST type enumerations
- Each AST node extends from base interfaces and can be visited/modified

#### 4. Sanitizer System
The sanitizer (`com.caoccao.javet.sanitizer`) provides security features:
- `checkers/`: Different checker implementations for code validation
  - `JavetSanitizerStatementListChecker` - Validates statement lists
  - `JavetSanitizerModuleChecker` - Validates modules
- `matchers/`: Pattern matchers for restrictions
  - `JavetSanitizerBuiltInObjectMatcher` - Protects built-in objects
  - `JavetSanitizerIdentifierMatcher` - Restricts identifiers
- `visitors/`: AST visitors for applying security rules
- `codegen/`: Code generation utilities (e.g., freezing identifiers)

#### 5. Jni2Rust Code Generation
- `src/test/java/com/caoccao/javet/swc4j/jni2rust/`: Code generation utilities
- **Purpose**: Automatically generates Rust JNI glue code from annotated Java classes
- **How it works**:
  - Java classes/methods are annotated with `@Jni2RustClass` and `@Jni2RustMethod`
  - `Jni2Rust.java` reads annotations and generates corresponding Rust structs and JNI call wrappers
  - Generated code is inserted between `/* StructName Begin */` and `/* StructName End */` markers in Rust files
- **Run codegen**: Execute `TestCodeGen` test class to regenerate Rust JNI bindings

### Data Flow

```
User Code
   ↓
Swc4j.parse/transform/transpile (Java)
   ↓
Swc4jNative.coreParse/coreTransform/coreTranspile (JNI)
   ↓
lib.rs → core.rs (Rust)
   ↓
deno_ast (SWC library)
   ↓
Return Output (Swc4jParseOutput/TransformOutput/TranspileOutput)
```

## Multi-Platform Native Libraries

swc4j ships platform-specific native libraries for:
- **Android**: arm, arm64, x86, x86_64
- **Linux**: arm64, x86_64
- **macOS**: arm64, x86_64
- **Windows**: arm64, x86_64

The `Swc4jLibLoader` class automatically loads the correct native library for the current platform at runtime.

## Options System

Each core operation has a corresponding options class:
- `Swc4jParseOptions`: Configure parsing (media type, syntax, capture AST/comments/tokens)
- `Swc4jTransformOptions`: Configure transformation (target ES version, minification)
- `Swc4jTranspileOptions`: Configure transpilation (specifier, source maps)

Options are converted from Java to Rust via JNI and passed to the SWC library.

## Plugin System

swc4j supports AST visitor plugins:
- Java-side: Implement `ISwc4jAstVisitor` interface
- Test implementations in `src/test/java/com/caoccao/javet/swc4j/plugins/`
- Plugins can transform AST nodes during parse/transform operations

## Testing

Tests are organized by feature:
- `src/test/java/com/caoccao/javet/swc4j/`: Core API tests
- `src/test/java/com/caoccao/javet/swc4j/ast/`: AST tests
- `src/test/java/com/caoccao/javet/sanitizer/`: Sanitizer tests
- `src/test/java/com/caoccao/javet/swc4j/tutorials/`: Tutorial examples
- Performance tests are tagged with `@Tag("performance")` and excluded from default test run

## Version Management

Version is defined in `build.gradle.kts` under `Config.Versions.SWC4J`. To change version:
1. Update `Config.Versions.SWC4J` in `build.gradle.kts`
2. Update `version` in `rust/Cargo.toml`
3. Use TypeScript script: `deno run -A scripts/ts/change_swc4j_version.ts <new-version>`

## Code Style

- Java target: Java 8 (1.8)
- Rust edition: 2024
- Java classes use comprehensive Javadoc
- Rust code uses standard rustfmt formatting
