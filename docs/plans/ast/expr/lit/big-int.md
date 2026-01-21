# BigInt Literal Implementation Plan

## Overview

This document outlines the implementation plan for supporting JavaScript/TypeScript BigInt literals (`Swc4jAstBigInt`) and compiling them to JVM bytecode as **Java BigInteger** objects.

**Current Status:** 📋 **PLANNED** (Comprehensive plan created, test structure defined)

**Implementation File:** ❌ BigIntLiteralGenerator.java (needs to be created)

**Test Files:** 📝 **2 placeholder test files created** (out of 7 planned)
- [TestCompileAstBigIntBasic.java](../../../../../src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/bigint/TestCompileAstBigIntBasic.java) - 15 tests (Phase 1)
- [TestCompileAstBigIntConversion.java](../../../../../src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/bigint/TestCompileAstBigIntConversion.java) - 12 tests (Phase 2)

**AST Definition:** ✅ [Swc4jAstBigInt.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstBigInt.java)

**Last Updated:** 2026-01-21 - Planning complete, initial test structure created

---

## BigInt Representation Strategy

### JavaScript BigInt vs Java BigInteger

JavaScript ES2020 introduced `BigInt` as a primitive type for arbitrary-precision integers. TypeScript supports BigInt with the `bigint` type and literal syntax using the `n` suffix:

```typescript
const bigValue = 123456789012345678901234567890n  // BigInt literal
```

Java provides `java.math.BigInteger` for arbitrary-precision integers, which is a **reference type** (not primitive):

```java
BigInteger bigValue = new BigInteger("123456789012345678901234567890");
```

### Single Representation Mode

**BigInteger Mode (Only Mode)**
```typescript
const value = 123n  // → java.math.BigInteger
```
- Type: `Ljava/math/BigInteger;` (reference type)
- Range: Unlimited (arbitrary precision)
- Bytecode: `new BigInteger(String)` constructor
- Always a heap-allocated object

**Key Difference from Number Literals:**
- **No primitive type**: Unlike `int`/`long`, BigInt always maps to BigInteger object
- **No boxing**: BigInteger is already a reference type
- **String-based construction**: BigInteger(String) constructor used for initialization
- **Immutable**: BigInteger objects are immutable like String

---

## AST Structure Analysis

### Swc4jAstBigInt Fields

```java
protected Optional<String> raw;           // Raw string (e.g., "123n")
protected Swc4jAstBigIntSign sign;        // NoSign, Plus, or Minus
protected BigInteger value;               // Parsed BigInteger value
```

### Swc4jAstBigIntSign Enum

```java
NoSign(0, "")   // No explicit sign: 123n
Minus(1, "-")   // Negative sign: -123n
Plus(2, "+")    // Positive sign: +123n (rare)
```

### Value Parsing

The `setRaw(String raw)` method parses the BigInt literal:
1. Strips the trailing `n` suffix
2. Converts to `BigInteger` using constructor
3. Empty/null raw defaults to `BigInteger.ZERO`

### Type Coercion Methods

Swc4jAstBigInt implements `ISwc4jAstCoercionPrimitive`, providing conversion methods:

```java
asBoolean()  // false if ZERO, true otherwise
asByte()     // bigInt.byteValue() - truncation
asShort()    // bigInt.shortValue() - truncation
asInt()      // bigInt.intValue() - truncation
asLong()     // bigInt.longValue() - truncation
asFloat()    // bigInt.floatValue() - precision loss
asDouble()   // bigInt.doubleValue() - precision loss
asString()   // bigInt.toString()
```

---

## Implementation Details

### JVM Bytecode Generation Strategy

**Creating BigInteger from String:**
```
ldc "123456789012345678901234567890"   // Load string constant
new java/math/BigInteger                // Create BigInteger object
dup                                      // Duplicate reference
ldc "123456789012345678901234567890"   // Load string again
invokespecial BigInteger.<init>(String) // Call constructor
```

**Alternative: Static Constants**
```
For common values (0, 1, 10), use predefined constants:
getstatic java/math/BigInteger.ZERO
getstatic java/math/BigInteger.ONE
getstatic java/math/BigInteger.TEN
```

**Negative BigInt:**
```
For negative values, can use negate():
<create positive BigInteger>
invokevirtual BigInteger.negate()Ljava/math/BigInteger;
```

### Conversion to Primitive Types

When TypeScript code uses BigInt with type annotations for primitives:

```typescript
const value: int = 123n  // Convert BigInt to int
```

Bytecode generation:
```
<create BigInteger>
invokevirtual BigInteger.intValue()I  // Call intValue()
```

Similarly for other primitives:
- `longValue()J` for long
- `byteValue()B` for byte
- `shortValue()S` for short
- `floatValue()F` for float
- `doubleValue()D` for double

---

## Test Coverage Plan

### Phase 1: Basic BigInt Values (15 tests)

**Goal:** Test fundamental BigInt literal functionality.

1. **testBigIntZero** - 0n
2. **testBigIntOne** - 1n
3. **testBigIntPositiveSmall** - 42n
4. **testBigIntNegativeSmall** - -42n
5. **testBigIntPositiveLarge** - 123456789n
6. **testBigIntNegativeLarge** - -123456789n
7. **testBigIntExceedingLong** - Value > Long.MAX_VALUE
8. **testBigIntVeryLarge** - 100+ digit number
9. **testBigIntVariable** - Assign to variable
10. **testBigIntConst** - const declaration
11. **testBigIntLet** - let declaration
12. **testBigIntMultipleVariables** - Multiple BigInt variables
13. **testBigIntPositiveSign** - +123n (explicit plus)
14. **testBigIntNoSign** - 123n (implicit positive)
15. **testBigIntMinusSign** - -123n (explicit minus)

### Phase 2: Conversion to Primitives (12 tests)

**Goal:** Test BigInt to primitive type conversions.

16. **testBigIntToInt** - BigInt → int (within range)
17. **testBigIntToIntTruncation** - BigInt → int (exceeds range)
18. **testBigIntToLong** - BigInt → long (within range)
19. **testBigIntToLongTruncation** - BigInt → long (exceeds range)
20. **testBigIntToByte** - BigInt → byte
21. **testBigIntToShort** - BigInt → short
22. **testBigIntToFloat** - BigInt → float (precision loss)
23. **testBigIntToDouble** - BigInt → double (precision loss)
24. **testBigIntToBoolean** - BigInt → boolean (0n=false, other=true)
25. **testBigIntNegativeToInt** - Negative BigInt → int
26. **testBigIntLargeToInt** - Large BigInt → int (high-order bits lost)
27. **testBigIntToString** - BigInt → String via toString()

### Phase 3: Special Values (10 tests)

**Goal:** Test edge cases and special BigInt values.

28. **testBigIntZeroValue** - Verify 0n equals BigInteger.ZERO
29. **testBigIntOneValue** - Verify 1n equals BigInteger.ONE
30. **testBigIntTenValue** - Verify 10n equals BigInteger.TEN
31. **testBigIntMaxLongValue** - Long.MAX_VALUE as BigInt
32. **testBigIntMaxLongPlusOne** - Long.MAX_VALUE + 1 as BigInt
33. **testBigIntMinLongValue** - Long.MIN_VALUE as BigInt
34. **testBigIntMinLongMinusOne** - Long.MIN_VALUE - 1 as BigInt
35. **testBigIntPowerOfTwo** - 2^128
36. **testBigIntFactorial** - 100! (very large number)
37. **testBigIntLeadingZeros** - 00123n (leading zeros ignored)

### Phase 4: BigInt Operations Context (10 tests)

**Goal:** Test BigInt in various usage contexts.

38. **testBigIntReturn** - Return BigInt from function
39. **testBigIntFunctionParameter** - Pass BigInt as parameter (out of scope)
40. **testBigIntArrayElement** - BigInt in array (out of scope)
41. **testBigIntObjectProperty** - BigInt as object property (out of scope)
42. **testBigIntMixedWithNumber** - BigInt and number in same scope
43. **testBigIntReassignment** - Reassign BigInt variable
44. **testBigIntMultipleReturns** - Multiple BigInt returns
45. **testBigIntWithAnnotation** - Explicit : bigint annotation
46. **testBigIntInferredType** - Type inference without annotation
47. **testBigIntDefaultValue** - BigInt default initialization (out of scope)

### Phase 5: Raw String Handling (8 tests)

**Goal:** Test raw BigInt string representation.

48. **testBigIntRawDecimal** - Decimal notation (default)
49. **testBigIntRawHexadecimal** - Hex notation: 0xFFn (if supported)
50. **testBigIntRawOctal** - Octal notation: 0o77n (if supported)
51. **testBigIntRawBinary** - Binary notation: 0b1111n (if supported)
52. **testBigIntRawWithUnderscore** - Numeric separators: 1_000_000n (if supported)
53. **testBigIntRawPreserved** - Raw string preservation
54. **testBigIntRawEmpty** - Empty raw (defaults to 0)
55. **testBigIntRawNull** - Null raw (defaults to 0)

### Phase 6: Type Annotations (10 tests)

**Goal:** Test BigInt with various type annotations.

56. **testBigIntAnnotationBigInteger** - : BigInteger
57. **testBigIntAnnotationInt** - : int (with conversion)
58. **testBigIntAnnotationLong** - : long (with conversion)
59. **testBigIntAnnotationByte** - : byte (with conversion)
60. **testBigIntAnnotationShort** - : short (with conversion)
61. **testBigIntAnnotationFloat** - : float (with conversion)
62. **testBigIntAnnotationDouble** - : double (with conversion)
63. **testBigIntAnnotationBoolean** - : boolean (with conversion)
64. **testBigIntAnnotationOnConst** - Type on const declaration
65. **testBigIntAnnotationOnFunction** - Type on function return

### Phase 7: Edge Cases (15 tests)

**Goal:** Test boundary conditions and unusual inputs.

66. **testBigIntSignPreservation** - Verify sign handling
67. **testBigIntNegateOperation** - Test negation (out of scope)
68. **testBigIntComparison** - Test equality (out of scope)
69. **testBigIntArithmetic** - Test addition/subtraction (out of scope)
70. **testBigIntVeryLargePositive** - Extremely large positive value
71. **testBigIntVeryLargeNegative** - Extremely large negative value
72. **testBigIntOverflowInt** - Value exceeding Integer.MAX_VALUE
73. **testBigIntOverflowLong** - Value exceeding Long.MAX_VALUE
74. **testBigIntUnderflowInt** - Value below Integer.MIN_VALUE
75. **testBigIntUnderflowLong** - Value below Long.MIN_VALUE
76. **testBigIntPrecisionLossFloat** - Precision loss to float
77. **testBigIntPrecisionLossDouble** - Precision loss to double
78. **testBigIntMultipleBigInts** - Many BigInt variables
79. **testBigIntNestedScopes** - BigInt in nested scopes (out of scope)
80. **testBigIntImmutability** - Verify immutability (out of scope)

---

## Edge Cases Summary

### 1. Sign Handling
- **NoSign**: `123n` (positive, no explicit sign)
- **Plus**: `+123n` (positive, explicit plus sign)
- **Minus**: `-123n` (negative, explicit minus sign)
- **Sign preservation**: Maintain original sign representation

### 2. Value Ranges
- **Zero**: `0n` → `BigInteger.ZERO` (static constant)
- **One**: `1n` → `BigInteger.ONE` (static constant)
- **Ten**: `10n` → `BigInteger.TEN` (static constant)
- **Small values**: Use direct `new BigInteger(String)` constructor
- **Large values**: Beyond long range (> 2^63 - 1)
- **Very large values**: 100+ digits, 1000+ digits

### 3. Primitive Conversion
- **Truncation**: BigInt > int range → high bits lost via `intValue()`
- **Overflow**: No exception thrown, wraps around
- **Precision loss**: BigInt → float/double may lose precision
- **Boolean conversion**: `0n` → false, all others → true
- **Negative handling**: Negative BigInt → negative primitive

### 4. Raw String Formats
- **Decimal**: `123n` (standard)
- **Hexadecimal**: `0xFFn` → 255n (if JavaScript supports)
- **Octal**: `0o77n` → 63n (if JavaScript supports)
- **Binary**: `0b1111n` → 15n (if JavaScript supports)
- **Numeric separators**: `1_000_000n` (if JavaScript supports)
- **Leading zeros**: `00123n` → `123n` (ignored)
- **Empty/null raw**: Defaults to `BigInteger.ZERO`

### 5. JVM Bytecode Constraints
- **No primitive BigInt**: Always creates heap object
- **Constructor overhead**: `new BigInteger(String)` for each literal
- **Constant pool**: String representation in constant pool
- **Memory allocation**: Each BigInt literal = new object
- **Immutability**: Safe to share references (like String)

### 6. Type System Integration
- **Default type**: BigInt literal → `BigInteger`
- **Explicit annotation**: `: BigInteger` or `: bigint`
- **Conversion annotation**: `: int`, `: long`, etc. (triggers conversion)
- **Type inference**: Infer BigInteger from literal usage
- **Mixed types**: BigInt and number in same scope

### 7. Special Cases
- **Static constants**: Use `BigInteger.ZERO`, `ONE`, `TEN` when possible
- **Negation**: For negative values, create positive then call `negate()`
- **String preservation**: Maintain original raw string if available
- **Default initialization**: Missing value → `BigInteger.ZERO`

### 8. Conversion Edge Cases
- **Int overflow**: `9999999999n` → int (wraps to negative)
- **Long overflow**: `2^64n` → long (wraps)
- **Byte truncation**: `300n` → byte (wraps to 44)
- **Short truncation**: `40000n` → short (wraps to -25536)
- **Float precision**: Large BigInt → float (may lose precision)
- **Double precision**: Very large BigInt → double (may lose precision)

### 9. JavaScript/TypeScript Compatibility
- **Suffix requirement**: Must end with `n`: `123n`
- **No decimal point**: `123.456n` is invalid in JavaScript
- **Integer only**: BigInt represents integers only
- **Bitwise safe**: Unlike IEEE 754 numbers, no precision loss in bitwise ops

### 10. Implementation Challenges
- **No direct bytecode**: JVM has no bigint primitive type
- **Object allocation**: Every BigInt literal creates new object
- **Performance**: Slower than primitive types
- **Optimization**: Limited optimization opportunities vs primitives
- **Conversion cost**: BigInt ↔ primitive requires method calls

---

## Implementation Status

### ❌ Not Implemented
1. **BigIntLiteralGenerator.java** - Needs to be created
2. **All test files** - No tests exist
3. **Type conversion logic** - BigInt → primitives
4. **Sign handling** - NoSign, Plus, Minus
5. **Raw string parsing** - Handle different number formats
6. **Static constant optimization** - Use ZERO, ONE, TEN

### 🔄 Partially Implemented
1. **AST structure** - ✅ Swc4jAstBigInt exists
2. **Sign enum** - ✅ Swc4jAstBigIntSign exists
3. **Conversion methods** - ✅ asInt(), asLong(), etc. in AST

### ❌ Out of Scope (Requires Other Features)
1. **BigInt arithmetic** - Addition, subtraction, multiplication, etc.
2. **BigInt comparisons** - <, >, ===, etc.
3. **BigInt bitwise operations** - &, |, ^, <<, >>
4. **BigInt in collections** - Arrays, objects
5. **BigInt method calls** - toString(), valueOf(), etc.
6. **BigInt type coercion** - Implicit conversions
7. **BigInt operations** - All binary/unary operators

---

## Test Organization

**Recommended Test File Split:**

1. **TestCompileAstBigIntBasic.java** - Phase 1 (15 tests)
   - Basic BigInt values
   - Positive, negative, zero
   - Sign variations
   - Variable declarations

2. **TestCompileAstBigIntConversion.java** - Phase 2 (12 tests)
   - Conversions to primitives
   - int, long, byte, short
   - float, double, boolean
   - Truncation and precision loss

3. **TestCompileAstBigIntSpecial.java** - Phase 3 (10 tests)
   - Special values (ZERO, ONE, TEN)
   - Max/min long boundaries
   - Powers of two
   - Very large numbers

4. **TestCompileAstBigIntContext.java** - Phase 4 (10 tests)
   - Return values
   - Type annotations
   - Type inference
   - Mixed with number types

5. **TestCompileAstBigIntRaw.java** - Phase 5 (8 tests)
   - Raw string formats
   - Hex, octal, binary (if supported)
   - Numeric separators
   - Raw preservation

6. **TestCompileAstBigIntAnnotations.java** - Phase 6 (10 tests)
   - Type annotations
   - Conversion via annotations
   - Declaration vs function annotations

7. **TestCompileAstBigIntEdgeCases.java** - Phase 7 (15 tests)
   - Boundary values
   - Overflow/underflow
   - Precision loss
   - Multiple BigInts

**Total: 80 planned tests across 7 files**

---

## Bytecode Patterns

### Creating BigInteger from String

**Pattern 1: Direct Constructor**
```
ldc "123"                              // Load string constant
new java/math/BigInteger               // Create new BigInteger
dup                                     // Duplicate reference for constructor
ldc "123"                              // Load string again for constructor parameter
invokespecial java/math/BigInteger/<init>(Ljava/lang/String;)V  // Call constructor
```

**Pattern 2: Static Constants (Optimized)**
```
// For 0n
getstatic java/math/BigInteger.ZERO:Ljava/math/BigInteger;

// For 1n
getstatic java/math/BigInteger.ONE:Ljava/math/BigInteger;

// For 10n
getstatic java/math/BigInteger.TEN:Ljava/math/BigInteger;
```

**Pattern 3: Negative Values**
```
// Option A: Use negative string directly
ldc "-123"
new java/math/BigInteger
dup
ldc "-123"
invokespecial java/math/BigInteger/<init>(Ljava/lang/String;)V

// Option B: Create positive then negate
<create positive BigInteger>
invokevirtual java/math/BigInteger/negate()Ljava/math/BigInteger;
```

### Converting BigInteger to Primitives

**To int:**
```
<BigInteger on stack>
invokevirtual java/math/BigInteger/intValue()I
```

**To long:**
```
<BigInteger on stack>
invokevirtual java/math/BigInteger/longValue()J
```

**To byte:**
```
<BigInteger on stack>
invokevirtual java/math/BigInteger/byteValue()B
```

**To short:**
```
<BigInteger on stack>
invokevirtual java/math/BigInteger/shortValue()S
```

**To float:**
```
<BigInteger on stack>
invokevirtual java/math/BigInteger/floatValue()F
```

**To double:**
```
<BigInteger on stack>
invokevirtual java/math/BigInteger/doubleValue()D
```

---

## Implementation Notes

### Key Design Decisions

1. **Always Use BigInteger Class**
   - No primitive equivalent in JVM
   - All BigInt literals create BigInteger objects
   - Immutable like String

2. **String-Based Construction**
   - Use `new BigInteger(String)` constructor
   - Store numeric value as string in constant pool
   - Strip trailing `n` suffix before construction

3. **Optimize Common Values**
   - Use `BigInteger.ZERO` for 0n
   - Use `BigInteger.ONE` for 1n
   - Use `BigInteger.TEN` for 10n
   - Saves object allocation

4. **Handle Signs Explicitly**
   - Check `Swc4jAstBigIntSign` enum
   - NoSign and Plus both positive
   - Minus creates negative BigInteger

5. **Conversion Strategy**
   - For primitive type annotations, call appropriate `xxxValue()` method
   - Accept truncation and precision loss (matches JavaScript behavior)
   - No range checking or exceptions

6. **Raw String Preservation**
   - Maintain original raw string if available
   - Support different number formats (hex, octal, binary)
   - Handle numeric separators if present

---

## References

- JavaScript BigInt: [MDN BigInt](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/BigInt)
- Java BigInteger: [Oracle Docs](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigInteger.html)
- TypeScript bigint: [TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-3-2.html#bigint)
- JVM Specification: Object Creation and Manipulation
- Swc4jAstBigInt: AST node for BigInt literals
- Swc4jAstBigIntSign: Sign enumeration

---

## Migration Notes

### From Number to BigInt

Key differences when migrating from number literal support:

| Feature | Number | BigInt |
|---------|--------|--------|
| **JVM Type** | Primitives (int, long, double) | Object (BigInteger) |
| **Boxing** | Required for wrapper types | Always object, no boxing |
| **Bytecode** | iconst, ldc, ldc2_w | new BigInteger(String) |
| **Memory** | Stack (primitives) or heap (boxed) | Always heap |
| **Range** | Limited (32/64-bit) | Unlimited |
| **Precision** | Limited (IEEE 754 for float/double) | Exact |
| **Performance** | Fast (hardware support) | Slower (software implementation) |
| **Immutability** | Primitives immutable, wrappers immutable | Immutable |
| **Default** | 0, 0.0 | BigInteger.ZERO |

### Implementation Complexity

| Aspect | Complexity | Reason |
|--------|-----------|--------|
| **Basic literals** | Medium | Object creation required |
| **Type conversion** | Easy | Built-in xxxValue() methods |
| **Sign handling** | Easy | Check enum, create negative string |
| **Optimization** | Medium | Use static constants for 0, 1, 10 |
| **Raw formats** | Hard | Parse hex, octal, binary strings |
| **Large values** | Easy | BigInteger handles automatically |

---

## Current Implementation Status

### ✅ Completed (Planning Phase)

1. **Comprehensive Plan Document** - big-int.md created with:
   - 80 test cases across 7 phases
   - Complete edge case analysis
   - Bytecode generation patterns
   - Implementation strategy
   - Type conversion details

2. **Test Structure Created** - bigint/ test directory with:
   - TestCompileAstBigIntBasic.java (15 tests, Phase 1)
   - TestCompileAstBigIntConversion.java (12 tests, Phase 2)
   - Clear test organization pattern established

3. **AST Analysis Complete** - Reviewed:
   - Swc4jAstBigInt class structure
   - Swc4jAstBigIntSign enum
   - Conversion methods (asInt, asLong, etc.)
   - Raw string handling

### ❌ Pending Implementation

1. **BigIntLiteralGenerator.java** - Needs to be created
   - Implement bytecode generation for BigInteger construction
   - Handle sign (NoSign, Plus, Minus)
   - Optimize for static constants (ZERO, ONE, TEN)
   - Support conversions to primitive types
   - Parse different number formats (hex, octal, binary)

2. **Remaining Test Files** (5 more files needed):
   - TestCompileAstBigIntSpecial.java (10 tests, Phase 3)
   - TestCompileAstBigIntContext.java (10 tests, Phase 4)
   - TestCompileAstBigIntRaw.java (8 tests, Phase 5)
   - TestCompileAstBigIntAnnotations.java (10 tests, Phase 6)
   - TestCompileAstBigIntEdgeCases.java (15 tests, Phase 7)

3. **ExpressionGenerator Integration**
   - Add case for Swc4jAstBigInt in expression dispatcher
   - Route BigInt nodes to BigIntLiteralGenerator

### 📊 Implementation Statistics

| Category | Status | Count |
|----------|--------|-------|
| **Plan Document** | ✅ Complete | 1 file |
| **Test Files Created** | 📝 In Progress | 2/7 files |
| **Test Cases Defined** | ✅ Complete | 80 tests |
| **Test Cases Implemented** | ❌ Pending | 0/80 tests |
| **Generator Created** | ❌ Pending | 0/1 files |
| **AST Support** | ✅ Exists | Swc4jAstBigInt |
| **Documentation** | ✅ Complete | Comprehensive |

### 🎯 Next Steps

1. **Immediate**:
   - Create BigIntLiteralGenerator.java
   - Implement basic BigInteger construction (new BigInteger(String))
   - Handle sign variations (NoSign, Plus, Minus)

2. **Short Term**:
   - Add static constant optimization (ZERO, ONE, TEN)
   - Implement primitive conversions (intValue, longValue, etc.)
   - Create remaining 5 test files

3. **Long Term**:
   - Support different number formats (hex, octal, binary)
   - Optimize for common BigInt patterns
   - Add comprehensive test coverage

### 📝 Notes

- **No existing implementation**: BigInt support is completely new
- **Test-first approach**: Tests defined before implementation
- **Based on AST structure**: Leverages existing Swc4jAstBigInt class
- **Java BigInteger**: Maps JavaScript BigInt to java.math.BigInteger
- **Reference type only**: No primitive BigInt equivalent in JVM
- **Conversion support**: Must handle BigInt → primitive conversions
- **Sign handling**: Three sign types (NoSign, Plus, Minus)
- **String-based**: BigInteger constructed from string representation

