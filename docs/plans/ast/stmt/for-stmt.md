# For Statement Implementation Plan

## Overview

This document outlines the implementation plan for supporting for loops in TypeScript to JVM bytecode compilation. For loops provide iterative execution of code blocks based on initialization, condition testing, and update operations.

**Current Status:** 🟡 **IN PROGRESS** - Core implementation complete, debugging bytecode verification issue

**Syntax:**
```typescript
for (init; test; update) { body }
for (let i = 0; i < 10; i++) { /* body */ }
for (; condition; ) { /* body */ }        // init and update optional
for (;;) { /* infinite loop */ }          // all parts optional
```

**Implementation File:** `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/ForStatementGenerator.java` (to be created)

**Test File:** `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/forstmt/TestCompileAstForStmt*.java` (to be created)

**AST Definition:** [Swc4jAstForStmt.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/stmt/Swc4jAstForStmt.java)

---

## For Statement Fundamentals

### Statement Semantics

A for statement has four components (all optional):
1. **Init** - Initialization expression or variable declaration (executed once before loop)
2. **Test** - Condition to evaluate before each iteration (continues if true/truthy)
3. **Update** - Expression executed after each iteration
4. **Body** - Statement(s) executed in each iteration

### JavaScript/TypeScript Behavior

```typescript
// Standard for loop
for (let i = 0; i < 10; i++) {
  console.log(i);
}

// Multiple variables in init
for (let i = 0, j = 10; i < j; i++, j--) {
  console.log(i, j);
}

// Infinite loop (all parts omitted)
for (;;) {
  if (condition) break;
}

// Only test condition
for (; x < 100; ) {
  x = process(x);
}

// No test (infinite until break)
for (let i = 0; ; i++) {
  if (i > 100) break;
}

// With break and continue
for (let i = 0; i < 10; i++) {
  if (i % 2 === 0) continue;
  if (i > 7) break;
  process(i);
}
```

### JVM Bytecode Strategy

Unlike simple if statements, for loops require **backward jumps** to repeat execution. The JVM uses labels and goto instructions to implement loops.

**Basic For Loop Pattern:**
```java
// For: for (init; test; update) { body }

init_code                 // Execute initialization
test_label:               // Loop entry point (for continue)
test_code                 // Evaluate condition
ifeq end_label            // Exit if false
body_code                 // Execute body
update_label:             // Continue target
update_code               // Execute update
goto test_label           // Jump back to test
end_label:                // Break target
                          // Continue execution
```

**Infinite Loop Pattern (no test):**
```java
// For: for (;;) { body }

loop_label:
body_code
goto loop_label           // Unconditional jump back
// Unreachable without break
```

**Stack Map Frames Required:**
- At test_label (loop entry point)
- At update_label (if continue exists)
- At end_label (after loop exits)

---

## Implementation Phases

### Phase 1: Basic For Loops (Priority: HIGH)

Support simple for loops with all components present.

**Scope:**
- For loops with initialization, test, and update
- Single variable initialization (let/const i = 0)
- Simple test conditions (i < 10)
- Simple update expressions (i++, i--, i += 1)
- Block statement body
- Local variable scoping within loop

**Example Bytecode:**
```
// For: for (let i = 0; i < 10; i++) { sum += i; }

iconst_0              // Initialize i
istore_1              // Store to local var 1 (i)
test_label:           // Loop entry
iload_1               // Load i
bipush 10             // Load 10
if_icmpge end_label   // Exit if i >= 10
iload_2               // Load sum
iload_1               // Load i
iadd                  // sum + i
istore_2              // Store to sum
iinc 1, 1             // i++
goto test_label       // Jump back
end_label:
```

**Test Coverage:**
1. Basic counting loop (0 to 10)
2. Loop with complex body
3. Loop with multiple statements in body
4. Loop with different comparison operators (>, >=, <, <=, ==, !=)
5. Loop with increment (i++)
6. Loop with decrement (i--)
7. Loop with compound assignment (i += 2)
8. Loop that doesn't execute (initial condition false)
9. Loop with local variable declarations in body
10. Loop with return statement in body

---

### Phase 2: Optional Components (Priority: HIGH)

Support for loops with missing init, test, or update.

**Scope:**
- Loops with no init (for (; test; update))
- Loops with no test (for (init; ; update)) - infinite until break
- Loops with no update (for (init; test; ))
- Loops with only body (for (;;)) - infinite loop
- Combinations of missing components

**Example Bytecode:**
```
// For: for (;;) { if (x > 100) break; }

loop_label:
iload_1               // Load x
bipush 100
if_icmple loop_label  // Continue if x <= 100
                      // Implicit break falls through
```

**Test Coverage:**
1. Loop with no init
2. Loop with no test (infinite)
3. Loop with no update
4. Loop with only body (for (;;))
5. Loop with only init and body
6. Loop with only test and body
7. Loop with only update and body
8. Empty for loop (for (;;) {})

---

### Phase 3: Break and Continue (Priority: HIGH)

Support break and continue statements within loops.

**Scope:**
- Break statement (exit loop immediately)
- Continue statement (skip to next iteration)
- Unlabeled break/continue (innermost loop)
- Multiple break/continue in same loop
- Break/continue in nested if within loop
- Break/continue in nested blocks

**Example Bytecode:**
```
// For: for (let i = 0; i < 10; i++) { if (i == 5) continue; if (i == 8) break; }

iconst_0
istore_1
test_label:
iload_1
bipush 10
if_icmpge end_label
iload_1
iconst_5
if_icmpne check_break
goto update_label     // continue: jump to update
check_break:
iload_1
bipush 8
if_icmpne body_end
goto end_label        // break: jump to end
body_end:
update_label:
iinc 1, 1
goto test_label
end_label:
```

**Test Coverage:**
1. Loop with break in body
2. Loop with continue in body
3. Loop with break in nested if
4. Loop with continue in nested if
5. Loop with multiple breaks
6. Loop with multiple continues
7. Loop with both break and continue
8. Break in first iteration
9. Continue in last iteration
10. Unreachable code after unconditional break

---

### Phase 4: Complex Initialization and Updates (Priority: MEDIUM)

Support for complex init and update expressions.

**Scope:**
- Multiple variable declarations in init (let i = 0, j = 10)
- Expression statements in init (existing variable)
- Multiple expressions in update (i++, j--)
- Complex update expressions (i += j, i *= 2)
- Init with complex expressions
- Update with method calls

**Example Bytecode:**
```
// For: for (let i = 0, j = 10; i < j; i++, j--) { }

iconst_0
istore_1              // i = 0
bipush 10
istore_2              // j = 10
test_label:
iload_1
iload_2
if_icmpge end_label   // Exit if i >= j
// body (empty)
iinc 1, 1             // i++
iinc 2, -1            // j--
goto test_label
end_label:
```

**Test Coverage:**
1. Multiple variable declarations in init
2. Multiple updates (i++, j--)
3. Complex init expressions
4. Complex update expressions
5. Update with method calls
6. Update with side effects
7. Mixed types in multiple variables
8. Dependent updates (j = i * 2)

---

### Phase 5: Nested Loops (Priority: MEDIUM)

Support for nested for loops.

**Scope:**
- For loop inside for loop
- Multiple levels of nesting (3+ deep)
- Break/continue in nested loops
- Shared variables between loops
- Different loop variables at each level

**Example Bytecode:**
```
// For: for (let i = 0; i < 3; i++) { for (let j = 0; j < 3; j++) { sum += i * j; } }

iconst_0
istore_1              // i = 0
outer_test:
iload_1
iconst_3
if_icmpge outer_end
  iconst_0
  istore_2            // j = 0
  inner_test:
  iload_2
  iconst_3
  if_icmpge inner_end
    iload_3           // Load sum
    iload_1           // Load i
    iload_2           // Load j
    imul              // i * j
    iadd              // sum + (i * j)
    istore_3          // Store sum
    iinc 2, 1         // j++
    goto inner_test
  inner_end:
  iinc 1, 1           // i++
  goto outer_test
outer_end:
```

**Test Coverage:**
1. Two-level nested loop
2. Three-level nested loop
3. Four+ level deep nesting
4. Break in outer loop
5. Break in inner loop
6. Continue in outer loop
7. Continue in inner loop
8. Shared variables between loops
9. Inner loop modifies outer loop variable
10. Empty inner loop

---

### Phase 6: Labeled Break and Continue (Priority: LOW)

Support labeled break and continue statements.

**Scope:**
- Label declarations on for loops
- Labeled break (break to specific loop)
- Labeled continue (continue specific loop)
- Break to outer loop from inner loop
- Continue outer loop from inner loop

**Example:**
```typescript
outer: for (let i = 0; i < 10; i++) {
  for (let j = 0; j < 10; j++) {
    if (i * j > 50) break outer;    // Break outer loop
    if (j === 5) continue outer;     // Continue outer loop
  }
}
```

**Test Coverage:**
1. Labeled break to outer loop
2. Labeled continue to outer loop
3. Multiple labeled loops
4. Label on for loop with break
5. Label on for loop with continue
6. Deeply nested with labeled break/continue

---

### Phase 7: Edge Cases and Advanced Scenarios (Priority: LOW)

Handle complex scenarios and edge cases.

**Scope:**
- Side effects in test condition
- Side effects in update
- Variables declared in init used after loop (scoping)
- Return statements in loop body
- Throw statements in loop body
- Nested control flow (if/switch in loop)
- Very large loop bodies
- Loops with no body statements
- Complex stack map frame scenarios

**Test Coverage:**
1. Side effects in test (x++ > 10)
2. Side effects in update (i += compute())
3. Return in loop body
4. Throw in loop body
5. Nested if/else in loop
6. Switch statement in loop
7. Empty loop body
8. Loop variable used after loop
9. Multiple returns in loop
10. Complex stack map scenarios

---

## Edge Cases and Special Scenarios

### Control Flow Edge Cases

1. **Empty Loop**
   ```typescript
   for (let i = 0; i < 10; i++) { }  // Valid, no body
   ```

2. **Infinite Loop**
   ```typescript
   for (;;) { }                       // Infinite, no break
   for (;;) { if (x) break; }         // Infinite with conditional break
   ```

3. **Never Executes**
   ```typescript
   for (let i = 0; i < 0; i++) { }    // Test fails immediately
   ```

4. **Single Iteration**
   ```typescript
   for (let i = 0; i < 1; i++) { }    // Executes once
   ```

5. **Break in First Iteration**
   ```typescript
   for (let i = 0; i < 10; i++) { break; }  // Only init executes
   ```

6. **Continue in All Iterations**
   ```typescript
   for (let i = 0; i < 10; i++) { continue; }  // Body effectively empty
   ```

7. **Return in First Iteration**
   ```typescript
   for (let i = 0; i < 10; i++) { return i; }  // Returns 0
   ```

8. **Multiple Breaks/Continues**
   ```typescript
   for (let i = 0; i < 10; i++) {
     if (i % 2 === 0) continue;
     if (i > 5) break;
     process(i);  // Only processes 1, 3, 5
   }
   ```

### Initialization Edge Cases

9. **No Initialization**
   ```typescript
   let i = 0;
   for (; i < 10; i++) { }             // Use existing variable
   ```

10. **Multiple Variables**
    ```typescript
    for (let i = 0, j = 10, k = 20; i < j; i++, j--, k *= 2) { }
    ```

11. **Expression as Init (not declaration)**
    ```typescript
    let i;
    for (i = 0; i < 10; i++) { }       // Assignment, not declaration
    ```

12. **Complex Init Expression**
    ```typescript
    for (let i = compute(); i < max; i++) { }
    ```

13. **Init with Side Effects**
    ```typescript
    for (let i = arr.length--; i > 0; i--) { }
    ```

14. **Different Variable Types**
    ```typescript
    for (let i: int = 0, d: double = 0.0; i < 10; i++, d += 0.1) { }
    ```

### Test Condition Edge Cases

15. **No Test Condition (infinite)**
    ```typescript
    for (let i = 0; ; i++) { if (i > 100) break; }
    ```

16. **Complex Boolean Expression**
    ```typescript
    for (let i = 0; i < 10 && j > 0 || k === 5; i++) { }
    ```

17. **Test with Side Effects**
    ```typescript
    for (let i = 0; (x++) < 10; i++) { }  // x increments each test
    ```

18. **Short-Circuit Evaluation in Test**
    ```typescript
    for (let i = 0; i < 10 && check(i); i++) { }  // check() not called if i >= 10
    ```

19. **Method Call in Test**
    ```typescript
    for (let i = 0; i < arr.length; i++) { }  // arr.length called each iteration
    ```

20. **Negated Condition**
    ```typescript
    for (let i = 0; !(i >= 10); i++) { }
    ```

### Update Expression Edge Cases

21. **No Update**
    ```typescript
    for (let i = 0; i < 10; ) { i++; }   // Update in body instead
    ```

22. **Multiple Updates**
    ```typescript
    for (let i = 0; i < 10; i++, j--, k *= 2) { }
    ```

23. **Complex Update Expression**
    ```typescript
    for (let i = 0; i < 10; i = i * 2 + 1) { }
    ```

24. **Update with Side Effects**
    ```typescript
    for (let i = 0; i < 10; i = compute(i)) { }
    ```

25. **Update with Method Calls**
    ```typescript
    for (let i = 0; i < 10; arr.push(i++)) { }
    ```

26. **Dependent Updates**
    ```typescript
    for (let i = 0; i < 10; i++, j = i * 2) { }  // j depends on i
    ```

### Body Edge Cases

27. **Empty Body**
    ```typescript
    for (let i = 0; i < 10; i++) { }   // No statements
    ```

28. **Single Statement Body**
    ```typescript
    for (let i = 0; i < 10; i++) process(i);  // No braces
    ```

29. **Block Statement Body**
    ```typescript
    for (let i = 0; i < 10; i++) { const x = i * 2; process(x); }
    ```

30. **Variable Declaration in Body**
    ```typescript
    for (let i = 0; i < 10; i++) {
      const x = i * 2;  // New scope each iteration
    }
    ```

31. **Return in Body**
    ```typescript
    for (let i = 0; i < 10; i++) { return i; }
    ```

32. **Multiple Returns in Body**
    ```typescript
    for (let i = 0; i < 10; i++) {
      if (i % 2 === 0) return i;
      return i + 1;
    }
    ```

33. **Throw in Body**
    ```typescript
    for (let i = 0; i < 10; i++) { throw new Error(); }
    ```

34. **Nested Control Flow**
    ```typescript
    for (let i = 0; i < 10; i++) {
      if (i % 2 === 0) { continue; }
      switch (i) {
        case 1: break;
        case 3: return;
      }
    }
    ```

### Variable Scoping Edge Cases

35. **Loop Variable Scope**
    ```typescript
    for (let i = 0; i < 10; i++) { }
    // i not visible here
    ```

36. **Variable Shadowing**
    ```typescript
    let i = 100;
    for (let i = 0; i < 10; i++) { }  // Shadows outer i
    // i is still 100 here
    ```

37. **Variables Declared in Body**
    ```typescript
    for (let i = 0; i < 3; i++) {
      let x = i;  // New x each iteration
    }
    ```

38. **Closure Over Loop Variable**
    ```typescript
    for (let i = 0; i < 3; i++) {
      const f = () => i;  // Each f captures different i
    }
    ```

39. **Modified in Nested Scope**
    ```typescript
    for (let i = 0; i < 10; i++) {
      if (true) { i += 5; }  // Modifies loop variable
    }
    ```

### Nested Loop Edge Cases

40. **Two-Level Nesting**
    ```typescript
    for (let i = 0; i < 3; i++) {
      for (let j = 0; j < 3; j++) { }
    }
    ```

41. **Deep Nesting (5+ levels)**
    ```typescript
    for (let i = 0; i < 2; i++) {
      for (let j = 0; j < 2; j++) {
        for (let k = 0; k < 2; k++) {
          for (let l = 0; l < 2; l++) {
            for (let m = 0; m < 2; m++) { }
          }
        }
      }
    }
    ```

42. **Break in Nested Loop**
    ```typescript
    for (let i = 0; i < 10; i++) {
      for (let j = 0; j < 10; j++) {
        if (j === 5) break;  // Breaks inner loop only
      }
    }
    ```

43. **Continue in Nested Loop**
    ```typescript
    for (let i = 0; i < 10; i++) {
      for (let j = 0; j < 10; j++) {
        if (j === 5) continue;  // Continues inner loop only
      }
    }
    ```

44. **Shared Variables**
    ```typescript
    for (let i = 0; i < 10; i++) {
      for (let j = 0; j < i; j++) { }  // Inner uses outer variable
    }
    ```

45. **Inner Modifies Outer Variable**
    ```typescript
    for (let i = 0; i < 10; i++) {
      for (let j = 0; j < 10; j++) {
        i++;  // Modifies outer loop variable
      }
    }
    ```

46. **Labeled Break to Outer**
    ```typescript
    outer: for (let i = 0; i < 10; i++) {
      for (let j = 0; j < 10; j++) {
        if (i * j > 50) break outer;
      }
    }
    ```

47. **Labeled Continue to Outer**
    ```typescript
    outer: for (let i = 0; i < 10; i++) {
      for (let j = 0; j < 10; j++) {
        if (j === 5) continue outer;
      }
    }
    ```

### Type Edge Cases

48. **Different Numeric Types**
    ```typescript
    for (let i: byte = 0; i < 10; i++) { }        // byte
    for (let i: short = 0; i < 100; i++) { }      // short
    for (let i: int = 0; i < 1000; i++) { }       // int
    for (let i: long = 0; i < 10000; i++) { }     // long
    ```

49. **Floating Point Loop**
    ```typescript
    for (let d: double = 0.0; d < 1.0; d += 0.1) { }
    for (let f: float = 0.0; f < 1.0; f += 0.1) { }
    ```

50. **Boolean Loop Variable**
    ```typescript
    for (let b: boolean = false; !b; b = true) { }  // Executes once
    ```

51. **Type Widening in Update**
    ```typescript
    for (let i: int = 0; i < 10; i = (long) i + 1) { }
    ```

52. **Overflow/Underflow**
    ```typescript
    for (let i: byte = 127; i < 200; i++) { }  // byte overflow
    ```

### Stack Map Edge Cases

53. **Complex Merge Points**
    ```typescript
    for (let i = 0; i < 10; i++) {
      let x: int;
      if (i % 2 === 0) {
        x = 1;
      } else {
        x = 2;
      }
      // Merge point with x definitely assigned
    }
    ```

54. **Multiple Entry Points (continue targets)**
    ```typescript
    for (let i = 0; i < 10; i++) {
      if (i % 2 === 0) continue;
      if (i % 3 === 0) continue;
      process(i);
    }
    ```

55. **Backward Jump Stack State**
    ```typescript
    for (let i = 0; i < 10; i++) {
      let x = compute();  // Stack must be consistent across iterations
    }
    ```

### Integration Edge Cases

56. **For Loop in Method**
    ```typescript
    function sum(n: int): int {
      let result = 0;
      for (let i = 0; i < n; i++) {
        result += i;
      }
      return result;
    }
    ```

57. **For Loop in Constructor**
    ```typescript
    constructor() {
      for (let i = 0; i < 10; i++) {
        this.array.push(i);
      }
    }
    ```

58. **For Loop with Field Access**
    ```typescript
    for (let i = 0; i < this.length; i++) {
      this.array[i] = i;
    }
    ```

59. **For Loop with Array Access**
    ```typescript
    for (let i = 0; i < arr.length; i++) {
      sum += arr[i];
    }
    ```

60. **For Loop with Method Calls**
    ```typescript
    for (let i = 0; i < 10; i++) {
      this.process(i);
    }
    ```

61. **Nested If in For Loop**
    ```typescript
    for (let i = 0; i < 10; i++) {
      if (i % 2 === 0) {
        even++;
      } else {
        odd++;
      }
    }
    ```

62. **For Loop in If Statement**
    ```typescript
    if (flag) {
      for (let i = 0; i < 10; i++) { }
    }
    ```

63. **Multiple Sequential Loops**
    ```typescript
    for (let i = 0; i < 10; i++) { sumA += i; }
    for (let j = 0; j < 10; j++) { sumB += j; }
    ```

### Performance Edge Cases

64. **Very Large Loop Count**
    ```typescript
    for (let i = 0; i < 1000000; i++) { }
    ```

65. **Very Large Loop Body**
    ```typescript
    for (let i = 0; i < 10; i++) {
      // Thousands of lines of code
    }
    ```

66. **Empty Loop (optimization opportunity)**
    ```typescript
    for (let i = 0; i < 1000000; i++) { }  // Could optimize away
    ```

67. **Loop Invariant Code Motion**
    ```typescript
    for (let i = 0; i < 10; i++) {
      const x = compute();  // Same value each iteration
    }
    ```

### Bytecode Edge Cases

68. **Long Jump Offsets (>32KB)**
    ```typescript
    for (let i = 0; i < 10; i++) {
      // Very large body requiring wide goto
    }
    ```

69. **Backward Jump Distance**
    ```typescript
    // Ensure backward jump offset calculated correctly
    ```

70. **Multiple Gotos to Same Label**
    ```typescript
    for (let i = 0; i < 10; i++) {
      if (i % 2 === 0) continue;
      if (i % 3 === 0) continue;
      if (i % 5 === 0) continue;
    }
    ```

### Error Handling Edge Cases

71. **Exception in Init**
    ```typescript
    for (let i = throwing(); i < 10; i++) { }
    ```

72. **Exception in Test**
    ```typescript
    for (let i = 0; mayThrow(i); i++) { }
    ```

73. **Exception in Update**
    ```typescript
    for (let i = 0; i < 10; throwing(i)) { }
    ```

74. **Exception in Body**
    ```typescript
    for (let i = 0; i < 10; i++) { throw new Error(); }
    ```

75. **Try-Catch in Loop**
    ```typescript
    for (let i = 0; i < 10; i++) {
      try { risky(); } catch (e) { handle(e); }
    }
    ```

76. **Loop in Try-Catch**
    ```typescript
    try {
      for (let i = 0; i < 10; i++) { risky(); }
    } catch (e) { }
    ```

### Special Statement Edge Cases

77. **Unreachable Code After Break**
    ```typescript
    for (let i = 0; i < 10; i++) {
      break;
      console.log("unreachable");  // Dead code
    }
    ```

78. **Unreachable Code After Continue**
    ```typescript
    for (let i = 0; i < 10; i++) {
      continue;
      console.log("unreachable");  // Dead code
    }
    ```

79. **Unreachable Code After Return**
    ```typescript
    for (let i = 0; i < 10; i++) {
      return i;
      console.log("unreachable");  // Dead code
    }
    ```

80. **All Iterations Return**
    ```typescript
    for (let i = 0; i < 10; i++) {
      return i;  // Always returns on first iteration
    }
    // Unreachable
    ```

### Miscellaneous Edge Cases

81. **Comments and Whitespace**
    ```typescript
    for /* comment */ (let i = 0; /* comment */ i < 10; /* comment */ i++) {
      // Should ignore comments
    }
    ```

82. **Unicode in Loop Variables**
    ```typescript
    for (let 変数 = 0; 変数 < 10; 変数++) { }
    ```

83. **Very Long Variable Names**
    ```typescript
    for (let veryLongVariableNameThatExceedsNormalLength = 0; ...) { }
    ```

84. **Reserved Keywords as Labels**
    ```typescript
    label: for (let i = 0; i < 10; i++) { break label; }
    ```

85. **Comma Operator in Update**
    ```typescript
    for (let i = 0; i < 10; (i++, j--, k *= 2)) { }
    ```

86. **Ternary in Test**
    ```typescript
    for (let i = 0; (flag ? i < 10 : i < 20); i++) { }
    ```

87. **Destructuring in Init (if supported)**
    ```typescript
    for (let [i, j] = [0, 10]; i < j; i++, j--) { }
    ```

88. **Multiple For Loops Same Variable Name**
    ```typescript
    for (let i = 0; i < 10; i++) { }
    for (let i = 0; i < 10; i++) { }  // Different i, different scope
    ```

---

## Bytecode Instruction Reference

### Loop-Specific Instructions

**Unconditional Jump (backward):**
- `goto <label>` (0xA7) - Jump back to test label
- `goto_w <label>` (0xC8) - Wide goto for long offsets (>32KB)

**Conditional Jumps:**
- Same as if statements, but used at loop entry/exit
- `ifeq <label>` (0x99) - Jump if false (exit loop)
- `if_icmplt <label>` (0xA1) - Jump if less than (continue loop)
- etc.

**Local Variable Increment:**
- `iinc <index> <const>` (0x84) - Increment local variable by constant
- Useful for simple i++ or i-- in update

**Stack Map Frames:**
- Required at loop entry (test label) for JVM verification
- Required at continue target if different from test label
- Must handle backward jump verification

---

## AST Structure

### Swc4jAstForStmt Components

```java
public class Swc4jAstForStmt {
    Optional<ISwc4jAstForHead> init;     // Init expression or VarDecl (optional)
    Optional<ISwc4jAstExpr> test;        // Test condition (optional)
    Optional<ISwc4jAstExpr> update;      // Update expression (optional)
    ISwc4jAstStmt body;                  // Body statement
}
```

### Related AST Types

- **ISwc4jAstForHead** - Can be VarDecl or Expr
  - Swc4jAstVarDecl - Variable declaration(s) in init
  - ISwc4jAstExpr - Expression in init (assignment, etc.)
- **ISwc4jAstExpr** - Test and update are expressions
- **ISwc4jAstStmt** - Body can be any statement
  - Swc4jAstBlockStmt - Block with multiple statements
  - Swc4jAstExprStmt - Single expression statement
  - Swc4jAstBreakStmt - Break statement
  - Swc4jAstContinueStmt - Continue statement
  - Swc4jAstReturnStmt - Return statement
  - Swc4jAstIfStmt - If statement (nested)
  - Swc4jAstForStmt - Nested for loop
  - etc.

---

## Implementation Strategy

### Code Generation Algorithm

```java
public static void generate(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        Swc4jAstForStmt forStmt,
        ReturnTypeInfo returnTypeInfo,
        CompilationContext context,
        ByteCodeCompilerOptions options) {

    // 1. Generate init (if present)
    if (forStmt.getInit().isPresent()) {
        generateInit(code, cp, forStmt.getInit().get(), context, options);
    }

    // 2. Mark test label (loop entry point)
    int testLabel = code.getCurrentOffset();

    // 3. Generate test condition (if present)
    if (forStmt.getTest().isPresent()) {
        ExpressionGenerator.generate(code, cp, forStmt.getTest().get(), ...);
        code.ifeq(0); // Placeholder
        int ifeqPos = code.getCurrentOffset() - 2;
        int ifeqOpcode = code.getCurrentOffset() - 3;
        context.pushBreakLabel(endLabel);
        context.pushContinueLabel(updateLabel);
    } else {
        // No test - infinite loop
        context.pushBreakLabel(endLabel);
        context.pushContinueLabel(updateLabel);
    }

    // 4. Generate body
    StatementGenerator.generate(code, cp, forStmt.getBody(), returnTypeInfo, context, options);

    // 5. Generate update (if present)
    int updateLabel = code.getCurrentOffset();
    if (forStmt.getUpdate().isPresent()) {
        ExpressionGenerator.generate(code, cp, forStmt.getUpdate().get(), null, context, options);
        // Pop result if update leaves value on stack
        String updateType = TypeResolver.inferTypeFromExpr(forStmt.getUpdate().get(), context, options);
        if (updateType != null && !updateType.equals("V")) {
            if (updateType.equals("D") || updateType.equals("J")) {
                code.pop2();
            } else {
                code.pop();
            }
        }
    }

    // 6. Jump back to test
    code.gotoLabel(testLabel);

    // 7. Mark end label (break target)
    int endLabel = code.getCurrentOffset();

    // 8. Patch conditional jump if test exists
    if (forStmt.getTest().isPresent()) {
        code.patchShort(ifeqPos, endLabel - ifeqOpcode);
    }

    // 9. Pop break/continue labels
    context.popBreakLabel();
    context.popContinueLabel();

    // 10. Generate stack map frame at loop entry
    code.addStackMapFrame(testLabel, ...);
}
```

### Label Management

Track break and continue target labels in CompilationContext:
```java
class CompilationContext {
    Stack<Integer> breakLabels;     // Stack of break targets (end labels)
    Stack<Integer> continueLabels;  // Stack of continue targets (update/test labels)

    void pushBreakLabel(int label);
    void popBreakLabel();
    int getCurrentBreakLabel();

    void pushContinueLabel(int label);
    void popContinueLabel();
    int getCurrentContinueLabel();
}
```

### Break Statement Generation

```java
// Generate: break;
int breakTarget = context.getCurrentBreakLabel();
code.gotoLabel(breakTarget);
```

### Continue Statement Generation

```java
// Generate: continue;
int continueTarget = context.getCurrentContinueLabel();
code.gotoLabel(continueTarget);
```

---

## Stack Map Frame Considerations

### Frame Points

Stack map frames needed at:
1. **Loop entry (test label)** - Entry point for each iteration
2. **Update label** - If different from test label and continue exists
3. **After loop (end label)** - Merge point after loop exits or breaks

### Frame Merging

```java
// Before loop: locals = {this, n}, stack = {}
for (let i = 0; i < n; i++) {
    // At test: locals = {this, n, i}, stack = {}
    let x = compute(i);
    // At update: locals = {this, n, i, x}, stack = {}
}
// After loop: locals = {this, n}, stack = {}
// i not visible (loop scope)
```

### Backward Jump Verification

JVM requires consistent stack state at backward jump targets:
- Stack must be empty at test label
- Local variable types must match
- Stack map frame at test label must match state from goto

---

## Integration Points

### Statement Generator

Update `StatementGenerator.java` to dispatch ForStmt:

```java
if (stmt instanceof Swc4jAstForStmt forStmt) {
    ForStatementGenerator.generate(code, cp, forStmt, returnTypeInfo, context, options);
}
```

Also handle break and continue statements:

```java
if (stmt instanceof Swc4jAstBreakStmt breakStmt) {
    BreakStatementGenerator.generate(code, cp, breakStmt, context, options);
}

if (stmt instanceof Swc4jAstContinueStmt continueStmt) {
    ContinueStatementGenerator.generate(code, cp, continueStmt, context, options);
}
```

### Expression Generator Integration

- Init expression uses existing expression generation
- Test condition uses existing expression generation
- Update expression uses existing expression generation
- Must handle potential side effects in all three

### Variable Scope Tracking

CompilationContext must track:
- Variables declared in init (loop scope)
- Variables declared in body (iteration scope)
- Variable visibility after loop completes
- Shadowing of outer variables

---

## Test Plan

### Phase 1 Tests (Basic For Loops)

1. testBasicCountingLoop - for (let i = 0; i < 10; i++)
2. testCountingDown - for (let i = 10; i > 0; i--)
3. testLoopWithComplexBody - Multiple statements in body
4. testLoopNeverExecutes - Initial condition false
5. testLoopSingleIteration - Executes once
6. testLoopWithReturn - Return statement in body
7. testLoopWithVarDecl - Variable declaration in body
8. testLoopGreaterThan - i > 0 condition
9. testLoopLessThanOrEqual - i <= 10 condition
10. testLoopNotEqual - i != 10 condition

### Phase 2 Tests (Optional Components)

11. testLoopNoInit - for (; i < 10; i++)
12. testLoopNoTest - for (let i = 0; ; i++) { if (i > 10) break; }
13. testLoopNoUpdate - for (let i = 0; i < 10; )
14. testLoopOnlyBody - for (;;) { if (x) break; }
15. testLoopInitAndBody - for (let i = 0;;)
16. testLoopTestAndBody - for (; i < 10;)
17. testLoopUpdateAndBody - for (;; i++)
18. testEmptyInfiniteLoop - for (;;) {}

### Phase 3 Tests (Break and Continue)

19. testLoopWithBreak - Break in body
20. testLoopWithContinue - Continue in body
21. testBreakInNestedIf - if (condition) break;
22. testContinueInNestedIf - if (condition) continue;
23. testMultipleBreaks - Multiple break points
24. testMultipleContinues - Multiple continue points
25. testBreakAndContinue - Both in same loop
26. testBreakFirstIteration - Immediate break
27. testContinueAllIterations - Continue in every iteration
28. testUnreachableAfterBreak - Code after unconditional break

### Phase 4 Tests (Complex Init/Update)

29. testMultipleVariablesInit - for (let i = 0, j = 10; ...)
30. testMultipleUpdates - for (...; i++, j--)
31. testComplexInitExpression - for (let i = compute(); ...)
32. testComplexUpdateExpression - for (...; i = i * 2 + 1)
33. testUpdateWithMethodCall - for (...; arr.push(i++))
34. testDependentUpdates - for (...; i++, j = i * 2)
35. testMixedTypes - for (let i: int = 0, d: double = 0.0; ...)

### Phase 5 Tests (Nested Loops)

36. testTwoLevelNested - Nested loop 2 deep
37. testThreeLevelNested - Nested loop 3 deep
38. testBreakInner - Break inner loop
39. testBreakOuter - Break from within inner loop
40. testContinueInner - Continue inner loop
41. testContinueOuter - Continue from within inner loop
42. testSharedVariables - Inner loop uses outer variable
43. testInnerModifiesOuter - Inner loop modifies outer variable

### Phase 6 Tests (Labeled Break/Continue)

44. testLabeledBreak - break outer;
45. testLabeledContinue - continue outer;
46. testMultipleLabels - Multiple labeled loops
47. testLabeledBreakDeep - Deep nesting with labeled break

### Phase 7 Tests (Edge Cases)

48. testSideEffectInTest - for (...; (x++) < 10; ...)
49. testSideEffectInUpdate - for (...; i += compute())
50. testReturnInLoop - Return statement in body
51. testThrowInLoop - Throw statement in body
52. testNestedIfInLoop - If statement in loop body
53. testEmptyLoopBody - for (...) {}
54. testLoopVariableAfterLoop - Scoping test
55. testFloatingPointLoop - for (let d = 0.0; d < 1.0; d += 0.1)
56. testByteOverflow - for (let b: byte = 120; b < 200; b++)
57. testVeryLargeCount - for (let i = 0; i < 1000000; i++)

---

## Success Criteria

- [ ] All 7 phases implemented
- [ ] 57+ comprehensive tests covering all edge cases
- [ ] Proper stack map frame generation at loop entry and exit
- [ ] Support for break and continue statements
- [ ] Support for labeled break and continue
- [ ] Correct backward jump generation
- [ ] Proper variable scoping (loop variables not visible after loop)
- [ ] Support for nested loops (5+ levels deep)
- [ ] Support for all optional components (init, test, update)
- [ ] Integration with expression generator for all expressions
- [ ] Complete documentation
- [ ] All tests passing
- [ ] Javadoc builds successfully

---

## Known Limitations (Before Implementation)

1. **For-in/For-of Loops:** This plan covers traditional for loops only, not for-in or for-of
2. **Optimization:** Initial implementation may not optimize empty loops or loop invariants
3. **Wide Jumps:** May not handle very large loop bodies (>32KB) initially (need goto_w)
4. **Const in Loop:** Loop variables declared as const that are modified may need special handling
5. **Destructuring:** Destructuring in init may not be supported initially
6. **Iterator Protocol:** For-of loops require iterator protocol implementation

---

## Implementation Checklist

### Code Generation
- [ ] Create `ForStatementGenerator.java`
- [ ] Create `BreakStatementGenerator.java`
- [ ] Create `ContinueStatementGenerator.java`
- [ ] Implement `generate()` method for for loops
- [ ] Handle optional init, test, update components
- [ ] Handle break and continue statements
- [ ] Implement labeled break and continue
- [ ] Generate proper backward jumps
- [ ] Implement stack map frame generation at loop entry
- [ ] Add break/continue label stack to CompilationContext

### Integration
- [ ] Add ForStmt case to StatementGenerator dispatch
- [ ] Add BreakStmt case to StatementGenerator dispatch
- [ ] Add ContinueStmt case to StatementGenerator dispatch
- [ ] Ensure expression generator works for init/test/update
- [ ] Handle VarDecl in init
- [ ] Track loop variable scopes
- [ ] Handle nested loops correctly
- [ ] Add debug/line number information

### Testing
- [ ] Create test directory `forstmt/`
- [ ] Create `TestCompileAstForStmtBasic.java`
- [ ] Create `TestCompileAstForStmtOptional.java`
- [ ] Create `TestCompileAstForStmtBreakContinue.java`
- [ ] Create `TestCompileAstForStmtComplex.java`
- [ ] Create `TestCompileAstForStmtNested.java`
- [ ] Create `TestCompileAstForStmtLabeled.java`
- [ ] Create `TestCompileAstForStmtEdgeCases.java`
- [ ] Add Phase 1 tests (basic for loops)
- [ ] Add Phase 2 tests (optional components)
- [ ] Add Phase 3 tests (break/continue)
- [ ] Add Phase 4 tests (complex init/update)
- [ ] Add Phase 5 tests (nested loops)
- [ ] Add Phase 6 tests (labeled break/continue)
- [ ] Add Phase 7 tests (edge cases)
- [ ] Verify all tests pass
- [ ] Verify javadoc builds

---

## References

- **JVM Specification:** Chapter 3.10 - Compiling Switches (for label/jump patterns)
- **JVM Specification:** Chapter 3 - Control Transfer Instructions
- **JVM Specification:** Chapter 4.10.1 - Stack Map Frame Verification
- **JavaScript Specification:** ECMAScript Section 13.7.4 - The for Statement
- **TypeScript Specification:** Section 5.5 - For Statements
- **Java Language Specification:** Section 14.14 - The for Statement
- **Existing Implementation:** IfStatementGenerator.java (for control flow patterns)
- **Test Reference:** TestCompileAstIfStmt*.java (for test structure)

---

## Notes

- For loops require **backward jumps** to loop entry point
- **Stack must be empty** at loop entry for backward jump verification
- **Stack map frames required** at loop entry and after loop (merge points)
- Break and continue require **label stack** in compilation context
- **Labeled statements** require mapping labels to loop entry/exit points
- Loop variables have **loop scope** (not visible after loop ends in for statement)
- **Nested loops** require nested label stacks for break/continue
- **iinc instruction** can optimize simple i++ and i-- updates
- Empty test means **infinite loop** - must have break to exit
- Multiple expressions in init/update separated by comma operator
- JVM verifier is strict about **consistent stack state** at backward jump targets

---

## Implementation Status

### Completed (2026-01-18)

✅ **Core Implementation:**
- Created `ForStatementGenerator.java` with full for loop code generation
- Created `BreakStatementGenerator.java` for break statement support
- Created `ContinueStatementGenerator.java` for continue statement support
- Updated `CompilationContext.java` with break/continue label stacks and patch tracking
- Updated `StatementGenerator.java` to dispatch ForStmt, BreakStmt, ContinueStmt
- Updated `VariableAnalyzer.java` to recursively analyze for loop variable declarations

✅ **Test Suite Created:**
- `TestCompileAstForStmtBasic.java` - 10 tests (Phase 1: Basic for loops)
- `TestCompileAstForStmtOptional.java` - 11 tests (Phase 2: Optional components)
- `TestCompileAstForStmtBreakContinue.java` - 13 tests (Phase 3: Break/continue)
- `TestCompileAstForStmtComplex.java` - 13 tests (Phase 4: Complex init/update)
- `TestCompileAstForStmtNested.java` - 15 tests (Phase 5: Nested loops)
- `TestCompileAstForStmtEdgeCases.java` - 25 tests (Phase 7: Edge cases)
- **Total: 87 comprehensive tests covering phases 1-5 and 7**

### In Progress

🟡 **Debugging:**
- JVM VerifyError: "Inconsistent stackmap frames at branch target"
- Issue appears to be related to stack map frame generation for backward jumps
- Bytecode structure is correct, but stack map verification is failing
- Likely needs adjustment to how loop entry point stack frames are generated

### Not Yet Implemented

🔴 **Phase 6: Labeled Break/Continue** (generators throw exception)
- Labeled loops
- Labeled break statements
- Labeled continue statements

### Known Issues

1. **VerifyError on Loop Execution:** Tests compile but fail at runtime with stack map frame inconsistency
   - Error occurs at the goto instruction (backward jump to loop test)
   - Stack is empty at goto, but stack map frame expects integer at target
   - May need special handling for backward jump verification in StackMapGenerator

### Next Steps

1. Debug and fix the VerifyError in for loop bytecode generation
2. Investigate StackMapGenerator handling of backward jumps
3. Test all 87 test cases once bytecode issue is resolved
4. Implement Phase 6 (labeled loops) if needed
5. Update documentation with final test results

---

## Future Work

1. Implement for-in loops (iterate over object keys)
2. Implement for-of loops (iterate over iterables)
3. Add loop optimization (dead code elimination, loop invariant code motion)
4. Add support for very large loop bodies (wide gotos)
5. Implement loop unrolling for constant small counts
6. Add const loop variable validation
7. Implement destructuring in loop init
8. Add support for async for-await-of loops
