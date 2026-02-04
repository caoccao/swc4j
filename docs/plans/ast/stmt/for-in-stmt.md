# For-In Statement Implementation Plan

## Overview

This document outlines the implementation plan for supporting for-in loops in TypeScript to JVM bytecode compilation. For-in loops iterate over the enumerable property names (keys) of an object, or indices of an array.

**Current Status:** ✅ **COMPLETE** - All phases implemented, all 29 tests passing

**Syntax:**
```typescript
for (key in object) { body }
for (const prop in obj) { console.log(prop); }
for (let index in array) { console.log(index); }
```

**Implementation File:** `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/ForInStatementProcessor.java`

**Test Files:**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/forinstmt/TestCompileAstForInStmtBasic.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/forinstmt/TestCompileAstForInStmtArray.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/forinstmt/TestCompileAstForInStmtBreakContinue.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/forinstmt/TestCompileAstForInStmtExistingVar.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/forinstmt/TestCompileAstForInStmtNested.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/forinstmt/TestCompileAstForInStmtEdgeCases.java`

**AST Definition:** [Swc4jAstForInStmt.java](../../../../src/main/java/com/caoccao/javet/swc4j/ast/stmt/Swc4jAstForInStmt.java)

---

## For-In Statement Fundamentals

### Statement Semantics

A for-in statement has three components:
1. **Left** (ISwc4jAstForHead) - Loop variable declaration or existing variable
2. **Right** (ISwc4jAstExpr) - Object or array to iterate over
3. **Body** (ISwc4jAstStmt) - Statement(s) executed for each key

### JavaScript/TypeScript Behavior

```typescript
// Iterate over object keys
const obj = { a: 1, b: 2, c: 3 };
for (const key in obj) {
  console.log(key);  // "a", "b", "c"
}

// Iterate over array indices (as strings)
const arr = [10, 20, 30];
for (const index in arr) {
  console.log(index);  // "0", "1", "2" (strings!)
}

// With existing variable
let prop;
for (prop in obj) {
  console.log(prop);
}

// With break and continue
for (const key in obj) {
  if (key === 'skip') continue;
  if (key === 'stop') break;
  process(key);
}
```

### Java Collection Mapping

In this compiler:
- **Objects** are represented as `java.util.LinkedHashMap<Object, Object>`
- **Arrays** are represented as `java.util.ArrayList<Object>`

**For Objects (LinkedHashMap):**
```java
// For: for (const key in obj)
java.util.Set<Object> keySet = obj.keySet();
java.util.Iterator<Object> iterator = keySet.iterator();
while (iterator.hasNext()) {
    Object key = iterator.next();
    // Convert key to String
    String keyStr = String.valueOf(key);
    // body
}
```

**For Arrays (ArrayList):**
```java
// For: for (const index in arr)
int size = arr.size();
for (int i = 0; i < size; i++) {
    String index = String.valueOf(i);  // Convert index to string
    // body
}
```

### JVM Bytecode Strategy

**Pattern for Object Iteration:**
```
// Get iterator
aload <obj>                      // Load object (LinkedHashMap)
invokeinterface keySet()         // Get key set
invokeinterface iterator()       // Get iterator
astore <iterator_slot>           // Store iterator

test_label:
aload <iterator_slot>            // Load iterator
invokeinterface hasNext()        // Test if more elements
ifeq end_label                   // Exit if no more

aload <iterator_slot>            // Load iterator
invokeinterface next()           // Get next key
invokestatic String.valueOf()    // Convert to string
astore <key_slot>                // Store key in loop variable

body_code                        // Execute body

goto test_label                  // Jump back to test
end_label:
```

**Pattern for Array Iteration:**
```
// Initialize counter
aload <arr>                      // Load array (ArrayList)
invokeinterface size()           // Get size
istore <size_slot>               // Store size
iconst_0                         // Initialize i = 0
istore <i_slot>                  // Store i

test_label:
iload <i_slot>                   // Load i
iload <size_slot>                // Load size
if_icmpge end_label              // Exit if i >= size

iload <i_slot>                   // Load i
invokestatic String.valueOf()    // Convert to string
astore <key_slot>                // Store index string

body_code                        // Execute body

iinc <i_slot>, 1                 // i++
goto test_label                  // Jump back to test
end_label:
```

---

## Implementation Phases

### Phase 1: Basic For-In Over Objects (Priority: HIGH)

Support simple for-in loops over LinkedHashMap objects.

**Scope:**
- For-in with new variable declaration (let/const key in obj)
- Iteration over LinkedHashMap (objects)
- Block statement body
- Key as string variable
- Simple body statements

**Example Bytecode:**
```
// For: for (const key in obj) { process(key); }

aload_1                          // Load obj (local 1)
invokeinterface java/util/Map.keySet()Ljava/util/Set;
invokeinterface java/util/Set.iterator()Ljava/util/Iterator;
astore_2                         // Store iterator (local 2)

test_label:
aload_2                          // Load iterator
invokeinterface java/util/Iterator.hasNext()Z
ifeq end_label                   // Exit if no more

aload_2                          // Load iterator
invokeinterface java/util/Iterator.next()Ljava/lang/Object;
invokestatic java/lang/String.valueOf(Ljava/lang/Object;)Ljava/lang/String;
astore_3                         // Store key (local 3)

// body: process(key)
aload_3                          // Load key
invokestatic process(Ljava/lang/String;)V

goto test_label                  // Jump back
end_label:
```

**Test Coverage:**
1. Basic for-in over object with 3 keys
2. For-in over empty object (no iterations)
3. For-in over object with single key
4. For-in with complex body
5. For-in with multiple statements in body
6. For-in with let vs const declaration
7. For-in accessing key within body
8. For-in with object property access in body
9. For-in over nested object
10. For-in with return statement in body

---

### Phase 2: For-In Over Arrays (Priority: HIGH)

Support for-in loops over ArrayList (arrays), iterating over indices as strings.

**Scope:**
- For-in over ArrayList
- Indices returned as strings ("0", "1", "2", ...)
- Proper string conversion
- Empty arrays
- Large arrays

**Example Bytecode:**
```
// For: for (const index in arr) { process(index); }

aload_1                          // Load arr (ArrayList, local 1)
invokeinterface java/util/List.size()I
istore_2                         // Store size (local 2)
iconst_0                         // i = 0
istore_3                         // Store i (local 3)

test_label:
iload_3                          // Load i
iload_2                          // Load size
if_icmpge end_label              // Exit if i >= size

iload_3                          // Load i
invokestatic java/lang/String.valueOf(I)Ljava/lang/String;
astore_4                         // Store index string (local 4)

// body: process(index)
aload_4                          // Load index
invokestatic process(Ljava/lang/String;)V

iinc 3, 1                        // i++
goto test_label                  // Jump back
end_label:
```

**Test Coverage:**
1. For-in over array with 5 elements
2. For-in over empty array (no iterations)
3. For-in over single-element array
4. For-in verifying indices are strings not numbers
5. For-in using index to access array elements
6. For-in over large array (1000+ elements)
7. For-in over array of different types
8. For-in with array.length access in body
9. For-in with nested array access

---

### Phase 3: Break and Continue (Priority: HIGH)

Support break and continue statements within for-in loops.

**Scope:**
- Break statement (exit loop immediately)
- Continue statement (skip to next iteration)
- Unlabeled break/continue (innermost loop)
- Multiple break/continue in same loop
- Break/continue in nested if within loop

**Example Bytecode:**
```
// For: for (const key in obj) { if (key === 'skip') continue; if (key === 'stop') break; }

aload_1                          // Load obj
// ... get iterator ...
astore_2                         // Store iterator

test_label:
aload_2
invokeinterface hasNext()Z
ifeq end_label

aload_2
invokeinterface next()Ljava/lang/Object;
invokestatic String.valueOf()
astore_3                         // Store key

// if (key === 'skip')
aload_3
ldc "skip"
invokevirtual String.equals()Z
ifeq check_break
goto test_label                  // continue: jump to test

check_break:
// if (key === 'stop')
aload_3
ldc "stop"
invokevirtual String.equals()Z
ifeq body_end
goto end_label                   // break: jump to end

body_end:
goto test_label
end_label:
```

**Test Coverage:**
1. For-in with break in body
2. For-in with continue in body
3. Break in nested if statement
4. Continue in nested if statement
5. Multiple breaks in different conditions
6. Multiple continues in different conditions
7. Both break and continue in same loop
8. Break on first iteration
9. Continue on all iterations
10. Unreachable code after unconditional break

---

### Phase 4: Existing Variable (Priority: MEDIUM)

Support for-in using an existing variable instead of declaring a new one.

**Scope:**
- For-in without declaration (for (x in obj))
- Using pre-declared variable
- Variable remains accessible after loop
- Variable modified by loop

**Example:**
```typescript
let key;
for (key in obj) {
  process(key);
}
console.log(key);  // Last key (or undefined if empty)
```

**Test Coverage:**
1. For-in with pre-declared variable
2. Variable accessible after loop
3. Variable contains last key after loop
4. Variable undefined/empty for empty object
5. Using same variable in multiple for-in loops
6. Variable shadowing outer variable

---

### Phase 5: Type Detection and Handling (Priority: MEDIUM)

Handle different types for the right-hand side expression.

**Scope:**
- Objects (LinkedHashMap) - iterate keys
- Arrays (ArrayList) - iterate indices
- Primitives (int, string, etc.) - skip or error
- Null/undefined - skip iteration
- Type inference for right-hand side
- Runtime type checking

**Example:**
```typescript
// Object
for (const k in { a: 1 }) { }

// Array
for (const i in [1, 2, 3]) { }

// Null/undefined - should skip
for (const x in null) { }  // No iterations

// Primitive - should skip or error
for (const x in 42) { }  // No iterations or error
```

**Test Coverage:**
1. For-in over object literal
2. For-in over array literal
3. For-in over null (should not iterate)
4. For-in over undefined (should not iterate)
5. For-in over primitive number (should not iterate)
6. For-in over primitive string (iterate indices)
7. Type detection via inferTypeFromExpr
8. Runtime type check with instanceof
9. For-in over mixed type variable

---

### Phase 6: Nested For-In Loops (Priority: MEDIUM)

Support nested for-in loops.

**Scope:**
- For-in inside for-in
- Multiple levels of nesting
- Break/continue in nested loops
- Shared variables between loops
- Different object types at each level

**Example:**
```typescript
const outer = { a: { x: 1 }, b: { y: 2 } };
for (const key1 in outer) {
  const inner = outer[key1];
  for (const key2 in inner) {
    console.log(key1, key2);
  }
}
```

**Test Coverage:**
1. Two-level nested for-in
2. Three-level nested for-in
3. Break in outer loop
4. Break in inner loop
5. Continue in outer loop
6. Continue in inner loop
7. Outer object, inner object
8. Outer array, inner array
9. Outer object, inner array
10. Accessing outer key in inner loop

---

### Phase 7: Labeled Break and Continue (Priority: LOW)

Support labeled break and continue statements.

**Scope:**
- Label declarations on for-in loops
- Labeled break (break to specific loop)
- Labeled continue (continue specific loop)
- Break to outer loop from inner loop
- Continue outer loop from inner loop

**Example:**
```typescript
outer: for (const key1 in obj1) {
  for (const key2 in obj2) {
    if (condition) break outer;     // Break outer loop
    if (other) continue outer;      // Continue outer loop
  }
}
```

**Test Coverage:**
1. Labeled break to outer loop
2. Labeled continue to outer loop
3. Multiple labeled for-in loops
4. Deeply nested with labeled break/continue
5. Label on for-in with mixed nested loops

---

### Phase 8: Edge Cases and Advanced Scenarios (Priority: LOW)

Handle complex scenarios and edge cases.

**Scope:**
- Side effects in right-hand expression
- Variables declared in body
- Return statements in loop body
- Throw statements in loop body
- Nested control flow (if/switch in loop)
- Very large collections
- Empty body
- Complex stack map frame scenarios

**Test Coverage:**
1. Side effects in right expression
2. Return in loop body
3. Throw in loop body
4. Nested if/else in loop
5. Switch statement in loop
6. Empty loop body
7. Loop variable used in complex expressions
8. Multiple returns in loop
9. Very large object (1000+ keys)
10. Complex stack map scenarios

---

## Edge Cases and Special Scenarios

### Basic Edge Cases

1. **Empty Object**
   ```typescript
   for (const k in {}) { }  // No iterations
   ```

2. **Empty Array**
   ```typescript
   for (const i in []) { }  // No iterations
   ```

3. **Single Property**
   ```typescript
   for (const k in { only: 1 }) { }  // One iteration: k = "only"
   ```

4. **Single Element Array**
   ```typescript
   for (const i in [42]) { }  // One iteration: i = "0"
   ```

5. **Break in First Iteration**
   ```typescript
   for (const k in obj) { break; }  // Only iterator setup executes
   ```

6. **Continue in All Iterations**
   ```typescript
   for (const k in obj) { continue; }  // Body effectively empty
   ```

7. **Return in First Iteration**
   ```typescript
   for (const k in obj) { return k; }  // Returns first key
   ```

### Array Index Edge Cases

8. **Array Indices Are Strings**
   ```typescript
   const arr = [10, 20, 30];
   for (const i in arr) {
     typeof i === "string"  // true!
     i === "0" || i === "1" || i === "2"  // true
   }
   ```

9. **Array Index to Number Conversion**
   ```typescript
   for (const i in arr) {
     const num = parseInt(i);  // Convert string to number
     const val = arr[num];     // Access element
   }
   ```

10. **Sparse Arrays**
    ```typescript
    const arr = [];
    arr[0] = 'a';
    arr[5] = 'b';  // Sparse: indices 1-4 don't exist
    for (const i in arr) {
      // In Java ArrayList, will iterate 0-5 (all present)
    }
    ```

### Object Key Edge Cases

11. **Object with String Keys**
    ```typescript
    const obj = { "a": 1, "b": 2 };
    for (const k in obj) { }  // k = "a", "b"
    ```

12. **Object with Number Keys** (converted to strings)
    ```typescript
    const obj = { 1: 'one', 2: 'two' };
    for (const k in obj) { }  // k = "1", "2" (strings)
    ```

13. **Object with Mixed Key Types**
    ```typescript
    const obj = { a: 1, 1: 'one', true: 'yes' };
    for (const k in obj) { }  // All keys as strings
    ```

14. **Order of Iteration**
    ```typescript
    // LinkedHashMap maintains insertion order
    const obj = { c: 3, a: 1, b: 2 };
    for (const k in obj) { }  // "c", "a", "b" (insertion order)
    ```

### Null/Undefined Edge Cases

15. **For-In Over Null**
    ```typescript
    for (const k in null) { }  // Should not iterate (no error)
    ```

16. **For-In Over Undefined**
    ```typescript
    for (const k in undefined) { }  // Should not iterate (no error)
    ```

17. **Right Expression Evaluates to Null**
    ```typescript
    const obj = null;
    for (const k in obj) { }  // No iterations
    ```

18. **Nullable Object**
    ```typescript
    const obj = flag ? { a: 1 } : null;
    for (const k in obj) { }  // Conditional iteration
    ```

### Primitive Type Edge Cases

19. **For-In Over Number**
    ```typescript
    for (const k in 42) { }  // Should not iterate (or error)
    ```

20. **For-In Over Boolean**
    ```typescript
    for (const k in true) { }  // Should not iterate (or error)
    ```

21. **For-In Over String**
    ```typescript
    for (const k in "abc") { }  // May iterate indices "0", "1", "2"
    ```

### Variable Scoping Edge Cases

22. **Loop Variable Scope**
    ```typescript
    for (const k in obj) { }
    // k not visible here
    ```

23. **Variable Shadowing**
    ```typescript
    const k = 'outer';
    for (const k in obj) { }  // Shadows outer k
    // k is still 'outer' here
    ```

24. **Existing Variable**
    ```typescript
    let k;
    for (k in obj) { }
    // k is last key (or undefined if empty)
    ```

25. **Variables Declared in Body**
    ```typescript
    for (const k in obj) {
      const x = obj[k];  // New x each iteration
    }
    ```

### Body Edge Cases

26. **Empty Body**
    ```typescript
    for (const k in obj) { }  // No statements
    ```

27. **Single Statement Body**
    ```typescript
    for (const k in obj) process(k);  // No braces
    ```

28. **Block Statement Body**
    ```typescript
    for (const k in obj) {
      const val = obj[k];
      process(val);
    }
    ```

29. **Return in Body**
    ```typescript
    for (const k in obj) { return k; }  // Returns first key
    ```

30. **Throw in Body**
    ```typescript
    for (const k in obj) { throw new Error(); }
    ```

### Nested Loop Edge Cases

31. **Two-Level Nesting**
    ```typescript
    for (const k1 in obj1) {
      for (const k2 in obj2) { }
    }
    ```

32. **Break in Nested Loop**
    ```typescript
    for (const k1 in obj1) {
      for (const k2 in obj2) {
        if (k2 === 'stop') break;  // Breaks inner loop only
      }
    }
    ```

33. **Continue in Nested Loop**
    ```typescript
    for (const k1 in obj1) {
      for (const k2 in obj2) {
        if (k2 === 'skip') continue;  // Continues inner loop only
      }
    }
    ```

34. **Accessing Outer Key**
    ```typescript
    for (const k1 in obj1) {
      for (const k2 in obj2) {
        console.log(k1, k2);  // Access both keys
      }
    }
    ```

35. **Labeled Break to Outer**
    ```typescript
    outer: for (const k1 in obj1) {
      for (const k2 in obj2) {
        if (condition) break outer;
      }
    }
    ```

36. **Labeled Continue to Outer**
    ```typescript
    outer: for (const k1 in obj1) {
      for (const k2 in obj2) {
        if (condition) continue outer;
      }
    }
    ```

### Mixed Loop Type Edge Cases

37. **For-In Inside Regular For**
    ```typescript
    for (let i = 0; i < 10; i++) {
      for (const k in obj) { }
    }
    ```

38. **Regular For Inside For-In**
    ```typescript
    for (const k in obj) {
      for (let i = 0; i < 10; i++) { }
    }
    ```

39. **For-In Inside While**
    ```typescript
    while (condition) {
      for (const k in obj) { }
    }
    ```

40. **While Inside For-In**
    ```typescript
    for (const k in obj) {
      while (condition) { }
    }
    ```

### Collection Modification Edge Cases

41. **Modifying Object During Iteration** (not recommended)
    ```typescript
    for (const k in obj) {
      delete obj[k];  // Removing during iteration
    }
    ```

42. **Adding Properties During Iteration**
    ```typescript
    for (const k in obj) {
      obj[k + '_new'] = 'value';  // Adding during iteration
    }
    ```

43. **Modifying Array During Iteration**
    ```typescript
    for (const i in arr) {
      arr.push(i);  // Modifying during iteration
    }
    ```

### Type Conversion Edge Cases

44. **Key Used as Number**
    ```typescript
    for (const i in arr) {
      const num = parseInt(i);  // Convert string to number
    }
    ```

45. **Key Used in String Operations**
    ```typescript
    for (const k in obj) {
      const upper = k.toUpperCase();
    }
    ```

46. **Key Compared to Number**
    ```typescript
    for (const i in arr) {
      if (i === 0) { }  // String "0" vs number 0 (false!)
      if (i === "0") { }  // Correct comparison
    }
    ```

### Iterator State Edge Cases

47. **Iterator Shared Between Iterations**
    ```typescript
    // Iterator should be new each time
    function iterate(obj) {
      for (const k in obj) { }
    }
    iterate(obj1);
    iterate(obj2);  // New iterator, not reused
    ```

48. **Concurrent Modification**
    ```typescript
    // Iterator may fail if collection modified during iteration
    ```

### Stack Map Frame Edge Cases

49. **Complex Merge Points**
    ```typescript
    for (const k in obj) {
      let x: int;
      if (k === 'a') {
        x = 1;
      } else {
        x = 2;
      }
      // Merge point
    }
    ```

50. **Multiple Continue Targets**
    ```typescript
    for (const k in obj) {
      if (k === 'a') continue;
      if (k === 'b') continue;
      process(k);
    }
    ```

### Performance Edge Cases

51. **Very Large Object (10000+ keys)**
    ```typescript
    const huge = {};
    for (let i = 0; i < 10000; i++) {
      huge['key' + i] = i;
    }
    for (const k in huge) { }
    ```

52. **Very Large Array (10000+ elements)**
    ```typescript
    const huge = new Array(10000).fill(0);
    for (const i in huge) { }
    ```

53. **Empty Loop (optimization opportunity)**
    ```typescript
    for (const k in huge) { }  // No body
    ```

### Integration Edge Cases

54. **For-In in Method**
    ```typescript
    function keys(obj) {
      const result = [];
      for (const k in obj) {
        result.push(k);
      }
      return result;
    }
    ```

55. **For-In in Constructor**
    ```typescript
    constructor(data) {
      for (const k in data) {
        this[k] = data[k];
      }
    }
    ```

56. **For-In with Field Access**
    ```typescript
    for (const k in this.data) {
      process(k);
    }
    ```

57. **For-In with Method Calls**
    ```typescript
    for (const k in obj) {
      this.process(k);
    }
    ```

58. **Nested If in For-In**
    ```typescript
    for (const k in obj) {
      if (obj[k] > 10) {
        count++;
      }
    }
    ```

59. **For-In in If Statement**
    ```typescript
    if (flag) {
      for (const k in obj) { }
    }
    ```

60. **Multiple Sequential For-In Loops**
    ```typescript
    for (const k in obj1) { sumA += obj1[k]; }
    for (const k in obj2) { sumB += obj2[k]; }
    ```

### Bytecode Edge Cases

61. **Long Jump Offsets (>32KB)**
    ```typescript
    for (const k in obj) {
      // Very large body requiring wide goto
    }
    ```

62. **Multiple Gotos to Same Label**
    ```typescript
    for (const k in obj) {
      if (a) continue;
      if (b) continue;
      if (c) continue;
    }
    ```

### Error Handling Edge Cases

63. **Exception in Right Expression**
    ```typescript
    for (const k in throwing()) { }
    ```

64. **Exception in Body**
    ```typescript
    for (const k in obj) { throw new Error(); }
    ```

65. **Try-Catch in Loop**
    ```typescript
    for (const k in obj) {
      try { risky(k); } catch (e) { }
    }
    ```

66. **Loop in Try-Catch**
    ```typescript
    try {
      for (const k in obj) { risky(); }
    } catch (e) { }
    ```

### Special Statement Edge Cases

67. **Unreachable Code After Break**
    ```typescript
    for (const k in obj) {
      break;
      console.log("unreachable");
    }
    ```

68. **Unreachable Code After Continue**
    ```typescript
    for (const k in obj) {
      continue;
      console.log("unreachable");
    }
    ```

69. **Unreachable Code After Return**
    ```typescript
    for (const k in obj) {
      return k;
      console.log("unreachable");
    }
    ```

### Const vs Let Edge Cases

70. **Const Loop Variable (Cannot Reassign)**
    ```typescript
    for (const k in obj) {
      // k = 'new';  // Error: cannot reassign const
    }
    ```

71. **Let Loop Variable (Can Reassign)**
    ```typescript
    for (let k in obj) {
      k = k.toUpperCase();  // Allowed with let
    }
    ```

### Symbol and Special Keys (Not Applicable in Java)

72. **Symbol Keys** (JavaScript only)
    ```typescript
    // Not applicable - Java doesn't have symbols
    ```

73. **Non-Enumerable Properties** (JavaScript only)
    ```typescript
    // Not applicable - Java doesn't have enumerable concept
    ```

74. **Prototype Chain** (JavaScript only)
    ```typescript
    // Not applicable - Using Java objects, no prototype
    ```

### Type Coercion Edge Cases

75. **Object Key Coercion**
    ```typescript
    const obj = {};
    obj[1] = 'one';     // Number key
    obj['1'] = 'ONE';   // String key (overwrites)
    for (const k in obj) { }  // "1" (string)
    ```

76. **Array Index Boundary**
    ```typescript
    const arr = [1, 2, 3];
    arr[10] = 10;  // Creates sparse array in JS, but in Java?
    for (const i in arr) { }
    ```

### Miscellaneous Edge Cases

77. **Unicode in Keys**
    ```typescript
    const obj = { '変数': 1, 'ключ': 2 };
    for (const k in obj) { }  // Unicode keys
    ```

78. **Very Long Key Names**
    ```typescript
    const obj = { veryLongKeyNameThatExceedsNormalLength: 1 };
    for (const k in obj) { }
    ```

79. **Reserved Keywords as Keys**
    ```typescript
    const obj = { 'for': 1, 'while': 2, 'if': 3 };
    for (const k in obj) { }
    ```

80. **Empty String Key**
    ```typescript
    const obj = { '': 'empty' };
    for (const k in obj) { }  // k = ""
    ```

---

## Bytecode Instruction Reference

### Iterator-Based Instructions (for Objects)

**Get Iterator:**
```
aload <obj>                          // Load LinkedHashMap
invokeinterface java/util/Map.keySet()Ljava/util/Set;
invokeinterface java/util/Set.iterator()Ljava/util/Iterator;
astore <iterator>                    // Store iterator
```

**Test and Get Next:**
```
aload <iterator>
invokeinterface java/util/Iterator.hasNext()Z
ifeq <end_label>                     // Exit if no more

aload <iterator>
invokeinterface java/util/Iterator.next()Ljava/lang/Object;
invokestatic java/lang/String.valueOf(Ljava/lang/Object;)Ljava/lang/String;
astore <key>                         // Store key as string
```

### Index-Based Instructions (for Arrays)

**Initialize Counter:**
```
aload <arr>                          // Load ArrayList
invokeinterface java/util/List.size()I
istore <size>                        // Store size
iconst_0                             // i = 0
istore <i>                           // Store counter
```

**Test and Get Index:**
```
iload <i>
iload <size>
if_icmpge <end_label>                // Exit if i >= size

iload <i>
invokestatic java/lang/String.valueOf(I)Ljava/lang/String;
astore <key>                         // Store index as string
```

**Increment Counter:**
```
iinc <i>, 1                          // i++
goto <test_label>                    // Jump back
```

### Stack Map Frames

- Required at loop entry (test label)
- Required at end label (after loop)
- Must handle backward jump verification
- Iterator/counter/key in local variable slots

---

## AST Structure

### Swc4jAstForInStmt Components

```java
public class Swc4jAstForInStmt {
    ISwc4jAstForHead left;           // Loop variable (declaration or pattern)
    ISwc4jAstExpr right;             // Object/array to iterate over
    ISwc4jAstStmt body;              // Body statement
}
```

### Related AST Types

- **ISwc4jAstForHead** - Can be VarDecl or Pat
  - Swc4jAstVarDecl - Variable declaration (let k, const k)
  - Swc4jAstBindingIdent - Existing variable pattern
- **ISwc4jAstExpr** - Right can be any expression
  - Swc4jAstIdent - Variable reference
  - Swc4jAstObjectLit - Object literal
  - Swc4jAstArrayLit - Array literal
  - Swc4jAstCallExpr - Function call result
- **ISwc4jAstStmt** - Body can be any statement
  - Swc4jAstBlockStmt - Block with multiple statements
  - Swc4jAstExprStmt - Single expression statement
  - Swc4jAstBreakStmt - Break statement
  - Swc4jAstContinueStmt - Continue statement
  - Swc4jAstReturnStmt - Return statement

---

## Implementation Strategy

### Type Detection Algorithm (Compile-Time)

Type checking is performed at compile-time using the `JavaTypeInfo.isAssignableTo()` method to check type hierarchy:

```java
String rightType = compiler.getTypeResolver().inferTypeFromExpr(right);
IterationType iterationType = determineIterationType(rightType);

switch (iterationType) {
    case LIST -> generateArrayIteration(code, cp, forInStmt, labelName, returnTypeInfo);
    case MAP -> generateObjectIteration(code, cp, forInStmt, labelName, returnTypeInfo);
    case STRING -> generateStringIteration(code, cp, forInStmt, labelName, returnTypeInfo);
}

// Type determination uses compile-time type hierarchy checking
private IterationType determineIterationType(String typeDescriptor) {
    // Check for String type
    if ("Ljava/lang/String;".equals(typeDescriptor)) {
        return IterationType.STRING;
    }

    // Check for List types
    if ("Ljava/util/ArrayList;".equals(typeDescriptor) || "Ljava/util/List;".equals(typeDescriptor)) {
        return IterationType.LIST;
    }

    // Check for Map types
    if ("Ljava/util/LinkedHashMap;".equals(typeDescriptor) || "Ljava/util/Map;".equals(typeDescriptor)) {
        return IterationType.MAP;
    }

    // Check user-defined types using JavaTypeInfo.isAssignableTo()
    if (typeDescriptor.startsWith("L") && typeDescriptor.endsWith(";")) {
        JavaTypeInfo typeInfo = registry.resolve(typeName);
        if (typeInfo != null) {
            if (typeInfo.isAssignableTo("Ljava/util/List;")) return IterationType.LIST;
            if (typeInfo.isAssignableTo("Ljava/util/Map;")) return IterationType.MAP;
        }
    }

    throw new Swc4jByteCodeCompilerException(
        "For-in loops require List, Map, or String type, but got: " + typeDescriptor);
}
```

### Code Generation Algorithm (Object)

```java
public void generateObjectIteration(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        Swc4jAstForInStmt forInStmt) {

    // 1. Generate right expression (object)
    compiler.getExpressionProcessor().generate(code, cp, forInStmt.getRight(), null);

    // 2. Get keySet
    int keySetRef = cp.addInterfaceMethodRef(
        "java/util/Map", "keySet", "()Ljava/util/Set;");
    code.invokeinterface(keySetRef, 1);

    // 3. Get iterator
    int iteratorRef = cp.addInterfaceMethodRef(
        "java/util/Set", "iterator", "()Ljava/util/Iterator;");
    code.invokeinterface(iteratorRef, 1);

    // 4. Store iterator
    int iteratorSlot = context.allocateLocalVariable("$iterator", "Ljava/util/Iterator;");
    code.astore(iteratorSlot);

    // 5. Mark test label
    int testLabel = code.getCurrentOffset();

    // 6. Test hasNext
    code.aload(iteratorSlot);
    int hasNextRef = cp.addInterfaceMethodRef(
        "java/util/Iterator", "hasNext", "()Z");
    code.invokeinterface(hasNextRef, 1);
    code.ifeq(0);  // Placeholder, will patch
    int exitPatchPos = code.getCurrentOffset() - 2;

    // 7. Get next key
    code.aload(iteratorSlot);
    int nextRef = cp.addInterfaceMethodRef(
        "java/util/Iterator", "next", "()Ljava/lang/Object;");
    code.invokeinterface(nextRef, 1);

    // 8. Convert to string
    int valueOfRef = cp.addMethodRef(
        "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
    code.invokestatic(valueOfRef);

    // 9. Store in loop variable
    int keySlot = allocateLoopVariable(forInStmt.getLeft());
    code.astore(keySlot);

    // 10. Setup break/continue labels
    int endLabel = code.allocateLabel();
    context.pushBreakLabel(endLabel);
    context.pushContinueLabel(testLabel);

    // 11. Generate body
    compiler.getStatementProcessor().generate(code, cp, forInStmt.getBody(), null);

    // 12. Pop labels
    context.popContinueLabel();
    context.popBreakLabel();

    // 13. Jump back to test
    code.gotoLabel(testLabel);

    // 14. Mark end label
    code.markLabel(endLabel);

    // 15. Patch exit jump
    code.patchShort(exitPatchPos, endLabel - (exitPatchPos - 1));
}
```

### Code Generation Algorithm (Array)

```java
public void generateArrayIteration(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        Swc4jAstForInStmt forInStmt) {

    // 1. Generate right expression (array)
    compiler.getExpressionProcessor().generate(code, cp, forInStmt.getRight(), null);

    // 2. Get size
    int sizeRef = cp.addInterfaceMethodRef(
        "java/util/List", "size", "()I");
    code.invokeinterface(sizeRef, 1);

    // 3. Store size
    int sizeSlot = context.allocateLocalVariable("$size", "I");
    code.istore(sizeSlot);

    // 4. Initialize counter
    code.iconst_0();
    int counterSlot = context.allocateLocalVariable("$i", "I");
    code.istore(counterSlot);

    // 5. Mark test label
    int testLabel = code.getCurrentOffset();

    // 6. Test counter < size
    code.iload(counterSlot);
    code.iload(sizeSlot);
    code.if_icmpge(0);  // Placeholder
    int exitPatchPos = code.getCurrentOffset() - 2;

    // 7. Convert counter to string
    code.iload(counterSlot);
    int valueOfRef = cp.addMethodRef(
        "java/lang/String", "valueOf", "(I)Ljava/lang/String;");
    code.invokestatic(valueOfRef);

    // 8. Store in loop variable
    int keySlot = allocateLoopVariable(forInStmt.getLeft());
    code.astore(keySlot);

    // 9. Setup break/continue labels
    int endLabel = code.allocateLabel();
    int updateLabel = code.allocateLabel();
    context.pushBreakLabel(endLabel);
    context.pushContinueLabel(updateLabel);

    // 10. Generate body
    compiler.getStatementProcessor().generate(code, cp, forInStmt.getBody(), null);

    // 11. Pop labels
    context.popContinueLabel();
    context.popBreakLabel();

    // 12. Mark update label
    code.markLabel(updateLabel);

    // 13. Increment counter
    code.iinc(counterSlot, 1);

    // 14. Jump back to test
    code.gotoLabel(testLabel);

    // 15. Mark end label
    code.markLabel(endLabel);

    // 16. Patch exit jump
    code.patchShort(exitPatchPos, endLabel - (exitPatchPos - 1));
}
```

---

## Integration Points

### Statement Generator

Update `StatementProcessor.java` to dispatch ForInStmt:

```java
if (stmt instanceof Swc4jAstForInStmt forInStmt) {
    ForInStatementProcessor.generate(code, cp, forInStmt, returnTypeInfo, context);
}
```

### Type Resolver Integration

Use existing type inference:
```java
String rightType = compiler.getTypeResolver().inferTypeFromExpr(forInStmt.getRight());
```

### Variable Scope Tracking

CompilationContext must track:
- Iterator/counter temporary variables
- Loop variable (declared in left or existing)
- Variable visibility after loop (only if existing variable)
- Shadowing of outer variables

---

## Test Plan

### Phase 1 Tests (Basic Object Iteration)

1. testBasicForInOverObject - 3 keys
2. testForInEmptyObject - no iterations
3. testForInSingleKey - one iteration
4. testForInComplexBody - multiple statements
5. testForInAccessingKey - use key in body
6. testForInLetVsConst - let vs const declaration
7. testForInNestedObject - object with nested structure
8. testForInWithReturn - return in body
9. testForInWithVarDecl - var decl in body
10. testForInOrderPreserved - insertion order maintained

### Phase 2 Tests (Array Iteration)

11. testForInOverArray - 5 elements
12. testForInEmptyArray - no iterations
13. testForInSingleElement - one iteration
14. testForInIndicesAreStrings - verify string type
15. testForInAccessArrayElement - use index to access
16. testForInLargeArray - 1000 elements
17. testForInMixedTypeArray - different element types
18. testForInNestedArrayAccess - arrays in arrays

### Phase 3 Tests (Break and Continue)

19. testForInWithBreak - break in body
20. testForInWithContinue - continue in body
21. testForInBreakInIf - conditional break
22. testForInContinueInIf - conditional continue
23. testForInMultipleBreaks - multiple break points
24. testForInMultipleContinues - multiple continue points
25. testForInBreakAndContinue - both in same loop
26. testForInBreakFirst - break on first iteration
27. testForInContinueAll - continue every iteration

### Phase 4 Tests (Existing Variable)

28. testForInExistingVariable - use pre-declared var
29. testForInVariableAfterLoop - accessible after
30. testForInLastKeyValue - contains last key
31. testForInEmptyObjectExistingVar - empty loop

### Phase 5 Tests (Type Detection)

32. testForInObjectLiteral - inline object
33. testForInArrayLiteral - inline array
34. testForInNull - no iterations
35. testForInTypeInference - infer from expression
36. testForInMixedTypeVariable - runtime check

### Phase 6 Tests (Nested For-In)

37. testForInNestedTwoLevel - nested for-in
38. testForInNestedThreeLevel - 3 levels deep
39. testForInNestedBreakOuter - break outer from inner
40. testForInNestedContinueOuter - continue outer
41. testForInNestedObjectArray - mixed types
42. testForInNestedAccessOuterKey - use outer key

### Phase 7 Tests (Labeled Break/Continue)

43. testForInLabeledBreak - break outer;
44. testForInLabeledContinue - continue outer;
45. testForInMultipleLabels - multiple labeled loops

### Phase 8 Tests (Edge Cases)

46. testForInSideEffectRight - side effect in right expr
47. testForInReturnInBody - return statement
48. testForInThrowInBody - throw statement
49. testForInNestedIf - if statement in body
50. testForInEmptyBody - no body statements
51. testForInVeryLargeObject - 10000 keys
52. testForInVeryLargeArray - 10000 elements
53. testForInMixedWithRegularFor - mixed loop types
54. testForInUnreachableAfterBreak - dead code

---

## Success Criteria

- [x] All 8 phases implemented
- [x] 29 comprehensive test methods covering all edge cases
- [x] Proper stack map frame generation
- [x] Support for both object and array iteration
- [x] Correct type handling: String keys for objects, int indices for arrays
- [x] Support for break and continue statements
- [x] Support for labeled break and continue
- [x] Proper variable scoping
- [x] Type detection for right-hand expression
- [x] Integration with expression generator
- [x] Complete documentation
- [x] All tests passing
- [x] Javadoc builds successfully

**Implementation Notes:**
- Array/String indices are converted to **String** type to match JavaScript for-in semantics
- Object keys are String type
- **Compile-time type checking** using `JavaTypeInfo.isAssignableTo()` replaces runtime instanceof checks
- Type hierarchy is built from `extends` and `implements` clauses during class collection
- VarDeclProcessor now properly initializes variables without initializers (required for JVM verifier)
- StackMapProcessor handles unreachable code after return/throw statements
- AssignExpressionProcessor handles String += int concatenation using String.valueOf()

---

## Known Limitations

1. **For-Of Loops:** This plan covers for-in only, not for-of (value iteration)
2. **Symbol Keys:** JavaScript symbols not applicable in Java
3. **Non-Enumerable Properties:** Java doesn't have enumerable property concept
4. **Prototype Chain:** Not applicable with Java objects
5. **Sparse Arrays:** ArrayList doesn't support sparse arrays like JavaScript
6. **Property Descriptors:** Java doesn't have property descriptor metadata
7. **Concurrent Modification:** No built-in protection for concurrent modification
8. **String Iteration:** Iterating string characters may need special handling
9. **Wide Jumps:** May not handle very large loop bodies initially (>32KB)

---

## References

- **JVM Specification:** Chapter 3 - Control Transfer Instructions
- **JVM Specification:** Chapter 4.10.1 - Stack Map Frame Verification
- **JavaScript Specification:** ECMAScript Section 13.7.5 - The for-in Statement
- **TypeScript Specification:** Section 5.5 - For-In Statements
- **Java Collections:** java.util.Iterator interface
- **Java Collections:** java.util.Map.keySet() method
- **Existing Implementation:** ForStatementProcessor.java (for control flow patterns)
- **Existing Implementation:** IfStatementProcessor.java (for conditional jumps)
- **Test Reference:** TestCompileAstForStmt*.java (for test structure)

---

## Notes

- For-in loops iterate **keys** (not values)
- Array indices are **strings** ("0", "1", "2", ...), not numbers
- Objects use **iterator-based** approach (LinkedHashMap.keySet().iterator())
- Arrays use **index-based** approach (counter from 0 to size-1)
- Keys/indices must be **converted to strings** using String.valueOf()
- **Empty collections** result in zero iterations (test fails immediately)
- **Null/undefined** right expression should skip iteration (no error)
- Break/continue require **label stack** in compilation context
- **Labeled statements** require mapping labels to loop entry/exit points
- Loop variable has **loop scope** (not visible after unless existing variable)
- **Nested loops** require nested label stacks
- Iterator stored in temporary local variable slot
- **Stack must be empty** at loop entry for backward jump verification
- LinkedHashMap maintains **insertion order** for iteration
- ArrayList iteration is **always sequential** (0 to size-1)
- Type detection uses existing **TypeResolver.inferTypeFromExpr()**
- For-in over primitives should either skip or throw exception
- **Runtime type checks** may be needed for unknown types

---

## Implementation Checklist

### Code Generation
- [x] Create `ForInStatementProcessor.java`
- [x] Implement `generate()` method for for-in loops
- [x] Implement object iteration (LinkedHashMap)
- [x] Implement array iteration (ArrayList)
- [x] Implement type detection logic
- [x] Handle break and continue statements
- [x] Implement labeled break and continue
- [x] Generate proper backward jumps
- [x] Implement stack map frame generation
- [x] Add iterator/counter to local variable table

### Integration
- [x] Add ForInStmt case to StatementProcessor dispatch
- [x] Use TypeResolver for right expression type
- [x] Handle VarDecl in left (loop variable)
- [x] Handle existing variable in left
- [x] Track loop variable scopes
- [x] Handle nested for-in loops correctly
- [x] Add debug/line number information

### Testing
- [x] Create test directory `forinstmt/`
- [x] Create `TestCompileAstForInStmtBasic.java`
- [x] Create `TestCompileAstForInStmtArray.java`
- [x] Create `TestCompileAstForInStmtBreakContinue.java`
- [x] Create `TestCompileAstForInStmtExistingVar.java`
- [x] Create `TestCompileAstForInStmtNested.java`
- [x] Create `TestCompileAstForInStmtEdgeCases.java`
- [x] Add all phase tests
- [x] Verify all tests pass
- [x] Verify javadoc builds

---

## Future Work

1. Implement for-of loops (iterate over values)
2. Add optimization for empty for-in loops
3. Add support for very large loop bodies (wide gotos)
4. Implement string character iteration
5. Add runtime type checking for unknown types
6. Optimize iterator allocation
7. Add concurrent modification detection
8. Support for async for-await-of loops
