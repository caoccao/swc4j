# Switch Statement Implementation Plan

## Overview

This document outlines the implementation plan for supporting switch statements in TypeScript to JVM bytecode compilation. Switch statements provide multi-way branching based on the value of an expression, with support for fall-through semantics and default cases.

**Current Status:** 🔴 **NOT STARTED**

**Dependencies:** ✅ TypeScript enum support (TsEnumDecl) - COMPLETED (2026-01-19)

**Supported Discriminant Types (in priority order):**
1. **enum** (highest priority) - Uses ordinal() values
2. **int** - Direct tableswitch/lookupswitch
3. **String** - Hash-based two-stage switch
4. **byte, short, char** - Promoted to int
5. **Byte, Short, Character, Integer** - Unboxed then promoted to int

**Unsupported types:** long, float, double, boolean, Object, or any other reference types

**Syntax:**
```typescript
switch (expression) {
  case value1:
    statements
    break
  case value2:
  case value3:
    statements
    break
  default:
    statements
}
```

**Implementation File:** `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/SwitchStatementGenerator.java` (to be created)

**Test Files:** `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/switchstmt/TestCompileAstSwitchStmt*.java` (to be created)

**AST Definition:** [Swc4jAstSwitchStmt.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/stmt/Swc4jAstSwitchStmt.java)

---

## Switch Statement Fundamentals

### Statement Semantics

A switch statement has three main components:
1. **Discriminant** - Expression evaluated once and compared against case values
2. **Case Clauses** - Each with a test value and optional statements
3. **Default Clause** - Optional fallback when no case matches

**Critical characteristics:**
- Discriminant is evaluated exactly once
- Cases are tested in order (for non-constant expressions)
- Fall-through behavior: execution continues to next case unless break/return encountered
- Default clause can appear anywhere (first, middle, or last)
- Empty cases are valid (useful for fall-through patterns)

### JavaScript/TypeScript Behavior

```typescript
// Standard switch with breaks
let x = 2;
switch (x) {
  case 1:
    console.log("one");
    break;
  case 2:
    console.log("two");  // Executes this
    break;
  default:
    console.log("other");
}

// Fall-through behavior
switch (x) {
  case 1:
  case 2:
  case 3:
    console.log("1, 2, or 3");  // Multiple cases share body
    break;
}

// Fall-through without break
switch (x) {
  case 2:
    console.log("two");     // Executes this
  case 3:
    console.log("three");   // And this (fall-through)
    break;
}
```

### JVM Bytecode Representation

Java bytecode provides two switch instructions:

#### 1. tableswitch (Dense Cases)
Used when case values are contiguous or nearly contiguous:
```
// switch (x) { case 0: ...; case 1: ...; case 2: ...; }
iload_1                    // Load discriminant (x)
tableswitch {              // Jump table for O(1) lookup
  min: 0
  max: 2
  default: DEFAULT_LABEL
  0: CASE_0_LABEL
  1: CASE_1_LABEL
  2: CASE_2_LABEL
}
CASE_0_LABEL:
  [case 0 body]
  goto END_LABEL
CASE_1_LABEL:
  [case 1 body]
  goto END_LABEL
CASE_2_LABEL:
  [case 2 body]
  goto END_LABEL
DEFAULT_LABEL:
  [default body]
END_LABEL:
  [continuation]
```

**tableswitch characteristics:**
- Requires contiguous integer range [min, max]
- O(1) lookup via direct array indexing
- Memory overhead: (max - min + 1) * 4 bytes for jump table
- Efficient when case density > ~50% (adjustable threshold)

#### 2. lookupswitch (Sparse Cases)
Used when case values are sparse or non-contiguous:
```
// switch (x) { case 1: ...; case 10: ...; case 100: ...; }
iload_1                    // Load discriminant (x)
lookupswitch {             // Binary search for O(log n) lookup
  default: DEFAULT_LABEL
  1: CASE_1_LABEL
  10: CASE_10_LABEL
  100: CASE_100_LABEL
}
CASE_1_LABEL:
  [case 1 body]
  goto END_LABEL
CASE_10_LABEL:
  [case 10 body]
  goto END_LABEL
CASE_100_LABEL:
  [case 100 body]
  goto END_LABEL
DEFAULT_LABEL:
  [default body]
END_LABEL:
```

**lookupswitch characteristics:**
- Cases must be sorted in ascending order
- O(log n) lookup via binary search
- Memory overhead: n * 8 bytes (key-value pairs)
- Used when cases are sparse or range is too large

#### Selection Heuristic
Choose tableswitch when:
- All cases are constant integers
- `density = count / (max - min + 1) >= 0.5`
- `range = max - min + 1` is reasonable (< 10000)

Otherwise use lookupswitch.

### String Switches (Java 7+)

String switches compile to a two-stage process:
```
// switch (str) { case "foo": ...; case "bar": ...; }

// Stage 1: Hash code switch
iload_1                    // Load string reference
invokevirtual String.hashCode()
lookupswitch {
  hash("foo"): HASH_FOO
  hash("bar"): HASH_BAR
  default: DEFAULT
}

// Stage 2: Equality checks (handle hash collisions)
HASH_FOO:
  aload_1                  // Load string again
  ldc "foo"
  invokevirtual String.equals()
  ifne CASE_FOO_LABEL
  goto DEFAULT             // Hash collision, not actually "foo"

CASE_FOO_LABEL:
  [case "foo" body]
  goto END

HASH_BAR:
  aload_1
  ldc "bar"
  invokevirtual String.equals()
  ifne CASE_BAR_LABEL
  goto DEFAULT

CASE_BAR_LABEL:
  [case "bar" body]
  goto END

DEFAULT:
  [default body]

END:
```

---

## Bytecode Generation Strategy

### Overall Approach

1. **Analyze discriminant type:**
   - **enum** → Use ordinal() to get int, then tableswitch or lookupswitch
   - **int/byte/short/char** → Use tableswitch or lookupswitch directly
   - **Integer/Byte/Short/Character** → Unbox to primitive, then tableswitch or lookupswitch
   - **String** → Use hash-based two-stage approach
   - **Unsupported types** → Compilation error

2. **Collect case information:**
   - Extract all case values (must be compile-time constants)
   - Detect duplicate cases (error)
   - Find default clause (if present)
   - Determine case order and fall-through patterns

3. **Choose instruction:**
   - For integers: Calculate density and select tableswitch vs lookupswitch
   - For strings: Always use hash-based lookupswitch

4. **Generate bytecode:**
   - Evaluate discriminant once, store if needed
   - Generate switch instruction with case labels
   - Generate case bodies in source order
   - Handle fall-through (no goto between consecutive cases)
   - Generate break statements as goto END_LABEL
   - Track break label for case bodies

5. **Scope management:**
   - Switch body is a single scope
   - Variables declared in cases are visible to subsequent cases
   - Handle shadowing correctly

### Fall-Through Semantics

Fall-through is when execution continues from one case to the next without a break:

```typescript
switch (x) {
  case 1:
    a();      // If x==1, executes a(), then falls through
  case 2:
    b();      // Executes for both x==1 and x==2
    break;
  case 3:
    c();
    break;
}
```

**Bytecode pattern:**
```
lookupswitch {
  1: CASE_1_LABEL
  2: CASE_2_LABEL
  3: CASE_3_LABEL
  default: END_LABEL
}
CASE_1_LABEL:
  [invoke a()]
  // NO goto - falls through to CASE_2_LABEL
CASE_2_LABEL:
  [invoke b()]
  goto END_LABEL      // Break
CASE_3_LABEL:
  [invoke c()]
  goto END_LABEL      // Break
END_LABEL:
```

**Key insight:** Only insert `goto END_LABEL` when case ends with break. Otherwise, let execution fall through to next label.

### Empty Cases (Case Groups)

Multiple cases can share the same body:
```typescript
switch (x) {
  case 1:
  case 2:
  case 3:
    doSomething();
    break;
}
```

**Bytecode pattern:**
```
lookupswitch {
  1: CASE_1_LABEL
  2: CASE_1_LABEL    // Same label for all three
  3: CASE_1_LABEL
  default: END_LABEL
}
CASE_1_LABEL:
  [invoke doSomething()]
  goto END_LABEL
END_LABEL:
```

**Optimization:** Empty cases point to the same label as the next non-empty case.

### Default Clause Handling

The default clause can appear anywhere in source code:
```typescript
// Default at end (most common)
switch (x) {
  case 1: a(); break;
  case 2: b(); break;
  default: c(); break;
}

// Default at beginning
switch (x) {
  default: c(); break;
  case 1: a(); break;
  case 2: b(); break;
}

// Default in middle
switch (x) {
  case 1: a(); break;
  default: c(); break;
  case 2: b(); break;
}
```

**Bytecode handling:**
- Default label is specified in switch instruction
- Default body is generated in source order (preserves fall-through)
- If no default clause exists, default label points to END_LABEL

### Break Statement Interaction

Switch statements introduce a new break target:
```typescript
switch (x) {
  case 1:
    if (condition) {
      break;  // Breaks from switch, not if
    }
    doMore();
    break;
}
```

**Context tracking:**
- Push switch's END_LABEL onto break label stack before generating cases
- Break statements jump to this label
- Pop break label after generating switch body

**Labeled break:**
```typescript
outer: switch (x) {
  case 1:
    inner: for (let i = 0; i < 10; i++) {
      if (i == 5) break outer;  // Breaks from switch
      if (i == 3) break inner;  // Breaks from for
    }
}
```

---

## Implementation Phases

### Phase 1: Basic Integer Switch (10 test cases)

**Goal:** Implement simple switch with constant integer cases and break statements.

**Features:**
- Single-type discriminant (int)
- Constant case values (literals)
- All cases end with break
- No default clause
- No fall-through

**Test Cases:**

1. **testSwitchBasicThreeCases** - Switch with 3 cases, all with break
   ```typescript
   let result = 0;
   switch (x) {
     case 1: result = 10; break;
     case 2: result = 20; break;
     case 3: result = 30; break;
   }
   ```
   Expected: Correct case executes, others don't

2. **testSwitchSingleCase** - Switch with only one case
   ```typescript
   switch (x) {
     case 5: result = 50; break;
   }
   ```
   Expected: Executes if x==5, otherwise does nothing

3. **testSwitchDenseCases** - Dense cases (use tableswitch)
   ```typescript
   switch (x) {
     case 0: result = 0; break;
     case 1: result = 1; break;
     case 2: result = 2; break;
     case 3: result = 3; break;
   }
   ```
   Expected: Uses tableswitch, O(1) lookup

4. **testSwitchSparseCases** - Sparse cases (use lookupswitch)
   ```typescript
   switch (x) {
     case 1: result = 1; break;
     case 10: result = 10; break;
     case 100: result = 100; break;
     case 1000: result = 1000; break;
   }
   ```
   Expected: Uses lookupswitch, cases sorted

5. **testSwitchNoMatch** - Discriminant matches no case
   ```typescript
   let result = -1;
   switch (99) {
     case 1: result = 1; break;
     case 2: result = 2; break;
   }
   ```
   Expected: result remains -1

6. **testSwitchFirstCase** - Matches first case
   ```typescript
   switch (1) {
     case 1: result = 100; break;
     case 2: result = 200; break;
     case 3: result = 300; break;
   }
   ```
   Expected: result = 100

7. **testSwitchLastCase** - Matches last case
   ```typescript
   switch (3) {
     case 1: result = 100; break;
     case 2: result = 200; break;
     case 3: result = 300; break;
   }
   ```
   Expected: result = 300

8. **testSwitchNegativeCases** - Negative case values
   ```typescript
   switch (x) {
     case -5: result = 5; break;
     case -3: result = 3; break;
     case 0: result = 0; break;
     case 3: result = -3; break;
   }
   ```
   Expected: Handles negative values correctly

9. **testSwitchWithExpressionDiscriminant** - Discriminant is expression
   ```typescript
   let x = 5;
   switch (x * 2) {  // Evaluates to 10
     case 5: result = 5; break;
     case 10: result = 10; break;
     case 15: result = 15; break;
   }
   ```
   Expected: Evaluates discriminant once, result = 10

10. **testSwitchWithVariableInCase** - Variable declared in case
    ```typescript
    switch (x) {
      case 1:
        const y: int = 10;
        result = y;
        break;
      case 2:
        const z: int = 20;
        result = z;
        break;
    }
    ```
    Expected: Variables scoped correctly

### Phase 2: Default Clause (8 test cases)

**Goal:** Add support for default clause in various positions.

**Features:**
- Default clause at end, beginning, or middle
- Default with and without break
- Default as only clause
- Switch with no matching case falls to default

**Test Cases:**

11. **testSwitchDefaultAtEnd** - Standard pattern
    ```typescript
    switch (x) {
      case 1: result = 1; break;
      case 2: result = 2; break;
      default: result = -1; break;
    }
    ```
    Expected: x==1 → 1, x==2 → 2, x==99 → -1

12. **testSwitchDefaultAtBeginning** - Default first
    ```typescript
    switch (x) {
      default: result = -1; break;
      case 1: result = 1; break;
      case 2: result = 2; break;
    }
    ```
    Expected: Same behavior as default at end

13. **testSwitchDefaultInMiddle** - Default between cases
    ```typescript
    switch (x) {
      case 1: result = 1; break;
      default: result = -1; break;
      case 2: result = 2; break;
    }
    ```
    Expected: Same behavior, position doesn't matter for matching

14. **testSwitchDefaultOnly** - Only default clause
    ```typescript
    switch (x) {
      default: result = 100; break;
    }
    ```
    Expected: Always executes default

15. **testSwitchDefaultNoBreak** - Default falls through
    ```typescript
    switch (x) {
      case 1: result = 1; break;
      default: result = -1;
      case 2: result += 10; break;
    }
    ```
    Expected: x==1 → 1, x==2 → 10, x==99 → 9 (default sets -1, falls to case 2)

16. **testSwitchEmptyDefault** - Empty default clause
    ```typescript
    switch (x) {
      case 1: result = 1; break;
      default:
    }
    ```
    Expected: No-op for non-matching values

17. **testSwitchDefaultWithReturn** - Default returns
    ```typescript
    switch (x) {
      case 1: return 1;
      default: return -1;
    }
    return 99;  // Unreachable if default has no break
    ```
    Expected: Always returns, never reaches end

18. **testSwitchDefaultWithMultipleStatements** - Complex default
    ```typescript
    switch (x) {
      case 1: result = 1; break;
      default:
        let tmp: int = x * 2;
        result = tmp + 10;
        break;
    }
    ```
    Expected: Default can have multiple statements

### Phase 3: Fall-Through Behavior (12 test cases)

**Goal:** Implement correct fall-through semantics when break is omitted.

**Features:**
- Cases without break fall through to next case
- Multiple empty cases (case grouping)
- Mix of break and fall-through
- Fall-through to default
- Default falls through to subsequent case

**Test Cases:**

19. **testSwitchFallThroughSimple** - Basic fall-through
    ```typescript
    let result = 0;
    switch (2) {
      case 1: result += 1;
      case 2: result += 2;
      case 3: result += 3;
    }
    ```
    Expected: result = 2 + 3 = 5 (case 2 falls through to case 3)

20. **testSwitchFallThroughAll** - Complete fall-through
    ```typescript
    let result = 0;
    switch (1) {
      case 1: result += 1;
      case 2: result += 2;
      case 3: result += 3;
      default: result += 10;
    }
    ```
    Expected: result = 1 + 2 + 3 + 10 = 16

21. **testSwitchCaseGrouping** - Empty cases
    ```typescript
    switch (x) {
      case 1:
      case 2:
      case 3:
        result = 123;
        break;
      case 4:
      case 5:
        result = 45;
        break;
    }
    ```
    Expected: x∈{1,2,3} → 123, x∈{4,5} → 45

22. **testSwitchMixedFallThrough** - Some break, some fall-through
    ```typescript
    let result = 0;
    switch (x) {
      case 1: result += 1; break;
      case 2: result += 2;
      case 3: result += 3; break;
      case 4: result += 4; break;
    }
    ```
    Expected: x==1 → 1, x==2 → 5, x==3 → 3, x==4 → 4

23. **testSwitchFallThroughToDefault** - Case falls to default
    ```typescript
    let result = 0;
    switch (x) {
      case 1: result += 1;
      default: result += 10;
    }
    ```
    Expected: x==1 → 11, x==99 → 10

24. **testSwitchDefaultFallsThrough** - Default falls to case
    ```typescript
    let result = 0;
    switch (x) {
      default: result += 10;
      case 1: result += 1; break;
    }
    ```
    Expected: x==1 → 1, x==99 → 11 (default falls to case 1)

25. **testSwitchFallThroughWithStatements** - Each case has statements
    ```typescript
    let result = "";
    switch (2) {
      case 1: result += "A";
      case 2: result += "B";
      case 3: result += "C"; break;
    }
    ```
    Expected: result = "BC"

26. **testSwitchFallThroughSkipsCase** - Falls through empty case
    ```typescript
    let result = 0;
    switch (2) {
      case 1: result = 1;
      case 2:
      case 3: result = 3; break;
    }
    ```
    Expected: result = 3 (case 2 is empty, falls to case 3)

27. **testSwitchFallThroughWithReturn** - Return stops fall-through
    ```typescript
    switch (x) {
      case 1: return 1;
      case 2: return 2;
      default: return -1;
    }
    ```
    Expected: No fall-through, always returns

28. **testSwitchFallThroughMultipleLevels** - Long fall-through chain
    ```typescript
    let result = 0;
    switch (1) {
      case 1: result += 1;
      case 2: result += 2;
      case 3: result += 3;
      case 4: result += 4;
      case 5: result += 5; break;
    }
    ```
    Expected: result = 1+2+3+4+5 = 15

29. **testSwitchFallThroughLastCase** - Last case has no break
    ```typescript
    let result = 0;
    switch (3) {
      case 1: result = 1; break;
      case 2: result = 2; break;
      case 3: result = 3;
    }
    ```
    Expected: result = 3, exits switch (implicit)

30. **testSwitchFallThroughComplexPattern** - Realistic pattern
    ```typescript
    let result = 0;
    switch (x) {
      case 0: result = 0; break;
      case 1:
      case 2:
        result = 12;
        break;
      case 3: result = 3;
      case 4: result += 4;
      default: result += 100;
    }
    ```
    Expected: x==0→0, x==1→12, x==2→12, x==3→107, x==4→104, x==99→100

### Phase 4: String Switches (10 test cases)

**Goal:** Implement string-based switches using hash code + equals pattern.

**Features:**
- String literal cases
- Hash-based lookup with collision handling
- String equals() checks
- Null handling
- Empty string cases

**Test Cases:**

31. **testSwitchStringBasic** - Simple string switch
    ```typescript
    let result = 0;
    switch (str) {
      case "foo": result = 1; break;
      case "bar": result = 2; break;
      case "baz": result = 3; break;
    }
    ```
    Expected: Two-stage compilation (hash + equals)

32. **testSwitchStringWithDefault** - String switch with default
    ```typescript
    switch (str) {
      case "apple": result = 1; break;
      case "banana": result = 2; break;
      default: result = -1; break;
    }
    ```
    Expected: str=="apple" → 1, str=="banana" → 2, str=="xyz" → -1

33. **testSwitchStringEmptyCase** - Empty string as case
    ```typescript
    switch (str) {
      case "": result = 0; break;
      case "foo": result = 1; break;
    }
    ```
    Expected: str=="" → 0

34. **testSwitchStringCaseSensitive** - Case sensitivity
    ```typescript
    switch (str) {
      case "Foo": result = 1; break;
      case "foo": result = 2; break;
      case "FOO": result = 3; break;
    }
    ```
    Expected: Distinguishes between cases

35. **testSwitchStringHashCollision** - Handle hash collisions
    ```typescript
    // Use strings with same hash code (if possible)
    switch (str) {
      case "Aa": result = 1; break;
      case "BB": result = 2; break;  // Same hash as "Aa"
    }
    ```
    Expected: Correctly uses equals() to distinguish

36. **testSwitchStringFallThrough** - String fall-through
    ```typescript
    let result = "";
    switch (str) {
      case "a": result += "A";
      case "b": result += "B"; break;
      case "c": result += "C"; break;
    }
    ```
    Expected: str=="a" → "AB", str=="b" → "B"

37. **testSwitchStringMultipleMatches** - Case grouping
    ```typescript
    switch (str) {
      case "red":
      case "green":
      case "blue":
        result = 1;
        break;
    }
    ```
    Expected: Any of three colors → 1

38. **testSwitchStringSpecialChars** - Special characters
    ```typescript
    switch (str) {
      case "hello\nworld": result = 1; break;
      case "tab\there": result = 2; break;
    }
    ```
    Expected: Handles escape sequences

39. **testSwitchStringLongStrings** - Long string literals
    ```typescript
    switch (str) {
      case "a very long string literal that exceeds normal length": result = 1; break;
      case "another long string": result = 2; break;
    }
    ```
    Expected: No length restrictions

40. **testSwitchStringUnicode** - Unicode strings
    ```typescript
    switch (str) {
      case "你好": result = 1; break;
      case "مرحبا": result = 2; break;
      case "🚀": result = 3; break;
    }
    ```
    Expected: Handles Unicode correctly

### Phase 5: Nested Switches (8 test cases)

**Goal:** Handle switches inside switches and other control structures.

**Features:**
- Switch inside switch case
- Switch inside loop
- Loop inside switch case
- Break disambiguation (inner vs outer)
- Complex nesting patterns

**Test Cases:**

41. **testSwitchNestedBasic** - Switch in switch
    ```typescript
    switch (x) {
      case 1:
        switch (y) {
          case 10: result = 110; break;
          case 20: result = 120; break;
        }
        break;
      case 2: result = 2; break;
    }
    ```
    Expected: x==1,y==10 → 110; x==1,y==20 → 120

42. **testSwitchNestedDeep** - Three-level nesting
    ```typescript
    switch (a) {
      case 1:
        switch (b) {
          case 10:
            switch (c) {
              case 100: result = 111; break;
              case 200: result = 112; break;
            }
            break;
        }
        break;
    }
    ```
    Expected: Correct three-level execution

43. **testSwitchInLoop** - Switch inside for loop
    ```typescript
    let sum = 0;
    for (let i = 0; i < 5; i++) {
      switch (i) {
        case 0:
        case 1: sum += 1; break;
        case 2:
        case 3: sum += 2; break;
        default: sum += 5; break;
      }
    }
    ```
    Expected: sum = 1 + 1 + 2 + 2 + 5 = 11

44. **testLoopInSwitch** - For loop inside switch case
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        for (let i = 0; i < 3; i++) {
          result += i;
        }
        break;
      case 2: result = 100; break;
    }
    ```
    Expected: x==1 → 3 (0+1+2), x==2 → 100

45. **testSwitchBreakAmbiguity** - Break targets outer switch
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        for (let i = 0; i < 10; i++) {
          if (i == 5) break;  // Breaks for loop, not switch
          result += i;
        }
        result += 100;
        break;
      case 2: result = 200; break;
    }
    ```
    Expected: x==1 → 110 (0+1+2+3+4 + 100)

46. **testSwitchLabeledBreak** - Labeled break from nested switch
    ```typescript
    let result = 0;
    outer: switch (x) {
      case 1:
        switch (y) {
          case 10:
            result = 10;
            break outer;  // Breaks outer switch
          case 20:
            result = 20;
            break;        // Breaks inner switch
        }
        result += 100;
        break;
      case 2: result = 200; break;
    }
    ```
    Expected: x==1,y==10 → 10; x==1,y==20 → 120

47. **testSwitchNestedFallThrough** - Fall-through in nested switch
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        switch (y) {
          case 10: result += 10;
          case 20: result += 20; break;
        }
      case 2: result += 2; break;
    }
    ```
    Expected: x==1,y==10 → 32; x==1,y==20 → 22; x==1,y==99 → 2

48. **testSwitchInWhile** - Switch inside while loop
    ```typescript
    let result = 0;
    let i = 0;
    while (i < 3) {
      switch (i) {
        case 0: result += 1; break;
        case 1: result += 10; break;
        case 2: result += 100; break;
      }
      i++;
    }
    ```
    Expected: result = 1 + 10 + 100 = 111

### Phase 6: Break and Continue (9 test cases)

**Goal:** Ensure break/continue interact correctly with switches and nested loops.

**Features:**
- Break exits switch
- Continue inside switch (error - must be in loop)
- Labeled break from switch
- Break in loop inside switch
- Continue in loop inside switch

**Test Cases:**

49. **testSwitchMultipleBreaks** - Multiple breaks in same case
    ```typescript
    switch (x) {
      case 1:
        if (condition1) break;
        doSomething();
        if (condition2) break;
        doMore();
        break;
    }
    ```
    Expected: All breaks exit switch

50. **testSwitchBreakInIfElse** - Break in conditional
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        if (y > 5) {
          result = 100;
          break;
        } else {
          result = 50;
        }
        result += 10;
        break;
    }
    ```
    Expected: x==1,y>5 → 100; x==1,y<=5 → 60

51. **testSwitchLabeledBreakToSwitch** - Labeled break to switch
    ```typescript
    let result = 0;
    mySwitch: switch (x) {
      case 1:
        result = 1;
        break mySwitch;
      case 2:
        result = 2;
        break;
    }
    ```
    Expected: Same as unlabeled break

52. **testSwitchLabeledBreakToOuter** - Break to outer label
    ```typescript
    let result = 0;
    outer: for (let i = 0; i < 3; i++) {
      switch (i) {
        case 1:
          result = 100;
          break outer;  // Exits for loop
        default:
          result += i;
          break;
      }
    }
    ```
    Expected: result = 100 (exits at i==1)

53. **testSwitchContinueInLoop** - Continue in loop inside switch
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        for (let i = 0; i < 5; i++) {
          if (i == 2) continue;  // Continues for loop
          result += i;
        }
        break;
    }
    ```
    Expected: x==1 → 8 (0+1+3+4, skips 2)

54. **testSwitchBreakInNestedLoop** - Break in nested loop
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        for (let i = 0; i < 10; i++) {
          if (i == 5) break;  // Breaks for loop
          result += i;
        }
        result += 100;  // Still executes
        break;
    }
    ```
    Expected: x==1 → 110

55. **testSwitchLabeledContinue** - Labeled continue from nested switch
    ```typescript
    let result = 0;
    outer: for (let i = 0; i < 3; i++) {
      switch (i) {
        case 1:
          continue outer;  // Continues for loop
        default:
          result += i;
          break;
      }
    }
    ```
    Expected: result = 0 + 2 = 2 (skips i==1)

56. **testSwitchNoBreakExit** - Case without break at end
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        result = 1;
        break;
      case 2:
        result = 2;
        // No break, but it's the last case
    }
    result += 10;
    ```
    Expected: x==1 → 11, x==2 → 12

57. **testSwitchReturnInsteadOfBreak** - Return exits early
    ```typescript
    function test(x: int): int {
      switch (x) {
        case 1: return 100;
        case 2: return 200;
        default: return -1;
      }
      return 999;  // Unreachable
    }
    ```
    Expected: Always returns from switch

### Phase 7: Edge Cases (22+ test cases)

**Goal:** Handle unusual patterns, error conditions, and boundary cases.

**Features:**
- Empty switch (no cases)
- Switch with only default
- Discriminant with side effects
- Large number of cases
- Maximum integer values
- Variable scope edge cases
- Unreachable code
- Type validation (supported and unsupported types)
- Null handling for String and boxed types

**Test Cases:**

58. **testSwitchEmpty** - No cases at all
    ```typescript
    let result = 0;
    switch (x) {
    }
    result = 1;
    ```
    Expected: result = 1 (switch does nothing)

59. **testSwitchOnlyDefault** - Just default clause
    ```typescript
    let result = 0;
    switch (x) {
      default: result = 100; break;
    }
    ```
    Expected: result = 100 (always executes)

60. **testSwitchDiscriminantSideEffect** - Discriminant evaluates once
    ```typescript
    let counter = 0;
    function getX(): int {
      counter++;
      return 5;
    }
    switch (getX()) {
      case 5: result = 1; break;
    }
    ```
    Expected: counter == 1 (evaluated once), result = 1

61. **testSwitchLargeDense** - Many dense cases (tableswitch)
    ```typescript
    switch (x) {
      case 0: result = 0; break;
      case 1: result = 1; break;
      // ... cases 2-98 ...
      case 99: result = 99; break;
    }
    ```
    Expected: Uses tableswitch, handles all 100 cases

62. **testSwitchLargeSparse** - Many sparse cases (lookupswitch)
    ```typescript
    switch (x) {
      case 1: result = 1; break;
      case 100: result = 100; break;
      case 1000: result = 1000; break;
      // ... more sparse values ...
      case 1000000: result = 1000000; break;
    }
    ```
    Expected: Uses lookupswitch, handles sparse distribution

63. **testSwitchMaxIntValue** - Integer edge values
    ```typescript
    switch (x) {
      case 2147483647: result = 1; break;  // Integer.MAX_VALUE
      case -2147483648: result = 2; break; // Integer.MIN_VALUE
      case 0: result = 3; break;
    }
    ```
    Expected: Handles extreme integer values

64. **testSwitchMinMaxRange** - Maximum range
    ```typescript
    switch (x) {
      case -2147483648: result = 1; break;
      case 0: result = 2; break;
      case 2147483647: result = 3; break;
    }
    ```
    Expected: Range too large for tableswitch, uses lookupswitch

65. **testSwitchVariableScope** - Variables visible across cases
    ```typescript
    let result = 0;
    switch (x) {
      case 1:
        const a: int = 10;
        result = a;
        break;
      case 2:
        // 'a' is in scope but not initialized
        const b: int = 20;
        result = b;
        break;
    }
    ```
    Expected: Each case can declare variables

66. **testSwitchVariableShadowing** - Variable shadowing
    ```typescript
    let x = 5;
    switch (x) {
      case 5:
        const x: int = 100;  // Shadows outer x
        result = x;
        break;
    }
    ```
    Expected: result = 100 (inner x shadows outer)

67. **testSwitchUnreachableAfterReturn** - Unreachable code
    ```typescript
    switch (x) {
      case 1:
        return 100;
        result = 200;  // Unreachable
        break;
    }
    ```
    Expected: Compiles, ignores unreachable code

68. **testSwitchUnreachableCase** - Case after default with return
    ```typescript
    switch (x) {
      default:
        return -1;
      case 1:  // Unreachable if default always returns
        return 1;
    }
    ```
    Expected: Depends on position; if default has return and is first, later cases unreachable

69. **testSwitchAllCasesReturn** - All cases return
    ```typescript
    function test(x: int): int {
      switch (x) {
        case 1: return 1;
        case 2: return 2;
        default: return -1;
      }
      // No explicit return needed (unreachable)
    }
    ```
    Expected: Method always returns

70. **testSwitchMixedBreakReturn** - Some break, some return
    ```typescript
    function test(x: int): int {
      let result = 0;
      switch (x) {
        case 1: result = 1; break;
        case 2: return 2;
        default: result = -1; break;
      }
      return result;
    }
    ```
    Expected: x==1 → 1, x==2 → 2, x==99 → -1

71. **testSwitchEmptyBlock** - Case with empty block
    ```typescript
    switch (x) {
      case 1: { }
      case 2: result = 2; break;
    }
    ```
    Expected: case 1 is empty, falls through

72. **testSwitchComplexFallThroughPattern** - Realistic fall-through
    ```typescript
    // Simulating weekday classification
    let isWeekend = false;
    switch (day) {
      case 0:  // Sunday
      case 6:  // Saturday
        isWeekend = true;
        break;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        isWeekend = false;
        break;
    }
    ```
    Expected: day∈{0,6} → true, day∈{1,2,3,4,5} → false

73. **testSwitchDensityBoundary** - Right at density threshold
    ```typescript
    // Cases: 0, 1, 2, 4 (missing 3)
    // Density = 4/5 = 80% (above threshold)
    switch (x) {
      case 0: result = 0; break;
      case 1: result = 1; break;
      case 2: result = 2; break;
      case 4: result = 4; break;
    }
    ```
    Expected: Uses tableswitch (high density)

74. **testSwitchBelowDensityThreshold** - Just below threshold
    ```typescript
    // Cases: 0, 2, 4 (missing 1, 3)
    // Density = 3/5 = 60% (may use lookupswitch if threshold is 66%)
    switch (x) {
      case 0: result = 0; break;
      case 2: result = 2; break;
      case 4: result = 4; break;
    }
    ```
    Expected: Choice depends on threshold

75. **testSwitchConsecutiveStrings** - String ordering doesn't matter
    ```typescript
    switch (str) {
      case "zebra": result = 1; break;
      case "apple": result = 2; break;
      case "monkey": result = 3; break;
    }
    ```
    Expected: Source order preserved in hash table

---

## Edge Cases and Error Conditions

### Compilation Errors (Should Not Compile)

76. **Duplicate case values:**
    ```typescript
    switch (x) {
      case 1: a(); break;
      case 1: b(); break;  // ERROR: duplicate case
    }
    ```
    Expected: Compilation error

77. **Multiple default clauses:**
    ```typescript
    switch (x) {
      case 1: a(); break;
      default: b(); break;
      default: c(); break;  // ERROR: duplicate default
    }
    ```
    Expected: Compilation error

78. **Non-constant case expressions:**
    ```typescript
    let y = 5;
    switch (x) {
      case y: a(); break;  // ERROR: case must be constant
    }
    ```
    Expected: Compilation error (unless TypeScript allows and we evaluate)

79. **Type mismatch:**
    ```typescript
    switch (5) {  // int
      case "5": a(); break;  // string - type mismatch
    }
    ```
    Expected: Type error

80. **Continue not in loop:**
    ```typescript
    switch (x) {
      case 1:
        continue;  // ERROR: not in loop
    }
    ```
    Expected: Compilation error

81. **Unsupported discriminant type (long):**
    ```typescript
    let x: long = 5L;
    switch (x) {  // ERROR: long not supported
      case 1L: result = 1; break;
    }
    ```
    Expected: Compilation error

82. **Unsupported discriminant type (boolean):**
    ```typescript
    switch (flag) {  // ERROR: boolean not supported
      case true: result = 1; break;
      case false: result = 2; break;
    }
    ```
    Expected: Compilation error

83. **Unsupported discriminant type (float/double):**
    ```typescript
    switch (x) {  // ERROR: float/double not supported
      case 1.0: result = 1; break;
      case 2.5: result = 2; break;
    }
    ```
    Expected: Compilation error

### Runtime Edge Cases

84. **Null discriminant (String):**
    ```typescript
    let str: string = null;
    switch (str) {
      case "foo": result = 1; break;
      case null: result = 2; break;
      default: result = 3; break;
    }
    ```
    Expected: NullPointerException when calling hashCode() on null, or compile-time null check

85. **Null boxed Integer:**
    ```typescript
    let i: Integer = null;
    switch (i) {  // NullPointerException when unboxing
      case 1: result = 1; break;
    }
    ```
    Expected: NullPointerException when unboxing null, or compile-time null check

86. **Switch on char:**
    ```typescript
    let c: char = 'A';
    switch (c) {
      case 'A': result = 1; break;
      case 'B': result = 2; break;
    }
    ```
    Expected: Works (char promotes to int)

87. **Switch on byte/short:**
    ```typescript
    let b: byte = 5;
    switch (b) {
      case 1: result = 1; break;
      case 5: result = 5; break;
    }
    ```
    Expected: Works (promotes to int)

88. **Switch on boxed Integer:**
    ```typescript
    let i: Integer = 5;
    switch (i) {
      case 1: result = 1; break;
      case 5: result = 5; break;
    }
    ```
    Expected: Works (unboxes to int, then switch)

89. **Switch on enum:**
    ```typescript
    enum Color { RED, GREEN, BLUE }
    switch (color) {
      case Color.RED: result = 1; break;
      case Color.GREEN: result = 2; break;
      case Color.BLUE: result = 3; break;
    }
    ```
    Expected: Uses enum ordinal() values for switch cases (TypeScript enums compile to Java enums with ordinal() method)

---

## Implementation Tasks

### Core Files to Create

1. **SwitchStatementGenerator.java**
   - Main generator for switch statements
   - tableswitch vs lookupswitch selection logic
   - Case label management
   - Fall-through detection
   - Break label stack management

2. **SwitchCaseAnalyzer.java** (helper class)
   - Analyze case values and determine dense vs sparse
   - Calculate density ratio
   - Sort cases for lookupswitch
   - Detect duplicates
   - Extract default clause

3. **StringSwitchGenerator.java** (helper class)
   - Generate hash-based string switches
   - Handle hash collisions with equals()
   - Build hash-to-label mapping

### Core Files to Modify

1. **StatementGenerator.java**
   - Add dispatch for `Swc4jAstSwitchStmt`
   - Route to `SwitchStatementGenerator.generate()`

2. **CompilationContext.java**
   - Add switch break label stack management
   - Track current switch for break statements

3. **VariableAnalyzer.java**
   - Analyze switch body as single scope
   - Handle variable declarations in cases

4. **BreakStatementGenerator.java**
   - May need updates to handle switch break labels
   - Should already work with label stack

### Test Files to Create

1. **TestCompileAstSwitchStmtBasic.java** - Phase 1 (10 tests)
2. **TestCompileAstSwitchStmtDefault.java** - Phase 2 (8 tests)
3. **TestCompileAstSwitchStmtFallThrough.java** - Phase 3 (12 tests)
4. **TestCompileAstSwitchStmtString.java** - Phase 4 (10 tests)
5. **TestCompileAstSwitchStmtNested.java** - Phase 5 (8 tests)
6. **TestCompileAstSwitchStmtBreak.java** - Phase 6 (9 tests)
7. **TestCompileAstSwitchStmtEdgeCases.java** - Phase 7 (22+ tests)

**Total: 79+ test cases**

---

## Bytecode Generation Details

### tableswitch Instruction Format

```
tableswitch {
  defaultOffset: <offset to default label>
  low: <minimum case value>
  high: <maximum case value>
  <offset for case low>
  <offset for case low+1>
  ...
  <offset for case high>
}
```

**Requirements:**
- Discriminant must be on stack as int
- Cases must cover contiguous range [low, high]
- Missing values in range jump to default
- Offsets are relative to tableswitch instruction

### lookupswitch Instruction Format

```
lookupswitch {
  defaultOffset: <offset to default label>
  npairs: <number of case pairs>
  <key1>: <offset1>
  <key2>: <offset2>
  ...
  <keyN>: <offsetN>
}
```

**Requirements:**
- Keys must be sorted in ascending order
- Each key is matched using binary search
- Offsets are relative to lookupswitch instruction

### Density Calculation

```java
int min = Collections.min(caseValues);
int max = Collections.max(caseValues);
long range = (long)max - (long)min + 1;  // Use long to avoid overflow
int count = caseValues.size();

if (range <= 0 || range > 10000) {
    // Range too large or wrapped, use lookupswitch
    return LOOKUPSWITCH;
}

double density = (double)count / (double)range;
if (density >= 0.5) {  // 50% threshold
    return TABLESWITCH;
} else {
    return LOOKUPSWITCH;
}
```

### Enum Switch Compilation

Enum switches have **highest priority** and use ordinal() values:

**Step 1: Detect enum type:**
```java
if (discriminantType instanceof EnumType) {
    // Switch on enum - use ordinal() values
    return generateEnumSwitch(switchStmt, discriminantType);
}
```

**Step 2: Generate ordinal() call:**
```
aload <enum var>
invokevirtual <EnumClass>.ordinal()I  // Returns int
```

**Step 3: Map case values to ordinal values:**
```java
// Case values are enum constants like Color.RED
// Convert to ordinal values at compile time
Map<Integer, CaseInfo> ordinalMap = new HashMap<>();
for (CaseInfo caseInfo : cases) {
    EnumConstant enumConst = (EnumConstant) caseInfo.value;
    int ordinal = enumConst.getOrdinal();  // Known at compile time
    ordinalMap.put(ordinal, caseInfo);
}
```

**Step 4: Generate tableswitch or lookupswitch:**
```
// After ordinal() call, switch stack has int value
// Use standard int switch logic (tableswitch or lookupswitch)
tableswitch {
  min: 0
  max: 2
  0: CASE_RED      // Color.RED (ordinal 0)
  1: CASE_GREEN    // Color.GREEN (ordinal 1)
  2: CASE_BLUE     // Color.BLUE (ordinal 2)
  default: DEFAULT
}
```

**Benefits:**
- O(1) lookup (tableswitch for small enum ranges)
- Type-safe at compile time
- No runtime type checking needed
- Leverages existing int switch infrastructure

### Boxed Type Switch Compilation

Boxed types (Integer, Byte, Short, Character) require unboxing:

**Step 1: Detect boxed type:**
```java
if (discriminantType.equals("Integer") || discriminantType.equals("Byte") ||
    discriminantType.equals("Short") || discriminantType.equals("Character")) {
    // Need to unbox before switch
}
```

**Step 2: Generate unboxing call:**
```
aload <boxed var>
invokevirtual Integer.intValue()I  // Or byteValue, shortValue, charValue
// Stack now has primitive int value
```

**Step 3: Use standard int switch:**
```
// After unboxing, proceed with tableswitch or lookupswitch
// Same as primitive int switch
```

**Null handling:**
```
// Option 1: Add null check before unboxing
aload <boxed var>
ifnull NULL_LABEL
invokevirtual Integer.intValue()I
// ... switch code ...
NULL_LABEL:
  // Handle null case or throw NullPointerException
```

### String Switch Compilation

**Step 1: Generate hash map:**
```java
Map<Integer, List<CaseInfo>> hashMap = new HashMap<>();
for (CaseInfo caseInfo : cases) {
    int hash = caseInfo.stringValue.hashCode();
    hashMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(caseInfo);
}
```

**Step 2: Generate hash switch:**
```
aload <string var>
invokevirtual String.hashCode()
lookupswitch {
  hash1: HASH_BLOCK_1
  hash2: HASH_BLOCK_2
  ...
  default: DEFAULT_LABEL
}
```

**Step 3: Generate equality checks for each hash block:**
```
HASH_BLOCK_1:
  aload <string var>
  ldc "first string with this hash"
  invokevirtual String.equals()
  ifne CASE_LABEL_1

  aload <string var>
  ldc "second string with same hash"
  invokevirtual String.equals()
  ifne CASE_LABEL_2

  goto DEFAULT_LABEL
```

### Fall-Through Implementation

**Detection:**
```java
boolean endsWithBreak(List<ISwc4jAstStmt> statements) {
    if (statements.isEmpty()) return false;
    ISwc4jAstStmt last = statements.get(statements.size() - 1);
    return last instanceof Swc4jAstBreakStmt ||
           last instanceof Swc4jAstReturnStmt;
}
```

**Code generation:**
```java
for (int i = 0; i < cases.size(); i++) {
    CaseInfo caseInfo = cases.get(i);

    // Mark case label
    code.markLabel(caseInfo.label);

    // Generate case body
    generateStatements(caseInfo.statements);

    // Add goto END if case ends with break
    if (endsWithBreak(caseInfo.statements)) {
        code.gotoLabel(endLabel);
    }
    // Otherwise, fall through to next case
}
```

### Scope Management

Switch body is a single scope:
```java
// Enter switch scope (for variable declarations in cases)
context.getLocalVariableTable().enterScope();

// Generate all cases (variables declared in any case are visible to all)
for (CaseInfo caseInfo : cases) {
    generateCase(caseInfo);
}

// Exit switch scope
context.getLocalVariableTable().exitScope();
```

---

## Implementation Estimate

**Effort:** HIGH (complex instruction selection, string handling, fall-through semantics)

**Time Estimate:**
- SwitchStatementGenerator core: 4-6 hours
- tableswitch/lookupswitch selection: 2-3 hours
- String switch support: 3-4 hours
- Fall-through logic: 2-3 hours
- Break/label management: 1-2 hours
- Testing: 6-8 hours (75+ test cases)
- Documentation: 1-2 hours
- **Total: 19-28 hours**

**Dependencies:**
- BreakStatementGenerator (already implemented)
- LabeledStatementGenerator (already implemented)
- CompilationContext label stacks (already implemented)
- ExpressionGenerator (for discriminant and case expressions)
- TypeResolver (for type checking)
- **TsEnumDecl support (COMPLETED 2026-01-19):**
  - TypeScript enums compile to Java enums with ordinal() method
  - Switch on enum uses ordinal() values (int) for cases
  - Enum implementation: `EnumGenerator.java` generates full Java enum bytecode
  - 66 passing tests covering numeric, string, const enums, and edge cases

**Complexity: HIGH**
- Two different bytecode instructions (tableswitch vs lookupswitch)
- Density calculation and selection logic
- String switch requires two-stage compilation
- Fall-through semantics require careful label management
- Scope handling for case-local variables
- Break label stack management

---

## References

- JVM Specification: tableswitch instruction
- JVM Specification: lookupswitch instruction
- Java Language Specification: Switch Statements (14.11)
- String Switch Implementation (JEP 200)
- TypeScript AST: SwitchStatement node

---

## Notes

- **Constant case values:** All case expressions must be compile-time constants in Java. TypeScript may be more permissive, but JVM bytecode requires constants.
- **Type restrictions:**
  - **Supported:** enum (highest priority), int, String, byte, short, char, Byte, Short, Character, Integer
  - **Unsupported:** long, float, double, boolean, Object, or any other reference types
  - Enum switches use ordinal() values (int)
  - Boxed types (Byte, Short, Character, Integer) are unboxed to primitives
  - Primitives (byte, short, char) are promoted to int
- **Type priority:** enum > int > String > byte/short/char > boxed types
- **Null handling:** String switches require null checks to avoid NullPointerException. Boxed types may need null checks before unboxing.
- **Performance:** tableswitch is O(1), lookupswitch is O(log n). Choose wisely based on case distribution.
- **Fall-through is intentional:** Unlike other languages, Java/JavaScript fall-through is a feature, not a bug. Properly handle it in bytecode.
- **Default position:** Default can appear anywhere in source, but always acts as fallback in bytecode.
- **Unreachable code:** Code after return/break in a case is unreachable but may still be in AST. Handle gracefully.
- **Hash collisions:** String switches must handle hash collisions correctly with equals() checks.
- **Stack map frames:** May need frames at case labels for JVM verifier.
