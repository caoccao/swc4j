# Rest Pattern Implementation Plan

## Overview

This document outlines the implementation plan for supporting rest patterns (`...rest`) in TypeScript to JVM bytecode compilation. Rest patterns collect remaining elements/properties into a new array or object.

**Current Status:** PARTIAL - Function parameter varargs implemented; object and array destructuring rest in for-of loops, variable declarations, assignment expressions, and nested destructuring implemented (2026-01-25)

**Syntax:**
```typescript
// Function parameter rest (varargs)
function sum(...numbers: int[]): int { }

// Array destructuring rest
const [first, second, ...rest] = [1, 2, 3, 4, 5];  // rest = [3, 4, 5]

// Object destructuring rest
const { a, b, ...rest } = { a: 1, b: 2, c: 3, d: 4 };  // rest = { c: 3, d: 4 }
```

**Implementation File:** Multiple files depending on context:
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/clazz/MethodGenerator.java` (function params)
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/VariableAnalyzer.java` (parameter analysis)
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/ForOfStatementGenerator.java` (loop destructuring)
- New: `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/pat/RestPatternGenerator.java`

**Test Files:**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/pat/restpat/TestCompileAstRestPatFunctionParam.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/pat/restpat/TestCompileAstRestPatArrayDestructuring.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/pat/restpat/TestCompileAstRestPatObjectDestructuring.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/pat/restpat/TestCompileAstRestPatVarDecl.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/pat/restpat/TestCompileAstRestPatAssignment.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/pat/restpat/TestCompileAstRestPatNestedDestructuring.java`

**AST Definition:** [Swc4jAstRestPat.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/pat/Swc4jAstRestPat.java)

---

## Rest Pattern Fundamentals

### AST Structure

```java
public class Swc4jAstRestPat extends Swc4jAst
        implements ISwc4jAstPat, ISwc4jAstObjectPatProp, ISwc4jAstTsFnParam {
    protected ISwc4jAstPat arg;           // The pattern to receive rest elements
    protected Swc4jSpan dot3Token;        // The '...' token
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;  // Optional type annotation
}
```

### Interfaces Implemented

1. **ISwc4jAstPat** - Can appear in array patterns as an element
2. **ISwc4jAstObjectPatProp** - Can appear in object patterns as a property
3. **ISwc4jAstTsFnParam** - Can appear as a function parameter

### JavaScript/TypeScript Behavior

```typescript
// 1. Function parameter rest (varargs)
function sum(...numbers: int[]): int {
  let total = 0;
  for (const n of numbers) {
    total += n;
  }
  return total;
}
sum(1, 2, 3, 4, 5);  // numbers = [1, 2, 3, 4, 5]

// 2. Array destructuring rest
const arr = [1, 2, 3, 4, 5];
const [first, second, ...rest] = arr;
// first = 1, second = 2, rest = [3, 4, 5]

// 3. Object destructuring rest
const obj = { a: 1, b: 2, c: 3, d: 4 };
const { a, b, ...rest } = obj;
// a = 1, b = 2, rest = { c: 3, d: 4 }
```

### Java Collection Mapping

In this compiler:
- **Arrays** are represented as `java.util.ArrayList<Object>`
- **Objects** are represented as `java.util.LinkedHashMap<Object, Object>`
- **Rest in arrays** creates a new ArrayList containing remaining elements
- **Rest in objects** creates a new LinkedHashMap containing remaining key-value pairs

---

## Implementation Phases

### Phase 1: Function Parameter Rest (Varargs) - Priority: HIGH

**Status:** Partially implemented

Support rest parameters in function definitions as JVM varargs.

**Current Implementation:**
- ACC_VARARGS flag is set when last parameter is RestPat
- Type extraction from RestPat's type annotation works
- Variable slot allocation for varargs parameter works

**Missing:**
- Proper handling when no type annotation is provided
- Nested patterns in rest arg (e.g., `...args` where arg is not BindingIdent)

**Example Bytecode:**
```
// For: function sum(...numbers: int[]): int
// Method descriptor: ([I)I (varargs)

Method:
  access_flags: ACC_PUBLIC | ACC_VARARGS
  descriptor: ([I)I

// Bytecode accesses numbers as int[] array
aload_1                          // Load int[] array
arraylength                      // Get length
istore_2                         // Store length
iconst_0                         // Initialize index
istore_3
// Loop through array...
```

**Test Coverage:**
1. Basic varargs function with typed parameter `(...args: int[])`
2. Varargs with Object array `(...args: Object[])`
3. Varargs as only parameter
4. Varargs after regular parameters `(a: int, ...rest: int[])`
5. Varargs with no arguments passed
6. Varargs with single argument
7. Varargs with many arguments
8. Calling varargs method from another method
9. Varargs in static method
10. Varargs in instance method

---

### Phase 2: Array Destructuring Rest - Priority: HIGH

**Status:** IMPLEMENTED (2026-01-25) - For-of loops with array destructuring rest patterns

Support rest pattern in array destructuring to collect remaining elements.

**Scope:**
- Rest pattern as last element in array destructuring
- Create new ArrayList with remaining elements
- Works in variable declarations
- Works in for-of loops

**Example:**
```typescript
const [first, second, ...rest] = [1, 2, 3, 4, 5];
// first = 1, second = 2, rest = ArrayList[3, 4, 5]
```

**Java Equivalent:**
```java
ArrayList<Object> arr = ...;  // Source array
Object first = arr.get(0);
Object second = arr.get(1);

// Create rest array with elements from index 2 onwards
ArrayList<Object> rest = new ArrayList<>();
for (int i = 2; i < arr.size(); i++) {
    rest.add(arr.get(i));
}
```

**Bytecode Pattern:**
```
// For: const [first, second, ...rest] = arr;

// First, store source array
aload <arr_slot>
astore <temp_arr_slot>

// Extract first element: arr.get(0)
aload <temp_arr_slot>
iconst_0
invokeinterface java/util/List.get(I)Ljava/lang/Object;
astore <first_slot>

// Extract second element: arr.get(1)
aload <temp_arr_slot>
iconst_1
invokeinterface java/util/List.get(I)Ljava/lang/Object;
astore <second_slot>

// Create rest ArrayList
new java/util/ArrayList
dup
invokespecial java/util/ArrayList.<init>()V
astore <rest_slot>

// Get source array size
aload <temp_arr_slot>
invokeinterface java/util/List.size()I
istore <size_slot>

// Initialize loop counter at rest_start_index (2)
iconst_2
istore <i_slot>

loop_start:
// Check i < size
iload <i_slot>
iload <size_slot>
if_icmpge loop_end

// rest.add(arr.get(i))
aload <rest_slot>
aload <temp_arr_slot>
iload <i_slot>
invokeinterface java/util/List.get(I)Ljava/lang/Object;
invokeinterface java/util/List.add(Ljava/lang/Object;)Z
pop  // Discard boolean return

// i++
iinc <i_slot>, 1
goto loop_start

loop_end:
```

**Test Coverage:**
1. Basic rest after single element `[first, ...rest]`
2. Rest after multiple elements `[a, b, c, ...rest]`
3. Rest with empty remaining elements
4. Rest as only element `[...all]` (copies entire array)
5. Rest with source array having exact elements (empty rest)
6. Rest with source array smaller than pattern (rest is empty)
7. Rest in nested array destructuring
8. Rest in for-of loop variable declaration
9. Rest with typed source array
10. Rest with mixed-type source array

---

### Phase 3: Object Destructuring Rest - Priority: MEDIUM

**Status:** IMPLEMENTED (2026-01-25) - For-of loops with object destructuring rest patterns

Support rest pattern in object destructuring to collect remaining properties.

**Scope:**
- Rest pattern as last property in object destructuring
- Create new LinkedHashMap with remaining key-value pairs
- Exclude explicitly extracted properties from rest
- Works in variable declarations
- Works in for-of loops

**Example:**
```typescript
const { a, b, ...rest } = { a: 1, b: 2, c: 3, d: 4 };
// a = 1, b = 2, rest = LinkedHashMap{ c: 3, d: 4 }
```

**Java Equivalent:**
```java
LinkedHashMap<Object, Object> obj = ...;  // Source object
Object a = obj.get("a");
Object b = obj.get("b");

// Create rest map excluding extracted keys
LinkedHashMap<Object, Object> rest = new LinkedHashMap<>(obj);
rest.remove("a");
rest.remove("b");
// rest now contains { c: 3, d: 4 }
```

**Alternative Approach (iterate and filter):**
```java
LinkedHashMap<Object, Object> rest = new LinkedHashMap<>();
Set<String> extractedKeys = Set.of("a", "b");
for (Map.Entry<Object, Object> entry : obj.entrySet()) {
    if (!extractedKeys.contains(entry.getKey())) {
        rest.put(entry.getKey(), entry.getValue());
    }
}
```

**Bytecode Pattern:**
```
// For: const { a, b, ...rest } = obj;

// Store source object
aload <obj_slot>
astore <temp_obj_slot>

// Extract a: obj.get("a")
aload <temp_obj_slot>
ldc "a"
invokeinterface java/util/Map.get(Ljava/lang/Object;)Ljava/lang/Object;
astore <a_slot>

// Extract b: obj.get("b")
aload <temp_obj_slot>
ldc "b"
invokeinterface java/util/Map.get(Ljava/lang/Object;)Ljava/lang/Object;
astore <b_slot>

// Create rest LinkedHashMap as copy
new java/util/LinkedHashMap
dup
aload <temp_obj_slot>
invokespecial java/util/LinkedHashMap.<init>(Ljava/util/Map;)V
astore <rest_slot>

// Remove extracted keys
aload <rest_slot>
ldc "a"
invokeinterface java/util/Map.remove(Ljava/lang/Object;)Ljava/lang/Object;
pop  // Discard removed value

aload <rest_slot>
ldc "b"
invokeinterface java/util/Map.remove(Ljava/lang/Object;)Ljava/lang/Object;
pop
```

**Test Coverage:**
1. Basic rest after single property `{ a, ...rest }`
2. Rest after multiple properties `{ a, b, c, ...rest }`
3. Rest with empty remaining properties
4. Rest as only property `{ ...all }` (copies entire object)
5. Rest with source object having exact properties (empty rest)
6. Rest with renamed properties `{ a: x, ...rest }`
7. Rest with default values `{ a = 1, ...rest }`
8. Rest in nested object destructuring
9. Rest in for-of loop variable declaration
10. Rest with mixed property types

---

### Phase 4: Variable Declaration with Rest - Priority: MEDIUM

**Status:** IMPLEMENTED (2026-01-25) - Array and object destructuring rest in variable declarations

Support rest patterns in standalone variable declarations.

**Scope:**
- `const [a, ...rest] = expr;`
- `let { x, ...rest } = expr;`
- Type inference for rest variable
- Proper scoping of rest variable

**Example:**
```typescript
const arr = [1, 2, 3, 4, 5];
const [head, ...tail] = arr;
console.log(head);  // 1
console.log(tail);  // [2, 3, 4, 5]

const obj = { x: 1, y: 2, z: 3 };
const { x, ...others } = obj;
console.log(x);       // 1
console.log(others);  // { y: 2, z: 3 }
```

**Test Coverage:**
1. Const with array rest
2. Let with array rest
3. Const with object rest
4. Let with object rest
5. Rest variable accessible after declaration
6. Rest variable type is ArrayList/LinkedHashMap
7. Multiple declarations with rest
8. Nested patterns with rest

---

### Phase 5: For-Of Loop with Rest - Priority: MEDIUM

**Status:** IMPLEMENTED (2026-01-25) - Both array and object destructuring rest in for-of loops implemented

Support rest patterns in for-of loop variable declarations.

**Scope:**
- `for (const [first, ...rest] of arrays) { }`
- `for (const { a, ...rest } of objects) { }`
- Rest pattern evaluated each iteration
- New rest collection created each iteration

**Example:**
```typescript
const arrays = [[1, 2, 3], [4, 5, 6], [7, 8, 9]];
for (const [first, ...rest] of arrays) {
  console.log(first, rest);
  // 1, [2, 3]
  // 4, [5, 6]
  // 7, [8, 9]
}

const objects = [{ a: 1, b: 2, c: 3 }, { a: 4, b: 5, c: 6 }];
for (const { a, ...rest } of objects) {
  console.log(a, rest);
  // 1, { b: 2, c: 3 }
  // 4, { b: 5, c: 6 }
}
```

**Test Coverage:**
1. For-of with array rest pattern
2. For-of with object rest pattern
3. For-of with nested rest
4. For-of rest with break/continue
5. For-of rest with return in loop
6. For-of with labeled break to outer

---

### Phase 6: Nested Rest Patterns - Priority: LOW

**Status:** IMPLEMENTED (2026-01-25) - Nested array and object destructuring rest in variable declarations (17 tests)

Support rest patterns in nested destructuring contexts.

**Scope:**
- `const [a, [b, ...inner], ...outer] = nested;`
- `const { x, nested: { y, ...innerRest }, ...outerRest } = obj;`
- Multiple levels of rest
- Combinations of array and object rest

**Example:**
```typescript
// Nested array rest
const nested = [1, [2, 3, 4], 5, 6];
const [a, [b, ...inner], ...outer] = nested;
// a = 1, b = 2, inner = [3, 4], outer = [5, 6]

// Nested object rest
const obj = {
  x: 1,
  nested: { y: 2, z: 3, w: 4 },
  a: 5,
  b: 6
};
const { x, nested: { y, ...innerRest }, ...outerRest } = obj;
// x = 1, y = 2, innerRest = { z: 3, w: 4 }, outerRest = { a: 5, b: 6 }
```

**Test Coverage:**
1. Two-level nested array rest
2. Two-level nested object rest
3. Mixed array/object nested rest
4. Three-level nesting
5. Rest at multiple levels
6. Empty rest at nested level

---

### Phase 7: Assignment Pattern Rest - Priority: LOW

**Status:** IMPLEMENTED (2026-01-25) - Array and object destructuring rest in assignment expressions

Support rest patterns in assignment expressions (not just declarations).

**Scope:**
- `[a, ...rest] = newArray;`
- `{ x, ...rest } = newObject;`
- Reassignment of existing rest variable
- Works with existing variables

**Example:**
```typescript
let a, b, rest;
[a, b, ...rest] = [1, 2, 3, 4, 5];
// a = 1, b = 2, rest = [3, 4, 5]

({ a, ...rest } = { a: 10, b: 20, c: 30 });
// a = 10, rest = { b: 20, c: 30 }
```

**Test Coverage:**
1. Array rest assignment to existing variable
2. Object rest assignment to existing variable
3. Multiple sequential rest assignments
4. Rest assignment in expression context

---

## Edge Cases and Special Scenarios

### Function Parameter Rest Edge Cases

1. **Empty Varargs Call**
   ```typescript
   function f(...args: int[]) { }
   f();  // args = empty int[]
   ```

2. **Single Argument Varargs**
   ```typescript
   f(1);  // args = [1]
   ```

3. **Varargs After Regular Parameters**
   ```typescript
   function f(a: int, b: int, ...rest: int[]) { }
   f(1, 2);  // a=1, b=2, rest=[]
   f(1, 2, 3, 4);  // a=1, b=2, rest=[3, 4]
   ```

4. **Varargs Only Parameter**
   ```typescript
   function f(...args: string[]) { }
   f("a", "b", "c");
   ```

5. **Varargs with No Type Annotation**
   ```typescript
   function f(...args) { }  // Default to Object[]
   ```

6. **Varargs with Array Type Annotation**
   ```typescript
   function f(...args: int[]): int { }  // int varargs
   function f(...args: Object[]): void { }  // Object varargs
   ```

7. **Varargs Accessing Length**
   ```typescript
   function f(...args: int[]): int {
     return args.length;
   }
   ```

8. **Varargs Iteration**
   ```typescript
   function f(...args: int[]): int {
     let sum = 0;
     for (const arg of args) { sum += arg; }
     return sum;
   }
   ```

9. **Varargs Passed to Another Varargs Function**
   ```typescript
   function inner(...args: int[]): int { return args.length; }
   function outer(...args: int[]): int { return inner(...args); }
   ```

10. **Varargs in Static vs Instance Method**
    ```typescript
    static sum(...nums: int[]): int { }
    total(...nums: int[]): int { }
    ```

### Array Destructuring Rest Edge Cases

11. **Rest as Only Element (Copy)**
    ```typescript
    const [...copy] = [1, 2, 3];  // copy = [1, 2, 3]
    ```

12. **Rest with Empty Remaining**
    ```typescript
    const [a, b, ...rest] = [1, 2];  // rest = []
    ```

13. **Rest with Source Smaller Than Pattern**
    ```typescript
    const [a, b, c, ...rest] = [1, 2];
    // a=1, b=2, c=undefined, rest=[]
    ```

14. **Rest with Single Element Before**
    ```typescript
    const [first, ...rest] = [1, 2, 3, 4, 5];
    // first=1, rest=[2,3,4,5]
    ```

15. **Rest with Many Elements Before**
    ```typescript
    const [a, b, c, d, e, ...rest] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
    // rest=[6,7,8,9,10]
    ```

16. **Rest from Empty Array**
    ```typescript
    const [...rest] = [];  // rest = []
    ```

17. **Rest with Holes (Sparse Array)**
    ```typescript
    const [a, , b, ...rest] = [1, 2, 3, 4, 5];
    // a=1, b=3, rest=[4,5] (skipped index 1)
    ```

18. **Rest in Nested Array**
    ```typescript
    const [[a, ...inner], ...outer] = [[1, 2, 3], [4, 5], [6]];
    // a=1, inner=[2,3], outer=[[4,5],[6]]
    ```

19. **Rest with Default Values (Not Applicable)**
    ```typescript
    // Note: Rest cannot have default value in JavaScript
    // const [...rest = []] = [];  // Syntax error
    ```

20. **Rest Index Calculation**
    ```typescript
    const [a, b, ...rest] = arr;
    // rest starts at index 2 (number of elements before rest)
    ```

### Object Destructuring Rest Edge Cases

21. **Rest as Only Property (Copy)**
    ```typescript
    const { ...copy } = { a: 1, b: 2 };  // copy = { a: 1, b: 2 }
    ```

22. **Rest with Empty Remaining**
    ```typescript
    const { a, b, ...rest } = { a: 1, b: 2 };  // rest = {}
    ```

23. **Rest with Renamed Property**
    ```typescript
    const { a: x, ...rest } = { a: 1, b: 2, c: 3 };
    // x=1, rest={ b: 2, c: 3 }
    ```

24. **Rest with Default Value Property**
    ```typescript
    const { a = 10, ...rest } = { b: 2, c: 3 };
    // a=10 (default), rest={ b: 2, c: 3 }
    ```

25. **Rest Preserves Property Order**
    ```typescript
    const { c, ...rest } = { a: 1, b: 2, c: 3, d: 4 };
    // rest = { a: 1, b: 2, d: 4 } (insertion order maintained)
    ```

26. **Rest from Empty Object**
    ```typescript
    const { ...rest } = {};  // rest = {}
    ```

27. **Rest with Missing Property**
    ```typescript
    const { a, ...rest } = { b: 2, c: 3 };
    // a=undefined, rest={ b: 2, c: 3 }
    ```

28. **Rest with Nested Object**
    ```typescript
    const { nested: { x, ...innerRest }, ...outerRest } = {
      nested: { x: 1, y: 2 },
      a: 3
    };
    // x=1, innerRest={ y: 2 }, outerRest={ a: 3 }
    ```

29. **Rest with Numeric Keys**
    ```typescript
    const { 0: first, ...rest } = { 0: 'a', 1: 'b', 2: 'c' };
    // first='a', rest={ 1: 'b', 2: 'c' }
    ```

30. **Rest with Symbol Keys (Not Applicable in Java)**
    ```typescript
    // Symbols not supported in Java compilation
    ```

### For-Of Loop Rest Edge Cases

31. **For-Of Array Rest Each Iteration**
    ```typescript
    for (const [first, ...rest] of [[1,2,3], [4,5,6]]) {
      // Iteration 1: first=1, rest=[2,3]
      // Iteration 2: first=4, rest=[5,6]
    }
    ```

32. **For-Of Object Rest Each Iteration**
    ```typescript
    for (const { a, ...rest } of [{ a:1, b:2 }, { a:3, c:4 }]) {
      // Iteration 1: a=1, rest={ b:2 }
      // Iteration 2: a=3, rest={ c:4 }
    }
    ```

33. **For-Of Rest with Break**
    ```typescript
    for (const [first, ...rest] of arrays) {
      if (first === 'stop') break;
    }
    ```

34. **For-Of Rest with Continue**
    ```typescript
    for (const { key, ...rest } of objects) {
      if (key === 'skip') continue;
      process(rest);
    }
    ```

35. **For-Of Rest with Return**
    ```typescript
    function find(items) {
      for (const [first, ...rest] of items) {
        if (first === target) return rest;
      }
      return [];
    }
    ```

36. **For-Of Nested Rest**
    ```typescript
    for (const [a, [b, ...inner], ...outer] of nestedArrays) {
      // Each iteration creates new inner and outer arrays
    }
    ```

### Variable Declaration Rest Edge Cases

37. **Const vs Let Rest**
    ```typescript
    const [...rest1] = [1, 2, 3];  // rest1 cannot be reassigned
    let [...rest2] = [1, 2, 3];    // rest2 can be reassigned
    ```

38. **Rest Variable Type Inference**
    ```typescript
    const [a, ...rest] = [1, 2, 3];
    // rest type is ArrayList<Object>
    ```

39. **Rest with Type Annotation**
    ```typescript
    const [a, ...rest]: [int, ...int[]] = [1, 2, 3];
    // Explicit type annotation (may not be supported initially)
    ```

40. **Multiple Rest in Same Statement (Invalid)**
    ```typescript
    // const [a, ...b, ...c] = arr;  // Syntax error: multiple rest
    ```

### Nested Pattern Rest Edge Cases

41. **Two-Level Array Nesting**
    ```typescript
    const [[a, ...inner], ...outer] = [[1, 2, 3], [4, 5], [6]];
    // a=1, inner=[2,3], outer=[[4,5],[6]]
    ```

42. **Two-Level Object Nesting**
    ```typescript
    const { x: { y, ...inner }, ...outer } = {
      x: { y: 1, z: 2 },
      a: 3
    };
    // y=1, inner={ z: 2 }, outer={ a: 3 }
    ```

43. **Mixed Array/Object Nesting**
    ```typescript
    const [{ a, ...objRest }, ...arrRest] = [
      { a: 1, b: 2 },
      { a: 3 }
    ];
    // a=1, objRest={ b: 2 }, arrRest=[{ a: 3 }]
    ```

44. **Three-Level Nesting**
    ```typescript
    const [[[a, ...l3], ...l2], ...l1] = [[[1, 2, 3], [4, 5]], [[6]]];
    ```

45. **Rest at Inner Level Only**
    ```typescript
    const [a, [b, ...inner]] = [1, [2, 3, 4]];
    // No outer rest, only inner rest
    ```

### Assignment Pattern Rest Edge Cases

46. **Assignment to Existing Variables**
    ```typescript
    let a, rest;
    [a, ...rest] = [1, 2, 3];
    ```

47. **Sequential Assignments**
    ```typescript
    let rest;
    [a, ...rest] = [1, 2, 3];
    [b, ...rest] = [4, 5, 6];  // rest reassigned
    ```

48. **Assignment in Expression Context**
    ```typescript
    let a, rest;
    console.log(([a, ...rest] = [1, 2, 3]));  // Returns the array
    ```

49. **Rest Assignment with Parentheses (Object)**
    ```typescript
    let a, rest;
    ({ a, ...rest } = { a: 1, b: 2 });  // Parentheses required
    ```

50. **Chained Assignments with Rest**
    ```typescript
    let a1, a2, rest1, rest2;
    [a1, ...rest1] = [a2, ...rest2] = [1, 2, 3];
    ```

### Type System Edge Cases

51. **Rest Parameter Type Inference**
    ```typescript
    function f(...args) { }  // args: any[] or Object[]
    ```

52. **Rest in Generic Context**
    ```typescript
    function f<T>(...args: T[]): T[] { return args; }
    ```

53. **Rest with Union Types (Not Supported)**
    ```typescript
    // function f(...args: (int | string)[]): void { }
    ```

54. **Rest Type Narrowing**
    ```typescript
    const [first, ...rest] = [1, 2, 3] as const;
    // first: 1, rest: readonly [2, 3]
    ```

55. **Rest with Tuple Types (Partial Support)**
    ```typescript
    const [a, b, ...rest]: [int, int, ...int[]] = [1, 2, 3, 4];
    ```

### Bytecode/JVM Edge Cases

56. **Rest Creating Large Arrays**
    ```typescript
    const [first, ...rest] = Array(10000).fill(0);
    // rest has 9999 elements
    ```

57. **Rest with Primitive Arrays**
    ```typescript
    function sum(...nums: int[]): int {
      // nums is int[] in JVM (primitive array)
    }
    ```

58. **Rest with Object Arrays**
    ```typescript
    function collect(...items: Object[]): ArrayList {
      // items is Object[] in JVM
    }
    ```

59. **Rest Spread Back to Varargs**
    ```typescript
    function inner(...args: int[]): int { return args.length; }
    function outer(...args: int[]): int {
      return inner(...args);  // Spread rest back to varargs
    }
    ```

60. **Rest in Static Initializer**
    ```typescript
    class A {
      static values = (() => {
        const [first, ...rest] = [1, 2, 3];
        return rest;
      })();
    }
    ```

### Error Handling Edge Cases

61. **Rest Must Be Last (Syntax)**
    ```typescript
    // const [a, ...rest, b] = arr;  // Syntax error
    ```

62. **Rest Cannot Have Default (Syntax)**
    ```typescript
    // const [...rest = []] = arr;  // Syntax error
    ```

63. **Only One Rest Allowed (Syntax)**
    ```typescript
    // const [...a, ...b] = arr;  // Syntax error
    ```

64. **Rest on Non-Iterable (Runtime)**
    ```typescript
    // const [...rest] = 42;  // Runtime error
    ```

65. **Rest on Null/Undefined (Runtime)**
    ```typescript
    // const [...rest] = null;  // Runtime error
    ```

### Performance Edge Cases

66. **Rest Creating Many Small Arrays**
    ```typescript
    for (const [first, ...rest] of manyArrays) {
      // Creates new ArrayList each iteration
    }
    ```

67. **Rest with Large Source Collection**
    ```typescript
    const [first, ...rest] = hugeArray;  // rest copies most elements
    ```

68. **Rest in Hot Loop**
    ```typescript
    for (let i = 0; i < 1000000; i++) {
      const [a, ...rest] = [1, 2, 3];
    }
    ```

69. **Rest with Deep Copy**
    ```typescript
    const { ...copy } = original;  // Shallow copy only
    ```

70. **Rest Memory Allocation**
    ```typescript
    // Each rest creates new collection, may need GC consideration
    ```

### Integration Edge Cases

71. **Rest with Map Destructuring**
    ```typescript
    for (const [key, value] of map) {
      // [key, value] is array destructuring, not rest
    }
    ```

72. **Rest in Function with Map Iteration**
    ```typescript
    function process(items: ArrayList) {
      for (const { id, ...data } of items) {
        store(id, data);
      }
    }
    ```

73. **Rest Combined with Optional Chaining (Not Supported)**
    ```typescript
    // const { a, ...rest } = obj?.nested;
    ```

74. **Rest with Spread Operator**
    ```typescript
    const arr = [1, 2, 3];
    const [first, ...rest] = [...arr, 4, 5];  // Spread then rest
    ```

75. **Rest in Catch Clause (Not Applicable)**
    ```typescript
    // try { } catch ({ message, ...rest }) { }
    ```

### Scope Edge Cases

76. **Rest Variable Shadowing**
    ```typescript
    const rest = 'outer';
    {
      const [a, ...rest] = [1, 2, 3];  // Shadows outer rest
    }
    console.log(rest);  // 'outer'
    ```

77. **Rest Accessible After Block**
    ```typescript
    let rest;
    {
      [a, ...rest] = [1, 2, 3];
    }
    console.log(rest);  // [2, 3] (if let at outer scope)
    ```

78. **Rest in Arrow Function**
    ```typescript
    const process = ([first, ...rest]) => rest.length;
    ```

79. **Rest in Callback**
    ```typescript
    items.map(({ id, ...data }) => ({ id, data }));
    ```

80. **Rest Variable Name Collision**
    ```typescript
    const [rest, ...rest2] = [1, 2, 3];  // rest2, not rest
    ```

---

## Bytecode Instruction Reference

### Creating ArrayList for Array Rest

```
// Create new ArrayList
new java/util/ArrayList
dup
invokespecial java/util/ArrayList.<init>()V
astore <rest_slot>

// Add elements in loop
aload <rest_slot>
aload <source>
iload <index>
invokeinterface java/util/List.get(I)Ljava/lang/Object;
invokeinterface java/util/List.add(Ljava/lang/Object;)Z
pop  // Discard boolean
```

### Creating LinkedHashMap for Object Rest

```
// Create copy of source map
new java/util/LinkedHashMap
dup
aload <source_obj>
invokespecial java/util/LinkedHashMap.<init>(Ljava/util/Map;)V
astore <rest_slot>

// Remove extracted keys
aload <rest_slot>
ldc "key_name"
invokeinterface java/util/Map.remove(Ljava/lang/Object;)Ljava/lang/Object;
pop  // Discard removed value
```

### Varargs Method Declaration

```
// Method with varargs:
// access_flags: ACC_PUBLIC | ACC_VARARGS (0x0081)
// descriptor: ([Ljava/lang/Object;)V for Object varargs
// descriptor: ([I)V for int varargs

// Accessing varargs parameter:
aload_1           // Load array parameter
arraylength       // Get length
istore_2          // Store length

aload_1           // Load array parameter
iconst_0          // Index
aaload/iaload     // Load element (reference or primitive)
```

---

## Implementation Strategy

### Rest Pattern Type Detection

```java
public void processRestPattern(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        Swc4jAstRestPat restPat,
        int sourceSlot,
        String sourceType,
        int restStartIndex,          // For arrays: index of first rest element
        Set<String> extractedKeys)   // For objects: keys already extracted
        throws Swc4jByteCodeCompilerException {

    ISwc4jAstPat arg = restPat.getArg();
    String varName = getPatternVariableName(arg);

    if (isArrayType(sourceType)) {
        generateArrayRestExtraction(code, cp, varName, sourceSlot, restStartIndex);
    } else if (isMapType(sourceType)) {
        generateObjectRestExtraction(code, cp, varName, sourceSlot, extractedKeys);
    } else {
        throw new Swc4jByteCodeCompilerException(restPat,
            "Rest pattern requires array or object source type");
    }
}
```

### Array Rest Code Generation

```java
private void generateArrayRestExtraction(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        String varName,
        int sourceSlot,
        int restStartIndex) {

    CompilationContext context = compiler.getMemory().getCompilationContext();

    // Create new ArrayList
    int arrayListClass = cp.addClass("java/util/ArrayList");
    int arrayListInit = cp.addMethodRef("java/util/ArrayList", "<init>", "()V");
    code.newObject(arrayListClass);
    code.dup();
    code.invokespecial(arrayListInit);
    int restSlot = context.getLocalVariableTable().allocateVariable(varName, "Ljava/util/ArrayList;");
    code.astore(restSlot);

    // Get source size
    code.aload(sourceSlot);
    int sizeRef = cp.addInterfaceMethodRef("java/util/List", "size", "()I");
    code.invokeinterface(sizeRef, 1);
    int sizeSlot = context.getLocalVariableTable().allocateVariable("$restSize", "I");
    code.istore(sizeSlot);

    // Initialize loop counter at restStartIndex
    code.iconst(restStartIndex);
    int iSlot = context.getLocalVariableTable().allocateVariable("$restI", "I");
    code.istore(iSlot);

    // Loop to copy remaining elements
    int loopStart = code.getCurrentOffset();
    code.iload(iSlot);
    code.iload(sizeSlot);
    code.if_icmpge(0);  // Placeholder
    int loopExitPatch = code.getCurrentOffset() - 2;

    // rest.add(source.get(i))
    code.aload(restSlot);
    code.aload(sourceSlot);
    code.iload(iSlot);
    int getRef = cp.addInterfaceMethodRef("java/util/List", "get", "(I)Ljava/lang/Object;");
    code.invokeinterface(getRef, 2);
    int addRef = cp.addInterfaceMethodRef("java/util/List", "add", "(Ljava/lang/Object;)Z");
    code.invokeinterface(addRef, 2);
    code.pop();  // Discard boolean

    // i++
    code.iinc(iSlot, 1);

    // goto loop start
    // ... (backward jump calculation)

    // Patch loop exit
    // ... (forward jump calculation)
}
```

---

## Success Criteria

- [ ] Phase 1: Function parameter rest (varargs) fully working
- [x] Phase 2: Array destructuring rest implemented (for-of loops, 18 tests)
- [x] Phase 3: Object destructuring rest implemented (for-of loops, 19 tests)
- [x] Phase 4: Variable declaration with rest working (20 tests)
- [x] Phase 5: For-of loop with rest working (array and object destructuring)
- [x] Phase 6: Nested rest patterns working (17 tests)
- [x] Phase 7: Assignment pattern rest working (17 tests)
- [x] Comprehensive test coverage for all edge cases (89 tests total)
- [x] Proper stack map frame generation
- [x] Correct type inference for rest variables (ArrayList for arrays, LinkedHashMap for objects)
- [x] All tests passing
- [x] Javadoc builds successfully

---

## Known Limitations

1. **Spread Operator:** This plan covers rest patterns only, not spread operator (`[...arr]` in array literals)
2. **Generic Types:** Rest with generic type parameters may have limited support
3. **Tuple Types:** Full TypeScript tuple type support not implemented
4. **Optional Chaining:** Rest with optional chaining not supported
5. **Rest in Catch:** Rest patterns in catch clause destructuring not applicable
6. **Symbol Properties:** JavaScript symbols not applicable in Java
7. **Primitive Default Values:** Default values with primitives require boxing
8. **Deep Copy:** Object rest creates shallow copy only
9. **Performance:** Each rest creates new collection, may impact performance in hot loops
10. **Multiple Rest:** Only single rest pattern per destructuring (enforced by parser)

---

## References

- **JVM Specification:** Chapter 4.3.3 - Method Descriptors (varargs)
- **JVM Specification:** ACC_VARARGS access flag (0x0080)
- **JavaScript Specification:** ECMAScript Section 13.3.3 - Destructuring Binding Patterns
- **TypeScript Specification:** Section 4.17 - Destructuring
- **Java Collections:** java.util.ArrayList for array rest
- **Java Collections:** java.util.LinkedHashMap for object rest
- **Existing Implementation:** MethodGenerator.java (varargs handling)
- **Existing Implementation:** ForOfStatementGenerator.java (destructuring patterns)

---

## Notes

- Rest pattern must be **last element** in array or object destructuring
- Rest collects **remaining** elements/properties not explicitly extracted
- Rest creates **new collection** (ArrayList or LinkedHashMap)
- For arrays, rest starts at index = number of elements before rest
- For objects, rest excludes all explicitly extracted keys (including renamed)
- Varargs in JVM uses ACC_VARARGS flag and array parameter type
- Rest variable type is always collection type (ArrayList/LinkedHashMap)
- Each iteration in for-of creates new rest collection
- Object rest preserves **insertion order** (LinkedHashMap)
- Array rest maintains **sequential order** from source index

---

## Implementation Checklist

### Code Generation
- [ ] Create `RestPatternGenerator.java`
- [x] Implement array rest extraction (in ForOfStatementGenerator)
- [x] Implement object rest extraction (in ForOfStatementGenerator)
- [ ] Handle varargs type resolution
- [x] Generate proper backward jumps for rest loops
- [x] Implement stack map frame generation for rest loops

### Integration
- [ ] Update VariableAnalyzer for rest pattern variables
- [x] Update ForOfStatementGenerator for rest in loop variables (array and object destructuring)
- [x] Update VarDeclGenerator for rest in declarations (array and object destructuring)
- [ ] Handle nested rest patterns
- [x] Track extracted keys for object rest
- [x] Track rest start index for array rest

### Testing
- [x] Create test directory `restpat/`
- [ ] Create `TestCompileAstRestPatFunctionParam.java`
- [x] Create `TestCompileAstRestPatArrayDestructuring.java` (18 tests)
- [x] Create `TestCompileAstRestPatObjectDestructuring.java` (19 tests)
- [x] Create `TestCompileAstRestPatVarDecl.java` (20 tests)
- [x] Verify all tests pass
- [x] Verify javadoc builds

---

## Future Work

1. Implement spread operator for array literals
2. Add optimization for small rest arrays (inline creation)
3. Support rest in function return destructuring
4. Add generic type inference for rest
5. Optimize repeated rest in loops (pool allocation)
6. Support rest with typed arrays (int[], String[], etc.)
7. Add runtime validation for rest source types
