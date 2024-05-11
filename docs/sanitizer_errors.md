# Sanitizer Errors

| Error                | Code | Message                                                                                |
|----------------------|-----:|----------------------------------------------------------------------------------------|
| UnknownError         |    1 | Unknown error: ${message}                                                              |
| EmptyCodeString      |    2 | The code string is empty.                                                              |
| VisitorNotFound      |    3 | Visitor ${name} is not found.                                                          |
| ParsingError         |    4 | ${message}                                                                             |
| IdentifierNotAllowed |  100 | Identifier ${identifier} is not allowed.                                               |
| KeywordNotAllowed    |  101 | Keyword ${keyword} is not allowed.                                                     |
| InvalidNode          |  200 | ${actualNode} is unexpected. Expecting ${expectedNode} in ${nodeName}.                 |
| NodeCountMismatch    |  220 | AST node count ${actualCount} mismatches the expected AST node count ${expectedCount}. |
| NodeCountTooSmall    |  221 | AST node count ${actualCount} is less than the minimal AST node count ${minCount}.     |
| NodeCountTooLarge    |  222 | AST node count ${actualCount} is greater than the maximal AST node count ${maxCount}.  |
| FunctionNotFound     |  300 | Function ${name} is not found.                                                         |
