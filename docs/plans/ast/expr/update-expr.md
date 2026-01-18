# Update Expression Implementation Plan

## Overview

This document outlines the implementation plan for supporting update expressions (`++` and `--` operators) in TypeScript to JVM bytecode compilation. Update expressions modify a variable's value and return either the old value (postfix) or new value (prefix).

**Current Status:** ❌ NOT IMPLEMENTED (0% complete)
- ❌ **Prefix Increment (`++i`)** - NOT implemented
- ❌ **Postfix Increment (`i++`)** - NOT implemented
- ❌ **Prefix Decrement (`--i`)** - NOT implemented
- ❌ **Postfix Decrement (`i--`)** - NOT implemented

**Implementation File:** To be created at `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/UpdateExpressionGenerator.java`

**Test File:** To be created at `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/TestCompileUpdateExpr.java`

**AST Definition:** [Swc4jAstUpdateExpr.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/Swc4jAstUpdateExpr.java)

**Enum Definition:** [Swc4jAstUpdateOp.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/enums/Swc4jAstUpdateOp.java)

---

## Update Expression Fundamentals

### Operator Types

Update expressions have two operations:
1. **PlusPlus (`++`)** - Increment by 1
2. **MinusMinus (`--`)** - Decrement by 1

Each operation can be used in two forms:
- **Prefix:** `++x` or `--x` - Modify first, then return new value
- **Postfix:** `x++` or `x--` - Return old value, then modify

### JavaScript/TypeScript Behavior

```typescript
// Prefix: increment/decrement BEFORE returning value
let x = 5;
let a = ++x;  // x = 6, a = 6
let b = --x;  // x = 5, b = 5

// Postfix: return value BEFORE increment/decrement
let y = 5;
let c = y++;  // c = 5, y = 6
let d = y--;  // d = 6, y = 5

// Standalone (no difference between prefix and postfix)
x++;  // x incremented, result discarded
++x;  // x incremented, result discarded
```

### JVM Bytecode Mapping

**Local Variable (simple case):**
```
// Prefix ++x (int)
iload x          // Load current value
iconst_1         // Push 1
iadd             // Add
dup              // Duplicate new value (for return)
istore x         // Store new value
                 // New value remains on stack

// Postfix x++ (int)
iload x          // Load current value
dup              // Duplicate old value (for return)
iconst_1         // Push 1
iadd             // Add
istore x         // Store new value
                 // Old value remains on stack
```

**Note:** The key difference is WHEN the value is duplicated:
- **Prefix:** Duplicate AFTER modification
- **Postfix:** Duplicate BEFORE modification

---

## AST Structure

```java
public class Swc4jAstUpdateExpr extends Swc4jAst implements ISwc4jAstExpr {
    protected ISwc4jAstExpr arg;        // The expression being updated
    protected Swc4jAstUpdateOp op;      // PlusPlus or MinusMinus
    protected boolean prefix;            // true for ++x, false for x++
}
```

**Key Fields:**
- `arg`: The left-hand side expression (must be assignable - variable, property, array element)
- `op`: Either `PlusPlus` or `MinusMinus`
- `prefix`: Determines whether operation happens before (true) or after (false) value is returned

---

## Implementation Strategy

### Phase 1: Local Variables (Priority: HIGH)

Support increment/decrement of local variables only.

**Scope:**
- ✅ Simple local variables: `i++`, `++i`, `count--`, `--count`
- ✅ All numeric types: `int`, `long`, `float`, `double`, and wrapper types
- ✅ Both prefix and postfix forms
- ✅ Used as expressions: `x = i++`, `return ++count`
- ✅ Used as standalone statements: `i++;` (result discarded)

**Not in Phase 1:**
- ❌ Member access: `obj.count++`
- ❌ Array access: `arr[i]++`
- ❌ Compound expressions: `(x + y)++` (invalid but should error gracefully)

**Bytecode Strategy:**

```java
// For local variable increment/decrement:
case PlusPlus, MinusMinus -> {
    if (!(updateExpr.getArg() instanceof Swc4jAstIdent ident)) {
        throw new Swc4jByteCodeCompilerException("Only local variables supported in Phase 1");
    }

    String varName = ident.getSym();
    LocalVariable localVar = context.getLocalVariable(varName);
    String varType = localVar.getType();
    int varIndex = localVar.getIndex();

    boolean isIncrement = updateExpr.getOp() == Swc4jAstUpdateOp.PlusPlus;

    // Optimize for int using iinc instruction
    if (varType.equals("I") && returnTypeInfo != null) {
        if (updateExpr.isPrefix()) {
            // ++i: increment, then load
            code.iinc(varIndex, isIncrement ? 1 : -1);
            code.iload(varIndex);
        } else {
            // i++: load, increment
            code.iload(varIndex);
            code.iinc(varIndex, isIncrement ? 1 : -1);
        }
    } else {
        // For other types or standalone statements
        generateGeneralUpdateLogic(code, cp, varType, varIndex, isIncrement, updateExpr.isPrefix(), returnTypeInfo);
    }
}
```

**Test Coverage:**
- Basic increment/decrement for all numeric types
- Prefix vs postfix behavior verification
- Use in expressions vs standalone statements
- Edge values (0, 1, MAX_VALUE, MIN_VALUE)
- Wrapper types (Integer, Long, etc.)

### Phase 2: Member Access (Priority: MEDIUM)

Support increment/decrement of object properties.

**Scope:**
- ✅ Direct property access: `obj.count++`, `++person.age`
- ✅ This property: `this.value++`
- ✅ Nested properties: `obj.inner.count++`

**Challenges:**
- Need to duplicate object reference for both get and set
- Must call getter (for objects) or use getfield (for fields)
- Must call setter or use putfield after modification
- More complex stack management

**Bytecode Strategy:**

```java
// For property access: obj.prop++
// 1. Load object reference
// 2. Duplicate for later putfield
// 3. getfield to load current value
// 4. For postfix: duplicate old value
// 5. Increment/decrement
// 6. For prefix: duplicate new value
// 7. putfield to store new value
// 8. Old (postfix) or new (prefix) value remains on stack

// Pseudocode:
aload obj         // Load object
dup               // Duplicate for putfield
getfield prop     // Load current value
[dup for postfix] // Save old value if postfix
iconst_1
iadd              // Increment
[dup for prefix]  // Save new value if prefix
putfield prop     // Store new value
                  // Saved value on stack
```

**Test Coverage:**
- Property increment/decrement
- This property access
- Nested property chains
- Different property types

### Phase 3: Array Access (Priority: MEDIUM)

Support increment/decrement of array elements.

**Scope:**
- ✅ Simple array access: `arr[0]++`, `++data[i]`
- ✅ Computed indices: `arr[i + 1]++`
- ✅ All array types: int[], long[], Object[], etc.

**Challenges:**
- Must duplicate both array reference AND index
- Need to handle different array types (iaload vs laload vs aaload)
- Must use corresponding store instruction
- Complex stack manipulation

**Bytecode Strategy:**

```java
// For array element: arr[i]++
// 1. Load array reference
// 2. Load index
// 3. Duplicate both (for later store)
// 4. Load array element
// 5. For postfix: duplicate old value
// 6. Increment/decrement
// 7. For prefix: duplicate new value
// 8. Store array element
// 9. Saved value on stack

// Pseudocode:
aload arr         // Load array
iload i           // Load index
dup2              // Duplicate array ref and index
iaload            // Load element
[dup for postfix]
iconst_1
iadd
[dup for prefix]
iastore           // Store element
                  // Saved value on stack
```

**Test Coverage:**
- Array element increment/decrement
- Different array types
- Computed indices
- Boundary conditions

### Phase 4: Edge Cases & Optimization (Priority: LOW)

**Optimizations:**
- Use `iinc` instruction for local int variables when possible (most efficient)
- Detect standalone statements where return value is unused (can skip dup instructions)
- Optimize constant increments

**Edge Cases to Handle:**
- Invalid targets (literals, method calls) - should throw error
- Type overflow behavior (consistent with Java/JavaScript)
- Null handling for wrapper types
- Double/float precision

---

## Complete Edge Cases List

### Category 1: Basic Operand Types

1. **Integer types:**
   - Primitive: `int`, `byte`, `short`
   - Wrapper: `Integer`, `Byte`, `Short`
   - Edge values: 0, 1, -1, MAX_VALUE, MIN_VALUE

2. **Long type:**
   - Primitive: `long`
   - Wrapper: `Long`
   - Edge values: 0L, 1L, -1L, MAX_VALUE, MIN_VALUE

3. **Floating-point types:**
   - Primitive: `float`, `double`
   - Wrapper: `Float`, `Double`
   - Edge values: 0.0, 1.0, -1.0, NaN, POSITIVE_INFINITY, NEGATIVE_INFINITY

4. **Invalid types** (should error):
   - Boolean: `let b = true; b++;` ❌
   - String: `let s = "5"; s++;` ❌
   - Object: `let o = {}; o++;` ❌
   - Null: `let n = null; n++;` ❌
   - Undefined: `let u; u++;` ❌

### Category 2: Prefix vs Postfix Behavior

5. **Prefix increment (`++x`):**
   - Increments BEFORE returning value
   - Returns new value
   - Test: `let x = 5; let y = ++x;` → x=6, y=6

6. **Postfix increment (`x++`):**
   - Returns old value BEFORE incrementing
   - Test: `let x = 5; let y = x++;` → x=6, y=5

7. **Prefix decrement (`--x`):**
   - Decrements BEFORE returning value
   - Returns new value
   - Test: `let x = 5; let y = --x;` → x=4, y=4

8. **Postfix decrement (`x--`):**
   - Returns old value BEFORE decrementing
   - Test: `let x = 5; let y = x--;` → x=4, y=5

### Category 3: Usage Context

9. **Standalone statement:**
   - Result is discarded
   - No difference between prefix and postfix
   - Test: `x++;` vs `++x;` (both just increment)

10. **In assignment:**
    - Test: `y = x++;` vs `y = ++x;`
    - Verify different values assigned to y

11. **In return statement:**
    - Test: `return x++;` vs `return ++x;`
    - Verify different return values

12. **In expression:**
    - Test: `z = x++ + 10;` vs `z = ++x + 10;`
    - Test: `arr[i++]` (use old value as index, then increment)

13. **Multiple updates in same expression:**
    - Test: `x++ + x++` (undefined order in JavaScript, but deterministic in bytecode)
    - Test: `arr[i++] = i++` (complex evaluation order)

### Category 4: Target Types

14. **Local variable:**
    - Test: `let x = 5; x++;`
    - Simple case with known index

15. **Member access:**
    - Test: `obj.count++`
    - Requires getfield/putfield or getter/setter
    - Test: `this.value++`
    - Test: `obj.inner.count++` (chained access)

16. **Array element:**
    - Test: `arr[0]++`
    - Test: `arr[i]++`
    - Test: `arr[i++]++` (nested update)
    - Test: `matrix[i][j]++` (2D array)

17. **Invalid targets** (should throw error):
    - Literals: `5++` ❌
    - Expressions: `(x + y)++` ❌
    - Method calls: `getX()++` ❌
    - Constants: `const MAX = 10; MAX++;` ❌

### Category 5: Type Conversion & Widening

18. **Byte overflow:**
    - Test: `let b: byte = 127; b++;` → -128 (wraps around)

19. **Integer overflow:**
    - Test: `let i = 2147483647; i++;` → -2147483648

20. **Long overflow:**
    - Test: `let l = 9223372036854775807L; l++;` → wraps to MIN_VALUE

21. **Float/Double precision:**
    - Test: `let d = 0.1; d++; d++;` → 2.1 (floating point precision)
    - Test: `let f: float = 1e38; f++;` (large numbers)

22. **Type widening in mixed context:**
    - Test: `let b: byte = 5; let i: int = b++;` (byte incremented, result widened to int)

### Category 6: Wrapper Types & Boxing

23. **Integer wrapper:**
    - Test: `let i: Integer = 5; i++;` (unbox, increment, box)

24. **Long wrapper:**
    - Test: `let l: Long = 10L; l++;`

25. **Double wrapper:**
    - Test: `let d: Double = 5.5; d++;`

26. **Null wrapper:**
    - Test: `let i: Integer = null; i++;` (should throw NullPointerException)

27. **Auto-boxing in return:**
    - Test: Method with `Integer` return type, `return i++;` where i is primitive int

### Category 7: Compound Scenarios

28. **Chained updates:**
    - Test: `x = y = z++;` (right-to-left evaluation)

29. **Update in loop:**
    - Test: `for (let i = 0; i < 10; i++)` (very common case)
    - Test: `while (count-- > 0)` (decrement in condition)

30. **Update with operators:**
    - Test: `x++ + y++`
    - Test: `arr[i++] + arr[i++]`
    - Test: `x++ * 2`

31. **Update in method call:**
    - Test: `foo(x++, y++)` (argument evaluation order)
    - Test: `Math.max(i++, j++)`

32. **Update in array construction:**
    - Test: `new int[]{i++, i++, i++}` (element evaluation order)

### Category 8: Optimization Cases

33. **iinc optimization for int:**
    - Local int variable can use efficient `iinc` instruction
    - Test: Verify bytecode uses `iinc` when appropriate

34. **Result discarded:**
    - Standalone statement doesn't need to duplicate value
    - Test: Bytecode for `x++;` vs `y = x++;`

35. **Constant folding:**
    - Cannot fold (unlike unary minus) because it modifies state
    - But can optimize local access

### Category 9: Error Cases

36. **Const variable:**
    - Test: `const x = 5; x++;` (should error - assignment to constant)

37. **Read-only property:**
    - Test: Update of property without setter (should error)

38. **Invalid operand type:**
    - Test: `"hello"++` (should error)
    - Test: `true++` (should error)

39. **Undefined variable:**
    - Test: `notDefined++` (should error - variable not found)

### Category 10: Complex Access Patterns

40. **Computed member access:**
    - Test: `obj["prop" + i]++`
    - Test: `obj[key]++` where key is variable

41. **Array with computed index:**
    - Test: `arr[i + j]++`
    - Test: `arr[getIndex()]++`

42. **Nested array access:**
    - Test: `arr[i++][j++]++` (multiple levels)

43. **Optional chaining (if supported):**
    - Test: `obj?.count++` (may not be applicable)

### Category 11: Thread Safety & Atomicity

44. **Non-atomic nature:**
    - Update expression is NOT atomic (read-modify-write)
    - Multiple instructions, not thread-safe
    - Document: Not equivalent to AtomicInteger.incrementAndGet()

45. **Race conditions:**
    - Same variable updated from multiple contexts
    - Note: This is a semantic issue, not a compiler issue

### Category 12: Special JVM Cases

46. **Wide local variables:**
    - Long and double take 2 local variable slots
    - Test: Ensure proper slot management

47. **Stack depth:**
    - Prefix needs extra stack slot for duplication
    - Test: Deep nesting doesn't overflow stack

48. **Method size limit:**
    - Many updates in one method
    - Test: Doesn't exceed 65535 byte method limit

### Category 13: TypeScript-Specific

49. **Type annotation mismatch:**
    - Test: `let x: number = 5; x++;` (should work)
    - Test: `let x: string = "5"; x++;` (should error)

50. **Strict null checks:**
    - Test: Behavior with `strictNullChecks` enabled
    - Nullable types: `let x: number | null = 5; x++;`

---

## Implementation Phases

### Phase 1: Local Variables ✅ Priority
**Goal:** Basic increment/decrement of local variables

**Tasks:**
1. Create UpdateExpressionGenerator.java
2. Implement basic increment (++) for int local variables
3. Implement basic decrement (--) for int local variables
4. Handle prefix vs postfix for int
5. Extend to all primitive numeric types (byte, short, long, float, double)
6. Add wrapper type support (Integer, Long, Double, etc.)
7. Optimize using `iinc` instruction for int variables
8. Handle standalone vs expression context

**Test Count:** 40-50 tests
- 10 tests: Basic prefix/postfix for all numeric types
- 10 tests: Edge values (MAX_VALUE, MIN_VALUE, overflow)
- 10 tests: Wrapper types
- 10 tests: Expression context vs standalone
- 10 tests: Error cases (invalid types, const variables)

**Deliverables:**
- UpdateExpressionGenerator.java
- TestCompileUpdateExpr.java (Phase 1 tests)
- Documentation update

### Phase 2: Member Access 🔶 Medium Priority
**Goal:** Support obj.prop++ and obj.prop--

**Tasks:**
1. Detect member expression as update target
2. Generate code for field access (getfield/putfield)
3. Handle method-based properties (getter/setter)
4. Manage stack for object reference duplication
5. Support `this` property updates
6. Handle nested property access

**Test Count:** 20-30 tests
- 10 tests: Basic property increment/decrement
- 5 tests: This property access
- 5 tests: Nested properties
- 5 tests: Different property types
- 5 tests: Error cases (read-only properties)

**Deliverables:**
- UpdateExpressionGenerator.java (Phase 2)
- TestCompileUpdateExpr.java (Phase 2 tests)

### Phase 3: Array Access 🔶 Medium Priority
**Goal:** Support arr[i]++ and arr[i]--

**Tasks:**
1. Detect array access as update target
2. Generate code for array element access (iaload/iastore, etc.)
3. Handle index expression evaluation
4. Manage stack for array + index duplication
5. Support all array types
6. Handle nested array access

**Test Count:** 20-30 tests
- 10 tests: Basic array increment/decrement
- 5 tests: Computed indices
- 5 tests: Different array types
- 5 tests: Nested arrays
- 5 tests: Error cases (index out of bounds - runtime)

**Deliverables:**
- UpdateExpressionGenerator.java (Phase 3)
- TestCompileUpdateExpr.java (Phase 3 tests)

### Phase 4: Optimization & Edge Cases ⚪ Low Priority
**Goal:** Optimize and handle all edge cases

**Tasks:**
1. Optimize for standalone statements (skip dup)
2. Verify overflow behavior matches JavaScript
3. Add comprehensive error messages
4. Performance testing
5. Documentation completion

**Test Count:** 10-20 tests
- Edge cases from Categories 10-13

**Deliverables:**
- Optimized implementation
- Complete test coverage
- Performance benchmarks
- Full documentation

---

## Bytecode Instruction Reference

### Load Instructions
- `iload <index>` - Load int from local variable
- `lload <index>` - Load long (takes 2 slots)
- `fload <index>` - Load float
- `dload <index>` - Load double (takes 2 slots)
- `aload <index>` - Load reference

### Store Instructions
- `istore <index>` - Store int to local variable
- `lstore <index>` - Store long
- `fstore <index>` - Store float
- `dstore <index>` - Store double
- `astore <index>` - Store reference

### Array Instructions
- `iaload`, `laload`, `faload`, `daload`, `aaload` - Load from array
- `iastore`, `lastore`, `fastore`, `dastore`, `aastore` - Store to array

### Field Instructions
- `getfield` - Get instance field
- `putfield` - Set instance field
- `getstatic` - Get static field
- `putstatic` - Set static field

### Arithmetic Instructions
- `iadd`, `ladd`, `fadd`, `dadd` - Add
- `isub`, `lsub`, `fsub`, `dsub` - Subtract
- `iinc <index> <const>` - Increment local int variable (OPTIMIZATION)

### Stack Manipulation
- `dup` - Duplicate top stack value
- `dup2` - Duplicate top 2 stack values (for long/double or array + index)
- `dup_x1` - Duplicate top and insert below second
- `dup2_x1` - Duplicate top 2 and insert below third

### Constants
- `iconst_1`, `iconst_m1` - Load constant 1 or -1
- `lconst_1` - Load long 1L
- `fconst_1` - Load float 1.0f
- `dconst_1` - Load double 1.0

---

## Type Mapping Reference

| TypeScript Type | JVM Descriptor | Load | Store | Add | Sub | Constant 1 |
|-----------------|----------------|------|-------|-----|-----|------------|
| `number` (int) | `I` | iload | istore | iadd | isub | iconst_1 |
| `number` (long) | `J` | lload | lstore | ladd | lsub | lconst_1 |
| `number` (float) | `F` | fload | fstore | fadd | fsub | fconst_1 |
| `number` (double) | `D` | dload | dstore | dadd | dsub | dconst_1 |
| `Integer` | `Ljava/lang/Integer;` | aload | astore | (unbox first) | (unbox first) | - |
| `Long` | `Ljava/lang/Long;` | aload | astore | (unbox first) | (unbox first) | - |
| `Double` | `Ljava/lang/Double;` | aload | astore | (unbox first) | (unbox first) | - |

---

## Known Limitations

1. **Not Atomic:** Update expressions compile to multiple instructions (load, modify, store) and are not thread-safe
2. **Evaluation Order:** In complex expressions like `arr[i++] = i++`, evaluation order follows left-to-right but may differ from JavaScript engines
3. **const Variables:** Update of const variables should be caught at TypeScript compilation, but may error at bytecode generation
4. **Side Effects:** Update expressions with side effects in the target (e.g., `obj[sideEffect()]++`) evaluate side effects once

---

## Success Criteria

- [ ] All 4 operations implemented (prefix/postfix increment/decrement)
- [ ] Supports local variables (Phase 1)
- [ ] Supports member access (Phase 2)
- [ ] Supports array access (Phase 3)
- [ ] 80+ comprehensive tests covering all edge cases
- [ ] Proper error handling for invalid targets
- [ ] Optimized bytecode (uses `iinc` where possible)
- [ ] Complete documentation
- [ ] All tests passing ✅

---

## References

- **JVM Specification:** Chapter 6 - Instructions
- **JavaScript Specification:** ECMAScript Section 12.4 - Update Expressions
- **TypeScript Specification:** Section 4.17 - Increment and Decrement Operators
- **Existing Implementation:** UnaryExpressionGenerator.java (for reference pattern)
- **Test Reference:** TestCompileUnaryExprMinus.java (for test structure)
