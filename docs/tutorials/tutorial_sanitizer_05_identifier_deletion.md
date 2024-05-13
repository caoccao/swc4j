# Tutorial Sanitizer 05: Identifier Deletion

There are a set of [identifiers](../features/identifier_deletion.md) that can be deleted in V8.

## Sample Identifier - WebAssembly

By default, `WebAssembly` is deleted so that there is not way of accessing `WebAssembly` in V8. If you check a script with `WebAssembly` with the default option, you will get an error. You may create your own option with a set of new identifiers and the same script will pass the check.

```java
String codeString = JavetSanitizerFridge.generate(JavetSanitizerOptions.Default);
try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
    // Initialize V8 with the default option.
    v8Runtime.getExecutor(codeString).executeVoid();
    codeString = "const a = WebAssembly;";
    v8Runtime.getExecutor(codeString).setResourceName("test.js").executeVoid();
} catch (JavetExecutionException e) {
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Invalid");
    System.out.println("/******************************************************/");
    System.out.println(e.getScriptingError());
}

// Create a new option with WebAssembly allowed.
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
options.getToBeDeletedIdentifierList().remove("WebAssembly");
options.seal();
codeString = JavetSanitizerFridge.generate(options);
try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
    // Initialize V8 with the new option.
    v8Runtime.getExecutor(codeString).executeVoid();
    codeString = "const a = WebAssembly;";
    v8Runtime.getExecutor(codeString).setResourceName("test.js").executeVoid();
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Valid");
    System.out.println("/******************************************************/");
}
```

The output is as follows:

```js
/******************************************************/
const a = WebAssembly; // Invalid
/******************************************************/
ReferenceError: WebAssembly is not defined
Resource: test.js
Source Code: const a = WebAssembly;
Line Number: 1
Column: 10, 11
Position: 10, 11
/******************************************************/
const a = WebAssembly; // Valid
/******************************************************/
```

The complete code is at [here](../../src/test/java/com/caoccao/javet/sanitizer/tutorials/TutorialSanitizer05IdentifierDeletion.java).
