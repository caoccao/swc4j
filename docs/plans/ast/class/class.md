# Class Implementation Plan

## Overview

This document outlines the implementation plan for supporting `Swc4jAstClass` in TypeScript to JVM bytecode compilation. Classes are fundamental constructs that contain methods, properties, constructors, and support inheritance.

**Current Status:** FULLY IMPLEMENTED - All class features implemented (basic, inheritance, abstract, interfaces, constructors, fields, static members, access modifiers, generics, direct access for classes without namespace, ES2022 private methods, explicit static blocks); class expressions return Class objects

**Remaining Work:**
- None - all features implemented

**Syntax:**
```typescript
// Basic class
class A {
  test(): int {
    return 123
  }
}

// Class with inheritance
class B extends A {
  override test(): int {
    return super.test() + 1
  }
}

// Abstract class
abstract class Shape {
  abstract area(): double
}

// Class implementing interface
class Circle implements IShape {
  radius: double
  area(): double {
    return 3.14159 * this.radius * this.radius
  }
}
```

**Implementation Files:**
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/clazz/ClassProcessor.java`
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/clazz/MethodGenerator.java`
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/clazz/ConstructorGenerator.java`
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/clazz/FieldGenerator.java`

**Test Files:**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassBasic.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassInheritance.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassAbstract.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassImplements.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassConstructor.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassFields.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassStatic.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassStaticBlock.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassAccessibility.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassPrivateFields.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassPrivateMethod.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassDecorators.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassGenerics.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/clazz/clazz/TestCompileAstClassEdgeCases.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/classexpr/TestCompileAstClassExpr.java`

**AST Definition:** [Swc4jAstClass.java](../../../../src/main/java/com/caoccao/javet/swc4j/ast/clazz/Swc4jAstClass.java)

---

## AST Structure

```java
public class Swc4jAstClass extends Swc4jAst {
    // Class body - list of class members (methods, fields, constructors, etc.)
    protected final List<ISwc4jAstClassMember> body;

    // Superclass expression (for inheritance)
    protected Optional<ISwc4jAstExpr> superClass;

    // Decorators applied to the class
    protected final List<Swc4jAstDecorator> decorators;

    // List of interfaces the class implements
    protected final List<Swc4jAstTsExprWithTypeArgs> _implements;

    // Whether the class is abstract
    protected boolean _abstract;

    // Type parameters for generic classes
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;

    // Type parameters for superclass instantiation
    protected Optional<Swc4jAstTsTypeParamInstantiation> superTypeParams;

    // Syntax context
    protected int ctxt;
}
```

### Class Members (ISwc4jAstClassMember)

1. **Swc4jAstClassMethod** - Instance and static methods
2. **Swc4jAstClassProp** - Instance properties/fields
3. **Swc4jAstPrivateMethod** - Private methods (#method)
4. **Swc4jAstPrivateProp** - Private properties (#prop)
5. **Swc4jAstConstructor** - Class constructor
6. **Swc4jAstStaticBlock** - Static initialization blocks
7. **Swc4jAstTsIndexSignature** - TypeScript index signatures
8. **Swc4jAstAutoAccessor** - Auto-accessor decorators
9. **Swc4jAstEmptyStmt** - Empty statements (semicolons)

---

## Test File Splitting Proposal

### Current State

Existing tests in `TestCompileAstClass.java`:
1. `testMultipleClassesWithTypeInfer` - Tests class calling another class without explicit types
2. `testMultipleClassesWithoutTypeInfer` - Tests class calling another class with explicit types

### Proposed Split

**1. TestCompileAstClassBasic.java** (from existing tests + new)
- Basic class definition
- Class with single method
- Class with multiple methods
- Class with no methods (empty body)
- Multiple classes in same namespace
- Class with type inference
- Class without type inference (explicit types)

**2. TestCompileAstClassInheritance.java** (new)
- Class extending another class
- Calling super methods
- Overriding methods
- Multi-level inheritance (A extends B extends C)
- Accessing inherited fields
- Constructor chaining with super()

**3. TestCompileAstClassAbstract.java** (new)
- Abstract class declaration
- Abstract method declaration
- Concrete class extending abstract class
- Abstract class with concrete methods
- Cannot instantiate abstract class

**4. TestCompileAstClassImplements.java** (new)
- Class implementing single interface
- Class implementing multiple interfaces
- Class extending and implementing
- Interface method implementation validation

**5. TestCompileAstClassConstructor.java** (new)
- Default constructor (no explicit constructor)
- Constructor with parameters
- Constructor with parameter type annotations
- Constructor calling super()
- Constructor initializing fields
- Multiple constructors (overloading)
- Constructor with default parameter values

**6. TestCompileAstClassFields.java** (new)
- Instance field declaration
- Field with initializer
- Field with type annotation
- Field without initializer
- Readonly fields
- Field access from methods

**7. TestCompileAstClassStatic.java** (new)
- Static method
- Static field
- Static field with initializer
- Static block initializer
- Accessing static members from instance methods
- Accessing static members from outside class

**8. TestCompileAstClassAccessibility.java** (new)
- Public methods (default)
- Private methods
- Protected methods
- Private fields (#field)
- Private methods (#method)
- Accessing private members within class
- Attempting to access private members outside class (should fail)

**9. TestCompileAstClassDecorators.java** (new, future)
- Class with decorator
- Method with decorator
- Field with decorator
- Parameter decorators
- Multiple decorators

**10. TestCompileAstClassGenerics.java** (new, future)
- Generic class with type parameter
- Generic class with multiple type parameters
- Generic class with constraints
- Generic class instantiation
- Generic method in class

**11. TestCompileAstClassEdgeCases.java** (new)
- Empty class
- Class with only constructor
- Class with getter/setter methods
- Nested classes (if supported)
- Anonymous classes (if supported)
- Class expression (unnamed class)

---

## Implementation Phases

### Phase 1: Basic Class Declaration - Priority: HIGH

**Status:** IMPLEMENTED

- Class definition generates valid JVM class bytecode
- Class name mapping to JVM internal name
- Public class access modifier
- Default constructor generation
- Basic method generation
- 'this' expression support (stack-based for nested classes)
- Instance method calling other instance methods via 'this'

### Phase 2: Inheritance (extends) - Priority: HIGH

**Status:** IMPLEMENTED

- Superclass resolution
- Setting superclass in bytecode
- `super.method()` calls
- Constructor chaining with `super()`
- Inherited field/method access

### Phase 3: Abstract Classes - Priority: MEDIUM

**Status:** IMPLEMENTED

- ACC_ABSTRACT flag on class - IMPLEMENTED
- Abstract method declaration (no body) - IMPLEMENTED
- Validation: concrete subclass implements all abstract methods - (runtime JVM validation)
- Cannot instantiate abstract class - (runtime JVM validation)

### Phase 4: Interface Implementation - Priority: MEDIUM

**Status:** IMPLEMENTED

- `implements` clause processing - IMPLEMENTED
- Interface method implementation - IMPLEMENTED
- Multiple interface implementation - IMPLEMENTED
- Interface resolution - IMPLEMENTED (via type alias registry)

### Phase 5: Constructors - Priority: HIGH

**Status:** IMPLEMENTED

- Constructor parameter handling - IMPLEMENTED
- Field initialization in constructor - IMPLEMENTED
- super() calls with arguments - IMPLEMENTED
- Constructor overloading - IMPLEMENTED
- this() calls (constructor chaining) - IMPLEMENTED

### Phase 6: Instance Fields - Priority: HIGH

**Status:** IMPLEMENTED

- Field declaration bytecode
- Field initializers
- Field access (getfield/putfield)
- Field type annotations
- Type inference for `this.field` expressions

### Phase 7: Static Members - Priority: MEDIUM

**Status:** IMPLEMENTED

- Static field declaration - IMPLEMENTED
- Static method declaration - IMPLEMENTED
- Static field initializers - IMPLEMENTED (via <clinit>)
- Static member access (getstatic/putstatic) - IMPLEMENTED
- Explicit static blocks (`static { ... }`) - IMPLEMENTED

### Phase 7.1: Explicit Static Blocks - Priority: MEDIUM

**Status:** IMPLEMENTED (2026-01-28)

**Scope:**
- Support `Swc4jAstStaticBlock` AST node
- Generate code in `<clinit>` method for explicit static blocks
- Support multiple static blocks (executed in order)
- Support mixing static field initializers and static blocks

**Syntax:**
```typescript
class A {
  static value: int = 0

  static {
    // Explicit static initialization block
    A.value = computeInitialValue()
    console.log("Class A initialized")
  }

  static {
    // Multiple static blocks are allowed
    A.value = A.value + 1
  }
}
```

**Implementation Details:**
- Added `StaticInitItem` sealed interface to track both static field initializers and static blocks in declaration order
- Updated `ClassProcessor` to collect `Swc4jAstStaticBlock` nodes alongside static field initializers
- Updated `generateClinitMethod()` to process both field initializers and static blocks in declaration order
- Added stack map table generation for proper verification of loops/conditionals in static blocks
- Test coverage in `TestCompileAstClassStaticBlock.java`: 9 tests covering basic, multiple blocks, local variables, loops, conditionals, interleaving with fields

### Phase 8: Access Modifiers - Priority: MEDIUM

**Status:** IMPLEMENTED

- Public (default) - IMPLEMENTED
- Private methods/fields (TypeScript `private` keyword) - IMPLEMENTED
- Protected methods/fields - IMPLEMENTED
- Private class fields (`#field` ES2022 syntax) - IMPLEMENTED
- Private class methods (`#method` ES2022 syntax) - IMPLEMENTED

### Phase 8.1: ES2022 Private Methods (#method) - Priority: MEDIUM

**Status:** IMPLEMENTED (2026-01-28)

**Scope:**
- Support `Swc4jAstPrivateMethod` AST node
- Generate private methods with `#` prefix using ACC_PRIVATE flag
- Support `this.#method()` call expressions
- Support static private methods (`static #method()`)

**Syntax:**
```typescript
class A {
  #privateHelper(): int {
    return 42
  }

  static #staticPrivateHelper(): int {
    return 100
  }

  public test(): int {
    return this.#privateHelper() + A.#staticPrivateHelper()
  }
}
```

**Implementation Details:**
- Updated `ClassProcessor` to handle `Swc4jAstPrivateMethod`:
  - Extract method name from `Swc4jAstPrivateName.getName()` (without `#` prefix)
  - Set ACC_PRIVATE access flag
  - Generate method body using same pattern as regular methods
- Updated `ClassCollector` to register private method signatures for return type lookup
- Updated `MethodGenerator` to generate private methods with full support for:
  - Instance and static private methods
  - Default parameter overloads
  - Return type inference
- Updated `CallExpressionForClassProcessor` to handle `this.#method()` and `ClassName.#method()` calls:
  - Recognize `Swc4jAstPrivateName` in member expressions
  - Generate `invokespecial` for instance private method calls
  - Generate `invokestatic` for static private method calls
- Updated `TypeResolver` to infer return types for private method calls
- Test coverage in `TestCompileAstClassPrivateMethod.java`: 13 tests covering basic, parameters, multiple calls, private field access, static private methods, mixed visibility, nested calls, loops, conditionals

### Phase 9: Decorators - Priority: LOW

**Status:** NOT SUPPORTED

Decorators are intentionally not supported. The JVM bytecode compilation targets a simpler subset of TypeScript without decorator metaprogramming.

### Phase 10: Generics - Priority: LOW

**Status:** IMPLEMENTED

JVM generics use type erasure - generic type parameters are replaced with Object (or their constraint type) at runtime.

- Type parameter declaration - IMPLEMENTED
- Type parameter scope tracking during compilation - IMPLEMENTED
- Generic constraints (T extends SomeType) - IMPLEMENTED
- Type erasure to Object or constraint type - IMPLEMENTED

**Implementation Details:**
- Added `TypeParameterScope` class to track type parameters in scope
- `CompilationContext` maintains a stack of type parameter scopes for nested generic classes/methods
- `TypeResolver.mapTypeNameToDescriptor()` checks for type parameters and erases to Object or constraint type
- `ClassProcessor` pushes type parameter scope when compiling generic classes
- `MethodGenerator` pushes type parameter scope when compiling generic methods
- `ClassCollector` also pushes type parameter scope during field/method analysis

Test coverage in `TestCompileAstClassGenerics.java`: 8 tests

---

## Edge Cases and Special Scenarios

### Basic Class Edge Cases

1. **Empty Class**
   ```typescript
   class Empty { }
   ```

2. **Class with Only Constructor**
   ```typescript
   class OnlyConstructor {
     constructor(value: int) { }
   }
   ```

3. **Class with No Explicit Constructor**
   ```typescript
   class NoConstructor {
     test(): int { return 1 }
   }
   // Compiler should generate default constructor
   ```

4. **Class with Same Name in Different Namespaces**
   ```typescript
   namespace a { export class A { } }
   namespace b { export class A { } }
   ```

5. **Class Name Matching Java Reserved Word**
   ```typescript
   class Class { }  // "Class" is not reserved in TS but maps to Class.class
   class Object { }  // Conflicts with java.lang.Object
   ```

6. **Class with Unicode Name**
   ```typescript
   class 类 { }  // Unicode class name
   ```

### Inheritance Edge Cases

7. **Simple Inheritance**
   ```typescript
   class A { }
   class B extends A { }
   ```

8. **Multi-Level Inheritance**
   ```typescript
   class A { }
   class B extends A { }
   class C extends B { }
   ```

9. **Method Override**
   ```typescript
   class A { test(): int { return 1 } }
   class B extends A { test(): int { return 2 } }
   ```

10. **Calling Super Method**
    ```typescript
    class A { test(): int { return 1 } }
    class B extends A {
      test(): int { return super.test() + 1 }
    }
    ```

11. **Accessing Inherited Field**
    ```typescript
    class A { value: int = 10 }
    class B extends A {
      getValue(): int { return this.value }
    }
    ```

12. **Constructor Super Call**
    ```typescript
    class A { constructor(x: int) { } }
    class B extends A {
      constructor(x: int, y: int) {
        super(x)
      }
    }
    ```

13. **Diamond Inheritance (via interfaces)**
    ```typescript
    interface A { }
    interface B extends A { }
    interface C extends A { }
    class D implements B, C { }
    ```

14. **Extending External Class (Java class)**
    ```typescript
    class MyList extends ArrayList<int> { }
    ```

15. **Shadowing Inherited Field**
    ```typescript
    class A { value: int = 1 }
    class B extends A { value: int = 2 }  // Shadows A.value
    ```

### Abstract Class Edge Cases

16. **Abstract Class Declaration**
    ```typescript
    abstract class Shape {
      abstract area(): double
    }
    ```

17. **Abstract Class with Concrete Methods**
    ```typescript
    abstract class Base {
      abstract compute(): int
      helper(): int { return 1 }
    }
    ```

18. **Concrete Class Implementing Abstract Method**
    ```typescript
    abstract class Shape { abstract area(): double }
    class Circle extends Shape {
      area(): double { return 3.14 * 10 * 10 }
    }
    ```

19. **Abstract Class Extending Abstract Class**
    ```typescript
    abstract class A { abstract f(): void }
    abstract class B extends A { abstract g(): void }
    ```

20. **Cannot Instantiate Abstract Class**
    ```typescript
    abstract class A { }
    new A()  // Should fail at compile time
    ```

### Interface Implementation Edge Cases

21. **Single Interface Implementation**
    ```typescript
    interface ITest { test(): int }
    class A implements ITest {
      test(): int { return 1 }
    }
    ```

22. **Multiple Interface Implementation**
    ```typescript
    interface IA { a(): void }
    interface IB { b(): void }
    class C implements IA, IB {
      a(): void { }
      b(): void { }
    }
    ```

23. **Extends and Implements**
    ```typescript
    class A { }
    interface IB { }
    class C extends A implements IB { }
    ```

24. **Interface with Generic Parameters**
    ```typescript
    interface Container<T> { get(): T }
    class Box implements Container<int> {
      get(): int { return 0 }
    }
    ```

25. **Interface Method Conflict (Same Signature)**
    ```typescript
    interface IA { test(): int }
    interface IB { test(): int }
    class C implements IA, IB {
      test(): int { return 1 }  // Single implementation
    }
    ```

### Constructor Edge Cases

26. **Default Constructor Generation**
    ```typescript
    class A { }
    // Should generate: public A() { super(); }
    ```

27. **Constructor with Parameters**
    ```typescript
    class A {
      constructor(x: int, y: String) { }
    }
    ```

28. **Constructor Initializing Fields**
    ```typescript
    class A {
      value: int
      constructor(v: int) {
        this.value = v
      }
    }
    ```

29. **Constructor with Default Parameter Value**
    ```typescript
    class A {
      constructor(x: int = 10) { }
    }
    ```

30. **Constructor Overloading**
    ```typescript
    class A {
      constructor()
      constructor(x: int)
      constructor(x?: int) { }
    }
    ```

31. **Private Constructor**
    ```typescript
    class Singleton {
      private constructor() { }
      static instance: Singleton = new Singleton()
    }
    ```

32. **Parameter Properties**
    ```typescript
    class A {
      constructor(public x: int, private y: String) { }
    }
    // x and y become fields automatically
    ```

33. **Super Call Must Be First**
    ```typescript
    class A { constructor(x: int) { } }
    class B extends A {
      constructor() {
        super(1)  // Must be first statement
      }
    }
    ```

### Field Edge Cases

34. **Field with Type Annotation**
    ```typescript
    class A {
      value: int
    }
    ```

35. **Field with Initializer**
    ```typescript
    class A {
      value: int = 10
    }
    ```

36. **Field with Complex Initializer**
    ```typescript
    class A {
      values: ArrayList<int> = new ArrayList<int>()
    }
    ```

37. **Readonly Field**
    ```typescript
    class A {
      readonly value: int = 10
    }
    // Can only be assigned in declaration or constructor
    ```

38. **Optional Field**
    ```typescript
    class A {
      value?: int  // May be undefined
    }
    ```

39. **Field Type Inference**
    ```typescript
    class A {
      value = 10  // Inferred as int
    }
    ```

40. **Null Field**
    ```typescript
    class A {
      value: String = null
    }
    ```

### Static Member Edge Cases

41. **Static Field**
    ```typescript
    class A {
      static count: int = 0
    }
    ```

42. **Static Method**
    ```typescript
    class A {
      static create(): A { return new A() }
    }
    ```

43. **Static Field with Complex Initializer**
    ```typescript
    class A {
      static values: ArrayList<int> = (() => {
        const list = new ArrayList<int>()
        list.add(1)
        return list
      })()
    }
    ```

44. **Static Block (clinit)**
    ```typescript
    class A {
      static {
        console.log("Class loaded")
      }
    }
    ```

45. **Accessing Static from Instance Method**
    ```typescript
    class A {
      static count: int = 0
      increment(): void { A.count++ }
    }
    ```

46. **Static Method Calling Instance Method (Error)**
    ```typescript
    class A {
      instance(): void { }
      static test(): void {
        this.instance()  // Error: 'this' not available
      }
    }
    ```

### Access Modifier Edge Cases

47. **Private Method**
    ```typescript
    class A {
      private helper(): int { return 1 }
      public test(): int { return this.helper() }
    }
    ```

48. **Protected Method**
    ```typescript
    class A {
      protected helper(): int { return 1 }
    }
    class B extends A {
      test(): int { return this.helper() }
    }
    ```

49. **Private Field (ES2022 syntax)**
    ```typescript
    class A {
      #value: int = 10
      getValue(): int { return this.#value }
    }
    ```

50. **Private Method (ES2022 syntax)**
    ```typescript
    class A {
      #helper(): int { return 1 }
      test(): int { return this.#helper() }
    }
    ```

51. **Accessing Private from Outside (Error)**
    ```typescript
    class A { private value: int = 1 }
    const a = new A()
    a.value  // Error: private
    ```

### Getter/Setter Edge Cases

52. **Getter Method**
    ```typescript
    class A {
      private _value: int = 0
      get value(): int { return this._value }
    }
    ```

53. **Setter Method**
    ```typescript
    class A {
      private _value: int = 0
      set value(v: int) { this._value = v }
    }
    ```

54. **Getter and Setter Pair**
    ```typescript
    class A {
      private _value: int = 0
      get value(): int { return this._value }
      set value(v: int) { this._value = v }
    }
    ```

55. **Read-Only Property (Getter Only)**
    ```typescript
    class A {
      get computed(): int { return 1 + 2 }
    }
    ```

### Generic Class Edge Cases

56. **Simple Generic Class**
    ```typescript
    class Box<T> {
      value: T
      constructor(v: T) { this.value = v }
    }
    ```

57. **Generic Class with Constraint**
    ```typescript
    class NumberBox<T extends Number> {
      value: T
    }
    ```

58. **Generic Class with Multiple Type Parameters**
    ```typescript
    class Pair<K, V> {
      key: K
      value: V
    }
    ```

59. **Generic Class Instantiation**
    ```typescript
    const box = new Box<int>(10)
    ```

60. **Generic Method in Generic Class**
    ```typescript
    class Container<T> {
      transform<R>(f: (t: T) => R): R {
        return f(this.value)
      }
    }
    ```

### Decorator Edge Cases

61. **Class Decorator**
    ```typescript
    @sealed
    class A { }
    ```

62. **Method Decorator**
    ```typescript
    class A {
      @log
      test(): void { }
    }
    ```

63. **Field Decorator**
    ```typescript
    class A {
      @readonly
      value: int = 10
    }
    ```

64. **Multiple Decorators**
    ```typescript
    @decorator1
    @decorator2
    class A { }
    ```

65. **Decorator Factory**
    ```typescript
    @logger("debug")
    class A { }
    ```

### Special Scenarios

66. **Class Expression (Anonymous Class)**
    ```typescript
    const A = class {
      test(): int { return 1 }
    }
    ```

67. **Class in Function Scope**
    ```typescript
    function create() {
      class Local { }
      return new Local()
    }
    ```

68. **Self-Referencing Class**
    ```typescript
    class Node {
      next: Node | null = null
    }
    ```

69. **Circular Class References**
    ```typescript
    class A { b: B }
    class B { a: A }
    ```

70. **Class with toString/equals Override**
    ```typescript
    class A {
      toString(): String { return "A" }
      equals(other: Object): boolean { return other instanceof A }
    }
    ```

### Method Scenarios

71. **Method Returning void**
    ```typescript
    class A {
      doSomething(): void { }
    }
    ```

72. **Method Returning this (Fluent API)**
    ```typescript
    class Builder {
      setValue(v: int): Builder {
        return this
      }
    }
    ```

73. **Method with Varargs**
    ```typescript
    class A {
      sum(...values: int[]): int {
        let total = 0
        for (const v of values) total += v
        return total
      }
    }
    ```

74. **Method Overloading**
    ```typescript
    class A {
      test(x: int): int
      test(x: String): String
      test(x: int | String): int | String { }
    }
    ```

75. **Method with Default Parameters**
    ```typescript
    class A {
      greet(name: String = "World"): String {
        return "Hello, " + name
      }
    }
    ```

### Instantiation Edge Cases

76. **new ClassName()**
    ```typescript
    const a = new A()
    ```

77. **new with Constructor Arguments**
    ```typescript
    const a = new A(1, "test")
    ```

78. **new in Return Statement**
    ```typescript
    class A {
      clone(): A { return new A() }
    }
    ```

79. **new in Field Initializer**
    ```typescript
    class A {
      child: A = new A()  // May cause infinite recursion
    }
    ```

80. **Conditional new**
    ```typescript
    const a = condition ? new A() : new B()
    ```

---

## Class Name Mapping

### Classes Without Namespace

Classes defined without a namespace are directly accessible in the bytecode map using their simple name:

```typescript
// No namespace
class A {
  test(): int { return 123 }
}

// Access via: byteCodeMap.get("A")
```

### Classes With Namespace

Classes defined within a namespace use the fully qualified name:

```typescript
namespace com.example {
  export class A {
    test(): int { return 123 }
  }
}

// Access via: byteCodeMap.get("com.example.A")
```

### Classes With Package Prefix Option

When a package prefix is configured via `ByteCodeCompilerOptions.packagePrefix()`:

```typescript
// With packagePrefix = "org.app"
class A {
  test(): int { return 123 }
}

// Access via: byteCodeMap.get("org.app.A")
```

---

## Bytecode Patterns

### Class Declaration

```
// For: class A { }
ClassWriter:
  access_flags: ACC_PUBLIC
  this_class: "namespace/A"
  super_class: "java/lang/Object"

// Default constructor:
  <init>()V:
    aload_0
    invokespecial java/lang/Object.<init>()V
    return
```

### Inheritance

```
// For: class B extends A { }
ClassWriter:
  super_class: "namespace/A"  // Instead of java/lang/Object

// Constructor:
  <init>()V:
    aload_0
    invokespecial namespace/A.<init>()V  // Call super()
    return
```

### Static Field

```
// For: static count: int = 0
Field:
  access_flags: ACC_PUBLIC | ACC_STATIC
  name: "count"
  descriptor: "I"

// Static initializer:
  <clinit>()V:
    iconst_0
    putstatic namespace/A.count:I
    return
```

---

## Success Criteria

- [x] Phase 1: Basic class declaration working
- [x] Phase 2: Class inheritance (extends) working
- [x] Phase 3: Abstract classes working
- [x] Phase 4: Interface implementation working
- [x] Phase 5: Constructors fully working
- [x] Phase 6: Instance fields working
- [x] Phase 7: Static members working (methods and fields)
- [x] Phase 7.1: Explicit Static Blocks (`static { }` syntax) - IMPLEMENTED (2026-01-28)
- [x] Phase 8: Access modifiers working (TypeScript private/protected keywords)
- [x] Phase 8.1: ES2022 Private Methods (#method syntax) - IMPLEMENTED (2026-01-28)
- [x] Phase 9: Decorators - NOT SUPPORTED (intentionally excluded)
- [x] Phase 10: Generics working (type erasure)
- [x] All current tests passing
- [x] Javadoc builds successfully

### Implementation Notes (2026-01-25)

**'this' Expression Support (Stack-based):**
- `CompilationContext` uses a `Stack<String> classStack` for tracking the current class internal name
- Supports nested classes via `pushClass()`/`popClass()` methods
- `ClassProcessor` uses try/finally to ensure proper stack cleanup

**Primitive Array Iteration Fix:**
- The `StackMapProcessor` was missing array operation handlers, causing compilation to hang when iterating primitive arrays inside for loops
- Fixed by adding handlers for `iaload`, `laload`, `faload`, `daload`, `aaload`, `baload`, `caload`, `saload`, `arraylength`, `newarray`, and `anewarray` instructions
- This fix enables methods that iterate over primitive arrays (e.g., `int[]`, `double[]`) to compile correctly

**Instance Fields Support (2026-01-25):**
- Added `FieldInfo` record class to store field metadata (name, descriptor, isStatic, initializer)
- Updated `JavaTypeInfo` to store fields and provide field lookup
- Updated `ClassCollector` to collect field information during class pre-processing
- Updated `ClassProcessor` to generate field declarations and initialize fields in constructor
- Updated `MemberExpressionProcessor` for `this.field` access (getfield instruction)
- Updated `AssignExpressionProcessor` for `this.field = value` assignment (putfield instruction)
- Updated `TypeResolver` to infer types for `this.field` expressions
- Added `getfield`/`putfield`/`getstatic`/`putstatic` handlers to `StackMapProcessor`
- Class registry lookup uses fallback pattern: try qualified name first, then simple name

**Inheritance (extends) Support (2026-01-25):**
- Updated `ClassProcessor.resolveSuperClass()` to resolve parent class from AST
- Updated `ClassWriter` constructor call to pass superclass internal name
- Updated constructor generation to call parent class `<init>` instead of Object.<init>
- Added `super.method()` call support in `CallExpressionProcessor` using `invokespecial`
- Added `resolveSuperClass()` method to `ScopedJavaTypeRegistry`
- Added inherited field lookup in `MemberExpressionProcessor.lookupFieldInHierarchy()`
- Added inherited field lookup in `TypeResolver.lookupFieldInHierarchy()`
- Updated `ScopedJavaTypeRegistry.resolveClassMethodReturnType()` to search parent classes
- Type inference for `super.method()` expressions in `TypeResolver`

**Explicit Constructor Support (2026-01-25):**
- Added `generateExplicitConstructor()` method in `ClassProcessor` to handle `Swc4jAstConstructor`
- Added `extractParameterName()` method in `TypeResolver` for constructor parameter name extraction
- Added `generateSuperConstructorCall()` method in `CallExpressionProcessor` for `super()` calls
- Constructor parameter allocation uses the same slot allocation system as method parameters
- Implicit `super()` injection when constructor body doesn't start with explicit `super()` call
- Multi-level inheritance with `super(args)` chaining is fully supported

**Static Fields Support (2026-01-25):**
- Added `generateClinitMethod()` in `ClassProcessor` for static field initialization
- Added static field read support in `MemberExpressionProcessor` using `getstatic`
- Added static field write support in `AssignExpressionProcessor` using `putstatic`
- Added static field type inference in `TypeResolver` for `ClassName.staticField` access
- Static fields are collected and initialized in `<clinit>` method

**Constructor Overloading and this() Calls (2026-01-25):**
- Updated `ClassProcessor.generateBytecode()` to collect ALL explicit constructors (not just first)
- Each constructor is generated via `generateExplicitConstructor()` with its own descriptor
- Added `generateThisConstructorCall()` in `CallExpressionProcessor` for `this()` calls
- Updated `ClassProcessor` to detect both `super()` and `this()` calls before injecting implicit `super()`
- Constructor chaining uses `invokespecial` to call another constructor in the same class
- Method descriptor built from argument types for overload resolution

**Abstract Classes Support (2026-01-25):**
- Updated `ClassProcessor.generateBytecode()` to set ACC_ABSTRACT (0x0400) flag when `clazz.isAbstract()` is true
- Added `generateAbstractMethod()` in `MethodGenerator` for abstract method declarations
- Abstract methods have ACC_ABSTRACT flag and no Code attribute (code = null)
- Updated `TypeResolver.analyzeReturnType()` to handle null body for abstract methods
- JVM validates at runtime that abstract classes cannot be instantiated and concrete subclasses implement all abstract methods

**Interface Implementation (Phase 4) Support (2026-01-25):**
- Updated `ClassWriter` to support interfaces:
  - Added `List<String> interfaces` field to store interface names
  - Added `addInterface(String interfaceInternalName)` method
  - Updated `toByteArray()` to pre-add interfaces to constant pool and write interface indexes
- Added `resolveInterfaces()` method in `ClassProcessor`:
  - Iterates over `clazz.getImplements()` list of `Swc4jAstTsExprWithTypeArgs`
  - Extracts interface name from `expr` (Swc4jAstIdent)
  - Resolves via type alias registry, Java type registry, or uses simple name as fallback
  - Calls `classWriter.addInterface()` with the resolved internal name
- Interface resolution requires interfaces to be registered in the type alias map (e.g., `"Runnable" -> "java.lang.Runnable"`)
- Supports single interface, multiple interfaces, and class extends + implements
- Test coverage in `TestCompileAstClassImplements.java`: 4 tests covering single/multiple interfaces, extends+implements, and method implementation

**Access Modifiers (Phase 8) Support (2026-01-25):**
- Updated `MethodGenerator`:
  - Added import for `Swc4jAstAccessibility`
  - Added `getAccessFlags()` helper method to convert `Swc4jAstAccessibility` enum to JVM access flags:
    - `Public` → `ACC_PUBLIC` (0x0001)
    - `Protected` → `ACC_PROTECTED` (0x0004)
    - `Private` → `ACC_PRIVATE` (0x0002)
  - Updated regular method generation to use `getAccessFlags(method.getAccessibility())` instead of hardcoded ACC_PUBLIC
  - Updated abstract method generation similarly
- Updated `ClassProcessor`:
  - Added same `getAccessFlags()` helper method
  - Updated field generation to use `getAccessFlags(prop.getAccessibility())` instead of hardcoded ACC_PUBLIC
- Default behavior unchanged: when `accessibility` is empty (not specified), defaults to ACC_PUBLIC
- Test coverage in `TestCompileAstClassAccessibility.java`: 6 tests covering private/protected fields and methods

**ES2022 Private Fields Support (2026-01-25):**
- Added support for ES2022 private fields (`#field` syntax) - both instance and static
- Updated `ClassCollector` to register private field metadata via `Swc4jAstPrivateProp`
- Updated `ClassProcessor` to generate private fields with ACC_PRIVATE flag
- Updated `MemberExpressionProcessor` to handle `this.#field` and `ClassName.#field` access
- Updated `AssignExpressionProcessor` to handle `this.#field = value` and `ClassName.#field = value` assignment
- Updated `TypeResolver.inferTypeFromExpr` to properly infer types for private field access (instance and static)
- Private fields are stored without the `#` prefix in JVM bytecode
- Test coverage in `TestCompileAstClassPrivateFields.java`: 12 tests covering:
  - Instance private fields: basic read/write, types, multiple fields, binary operations
  - Static private fields: basic read/write, types, counter pattern
  - Mixed instance and static private fields

---

## Known Limitations

1. **Nested Classes**: Inner classes may have limited support
2. **Anonymous Classes**: Class expressions may not be fully supported
3. **Decorators**: Intentionally not supported
4. **Generics**: Fully supported with type erasure (type parameters erase to Object or constraint type)
5. **Private Fields (#)**: ES2022 private fields fully supported (both instance and static)
6. **Private Methods (#)**: ES2022 private methods fully supported (both instance and static)
7. **Static Blocks**: Explicit static blocks (`static { ... }`) fully supported
8. **Multiple Inheritance**: Only single class inheritance (Java limitation)
9. **Dynamic Class Loading**: Not supported at compile time
10. **Generic Type Constraints**: Simple constraints (T extends SomeType) are supported; complex constraints may need fully qualified class names

---

## References

- **JVM Specification:** Chapter 4.1 - ClassFile Structure
- **JVM Specification:** Chapter 4.5 - Fields
- **JVM Specification:** Chapter 4.6 - Methods
- **TypeScript Specification:** Classes
- **ECMAScript Specification:** Class Definitions
- **Existing Implementation:** ClassProcessor.java, MethodGenerator.java
