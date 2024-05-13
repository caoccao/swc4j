# Parse

`parse()` parses the code, but does not transform or transpile the code. The following features are supported:

* Analyze the AST
* Capture the comments, tokens and AST
* Analyze the scope
* Update the AST via plugins on-the-fly

It is a light-weight API for code analysis.

## Options

### Media Type (Shared)

Media type of the source text contains the following values:

| Media Type  | Extension    |
|-------------|--------------|
| JavaScript  | .js          |
| Jsx         | .jsx         |
| Mjs         | .mjs         |
| Cjs         | .cjs         |
| TypeScript  | .ts          |
| Mts         | .mts         |
| Cts         | .cts         |
| Dts         | .d.ts        |
| Dmts        | .d.mts       |
| Dcts        | .d.cts       |
| Tsx         | .tsx         |
| Json        | .json        |
| Wasm        | .wasm        |
| TsBuildInfo | .tsbuildinfo |
| SourceMap   | .map         |
| Unknown     | N/A          |

### Parse Mode (Shared)

Parse mode tells SWC to parse the code as Module or Script. Default is `Program` that lets SWC to determine the actual parse mode. The values are as follows:

| Parse Mode |
|------------|
| Program    |
| Module     |
| Script     |

### Plugin Host (Shared)

plugin host is an interface that takes the AST program as the input. After the plugin host is set, it will be called to make changes to the AST on-the-fly. Default is `null`.

### Specifier (Shared)

Specifier of the source text is an `URL`. It will appear in the error massage.

### Capture AST

It determines whether to capture the AST or not. Once it's set, the AST will be available in the `Swc4jParseOutput.getProgram()`.

### Capture Comments

It determines whether to capture the comments or not. Once it's set, the comments will be available in the `Swc4jParseOutput.getComments()`.

### Capture Tokens

It determines whether to capture the tokens or not. Once it's set, the tokens will be available in the `Swc4jParseOutput.getTokens()`.

### Scope Analysis

It determines whether to analyze the scope or not. It hasn't been completely implemented yet.
