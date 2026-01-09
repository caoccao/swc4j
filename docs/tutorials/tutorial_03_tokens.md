# Tutorial 03: Tokens

In this tutorial, we are going to learn the following.

* Parse a TypeScript code snippet.
* Process the tokens.

## Parse TypeScript

* Create a quick sort function in TypeScript as follows. Please make sure capture tokens is set to true. Then parse the code and get the output.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a TypeScript code snippet.
String code = "function quickSort(arr: number[]): number[] {\n" +
        "  if (arr.length <= 1) {\n" +
        "    return arr;\n" +
        "  }\n" +
        "\n" +
        "  const pivot = arr[arr.length - 1];\n" +
        "  const leftArr = [];\n" +
        "  const rightArr = [];\n" +
        "\n" +
        "  for (let i = 0; i < arr.length - 1; i++) {\n" +
        "    if (arr[i] < pivot) {\n" +
        "      leftArr.push(arr[i]);\n" +
        "    } else {\n" +
        "      rightArr.push(arr[i]);\n" +
        "    }\n" +
        "  }\n" +
        "\n" +
        "  return [...quickSort(leftArr), pivot, ...quickSort(rightArr)];\n" +
        "}";
// Prepare a script name.
URL specifier = URI.create("file:///abc.ts").toURL();
// Prepare an option with script name and media type.
Swc4jTranspileOptions options = new Swc4jTranspileOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.TypeScript)
        // Set capture tokens.
        .setCaptureTokens(true)
        .setParseMode(Swc4jParseMode.Script);
// Parse the code.
Swc4jParseOutput output = swc4j.parse(code, options);
// Print the tokens.
System.out.println("/*********************************************");
System.out.println("         The tokens are as follows.");
System.out.println("*********************************************/");
output.getTokens().forEach(System.out::println);
```

* The tokens in the output is printed as follows.

```js
/*********************************************
         The tokens are as follows.
*********************************************/
{ "lineBreakAhead": true, "span": { start: 0, end: 8, line: 1, column: 1 }, "type": "Function", "text": "function" }
{ "lineBreakAhead": false, "span": { start: 9, end: 18, line: 1, column: 10 }, "type": "IdentOther", "text": "quickSort" }
{ "lineBreakAhead": false, "span": { start: 18, end: 19, line: 1, column: 19 }, "type": "LParen", "text": "(" }
{ "lineBreakAhead": false, "span": { start: 19, end: 22, line: 1, column: 20 }, "type": "IdentOther", "text": "arr" }
{ "lineBreakAhead": false, "span": { start: 22, end: 23, line: 1, column: 23 }, "type": "Colon", "text": ":" }
{ "lineBreakAhead": false, "span": { start: 24, end: 30, line: 1, column: 25 }, "type": "IdentKnown", "text": "number" }
{ "lineBreakAhead": false, "span": { start: 30, end: 31, line: 1, column: 31 }, "type": "LBracket", "text": "[" }
{ "lineBreakAhead": false, "span": { start: 31, end: 32, line: 1, column: 32 }, "type": "RBracket", "text": "]" }
{ "lineBreakAhead": false, "span": { start: 32, end: 33, line: 1, column: 33 }, "type": "RParen", "text": ")" }
{ "lineBreakAhead": false, "span": { start: 33, end: 34, line: 1, column: 34 }, "type": "Colon", "text": ":" }
{ "lineBreakAhead": false, "span": { start: 35, end: 41, line: 1, column: 36 }, "type": "IdentKnown", "text": "number" }
{ "lineBreakAhead": false, "span": { start: 41, end: 42, line: 1, column: 42 }, "type": "LBracket", "text": "[" }
{ "lineBreakAhead": false, "span": { start: 42, end: 43, line: 1, column: 43 }, "type": "RBracket", "text": "]" }
{ "lineBreakAhead": false, "span": { start: 44, end: 45, line: 1, column: 45 }, "type": "LBrace", "text": "{" }
// ...
{ "lineBreakAhead": true, "span": { start: 329, end: 335, line: 18, column: 3 }, "type": "Return", "text": "return" }
{ "lineBreakAhead": false, "span": { start: 336, end: 337, line: 18, column: 10 }, "type": "LBracket", "text": "[" }
{ "lineBreakAhead": false, "span": { start: 337, end: 340, line: 18, column: 11 }, "type": "DotDotDot", "text": "..." }
{ "lineBreakAhead": false, "span": { start: 340, end: 349, line: 18, column: 14 }, "type": "IdentOther", "text": "quickSort" }
{ "lineBreakAhead": false, "span": { start: 349, end: 350, line: 18, column: 23 }, "type": "LParen", "text": "(" }
{ "lineBreakAhead": false, "span": { start: 350, end: 357, line: 18, column: 24 }, "type": "IdentOther", "text": "leftArr" }
{ "lineBreakAhead": false, "span": { start: 357, end: 358, line: 18, column: 31 }, "type": "RParen", "text": ")" }
{ "lineBreakAhead": false, "span": { start: 358, end: 359, line: 18, column: 32 }, "type": "Comma", "text": "," }
{ "lineBreakAhead": false, "span": { start: 360, end: 365, line: 18, column: 34 }, "type": "IdentOther", "text": "pivot" }
{ "lineBreakAhead": false, "span": { start: 365, end: 366, line: 18, column: 39 }, "type": "Comma", "text": "," }
{ "lineBreakAhead": false, "span": { start: 367, end: 370, line: 18, column: 41 }, "type": "DotDotDot", "text": "..." }
{ "lineBreakAhead": false, "span": { start: 370, end: 379, line: 18, column: 44 }, "type": "IdentOther", "text": "quickSort" }
{ "lineBreakAhead": false, "span": { start: 379, end: 380, line: 18, column: 53 }, "type": "LParen", "text": "(" }
{ "lineBreakAhead": false, "span": { start: 380, end: 388, line: 18, column: 54 }, "type": "IdentOther", "text": "rightArr" }
{ "lineBreakAhead": false, "span": { start: 388, end: 389, line: 18, column: 62 }, "type": "RParen", "text": ")" }
{ "lineBreakAhead": false, "span": { start: 389, end: 390, line: 18, column: 63 }, "type": "RBracket", "text": "]" }
{ "lineBreakAhead": false, "span": { start: 390, end: 391, line: 18, column: 64 }, "type": "Semi", "text": ";" }
{ "lineBreakAhead": true, "span": { start: 392, end: 393, line: 19, column: 1 }, "type": "RBrace", "text": "}" }
```

## What Can I Do with Tokens?

* **Script Sanitization** - Sometimes we may want to prevent script from calling `eval()`, using `var`. We can simply scan the tokens for ident `eval`, keyword `var` and raise exceptions with detailed error messages.
* **Script Data Mining** - Sometimes we may want to generate reports on the ratio among `let`, `const`, `var`, enforce coding conventions. We can build our data mining algorithms by scanning the tokens.

## Conclusion

In this tutorial we've learned how to parse the code and process the tokens. The `parse()` provides a light-weighted insight over the scripts without paying the extra performance overhead on transpiling the scripts.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial03Tokens.java).
