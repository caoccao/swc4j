# Tutorial Sanitizer 04: Function Restriction

There are a set of [functions](../features/function_restriction.md) that must be declared to form a valid module to be executed.

## Sample Function - main()

By default, `function main()` must be declared so that the script has a default entry function. If you check a script without `function main()` in `JavetSanitizerModuleFunctionChecker` with the default option, you will get an error. You may create your own option with a set of new functions and the same script will pass the check.

```java
String codeString = "function myMain() {}";
// Check the script with the default option.
try {
    new JavetSanitizerModuleFunctionChecker().check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Invalid");
    System.out.println("/******************************************************/");
    System.out.println(e.getMessage());
}

// Create a new option with keyword import enabled.
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
options.getReservedFunctionIdentifierSet().remove("main");
options.getReservedFunctionIdentifierSet().add("myMain");
options.seal();
// Check the script with the new option.
new JavetSanitizerModuleFunctionChecker(options).check(codeString);
System.out.println("/******************************************************/");
System.out.println(codeString + " // Valid");
System.out.println("/******************************************************/");
```

The output is as follows:

```js
/******************************************************/
function myMain() {} // Invalid
/******************************************************/
Function main is not found.
/******************************************************/
function myMain() {} // Valid
/******************************************************/
```

The complete code is at [here](../../src/test/java/com/caoccao/javet/sanitizer/tutorials/TutorialSanitizer04FunctionRestriction.java).
