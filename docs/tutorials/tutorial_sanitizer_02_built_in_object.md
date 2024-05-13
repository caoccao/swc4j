# Tutorial Sanitizer 02: Built-in Objects

There are a set of [built-in objects](../features/built_in_object_protection.md) that cannot be tampered, in other words, remain immutable to prevent malicious script execution.

## Sample Built-in Object - Date

By default, built-in object `Date` is immutable so that the script cannot hijack `Date`. If you check a script with built-in object `Date` in `JavetSanitizerModuleChecker` with the default option, you will get an error. You may create your own option with built-in object `Date` allowed and the same script will pass the check.

```java
String codeString = "function main() { Date.parse = () => {}; }";
// Check the script with the default options.
try {
    new JavetSanitizerModuleChecker().check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Invalid");
    System.out.println("/******************************************************/");
    System.out.println(e.getDetailedMessage());
}

// Create a new options with built-in object Data allowed.
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
options.getBuiltInObjectSet().remove("Date");
options.seal();
// Check the script with the new options.
new JavetSanitizerModuleChecker(options).check(codeString);
System.out.println("/******************************************************/");
System.out.println(codeString + " // Valid");
System.out.println("/******************************************************/");
```

The output is as follows:

```js
/******************************************************/
function main() { Date.parse = () => {}; } // Invalid
/******************************************************/
Identifier Date is not allowed.
Source: Date
Line: 1
Column: 19
Start: 18
End: 22
/******************************************************/
function main() { Date.parse = () => {}; } // Valid
/******************************************************/
```

The complete code is at [here](../../src/test/java/com/caoccao/javet/sanitizer/tutorials/TutorialSanitizer02BuiltInObjects.java).
