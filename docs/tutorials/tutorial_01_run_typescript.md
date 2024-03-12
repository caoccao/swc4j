# Tutorial 01: Run TypeScript

In this tutorial, we are going to learn the follows.

* Transpile a TypeScript code snippet into a JavaScript one.
* Execute the transpiled JavaScript code in Javet.
* Generate a separate source map.

## Preparation

* Follow the [instructions](../../) to add swc4j to your project.
* Follow the [instructions](https://github.com/caoccao/Javet) to add Javet to your project.

## Transpile from TypeScript to JavaScript

* Create a simple Java application with the code as follows.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a simple TypeScript code snippet.
String code = "function add(a:number, b:number) { return a+b; }";
// Prepare a script name.
String specifier = "file:///abc.ts";
// Prepare an option with script name and media type.
Swc4jTranspileOptions options = new Swc4jTranspileOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.TypeScript);
// Transpile the code.
Swc4jTranspileOutput output = swc4j.transpile(code, options);
// Print the transpiled code.
System.out.println("/*********************************************");
System.out.println("      The transpiled code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
```

* The output is as follows. As you can see, the TypeScript code is transpiled into JavaScript code with the source map inlined.

```js
/*********************************************
      The transpiled code is as follows.
*********************************************/
function add(a, b) {
  return a + b;
}
//# sourceMappingURL=data:application/json;base64,...
```

## Execute the Code in Javet

* Append the following code to that Java application.

```java
// Run the code in Javet.
System.out.println("/*********************************************");
System.out.println("   The transpiled code is executed in Javet.");
System.out.println("*********************************************/");
try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
    v8Runtime.getExecutor(output.getCode()).executeVoid();
    System.out.println("1 + 2 = " +
            v8Runtime.getGlobalObject().invokeInteger(
                    "add", 1, 2));
}
```

* The output is as follows. As you can see, the JavaScript code is executed in Javet successfully.

```js
/*********************************************
   The transpiled code is executed in Javet.
*********************************************/
1 + 2 = 3
```

## Generate a Separate Source Map

You may want to generate a separate source map as the inline source map sometimes only slows down the script execution a little bit and a separate source map file can be useful in other cases. Yes, it's so easy to do so in swc4j.

* Append the following code to that Java application.

```java
// Remove the inline source map.
options.setInlineSourceMap(false).setSourceMap(true);
output = swc4j.transpile(code, options);
// Print the transpiled code.
System.out.println("/*********************************************");
System.out.println("      The transpiled code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
System.out.println("/*********************************************");
System.out.println("   The transpiled source map is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getSourceMap());
```

* The output is as follows. As you can see, the source map is not inlined. Note: The actual source map is a minified Json string. The output below shows a beautified Json string for better readability.

```js
/*********************************************
      The transpiled code is as follows.
*********************************************/
function add(a, b) {
  return a + b;
}

/*********************************************
   The transpiled source map is as follows.
*********************************************/
{
    "version": 3,
    "sources": [
        "file:///abc.ts"
    ],
    "sourcesContent": [
        "function add(a:number, b:number) { return a+b; }"
    ],
    "names": [],
    "mappings": "AAAA,SAAS,IAAI,CAAQ,EAAE,CAAQ;EAAI,OAAO,IAAE;AAAG"
}
```

## Conclusion

In this tutorial we've learned how to transpile from TypeScript to JavaScript, execute the code in Javet, and get a separated source map.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial01RunTypeScript.java).
