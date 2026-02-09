# AGENTS.md

This file guides coding agents working in this repository. Keep changes minimal, consistent, and testable.

## Project Overview
- `swc4j` bridges SWC (Rust) to Java through JNI.
- Main architecture: Java API -> JNI (`Swc4jNative`) -> Rust core.
- TypeScript-to-JVM bytecode compiler lives under `src/main/java/com/caoccao/javet/swc4j/compiler`.
- Bytecode compiler support is currently JDK 17 only (`JdkVersion.JDK_17`).

## Build, Test, and Verification

### Gradle (primary for Java work)
- Build: `./gradlew build`
- Clean: `./gradlew clean`
- Unit tests: `./gradlew test`
- Performance tests: `./gradlew performanceTest`
- Single test class: `./gradlew test --tests "com.caoccao.javet.swc4j.TestSwc4j"`
- Single test method: `./gradlew test --tests "com.caoccao.javet.swc4j.TestSwc4j.testGetVersion"`
- Javadoc: `./gradlew javadoc`
- JNI headers: `./gradlew buildJNIHeaders`

### Rust (only when requested or relevant)
- Rust code is under `rust/`.
- Build: `cargo build` / `cargo build --release`
- Test: `cargo test`
- Format: `cargo fmt`

## Source of Truth for Compiler Changes

### Where to implement bytecode features
- Entry compiler: `src/main/java/com/caoccao/javet/swc4j/compiler/ByteCodeCompiler17.java`
- Processor wiring: `src/main/java/com/caoccao/javet/swc4j/compiler/ByteCodeCompiler.java`
- AST processors: `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/**`
- Type inference/mapping: `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/TypeResolver.java`
- ASM utilities: `src/main/java/com/caoccao/javet/swc4j/compiler/asm/**`

### JDK reference requirement
- When generating/changing bytecode semantics, reference JDK 17 compiler sources in `../jdk` (for example `Gen.java`, `Items.java`) to match opcode patterns and stack behavior.

### Typical feature-completion checklist (compiler)
1. Add dispatch handling in statement/expression processors.
2. Implement generation in specialized processor(s).
3. Update type inference in `TypeResolver` if expression/statement result types are affected.
4. Add/expand tests (happy path + edge/error cases).
5. Update docs/plans (especially `docs/plans/todo.md`) and remove completed TODO items.

## Coding Conventions

### General
- Preserve Apache 2.0 license headers.
- Follow existing file structure: package -> imports -> Javadoc -> class.
- Keep methods focused and aligned with surrounding processor style.
- Avoid unrelated refactors.

### Java
- Project compiles with Java 17 (`build.gradle.kts`).
- Use existing style in each package (including pattern-matching `instanceof` where already used).
- Do not use fully qualified names in code; import types instead.
- Use AssertJ and JUnit 5 patterns already in this repo.
- Keep exception messages explicit and actionable.
- For compiler errors, prefer `Swc4jByteCodeCompilerException` with AST/source context.

### Imports
- Match the local file’s convention.
- In compiler packages, wildcard imports for dense AST type sets are common and acceptable.

### ASM / Bytecode
- Do not add external bytecode libraries.
- If new low-level helpers are needed, place them under `src/main/java/com/caoccao/javet/swc4j/compiler/asm`.

## JNI / Codegen Safety
- Do not manually edit generated JNI blocks.
- Generated Rust sections are delimited by markers like `/* StructName Begin */` / `/* StructName End */`.
- Prefer generator workflows/tests for regeneration.

## Testing Conventions
- Tests live under `src/test/java/**`.
- Compiler tests usually:
  - extend `BaseTestCompileSuite`
  - use `@ParameterizedTest` + `@EnumSource(JdkVersion.class)`
  - assert with AssertJ
- Follow existing assertion style, including grouped assertions with `List.of()`, `Map.of()`, and `SimpleMap.of()` when it improves readability.
- For pattern reference, inspect tests under:
  - `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/trystmt`
- Temporary or exploratory tests can be added under:
  - `src/test/java/com/caoccao/javet/temp`

## Documentation and Planning
- Relevant docs:
  - `docs/README.md`
  - `docs/typescript_to_jvm_bytecode.md`
  - `docs/plans/todo.md`
- If a planned feature is fully implemented, remove its section from `docs/plans/todo.md`.
- Do not create new documentation files unless requested.

## Working Safely
- Keep changes scoped to the request.
- Ignore `rust/` for Java-only compiler tasks unless explicitly asked.
- Before finishing Java/compiler tasks, run:
  - `./gradlew test`
  - `./gradlew javadoc`
