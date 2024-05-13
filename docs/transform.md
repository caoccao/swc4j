# Transform

`transform()` transforms the code, but does not transform the code.

## Features

* Minify the code
* Generate the source map
* Inline source code in the source map
* Keep the comments
* Use only ascii characters
* Specify the target ES version
* Update the AST via plugins on-the-fly

## Options

The following options are shared with `parse().

* Media Type
* Parse Mode
* Plugin Host
* Specifier

### ASCII Only

Forces the code generator to use only ascii characters. This is useful for environments that do not support unicode.

### Emit Assert for Import Attributes

It determines whether to emit assert for import attributes. Defaults to `false`.

### Inline Sources

It determines whether the sources to be inlined in the source map. Defaults to `true`.

### Keep Comments

It determines whether to keep comments in the output. Defaults to `false`.

### Minify

It determines whether to minify the code. Defaults to `true`.

### Omit Last Semi

It determines whether the code generator to emit the latest semicolon. Defaults to `false`.

### Source Map

It determines how and if source maps should be generated. Defaults to `Inline`. The values are as follows:

| Source Map Option | Description                                             |
|-------------------|---------------------------------------------------------|
| Inline            | Source map should be inlined into the source. (Default) |
| Separate          | Source map should be generated as a separate file.      |
| None              | Source map should not be generated at all.              |

### Target

The target runtime environment. This defaults to `EsVersion.Latest` because it preserves input as much as possible.

Note: This does not verify if output is valid for the target runtime. e.g. `const foo = 1;` with `EsVersion.Es3` will emit as `const foo = 1` without verification. This is because it's not a concern of the code generator.

The values are as follows:

| ES Version | Name   |
|------------|--------|
| ES3        | es3    |
| ES5        | es5    |
| ES2015     | es2015 |
| ES2016     | es2016 |
| ES2017     | es2017 |
| ES2018     | es2018 |
| ES2019     | es2019 |
| ES2020     | es2020 |
| ES2021     | es2021 |
| ES2022     | es2022 |
| ESNext     | esnext |
