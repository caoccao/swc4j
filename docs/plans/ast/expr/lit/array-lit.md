# Array Literal Implementation Plan

## Overview

This document outlines the implementation plan for supporting JavaScript/TypeScript array literals (`Swc4jAstArrayLit`) and compiling them to JVM bytecode using **ArrayList** for dynamic arrays and **Java arrays** for typed arrays.

**Current Status:** ✅ Partially Implemented (Basic functionality working)

**Implementation File:** [ArrayLiteralGenerator.java](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/lit/ArrayLiteralGenerator.java) ✅

**Test File:** [TestCompileAstArrayLit.java](../../../../../src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/TestCompileAstArrayLit.java) ✅ (97 tests passing)

**AST Definition:** [Swc4jAstArrayLit.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstArrayLit.java)

---

## Array Representation Strategy

### Two Representation Modes

1. **ArrayList Mode (Default - No Type Annotation)**
   ```typescript
   const arr = [1, 2, 3]  // → ArrayList<Object>
   ```
   - Type: `Ljava/util/ArrayList;`
   - Dynamic sizing (can grow/shrink)
   - Supports all array methods (push, pop, splice, etc.)
   - Elements are boxed Objects

2. **Java Array Mode (With Type Annotation)**
   ```typescript
   const arr: int[] = [1, 2, 3]  // → int[]
   ```
   - Type: `[I` (or other primitive/reference array descriptors)
   - Fixed size at creation
   - Direct memory access (faster)
   - Cannot use dynamic methods (push, pop, etc.)

---

## Current Implementation Review

### ArrayLiteralGenerator.java Status

**✅ Implemented Features:**

1. **Empty Arrays**
   - ArrayList: `new ArrayList<>()`
   - Java array: `new int[0]`

2. **Simple Element Storage**
   - ArrayList: Uses `ArrayList.add(Object)`
   - Java array: Uses array store instructions (`iastore`, `aastore`, etc.)

3. **Type Detection**
   - Checks `returnTypeInfo.descriptor()` to determine array type
   - Starts with `[` → Java array mode
   - Otherwise → ArrayList mode

4. **Primitive Array Support**
   - All 8 primitive types: `boolean`, `byte`, `char`, `short`, `int`, `long`, `float`, `double`
   - Uses appropriate `newarray` type codes (4-11)

5. **Reference Array Support**
   - String arrays: `String[]`
   - Object arrays: `Object[]`
   - Uses `anewarray` instruction

6. **Value Boxing**
   - Primitives boxed to wrapper types for ArrayList
   - Uses `Integer.valueOf()`, `Long.valueOf()`, etc.

7. **Type Conversion**
   - Unboxing for Java array elements
   - Primitive type conversion (e.g., int → long)

### TestCompileAstArrayLit.java Status

**✅ Passing Tests (30 tests):**

1. **Basic Array Creation:**
   - `testReturnEmptyArray` - Empty ArrayList
   - `testReturnArrayWithElements` - ArrayList with elements
   - `testEmptyIntArray` - Empty Java int array

2. **Typed Arrays:**
   - `testArrayAnnotation` - int[] with values
   - `testBooleanArray` - boolean[]
   - `testDoubleArray` - double[]
   - `testFloatArray` - float[]
   - `testLongArray` - long[]
   - `testStringArray` - String[]

3. **ArrayList with Generics:**
   - `testListAnnotation` - Array<Integer>
   - `testArrayListOfStrings` - Array<String>
   - `testArrayListOfDoubles` - Array<Double>

4. **Array Operations:**
   - `testArrayLength` / `testJavaArrayLength` - Read length property
   - `testArrayIndexGet` / `testJavaArrayIndexGet` - Index access `arr[i]`
   - `testArrayIndexSet` / `testJavaArrayIndexSet` - Index assignment `arr[i] = x`
   - `testDoubleArrayOperations` - Multiple operations
   - `testStringArrayOperations` - String array ops

5. **ArrayList Mutating Methods:**
   - `testArrayPush` - push() method
   - `testArrayPop` - pop() method (8 tests)
     - `testArrayPop` - Basic pop returns last element
     - `testArrayPopChangesLength` - Pop reduces array length
     - `testArrayPopMultipleTimes` - Multiple pops work correctly
     - `testArrayPopReturnsCorrectType` - Pop works with strings
     - `testArrayPopSingleElement` - Pop on single-element array
     - `testArrayPopSingleElementLeavesEmpty` - Pop leaves empty array
     - `testArrayPopWithMixedTypes` - Pop works with mixed types
     - `testArrayPushAndPop` - Push and pop work together
   - `testArrayShift` - shift() method (9 tests)
     - `testArrayShift` - Basic shift returns first element
     - `testArrayShiftChangesLength` - Shift reduces array length
     - `testArrayShiftMultipleTimes` - Multiple shifts work correctly
     - `testArrayShiftReturnsCorrectType` - Shift works with strings
     - `testArrayShiftSingleElement` - Shift on single-element array
     - `testArrayShiftSingleElementLeavesEmpty` - Shift leaves empty array
     - `testArrayShiftWithMixedTypes` - Shift works with mixed types
     - `testArrayPushAndShift` - Push and shift work together
     - `testArrayShiftAndRemainingElements` - Verify remaining elements after shift
   - `testArrayUnshift` - unshift() method (9 tests)
     - `testArrayUnshift` - Basic unshift adds to beginning
     - `testArrayUnshiftChangesLength` - Unshift increases array length
     - `testArrayUnshiftMultipleTimes` - Multiple unshifts work correctly
     - `testArrayUnshiftOnEmptyArray` - Unshift on empty array
     - `testArrayUnshiftString` - Unshift works with strings
     - `testArrayUnshiftWithMixedTypes` - Unshift works with mixed types
     - `testArrayUnshiftAndShift` - Unshift and shift work together
     - `testArrayUnshiftAndPush` - Unshift and push work together
     - `testArrayUnshiftPreservesOrder` - Verify element order after unshift
   - `testArrayIndexOf` - indexOf() method (10 tests)
     - `testArrayIndexOf` - Basic indexOf finds element
     - `testArrayIndexOfNotFound` - Returns -1 when not found
     - `testArrayIndexOfFirstElement` - Find first element (index 0)
     - `testArrayIndexOfLastElement` - Find last element
     - `testArrayIndexOfDuplicates` - Returns first occurrence index
     - `testArrayIndexOfString` - indexOf works with strings
     - `testArrayIndexOfStringNotFound` - String not found returns -1
     - `testArrayIndexOfOnEmptyArray` - Empty array returns -1
     - `testArrayIndexOfWithMixedTypes` - indexOf works with mixed types
     - `testArrayIndexOfAfterModification` - indexOf after push/unshift operations
   - `testArrayIncludes` - includes() method (10 tests)
     - `testArrayIncludes` - Basic includes finds element
     - `testArrayIncludesNotFound` - Returns false when not found
     - `testArrayIncludesFirstElement` - Find first element
     - `testArrayIncludesLastElement` - Find last element
     - `testArrayIncludesString` - includes works with strings
     - `testArrayIncludesStringNotFound` - String not found returns false
     - `testArrayIncludesOnEmptyArray` - Empty array returns false
     - `testArrayIncludesWithMixedTypes` - includes works with mixed types
     - `testArrayIncludesAfterModification` - includes after push/unshift operations
     - `testArrayIncludesReturnsFalseForDifferentType` - Type mismatch returns false
   - `testArrayDelete` - delete arr[i]
   - `testArrayClear` - arr.length = 0
   - `testArrayShrink` - arr.length = 2

6. **Error Handling:**
   - `testJavaArrayDeleteNotSupported` - delete on Java array throws error
   - `testJavaArrayPushNotSupported` - push() on Java array throws error
   - `testJavaArrayPopNotSupported` - pop() on Java array throws error
   - `testJavaArrayShiftNotSupported` - shift() on Java array throws error
   - `testJavaArrayUnshiftNotSupported` - unshift() on Java array throws error
   - `testJavaArrayIndexOfNotSupported` - indexOf() on Java array throws error
   - `testJavaArrayIncludesNotSupported` - includes() on Java array throws error
   - `testJavaArraySetLengthNotSupported` - length assignment throws error

---

## JavaScript Array API → Java ArrayList/Array API Mapping

### Property Access

| JavaScript Operation | ArrayList Equivalent | Java Array Equivalent | Status |
|---------------------|---------------------|----------------------|---------|
| `arr.length` | `list.size()` | `array.length` (field) | ✅ Implemented |
| `arr[index]` | `list.get(index)` | `array[index]` | ✅ Implemented |
| `arr[index] = value` | `list.set(index, value)` | `array[index] = value` | ✅ Implemented |
| `arr.length = n` | Custom resize logic | ❌ Not supported (error) | ✅ Implemented |

### Mutating Methods (ArrayList Only)

| JavaScript Method | ArrayList Equivalent | Notes | Status |
|------------------|---------------------|-------|---------|
| `push(elem)` | `add(elem)` | Returns new length | ✅ Implemented |
| `pop()` | `remove(size()-1)` | Returns removed element | ✅ Implemented |
| `shift()` | `remove(0)` | Returns removed element | ✅ Implemented |
| `unshift(elem)` | `add(0, elem)` | Returns new length | ✅ Implemented |
| `splice(i, n, ...)` | Multiple operations | Complex - add/remove | ❌ Not implemented |
| `reverse()` | `Collections.reverse(list)` | Mutates in place, returns array | ✅ Implemented |
| `sort()` | `Collections.sort(list)` | Mutates in place, returns array | ✅ Implemented |
| `fill(val, start, end)` | Loop with `set()` | Fill range with value | ❌ Not implemented |
| `copyWithin(t, s, e)` | Manual copy | Copy within array | ❌ Not implemented |

### Non-Mutating Methods (Both ArrayList and Arrays)

| JavaScript Method | ArrayList Equivalent | Java Array Equivalent | Status |
|------------------|---------------------|----------------------|---------|
| `concat(arr2)` | `new ArrayList<>(list1); addAll(list2)` | `Arrays.copyOf()` + manual copy | ❌ Not implemented |
| `slice(start, end)` | `subList(start, end)` | `Arrays.copyOfRange()` | ❌ Not implemented |
| `indexOf(elem)` | `indexOf(elem)` | Manual loop | ✅ Implemented |
| `lastIndexOf(elem)` | `lastIndexOf(elem)` | Manual loop | ❌ Not implemented |
| `includes(elem)` | `contains(elem)` | Manual loop | ✅ Implemented |
| `join(sep)` | String concat loop | String concat loop | ❌ Not implemented |
| `toString()` | `toString()` | `Arrays.toString()` | ❌ Not implemented |
| `toLocaleString()` | Custom formatting | Custom formatting | ❌ Not implemented |

### Functional Methods (Require Function Support)

| JavaScript Method | Java Equivalent | Notes | Status |
|------------------|----------------|-------|---------|
| `forEach(fn)` | `forEach(Consumer)` | Requires lambda support | ⏸️ Deferred |
| `map(fn)` | `stream().map()` | Returns new array | ⏸️ Deferred |
| `filter(fn)` | `stream().filter()` | Returns new array | ⏸️ Deferred |
| `find(fn)` | `stream().filter().findFirst()` | Returns element | ⏸️ Deferred |
| `findIndex(fn)` | Manual loop | Returns index | ⏸️ Deferred |
| `some(fn)` | `stream().anyMatch()` | Returns boolean | ⏸️ Deferred |
| `every(fn)` | `stream().allMatch()` | Returns boolean | ⏸️ Deferred |
| `reduce(fn, init)` | `stream().reduce()` | Accumulator | ⏸️ Deferred |
| `reduceRight(fn, init)` | Reverse + reduce | Right-to-left | ⏸️ Deferred |
| `flat(depth)` | Recursive flattening | Flatten nested arrays | ⏸️ Deferred |
| `flatMap(fn)` | `stream().flatMap()` | Map then flatten | ⏸️ Deferred |

### ES2023 Non-Mutating Alternatives

| JavaScript Method | Java Equivalent | Notes | Status |
|------------------|----------------|-------|---------|
| `toReversed()` | `new ArrayList<>(list); reverse()` | Returns new array | ❌ Not implemented |
| `toSorted()` | `new ArrayList<>(list); sort()` | Returns new array | ❌ Not implemented |
| `toSpliced(i, n, ...)` | Manual copy + splice | Returns new array | ❌ Not implemented |
| `with(index, value)` | `new ArrayList<>(list); set()` | Returns new array | ❌ Not implemented |

### Iterator Methods

| JavaScript Method | Java Equivalent | Notes | Status |
|------------------|----------------|-------|---------|
| `keys()` | `IntStream.range(0, size())` | Index iterator | ❌ Not implemented |
| `values()` | `iterator()` | Value iterator | ❌ Not implemented |
| `entries()` | Custom iterator | [index, value] pairs | ❌ Not implemented |

### Static Methods

| JavaScript Method | Java Equivalent | Notes | Status |
|------------------|----------------|-------|---------|
| `Array.isArray(obj)` | `obj instanceof ArrayList` | Type check | ❌ Not implemented |
| `Array.from(iterable)` | Various conversions | Create from iterable | ❌ Not implemented |
| `Array.of(...elems)` | `Arrays.asList()` | Create from elements | ❌ Not implemented |

---

## Nested Arrays and Objects Support

### Nested Arrays

**Current Status:** ✅ Supported (via recursive generation)

```typescript
const nested = [[1, 2], [3, 4], [5, 6]]
```

**Implementation:**
- Inner arrays generate their own ArrayList/array bytecode
- Outer array stores inner array references
- Type inference works recursively

**Test Coverage:** Need more comprehensive tests

### Arrays of Objects

**Current Status:** ⏸️ Depends on Object Literal implementation

```typescript
const arr = [{a: 1}, {b: 2}, {c: 3}]
```

**Implementation Strategy:**
- Each object literal generates a LinkedHashMap
- ArrayList stores Map references
- Type: `ArrayList<LinkedHashMap<Object, Object>>`

### Mixed Type Arrays

**Current Status:** ✅ Supported in ArrayList mode

```typescript
const mixed = [1, "hello", true, {key: "value"}, [1, 2, 3]]
```

**Implementation:**
- All elements boxed to Object
- Type: `ArrayList<Object>`
- Runtime type checking required

---

## Edge Cases and Special Scenarios

### 1. Empty Arrays

**Scenario:**
```javascript
const empty = []
const emptyTyped: int[] = []
```

**Handling:**
- ArrayList: `new ArrayList<>()` with size 0
- Java array: `new int[0]`

**Status:** ✅ Implemented (`testReturnEmptyArray`, `testEmptyIntArray`)

---

### 2. Sparse Arrays (Holes)

**Scenario:**
```javascript
const sparse = [1, , 3, , 5]  // Holes at index 1 and 3
```

**JavaScript Behavior:** Holes are `undefined` when accessed

**Java Representation:**
- ArrayList: Store `null` for holes
- Java array: Use default value (0 for int, null for Object)

**Status:** ❌ Not tested - Need to verify hole handling

**Implementation:**
```java
// AST has Optional<Swc4jAstExprOrSpread>
for (var elemOpt : arrayLit.getElems()) {
    if (elemOpt.isPresent()) {
        // Generate element
    } else {
        // This is a hole - store null/default
        code.aconst_null();  // For ArrayList
    }
}
```

---

### 3. Very Large Arrays

**Scenario:**
```javascript
const large = new Array(1000000)  // 1 million elements
```

**Challenges:**
- JVM method size limit: 65535 bytes
- Inline initialization may exceed limit
- Need to split into multiple initialization blocks

**Status:** ❌ Not tested

**Mitigation:**
- For large arrays, use loop initialization instead of inline
- Threshold: ~1000 elements before switching to loop

---

### 4. Array Length Assignment (ArrayList Only)

**Scenario:**
```javascript
const arr = [1, 2, 3, 4, 5]
arr.length = 3      // Shrink to [1, 2, 3]
arr.length = 0      // Clear array
arr.length = 10     // Grow with undefined (nulls)
```

**Implementation:**
```java
// Shrink
while (list.size() > newLength) {
    list.remove(list.size() - 1);
}

// Grow
while (list.size() < newLength) {
    list.add(null);
}

// Clear
list.clear();
```

**Status:** ✅ Partially implemented (`testArrayClear`, `testArrayShrink`)
- ✅ Shrink/clear
- ❌ Grow with nulls - not tested

---

### 5. Out-of-Bounds Access

**Scenario:**
```javascript
const arr = [1, 2, 3]
const x = arr[10]     // undefined in JS
arr[10] = 99          // JS auto-expands array
```

**Java Behavior:**
- ArrayList.get(10): Throws `IndexOutOfBoundsException`
- Array[10]: Throws `ArrayIndexOutOfBoundsException`

**JavaScript Behavior:**
- Read: Returns `undefined`
- Write: Auto-expands with holes

**Status:** ❌ Not handled - throws exception (different from JS)

**Options:**
1. **Throw exception** (current behavior) - Java semantics
2. **Auto-expand ArrayList** - More JS-like, but breaks Java expectations
3. **Return null** for reads - Partial JS semantics

**Recommendation:** Keep current behavior (throw exception) for now

---

### 6. Negative Indices

**Scenario:**
```javascript
const arr = [1, 2, 3, 4, 5]
const last = arr[-1]      // undefined in JS (not Python-style)
arr[-1] = 99              // Creates property "-1", doesn't modify array
```

**JavaScript Behavior:** Negative indices are treated as string properties, not array indices

**Java Behavior:**
- ArrayList.get(-1): Throws `IndexOutOfBoundsException`
- Array[-1]: Throws `ArrayIndexOutOfBoundsException`

**Status:** ❌ Not tested - likely throws exception

**Recommendation:** Keep current behavior (matches Java semantics)

---

### 7. Non-Integer Indices

**Scenario:**
```javascript
arr[3.14] = "pi"       // Creates property "3.14"
arr["hello"] = "world" // Creates property "hello"
```

**JavaScript Behavior:** Non-integer indices create object properties, not array elements

**Java Behavior:** ArrayList only accepts integer indices

**Status:** ❌ Not supported - ArrayList is not a Map

**Recommendation:**
- Compile-time error if non-integer index detected
- This is a fundamental difference between JS arrays (which are objects) and Java collections

---

### 8. Type Coercion in Typed Arrays

**Scenario:**
```typescript
const arr: int[] = [1.5, 2.7, 3.9]  // Doubles to ints
const arr2: long[] = [1, 2, 3]      // Ints to longs
```

**Handling:**
- Use `TypeConversionHelper.convertPrimitiveType()`
- Truncate or widen as needed

**Status:** ✅ Implemented in `generateJavaArray()`

**Edge Cases:**
- ❗ Overflow: `long` value → `int` (truncates)
- ❗ Precision loss: `double` → `int` (truncates decimal)
- ❗ Boolean to int: true → 1, false → 0 (if supported)

---

### 9. Null and Undefined Elements

**Scenario:**
```javascript
const arr = [1, null, undefined, 4]
```

**Handling:**
- **ArrayList mode:** null → null, undefined → null (or error)
- **Java array (primitives):** Cannot store null - error
- **Java array (reference types):** null allowed

**Status:** ❌ Not fully tested

**Implementation:**
```typescript
// Should work
const arr: Array<Integer> = [1, null, 3]

// Should error - primitives can't be null
const arr2: int[] = [1, null, 3]

// undefined should error or be treated as null
const arr3 = [1, undefined, 3]
```

---

### 10. Mixed Type Arrays (ArrayList Only)

**Scenario:**
```javascript
const mixed = [1, "hello", true, 3.14, {key: "value"}, [1, 2]]
```

**Handling:**
- All elements boxed to Object
- Type: `ArrayList<Object>`
- No compile-time type safety

**Status:** ✅ Supported (default ArrayList mode)

**Trade-offs:**
- ✅ Flexibility (can store anything)
- ❌ No type safety
- ❌ Requires runtime casting for retrieval

---

### 11. Array Destructuring (Future)

**Scenario:**
```javascript
const [a, b, c] = [1, 2, 3]
const [first, ...rest] = [1, 2, 3, 4, 5]
```

**Status:** ⏸️ Requires pattern matching support - deferred

---

### 12. Spread Operator in Arrays

**Scenario:**
```javascript
const arr1 = [1, 2, 3]
const arr2 = [4, 5, 6]
const merged = [0, ...arr1, ...arr2, 7]  // [0, 1, 2, 3, 4, 5, 6, 7]
```

**Status:** ❌ Not implemented

**Detection:** `Swc4jAstArrayLit.isSpreadPresent()` method exists in AST

**Implementation Strategy:**
```java
for (var elemOpt : arrayLit.getElems()) {
    if (elemOpt.isPresent()) {
        var elem = elemOpt.get();
        if (elem.getSpread().isPresent()) {
            // Spread element - generate spread expression
            ISwc4jAstExpr spreadExpr = elem.getExpr();
            callback.generateExpr(code, cp, spreadExpr, null, context, options);

            // Call list.addAll(spreadList) or manual array copying
            int addAllMethod = cp.addMethodRef("java/util/ArrayList",
                "addAll", "(Ljava/util/Collection;)Z");
            code.invokevirtual(addAllMethod);
            code.pop();  // Discard boolean return
        } else {
            // Regular element
            // ... existing element generation
        }
    }
}
```

---

### 13. Array.from() and Array Constructors

**Scenario:**
```javascript
const arr1 = new Array(5)           // Length 5, all undefined
const arr2 = new Array(1, 2, 3)     // [1, 2, 3]
const arr3 = Array.from("hello")    // ["h", "e", "l", "l", "o"]
```

**Status:** ❌ Not implemented - requires function call support

---

### 14. Multi-Dimensional Arrays

**Scenario:**
```typescript
const matrix: int[][] = [[1, 2], [3, 4], [5, 6]]
const cube: int[][][] = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
```

**Status:** ⏸️ Partially supported

**Implementation:**
- Java array mode: `[[I` descriptor for int[][]
- Recursive generation of nested arrays
- Each dimension creates its own array

**Test Coverage:** Need comprehensive multi-dimensional tests

---

### 15. Arrays with Function Elements (Future)

**Scenario:**
```javascript
const funcs = [
  function() { return 1; },
  () => 2,
  function named() { return 3; }
]
```

**Status:** ⏸️ Deferred until function compilation support

---

### 16. Reference vs. Value Semantics

**Scenario:**
```javascript
const arr1 = [1, 2, 3]
const arr2 = arr1        // Reference, not copy
arr2[0] = 99
console.log(arr1[0])     // 99 - modified!
```

**Java Behavior:** Same - ArrayList is a reference type

**Status:** ✅ Correct by default (Java references)

**Note:** Spreading creates a shallow copy:
```javascript
const arr2 = [...arr1]   // New array (shallow copy)
```

---

### 17. Array Methods Return Types

**Scenario:**
```javascript
arr.push(4)        // Returns new length (number)
arr.pop()          // Returns removed element
arr.concat([1,2])  // Returns new array
```

**Handling:** Each method must return correct type

**Status:** ❌ Most methods not implemented yet

---

### 18. toString() and valueOf()

**Scenario:**
```javascript
const arr = [1, 2, 3]
String(arr)        // "1,2,3"
arr.toString()     // "1,2,3"
arr.valueOf()      // [1, 2, 3] (returns self)
```

**Java Equivalent:**
- `toString()`: ArrayList.toString() returns "[1, 2, 3]" (different format!)
- Arrays: Arrays.toString() needed

**Status:** ❌ Not implemented

---

### 19. Array Comparison

**Scenario:**
```javascript
[1, 2] == [1, 2]   // false (reference comparison)
```

**Java Behavior:** Same - reference comparison

**Deep Equality:** Would need Arrays.equals() or custom comparison

**Status:** ✅ Default reference comparison is correct

---

### 20. Frozen/Sealed Arrays

**Scenario:**
```javascript
const arr = Object.freeze([1, 2, 3])
```

**Java Equivalent:** `Collections.unmodifiableList()`

**Status:** ❌ Not implemented

---

### 21. Array-like Objects

**Scenario:**
```javascript
const arrayLike = {0: "a", 1: "b", 2: "c", length: 3}
Array.from(arrayLike)  // ["a", "b", "c"]
```

**Status:** ❌ Not applicable - Maps are separate from arrays

---

### 22. Typed Arrays (Uint8Array, etc.)

**Scenario:**
```javascript
const buffer = new Uint8Array([1, 2, 3, 4])
```

**Status:** ❌ Not planned - use byte[] instead

---

### 23. ArrayBuffer and Views

**Scenario:**
```javascript
const buffer = new ArrayBuffer(16)
```

**Status:** ❌ Not planned - use ByteBuffer if needed

---

### 24. Proxy Arrays

**Scenario:**
```javascript
const handler = {
  get(target, prop) { /* custom logic */ }
}
const arr = new Proxy([1, 2, 3], handler)
```

**Status:** ❌ Not supported - no Proxy equivalent in Java

---

### 25. Array Subclassing

**Scenario:**
```javascript
class MyArray extends Array {
  // custom methods
}
```

**Status:** ❌ Not planned

---

### 26. Delete Operator on Array Elements

**Scenario:**
```javascript
const arr = [1, 2, 3, 4, 5]
delete arr[2]      // Creates hole, doesn't shift
// arr is now [1, 2, <empty>, 4, 5]
```

**ArrayList Behavior:** Remove element and shift
```java
list.remove(2);  // Removes and shifts: [1, 2, 4, 5]
```

**Java Array Behavior:** Not allowed - fixed size

**Status:** ✅ Implemented for ArrayList (`testArrayDelete`)
- Sets element to null (hole) or removes it?
- **Current implementation:** Removes element (shifts) - Different from JS!

**Fix Needed:** Should set to null instead of removing

---

### 27. Array Constructor with Holes

**Scenario:**
```javascript
const arr = new Array(5)  // [empty × 5]
```

**Status:** ❌ Not implemented - requires constructor call support

---

### 28. Getter/Setter on Array Indices

**Scenario:**
```javascript
Object.defineProperty(arr, 0, {
  get() { return this._value; },
  set(v) { this._value = v; }
})
```

**Status:** ❌ Not applicable - ArrayList doesn't support property descriptors

---

### 29. Non-Writable Length Property

**Scenario:**
```javascript
Object.defineProperty(arr, 'length', {writable: false})
```

**Status:** ❌ Not applicable - ArrayList.size() is always computed

---

### 30. Array-like Object Properties

**Scenario:**
```javascript
const arr = [1, 2, 3]
arr.customProperty = "hello"  // JS allows this
```

**Status:** ❌ Not supported - ArrayList is not an object with arbitrary properties

---

## Implementation Priorities

### Phase 1: Core Array Creation ✅ COMPLETE
- [x] Empty arrays (ArrayList and Java arrays)
- [x] Simple element storage
- [x] Type detection (array descriptor parsing)
- [x] All primitive types (8 types)
- [x] Reference types (String[], Object[])
- [x] Value boxing for ArrayList
- [x] Type conversion for Java arrays

### Phase 2: Basic Operations ✅ MOSTLY COMPLETE
- [x] Length property read
- [x] Index access (get)
- [x] Index assignment (set)
- [x] push() method
- [x] delete operator
- [x] length assignment (shrink/clear)
- [ ] Nested arrays (tested more thoroughly)
- [ ] Sparse arrays (holes)

### Phase 3: Common Array Methods (Priority: HIGH)
- [x] `pop()` - Remove last element ✅ **IMPLEMENTED**
  - **Implementation:** CallExpressionGenerator.java lines 78-93
  - **Bytecode:** Uses `dup`, `size()`, `iconst 1`, `isub`, `remove(I)Ljava/lang/Object;`
  - **Return:** Returns the removed element (Object type)
  - **Tests:** 8 comprehensive tests covering basic functionality, length changes, multiple pops, type compatibility, edge cases, integration with push, error handling
- [x] `shift()` - Remove first element ✅ **IMPLEMENTED**
  - **Implementation:** CallExpressionGenerator.java lines 94-103
  - **Bytecode:** Uses `iconst 0`, `remove(I)Ljava/lang/Object;`
  - **Return:** Returns the removed element (Object type)
  - **Tests:** 9 comprehensive tests covering basic functionality, length changes, multiple shifts, type compatibility, edge cases, integration with push, remaining elements verification, error handling
- [x] `unshift(elem)` - Add to beginning ✅ **IMPLEMENTED**
  - **Implementation:** CallExpressionGenerator.java lines 104-121
  - **Bytecode:** Uses `iconst 0`, element expression, boxing if needed, `add(ILjava/lang/Object;)V`
  - **Return:** void (JavaScript returns new length, but we don't return it yet)
  - **Tests:** 9 comprehensive tests covering basic functionality, length changes, multiple unshifts, empty array, type compatibility, integration with shift/push, order preservation, error handling
- [x] `indexOf(elem)` - Find index ✅ **IMPLEMENTED**
  - **Implementation:** CallExpressionGenerator.java lines 122-146
  - **Bytecode:** Uses element expression, boxing if needed, `indexOf(Ljava/lang/Object;)I`, then boxes int result to Integer
  - **Return:** Integer (boxed int) - returns index or -1 if not found
  - **Tests:** 10 comprehensive tests covering basic functionality, not found case, first/last element, duplicates (returns first), strings, empty array, mixed types, after modifications, error handling
  - **Note:** Result must be boxed because indexOf() returns primitive int but expressions expect Object
- [x] `includes(elem)` - Check existence ✅ **IMPLEMENTED**
  - **Implementation:** CallExpressionGenerator.java lines 147-171
  - **Bytecode:** Uses element expression, boxing if needed, `contains(Ljava/lang/Object;)Z`, then boxes boolean result to Boolean
  - **Return:** Boolean (boxed boolean) - returns true if element exists, false otherwise
  - **Tests:** 10 comprehensive tests covering basic functionality, not found case, first/last element, strings, empty array, mixed types, after modifications, type mismatch, error handling
  - **Note:** Result must be boxed because contains() returns primitive boolean but expressions expect Object
- [x] `reverse()` - Reverse in place ✅ **IMPLEMENTED**
  - **Implementation:** CallExpressionGenerator.java lines 172-182
  - **Bytecode:** Uses `dup` to duplicate array reference, then `invokestatic Collections.reverse(List)V`
  - **Return:** ArrayList (the array itself) - JavaScript's reverse() returns the array for method chaining
  - **Tests:** 8 comprehensive tests covering basic reverse, returns array, strings, empty array, single element, mixed types, reverse twice (restore original), after modifications, error handling
  - **Note:** Collections.reverse() returns void, so we use `dup` before the call to keep the array reference on the stack for return
- [x] `sort()` - Sort in place ✅ **IMPLEMENTED**
  - **Implementation:** CallExpressionGenerator.java lines 183-193
  - **Bytecode:** Uses `dup` to duplicate array reference, then `invokestatic Collections.sort(List)V`
  - **Return:** ArrayList (the array itself) - JavaScript's sort() returns the array for method chaining
  - **Tests:** 10 comprehensive tests covering basic sort (integers), returns array, strings (alphabetical), empty array (via pop operations), single element, already sorted, reverse sorted, after modifications (push/unshift), duplicates, error handling
  - **Note:** Collections.sort() returns void, so we use `dup` before the call to keep the array reference on the stack for return
  - **Limitation:** Elements must be Comparable - sorting mixed types (e.g., Integer + String) will throw ClassCastException at runtime
- [ ] `splice(index, count, ...items)` - Complex insertion/deletion
- [ ] `concat(arr2)` - Merge arrays
- [ ] `slice(start, end)` - Extract subarray
- [ ] `join(sep)` - Convert to string

### Phase 4: Spread Operator (Priority: HIGH)
- [ ] Detect spread elements in AST
- [ ] Generate `addAll()` for ArrayList
- [ ] Array copying for Java arrays
- [ ] Nested spreads
- [ ] Test spread order

### Phase 5: Advanced Features (Priority: MEDIUM)
- [ ] Multi-dimensional arrays
- [ ] Array methods with return values
- [ ] `toString()` / `toLocaleString()`
- [ ] `fill(value, start, end)`
- [ ] `copyWithin(target, start, end)`
- [ ] ES2023 non-mutating methods (`toReversed`, `toSorted`, `with`)

### Phase 6: Functional Methods (Priority: LOW - Requires Functions)
- [ ] `forEach(callback)`
- [ ] `map(callback)`
- [ ] `filter(callback)`
- [ ] `find(callback)` / `findIndex(callback)`
- [ ] `some(callback)` / `every(callback)`
- [ ] `reduce(callback, initial)`
- [ ] `flat(depth)` / `flatMap(callback)`

### Phase 7: Static Methods (Priority: LOW)
- [ ] `Array.isArray(obj)`
- [ ] `Array.from(arrayLike)`
- [ ] `Array.of(...elements)`

---

## Known Issues and Limitations

### 1. Delete Behavior Mismatch
**Issue:** `delete arr[i]` in ArrayList mode removes element (shifts indices) instead of creating hole

**JavaScript:** `delete arr[2]` → `[1, 2, <empty>, 4, 5]` (hole)
**Current Java:** `list.remove(2)` → `[1, 2, 4, 5]` (shift)

**Fix:** Set element to null instead of removing:
```java
list.set(index, null);  // Creates hole
```

### 2. Out-of-Bounds Access
**Issue:** Throws exception instead of returning undefined

**JavaScript:** `arr[100]` → `undefined`
**Java:** Throws `IndexOutOfBoundsException`

**Status:** Acceptable difference (Java semantics)

### 3. Length Expansion with Undefined
**Issue:** `arr.length = 10` doesn't fill with nulls

**Status:** Partially implemented, needs testing

### 4. Array toString() Format
**Issue:** Java format `[1, 2, 3]` vs JS format `1,2,3`

**Fix:** Custom toString() implementation

### 5. No Property Descriptors
**Limitation:** Cannot make array read-only, non-configurable, etc.

**Status:** Fundamental difference from JS

### 6. No Arbitrary Properties
**Limitation:** `arr.customProp = value` not supported

**Status:** Fundamental difference - ArrayList is not an object

### 7. Spread Operator
**Issue:** Not implemented yet

**Priority:** HIGH - common feature

### 8. Functional Methods
**Issue:** Requires function/lambda support

**Status:** Deferred until function compilation is available

---

## Performance Considerations

### ArrayList vs. Java Arrays

**ArrayList:**
- ✅ Dynamic sizing
- ✅ Rich method API
- ❌ Boxing overhead for primitives
- ❌ Indirection (slower access)
- ❌ Higher memory usage

**Java Arrays:**
- ✅ Direct memory access (fast)
- ✅ No boxing for primitives
- ✅ Lower memory usage
- ❌ Fixed size
- ❌ Limited API (no push, pop, etc.)

### Memory Overhead

**ArrayList<Integer> vs. int[]:**
```
int[100]:        400 bytes (4 bytes × 100)
ArrayList<Integer>: ~1600 bytes (16 bytes per Integer object × 100 + overhead)
```

**Recommendation:** Use typed arrays when possible for better performance

### Large Array Initialization

**Issue:** Inline initialization may exceed JVM method size limit (65535 bytes)

**Mitigation:**
- Threshold: ~1000 elements
- Above threshold: Use loop initialization
- Split into multiple methods if needed

---

## Testing Strategy

### Unit Tests Needed

1. **Sparse Arrays:**
   ```typescript
   const sparse = [1, , 3, , 5]
   assertEquals(null, sparse.get(1))  // Hole
   ```

2. **Array Growth:**
   ```typescript
   const arr = [1, 2, 3]
   arr.length = 5
   assertEquals(5, arr.size())
   assertEquals(null, arr.get(4))  // Filled with null
   ```

3. **Nested Arrays:**
   ```typescript
   const nested = [[1, 2], [3, 4]]
   const inner = nested.get(0) as ArrayList<Integer>
   assertEquals(1, inner.get(0))
   ```

4. **Mixed Types:**
   ```typescript
   const mixed = [1, "hello", true]
   assertEquals(1, ((Integer) mixed.get(0)).intValue())
   assertEquals("hello", (String) mixed.get(1))
   ```

5. **Spread Operator:**
   ```typescript
   const arr1 = [1, 2, 3]
   const arr2 = [0, ...arr1, 4]
   assertEquals([0, 1, 2, 3, 4], arr2)
   ```

6. **Multi-Dimensional:**
   ```typescript
   const matrix: int[][] = [[1, 2], [3, 4]]
   assertEquals(3, matrix[1][0])
   ```

7. **Array Methods:**
   - pop(), shift(), unshift()
   - splice() with various arguments
   - concat(), slice()
   - indexOf(), includes()
   - reverse(), sort()

---

## Example Test Cases

```typescript
namespace com {
  export class ArrayTests {
    // Test 1: Sparse array
    testSparseArray() {
      const arr = [1, , 3, , 5]
      return arr.length  // Should be 5
    }

    // Test 2: Array growth
    testArrayGrowth() {
      const arr = [1, 2, 3]
      arr.length = 5
      return arr.length  // Should be 5
    }

    // Test 3: Nested arrays
    testNestedArrays() {
      const nested = [[1, 2], [3, 4], [5, 6]]
      const inner = nested[1] as Array<Integer>
      return inner[0]  // Should be 3
    }

    // Test 4: Spread operator
    testSpread() {
      const arr1: Array<Integer> = [1, 2, 3]
      const arr2: Array<Integer> = [0, ...arr1, 4]
      return arr2.length  // Should be 5
    }

    // Test 5: Array concatenation
    testConcat() {
      const arr1 = [1, 2]
      const arr2 = [3, 4]
      const merged = arr1.concat(arr2)
      return merged.length  // Should be 4
    }

    // Test 6: Array slice
    testSlice() {
      const arr = [1, 2, 3, 4, 5]
      const sliced = arr.slice(1, 3)
      return sliced.length  // Should be 2
    }

    // Test 7: Array indexOf
    testIndexOf() {
      const arr = [10, 20, 30, 40]
      return arr.indexOf(30)  // Should be 2
    }

    // Test 8: Array reverse
    testReverse() {
      const arr = [1, 2, 3]
      arr.reverse()
      return arr[0]  // Should be 3
    }

    // Test 9: Array splice
    testSplice() {
      const arr = [1, 2, 3, 4, 5]
      arr.splice(2, 1, 99)  // Remove 1 at index 2, insert 99
      return arr[2]  // Should be 99
    }

    // Test 10: Pop and push
    testPopPush() {
      const arr = [1, 2, 3]
      const popped = arr.pop()  // Returns 3
      arr.push(4)
      return arr.length  // Should be 3
    }

    // Test 11: Multi-dimensional typed array
    testMultiDim() {
      const matrix: int[][] = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
      return matrix[1][1]  // Should be 5
    }

    // Test 12: Mixed types
    testMixedTypes() {
      const mixed = [42, "hello", true, {a: 1}, [1, 2]]
      return mixed.length  // Should be 5
    }
  }
}
```

---

## References

- [JavaScript Array Methods (MDN)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array)
- [Java ArrayList Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)
- [Java Arrays Class](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html)
- [JVM Array Instructions](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html#jvms-6.5.newarray)
- [ArrayLiteralGenerator.java](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/lit/ArrayLiteralGenerator.java)
- [Swc4jAstArrayLit.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstArrayLit.java)

---

## Summary

**Current Implementation:** ✅ Solid foundation (97 tests passing)
- Basic array creation and operations work
- Both ArrayList and Java array modes supported
- Type conversion and boxing implemented
- Array methods: `push()`, `pop()`, `shift()`, `unshift()`, `indexOf()`, `includes()`, `reverse()`, `sort()` ✅

**Recently Completed:**
- ✅ **sort() method** - Implemented in CallExpressionGenerator.java
  - Sorts ArrayList in place using Collections.sort() with natural ordering
  - 10 comprehensive tests added
  - Error handling for Java arrays (throws exception)
  - Returns the array itself for method chaining (JavaScript behavior)
  - Uses `dup` instruction before Collections.sort() call to keep array reference on stack
  - Works with integers, strings, and any Comparable types
  - **Note:** Sorting mixed types will throw ClassCastException at runtime (Java limitation)

**Next Steps:**
1. Implement spread operator support (HIGH priority)
2. Add common array methods (`join`, `concat`, `slice`, `splice`)
3. Fix delete behavior to create holes instead of shifting
4. Test nested and multi-dimensional arrays thoroughly
5. Add functional methods (when function support is available)

**Key Differences from JavaScript:**
- No arbitrary properties on arrays
- Out-of-bounds throws exception instead of undefined
- Fixed size for Java arrays (no dynamic resizing)
- ArrayList.toString() format differs from JS
- No property descriptors or Proxy support
