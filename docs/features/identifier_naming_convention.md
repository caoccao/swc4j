# Feature - Identifier Naming Convention

Javet Sanitizer has built-in support for enforcing the naming convention of reserved identifiers so that the application can embed some reserved identifiers invisible or read-only to the guest scripts, this feature is disabled. The rules are as follows.

- A lambda expression can define the reserved identifier matcher.

```java
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
        .setReservedIdentifierMatcher(identifier -> identifier.startsWith("$"))
        .seal();
new JavetSanitizerModuleChecker(options).check("function main() { $a = 1; }"); // Invalid
```

- Some pre-defined reserved identifiers are allowed.

```java
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
        .setReservedIdentifierMatcher(identifier -> identifier.startsWith("$"));
options.getReservedIdentifierSet().add("$a");
options.seal();
new JavetSanitizerStatementListChecker(options).check("x = $a;"); // Valid
new JavetSanitizerStatementListChecker(options).check("$a = 1;"); // Invalid
new JavetSanitizerStatementListChecker(options).check("$b = 1;"); // Invalid
```

- Some pre-defined reserved identifiers are now allowed to be at left hand side so that these reserved identifiers remain immutable.

```java
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
        .setReservedIdentifierMatcher(identifier -> identifier.startsWith("$"));
options.getReservedIdentifierSet().add("$a");
options.getReservedMutableIdentifierSet().add("$a");
options.seal();
new JavetSanitizerStatementListChecker(options).check("x = $a;"); // Valid
new JavetSanitizerStatementListChecker(options).check("$a = 1;"); // Valid
new JavetSanitizerStatementListChecker(options).check("$b = 1;"); // Invalid
```
