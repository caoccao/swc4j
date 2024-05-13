# Tutorial Sanitizer 06: Identifier Freeze

There are a set of [identifiers](../features/identifier_freeze.md) that can be frozen in V8.

## Sample Identifier - JSON

By default, `JSON` is frozen so that there is not way of tampering `JSON.stringify`, `JSON.parse` in V8. If you check a script with `JSON.stringify` to be replaced with the default option, you will get an error. You may create your own option with a set of new identifiers and the same script will pass the check.

```java
String codeString = JavetSanitizerFridge.generate(JavetSanitizerOptions.Default);
try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
    // Initialize V8 with the default option.
    v8Runtime.getExecutor(codeString).executeVoid();
    codeString = "JSON.stringify = (str) => {}";
    v8Runtime.getExecutor(codeString).setResourceName("test.js").executeVoid();
} catch (JavetExecutionException e) {
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Invalid");
    System.out.println("/******************************************************/");
    System.out.println(e.getScriptingError());
}

// Create a new option with JSON allowed.
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
options.getToBeFrozenIdentifierList().remove("JSON");
options.seal();
codeString = JavetSanitizerFridge.generate(options);
try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
    // Initialize V8 with the new option.
    v8Runtime.getExecutor(codeString).executeVoid();
    codeString = "JSON.stringify = (str) => {}";
    v8Runtime.getExecutor(codeString).setResourceName("test.js").executeVoid();
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Valid");
    System.out.println("/******************************************************/");
}
```

The output is as follows:

```js
/******************************************************/
JSON.stringify = (str) => {} // Invalid
/******************************************************/
TypeError: Cannot assign to read only property 'stringify' of object '#<Object>'
Resource: test.js
Source Code: JSON.stringify = (str) => {}
Line Number: 1
Column: 15, 16
Position: 15, 16
/******************************************************/
JSON.stringify = (str) => {} // Valid
/******************************************************/
```

The complete code is at [here](../../src/test/java/com/caoccao/javet/sanitizer/tutorials/TutorialSanitizer06IdentifierFreeze.java).
