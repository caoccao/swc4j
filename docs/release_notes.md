# Release Notes

## 1.2.0

* Upgraded deno_ast to v0.43.3
* Removed `TsBuildInfo` from `Swc4jMediaType`
* Added `Css` to `Swc4jMediaType`
* Added `verbatimModuleSyntax` to `Swc4jTranspileOptions`
* Added `Swc4jModuleKind` to `Swc4jTranspileOptions`

## 1.1.0

* Upgraded deno_ast to v0.42.2
* Upgraded rust toolchain to v1.81.0

## 1.0.0

* Upgraded deno_ast to v0.42.0
* Revised internal error handling by relaying exception in `parse()`, `transform()`, `transpile()`
* Enhanced logging in rust lib
* Supported Android

## 0.11.0

* Upgraded deno_ast to v0.41.1
* Revised internal error handling
* Added `IdentName`

## 0.10.0

* Upgraded deno_ast to v0.40.0
* Upgraded rust toolchain to v1.79.0
* Replaced `debug_print` with `log`
* Fixed extra line separators in JsxText

## 0.9.0

* Upgraded deno_ast to v0.39.0
* Added `create()` to AST types

## 0.8.0

* Added `replaceNode()` to `ISwc4jAst`
* Added coercion to primitive AST types
* Added experimental JsFuck decoder
* Added plugin ES2015 transform spread

## 0.7.0

* Upgraded deno_ast to v0.38.1
* Added Sanitizer
  * Added anonymous function checker
  * Added module checker
  * Added module function checker
  * Added single statement checker
  * Added statement list checker
* Fix span for BindingIdent

## 0.6.0

* Supported plugins

## 0.5.0

* Supported `transform()`
* Supported Windows arm64 and Linux arm64

## 0.4.0

* Upgraded deno_ast to v0.37.0
* Updated specifier from String to URL
* Supported comments.

## 0.3.0

* Upgraded deno_ast to v0.36.1
* Fixed null pointer exception in AstNewExpr

## 0.2.0

* Fully supported `parse()` with AST, tokens available
* Fully supported `transpile()` with AST, tokens available

## 0.1.0

* Partially supported `transpile()` with JS, TS, JSX, TSX, etc.
