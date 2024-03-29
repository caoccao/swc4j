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

use jni::objects::{GlobalRef, JObject, JStaticMethodID};
use jni::signature::ReturnType;
use jni::sys::jvalue;
use jni::JNIEnv;

use crate::converter;
use crate::jni_utils::delete_local_ref;

use std::ops::Range;
use std::ptr::null_mut;

/* JavaSwc4jAstFactory Begin */
struct JavaSwc4jAstFactory {
  #[allow(dead_code)]
  class: GlobalRef,
  method_create_binding_ident: JStaticMethodID,
  method_create_ident: JStaticMethodID,
  method_create_module: JStaticMethodID,
  method_create_script: JStaticMethodID,
  method_create_var_decl: JStaticMethodID,
  method_create_var_declarator: JStaticMethodID,
}
unsafe impl Send for JavaSwc4jAstFactory {}
unsafe impl Sync for JavaSwc4jAstFactory {}

impl JavaSwc4jAstFactory {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/ast/Swc4jAstFactory")
      .expect("Couldn't find class Swc4jAstFactory");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jAstFactory");
    let method_create_binding_ident = env
      .get_static_method_id(
        &class,
        "createBindingIdent",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;II)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstBindingIdent;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBindingIdent");
    let method_create_ident = env
      .get_static_method_id(
        &class,
        "createIdent",
        "(Ljava/lang/String;ZII)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createIdent");
    let method_create_module = env
      .get_static_method_id(
        &class,
        "createModule",
        "(Ljava/util/List;Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstModule;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createModule");
    let method_create_script = env
      .get_static_method_id(
        &class,
        "createScript",
        "(Ljava/util/List;Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstScript;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createScript");
    let method_create_var_decl = env
      .get_static_method_id(
        &class,
        "createVarDecl",
        "(IZLjava/util/List;II)Lcom/caoccao/javet/swc4j/ast/stmt/decl/Swc4jAstVarDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createVarDecl");
    let method_create_var_declarator = env
      .get_static_method_id(
        &class,
        "createVarDeclarator",
        "(Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstPat;Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstExpr;ZII)Lcom/caoccao/javet/swc4j/ast/stmt/decl/Swc4jAstVarDeclarator;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createVarDeclarator");
    JavaSwc4jAstFactory {
      class,
      method_create_binding_ident,
      method_create_ident,
      method_create_module,
      method_create_script,
      method_create_var_decl,
      method_create_var_declarator,
    }
  }

  pub fn create_binding_ident<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    type_ann: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = jvalue { l: id.as_raw() };
    let type_ann = jvalue { l: type_ann.as_raw() };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let return_value = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_binding_ident,
          ReturnType::Object,
          &[id, type_ann, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstBindingIdent by create_binding_ident()")
        .l()
        .expect("Couldn't convert Swc4jAstBindingIdent by create_binding_ident()")
    };
    return_value
  }

  pub fn create_ident<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    sym: &str,
    optional: bool,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_sym = converter::string_to_jstring(env, &sym);
    let sym = jvalue {
      l: java_sym.as_raw(),
    };
    let optional = jvalue {
      z: optional as u8,
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let return_value = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_ident,
          ReturnType::Object,
          &[sym, optional, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstIdent by create_ident()")
        .l()
        .expect("Couldn't convert Swc4jAstIdent by create_ident()")
    };
    delete_local_ref!(env, java_sym);
    return_value
  }

  pub fn create_module<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    shebang: &Option<String>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let body = jvalue { l: body.as_raw() };
    let java_shebang = match &shebang {
      Some(shebang) => converter::string_to_jstring(env, &shebang),
      None => Default::default(),
    };
    let shebang = jvalue {
      l: java_shebang.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let return_value = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_module,
          ReturnType::Object,
          &[body, shebang, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstModule by create_module()")
        .l()
        .expect("Couldn't convert Swc4jAstModule by create_module()")
    };
    delete_local_ref!(env, java_shebang);
    return_value
  }

  pub fn create_script<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    shebang: &Option<String>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let body = jvalue { l: body.as_raw() };
    let java_shebang = match &shebang {
      Some(shebang) => converter::string_to_jstring(env, &shebang),
      None => Default::default(),
    };
    let shebang = jvalue {
      l: java_shebang.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let return_value = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_script,
          ReturnType::Object,
          &[body, shebang, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstScript by create_script()")
        .l()
        .expect("Couldn't convert Swc4jAstScript by create_script()")
    };
    delete_local_ref!(env, java_shebang);
    return_value
  }

  pub fn create_var_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    kind_id: i32,
    declare: bool,
    decls: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let kind_id = jvalue {
      i: kind_id as i32,
    };
    let declare = jvalue {
      z: declare as u8,
    };
    let decls = jvalue { l: decls.as_raw() };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let return_value = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_var_decl,
          ReturnType::Object,
          &[kind_id, declare, decls, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstVarDecl by create_var_decl()")
        .l()
        .expect("Couldn't convert Swc4jAstVarDecl by create_var_decl()")
    };
    return_value
  }

  pub fn create_var_declarator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    name: &JObject<'_>,
    init: &Option<JObject>,
    definite: bool,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let name = jvalue { l: name.as_raw() };
    let init = jvalue {
      l: match init {
        Some(init) => init.as_raw(),
        None => null_mut(),
      },
    };
    let definite = jvalue {
      z: definite as u8,
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let return_value = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_var_declarator,
          ReturnType::Object,
          &[name, init, definite, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstVarDeclarator by create_var_declarator()")
        .l()
        .expect("Couldn't convert Swc4jAstVarDeclarator by create_var_declarator()")
    };
    return_value
  }
}
/* JavaSwc4jAstFactory End */

static mut JAVA_AST_FACTORY: Option<JavaSwc4jAstFactory> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_AST_FACTORY = Some(JavaSwc4jAstFactory::new(env));
  }
}

pub mod span {
  use crate::position_utils::ByteToIndexMap;
  use deno_ast::swc::ast::*;

  fn register_assign_expr(map: &mut ByteToIndexMap, node: &AssignExpr) {
    map.register_by_span(&node.span);
    match &node.left {
      PatOrExpr::Expr(node) => register_expr(map, node),
      PatOrExpr::Pat(node) => register_pat(map, node),
    }
    register_expr(map, &node.right);
  }

  fn register_array_lit(map: &mut ByteToIndexMap, node: &ArrayLit) {
    map.register_by_span(&node.span);
    node.elems.iter().for_each(|node| {
      node.as_ref().map(|node| {
        node.spread.as_ref().map(|node| map.register_by_span(node));
        register_expr(map, &node.expr);
      });
    });
  }

  fn register_arrow_expr(map: &mut ByteToIndexMap, node: &ArrowExpr) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| register_pat(map, node));
    register_block_stmt_or_expr(map, node.body.as_ref());
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node.as_ref()));
    node.return_type.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_auto_accessor(map: &mut ByteToIndexMap, node: &AutoAccessor) {
    map.register_by_span(&node.span);
    register_key(map, &node.key);
    node.value.as_ref().map(|node| register_expr(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node.decorators.iter().for_each(|node| register_decorator(map, node));
  }

  fn register_await_expr(map: &mut ByteToIndexMap, node: &AwaitExpr) {
    map.register_by_span(&node.span);
    register_expr(map, &node.arg);
  }

  fn register_big_int(map: &mut ByteToIndexMap, node: &BigInt) {
    map.register_by_span(&node.span);
  }

  fn register_bin_expr(map: &mut ByteToIndexMap, node: &BinExpr) {
    map.register_by_span(&node.span);
    register_expr(map, &node.left);
    register_expr(map, &node.right);
  }

  fn register_block_stmt(map: &mut ByteToIndexMap, node: &BlockStmt) {
    map.register_by_span(&node.span);
    node.stmts.iter().for_each(|node| register_stmt(map, node));
  }

  fn register_block_stmt_or_expr(map: &mut ByteToIndexMap, node: &BlockStmtOrExpr) {
    match node {
      BlockStmtOrExpr::BlockStmt(node) => register_block_stmt(map, node),
      BlockStmtOrExpr::Expr(node) => register_expr(map, node),
    }
  }

  fn register_bool(map: &mut ByteToIndexMap, node: &Bool) {
    map.register_by_span(&node.span);
  }

  fn register_call_expr(map: &mut ByteToIndexMap, node: &CallExpr) {
    map.register_by_span(&node.span);
    register_callee(map, &node.callee);
    node.args.iter().for_each(|node| register_expr_or_spread(map, node));
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_callee(map: &mut ByteToIndexMap, node: &Callee) {
    match node {
      Callee::Super(node) => register_super(map, node),
      Callee::Import(node) => register_import(map, node),
      Callee::Expr(node) => register_expr(map, node),
    }
  }

  fn register_class(map: &mut ByteToIndexMap, node: &Class) {
    map.register_by_span(&node.span);
    node.decorators.iter().for_each(|node| register_decorator(map, node));
    node.body.iter().for_each(|node| register_class_member(map, node));
    node.super_class.as_ref().map(|node| register_expr(map, &node.as_ref()));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node.as_ref()));
    node
      .super_type_params
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node.as_ref()));
    node
      .implements
      .iter()
      .for_each(|node| register_ts_expr_with_type_args(map, &node));
  }

  fn register_class_decl(map: &mut ByteToIndexMap, node: &ClassDecl) {
    register_ident(map, &node.ident);
    register_class(map, &node.class);
  }

  fn register_class_expr(map: &mut ByteToIndexMap, node: &ClassExpr) {
    node.ident.as_ref().map(|node| register_ident(map, node));
    register_class(map, &node.class);
  }

  fn register_class_member(map: &mut ByteToIndexMap, node: &ClassMember) {
    match node {
      ClassMember::Constructor(node) => register_constructor(map, node),
      ClassMember::Method(node) => register_class_method(map, node),
      ClassMember::PrivateMethod(node) => register_private_method(map, node),
      ClassMember::ClassProp(node) => register_class_prop(map, node),
      ClassMember::PrivateProp(node) => register_private_prop(map, node),
      ClassMember::TsIndexSignature(node) => register_ts_index_signature(map, node),
      ClassMember::Empty(node) => register_empty_stmt(map, node),
      ClassMember::StaticBlock(node) => register_static_block(map, node),
      ClassMember::AutoAccessor(node) => register_auto_accessor(map, node),
    }
  }

  fn register_class_method(map: &mut ByteToIndexMap, node: &ClassMethod) {
    map.register_by_span(&node.span);
    register_prop_name(map, &node.key);
    register_function(map, &node.function);
  }

  fn register_class_prop(map: &mut ByteToIndexMap, node: &ClassProp) {
    map.register_by_span(&node.span);
    register_prop_name(map, &node.key);
    node.value.as_ref().map(|node| register_expr(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node.decorators.iter().for_each(|node| register_decorator(map, node));
  }

  fn register_cond_expr(map: &mut ByteToIndexMap, node: &CondExpr) {
    map.register_by_span(&node.span);
    register_expr(map, &node.test);
    register_expr(map, &node.cons);
    register_expr(map, &node.alt);
  }

  fn register_constructor(map: &mut ByteToIndexMap, node: &Constructor) {
    map.register_by_span(&node.span);
    register_prop_name(map, &node.key);
    node
      .params
      .iter()
      .for_each(|node| register_param_or_ts_param_prop(map, node));
    node.body.as_ref().map(|node| register_block_stmt(map, node));
  }

  fn register_computed_prop_name(map: &mut ByteToIndexMap, node: &ComputedPropName) {
    map.register_by_span(&node.span);
    register_expr(map, &node.expr);
  }

  fn register_decl(map: &mut ByteToIndexMap, node: &Decl) {
    match node {
      Decl::Class(node) => register_class_decl(map, &node),
      Decl::Fn(node) => register_fn_decl(map, &node),
      Decl::Var(node) => register_var_decl(map, node.as_ref()),
      Decl::Using(node) => register_using_decl(map, &node.as_ref()),
      Decl::TsInterface(node) => register_ts_interface_decl(map, &node.as_ref()),
      Decl::TsTypeAlias(node) => register_ts_type_alias_decl(map, &node.as_ref()),
      Decl::TsEnum(node) => register_ts_enum_decl(map, &node.as_ref()),
      Decl::TsModule(node) => register_ts_module_decl(map, &node.as_ref()),
    };
  }

  fn register_decorator(map: &mut ByteToIndexMap, node: &Decorator) {
    map.register_by_span(&node.span);
    register_expr(map, &node.expr.as_ref());
  }

  fn register_default_decl(map: &mut ByteToIndexMap, node: &DefaultDecl) {
    match node {
      DefaultDecl::Class(node) => register_class_expr(map, node),
      DefaultDecl::Fn(node) => register_fn_expr(map, node),
      DefaultDecl::TsInterfaceDecl(node) => register_ts_interface_decl(map, node),
    }
  }

  fn register_empty_stmt(map: &mut ByteToIndexMap, node: &EmptyStmt) {
    map.register_by_span(&node.span);
  }

  fn register_export_decl(map: &mut ByteToIndexMap, node: &ExportDecl) {
    map.register_by_span(&node.span);
    register_decl(map, &node.decl);
  }

  fn register_export_default_decl(map: &mut ByteToIndexMap, node: &ExportDefaultDecl) {
    map.register_by_span(&node.span);
    register_default_decl(map, &node.decl);
  }

  fn register_export_default_specifier(map: &mut ByteToIndexMap, node: &ExportDefaultSpecifier) {
    register_ident(map, &node.exported);
  }

  fn register_export_named_specifier(map: &mut ByteToIndexMap, node: &ExportNamedSpecifier) {
    map.register_by_span(&node.span);
    register_module_export_name(map, &node.orig);
    node
      .exported
      .as_ref()
      .map(|node| register_module_export_name(map, node));
  }

  fn register_export_namespace_specifier(map: &mut ByteToIndexMap, node: &ExportNamespaceSpecifier) {
    map.register_by_span(&node.span);
    register_module_export_name(map, &node.name);
  }

  fn register_export_specifier(map: &mut ByteToIndexMap, node: &ExportSpecifier) {
    match node {
      ExportSpecifier::Namespace(node) => register_export_namespace_specifier(map, node),
      ExportSpecifier::Default(node) => register_export_default_specifier(map, node),
      ExportSpecifier::Named(node) => register_export_named_specifier(map, node),
    }
  }

  fn register_expr(map: &mut ByteToIndexMap, node: &Expr) {
    match node {
      Expr::This(node) => register_this_expr(map, node),
      Expr::Array(node) => register_array_lit(map, node),
      Expr::Object(node) => register_object_lit(map, node),
      Expr::Fn(node) => register_fn_expr(map, node),
      Expr::Unary(node) => register_unary_expr(map, node),
      Expr::Update(node) => register_update_expr(map, node),
      Expr::Bin(node) => register_bin_expr(map, node),
      Expr::Assign(node) => register_assign_expr(map, node),
      Expr::Member(node) => register_member_expr(map, node),
      Expr::SuperProp(node) => register_super_prop_expr(map, node),
      Expr::Cond(node) => register_cond_expr(map, node),
      Expr::Call(node) => register_call_expr(map, node),
      Expr::New(node) => register_new_expr(map, node),
      Expr::Seq(node) => register_seq_expr(map, node),
      Expr::Ident(node) => register_ident(map, node),
      Expr::Lit(node) => register_lit(map, node),
      Expr::Tpl(node) => register_tpl(map, node),
      Expr::TaggedTpl(node) => register_tagged_tpl(map, node),
      Expr::Arrow(node) => register_arrow_expr(map, node),
      Expr::Class(node) => register_class_expr(map, node),
      Expr::Yield(node) => register_yield_expr(map, node),
      Expr::MetaProp(node) => register_meta_prop_expr(map, node),
      Expr::Await(node) => register_await_expr(map, node),
      Expr::Paren(node) => register_paren_expr(map, node),
      Expr::JSXMember(node) => register_jsx_member_expr(map, node),
      Expr::JSXNamespacedName(node) => register_jsx_namespaced_name(map, node),
      Expr::JSXEmpty(node) => register_jsx_empty_expr(map, node),
      Expr::JSXElement(node) => register_jsx_element(map, node),
      Expr::JSXFragment(node) => register_jsx_fragment(map, node),
      Expr::TsTypeAssertion(node) => register_ts_type_assertion(map, node),
      Expr::TsConstAssertion(node) => register_ts_const_assertion(map, node),
      Expr::TsNonNull(node) => register_ts_non_null_expr(map, node),
      Expr::TsAs(node) => register_ts_as_expr(map, node),
      Expr::TsInstantiation(node) => register_ts_instantiation(map, node),
      Expr::TsSatisfies(node) => register_ts_satisfies_expr(map, node),
      Expr::PrivateName(node) => register_private_name(map, node),
      Expr::OptChain(node) => register_opt_chain_expr(map, node),
      Expr::Invalid(node) => register_invalid(map, node),
    }
  }

  fn register_expr_or_spread(map: &mut ByteToIndexMap, node: &ExprOrSpread) {
    node.spread.as_ref().map(|node| map.register_by_span(node));
    register_expr(map, &node.expr);
  }

  fn register_fn_decl(map: &mut ByteToIndexMap, node: &FnDecl) {
    register_ident(map, &node.ident);
    register_function(map, &node.function);
  }

  fn register_fn_expr(map: &mut ByteToIndexMap, node: &FnExpr) {
    node.ident.as_ref().map(|node| register_ident(map, node));
    register_function(map, &node.function);
  }

  fn register_function(map: &mut ByteToIndexMap, node: &Function) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| register_param(map, node));
    node.decorators.iter().for_each(|node| register_decorator(map, node));
    node.body.as_ref().map(|node| register_block_stmt(map, node));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node.as_ref()));
    node.return_type.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_ident(map: &mut ByteToIndexMap, node: &Ident) {
    map.register_by_span(&node.span);
  }

  fn register_import(map: &mut ByteToIndexMap, node: &Import) {
    map.register_by_span(&node.span);
  }

  fn register_import_decl(map: &mut ByteToIndexMap, node: &ImportDecl) {
    map.register_by_span(&node.span);
    node
      .specifiers
      .iter()
      .for_each(|node| register_import_specifier(map, node));
    register_str(map, &node.src);
    node.with.as_ref().map(|node| register_object_lit(map, node));
  }

  fn register_import_default_specifier(map: &mut ByteToIndexMap, node: &ImportDefaultSpecifier) {
    map.register_by_span(&node.span);
    register_ident(map, &node.local);
  }

  fn register_import_named_specifier(map: &mut ByteToIndexMap, node: &ImportNamedSpecifier) {
    map.register_by_span(&node.span);
    register_ident(map, &node.local);
    node
      .imported
      .as_ref()
      .map(|node| register_module_export_name(map, node));
  }

  fn register_import_specifier(map: &mut ByteToIndexMap, node: &ImportSpecifier) {
    match node {
      ImportSpecifier::Named(node) => register_import_named_specifier(map, node),
      ImportSpecifier::Default(node) => register_import_default_specifier(map, node),
      ImportSpecifier::Namespace(node) => register_import_star_as_specifier(map, node),
    }
  }

  fn register_import_star_as_specifier(map: &mut ByteToIndexMap, node: &ImportStarAsSpecifier) {
    map.register_by_span(&node.span);
    register_ident(map, &node.local);
  }

  fn register_invalid(map: &mut ByteToIndexMap, node: &Invalid) {
    map.register_by_span(&node.span);
  }

  fn register_jsx_attr(map: &mut ByteToIndexMap, node: &JSXAttr) {
    map.register_by_span(&node.span);
    register_jsx_attr_name(map, &node.name);
    node.value.as_ref().map(|node| register_jsx_attr_value(map, node));
  }

  fn register_jsx_attr_name(map: &mut ByteToIndexMap, node: &JSXAttrName) {
    match node {
      JSXAttrName::Ident(node) => register_ident(map, node),
      JSXAttrName::JSXNamespacedName(node) => register_jsx_namespaced_name(map, node),
    }
  }

  fn register_jsx_attr_or_spread(map: &mut ByteToIndexMap, node: &JSXAttrOrSpread) {
    match node {
      JSXAttrOrSpread::JSXAttr(node) => register_jsx_attr(map, node),
      JSXAttrOrSpread::SpreadElement(node) => register_spread_element(map, node),
    }
  }

  fn register_jsx_attr_value(map: &mut ByteToIndexMap, node: &JSXAttrValue) {
    match node {
      JSXAttrValue::Lit(node) => register_lit(map, node),
      JSXAttrValue::JSXExprContainer(node) => register_jsx_expr_container(map, node),
      JSXAttrValue::JSXElement(node) => register_jsx_element(map, node),
      JSXAttrValue::JSXFragment(node) => register_jsx_fragment(map, node),
    }
  }

  fn register_jsx_closing_element(map: &mut ByteToIndexMap, node: &JSXClosingElement) {
    map.register_by_span(&node.span);
    register_jsx_element_name(map, &node.name);
  }

  fn register_jsx_closing_fragment(map: &mut ByteToIndexMap, node: &JSXClosingFragment) {
    map.register_by_span(&node.span);
  }

  fn register_jsx_element(map: &mut ByteToIndexMap, node: &JSXElement) {
    map.register_by_span(&node.span);
    register_jsx_opening_element(map, &node.opening);
    node
      .children
      .iter()
      .for_each(|node| register_jsx_element_child(map, node));
    node
      .closing
      .as_ref()
      .map(|node| register_jsx_closing_element(map, node));
  }

  fn register_jsx_element_child(map: &mut ByteToIndexMap, node: &JSXElementChild) {
    match node {
      JSXElementChild::JSXText(node) => register_jsx_text(map, node),
      JSXElementChild::JSXExprContainer(node) => register_jsx_expr_container(map, node),
      JSXElementChild::JSXSpreadChild(node) => register_jsx_spread_child(map, node),
      JSXElementChild::JSXElement(node) => register_jsx_element(map, node),
      JSXElementChild::JSXFragment(node) => register_jsx_fragment(map, node),
    }
  }

  fn register_jsx_element_name(map: &mut ByteToIndexMap, node: &JSXElementName) {
    match node {
      JSXElementName::Ident(node) => register_ident(map, node),
      JSXElementName::JSXMemberExpr(node) => register_jsx_member_expr(map, node),
      JSXElementName::JSXNamespacedName(node) => register_jsx_namespaced_name(map, node),
    }
  }

  fn register_jsx_empty_expr(map: &mut ByteToIndexMap, node: &JSXEmptyExpr) {
    map.register_by_span(&node.span);
  }

  fn register_jsx_expr(map: &mut ByteToIndexMap, node: &JSXExpr) {
    match node {
      JSXExpr::JSXEmptyExpr(node) => register_jsx_empty_expr(map, node),
      JSXExpr::Expr(node) => register_expr(map, node),
    }
  }

  fn register_jsx_expr_container(map: &mut ByteToIndexMap, node: &JSXExprContainer) {
    map.register_by_span(&node.span);
    register_jsx_expr(map, &node.expr);
  }

  fn register_jsx_fragment(map: &mut ByteToIndexMap, node: &JSXFragment) {
    map.register_by_span(&node.span);
    register_jsx_opening_fragment(map, &node.opening);
    node
      .children
      .iter()
      .for_each(|node| register_jsx_element_child(map, node));
    register_jsx_closing_fragment(map, &node.closing);
  }

  fn register_jsx_member_expr(map: &mut ByteToIndexMap, node: &JSXMemberExpr) {
    register_jsx_object(map, &node.obj);
    register_ident(map, &node.prop);
  }

  fn register_jsx_namespaced_name(map: &mut ByteToIndexMap, node: &JSXNamespacedName) {
    register_ident(map, &node.name);
    register_ident(map, &node.ns);
  }

  fn register_jsx_object(map: &mut ByteToIndexMap, node: &JSXObject) {
    match node {
      JSXObject::JSXMemberExpr(node) => register_jsx_member_expr(map, node),
      JSXObject::Ident(node) => register_ident(map, node),
    }
  }

  fn register_jsx_opening_element(map: &mut ByteToIndexMap, node: &JSXOpeningElement) {
    map.register_by_span(&node.span);
    register_jsx_element_name(map, &node.name);
    node
      .attrs
      .iter()
      .for_each(|node| register_jsx_attr_or_spread(map, node));
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_jsx_opening_fragment(map: &mut ByteToIndexMap, node: &JSXOpeningFragment) {
    map.register_by_span(&node.span);
  }

  fn register_jsx_spread_child(map: &mut ByteToIndexMap, node: &JSXSpreadChild) {
    map.register_by_span(&node.span);
    register_expr(map, &node.expr);
  }

  fn register_jsx_text(map: &mut ByteToIndexMap, node: &JSXText) {
    map.register_by_span(&node.span);
  }

  fn register_key(map: &mut ByteToIndexMap, node: &Key) {
    match node {
      Key::Private(node) => register_private_name(map, node),
      Key::Public(node) => register_prop_name(map, node),
    }
  }

  fn register_lit(map: &mut ByteToIndexMap, node: &Lit) {
    match node {
      Lit::Str(node) => register_str(map, node),
      Lit::Bool(node) => register_bool(map, node),
      Lit::Null(node) => register_null(map, node),
      Lit::Num(node) => register_number(map, node),
      Lit::BigInt(node) => register_big_int(map, node),
      Lit::Regex(node) => register_regex(map, node),
      Lit::JSXText(node) => register_jsx_text(map, node),
    }
  }

  fn register_member_expr(map: &mut ByteToIndexMap, node: &MemberExpr) {
    map.register_by_span(&node.span);
    register_expr(map, &node.obj);
    register_member_prop(map, &node.prop);
  }

  fn register_member_prop(map: &mut ByteToIndexMap, node: &MemberProp) {
    match node {
      MemberProp::Ident(node) => register_ident(map, node),
      MemberProp::PrivateName(node) => register_private_name(map, node),
      MemberProp::Computed(node) => register_computed_prop_name(map, node),
    }
  }

  fn register_meta_prop_expr(map: &mut ByteToIndexMap, node: &MetaPropExpr) {
    map.register_by_span(&node.span);
  }

  fn register_module(map: &mut ByteToIndexMap, node: &Module) {
    map.register_by_span(&node.span);
    node.body.iter().for_each(|node| register_module_item(map, &node));
  }

  fn register_module_decl(map: &mut ByteToIndexMap, node: &ModuleDecl) {
    match node {
      ModuleDecl::Import(node) => register_import_decl(map, node),
      ModuleDecl::ExportDecl(node) => register_export_decl(map, node),
      ModuleDecl::ExportNamed(node) => register_named_export(map, node),
      ModuleDecl::ExportDefaultDecl(node) => register_export_default_decl(map, node),
      _ => {} // TODO
    }
  }

  fn register_module_export_name(map: &mut ByteToIndexMap, node: &ModuleExportName) {
    match node {
      ModuleExportName::Ident(node) => register_ident(map, node),
      ModuleExportName::Str(node) => register_str(map, node),
    }
  }

  fn register_module_item(map: &mut ByteToIndexMap, node: &ModuleItem) {
    match node {
      ModuleItem::ModuleDecl(node) => register_module_decl(map, &node),
      ModuleItem::Stmt(node) => register_stmt(map, &node),
    }
  }

  fn register_named_export(map: &mut ByteToIndexMap, node: &NamedExport) {
    map.register_by_span(&node.span);
    node
      .specifiers
      .iter()
      .for_each(|node| register_export_specifier(map, node));
    node.src.as_ref().map(|node| register_str(map, node));
    node.with.as_ref().map(|node| register_object_lit(map, node));
  }

  fn register_new_expr(map: &mut ByteToIndexMap, node: &NewExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_null(map: &mut ByteToIndexMap, node: &Null) {
    map.register_by_span(&node.span);
  }

  fn register_number(map: &mut ByteToIndexMap, node: &Number) {
    map.register_by_span(&node.span);
  }

  fn register_object_lit(map: &mut ByteToIndexMap, node: &ObjectLit) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_opt_chain_expr(map: &mut ByteToIndexMap, node: &OptChainExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_param(map: &mut ByteToIndexMap, node: &Param) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_param_or_ts_param_prop(map: &mut ByteToIndexMap, node: &ParamOrTsParamProp) {
    // TODO
  }

  fn register_paren_expr(map: &mut ByteToIndexMap, node: &ParenExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_pat(map: &mut ByteToIndexMap, node: &Pat) {
    match &node {
      Pat::Ident(node) => register_ident(map, &node.id),
      _ => {}
    }
    // TODO
  }

  fn register_private_method(map: &mut ByteToIndexMap, node: &PrivateMethod) {
    map.register_by_span(&node.span);
    register_private_name(map, &node.key);
    register_function(map, &node.function);
    // TODO
  }

  fn register_private_name(map: &mut ByteToIndexMap, node: &PrivateName) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
  }

  fn register_private_prop(map: &mut ByteToIndexMap, node: &PrivateProp) {
    map.register_by_span(&node.span);
    register_private_name(map, &node.key);
    // TODO
  }

  pub fn register_program(map: &mut ByteToIndexMap, node: &Program) {
    match node {
      Program::Module(node) => register_module(map, node),
      Program::Script(node) => register_script(map, node),
    }
  }

  fn register_prop_name(map: &mut ByteToIndexMap, node: &PropName) {
    // TODO
  }

  fn register_regex(map: &mut ByteToIndexMap, node: &Regex) {
    map.register_by_span(&node.span);
  }

  fn register_script(map: &mut ByteToIndexMap, node: &Script) {
    map.register_by_span(&node.span);
    node.body.iter().for_each(|node| register_stmt(map, node))
  }

  fn register_seq_expr(map: &mut ByteToIndexMap, node: &SeqExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_spread_element(map: &mut ByteToIndexMap, node: &SpreadElement) {
    map.register_by_span(&node.dot3_token);
    register_expr(map, &node.expr);
  }

  fn register_static_block(map: &mut ByteToIndexMap, node: &StaticBlock) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_stmt(map: &mut ByteToIndexMap, node: &Stmt) {
    match node {
      Stmt::Decl(node) => register_decl(map, node),
      _ => {}
    };
  }

  fn register_str(map: &mut ByteToIndexMap, node: &Str) {
    map.register_by_span(&node.span);
  }

  fn register_super(map: &mut ByteToIndexMap, node: &Super) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_super_prop_expr(map: &mut ByteToIndexMap, node: &SuperPropExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_tagged_tpl(map: &mut ByteToIndexMap, node: &TaggedTpl) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_this_expr(map: &mut ByteToIndexMap, node: &ThisExpr) {
    map.register_by_span(&node.span);
  }

  fn register_ts_as_expr(map: &mut ByteToIndexMap, node: &TsAsExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_enum_decl(map: &mut ByteToIndexMap, node: &TsEnumDecl) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_expr_with_type_args(map: &mut ByteToIndexMap, node: &TsExprWithTypeArgs) {
    map.register_by_span(&node.span);
    register_expr(map, &node.expr);
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_ts_index_signature(map: &mut ByteToIndexMap, node: &TsIndexSignature) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_interface_decl(map: &mut ByteToIndexMap, node: &TsInterfaceDecl) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_instantiation(map: &mut ByteToIndexMap, node: &TsInstantiation) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_module_decl(map: &mut ByteToIndexMap, node: &TsModuleDecl) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_non_null_expr(map: &mut ByteToIndexMap, node: &TsNonNullExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_satisfies_expr(map: &mut ByteToIndexMap, node: &TsSatisfiesExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_type_alias_decl(map: &mut ByteToIndexMap, node: &TsTypeAliasDecl) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_type_ann(map: &mut ByteToIndexMap, node: &TsTypeAnn) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_type_assertion(map: &mut ByteToIndexMap, node: &TsTypeAssertion) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_const_assertion(map: &mut ByteToIndexMap, node: &TsConstAssertion) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_type_param_decl(map: &mut ByteToIndexMap, node: &TsTypeParamDecl) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_ts_type_param_instantiation(map: &mut ByteToIndexMap, node: &TsTypeParamInstantiation) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_tpl(map: &mut ByteToIndexMap, node: &Tpl) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_unary_expr(map: &mut ByteToIndexMap, node: &UnaryExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_update_expr(map: &mut ByteToIndexMap, node: &UpdateExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_using_decl(map: &mut ByteToIndexMap, node: &UsingDecl) {
    map.register_by_span(&node.span);
    node.decls.iter().for_each(|node| register_var_declarator(map, node));
  }

  fn register_var_decl(map: &mut ByteToIndexMap, node: &VarDecl) {
    map.register_by_span(&node.span);
    node.decls.iter().for_each(|node| register_var_declarator(map, node));
  }

  fn register_var_declarator(map: &mut ByteToIndexMap, node: &VarDeclarator) {
    map.register_by_span(&node.span);
    register_pat(map, &node.name);
  }

  fn register_yield_expr(map: &mut ByteToIndexMap, node: &YieldExpr) {
    map.register_by_span(&node.span);
    // TODO
  }
}

pub mod program {
  use jni::objects::JObject;
  use jni::JNIEnv;

  use crate::ast_utils::JAVA_AST_FACTORY;
  use crate::enums::IdentifiableEnum;
  use crate::jni_utils::{delete_local_ref, JAVA_ARRAY_LIST};
  use crate::position_utils::ByteToIndexMap;

  use std::sync::Arc;

  use deno_ast::swc::ast::*;
  use deno_ast::swc::common::Spanned;

  fn create_binding_ident<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    binding_ident: &BindingIdent,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&binding_ident.span());
    let java_id = create_ident(env, map, &binding_ident.id);
    let java_type_ann: JObject<'_> = Default::default(); // TODO
    let return_value = java_ast_factory.create_binding_ident(env, &java_id, &java_type_ann, &range);
    delete_local_ref!(env, java_id);
    delete_local_ref!(env, java_type_ann);
    return_value
  }

  fn create_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, expr: &Expr) -> JObject<'a>
  where
    'local: 'a,
  {
    match expr {
      Expr::Ident(node) => create_ident(env, map, &node),
      _ => Default::default(),
    }
  }

  fn create_ident<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, ident: &Ident) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&ident.span());
    let sym = ident.sym.as_str();
    let optional = ident.optional;
    java_ast_factory.create_ident(env, sym, optional, &range)
  }

  fn create_module<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, module: &Module) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let shebang: Option<String> = module.shebang.to_owned().map(|s| s.to_string());
    let range = map.get_range_by_span(&module.span());
    let java_body = create_module_body(env, map, &module.body);
    let java_module = java_ast_factory.create_module(env, &java_body, &shebang, &range);
    delete_local_ref!(env, java_body);
    java_module
  }

  fn create_module_body<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    body: &Vec<ModuleItem>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_body = java_array_list.construct(env, body.len());
    // TODO
    java_body
  }

  fn create_pat<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, pat: &Pat) -> JObject<'a>
  where
    'local: 'a,
  {
    match pat {
      Pat::Ident(node) => create_binding_ident(env, map, &node),
      _ => Default::default(),
    }
  }

  pub fn create_program<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    program: &Option<Arc<Program>>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match program {
      Some(node) => match node.as_ref() {
        Program::Module(node) => create_module(env, map, node),
        Program::Script(node) => create_script(env, map, node),
      },
      None => Default::default(),
    }
  }

  fn create_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, statement: &Stmt) -> JObject<'a>
  where
    'local: 'a,
  {
    match statement {
      Stmt::Decl(node) => create_stmt_decl(env, map, &node),
      _ => Default::default(),
    }
  }

  fn create_stmt_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, decl: &Decl) -> JObject<'a>
  where
    'local: 'a,
  {
    match decl {
      Decl::Var(node) => create_var_decl(env, map, &node),
      _ => Default::default(),
    }
  }

  fn create_script<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, script: &Script) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let shebang: Option<String> = script.shebang.to_owned().map(|s| s.to_string());
    let range = map.get_range_by_span(&script.span());
    let java_body = create_script_body(env, map, &script.body);
    let java_script = java_ast_factory.create_script(env, &java_body, &shebang, &range);
    delete_local_ref!(env, java_body);
    java_script
  }

  fn create_script_body<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, body: &Vec<Stmt>) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_body = java_array_list.construct(env, body.len());
    body.into_iter().for_each(|node| {
      let java_stmt = create_stmt(env, map, node);
      java_array_list.add(env, &java_body, &java_stmt);
      delete_local_ref!(env, java_stmt);
    });
    java_body
  }

  fn create_var_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, var_decl: &VarDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let declare = var_decl.declare;
    let kind_id = var_decl.kind.get_id();
    let range = map.get_range_by_span(&var_decl.span());
    let java_decls = java_array_list.construct(env, var_decl.decls.len());
    var_decl.decls.iter().for_each(|node| {
      let java_var_declarator = create_var_declarator(env, map, node);
      java_array_list.add(env, &java_decls, &java_var_declarator);
      delete_local_ref!(env, java_var_declarator);
    });
    let return_value = java_ast_factory.create_var_decl(env, kind_id, declare, &java_decls, &range);
    delete_local_ref!(env, java_decls);
    return_value
  }

  fn create_var_declarator<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    var_declarator: &VarDeclarator,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let definite = var_declarator.definite;
    let java_option_init: Option<JObject> = var_declarator
      .init
      .as_ref()
      .map(|node| create_expr(env, map, node.as_ref()));
    let java_name = create_pat(env, map, &var_declarator.name);
    let range = map.get_range_by_span(&var_declarator.span());
    let return_value = java_ast_factory.create_var_declarator(env, &java_name, &java_option_init, definite, &range);
    if java_option_init.is_some() {
      delete_local_ref!(env, java_option_init.unwrap());
    }
    delete_local_ref!(env, java_name);
    return_value
  }
}
