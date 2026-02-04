# Tagged Template Literals - Implementation Plan

## Overview

Tagged templates allow custom processing of template literals by calling a tag function:
```typescript
this.tag`Hello ${name}!`
// Compiles to equivalent of:
this.tag($tpl$0, name)  // where $tpl$0 is a cached static final String[]
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

**File:** `TaggedTemplateLiteralProcessor.java`

**Strategy:** Method call transformation - tagged templates are compiled to direct method calls
where the tag function receives a `String[]` of quasis followed by individual expression arguments.

**Bytecode Generation:**
```java
// For: this.tag`Hello ${name}!`
1. Generate object reference (this)
2. Load cached String[] for quasis via GETSTATIC
3. Generate each interpolated expression
4. Resolve method descriptor from parameter types
5. INVOKEVIRTUAL tag method
6. Handle return type conversion if needed
```

**Supported Features:**
- Member expression tags (`this.method` style)
- Standalone function tags (non-member expression tags)
- Multiple interpolated expressions with mixed types
- String and primitive return types
- Empty templates (no quasis content)
- Templates with no interpolation
- Return type conversion (boxing/unboxing)
- Template caching (String[] created once at class load, reused on every call)
- Deduplication of identical quasis within the same class

**Tag Function Resolution:**
- Member expression tags: Method name extracted from `Swc4jAstIdentName` or `Swc4jAstIdent` property
  - Object type inferred via `TypeResolver.inferTypeFromExpr()`
  - Return type resolved via `ScopedJavaTypeRegistry.resolveClassMethodReturnType()`
- Standalone function tags: Function name from `Swc4jAstIdent`
  - Function looked up in `ScopedStandaloneFunctionRegistry`
  - Invoked via `INVOKESTATIC` on the dummy class (e.g., `com/$`)

**Limitations:**
- None (all features implemented)

### ✅ Implemented: Template Object Caching

**File:** `ScopedTemplateCacheRegistry.java`

**Strategy:** Cache quasis String[] arrays as private static final fields, initialized once in `<clinit>`.
Uses a scoped registry pattern for proper cleanup and nested class support.

**How it works:**
1. `ClassProcessor` calls `enterScope()` when starting class compilation
2. During method compilation, `TaggedTemplateLiteralProcessor` registers quasis with `ScopedTemplateCacheRegistry`
3. Registry returns a field name (e.g., `$tpl$0`) and deduplicates identical quasis within the scope
4. After all methods are compiled, `ClassProcessor` adds static fields and `<clinit>` initialization
5. `ClassProcessor` calls `exitScope()` in finally block (automatic cleanup)
6. At runtime, `GETSTATIC` loads the cached array instead of creating a new one

**Bytecode (before caching):**
```
ICONST 2
ANEWARRAY java/lang/String
DUP
ICONST 0
LDC "Hello "
AASTORE
DUP
ICONST 1
LDC "!"
AASTORE
```

**Bytecode (with caching):**
```
GETSTATIC com/A.$tpl$0 : [Ljava/lang/String;
```

**Benefits:**
- Reduced bytecode size (1 instruction vs 10+ instructions)
- No array allocation on every call
- Lower GC pressure
- Identical quasis share the same cached field

### ✅ Implemented: Standalone Function Tags

**File:** `TaggedTemplateLiteralProcessor.java`

**Strategy:** Support `Swc4jAstIdent` tags (standalone function names) in addition to `MemberExpr` tags.
Standalone functions are compiled into dummy classes (e.g., `$` or `com/$`) and invoked via `INVOKESTATIC`.

**Bytecode Generation:**
```java
// For: tag`Hello ${name}!` (where tag is a standalone function)
1. Load cached String[] for quasis via GETSTATIC
2. Generate each interpolated expression
3. Build method descriptor from quasis and expression types
4. INVOKESTATIC on the dummy class (e.g., com/$.tag)
5. Handle return type conversion if needed
```

**How it works:**
1. Tag expression is `Swc4jAstIdent` (simple function name like `tag`)
2. Function is looked up in `ScopedStandaloneFunctionRegistry` using current package
3. Dummy class name is retrieved from registry (e.g., `$`, `$1`, `com/$`)
4. Method descriptor is built from `String[]` + expression types + return type
5. `INVOKESTATIC` is generated to call the static method on the dummy class

**Example:**
```typescript
namespace com {
  export function tag(strings: String[], value: String): String {
    return strings[0] + value + strings[1]
  }
  export class A {
    test(): String {
      return tag`Hello ${"World"}!`  // Calls com/$.tag via INVOKESTATIC
    }
  }
}
```

### ✅ Implemented: Raw String Array Support

**File:** `TemplateStringsArray.java`, `TaggedTemplateLiteralProcessor.java`

**Strategy:** Create a `TemplateStringsArray` class that provides access to both cooked (processed) and raw
(unprocessed) template strings. Tag functions can declare `TemplateStringsArray` as their first parameter
to access raw strings via the `raw` field.

**TemplateStringsArray class:**
```java
public final class TemplateStringsArray {
    public final String[] raw;     // Raw strings (escape sequences preserved)
    public final int length;       // Number of template string parts
    private final String[] cooked; // Cooked strings (escape sequences processed)

    public String get(int index) { return cooked[index]; }
}
```

**How it works:**
1. `ClassProcessor` creates two cached fields for each template:
   - `$tpl$N` - `String[]` containing cooked strings
   - `$tpl$N$raw` - `TemplateStringsArray` containing both cooked and raw strings
2. `TaggedTemplateLiteralProcessor` detects if the tag function accepts `TemplateStringsArray`:
   - If yes, loads the `$tpl$N$raw` field via `GETSTATIC`
   - If no (accepts `String[]`), loads the `$tpl$N` field (backward compatible)
3. Tag functions access raw strings via `strings.raw[index]`

**Example:**
```typescript
import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
namespace com {
  export class A {
    tag(strings: TemplateStringsArray, value: String): String {
      // strings.get(0) - cooked string (escape sequences processed)
      // strings.raw[0] - raw string (escape sequences preserved)
      return strings.get(0) + "|" + strings.raw[0]
    }
    test(): String {
      return this.tag`line1\nline2 ${"value"}`
      // Cooked: "line1" + newline + "line2 "
      // Raw: "line1\\nline2 "
    }
  }
}
```

**Cooked vs Raw strings:**
- Cooked: Escape sequences are processed (e.g., `\n` becomes a newline character)
- Raw: Escape sequences are preserved as literal characters (e.g., `\n` remains as backslash-n)

## Test Coverage

### ✅ Implemented Tests (TestCompileAstTaggedTplBasic)

All 8 basic tagged template tests passing (100% success rate):

1. **testTaggedTemplateBasic** - Basic tagged template: `this.tag\`Hello ${name}!\``
2. **testTaggedTemplateNoInterpolation** - Tag with no expressions
3. **testTaggedTemplateMultipleValues** - Multiple interpolated values
4. **testTaggedTemplateWithIntValue** - Integer expression in tagged template
5. **testTaggedTemplateReturnInt** - Tag function returning int
6. **testTaggedTemplateStringFirstElement** - Accessing strings[0] from quasis
7. **testTaggedTemplateEmptyTemplate** - Empty tagged template
8. **testTaggedTemplateCustomJoin** - Custom join with brackets

### ✅ Implemented Tests (TestCompileAstTaggedTplAdvanced)

All 10 advanced tagged template tests passing (100% success rate):

1. **testTaggedTemplateInConditional** - Tagged template in ternary expression
2. **testTaggedTemplateManyInterpolations** - Four interpolated values with delimiters
3. **testTaggedTemplateNestedRegularTemplate** - Regular template literal as interpolated expression
4. **testTaggedTemplateNestedTaggedTemplate** - Tagged template result as interpolated expression
5. **testTaggedTemplateReturnedFromMethod** - Tagged template returned from helper method
6. **testTaggedTemplateWithBooleanValue** - Boolean expression in tagged template
7. **testTaggedTemplateWithConditionalExpression** - Conditional expression result as argument
8. **testTaggedTemplateWithDoubleValue** - Double primitive expression
9. **testTaggedTemplateWithLongValue** - Long primitive expression
10. **testTaggedTemplateWithMethodCallExpression** - Method call result as argument

### ✅ Implemented Tests (TestCompileAstTaggedTplCaching)

All 6 caching tests passing (100% success rate):

1. **testTemplateCacheFieldExists** - Verifies static final $tpl$ field is generated
2. **testTemplateCacheDeduplication** - Identical quasis share same cached field
3. **testTemplateCacheMultipleDifferentTemplates** - Different quasis create different fields
4. **testTemplateCacheEmptyTemplate** - Empty template creates cache entry
5. **testTemplateCacheNoInterpolation** - Template with no interpolation has single-element cache
6. **testTemplateCacheManyInterpolations** - Template with many interpolations has correct cache

### ✅ Implemented Tests (TestCompileAstTaggedTplStandalone)

All 8 standalone function tag tests passing (100% success rate):

1. **testStandaloneTagBasic** - Basic standalone function tag: `tag\`Hello ${name}!\``
2. **testStandaloneTagMultipleInterpolations** - Multiple interpolated values
3. **testStandaloneTagNoInterpolation** - Tag with no expressions
4. **testStandaloneTagWithIntValue** - Integer expression in tagged template
5. **testStandaloneTagReturnsInt** - Standalone tag function returning int
6. **testStandaloneTagWithVariable** - Using local variable in interpolation
7. **testStandaloneTagInDefaultPackage** - Standalone tag in default package (no namespace)
8. **testStandaloneTagWithMethodCall** - Method call result as argument

### ✅ Implemented Tests (TestCompileAstTaggedTplRaw)

All 8 raw string access tests passing (100% success rate):

1. **testRawStringAccessBasic** - Basic raw string access: `strings.raw[0]`
2. **testRawStringWithEscapeSequence** - Raw preserves `\n` as backslash-n
3. **testRawStringWithTab** - Raw preserves `\t` as backslash-t
4. **testRawStringLength** - `strings.length` returns count of template parts
5. **testRawStringMultipleParts** - Access multiple raw string parts
6. **testRawStringCacheFieldExists** - Verify `$tpl$0` and `$tpl$0$raw` fields exist
7. **testRawStringStandaloneFunction** - Raw string access with standalone function tag
8. **testBackwardCompatibilityWithStringArray** - Tag with `String[]` still works

### 🔲 Missing Test Coverage

- (None)

## Integration Points

### ExpressionProcessor
```java
} else if (expr instanceof Swc4jAstTaggedTpl taggedTpl) {
    compiler.getTaggedTemplateLiteralProcessor().generate(code, cp, taggedTpl, returnTypeInfo);
}
```

### ByteCodeCompiler
```java
protected final TaggedTemplateLiteralProcessor taggedTemplateLiteralProcessor;

// In constructor:
taggedTemplateLiteralProcessor = new TaggedTemplateLiteralProcessor(this);

// Getter:
public TaggedTemplateLiteralProcessor getTaggedTemplateLiteralProcessor() { ... }
```

## Error Handling

### Error Cases
1. **Unsupported tag expression type** - Only `MemberExpr` and `Ident` tags supported; throws `Swc4jByteCodeCompilerException`
2. **Unsupported tag property type** - Only `Swc4jAstIdentName` and `Swc4jAstIdent` supported for member expression tags
3. **Cannot infer object type** - Object type must resolve to `L...;` descriptor
4. **Cannot infer return type** - Method must be resolvable via `ScopedJavaTypeRegistry`
5. **Standalone function not found** - Function name must exist in `ScopedStandaloneFunctionRegistry`
6. **Dummy class not found** - Package must have a dummy class registered for standalone functions

## Dependencies

### Used By Tagged Templates
- `TypeResolver.inferTypeFromExpr()` - Object and expression type inference
  - Added support for `Swc4jAstTpl` → returns `Ljava/lang/String;`
  - Added support for `Swc4jAstTaggedTpl` → returns `null` (resolved by method lookup)
  - Added support for `TemplateStringsArray.raw` → returns `[Ljava/lang/String;`
  - Added support for `TemplateStringsArray.length` → returns `I`
- `ScopedJavaTypeRegistry.resolveClassMethodReturnType()` - Tag method return type resolution
- `ScopedStandaloneFunctionRegistry` - Standalone function lookup for function tags
- `ExpressionProcessor.generate()` - Object reference and expression bytecode generation
- `TypeConversionUtils.boxPrimitiveType()` / `unboxWrapperType()` - Return type conversion
- `ScopedTemplateCacheRegistry` - Scoped template cache tracking with cooked and raw strings
- `ClassProcessor` - Generates static fields and `<clinit>` initialization for template caches
- `MemberExpressionProcessor` - Field access for `TemplateStringsArray.raw` and `TemplateStringsArray.length`
- `TemplateStringsArray` - Wrapper class for cooked and raw template strings

## Future Enhancements

- (All planned features have been implemented)

## References

- **TypeScript Spec:** [Template Literals](https://www.typescriptlang.org/docs/handbook/2/template-literal-types.html)
- **ECMAScript Spec:** [Tagged Templates](https://tc39.es/ecma262/#sec-tagged-templates)
- **Implementation:** `TaggedTemplateLiteralProcessor.java`, `ScopedTemplateCacheRegistry.java`, `TemplateStringsArray.java`
- **Tests:** `TestCompileAstTaggedTplBasic.java`, `TestCompileAstTaggedTplAdvanced.java`, `TestCompileAstTaggedTplCaching.java`, `TestCompileAstTaggedTplStandalone.java`, `TestCompileAstTaggedTplRaw.java`
- **Related:** `TypeConversionUtils.java`, `TypeResolver.java`, `ScopedJavaTypeRegistry.java`, `ClassProcessor.java`
- **See also:** [Template Literals](tpl.md)
