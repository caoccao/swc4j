# Tagged Template Literals - Implementation Plan

## Overview

Tagged templates allow custom processing of template literals by calling a tag function:
```typescript
this.tag`Hello ${name}!`
// Compiles to equivalent of:
this.tag(new String[]{"Hello ", "!"}, name)
```

This document covers the implementation of `Swc4jAstTaggedTpl` (tagged template literals).

## AST Structure

### Swc4jAstTaggedTpl (Tagged Template Literal)
```java
public class Swc4jAstTaggedTpl extends Swc4jAst implements ISwc4jAstExpr {
    protected ISwc4jAstExpr tag;                          // Tag function expression
    protected Swc4jAstTpl tpl;                            // Template literal
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeParams; // Type parameters
}
```

The inner `Swc4jAstTpl` contains:
- `quasis` (`List<Swc4jAstTplElement>`) - Static string parts
- `exprs` (`List<ISwc4jAstExpr>`) - Interpolated expressions

### Tagged Template Structure Pattern
```
tag(new String[]{quasis[0], quasis[1], ..., quasis[n]}, exprs[0], exprs[1], ..., exprs[n-1])

where: quasis.length = exprs.length + 1
```

Example:
```typescript
this.tag`Hello ${firstName} ${lastName}!`
```
Compiles to:
```java
this.tag(new String[]{"Hello ", " ", "!"}, firstName, lastName)
```

## Implementation Status

### ✅ Implemented: Tagged Templates (Swc4jAstTaggedTpl)

**File:** `TaggedTemplateLiteralGenerator.java`

**Strategy:** Method call transformation - tagged templates are compiled to direct method calls
where the tag function receives a `String[]` of quasis followed by individual expression arguments.

**Bytecode Generation:**
```java
// For: this.tag`Hello ${name}!`
1. Generate object reference (this)
2. Create String[] for quasis:
   a. ICONST size
   b. ANEWARRAY java/lang/String
   c. For each quasi: DUP, ICONST index, LDC string, AASTORE
3. Generate each interpolated expression
4. Resolve method descriptor from parameter types
5. INVOKEVIRTUAL tag method
6. Handle return type conversion if needed
```

**Supported Features:**
- Member expression tags (`this.method` style)
- Multiple interpolated expressions with mixed types
- String and primitive return types
- Empty templates (no quasis content)
- Templates with no interpolation
- Return type conversion (boxing/unboxing)

**Tag Function Resolution:**
- Method name extracted from `Swc4jAstIdentName` or `Swc4jAstIdent` property
- Object type inferred via `TypeResolver.inferTypeFromExpr()`
- Return type resolved via `ScopedJavaTypeRegistry.resolveClassMethodReturnType()`

**Limitations:**
- Only supports `MemberExpr` tags (e.g., `this.tag`). Standalone function tags not yet supported.
- No template object caching (String[] created per call site)
- No raw string array support

## Test Coverage

### ✅ Implemented Tests (TestCompileAstTaggedTplBasic)

All 8 tagged template tests passing (100% success rate):

1. **testTaggedTemplateBasic** - Basic tagged template: `this.tag\`Hello ${name}!\``
2. **testTaggedTemplateNoInterpolation** - Tag with no expressions
3. **testTaggedTemplateMultipleValues** - Multiple interpolated values
4. **testTaggedTemplateWithIntValue** - Integer expression in tagged template
5. **testTaggedTemplateReturnInt** - Tag function returning int
6. **testTaggedTemplateStringFirstElement** - Accessing strings[0] from quasis
7. **testTaggedTemplateEmptyTemplate** - Empty tagged template
8. **testTaggedTemplateCustomJoin** - Custom join with brackets

### 🔲 Missing Test Coverage

- Standalone function tags (non-member expression)
- Nested tagged templates: `outer\`prefix ${inner\`${x}\`} suffix\``
- Raw string access in tag function

## Integration Points

### ExpressionGenerator
```java
} else if (expr instanceof Swc4jAstTaggedTpl taggedTpl) {
    compiler.getTaggedTemplateLiteralGenerator().generate(code, cp, taggedTpl, returnTypeInfo);
}
```

### ByteCodeCompiler
```java
protected final TaggedTemplateLiteralGenerator taggedTemplateLiteralGenerator;

// In constructor:
taggedTemplateLiteralGenerator = new TaggedTemplateLiteralGenerator(this);

// Getter:
public TaggedTemplateLiteralGenerator getTaggedTemplateLiteralGenerator() { ... }
```

## Error Handling

### Error Cases
1. **Unsupported tag expression type** - Only `MemberExpr` tags supported; throws `Swc4jByteCodeCompilerException`
2. **Unsupported tag property type** - Only `Swc4jAstIdentName` and `Swc4jAstIdent` supported
3. **Cannot infer object type** - Object type must resolve to `L...;` descriptor
4. **Cannot infer return type** - Method must be resolvable via `ScopedJavaTypeRegistry`

## Dependencies

### Used By Tagged Templates
- `TypeResolver.inferTypeFromExpr()` - Object and expression type inference
- `ScopedJavaTypeRegistry.resolveClassMethodReturnType()` - Tag method return type resolution
- `ExpressionGenerator.generate()` - Object reference and expression bytecode generation
- `TypeConversionUtils.boxPrimitiveType()` / `unboxWrapperType()` - Return type conversion

## Future Enhancements

1. **Standalone Function Tags** (Medium Priority)
   - Support non-member expression tags (e.g., standalone function references)

2. **Template Object Caching** (Medium Priority)
   - Cache String[] per call site using static final fields
   - Use content hash to deduplicate identical template literals
   - Generate unique field names: `$tpl$0`, `$tpl$1`, etc.

3. **Raw String Array Support** (Low Priority)
   - Provide both cooked and raw string arrays to tag functions
   - Support `strings.raw` access pattern

## References

- **TypeScript Spec:** [Template Literals](https://www.typescriptlang.org/docs/handbook/2/template-literal-types.html)
- **ECMAScript Spec:** [Tagged Templates](https://tc39.es/ecma262/#sec-tagged-templates)
- **Implementation:** `TaggedTemplateLiteralGenerator.java`
- **Tests:** `TestCompileAstTaggedTplBasic.java`
- **Related:** `TypeConversionUtils.java`, `TypeResolver.java`, `ScopedJavaTypeRegistry.java`
- **See also:** [Template Literals](tpl.md)
