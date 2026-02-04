# Template Literals - Implementation Plan

## Overview

Template literals allow embedding expressions inside string literals using backtick syntax:
```typescript
const name = "World";
const greeting = `Hello ${name}!`;  // "Hello World!"
```

This document covers the implementation of `Swc4jAstTpl` (basic template literals).

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

**File:** `TemplateLiteralProcessor.java`

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
compiler.getExpressionProcessor().generate(code, cp, expr, null);

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

## Test Coverage

### ✅ Implemented Tests (TestCompileAstTplBasic)

All 10 basic tests passing (100% success rate):

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

### ✅ Implemented Tests (TestCompileAstTplAdvanced)

All 10 advanced tests passing (100% success rate):

1. **testTemplateNestedTemplate** - Nested template literals
2. **testTemplateInConditional** - Template in ternary expression
3. **testTemplateWithNull** - Null interpolation → "null"
4. **testTemplateWithBoolean** - Boolean interpolation → "true"/"false"
5. **testTemplateWithMethodCall** - Method call in interpolation
6. **testTemplateWithConditionalExpression** - Ternary inside interpolation
7. **testTemplateWithLongValue** - Long primitive interpolation
8. **testTemplateWithDoubleValue** - Double primitive interpolation
9. **testTemplateReturnedFromMethod** - Template as method return value
10. **testTemplateManyInterpolations** - Five interpolations in one template

## Integration Points

### ExpressionProcessor
```java
} else if (expr instanceof Swc4jAstTpl tpl) {
    compiler.getTemplateLiteralProcessor().generate(code, cp, tpl, returnTypeInfo);
}
```

### ByteCodeCompiler
```java
protected final TemplateLiteralProcessor templateLiteralProcessor;

// In constructor:
templateLiteralProcessor = new TemplateLiteralProcessor(this);

// Getter:
public TemplateLiteralProcessor getTemplateLiteralProcessor() { ... }
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

## Error Handling

### Current Error Cases
1. **Empty cooked value** - Falls back to raw value
2. **Null expression type** - TypeResolver handles gracefully
3. **Unsupported expression** - Propagates from ExpressionProcessor

## Dependencies

### Used By Template Literals
- `TypeResolver.inferTypeFromExpr()` - Expression type inference
- `TypeConversionUtils.isPrimitiveType()` - Primitive detection
- `TypeConversionUtils.getWrapperType()` - Wrapper type mapping
- `TypeConversionUtils.boxPrimitiveType()` - Primitive boxing
- `ExpressionProcessor.generate()` - Expression bytecode generation

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

1. **Optimization Passes** (Medium Priority)
   - Compile-time string folding for constant expressions
   - Skip StringBuilder for single-expression templates
   - Inline small templates

2. **Extended String Features** (Low Priority)
   - Support for template literal types (TypeScript type system)
   - Integration with string literal types in type checking

3. **Diagnostics** (Medium Priority)
   - Better error messages for unsupported escape sequences
   - Type mismatch warnings for expressions

## References

- **TypeScript Spec:** [Template Literals](https://www.typescriptlang.org/docs/handbook/2/template-literal-types.html)
- **ECMAScript Spec:** [Template Literals](https://tc39.es/ecma262/#sec-template-literals)
- **Implementation:** `TemplateLiteralProcessor.java`
- **Tests:** `TestCompileAstTplBasic.java`, `TestCompileAstTplAdvanced.java`
- **Related:** `TypeConversionUtils.java`, `TypeResolver.java`
- **See also:** [Tagged Template Literals](tagged-tpl.md)
