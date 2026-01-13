# Binary Expression Operations Implementation Plan

## Overview

This document outlines the implementation plan for supporting all 25 binary operations defined in `Swc4jAstBinaryOp` for TypeScript to JVM bytecode compilation.

**Current Status:** 12 of 25 operations implemented (48% complete)
- ✅ **Add (`+`)** - Fully implemented with numeric addition and string concatenation
- ✅ **Sub (`-`)** - Fully implemented with type widening and null handling
- ✅ **Mul (`*`)** - Fully implemented with type widening and null handling
- ✅ **Div (`/`)** - Fully implemented with type widening, null handling, and JVM-native division behavior
- ✅ **Mod (`%`)** - Fully implemented with type widening, null handling, and JVM-native remainder behavior
- ✅ **Exp (`**`)** - Fully implemented using Math.pow with type conversion and null handling
- ✅ **LShift (`<<`)** - Fully implemented with automatic shift amount masking and type conversion
- ✅ **RShift (`>>`)** - Fully implemented with sign-extension (arithmetic shift) and automatic masking
- ✅ **ZeroFillRShift (`>>>`)** - Fully implemented with zero-fill (logical shift) and automatic masking
- ✅ **BitAnd (`&`)** - Fully implemented with type widening and bitwise AND operation
- ✅ **BitOr (`|`)** - Fully implemented with type widening and bitwise OR operation
- ✅ **BitXor (`^`)** - Fully implemented with type widening and bitwise XOR operation
- ❌ **13 operations remaining**

**Implementation File:** [BinaryExpressionGenerator.java](../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/BinaryExpressionGenerator.java)  
**Test File:** [TestCompileBinExpr.java](../../src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/TestCompileBinExpr.java)  
**Enum Definition:** [Swc4jAstBinaryOp.java](../../src/main/java/com/caoccao/javet/swc4j/ast/enums/Swc4jAstBinaryOp.java)

---

## Complete Binary Operations List

| # | Operation | Symbol | Category | Status | Complexity |
|---|-----------|--------|----------|--------|------------|
| 0 | Add | `+` | Arithmetic | ✅ Implemented | Medium |
| 23 | Sub | `-` | Arithmetic | ✅ Implemented | Low |
| 18 | Mul | `*` | Arithmetic | ✅ Implemented | Low |
| 4 | Div | `/` | Arithmetic | ✅ Implemented | Medium |
| 17 | Mod | `%` | Arithmetic | ✅ Implemented | Medium |
| 7 | Exp | `**` | Arithmetic | ✅ Implemented | High |
| 14 | LShift | `<<` | Bitwise | ✅ Implemented | Medium |
| 22 | RShift | `>>` | Bitwise | ✅ Implemented | Medium |
| 24 | ZeroFillRShift | `>>>` | Bitwise | ✅ Implemented | Medium |
| 1 | BitAnd | `&` | Bitwise | ✅ Implemented | Low |
| 2 | BitOr | `\|` | Bitwise | ✅ Implemented | Low |
| 3 | BitXor | `^` | Bitwise | ✅ Implemented | Low |
| 5 | EqEq | `==` | Comparison | ❌ Not Implemented | High |
| 6 | EqEqEq | `===` | Comparison | ❌ Not Implemented | High |
| 19 | NotEq | `!=` | Comparison | ❌ Not Implemented | High |
| 20 | NotEqEq | `!==` | Comparison | ❌ Not Implemented | High |
| 15 | Lt | `<` | Comparison | ❌ Not Implemented | Medium |
| 16 | LtEq | `<=` | Comparison | ❌ Not Implemented | Medium |
| 8 | Gt | `>` | Comparison | ❌ Not Implemented | Medium |
| 9 | GtEq | `>=` | Comparison | ❌ Not Implemented | Medium |
| 12 | LogicalAnd | `&&` | Logical | ❌ Not Implemented | High |
| 13 | LogicalOr | `\|\|` | Logical | ❌ Not Implemented | High |
| 21 | NullishCoalescing | `??` | Special | ❌ Not Implemented | High |
| 11 | InstanceOf | `instanceof` | Special | ❌ Not Implemented | Very High |
| 10 | In | `in` | Special | ❌ Not Implemented | Very High |

---

## Current Implementation: Add Operation

### Implementation Pattern (Reference)

The Add operation serves as the reference implementation pattern:

```java
case Add -> {
    // 1. Infer types of left and right operands
    String leftType = TypeResolver.inferExpressionType(binExpr.getLeft(), context);
    String rightType = TypeResolver.inferExpressionType(binExpr.getRight(), context);
    
    // 2. Handle null literals (default to Object)
    if (leftType == null) leftType = "Ljava/lang/Object;";
    if (rightType == null) rightType = "Ljava/lang/Object;";
    
    // 3. String concatenation path
    if (leftType.equals("Ljava/lang/String;") || rightType.equals("Ljava/lang/String;")) {
        ByteCodeStringUtils.generateStringConcatenation(code, cp, binExpr, context, options);
    } 
    // 4. Numeric addition path
    else {
        // Infer result type (type widening)
        String resultType = TypeResolver.inferBinaryResultType(leftType, rightType);
        
        // Generate left operand
        ExpressionGenerator.generate(code, cp, binExpr.getLeft(), context, options);
        ByteCodeTypeUtils.unboxIfNeeded(code, leftType);
        ByteCodeTypeUtils.convertPrimitive(code, leftType, resultType);
        
        // Generate right operand
        ExpressionGenerator.generate(code, cp, binExpr.getRight(), context, options);
        ByteCodeTypeUtils.unboxIfNeeded(code, rightType);
        ByteCodeTypeUtils.convertPrimitive(code, rightType, resultType);
        
        // Emit appropriate add instruction
        code.iadd(); // or ladd/fadd/dadd based on resultType
    }
}
```

### Key Features
- **Type Inference:** Determines operand types before generating bytecode
- **Type Widening:** Promotes to widest type (e.g., int + long → long)
- **String Concatenation:** Uses StringBuilder optimization for efficient concatenation
- **Null Handling:** Treats null as Object type, renders "null" in strings
- **Boxing/Unboxing:** Automatic wrapper type handling via `ByteCodeTypeUtils`
- **Nested Optimization:** Flattens nested additions into single StringBuilder chain

---

## Implementation Tiers

### Tier 1: Simple Arithmetic (Low Complexity)

**Operations:** Sub (`-`), Mul (`*`)

**Implementation Strategy:**
- Reuse Add operation pattern completely
- Replace `iadd/ladd/fadd/dadd` with `isub/lsub/fsub/dsub` or `imul/lmul/fmul/dmul`
- Same type inference and widening logic
- **No string concatenation** (Sub/Mul don't support strings)

**JVM Instructions:**
- **int:** `isub`, `imul`
- **long:** `lsub`, `lmul`
- **float:** `fsub`, `fmul`
- **double:** `dsub`, `dmul`

**Edge Cases:**
- Overflow/underflow (follows JVM semantics, wraps around)
- Character arithmetic: `char - char → int`, `char * char → int`
- Null operands: Should error or convert to 0? (JavaScript coerces to NaN)

---

### Tier 2: Division and Modulo (Medium Complexity)

**Operations:** Div (`/`), Mod (`%`)

**Implementation Strategy:**
- Similar to Sub/Mul but with special handling
- **Division by zero:** JVM throws ArithmeticException for integers, returns Infinity/NaN for floats
- **Integer division:** Truncates toward zero (e.g., 7/2 = 3)
- Modulo has same divisor-zero behavior

**JVM Instructions:**
- **int:** `idiv`, `irem`
- **long:** `ldiv`, `lrem`
- **float:** `fdiv`, `frem`
- **double:** `ddiv`, `drem`

**Edge Cases:**
- **Division by zero:**
  - Integer: Throws `ArithmeticException` at runtime
  - Float/double: Returns `Infinity` or `NaN`
  - JavaScript behavior: Always returns `Infinity` or `NaN` (never throws)
- **Integer division truncation:** 7/2 = 3 (not 3.5)
- **Negative modulo:** -7 % 3 = -1 (JVM) vs JavaScript may differ
- **Float modulo precision:** Can produce unexpected results

**Decision Required:** Match JavaScript semantics (catch ArithmeticException, return Infinity) or allow JVM native behavior?

---

### Tier 3: Bitwise Operations (Medium Complexity)

**Operations:** BitAnd (`&`), BitOr (`|`), BitXor (`^`), LShift (`<<`), RShift (`>>`), ZeroFillRShift (`>>>`)

**Implementation Strategy:**
- **Type constraint:** Bitwise operations work on integers only
- **JavaScript semantics:** Converts operands to 32-bit signed integers (ToInt32)
- Implement conversion: float/double → int (truncate, mask to 32-bit)
- No type widening: Always operates on `int` or `long`

**JVM Instructions:**
- **int:** `iand`, `ior`, `ixor`, `ishl`, `ishr`, `iushr`
- **long:** `land`, `lor`, `lxor`, `lshl`, `lshr`, `lushr`

**Type Conversion Required:**
```java
// Convert float/double to int for bitwise ops (JavaScript ToInt32)
ByteCodeTypeUtils.convertPrimitive(code, "F", "I"); // f2i
code.ldc(0xFFFFFFFF);
code.iand(); // Mask to 32-bit
```

**Edge Cases:**
- **Float/double operands:** Must convert to int32 (JavaScript ToInt32 behavior)
- **Shift amount masking:** `x << 33` same as `x << 1` (only lower 5 bits used for int, 6 bits for long)
- **ZeroFillRShift on negative:** `>>>` treats number as unsigned (fills with 0)
- **Long vs int:** Should bitwise ops promote to long, or always use int?

**JavaScript ToInt32 Algorithm:**
1. Convert to number
2. If NaN/Infinity: return 0
3. Truncate to integer
4. Modulo 2^32
5. Map to range [-2^31, 2^31-1]

---

### Tier 4: Comparison Operators (High Complexity)

**Operations:** Lt (`<`), LtEq (`<=`), Gt (`>`), GtEq (`>=`), EqEq (`==`), EqEqEq (`===`), NotEq (`!=`), NotEqEq (`!==`)

**Implementation Strategy:**
- **Returns boolean:** Comparison operators return true/false
- **Requires branching:** Use conditional jump instructions
- **Boolean encoding:** Decide on int (0/1) or boolean type

**JVM Instructions - Numeric Comparison:**
```java
// Pattern for x < y (leaves 1 or 0 on stack)
<generate x>
<generate y>
// For int:
if_icmpge falseLabel   // Jump if x >= y
iconst_1               // True case
goto endLabel
falseLabel:
iconst_0               // False case
endLabel:
```

**JVM Instructions Available:**
- **int:** `if_icmplt`, `if_icmple`, `if_icmpgt`, `if_icmpge`, `if_icmpeq`, `if_icmpne`
- **long:** `lcmp` (returns -1/0/1), then `iflt/ifle/ifgt/ifge/ifeq/ifne`
- **float:** `fcmpl`/`fcmpg` (handles NaN), then `iflt/ifle/...`
- **double:** `dcmpl`/`dcmpg` (handles NaN), then `iflt/ifle/...`
- **Object:** `if_acmpeq`, `if_acmpne` (reference equality)

**Edge Cases:**
- **NaN comparisons:** All comparisons with NaN return false (except `!=` returns true)
  - Use `fcmpl` vs `fcmpg` based on NaN behavior needed
- **Type coercion for `==` vs `===`:**
  - `===` (strict): No type coercion, types must match
  - `==` (loose): Complex coercion rules (string to number, null == undefined, etc.)
- **String comparison:** `<`, `>`, etc. compare lexicographically (need String.compareTo)
- **Object comparison:** `==` checks reference equality, not value equality
- **null/undefined:** `null == undefined` is true, but `null === undefined` is false
- **Boolean to number:** `true == 1`, `false == 0`

**JavaScript `==` Coercion Rules (Partial List):**
1. If types match: Use `===` logic
2. null == undefined: true
3. Number == String: Convert string to number
4. Boolean: Convert to number (true→1, false→0)
5. Object: Call ToPrimitive
6. Everything else: false

**Decision Required:** Full JavaScript `==` coercion is very complex. Should we:
- Implement simplified version (type must match)?
- Implement full JavaScript semantics (very complex)?
- Only support `===` (strict equality) initially?

---

### Tier 5: Logical Operators (High Complexity)

**Operations:** LogicalAnd (`&&`), LogicalOr (`||`)

**Implementation Strategy:**
- **Short-circuit evaluation:** Must NOT evaluate right operand if not needed
  - `&&`: If left is false, don't evaluate right
  - `||`: If left is true, don't evaluate right
- **Returns operand value:** JavaScript `&&`/`||` return operand, not boolean
  - `5 && 7` returns `7` (not `true`)
  - `0 || "hello"` returns `"hello"`
- **Truthiness:** Convert operand to boolean for test, but keep original value

**JVM Implementation Pattern:**
```java
// x && y (short-circuit)
<generate x>
dup                    // Duplicate for truthiness test
<convert to boolean>   // Check if truthy
ifeq shortCircuitLabel // If false, skip right operand
pop                    // Remove left value
<generate y>           // Evaluate right
shortCircuitLabel:     // Left value already on stack
```

**Truthiness Conversion (JavaScript):**
- **Falsy:** `false`, `0`, `""`, `null`, `undefined`, `NaN`
- **Truthy:** Everything else

**Edge Cases:**
- **Type preservation:** Must return original operand, not boolean
- **Side effects:** Right operand may have side effects (only execute if needed)
- **Nested logical ops:** `a && b || c` requires careful ordering
- **Non-boolean operands:** `5 && "hello"` returns `"hello"`
- **Stack management:** Must ensure only one value remains on stack

---

### Tier 6: Special Operators (Very High Complexity)

#### NullishCoalescing (`??`)

**Semantics:** Returns right operand if left is `null` or `undefined`, otherwise left

**Implementation Strategy:**
```java
<generate left>
dup
ifnull rightLabel      // If null, use right
// Check for undefined (if tracking undefined separately)
goto endLabel
rightLabel:
pop                    // Remove left null
<generate right>
endLabel:
```

**Edge Cases:**
- **null vs undefined:** JavaScript distinguishes, JVM doesn't have undefined
- **Falsy values:** `0 ?? 5` returns `0` (not `5`), unlike `||`
- **Type preservation:** Must return original operand type

---

#### InstanceOf

**Semantics:** Tests if object is instance of a type

**Implementation Strategy:**
```java
<generate object>
instanceof <className>  // JVM instruction
// Result: 1 (true) or 0 (false) on stack
```

**Edge Cases:**
- **Type descriptor from AST:** Need to extract class name from right operand
- **Right operand must be type:** Not a runtime value (compile-time only in TypeScript)
- **Null object:** `null instanceof X` returns `false`
- **Primitive types:** Cannot use instanceof on primitives (need wrapper check)
- **Interface checks:** Works for interfaces too
- **Array types:** `[] instanceof Array`

**Challenges:**
- TypeScript type information may not map cleanly to JVM types
- Right operand is a type identifier, not an expression
- Need type resolution from TypeScript AST to JVM class names

---

#### In

**Semantics:** Tests if property exists in object

**JavaScript Example:** `'length' in [1,2,3]` returns `true`

**Challenges:**
- **Requires reflection:** JVM needs runtime introspection
- **Property lookup:** No direct JVM equivalent
- **Objects needed:** Requires object runtime, not available in current JVM compiler
- **Type information:** Need to know object structure at runtime

**Implementation Options:**
1. **Not supported:** Error at compile time (most realistic for current architecture)
2. **Generate reflection code:** Use Java reflection API (very complex)
3. **Require runtime support:** Need JavaScript-like object system (major architecture change)

**Edge Cases:**
- **Prototype chain:** JavaScript checks prototype chain, JVM doesn't have this
- **Non-object right operand:** Type error in JavaScript
- **Symbol properties:** JavaScript symbols not in JVM
- **Array indices:** `0 in [1,2,3]` returns `true`

**Recommendation:** Mark as unsupported until runtime object system exists

---

#### Exp (`**`)

**Semantics:** Exponentiation (e.g., `2 ** 3` = 8)

**Implementation Strategy:**
- **No JVM instruction:** No native exp instruction like `iadd`
- **Use Math.pow:** Call `java.lang.Math.pow(double, double)`
- **Type conversion:** Convert operands to double

**JVM Implementation:**
```java
<generate base>
ByteCodeTypeUtils.convertToDouble(code, baseType);
<generate exponent>
ByteCodeTypeUtils.convertToDouble(code, expType);
code.invokestatic(
    "java/lang/Math",
    "pow",
    "(DD)D"
);
```

**Edge Cases:**
- **Integer result:** `2 ** 3` should return `8` (int), not `8.0` (double)
  - Convert back to int if both operands were integers?
- **Negative exponents:** `2 ** -1` = 0.5 (always double result)
- **Special cases:**
  - `0 ** 0` = 1 (by convention, Math.pow returns 1)
  - `(-1) ** Infinity` = NaN
  - `1 ** Infinity` = NaN (JavaScript) vs 1.0 (Math.pow)
- **Precision:** Large exponents can overflow/underflow
- **Performance:** Method call overhead vs inline calculation

**JavaScript vs Java Math.pow differences:**
- Some edge cases differ (1^Infinity, NaN handling)
- May need wrapper method for exact JavaScript semantics

---

## Edge Cases Summary

### Universal Edge Cases (All Operations)

1. **Null Operands**
   - Current: Null defaults to `Object` type
   - JavaScript: Null coerces to 0 for arithmetic, "null" for strings
   - Decision needed: Match JavaScript null coercion or error?

2. **Undefined Operands**
   - JVM has no `undefined` concept
   - JavaScript: Undefined coerces to NaN for arithmetic
   - Options: Track undefined separately, or treat as null?

3. **Type Widening**
   - Current: Promotes to widest type (byte + long → long)
   - JavaScript: Converts to Number (64-bit float)
   - Works well for most cases

4. **Wrapper Types (Boxing/Unboxing)**
   - Current: `ByteCodeTypeUtils.unboxIfNeeded()` handles automatically
   - Edge: Null wrapper throws NullPointerException on unbox
   - Need null checks before unboxing?

5. **Mixed Type Operations**
   - Number + String = String (implemented for Add)
   - Other ops: Error or coerce?
   - JavaScript: Complex coercion, JVM: type errors

### Arithmetic Edge Cases

6. **Overflow/Underflow**
   - JVM: Silent wrapping (Integer.MAX_VALUE + 1 = Integer.MIN_VALUE)
   - JavaScript: Converts to Infinity or maintains precision in float
   - Acceptable to use JVM semantics?

7. **Division by Zero**
   - Integer: Throws `ArithmeticException`
   - Float: Returns `Infinity` or `NaN`
   - JavaScript: Always returns Infinity/NaN (never throws)
   - Should catch exception and return Infinity?

8. **Integer Division Truncation**
   - 7 / 2 = 3 (JVM integer division)
   - JavaScript: 7 / 2 = 3.5 (always float division)
   - **Critical:** May need to always use double division for JavaScript semantics

9. **Modulo with Negative Numbers**
   - -7 % 3 = -1 (JVM)
   - -7 % 3 = -1 (JavaScript, same)
   - Should match, but verify sign handling

### Bitwise Edge Cases

10. **Bitwise on Float/Double**
    - JavaScript: Converts to int32 via ToInt32 algorithm
    - JVM: Type error (bitwise only for int/long)
    - Must implement f2i/d2i conversion

11. **Shift Amount Masking**
    - `x << 33` same as `x << 1` (JVM masks to 5 bits for int)
    - JavaScript: Also masks (int: 5 bits, matches JVM)
    - Should work correctly

12. **ZeroFillRShift on Negative**
    - `>>>` treats as unsigned (fills with 0)
    - -1 >>> 1 = 2147483647
    - Requires `iushr` (not `ishr`)

### Comparison Edge Cases

13. **NaN Comparisons**
    - NaN < x, NaN > x, NaN == x: all false
    - NaN != x: true (only this is true)
    - Use `fcmpl` vs `fcmpg` for correct NaN ordering

14. **String Comparisons**
    - "10" < "9" = true (lexicographic)
    - Need String.compareTo() for strings
    - Mixed types: error or coerce?

15. **Loose Equality (`==`) Coercion**
    - null == undefined: true
    - 0 == "0": true (string→number)
    - false == 0: true (boolean→number)
    - Very complex, 28+ rules in spec

16. **Object Reference Equality**
    - `==` checks reference, not value
    - {} == {}: false (different objects)
    - Same in JavaScript and JVM

### Logical Edge Cases

17. **Truthiness**
    - Falsy: false, 0, "", null, undefined, NaN
    - Need helper to check truthiness
    - Must preserve original value (not convert to boolean)

18. **Short-circuit Side Effects**
    - `false && throwError()`: doesn't throw
    - `true || throwError()`: doesn't throw
    - Must not evaluate right if not needed

19. **Return Value Type**
    - `5 && 7` returns 7 (not true)
    - Must preserve operand types
    - Stack management critical

### Special Operator Edge Cases

20. **InstanceOf with Primitives**
    - Cannot use on primitives directly
    - Need to check wrapper types?
    - Or error at compile time?

21. **In Operator without Runtime**
    - Requires reflection or runtime object system
    - Not feasible in current architecture
    - Likely needs to be unsupported

22. **Exponentiation Result Type**
    - `2 ** 3` should be 8 (int) or 8.0 (double)?
    - JavaScript: Always returns number (float)
    - Math.pow returns double

---

## Test Coverage Requirements

Following the pattern in [TestCompileBinExpr.java](../../src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/TestCompileBinExpr.java), each operation needs:

### Basic Type Tests (per operation)
- [ ] All primitive types: byte, short, int, long, float, double, char
- [ ] All wrapper types: Byte, Short, Integer, Long, Float, Double, Character
- [ ] Mixed primitive and wrapper combinations
- [ ] Type widening cases (byte + long, int + double, etc.)
- [ ] Character arithmetic (char + int, char + char)

### Special Cases (per operation)
- [ ] Null operands (left null, right null, both null)
- [ ] Explicit type casting (`(x as double) + (y as double)`)
- [ ] Nested expressions (multiple ops in one expression)
- [ ] Const folding opportunities (compile-time evaluation)
- [ ] Edge values (MAX_VALUE, MIN_VALUE, zero)

### Operation-Specific Tests

**Division/Modulo:**
- [ ] Division by zero (integer and float)
- [ ] Integer division truncation
- [ ] Negative operands
- [ ] Remainder sign preservation

**Bitwise:**
- [ ] Float/double operands (ToInt32 conversion)
- [ ] Shift amounts (0, negative, > 32, > 64)
- [ ] ZeroFillRShift on negative numbers
- [ ] Bitwise operations on negative numbers

**Comparison:**
- [ ] NaN operands
- [ ] Infinity operands
- [ ] String comparisons
- [ ] Boolean result verification (0 or 1)
- [ ] Loose vs strict equality differences

**Logical:**
- [ ] Short-circuit verification (right side not evaluated)
- [ ] Truthiness of all types
- [ ] Return value type preservation
- [ ] Nested logical operations

**Special:**
- [ ] InstanceOf with various types (class, interface, array)
- [ ] InstanceOf with null
- [ ] NullishCoalescing vs LogicalOr difference (0, false)
- [ ] Exponentiation special cases (0^0, negative exponents)

### Performance Tests
- [ ] Nested operations (deeply nested expressions)
- [ ] String concatenation optimization (multiple additions)
- [ ] Type conversion overhead
- [ ] Const expression folding

---

## Recommended Implementation Order

### Phase 1: Foundation Arithmetic (Weeks 1-2)
1. **Sub (`-`)** - Lowest hanging fruit, identical to Add pattern
2. **Mul (`*`)** - Same pattern as Sub
3. **Div (`/`)** - Add division-by-zero handling
4. **Mod (`%`)** - Similar to Div

**Goal:** Complete basic arithmetic operations with full test coverage

### Phase 2: Bitwise Operations (Week 3)
5. **BitAnd (`&`)** - Implement ToInt32 conversion pattern
6. **BitOr (`|`)** - Reuse BitAnd pattern
7. **BitXor (`^`)** - Reuse BitAnd pattern
8. **LShift (`<<`)** - Add shift instructions
9. **RShift (`>>`)** - Signed shift
10. **ZeroFillRShift (`>>>`)** - Unsigned shift

**Goal:** Complete bitwise operations with proper type constraints

### Phase 3: Comparison Operators (Week 4)
11. **Lt (`<`)** - Implement branching pattern
12. **LtEq (`<=`)** - Reuse Lt pattern
13. **Gt (`>`)** - Reuse Lt pattern
14. **GtEq (`>=`)** - Reuse Lt pattern
15. **EqEqEq (`===`)** - Strict equality (simpler than `==`)
16. **NotEqEq (`!==`)** - Negation of `===`

**Goal:** Basic comparisons working, boolean result generation

### Phase 4: Equality with Coercion (Week 5)
17. **EqEq (`==`)** - Implement simplified or full coercion
18. **NotEq (`!=`)** - Negation of `==`

**Goal:** Equality working (complexity depends on coercion level chosen)

### Phase 5: Logical Operators (Week 6)
19. **LogicalAnd (`&&`)** - Implement short-circuit evaluation
20. **LogicalOr (`||`)** - Reuse short-circuit pattern
21. **NullishCoalescing (`??`)** - Null-specific short-circuit

**Goal:** Control flow and short-circuit evaluation working

### Phase 6: Special Operations (Week 7+)
22. **Exp (`**`)** - Math.pow integration
23. **InstanceOf** - Type checking (if feasible)
24. **In** - Property checking (likely unsupported)

**Goal:** Complete remaining operations where architecturally feasible

---

## Architecture Decisions Needed

### Decision 1: JavaScript Semantics Fidelity
**Question:** How strictly match JavaScript behavior vs use JVM native semantics?

**Options:**
- **A) Full JavaScript semantics** - Most compatible but complex
  - All division returns double (7/2 = 3.5)
  - Full `==` coercion (28+ rules)
  - Division by zero returns Infinity
  - Track undefined separately from null
- **B) Hybrid approach** - Balance compatibility and simplicity
  - Strict equality (`===`) only, error on `==`
  - Integer division for int/int, float for others
  - Allow JVM exceptions for division by zero
  - Treat undefined as null
- **C) JVM-native** - Simplest but least compatible
  - Integer division truncates
  - No type coercion
  - JVM exceptions propagate
  - TypeScript-like strict typing

**Recommendation:** Start with **Option B (Hybrid)**, add full JavaScript semantics incrementally

### Decision 2: Boolean Encoding
**Question:** How to represent boolean results from comparisons and logical operations?

**Options:**
- **A) int (0/1)** - JVM's natural boolean representation
  - Pro: Simple, efficient
  - Con: Type confusion (int vs boolean)
- **B) Boolean type** - Explicit boolean type
  - Pro: Type safety
  - Con: Boxing overhead, more complex
- **C) Context-dependent** - int in numeric context, Boolean in boolean context
  - Pro: Flexible
  - Con: Complex type tracking

**Recommendation:** **Option A (int)** for simplicity, considering adding Boolean type later

### Decision 3: Type Coercion Strategy
**Question:** How to handle mixed-type operations?

**Current for Add:** String + anything = string concatenation

**Options:**
- **A) Type must match** - Error on mixed types
- **B) Numeric promotion** - Promote to Number
- **C) Full JavaScript coercion** - Complex rules per operation

**Recommendation:** 
- Arithmetic: **Option B** (numeric promotion, error on string)
- Comparison: **Option A** initially (strict types), **Option C** for `==` later
- Bitwise: **Option B** (ToInt32 conversion)

### Decision 4: Null/Undefined Handling
**Question:** How to handle null and undefined operands?

**Options:**
- **A) Null = 0, undefined unsupported**
- **B) Both null and undefined = NaN/error**
- **C) Track undefined separately**
- **D) Null-safe: null op anything = null**

**Recommendation:** **Option A** initially (simplest), consider **Option C** for full compatibility

### Decision 5: Error Handling
**Question:** When to throw compile-time errors vs generate runtime checks?

**Options:**
- **A) Compile-time errors** - Strict checking
  - Pro: Catch issues early
  - Con: Less flexible
- **B) Runtime checks** - Generate try-catch blocks
  - Pro: Handles dynamic cases
  - Con: Performance overhead
- **C) Hybrid** - Compile-time when possible, runtime otherwise
  - Pro: Best of both
  - Con: Complex

**Recommendation:** **Option C** - Compile-time for obvious errors (string / string), runtime for dynamic cases

---

## Implementation Utilities Needed

### Type Conversion Utilities
Extend `ByteCodeTypeUtils`:
- [ ] `convertToInt32()` - JavaScript ToInt32 for bitwise ops
- [ ] `convertToBoolean()` - Truthiness check
- [ ] `convertToDouble()` - For Math.pow and division
- [ ] `boxIfNeeded()` - Complement to unboxIfNeeded
- [ ] `checkNull()` - Null checking before operations

### Comparison Utilities
New class: `ByteCodeComparisonUtils`:
- [ ] `generateNumericComparison()` - Pattern for <, <=, >, >=
- [ ] `generateEquality()` - Pattern for ==, ===, !=, !==
- [ ] `generateNaNCheck()` - NaN-aware comparisons
- [ ] `generateStringComparison()` - String.compareTo wrapper

### Logical Utilities
New class: `ByteCodeLogicalUtils`:
- [ ] `generateShortCircuitAnd()` - && with short-circuit
- [ ] `generateShortCircuitOr()` - || with short-circuit
- [ ] `generateTruthinessCheck()` - Convert to boolean without changing type
- [ ] `generateNullishCheck()` - ?? operator logic

### Label Management
Enhance `CodeBuilder`:
- [ ] Better label creation and management for branching
- [ ] Try-catch block generation for runtime errors
- [ ] Stack depth tracking for complex expressions

---

## Success Criteria

### Implementation Complete When:
- [ ] All 25 operations have implementation or documented "unsupported" status
- [ ] Test coverage >= 80% for each implemented operation
- [ ] All edge cases documented with test coverage
- [ ] Performance benchmarks show reasonable efficiency
- [ ] Documentation updated with examples and limitations

### Quality Gates:
- [ ] No regressions in existing Add operation tests
- [ ] All new tests pass
- [ ] Bytecode verification succeeds (valid class files)
- [ ] Runtime execution produces correct results
- [ ] Edge cases handled gracefully (error or correct result)

---

## Open Questions

1. **Should division always return double for JavaScript compatibility?**
   - Current: Integer division truncates
   - JavaScript: All division returns float
   - Impact: May break type inference

2. **How to handle `==` vs `===` coercion complexity?**
   - Simple: Only support `===`
   - Complex: Full JavaScript coercion rules
   - Trade-off: Compatibility vs implementation effort

3. **Should we track undefined separately from null?**
   - JavaScript distinguishes them
   - JVM has no undefined
   - Options: Map to null, use special marker, runtime wrapper

4. **How to handle In and InstanceOf without runtime?**
   - InstanceOf: Map TypeScript types to JVM types (feasible)
   - In: Requires object runtime (not feasible currently)
   - Decision: Mark In as unsupported?

5. **Should we optimize constant expressions at compile time?**
   - Example: `2 + 3` → `5` (const folding)
   - Benefit: Performance
   - Complexity: Need constant evaluator

6. **How to report unsupported operations to users?**
   - Compile error with clear message
   - Runtime error (generate throw bytecode)
   - Documentation only

---

## References

- **Current Implementation:** [BinaryExpressionGenerator.java](../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/BinaryExpressionGenerator.java)
- **Test Suite:** [TestCompileBinExpr.java](../../src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/TestCompileBinExpr.java)
- **Enum Definition:** [Swc4jAstBinaryOp.java](../../src/main/java/com/caoccao/javet/swc4j/ast/enums/Swc4jAstBinaryOp.java)
- **Type Utilities:** [ByteCodeTypeUtils.java](../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/utils/ByteCodeTypeUtils.java)
- **String Utilities:** [ByteCodeStringUtils.java](../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/utils/ByteCodeStringUtils.java)
- **Main Documentation:** [typescript_to_jvm_bytecode.md](../../typescript_to_jvm_bytecode.md)
- **ECMAScript Spec:** [ECMA-262 Binary Operators](https://tc39.es/ecma262/#sec-binary-operators)
- **JVM Spec:** [JVM Instruction Set](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html)

---

## Appendix: JVM Instruction Quick Reference

### Arithmetic Instructions
```
iadd, ladd, fadd, dadd  - Addition
isub, lsub, fsub, dsub  - Subtraction
imul, lmul, fmul, dmul  - Multiplication
idiv, ldiv, fdiv, ddiv  - Division
irem, lrem, frem, drem  - Remainder (modulo)
```

### Bitwise Instructions
```
iand, land              - Bitwise AND
ior, lor                - Bitwise OR
ixor, lxor              - Bitwise XOR
ishl, lshl              - Shift left
ishr, lshr              - Arithmetic shift right (sign extend)
iushr, lushr            - Logical shift right (zero fill)
```

### Comparison Instructions
```
lcmp                    - Compare long (-1, 0, 1)
fcmpl, fcmpg            - Compare float (NaN handling)
dcmpl, dcmpg            - Compare double (NaN handling)
```

### Conditional Branch Instructions
```
if_icmplt, if_icmple    - If int less than (or equal)
if_icmpgt, if_icmpge    - If int greater than (or equal)
if_icmpeq, if_icmpne    - If int equal (or not equal)
if_acmpeq, if_acmpne    - If reference equal (or not equal)
ifeq, ifne              - If zero (or non-zero)
iflt, ifle, ifgt, ifge  - If less/greater than zero
ifnull, ifnonnull       - If reference null (or not null)
```

### Type Conversion Instructions
```
i2l, i2f, i2d           - int to long/float/double
l2i, l2f, l2d           - long to int/float/double
f2i, f2l, f2d           - float to int/long/double
d2i, d2l, d2f           - double to int/long/float
i2b, i2c, i2s           - int to byte/char/short
```

### Object Instructions
```
instanceof              - Check if object is instance of type
checkcast               - Cast to type (throws ClassCastException)
```

---

*Last Updated: January 13, 2026*
*Status: Implementation Phase*
*Next Step: Implement comparison operators (Lt, LtEq, Gt, GtEq)*
