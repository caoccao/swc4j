# AGENTS.md

This file guides coding agents working in this repository. Keep changes consistent with existing conventions.

## Project Overview
- swc4j is SWC for Java: Java API + JNI bridge + Rust core.
- Primary language: Java (target 1.8) with Rust native library (edition 2024).
- Architecture: Java API -> JNI (Swc4jNative) -> Rust core -> deno_ast.
- JNI code generation exists; do not hand-edit generated blocks.

## Build, Test, Lint

### Gradle (Java)
- Build: `./gradlew build`
- Clean: `./gradlew clean`
- Run all tests (excluding performance): `./gradlew test`
- Run performance tests: `./gradlew performanceTest`
- Run a single test class: `./gradlew test --tests "com.caoccao.javet.swc4j.TestSwc4j"`
- Run a single test method: `./gradlew test --tests "com.caoccao.javet.swc4j.TestSwc4j.testGetVersion"`
- JNI headers: `./gradlew buildJNIHeaders`
- JARs: `./gradlew jar sourcesJar javadocJar`
- Generate POM: `./gradlew generatePomFileForGeneratePomPublication`

### Rust (native library in rust/)
- Build (debug): `cargo build`
- Build (release): `cargo build --release`
- Run Rust tests: `cargo test`
- Format (standard rustfmt): `cargo fmt`

### Cross-compile (Rust)
- See `rust/README.md` for target setup and cross-compile commands.
- Android builds use `cargo-ndk` and require `ANDROID_NDK_HOME`.

### Linting / Formatting
- Java: no explicit formatter or linter configured; follow existing style.
- Rust: use `cargo fmt` and follow rustfmt defaults.

## Code Style Guidelines

### General
- Preserve the Apache 2.0 license header at the top of files.
- Keep public APIs documented with Javadoc (including `@since`).
- Prefer small, focused classes and methods; use descriptive names.
- Maintain consistent structure: package -> imports -> Javadoc -> class.

### Java
- Target Java 8; avoid newer language features.
- Indentation: 4 spaces; braces on the same line.
- Naming:
  - Classes/Enums: PascalCase.
  - Interfaces: `I*` prefix (e.g., `ISwc4jLogger`).
  - Methods/fields: camelCase.
  - Constants: `UPPER_SNAKE_CASE`.
- Imports:
  - Group by package with blank lines between groups (java.* vs com.*).
  - Prefer explicit imports; avoid `*`.
- Null handling:
  - Use `Objects.requireNonNull()` or `AssertionUtils.notNull()`.
  - Avoid returning null where an object is expected.
- Error handling:
  - Use domain exceptions (`Swc4jCoreException`, `JavetSanitizerException`).
  - Prefer factory methods for exception creation (see `JavetSanitizerException`).
- Fluent setters return `this` (see sanitizer exceptions/options).
- Logging:
  - Java-side logging is minimal; keep output quiet by default.

### Rust
- Follow rustfmt defaults; do not hand-align beyond standard formatting.
- Use `anyhow::Result` for fallible helpers and propagate with `?`.
- Keep JNI entry points `extern "system"` and annotated `#[unsafe(no_mangle)]`.
- Error handling:
  - JNI calls return Java-side errors via `error::throw_*` helpers.
  - Prefer converting errors to messages and throwing Java exceptions.
- FFI safety:
  - Use JNI helper utilities for string conversions (`jni_utils`).
  - Convert raw JNI objects immediately and avoid double-free.

### JNI + Codegen
- JNI entry points live in `rust/src/lib.rs` and Java declarations in `Swc4jNative.java`.
- Codegen inserts Rust blocks between `/* StructName Begin */` and `/* StructName End */`.
- Do not edit generated blocks manually; run the Jni2Rust generator test if needed.

### Testing Conventions
- Java tests live under `src/test/java/...`.
- Performance tests are tagged with `@Tag("performance")` and are excluded from `./gradlew test`.
- When adding tests, follow existing naming patterns in the related package.

## Docs and References
- Top-level docs: `docs/README.md` and feature docs in `docs/features/`.
- Tutorials: `docs/tutorials/`.
- Rust build details: `rust/README.md`.

## Cursor / Copilot Rules
- No Cursor rules found in `.cursor/rules/` or `.cursorrules`.
- No Copilot instructions found in `.github/copilot-instructions.md`.

## Working Safely
- Avoid editing unrelated files.
- Keep changes minimal and consistent with existing patterns.
- If you touch versioning, update both Java and Rust versions (see `build.gradle.kts` and `rust/Cargo.toml`).
