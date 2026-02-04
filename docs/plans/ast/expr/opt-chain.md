# Optional Chaining - Implementation Plan

## Overview

This document outlines the implementation plan for supporting optional chaining and optional calls in TypeScript to JVM bytecode compilation. Optional chaining evaluates a chain and returns null if any optional link is null.

**Current Status:** ✅ COMPLETE

**Syntax:** `obj?.prop`, `obj?.method()`, `fn?.()`

**Implementation File:** `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/OptionalChainExpressionProcessor.java` ✅ CREATED

**Test File:** `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/optchain/TestCompileAstOptionalChain.java` ✅ CREATED

**AST Definition:** [Swc4jAstOptChainExpr.java](../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/Swc4jAstOptChainExpr.java)

---

## Implementation Summary

### ✅ Completed Features

1. **Optional Member Access** - `obj?.prop` short-circuits to null
2. **Optional Method Calls** - `obj?.method()` short-circuits and returns null
3. **Optional Direct Calls** - `fn?.()` for functional interfaces
4. **Nested Chains** - `a?.b?.c` with correct short-circuit behavior
5. **Boxing for Nullability** - Primitive results are boxed to allow null

### Key Fixes Applied

1. **Expression Generator Wiring**
   - Added `Swc4jAstOptChainExpr` handling in `ExpressionProcessor`
   - Initialized and exposed `OptionalChainExpressionProcessor`

2. **Short-Circuit Control Flow**
   - Null checks at optional points
   - Shared temp slot for result to keep stack frames consistent

3. **Method Resolution for Optional Calls**
   - Resolves Java method overloads via registry
   - Boxing-aware matching and varargs support

4. **Member Access Support**
   - Array, ArrayList/List, String, LinkedHashMap, and field access paths
   - Boxes primitive member results for nullability

### Test Results

**All Optional Chain Tests Passing:**
- testOptionalMemberAccess ✅
- testOptionalMethodCall ✅
- testOptionalDirectCall ✅
- testOptionalCallShortCircuit ✅

---

## Implementation Architecture

### Core Components

- **OptionalChainExpressionProcessor**
  - Emits null checks and short-circuit jumps
  - Boxes primitive results to `Object`
  - Handles optional member and call chains

- **ExpressionProcessor**
  - Dispatches `Swc4jAstOptChainExpr`

### Bytecode Strategy

1. Evaluate chain base
2. If optional, null-check and jump to null result
3. Continue to next chain link
4. Store result in a temp slot
5. Load result to unify stack frames

### Return Type Behavior

- Optional chains return `Object` at the bytecode level
- Primitive member/call results are boxed for nullability

---

## Known Limitations

- Optional chaining in assignments is not supported (optional chain is not a valid assignment target)
- Optional chaining on non-resolvable user-defined method targets requires explicit type info

---

## Next Steps

- Add coverage for optional chaining with computed array indices and map keys
- Expand method resolution for optional calls on custom classes
