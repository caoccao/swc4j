# Unary Expression Operations Implementation Plan

## Overview

This document outlines the implementation plan for supporting all 7 unary operations defined in `Swc4jAstUnaryOp` for TypeScript to JVM bytecode compilation.

**Current Status:** 3 of 7 operations implemented (43% complete)
- ✅ **Bang (`!`)** - Fully implemented with boolean inversion
- 🔶 **Delete (`delete`)** - Partially implemented (only ArrayList element removal)
- ✅ **Minus (`-`)** - Fully implemented with type widening and literal optimization
- ❌ **Plus (`+`)** - NOT implemented (unary plus for type coercion)
- ❌ **Tilde (`~`)** - NOT implemented (bitwise NOT)
- ❌ **TypeOf (`typeof`)** - NOT implemented (runtime type checking)
- ❌ **Void (`void`)** - NOT implemented (evaluates expression and returns undefined)

**Implementation File:** [UnaryExpressionGenerator.java](../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/UnaryExpressionGenerator.java)
**Test Files:**
- [TestCompileUnaryExprBang.java](../../src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/TestCompileUnaryExprBang.java) (Bang operator tests)
- Additional test files to be created for other operators
**Enum Definition:** [Swc4jAstUnaryOp.java](../../src/main/java/com/caoccao/javet/swc4j/ast/enums/Swc4jAstUnaryOp.java)

---

## Complete Unary Operations List

| # | Operation | Symbol | Category | Status | Complexity | Priority |
|---|-----------|--------|----------|--------|------------|----------|
| 1 | Bang | `!` | Logical | ✅ Implemented | Low | - |
| 2 | Delete | `delete` | Special | 🔶 Partial | Very High | Medium |
| 3 | Minus | `-` | Arithmetic | ✅ Implemented | Low | - |
| 4 | Plus | `+` | Arithmetic | ❌ Not Implemented | Low | High |
| 5 | Tilde | `~` | Bitwise | ❌ Not Implemented | Low | Medium |
| 6 | TypeOf | `typeof` | Special | ❌ Not Implemented | High | Low |
| 0 | Void | `void` | Special | ❌ Not Implemented | Low | Low |

---

## Current Implementation: Minus Operation

### Implementation Review

The Minus operation is **fully implemented** and serves as the reference pattern:

```java
case Minus -> {
    ISwc4jAstExpr arg = unaryExpr.getArg();

    // Optimization: directly generate negated literals
    if (arg instanceof Swc4jAstNumber number) {
        double value = number.getValue();
        // ... extensive type handling for int, long, float, double, wrappers
        // Uses iconst/ldc/ldc2_w for optimal constant loading
    } else {
        // For complex expressions, generate then negate
        ExpressionGenerator.generate(code, cp, arg, null, context, options);
        String argType = TypeResolver.inferTypeFromExpr(arg, context, options);

        switch (argType) {
            case "D" -> code.dneg();
            case "F" -> code.fneg();
            case "J" -> code.lneg();
            default -> code.ineg();
        }
    }
}
```

### Key Features
- **Literal Optimization:** Directly generates negated constants without runtime negation
- **Type Inference:** Determines operand type before generating bytecode
- **Wrapper Support:** Handles Integer, Long, Float, Double, Byte, Short wrappers
- **JVM Instructions:** Uses `ineg`, `lneg`, `fneg`, `dneg` for runtime negation

### Issues with Current Implementation
1. **MIN_VALUE Bug:** `-2147483648` incorrectly loaded as `-2147483647` (identified in earlier conversation)
2. **No Boolean Handling:** Doesn't reject boolean operands (should throw error)

---

## Operation Details

### 1. Bang (`!`) - Logical NOT (CRITICAL PRIORITY)

**JavaScript Behavior:**
```javascript
!true        // false
!false       // true
!0           // true
!1           // false
!null        // true
!undefined   // true
!"hello"     // false
!""          // true
```

**Java Implementation:**
```java
case Bang -> {
    ISwc4jAstExpr arg = unaryExpr.getArg();
    String argType = TypeResolver.inferTypeFromExpr(arg, context, options);

    // Generate the operand
    ExpressionGenerator.generate(code, cp, arg, null, context, options);

    // For boolean types, use simple inversion
    if ("Z".equals(argType) || "Ljava/lang/Boolean;".equals(argType)) {
        // Unbox if wrapper
        TypeConversionHelper.unboxWrapperType(code, cp, argType);

        // Invert: if value == 0, push 1; else push 0
        code.ifeq(0); // Placeholder offset
        int ifeqPos = code.getCurrentOffset() - 2;
        code.iconst(0); // Value was true, push false
        code.gotoLabel(0); // Placeholder
        int gotoPos = code.getCurrentOffset() - 2;
        int trueLabel = code.getCurrentOffset();
        code.iconst(1); // Value was false, push true
        int endLabel = code.getCurrentOffset();

        // Patch offsets
        code.patchShort(ifeqPos, trueLabel - (ifeqPos - 1));
        code.patchShort(gotoPos, endLabel - (gotoPos - 1));
    } else {
        // For non-boolean types, JavaScript coerces to boolean first
        // In Java, this should throw an error for type safety
        throw new Swc4jByteCodeCompilerException(
            "Logical NOT (!) requires boolean operand, got: " + argType);
    }
}
```

**JVM Bytecode Pattern:**
```
[generate operand]       // Stack: [value]
ifeq TRUE_LABEL         // if value == 0 (false), jump to TRUE_LABEL
iconst_0                // push 0 (false)
goto END_LABEL
TRUE_LABEL:
iconst_1                // push 1 (true)
END_LABEL:
```

**Edge Cases:**
- ❗ Boolean wrapper (Boolean) - must unbox first
- ❗ Non-boolean types - JavaScript coerces, Java should reject for type safety
- ❗ Null values - JavaScript treats as falsy, Java should reject
- ❗ Double negation `!!x` - should work correctly
- ❗ Comparison results: `!(a < b)` - must handle boolean result from comparisons

**Test Cases:**
```typescript
!true                    // false
!false                   // true
!(5 > 3)                 // false
!(5 < 3)                 // true
!!(5 > 3)                // true (double negation)
const b: boolean = false
!b                       // true
```

**Priority:** ~~**CRITICAL**~~ - ✅ **COMPLETED** - Previously blocking LogicalAnd tests with negation, now implemented and all tests passing

---

### 2. Delete (`delete`) - Property/Element Deletion

**Current Implementation Status:** 🔶 Partial
- ✅ ArrayList element removal: `delete arr[index]`
- ❌ Object property deletion: `delete obj.prop`
- ❌ Map entry removal: `delete map.get(key)`
- ❌ Array element deletion (Java arrays): throws error (correct)

**JavaScript Behavior:**
```javascript
delete arr[1]           // Removes element at index 1, returns true
delete obj.prop         // Removes property, returns true
delete nonExistent      // Returns true (no-op)
```

**JVM Implementation Strategy:**

```java
case Delete -> {
    ISwc4jAstExpr arg = unaryExpr.getArg();

    if (arg instanceof Swc4jAstMemberExpr memberExpr) {
        String objType = TypeResolver.inferTypeFromExpr(memberExpr.getObj(), context, options);

        // Java array - fixed size, cannot delete
        if (objType != null && objType.startsWith("[")) {
            throw new Swc4jByteCodeCompilerException(
                "Delete operator not supported on Java arrays - arrays have fixed size");
        }

        // ArrayList element removal
        if ("Ljava/util/ArrayList;".equals(objType)) {
            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                // delete arr[index] → arr.remove(index)
                ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options);
                ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options);

                int removeMethod = cp.addMethodRef("java/util/ArrayList", "remove", "(I)Ljava/lang/Object;");
                code.invokevirtual(removeMethod);
                code.pop(); // Discard removed element
                code.iconst(1); // Push true
                return;
            }
        }

        // HashMap entry removal
        if ("Ljava/util/HashMap;".equals(objType)) {
            // delete map[key] → map.remove(key)
            ExpressionGenerator.generate(code, cp, memberExpr.getObj(), null, context, options);

            if (memberExpr.getProp() instanceof Swc4jAstComputedPropName computedProp) {
                ExpressionGenerator.generate(code, cp, computedProp.getExpr(), null, context, options);
                // Box primitive keys if needed
                // ... type checking and boxing logic ...

                int removeMethod = cp.addMethodRef("java/util/HashMap", "remove",
                    "(Ljava/lang/Object;)Ljava/lang/Object;");
                code.invokevirtual(removeMethod);
                code.pop(); // Discard removed value
                code.iconst(1); // Push true
                return;
            }
        }

        // Object field deletion - Not supported in Java
        throw new Swc4jByteCodeCompilerException(
            "Delete operator not supported on Java object fields");
    }

    throw new Swc4jByteCodeCompilerException(
        "Delete operator not supported for: " + arg.getClass().getSimpleName());
}
```

**Edge Cases:**
- ❗ Java arrays - should throw error (fixed size)
- ❗ Object fields - should throw error (Java fields cannot be deleted at runtime)
- ❗ Out-of-bounds array access - ArrayList throws IndexOutOfBoundsException
- ❗ Non-existent map keys - returns null, but delete should return true
- ❗ Delete with side effects: `delete arr[i++]` - must evaluate index expression
- ❗ Nested properties: `delete obj.nested.prop` - not straightforward in Java

**Test Cases:**
```typescript
const arr: ArrayList<int> = [1, 2, 3]
delete arr[1]            // true, arr becomes [1, 3]

const map: HashMap<String, int> = {"a": 1}
delete map["a"]          // true, map becomes empty
```

**Priority:** Medium (already partially working)

---

### 3. Plus (`+`) - Unary Plus / Numeric Conversion

**JavaScript Behavior:**
```javascript
+5          // 5
+"5"        // 5 (string to number conversion)
+true       // 1
+false      // 0
+null       // 0
+undefined  // NaN
```

**Java Implementation:**
```java
case Plus -> {
    ISwc4jAstExpr arg = unaryExpr.getArg();
    String argType = TypeResolver.inferTypeFromExpr(arg, context, options);

    // For numeric types, unary plus is a no-op (just generate the value)
    if (argType != null && (argType.equals("I") || argType.equals("J") ||
                            argType.equals("F") || argType.equals("D") ||
                            argType.equals("B") || argType.equals("S") ||
                            argType.equals("C"))) {
        // Just generate the operand as-is
        ExpressionGenerator.generate(code, cp, arg, returnTypeInfo, context, options);
        return;
    }

    // For wrapper types, unbox to primitive
    if ("Ljava/lang/Integer;".equals(argType) ||
        "Ljava/lang/Long;".equals(argType) ||
        "Ljava/lang/Float;".equals(argType) ||
        "Ljava/lang/Double;".equals(argType) ||
        "Ljava/lang/Byte;".equals(argType) ||
        "Ljava/lang/Short;".equals(argType)) {
        ExpressionGenerator.generate(code, cp, arg, null, context, options);
        TypeConversionHelper.unboxWrapperType(code, cp, argType);
        return;
    }

    // For string to number conversion - not directly supported in Java
    // Would require Double.parseDouble() call
    if ("Ljava/lang/String;".equals(argType)) {
        throw new Swc4jByteCodeCompilerException(
            "Unary plus (+) string-to-number conversion not supported. " +
            "Use explicit parsing: Integer.parseInt() or Double.parseDouble()");
    }

    // For boolean - JavaScript converts to 0/1, Java should reject
    if ("Z".equals(argType) || "Ljava/lang/Boolean;".equals(argType)) {
        throw new Swc4jByteCodeCompilerException(
            "Unary plus (+) not supported on boolean types");
    }

    throw new Swc4jByteCodeCompilerException(
        "Unary plus (+) not supported for type: " + argType);
}
```

**Edge Cases:**
- ❗ Numeric types - no-op, just return value
- ❗ String conversion - JavaScript behavior, Java doesn't support implicit conversion
- ❗ Boolean conversion - JavaScript converts true→1, false→0, Java should reject
- ❗ Null/undefined - JavaScript converts to 0/NaN, Java should reject
- ❗ Object types - JavaScript calls valueOf(), Java doesn't have equivalent
- ❗ Wrapper types - must unbox to primitive

**Test Cases:**
```typescript
+5                       // 5
+5.5                     // 5.5
const x: int = 10
+x                       // 10

const y: Integer = 20
+y                       // 20 (unboxed)
```

**Priority:** High (simple no-op for most numeric cases)

---

### 4. Tilde (`~`) - Bitwise NOT

**JavaScript Behavior:**
```javascript
~5          // -6 (bitwise NOT: ~0101 = 1010, two's complement)
~-1         // 0
~0          // -1
```

**Java Implementation:**
```java
case Tilde -> {
    ISwc4jAstExpr arg = unaryExpr.getArg();
    String argType = TypeResolver.inferTypeFromExpr(arg, context, options);

    // Handle null type - default to int
    if (argType == null) argType = "I";

    // Bitwise NOT only works on integer types
    if (argType.equals("I") || argType.equals("J") ||
        argType.equals("B") || argType.equals("S") || argType.equals("C")) {

        // Generate the operand
        ExpressionGenerator.generate(code, cp, arg, null, context, options);

        // Unbox if wrapper
        TypeConversionHelper.unboxWrapperType(code, cp, argType);

        // Convert to appropriate integer type
        String primitiveType = TypeConversionHelper.getPrimitiveType(argType);

        // For int types, use iconst_m1 XOR technique
        // ~x is equivalent to x ^ -1 (XOR with all 1s)
        if (primitiveType.equals("I") || primitiveType.equals("B") ||
            primitiveType.equals("S") || primitiveType.equals("C")) {
            code.iconst(-1);
            code.ixor();
        } else if (primitiveType.equals("J")) {
            // For long
            code.lconst(-1L);
            code.lxor();
        }
    } else {
        throw new Swc4jByteCodeCompilerException(
            "Bitwise NOT (~) requires integer type, got: " + argType);
    }
}
```

**JVM Bytecode Pattern:**
```
[generate operand]       // Stack: [value]
iconst_m1               // Stack: [value, -1]
ixor                    // Stack: [value ^ -1] = ~value
```

**Edge Cases:**
- ❗ Integer types (byte, short, int) - all promoted to int, result is int
- ❗ Long type - separate lxor instruction
- ❗ Floating-point types - should throw error (bitwise ops on floats not meaningful)
- ❗ Boolean type - should throw error (use logical NOT `!` instead)
- ❗ Character type - treated as unsigned int (char is 16-bit unsigned in Java)
- ❗ Wrapper types - must unbox first
- ❗ Double tilde `~~x` - JavaScript idiom for truncation, works in Java too

**Test Cases:**
```typescript
~5                       // -6
~-1                      // 0
~0                       // -1
const x: int = 42
~x                       // -43

const y: long = 100L
~y                       // -101L

~~3.7                    // 3 (double tilde truncation)
```

**Priority:** Medium (useful for bitwise operations)

---

### 5. TypeOf (`typeof`) - Runtime Type Checking

**JavaScript Behavior:**
```javascript
typeof 5              // "number"
typeof "hello"        // "string"
typeof true           // "boolean"
typeof undefined      // "undefined"
typeof null           // "object" (historical bug)
typeof {}             // "object"
typeof []             // "object"
typeof function(){}   // "function"
```

**Java Implementation Challenge:**

JavaScript's `typeof` is **fundamentally different** from Java's type system:
- JavaScript has **runtime types** (values carry type information)
- Java has **compile-time types** (types erased at runtime for primitives)
- Primitives don't carry type information on JVM stack
- Objects have runtime class information via `getClass()`

**Implementation Strategy:**

```java
case TypeOf -> {
    ISwc4jAstExpr arg = unaryExpr.getArg();
    String argType = TypeResolver.inferTypeFromExpr(arg, context, options);

    // For primitives, we know the type at compile time
    if (argType != null) {
        String typeString;
        switch (argType) {
            case "I", "J", "F", "D", "B", "S", "C" -> typeString = "number";
            case "Z" -> typeString = "boolean";
            case "Ljava/lang/String;" -> typeString = "string";
            default -> {
                // For objects, generate runtime check
                ExpressionGenerator.generate(code, cp, arg, null, context, options);

                // Check for null
                code.dup();
                code.ifnull(/* offset to "object" string */);

                // Check if it's a String
                code.dup();
                code.instanceof(cp.addClass("java/lang/String"));
                code.ifne(/* offset to "string" string */);

                // Check if it's a Number wrapper
                code.dup();
                code.instanceof(cp.addClass("java/lang/Number"));
                code.ifne(/* offset to "number" string */);

                // Check if it's Boolean
                code.dup();
                code.instanceof(cp.addClass("java/lang/Boolean"));
                code.ifne(/* offset to "boolean" string */);

                // Default to "object"
                // ... complex branching logic ...

                return;
            }
        }

        // For primitives, just load the constant string
        int stringIndex = cp.addString(typeString);
        code.ldc(stringIndex);
    } else {
        throw new Swc4jByteCodeCompilerException(
            "TypeOf requires compile-time type information");
    }
}
```

**Edge Cases:**
- ❗ Primitive types - compile-time known, return constant string
- ❗ Wrapper types - runtime instanceof checks needed
- ❗ Null values - JavaScript returns "object", Java can detect null
- ❗ Arrays - JavaScript returns "object", Java can check with instanceof
- ❗ Functions/Methods - JavaScript returns "function", no direct Java equivalent
- ❗ Undefined - doesn't exist in Java
- ❗ Side effects - `typeof (x++)` must still evaluate x++

**Test Cases:**
```typescript
typeof 5              // "number"
typeof 5.5            // "number"
typeof true           // "boolean"
typeof "hello"        // "string"

const x: int = 10
typeof x              // "number"

const s: String = "test"
typeof s              // "string"
```

**Priority:** Low (complex to implement, limited utility in typed context)

**Recommendation:** Consider making this **compile-time only** and throw error for runtime type checks that cannot be determined statically.

---

### 6. Void (`void`) - Discard Expression Result

**JavaScript Behavior:**
```javascript
void 0               // undefined (evaluates 0, returns undefined)
void (x++)           // undefined (increments x, returns undefined)
void function(){}()  // undefined (calls function, returns undefined)
```

**Java Implementation:**
```java
case Void -> {
    ISwc4jAstExpr arg = unaryExpr.getArg();

    // Generate the operand (to evaluate side effects)
    ExpressionGenerator.generate(code, cp, arg, null, context, options);

    // Determine the return type to know how much to pop
    String argType = TypeResolver.inferTypeFromExpr(arg, context, options);

    // Pop the result from the stack
    if (argType != null) {
        if (argType.equals("J") || argType.equals("D")) {
            // Long and double are 2 slots
            code.pop2();
        } else if (!argType.equals("V")) {
            // Everything else is 1 slot (except void)
            code.pop();
        }
        // If argType is "V" (void), nothing to pop
    }

    // JavaScript void returns undefined
    // In Java, we can represent this as null or void
    // For expressions that need a value, push null
    if (returnTypeInfo != null && returnTypeInfo.type() != ReturnType.VOID) {
        code.aconst_null();
    }
    // If void is expected, don't push anything
}
```

**JVM Bytecode Pattern:**
```
[generate operand]       // Stack: [value]
pop                     // Stack: [] (discard value)
aconst_null             // Stack: [null] (push null as "undefined")
```

**Edge Cases:**
- ❗ Side effects must still execute: `void (x++)` increments x
- ❗ Long/double values - use pop2 (2 stack slots)
- ❗ Void expressions - nothing to pop
- ❗ Method calls with return values - must pop result
- ❗ Nested void: `void (void x)` - each void pops and pushes null
- ❗ Statement context vs expression context - in statement context, no null needed

**Test Cases:**
```typescript
const x: int = 5
void x                   // null/undefined (x is not changed)

let y: int = 0
void (y++)               // null/undefined (y becomes 1)

void (1 + 2)            // null/undefined (3 is discarded)
```

**Priority:** Low (rarely used, mainly for IIFE pattern which Java doesn't need)

---

## Implementation Priority

### Phase 1: Critical Operations (Blocking Tests)
1. **Bang (`!`)** - ✅ **COMPLETED**
   - Actual effort: ~2 hours
   - Complexity: Low
   - Impact: High (unblocked multiple tests)
   - Implementation: Lines 47-87 in UnaryExpressionGenerator.java
   - Tests: TestCompileUnaryExprBang.java (15 test cases)

### Phase 2: Common Operations
2. **Plus (`+`)** - High priority (common, mostly no-op)
   - Estimated effort: 1-2 hours
   - Complexity: Low
   - Impact: Medium

3. **Tilde (`~`)** - Medium priority (useful for bitwise ops)
   - Estimated effort: 2 hours
   - Complexity: Low
   - Impact: Low

### Phase 3: Special Operations
4. **Delete (`delete`)** - Extend current implementation
   - Estimated effort: 3-4 hours
   - Complexity: Very High
   - Impact: Medium

5. **TypeOf (`typeof`)** - Low priority (complex, limited use in typed context)
   - Estimated effort: 4-6 hours
   - Complexity: High
   - Impact: Low

6. **Void (`void`)** - Low priority (rarely used)
   - Estimated effort: 1 hour
   - Complexity: Low
   - Impact: Very Low

---

## Common Edge Cases Across All Operations

### Type System Edge Cases
1. **Null Values**
   - JavaScript: Implicit coercion (null → 0 for numeric, false for boolean)
   - Java Strategy: Throw error for type safety, require explicit checks

2. **Undefined Values**
   - JavaScript: Has undefined type
   - Java Strategy: No equivalent, could represent as null or throw error

3. **Type Coercion**
   - JavaScript: Automatic coercion (string to number, boolean to number, etc.)
   - Java Strategy: Explicit conversions only, throw error for implicit coercion

4. **Wrapper Types**
   - Must unbox before operations
   - Must handle null wrappers (NullPointerException risk)

### Operator Precedence Edge Cases
1. **Multiple Unary Operators**
   - `!!x` - double negation
   - `~~x` - double bitwise NOT (truncation)
   - `-+x` - unary minus and plus
   - `!-x` - logical NOT of negated value

2. **Unary with Binary Operators**
   - `-a + b` vs `-(a + b)` - precedence matters
   - `!a && b` vs `!(a && b)` - different results

### JVM Stack Management Edge Cases
1. **Long and Double (2-slot values)**
   - Use `pop2` instead of `pop`
   - Use `dup2` instead of `dup`
   - Different load/store instructions

2. **Void Expressions**
   - No value on stack
   - Must not pop or push

3. **Exception Handling**
   - Division by zero in `-x/y`
   - NullPointerException for null wrappers
   - IndexOutOfBoundsException for delete

### Literal Optimization Edge Cases
1. **Constant Folding**
   - `-5` should generate iconst directly, not iconst+ineg
   - `!true` should generate iconst_0, not iconst_1+invert
   - `~-1` should generate iconst_0, not computation

2. **MIN_VALUE Special Case**
   - `-Integer.MIN_VALUE` = Integer.MIN_VALUE (overflow)
   - `-Long.MIN_VALUE` = Long.MIN_VALUE (overflow)
   - Should handle correctly or document behavior

3. **NaN and Infinity**
   - `-Infinity` = `-Infinity`
   - `-NaN` = `NaN`
   - Special float/double values

### Test Coverage Requirements
Each operation should have tests for:
1. ✅ Primitive types (int, long, float, double, byte, short, char, boolean)
2. ✅ Wrapper types (Integer, Long, Float, Double, Byte, Short, Character, Boolean)
3. ✅ Literal optimization (constants folded at compile time)
4. ✅ Expression operands (not just literals)
5. ✅ Edge values (MIN_VALUE, MAX_VALUE, 0, -1, NaN, Infinity)
6. ✅ Multiple operators (`!!x`, `~~x`, `-+x`)
7. ✅ Error cases (wrong types, null values)
8. ✅ Side effects preservation (`void (x++)`)

---

## Known Issues to Fix

### 1. Minus Operation MIN_VALUE Bug
**Issue:** `-2147483648` is loaded as `-2147483647`

**Root Cause:** Integer literal parsing or constant generation issue

**Fix Required:** Investigate NumberLiteralGenerator or constant pool generation

### 2. Missing Boolean Type Rejection
**Issue:** Minus operation doesn't reject boolean operands

**Fix Required:** Add type check to throw error for boolean in Minus case

### 3. Delete Operation Limited Support
**Issue:** Only supports ArrayList.remove(int), not HashMap or other collections

**Fix Required:** Extend to HashMap.remove(key) and other collection types

---

## Testing Strategy

### Unit Test Structure
Create `TestCompileUnaryExpr.java` with separate test methods:

```java
// Bang (!)
testBangTrue()
testBangFalse()
testBangComparison()
testDoubleBang()

// Minus (-)
testMinusInt()
testMinusLong()
testMinusFloat()
testMinusDouble()
testMinusWrapper()
testMinusExpression()

// Plus (+)
testPlusInt()
testPlusWrapper()
testPlusNoOp()

// Tilde (~)
testTildeInt()
testTildeLong()
testTildeNegativeOne()
testDoubleTilde()

// Delete
testDeleteArrayListElement()
testDeleteHashMapEntry()
testDeleteJavaArrayError()

// TypeOf
testTypeOfPrimitive()
testTypeOfString()
testTypeOfObject()

// Void
testVoidExpression()
testVoidSideEffect()
```

### Integration Tests
Test interactions with binary operators:
- `!(a < b)` - NOT of comparison
- `-(a + b)` - negation of addition
- `~(a & b)` - bitwise NOT of AND
- `void (a = b)` - void with assignment

---

## Implementation Checklist

- [x] Implement Bang (`!`) operator
- [ ] Fix Minus operation MIN_VALUE bug
- [ ] Implement Plus (`+`) operator
- [ ] Implement Tilde (`~`) operator
- [ ] Extend Delete operator for HashMap
- [ ] Implement TypeOf operator (or document as not supported)
- [ ] Implement Void operator
- [x] Create comprehensive test suite for Bang operator
- [x] Document all edge cases in tests for Bang operator
- [ ] Update this plan with actual implementation details

---

## References

- [JVM Instruction Set](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html)
- [TypeScript Unary Operators](https://www.typescriptlang.org/docs/handbook/2/everyday-types.html)
- [JavaScript Unary Operators (MDN)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators#unary_operators)
- [Java Wrapper Classes](https://docs.oracle.com/javase/tutorial/java/data/numberclasses.html)
