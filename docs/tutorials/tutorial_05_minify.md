# Tutorial 05: Minify

In this tutorial, we are going to learn the following.

* Transform a TypeScript code snippet.
* Minify the code.
* Fetch the source map.

## Transform TypeScript

* Create an email validation class in TypeScript as follows. Then transform the code and get the output.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a TypeScript code snippet.
String code = "import { Validator } from './Validator';\n" +
        "\n" +
        "class EmailValidator implements Validator {\n" +
        "    isValid(s: string): boolean {\n" +
        "        // This is a regex for email validation.\n" +
        "        const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;\n" +
        "        return emailRegex.test(s);\n" +
        "    }\n" +
        "}\n" +
        "\n" +
        "export { EmailValidator };";
// Prepare a script name.
URL specifier = new URL("file://abc.ts");
// Prepare an option with script name and media type.
// Minify is turned on by default.
Swc4jTransformOptions options = new Swc4jTransformOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.TypeScript)
        .setParseMode(Swc4jParseMode.Module)
        .setSourceMap(Swc4jSourceMapOption.Separate);
// Parse the code.
Swc4jTransformOutput output = swc4j.transform(code, options);
// Print the minified code.
System.out.println("/*********************************************");
System.out.println("       The minified code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
System.out.println("/*********************************************");
System.out.println("       The source map is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getSourceMap());
```

* The minified code in the output is printed as follows. Please note that the comment is removed.

```js
/*********************************************
       The minified code is as follows.
*********************************************/
import{Validator}from"./Validator";class EmailValidator implements Validator{isValid(s:string):boolean{const emailRegex=/^[^\s@]+@[^\s@]+\.[^\s@]+$/;return emailRegex.test(s);}}export{EmailValidator};
```

* The source map in the output is printed as follows.

```js
/*********************************************
       The source map is as follows.
*********************************************/
{
    "version": 3,
    "sources": [
        "file://abc.ts/"
    ],
    "sourcesContent": [
        "import { Validator } from './Validator';\n\nclass EmailValidator implements Validator {\n    isValid(s: string): boolean {\n        // This is a regex for email validation.\n        const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;\n        return emailRegex.test(s);\n    }\n}\n\nexport { EmailValidator };"
    ],
    "names": [],
    "mappings": "AAAA,OAAS,SAAS,KAAQ,cAAc,AAExC,MAAM,0BAA0B,UAC5B,QAAQ,EAAG,MAAM,EAAG,OAAO,AAAC,CAExB,MAAM,WAAa,6BACnB,OAAO,WAAW,IAAI,CAAC,GAC3B,CACJ,CAEA,OAAS,cAAc,EAAG"
}
```

## Comments

Sometimes the comments are expected to be preserved in the minified code. Yes, that's supported.

* Turn the comments on as follows.

```java
// Turn on keep comments.
options.setKeepComments(true);
// Parse the code again.
output = swc4j.transform(code, options);
// Print the minified code.
System.out.println("/*********************************************");
System.out.println("       The minified code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
```

* The minified code in the output is printed as follows. Please note that the comment is preserved.

```js
/*********************************************
       The minified code is as follows.
*********************************************/
import{Validator}from"./Validator";class EmailValidator implements Validator{isValid(s:string):boolean{// This is a regex for email validation.
const emailRegex=/^[^\s@]+@[^\s@]+\.[^\s@]+$/;return emailRegex.test(s);}}export{EmailValidator};
```

## Conclusion

In this tutorial we've learned how to transform the code. The `transform()` provides minify, beautify and some other features.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial05Minify.java).
