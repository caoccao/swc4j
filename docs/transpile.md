# Transpile

`transpile()` transpiles the code from various languages into JavaScript.

## Features

* Custom JSX factory
* Custom JSX fragment factory
* Emit metadata
* Generate the source map
* Inline source code in the source map
* Keep the comments
* Precompile JSX
* Update the AST via plugins on-the-fly

## Options

The following options are shared with [Parse](parse.md).

* Media Type
* Parse Mode
* Plugin Host
* Specifier

The following options are shared with [Transform](transform.md).

* Inline Sources
* Keep Comments
* Source Map

### Emit Metadata

It determines when emit a legacy decorator, whether also emit experimental decorator meta data. Defaults to `false`.

### Imports Not Used As Values

What to do with import statements that only import types i.e. whether to remove them (`Remove`), keep them as side-effect imports (`Preserve`) or error (`Error`). Defaults to `Remove`. The values are as follows:

| Value    |
|----------|
| Remove   |
| Preserve |
| Error    |

### JSX Automatic

It determines whether the program should use an implicit JSX import source/the `new` JSX transforms. Defaults to `false`.

### JSX Development

If JSX is automatic, if it is in development mode, meaning that it should import `jsx-dev-runtime` and transform JSX using `jsxDEV` import from the JSX import source as well as provide additional debug information to the JSX factory.

### JSX Factory

When transforming JSX, what value should be used for the JSX factory. Defaults to `React.createElement`.

### JSX Fragment Factory

When transforming JSX, what value should be used for the JSX fragment factory. Defaults to `React.Fragment`.

### JSX Import Source

The string module specifier to implicitly import JSX factories from when transpiling JSX.

### Precompile JSX

Should JSX be precompiled into static strings that need to be concatenated with dynamic content. Defaults to `false`, mutually exclusive with `transform_jsx`.

### Precompile JSX Dynamic Props

List of properties/attributes that should always be treated as dynamic.

### Precompile JSX Skip Elements

List of elements that should not be precompiled when the JSX precompile transform is used.

### Transform JSX

Should JSX be transformed. Defaults to `true`.

### Use Decorators Proposal

TC39 Decorators Proposal - https://github.com/tc39/proposal-decorators

### Use TS Decorators

TypeScript experimental decorators.

### Var Decl Imports

Should import declarations be transformed to variable declarations using a dynamic import. This is useful for import and export declaration support in script contexts such as the Deno REPL. Defaults to `false`.
