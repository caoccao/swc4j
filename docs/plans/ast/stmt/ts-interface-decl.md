# TypeScript Interface Declaration - Implementation Plan

## Overview

This plan covers the implementation of `Swc4jAstTsInterfaceDecl` for compiling TypeScript interface declarations to JVM bytecode. TypeScript interfaces are compiled to Java interfaces with appropriate getters, setters, and method signatures.

**Current Status:** NOT IMPLEMENTED

**Syntax:**
```typescript
interface Person {
  name: string
  age: number
  readonly id: string
  email?: string
  greet(message: string): void
}

interface Employee extends Person {
  department: string
  salary: number
}

interface Container<T> {
  value: T
  getValue(): T
}
```

**Implementation File(s):**
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/stmt/TsInterfaceGenerator.java`

**Test Files:**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/tsinterfacedecl/TestCompileAstTsInterfaceDeclBasic.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/tsinterfacedecl/TestCompileAstTsInterfaceDeclInheritance.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/tsinterfacedecl/TestCompileAstTsInterfaceDeclGenerics.java`
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/stmt/tsinterfacedecl/TestCompileAstTsInterfaceDeclMethods.java`

**AST Definition:**
- `src/main/java/com/caoccao/javet/swc4j/ast/stmt/Swc4jAstTsInterfaceDecl.java`

---

## Interface Declaration Fundamentals

### TypeScript to Java Mapping

TypeScript interfaces map to Java interfaces. The key transformation rules are:

1. **Property Signature** (`name: string`) becomes:
   - Getter method: `String getName()`
   - Setter method: `void setName(String name)`
   - Note: Java interfaces don't have fields, but implementing classes will need backing fields

2. **Readonly Property** (`readonly id: string`) becomes:
   - Getter method only: `String getId()`
   - No setter method

3. **Optional Property** (`email?: string`) becomes:
   - Same as regular property but documented as optional
   - Implementing classes may return `null`

4. **Method Signature** (`greet(message: string): void`) becomes:
   - Interface method: `void greet(String message)`

5. **Interface Extension** (`extends Person`) becomes:
   - Java interface extension: `extends Person`

### JVM Bytecode Strategy

Java interfaces are compiled with:
- Access flags: `ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT`
- Version: Java 17 (61.0)
- All methods are implicitly `public abstract`

**Example Interface Bytecode:**
```
// TypeScript: interface Person { name: string; readonly id: string; greet(): void }
// Java equivalent: interface Person { String getName(); void setName(String); String getId(); void greet(); }

.class public interface abstract com/example/Person
.super java/lang/Object

; Getter for 'name' property
.method public abstract getName()Ljava/lang/String;
.end method

; Setter for 'name' property
.method public abstract setName(Ljava/lang/String;)V
.end method

; Getter for 'readonly id' property (no setter)
.method public abstract getId()Ljava/lang/String;
.end method

; Method signature
.method public abstract greet()V
.end method
```

---

## AST Structure

### Swc4jAstTsInterfaceDecl

```java
public class Swc4jAstTsInterfaceDecl extends Swc4jAst implements ISwc4jAstDecl {
    protected Swc4jAstTsInterfaceBody body;      // Interface body with members
    protected boolean declare;                    // 'declare' keyword present
    protected Swc4jAstIdent id;                   // Interface name
    protected List<Swc4jAstTsExprWithTypeArgs> _extends;  // Extended interfaces
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;  // Generic type params
}
```

### Swc4jAstTsInterfaceBody

```java
public class Swc4jAstTsInterfaceBody extends Swc4jAst {
    protected List<ISwc4jAstTsTypeElement> body;  // Interface members
}
```

### ISwc4jAstTsTypeElement Implementations

| Type | Description | Example |
|------|-------------|---------|
| `Swc4jAstTsPropertySignature` | Property declaration | `name: string` |
| `Swc4jAstTsMethodSignature` | Method declaration | `greet(): void` |
| `Swc4jAstTsIndexSignature` | Index signature | `[key: string]: any` |
| `Swc4jAstTsCallSignatureDecl` | Call signature | `(): void` |
| `Swc4jAstTsConstructSignatureDecl` | Constructor signature | `new (): Type` |
| `Swc4jAstTsGetterSignature` | Getter signature | `get prop(): Type` |
| `Swc4jAstTsSetterSignature` | Setter signature | `set prop(v: Type)` |

### Swc4jAstTsPropertySignature

```java
public class Swc4jAstTsPropertySignature extends Swc4jAst implements ISwc4jAstTsTypeElement {
    protected ISwc4jAstExpr key;                  // Property name
    protected boolean readonly;                   // Readonly modifier
    protected boolean optional;                   // Optional property (?)
    protected boolean computed;                   // Computed property name
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;  // Type annotation
}
```

### Swc4jAstTsMethodSignature

```java
public class Swc4jAstTsMethodSignature extends Swc4jAst implements ISwc4jAstTsTypeElement {
    protected ISwc4jAstExpr key;                  // Method name
    protected List<ISwc4jAstTsFnParam> params;    // Parameters
    protected boolean optional;                   // Optional method (?)
    protected boolean computed;                   // Computed method name
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;  // Return type
    protected Optional<Swc4jAstTsTypeParamDecl> typeParams;  // Generic params
}
```

---

## Implementation Phases

### Phase 1: Basic Interface with Properties - Priority: HIGH

**Status:** NOT IMPLEMENTED

**Scope:**
- Simple interface with property signatures
- Generate getter methods for all properties
- Generate setter methods for non-readonly properties
- Basic type mapping (string, number, boolean)

**Example:**
```typescript
interface Person {
  name: string
  age: number
  active: boolean
}
```

**Generated Java Interface:**
```java
public interface Person {
    String getName();
    void setName(String name);
    int getAge();
    void setAge(int age);
    boolean isActive();    // 'is' prefix for boolean
    void setActive(boolean active);
}
```

**Example Bytecode:**
```
.class public interface abstract com/Person
.super java/lang/Object

.method public abstract getName()Ljava/lang/String;
.end method

.method public abstract setName(Ljava/lang/String;)V
.end method

.method public abstract getAge()I
.end method

.method public abstract setAge(I)V
.end method

.method public abstract isActive()Z
.end method

.method public abstract setActive(Z)V
.end method
```

**Test Coverage:**
1. Interface with single string property
2. Interface with single int property
3. Interface with single boolean property
4. Interface with multiple properties of same type
5. Interface with mixed primitive types
6. Interface with object type properties

---

### Phase 2: Readonly Properties - Priority: HIGH

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle `readonly` modifier on properties
- Generate only getter methods (no setters)

**Example:**
```typescript
interface Entity {
  readonly id: string
  readonly createdAt: number
  name: string  // not readonly, has setter
}
```

**Generated Java Interface:**
```java
public interface Entity {
    String getId();      // No setter
    long getCreatedAt(); // No setter
    String getName();
    void setName(String name);
}
```

**Test Coverage:**
7. Interface with single readonly property
8. Interface with all readonly properties
9. Interface with mix of readonly and mutable properties
10. Readonly property with primitive type
11. Readonly property with object type

---

### Phase 3: Optional Properties - Priority: MEDIUM

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle optional properties (`?`)
- Generate same methods as required properties
- Optional properties may return null in implementations

**Example:**
```typescript
interface User {
  name: string
  email?: string
  phone?: string
}
```

**Generated Java Interface:**
```java
public interface User {
    String getName();
    void setName(String name);
    String getEmail();      // May return null
    void setEmail(String email);
    String getPhone();      // May return null
    void setPhone(String phone);
}
```

**Test Coverage:**
12. Interface with single optional property
13. Interface with all optional properties
14. Interface with mix of required and optional properties
15. Optional readonly property
16. Optional property with primitive type (uses boxed type)

---

### Phase 4: Method Signatures - Priority: HIGH

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle method signatures in interfaces
- Map parameters and return types
- Handle void return type

**Example:**
```typescript
interface Calculator {
  add(a: number, b: number): number
  subtract(a: number, b: number): number
  reset(): void
  getValue(): number
}
```

**Generated Java Interface:**
```java
public interface Calculator {
    int add(int a, int b);
    int subtract(int a, int b);
    void reset();
    int getValue();
}
```

**Test Coverage:**
17. Method with no parameters and void return
18. Method with no parameters and primitive return
19. Method with single primitive parameter
20. Method with multiple primitive parameters
21. Method with object parameter
22. Method with object return type
23. Method with mixed parameter types
24. Multiple methods in same interface

---

### Phase 5: Interface Inheritance (extends) - Priority: HIGH

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle `extends` clause for interface inheritance
- Support single interface extension
- Support multiple interface extension

**Example:**
```typescript
interface Named {
  name: string
}

interface Aged {
  age: number
}

interface Person extends Named, Aged {
  email: string
}
```

**Generated Java Interfaces:**
```java
public interface Named {
    String getName();
    void setName(String name);
}

public interface Aged {
    int getAge();
    void setAge(int age);
}

public interface Person extends Named, Aged {
    String getEmail();
    void setEmail(String email);
}
```

**Test Coverage:**
25. Interface extending single interface
26. Interface extending multiple interfaces
27. Deep inheritance chain (A extends B extends C)
28. Diamond inheritance pattern
29. Override property type in child (narrowing)
30. Inherit readonly property

---

### Phase 6: Generic Interfaces - Priority: MEDIUM

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle generic type parameters
- Map type parameters to Java generics
- Handle bounded type parameters

**Example:**
```typescript
interface Container<T> {
  value: T
  getValue(): T
  setValue(value: T): void
}

interface Comparable<T extends number> {
  compareTo(other: T): number
}
```

**Generated Java Interfaces:**
```java
public interface Container<T> {
    T getValue();
    void setValue(T value);
}

public interface Comparable<T extends Number> {
    int compareTo(T other);
}
```

**Test Coverage:**
31. Interface with single type parameter
32. Interface with multiple type parameters
33. Type parameter with extends constraint
34. Type parameter used in property
35. Type parameter used in method parameter
36. Type parameter used in method return type
37. Nested generic types (Container<List<T>>)

---

### Phase 7: Index Signatures - Priority: LOW

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle index signatures
- Map to appropriate Java patterns (Map-like interface)

**Example:**
```typescript
interface Dictionary {
  [key: string]: string
}

interface NumberMap {
  [index: number]: string
}
```

**Note:** Index signatures are challenging to map directly to Java interfaces. Options:
1. Generate `get(key)` and `put(key, value)` methods
2. Extend `Map<K, V>` interface
3. Skip index signatures (document as limitation)

**Test Coverage:**
38. String index signature
39. Number index signature
40. Readonly index signature
41. Index signature with other properties

---

### Phase 8: Call and Construct Signatures - Priority: LOW

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle call signatures (functional interfaces)
- Handle construct signatures

**Example:**
```typescript
interface Callable {
  (x: number): number
}

interface Constructable {
  new (name: string): object
}
```

**Note:** Call signatures map well to Java functional interfaces. Construct signatures are challenging.

**Test Coverage:**
42. Simple call signature
43. Call signature with multiple parameters
44. Call signature with generic
45. Construct signature (may be unsupported)

---

### Phase 9: Explicit Getter/Setter Signatures - Priority: MEDIUM

**Status:** NOT IMPLEMENTED

**Scope:**
- Handle explicit `get` and `set` accessors in interfaces
- Differentiate from auto-generated getters/setters for properties

**Example:**
```typescript
interface Accessor {
  get value(): number
  set value(v: number)
  get readonly count(): number  // getter only
}
```

**Generated Java Interface:**
```java
public interface Accessor {
    int getValue();
    void setValue(int v);
    int getCount();  // No setter
}
```

**Test Coverage:**
46. Interface with explicit getter
47. Interface with explicit setter
48. Interface with both getter and setter
49. Getter without setter (readonly)
50. Mix of explicit accessors and property signatures

---

## Edge Cases

### Property Edge Cases

1. **Property name is Java keyword**
   ```typescript
   interface Reserved {
     class: string      // 'class' is Java keyword
     default: number    // 'default' is Java keyword
     public: boolean    // 'public' is Java keyword
   }
   ```
   - Solution: Escape or rename (e.g., `getClass_()`, `getDefault_()`)

2. **Property name starts with number**
   ```typescript
   interface Numbers {
     "123abc": string
   }
   ```
   - Solution: Prefix with underscore or skip computed properties

3. **Property name with special characters**
   ```typescript
   interface Special {
     "foo-bar": string
     "hello.world": number
   }
   ```
   - Solution: Convert to camelCase or skip

4. **Property with same name as Object method**
   ```typescript
   interface Conflicting {
     toString: string    // Conflicts with Object.toString()
     hashCode: number    // Conflicts with Object.hashCode()
     equals: boolean     // Conflicts with Object.equals()
   }
   ```
   - Solution: Rename or use different accessor pattern

5. **Boolean property naming**
   ```typescript
   interface Booleans {
     isActive: boolean   // Should become isIsActive() or isActive()?
     hasValue: boolean   // Should become isHasValue() or hasValue()?
     canEdit: boolean
     active: boolean     // Should become isActive()
   }
   ```
   - Solution: Special handling for `is`, `has`, `can` prefixes

6. **Computed property names**
   ```typescript
   const key = "dynamicKey"
   interface Dynamic {
     [key]: string       // Computed property
     ["literal"]: number // String literal key
   }
   ```
   - Solution: Evaluate at compile time if possible, otherwise skip

7. **Symbol property keys**
   ```typescript
   interface WithSymbol {
     [Symbol.iterator]: () => Iterator<number>
   }
   ```
   - Solution: Skip symbol keys (not supported in Java)

8. **Property with union type**
   ```typescript
   interface Union {
     value: string | number
     status: "active" | "inactive"
   }
   ```
   - Solution: Use common supertype (Object) or first type

9. **Property with intersection type**
   ```typescript
   interface Intersection {
     data: TypeA & TypeB
   }
   ```
   - Solution: Complex - may need to skip or use Object

10. **Property with array type**
    ```typescript
    interface WithArrays {
      items: string[]
      numbers: Array<number>
      matrix: number[][]
    }
    ```
    - Solution: Map to `String[]`, `int[]`, `int[][]` or `List<T>`

11. **Property with tuple type**
    ```typescript
    interface WithTuple {
      pair: [string, number]
      triple: [number, string, boolean]
    }
    ```
    - Solution: Map to `Object[]` or skip

12. **Property with function type**
    ```typescript
    interface WithFunction {
      callback: (x: number) => void
      transformer: (input: string) => string
    }
    ```
    - Solution: Map to appropriate functional interface

13. **Property with literal type**
    ```typescript
    interface WithLiteral {
      type: "user"           // String literal
      code: 200              // Number literal
      success: true          // Boolean literal
    }
    ```
    - Solution: Use base type (String, int, boolean)

14. **Nullable property type**
    ```typescript
    interface Nullable {
      value: string | null
      data: number | undefined
    }
    ```
    - Solution: Mark as optional, use boxed types

15. **Property with 'any' type**
    ```typescript
    interface WithAny {
      data: any
      value: unknown
    }
    ```
    - Solution: Map to `Object`

### Method Edge Cases

16. **Method with overloads**
    ```typescript
    interface Overloaded {
      process(x: string): string
      process(x: number): number
    }
    ```
    - Solution: Generate both overloads (valid in Java interfaces)

17. **Method with optional parameters**
    ```typescript
    interface OptionalParams {
      greet(name: string, greeting?: string): void
    }
    ```
    - Solution: Generate single method or multiple overloads

18. **Method with default parameters**
    ```typescript
    interface DefaultParams {
      format(value: number, decimals: number = 2): string
    }
    ```
    - Solution: Ignore defaults in interface (implementation handles it)

19. **Method with rest parameters**
    ```typescript
    interface RestParams {
      sum(...numbers: number[]): number
    }
    ```
    - Solution: Map to varargs `int... numbers`

20. **Method with 'this' parameter**
    ```typescript
    interface Fluent {
      setName(this: Fluent, name: string): this
    }
    ```
    - Solution: Ignore `this` parameter type annotation

21. **Generic method in non-generic interface**
    ```typescript
    interface NonGeneric {
      identity<T>(value: T): T
    }
    ```
    - Solution: Generate generic method `<T> T identity(T value)`

22. **Method with complex return type**
    ```typescript
    interface Complex {
      getData(): Promise<string>
      getItems(): Map<string, number>
    }
    ```
    - Solution: Map to Java equivalents or Object

23. **Async method**
    ```typescript
    interface AsyncInterface {
      fetchData(): Promise<string>
      async loadUser(): Promise<User>
    }
    ```
    - Solution: Map `Promise<T>` to `CompletableFuture<T>` or just `T`

24. **Method name is Java keyword**
    ```typescript
    interface Keywords {
      new(name: string): void    // 'new' is keyword
      default(): void            // 'default' is keyword
    }
    ```
    - Solution: Rename with suffix

### Inheritance Edge Cases

25. **Circular inheritance (error in TS)**
    ```typescript
    interface A extends B { }
    interface B extends A { }  // Error
    ```
    - Solution: Should be caught by TypeScript, but handle gracefully

26. **Extending non-interface type**
    ```typescript
    type AliasedInterface = { name: string }
    interface Extended extends AliasedInterface { }
    ```
    - Solution: Resolve type alias first

27. **Extending generic interface with concrete type**
    ```typescript
    interface StringContainer extends Container<string> { }
    ```
    - Solution: Generate `extends Container<String>`

28. **Multiple inheritance with conflicting properties**
    ```typescript
    interface A { value: string }
    interface B { value: number }
    interface C extends A, B { }  // Error in TS
    ```
    - Solution: Should be caught by TypeScript

29. **Inheriting from interface with same method**
    ```typescript
    interface Base { getName(): string }
    interface Child extends Base { getName(): string }
    ```
    - Solution: Single method in output (valid in Java)

30. **Extending interface with additional type parameters**
    ```typescript
    interface Base<T> { value: T }
    interface Extended<T, U> extends Base<T> { extra: U }
    ```

### Generic Edge Cases

31. **Multiple type parameters with same constraint**
    ```typescript
    interface Multi<T extends Comparable, U extends Comparable> { }
    ```

32. **Recursive type parameter constraint**
    ```typescript
    interface Recursive<T extends Recursive<T>> {
      compareTo(other: T): number
    }
    ```
    - Solution: Map carefully to Java's recursive generics

33. **Type parameter with multiple constraints**
    ```typescript
    interface Multi<T extends A & B> { }
    ```
    - Solution: Java supports this with `T extends A & B`

34. **Type parameter used as constraint**
    ```typescript
    interface Keyed<K, V extends Map<K, any>> { }
    ```

35. **Default type parameter**
    ```typescript
    interface WithDefault<T = string> { value: T }
    ```
    - Solution: Java doesn't support default type args - skip default

36. **Variance annotations (in/out)**
    ```typescript
    interface Producer<out T> { get(): T }
    interface Consumer<in T> { accept(value: T): void }
    ```
    - Solution: Java uses use-site variance - ignore declaration-site

### Modifier Edge Cases

37. **Declare modifier**
    ```typescript
    declare interface External { }
    ```
    - Solution: Generate same as regular interface

38. **Export modifier**
    ```typescript
    export interface Exported { }
    ```
    - Solution: Always generate public interface

39. **Interface in namespace**
    ```typescript
    namespace MyLib {
      export interface Config { }
    }
    ```
    - Solution: Use namespace as package prefix

40. **Interface merging (declaration merging)**
    ```typescript
    interface User { name: string }
    interface User { age: number }
    // Merged: interface User { name: string; age: number }
    ```
    - Solution: Merge before code generation or process in order

### Special Interface Patterns

41. **Empty interface**
    ```typescript
    interface Empty { }
    ```
    - Solution: Generate empty Java interface (marker interface)

42. **Interface with only methods (no properties)**
    ```typescript
    interface Service {
      start(): void
      stop(): void
    }
    ```

43. **Interface with only properties (no methods)**
    ```typescript
    interface Data {
      name: string
      value: number
    }
    ```

44. **Hybrid interface (call + properties)**
    ```typescript
    interface Hybrid {
      (x: number): number     // Callable
      value: string           // Property
    }
    ```
    - Solution: Complex - may need synthetic interface pattern

45. **Interface extending function type**
    ```typescript
    interface MyFunc extends Function {
      customProp: string
    }
    ```

46. **Interface with static members (not standard TS)**
    ```typescript
    // Not directly supported in TS interfaces
    ```

### Type Annotation Edge Cases

47. **Missing type annotation**
    ```typescript
    interface Untyped {
      value;  // No type annotation
    }
    ```
    - Solution: Infer as `Object` or `any`

48. **Complex nested type**
    ```typescript
    interface Nested {
      data: Map<string, List<Map<number, string>>>
    }
    ```

49. **Conditional type in property**
    ```typescript
    interface Conditional<T> {
      value: T extends string ? string : number
    }
    ```
    - Solution: Evaluate at compile time if possible, else use Object

50. **Mapped type (not in interface directly)**
    ```typescript
    type Mapped<T> = { [K in keyof T]: T[K] }
    ```
    - Note: Not applicable to interface declarations directly

### Index Signature Edge Cases

51. **Multiple index signatures**
    ```typescript
    interface Multi {
      [key: string]: any
      [index: number]: string  // Must be subtype of string index
    }
    ```

52. **Index signature with other properties**
    ```typescript
    interface Mixed {
      [key: string]: any
      name: string   // Must be compatible with index signature
      count: number  // Must be compatible
    }
    ```

53. **Readonly index signature**
    ```typescript
    interface ReadonlyDict {
      readonly [key: string]: string
    }
    ```

### Naming Convention Edge Cases

54. **Interface name conflicts with Java class**
    ```typescript
    interface String { }   // Conflicts with java.lang.String
    interface Object { }   // Conflicts with java.lang.Object
    interface Class { }    // Conflicts with java.lang.Class
    ```
    - Solution: Use fully qualified names or rename

55. **Interface name with generics in name**
    ```typescript
    interface "List<T>" { }  // Invalid TS, but handle gracefully
    ```

56. **Unicode interface name**
    ```typescript
    interface 用户 {
      名前: string
    }
    ```
    - Solution: Java supports Unicode identifiers

57. **Interface name starting with lowercase**
    ```typescript
    interface myInterface { }  // Convention violation
    ```
    - Solution: Convert to PascalCase or preserve

### Registry Edge Cases

58. **Interface referenced before declaration**
    ```typescript
    interface Child extends Parent { }  // Parent not yet declared
    interface Parent { name: string }
    ```
    - Solution: Two-pass processing - register all interfaces first, then generate bytecode

59. **Interface with same name in different namespaces**
    ```typescript
    namespace A {
      export interface Config { host: string }
    }
    namespace B {
      export interface Config { port: number }
    }
    ```
    - Solution: Use fully qualified names in registries (`A.Config`, `B.Config`)

60. **Interface shadowing imported type**
    ```typescript
    import { List } from 'java.util'
    interface List { }  // Shadows imported List
    ```
    - Solution: Local interface takes precedence in type alias registry

61. **Circular interface references**
    ```typescript
    interface Node {
      children: Node[]
      parent: Node
    }
    ```
    - Solution: Register interface first, then resolve self-references

62. **Interface extending interface from different namespace**
    ```typescript
    namespace models {
      export interface Base { id: string }
    }
    namespace views {
      import { Base } from 'models'
      export interface View extends Base { render(): void }
    }
    ```
    - Solution: Resolve `Base` through type alias registry to `models.Base`

63. **Generic interface instantiation in registry**
    ```typescript
    interface Container<T> { value: T }
    interface StringContainer extends Container<string> { }
    ```
    - Solution: Store generic interface with type parameters; instantiate when extending

64. **Interface method overload resolution**
    ```typescript
    interface Processor {
      process(x: string): string
      process(x: number): number
    }
    ```
    - Solution: Register both method signatures in JavaTypeInfo

65. **Interface with same name as class**
    ```typescript
    interface Person { name: string }
    class Person implements Person { name: string = "" }  // Valid in TS
    ```
    - Solution: Interface and class share same entry in type alias registry; JavaTypeInfo distinguishes via `isInterface` flag

---

## Implementation Strategy

### 1. Interface Registration

Interfaces must be registered in **both** registries:
1. **Java Type Registry** (`ScopedJavaTypeRegistry`) - For type resolution, method lookup, and field lookup
2. **Type Alias Registry** (`ScopedTypeAliasRegistry`) - For resolving the interface name to its fully qualified name

```java
// Register interface in both type registries before processing body
public void registerInterface(Swc4jAstTsInterfaceDecl decl) {
    String interfaceName = decl.getId().getSym();
    String packageName = currentPackage.replace('/', '.');
    String internalName = currentPackage + "/" + interfaceName;
    String fullyQualifiedName = packageName + "." + interfaceName;

    // 1. Register in Java Type Registry (for type resolution and method lookup)
    JavaTypeInfo typeInfo = new JavaTypeInfo(interfaceName, packageName, internalName);
    typeInfo.setInterface(true);

    // Register methods from interface body for method resolution
    for (ISwc4jAstTsTypeElement element : decl.getBody().getBody()) {
        if (element instanceof Swc4jAstTsPropertySignature prop) {
            // Register getter method
            String propName = getPropertyName(prop.getKey());
            String descriptor = resolveTypeDescriptor(prop.getTypeAnn());
            String getterName = getGetterName(propName, descriptor);
            typeInfo.addMethod(new MethodInfo(getterName, "()" + descriptor, false));

            // Register setter method (if not readonly)
            if (!prop.isReadonly()) {
                String setterName = "set" + capitalize(propName);
                typeInfo.addMethod(new MethodInfo(setterName, "(" + descriptor + ")V", false));
            }
        } else if (element instanceof Swc4jAstTsMethodSignature method) {
            String methodName = getMethodName(method.getKey());
            String methodDescriptor = buildMethodDescriptor(method.getParams(), method.getTypeAnn());
            typeInfo.addMethod(new MethodInfo(methodName, methodDescriptor, false));
        }
    }

    memory.getScopedJavaTypeRegistry().register(interfaceName, typeInfo);

    // 2. Register in Type Alias Registry (for name resolution)
    // This allows the interface name to be resolved to its fully qualified name
    memory.getScopedTypeAliasRegistry().register(interfaceName, fullyQualifiedName);
}
```

**Why Both Registries?**

| Registry | Purpose | Used For |
|----------|---------|----------|
| `ScopedJavaTypeRegistry` | Stores `JavaTypeInfo` with methods, fields, parent types | Type resolution, method calls, field access, inheritance |
| `ScopedTypeAliasRegistry` | Maps simple names to fully qualified names | Resolving type references in annotations, extends clauses, variable types |

**Example Registration Flow:**
```typescript
namespace com.example {
  export interface Person {
    name: string
    readonly id: string
    greet(): void
  }
}
```

After registration:
- **Java Type Registry**: `"Person"` → `JavaTypeInfo { internalName: "com/example/Person", methods: [getName, setName, getId, greet], isInterface: true }`
- **Type Alias Registry**: `"Person"` → `"com.example.Person"`

### 2. Interface Bytecode Generation

```java
public void generateInterface(Swc4jAstTsInterfaceDecl decl) {
    String interfaceName = decl.getId().getSym();
    ClassWriter cw = new ClassWriter();

    // Set interface flags
    int access = ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT;

    // Handle extends
    String[] interfaces = processExtends(decl.getExtends());

    // Handle type parameters
    String signature = processTypeParams(decl.getTypeParams());

    cw.visit(V17, access, internalName, signature, "java/lang/Object", interfaces);

    // Process body members
    for (ISwc4jAstTsTypeElement element : decl.getBody().getBody()) {
        processMember(cw, element);
    }

    cw.visitEnd();
    return cw.toByteArray();
}
```

### 3. Property Processing

```java
public void processPropertySignature(ClassWriter cw, Swc4jAstTsPropertySignature prop) {
    String propName = getPropertyName(prop.getKey());
    String descriptor = resolveTypeDescriptor(prop.getTypeAnn());

    // Generate getter
    String getterName = getGetterName(propName, descriptor);
    MethodVisitor mv = cw.visitMethod(
        ACC_PUBLIC | ACC_ABSTRACT,
        getterName,
        "()" + descriptor,
        null, null
    );
    mv.visitEnd();

    // Generate setter (if not readonly)
    if (!prop.isReadonly()) {
        String setterName = "set" + capitalize(propName);
        mv = cw.visitMethod(
            ACC_PUBLIC | ACC_ABSTRACT,
            setterName,
            "(" + descriptor + ")V",
            null, null
        );
        mv.visitEnd();
    }
}

private String getGetterName(String propName, String descriptor) {
    if ("Z".equals(descriptor)) {
        // Boolean: use 'is' prefix unless already prefixed
        if (propName.startsWith("is") || propName.startsWith("has") || propName.startsWith("can")) {
            return propName;
        }
        return "is" + capitalize(propName);
    }
    return "get" + capitalize(propName);
}
```

### 4. Method Processing

```java
public void processMethodSignature(ClassWriter cw, Swc4jAstTsMethodSignature method) {
    String methodName = getMethodName(method.getKey());
    String descriptor = buildMethodDescriptor(method.getParams(), method.getTypeAnn());
    String signature = buildGenericSignature(method);

    MethodVisitor mv = cw.visitMethod(
        ACC_PUBLIC | ACC_ABSTRACT,
        methodName,
        descriptor,
        signature,
        null
    );
    mv.visitEnd();
}
```

---

## Integration Points

### 1. AstProcessor Integration

```java
// In AstProcessor.java
case TsInterfaceDecl -> {
    Swc4jAstTsInterfaceDecl interfaceDecl = (Swc4jAstTsInterfaceDecl) item;
    compiler.getTsInterfaceGenerator().generate(interfaceDecl);
}
```

### 2. Type Registry Integration

Interfaces must be registered in **both** registries for proper type resolution:

#### ScopedJavaTypeRegistry
- Stores `JavaTypeInfo` containing:
  - Interface methods (getters, setters, declared methods)
  - Parent interfaces (from `extends` clause)
  - Generic type parameters
- Used for:
  - Method resolution when calling interface methods
  - Type checking when implementing interfaces
  - Inheritance chain traversal

#### ScopedTypeAliasRegistry
- Maps simple interface name to fully qualified name
- Used for:
  - Resolving type annotations (e.g., `param: Person`)
  - Resolving `extends` clause references
  - Resolving variable/field type declarations

```java
// In ByteCodeCompilerMemory.resolveType()
public JavaTypeInfo resolveType(String className, ISwc4jAst ast) {
    // First try type alias registry (for simple name -> FQN mapping)
    String resolvedType = scopedTypeAliasRegistry.resolve(className);
    if (resolvedType != null) {
        String internalName = resolvedType.replace('.', '/');
        // ... create JavaTypeInfo from resolved name
    }

    // Then try java type registry (for full type info)
    var javaTypeInfo = scopedJavaTypeRegistry.resolve(className);
    if (javaTypeInfo != null) {
        return javaTypeInfo;
    }
    // ...
}
```

### 3. Type Resolution

- `TypeResolver.resolveTypeAnnotation()` must handle interface types
- Interface type descriptor is `L<internalName>;` (e.g., `Lcom/example/Person;`)
- Generic interfaces have signatures with type parameters

### 4. Class Implementation (implements clause)

When a class implements an interface:
1. Look up interface in `ScopedJavaTypeRegistry`
2. Validate all interface methods are implemented
3. Generate bridge methods if needed for generics
4. Add interface to class's `interfaces` array in bytecode

### 5. Interface Inheritance (extends clause)

When an interface extends another interface:
1. Resolve parent interface from `ScopedTypeAliasRegistry` (name → FQN)
2. Look up parent in `ScopedJavaTypeRegistry` (FQN → JavaTypeInfo)
3. Add parent's internal name to bytecode `interfaces` array
4. Inherit parent's methods for type checking

---

## Test Plan

### Phase 1 Tests (Basic Properties)
- Tests 1-6: Basic property types and combinations

### Phase 2 Tests (Readonly)
- Tests 7-11: Readonly modifier handling

### Phase 3 Tests (Optional)
- Tests 12-16: Optional property handling

### Phase 4 Tests (Methods)
- Tests 17-24: Method signatures

### Phase 5 Tests (Inheritance)
- Tests 25-30: Interface extension

### Phase 6 Tests (Generics)
- Tests 31-37: Generic type parameters

### Phase 7-9 Tests (Advanced)
- Tests 38-50: Index signatures, call signatures, accessors

### Edge Case Tests
- Tests 51+: All documented edge cases

---

## Success Criteria

### Implementation Complete When:
- [ ] Phase 1: Basic interface with properties working
- [ ] Phase 2: Readonly properties working
- [ ] Phase 3: Optional properties working
- [ ] Phase 4: Method signatures working
- [ ] Phase 5: Interface inheritance working
- [ ] Phase 6: Generic interfaces working
- [ ] Phase 7: Index signatures (or documented as limitation)
- [ ] Phase 8: Call/construct signatures (or documented as limitation)
- [ ] Phase 9: Explicit getters/setters working
- [ ] All edge cases handled or documented as limitations
- [ ] All tests passing

### Quality Gates:
- [ ] Generated interfaces are valid Java bytecode
- [ ] Getter/setter naming follows Java conventions
- [ ] Boolean properties use 'is' prefix
- [ ] Readonly properties have no setter
- [ ] Generic interfaces compile correctly
- [ ] Interface inheritance generates correct extends clause
- [ ] Javadoc passes

---

## Known Limitations

The following TypeScript interface features are NOT supported:

1. **Symbol property keys** - Java doesn't support symbols
2. **Computed property names** (unless string literal) - Cannot evaluate at compile time
3. **Call signatures** - May be partially supported via functional interface pattern
4. **Construct signatures** - No direct Java equivalent
5. **Index signatures** - May be mapped to Map-like pattern or skipped
6. **Declaration merging** - Interfaces with same name are not merged
7. **Conditional types in properties** - Complex to evaluate
8. **Mapped types** - Not applicable to interface declarations

---

## References

- [JVM Specification - Interfaces](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html)
- [TypeScript Handbook - Interfaces](https://www.typescriptlang.org/docs/handbook/interfaces.html)
- [Java Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)
- Existing class implementation: `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/clazz/ClassGenerator.java`

---

## Notes

1. **Property vs Field**: Java interfaces don't have instance fields. Properties in TypeScript interfaces become abstract getter/setter methods. Implementing classes will have the actual backing fields.

2. **Naming Conflicts**: Care must be taken with property names that conflict with `Object` methods or Java keywords.

3. **Type Erasure**: Generic interfaces undergo type erasure same as Java, so runtime type information is limited.

4. **Optional Properties**: Java doesn't have optional properties. Optional is a documentation/convention concern for implementing classes.

5. **Default Methods**: Java 8+ interfaces can have default methods, but this plan focuses on abstract methods matching TypeScript interface semantics.
