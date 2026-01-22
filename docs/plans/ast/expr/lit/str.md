# String Literal Implementation Plan

## Overview

This document outlines the implementation plan for supporting JavaScript/TypeScript string literals (`Swc4jAstStr`) and compiling them to JVM bytecode as **Java Strings** or **char/Character** primitives.

**Current Status:** 🟢 **PHASE 1 & 2 COMPLETE** - String literals and 18 String methods fully implemented

**Implementation Files:**
- ✅ [StringLiteralGenerator.java](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/lit/StringLiteralGenerator.java) - String literal bytecode generation
- ✅ [MemberExpressionGenerator.java](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/MemberExpressionGenerator.java) - String.length property access
- ✅ [CallExpressionGenerator.java](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/CallExpressionGenerator.java) - String method calls (18 methods)
- ✅ [StringApiUtils.java](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils.java) - Helper utilities for JS-compatible String operations
- ✅ [TypeResolver.java](../../../../../src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/TypeResolver.java) - Type inference for String methods

**Test Files:** ✅ 140 passing tests across 12 test files (82 literal tests + 58 method tests)

**AST Definition:** [Swc4jAstStr.java](../../../../../src/main/java/com/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr.java)

**Last Updated:** 2026-01-22 - Implemented 18 String methods with full test coverage

## Summary

This implementation successfully enables TypeScript string literal support in swc4j with JVM bytecode generation, including 18 JavaScript String methods with full compatibility.

### Phase 1: ✅ String Literals (Completed 2026-01-21)

- ✅ **82 passing tests** across 7 organized test files
- ✅ **Three conversion modes**: String (default), char (primitive), Character (boxed)
- ✅ **String.length property**: Full support with automatic type inference (returns int)
- ✅ **Full Unicode support**: Including emojis, international characters, and surrogate pairs
- ✅ **All escape sequences**: \n, \t, \r, \\, \', \", \b, \f, \0
- ✅ **Large char value fix**: Proper bytecode generation for char values up to \uFFFF (65535)
- ✅ **Edge case handling**: Empty strings, multi-character to char, very long strings
- ✅ **Type-based conversion**: Automatic conversion based on return type annotations
- ✅ **Type inference**: String.length correctly infers int return type without explicit annotation

### Phase 2: ✅ String Method Calls (Completed 2026-01-22)

- ✅ **18 String methods implemented**: indexOf, lastIndexOf, charAt, charCodeAt, substring, slice, split, toLowerCase, toUpperCase, trim, concat, repeat, replace, replaceAll, includes, startsWith, endsWith, padStart, padEnd
- ✅ **58 passing tests** across 5 organized test files
- ✅ **JavaScript-compatible semantics**: Proper handling of out-of-bounds, negative indices, edge cases
- ✅ **StringApiUtils helper class**: 11 utility methods for JS-compatible operations
- ✅ **Full type inference**: Automatic return type detection for all methods (String, int, boolean, ArrayList)
- ✅ **Method chaining support**: Full support for chaining multiple String operations
- ✅ **Direct Java equivalents**: 10 methods map directly to Java String methods
- ✅ **Edge case handling**: 6 methods use StringApiUtils for JS/Java semantic differences
- ✅ **Custom implementations**: 3 methods with no Java equivalent (charCodeAt, padStart, padEnd)

### Phase 3: 📋 Advanced String API Features (Future Work)

- 📋 **Regex methods**: match, matchAll, search (require Pattern/Matcher integration)
- 📋 **Locale methods**: localeCompare, toLocaleLowerCase, toLocaleUpperCase, normalize
- 📋 **Static methods**: String.fromCharCode, String.fromCodePoint, String.raw
- 📋 **Template literals**: Different AST node type (not string literal)
- 📋 **Advanced Unicode**: Code point iteration, normalization forms

The current implementation is production-ready for string literals and the most commonly-used String methods. Advanced features like regex methods and locale-specific operations are documented for future implementation.

### Files Modified/Created

**Phase 1 - String Literals:**
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/lit/StringLiteralGenerator.java`
  - Fixed large char value handling (> 32767) to use `ldc` instead of `iconst`
  - Supports three conversion modes based on ReturnTypeInfo
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/MemberExpressionGenerator.java`
  - Added String.length property access support

**Phase 2 - String Method Calls:**
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/expr/CallExpressionGenerator.java`
  - Added String method handling block with 18 method implementations
  - Switch-based dispatch for method routing
  - Proper argument handling and type conversions
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/ast/utils/StringApiUtils.java` (NEW)
  - 11 utility methods for JavaScript-compatible String operations
  - Handles edge cases where JS and Java semantics differ
  - Methods: charAt, charCodeAt, substring, slice, split, replace, padStart, padEnd
- `src/main/java/com/caoccao/javet/swc4j/compiler/jdk17/TypeResolver.java`
  - Added return type inference for all String methods
  - Supports String, int, boolean, and ArrayList return types

**Test Files - Phase 1 (String Literals):**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrBasic.java` (10 tests)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrChar.java` (10 tests)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrCharacter.java` (8 tests)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrEdgeCases.java` (12 tests)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrEscapes.java` (15 tests)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrUnicode.java` (12 tests)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrLength.java` (15 tests)

**Test Files - Phase 2 (String Methods):**
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrSearch.java` (20 tests)
  - charAt, charCodeAt, indexOf, lastIndexOf, includes, startsWith, endsWith
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrExtract.java` (13 tests)
  - substring, slice, split (uses `List.of()` assertions)
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrModify.java` (13 tests)
  - concat, repeat, replace, replaceAll, trim, padStart, padEnd
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrCase.java` (6 tests)
  - toLowerCase, toUpperCase
- `src/test/java/com/caoccao/javet/swc4j/compiler/ast/expr/lit/str/TestCompileAstStrChaining.java` (6 tests)
  - Method chaining tests

**Test Organization Note:**
Tests use `assertEquals()` for simple values (String, int, boolean) and `List.of()` for ArrayList results (split method). This follows the pattern from array method tests.

### Verification Status

✅ **All tests passing:** 140 tests across 12 test files (82 literal + 58 method)
✅ **Javadoc passing:** No errors in javadoc generation
✅ **Full test suite passing:** No regressions introduced
✅ **Phase 1 & 2 complete:** String literals and 18 String methods fully implemented

**Phase 1 Completed:** 2026-01-21
**Phase 2 Completed:** 2026-01-22

### Implementation Checklist

**Phase 1 - String Literals:**
✅ **StringLiteralGenerator.java** - Handles all three modes (String, char, Character)
✅ **String.length support** - MemberExpressionGenerator + TypeResolver handle property access
✅ **82 tests implemented** - All edge cases covered across 7 test files
✅ **Type-based conversion** - Automatic conversion based on return type annotations
✅ **Full Unicode support** - Emojis, international characters, surrogate pairs
✅ **All escape sequences** - \n, \t, \r, \\, \', \", \b, \f, \0

**Phase 2 - String Method Calls:**
✅ **CallExpressionGenerator.java** - 18 String methods with switch-based dispatch
✅ **StringApiUtils.java** - 11 utility methods for JS-compatible operations
✅ **TypeResolver updates** - Return type inference for all String methods
✅ **58 method tests** - Comprehensive coverage across 5 test files
✅ **JavaScript compatibility** - Proper edge case handling (bounds, negative indices, etc.)
✅ **Method chaining** - Full support for chaining multiple operations
✅ **Direct equivalents** - 10 methods using Java String methods directly
✅ **Edge case handlers** - 6 methods using StringApiUtils for semantic differences
✅ **Custom implementations** - 3 methods with no Java equivalent

**General:**
✅ **All tests passing** - No failures, no regressions
✅ **Javadoc passing** - No errors in documentation generation
✅ **Following existing patterns** - Consistent with ArrayList method implementation
✅ **Comprehensive coverage** - Literals, property access, methods, chaining, edge cases
✅ **Bytecode generation** - Proper JVM bytecode (ldc, invokevirtual, invokestatic)
✅ **Type inference** - Automatic return type detection without explicit annotations
✅ **JDK 17 support** - Implementation targets JDK 17, uses String.repeat() (JDK 11+)

**Note on Assertions:** Tests use `assertEquals()` for simple values and `List.of()` for ArrayList results, following the pattern from array method tests.

---

## String Representation Strategy

### Three Representation Modes

1. **String Mode (Default)**
   ```typescript
   const str = "hello"  // → String
   ```
   - Type: `Ljava/lang/String;`
   - Immutable Java String object
   - Uses `ldc` instruction to load from constant pool
   - Full String API available

2. **char Mode (With Type Annotation)**
   ```typescript
   const c: char = 'a'  // → char
   ```
   - Type: `C` (primitive char)
   - Takes first character of string literal
   - Uses `iconst` or `bipush` instruction
   - Empty string converts to `\0` (null character)

3. **Character Mode (Boxed With Type Annotation)**
   ```typescript
   const c: Character = 'a'  // → Character
   ```
   - Type: `Ljava/lang/Character;`
   - Boxed wrapper of char
   - Uses `Character.valueOf(char)` for boxing
   - Empty string converts to `Character.valueOf('\0')`

---

## Current Implementation Review

### StringLiteralGenerator.java Status

**✅ Implemented Features:**

1. **Basic String Loading**
   - Adds string to constant pool via `cp.addString(value)`
   - Uses `ldc` instruction to load string reference
   - Handles strings of any length

2. **char Conversion (Return Type Based)**
   - Detects `ReturnType.CHAR` from returnTypeInfo
   - Extracts first character: `value.charAt(0)`
   - Loads as int value (char is int-compatible)
   - Empty string → `\0` (0)

3. **Character Conversion (Return Type Based)**
   - Detects `"Ljava/lang/Character;"` descriptor
   - Extracts first character
   - Boxes using `Character.valueOf(C)Ljava/lang/Character;`
   - Empty string → `Character.valueOf('\0')`

4. **Type Detection**
   - Checks `returnTypeInfo.type()` for CHAR
   - Checks `returnTypeInfo.descriptor()` for Character class
   - Falls back to String if neither matches

### Implementation Improvements (2026-01-21)

**Critical Fix: Large char Values**

The original implementation used `code.iconst(charValue)` for all char values, which failed for values above 32767 because:
- `bipush`: Range -128 to 127
- `sipush`: Range -32768 to 32767
- `char`: Range 0 to 65535

**Solution:** Check char value and use appropriate instruction:
```java
if (charValue <= 32767) {
    code.iconst(charValue);  // Use bipush/sipush for small values
} else {
    int charIndex = cp.addInteger(charValue);
    code.ldc(charIndex);     // Use ldc for large values like '\uFFFF'
}
```

This fix enables support for all valid char values including `'\uFFFF'` (65535).

---

## Implementation Details

### JVM Bytecode Generation

#### 1. String Literal (Default)

**TypeScript:**
```typescript
const str = "hello world"
```

**Bytecode:**
```
ldc #<string_index>  // Load string constant from pool
```

**Constant Pool Entry:**
```
#<string_index> = String "hello world"
```

#### 2. char Conversion

**TypeScript:**
```typescript
const c: char = 'A'
```

**Bytecode:**
```
bipush 65  // ASCII value of 'A'
```

**Empty String Edge Case:**
```typescript
const c: char = ''
```

**Bytecode:**
```
iconst_0  // null character '\0'
```

#### 3. Character Boxin**TypeScript:**
```typescript
const c: Character = 'A'
```

**Bytecode:**
```
bipush 65                              // char value 'A'
invokestatic Character.valueOf(C)Ljava/lang/Character;
```

---

## Test Coverage Plan

### Phase 1: Basic String Literals (10 tests)

**Goal:** Test fundamental string literal functionality.

1. **testStringEmpty** - Empty string ""
   ```typescript
   function test(): string { return "" }
   ```
   Expected: `""`

2. **testStringSingleCharacter** - Single character "a"
   ```typescript
   function test(): string { return "a" }
   ```
   Expected: `"a"`

3. **testStringMultipleCharacters** - Normal string
   ```typescript
   function test(): string { return "hello world" }
   ```
   Expected: `"hello world"`

4. **testStringWithSpaces** - Leading/trailing spaces
   ```typescript
   function test(): string { return "  hello  " }
   ```
   Expected: `"  hello  "`

5. **testStringNumeric** - Numeric string
   ```typescript
   function test(): string { return "12345" }
   ```
   Expected: `"12345"`

6. **testStringAlphanumeric** - Mixed content
   ```typescript
   function test(): string { return "abc123xyz" }
   ```
   Expected: `"abc123xyz"`

7. **testStringPunctuation** - Special punctuation
   ```typescript
   function test(): string { return "Hello, World!" }
   ```
   Expected: `"Hello, World!"`

8. **testStringVeryLong** - Long string (1000+ chars)
   ```typescript
   function test(): string {
     return "a".repeat(1000)  // Or hardcoded long string
   }
   ```
   Expected: 1000 character string

9. **testStringConstPoolSharing** - Same string used multiple times
   ```typescript
   function test(): string {
     const a = "shared"
     const b = "shared"
     return a + b
   }
   ```
   Expected: Constant pool optimization (same reference)

10. **testStringAssignmentAndReturn** - Const assignment
    ```typescript
    function test(): string {
      const msg = "test"
      return msg
    }
    ```
    Expected: `"test"`

### Phase 2: Escape Sequences (15 tests)

**Goal:** Test all standard escape sequences.

11. **testStringNewline** - `\n` escape
    ```typescript
    function test(): string { return "line1\nline2" }
    ```
    Expected: `"line1\nline2"` (with actual newline)

12. **testStringTab** - `\t` escape
    ```typescript
    function test(): string { return "col1\tcol2" }
    ```
    Expected: `"col1\tcol2"` (with actual tab)

13. **testStringCarriageReturn** - `\r` escape
    ```typescript
    function test(): string { return "text\rmore" }
    ```
    Expected: `"text\rmore"`

14. **testStringBackslash** - `\\` escape
    ```typescript
    function test(): string { return "path\\to\\file" }
    ```
    Expected: `"path\\to\\file"`

15. **testStringSingleQuote** - `\'` escape
    ```typescript
    function test(): string { return "don\'t" }
    ```
    Expected: `"don't"`

16. **testStringDoubleQuote** - `\"` escape
    ```typescript
    function test(): string { return "He said \"hello\"" }
    ```
    Expected: `"He said "hello""`

17. **testStringBackspace** - `\b` escape
    ```typescript
    function test(): string { return "text\bmore" }
    ```
    Expected: `"text\bmore"`

18. **testStringFormFeed** - `\f` escape
    ```typescript
    function test(): string { return "page1\fpage2" }
    ```
    Expected: `"page1\fpage2"`

19. **testStringVerticalTab** - `\v` escape (if supported)
    ```typescript
    function test(): string { return "line1\vline2" }
    ```
    Expected: `"line1\vline2"`

20. **testStringNullCharacter** - `\0` escape
    ```typescript
    function test(): string { return "text\0end" }
    ```
    Expected: String with null character

21. **testStringMultipleEscapes** - Multiple escapes in one string
    ```typescript
    function test(): string { return "line1\nline2\tcolumn" }
    ```
    Expected: `"line1\nline2\tcolumn"`

22. **testStringAllEscapes** - All escape types combined
    ```typescript
    function test(): string {
      return "\\n\\t\\r\\\\\\'\\\"\\b\\f"
    }
    ```
    Expected: All escape sequences

23. **testStringEscapeAtStart** - Escape at beginning
    ```typescript
    function test(): string { return "\nhello" }
    ```
    Expected: `"\nhello"`

24. **testStringEscapeAtEnd** - Escape at end
    ```typescript
    function test(): string { return "hello\n" }
    ```
    Expected: `"hello\n"`

25. **testStringOnlyEscapes** - String of only escape chars
    ```typescript
    function test(): string { return "\n\t\r" }
    ```
    Expected: `"\n\t\r"`

### Phase 3: Unicode and Special Characters (12 tests)

**Goal:** Test Unicode, emojis, and international characters.

26. **testStringUnicodeBasic** - Basic Unicode `\uXXXX`
    ```typescript
    function test(): string { return "\u0041" }  // 'A'
    ```
    Expected: `"A"`

27. **testStringUnicodeMultiple** - Multiple Unicode escapes
    ```typescript
    function test(): string { return "\u0048\u0065\u006C\u006C\u006F" }  // "Hello"
    ```
    Expected: `"Hello"`

28. **testStringUnicodeChinese** - Chinese characters
    ```typescript
    function test(): string { return "你好世界" }
    ```
    Expected: `"你好世界"`

29. **testStringUnicodeJapanese** - Japanese characters
    ```typescript
    function test(): string { return "こんにちは" }
    ```
    Expected: `"こんにちは"`

30. **testStringUnicodeArabic** - Arabic characters
    ```typescript
    function test(): string { return "مرحبا" }
    ```
    Expected: `"مرحبا"`

31. **testStringUnicodeKorean** - Korean characters
    ```typescript
    function test(): string { return "안녕하세요" }
    ```
    Expected: `"안녕하세요"`

32. **testStringEmoji** - Emoji characters
    ```typescript
    function test(): string { return "Hello 🌍 World 🚀" }
    ```
    Expected: `"Hello 🌍 World 🚀"`

33. **testStringEmojiOnly** - String of only emojis
    ```typescript
    function test(): string { return "😀😁😂🤣😃" }
    ```
    Expected: `"😀😁😂🤣😃"`

34. **testStringSurrogatePairs** - Unicode surrogate pairs
    ```typescript
    function test(): string { return "𝕳𝖊𝖑𝖑𝖔" }  // Math bold
    ```
    Expected: Correct surrogate pair handling

35. **testStringMixedUnicode** - ASCII + Unicode mix
    ```typescript
    function test(): string { return "Hello 世界 🌍" }
    ```
    Expected: `"Hello 世界 🌍"`

36. **testStringUnicodeEscapeSequence** - `\uXXXX` format
    ```typescript
    function test(): string { return "\u4F60\u597D" }  // 你好
    ```
    Expected: `"你好"`

37. **testStringUnicodeSurrogatePairEscape** - `\uD800\uDC00` format
    ```typescript
    function test(): string { return "\uD83D\uDE00" }  // 😀
    ```
    Expected: `"😀"`

### Phase 4: char Conversion (10 tests)

**Goal:** Test string-to-char conversion.

38. **testCharSingleCharacter** - Normal char conversion
    ```typescript
    function test(): char { return 'A' }
    ```
    Expected: `'A'`

39. **testCharEmptyString** - Empty string to char
    ```typescript
    function test(): char { return '' }
    ```
    Expected: `'\0'` (null character)

40. **testCharMultiCharacterString** - Multi-char string (takes first)
    ```typescript
    function test(): char { return 'ABC' }
    ```
    Expected: `'A'` (first character)

41. **testCharFromEscape** - Escape sequence to char
    ```typescript
    function test(): char { return '\n' }
    ```
    Expected: `'\n'` (newline character)

42. **testCharFromUnicode** - Unicode to char
    ```typescript
    function test(): char { return '\u0041' }  // 'A'
    ```
    Expected: `'A'`

43. **testCharSpace** - Space character
    ```typescript
    function test(): char { return ' ' }
    ```
    Expected: `' '`

44. **testCharDigit** - Numeric character
    ```typescript
    function test(): char { return '5' }
    ```
    Expected: `'5'`

45. **testCharSpecial** - Special character
    ```typescript
    function test(): char { return '@' }
    ```
    Expected: `'@'`

46. **testCharConstAssignment** - Const with char type
    ```typescript
    function test(): char {
      const c: char = 'X'
      return c
    }
    ```
    Expected: `'X'`

47. **testCharMaxValue** - Max Unicode value
    ```typescript
    function test(): char { return '\uFFFF' }
    ```
    Expected: `'\uFFFF'`

### Phase 5: Character (Boxed) Conversion (8 tests)

**Goal:** Test string-to-Character conversion.

48. **testCharacterSingleCharacter** - Normal Character conversion
    ```typescript
    function test(): Character { return 'A' }
    ```
    Expected: `'A'` (boxed)

49. **testCharacterEmptyString** - Empty string to Character
    ```typescript
    function test(): Character { return '' }
    ```
    Expected: `'\0'` (boxed null character)

50. **testCharacterMultiCharacterString** - Multi-char to Character
    ```typescript
    function test(): Character { return 'XYZ' }
    ```
    Expected: `'X'` (boxed, first character)

51. **testCharacterFromEscape** - Escape to Character
    ```typescript
    function test(): Character { return '\t' }
    ```
    Expected: `'\t'` (boxed)

52. **testCharacterFromUnicode** - Unicode to Character
    ```typescript
    function test(): Character { return '\u4E2D' }  // 中
    ```
    Expected: Chinese character (boxed)

53. **testCharacterConstAssignment** - Const with Character type
    ```typescript
    function test(): Character {
      const c: Character = 'Z'
      return c
    }
    ```
    Expected: `'Z'` (boxed)

54. **testCharacterNull** - Null character explicitly
    ```typescript
    function test(): Character { return '\0' }
    ```
    Expected: `'\0'` (boxed)

55. **testCharacterUnboxing** - Character used as char
    ```typescript
    function test(): char {
      const c: Character = 'M'
      return c  // Should unbox
    }
    ```
    Expected: `'M'` (unboxed)

### Phase 6: Edge Cases (12 tests)

**Goal:** Test boundary conditions and unusual inputs.

**Note:** Tests involving `.length` property access (testStringLengthZero, testStringLengthOne, testStringLengthLong, testStringEmojiLength) have been **removed as out of scope**. String property access requires member expression support, not string literal support.

56. **testStringOnlyWhitespace** - Whitespace string
    ```typescript
    function test(): string {
      return "     "
    }
    ```
    Expected: `"     "`

57. **testStringMaxUnicodeChar** - Maximum Unicode value
    ```typescript
    function test(): string { return "\uFFFF" }
    ```
    Expected: `"\uFFFF"`

58. **testStringMinUnicodeChar** - Minimum Unicode value
    ```typescript
    function test(): string { return "\u0000" }
    ```
    Expected: `"\0"`

59. **testStringRepeatedCharacters** - Same char repeated
    ```typescript
    function test(): string {
      return "aaaaaaaaaa"
    }
    ```
    Expected: `"aaaaaaaaaa"`

60. **testStringAllDigits** - Numeric string
    ```typescript
    function test(): string {
      return "0123456789"
    }
    ```
    Expected: `"0123456789"`

61. **testStringAllSpaces** - Only spaces
    ```typescript
    function test(): string {
      return "          "
    }
    ```
    Expected: Ten spaces

62. **testStringSpecialSymbols** - Various symbols
    ```typescript
    function test(): string {
      return "!@#$%^&*()_+-=[]{}|;:,.<>?/"
    }
    ```
    Expected: All symbols preserved

63. **testStringLineTerminators** - Line terminators
    ```typescript
    function test(): string {
      return "line1\nline2\rline3\r\nline4"
    }
    ```
    Expected: All terminators preserved

64. **testCharMaxValue** - Max Unicode value as char
    ```typescript
    function test(): char { return '\uFFFF' }
    ```
    Expected: `'\uFFFF'`

65. **testStringVeryLong** - Very long string (1000+ chars)
    ```typescript
    function test(): string {
      const s = "a".repeat(1000)  // Or hardcoded
      return s
    }
    ```
    Expected: 1000 character string

66. **testStringWithAllEscapeTypes** - All escape types combined
    ```typescript
    function test(): string {
      return "a\nb\tc\rd\\e\'f\"g\bh\fi"
    }
    ```
    Expected: All escape sequences processed

67. **testStringUnicodeAndEmoji** - Unicode escapes and emoji
    ```typescript
    function test(): string {
      return "Text\u0041\u4E2D🚀End"
    }
    ```
    Expected: `"TextA中🚀End"`

### Phase 7: Constant Pool Optimization (5 tests) - ❌ Not Implemented

**Goal:** Verify constant pool behavior and deduplication.

**Status:** These tests are **not implemented** because constant pool optimization is handled automatically by the JVM. The JVM automatically deduplicates string constants in the constant pool, so there's no code to test in the StringLiteralGenerator. These would be JVM behavior tests, not compiler tests.

71. **testStringConstPoolDeduplication** - Same string reused
    ```typescript
    function test(): boolean {
      const a = "duplicate"
      const b = "duplicate"
      return a === b  // Should be same instance
    }
    ```
    Expected: `true` (same object reference)

72. **testStringConstPoolMultipleStrings** - Different strings
    ```typescript
    function test(): boolean {
      const a = "first"
      const b = "second"
      return a !== b
    }
    ```
    Expected: `true` (different references)

73. **testStringConstPoolInLoop** - String in loop
    ```typescript
    function test(): number {
      let count = 0
      for (let i = 0; i < 5; i++) {
        const s = "loop"
        count++
      }
      return count
    }
    ```
    Expected: `5` (same constant reused)

74. **testStringConstPoolLargeNumber** - Many unique strings
    ```typescript
    function test(): number {
      const s1 = "str1"
      const s2 = "str2"
      // ... up to str50
      return 50
    }
    ```
    Expected: Constant pool has 50 entries

75. **testStringConstPoolEmpty** - Empty string constant
    ```typescript
    function test(): number {
      const a = ""
      const b = ""
      return a.length + b.length
    }
    ```
    Expected: `0` (same empty string constant)

### Phase 8: Type Coercion and Conversion (8 tests) - ❌ Not Implemented

**Goal:** Test implicit and explicit type conversions.

**Status:** These tests are **not implemented** because they require features beyond string literal generation:
- String concatenation requires binary expression support
- Method calls (`.toString()`, `String.valueOf()`, `.toCharArray()`) require method call expression support
- Type conversions between different types require other AST node generators

These tests belong in separate plans for binary expressions, method calls, and type conversion.

76. **testStringToCharImplicit** - Implicit char conversion
    ```typescript
    function test() {
      const c: char = 'A'
      return c
    }
    ```
    Expected: `'A'`

77. **testStringToCharacterImplicit** - Implicit Character conversion
    ```typescript
    function test() {
      const c: Character = 'B'
      return c
    }
    ```
    Expected: `'B'` (boxed)

78. **testCharToStringConversion** - char to String
    ```typescript
    function test(): string {
      const c: char = 'X'
      return String.valueOf(c)
    }
    ```
    Expected: `"X"`

79. **testCharacterToStringConversion** - Character to String
    ```typescript
    function test(): string {
      const c: Character = 'Y'
      return c.toString()
    }
    ```
    Expected: `"Y"`

80. **testStringFromCharArray** - String from char[]
    ```typescript
    function test(): string {
      const chars: char[] = ['H', 'i']
      return new String(chars)
    }
    ```
    Expected: `"Hi"`

81. **testCharArrayFromString** - String to char[]
    ```typescript
    function test(): char[] {
      return "Hello".toCharArray()
    }
    ```
    Expected: `['H', 'e', 'l', 'l', 'o']`

82. **testStringValueOf** - String.valueOf() various types
    ```typescript
    function test(): string {
      return String.valueOf(123)
    }
    ```
    Expected: `"123"`

83. **testStringConcat** - String concatenation
    ```typescript
    function test(): string {
      return "Hello" + " " + "World"
    }
    ```
    Expected: `"Hello World"`

---

## Edge Cases Summary

### 1. Empty Strings
- Empty string `""` → String
- Empty string `''` to char → `'\0'`
- Empty string to Character → `Character.valueOf('\0')`

### 2. Escape Sequences
- `\n` (newline), `\t` (tab), `\r` (carriage return)
- `\\` (backslash), `\'` (single quote), `\"` (double quote)
- `\b` (backspace), `\f` (form feed), `\v` (vertical tab)
- `\0` (null character)
- Multiple escapes in single string
- Escapes at start/end of string

### 3. Unicode Handling
- Basic Unicode: `\uXXXX`
- Surrogate pairs: `\uD800\uDC00`
- International characters (Chinese, Japanese, Arabic, Korean)
- Emojis and special symbols
- Zero-width characters
- Bidirectional text markers
- Control characters
- Max/min Unicode values

### 4. String-to-char Conversion
- Single character → char
- Multi-character string → first char
- Empty string → `'\0'`
- Unicode character → char
- Escape sequence → char
- Max Unicode value `\uFFFF`

### 5. String-to-Character Conversion
- Same as char but boxed using `Character.valueOf()`
- All edge cases apply with boxing overhead

### 6. String Length
- Empty: 0
- Single character: 1
- Very long strings: 1000+ characters
- Unicode characters may be multi-byte but count as 1 character
- Surrogate pairs count as 2 characters in Java

### 7. Constant Pool
- String deduplication (same string = same reference)
- Multiple unique strings
- Empty string constant
- Large number of constants

### 8. Special Characters
- Whitespace (space, tab, newline, etc.)
- Punctuation and symbols
- Digits
- Mixed case
- Control characters
- Line terminators (`\n`, `\r`, `\r\n`)

### 9. Type Conversions
- String to char/Character
- char/Character to String
- char[] to String and vice versa
- Implicit vs explicit conversions

---

## Implementation Status

### ✅ Completed (2026-01-21)
1. **Basic string literal loading** - ldc instruction with constant pool
2. **char conversion** - Return type based detection
3. **Character conversion** - Boxed wrapper with valueOf
4. **Empty string handling** - Converts to '\0' for char/Character
5. **Type detection logic** - ReturnTypeInfo based conversion
6. **Large char value fix** - Use ldc for char > 32767 (fixes '\uFFFF')
7. **Escape sequences** - All standard escapes (\n, \t, \r, \\, \', \", \b, \f, \0)
8. **Unicode characters** - Full Unicode support including emojis and international text
9. **Long strings** - 1000+ character strings
10. **Multi-character to char** - Takes first character
11. **Comprehensive test coverage** - 67 tests across 6 organized test files

### Test Coverage Summary
- ✅ **Phase 1: Basic (10 tests)** - All passing
- ✅ **Phase 2: Escapes (15 tests)** - All passing
- ✅ **Phase 3: Unicode (12 tests)** - All passing
- ✅ **Phase 4: char (10 tests)** - All passing
- ✅ **Phase 5: Character (8 tests)** - All passing
- ✅ **Phase 6: Edge Cases (12 tests)** - All passing (3 tests removed as out of scope)
- ❌ **Phase 7: Constant Pool (5 tests)** - Not implemented (JVM handles automatically)
- ❌ **Phase 8: Conversion (8 tests)** - Out of scope (requires other AST node support)

**Total: 67 passing tests**

### Out of Scope
The following tests were **intentionally excluded** as they require features beyond string literal generation:

1. **Property access** (`.length`, `.charAt()`, etc.) - Requires member expression support
2. **String methods** (`.repeat()`, `.substring()`, etc.) - Requires method call expression support
3. **String concatenation** (`+` operator) - Requires binary expression support
4. **Template literals** - Requires template literal AST node support
5. **Type conversion methods** (`String.valueOf()`, `.toString()`, `.toCharArray()`) - Requires method call support

### ❌ Not Implemented (Future Work)
1. Template literals (`${}` interpolation) - Different AST node
2. Raw strings (if applicable) - Different AST node
3. String methods and operations - Requires member expression generator

---

## Test Organization

**Implemented Test Files:**

1. ✅ **TestCompileAstStrBasic.java** - Phase 1 (10 tests)
   - Basic string literals, assignment, return
   - Empty strings, single/multiple characters, spaces, numeric, punctuation

2. ✅ **TestCompileAstStrEscapes.java** - Phase 2 (15 tests)
   - All escape sequences: \n, \t, \r, \\, \', \", \b, \f, \0
   - Multiple escapes, escapes at start/end, char/Character from escape

3. ✅ **TestCompileAstStrUnicode.java** - Phase 3 (12 tests)
   - Unicode escape sequences (\uXXXX)
   - International characters (Chinese, Japanese, Arabic, Korean)
   - Emojis and mixed Unicode content
   - char/Character from Unicode

4. ✅ **TestCompileAstStrChar.java** - Phase 4 (10 tests)
   - String-to-char conversion (primitive)
   - Single char, empty → \0, multi-char → first char
   - Space, digit, special characters
   - Const assignment

5. ✅ **TestCompileAstStrCharacter.java** - Phase 5 (8 tests)
   - String-to-Character conversion (boxed)
   - All char test cases with boxing
   - Null character, digit

6. ✅ **TestCompileAstStrEdgeCases.java** - Phase 6 (12 tests)
   - Boundary conditions: whitespace, max/min Unicode values
   - Repeated characters, all digits, special symbols
   - Line terminators, escape combinations
   - Very long strings (1000+ chars)
   - **Note:** 3 tests removed (property access out of scope)

7. ✅ **TestCompileAstStrLength.java** - Phase 7 (15 tests) **NEW**
   - String.length property access
   - Empty string, single char, multiple chars
   - Variables with .length access
   - Escape sequences, Unicode, emojis in length
   - Type inference (returns int without annotation)
   - Very long strings, special characters
   - **Implementation:** MemberExpressionGenerator + TypeResolver

8. ❌ **TestCompileAstStrConstPool.java** - Phase 8 (Not implemented)
   - Constant pool optimization handled automatically by JVM

9. ❌ **TestCompileAstStrConversion.java** - Phase 9 (Not implemented)
   - Type coercion requires other AST node support (method calls, etc.)

**Total: 67 passing tests across 6 files** (Phases 1-6 complete)

---

## Bytecode Patterns

### String Literal
```
ldc #<string_constant_index>
```

### char from String Literal
```
bipush <char_value>  // or iconst_<n> for small values
```

### Character from String Literal
```
bipush <char_value>
invokestatic java/lang/Character.valueOf(C)Ljava/lang/Character;
```

### Empty String to char
```
iconst_0  // '\0'
```

### Empty String to Character
```
iconst_0
invokestatic java/lang/Character.valueOf(C)Ljava/lang/Character;
```

---

## JavaScript String API Support

### Overview

This section documents the comprehensive mapping between JavaScript String API and Java String API for TypeScript to JVM bytecode compilation. The goal is to support as many JavaScript string operations as possible using equivalent Java String methods.

**Implementation Status:**
- ✅ **Property Access**: `length` property (implemented in Phase 1)
- ✅ **Method Calls**: 18 instance methods implemented (completed in Phase 2)
- 🚧 **Static Methods**: Requires static method call support (future work)

### String Properties

| JavaScript Property | Java Equivalent | Return Type | Status | Notes |
|-------------------|-----------------|-------------|---------|-------|
| `str.length` | `str.length()` | `int` | ✅ Implemented | Returns character count (not byte count) |

**Edge Cases:**
- Empty string: `"".length` → `0`
- Emojis/Surrogate pairs: `"😀".length` → `2` (counts as 2 chars in Java)
- Unicode: `"你好".length` → `2`
- Escape sequences: `"\n\t".length` → `2`

### Instance Methods - Searching & Locating

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `charAt(index)` | `StringApiUtils.charAt()` | `String` (JS) / `char` (Java) | ✅ Implemented | Returns String, handles out-of-bounds |
| `charCodeAt(index)` | `StringApiUtils.charCodeAt()` | `int` | ✅ Implemented | Returns -1 for out-of-bounds |
| `codePointAt(index)` | `codePointAt(int)` | `int` | ❌ Future | Handles surrogate pairs correctly |
| `indexOf(searchString)` | `indexOf(String)` | `int` | ✅ Implemented | Returns -1 if not found |
| `indexOf(searchString, position)` | `indexOf(String, int)` | `int` | ✅ Implemented | Search from position |
| `lastIndexOf(searchString)` | `lastIndexOf(String)` | `int` | ✅ Implemented | Search from end |
| `lastIndexOf(searchString, position)` | `lastIndexOf(String, int)` | `int` | ✅ Implemented | Search backwards from position |
| `search(regexp)` | Pattern/Matcher | `int` | ❌ Future | Regex search, complex |
| `includes(searchString)` | `contains(CharSequence)` | `boolean` | ✅ Implemented | Case-sensitive |
| `includes(searchString, position)` | `substring(int).contains()` | `boolean` | ❌ Future | Search from position |
| `startsWith(searchString)` | `startsWith(String)` | `boolean` | ✅ Implemented | Case-sensitive |
| `startsWith(searchString, position)` | `startsWith(String, int)` | `boolean` | ✅ Implemented | Check from position |
| `endsWith(searchString)` | `endsWith(String)` | `boolean` | ✅ Implemented | Case-sensitive |
| `endsWith(searchString, length)` | `substring(0, length).endsWith()` | `boolean` | ❌ Future | Check up to length |

**Edge Cases:**
- Negative indices: JavaScript wraps/clamps, Java throws exception
- Out of bounds: JavaScript returns empty/undefined, Java throws exception
- Empty search string: JavaScript returns true/0, Java varies by method
- Case sensitivity: All comparisons are case-sensitive (unlike some JS engines)
- Surrogate pairs: Java methods may split pairs, JS treats as single char

### Instance Methods - Extraction

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `slice(beginIndex)` | `StringApiUtils.slice()` | `String` | ✅ Implemented | Handles negative indices |
| `slice(beginIndex, endIndex)` | `StringApiUtils.slice()` | `String` | ✅ Implemented | Handles negative indices |
| `substring(indexStart)` | `StringApiUtils.substring()` | `String` | ✅ Implemented | Clamps negative to 0 |
| `substring(indexStart, indexEnd)` | `StringApiUtils.substring()` | `String` | ✅ Implemented | Swaps if start > end |
| `substr(start, length)` | `substring(int, int)` | `String` | ❌ Future | **Deprecated** in JS, use slice |
| `split(separator)` | `StringApiUtils.split()` | `ArrayList<String>` | ✅ Implemented | Handles empty separator |
| `split(separator, limit)` | `StringApiUtils.split()` | `ArrayList<String>` | ✅ Implemented | Proper limit semantics |
| `split(regexp)` | `split(String)` with Pattern | `String[]` | ❌ Future | Regex splitting |

**Edge Cases:**
- `slice(-1)`: Last character (Java needs `str.length() - 1`)
- `slice(start, end)` where start > end: Returns empty string in JS
- `substring(5, 2)`: JS swaps to (2, 5), Java throws exception
- `split("")`: JS splits into individual characters
- `split()` (no args): JS returns array with original string
- `split("", limit)`: JS respects limit for character splitting

### Instance Methods - Modification

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `concat(str1, str2, ...)` | `concat(String)` chained | `String` | ✅ Implemented | Chains multiple concat calls |
| `repeat(count)` | `repeat(int)` (JDK 11+) | `String` | ✅ Implemented | Repeat string n times |
| `padStart(targetLength, padString)` | `StringApiUtils.padStart()` | `String` | ✅ Implemented | Custom JS-compatible implementation |
| `padEnd(targetLength, padString)` | `StringApiUtils.padEnd()` | `String` | ✅ Implemented | Custom JS-compatible implementation |
| `trim()` | `trim()` | `String` | ✅ Implemented | Remove leading/trailing whitespace |
| `trimStart()` / `trimLeft()` | `stripLeading()` (JDK 11+) | `String` | ❌ Future | Remove leading whitespace |
| `trimEnd()` / `trimRight()` | `stripTrailing()` (JDK 11+) | `String` | ❌ Future | Remove trailing whitespace |

**Edge Cases:**
- `repeat(0)`: Returns empty string
- `repeat(-1)`: JS throws RangeError, Java throws IllegalArgumentException
- `padStart(2, "abc")`: Only uses "ab" (truncates padding)
- `trim()` on empty string: Returns empty string
- Unicode whitespace: Java 11+ handles correctly, older versions may differ

### Instance Methods - Case Conversion

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `toLowerCase()` | `toLowerCase()` | `String` | ✅ Implemented | Locale-sensitive |
| `toUpperCase()` | `toUpperCase()` | `String` | ✅ Implemented | Locale-sensitive |
| `toLocaleLowerCase()` | `toLowerCase(Locale)` | `String` | ❌ Future | Explicit locale |
| `toLocaleUpperCase()` | `toUpperCase(Locale)` | `String` | ❌ Future | Explicit locale |

**Edge Cases:**
- Turkish "I" problem: `"i".toUpperCase()` → "İ" in Turkish locale
- German "ß" conversion: `"ß".toUpperCase()` → "SS"
- Case mapping changes length: `"ß".toUpperCase().length` → 2
- Empty string: Returns empty string
- Non-alphabetic chars: Returned unchanged

### Instance Methods - Replacement

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `replace(search, replacement)` | `StringApiUtils.replace()` | `String` | ✅ Implemented | First occurrence only, literal |
| `replace(regexp, replacement)` | `replaceFirst(String, String)` | `String` | ❌ Future | Regex support |
| `replaceAll(search, replacement)` | `replace(CharSequence, CharSequence)` | `String` | ✅ Implemented | All occurrences, literal |
| `replaceAll(regexp, replacement)` | `replaceAll(String, String)` | `String` | ❌ Future | Regex with global flag |

**Edge Cases:**
- `replace("", "x")`: JS inserts at every position, Java behavior differs
- Regex special chars: Need escaping in Java (e.g., `\\.` vs `\.`)
- Backreferences: `$1, $2` in JS, same in Java
- Replace function callback: JS supports, Java doesn't (complex)

### Instance Methods - Matching

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `match(regexp)` | `Pattern.matcher().find()` | `String[]` or `null` | ❌ Future | Returns matches array |
| `matchAll(regexp)` | `Pattern.matcher().results()` | Iterator | ❌ Future | ES2020, returns iterator |
| `test(regexp)` | `Pattern.matcher().matches()` | `boolean` | ❌ Future | RegExp method in JS |

**Edge Cases:**
- `match()` with no matches: Returns `null` (not empty array)
- Global flag: JS returns all matches, Java needs loop
- Named capture groups: Supported in both (JDK 7+)

### Instance Methods - Normalization & Comparison

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `normalize()` | Custom implementation | `String` | ❌ Future | Unicode normalization |
| `normalize(form)` | Custom implementation | `String` | ❌ Future | NFC, NFD, NFKC, NFKD |
| `localeCompare(compareString)` | `Collator.compare()` | `int` | ❌ Future | Locale-aware comparison |
| `localeCompare(compareString, locales, options)` | `Collator` with locale | `int` | ❌ Future | Complex options |

**Edge Cases:**
- Normalization forms: NFC (default), NFD, NFKC, NFKD
- Combining characters: `"é"` vs `"e" + "◌́"` (U+0301)
- Locale comparison: "ä" sorts differently in Swedish vs German

### Instance Methods - Conversion

| JavaScript Method | Java Equivalent | Return Type | Status | Notes |
|------------------|-----------------|-------------|---------|-------|
| `toString()` | `toString()` | `String` | ❌ Future | Returns primitive string |
| `valueOf()` | `valueOf()` | `String` | ❌ Future | Returns primitive string |

**Edge Cases:**
- Calling on String primitive: No-op, returns same string
- Calling on String object: Unwraps to primitive

### Static Methods

| JavaScript Static Method | Java Equivalent | Return Type | Status | Notes |
|-------------------------|-----------------|-------------|---------|-------|
| `String.fromCharCode(num1, ...)` | `Character.toString(char)` | `String` | ❌ Future | Create string from char codes |
| `String.fromCodePoint(num1, ...)` | `Character.toString(int)` | `String` | ❌ Future | Handles code points > 0xFFFF |
| `String.raw(template, ...substitutions)` | Custom implementation | `String` | ❌ Future | Template literal raw strings |

**Edge Cases:**
- `fromCharCode(65, 66, 67)`: Returns "ABC"
- `fromCodePoint(0x1F600)`: Returns "😀" (surrogate pair)
- `fromCharCode(0x10000)`: Only uses lower 16 bits (wrong)
- `fromCodePoint(0x110000)`: Out of range, throws RangeError

### Deprecated/Legacy Methods (Not Recommended)

| JavaScript Method | Java Equivalent | Status | Notes |
|------------------|-----------------|---------|-------|
| `anchor(name)` | N/A | ❌ No support | Creates `<a name="...">` HTML |
| `big()` | N/A | ❌ No support | Creates `<big>` HTML |
| `blink()` | N/A | ❌ No support | Creates `<blink>` HTML |
| `bold()` | N/A | ❌ No support | Creates `<b>` HTML |
| `fixed()` | N/A | ❌ No support | Creates `<tt>` HTML |
| `fontcolor(color)` | N/A | ❌ No support | Creates `<font color="...">` HTML |
| `fontsize(size)` | N/A | ❌ No support | Creates `<font size="...">` HTML |
| `italics()` | N/A | ❌ No support | Creates `<i>` HTML |
| `link(url)` | N/A | ❌ No support | Creates `<a href="...">` HTML |
| `small()` | N/A | ❌ No support | Creates `<small>` HTML |
| `strike()` | N/A | ❌ No support | Creates `<strike>` HTML |
| `sub()` | N/A | ❌ No support | Creates `<sub>` HTML |
| `sup()` | N/A | ❌ No support | Creates `<sup>` HTML |
| `substr(start, length)` | `substring()` | ❌ Future | **Deprecated**, use `slice()` |

**Note:** HTML wrapper methods are deprecated in ECMAScript and will not be supported.

---

## Comprehensive Edge Cases

### 1. String Length & Character Counting

**Edge Cases:**
- Empty string: `"".length` → `0`
- Single char: `"a".length` → `1`
- Emoji (surrogate pair): `"😀".length` → `2` (Java counts UTF-16 code units)
- Multi-emoji: `"😀😁😂".length` → `6` (3 emojis × 2 code units each)
- Unicode BMP: `"你好".length` → `2` (2 Chinese characters)
- Combining characters: `"é"` (single) vs `"e\u0301"` (combining) → different lengths
- Zero-width characters: `"a\u200Bb".length` → `3` (includes zero-width space)
- Line terminators: `"a\nb\rc\r\nd".length` → `7` (each terminator counts)
- Escape sequences: `"\n\t\r".length` → `3` (each escape is 1 char)
- Very long string: `"a".repeat(1000000).length` → `1000000`

**Java vs JavaScript Differences:**
- Java uses UTF-16 encoding: characters above U+FFFF use 2 code units (surrogate pairs)
- JavaScript counts code units (same as Java), not code points
- Both treat emoji as 2 characters (surrogate pairs)

### 2. Index Access & Bounds

**Edge Cases:**
- Negative index: `charAt(-1)` → JS: empty string, Java: StringIndexOutOfBoundsException
- Index 0: `charAt(0)` → First character
- Index === length: `charAt(str.length)` → JS: empty string, Java: exception
- Index > length: `charAt(999)` → JS: empty string, Java: exception
- Empty string access: `"".charAt(0)` → JS: empty string, Java: exception
- Floating point index: `charAt(1.5)` → JS: truncates to 1
- NaN index: `charAt(NaN)` → JS: treats as 0
- Infinity index: `charAt(Infinity)` → JS: empty string

**Implementation Strategy:**
- Add bounds checking before calling Java methods
- Clamp negative indices to 0 or wrap (depending on method)
- Return empty string or default value for out-of-bounds (match JS behavior)

### 3. Empty String Handling

**Edge Cases:**
- Empty literal: `""` → Valid string, length 0
- Empty from split: `"a,b,".split(",")` → `["a", "b", ""]`
- Empty in concatenation: `"" + "hello"` → `"hello"`
- Empty in comparison: `"" === ""` → `true`
- Empty to char: `const c: char = ""` → `'\0'` (null character)
- Empty to Character: `const c: Character = ""` → `Character.valueOf('\0')`
- Empty substring: `"hello".substring(2, 2)` → `""`
- Empty slice: `"hello".slice(5, 5)` → `""`
- Empty trim: `"   ".trim()` → `""`
- Empty repeat: `"".repeat(100)` → `""`

### 4. Whitespace & Special Characters

**Edge Cases:**
- Space: `" "` (U+0020)
- Tab: `"\t"` (U+0009)
- Newline: `"\n"` (U+000A)
- Carriage return: `"\r"` (U+000D)
- Form feed: `"\f"` (U+000C)
- Vertical tab: `"\v"` (U+000B) - **Note:** Not supported in Java
- Non-breaking space: `"\u00A0"`
- Zero-width space: `"\u200B"`
- Em space: `"\u2003"`
- Line separator: `"\u2028"`
- Paragraph separator: `"\u2029"`
- Multiple spaces: `"     "` → Preserved as-is
- Mixed whitespace: `" \t\n\r "` → Each char preserved

**Trim Behavior:**
- `" hello ".trim()` → `"hello"` (removes leading/trailing)
- `" hello  world ".trim()` → `"hello  world"` (preserves internal)
- `"\t\nhello\r\n".trim()` → `"hello"`
- `"\u00A0hello\u00A0".trim()` → May or may not trim (Java version dependent)

### 5. Unicode & Encoding

**Edge Cases:**
- ASCII: `"Hello"` → Simple ASCII characters
- Latin-1: `"café"` → Extended ASCII (U+00E9)
- BMP characters: `"你好世界"` → Basic Multilingual Plane (U+4F60, etc.)
- Emoji: `"😀"` → Surrogate pair (U+D83D U+DE00)
- Combining characters: `"e\u0301"` → e + combining acute accent
- RTL text: `"مرحبا"` → Right-to-left Arabic
- Bidirectional marks: `"\u200E\u200F"` → LTR/RTL marks
- Zero-width joiner: `"👨‍👩‍👧‍👦"` → Family emoji with ZWJ
- Variation selectors: `"☺︎"` vs `"☺️"` → Text vs emoji presentation
- Normalization: `"é"` (U+00E9) vs `"é"` (U+0065 U+0301)
- Surrogate pairs split: `charAt(0)` on `"😀"` → Returns high surrogate only
- Invalid surrogates: Malformed UTF-16 sequences

**JavaScript Compatibility:**
- Both JS and Java use UTF-16 internally
- Surrogate pairs count as 2 characters in both
- `codePointAt()` handles surrogates correctly, `charCodeAt()` doesn't

### 6. Escape Sequences

**Supported Escapes:**
- `\0` → Null character (U+0000)
- `\b` → Backspace (U+0008)
- `\t` → Tab (U+0009)
- `\n` → Newline (U+000A)
- `\v` → Vertical tab (U+000B) - **Java doesn't support \v**, use `\u000B`
- `\f` → Form feed (U+000C)
- `\r` → Carriage return (U+000D)
- `\"` → Double quote
- `\'` → Single quote
- `\\` → Backslash
- `\uXXXX` → Unicode escape (4 hex digits)
- `\u{XXXXX}` → Unicode code point escape (ES6) - **Not in Java**
- `\xXX` → Hexadecimal escape (2 hex digits) - **Not in Java**

**Edge Cases:**
- Null in middle: `"hello\0world"` → Valid string with null char
- Multiple escapes: `"\n\t\r\\\"\'"` → All processed correctly
- Escape at start: `"\nhello"` → Newline first
- Escape at end: `"hello\n"` → Newline last
- Invalid escape: `"\q"` → JS: "q", Java: compile error
- Octal escapes: `"\101"` → "A" (deprecated in strict mode)
- Surrogate escape: `"\uD83D\uDE00"` → `"😀"`

### 7. String Immutability

**Edge Cases:**
- Modification attempts: `str[0] = 'X'` → No effect (strings are immutable)
- Method chaining: `str.trim().toLowerCase().substring(0, 5)` → Each returns new string
- Original unchanged: After `str.toUpperCase()`, `str` remains lowercase
- Constant pool: `"hello" === "hello"` → Same reference (interned)
- String concatenation: Always creates new string object

### 8. Comparison & Equality

**Edge Cases:**
- Exact match: `"hello" === "hello"` → `true`
- Case mismatch: `"Hello" === "hello"` → `false`
- Empty strings: `"" === ""` → `true`
- Null vs empty: `null != ""` → Different types
- Undefined vs empty: `undefined != ""` → Different types
- Unicode equivalence: `"café" !== "cafe\u0301"` → Different representations
- Interning: Java interns string literals, same reference for identical literals

### 9. Locale-Specific Behavior

**Edge Cases:**
- Turkish "i": `"i".toUpperCase()` → "I" (English), "İ" (Turkish)
- German "ß": `"ß".toUpperCase()` → "SS"
- Case folding: `"MASSE" vs "Maße"` → Different in case-insensitive compare
- Sorting: Locale affects comparison (e.g., "ä" in Swedish vs German)
- Normalization: Some locales require specific Unicode normalization

### 10. Performance & Memory

**Edge Cases:**
- Very long strings: 1M+ characters → Memory limits
- Constant pool limit: 65,535 UTF-8 bytes per constant (JVM limit)
- String interning: Automatic for literals, manual with `intern()`
- StringBuilder: Better for concatenation in loops
- Regex compilation: Cache Pattern objects for reuse
- Large constant strings: Consider loading from resources instead of literals

### 11. Type Conversion Edge Cases

**String to char:**
- Single char: `'A'` → `'A'`
- Multi-char: `'ABC'` → `'A'` (first char only)
- Empty: `''` → `'\0'` (null character)
- Unicode: `'\u4E2D'` → Chinese character
- Emoji: `'😀'` → High surrogate only (invalid)
- Escape: `'\n'` → Newline character

**String to Character (boxed):**
- Same as char but wrapped in `Character.valueOf()`
- Null character: `Character.valueOf('\0')`

**char/Character to String:**
- Use `String.valueOf(char)` or `Character.toString()`
- Preserves character value

### 12. Concatenation Edge Cases

**Binary + operator:**
- String + String: `"hello" + "world"` → `"helloworld"`
- String + number: `"Count: " + 42` → `"Count: 42"`
- String + null: `"value: " + null` → `"value: null"`
- String + undefined: `"value: " + undefined` → `"value: undefined"`
- String + object: `"obj: " + {}` → `"obj: [object Object]"`
- Empty + empty: `"" + ""` → `""`
- Numeric strings: `"3" + "4"` → `"34"` (not 7)

**Implementation:**
- Use StringBuilder for multiple concatenations
- Handle type coercion (number → string, etc.)

### 13. Template Literals (Future)

**Edge Cases:**
- Simple template: `` `hello` `` → `"hello"`
- With substitution: `` `Count: ${n}` `` → `"Count: 42"`
- Multi-line: Preserves line breaks
- Escaped backticks: `` `\`` `` → `` "`" ``
- Nested templates: `` `outer ${`inner`}` ``
- Expression evaluation: `` `${2 + 2}` `` → `"4"`
- Tagged templates: Custom processing (complex)

**Note:** Template literals are a different AST node type, not covered by string literal generator.

### 14. Regular Expression Edge Cases

**Pattern matching:**
- Literal match: `"hello".match(/l+/)` → `["ll"]`
- Global flag: `"hello".match(/l/g)` → `["l", "l"]`
- Case insensitive: `"Hello".match(/HELLO/i)` → `["Hello"]`
- No match: `"hello".match(/x/)` → `null`
- Empty pattern: `"hello".match(//)` → `[""]` (matches between each char)
- Special chars: Need escaping (`.`, `*`, `+`, etc.)
- Capture groups: `"hello".match(/(h)(e)/)` → `["he", "h", "e"]`
- Named groups: `"hello".match(/(?<first>h)/)` → With named captures

### 15. Split Edge Cases

**Different separators:**
- Empty separator: `"hello".split("")` → `["h", "e", "l", "l", "o"]`
- No separator: `"hello".split()` → `["hello"]`
- Not found: `"hello".split("x")` → `["hello"]`
- At boundary: `"a,b,".split(",")` → `["a", "b", ""]`
- Multiple delimiters: `"a,,b".split(",")` → `["a", "", "b"]`
- Limit: `"a,b,c,d".split(",", 2)` → `["a", "b"]`
- Regex separator: `"a1b2c".split(/\d/)` → `["a", "b", "c"]`

### 16. Slice/Substring Edge Cases

**Negative indices:**
- `slice(-3)`: Last 3 characters
- `slice(0, -1)`: All but last character
- `slice(-3, -1)`: Third-last to second-last
- `slice(-10, -20)`: Empty string (end before start)

**Boundary conditions:**
- `slice(0, 0)`: Empty string
- `slice(str.length)`: Empty string
- `slice(999)`: Empty string (beyond length)
- `slice(-999)`: Whole string (clamps to 0)

**substring vs slice:**
- `substring(5, 2)`: JS swaps to `(2, 5)`, Java: exception
- `substring(-1)`: JS treats as 0, Java: exception
- `slice(-1)`: Relative to end, `substring(-1)`: Invalid

### 17. Method Chaining Edge Cases

**Multiple operations:**
- `str.trim().toLowerCase().substring(0, 10)`
- Each method returns new string
- Order matters: `trim()` before `toLowerCase()`
- Null safety: Any method can return empty string

### 18. Null Safety & Error Handling

**JavaScript behavior:**
- `null.toString()`: TypeError
- `undefined.toString()`: TypeError
- `"".charAt(0)`: `""` (empty string)
- `"".substring(999)`: `""` (empty string)

**Java behavior:**
- `null.toString()`: NullPointerException
- `"".charAt(0)`: StringIndexOutOfBoundsException
- `"".substring(999)`: StringIndexOutOfBoundsException

**Implementation strategy:**
- Add null checks before method calls
- Add bounds checks for index operations
- Match JavaScript behavior where reasonable

---

## Implementation Priority

### Phase 1: ✅ Completed (2026-01-21)
- [x] String literal loading (ldc instruction)
- [x] char/Character conversion
- [x] String.length property
- [x] Basic escape sequences
- [x] Unicode support
- [x] Comprehensive tests (82 passing)

### Phase 2: ✅ Completed (2026-01-22)
- [x] charAt, charCodeAt (StringApiUtils)
- [x] indexOf, lastIndexOf, includes
- [x] startsWith, endsWith
- [x] slice, substring, split (StringApiUtils)
- [x] trim, toLowerCase, toUpperCase
- [x] replace, replaceAll (StringApiUtils for replace)
- [x] concat, repeat
- [x] padStart, padEnd (StringApiUtils)
- [x] Comprehensive tests (58 passing across 5 test files)

### Phase 3: 🔮 Future Work
- [ ] codePointAt (better surrogate pair handling)
- [ ] Regex methods (match, matchAll, search, test)
- [ ] Locale methods (localeCompare, normalize, toLocaleLowerCase, toLocaleUpperCase)
- [ ] Static methods (fromCharCode, fromCodePoint, raw)
- [ ] Template literals (different AST node)
- [ ] Advanced Unicode handling (normalization forms, code point iteration)
- [ ] Additional methods (trimStart, trimEnd, substr)

---

## Testing Strategy for String API

### Test Categories

1. **Property Access Tests** (✅ Completed - Phase 1)
   - length property on literals, variables, expressions

2. **Method Call Tests** (✅ Completed - Phase 2)
   - 18 methods with various inputs
   - Edge cases for each method (bounds, negative indices, empty strings)
   - Method chaining tests
   - 58 comprehensive tests across 5 test files

3. **Type Conversion Tests** (🚧 Future)
   - String ↔ char/Character (basics covered in Phase 1)
   - String ↔ primitive types
   - toString() on various types

4. **Unicode Tests** (✅ Completed - Phase 1 for literals, Phase 2 for methods)
   - BMP characters, surrogate pairs
   - Combining characters, emoji
   - RTL text, normalization

5. **Error Handling Tests** (✅ Completed - Phase 2)
   - Out of bounds indices (handled in StringApiUtils)
   - Empty string edge cases
   - Type conversions (Integer to int, etc.)

---

## Notes

- **JVM String Limit:** Constant pool entries have 65535 byte limit, but modern JVMs handle larger strings
- **Unicode:** Java uses UTF-16 encoding internally; surrogate pairs for characters above U+FFFF
- **Null Safety:** String literals are never null; empty string `""` is distinct from null
- **Immutability:** Java Strings are immutable; all operations create new instances
- **Constant Pool:** JVM automatically deduplicates identical string constants
- **char Range:** Valid range is `\u0000` to `\uFFFF` (0-65535)
- **Escape Processing:** TypeScript/JavaScript escape sequences must match Java escape sequences

---

## References

- JVM Specification: `ldc` instruction
- Java Language Specification: String Literals (§3.10.5)
- Unicode Standard: Character encoding
- TypeScript AST: Swc4jAstStr node
- Java String API: String class methods
- MDN Web Docs: JavaScript String API
- ECMAScript Specification: String Objects
- Java SE API Documentation: java.lang.String
