# Source Map

swc4j provides comprehensive support for JavaScript/TypeScript source maps through the `SourceMapUtils` class, enabling accurate mapping between generated code and original source code for debugging and error tracking.

## Overview

Source maps are JSON files that map positions in generated/transformed JavaScript code back to their original positions in the source TypeScript/JavaScript files. This is essential for:

- **Debugging transformed code** - Step through original source code when debugging minified/transpiled code
- **Error reporting** - Display errors at their original source locations
- **Code coverage** - Map coverage data from generated code back to source files
- **Development tools** - Enable browser DevTools to show original source code

swc4j's `SourceMapUtils` class parses [Source Map Revision 3](https://sourcemaps.info/spec.html) format and provides efficient position mapping with lazy parsing for optimal performance.

## What is a Source Map?

A source map is a JSON file with the following structure:

```json
{
  "version": 3,
  "sources": ["original.ts", "utils.ts"],
  "sourcesContent": ["const x = 1;", "export function fn() {}"],
  "names": ["myVar", "myFunc"],
  "mappings": "AAAA,SAAS,GAAG;EACZ,OAAO"
}
```

**Key Fields:**

- `version` - Source map format version (swc4j supports version 3)
- `sources` - Array of original source file paths
- `sourcesContent` - Array of original source file contents (can contain `null`)
- `names` - Array of identifier names from the original code (can contain `null`)
- `mappings` - VLQ-encoded string representing position mappings

### VLQ Encoding

The `mappings` field uses **Variable-Length Quantity (VLQ)** encoding with Base64:

- Each segment represents a mapping from generated position to original position
- Segments are comma-separated within a line, lines are semicolon-separated
- Values are delta-encoded (relative to previous values)

**Segment Types:**

- **1 field**: `[generatedColumn]` - Position marker without source mapping
- **4 fields**: `[generatedColumn, sourceIndex, originalLine, originalColumn]`
- **5 fields**: `[generatedColumn, sourceIndex, originalLine, originalColumn, nameIndex]`

## Use Cases

### 1. Parsing Transform Output Source Maps

```java
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.utils.SourceMapUtils;

Swc4j swc4j = new Swc4j();

// Transform TypeScript with source map generation
String code = "const add = (a: number, b: number) => a + b;";
Swc4jTransformOutput output = swc4j.transform(code, transformOptions
    .setSourceMap(Swc4jSourceMapOption.Separate));

// Parse the source map
SourceMapUtils sourceMap = SourceMapUtils.of(output.getSourceMap());

System.out.println("Sources: " + sourceMap.getSourceFilePaths());
System.out.println("Mappings: " + sourceMap.getMappings());
```

### 2. Mapping Generated Positions to Original Code

```java
// Get mapping for position in generated code (0-based line and column)
SourceMapUtils.SourceNode node = sourceMap.getNode(0, 15);

if (node != null) {
    System.out.println("Generated position: " +
        node.generatedPosition.line + ":" + node.generatedPosition.column);
    System.out.println("Original position: " +
        node.originalPosition.line + ":" + node.originalPosition.column);
    System.out.println("Source file: " +
        sourceMap.getSourceFilePaths().get(node.sourceFileIndex));

    // Check if this position has an associated name
    if (node.nameIndex >= 0) {
        System.out.println("Original name: " +
            sourceMap.getNames().get(node.nameIndex));
    }
}
```

### 3. Error Location Mapping

```java
/**
 * Map an error from generated code back to original source.
 */
public void reportError(String sourceMapJson, int errorLine, int errorColumn, String message) {
    SourceMapUtils sourceMap = SourceMapUtils.of(sourceMapJson);
    SourceMapUtils.SourceNode node = sourceMap.getNode(errorLine, errorColumn);

    if (node != null) {
        String sourceFile = sourceMap.getSourceFilePaths().get(node.sourceFileIndex);
        int originalLine = node.originalPosition.line;
        int originalColumn = node.originalPosition.column;

        System.err.printf("Error in %s at line %d, column %d: %s%n",
            sourceFile, originalLine + 1, originalColumn + 1, message);

        // Optionally show the original source code line
        String sourceContent = sourceMap.getSourceContents().get(node.sourceFileIndex);
        if (sourceContent != null) {
            String[] lines = sourceContent.split("\n");
            if (originalLine < lines.length) {
                System.err.println("  " + lines[originalLine]);
            }
        }
    } else {
        System.err.printf("Error at generated position %d:%d: %s%n",
            errorLine + 1, errorColumn + 1, message);
    }
}
```

### 4. Lazy Parsing for Performance

```java
// SourceMapUtils uses lazy parsing - mappings are only decoded on demand
SourceMapUtils sourceMap = SourceMapUtils.of(largeSourceMapJson);

// Only the requested line is parsed initially
SourceMapUtils.SourceNode node1 = sourceMap.getNode(5, 10);  // Parses lines 0-5

// Subsequent requests for earlier lines use cached data
SourceMapUtils.SourceNode node2 = sourceMap.getNode(3, 8);   // Uses cached line 3

// Later lines trigger incremental parsing
SourceMapUtils.SourceNode node3 = sourceMap.getNode(100, 20); // Parses lines 6-100
```

## API Reference

### SourceMapUtils

**Factory Method:**

```java
static SourceMapUtils of(String sourceMapJson)
```

Parse a source map JSON string. Throws `IllegalArgumentException` if:

- JSON is invalid or not an object
- Required fields are missing (`version`, `sources`, `sourcesContent`, `names`, `mappings`)
- Version is not "3"
- Arrays are empty (except `names` can be empty)

**Query Methods:**

```java
SourceNode getNode(int line, int column)
SourceNode getNode(SourcePosition position)
```

Get the source mapping for a position in generated code (0-based). Returns `null` if no mapping exists for that position.

**Accessor Methods:**

```java
List<String> getSourceFilePaths()    // May contain null elements
List<String> getSourceContents()     // May contain null elements
List<String> getNames()               // May contain null elements
String getMappings()                  // Raw VLQ-encoded mappings string
```

### SourceNode

Represents a mapping between generated and original code positions.

**Fields:**

```java
SourcePosition generatedPosition    // Position in generated code
SourcePosition originalPosition     // Position in original source
int sourceFileIndex                 // Index into sources array (0-based)
int nameIndex                       // Index into names array (0-based, or -1 if no name)
```

**Factory Methods:**

```java
static SourceNode of()
static SourceNode of(SourceNode node)
static SourceNode of(SourcePosition originalPosition, SourcePosition generatedPosition)
static SourceNode of(SourcePosition originalPosition, SourcePosition generatedPosition, int sourceFileIndex)
static SourceNode of(SourcePosition originalPosition, SourcePosition generatedPosition, int sourceFileIndex, int nameIndex)
```

### SourcePosition

Represents a position in source code (0-based line and column).

**Fields:**

```java
int line      // 0-based line number
int column    // 0-based column number
```

**Factory Methods:**

```java
static SourcePosition of()                          // Returns (0, 0)
static SourcePosition of(int line, int column)
static SourcePosition of(SourcePosition position)   // Copy constructor
```

## Technical Details

### VLQ Decoding

SourceMapUtils implements the Source Map Revision 3 VLQ decoding algorithm:

1. **Base64 decoding** - Each character maps to a 6-bit value (0-63)
2. **Continuation bit** - Bit 5 indicates if more digits follow
3. **Sign bit** - Bit 0 of the final value indicates sign
4. **Delta encoding** - Values are relative to previous segment

Example VLQ decoding:

- `A` = `0` → Binary `000000` → Decimal `0`
- `C` = `2` → Binary `000010` → Decimal `1` (after sign bit processing)
- `AAAA` = `[0, 0, 0, 0]` → Generated column +0, source index +0, original line +0, original column +0

### Null Preservation

The source map specification allows `null` values in arrays to indicate missing or unavailable data. SourceMapUtils preserves these nulls to maintain correct index alignment:

```java
// Source map with nulls
{
  "sources": ["file1.js", null, "file3.js"],
  "sourcesContent": ["content1", null, "content3"]
}

// Accessing via index maintains alignment
List<String> sources = sourceMap.getSourceFilePaths();
sources.get(0);  // "file1.js"
sources.get(1);  // null
sources.get(2);  // "file3.js"
```

### Index Bounds Checking

SourceMapUtils validates all indices during parsing:

- **sourceFileIndex**: Must be `>= 0` and `< sources.length`
- **nameIndex**: Must be `>= -1` (where `-1` means "no name") and `< names.length`

Invalid indices throw `IllegalArgumentException` with descriptive error messages.

### Performance Characteristics

- **Lazy parsing**: Mappings are parsed line-by-line on demand
- **Cached results**: Parsed lines are cached for subsequent queries
- **Incremental parsing**: Only parses up to the requested line
- **Memory efficient**: Avoids parsing the entire mappings string upfront

**Time Complexity:**

- First query for line N: O(N) - must parse lines 0 through N
- Subsequent queries for line ≤ N: O(log M) - binary search in cached nodes (M = nodes per line)
- Query for line > N: O(K) where K = number of new lines to parse

## Common Patterns

### Pattern 1: Full Source Map Information

```java
SourceMapUtils sourceMap = SourceMapUtils.of(sourceMapJson);

// Get complete mapping information
SourceNode node = sourceMap.getNode(line, column);
if (node != null) {
    String sourceFile = sourceMap.getSourceFilePaths().get(node.sourceFileIndex);
    String sourceContent = sourceMap.getSourceContents().get(node.sourceFileIndex);
    String originalName = node.nameIndex >= 0
        ? sourceMap.getNames().get(node.nameIndex)
        : null;

    System.out.printf("Position %d:%d in generated code maps to:%n",
        node.generatedPosition.line, node.generatedPosition.column);
    System.out.printf("  File: %s%n", sourceFile);
    System.out.printf("  Position: %d:%d%n",
        node.originalPosition.line, node.originalPosition.column);
    if (originalName != null) {
        System.out.printf("  Name: %s%n", originalName);
    }
}
```

### Pattern 2: Handling Null Sources

```java
SourceNode node = sourceMap.getNode(line, column);
if (node != null) {
    String sourceFile = sourceMap.getSourceFilePaths().get(node.sourceFileIndex);
    String sourceContent = sourceMap.getSourceContents().get(node.sourceFileIndex);

    // Handle potential null values
    if (sourceFile != null && sourceContent != null) {
        // Both source file path and content available
        displaySourceContext(sourceFile, sourceContent, node.originalPosition);
    } else if (sourceFile != null) {
        // Only file path available, content is null
        System.out.println("Source: " + sourceFile + " (content not available)");
    } else {
        // Source file path is null
        System.out.println("Source information not available");
    }
}
```

### Pattern 3: Position Range Mapping

```java
/**
 * Map a range in generated code to original source.
 */
public void mapRange(SourceMapUtils sourceMap, int startLine, int startCol, int endLine, int endCol) {
    SourceNode startNode = sourceMap.getNode(startLine, startCol);
    SourceNode endNode = sourceMap.getNode(endLine, endCol);

    if (startNode != null && endNode != null) {
        // Both start and end positions have mappings
        if (startNode.sourceFileIndex == endNode.sourceFileIndex) {
            // Range maps to single source file
            System.out.printf("Range maps to %s from %d:%d to %d:%d%n",
                sourceMap.getSourceFilePaths().get(startNode.sourceFileIndex),
                startNode.originalPosition.line, startNode.originalPosition.column,
                endNode.originalPosition.line, endNode.originalPosition.column);
        } else {
            // Range spans multiple source files
            System.out.println("Range spans multiple source files");
        }
    }
}
```

## Integration with swc4j

Source maps are generated by transformation operations when configured:

```java
Swc4j swc4j = new Swc4j();

// Enable source map generation
Swc4jTransformOptions options = new Swc4jTransformOptions()
    .setSourceMap(Swc4jSourceMapOption.Separate);  // or Inline

Swc4jTransformOutput output = swc4j.transform(code, options);

// Parse the generated source map
SourceMapUtils sourceMap = SourceMapUtils.of(output.getSourceMap());
```

**Source Map Options:**

- `Swc4jSourceMapOption.None` - No source map generated
- `Swc4jSourceMapOption.Inline` - Source map embedded in output code
- `Swc4jSourceMapOption.Separate` - Source map returned separately

## Error Handling

### Invalid JSON Structure

```java
try {
    SourceMapUtils sourceMap = SourceMapUtils.of(invalidJson);
} catch (IllegalArgumentException e) {
    // Handle parsing errors
    System.err.println("Invalid source map: " + e.getMessage());
}
```

Common validation errors:

- Missing required fields (`version`, `sources`, `sourcesContent`, `names`, `mappings`)
- Unsupported version (only version 3 is supported)
- Empty arrays (sources and sourcesContent must not be empty)
- Invalid VLQ encoding in mappings
- Out-of-bounds indices in mappings

### Position Not Found

```java
SourceNode node = sourceMap.getNode(line, column);
if (node == null) {
    // No mapping exists for this position
    // This is normal for:
    // - Positions in whitespace
    // - Positions in generated code without source correspondence
    // - Invalid positions (negative or beyond parsed content)
}
```

## Best Practices

1. **Cache SourceMapUtils instances** - Parsing is lazy but initial setup has overhead
2. **Check for null** - Source file paths, contents, and names can be `null`
3. **Use 0-based indices** - All positions and indices are 0-based
4. **Validate nameIndex** - Check `>= 0` before accessing names array
5. **Handle missing mappings** - Not all generated positions have source mappings

## Limitations

- Only Source Map Revision 3 is supported (version 3)
- Source map must be valid JSON
- All VLQ segments must be properly encoded
- Large source maps are parsed incrementally but still consume memory for cached lines

## References

- [Source Map Revision 3 Specification](https://sourcemaps.info/spec.html)
- [Source Map Introduction](https://developer.mozilla.org/en-US/docs/Tools/Debugger/How_to/Use_a_source_map)
- [VLQ Encoding Explanation](https://www.lucidchart.com/techblog/2019/08/22/decode-encoding-base64-vlqs-source-maps/)

## See Also

- [Transform](../transform.md) - Generate source maps during transformation
- [Transpile](../transpile.md) - TypeScript transpilation with source maps
- [Parse](../parse.md) - Parse JavaScript/TypeScript code
