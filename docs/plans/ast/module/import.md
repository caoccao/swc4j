# Java Class Imports and Static Method Calls

## Overview

swc4j now supports importing Java classes and calling their static methods from TypeScript/JavaScript code. This feature allows seamless interoperability between TypeScript code and the Java standard library.

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

Currently, the compiler supports importing Java classes from `java.*` and `javax.*` packages using named imports:

```typescript
import { Math, String, System } from 'java.lang'
import { ArrayList } from 'java.util'
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

## Implementation Details

### Architecture

The Java import feature consists of several key components:

1. **ScopedJavaClassRegistry**: Manages Java class registrations with file-level scope isolation
2. **ImportDeclProcessor**: Processes import declarations and registers Java classes using reflection
3. **JavaClassInfo**: Stores metadata about imported Java classes (methods, descriptors, etc.)
4. **CallExpressionForJavaClassGenerator**: Generates bytecode for Java static method calls
5. **TypeResolver**: Enhanced to infer return types from Java methods

### File-Level Import Isolation

Imports are scoped per file to prevent leakage between compilation units:

```java
// Each file compilation enters a new scope
memory.getScopedJavaClassRegistry().enterScope();
try {
    // Process imports and compile
} finally {
    // Always exit scope, even on exceptions
    memory.getScopedJavaClassRegistry().exitScope();
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

1. **Static Methods Only**: Currently only static method calls are supported (e.g., `Math.floor()`)
2. **Java Packages Only**: Only classes from `java.*` and `javax.*` packages can be imported
3. **Named Imports Only**: Only named imports are supported (not `import * as`)
4. **Public Methods Only**: Only public methods are accessible
5. **Varargs Bytecode Generation**: While varargs methods are recognized and scored correctly, bytecode generation for varargs calls is not yet implemented
6. **Reference Type Hierarchy**: Overload resolution for reference types doesn't check class hierarchy (only exact matches)

## Future Enhancements

Potential future improvements:
- Support for instance methods and constructors
- Support for custom Java classes (beyond java.*/javax.*)
- Reference type hierarchy checking in overload resolution
- Support for Java fields and constants
- Support for default imports (import entire package)
- Varargs method support

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
