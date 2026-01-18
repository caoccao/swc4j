# If Statement Implementation Plan

## Overview

This document outlines the implementation plan for supporting if statements in TypeScript to JVM bytecode compilation. If statements provide conditional execution of code blocks based on boolean conditions.

**Current Status:** 🔴 NOT STARTED

**Syntax:**
```typescript
if (condition) { consequent }
if (condition) { consequent } else { alternate }
if (c1) { b1 } else if (c2) { b2 } else { b3 }
```

**Implementation File:** `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/IfStatementGenerator.java` (TO BE CREATED)

**Test File:** `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/TestCompileAstIfStmt.java` (TO BE CREATED)

**AST Definition:** [Swc4jAstIfStmt.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/stmt/Swc4jAstIfStmt.java)

---

## If Statement Fundamentals

### Statement Semantics

An if statement has these components:
1. **Test** - The condition to evaluate (must be boolean)
2. **Consequent** - Statement(s) executed if condition is true
3. **Alternate** - Optional statement(s) executed if condition is false

### JavaScript/TypeScript Behavior

```typescript
// Basic if
if (x > 5) {
  console.log("greater");
}

// If-else
if (x > 5) {
  return "big";
} else {
  return "small";
}

// Else-if chain
if (score >= 90) {
  grade = "A";
} else if (score >= 80) {
  grade = "B";
} else if (score >= 70) {
  grade = "C";
} else {
  grade = "F";
}

// Nested
if (condition1) {
  if (condition2) {
    doSomething();
  }
}
```

### JVM Bytecode Strategy

Unlike conditional expressions, if statements don't leave values on the stack. They control which code blocks execute.

**Basic Pattern (if only):**
```java
// For: if (condition) { body }

evaluate(condition)        // [boolean]
ifeq end_label            // Jump if false (0)
generate(body)            // Execute body
end_label:
                          // Continue execution
```

**If-Else Pattern:**
```java
// For: if (condition) { cons } else { alt }

evaluate(condition)        // [boolean]
ifeq else_label           // Jump if false (0)
generate(cons)            // True branch
goto end_label
else_label:
generate(alt)             // False branch
end_label:
                          // Continue execution
```

**Else-If Chain Pattern:**
```java
// For: if (c1) { b1 } else if (c2) { b2 } else { b3 }

evaluate(c1)              // [boolean]
ifeq else_if_1_label      // Jump if false
generate(b1)              // First body
goto end_label
else_if_1_label:
evaluate(c2)              // [boolean]
ifeq else_label           // Jump if false
generate(b2)              // Second body
goto end_label
else_label:
generate(b3)              // Final else body
end_label:
                          // Continue execution
```

---

## Implementation Phases

### Phase 1: Basic If Statements (Priority: HIGH)

Support simple if statements without else.

**Scope:**
- Basic if with single statement body
- Basic if with block statement body
- Boolean literal conditions
- Comparison conditions (x > 5, x == 10, etc.)
- Logical conditions (&&, ||, !)
- Empty if blocks (valid but do nothing)

**Example Bytecode:**
```
// For: if (x > 5) { y = 10; }

iload_1               // Load x (local var 1)
iconst_5              // Load 5
if_icmple end_label   // Jump if x <= 5
iconst_10             // Load 10
istore_2              // Store to y (local var 2)
end_label:
```

**Test Coverage:**
1. If with true literal condition
2. If with false literal condition
3. If with integer comparison
4. If with boolean variable
5. If with complex condition (&&, ||)
6. If with negated condition (!)
7. Empty if block
8. If with multiple statements in block
9. If with variable declaration in block
10. If with return statement in block

---

### Phase 2: If-Else Statements (Priority: HIGH)

Support if statements with else clause.

**Scope:**
- If-else with both branches having blocks
- If-else with single statement branches
- Empty else blocks
- Different statement types in branches
- Returns in both branches
- Returns in one branch only

**Example Bytecode:**
```
// For: if (flag) { x = 1; } else { x = 2; }

iload_1               // Load flag
ifeq else_label       // Jump if false (0)
iconst_1              // True branch
istore_2              // x = 1
goto end_label
else_label:
iconst_2              // False branch
istore_2              // x = 2
end_label:
```

**Test Coverage:**
1. If-else with both blocks
2. If-else with empty consequent
3. If-else with empty alternate
4. If-else with returns in both branches
5. If-else with return in consequent only
6. If-else with return in alternate only
7. If-else with variable declarations
8. If-else with different types of statements
9. If-else with side effects in condition
10. If-else with method calls in branches

---

### Phase 3: Else-If Chains (Priority: MEDIUM)

Support chained if-else-if statements.

**Scope:**
- Single else-if (2 conditions)
- Multiple else-if (3+ conditions)
- Else-if with final else
- Else-if without final else
- Complex conditions in each test

**Example Bytecode:**
```
// For: if (x > 90) { g = "A"; } else if (x > 80) { g = "B"; } else { g = "C"; }

iload_1               // Load x
bipush 90
if_icmple else_if_1
ldc "A"
astore_2              // g = "A"
goto end_label
else_if_1:
iload_1               // Load x
bipush 80
if_icmple else_label
ldc "B"
astore_2              // g = "B"
goto end_label
else_label:
ldc "C"
astore_2              // g = "C"
end_label:
```

**Test Coverage:**
1. Single else-if (2 conditions total)
2. Double else-if (3 conditions total)
3. Triple+ else-if (4+ conditions)
4. Else-if without final else
5. Else-if with complex conditions
6. Else-if with returns in some branches
7. Else-if with early returns
8. Else-if with variable declarations
9. Else-if chains with side effects
10. Else-if with mixed statement types

---

### Phase 4: Nested If Statements (Priority: MEDIUM)

Support nested if statements.

**Scope:**
- If nested in consequent
- If nested in alternate
- Multiple levels of nesting (3+ deep)
- Nested if-else combinations
- Nested within loops (if applicable)

**Example Bytecode:**
```
// For: if (a) { if (b) { x = 1; } else { x = 2; } } else { x = 3; }

iload_1               // Load a
ifeq outer_else
  iload_2             // Load b (nested)
  ifeq inner_else
    iconst_1
    istore_3          // x = 1
    goto inner_end
  inner_else:
    iconst_2
    istore_3          // x = 2
  inner_end:
  goto outer_end
outer_else:
  iconst_3
  istore_3            // x = 3
outer_end:
```

**Test Coverage:**
1. If nested in consequent
2. If nested in alternate
3. If nested in both branches
4. 3-level deep nesting
5. 4+ level deep nesting
6. Nested if-else combinations
7. Nested with different condition types
8. Nested with returns at various levels
9. Nested with early exits
10. Nested with variable scoping

---

### Phase 5: Edge Cases and Advanced Scenarios (Priority: LOW)

Handle complex scenarios and edge cases.

**Scope:**
- Side effects in conditions (++, --, assignments)
- Short-circuit evaluation (&& and ||)
- Type coercion (truthy/falsy values if supported)
- Unreachable code detection
- Stack map frame generation
- Local variable scoping
- Multiple return paths
- Exception handling integration

**Test Coverage:**
1. Side effects in condition (i++, x = y++)
2. Short-circuit with side effects (a() && b())
3. Multiple returns with different types
4. Unreachable code after return
5. Variable shadowing in blocks
6. Variable declared in if used after
7. Break/continue in if within loop
8. Throw statements in branches
9. Complex stack map scenarios
10. Deep nesting with multiple variables

---

## Edge Cases and Special Scenarios

### Control Flow Edge Cases

1. **Empty Blocks**
   ```typescript
   if (condition) { }           // Valid, do nothing
   if (condition) { } else { }  // Valid, both empty
   ```

2. **No Else Clause**
   ```typescript
   if (condition) { x = 1; }    // No else, execution continues
   ```

3. **Return in Consequent Only**
   ```typescript
   if (condition) { return true; }
   // Execution continues here if condition false
   ```

4. **Return in Both Branches**
   ```typescript
   if (condition) { return true; } else { return false; }
   // Code after is unreachable
   ```

5. **Multiple Returns in Branch**
   ```typescript
   if (condition) {
     if (nested) return 1;
     return 2;
   }
   ```

### Condition Edge Cases

6. **Side Effects in Condition**
   ```typescript
   if ((x++) > 5) { /* x already incremented */ }
   if ((y = compute()) > 0) { /* y assigned before test */ }
   ```

7. **Short-Circuit Evaluation**
   ```typescript
   if (a() && b()) { }  // b() not called if a() returns false
   if (x || y()) { }    // y() not called if x is true
   ```

8. **Complex Boolean Expressions**
   ```typescript
   if (a && b || c && !d) { }
   if ((x > 5 && y < 10) || (z == 0)) { }
   ```

9. **Method Call Conditions**
   ```typescript
   if (obj.method()) { }
   if (checkCondition(a, b, c)) { }
   ```

10. **Truthy/Falsy Values** (if supported)
    ```typescript
    if (5) { }           // Non-zero integer
    if (0) { }           // Zero
    if (null) { }        // Null reference
    if ("") { }          // Empty string
    ```

### Variable Scoping Edge Cases

11. **Variable Declaration in Consequent**
    ```typescript
    if (condition) {
      const x = 10;
      // x visible here
    }
    // x not visible here
    ```

12. **Variable Declaration in Alternate**
    ```typescript
    if (condition) {
      const x = 1;
    } else {
      const x = 2;  // Different x, different scope
    }
    ```

13. **Variable Used After If**
    ```typescript
    let x;
    if (condition) {
      x = 10;
    } else {
      x = 20;
    }
    return x;  // x definitely assigned
    ```

14. **Variable Shadowing**
    ```typescript
    let x = 1;
    if (condition) {
      let x = 2;  // Shadows outer x
      return x;   // Returns 2
    }
    return x;     // Returns 1
    ```

### Statement Type Edge Cases

15. **Expression Statements**
    ```typescript
    if (flag) { x++; y--; }
    if (flag) { method(); }
    ```

16. **Block Statements**
    ```typescript
    if (flag) {
      const a = 1;
      const b = 2;
      return a + b;
    }
    ```

17. **Return Statements**
    ```typescript
    if (condition) return value;
    if (condition) { return compute(); }
    ```

18. **Break/Continue Statements** (within loops)
    ```typescript
    while (true) {
      if (condition) break;
      if (other) continue;
    }
    ```

19. **Throw Statements**
    ```typescript
    if (error) throw new Error("message");
    if (invalid) { validate(); throw err; }
    ```

20. **Nested Blocks**
    ```typescript
    if (a) {
      {
        const x = 1;
      }
      // x not visible here
    }
    ```

### Nested If Edge Cases

21. **Deeply Nested (5+ levels)**
    ```typescript
    if (a) {
      if (b) {
        if (c) {
          if (d) {
            if (e) {
              doSomething();
            }
          }
        }
      }
    }
    ```

22. **Mixed Nesting with Else**
    ```typescript
    if (a) {
      if (b) { x = 1; } else { x = 2; }
    } else {
      if (c) { x = 3; } else { x = 4; }
    }
    ```

23. **Nested in Else-If Chain**
    ```typescript
    if (a) {
      // ...
    } else if (b) {
      if (c) {
        // nested in else-if
      }
    } else {
      // ...
    }
    ```

### Stack Map Edge Cases

24. **Branch Merge with Different Locals**
    ```typescript
    let x;
    if (flag) {
      x = 10;
    } else {
      x = 20;
    }
    // Stack map must merge both paths
    ```

25. **Branch with Return vs Continue**
    ```typescript
    if (condition) {
      return value;  // One path exits
    }
    // Other path continues - stack map needed
    ```

26. **Nested Merges**
    ```typescript
    if (a) {
      if (b) { return 1; }
    } else {
      if (c) { return 2; }
    }
    // Multiple merge points
    ```

### Type System Edge Cases

27. **Non-Boolean Condition** (should error or coerce)
    ```typescript
    if (5) { }           // int as condition
    if ("string") { }    // string as condition
    if (obj) { }         // object as condition (null check?)
    ```

28. **Null Condition**
    ```typescript
    if (null) { }        // Should this work? (falsy)
    if (obj == null) { } // Explicit null check
    ```

29. **Undefined Condition**
    ```typescript
    if (undefined) { }   // Should this work?
    ```

### Label and Jump Edge Cases

30. **Long Jump Offsets**
    ```typescript
    if (condition) {
      // Very large block (>32KB bytecode)
      // May need wide jump instructions
    }
    ```

31. **Backward Jumps** (shouldn't occur in if, but verify)
    ```typescript
    // If statements only jump forward
    ```

32. **Multiple Gotos**
    ```typescript
    if (a) {
      if (b) { return 1; }
      if (c) { return 2; }
    } else {
      if (d) { return 3; }
    }
    // Multiple goto end_label from different paths
    ```

### Integration Edge Cases

33. **If in Method**
    ```typescript
    function test() {
      if (condition) {
        return true;
      }
      return false;
    }
    ```

34. **If in Constructor**
    ```typescript
    constructor() {
      if (condition) {
        this.x = 1;
      } else {
        this.x = 2;
      }
    }
    ```

35. **If in Static Initializer**
    ```typescript
    static {
      if (condition) {
        staticVar = 1;
      }
    }
    ```

36. **If with Field Access**
    ```typescript
    if (this.field > 5) {
      this.other = 10;
    }
    ```

37. **If with Array Access**
    ```typescript
    if (arr[0] > 5) {
      arr[1] = 10;
    }
    ```

38. **If with Method Calls**
    ```typescript
    if (obj.method()) {
      obj.other();
    }
    ```

### Optimization Edge Cases

39. **Constant Condition** (could optimize away)
    ```typescript
    if (true) { x = 1; }   // Could just generate: x = 1
    if (false) { x = 1; }  // Could omit entire block
    ```

40. **Empty Consequent with Else**
    ```typescript
    if (condition) { } else { x = 1; }
    // Could optimize to: if (!condition) { x = 1; }
    ```

41. **Inverted Logic Opportunity**
    ```typescript
    if (condition) {
      // small block
    } else {
      // large block
    }
    // Might be better to invert condition
    ```

### Error Handling Edge Cases

42. **If with Try-Catch**
    ```typescript
    if (condition) {
      try {
        riskyOperation();
      } catch (e) {
        handle(e);
      }
    }
    ```

43. **If Inside Try-Catch**
    ```typescript
    try {
      if (condition) {
        throw new Error();
      }
    } catch (e) {
      // ...
    }
    ```

### Special Statement Edge Cases

44. **If with Debugger Statement**
    ```typescript
    if (debug) {
      debugger;
    }
    ```

45. **If with With Statement** (if supported)
    ```typescript
    if (condition) {
      with (obj) {
        property = value;
      }
    }
    ```

46. **If with Labeled Statement**
    ```typescript
    label: if (condition) {
      break label;  // Legal?
    }
    ```

### Performance Edge Cases

47. **Branch Prediction Hints** (future optimization)
    ```typescript
    if (likelyTrue) {
      // Frequent path
    } else {
      // Rare path
    }
    ```

48. **Code Size vs Speed Trade-offs**
    ```typescript
    // Large if blocks might want different optimization
    ```

### Miscellaneous Edge Cases

49. **Comments and Whitespace**
    ```typescript
    if /* comment */ (condition) /* comment */ {
      // Should ignore comments
    }
    ```

50. **Unicode in Conditions/Variables**
    ```typescript
    const 变量 = true;
    if (变量) { }
    ```

51. **Very Long Condition Expressions**
    ```typescript
    if (a && b && c && d && e && f && g && h && i && j && k) {
      // Many operands
    }
    ```

52. **Goto End Optimization**
    ```typescript
    if (condition) {
      x = 1;
      // Last statement in consequent before else
    } else {
      x = 2;
    }
    // Consequent might not need goto if else is next
    ```

---

## Bytecode Instruction Reference

### Conditional Jumps

**Integer Comparison (compare with zero):**
- `ifeq <label>` (0x99) - Jump if value == 0 (false)
- `ifne <label>` (0x9A) - Jump if value != 0 (true)
- `iflt <label>` (0x9B) - Jump if value < 0
- `ifle <label>` (0x9C) - Jump if value <= 0
- `ifgt <label>` (0x9D) - Jump if value > 0
- `ifge <label>` (0x9E) - Jump if value >= 0

**Two Integer Comparison:**
- `if_icmpeq <label>` (0x9F) - Jump if int1 == int2
- `if_icmpne <label>` (0xA0) - Jump if int1 != int2
- `if_icmplt <label>` (0xA1) - Jump if int1 < int2
- `if_icmple <label>` (0xA2) - Jump if int1 <= int2
- `if_icmpgt <label>` (0xA3) - Jump if int1 > int2
- `if_icmpge <label>` (0xA4) - Jump if int1 >= int2

**Reference Comparison:**
- `ifnull <label>` (0xC6) - Jump if reference is null
- `ifnonnull <label>` (0xC7) - Jump if reference is not null
- `if_acmpeq <label>` (0xA5) - Jump if ref1 == ref2
- `if_acmpne <label>` (0xA6) - Jump if ref1 != ref2

**Unconditional Jump:**
- `goto <label>` (0xA7) - Unconditional jump
- `goto_w <label>` (0xC8) - Wide goto for long offsets (>32KB)

---

## AST Structure

### Swc4jAstIfStmt Components

```java
public class Swc4jAstIfStmt {
    ISwc4jAstExpr test;        // Condition expression
    ISwc4jAstStmt cons;        // Consequent (if true)
    Optional<ISwc4jAstStmt> alt;  // Alternate (if false) - optional
}
```

### Related AST Types

- **ISwc4jAstExpr** - Test condition (must evaluate to boolean)
- **ISwc4jAstStmt** - Consequent/alternate can be any statement
  - Swc4jAstBlockStmt - Block with multiple statements
  - Swc4jAstExprStmt - Single expression statement
  - Swc4jAstReturnStmt - Return statement
  - Swc4jAstIfStmt - Nested if (for else-if)
  - Swc4jAstVarDecl - Variable declaration
  - Swc4jAstBreakStmt - Break statement
  - Swc4jAstContinueStmt - Continue statement
  - Swc4jAstThrowStmt - Throw statement
  - etc.

---

## Implementation Strategy

### Code Generation Algorithm

```java
public static void generate(
        CodeBuilder code,
        ClassWriter.ConstantPool cp,
        Swc4jAstIfStmt ifStmt,
        CompilationContext context,
        ByteCodeCompilerOptions options) {

    // 1. Generate test condition
    ExpressionGenerator.generate(code, cp, ifStmt.getTest(), ...);

    // 2. Jump if condition is false
    code.ifeq(0); // Placeholder
    int ifeqPos = code.getCurrentOffset() - 2;
    int ifeqOpcode = code.getCurrentOffset() - 3;

    // 3. Generate consequent
    StatementGenerator.generate(code, cp, ifStmt.getCons(), ...);

    // 4. If there's an alternate, add goto and generate it
    if (ifStmt.getAlt().isPresent()) {
        code.gotoLabel(0); // Placeholder
        int gotoPos = code.getCurrentOffset() - 2;
        int gotoOpcode = code.getCurrentOffset() - 3;

        int elseLabel = code.getCurrentOffset();
        StatementGenerator.generate(code, cp, ifStmt.getAlt().get(), ...);

        int endLabel = code.getCurrentOffset();

        // Patch both jumps
        code.patchShort(ifeqPos, elseLabel - ifeqOpcode);
        code.patchShort(gotoPos, endLabel - gotoOpcode);
    } else {
        // No alternate - just patch the ifeq
        int endLabel = code.getCurrentOffset();
        code.patchShort(ifeqPos, endLabel - ifeqOpcode);
    }
}
```

### Else-If Detection

Detect else-if pattern:
```java
if (ifStmt.getAlt().isPresent() &&
    ifStmt.getAlt().get() instanceof Swc4jAstIfStmt) {
    // This is an else-if, can optimize goto elimination
}
```

### Label Management

For nested if statements, ensure unique labels:
- `if_end_<depth>_<counter>`
- `if_else_<depth>_<counter>`

Or use offset-based labels (current position).

---

## Stack Map Frame Considerations

### Frame Points

Stack map frames needed at:
1. **Start of alternate block** - After ifeq jump
2. **After if statement** - Merge point of both paths
3. **Each else-if test** - Multiple branch points

### Frame Merging

```java
// Before if: locals = {this, x, y}, stack = {}
if (condition) {
    let z = 10;
    // locals = {this, x, y, z}, stack = {}
} else {
    let w = 20;
    // locals = {this, x, y, w}, stack = {}
}
// After if: locals = {this, x, y}, stack = {}
// z and w not visible (different scopes)
```

### Return Path Handling

```java
if (condition) {
    return value;  // This path exits
}
// Stack map only needs to account for non-return path
```

---

## Integration Points

### Statement Generator

Update `StatementGenerator.java` to dispatch IfStmt:

```java
if (stmt instanceof Swc4jAstIfStmt ifStmt) {
    IfStatementGenerator.generate(code, cp, ifStmt, context, options);
}
```

### Expression Generator Integration

Test condition uses existing expression generation:
- BinaryExpr for comparisons (x > 5)
- UnaryExpr for negation (!flag)
- Identifier for boolean variables
- CallExpr for method calls returning boolean

### Variable Scope Tracking

CompilationContext must track:
- Variables declared in if blocks
- Variable visibility after if completes
- Local variable slots

---

## Test Plan

### Phase 1 Tests (Basic If)

1. **testBasicIfTrue** - `if (true) { x = 1; }`
2. **testBasicIfFalse** - `if (false) { x = 1; }`
3. **testIfWithIntComparison** - `if (x > 5) { y = 10; }`
4. **testIfWithBooleanVariable** - `if (flag) { doSomething(); }`
5. **testIfWithComplexConditionAnd** - `if (a && b) { ... }`
6. **testIfWithComplexConditionOr** - `if (a || b) { ... }`
7. **testIfWithNegation** - `if (!flag) { ... }`
8. **testEmptyIfBlock** - `if (condition) { }`
9. **testIfWithMultipleStatements** - `if (c) { x = 1; y = 2; z = 3; }`
10. **testIfWithReturn** - `if (c) { return value; }`

### Phase 2 Tests (If-Else)

11. **testIfElseBasic** - `if (c) { x = 1; } else { x = 2; }`
12. **testIfElseWithReturns** - `if (c) { return 1; } else { return 2; }`
13. **testIfElseWithReturnInConsequent** - `if (c) { return 1; } x = 2;`
14. **testIfElseWithReturnInAlternate** - `if (c) { x = 1; } else { return 2; }`
15. **testIfElseEmptyConsequent** - `if (c) { } else { x = 1; }`
16. **testIfElseEmptyAlternate** - `if (c) { x = 1; } else { }`
17. **testIfElseWithVarDecls** - `if (c) { const a = 1; } else { const b = 2; }`
18. **testIfElseBothEmpty** - `if (c) { } else { }`
19. **testIfElseWithMethodCalls** - `if (c) { m1(); } else { m2(); }`
20. **testIfElseWithSideEffects** - `if ((x++) > 5) { ... } else { ... }`

### Phase 3 Tests (Else-If)

21. **testElseIfSingle** - `if (a) { x = 1; } else if (b) { x = 2; } else { x = 3; }`
22. **testElseIfDouble** - Three conditions total
23. **testElseIfTriple** - Four conditions total
24. **testElseIfWithoutFinalElse** - `if (a) { } else if (b) { }`
25. **testElseIfWithComplexConditions** - Complex boolean in each test
26. **testElseIfWithReturns** - Returns in various branches
27. **testElseIfWithEarlyReturn** - `if (a) { return 1; } else if (b) { ... }`
28. **testElseIfAllEmpty** - All branches empty
29. **testElseIfMixedStatements** - Different statement types
30. **testElseIfLongChain** - 5+ else-if branches

### Phase 4 Tests (Nested If)

31. **testNestedInConsequent** - `if (a) { if (b) { x = 1; } }`
32. **testNestedInAlternate** - `if (a) { x = 1; } else { if (b) { x = 2; } }`
33. **testNestedInBoth** - Nested if in both branches
34. **testNestedThreeLevels** - 3-level deep nesting
35. **testNestedFourLevels** - 4-level deep nesting
36. **testNestedIfElse** - `if (a) { if (b) { } else { } } else { if (c) { } }`
37. **testNestedWithReturns** - Returns at various nesting levels
38. **testNestedWithComplexConditions** - Complex conditions at each level
39. **testNestedInElseIf** - `if (a) { } else if (b) { if (c) { } }`
40. **testNestedVarScoping** - Variable declarations at different levels

### Phase 5 Tests (Edge Cases)

41. **testSideEffectInCondition** - `if ((i++) > 5) { }`
42. **testSideEffectInConditionWithElse** - Both paths with side effects
43. **testShortCircuitAnd** - `if (a() && b()) { }` - b() not called if a() false
44. **testShortCircuitOr** - `if (a() || b()) { }` - b() not called if a() true
45. **testMultipleReturns** - Different return values from branches
46. **testUnreachableCodeAfterReturn** - Code after if with returns in all branches
47. **testVarScopingAcrossBranches** - `let x; if (c) { x = 1; } else { x = 2; } return x;`
48. **testNullCondition** - `if (obj == null) { }` or `if (obj) { }`
49. **testComplexExpressionsInBlock** - Conditional expressions, method calls, etc.
50. **testIfWithThrow** - `if (error) { throw new Error(); }`

### Integration Tests

51. **testIfInMethod** - If statement in method body
52. **testIfInConstructor** - If statement in constructor
53. **testIfWithFieldAccess** - `if (this.field > 5) { this.other = 10; }`
54. **testIfWithArrayAccess** - `if (arr[0] > 5) { arr[1] = 10; }`
55. **testIfWithMethodCallCondition** - `if (obj.check()) { }`
56. **testMultipleSequentialIfs** - Multiple if statements in sequence
57. **testIfInsideLoop** - If statement within while/for loop
58. **testIfWithBreakContinue** - Break/continue in if within loop

---

## Success Criteria

- [ ] All 5 phases implemented
- [ ] 57+ comprehensive tests covering all edge cases
- [ ] Proper stack map frame generation
- [ ] Support for all statement types in branches
- [ ] Correct variable scoping
- [ ] Proper handling of return statements
- [ ] Integration with expression generator
- [ ] Complete documentation
- [ ] All tests passing ✅
- [ ] Javadoc builds successfully ✅

---

## Known Limitations (Before Implementation)

1. **Truthy/Falsy Conversion:** May require strict boolean conditions (no automatic conversion)
2. **Goto Optimization:** Initial implementation may not optimize goto for tail if-else
3. **Constant Folding:** No optimization for constant conditions initially
4. **Branch Prediction:** No JVM hints for likely/unlikely branches
5. **Wide Jumps:** May not handle >32KB blocks initially (need goto_w)

---

## Implementation Checklist

### Code Generation
- [ ] Create `IfStatementGenerator.java`
- [ ] Implement `generate()` method
- [ ] Add label generation and management
- [ ] Handle if-only (no else)
- [ ] Handle if-else
- [ ] Detect and optimize else-if chains
- [ ] Handle nested if statements
- [ ] Implement stack map frame generation
- [ ] Add local variable scope tracking

### Integration
- [ ] Add IfStmt case to StatementGenerator dispatch
- [ ] Ensure expression generator works for conditions
- [ ] Handle block statement generation
- [ ] Track variable scopes across branches
- [ ] Handle return statements in branches
- [ ] Add debug/line number information

### Testing
- [ ] Create `TestCompileAstIfStmt.java`
- [ ] Add Phase 1 tests (basic if)
- [ ] Add Phase 2 tests (if-else)
- [ ] Add Phase 3 tests (else-if)
- [ ] Add Phase 4 tests (nested)
- [ ] Add Phase 5 tests (edge cases)
- [ ] Add integration tests
- [ ] Verify all tests pass
- [ ] Verify javadoc builds

---

## References

- **JVM Specification:** Chapter 3 - Compiling for the Java Virtual Machine (If-Then, If-Then-Else)
- **JVM Specification:** Chapter 6 - Instructions (Conditional Branches)
- **JavaScript Specification:** ECMAScript Section 13.6 - If Statement
- **TypeScript Specification:** Section 5.4 - If, Do, and While Statements
- **Java Language Specification:** Section 14.9 - If Statement
- **Existing Implementation:** ConditionalExpressionGenerator.java (for conditional logic patterns)
- **Test Reference:** TestCompileAstCondExpr.java (for test structure)

---

## Notes

- If statements are **control flow** (no value), unlike conditional expressions (which produce values)
- Unlike conditional expressions, if statements can have **no else clause**
- Else-if is syntactic sugar: `else if (c)` = `else { if (c) }`
- Stack after if statement execution should be **same as before** (statements don't leave values)
- Local variables declared in if blocks have **block scope** (not visible after block)
- Stack map frames required at **merge points** (after if, at else)
- Return statements in branches affect control flow and stack map computation
- Label naming convention: `if_else_<id>`, `if_end_<id>` for uniqueness
