# Feature - Keyword Restriction

The JavaScript keywords can be restricted. The following example shows how to turn off the keyword restriction.

```java
JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
        .setKeywordAsyncEnabled(true)
        .setKeywordAwaitEnabled(true)
        .setKeywordDebuggerEnabled(true)
        .setKeywordExportEnabled(true)
        .setKeywordImportEnabled(true)
        .setKeywordVarEnabled(true)
        .setKeywordWithEnabled(true)
        .setKeywordYieldEnabled(true)
        .seal();
```

The default restricted keyword list is as follows:

| Keyword  |
|----------|
| async    |
| await    |
| debugger |
| export   |
| import   |
| var      |
| with     |
| yield    |

Please refer to the [tutorial](../tutorials/tutorial_sanitizer_03_keyword_restriction.md) for more details.
