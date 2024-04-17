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

use swc4j::enums::IdentifiableEnum;

/*
 * This test case is for detecting if ast structs are updated.
 * The rustc strictly checks that all properties of the struct must all be constructed,
 * otherwise it raises an error.
 * This behavior can be used to detect changes in the structs
 * when the dependencies get updated.
 */

trait GetDefault<T> {
  fn get_default() -> T;
}

/* GetDefault Begin */
impl GetDefault<Accessibility> for Accessibility {
  fn get_default() -> Accessibility {
    Accessibility::parse_by_id(0)
  }
}

impl GetDefault<AssignOp> for AssignOp {
  fn get_default() -> AssignOp {
    AssignOp::parse_by_id(0)
  }
}

impl GetDefault<AssignTarget> for AssignTarget {
  fn get_default() -> AssignTarget {
    AssignTarget::dummy()
  }
}

impl GetDefault<AssignTargetPat> for AssignTargetPat {
  fn get_default() -> AssignTargetPat {
    AssignTargetPat::dummy()
  }
}

impl GetDefault<BinaryOp> for BinaryOp {
  fn get_default() -> BinaryOp {
    BinaryOp::parse_by_id(0)
  }
}

impl GetDefault<BlockStmtOrExpr> for BlockStmtOrExpr {
  fn get_default() -> BlockStmtOrExpr {
    BlockStmtOrExpr::dummy()
  }
}

impl GetDefault<Callee> for Callee {
  fn get_default() -> Callee {
    Callee::dummy()
  }
}

impl GetDefault<ClassMember> for ClassMember {
  fn get_default() -> ClassMember {
    ClassMember::dummy()
  }
}

impl GetDefault<Decl> for Decl {
  fn get_default() -> Decl {
    Decl::dummy()
  }
}

impl GetDefault<DefaultDecl> for DefaultDecl {
  fn get_default() -> DefaultDecl {
    DefaultDecl::Class(ClassExpr::get_default())
  }
}

impl GetDefault<ExportSpecifier> for ExportSpecifier {
  fn get_default() -> ExportSpecifier {
    ExportSpecifier::Default(ExportDefaultSpecifier::get_default())
  }
}

impl GetDefault<Expr> for Expr {
  fn get_default() -> Expr {
    Expr::dummy()
  }
}

impl GetDefault<ForHead> for ForHead {
  fn get_default() -> ForHead {
    ForHead::dummy()
  }
}

impl GetDefault<ImportPhase> for ImportPhase {
  fn get_default() -> ImportPhase {
    ImportPhase::parse_by_id(0)
  }
}

impl GetDefault<ImportSpecifier> for ImportSpecifier {
  fn get_default() -> ImportSpecifier {
    ImportSpecifier::Default(ImportDefaultSpecifier::get_default())
  }
}

impl GetDefault<JSXAttrName> for JSXAttrName {
  fn get_default() -> JSXAttrName {
    JSXAttrName::Ident(Ident::get_default())
  }
}

impl GetDefault<JSXAttrOrSpread> for JSXAttrOrSpread {
  fn get_default() -> JSXAttrOrSpread {
    JSXAttrOrSpread::JSXAttr(JSXAttr::get_default())
  }
}

impl GetDefault<JSXAttrValue> for JSXAttrValue {
  fn get_default() -> JSXAttrValue {
    JSXAttrValue::Lit(Lit::get_default())
  }
}

impl GetDefault<JSXElementChild> for JSXElementChild {
  fn get_default() -> JSXElementChild {
    JSXElementChild::JSXText(JSXText::get_default())
  }
}

impl GetDefault<JSXElementName> for JSXElementName {
  fn get_default() -> JSXElementName {
    JSXElementName::dummy()
  }
}

impl GetDefault<JSXExpr> for JSXExpr {
  fn get_default() -> JSXExpr {
    JSXExpr::Expr(Box::new(Expr::get_default()))
  }
}

impl GetDefault<JSXObject> for JSXObject {
  fn get_default() -> JSXObject {
    JSXObject::Ident(Ident::get_default())
  }
}

impl GetDefault<Key> for Key {
  fn get_default() -> Key {
    Key::dummy()
  }
}

impl GetDefault<Lit> for Lit {
  fn get_default() -> Lit {
    Lit::Null(Null::get_default())
  }
}

impl GetDefault<MemberProp> for MemberProp {
  fn get_default() -> MemberProp {
    MemberProp::dummy()
  }
}

impl GetDefault<MetaPropKind> for MetaPropKind {
  fn get_default() -> MetaPropKind {
    MetaPropKind::parse_by_id(0)
  }
}

impl GetDefault<MethodKind> for MethodKind {
  fn get_default() -> MethodKind {
    MethodKind::parse_by_id(0)
  }
}

impl GetDefault<ModuleDecl> for ModuleDecl {
  fn get_default() -> ModuleDecl {
    ModuleDecl::dummy()
  }
}

impl GetDefault<ModuleExportName> for ModuleExportName {
  fn get_default() -> ModuleExportName {
    ModuleExportName::Ident(Ident::get_default())
  }
}

impl GetDefault<ModuleItem> for ModuleItem {
  fn get_default() -> ModuleItem {
    ModuleItem::dummy()
  }
}

impl GetDefault<ObjectPatProp> for ObjectPatProp {
  fn get_default() -> ObjectPatProp {
    ObjectPatProp::Rest(RestPat::get_default())
  }
}

impl GetDefault<OptChainBase> for OptChainBase {
  fn get_default() -> OptChainBase {
    OptChainBase::Call(OptCall::get_default())
  }
}

impl GetDefault<ParamOrTsParamProp> for ParamOrTsParamProp {
  fn get_default() -> ParamOrTsParamProp {
    ParamOrTsParamProp::Param(Param::get_default())
  }
}

impl GetDefault<Pat> for Pat {
  fn get_default() -> Pat {
    Pat::dummy()
  }
}

impl GetDefault<Program> for Program {
  fn get_default() -> Program {
    Program::Script(Script::get_default())
  }
}

impl GetDefault<Prop> for Prop {
  fn get_default() -> Prop {
    Prop::Shorthand(Ident::get_default())
  }
}

impl GetDefault<PropName> for PropName {
  fn get_default() -> PropName {
    PropName::dummy()
  }
}

impl GetDefault<PropOrSpread> for PropOrSpread {
  fn get_default() -> PropOrSpread {
    PropOrSpread::dummy()
  }
}

impl GetDefault<SimpleAssignTarget> for SimpleAssignTarget {
  fn get_default() -> SimpleAssignTarget {
    SimpleAssignTarget::dummy()
  }
}

impl GetDefault<Stmt> for Stmt {
  fn get_default() -> Stmt {
    Stmt::dummy()
  }
}

impl GetDefault<SuperProp> for SuperProp {
  fn get_default() -> SuperProp {
    SuperProp::dummy()
  }
}

impl GetDefault<TruePlusMinus> for TruePlusMinus {
  fn get_default() -> TruePlusMinus {
    TruePlusMinus::parse_by_id(0)
  }
}

impl GetDefault<TsEntityName> for TsEntityName {
  fn get_default() -> TsEntityName {
    TsEntityName::Ident(Ident::get_default())
  }
}

impl GetDefault<TsEnumMemberId> for TsEnumMemberId {
  fn get_default() -> TsEnumMemberId {
    TsEnumMemberId::Ident(Ident::get_default())
  }
}

impl GetDefault<TsFnOrConstructorType> for TsFnOrConstructorType {
  fn get_default() -> TsFnOrConstructorType {
    TsFnOrConstructorType::TsFnType(TsFnType::get_default())
  }
}

impl GetDefault<TsFnParam> for TsFnParam {
  fn get_default() -> TsFnParam {
    TsFnParam::Ident(BindingIdent::get_default())
  }
}

impl GetDefault<TsKeywordTypeKind> for TsKeywordTypeKind {
  fn get_default() -> TsKeywordTypeKind {
    TsKeywordTypeKind::parse_by_id(0)
  }
}

impl GetDefault<TsLit> for TsLit {
  fn get_default() -> TsLit {
    TsLit::Bool(Bool::get_default())
  }
}

impl GetDefault<TsModuleName> for TsModuleName {
  fn get_default() -> TsModuleName {
    TsModuleName::Ident(Ident::get_default())
  }
}

impl GetDefault<TsModuleRef> for TsModuleRef {
  fn get_default() -> TsModuleRef {
    TsModuleRef::TsEntityName(TsEntityName::get_default())
  }
}

impl GetDefault<TsNamespaceBody> for TsNamespaceBody {
  fn get_default() -> TsNamespaceBody {
    TsNamespaceBody::TsModuleBlock(TsModuleBlock::get_default())
  }
}

impl GetDefault<TsParamPropParam> for TsParamPropParam {
  fn get_default() -> TsParamPropParam {
    TsParamPropParam::Ident(BindingIdent::get_default())
  }
}

impl GetDefault<TsThisTypeOrIdent> for TsThisTypeOrIdent {
  fn get_default() -> TsThisTypeOrIdent {
    TsThisTypeOrIdent::Ident(Ident::get_default())
  }
}

impl GetDefault<TsType> for TsType {
  fn get_default() -> TsType {
    TsType::TsThisType(TsThisType::get_default())
  }
}

impl GetDefault<TsTypeElement> for TsTypeElement {
  fn get_default() -> TsTypeElement {
    TsTypeElement::TsConstructSignatureDecl(TsConstructSignatureDecl::get_default())
  }
}

impl GetDefault<TsTypeOperatorOp> for TsTypeOperatorOp {
  fn get_default() -> TsTypeOperatorOp {
    TsTypeOperatorOp::parse_by_id(0)
  }
}

impl GetDefault<TsTypeQueryExpr> for TsTypeQueryExpr {
  fn get_default() -> TsTypeQueryExpr {
    TsTypeQueryExpr::Import(TsImportType::get_default())
  }
}

impl GetDefault<TsUnionOrIntersectionType> for TsUnionOrIntersectionType {
  fn get_default() -> TsUnionOrIntersectionType {
    TsUnionOrIntersectionType::TsUnionType(TsUnionType::get_default())
  }
}

impl GetDefault<UnaryOp> for UnaryOp {
  fn get_default() -> UnaryOp {
    UnaryOp::parse_by_id(0)
  }
}

impl GetDefault<UpdateOp> for UpdateOp {
  fn get_default() -> UpdateOp {
    UpdateOp::parse_by_id(0)
  }
}

impl GetDefault<VarDeclKind> for VarDeclKind {
  fn get_default() -> VarDeclKind {
    VarDeclKind::parse_by_id(0)
  }
}

impl GetDefault<VarDeclOrExpr> for VarDeclOrExpr {
  fn get_default() -> VarDeclOrExpr {
    VarDeclOrExpr::dummy()
  }
}

impl GetDefault<ArrayLit> for ArrayLit {
  fn get_default() -> ArrayLit {
    ArrayLit {
      span: DUMMY_SP,
      elems: Default::default(),
    }
  }
}

impl GetDefault<ArrayPat> for ArrayPat {
  fn get_default() -> ArrayPat {
    ArrayPat {
      span: DUMMY_SP,
      elems: Default::default(),
      optional: Default::default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<ArrowExpr> for ArrowExpr {
  fn get_default() -> ArrowExpr {
    ArrowExpr {
      span: DUMMY_SP,
      is_async: Default::default(),
      body: Box::new(BlockStmtOrExpr::get_default()),
      is_generator: Default::default(),
      params: Default::default(),
      return_type: Default::default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<AssignExpr> for AssignExpr {
  fn get_default() -> AssignExpr {
    AssignExpr {
      span: DUMMY_SP,
      left: AssignTarget::get_default(),
      op: AssignOp::get_default(),
      right: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<AssignPat> for AssignPat {
  fn get_default() -> AssignPat {
    AssignPat {
      span: DUMMY_SP,
      left: Box::new(Pat::get_default()),
      right: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<AssignPatProp> for AssignPatProp {
  fn get_default() -> AssignPatProp {
    AssignPatProp {
      span: DUMMY_SP,
      key: BindingIdent::get_default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<AssignProp> for AssignProp {
  fn get_default() -> AssignProp {
    AssignProp {
      key: Ident::get_default(),
      value: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<AutoAccessor> for AutoAccessor {
  fn get_default() -> AutoAccessor {
    AutoAccessor {
      span: DUMMY_SP,
      is_override: Default::default(),
      is_static: Default::default(),
      accessibility: Default::default(),
      decorators: Default::default(),
      definite: Default::default(),
      key: Key::get_default(),
      type_ann: Default::default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<AwaitExpr> for AwaitExpr {
  fn get_default() -> AwaitExpr {
    AwaitExpr {
      span: DUMMY_SP,
      arg: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<BigInt> for BigInt {
  fn get_default() -> BigInt {
    BigInt {
      span: DUMMY_SP,
      raw: Default::default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<BinExpr> for BinExpr {
  fn get_default() -> BinExpr {
    BinExpr {
      span: DUMMY_SP,
      left: Box::new(Expr::get_default()),
      op: BinaryOp::get_default(),
      right: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<BindingIdent> for BindingIdent {
  fn get_default() -> BindingIdent {
    BindingIdent {
      id: Ident::get_default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<BlockStmt> for BlockStmt {
  fn get_default() -> BlockStmt {
    BlockStmt {
      span: DUMMY_SP,
      stmts: Default::default(),
    }
  }
}

impl GetDefault<Bool> for Bool {
  fn get_default() -> Bool {
    Bool {
      span: DUMMY_SP,
      value: Default::default(),
    }
  }
}

impl GetDefault<BreakStmt> for BreakStmt {
  fn get_default() -> BreakStmt {
    BreakStmt {
      span: DUMMY_SP,
      label: Default::default(),
    }
  }
}

impl GetDefault<CallExpr> for CallExpr {
  fn get_default() -> CallExpr {
    CallExpr {
      span: DUMMY_SP,
      args: Default::default(),
      callee: Callee::get_default(),
      type_args: Default::default(),
    }
  }
}

impl GetDefault<CatchClause> for CatchClause {
  fn get_default() -> CatchClause {
    CatchClause {
      span: DUMMY_SP,
      body: BlockStmt::get_default(),
      param: Default::default(),
    }
  }
}

impl GetDefault<Class> for Class {
  fn get_default() -> Class {
    Class {
      span: DUMMY_SP,
      is_abstract: Default::default(),
      implements: Default::default(),
      body: Default::default(),
      decorators: Default::default(),
      super_class: Default::default(),
      super_type_params: Default::default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<ClassDecl> for ClassDecl {
  fn get_default() -> ClassDecl {
    ClassDecl {
      class: Box::new(Class::get_default()),
      declare: Default::default(),
      ident: Ident::get_default(),
    }
  }
}

impl GetDefault<ClassExpr> for ClassExpr {
  fn get_default() -> ClassExpr {
    ClassExpr {
      class: Box::new(Class::get_default()),
      ident: Default::default(),
    }
  }
}

impl GetDefault<ClassMethod> for ClassMethod {
  fn get_default() -> ClassMethod {
    ClassMethod {
      span: DUMMY_SP,
      is_abstract: Default::default(),
      is_override: Default::default(),
      is_static: Default::default(),
      accessibility: Default::default(),
      function: Box::new(Function::get_default()),
      key: PropName::get_default(),
      kind: MethodKind::get_default(),
      is_optional: Default::default(),
    }
  }
}

impl GetDefault<ClassProp> for ClassProp {
  fn get_default() -> ClassProp {
    ClassProp {
      span: DUMMY_SP,
      is_abstract: Default::default(),
      is_override: Default::default(),
      is_static: Default::default(),
      accessibility: Default::default(),
      declare: Default::default(),
      decorators: Default::default(),
      definite: Default::default(),
      key: PropName::get_default(),
      is_optional: Default::default(),
      readonly: Default::default(),
      type_ann: Default::default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<ComputedPropName> for ComputedPropName {
  fn get_default() -> ComputedPropName {
    ComputedPropName {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<CondExpr> for CondExpr {
  fn get_default() -> CondExpr {
    CondExpr {
      span: DUMMY_SP,
      alt: Box::new(Expr::get_default()),
      cons: Box::new(Expr::get_default()),
      test: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<Constructor> for Constructor {
  fn get_default() -> Constructor {
    Constructor {
      span: DUMMY_SP,
      accessibility: Default::default(),
      body: Default::default(),
      key: PropName::get_default(),
      is_optional: Default::default(),
      params: Default::default(),
    }
  }
}

impl GetDefault<ContinueStmt> for ContinueStmt {
  fn get_default() -> ContinueStmt {
    ContinueStmt {
      span: DUMMY_SP,
      label: Default::default(),
    }
  }
}

impl GetDefault<DebuggerStmt> for DebuggerStmt {
  fn get_default() -> DebuggerStmt {
    DebuggerStmt {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<Decorator> for Decorator {
  fn get_default() -> Decorator {
    Decorator {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<DoWhileStmt> for DoWhileStmt {
  fn get_default() -> DoWhileStmt {
    DoWhileStmt {
      span: DUMMY_SP,
      body: Box::new(Stmt::get_default()),
      test: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<EmptyStmt> for EmptyStmt {
  fn get_default() -> EmptyStmt {
    EmptyStmt {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<ExportAll> for ExportAll {
  fn get_default() -> ExportAll {
    ExportAll {
      span: DUMMY_SP,
      src: Box::new(Str::get_default()),
      type_only: Default::default(),
      with: Default::default(),
    }
  }
}

impl GetDefault<ExportDecl> for ExportDecl {
  fn get_default() -> ExportDecl {
    ExportDecl {
      span: DUMMY_SP,
      decl: Decl::get_default(),
    }
  }
}

impl GetDefault<ExportDefaultDecl> for ExportDefaultDecl {
  fn get_default() -> ExportDefaultDecl {
    ExportDefaultDecl {
      span: DUMMY_SP,
      decl: DefaultDecl::get_default(),
    }
  }
}

impl GetDefault<ExportDefaultExpr> for ExportDefaultExpr {
  fn get_default() -> ExportDefaultExpr {
    ExportDefaultExpr {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<ExportDefaultSpecifier> for ExportDefaultSpecifier {
  fn get_default() -> ExportDefaultSpecifier {
    ExportDefaultSpecifier {
      exported: Ident::get_default(),
    }
  }
}

impl GetDefault<ExportNamedSpecifier> for ExportNamedSpecifier {
  fn get_default() -> ExportNamedSpecifier {
    ExportNamedSpecifier {
      span: DUMMY_SP,
      exported: Default::default(),
      orig: ModuleExportName::get_default(),
      is_type_only: Default::default(),
    }
  }
}

impl GetDefault<ExportNamespaceSpecifier> for ExportNamespaceSpecifier {
  fn get_default() -> ExportNamespaceSpecifier {
    ExportNamespaceSpecifier {
      span: DUMMY_SP,
      name: ModuleExportName::get_default(),
    }
  }
}

impl GetDefault<ExprOrSpread> for ExprOrSpread {
  fn get_default() -> ExprOrSpread {
    ExprOrSpread {
      expr: Box::new(Expr::get_default()),
      spread: Default::default(),
    }
  }
}

impl GetDefault<ExprStmt> for ExprStmt {
  fn get_default() -> ExprStmt {
    ExprStmt {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<FnDecl> for FnDecl {
  fn get_default() -> FnDecl {
    FnDecl {
      declare: Default::default(),
      function: Box::new(Function::get_default()),
      ident: Ident::get_default(),
    }
  }
}

impl GetDefault<FnExpr> for FnExpr {
  fn get_default() -> FnExpr {
    FnExpr {
      function: Box::new(Function::get_default()),
      ident: Default::default(),
    }
  }
}

impl GetDefault<ForInStmt> for ForInStmt {
  fn get_default() -> ForInStmt {
    ForInStmt {
      span: DUMMY_SP,
      body: Box::new(Stmt::get_default()),
      left: ForHead::get_default(),
      right: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<ForOfStmt> for ForOfStmt {
  fn get_default() -> ForOfStmt {
    ForOfStmt {
      span: DUMMY_SP,
      is_await: Default::default(),
      body: Box::new(Stmt::get_default()),
      left: ForHead::get_default(),
      right: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<ForStmt> for ForStmt {
  fn get_default() -> ForStmt {
    ForStmt {
      span: DUMMY_SP,
      body: Box::new(Stmt::get_default()),
      init: Default::default(),
      test: Default::default(),
      update: Default::default(),
    }
  }
}

impl GetDefault<Function> for Function {
  fn get_default() -> Function {
    Function {
      span: DUMMY_SP,
      is_async: Default::default(),
      body: Default::default(),
      decorators: Default::default(),
      is_generator: Default::default(),
      params: Default::default(),
      return_type: Default::default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<GetterProp> for GetterProp {
  fn get_default() -> GetterProp {
    GetterProp {
      span: DUMMY_SP,
      body: Default::default(),
      key: PropName::get_default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<Ident> for Ident {
  fn get_default() -> Ident {
    Ident {
      span: DUMMY_SP,
      optional: Default::default(),
      sym: Default::default(),
    }
  }
}

impl GetDefault<IfStmt> for IfStmt {
  fn get_default() -> IfStmt {
    IfStmt {
      span: DUMMY_SP,
      alt: Default::default(),
      cons: Box::new(Stmt::get_default()),
      test: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<Import> for Import {
  fn get_default() -> Import {
    Import {
      span: DUMMY_SP,
      phase: ImportPhase::get_default(),
    }
  }
}

impl GetDefault<ImportDecl> for ImportDecl {
  fn get_default() -> ImportDecl {
    ImportDecl {
      span: DUMMY_SP,
      phase: ImportPhase::get_default(),
      specifiers: Default::default(),
      src: Box::new(Str::get_default()),
      type_only: Default::default(),
      with: Default::default(),
    }
  }
}

impl GetDefault<ImportDefaultSpecifier> for ImportDefaultSpecifier {
  fn get_default() -> ImportDefaultSpecifier {
    ImportDefaultSpecifier {
      span: DUMMY_SP,
      local: Ident::get_default(),
    }
  }
}

impl GetDefault<ImportNamedSpecifier> for ImportNamedSpecifier {
  fn get_default() -> ImportNamedSpecifier {
    ImportNamedSpecifier {
      span: DUMMY_SP,
      imported: Default::default(),
      local: Ident::get_default(),
      is_type_only: Default::default(),
    }
  }
}

impl GetDefault<ImportStarAsSpecifier> for ImportStarAsSpecifier {
  fn get_default() -> ImportStarAsSpecifier {
    ImportStarAsSpecifier {
      span: DUMMY_SP,
      local: Ident::get_default(),
    }
  }
}

impl GetDefault<Invalid> for Invalid {
  fn get_default() -> Invalid {
    Invalid {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<JSXAttr> for JSXAttr {
  fn get_default() -> JSXAttr {
    JSXAttr {
      span: DUMMY_SP,
      name: JSXAttrName::get_default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<JSXClosingElement> for JSXClosingElement {
  fn get_default() -> JSXClosingElement {
    JSXClosingElement {
      span: DUMMY_SP,
      name: JSXElementName::get_default(),
    }
  }
}

impl GetDefault<JSXClosingFragment> for JSXClosingFragment {
  fn get_default() -> JSXClosingFragment {
    JSXClosingFragment {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<JSXElement> for JSXElement {
  fn get_default() -> JSXElement {
    JSXElement {
      span: DUMMY_SP,
      children: Default::default(),
      closing: Default::default(),
      opening: JSXOpeningElement::get_default(),
    }
  }
}

impl GetDefault<JSXEmptyExpr> for JSXEmptyExpr {
  fn get_default() -> JSXEmptyExpr {
    JSXEmptyExpr {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<JSXExprContainer> for JSXExprContainer {
  fn get_default() -> JSXExprContainer {
    JSXExprContainer {
      span: DUMMY_SP,
      expr: JSXExpr::get_default(),
    }
  }
}

impl GetDefault<JSXFragment> for JSXFragment {
  fn get_default() -> JSXFragment {
    JSXFragment {
      span: DUMMY_SP,
      children: Default::default(),
      closing: JSXClosingFragment::get_default(),
      opening: JSXOpeningFragment::get_default(),
    }
  }
}

impl GetDefault<JSXMemberExpr> for JSXMemberExpr {
  fn get_default() -> JSXMemberExpr {
    JSXMemberExpr {
      obj: JSXObject::get_default(),
      prop: Ident::get_default(),
    }
  }
}

impl GetDefault<JSXNamespacedName> for JSXNamespacedName {
  fn get_default() -> JSXNamespacedName {
    JSXNamespacedName {
      name: Ident::get_default(),
      ns: Ident::get_default(),
    }
  }
}

impl GetDefault<JSXOpeningElement> for JSXOpeningElement {
  fn get_default() -> JSXOpeningElement {
    JSXOpeningElement {
      span: DUMMY_SP,
      attrs: Default::default(),
      name: JSXElementName::get_default(),
      self_closing: Default::default(),
      type_args: Default::default(),
    }
  }
}

impl GetDefault<JSXOpeningFragment> for JSXOpeningFragment {
  fn get_default() -> JSXOpeningFragment {
    JSXOpeningFragment {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<JSXSpreadChild> for JSXSpreadChild {
  fn get_default() -> JSXSpreadChild {
    JSXSpreadChild {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<JSXText> for JSXText {
  fn get_default() -> JSXText {
    JSXText {
      span: DUMMY_SP,
      raw: Default::default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<KeyValuePatProp> for KeyValuePatProp {
  fn get_default() -> KeyValuePatProp {
    KeyValuePatProp {
      key: PropName::get_default(),
      value: Box::new(Pat::get_default()),
    }
  }
}

impl GetDefault<KeyValueProp> for KeyValueProp {
  fn get_default() -> KeyValueProp {
    KeyValueProp {
      key: PropName::get_default(),
      value: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<LabeledStmt> for LabeledStmt {
  fn get_default() -> LabeledStmt {
    LabeledStmt {
      span: DUMMY_SP,
      body: Box::new(Stmt::get_default()),
      label: Ident::get_default(),
    }
  }
}

impl GetDefault<MemberExpr> for MemberExpr {
  fn get_default() -> MemberExpr {
    MemberExpr {
      span: DUMMY_SP,
      obj: Box::new(Expr::get_default()),
      prop: MemberProp::get_default(),
    }
  }
}

impl GetDefault<MetaPropExpr> for MetaPropExpr {
  fn get_default() -> MetaPropExpr {
    MetaPropExpr {
      span: DUMMY_SP,
      kind: MetaPropKind::get_default(),
    }
  }
}

impl GetDefault<MethodProp> for MethodProp {
  fn get_default() -> MethodProp {
    MethodProp {
      function: Box::new(Function::get_default()),
      key: PropName::get_default(),
    }
  }
}

impl GetDefault<Module> for Module {
  fn get_default() -> Module {
    Module {
      span: DUMMY_SP,
      body: Default::default(),
      shebang: Default::default(),
    }
  }
}

impl GetDefault<NamedExport> for NamedExport {
  fn get_default() -> NamedExport {
    NamedExport {
      span: DUMMY_SP,
      specifiers: Default::default(),
      src: Default::default(),
      type_only: Default::default(),
      with: Default::default(),
    }
  }
}

impl GetDefault<NewExpr> for NewExpr {
  fn get_default() -> NewExpr {
    NewExpr {
      span: DUMMY_SP,
      args: Default::default(),
      callee: Box::new(Expr::get_default()),
      type_args: Default::default(),
    }
  }
}

impl GetDefault<Null> for Null {
  fn get_default() -> Null {
    Null {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<Number> for Number {
  fn get_default() -> Number {
    Number {
      span: DUMMY_SP,
      raw: Default::default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<ObjectLit> for ObjectLit {
  fn get_default() -> ObjectLit {
    ObjectLit {
      span: DUMMY_SP,
      props: Default::default(),
    }
  }
}

impl GetDefault<ObjectPat> for ObjectPat {
  fn get_default() -> ObjectPat {
    ObjectPat {
      span: DUMMY_SP,
      optional: Default::default(),
      props: Default::default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<OptCall> for OptCall {
  fn get_default() -> OptCall {
    OptCall {
      span: DUMMY_SP,
      args: Default::default(),
      callee: Box::new(Expr::get_default()),
      type_args: Default::default(),
    }
  }
}

impl GetDefault<OptChainExpr> for OptChainExpr {
  fn get_default() -> OptChainExpr {
    OptChainExpr {
      span: DUMMY_SP,
      base: Box::new(OptChainBase::get_default()),
      optional: Default::default(),
    }
  }
}

impl GetDefault<Param> for Param {
  fn get_default() -> Param {
    Param {
      span: DUMMY_SP,
      decorators: Default::default(),
      pat: Pat::get_default(),
    }
  }
}

impl GetDefault<ParenExpr> for ParenExpr {
  fn get_default() -> ParenExpr {
    ParenExpr {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<PrivateMethod> for PrivateMethod {
  fn get_default() -> PrivateMethod {
    PrivateMethod {
      span: DUMMY_SP,
      is_abstract: Default::default(),
      is_override: Default::default(),
      is_static: Default::default(),
      accessibility: Default::default(),
      function: Box::new(Function::get_default()),
      key: PrivateName::get_default(),
      kind: MethodKind::get_default(),
      is_optional: Default::default(),
    }
  }
}

impl GetDefault<PrivateName> for PrivateName {
  fn get_default() -> PrivateName {
    PrivateName {
      span: DUMMY_SP,
      id: Ident::get_default(),
    }
  }
}

impl GetDefault<PrivateProp> for PrivateProp {
  fn get_default() -> PrivateProp {
    PrivateProp {
      span: DUMMY_SP,
      is_override: Default::default(),
      is_static: Default::default(),
      accessibility: Default::default(),
      decorators: Default::default(),
      definite: Default::default(),
      key: PrivateName::get_default(),
      is_optional: Default::default(),
      readonly: Default::default(),
      type_ann: Default::default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<Regex> for Regex {
  fn get_default() -> Regex {
    Regex {
      span: DUMMY_SP,
      exp: Default::default(),
      flags: Default::default(),
    }
  }
}

impl GetDefault<RestPat> for RestPat {
  fn get_default() -> RestPat {
    RestPat {
      span: DUMMY_SP,
      arg: Box::new(Pat::get_default()),
      dot3_token: Default::default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<ReturnStmt> for ReturnStmt {
  fn get_default() -> ReturnStmt {
    ReturnStmt {
      span: DUMMY_SP,
      arg: Default::default(),
    }
  }
}

impl GetDefault<Script> for Script {
  fn get_default() -> Script {
    Script {
      span: DUMMY_SP,
      body: Default::default(),
      shebang: Default::default(),
    }
  }
}

impl GetDefault<SeqExpr> for SeqExpr {
  fn get_default() -> SeqExpr {
    SeqExpr {
      span: DUMMY_SP,
      exprs: Default::default(),
    }
  }
}

impl GetDefault<SetterProp> for SetterProp {
  fn get_default() -> SetterProp {
    SetterProp {
      span: DUMMY_SP,
      body: Default::default(),
      key: PropName::get_default(),
      param: Box::new(Pat::get_default()),
      this_param: Default::default(),
    }
  }
}

impl GetDefault<SpreadElement> for SpreadElement {
  fn get_default() -> SpreadElement {
    SpreadElement {
      dot3_token: Default::default(),
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<StaticBlock> for StaticBlock {
  fn get_default() -> StaticBlock {
    StaticBlock {
      span: DUMMY_SP,
      body: BlockStmt::get_default(),
    }
  }
}

impl GetDefault<Str> for Str {
  fn get_default() -> Str {
    Str {
      span: DUMMY_SP,
      raw: Default::default(),
      value: Default::default(),
    }
  }
}

impl GetDefault<Super> for Super {
  fn get_default() -> Super {
    Super {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<SuperPropExpr> for SuperPropExpr {
  fn get_default() -> SuperPropExpr {
    SuperPropExpr {
      span: DUMMY_SP,
      obj: Super::get_default(),
      prop: SuperProp::get_default(),
    }
  }
}

impl GetDefault<SwitchCase> for SwitchCase {
  fn get_default() -> SwitchCase {
    SwitchCase {
      span: DUMMY_SP,
      cons: Default::default(),
      test: Default::default(),
    }
  }
}

impl GetDefault<SwitchStmt> for SwitchStmt {
  fn get_default() -> SwitchStmt {
    SwitchStmt {
      span: DUMMY_SP,
      cases: Default::default(),
      discriminant: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<TaggedTpl> for TaggedTpl {
  fn get_default() -> TaggedTpl {
    TaggedTpl {
      span: DUMMY_SP,
      tag: Box::new(Expr::get_default()),
      tpl: Box::new(Tpl::get_default()),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<ThisExpr> for ThisExpr {
  fn get_default() -> ThisExpr {
    ThisExpr {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<ThrowStmt> for ThrowStmt {
  fn get_default() -> ThrowStmt {
    ThrowStmt {
      span: DUMMY_SP,
      arg: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<Tpl> for Tpl {
  fn get_default() -> Tpl {
    Tpl {
      span: DUMMY_SP,
      exprs: Default::default(),
      quasis: Default::default(),
    }
  }
}

impl GetDefault<TplElement> for TplElement {
  fn get_default() -> TplElement {
    TplElement {
      span: DUMMY_SP,
      cooked: Default::default(),
      raw: Default::default(),
      tail: Default::default(),
    }
  }
}

impl GetDefault<TryStmt> for TryStmt {
  fn get_default() -> TryStmt {
    TryStmt {
      span: DUMMY_SP,
      block: BlockStmt::get_default(),
      finalizer: Default::default(),
      handler: Default::default(),
    }
  }
}

impl GetDefault<TsArrayType> for TsArrayType {
  fn get_default() -> TsArrayType {
    TsArrayType {
      span: DUMMY_SP,
      elem_type: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsAsExpr> for TsAsExpr {
  fn get_default() -> TsAsExpr {
    TsAsExpr {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsCallSignatureDecl> for TsCallSignatureDecl {
  fn get_default() -> TsCallSignatureDecl {
    TsCallSignatureDecl {
      span: DUMMY_SP,
      params: Default::default(),
      type_ann: Default::default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsConditionalType> for TsConditionalType {
  fn get_default() -> TsConditionalType {
    TsConditionalType {
      span: DUMMY_SP,
      check_type: Box::new(TsType::get_default()),
      extends_type: Box::new(TsType::get_default()),
      false_type: Box::new(TsType::get_default()),
      true_type: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsConstAssertion> for TsConstAssertion {
  fn get_default() -> TsConstAssertion {
    TsConstAssertion {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<TsConstructSignatureDecl> for TsConstructSignatureDecl {
  fn get_default() -> TsConstructSignatureDecl {
    TsConstructSignatureDecl {
      span: DUMMY_SP,
      params: Default::default(),
      type_ann: Default::default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsConstructorType> for TsConstructorType {
  fn get_default() -> TsConstructorType {
    TsConstructorType {
      span: DUMMY_SP,
      is_abstract: Default::default(),
      params: Default::default(),
      type_ann: Box::new(TsTypeAnn::get_default()),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsEnumDecl> for TsEnumDecl {
  fn get_default() -> TsEnumDecl {
    TsEnumDecl {
      span: DUMMY_SP,
      is_const: Default::default(),
      declare: Default::default(),
      id: Ident::get_default(),
      members: Default::default(),
    }
  }
}

impl GetDefault<TsEnumMember> for TsEnumMember {
  fn get_default() -> TsEnumMember {
    TsEnumMember {
      span: DUMMY_SP,
      id: TsEnumMemberId::get_default(),
      init: Default::default(),
    }
  }
}

impl GetDefault<TsExportAssignment> for TsExportAssignment {
  fn get_default() -> TsExportAssignment {
    TsExportAssignment {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<TsExprWithTypeArgs> for TsExprWithTypeArgs {
  fn get_default() -> TsExprWithTypeArgs {
    TsExprWithTypeArgs {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
      type_args: Default::default(),
    }
  }
}

impl GetDefault<TsExternalModuleRef> for TsExternalModuleRef {
  fn get_default() -> TsExternalModuleRef {
    TsExternalModuleRef {
      span: DUMMY_SP,
      expr: Str::get_default(),
    }
  }
}

impl GetDefault<TsFnType> for TsFnType {
  fn get_default() -> TsFnType {
    TsFnType {
      span: DUMMY_SP,
      params: Default::default(),
      type_ann: Box::new(TsTypeAnn::get_default()),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsGetterSignature> for TsGetterSignature {
  fn get_default() -> TsGetterSignature {
    TsGetterSignature {
      span: DUMMY_SP,
      computed: Default::default(),
      key: Box::new(Expr::get_default()),
      optional: Default::default(),
      readonly: Default::default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<TsImportEqualsDecl> for TsImportEqualsDecl {
  fn get_default() -> TsImportEqualsDecl {
    TsImportEqualsDecl {
      span: DUMMY_SP,
      is_export: Default::default(),
      id: Ident::get_default(),
      module_ref: TsModuleRef::get_default(),
      is_type_only: Default::default(),
    }
  }
}

impl GetDefault<TsImportType> for TsImportType {
  fn get_default() -> TsImportType {
    TsImportType {
      span: DUMMY_SP,
      arg: Str::get_default(),
      qualifier: Default::default(),
      type_args: Default::default(),
    }
  }
}

impl GetDefault<TsIndexSignature> for TsIndexSignature {
  fn get_default() -> TsIndexSignature {
    TsIndexSignature {
      span: DUMMY_SP,
      is_static: Default::default(),
      params: Default::default(),
      readonly: Default::default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<TsIndexedAccessType> for TsIndexedAccessType {
  fn get_default() -> TsIndexedAccessType {
    TsIndexedAccessType {
      span: DUMMY_SP,
      index_type: Box::new(TsType::get_default()),
      obj_type: Box::new(TsType::get_default()),
      readonly: Default::default(),
    }
  }
}

impl GetDefault<TsInferType> for TsInferType {
  fn get_default() -> TsInferType {
    TsInferType {
      span: DUMMY_SP,
      type_param: TsTypeParam::get_default(),
    }
  }
}

impl GetDefault<TsInstantiation> for TsInstantiation {
  fn get_default() -> TsInstantiation {
    TsInstantiation {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
      type_args: Box::new(TsTypeParamInstantiation::get_default()),
    }
  }
}

impl GetDefault<TsInterfaceBody> for TsInterfaceBody {
  fn get_default() -> TsInterfaceBody {
    TsInterfaceBody {
      span: DUMMY_SP,
      body: Default::default(),
    }
  }
}

impl GetDefault<TsInterfaceDecl> for TsInterfaceDecl {
  fn get_default() -> TsInterfaceDecl {
    TsInterfaceDecl {
      span: DUMMY_SP,
      extends: Default::default(),
      body: TsInterfaceBody::get_default(),
      declare: Default::default(),
      id: Ident::get_default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsIntersectionType> for TsIntersectionType {
  fn get_default() -> TsIntersectionType {
    TsIntersectionType {
      span: DUMMY_SP,
      types: Default::default(),
    }
  }
}

impl GetDefault<TsKeywordType> for TsKeywordType {
  fn get_default() -> TsKeywordType {
    TsKeywordType {
      span: DUMMY_SP,
      kind: TsKeywordTypeKind::get_default(),
    }
  }
}

impl GetDefault<TsLitType> for TsLitType {
  fn get_default() -> TsLitType {
    TsLitType {
      span: DUMMY_SP,
      lit: TsLit::get_default(),
    }
  }
}

impl GetDefault<TsMappedType> for TsMappedType {
  fn get_default() -> TsMappedType {
    TsMappedType {
      span: DUMMY_SP,
      name_type: Default::default(),
      optional: Default::default(),
      readonly: Default::default(),
      type_ann: Default::default(),
      type_param: TsTypeParam::get_default(),
    }
  }
}

impl GetDefault<TsMethodSignature> for TsMethodSignature {
  fn get_default() -> TsMethodSignature {
    TsMethodSignature {
      span: DUMMY_SP,
      computed: Default::default(),
      key: Box::new(Expr::get_default()),
      optional: Default::default(),
      params: Default::default(),
      readonly: Default::default(),
      type_ann: Default::default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsModuleBlock> for TsModuleBlock {
  fn get_default() -> TsModuleBlock {
    TsModuleBlock {
      span: DUMMY_SP,
      body: Default::default(),
    }
  }
}

impl GetDefault<TsModuleDecl> for TsModuleDecl {
  fn get_default() -> TsModuleDecl {
    TsModuleDecl {
      span: DUMMY_SP,
      body: Default::default(),
      declare: Default::default(),
      global: Default::default(),
      id: TsModuleName::get_default(),
    }
  }
}

impl GetDefault<TsNamespaceDecl> for TsNamespaceDecl {
  fn get_default() -> TsNamespaceDecl {
    TsNamespaceDecl {
      span: DUMMY_SP,
      body: Box::new(TsNamespaceBody::get_default()),
      declare: Default::default(),
      global: Default::default(),
      id: Ident::get_default(),
    }
  }
}

impl GetDefault<TsNamespaceExportDecl> for TsNamespaceExportDecl {
  fn get_default() -> TsNamespaceExportDecl {
    TsNamespaceExportDecl {
      span: DUMMY_SP,
      id: Ident::get_default(),
    }
  }
}

impl GetDefault<TsNonNullExpr> for TsNonNullExpr {
  fn get_default() -> TsNonNullExpr {
    TsNonNullExpr {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<TsOptionalType> for TsOptionalType {
  fn get_default() -> TsOptionalType {
    TsOptionalType {
      span: DUMMY_SP,
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsParamProp> for TsParamProp {
  fn get_default() -> TsParamProp {
    TsParamProp {
      span: DUMMY_SP,
      is_override: Default::default(),
      accessibility: Default::default(),
      decorators: Default::default(),
      param: TsParamPropParam::get_default(),
      readonly: Default::default(),
    }
  }
}

impl GetDefault<TsParenthesizedType> for TsParenthesizedType {
  fn get_default() -> TsParenthesizedType {
    TsParenthesizedType {
      span: DUMMY_SP,
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsPropertySignature> for TsPropertySignature {
  fn get_default() -> TsPropertySignature {
    TsPropertySignature {
      span: DUMMY_SP,
      computed: Default::default(),
      init: Default::default(),
      key: Box::new(Expr::get_default()),
      optional: Default::default(),
      params: Default::default(),
      readonly: Default::default(),
      type_ann: Default::default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsQualifiedName> for TsQualifiedName {
  fn get_default() -> TsQualifiedName {
    TsQualifiedName {
      left: TsEntityName::get_default(),
      right: Ident::get_default(),
    }
  }
}

impl GetDefault<TsRestType> for TsRestType {
  fn get_default() -> TsRestType {
    TsRestType {
      span: DUMMY_SP,
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsSatisfiesExpr> for TsSatisfiesExpr {
  fn get_default() -> TsSatisfiesExpr {
    TsSatisfiesExpr {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsSetterSignature> for TsSetterSignature {
  fn get_default() -> TsSetterSignature {
    TsSetterSignature {
      span: DUMMY_SP,
      computed: Default::default(),
      key: Box::new(Expr::get_default()),
      optional: Default::default(),
      param: TsFnParam::get_default(),
      readonly: Default::default(),
    }
  }
}

impl GetDefault<TsThisType> for TsThisType {
  fn get_default() -> TsThisType {
    TsThisType {
      span: DUMMY_SP,
    }
  }
}

impl GetDefault<TsTplLitType> for TsTplLitType {
  fn get_default() -> TsTplLitType {
    TsTplLitType {
      span: DUMMY_SP,
      quasis: Default::default(),
      types: Default::default(),
    }
  }
}

impl GetDefault<TsTupleElement> for TsTupleElement {
  fn get_default() -> TsTupleElement {
    TsTupleElement {
      span: DUMMY_SP,
      label: Default::default(),
      ty: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsTupleType> for TsTupleType {
  fn get_default() -> TsTupleType {
    TsTupleType {
      span: DUMMY_SP,
      elem_types: Default::default(),
    }
  }
}

impl GetDefault<TsTypeAliasDecl> for TsTypeAliasDecl {
  fn get_default() -> TsTypeAliasDecl {
    TsTypeAliasDecl {
      span: DUMMY_SP,
      declare: Default::default(),
      id: Ident::get_default(),
      type_ann: Box::new(TsType::get_default()),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsTypeAnn> for TsTypeAnn {
  fn get_default() -> TsTypeAnn {
    TsTypeAnn {
      span: DUMMY_SP,
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsTypeAssertion> for TsTypeAssertion {
  fn get_default() -> TsTypeAssertion {
    TsTypeAssertion {
      span: DUMMY_SP,
      expr: Box::new(Expr::get_default()),
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsTypeLit> for TsTypeLit {
  fn get_default() -> TsTypeLit {
    TsTypeLit {
      span: DUMMY_SP,
      members: Default::default(),
    }
  }
}

impl GetDefault<TsTypeOperator> for TsTypeOperator {
  fn get_default() -> TsTypeOperator {
    TsTypeOperator {
      span: DUMMY_SP,
      op: TsTypeOperatorOp::get_default(),
      type_ann: Box::new(TsType::get_default()),
    }
  }
}

impl GetDefault<TsTypeParam> for TsTypeParam {
  fn get_default() -> TsTypeParam {
    TsTypeParam {
      span: DUMMY_SP,
      is_const: Default::default(),
      default: Default::default(),
      constraint: Default::default(),
      is_in: Default::default(),
      name: Ident::get_default(),
      is_out: Default::default(),
    }
  }
}

impl GetDefault<TsTypeParamDecl> for TsTypeParamDecl {
  fn get_default() -> TsTypeParamDecl {
    TsTypeParamDecl {
      span: DUMMY_SP,
      params: Default::default(),
    }
  }
}

impl GetDefault<TsTypeParamInstantiation> for TsTypeParamInstantiation {
  fn get_default() -> TsTypeParamInstantiation {
    TsTypeParamInstantiation {
      span: DUMMY_SP,
      params: Default::default(),
    }
  }
}

impl GetDefault<TsTypePredicate> for TsTypePredicate {
  fn get_default() -> TsTypePredicate {
    TsTypePredicate {
      span: DUMMY_SP,
      asserts: Default::default(),
      param_name: TsThisTypeOrIdent::get_default(),
      type_ann: Default::default(),
    }
  }
}

impl GetDefault<TsTypeQuery> for TsTypeQuery {
  fn get_default() -> TsTypeQuery {
    TsTypeQuery {
      span: DUMMY_SP,
      expr_name: TsTypeQueryExpr::get_default(),
      type_args: Default::default(),
    }
  }
}

impl GetDefault<TsTypeRef> for TsTypeRef {
  fn get_default() -> TsTypeRef {
    TsTypeRef {
      span: DUMMY_SP,
      type_name: TsEntityName::get_default(),
      type_params: Default::default(),
    }
  }
}

impl GetDefault<TsUnionType> for TsUnionType {
  fn get_default() -> TsUnionType {
    TsUnionType {
      span: DUMMY_SP,
      types: Default::default(),
    }
  }
}

impl GetDefault<UnaryExpr> for UnaryExpr {
  fn get_default() -> UnaryExpr {
    UnaryExpr {
      span: DUMMY_SP,
      arg: Box::new(Expr::get_default()),
      op: UnaryOp::get_default(),
    }
  }
}

impl GetDefault<UpdateExpr> for UpdateExpr {
  fn get_default() -> UpdateExpr {
    UpdateExpr {
      span: DUMMY_SP,
      arg: Box::new(Expr::get_default()),
      op: UpdateOp::get_default(),
      prefix: Default::default(),
    }
  }
}

impl GetDefault<UsingDecl> for UsingDecl {
  fn get_default() -> UsingDecl {
    UsingDecl {
      span: DUMMY_SP,
      is_await: Default::default(),
      decls: Default::default(),
    }
  }
}

impl GetDefault<VarDecl> for VarDecl {
  fn get_default() -> VarDecl {
    VarDecl {
      span: DUMMY_SP,
      declare: Default::default(),
      decls: Default::default(),
      kind: VarDeclKind::get_default(),
    }
  }
}

impl GetDefault<VarDeclarator> for VarDeclarator {
  fn get_default() -> VarDeclarator {
    VarDeclarator {
      span: DUMMY_SP,
      definite: Default::default(),
      init: Default::default(),
      name: Pat::get_default(),
    }
  }
}

impl GetDefault<WhileStmt> for WhileStmt {
  fn get_default() -> WhileStmt {
    WhileStmt {
      span: DUMMY_SP,
      body: Box::new(Stmt::get_default()),
      test: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<WithStmt> for WithStmt {
  fn get_default() -> WithStmt {
    WithStmt {
      span: DUMMY_SP,
      body: Box::new(Stmt::get_default()),
      obj: Box::new(Expr::get_default()),
    }
  }
}

impl GetDefault<YieldExpr> for YieldExpr {
  fn get_default() -> YieldExpr {
    YieldExpr {
      span: DUMMY_SP,
      arg: Default::default(),
      delegate: Default::default(),
    }
  }
}

/* GetDefault End */

#[test]
fn test_structs() {
  assert!(true);
}
