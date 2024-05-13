# Tutorial Sanitizer 03: Keyword Restriction

There are a set of [keywords](../features/keyword_restriction.md) that cannot be used to prevent unexpected script execution behavior. E.g. async, await, Promise.

## Sample Keyword - import

By default, keyword `import` is disallowed so that the script cannot reference any modules. If you check a script with keyword `import` in `JavetSanitizerModuleChecker` with the default option, you will get an error. You may create your own option with keyword `import` enabled and the same script will pass the check.

```java
String codeString = "import { x } from 'x.mjs'; function main() {}";
// Check the script with the default option.
try {
    new JavetSanitizerModuleChecker().check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("/******************************************************/");
    System.out.println(codeString + " // Invalid");
    System.out.println("/******************************************************/");
    System.out.println(e.getDetailedMessage());
}

// Create a new option with keyword import enabled.
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
        .setKeywordImportEnabled(true)
        .seal();
// Check the script with the new option.
new JavetSanitizerModuleChecker(options).check(codeString);
System.out.println("/******************************************************/");
System.out.println(codeString + " // Valid");
System.out.println("/******************************************************/");
```

The output is as follows:

```js
/******************************************************/
import { x } from 'x.mjs'; function main() {} // Invalid
/******************************************************/
Keyword import is not allowed.
Source: import { x } from 'x.mjs';
Line: 1
Column: 1
Start: 0
End: 26
/******************************************************/
import { x } from 'x.mjs'; function main() {} // Valid
/******************************************************/
```

The complete code is at [here](../../src/test/java/com/caoccao/javet/sanitizer/tutorials/TutorialSanitizer03KeywordRestriction.java).
