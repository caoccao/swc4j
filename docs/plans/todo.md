# TypeScript -> JVM Bytecode: Missing Feature Plan

This report consolidates gaps found in `docs/plans/**` and cross-checked against existing Java compiler code and tests. Each missing feature below has its own plan section.

## Feature: Async/Await (Functions, Arrows, Try/Catch)
- Evidence: `docs/plans/ast/class/function.md`, `docs/plans/ast/expr/arrow.md`, `docs/plans/ast/stmt/try-stmt.md`.
- Status: Not supported by design; no state machine generation.
- Confidence: 25%.
- Plan: Define async lowering strategy (promise-like runtime or coroutine), add async AST handling, generate state machine, update StackMap logic.
- Tests: Add async function/arrow/try-catch cases with await, promise chains, and error propagation.

## Feature: Generators (Functions, Arrows)
- Evidence: `docs/plans/ast/class/function.md`, `docs/plans/ast/expr/arrow.md`.
- Status: Not supported; requires resumable execution state.
- Confidence: 20%.
- Plan: Design generator runtime, yield state machine, iterator protocol mapping, and integration with expression/statement generators.
- Tests: Basic yield, yield*, nested generators, and generator-in-loop cases.

## Feature: Decorators (Class/Method/Parameter)
- Evidence: `docs/plans/ast/class/class.md`, `docs/plans/ast/class/function.md`.
- Status: Not supported (intentionally excluded).
- Confidence: 30%.
- Plan: Decide on annotation or bytecode weaving strategy, define metadata model, wire decorator evaluation order.
- Tests: Class, method, and parameter decorator ordering and metadata visibility.

## Feature: Arrow Return Union Types
- Evidence: `docs/plans/ast/expr/arrow.md`.
- Status: Not supported; JVM requires single return type.
- Confidence: 45%.
- Plan: Add union coercion strategy (Object return + tagging), update TypeResolver, and ensure boxing rules.
- Tests: Union returns with numeric/object/string branches and nested arrows.

## Feature: Array Literal Semantics (Holes, Bounds, Length Growth)
- Evidence: `docs/plans/ast/expr/lit/array-lit.md`.
- Status: Holes untested, out-of-bounds behavior JS-incompatible, length grow not verified.
- Confidence: 65%.
- Plan: Define JS-compatible semantics for holes and bounds, implement auto-expand for ArrayList mode, add large-array init path.
- Tests: Sparse arrays, out-of-bounds reads/writes, `arr.length` grow/shrink/clear.

## Feature: Try/Catch Multi-Type Guard Lowering
- Evidence: `docs/plans/ast/stmt/try-stmt.md`.
- Status: Multiple catch-type branching not implemented.
- Confidence: 55%.
- Plan: Generate catch-all then branch on `instanceof`, or emit multi-catch table entries with type guards.
- Tests: Multiple type guards inside a single catch block.

## Feature: TypeScript Project -> Jar Entry Point
- Evidence: New requirement for `java -jar swc4j-<version>.jar <main-entry> -i <root-to-ts-project> -o <output-jar-file-path>`.
- Status: Not implemented.
- Confidence: 55%.
- Plan:
  - Add a CLI main entry that resolves project root, finds TS sources, parses, compiles to class files, and packages a runnable jar.
  - Define module/class naming rules from file paths and namespaces; map `<main-entry>` to the entry class and method.
  - Implement jar manifest generation (Main-Class) and resource copying policy.
  - Add deterministic output ordering and error aggregation for multi-file builds.
- Tests: CLI end-to-end build from sample TS project, jar executes entry, error formatting for compile failures.

### Error Reporting Gaps (Swc4jByteCodeCompilerException)
These need to be addressed so errors always include source snippet, line, column, and reason.
- No built-in formatting for span -> line/column/source snippet in `Swc4jByteCodeCompilerException`.
- Some exceptions are constructed with `ast` set to null (e.g., default error branches), losing span context.
- No source file path stored alongside AST/exception, so multi-file project errors cannot report filename.
- Compiler pathways pass only code strings; there is no centralized source map to resolve span offsets to line/column and snippet.
- Error propagation does not standardize a single formatter for all thrown `Swc4jByteCodeCompilerException` cases.

## Feature: With Statement
- Evidence: `StatementProcessor` lacks `Swc4jAstWithStmt` handling.
- Status: Not supported (dynamic scope cannot be safely compiled to JVM bytecode).
- Confidence: 10%.
- Plan: Reject with clear compile-time error explaining unsupported dynamic scope.
- Tests: with-statement rejection diagnostics.

## Feature: JSX AST Nodes
- Evidence: `ExpressionProcessor` lacks `Swc4jAstJsx*` handling; AST includes JSX nodes.
- Status: Not supported in bytecode compiler; requires prior JSX transform.
- Confidence: 20%.
- Plan: Require JSX-to-JS transform (e.g., via swc transform) before bytecode compilation; add guardrail error if JSX nodes appear.
- Tests: JSX input triggers clear error with location.

## Feature: Module Exports and TS Namespaces
- Evidence: No generator handles `Swc4jAstExport*`, `Swc4jAstNamedExport`, `Swc4jAstTsModuleDecl`, `Swc4jAstTsNamespaceDecl`, `Swc4jAstTsNamespaceExportDecl`.
- Status: Not implemented.
- Confidence: 45%.
- Plan: Define module system mapping to Java packages/classes; decide export semantics (public classes/methods), and integrate with jar entry point.
- Tests: Exported functions/classes referenced across files and namespaces.

## Feature: Meta Property Expressions
- Evidence: `ExpressionProcessor` lacks `Swc4jAstMetaPropExpr` handling.
- Status: Not supported; `import.meta` and `new.target` have no direct JVM equivalent.
- Confidence: 20%.
- Plan: Reject with clear errors; optionally support `new.target` only inside constructors with synthetic metadata.
- Tests: `import.meta` and `new.target` diagnostics.

## Feature: Super Property Expressions
- Evidence: `ExpressionProcessor` lacks `Swc4jAstSuperPropExpr` handling.
- Status: Not implemented.
- Confidence: 40%.
- Plan: Resolve super field/method access in class hierarchy, emit invokespecial/getfield/putfield as needed.
- Tests: Super property access in overridden methods and constructors.

## Feature: TS Type System Coverage in TypeResolver
- Evidence: `TypeResolver.mapTsTypeToDescriptor()` only handles `TsArrayType`, `TsKeywordType`, `TsTypeRef`, `TsFnType`.
- Status: Partial; many TS type nodes are not mapped.
- Confidence: 50%.
- Plan: Add mapping rules or explicit rejections for `TsUnionType`, `TsIntersectionType`, `TsConditionalType`, `TsMappedType`, `TsIndexedAccessType`, `TsTypeOperator`, `TsTypeQuery`, `TsImportType`, `TsInferType`, `TsTupleType`, `TsOptionalType`, `TsRestType`, `TsLitType`, `TsTplLitType`, `TsTypePredicate`.
- Tests: Type annotation parsing with each TS type feature and clear diagnostics.
