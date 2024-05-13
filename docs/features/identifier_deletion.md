# Feature - Identifier Deletion

Identifiers can be deleted from V8.

```js
eval(''); // eval() is deleted.
Function(''); // Function() is deleted.
WebAssemble(''); // WebAssemble() is deleted.
```

The following example shows how to update the to be deleted identifier list.

```java
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
options.getToBeDeletedIdentifierList().remove("WebAssemble");
options.getToBeDeletedIdentifierList().add("Promise");
options.seal();
```

The default to be deleted identifier list is as follows:

```js
eval
Function
WebAssembly
```

Please refer to the [tutorial](../tutorials/tutorial_sanitizer_05_identifier_deletion.md) for more details.
