# Arrow Expression Implementation Plan

## Overview

This document outlines the implementation plan for supporting `Swc4jAstArrowExpr` in TypeScript to JVM bytecode compilation. Arrow expressions (arrow functions/lambdas) are first-class function values that capture variables from their enclosing scope.

**Current Status:** PARTIALLY IMPLEMENTED

**Strategy:** Arrow expressions will be implemented as **Anonymous Inner Classes** that implement a functional interface. This approach is compatible with JDK 17 and provides full closure semantics.

**Syntax:**
```typescript
// Basic arrow function
const a = () => {}

// Arrow with parameters
const add = (x: int, y: int): int => x + y

// Arrow with block body
const compute = (x: int): int => {
  const result = x * 2
  return result
}

// Arrow capturing variables (closure)
class Calculator {
  multiplier: int = 10

  getMultiplier(): (x: int) => int {
    return (x: int): int => x * this.multiplier
  }
}
```

**Implementation Files:**
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/ArrowExpressionGenerator.java`
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/AnonymousInnerClassGenerator.java`
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ClosureAnalyzer.java`

**Test Files:**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowBasic.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowParams.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowBody.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowClosure.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowFunctionalInterface.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowNested.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowEdgeCases.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowCustomInterface.java` ✓
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/arrow/TestCompileAstArrowTypeInference.java` ✓

**AST Definition:** [Swc4jAstArrowExpr.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/Swc4jAstArrowExpr.java)

---

## AST Structure

```java
public class Swc4jAstArrowExpr extends Swc4jAst implements ISwc4jAstExpr {
    // List of parameters (ISwc4jAstPat: BindingIdent, ArrayPat, ObjectPat, AssignPat, RestPat)
    protected final List<ISwc4jAstPat> params;

    // Function body - either a block statement or a single expression
    protected ISwc4jAstBlockStmtOrExpr body;

    // Whether this is an async arrow function
    protected boolean _async;

    // Whether this is a generator arrow function (not valid TypeScript)
    protected boolean generator;

    // Return type annotation (optional)
    protected Optional<Swc4jAstTsTypeAnn> returnType;

    // Type parameters for generic arrows (optional)
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    // Syntax context
    protected int ctxt;
}
```

### Body Types (ISwc4jAstBlockStmtOrExpr)

1. **Swc4jAstBlockStmt** - Block body with explicit statements
   ```typescript
   (x) => { return x * 2; }
   ```

2. **ISwc4jAstExpr** - Expression body with implicit return
   ```typescript
   (x) => x * 2
   ```

---

## Anonymous Inner Class Strategy

### Compilation Model

Arrow expressions within the same scope are consolidated into a **single anonymous inner class** with multiple methods. This simplifies implementation and reduces the number of generated classes.

#### Key Design Principles

1. **One Inner Class Per Scope**: All arrow functions within the same method/class scope share a single inner class
2. **Multiple Methods**: Each arrow becomes a separate method in the shared inner class (e.g., `lambda$0()`, `lambda$1()`, etc.)
3. **No Interface Required for Internal Use**: The inner class does NOT need to implement any Java functional interface unless the arrow is explicitly assigned to a functional interface type
4. **Shared Captures**: Captured variables from the enclosing scope are stored as fields and shared across all lambda methods

#### When Functional Interface Is Required

When an arrow is assigned to a variable with a declared functional interface type (e.g., `Supplier`, `Function`, `IntUnaryOperator`):
- The inner class implements the specified interface
- The corresponding lambda method serves as the interface method implementation
- A wrapper or adapter may be needed if multiple arrows require different interfaces

#### When No Interface Is Required (Default)

When arrows are used internally without being assigned to a functional interface type:
- The inner class is a plain class with no `implements` clause
- Each arrow becomes a simple method that can be called directly
- This applies to: IIFE, untyped variables, internal helpers, etc.

**Rationale:**
- Fewer classes to generate and load at runtime
- Simpler bytecode generation logic
- More efficient capture sharing when multiple arrows reference the same outer variables
- Reduced memory footprint

### Example Transformation (Multiple Arrows - Single Inner Class)

**TypeScript Input:**
```typescript
class Calculator {
  multiplier: int = 2

  compute(x: int): int {
    // Arrow 1: doubles the value
    const double = (n: int): int => n * 2

    // Arrow 2: adds multiplier
    const addMultiplier = (n: int): int => n + this.multiplier

    // Arrow 3: IIFE
    const result = ((n: int): int => n + 1)(double(x))

    return addMultiplier(result)
  }
}
```

**Generated Bytecode Equivalent (conceptual Java):**
```java
class Calculator {
  int multiplier = 2;

  int compute(int x) {
    // Single inner class instance for ALL arrows in this method
    Calculator$Lambda$1 lambdas = new Calculator$Lambda$1(this);

    // Arrow 1: call lambda$0
    int doubled = lambdas.lambda$0(x);

    // Arrow 3: IIFE - call lambda$2
    int result = lambdas.lambda$2(doubled);

    // Arrow 2: call lambda$1
    return lambdas.lambda$1(result);
  }
}

// SINGLE inner class containing ALL lambda methods
class Calculator$Lambda$1 {
  // Captured variable (shared by all lambdas that need it)
  private final Calculator captured$this;

  Calculator$Lambda$1(Calculator captured$this) {
    this.captured$this = captured$this;
  }

  // Arrow 1: double
  public int lambda$0(int n) {
    return n * 2;
  }

  // Arrow 2: addMultiplier (uses captured this)
  public int lambda$1(int n) {
    return n + this.captured$this.multiplier;
  }

  // Arrow 3: IIFE increment
  public int lambda$2(int n) {
    return n + 1;
  }
}
```

### Example With Functional Interface

**TypeScript Input:**
```typescript
class Counter {
  count: int = 0

  getIncrementer(): IntSupplier {
    // This arrow must implement IntSupplier
    return (): int => {
      this.count++
      return this.count
    }
  }
}
```

**Generated Bytecode Equivalent (conceptual Java):**
```java
class Counter {
  int count = 0;

  IntSupplier getIncrementer() {
    return new Counter$Lambda$1(this);
  }
}

// Inner class implements the required interface
class Counter$Lambda$1 implements IntSupplier {
  private final Counter captured$this;

  Counter$Lambda$1(Counter captured$this) {
    this.captured$this = captured$this;
  }

  @Override
  public int getAsInt() {
    this.captured$this.count++;
    return this.captured$this.count;
  }
}
```

### Naming Convention

Anonymous inner classes will be named:
- `OuterClass$Lambda$N` where N is a sequential counter per class
- For nested classes: `OuterClass$InnerClass$Lambda$N`
- For standalone functions: `$Lambda$N` (in dummy class)

Methods within the inner class:
- `lambda$0`, `lambda$1`, `lambda$2`, etc. for internal arrows
- Interface method name (e.g., `getAsInt`, `apply`) when implementing a functional interface

---

## Implementation Phases

### Phase 1: Basic Arrow Expression - Priority: HIGH

**Status:** IMPLEMENTED

**Scope:**
- Arrow with no parameters, empty body: `() => {}`
- Arrow with expression body: `(x: int) => x * 2`
- Arrow with block body: `(x: int) => { return x * 2; }`
- No variable capture (pure functions)
- Explicit parameter and return type annotations

**Bytecode Generation:**
1. Generate anonymous inner class implementing functional interface
2. Generate default constructor (no captures)
3. Generate abstract method implementation
4. At call site: `new ClassName()`

### Phase 2: Variable Capture (Closures) - Priority: HIGH

**Status:** IMPLEMENTED (basic capture, method parameter capture, and `this` capture work; mutable capture not yet implemented)

**Scope:**
- Capture local variables from enclosing scope
- Capture `this` reference from enclosing class
- Capture method parameters
- Effectively final variable enforcement

**Implementation:**
1. Analyze arrow body for free variables (variables not defined in arrow)
2. Determine which variables are captured
3. Generate fields for captured variables
4. Generate constructor accepting captured values
5. Replace variable references with field access in body

**Captured Variable Handling:**
- Primitive captures: Store as primitive fields
- Object captures: Store as object reference fields
- `this` capture: Store enclosing instance reference
- Mutable captures: Wrap in holder object (e.g., `int[]` for `int`)

### Phase 3: Functional Interface Resolution - Priority: HIGH

**Status:** IMPLEMENTED

**Scope:**
- Determine target functional interface from context
- Support built-in functional interfaces (Supplier, Consumer, Function, etc.)
- Support custom SAM interfaces via type annotations

**Resolution Strategy:**
1. Check if arrow is assigned to typed variable → use that interface
2. Check if arrow is passed to method → use parameter type
3. Check if arrow is returned → use return type
4. Default to generated interface if no context

**Built-in Functional Interface Mapping:**
| Arrow Signature | Java Interface |
|-----------------|----------------|
| `() => T` | `java.util.function.Supplier<T>` |
| `(T) => void` | `java.util.function.Consumer<T>` |
| `(T) => R` | `java.util.function.Function<T, R>` |
| `(T, U) => R` | `java.util.function.BiFunction<T, U, R>` |
| `() => boolean` | `java.util.function.BooleanSupplier` |
| `(int) => int` | `java.util.function.IntUnaryOperator` |
| `(T) => boolean` | `java.util.function.Predicate<T>` |

### Phase 4: Parameter Handling - Priority: MEDIUM

**Status:** PARTIALLY IMPLEMENTED

**Scope:**
- Multiple parameters with types ✓
- Default parameter values (limited - see notes)
- Rest parameters (`...args`) (limited - see notes)
- Destructuring parameters (object, array) ✗
- Parameter type inference ✓

**Implementation Notes:**

When arrow expressions are assigned to standard functional interfaces (e.g., `IntUnaryOperator`, `Function<T,R>`), the interface dictates the method signature. This creates limitations:

1. **Default Parameter Values**: The interface always provides the parameter, so default values are never used. Syntax is parsed but the default value is ignored.
   ```typescript
   // Default value is parsed but ignored since IntUnaryOperator always provides 'x'
   const fn: IntUnaryOperator = (x: int = 10) => x * 2
   ```

2. **Rest Parameters**: Standard functional interfaces have fixed arity. Varargs would require custom interfaces.
   ```typescript
   // Not compatible with standard functional interfaces
   const sum = (...values: int[]) => total(values)
   ```

3. **Optional Parameters**: Similar to defaults - the interface requires the parameter.
   ```typescript
   // Interface always provides 'x', so it's never null/undefined
   const fn: IntUnaryOperator = (x?: int) => x ?? 0
   ```

4. **Destructuring Parameters**: Requires generating field/array access code. Not yet implemented.

**Working Features:**
- Single and multiple typed parameters (int, long, double, boolean, object)
- Parameter capture from enclosing scope
- Parameter type inference for return type calculation
- All primitive specializations (IntUnaryOperator, LongBinaryOperator, etc.)

### Phase 5: Type Inference - Priority: MEDIUM

**Status:** PARTIALLY IMPLEMENTED

**Scope:**
- Infer parameter types from context (target interface) - LIMITED (requires explicit type annotations)
- Infer return type from expression or return statements ✓
- Handle generic type parameters - LIMITED (type erasure to Object)

**Implementation Notes:**

1. **Return Type Inference from Expressions**: Fully working. The system correctly infers return types from:
   - Binary operations (arithmetic, bitwise, logical)
   - Unary operations (negation)
   - Ternary expressions (including nested)
   - Captured variables
   - Local variable types
   - Comparison expressions (boolean inference)

2. **Return Type Inference from Return Statements**: Fully working. The system analyzes block bodies and:
   - Finds return statements recursively (including in if blocks)
   - Infers type from the returned expression
   - Supports multiple return paths

3. **Type Widening**: Working. When mixing types in operations:
   - int + long → long
   - int + double → double
   - Follows JVM numeric promotion rules

4. **Parameter Type Inference from Context**: Limited. Parameters require explicit type annotations:
   ```typescript
   // Works: explicit parameter type
   const fn: IntUnaryOperator = (x: int) => x * 2

   // NOT supported: inferring x:int from IntUnaryOperator
   // const fn: IntUnaryOperator = x => x * 2
   ```

5. **Generic Type Parameters**: Limited. Generic arrows like `<T>(x: T): T => x` are parsed but use type erasure to Object. Full generic support would require:
   - Type parameter propagation
   - Constraint resolution
   - Specialized interface generation

### Phase 6: Nested Arrows - Priority: MEDIUM

**Status:** IMPLEMENTED

**Scope:**
- Arrow returning arrow ✓ (via curried function pattern)
- Arrow inside arrow ✓
- Multi-level variable capture ✓
- Naming for nested anonymous classes ✓

**Implementation Notes:**

Nested arrows work through the curried function pattern:
```typescript
// Method returns arrow (curried function)
createAdder(x: int): IntUnaryOperator {
  return (y: int) => x + y
}
```

This compiles to a method that returns an anonymous inner class instance. The outer parameter (`x`) is captured by the inner class and used in the lambda body.

**Supported Capture Types:**
- Primitive captures: int, long, double ✓
- Object captures: String and other reference types ✓
- Multiple captures: combining primitives and objects ✓
- Static method arrows with capture ✓
- Multiple arrows from same class with different captures ✓

### Phase 7: Async Arrow Functions - Priority: LOW

**Status:** NOT SUPPORTED

Async arrow functions are intentionally not supported. The JVM bytecode compilation targets a simpler subset of TypeScript without async/await syntax. State machine generation for coroutines would require significant complexity.

### Phase 8: Generator Arrow Functions - Priority: LOW

**Status:** NOT SUPPORTED

Generator arrow functions are intentionally not supported. Note that generator arrow functions are not valid TypeScript/JavaScript syntax (`*() => {}` is a syntax error), but the AST has a `generator` flag. Any arrow with `generator=true` will be rejected at compile time.

---

## Edge Cases and Special Scenarios

### Basic Syntax Edge Cases

1. **No Parameters, Empty Body**
   ```typescript
   const a = () => {}
   // Returns void, no-op function
   ```

2. **No Parameters, Expression Body**
   ```typescript
   const a = () => 42
   // Implicit return of expression
   ```

3. **Single Parameter Without Parentheses**
   ```typescript
   const double = x => x * 2
   // TypeScript requires type annotation or parentheses
   ```

4. **Single Parameter With Parentheses**
   ```typescript
   const double = (x: int): int => x * 2
   ```

5. **Multiple Parameters**
   ```typescript
   const add = (x: int, y: int): int => x + y
   ```

6. **Many Parameters (JVM Limit)**
   ```typescript
   const manyParams = (a: int, b: int, c: int, d: int, e: int, ...): int => a
   // JVM: 255 parameter limit (254 for instance methods + 'this')
   ```

### Parameter Type Edge Cases

7. **Typed Parameters**
   ```typescript
   const fn = (x: int, y: String): String => y + x
   ```

8. **Default Parameter Values**
   ```typescript
   const fn = (x: int = 10): int => x * 2
   // Needs overloaded methods or null check
   ```

9. **Rest Parameters**
   ```typescript
   const sum = (...values: int[]): int => {
     let total = 0
     for (const v of values) total += v
     return total
   }
   ```

10. **Rest Parameters After Regular**
    ```typescript
    const format = (template: String, ...args: Object[]): String => template
    ```

11. **Object Destructuring Parameter**
    ```typescript
    const fn = ({x, y}: {x: int, y: int}): int => x + y
    ```

12. **Array Destructuring Parameter**
    ```typescript
    const fn = ([first, second]: int[]): int => first + second
    ```

13. **Nested Destructuring**
    ```typescript
    const fn = ({point: {x, y}}: {point: {x: int, y: int}}): int => x + y
    ```

14. **Optional Parameters**
    ```typescript
    const fn = (x?: int): int => x ?? 0
    ```

### Body Type Edge Cases

15. **Expression Body - Primitive Return**
    ```typescript
    const fn = (x: int): int => x * 2
    ```

16. **Expression Body - Object Literal**
    ```typescript
    const fn = (): Object => ({ x: 1, y: 2 })
    // Note: Parentheses needed to distinguish from block
    ```

17. **Expression Body - Array Literal**
    ```typescript
    const fn = (): int[] => [1, 2, 3]
    ```

18. **Expression Body - Ternary**
    ```typescript
    const fn = (x: int): int => x > 0 ? x : -x
    ```

19. **Expression Body - Binary Operation**
    ```typescript
    const fn = (x: int, y: int): int => x + y * 2
    ```

20. **Expression Body - Method Call**
    ```typescript
    const fn = (s: String): int => s.length()
    ```

21. **Expression Body - New Expression**
    ```typescript
    const fn = (): ArrayList<int> => new ArrayList<int>()
    ```

22. **Block Body - Empty**
    ```typescript
    const fn = (): void => {}
    ```

23. **Block Body - Single Return**
    ```typescript
    const fn = (x: int): int => { return x * 2 }
    ```

24. **Block Body - Multiple Statements**
    ```typescript
    const fn = (x: int): int => {
      const doubled = x * 2
      const result = doubled + 1
      return result
    }
    ```

25. **Block Body - No Return (Void)**
    ```typescript
    const fn = (x: int): void => {
      console.log(x)
    }
    ```

26. **Block Body - Conditional Returns**
    ```typescript
    const fn = (x: int): int => {
      if (x > 0) return x
      return -x
    }
    ```

27. **Block Body - Loop**
    ```typescript
    const sum = (arr: int[]): int => {
      let total = 0
      for (const x of arr) {
        total += x
      }
      return total
    }
    ```

28. **Block Body - Try/Catch**
    ```typescript
    const safe = (fn: () => int): int => {
      try {
        return fn()
      } catch (e) {
        return 0
      }
    }
    ```

### Closure/Capture Edge Cases

29. **Capture Local Variable**
    ```typescript
    const x = 10
    const fn = (): int => x
    ```

30. **Capture Multiple Local Variables**
    ```typescript
    const x = 10
    const y = 20
    const fn = (): int => x + y
    ```

31. **Capture `this` Reference**
    ```typescript
    class A {
      value: int = 10
      getValueFn(): () => int {
        return (): int => this.value
      }
    }
    ```

32. **Capture Method Parameter**
    ```typescript
    class A {
      createAdder(x: int): (y: int) => int {
        return (y: int): int => x + y
      }
    }
    ```

33. **Capture Both `this` and Local Variables**
    ```typescript
    class A {
      multiplier: int = 2
      compute(base: int): () => int {
        const offset = 10
        return (): int => base * this.multiplier + offset
      }
    }
    ```

34. **Capture Variable Modified After Arrow Definition**
    ```typescript
    let x = 10
    const fn = (): int => x  // Captures reference, not value?
    x = 20
    fn()  // Returns 10 or 20?
    // JVM: Must be effectively final - compile error
    ```

35. **Capture Loop Variable**
    ```typescript
    const functions: (() => int)[] = []
    for (let i = 0; i < 3; i++) {
      functions.push((): int => i)  // Classic closure problem
    }
    // Each function should return different value
    ```

36. **Capture Variable in Nested Scope**
    ```typescript
    const outer = (): () => int => {
      const x = 10
      return (): int => x
    }
    ```

37. **Multi-Level Capture**
    ```typescript
    const a = 1
    const f1 = (): (() => int) => {
      const b = 2
      return (): int => a + b
    }
    ```

38. **Capture from Constructor**
    ```typescript
    class A {
      fn: () => int
      constructor(x: int) {
        this.fn = (): int => x
      }
    }
    ```

39. **Capture Static Field**
    ```typescript
    class A {
      static value: int = 10
      static getFn(): () => int {
        return (): int => A.value
      }
    }
    ```

40. **No Capture (Pure Function)**
    ```typescript
    const add = (x: int, y: int): int => x + y
    // No external dependencies, can optimize
    ```

### Context/Usage Edge Cases

41. **Assigned to const Variable**
    ```typescript
    const fn = (x: int): int => x * 2
    ```

42. **Assigned to let Variable**
    ```typescript
    let fn = (x: int): int => x * 2
    fn = (x: int): int => x * 3  // Reassignment
    ```

43. **Assigned to Typed Variable**
    ```typescript
    const fn: Function<int, int> = (x: int): int => x * 2
    ```

44. **As Method Argument**
    ```typescript
    list.forEach((item: int): void => console.log(item))
    ```

45. **As Method Argument with Type Inference**
    ```typescript
    list.map(x => x * 2)  // Type inferred from list element type
    ```

46. **As Return Value**
    ```typescript
    function getAdder(x: int): (y: int) => int {
      return (y: int): int => x + y
    }
    ```

47. **In Array Literal**
    ```typescript
    const fns = [
      (x: int): int => x + 1,
      (x: int): int => x + 2,
      (x: int): int => x + 3
    ]
    ```

48. **In Object Literal**
    ```typescript
    const obj = {
      add: (x: int, y: int): int => x + y,
      sub: (x: int, y: int): int => x - y
    }
    ```

49. **As Field Initializer**
    ```typescript
    class A {
      processor: (x: int) => int = (x: int): int => x * 2
    }
    ```

50. **In Conditional Expression**
    ```typescript
    const fn = condition
      ? (x: int): int => x + 1
      : (x: int): int => x - 1
    ```

51. **Immediately Invoked (IIFE)**
    ```typescript
    const result = ((x: int): int => x * 2)(5)
    // result = 10
    ```

52. **Chained Arrow Calls**
    ```typescript
    const result = ((x: int): (y: int) => int => (y: int): int => x + y)(5)(3)
    // result = 8
    ```

### Nested Scenarios Edge Cases

53. **Arrow Inside Arrow**
    ```typescript
    const outer = (x: int): (y: int) => int => {
      return (y: int): int => x + y
    }
    ```

54. **Arrow Inside Regular Function**
    ```typescript
    function outer(x: int): (y: int) => int {
      return (y: int): int => x + y
    }
    ```

55. **Arrow Inside Class Method**
    ```typescript
    class A {
      getValue(): int {
        const fn = (): int => this.value
        return fn()
      }
    }
    ```

56. **Arrow Inside Constructor**
    ```typescript
    class A {
      handler: () => void
      constructor() {
        this.handler = (): void => this.doSomething()
      }
    }
    ```

57. **Arrow Inside Static Method**
    ```typescript
    class A {
      static create(): A {
        const factory = (): A => new A()
        return factory()
      }
    }
    ```

58. **Deeply Nested Arrows**
    ```typescript
    const f = (a: int): (b: int) => (c: int) => int =>
      (b: int): (c: int) => int =>
        (c: int): int => a + b + c
    ```

### Return Type Edge Cases

59. **Explicit Return Type**
    ```typescript
    const fn = (x: int): int => x * 2
    ```

60. **Inferred Return Type from Expression**
    ```typescript
    const fn = (x: int) => x * 2  // Inferred: int
    ```

61. **Inferred Return Type from Return Statement**
    ```typescript
    const fn = (x: int) => { return x * 2 }  // Inferred: int
    ```

62. **Void Return Type**
    ```typescript
    const fn = (x: int): void => { console.log(x) }
    ```

63. **Return Arrow Function**
    ```typescript
    const fn = (x: int): (y: int) => int => (y: int): int => x + y
    ```

64. **Return Object Type**
    ```typescript
    const fn = (): { x: int, y: int } => ({ x: 1, y: 2 })
    ```

65. **Return Array Type**
    ```typescript
    const fn = (): int[] => [1, 2, 3]
    ```

66. **Return Union Type** - NOT SUPPORTED
    ```typescript
    const fn = (x: boolean): int | String => x ? 1 : "one"
    ```

67. **Return Generic Type**
    ```typescript
    const identity = <T>(x: T): T => x
    ```

### Generic Arrow Edge Cases

68. **Generic Arrow Function**
    ```typescript
    const identity = <T>(x: T): T => x
    ```

69. **Generic with Constraint**
    ```typescript
    const first = <T extends Iterable<any>>(x: T): any => x.iterator().next()
    ```

70. **Multiple Type Parameters**
    ```typescript
    const pair = <T, U>(x: T, y: U): [T, U] => [x, y]
    ```

71. **Generic Arrow Capturing Generic**
    ```typescript
    class Container<T> {
      value: T
      getMapper<U>(): (fn: (t: T) => U) => U {
        return (fn: (t: T) => U): U => fn(this.value)
      }
    }
    ```

### Functional Interface Edge Cases

72. **Implementing Supplier<T>**
    ```typescript
    const supplier: Supplier<int> = (): int => 42
    ```

73. **Implementing Consumer<T>**
    ```typescript
    const consumer: Consumer<int> = (x: int): void => console.log(x)
    ```

74. **Implementing Function<T, R>**
    ```typescript
    const fn: Function<int, String> = (x: int): String => x.toString()
    ```

75. **Implementing Predicate<T>**
    ```typescript
    const predicate: Predicate<int> = (x: int): boolean => x > 0
    ```

76. **Implementing Comparator<T>**
    ```typescript
    const comparator: Comparator<int> = (a: int, b: int): int => a - b
    ```

77. **Implementing Custom Functional Interface**
    ```typescript
    // import { MyFunction } from 'com.example'
    const fn: MyFunction = (x: int, y: String): boolean => x > y.length()
    ```

78. **Implementing Runnable**
    ```typescript
    const runnable: Runnable = (): void => console.log("Running")
    ```

79. **Implementing Callable<T>**
    ```typescript
    const callable: Callable<int> = (): int => 42
    ```

### Async/Generator Arrow Edge Cases

> **NOTE:** Async and Generator arrows are NOT SUPPORTED. The following examples are for reference only.

80. **Async Arrow Function** - NOT SUPPORTED
    ```typescript
    const fn = async (): Promise<int> => 42
    // Compile error: Async arrow functions are not supported
    ```

81. **Async Arrow with Await** - NOT SUPPORTED
    ```typescript
    const fn = async (): Promise<int> => await fetchValue()
    // Compile error: Async arrow functions are not supported
    ```

82. **Generator Arrow Function** - NOT SUPPORTED (also invalid TypeScript)
    ```typescript
    // Note: This is NOT valid TypeScript/JavaScript syntax
    // Generator arrow functions do not exist in the language
    // The AST flag exists but should never be true for valid code
    ```

### Error Handling Edge Cases

83. **Arrow in Try Block**
    ```typescript
    try {
      const fn = (x: int): int => {
        if (x < 0) throw new Error("negative")
        return x
      }
      fn(-1)
    } catch (e) {}
    ```

84. **Arrow Throwing Exception**
    ```typescript
    const fn = (x: int): int => {
      throw new Error("always throws")
    }
    ```

### Special Scenarios

85. **Recursive Arrow (Named Binding Required)**
    ```typescript
    const factorial: (n: int) => int = (n: int): int =>
      n <= 1 ? 1 : n * factorial(n - 1)
    ```

86. **Mutually Recursive Arrows**
    ```typescript
    const isEven: (n: int) => boolean = (n: int): boolean =>
      n === 0 ? true : isOdd(n - 1)
    const isOdd: (n: int) => boolean = (n: int): boolean =>
      n === 0 ? false : isEven(n - 1)
    ```

87. **Arrow as Comparator for Sort**
    ```typescript
    list.sort((a: int, b: int): int => a - b)
    ```

88. **Arrow as Filter Predicate**
    ```typescript
    list.filter((x: int): boolean => x > 0)
    ```

89. **Arrow as Map Transformer**
    ```typescript
    list.map((x: int): int => x * 2)
    ```

90. **Arrow as Reduce Accumulator**
    ```typescript
    list.reduce((acc: int, x: int): int => acc + x, 0)
    ```

91. **Arrow with Side Effects**
    ```typescript
    let counter = 0
    const increment = (): int => ++counter  // Modifies external state
    ```

92. **Arrow Returning Null**
    ```typescript
    const fn = (): String | null => null
    ```

93. **Arrow with Null Parameter**
    ```typescript
    const fn = (x: String | null): int => x?.length() ?? 0
    ```

94. **Empty Arrow in Method Chain**
    ```typescript
    promise.then((): void => {}).catch((): void => {})
    ```

95. **Arrow with Varargs and Regular Params**
    ```typescript
    const fn = (first: int, ...rest: int[]): int => first + rest.length
    ```

96. **Arrow Assigned to Interface Field**
    ```typescript
    interface Handler {
      process: (x: int) => int
    }
    const handler: Handler = {
      process: (x: int): int => x * 2
    }
    ```

---

## Bytecode Patterns

### Basic Arrow (No Capture)

```typescript
const double = (x: int): int => x * 2
```

**Generated Classes:**

1. **Anonymous Inner Class:** `OuterClass$Lambda$1`
```java
class OuterClass$Lambda$1 implements IntUnaryOperator {
    public int applyAsInt(int x) {
        return x * 2;
    }
}
```

2. **At usage site:**
```
new OuterClass$Lambda$1                // Create instance
// Stack: [IntUnaryOperator]
```

### Arrow with Capture

```typescript
class A {
  multiplier: int = 10
  createMultiplier(): (x: int) => int {
    return (x: int): int => x * this.multiplier
  }
}
```

**Generated Anonymous Inner Class:** `A$Lambda$1`
```java
class A$Lambda$1 implements IntUnaryOperator {
    private final A captured$this;

    A$Lambda$1(A captured$this) {
        this.captured$this = captured$this;
    }

    public int applyAsInt(int x) {
        return x * this.captured$this.multiplier;
    }
}
```

**Bytecode at arrow expression site:**
```
aload_0                                // Load 'this' (enclosing instance)
new A$Lambda$1                         // Create lambda instance
dup
aload_0                                // Load 'this' for constructor
invokespecial A$Lambda$1.<init>(LA;)V  // Call constructor with capture
// Stack: [A$Lambda$1 (as IntUnaryOperator)]
```

### Expression Body vs Block Body

**Expression Body:**
```typescript
(x: int): int => x * 2
```
```java
public int applyAsInt(int x) {
    return x * 2;  // Implicit return
}
```

**Block Body:**
```typescript
(x: int): int => { return x * 2; }
```
```java
public int applyAsInt(int x) {
    return x * 2;  // Explicit return
}
```

---

## Implementation Details

### Closure Analysis

The `ClosureAnalyzer` class will:
1. Walk the arrow body AST
2. Identify all identifier references
3. Determine which are free variables (not parameters, not locally declared)
4. Categorize captures:
   - `this` reference
   - Local variables from enclosing scope
   - Method parameters from enclosing method
   - Other captured variables from enclosing arrow

### Anonymous Inner Class Generation

The `AnonymousInnerClassGenerator` will:
1. Determine the functional interface to implement
2. Generate class header with unique name
3. Generate fields for each captured variable
4. Generate constructor accepting all captures
5. Generate the SAM method implementation
6. Handle return type conversion if needed

### Effectively Final Enforcement

Variables captured by arrows must be effectively final:
- Assigned only once
- Never modified after initial assignment
- Mutable captures require wrapping in holder object

**Mutable Capture Pattern:**
```typescript
let x = 10
const fn = (): int => x  // x is mutable
x = 20
```

**Implementation:**
```java
// Wrap in holder
int[] x$holder = new int[] { 10 };
Lambda$1 fn = new Lambda$1(x$holder);
x$holder[0] = 20;

class Lambda$1 {
    private final int[] captured$x;
    public int get() {
        return captured$x[0];
    }
}
```

---

## Functional Interface Resolution

### Resolution Priority

1. **Explicit Type Annotation** - Variable or parameter type
   ```typescript
   const fn: Function<int, int> = (x: int): int => x
   ```

2. **Method Parameter Type** - When passed to method
   ```typescript
   list.map((x: int): int => x * 2)  // Uses Function<T, R>
   ```

3. **Return Type** - When returned from method
   ```typescript
   function getFn(): Predicate<int> {
     return (x: int): boolean => x > 0
   }
   ```

4. **Assignment Target** - Variable type
   ```typescript
   const fn: Predicate<int> = (x: int): boolean => x > 0
   ```

5. **Generated Interface** - If no context available
   - Generate custom interface matching arrow signature

### Primitive Specializations

Use primitive specializations to avoid boxing:

| Arrow Type | Preferred Interface | Status |
|------------|---------------------|--------|
| `() => int` | `IntSupplier` | ✓ IMPLEMENTED |
| `() => long` | `LongSupplier` | ✓ IMPLEMENTED |
| `() => double` | `DoubleSupplier` | ✓ IMPLEMENTED |
| `() => boolean` | `BooleanSupplier` | ✓ IMPLEMENTED |
| `(int) => void` | `IntConsumer` | ✓ IMPLEMENTED |
| `(long) => void` | `LongConsumer` | ✓ IMPLEMENTED |
| `(double) => void` | `DoubleConsumer` | ✓ IMPLEMENTED |
| `(int) => int` | `IntUnaryOperator` | ✓ IMPLEMENTED |
| `(long) => long` | `LongUnaryOperator` | ✓ IMPLEMENTED |
| `(double) => double` | `DoubleUnaryOperator` | ✓ IMPLEMENTED |
| `(int) => boolean` | `IntPredicate` | ✓ IMPLEMENTED |
| `(int, int) => int` | `IntBinaryOperator` | ✓ IMPLEMENTED |
| `(long, long) => long` | `LongBinaryOperator` | ✓ IMPLEMENTED |
| `(double, double) => double` | `DoubleBinaryOperator` | ✓ IMPLEMENTED |

---

## Success Criteria

### Implementation Complete When:
- [x] Phase 1: Basic arrow expressions with no capture working
- [x] Phase 2: Variable capture (closures) working (local vars, params, and `this` capture)
- [x] Phase 3: Functional interface resolution working
- [x] Phase 4: Basic parameter types working (typed params, multiple params, param capture)
- [ ] Phase 4: Advanced parameter features (destructuring) - NOT IMPLEMENTED
- [x] Phase 5: Return type inference working (expressions, statements, operators)
- [ ] Phase 5: Parameter type inference from context - LIMITED
- [ ] Phase 5: Generic type parameters - LIMITED (type erasure)
- [x] Phase 6: Nested arrows working (curried functions, multi-level capture, mixed type captures)
- [x] Phase 7: Async arrows - NOT SUPPORTED (intentionally excluded)
- [x] Phase 8: Generator arrows - NOT SUPPORTED (intentionally excluded, also invalid syntax)
- [x] Core edge cases documented and tested (params, body types, closures, custom interfaces)
- [x] All tests passing

### Quality Gates:
- [x] Anonymous inner classes generate valid bytecode
- [x] Captured variables correctly accessible in lambda body
- [x] Functional interfaces correctly implemented
- [x] Stack map frames generated correctly
- [ ] No memory leaks from captures

---

## Known Limitations

1. **Async Arrows**: NOT SUPPORTED - Async/await requires state machine transformation too complex for bytecode compilation
2. **Generator Arrows**: NOT SUPPORTED - Generator arrow functions are not valid TypeScript/JavaScript syntax; the AST flag exists but should never be true for valid code
3. **Union Return Types**: NOT SUPPORTED - JVM requires single return type
4. **Mutable Captures**: Require holder object pattern (performance overhead)
5. **Reflection on Arrows**: Limited - Anonymous inner class details not accessible
6. **Serialization**: Anonymous inner classes may have serialization issues
7. **Debugging**: Anonymous class names may be harder to debug
8. **Custom Interfaces**: Arrow expressions currently only implement java.util.function interfaces. Direct implementation of custom TypeScript interfaces is NOT YET SUPPORTED - use explicit class implementations instead

---

## References

- **AST Definition:** [Swc4jAstArrowExpr.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/Swc4jAstArrowExpr.java)
- **JVM Specification:** Chapter 4.7.6 - Inner Classes
- **Java Lambda Specification:** JSR 335
- **TypeScript Specification:** Arrow Functions
- **ECMAScript Specification:** Arrow Function Definitions
- **Related:** [Function Implementation](../class/function.md)

---

## Test Coverage Summary

The following edge cases are now covered by tests:

**Body Types (TestCompileAstArrowBody.java):**
- Edge case 15: Expression body - primitive return ✓
- Edge case 18: Expression body - ternary ✓
- Edge case 19: Expression body - binary operation ✓
- Edge case 20: Expression body - captured value return ✓
- Edge case 22: Block body - empty (void) ✓
- Edge case 23: Block body - single return ✓
- Edge case 25: Block body - no return (void) ✓
- Edge case 26: Block body - conditional returns ✓
- Edge case 27: Block body - while/for loop ✓

**Edge Cases (TestCompileAstArrowEdgeCases.java):**
- Edge case 30: Capture multiple local variables ✓
- Edge case 33: Capture both `this` and local variables ✓
- Edge case 39: Capture static field ✓
- Edge case 46: As return value ✓
- Edge case 49: As field initializer ✓
- Edge case 57: Arrow inside static method ✓
- Multiple arrows with different functional interface types ✓
- Multiple parameter capture ✓

**Parameters (TestCompileAstArrowParams.java):**
- Edge case 1: No parameters ✓
- Edge case 4: Single parameter with parentheses ✓
- Edge case 5: Two int parameters ✓
- Edge case 6: Many parameters via closure capture ✓
- Edge case 7: Typed parameters with mixed types (via capture) ✓
- All primitive specializations (int, long, double) for suppliers, consumers, operators ✓
- IntPredicate, IntConsumer, LongConsumer, DoubleConsumer ✓
- IntBinaryOperator, LongBinaryOperator, DoubleBinaryOperator ✓

**Closures (TestCompileAstArrowClosure.java):**
- Edge case 29: Capture local variable ✓
- Edge case 31: Capture `this` reference ✓
- Edge case 32: Capture method parameter ✓
- Multiple arrows in same scope ✓

**Type Inference (TestCompileAstArrowTypeInference.java):**
- Edge case 60: Inferred return type from expression ✓
- Edge case 61: Inferred return type from return statement ✓
- Return type inference from binary operations (add, multiply, modulo) ✓
- Return type inference from unary operations (negation) ✓
- Return type inference from ternary expressions (including nested) ✓
- Return type inference from captured variables ✓
- Return type inference from local/const variables ✓
- Return type inference from boolean comparisons ✓
- Return type inference from bitwise operations (and, or, xor) ✓
- Type widening (int + long → long) ✓
- Conditional returns in block body ✓
- Multiple suppliers (IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier) ✓
- Binary operators (IntBinaryOperator, LongBinaryOperator, DoubleBinaryOperator) ✓

**Nested Arrows (TestCompileAstArrowNested.java):**
- Edge case 53: Arrow inside arrow - curried function pattern ✓
- Edge case 37: Multi-level capture - capturing multiple params ✓
- Edge case 57: Arrow inside static method with capture ✓
- Arrow with offset capture (parameter capture) ✓
- Arrow with string capture (reference type capture) ✓
- Arrow with this capture (instance field access) ✓
- Arrow chained with capture (multiple arrows in same class) ✓
- Multiple arrows from same class with different captures ✓
- Arrow capturing different types (String + int) ✓
- Arrow capturing multiple primitive values (4 ints) ✓
- Arrow capturing long values ✓
- Arrow capturing double values ✓
- Arrow capturing mixed types (int + long + double) ✓

---

*Last Updated: January 26, 2026*
*Status: PARTIALLY IMPLEMENTED (Phase 1-3 complete, Phase 4-6 mostly complete with documented limitations)*
*Next Step: Consider implementing Phase 4 advanced features (destructuring parameters) or improving Phase 5 parameter type inference from context*
