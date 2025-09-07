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

### Decorators

Kind of decorators to use. Defaults to `None`. The values are as follows:

* None
* Ecma
* LegacyTypeScript
  * Emit Metadata - It determines when emit a legacy decorator, whether also emit experimental decorator meta data. Defaults to `false`.

### Imports Not Used As Values

What to do with import statements that only import types i.e. whether to remove them (`Remove`), keep them as side-effect imports (`Preserve`) or error (`Error`). Defaults to `Remove`. The values are as follows:

* Remove
* Preserve
* Error

### JSX

Options for transforming JSX. Defaults to `null`. The values are as follows:

* Automatic
  * Development - If JSX is automatic, if it is in development mode, meaning that it should import `jsx-dev-runtime` and transform JSX using `jsxDEV` import from the JSX import source as well as provide additional debug information to the JSX factory.
  * Import Source - The string module specifier to implicitly import JSX factories from when transpiling JSX.
* Classic
  * Factory - When transforming JSX, what value should be used for the JSX factory. Defaults to `React.createElement`.
  * Fragment Factory - When transforming JSX, what value should be used for the JSX fragment factory. Defaults to `React.Fragment`.
* Precompile
  * Automatic
  * Dynamic Props - List of properties/attributes that should always be treated as dynamic.
  * Skip Elements - List of elements that should not be precompiled when the JSX precompile transform is used.

### Module Kind

The kind of module being transpiled. Defaults to being derived from the media type of the parsed source.

### Var Decl Imports

Should import declarations be transformed to variable declarations using a dynamic import. This is useful for import and export declaration support in script contexts such as the Deno REPL. Defaults to `false`.

### Verbatim Module Syntax

`true` changes type stripping behavior so that _only_ `type` imports are stripped.
