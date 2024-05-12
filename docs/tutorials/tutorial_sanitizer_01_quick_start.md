# Tutorial Sanitizer 01: Quick Start

In this tutorial, we are going to learn the follows.

* What is Javet Sanitizer?
* Have a quick check.

## Preparation

* Follow the [instructions](../../) to add swc4j to our project.
* Follow the [instructions](https://github.com/caoccao/Javet) to add Javet to our project.

## What is Javet Sanitizer?

Javet Sanitizer is a sanitizer framework for parsing and validating JavaScript code on JVM. It is built on top of swc4j.

Javet Sanitizer provides a set of rich checkers at AST level for [Javet](https://github.com/caoccao/Javet) so that applications can address and eliminate the potential threats before the JavaScript code is executed.

## Have a Quick Check

* Create a simple Java application with the code as follows.

```java
JavetSanitizerStatementListChecker checker = new JavetSanitizerStatementListChecker();

// 1. Check if keyword const can be used.
String codeString = "const a = 1;";
checker.check(codeString);
System.out.println("1. " + codeString + " // Valid.");

// 2. Check if keyword var can be used.
codeString = "var a = 1;";
try {
    checker.check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("2. " + codeString + " // Invalid: " + e.getMessage());
}

// 3. Check if Object is mutable.
codeString = "Object = {};";
try {
    checker.check(codeString);
} catch (JavetSanitizerException e) {
    System.out.println("3. " + codeString + " // Invalid: " + e.getMessage());
}
```

* The output is as follows. As we can see, the `const` is allowed to be used, but `var` is not allowed to be used. And `Object` is immutable, so it is not allowed to be at the left side of the assignment.

```js
1. const a = 1; // Valid.
2. var a = 1; // Invalid: Keyword var is not allowed.
3. Object = {}; // Invalid: Identifier Object is not allowed.
```

## Conclusion

In this tutorial we've learned how to what Javet Sanitizer is and how to have a quick start.

The source code of this tutorial is at [here](../../src/test/java/com/caoccao/javet/sanitizer/tutorials/TutorialSanitizer01QuickStart.java).
