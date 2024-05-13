# Feature - Identifier Restriction

JavaScript allows the identifiers to be named as built-in objects. Sometimes that creates some confusions. JavetSanitizer can disallow a set of identifiers to avoid such confusions. The following example shows how to customize the disallowed identifiers.

```java
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
options.getDisallowedIdentifierSet().add("prototype");
options.getDisallowedIdentifierSet().remove("Promise");
options.seal();
```

The default disallowed identifier list is as follows:

| Identifier             |
|------------------------|
| __proto__              |
| apply                  |
| AsyncFunction          |
| AsyncGenerator         |
| AsyncGeneratorFunction |
| bind                   |
| call                   |
| clearInterval          |
| clearTimeout           |
| defineProperties       |
| defineProperty         |
| eval                   |
| Function               |
| Generator              |
| GeneratorFunction      |
| getPrototypeOf         |
| global                 |
| globalThis             |
| Intl                   |
| Promise                |
| prototype              |
| Proxy                  |
| Reflect                |
| require                |
| setImmediate           |
| setInterval            |
| setPrototypeOf         |
| setTimeout             |
| Symbol                 |
| uneval                 |
| WebAssembly            |
| window                 |
| XMLHttpRequest         |

## Why are global and globalThis disallowed?

It's so easy to access or change `global` or `globalThis` in a script to impact the next script to be executed. In order to fully reuse the V8 runtime, such behavior is addressed as an error in the sanity check.

## Why are apply, bind, call and eval disallowed?

`apply`, `bind`, `call` and `eval` are commonly used in the JS obfuscators. The obfuscated code can bypass the sanity check, is hard to read and maintain.

Please refer to the [tutorial](../tutorials/tutorial_sanitizer_07_identifier_restriction.md) for more details.
