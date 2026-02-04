# While Statement Implementation Plan

## Overview

This document outlines the implementation plan for supporting while loops in TypeScript to JVM bytecode compilation. While loops provide iterative execution of code blocks based on a condition that is tested before each iteration.

**Current Status:** 🟢 **COMPLETED**

**Syntax:**
```typescript
while (condition) { body }
while (i < 10) { /* body */ }
while (true) { /* infinite loop */ }
```

**Implementation File:** `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/WhileStatementProcessor.java` (to be created)

**Test File:** `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/whilestmt/TestCompileAstWhileStmt*.java` (to be created)

**AST Definition:** [Swc4jAstWhileStmt.java](../../../../src/main/java/com/caoccao/javet/swc4j/ast/stmt/Swc4jAstWhileStmt.java)

---

## While Statement Fundamentals

### Statement Semantics

A while statement has two components:
1. **Test** - Condition to evaluate before each iteration (continues if true/truthy)
2. **Body** - Statement(s) executed in each iteration

Key difference from for loops:
- No initialization section (handled before the loop)
- No update section (handled inside the body)
- Simpler structure, easier to implement

### JavaScript/TypeScript Behavior

```typescript
// Standard while loop
let i = 0;
while (i < 10) {
  console.log(i);
  i++;
}

// Infinite loop
while (true) {
  if (condition) break;
}

// Never executes
while (false) {
  // unreachable
}

// With break and continue
let i = 0;
while (i < 10) {
  i++;
  if (i % 2 === 0) continue;
  if (i > 7) break;
  process(i);
}

// Complex condition
while (x < 100 && y > 0) {
  x = compute(x);
  y--;
}
```

### JVM Bytecode Strategy

While loops use backward jumps to repeat execution, similar to for loops but simpler.

**Basic While Loop Pattern:**
```java
// While: while (test) { body }

TEST_LABEL:               // Loop entry point
[test code]               // Evaluate condition
ifeq END_LABEL            // Exit if false
[body statements]         // Execute body
goto TEST_LABEL           // Jump back to test (backward jump)
END_LABEL:                // Break target
```

**Infinite Loop Pattern (while(true)):**
```java
// While: while (true) { body }

LOOP_LABEL:
[body statements]
goto LOOP_LABEL           // Unconditional jump back
// Unreachable without break
```

**Stack Map Frames Required:**
- At TEST_LABEL (loop entry point)
- At END_LABEL (after loop exits)

---

## Implementation Phases

### Phase 1: Basic While Loops (Priority: HIGH)

Support simple while loops with condition and body.

**Scope:**
- While loops with test condition
- Simple test conditions (i &lt; 10, x &gt; 0)
- Block statement body
- Local variable usage within loop
- Basic control flow

**Example Bytecode:**
```
// While: let i = 0; while (i < 10) { sum += i; i++; }

iconst_0              // Initialize i (before loop)
istore_1              // Store to local var 1 (i)
TEST_LABEL:           // Loop entry
iload_1               // Load i
bipush 10             // Load 10
if_icmpge END_LABEL   // Exit if i >= 10
iload_2               // Load sum
iload_1               // Load i
iadd                  // sum + i
istore_2              // Store to sum
iinc 1, 1             // i++
goto TEST_LABEL       // Jump back
END_LABEL:
```

**Test Coverage:**
1. Basic counting loop with condition
2. Loop with complex body
3. Loop with multiple statements in body
4. Loop with different comparison operators (&gt;, &gt;=, &lt;, &lt;=, ==, !=)
5. Loop that never executes (condition initially false)
6. Loop that executes once
7. Loop with local variable declarations in body
8. Loop with return statement in body
9. Loop modifying the test variable
10. Loop with multiple variables

---

### Phase 2: Infinite Loops (Priority: HIGH)

Support infinite while loops (while(true)).

**Scope:**
- Loops with constant true condition
- Loops with no natural exit (require break)
- Unconditional backward jumps
- Detection of unreachable code after infinite loop without break

**Example Bytecode:**
```
// While: while (true) { if (x > 100) break; x++; }

LOOP_LABEL:
iload_1               // Load x
bipush 100
if_icmple CONTINUE    // Continue if x <= 100
goto END_LABEL        // Implicit break
CONTINUE:
iinc 1, 1             // x++
goto LOOP_LABEL       // Unconditional jump back
END_LABEL:
```

**Test Coverage:**
1. while(true) with break
2. while(true) with conditional break
3. while(true) with return
4. while(true) with throw
5. while(1) (truthy constant)
6. while(true) without exit (infinite loop detection)
7. Code after while(true) without break (unreachable)

---

### Phase 3: Break and Continue (Priority: HIGH)

Support break and continue statements within while loops.

**Scope:**
- Break statement (exit loop immediately)
- Continue statement (skip to next iteration)
- Unlabeled break/continue (innermost loop)
- Multiple break/continue in same loop
- Break/continue in nested if within loop
- Break/continue in nested blocks

**Example Bytecode:**
```
// While: while (i < 10) { if (i == 5) continue; if (i == 8) break; i++; }

TEST_LABEL:
iload_1
bipush 10
if_icmpge END_LABEL
iload_1
iconst_5
if_icmpne check_break
goto TEST_LABEL       // continue: jump to test
check_break:
iload_1
bipush 8
if_icmpne body_end
goto END_LABEL        // break: jump to end
body_end:
iinc 1, 1
goto TEST_LABEL
END_LABEL:
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
9. Continue in all iterations
10. Unreachable code after unconditional break

---

### Phase 4: Complex Conditions (Priority: MEDIUM)

Support complex boolean expressions in test condition.

**Scope:**
- Boolean operators (&&, ||, !)
- Comparison chains
- Method calls in condition
- Side effects in condition
- Short-circuit evaluation
- Truthy/falsy values

**Example Bytecode:**
```
// While: while (i < 10 && j > 0) { }

TEST_LABEL:
iload_1               // Load i
bipush 10
if_icmpge END_LABEL   // Exit if i >= 10
iload_2               // Load j
ifle END_LABEL        // Exit if j <= 0
// body
goto TEST_LABEL
END_LABEL:
```

**Test Coverage:**
1. while (a && b)
2. while (a || b)
3. while (!condition)
4. while (a && b || c)
5. while (a &lt; b && c &lt; d)
6. while (method())
7. while (x++ &lt; 10) - side effect in test
8. while (array[i] != null)
9. Complex nested boolean expressions
10. Short-circuit evaluation verification

---

### Phase 5: Nested Loops (Priority: MEDIUM)

Support nested while loops.

**Scope:**
- While loop inside while loop
- Multiple levels of nesting (3+ deep)
- Break/continue in nested loops
- Shared variables between loops
- Mixed nesting (while in for, for in while)

**Example Bytecode:**
```
// While: let i = 0; while (i < 3) { let j = 0; while (j < 3) { sum++; j++; } i++; }

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
    iinc 3, 1         // sum++
    iinc 2, 1         // j++
    goto inner_test
  inner_end:
  iinc 1, 1           // i++
  goto outer_test
outer_end:
```

**Test Coverage:**
1. Two-level nested while
2. Three-level nested while
3. Four+ level deep nesting
4. Break in outer loop
5. Break in inner loop
6. Continue in outer loop
7. Continue in inner loop
8. Shared variables between loops
9. Inner loop modifies outer loop variable
10. Mixed nesting (while in for, for in while)

---

### Phase 6: Labeled Break and Continue (Priority: LOW)

Support labeled break and continue statements (reuse implementation from for loops).

**Scope:**
- Label declarations on while loops
- Labeled break (break to specific loop)
- Labeled continue (continue specific loop)
- Break to outer loop from inner loop
- Continue outer loop from inner loop

**Example:**
```typescript
outer: while (i < 10) {
  while (j < 10) {
    if (i * j > 50) break outer;    // Break outer loop
    if (j === 5) continue outer;     // Continue outer loop
    j++;
  }
  i++;
}
```

**Test Coverage:**
1. Labeled break to outer loop
2. Labeled continue to outer loop
3. Multiple labeled loops
4. Label on while loop with break
5. Label on while loop with continue
6. Deeply nested with labeled break/continue
7. Mixed labeled and unlabeled
8. Label on single loop

---

### Phase 7: Edge Cases and Advanced Scenarios (Priority: LOW)

Handle complex scenarios and edge cases.

**Scope:**
- Side effects in test condition
- Variables declared in body scope
- Return statements in loop body
- Throw statements in loop body
- Nested control flow (if/switch in loop)
- Very large loop bodies
- Empty body loops
- Complex stack map frame scenarios
- Do-while equivalence patterns

**Test Coverage:**
1. Side effects in test (x++ &gt; 10)
2. Return in loop body
3. Throw in loop body
4. Nested if/else in loop
5. Switch statement in loop
6. Empty loop body while(condition) {}
7. Multiple returns in loop
8. Complex stack map scenarios
9. while(true) { if(...) break; } pattern
10. Optimization opportunities

---

## Edge Cases and Special Scenarios

### Control Flow Edge Cases

1. **Empty Loop**
   ```typescript
   while (i < 10) { }  // Valid, no body (infinite if i never changes)
   ```

2. **Infinite Loop**
   ```typescript
   while (true) { }                       // Infinite, no break
   while (true) { if (x) break; }         // Infinite with conditional break
   ```

3. **Never Executes**
   ```typescript
   while (false) { }                      // Never executes
   let i = 10; while (i < 0) { }          // Condition initially false
   ```

4. **Single Iteration**
   ```typescript
   let i = 0; while (i < 1) { i++; }      // Executes once
   ```

5. **Break in First Iteration**
   ```typescript
   while (condition) { break; }           // Only test executes
   ```

6. **Continue in All Iterations**
   ```typescript
   while (i < 10) { i++; continue; }      // Body effectively just i++
   ```

7. **Return in First Iteration**
   ```typescript
   while (condition) { return value; }    // Returns immediately
   ```

8. **Multiple Breaks/Continues**
   ```typescript
   while (i < 10) {
     if (i % 2 === 0) continue;
     if (i > 5) break;
     process(i);
     i++;
   }
   ```

### Test Condition Edge Cases

9. **Constant True**
   ```typescript
   while (true) { if (x > 100) break; }
   ```

10. **Constant False**
    ```typescript
    while (false) { /* unreachable */ }
    ```

11. **Complex Boolean Expression**
    ```typescript
    while (i < 10 && j > 0 || k === 5) { }
    ```

12. **Test with Side Effects**
    ```typescript
    while ((x++) < 10) { }  // x increments each test
    ```

13. **Short-Circuit Evaluation in Test**
    ```typescript
    while (i < 10 && check(i)) { i++; }  // check() not called if i >= 10
    ```

14. **Method Call in Test**
    ```typescript
    while (hasNext()) { process(); }  // hasNext() called each iteration
    ```

15. **Negated Condition**
    ```typescript
    while (!(i >= 10)) { i++; }
    ```

16. **Null/Undefined Check**
    ```typescript
    while (obj != null) { obj = obj.next; }
    ```

17. **Array/String Length**
    ```typescript
    while (i < arr.length) { /* ... */ i++; }
    ```

18. **Truthy/Falsy Values**
    ```typescript
    while (count) { count--; }  // Loops while count is truthy
    ```

### Body Edge Cases

19. **Empty Body**
    ```typescript
    while (condition) { }   // No statements
    ```

20. **Single Statement Body**
    ```typescript
    while (condition) process();  // No braces
    ```

21. **Block Statement Body**
    ```typescript
    while (condition) { const x = compute(); process(x); }
    ```

22. **Variable Declaration in Body**
    ```typescript
    while (i < 10) {
      const x = i * 2;  // New scope each iteration
      i++;
    }
    ```

23. **Return in Body**
    ```typescript
    while (condition) { return value; }
    ```

24. **Multiple Returns in Body**
    ```typescript
    while (condition) {
      if (a) return x;
      return y;
    }
    ```

25. **Throw in Body**
    ```typescript
    while (condition) { throw new Error(); }
    ```

26. **Nested Control Flow**
    ```typescript
    while (i < 10) {
      if (i % 2 === 0) { continue; }
      switch (i) {
        case 1: break;
        case 3: return;
      }
      i++;
    }
    ```

### Variable Modification Edge Cases

27. **Test Variable Modified in Body**
    ```typescript
    let i = 0;
    while (i < 10) { i += 2; }
    ```

28. **Test Variable Not Modified (Infinite)**
    ```typescript
    let i = 0;
    while (i < 10) { process(); }  // Infinite if no break
    ```

29. **Multiple Variables in Condition**
    ```typescript
    while (i < 10 && j > 0) { i++; j--; }
    ```

30. **External Variable Modified**
    ```typescript
    while (arr.length > 0) { arr.pop(); }
    ```

31. **Closure Over Loop Variable**
    ```typescript
    let i = 0;
    while (i < 3) {
      const f = () => i;  // Each f captures current i
      i++;
    }
    ```

### Nested Loop Edge Cases

32. **Two-Level Nesting**
    ```typescript
    let i = 0;
    while (i < 3) {
      let j = 0;
      while (j < 3) { j++; }
      i++;
    }
    ```

33. **Deep Nesting (5+ levels)**
    ```typescript
    while (a) { while (b) { while (c) { while (d) { while (e) { } } } } }
    ```

34. **Break in Nested Loop**
    ```typescript
    while (i < 10) {
      while (j < 10) {
        if (j === 5) break;  // Breaks inner loop only
        j++;
      }
      i++;
    }
    ```

35. **Continue in Nested Loop**
    ```typescript
    while (i < 10) {
      while (j < 10) {
        if (j === 5) continue;  // Continues inner loop only
        j++;
      }
      i++;
    }
    ```

36. **Shared Variables**
    ```typescript
    let sum = 0;
    while (i < 10) {
      while (j < i) { sum++; j++; }
      i++;
    }
    ```

37. **Inner Modifies Outer Variable**
    ```typescript
    while (i < 10) {
      while (j < 10) {
        i++;  // Modifies outer loop variable
        j++;
      }
    }
    ```

38. **Labeled Break to Outer**
    ```typescript
    outer: while (i < 10) {
      while (j < 10) {
        if (i * j > 50) break outer;
        j++;
      }
      i++;
    }
    ```

39. **Labeled Continue to Outer**
    ```typescript
    outer: while (i < 10) {
      while (j < 10) {
        if (j === 5) continue outer;
        j++;
      }
      i++;
    }
    ```

### Type Edge Cases

40. **Different Numeric Types**
    ```typescript
    let i: byte = 0; while (i < 10) { i++; }        // byte
    let i: short = 0; while (i < 100) { i++; }      // short
    let i: int = 0; while (i < 1000) { i++; }       // int
    let i: long = 0; while (i < 10000) { i++; }     // long
    ```

41. **Floating Point Loop**
    ```typescript
    let d: double = 0.0; while (d < 1.0) { d += 0.1; }
    let f: float = 0.0; while (f < 1.0) { f += 0.1; }
    ```

42. **Boolean Condition**
    ```typescript
    let done: boolean = false;
    while (!done) { done = check(); }
    ```

43. **Type Widening in Body**
    ```typescript
    let i: int = 0;
    while (i < 10) { i = (long) i + 1; }  // Type mismatch
    ```

44. **Overflow/Underflow**
    ```typescript
    let i: byte = 127;
    while (i < 200) { i++; }  // byte overflow
    ```

### Stack Map Edge Cases

45. **Complex Merge Points**
    ```typescript
    while (condition) {
      let x: int;
      if (flag) {
        x = 1;
      } else {
        x = 2;
      }
      // Merge point with x definitely assigned
    }
    ```

46. **Multiple Entry Points (continue targets)**
    ```typescript
    while (i < 10) {
      if (i % 2 === 0) continue;
      if (i % 3 === 0) continue;
      process(i);
      i++;
    }
    ```

47. **Backward Jump Stack State**
    ```typescript
    while (i < 10) {
      let x = compute();  // Stack must be consistent across iterations
      i++;
    }
    ```

### Integration Edge Cases

48. **While Loop in Method**
    ```typescript
    function sum(n: int): int {
      let result = 0;
      let i = 0;
      while (i < n) {
        result += i;
        i++;
      }
      return result;
    }
    ```

49. **While Loop in Constructor**
    ```typescript
    constructor() {
      let i = 0;
      while (i < 10) {
        this.array.push(i);
        i++;
      }
    }
    ```

50. **While Loop with Field Access**
    ```typescript
    while (this.index < this.length) {
      this.array[this.index] = this.index;
      this.index++;
    }
    ```

51. **While Loop with Array Access**
    ```typescript
    let i = 0;
    while (i < arr.length) {
      sum += arr[i];
      i++;
    }
    ```

52. **While Loop with Method Calls**
    ```typescript
    while (hasMore()) {
      this.process(getNext());
    }
    ```

53. **Nested If in While Loop**
    ```typescript
    while (i < 10) {
      if (i % 2 === 0) {
        even++;
      } else {
        odd++;
      }
      i++;
    }
    ```

54. **While Loop in If Statement**
    ```typescript
    if (flag) {
      while (i < 10) { i++; }
    }
    ```

55. **Multiple Sequential Loops**
    ```typescript
    let i = 0;
    while (i < 10) { sumA += i; i++; }
    let j = 0;
    while (j < 10) { sumB += j; j++; }
    ```

### Performance Edge Cases

56. **Very Large Loop Count**
    ```typescript
    let i = 0;
    while (i < 1000000) { i++; }
    ```

57. **Very Large Loop Body**
    ```typescript
    while (condition) {
      // Thousands of lines of code
    }
    ```

58. **Empty Loop (optimization opportunity)**
    ```typescript
    let i = 0;
    while (i < 1000000) { i++; }  // Could optimize
    ```

59. **Loop Invariant Code Motion**
    ```typescript
    while (i < 10) {
      const x = compute();  // Same value each iteration if compute() is pure
      i++;
    }
    ```

### Bytecode Edge Cases

60. **Long Jump Offsets (&gt;32KB)**
    ```typescript
    while (condition) {
      // Very large body requiring wide goto
    }
    ```

61. **Backward Jump Distance**
    ```typescript
    // Ensure backward jump offset calculated correctly
    ```

62. **Multiple Gotos to Same Label**
    ```typescript
    while (i < 10) {
      if (i % 2 === 0) continue;
      if (i % 3 === 0) continue;
      if (i % 5 === 0) continue;
      i++;
    }
    ```

### Error Handling Edge Cases

63. **Exception in Test**
    ```typescript
    while (mayThrow()) { }
    ```

64. **Exception in Body**
    ```typescript
    while (condition) { throw new Error(); }
    ```

65. **Try-Catch in Loop**
    ```typescript
    while (i < 10) {
      try { risky(); } catch (e) { handle(e); }
      i++;
    }
    ```

66. **Loop in Try-Catch**
    ```typescript
    try {
      while (i < 10) { risky(); i++; }
    } catch (e) { }
    ```

### Special Statement Edge Cases

67. **Unreachable Code After Break**
    ```typescript
    while (condition) {
      break;
      console.log("unreachable");  // Dead code
    }
    ```

68. **Unreachable Code After Continue**
    ```typescript
    while (condition) {
      continue;
      console.log("unreachable");  // Dead code
    }
    ```

69. **Unreachable Code After Return**
    ```typescript
    while (condition) {
      return value;
      console.log("unreachable");  // Dead code
    }
    ```

70. **All Iterations Return**
    ```typescript
    while (condition) {
      return value;  // Always returns on first iteration
    }
    // Unreachable
    ```

71. **Code After Infinite Loop Without Break**
    ```typescript
    while (true) { }
    console.log("unreachable");  // Dead code
    ```

### Conversion Pattern Edge Cases

72. **For-to-While Equivalence**
    ```typescript
    // for (let i = 0; i < 10; i++) { body }
    // Equivalent to:
    let i = 0;
    while (i < 10) { body; i++; }
    ```

73. **Do-While Pattern**
    ```typescript
    // do { body } while (condition)
    // Can be expressed as:
    body;
    while (condition) { body; }
    ```

74. **Sentinel Value Pattern**
    ```typescript
    let value = read();
    while (value !== SENTINEL) {
      process(value);
      value = read();
    }
    ```

75. **Iterator Pattern**
    ```typescript
    let iterator = getIterator();
    while (iterator.hasNext()) {
      process(iterator.next());
    }
    ```

### Miscellaneous Edge Cases

76. **Comments and Whitespace**
    ```typescript
    while /* comment */ (condition) {
      // Should ignore comments
    }
    ```

77. **Unicode in Variables**
    ```typescript
    let 変数 = 0;
    while (変数 < 10) { 変数++; }
    ```

78. **Very Long Variable Names**
    ```typescript
    let veryLongVariableNameThatExceedsNormalLength = 0;
    while (veryLongVariableNameThatExceedsNormalLength < 10) { }
    ```

79. **Reserved Keywords as Labels**
    ```typescript
    label: while (condition) { break label; }
    ```

80. **Ternary in Test**
    ```typescript
    while (flag ? i < 10 : i < 20) { i++; }
    ```

81. **Assignment in Test**
    ```typescript
    while ((value = read()) !== null) { process(value); }
    ```

82. **Increment in Test**
    ```typescript
    while (++i < 10) { }  // i incremented before test
    while (i++ < 10) { }  // i incremented after test
    ```

83. **Multiple While Loops Same Variable**
    ```typescript
    let i = 0;
    while (i < 10) { i++; }
    i = 0;  // Reset
    while (i < 10) { i++; }
    ```

84. **While Loop Modifying Multiple Variables**
    ```typescript
    while (i < 10 && j > 0) {
      i += 2;
      j -= 3;
    }
    ```

85. **Complex Object State in Condition**
    ```typescript
    while (obj.property.method() > threshold) {
      update(obj);
    }
    ```

---

## Bytecode Instruction Reference

### Loop-Specific Instructions

**Unconditional Jump (backward):**
- `goto <label>` (0xA7) - Jump back to test label
- `goto_w <label>` (0xC8) - Wide goto for long offsets (&gt;32KB)

**Conditional Jumps:**
- `ifeq <label>` (0x99) - Jump if false (exit loop)
- `if_icmplt <label>` (0xA1) - Jump if less than (continue loop)
- `if_icmpge <label>` (0xA2) - Jump if greater or equal (exit loop)
- etc.

**Stack Map Frames:**
- Required at loop entry (test label) for JVM verification
- Required at end label (after loop)
- Must handle backward jump verification

---

## AST Structure

### Swc4jAstWhileStmt Components

```java
public class Swc4jAstWhileStmt {
    ISwc4jAstExpr test;     // Test condition (required)
    ISwc4jAstStmt body;     // Body statement (required)
}
```

### Related AST Types

- **ISwc4jAstExpr** - Test is an expression
  - Swc4jAstBinExpr - Binary comparison
  - Swc4jAstUnaryExpr - Negation, etc.
  - Swc4jAstIdent - Variable reference
  - Swc4jAstLit - Literal (true, false, numbers)
  - Swc4jAstCallExpr - Method call
- **ISwc4jAstStmt** - Body can be any statement
  - Swc4jAstBlockStmt - Block with multiple statements
  - Swc4jAstExprStmt - Single expression statement
  - Swc4jAstBreakStmt - Break statement
  - Swc4jAstContinueStmt - Continue statement
  - Swc4jAstReturnStmt - Return statement
  - Swc4jAstIfStmt - If statement (nested)
  - Swc4jAstWhileStmt - Nested while loop
  - Swc4jAstForStmt - Nested for loop

---

## Implementation Strategy

### Code Generation Algorithm

```java
public static void generate(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        Swc4jAstWhileStmt whileStmt,
        ReturnTypeInfo returnTypeInfo,
        CompilationContext context,
        ByteCodeCompilerOptions options) {

    // 1. Mark test label (loop entry point)
    int testLabel = code.getCurrentOffset();

    // 2. Generate test condition
    ISwc4jAstExpr testExpr = whileStmt.getTest();

    // Try to generate direct conditional jump (like javac does)
    if (testExpr instanceof Swc4jAstBinExpr binExpr) {
        boolean generated = generateDirectConditionalJump(code, cp, binExpr, context, options);
        if (!generated) {
            // Fallback: generate boolean expression and use ifeq
            ExpressionProcessor.generate(code, cp, testExpr, null, context, options);
            code.ifeq(0); // Placeholder
        }
    } else {
        // Non-binary expression: generate as boolean and use ifeq
        ExpressionProcessor.generate(code, cp, testExpr, null, context, options);
        code.ifeq(0); // Placeholder
    }

    int condJumpOffsetPos = code.getCurrentOffset() - 2;
    int condJumpOpcodePos = code.getCurrentOffset() - 3;

    // 3. Create label info for break and continue
    CompilationContext.LoopLabelInfo breakLabel = new CompilationContext.LoopLabelInfo(labelName);
    CompilationContext.LoopLabelInfo continueLabel = new CompilationContext.LoopLabelInfo(labelName);

    // Push labels onto stack before generating body
    context.pushBreakLabel(breakLabel);
    context.pushContinueLabel(continueLabel);

    // 4. Generate body
    StatementProcessor.generate(code, cp, whileStmt.getBody(), returnTypeInfo, context, options);

    // 5. Mark continue label (continue jumps here, which is same as test)
    continueLabel.setTargetOffset(testLabel);

    // 6. Jump back to test (backward jump)
    code.gotoLabel(0); // Placeholder
    int backwardGotoOffsetPos = code.getCurrentOffset() - 2;
    int backwardGotoOpcodePos = code.getCurrentOffset() - 3;

    // Calculate and patch the backward jump offset
    int backwardGotoOffset = testLabel - backwardGotoOpcodePos;
    code.patchShort(backwardGotoOffsetPos, backwardGotoOffset);

    // 7. Mark end label (break target)
    int endLabel = code.getCurrentOffset();
    breakLabel.setTargetOffset(endLabel);

    // 8. Patch all break statements to jump to end label
    for (CompilationContext.LoopLabelInfo.PatchInfo patchInfo : breakLabel.getPatchPositions()) {
        int offset = endLabel - patchInfo.getOpcodePos();
        code.patchShort(patchInfo.getOffsetPos(), offset);
    }

    // 9. Patch all continue statements to jump to test label
    for (CompilationContext.LoopLabelInfo.PatchInfo patchInfo : continueLabel.getPatchPositions()) {
        int offset = testLabel - patchInfo.getOpcodePos();
        code.patchShort(patchInfo.getOffsetPos(), offset);
    }

    // 10. Patch conditional jump
    int condJumpOffset = endLabel - condJumpOpcodePos;
    code.patchShort(condJumpOffsetPos, condJumpOffset);

    // 11. Pop labels from stack
    context.popBreakLabel();
    context.popContinueLabel();
}
```

### Differences from For Loop Generation

1. **No initialization section** - handled before the while statement
2. **No update section** - handled inside the body
3. **Simpler structure** - only test and body
4. **Continue target** - jumps to test label, not update label
5. **No scope management for loop variables** - while doesn't introduce new scope

### Label Management

Reuse existing label management from for loop implementation:
- Break/continue label stacks in CompilationContext
- Label search for labeled statements
- Patch position tracking

---

## Stack Map Frame Considerations

### Frame Points

Stack map frames needed at:
1. **Loop entry (test label)** - Entry point for each iteration
2. **After loop (end label)** - Merge point after loop exits or breaks

### Frame Merging

```java
// Before loop: locals = {this, n, i}, stack = {}
while (i < n) {
    // At test: locals = {this, n, i}, stack = {}
    let x = compute(i);
    i++;
    // At goto: locals = {this, n, i, x}, stack = {}
}
// After loop: locals = {this, n, i}, stack = {}
```

### Backward Jump Verification

JVM requires consistent stack state at backward jump targets:
- Stack must be empty at test label
- Local variable types must match
- Stack map frame at test label must match state from goto

---

## Integration Points

### Statement Generator

Update `StatementProcessor.java` to dispatch WhileStmt:

```java
if (stmt instanceof Swc4jAstWhileStmt whileStmt) {
    WhileStatementProcessor.generate(code, cp, whileStmt, returnTypeInfo, context, options);
}
```

Break and continue statements are already handled (implemented for for loops).

### Expression Generator Integration

- Test condition uses existing expression generation
- Must handle potential side effects in test
- Reuse direct conditional jump optimization from for loops

### Variable Scope Tracking

- While loops do NOT introduce new scope (unlike for loops with init)
- Variables declared before while remain in scope
- Variables declared inside body have block scope (if in BlockStmt)

### Labeled Statement Support

Reuse existing `LabeledStatementProcessor`:

```java
if (body instanceof Swc4jAstWhileStmt whileStmt) {
    WhileStatementProcessor.generate(code, cp, whileStmt, labelName, returnTypeInfo, context, options);
}
```

---

## Test Plan

### Phase 1 Tests (Basic While Loops)

1. testBasicWhileLoop - while (i &lt; 10) { i++; }
2. testWhileWithSum - while (i &lt; 10) { sum += i; i++; }
3. testWhileComplexBody - Multiple statements in body
4. testWhileNeverExecutes - Condition initially false
5. testWhileSingleIteration - Executes once
6. testWhileWithReturn - Return statement in body
7. testWhileGreaterThan - while (i &gt; 0)
8. testWhileLessThanOrEqual - while (i &lt;= 10)
9. testWhileNotEqual - while (i != 10)
10. testWhileWithVarDecl - Variable declaration in body

### Phase 2 Tests (Infinite Loops)

11. testWhileTrue - while (true) { if (x &gt; 100) break; }
12. testWhileTrueWithReturn - while (true) { return; }
13. testWhileConstantTruthy - while (1) { }
14. testWhileTrueConditionalBreak - Conditional exit
15. testInfiniteLoopDetection - No exit path

### Phase 3 Tests (Break and Continue)

16. testWhileWithBreak - Break in body
17. testWhileWithContinue - Continue in body
18. testBreakInNestedIf - if (condition) break;
19. testContinueInNestedIf - if (condition) continue;
20. testMultipleBreaks - Multiple break points
21. testMultipleContinues - Multiple continue points
22. testBreakAndContinue - Both in same loop
23. testBreakFirstIteration - Immediate break
24. testContinueAllIterations - Continue in every iteration

### Phase 4 Tests (Complex Conditions)

25. testWhileAndCondition - while (a && b)
26. testWhileOrCondition - while (a || b)
27. testWhileNegated - while (!condition)
28. testWhileMethodCall - while (hasNext())
29. testWhileSideEffect - while ((x++) &lt; 10)
30. testWhileShortCircuit - while (a && expensive())
31. testWhileComplexBoolean - while (a &lt; b && c &gt; d || e)

### Phase 5 Tests (Nested Loops)

32. testTwoLevelNestedWhile - Nested while 2 deep
33. testThreeLevelNestedWhile - Nested while 3 deep
34. testBreakInner - Break inner while
35. testBreakOuter - Break from inner to outer
36. testContinueInner - Continue inner while
37. testContinueOuter - Continue from inner to outer
38. testSharedVariables - Loops share variables
39. testInnerModifiesOuter - Inner modifies outer variable
40. testMixedNesting - while in for, for in while

### Phase 6 Tests (Labeled Loops)

41. testLabeledBreak - break outer;
42. testLabeledContinue - continue outer;
43. testMultipleLabels - Multiple labeled whiles
44. testLabeledBreakDeep - Deep nesting with labeled break
45. testLabelOnSingleLoop - Label on non-nested loop

### Phase 7 Tests (Edge Cases)

46. testSideEffectInTest - while ((x++) &gt; 10)
47. testReturnInLoop - Return statement
48. testThrowInLoop - Throw statement
49. testNestedIfInLoop - If statement in body
50. testEmptyBody - while (condition) {}
51. testFloatingPointCondition - while (d &lt; 1.0)
52. testVeryLargeCount - while (i &lt; 1000000)
53. testAssignmentInCondition - while ((x = read()) != null)
54. testIncrementInCondition - while (++i &lt; 10)

---

## Success Criteria

- [x] All 7 phases implemented
- [x] 59 comprehensive test methods covering all edge cases
- [x] Proper stack map frame generation at loop entry and exit
- [x] Support for break and continue statements (unlabeled and labeled)
- [x] Correct backward jump generation
- [x] Support for nested loops (4+ levels deep tested)
- [x] Support for complex boolean conditions
- [x] Integration with expression generator for test expressions
- [x] Reuse of existing break/continue/labeled infrastructure
- [x] Complete documentation
- [x] All tests passing (59/59)
- [x] Javadoc builds successfully

---

## Known Limitations

1. **Do-While Loops:** This plan covers while loops only, not do-while (different AST node)
2. **Optimization:** Initial implementation may not optimize infinite loops or constant conditions
3. **Wide Jumps:** Supported for long back edges and exit paths via `goto_w`
4. **Iterator Protocol:** for-of/for-in loops are separate implementations
5. **Complex Control Flow:** Switch statements inside while may need special handling

---

## Implementation Checklist

### Code Generation
- [x] Create `WhileStatementProcessor.java`
- [x] Implement `generate()` method for while loops
- [x] Handle test condition evaluation
- [x] Reuse break and continue statement generators
- [x] Reuse labeled statement generator
- [x] Generate proper backward jumps
- [x] Implement stack map frame generation at loop entry
- [x] Handle infinite loop patterns (while(true))

### Integration
- [x] Add WhileStmt case to StatementProcessor dispatch
- [x] Update LabeledStatementProcessor for while loops
- [x] Ensure expression generator works for test condition
- [x] Handle nested loops correctly
- [x] Add debug/line number information

### Testing
- [x] Create test directory `whilestmt/`
- [x] Create `TestCompileAstWhileStmtBasic.java` (10 tests)
- [x] Create `TestCompileAstWhileStmtInfinite.java` (7 tests)
- [x] Create `TestCompileAstWhileStmtBreakContinue.java` (9 tests)
- [x] Create `TestCompileAstWhileStmtComplex.java` (7 tests)
- [x] Create `TestCompileAstWhileStmtNested.java` (9 tests)
- [x] Create `TestCompileAstWhileStmtLabeled.java` (8 tests)
- [x] Create `TestCompileAstWhileStmtEdgeCases.java` (9 tests)
- [x] Add Phase 1 tests (basic while loops)
- [x] Add Phase 2 tests (infinite loops)
- [x] Add Phase 3 tests (break/continue)
- [x] Add Phase 4 tests (complex conditions)
- [x] Add Phase 5 tests (nested loops)
- [x] Add Phase 6 tests (labeled loops)
- [x] Add Phase 7 tests (edge cases)
- [x] Verify all tests pass
- [x] Verify javadoc builds

---

## References

- **JVM Specification:** Chapter 3 - Control Transfer Instructions
- **JVM Specification:** Chapter 4.10.1 - Stack Map Frame Verification
- **JavaScript Specification:** ECMAScript Section 13.7.2 - The while Statement
- **TypeScript Specification:** Section 5.3 - While Statements
- **Java Language Specification:** Section 14.12 - The while Statement
- **Existing Implementation:** ForStatementProcessor.java (for control flow patterns)
- **Existing Implementation:** BreakStatementProcessor.java, ContinueStatementProcessor.java
- **Test Reference:** TestCompileAstForStmt*.java (for test structure)

---

## Notes

- While loops are **simpler than for loops** - no init, no update, no scope management
- **Backward jumps** required to loop entry point (test label)
- **Stack must be empty** at loop entry for backward jump verification
- **Continue jumps to test** (not update like for loops)
- Break and continue require **label stack** in compilation context (already implemented)
- **Labeled statements** reuse existing infrastructure from for loops
- Empty test means **syntax error** (unlike for loops where it means infinite)
- JVM verifier is strict about **consistent stack state** at backward jump targets
- Can **reuse most infrastructure** from for loop implementation
- Main difference is **simpler structure** (no init/update sections)

---

## Implementation Estimate

**Effort:** LOW-MEDIUM (most infrastructure already exists from for loops)

**Time Estimate:**
- Code generation: 1-2 hours (simpler than for loops)
- Testing: 2-3 hours (54+ test cases)
- Documentation: 1 hour
- **Total: 4-6 hours**

**Dependencies:**
- ForStatementProcessor (for pattern reference)
- BreakStatementProcessor (already implemented)
- ContinueStatementProcessor (already implemented)
- LabeledStatementProcessor (already implemented)
- CompilationContext label stacks (already implemented)

**Complexity: LOW** - Simpler version of for loops with existing infrastructure
