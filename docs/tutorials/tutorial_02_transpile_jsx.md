# Tutorial 02: Transpile Jsx

In this tutorial, we are going to learn the follows.

* Transpile a React Jsx code snippet into a JavaScript one.
* Customize the Jsx factory for React.

## Transpile from Jsx to JavaScript

* Create a simple Java application with the code as follows. Please make sure the parse mode is set to module mode which is the default mode. There are 2 modes in swc4j: script mode and module mode.

```java
// Create an instance of swc4j.
Swc4j swc4j = new Swc4j();
// Prepare a simple React Hello World Jsx code snippet.
String code = "import React from 'react';\n" +
        "import './App.css';\n" +
        "function App() {\n" +
        "    return (\n" +
        "        <h1> Hello World! </h1>\n" +
        "    );\n" +
        "}\n" +
        "export default App;";
// Prepare a script name.
String specifier = "file:///abc.ts";
// Prepare an option with script name and media type.
Swc4jTranspileOptions options = new Swc4jTranspileOptions()
        .setSpecifier(specifier)
        .setMediaType(Swc4jMediaType.Jsx)
        // React jsx must be parsed in module mode.
        .setParseMode(Swc4jParseMode.Module);
// Transpile the code.
Swc4jTranspileOutput output = swc4j.transpile(code, options);
// Print the transpiled code.
System.out.println("/*********************************************");
System.out.println("      The transpiled code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
```

* The output is as follows. As you can see, the Jsx code is transpiled into JavaScript code with the source map inlined.

```js
/*********************************************
      The transpiled code is as follows.
*********************************************/
import React from 'react';
import './App.css';
function App() {
  return /*#__PURE__*/ React.createElement("h1", null, " Hello World! ");
}
export default App;
//# sourceMappingURL=data:application/json;base64,...
```

## Customize the Jsx Factory for React

You may want to replace the default Jsx factory with a customized one. Yes, that's supported.

* Append the following code to that Java application.

```java
// Remove the inline source map.
options.setInlineSourceMap(false).setSourceMap(true);
// Customize the Jsx factory.
options.setJsxFactory("CustomJsxFactory.createElement");
output = swc4j.transpile(code, options);
// Print the transpiled code.
System.out.println("/*********************************************");
System.out.println("      The transpiled code is as follows.");
System.out.println("*********************************************/");
System.out.println(output.getCode());
```

* The output is as follows. As you can see, the Jsx factory is updated.

```js
/*********************************************
      The transpiled code is as follows.
*********************************************/
import React from 'react';
import './App.css';
function App() {
  return /*#__PURE__*/ CustomJsxFactory.createElement("h1", null, " Hello World! ");
}
export default App;
```

## Conclusion

In this tutorial we've learned how to transpile from Jsx to JavaScript, and how to customized the Jsx factory.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/swc4j/tutorials/Tutorial02TranspileJsx.java).
