# Template Literals - Implementation Plan

## Overview

Template literals allow embedding expressions inside string literals using backtick syntax:
```typescript
const name = "World";
const greeting = `Hello ${name}!`;  // "Hello World!"
```

This document covers the implementation of `Swc4jAstTpl` (basic template literals) and the plan for `Swc4jAstTaggedTpl` (tagged templates).

## AST Structure

### Swc4jAstTpl (Template Literal)
```java
public class Swc4jAstTpl extends Swc4jAst implements ISwc4jAstExpr {
    protected final List<ISwc4jAstExpr> exprs;      // Interpolated expressions
    protected final List<Swc4jAstTplElement> quasis; // Static string parts
}
```

### Swc4jAstTplElement (Template Element)
```java
public class Swc4jAstTplElement extends Swc4jAst {
    protected Optional<String> cooked;  // Processed string value
    protected String raw;               // Raw string value
    protected boolean tail;             // Marks last element
}
```

### Template Structure Pattern
```
quasis[0] + exprs[0] + quasis[1] + exprs[1] + ... + quasis[n]

where: quasis.length = exprs.length + 1
```

Example:
```typescript
`Hello ${firstName} ${lastName}!`
```
Breaks down to:
- quasis[0]: "Hello "
- exprs[0]: firstName
- quasis[1]: " "
- exprs[1]: lastName
- quasis[2]: "!"

## Implementation Status

### ✅ Implemented: Basic Template Literals (Swc4jAstTpl)

**File:** `TemplateLiteralGenerator.java`

**Strategy:** StringBuilder-based string concatenation

**Bytecode Generation:**
```java
// For: `Hello ${name}!`
// Generates equivalent to: new StringBuilder().append("Hello ").append(name).append("!").toString()

1. Create StringBuilder instance
2. For each quasi/expr pair:
   a. If quasi is non-empty, append it
   b. If expr exists:
      - Generate expression bytecode
      - Infer expression type
      - Box primitives to wrapper types
      - Convert to string via String.valueOf(Object)
      - Append to StringBuilder
3. Call toString() for final result
```

**Special Cases:**
- **Empty template** (` `` `): Returns empty string constant
- **No interpolation** (`` `Hello World!` ``): Returns string constant directly
- **Single expression** (`` `${value}` ``): Still uses StringBuilder for consistency
- **Consecutive interpolations** (`` `${a}${b}` ``): Handled with empty quasi between them

**Type Handling:**
```java
// Infer expression type
String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);

// Generate expression with natural type
compiler.getExpressionGenerator().generate(code, cp, expr, null);

// Box primitives to their wrapper types
if (TypeConversionUtils.isPrimitiveType(exprType)) {
    String wrapperType = TypeConversionUtils.getWrapperType(exprType);
    TypeConversionUtils.boxPrimitiveType(code, cp, exprType, wrapperType);
}

// Convert to String via String.valueOf(Object)
int valueOfRef = cp.addMethodRef("java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
code.invokestatic(valueOfRef);
```

**Key Design Decisions:**
1. **StringBuilder over String concatenation** - More efficient for multiple interpolations
2. **String.valueOf(Object)** - Uniform conversion handling for all types including null
3. **TypeResolver integration** - Leverages existing type inference infrastructure
4. **TypeConversionUtils boxing** - Reuses primitive boxing utilities

### 🔲 Not Implemented: Tagged Templates (Swc4jAstTaggedTpl)

Tagged templates allow custom processing of template literals:
```typescript
function tag(strings: string[], ...values: any[]): string {
    // strings[0] = "Hello "
    // strings[1] = "!"
    // strings.raw[0] = "Hello "
    // strings.raw[1] = "!"
    // values[0] = name
    return strings[0] + values[0].toUpperCase() + strings[1];
}

const name = "world";
const result = tag`Hello ${name}!`;  // "Hello WORLD!"
```

**Challenges:**
1. **Template Object Caching** - Tagged template objects should be cached per call site
2. **Raw String Array** - Need to provide both cooked and raw string arrays
3. **Runtime Support** - Requires runtime class/interface for template objects
4. **Function Interface Mapping** - Tag function can have any signature

**Proposed Implementation Plan:**

#### Step 1: Define Template Object Interface
```java
// Runtime support class
package com.caoccao.javet.swc4j.runtime;

public interface TemplateObject {
    String[] getStrings();      // Cooked strings
    String[] getRawStrings();   // Raw strings
}
```

#### Step 2: Generate Template Object per Call Site
```java
// For each tagged template call site, generate:
private static final TemplateObject $tpl$0 = new TemplateObjectImpl(
    new String[]{"Hello ", "!"},  // cooked
    new String[]{"Hello ", "!"}   // raw
);
```

#### Step 3: Generate Tag Function Call
```java
// tag`Hello ${name}!` becomes:
// tag($tpl$0.getStrings(), name)  // if tag expects (String[], Object...)
// or
// tag($tpl$0, name)  // if tag expects (TemplateObject, Object...)
```

#### Step 4: Handle Different Tag Signatures
- Infer tag function signature from type annotations
- Support common patterns: `(strings: string[], ...values: any[]) => T`
- Fall back to Object[] for unknown signatures

#### Step 5: Template Object Deduplication
- Cache template objects at class level (static final fields)
- Use content hash to deduplicate identical template literals
- Generate unique field names: `$tpl$0`, `$tpl$1`, etc.

## Test Coverage

### ✅ Implemented Tests (TestCompileAstTemplateLiteral)

All 10 tests passing (100% success rate):

1. **testTemplateSimple** - Basic interpolation: `` `Hello ${name}!` ``
2. **testTemplateNoInterpolation** - Plain text: `` `Hello World!` ``
3. **testTemplateMultipleInterpolations** - Multiple expressions: `` `${first} ${last}` ``
4. **testTemplateWithNumbers** - Number interpolation: `` `Age: ${age}` ``
5. **testTemplateWithExpressions** - Binary expressions: `` `Sum: ${a + b}` ``
6. **testTemplateEmpty** - Empty template: `` `` ``
7. **testTemplateOnlyInterpolation** - Single expression: `` `${value}` ``
8. **testTemplateMultiline** - Multi-line templates with newlines
9. **testTemplateWithEscapes** - Escape sequences: `` `Tab\there` ``
10. **testTemplateConsecutiveInterpolations** - Back-to-back: `` `${a}${b}` ``

### 🔲 Missing Test Coverage

**Template Literals:**
- Nested template literals: `` `Outer ${`inner ${x}`} end` ``
- Template in conditional: `condition ? \`yes\` : \`no\``
- Template with complex expressions: `` `Result: ${obj.method().field}` ``
- Template with null/undefined: `` `Value: ${null}` `` → "Value: null"
- Template with boolean: `` `Flag: ${true}` `` → "Flag: true"

**Tagged Templates (Future):**
- Basic tagged template: `tag\`Hello ${name}\``
- Tagged template with multiple values: `tag\`${a} and ${b}\``
- Nested tagged templates: `outer\`prefix ${inner\`${x}\`} suffix\``
- Tag function with custom return type
- Raw string access in tag function

## Integration Points

### ExpressionGenerator
```java
} else if (expr instanceof Swc4jAstTpl tpl) {
    compiler.getTemplateLiteralGenerator().generate(code, cp, tpl, returnTypeInfo);
} else if (expr instanceof Swc4jAstTaggedTpl taggedTpl) {
    // TODO: Implement tagged template support
    throw new Swc4jByteCodeCompilerException(expr, "Tagged templates are not yet supported");
}
```

### ByteCodeCompiler
```java
protected final TemplateLiteralGenerator templateLiteralGenerator;

// In constructor:
templateLiteralGenerator = new TemplateLiteralGenerator(this);

// Getter:
public TemplateLiteralGenerator getTemplateLiteralGenerator() {
    return templateLiteralGenerator;
}
```

### TypeResolver
Template literals always return `String`:
```java
if (expr instanceof Swc4jAstTpl) {
    return "Ljava/lang/String;";
}
```

## Performance Considerations

### Current Implementation (StringBuilder)
- **Pros:**
  - Efficient for multiple concatenations
  - Single allocation for the StringBuilder
  - Standard Java pattern

- **Cons:**
  - Overhead for simple cases (no interpolation)
  - Always allocates StringBuilder even for single expression

### Optimization Opportunities
1. **String Constant Folding** - Already implemented for no-interpolation case
2. **Single Expression Optimization** - Could skip StringBuilder for `` `${expr}` ``
3. **Compile-Time Concatenation** - Could merge adjacent string literals at compile time

### Tagged Template Performance
- Template object caching prevents repeated allocations
- Static final fields ensure one-time initialization
- No runtime overhead for template string preparation

## Error Handling

### Current Error Cases
1. **Empty cooked value** - Falls back to raw value
2. **Null expression type** - TypeResolver handles gracefully
3. **Unsupported expression** - Propagates from ExpressionGenerator

### Future Error Cases (Tagged Templates)
1. **Unknown tag function signature** - Should provide clear error
2. **Invalid tag function reference** - Type checking needed
3. **Missing template object support** - Runtime dependency check

## Dependencies

### Used By Template Literals
- `TypeResolver.inferTypeFromExpr()` - Expression type inference
- `TypeConversionUtils.isPrimitiveType()` - Primitive detection
- `TypeConversionUtils.getWrapperType()` - Wrapper type mapping
- `TypeConversionUtils.boxPrimitiveType()` - Primitive boxing
- `ExpressionGenerator.generate()` - Expression bytecode generation

### Required For Tagged Templates (Future)
- Runtime template object interface
- Template object caching mechanism
- Function signature analysis
- Spread argument handling

## JVM Bytecode Details

### Example Bytecode (`` `Hello ${name}!` ``)
```
// new StringBuilder()
NEW java/lang/StringBuilder
DUP
INVOKESPECIAL java/lang/StringBuilder.<init>()V

// .append("Hello ")
LDC "Hello "
INVOKEVIRTUAL java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;

// Load name variable
ALOAD 1  // or appropriate variable index

// .append(String.valueOf(name))
INVOKESTATIC java/lang/String.valueOf(Ljava/lang/Object;)Ljava/lang/String;
INVOKEVIRTUAL java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;

// .append("!")
LDC "!"
INVOKEVIRTUAL java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;

// .toString()
INVOKEVIRTUAL java/lang/StringBuilder.toString()Ljava/lang/String;
```

### Example Bytecode with Primitive (`` `Age: ${age}` ``)
```
// new StringBuilder()
NEW java/lang/StringBuilder
DUP
INVOKESPECIAL java/lang/StringBuilder.<init>()V

// .append("Age: ")
LDC "Age: "
INVOKEVIRTUAL java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;

// Load age (int)
ILOAD 1

// Box to Integer
INVOKESTATIC java/lang/Integer.valueOf(I)Ljava/lang/Integer;

// Convert to String
INVOKESTATIC java/lang/String.valueOf(Ljava/lang/Object;)Ljava/lang/String;

// .append(...)
INVOKEVIRTUAL java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;

// .toString()
INVOKEVIRTUAL java/lang/StringBuilder.toString()Ljava/lang/String;
```

## Comparison with JavaScript Semantics

### Matching Behavior
✅ String interpolation with automatic toString conversion
✅ Multi-line string support
✅ Escape sequence handling (cooked vs raw)
✅ Null/undefined conversion to "null"/"undefined"

### Differences
⚠️ **No special handling for Symbols** - Java has no Symbol type
⚠️ **No reference to global String** - Uses Java's String class
⚠️ **No prototype chain** - Boxing uses wrapper valueOf() methods

## Future Enhancements

1. **Tagged Template Support** (High Priority)
   - Implement `Swc4jAstTaggedTpl` handling
   - Add runtime template object support
   - Cache template objects per call site

2. **Optimization Passes** (Medium Priority)
   - Compile-time string folding for constant expressions
   - Skip StringBuilder for single-expression templates
   - Inline small templates

3. **Extended String Features** (Low Priority)
   - Support for template literal types (TypeScript type system)
   - Integration with string literal types in type checking

4. **Diagnostics** (Medium Priority)
   - Better error messages for unsupported escape sequences
   - Type mismatch warnings for expressions

## References

- **TypeScript Spec:** [Template Literals](https://www.typescriptlang.org/docs/handbook/2/template-literal-types.html)
- **ECMAScript Spec:** [Template Literals](https://tc39.es/ecma262/#sec-template-literals)
- **Implementation:** `TemplateLiteralGenerator.java`
- **Tests:** `TestCompileAstTemplateLiteral.java`
- **Related:** `TypeConversionUtils.java`, `TypeResolver.java`
