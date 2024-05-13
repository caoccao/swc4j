# Feature - Function Restriction

In a JavaScript module, it is recommended to export function only so that the module is tamper proof. The function restriction ensure the following.

* There are no module wide statements other than function declarations.
* There are some functions that must be declared.
* To a particular function, the argument count can be also validated.

`JavetSanitizerModuleFunctionChecker` is the one that enforces this check.

## Valid Sample

```js
import { x } from 'x.mjs';

function main() {
  // Do something.
}
```

## Invalid Samples

```js
const a = 1; // Variable declaration is invalid.
class A {} // Class declaration is invalid.
JSON.stringify(undefined); // Regular statement is invalid.
```

## List

The default must-have function list is as follows:

```js
main
```

Please refer to the [tutorial](../tutorials/tutorial_sanitizer_04_function_restriction.md) for more details.
