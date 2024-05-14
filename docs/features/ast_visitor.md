# AST Visitor

Swc4j AST (Abstract Syntax Tree) visitor is a design pattern used to process the nodes of an AST in a structured way.

## How to Implement a Visitor

There are typically two ways to implement a visitor.

1. Extend the built-in abstract class `Swc4jAstVisitor`. This is a light-weight, but less flexible way.
2. Implement the built-in interface `ISwc4jAstVisitor` from scratch. This is a heavy, but more flexible way.

## Visit AST in Parse Output

This [tutorial](../tutorials/tutorial_04_ast.md) demonstrates how to visit the AST in a Parse output.

## Update AST On-the-fly

This [tutorial](../tutorials/tutorial_06_plugin.md) demonstrates how to update the AST on-the-fly via a plugin.

## AST Nodes

The following AST nodes are supported.

| A-I                      | J-S                | T                        | U-Z           |
|--------------------------|--------------------|--------------------------|---------------|
| ArrayLit                 | JsxAttr            | TaggedTpl                | UnaryExpr     |
| ArrayPat                 | JsxClosingElement  | ThisExpr                 | UpdateExpr    |
| ArrowExpr                | JsxClosingFragment | ThrowStmt                | UsingDecl     |
| AssignExpr               | JsxElement         | Tpl                      | VarDecl       |
| AssignPat                | JsxEmptyExpr       | TplElement               | VarDeclarator |
| AssignPatProp            | JsxExprContainer   | TryStmt                  | WhileStmt     |
| AssignProp               | JsxFragment        | TsArrayType              | WithStmt      |
| AutoAccessor             | JsxMemberExpr      | TsAsExpr                 | YieldExpr     |
| AwaitExpr                | JsxNamespacedName  | TsCallSignatureDecl      |               |
| BigInt                   | JsxOpeningElement  | TsConditionalType        |               |
| BindingIdent             | JsxOpeningFragment | TsConstAssertion         |               |
| BinExpr                  | JsxSpreadChild     | TsConstructorType        |               |
| BlockStmt                | JsxText            | TsConstructSignatureDecl |               |
| Bool                     | KeyValuePatProp    | TsEnumDecl               |               |
| BreakStmt                | KeyValueProp       | TsEnumMember             |               |
| CallExpr                 | LabeledStmt        | TsExportAssignment       |               |
| CatchClause              | MemberExpr         | TsExprWithTypeArgs       |               |
| Class                    | MetaPropExpr       | TsExternalModuleRef      |               |
| ClassDecl                | MethodProp         | TsFnType                 |               |
| ClassExpr                | Module             | TsGetterSignature        |               |
| ClassMethod              | NamedExport        | TsImportEqualsDecl       |               |
| ClassProp                | NewExpr            | TsImportType             |               |
| ComputedPropName         | Null               | TsIndexedAccessType      |               |
| CondExpr                 | Number             | TsIndexSignature         |               |
| Constructor              | ObjectLit          | TsInferType              |               |
| ContinueStmt             | ObjectPat          | TsInstantiation          |               |
| DebuggerStmt             | OptCall            | TsInterfaceBody          |               |
| Decorator                | OptChainExpr       | TsInterfaceDecl          |               |
| DoWhileStmt              | Param              | TsIntersectionType       |               |
| EmptyStmt                | ParenExpr          | TsKeywordType            |               |
| ExportAll                | PrivateMethod      | TsLitType                |               |
| ExportDecl               | PrivateName        | TsMappedType             |               |
| ExportDefaultDecl        | PrivateProp        | TsMethodSignature        |               |
| ExportDefaultExpr        | Regex              | TsModuleBlock            |               |
| ExportDefaultSpecifier   | RestPat            | TsModuleDecl             |               |
| ExportNamedSpecifier     | ReturnStmt         | TsNamespaceDecl          |               |
| ExportNamespaceSpecifier | Script             | TsNamespaceExportDecl    |               |
| ExprOrSpread             | SeqExpr            | TsNonNullExpr            |               |
| ExprStmt                 | SetterProp         | TsOptionalType           |               |
| FnDecl                   | SpreadElement      | TsParamProp              |               |
| FnExpr                   | StaticBlock        | TsParenthesizedType      |               |
| ForInStmt                | Str                | TsPropertySignature      |               |
| ForOfStmt                | Super              | TsQualifiedName          |               |
| ForStmt                  | SuperPropExpr      | TsRestType               |               |
| Function                 | SwitchCase         | TsSatisfiesExpr          |               |
| GetterProp               | SwitchStmt         | TsSetterSignature        |               |
| Ident                    |                    | TsThisType               |               |
| IfStmt                   |                    | TsTplLitType             |               |
| Import                   |                    | TsTupleElement           |               |
| ImportDecl               |                    | TsTupleType              |               |
| ImportDefaultSpecifier   |                    | TsTypeAliasDecl          |               |
| ImportNamedSpecifier     |                    | TsTypeAnn                |               |
| ImportStarAsSpecifier    |                    | TsTypeAssertion          |               |
| Invalid                  |                    | TsTypeLit                |               |
|                          |                    | TsTypeOperator           |               |
|                          |                    | TsTypeParam              |               |
|                          |                    | TsTypeParamDecl          |               |
|                          |                    | TsTypeParamInstantiation |               |
|                          |                    | TsTypePredicate          |               |
|                          |                    | TsTypeQuery              |               |
|                          |                    | TsTypeRef                |               |
|                          |                    | TsUnionType              |               |
