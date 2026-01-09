# TypeScript to JVM Bytecode Compiler

## Overview

The TypeScript to JVM Bytecode Compiler is an experimental feature in swc4j that compiles TypeScript-like code directly into Java bytecode. This allows you to write TypeScript-style code that executes as native Java classes on the JVM, combining TypeScript's syntax with Java's type system and performance.

## What is This Feature?

This feature provides a compiler that:

- **Parses TypeScript-like syntax** using SWC's parser
- **Performs type inference** on variables and expressions
- **Generates JVM bytecode** that can be loaded and executed directly
- **Supports Java primitive types** (int, long, float, double, char, short) and their wrapper types (Integer, Long, Float, Double, Character, Short)
- **Enables type annotations** on variables, parameters, and return types
- **Compiles classes, methods, and expressions** into executable Java classes

### Key Capabilities

- ✅ Compile TypeScript namespaces to Java packages
- ✅ Compile TypeScript classes to Java classes
- ✅ Type inference for variables and expressions
- ✅ Explicit type annotations with Java types
- ✅ Numeric operations (addition, subtraction, negation)
- ✅ String concatenation
- ✅ Primitive types and wrapper types (boxing/unboxing)
- ✅ Return type annotations
- ✅ Type aliases

### Current Limitations

- ⚠️ **Experimental**: This is an early-stage feature
- ⚠️ **Limited TypeScript support**: Not all TypeScript features are supported
- ⚠️ **No function parameters yet**: Only parameterless methods
- ⚠️ **JDK 17 only**: Currently targets Java 17 bytecode
- ⚠️ **Limited expressions**: Only basic operations supported

## Technical Design

### Architecture

The compiler follows a **multi-phase compilation pipeline**:

```
TypeScript Source Code
         ↓
    [SWC Parser]
         ↓
    AST (Abstract Syntax Tree)
         ↓
  [Type Alias Collection]  ← Collect type aliases (type MyInt = int)
         ↓
   [AST Processing]        ← Walk AST and identify classes/methods
         ↓
  [Variable Analysis]      ← Analyze variables and infer types
         ↓
  [Type Resolution]        ← Resolve return types from annotations/inference
         ↓
  [Code Generation]        ← Generate JVM bytecode instructions
         ↓
   JVM Class Files
```

### Core Components

#### 1. **ByteCodeCompiler** (`com.caoccao.javet.swc4j.compiler`)

The main entry point for compilation:

- `ByteCodeCompiler.of(options)`: Factory method to create a compiler
- `compile(String code)`: Compiles TypeScript code to bytecode
- Returns `Map<String, byte[]>`: Map of fully-qualified class names to bytecode

#### 2. **ASM Layer** (`com.caoccao.javet.swc4j.asm`)

Low-level bytecode generation:

- **ClassWriter**: Generates JVM class file structure
  - Handles constant pool, methods, attributes
  - Writes class bytecode following JVM spec
- **CodeBuilder**: Builds method bytecode instructions
  - Provides fluent API for JVM instructions (iload, istore, iadd, etc.)
  - Handles instruction encoding and operands

#### 3. **JDK 17 Compiler** (`com.caoccao.javet.swc4j.compiler.jdk17`)

The actual compilation logic:

- **AstProcessor**: Walks the AST and processes TypeScript constructs
  - Processes modules, namespaces, classes, methods
  - Builds package structure from namespaces

- **TypeResolver**: Resolves types from annotations and infers types
  - Maps TypeScript type names to JVM descriptors (int → I, String → Ljava/lang/String;)
  - Infers types from expressions (numbers, strings, identifiers)
  - Analyzes return types from method bodies or annotations

- **VariableAnalyzer**: Analyzes variable declarations and builds local variable table
  - Extracts type information from type annotations
  - Assigns local variable slots
  - Tracks variable types for code generation

- **CodeGenerator**: Generates JVM bytecode for expressions and statements
  - Handles literals (numbers, strings)
  - Generates binary operations (addition, concatenation)
  - Generates unary operations (negation)
  - Handles type conversions (boxing, unboxing, primitive conversions)

- **CompilationContext**: Maintains compilation state
  - Local variable table
  - Inferred types map
  - Scoping information

#### 4. **Type System**

The compiler supports:

**Primitive Types:**
- `int` (I) - 32-bit integer
- `short` (S) - 16-bit integer
- `long` (J) - 64-bit integer
- `char` (C) - 16-bit character
- `float` (F) - 32-bit floating point
- `double` (D) - 64-bit floating point

**Wrapper Types:**
- `Integer` (Ljava/lang/Integer;)
- `Short` (Ljava/lang/Short;)
- `Long` (Ljava/lang/Long;)
- `Character` (Ljava/lang/Character;)
- `Float` (Ljava/lang/Float;)
- `Double` (Ljava/lang/Double;)
- `String` (Ljava/lang/String;)

**Type Inference:**
- Numbers without decimals → `int`
- Numbers with decimals → `double`
- String literals → `String`
- Variables → lookup in inferred types map

**Type Conversions:**
- Explicit annotations override inference (e.g., `const a: float = 123.456`)
- Automatic boxing/unboxing for wrapper types
- Primitive conversions (int → long, float → double)

### Bytecode Generation Strategy

#### Constants

- **Small integers** (-1 to 5): Use iconst_m1 through iconst_5
- **Byte/short integers**: Use bipush/sipush
- **Large integers**: Use ldc with integer constant pool entry
- **Float/double**: Use ldc/ldc2_w with constant pool entries
- **Strings**: Use ldc with string constant pool entry

#### Variables

- **Local variables**: Stored in local variable table with slot index
- **Load**: iload, fload, lload, dload, aload (based on type)
- **Store**: istore, fstore, lstore, dstore, astore (based on type)

#### Operations

- **Numeric addition**: iadd, ladd, fadd, dadd
- **Numeric negation**: ineg, lneg, fneg, dneg
- **String concatenation**: StringBuilder with append() and toString()

#### Return

- **Primitive returns**: ireturn, lreturn, freturn, dreturn
- **Object returns**: areturn
- **Void methods**: return

## Usage

### Basic Example

```java
import com.caoccao.javet.swc4j.compiler.*;

// Create compiler
ByteCodeCompilerOptions options = new ByteCodeCompilerOptions(JdkVersion.JDK_17);
ByteCodeCompiler compiler = ByteCodeCompiler.of(options);

// Compile TypeScript code
String code = """
    namespace com {
      export class Calculator {
        add(): int {
          return 5 + 10
        }
      }
    }
    """;

Map<String, byte[]> bytecodeMap = compiler.compile(code);

// Load and execute
byte[] bytecode = bytecodeMap.get("com.Calculator");
Class<?> calculatorClass = new ByteArrayClassLoader().loadClassFromBytes(bytecode);
Object calculator = calculatorClass.getConstructor().newInstance();
int result = (int) calculatorClass.getMethod("add").invoke(calculator);
```

### Examples from Tests

#### 1. **Integer Addition**

```typescript
namespace com {
  export class A {
    test() {
      const a: int = 5
      const b: int = 10
      const c = a + b  // Type inferred as int
      return c
    }
  }
}
```
Result: Returns `15`

#### 2. **Float with Type Annotation**

```typescript
namespace com {
  export class A {
    test(): float {
      return 123.456  // Converted to float
    }
  }
}
```
Result: Returns `123.456F`

#### 3. **Type Inference from Variable**

```typescript
namespace com {
  export class A {
    test() {
      var a: double = 123.456  // Explicit type
      return a                  // Return type inferred as double
    }
  }
}
```
Result: Returns `123.456D`

#### 4. **String Concatenation**

```typescript
namespace com {
  export class A {
    test() {
      const a: String = 'hello'
      const b: String = 'world'
      const c = a + b  // String concatenation
      return c
    }
  }
}
```
Result: Returns `"helloworld"`

#### 5. **Wrapper Types (Boxing/Unboxing)**

```typescript
namespace com {
  export class A {
    test() {
      const a: Integer = 5    // Boxed
      const b: Integer = 10   // Boxed
      const c = a + b         // Unboxed, added, result is int
      return c
    }
  }
}
```
Result: Returns `15`

#### 6. **Character to String**

```typescript
namespace com {
  export class A {
    test() {
      const a: char = 'X'
      const b: String = 'YZ'
      const c = a + b  // String concatenation
      return c
    }
  }
}
```
Result: Returns `"XYZ"`

#### 7. **Unary Negation**

```typescript
namespace com {
  export class A {
    test(): long {
      return -100
    }
  }
}
```
Result: Returns `-100L`

#### 8. **Type Aliases**

```typescript
export type MyString = String;

namespace com {
  export class A {
    test() {
      const a: MyString = 'test'
      return a
    }
  }
}
```
Result: Returns `"test"`

### Advanced Usage

#### Custom Type Aliases

```java
Map<String, String> typeAliasMap = new HashMap<>();
typeAliasMap.put("MyInt", "java.lang.Integer");
typeAliasMap.put("MyString", "java.lang.String");

ByteCodeCompilerOptions options = new ByteCodeCompilerOptions(
    JdkVersion.JDK_17,
    typeAliasMap
);
```

#### Package Prefix

```java
ByteCodeCompilerOptions options = new ByteCodeCompilerOptions(
    JdkVersion.JDK_17,
    new HashMap<>(),
    "com.mycompany"  // Package prefix
);
```

## Testing

The compiler is thoroughly tested with unit tests in:

- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/` - Literal tests (numbers, strings)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/` - Expression tests (binary operations)

Example test structure:

```java
@ParameterizedTest
@EnumSource(JdkVersion.class)
public void testReturnDouble(JdkVersion jdkVersion) throws Exception {
    var map = getCompiler(jdkVersion).compile("""
        namespace com {
          export class A {
            test(): double {
              return 123.456
            }
          }
        }""");
    Class<?> classA = loadClass(map.get("com.A"));
    var instance = classA.getConstructor().newInstance();
    assertEquals(123.456D, (double) classA.getMethod("test").invoke(instance), 0.00001D);
}
```

## Implementation Details

### Constant Pool Management

The ClassWriter manages a constant pool that stores:

- UTF-8 strings (method names, descriptors, class names)
- Class references
- Method references
- String constants
- Numeric constants (int, long, float, double)

Constants are cached to avoid duplicates and referenced by index in bytecode instructions.

### Local Variable Table

Variables are assigned slots in the local variable table:

- Slot 0: `this` reference (for instance methods)
- Slots 1+: Method parameters (not yet supported)
- Remaining slots: Local variables

**Type-specific slots:**

- `int`, `float`, `char`, `short`, references: 1 slot
- `long`, `double`: 2 slots (long/double take two slots in JVM)

### Type Conversion

**Explicit conversions** (when type annotation differs from literal):

```typescript
const a: float = 123.456  // double → float conversion
const b: Character = 'x'  // char → Character boxing
```

**Implicit inference** (no annotation):

```typescript
const c = 123      // inferred as int
const d = 123.456  // inferred as double
const e = 'hello'  // inferred as String
```

### String Concatenation

String concatenation uses `StringBuilder`:

```
new StringBuilder
dup
invokespecial StringBuilder.<init>()V
[generate left operand]
invokevirtual StringBuilder.append(...)Ljava/lang/StringBuilder;
[generate right operand]
invokevirtual StringBuilder.append(...)Ljava/lang/StringBuilder;
invokevirtual StringBuilder.toString()Ljava/lang/String;
```

The compiler optimizes nested string concatenations by flattening them into a single StringBuilder chain.

## Future Enhancements

Planned features:
- [ ] Method parameters
- [ ] Field declarations
- [ ] Control flow (if/else, while, for)
- [ ] Arrays
- [ ] Method calls
- [ ] Object instantiation
- [ ] Inheritance
- [ ] Interfaces
- [ ] Generics
- [ ] Lambda expressions
- [ ] More TypeScript features

## Troubleshooting

### VerifyError

If you get a `VerifyError` at runtime, this usually means:
- Incorrect bytecode generation (wrong instruction sequence)
- Type mismatch (e.g., trying to freturn when method returns int)
- Stack imbalance (wrong max stack size)

Solution: Check the method descriptor matches the actual return type.

### ClassCastException

If you get a `ClassCastException` when invoking methods:
- Method returns wrong type (e.g., int instead of float)
- Missing type conversion

Solution: Ensure type annotations match expected types.

### UnsupportedOperationException

If a TypeScript feature is not yet implemented, you'll get a compilation error or exception.

Solution: Check the current limitations section and use only supported features.

## Performance Considerations

- **Compilation time**: Parsing and bytecode generation add overhead
- **Runtime performance**: Generated bytecode runs at native JVM speed (same as compiled Java)
- **Memory**: Each compiled class consumes memory for bytecode and loaded class metadata

For production use, consider:
- Caching compiled classes
- Using a custom ClassLoader with class unloading
- Monitoring memory usage

## Conclusion

The TypeScript to JVM Bytecode Compiler is an experimental bridge between TypeScript syntax and JVM execution. While still in early stages, it demonstrates the potential for polyglot programming on the JVM and provides a foundation for future enhancements.

For questions or contributions, please visit the [swc4j GitHub repository](https://github.com/caoccao/swc4j).
