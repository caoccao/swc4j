/*
* Copyright (c) 2024. caoccao.com Sam Cao
* All rights reserved.

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

use deno_ast::swc::{ast::*, common::util::take::Take};

// This test case is to detect if ast structs are updated. 

#[test]
fn test_structs() {
  let _ = ArrayLit {
    span: Default::default(),
    elems: Default::default(),
  };
  let _ = ArrayPat {
    span: Default::default(),
    elems: Default::default(),
    optional: Default::default(),
    type_ann: Default::default(),
  };
  let _ = ArrowExpr {
    span: Default::default(),
    is_async: Default::default(),
    body: Box::new(BlockStmtOrExpr::dummy()),
    is_generator: Default::default(),
    params: Default::default(),
    return_type: Default::default(),
    type_params: Default::default(),
  };
  let _ = AssignExpr {
    span: Default::default(),
    left: PatOrExpr::dummy(),
    op: AssignOp::AddAssign,
    right: Box::new(Expr::dummy()),
  };
  let _ = AssignPat {
    span: Default::default(),
    left: Box::new(Pat::dummy()),
    right: Box::new(Expr::dummy()),
  };
  let _ = AssignPatProp {
    span: Default::default(),
    key: Ident::dummy(),
    value: Default::default(),
  };
  let _ = AssignProp {
    key: Ident::dummy(),
    value: Box::new(Expr::dummy()),
  };
  let _ = AutoAccessor {
    span: Default::default(),
    is_static: Default::default(),
    accessibility: Default::default(),
    decorators: Default::default(),
    key: Key::dummy(),
    type_ann: Default::default(),
    value: Default::default(),
  };
  let _ = AwaitExpr {
    span: Default::default(),
    arg: Box::new(Expr::dummy()),
  };
  let _ = BigInt {
    span: Default::default(),
    raw: Default::default(),
    value: Default::default(),
  };
  let _ = BinExpr {
    span: Default::default(),
    left: Box::new(Expr::dummy()),
    op: BinaryOp::Add,
    right: Box::new(Expr::dummy()),
  };
  let _ = BindingIdent {
    id: Ident::dummy(),
    type_ann: Default::default(),
  };
  let _ = BlockStmt {
    span: Default::default(),
    stmts: Default::default(),
  };
  let _ = Bool {
    span: Default::default(),
    value: Default::default(),
  };
  let _ = CallExpr {
    span: Default::default(),
    args: Default::default(),
    callee: Callee::dummy(),
    type_args: Default::default(),
  };
  let _ = Class {
    span: Default::default(),
    is_abstract: Default::default(),
    implements: Default::default(),
    body: Default::default(),
    decorators: Default::default(),
    super_class: Default::default(),
    super_type_params: Default::default(),
    type_params: Default::default(),
  };
  let _ = ClassDecl {
    class: Box::new(Class::dummy()),
    declare: Default::default(),
    ident: Ident::dummy(),
  };
  let _ = ClassExpr {
    class: Box::new(Class::dummy()),
    ident: Default::default(),
  };
  let _ = ClassMethod {
    span: Default::default(),
    is_abstract: Default::default(),
    is_override: Default::default(),
    is_static: Default::default(),
    accessibility: Default::default(),
    function: Box::new(Function::dummy()),
    key: PropName::dummy(),
    kind: MethodKind::Getter,
    is_optional: Default::default(),
  };
  let _ = ClassProp {
    span: Default::default(),
    is_abstract: Default::default(),
    is_override: Default::default(),
    is_static: Default::default(),
    accessibility: Default::default(),
    declare: Default::default(),
    decorators: Default::default(),
    definite: Default::default(),
    key: PropName::dummy(),
    is_optional: Default::default(),
    readonly: Default::default(),
    type_ann: Default::default(),
    value: Default::default(),
  };
  let _ = ComputedPropName {
    span: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = CondExpr {
    span: Default::default(),
    alt: Box::new(Expr::dummy()),
    cons: Box::new(Expr::dummy()),
    test: Box::new(Expr::dummy()),
  };
  let _ = Constructor {
    span: Default::default(),
    accessibility: Default::default(),
    body: Default::default(),
    key: PropName::dummy(),
    is_optional: Default::default(),
    params: Default::default(),
  };
  let _ = DebuggerStmt {
    span: Default::default(),
  };
  let _ = Decorator {
    span: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = EmptyStmt {
    span: Default::default(),
  };
  let _ = ExportAll {
    span: Default::default(),
    src: Box::new(Str::dummy()),
    type_only: Default::default(),
    with: Default::default(),
  };
  let _ = ExportDecl {
    span: Default::default(),
    decl: Decl::dummy(),
  };
  let _ = ExportDefaultDecl {
    span: Default::default(),
    decl: DefaultDecl::Class(ClassExpr::dummy()),
  };
  let _ = ExportDefaultExpr {
    span: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = ExprOrSpread {
    expr: Box::new(Expr::dummy()),
    spread: Default::default(),
  };
  let _ = ExprStmt {
    span: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = FnDecl {
    declare: Default::default(),
    function: Box::new(Function::dummy()),
    ident: Ident::dummy(),
  };
  let _ = FnExpr {
    function: Box::new(Function::dummy()),
    ident: Default::default(),
  };
  let _ = Function {
    span: Default::default(),
    is_async: Default::default(),
    body: Default::default(),
    decorators: Default::default(),
    is_generator: Default::default(),
    params: Default::default(),
    return_type: Default::default(),
    type_params: Default::default(),
  };
  let _ = GetterProp {
    span: Default::default(),
    body: Default::default(),
    key: PropName::dummy(),
    type_ann: Default::default(),
  };
  let _ = Ident {
    span: Default::default(),
    optional: Default::default(),
    sym: Default::default(),
  };
  let _ = ImportDecl {
    span: Default::default(),
    specifiers: Default::default(),
    src: Box::new(Str::dummy()),
    type_only: Default::default(),
    with: Default::default(),
  };
  let _ = ImportDefaultSpecifier {
    span: Default::default(),
    local: Ident::dummy(),
  };
  let _ = ImportNamedSpecifier {
    span: Default::default(),
    imported: Default::default(),
    local: Ident::dummy(),
    is_type_only: Default::default(),
  };
  let _ = ImportStarAsSpecifier {
    span: Default::default(),
    local: Ident::dummy(),
  };
  let _ = Invalid {
    span: Default::default(),
  };
  let _ = JSXText {
    span: Default::default(),
    raw: Default::default(),
    value: Default::default(),
  };
  let _ = KeyValuePatProp {
    key: PropName::dummy(),
    value: Box::new(Pat::dummy()),
  };
  let _ = KeyValueProp {
    key: PropName::dummy(),
    value: Box::new(Expr::dummy()),
  };
  let _ = MemberExpr {
    span: Default::default(),
    obj: Box::new(Expr::dummy()),
    prop: MemberProp::dummy(),
  };
  let _ = MetaPropExpr {
    span: Default::default(),
    kind: MetaPropKind::NewTarget,
  };
  let _ = MethodProp {
    function: Box::new(Function::dummy()),
    key: PropName::dummy(),
  };
  let _ = NamedExport {
    span: Default::default(),
    specifiers: Default::default(),
    src: Default::default(),
    type_only: Default::default(),
    with: Default::default(),
  };
  let _ = NewExpr {
    span: Default::default(),
    args: Default::default(),
    callee: Box::new(Expr::dummy()),
    type_args: Default::default(),
  };
  let _ = Null {
    span: Default::default(),
  };
  let _ = Number {
    span: Default::default(),
    raw: Default::default(),
    value: Default::default(),
  };
  let _ = ObjectLit {
    span: Default::default(),
    props: Default::default(),
  };
  let _ = ObjectPat {
    span: Default::default(),
    optional: Default::default(),
    props: Default::default(),
    type_ann: Default::default(),
  };
  let _ = Param {
    span: Default::default(),
    decorators: Default::default(),
    pat: Pat::dummy(),
  };
  let _ = ParenExpr {
    span: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = PrivateMethod {
    span: Default::default(),
    is_abstract: Default::default(),
    is_override: Default::default(),
    is_static: Default::default(),
    accessibility: Default::default(),
    function: Box::new(Function::dummy()),
    key: PrivateName { span: Default::default(), id: Ident::dummy() },
    kind: MethodKind::Getter,
    is_optional: Default::default(),
  };
  let _ = PrivateName {
    span: Default::default(),
    id: Ident::dummy(),
  };
  let _ = PrivateProp {
    span: Default::default(),
    is_override: Default::default(),
    is_static: Default::default(),
    accessibility: Default::default(),
    decorators: Default::default(),
    definite: Default::default(),
    key: PrivateName { span: Default::default(), id: Ident::dummy() },
    is_optional: Default::default(),
    readonly: Default::default(),
    type_ann: Default::default(),
    value: Default::default(),
  };
  let _ = Regex {
    span: Default::default(),
    exp: Default::default(),
    flags: Default::default(),
  };
  let _ = RestPat {
    span: Default::default(),
    arg: Box::new(Pat::dummy()),
    dot3_token: Default::default(),
    type_ann: Default::default(),
  };
  let _ = SetterProp {
    span: Default::default(),
    body: Default::default(),
    key: PropName::dummy(),
    param: Box::new(Pat::dummy()),
  };
  let _ = SpreadElement {
    dot3_token: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = StaticBlock {
    span: Default::default(),
    body: BlockStmt::dummy(),
  };
  let _ = Str {
    span: Default::default(),
    raw: Default::default(),
    value: Default::default(),
  };
  let _ = Super {
    span: Default::default(),
  };
  let _ = SuperPropExpr {
    span: Default::default(),
    obj: Super::dummy(),
    prop: SuperProp::dummy(),
  };
  let _ = TaggedTpl {
    span: Default::default(),
    tag: Box::new(Expr::dummy()),
    tpl: Box::new(Tpl::dummy()),
    type_params: Default::default(),
  };
  let _ = ThisExpr {
    span: Default::default(),
  };
  let _ = Tpl {
    span: Default::default(),
    exprs: Default::default(),
    quasis: Default::default(),
  };
  let _ = TplElement {
    span: Default::default(),
    cooked: Default::default(),
    raw: Default::default(),
    tail: Default::default(),
  };
  let _ = TsEnumDecl {
    span: Default::default(),
    is_const: Default::default(),
    declare: Default::default(),
    id: Ident::dummy(),
    members: Default::default(),
  };
  let _ = TsEnumMember {
    span: Default::default(),
    id: TsEnumMemberId::Ident(Ident::dummy()),
    init: Default::default(),
  };
  let _ = TsExportAssignment {
    span: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = TsExprWithTypeArgs {
    span: Default::default(),
    expr: Box::new(Expr::dummy()),
    type_args: Default::default(),
  };
  let _ = TsExternalModuleRef {
    span: Default::default(),
    expr: Str::dummy(),
  };
  let _ = TsImportEqualsDecl {
    span: Default::default(),
    is_export: Default::default(),
    id: Ident::dummy(),
    module_ref: TsModuleRef::TsEntityName(TsEntityName::Ident(Ident::dummy())),
    is_type_only: Default::default(),
  };
  let _ = TsIndexSignature {
    span: Default::default(),
    is_static: Default::default(),
    params: Default::default(),
    readonly: Default::default(),
    type_ann: Default::default(),
  };
  let _ = TsInterfaceBody {
    span: Default::default(),
    body: Default::default(),
  };
  let _ = TsInterfaceDecl {
    span: Default::default(),
    extends: Default::default(),
    body: TsInterfaceBody { span: Default::default(), body: Default::default() },
    declare: Default::default(),
    id: Ident::dummy(),
    type_params: Default::default(),
  };
  let _ = TsModuleDecl {
    span: Default::default(),
    body: Default::default(),
    declare: Default::default(),
    global: Default::default(),
    id: TsModuleName::Ident(Ident::dummy()),
  };
  let _ = TsNamespaceExportDecl {
    span: Default::default(),
    id: Ident::dummy(),
  };
  let _ = TsQualifiedName {
    left: TsEntityName::Ident(Ident::dummy()),
    right: Ident::dummy(),
  };
  let _ = TsTypeAliasDecl {
    span: Default::default(),
    declare: Default::default(),
    id: Ident::dummy(),
    type_ann: Box::new(TsType::TsThisType(TsThisType { span: Default::default() })),
    type_params: Default::default(),
  };
  let _ = TsTypeAnn {
    span: Default::default(),
    type_ann: Box::new(TsType::TsThisType(TsThisType { span: Default::default() })),
  };
  let _ = TsTypeParam {
    span: Default::default(),
    is_const: Default::default(),
    default: Default::default(),
    constraint: Default::default(),
    is_in: Default::default(),
    name: Ident::dummy(),
    is_out: Default::default(),
  };
  let _ = TsTypeParamDecl {
    span: Default::default(),
    params: Default::default(),
  };
  let _ = TsTypeParamInstantiation {
    span: Default::default(),
    params: Default::default(),
  };
  let _ = UnaryExpr {
    span: Default::default(),
    arg: Box::new(Expr::dummy()),
    op: UnaryOp::Void,
  };
  let _ = UpdateExpr {
    span: Default::default(),
    arg: Box::new(Expr::dummy()),
    op: UpdateOp::PlusPlus,
    prefix: Default::default(),
  };
  let _ = UsingDecl {
    span: Default::default(),
    is_await: Default::default(),
    decls: Default::default(),
  };
  let _ = VarDecl {
    span: Default::default(),
    declare: Default::default(),
    decls: Default::default(),
    kind: VarDeclKind::Const,
  };
  let _ = VarDeclarator {
    span: Default::default(),
    definite: Default::default(),
    init: Default::default(),
    name: Pat::dummy(),
  };
  let _ = YieldExpr {
    span: Default::default(),
    arg: Default::default(),
    delegate: Default::default(),
  };
}
