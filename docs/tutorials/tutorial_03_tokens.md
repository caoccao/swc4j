# Tutorial 03: Tokens

In this tutorial, we are going to learn the follows.

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
String specifier = "file:///abc.ts";
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
{ "lineBreakAhead": true, "start": 0, "end": 8, "type": "Function", "text": "function" }
{ "lineBreakAhead": false, "start": 9, "end": 18, "type": "IdentOther", "text": "quickSort" }
{ "lineBreakAhead": false, "start": 18, "end": 19, "type": "LParen", "text": "(" }
{ "lineBreakAhead": false, "start": 19, "end": 22, "type": "IdentOther", "text": "arr" }
{ "lineBreakAhead": false, "start": 22, "end": 23, "type": "Colon", "text": ":" }
{ "lineBreakAhead": false, "start": 24, "end": 30, "type": "IdentKnown", "text": "number" }
{ "lineBreakAhead": false, "start": 30, "end": 31, "type": "LBracket", "text": "[" }
{ "lineBreakAhead": false, "start": 31, "end": 32, "type": "RBracket", "text": "]" }
{ "lineBreakAhead": false, "start": 32, "end": 33, "type": "RParen", "text": ")" }
{ "lineBreakAhead": false, "start": 33, "end": 34, "type": "Colon", "text": ":" }
{ "lineBreakAhead": false, "start": 35, "end": 41, "type": "IdentKnown", "text": "number" }
{ "lineBreakAhead": false, "start": 41, "end": 42, "type": "LBracket", "text": "[" }
{ "lineBreakAhead": false, "start": 42, "end": 43, "type": "RBracket", "text": "]" }
{ "lineBreakAhead": false, "start": 44, "end": 45, "type": "LBrace", "text": "{" }
// ...
{ "lineBreakAhead": true, "start": 329, "end": 335, "type": "Return", "text": "return" }
{ "lineBreakAhead": false, "start": 336, "end": 337, "type": "LBracket", "text": "[" }
{ "lineBreakAhead": false, "start": 337, "end": 340, "type": "DotDotDot", "text": "..." }
{ "lineBreakAhead": false, "start": 340, "end": 349, "type": "IdentOther", "text": "quickSort" }
{ "lineBreakAhead": false, "start": 349, "end": 350, "type": "LParen", "text": "(" }
{ "lineBreakAhead": false, "start": 350, "end": 357, "type": "IdentOther", "text": "leftArr" }
{ "lineBreakAhead": false, "start": 357, "end": 358, "type": "RParen", "text": ")" }
{ "lineBreakAhead": false, "start": 358, "end": 359, "type": "Comma", "text": "," }
{ "lineBreakAhead": false, "start": 360, "end": 365, "type": "IdentOther", "text": "pivot" }
{ "lineBreakAhead": false, "start": 365, "end": 366, "type": "Comma", "text": "," }
{ "lineBreakAhead": false, "start": 367, "end": 370, "type": "DotDotDot", "text": "..." }
{ "lineBreakAhead": false, "start": 370, "end": 379, "type": "IdentOther", "text": "quickSort" }
{ "lineBreakAhead": false, "start": 379, "end": 380, "type": "LParen", "text": "(" }
{ "lineBreakAhead": false, "start": 380, "end": 388, "type": "IdentOther", "text": "rightArr" }
{ "lineBreakAhead": false, "start": 388, "end": 389, "type": "RParen", "text": ")" }
{ "lineBreakAhead": false, "start": 389, "end": 390, "type": "RBracket", "text": "]" }
{ "lineBreakAhead": false, "start": 390, "end": 391, "type": "Semi", "text": ";" }
{ "lineBreakAhead": true, "start": 392, "end": 393, "type": "RBrace", "text": "}" }
```

## What Can I Do with Tokens?

* **Script Sanitization** - Sometimes you may want to prevent script from calling `eval()`, using `var`. You can simply scan the tokens for ident `eval`, keyword `var` and raise exceptions with detailed error messages.
* **Script Data Mining** - Sometimes you may want to generate reports on the ratio among `let`, `const`, `var`, enforce coding conventions. You can build your data mining algorithms by scanning the tokens.

## Conclusion

In this tutorial we've learned how to parse the code and process the tokens. The `parse()` provides a light-weighted insight over the scripts without paying the extra performance overhead on transpiling the scripts.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial03Tokens.java).
