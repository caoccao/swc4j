# Tutorial Sanitizer 07: Identifier Restriction

There are a set of [identifiers](../features/identifier_restriction.md) that cannot be referenced to prevent malicious script execution.

## Sample Identifier - eval

By default, identifier `eval` is disallowed so that the script cannot reference `eval`. If you check a script with identifier `eval` in `JavetSanitizerModuleChecker` with the default option, you will get an error. You may create your own option with identifier `eval` allowed and the same script will pass the check.

```java
String codeString = "function main() { eval('1'); }";
// Check the script with the default option.
try {
    new JavetSanitizerModuleChecker().check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Invalid");
    System.out.println("/******************************************************/");
    System.out.println(e.getDetailedMessage());
}

// Create a new option with identifier eval allowed.
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
options.getDisallowedIdentifierSet().remove("eval");
options.seal();
// Check the script with the new option.
new JavetSanitizerModuleChecker(options).check(codeString);
System.out.println("/******************************************************/");
System.out.println(codeString + " // Valid");
System.out.println("/******************************************************/");
```

The output is as follows:

```js
/******************************************************/
function main() { eval('1'); } // Invalid
/******************************************************/
Identifier eval is not allowed.
Source: eval
Line: 1
Column: 19
Start: 18
End: 22
/******************************************************/
function main() { eval('1'); } // Valid
/******************************************************/
```

The complete code is at [here](../../src/test/java/com/caoccao/javet/sanitizer/tutorials/TutorialSanitizer07IdentifierRestriction.java).
