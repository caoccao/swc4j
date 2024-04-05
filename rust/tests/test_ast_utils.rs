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

use deno_ast::swc::{
  ast::*,
  common::{util::take::Take, DUMMY_SP},
};

/*
 * This test case is for detecting if ast structs are updated.
 * The rustc strictly checks that all properties of the struct must all be constructed,
 * otherwise it raises an error.
 * This behavior can be used to detect changes in the structs
 * when the dependencies get updated.
 */

#[test]
fn test_structs() {
  let _ = ArrayLit {
    span: DUMMY_SP,
    elems: Default::default(),
  };
  let _ = ArrayPat {
    span: DUMMY_SP,
    elems: Default::default(),
    optional: Default::default(),
    type_ann: Default::default(),
  };
  let _ = ArrowExpr {
    span: DUMMY_SP,
    is_async: Default::default(),
    body: Box::new(BlockStmtOrExpr::dummy()),
    is_generator: Default::default(),
    params: Default::default(),
    return_type: Default::default(),
    type_params: Default::default(),
  };
  let _ = AssignExpr {
    span: DUMMY_SP,
    left: PatOrExpr::dummy(),
    op: AssignOp::AddAssign,
    right: Box::new(Expr::dummy()),
  };
  let _ = AssignPat {
    span: DUMMY_SP,
    left: Box::new(Pat::dummy()),
    right: Box::new(Expr::dummy()),
  };
  let _ = AssignPatProp {
    span: DUMMY_SP,
    key: Ident::dummy(),
    value: Default::default(),
  };
  let _ = AssignProp {
    key: Ident::dummy(),
    value: Box::new(Expr::dummy()),
  };
  let _ = AutoAccessor {
    span: DUMMY_SP,
    is_static: Default::default(),
    accessibility: Default::default(),
    decorators: Default::default(),
    key: Key::dummy(),
    type_ann: Default::default(),
    value: Default::default(),
  };
  let _ = AwaitExpr {
    span: DUMMY_SP,
    arg: Box::new(Expr::dummy()),
  };
  let _ = BigInt {
    span: DUMMY_SP,
    raw: Default::default(),
    value: Default::default(),
  };
  let _ = BinExpr {
    span: DUMMY_SP,
    left: Box::new(Expr::dummy()),
    op: BinaryOp::Add,
    right: Box::new(Expr::dummy()),
  };
  let _ = BindingIdent {
    id: Ident::dummy(),
    type_ann: Default::default(),
  };
  let _ = BlockStmt {
    span: DUMMY_SP,
    stmts: Default::default(),
  };
  let _ = Bool {
    span: DUMMY_SP,
    value: Default::default(),
  };
  let _ = CallExpr {
    span: DUMMY_SP,
    args: Default::default(),
    callee: Callee::dummy(),
    type_args: Default::default(),
  };
  let _ = Class {
    span: DUMMY_SP,
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
    span: DUMMY_SP,
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
    span: DUMMY_SP,
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
    span: DUMMY_SP,
    expr: Box::new(Expr::dummy()),
  };
  let _ = CondExpr {
    span: DUMMY_SP,
    alt: Box::new(Expr::dummy()),
    cons: Box::new(Expr::dummy()),
    test: Box::new(Expr::dummy()),
  };
  let _ = Constructor {
    span: DUMMY_SP,
    accessibility: Default::default(),
    body: Default::default(),
    key: PropName::dummy(),
    is_optional: Default::default(),
    params: Default::default(),
  };
  let _ = DebuggerStmt {
    span: DUMMY_SP,
  };
  let _ = Decorator {
    span: DUMMY_SP,
    expr: Box::new(Expr::dummy()),
  };
  let _ = EmptyStmt {
    span: DUMMY_SP,
  };
  let _ = ExportAll {
    span: DUMMY_SP,
    src: Box::new(Str::dummy()),
    type_only: Default::default(),
    with: Default::default(),
  };
  let _ = ExportDecl {
    span: DUMMY_SP,
    decl: Decl::dummy(),
  };
  let _ = ExportDefaultDecl {
    span: DUMMY_SP,
    decl: DefaultDecl::Class(ClassExpr::dummy()),
  };
  let _ = ExportDefaultExpr {
    span: DUMMY_SP,
    expr: Box::new(Expr::dummy()),
  };
  let _ = ExprOrSpread {
    expr: Box::new(Expr::dummy()),
    spread: Default::default(),
  };
  let _ = ExprStmt {
    span: DUMMY_SP,
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
    span: DUMMY_SP,
    is_async: Default::default(),
    body: Default::default(),
    decorators: Default::default(),
    is_generator: Default::default(),
    params: Default::default(),
    return_type: Default::default(),
    type_params: Default::default(),
  };
  let _ = GetterProp {
    span: DUMMY_SP,
    body: Default::default(),
    key: PropName::dummy(),
    type_ann: Default::default(),
  };
  let _ = Ident {
    span: DUMMY_SP,
    optional: Default::default(),
    sym: Default::default(),
  };
  let _ = ImportDecl {
    span: DUMMY_SP,
    specifiers: Default::default(),
    src: Box::new(Str::dummy()),
    type_only: Default::default(),
    with: Default::default(),
  };
  let _ = ImportDefaultSpecifier {
    span: DUMMY_SP,
    local: Ident::dummy(),
  };
  let _ = ImportNamedSpecifier {
    span: DUMMY_SP,
    imported: Default::default(),
    local: Ident::dummy(),
    is_type_only: Default::default(),
  };
  let _ = ImportStarAsSpecifier {
    span: DUMMY_SP,
    local: Ident::dummy(),
  };
  let _ = Invalid {
    span: DUMMY_SP,
  };
  let _ = JSXText {
    span: DUMMY_SP,
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
    span: DUMMY_SP,
    obj: Box::new(Expr::dummy()),
    prop: MemberProp::dummy(),
  };
  let _ = MetaPropExpr {
    span: DUMMY_SP,
    kind: MetaPropKind::NewTarget,
  };
  let _ = MethodProp {
    function: Box::new(Function::dummy()),
    key: PropName::dummy(),
  };
  let _ = NamedExport {
    span: DUMMY_SP,
    specifiers: Default::default(),
    src: Default::default(),
    type_only: Default::default(),
    with: Default::default(),
  };
  let _ = NewExpr {
    span: DUMMY_SP,
    args: Default::default(),
    callee: Box::new(Expr::dummy()),
    type_args: Default::default(),
  };
  let _ = Null {
    span: DUMMY_SP,
  };
  let _ = Number {
    span: DUMMY_SP,
    raw: Default::default(),
    value: Default::default(),
  };
  let _ = ObjectLit {
    span: DUMMY_SP,
    props: Default::default(),
  };
  let _ = ObjectPat {
    span: DUMMY_SP,
    optional: Default::default(),
    props: Default::default(),
    type_ann: Default::default(),
  };
  let _ = OptCall {
    span: DUMMY_SP,
    args: Default::default(),
    callee: Box::new(Expr::dummy()),
    type_args: Default::default(),
  };
  let _ = OptChainExpr {
    span: DUMMY_SP,
    base: Box::new(OptChainBase::Call(OptCall::dummy())),
    optional: Default::default(),
  };
  let _ = Param {
    span: DUMMY_SP,
    decorators: Default::default(),
    pat: Pat::dummy(),
  };
  let _ = ParenExpr {
    span: DUMMY_SP,
    expr: Box::new(Expr::dummy()),
  };
  let _ = PrivateMethod {
    span: DUMMY_SP,
    is_abstract: Default::default(),
    is_override: Default::default(),
    is_static: Default::default(),
    accessibility: Default::default(),
    function: Box::new(Function::dummy()),
    key: PrivateName { span: DUMMY_SP, id: Ident::dummy() },
    kind: MethodKind::Getter,
    is_optional: Default::default(),
  };
  let _ = PrivateName {
    span: DUMMY_SP,
    id: Ident::dummy(),
  };
  let _ = PrivateProp {
    span: DUMMY_SP,
    is_override: Default::default(),
    is_static: Default::default(),
    accessibility: Default::default(),
    decorators: Default::default(),
    definite: Default::default(),
    key: PrivateName { span: DUMMY_SP, id: Ident::dummy() },
    is_optional: Default::default(),
    readonly: Default::default(),
    type_ann: Default::default(),
    value: Default::default(),
  };
  let _ = Regex {
    span: DUMMY_SP,
    exp: Default::default(),
    flags: Default::default(),
  };
  let _ = RestPat {
    span: DUMMY_SP,
    arg: Box::new(Pat::dummy()),
    dot3_token: Default::default(),
    type_ann: Default::default(),
  };
  let _ = SetterProp {
    span: DUMMY_SP,
    body: Default::default(),
    key: PropName::dummy(),
    param: Box::new(Pat::dummy()),
  };
  let _ = SpreadElement {
    dot3_token: Default::default(),
    expr: Box::new(Expr::dummy()),
  };
  let _ = StaticBlock {
    span: DUMMY_SP,
    body: BlockStmt::dummy(),
  };
  let _ = Str {
    span: DUMMY_SP,
    raw: Default::default(),
    value: Default::default(),
  };
  let _ = Super {
    span: DUMMY_SP,
  };
  let _ = SuperPropExpr {
    span: DUMMY_SP,
    obj: Super::dummy(),
    prop: SuperProp::dummy(),
  };
  let _ = TaggedTpl {
    span: DUMMY_SP,
    tag: Box::new(Expr::dummy()),
    tpl: Box::new(Tpl::dummy()),
    type_params: Default::default(),
  };
  let _ = ThisExpr {
    span: DUMMY_SP,
  };
  let _ = Tpl {
    span: DUMMY_SP,
    exprs: Default::default(),
    quasis: Default::default(),
  };
  let _ = TplElement {
    span: DUMMY_SP,
    cooked: Default::default(),
    raw: Default::default(),
    tail: Default::default(),
  };
  let _ = TsEnumDecl {
    span: DUMMY_SP,
    is_const: Default::default(),
    declare: Default::default(),
    id: Ident::dummy(),
    members: Default::default(),
  };
  let _ = TsEnumMember {
    span: DUMMY_SP,
    id: TsEnumMemberId::Ident(Ident::dummy()),
    init: Default::default(),
  };
  let _ = TsExportAssignment {
    span: DUMMY_SP,
    expr: Box::new(Expr::dummy()),
  };
  let _ = TsExprWithTypeArgs {
    span: DUMMY_SP,
    expr: Box::new(Expr::dummy()),
    type_args: Default::default(),
  };
  let _ = TsExternalModuleRef {
    span: DUMMY_SP,
    expr: Str::dummy(),
  };
  let _ = TsImportEqualsDecl {
    span: DUMMY_SP,
    is_export: Default::default(),
    id: Ident::dummy(),
    module_ref: TsModuleRef::TsEntityName(TsEntityName::Ident(Ident::dummy())),
    is_type_only: Default::default(),
  };
  let _ = TsIndexSignature {
    span: DUMMY_SP,
    is_static: Default::default(),
    params: Default::default(),
    readonly: Default::default(),
    type_ann: Default::default(),
  };
  let _ = TsInterfaceBody {
    span: DUMMY_SP,
    body: Default::default(),
  };
  let _ = TsInterfaceDecl {
    span: DUMMY_SP,
    extends: Default::default(),
    body: TsInterfaceBody { span: DUMMY_SP, body: Default::default() },
    declare: Default::default(),
    id: Ident::dummy(),
    type_params: Default::default(),
  };
  let _ = TsModuleDecl {
    span: DUMMY_SP,
    body: Default::default(),
    declare: Default::default(),
    global: Default::default(),
    id: TsModuleName::Ident(Ident::dummy()),
  };
  let _ = TsNamespaceExportDecl {
    span: DUMMY_SP,
    id: Ident::dummy(),
  };
  let _ = TsQualifiedName {
    left: TsEntityName::Ident(Ident::dummy()),
    right: Ident::dummy(),
  };
  let _ = TsTypeAliasDecl {
    span: DUMMY_SP,
    declare: Default::default(),
    id: Ident::dummy(),
    type_ann: Box::new(TsType::TsThisType(TsThisType { span: DUMMY_SP })),
    type_params: Default::default(),
  };
  let _ = TsTypeAnn {
    span: DUMMY_SP,
    type_ann: Box::new(TsType::TsThisType(TsThisType { span: DUMMY_SP })),
  };
  let _ = TsTypeParam {
    span: DUMMY_SP,
    is_const: Default::default(),
    default: Default::default(),
    constraint: Default::default(),
    is_in: Default::default(),
    name: Ident::dummy(),
    is_out: Default::default(),
  };
  let _ = TsTypeParamDecl {
    span: DUMMY_SP,
    params: Default::default(),
  };
  let _ = TsTypeParamInstantiation {
    span: DUMMY_SP,
    params: Default::default(),
  };
  let _ = UnaryExpr {
    span: DUMMY_SP,
    arg: Box::new(Expr::dummy()),
    op: UnaryOp::Void,
  };
  let _ = UpdateExpr {
    span: DUMMY_SP,
    arg: Box::new(Expr::dummy()),
    op: UpdateOp::PlusPlus,
    prefix: Default::default(),
  };
  let _ = UsingDecl {
    span: DUMMY_SP,
    is_await: Default::default(),
    decls: Default::default(),
  };
  let _ = VarDecl {
    span: DUMMY_SP,
    declare: Default::default(),
    decls: Default::default(),
    kind: VarDeclKind::Const,
  };
  let _ = VarDeclarator {
    span: DUMMY_SP,
    definite: Default::default(),
    init: Default::default(),
    name: Pat::dummy(),
  };
  let _ = YieldExpr {
    span: DUMMY_SP,
    arg: Default::default(),
    delegate: Default::default(),
  };
}
