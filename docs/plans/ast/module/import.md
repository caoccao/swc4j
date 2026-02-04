# Java Class Imports: Static Methods, Instance Methods, Constructors, and Varargs

## Overview

swc4j supports importing Java classes and calling their methods from TypeScript/JavaScript code. This includes:
- Static method calls
- Instance method calls with method chaining
- Constructor calls with overload resolution
- Varargs method support
- Inner/nested class imports
- Custom package imports

This feature provides seamless interoperability between TypeScript code and the Java standard library as well as custom Java classes.

## Usage

### Basic Import Syntax

```typescript
import { Math } from 'java.lang'

namespace com {
  export class A {
    public calculate(x: double): double {
      return Math.floor(x)
    }
  }
}
```

### Supported Import Patterns

The compiler supports importing Java classes from any package using named imports:

```typescript
// Standard library imports
import { Math, String, System } from 'java.lang'
import { ArrayList, LinkedHashMap } from 'java.util'

// Custom package imports
import { Point } from 'com.example.shapes'
import { MathHelper } from 'com.example.utils'
```

Inner/nested classes are also supported:

```typescript
// Inner class import (TestClass.Point becomes TestClass$Point internally)
import { Point } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportConstructors'
```

### Calling Java Static Methods

Once a Java class is imported, you can call its public static methods directly:

```typescript
import { Math } from 'java.lang'

namespace com {
  export class Calculator {
    public max(a: double, b: double): double {
      return Math.max(a, b)
    }

    public abs(x: double): double {
      return Math.abs(x)
    }

    public floor(x: double): double {
      return Math.floor(x)
    }
  }
}
```

### Creating Instances and Calling Instance Methods

You can create instances of imported Java classes using `new` and call their instance methods:

```typescript
import { StringBuilder } from 'java.lang'

namespace com {
  export class A {
    public test(): String {
      const sb = new StringBuilder("Hello")
      sb.append(" ")
      sb.append("World")
      return sb.toString()
    }
  }
}
```

Method chaining is fully supported:

```typescript
import { StringBuilder } from 'java.lang'

namespace com {
  export class A {
    public test(): String {
      const sb = new StringBuilder()
      return sb.append("Hello").append(" ").append("World").toString()
    }
  }
}
```

### Calling Varargs Methods

Varargs methods are fully supported, including methods from the Java standard library:

```typescript
import { String } from 'java.lang'

namespace com {
  export class A {
    public test(): String {
      return String.format("Hello %s, you are %d years old", "Alice", 25)
    }
  }
}
```

Custom varargs methods also work:

```typescript
import { MathHelper } from 'com.example.utils'

namespace com {
  export class A {
    public test(): int {
      // MathHelper.sum is a varargs method: sum(int... numbers)
      return MathHelper.sum(1, 2, 3, 4, 5)
    }
  }
}
```

## Implementation Details

### Architecture

The Java import feature consists of several key components:

1. **ScopedJavaTypeRegistry**: Manages Java class registrations with file-level scope isolation
2. **ImportDeclProcessor**: Processes import declarations and registers Java classes using reflection (including inner classes)
3. **JavaTypeInfo**: Stores metadata about imported Java classes (methods, constructors, descriptors, varargs info, etc.)
4. **CallExpressionForClassProcessor**: Generates bytecode for Java static and instance method calls with varargs support
5. **NewExpressionProcessor**: Generates bytecode for Java constructor calls
6. **TypeResolver**: Enhanced to infer return types from Java methods and handle type conversions

### File-Level Import Isolation

Imports are scoped per file to prevent leakage between compilation units:

```java
// Each file compilation enters a new scope
memory.getScopedJavaTypeRegistry().enterScope();
try {
    // Process imports and compile
} finally {
    // Always exit scope, even on exceptions
    memory.getScopedJavaTypeRegistry().exitScope();
}
```

### Method Overload Resolution

The implementation uses a **scoring-based system** to select the best matching method from all available overloads:

#### Scoring Algorithm

Each candidate method is scored based on how well the argument types match the parameter types. The method with the highest score is selected:

- **1.0**: Exact type match (e.g., `int` → `int`)
- **0.95-0.99**: Primitive widening conversions (closer types score higher)
  - `byte` → `short` (0.99), `int` (0.98), `long` (0.97), `float` (0.96), `double` (0.95)
  - `short` → `int` (0.99), `long` (0.98), `float` (0.97), `double` (0.96)
  - `char` → `int` (0.99), `long` (0.98), `float` (0.97), `double` (0.96)
  - `int` → `long` (0.99), `float` (0.98), `double` (0.97)
  - `long` → `float` (0.99), `double` (0.98)
  - `float` → `double` (0.99)
- **0.7**: Boxing/unboxing with exact match (e.g., `int` → `Integer`)
- **0.6-0.69**: Boxing + widening
- **0.5**: Reference type to `Object`
- **0.95× actual score**: Varargs match (slightly lower score than exact match)

The total score is the average of all parameter scores. The method with the highest total score wins.

#### Why Scoring Instead of Filtering?

- **Handles varargs**: Methods with varargs accept variable numbers of arguments, so argument count can't be used for filtering
- **Finds best match**: Evaluates all candidates and selects the most specific one
- **Mirrors Java**: Follows Java's method resolution algorithm

#### Example

```typescript
import { Math } from 'java.lang'

namespace com {
  export class A {
    public testInt(a: int, b: int): int {
      // Calls Math.min(int, int) - exact match (score: 1.0)
      return Math.min(a, b)
    }

    public testDouble(a: double, b: double): double {
      // Calls Math.min(double, double) - exact match (score: 1.0)
      return Math.min(a, b)
    }

    public testWidening(a: int, b: int): double {
      // Could call Math.min(int, int) or Math.min(double, double)
      // Selects Math.min(int, int) because it scores higher:
      // - Math.min(int, int): score = 1.0 (exact match)
      // - Math.min(double, double): score = 0.97 (int->double widening)
      return Math.min(a, b)
    }
  }
}
```

### Type Conversion

The compiler automatically handles type conversions between TypeScript and Java types:
- Primitive type conversions (int ↔ double, etc.)
- Boxing/unboxing (int ↔ Integer)
- Type inference for method return types

## Current Limitations

1. **Named Imports Only**: Only named imports are supported (not `import * as`)
2. **Public Members Only**: Only public methods and constructors are accessible
3. **Reference Type Hierarchy**: Overload resolution for reference types doesn't fully check class hierarchy
4. **No Field Access**: Direct access to Java class fields is not yet supported
5. **No Generic Type Parameters**: Generic type parameters in method calls are not yet supported

## Future Enhancements

Potential future improvements:
- Reference type hierarchy checking in overload resolution
- Support for Java fields and constants
- Support for default imports (import entire package)
- Generic type parameter support
- Better error messages for ambiguous overloads

## Examples

### Example 1: Prime Number Checker

```typescript
import { Math } from 'java.lang'

namespace com {
  export class A {
    public isPrime(number: int): boolean {
      const limit = Math.floor(number / 2)
      for (let i = 2; i <= limit; i++) {
        if (number % i === 0) {
          return false
        }
      }
      return true
    }
  }
}
```

### Example 2: Mathematical Operations

```typescript
import { Math } from 'java.lang'

namespace com {
  export class Calculator {
    public hypotenuse(a: double, b: double): double {
      return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2))
    }

    public roundUp(x: double): double {
      return Math.ceil(x)
    }

    public roundDown(x: double): double {
      return Math.floor(x)
    }
  }
}
```

### Example 3: Overload Resolution

```typescript
import { Math } from 'java.lang'

namespace com {
  export class A {
    // Automatically selects Math.max(int, int) based on parameter types
    public maxInt(a: int, b: int): int {
      return Math.max(a, b)
    }

    // Automatically selects Math.max(double, double) based on parameter types
    public maxDouble(a: double, b: double): double {
      return Math.max(a, b)
    }
  }
}
```

### Example 4: StringBuilder with Instance Methods

```typescript
import { StringBuilder } from 'java.lang'

namespace com {
  export class A {
    public buildMessage(name: String, age: int): String {
      const sb = new StringBuilder()
      sb.append("Name: ")
      sb.append(name)
      sb.append(", Age: ")
      sb.append(age)
      return sb.toString()
    }

    // Method chaining
    public buildMessageChained(name: String, age: int): String {
      return new StringBuilder()
        .append("Name: ")
        .append(name)
        .append(", Age: ")
        .append(age)
        .toString()
    }
  }
}
```

### Example 5: Varargs Methods

```typescript
import { String } from 'java.lang'

namespace com {
  export class A {
    public formatMessage(name: String, age: int, score: double): String {
      return String.format(
        "Student: %s, Age: %d, Score: %.2f",
        name,
        age,
        score
      )
    }

    public noVarargs(): String {
      return String.format("No formatting needed")
    }
  }
}
```

### Example 6: Custom Class with Constructors

```typescript
import { ArrayList } from 'java.util'

namespace com {
  export class A {
    public createList(): int {
      // No-arg constructor
      const list1 = new ArrayList()
      list1.add("First")
      list1.add("Second")

      // Constructor with initial capacity
      const list2 = new ArrayList(10)
      list2.add("Item")

      return list1.size() + list2.size()
    }
  }
}
```

### Example 7: Inner Class Import

```typescript
import { Point } from 'com.example.shapes.Shape'

namespace com {
  export class A {
    public createPoint(): String {
      const p = new Point(10, 20)
      return p.toString()
    }
  }
}
```
