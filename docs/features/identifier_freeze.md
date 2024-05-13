# Feature - Identifier Freeze

Identifiers can be frozen in V8.

```js
JSON.parse = (str) => {}; // JSON is frozen.
Object.keys = (obj) => {}; // Object is frozen.
```

The following example shows how to update the to be frozen identifier list.

```java
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
options.getToBeFrozenIdentifierList().remove("JSON");
options.getToBeFrozenIdentifierList().add("XMLHttpRequest");
options.seal();
```

The default to be frozen object list is as follows:

| Object               |
|----------------------|
| AggregateError       |
| Array                |
| ArrayBuffer          |
| Atomics              |
| BigInt               |
| BigInt64Array        |
| BigUint64Array       |
| Boolean              |
| DataView             |
| Date                 |
| decodeURI            |
| decodeURIComponent   |
| encodeURI            |
| encodeURIComponent   |
| Error                |
| escape               |
| EvalError            |
| FinalizationRegistry |
| Float32Array         |
| Float64Array         |
| Int16Array           |
| Int32Array           |
| Int8Array            |
| isFinite             |
| isNaN                |
| JSON                 |
| Map                  |
| Math                 |
| Number               |
| Object               |
| parseFloat           |
| parseInt             |
| Promise              |
| Proxy                |
| RangeError           |
| ReferenceError       |
| Reflect              |
| RegExp               |
| Set                  |
| SharedArrayBuffer    |
| String               |
| Symbol               |
| SyntaxError          |
| TypeError            |
| Uint16Array          |
| Uint32Array          |
| Uint8Array           |
| Uint8ClampedArray    |
| unescape             |
| URIError             |
| WeakMap              |
| WeakRef              |
| WeakSet              |

Please refer to the [tutorial](../tutorials/tutorial_sanitizer_06_identifier_freeze.md) for more details.
