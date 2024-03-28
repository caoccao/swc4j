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
    // TODO
  }

  fn register_array_lit(map: &mut ByteToIndexMap, node: &ArrayLit) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_arrow_expr(map: &mut ByteToIndexMap, node: &ArrowExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_auto_accessor(map: &mut ByteToIndexMap, node: &AutoAccessor) {
    map.register_by_span(&node.span);
    register_key(map, &node.key);
    node.value.as_ref().map(|expr| register_expr(map, expr));
    node
      .type_ann
      .as_ref()
      .map(|ts_type_ann| register_ts_type_ann(map, ts_type_ann));
    node
      .decorators
      .iter()
      .for_each(|decorator| register_decorator(map, decorator));
  }

  fn register_await_expr(map: &mut ByteToIndexMap, node: &AwaitExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_bin_expr(map: &mut ByteToIndexMap, node: &BinExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_block_stmt(map: &mut ByteToIndexMap, node: &BlockStmt) {
    map.register_by_span(&node.span);
    node.stmts.iter().for_each(|stmt| register_stmt(map, stmt));
  }

  fn register_call_expr(map: &mut ByteToIndexMap, node: &CallExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_class(map: &mut ByteToIndexMap, node: &Class) {
    map.register_by_span(&node.span);
    node
      .decorators
      .iter()
      .for_each(|decorator| register_decorator(map, decorator));
    node
      .body
      .iter()
      .for_each(|class_member| register_class_member(map, class_member));
    node
      .super_class
      .as_ref()
      .map(|box_expr| register_expr(map, &box_expr.as_ref()));
    node
      .type_params
      .as_ref()
      .map(|box_ts_type_param_decl| register_ts_type_param_decl(map, box_ts_type_param_decl.as_ref()));
    node.super_type_params.as_ref().map(|box_ts_type_param_instantiation| {
      register_ts_type_param_instantiation(map, &box_ts_type_param_instantiation.as_ref())
    });
    node
      .implements
      .iter()
      .for_each(|ts_expr_with_type_args| register_ts_expr_with_type_args(map, &ts_expr_with_type_args));
  }

  fn register_class_decl(map: &mut ByteToIndexMap, node: &ClassDecl) {
    register_ident(map, &node.ident);
    register_class(map, &node.class.as_ref());
  }

  fn register_class_expr(map: &mut ByteToIndexMap, node: &ClassExpr) {
    // TODO
  }

  fn register_class_member(map: &mut ByteToIndexMap, node: &ClassMember) {
    match node {
      ClassMember::Constructor(constructor) => register_constructor(map, constructor),
      ClassMember::Method(class_method) => register_class_method(map, class_method),
      ClassMember::PrivateMethod(private_method) => register_private_method(map, private_method),
      ClassMember::ClassProp(class_prop) => register_class_prop(map, class_prop),
      ClassMember::PrivateProp(private_prop) => register_private_prop(map, private_prop),
      ClassMember::TsIndexSignature(ts_index_signature) => register_ts_index_signature(map, ts_index_signature),
      ClassMember::Empty(empty_stmt) => register_empty_stmt(map, empty_stmt),
      ClassMember::StaticBlock(static_block) => register_static_block(map, static_block),
      ClassMember::AutoAccessor(auto_accessor) => register_auto_accessor(map, auto_accessor),
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
    node.value.as_ref().map(|expr| register_expr(map, expr));
    node
      .type_ann
      .as_ref()
      .map(|ts_type_ann| register_ts_type_ann(map, ts_type_ann));
    node
      .decorators
      .iter()
      .for_each(|decorator| register_decorator(map, decorator));
  }

  fn register_cond_expr(map: &mut ByteToIndexMap, node: &CondExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_constructor(map: &mut ByteToIndexMap, node: &Constructor) {
    map.register_by_span(&node.span);
    register_prop_name(map, &node.key);
    node
      .params
      .iter()
      .for_each(|param_or_ts_param_prop| register_param_or_ts_param_prop(map, param_or_ts_param_prop));
    node
      .body
      .as_ref()
      .map(|block_stmt| register_block_stmt(map, block_stmt));
  }

  fn register_decl(map: &mut ByteToIndexMap, node: &Decl) {
    match node {
      Decl::Class(class_decl) => register_class_decl(map, &class_decl),
      Decl::Fn(fn_decl) => register_fn_decl(map, &fn_decl),
      Decl::Var(var_decl) => register_var_decl(map, var_decl.as_ref()),
      Decl::Using(using_decl) => register_using_decl(map, &using_decl.as_ref()),
      Decl::TsInterface(ts_interface_decl) => register_ts_interface_decl(map, &ts_interface_decl.as_ref()),
      Decl::TsTypeAlias(ts_type_alias_decl) => register_ts_type_alias_decl(map, &ts_type_alias_decl.as_ref()),
      Decl::TsEnum(ts_enum_decl) => register_ts_enum_decl(map, &ts_enum_decl.as_ref()),
      Decl::TsModule(ts_module_decl) => register_ts_module_decl(map, &ts_module_decl.as_ref()),
    };
  }

  fn register_decorator(map: &mut ByteToIndexMap, node: &Decorator) {
    map.register_by_span(&node.span);
    register_expr(map, &node.expr.as_ref());
  }

  fn register_empty_stmt(map: &mut ByteToIndexMap, node: &EmptyStmt) {
    map.register_by_span(&node.span);
  }

  fn register_expr(map: &mut ByteToIndexMap, node: &Expr) {
    match node {
      Expr::This(this_expr) => register_this_expr(map, this_expr),
      Expr::Array(array_lit) => register_array_lit(map, array_lit),
      Expr::Object(object_lit) => register_object_lit(map, object_lit),
      Expr::Fn(fn_expr) => register_fn_expr(map, fn_expr),
      Expr::Unary(unary_expr) => register_unary_expr(map, unary_expr),
      Expr::Update(update_expr) => register_update_expr(map, update_expr),
      Expr::Bin(bin_expr) => register_bin_expr(map, bin_expr),
      Expr::Assign(assign_expr) => register_assign_expr(map, assign_expr),
      Expr::Member(member_expr) => register_member_expr(map, member_expr),
      Expr::SuperProp(super_prop_expr) => register_super_prop_expr(map, super_prop_expr),
      Expr::Cond(cond_expr) => register_cond_expr(map, cond_expr),
      Expr::Call(call_expr) => register_call_expr(map, call_expr),
      Expr::New(new_expr) => register_new_expr(map, new_expr),
      Expr::Seq(seq_expr) => register_seq_expr(map, seq_expr),
      Expr::Ident(ident) => register_ident(map, ident),
      Expr::Lit(lit) => register_lit(map, lit),
      Expr::Tpl(tpl) => register_tpl(map, tpl),
      Expr::TaggedTpl(tagged_tpl) => register_tagged_tpl(map, tagged_tpl),
      Expr::Arrow(arrow_expr) => register_arrow_expr(map, arrow_expr),
      Expr::Class(class_expr) => register_class_expr(map, class_expr),
      Expr::Yield(yield_expr) => register_yield_expr(map, yield_expr),
      Expr::MetaProp(meta_prop_expr) => register_meta_prop_expr(map, meta_prop_expr),
      Expr::Await(await_expr) => register_await_expr(map, await_expr),
      Expr::Paren(paren_expr) => register_paren_expr(map, paren_expr),
      Expr::JSXMember(jsx_member_expr) => register_jsx_member_expr(map, jsx_member_expr),
      Expr::JSXNamespacedName(jsx_namespaced_name) => register_jsx_namespaced_name(map, jsx_namespaced_name),
      Expr::JSXEmpty(jsx_empty_expr) => register_jsx_empty_expr(map, jsx_empty_expr),
      Expr::JSXElement(box_jsx_element) => register_jsx_element(map, box_jsx_element),
      Expr::JSXFragment(jsx_fragment) => register_jsx_fragment(map, jsx_fragment),
      Expr::TsTypeAssertion(ts_type_assertion) => register_ts_type_assertion(map, ts_type_assertion),
      Expr::TsConstAssertion(ts_const_assertion) => register_ts_const_assertion(map, ts_const_assertion),
      Expr::TsNonNull(ts_non_null_expr) => register_ts_non_null_expr(map, ts_non_null_expr),
      Expr::TsAs(ts_as_expr) => register_ts_as_expr(map, ts_as_expr),
      Expr::TsInstantiation(ts_instantiation) => register_ts_instantiation(map, ts_instantiation),
      Expr::TsSatisfies(ts_satisfies_expr) => register_ts_satisfies_expr(map, ts_satisfies_expr),
      Expr::PrivateName(private_name) => register_private_name(map, private_name),
      Expr::OptChain(opt_chain_expr) => register_opt_chain_expr(map, opt_chain_expr),
      Expr::Invalid(invalid) => register_invalid(map, invalid),
    }
  }

  fn register_fn_decl(map: &mut ByteToIndexMap, node: &FnDecl) {
    register_ident(map, &node.ident);
    register_function(map, &node.function);
  }

  fn register_fn_expr(map: &mut ByteToIndexMap, node: &FnExpr) {
    // TODO
  }

  fn register_function(map: &mut ByteToIndexMap, node: &Function) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|param| register_param(map, param));
    node
      .decorators
      .iter()
      .for_each(|decorator| register_decorator(map, decorator));
    node
      .body
      .as_ref()
      .map(|block_stmt| register_block_stmt(map, block_stmt));
    node
      .type_params
      .as_ref()
      .map(|box_ts_type_param_decl| register_ts_type_param_decl(map, box_ts_type_param_decl.as_ref()));
    node
      .return_type
      .as_ref()
      .map(|box_type_ann| register_ts_type_ann(map, box_type_ann));
  }

  fn register_ident(map: &mut ByteToIndexMap, node: &Ident) {
    map.register_by_span(&node.span);
  }

  fn register_invalid(map: &mut ByteToIndexMap, node: &Invalid) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_jsx_element(map: &mut ByteToIndexMap, node: &JSXElement) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_jsx_fragment(map: &mut ByteToIndexMap, node: &JSXFragment) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_jsx_empty_expr(map: &mut ByteToIndexMap, node: &JSXEmptyExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_jsx_member_expr(map: &mut ByteToIndexMap, node: &JSXMemberExpr) {
    // TODO
  }

  fn register_jsx_namespaced_name(map: &mut ByteToIndexMap, node: &JSXNamespacedName) {
    register_ident(map, &node.name);
    register_ident(map, &node.ns);
  }

  fn register_key(map: &mut ByteToIndexMap, node: &Key) {
    // TODO
  }

  fn register_lit(map: &mut ByteToIndexMap, node: &Lit) {
    // TODO
  }

  fn register_member_expr(map: &mut ByteToIndexMap, node: &MemberExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_meta_prop_expr(map: &mut ByteToIndexMap, node: &MetaPropExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_module(map: &mut ByteToIndexMap, node: &Module) {
    map.register_by_span(&node.span);
    node
      .body
      .iter()
      .for_each(|module_item| register_module_item(map, &module_item));
  }

  fn register_module_decl(map: &mut ByteToIndexMap, node: &ModuleDecl) {
    match node {
      _ => {} // TODO
    }
  }

  fn register_module_item(map: &mut ByteToIndexMap, node: &ModuleItem) {
    match node {
      ModuleItem::ModuleDecl(module_decl) => register_module_decl(map, &module_decl),
      ModuleItem::Stmt(stmt) => register_stmt(map, &stmt),
    }
  }

  fn register_new_expr(map: &mut ByteToIndexMap, node: &NewExpr) {
    map.register_by_span(&node.span);
    // TODO
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
      Pat::Ident(binding_ident) => register_ident(map, &binding_ident.id),
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
      Program::Module(module) => register_module(map, module),
      Program::Script(script) => register_script(map, script),
    }
  }

  fn register_prop_name(map: &mut ByteToIndexMap, node: &PropName) {
    // TODO
  }

  fn register_script(map: &mut ByteToIndexMap, node: &Script) {
    map.register_by_span(&node.span);
    node.body.iter().for_each(|stmt| register_stmt(map, stmt))
  }

  fn register_seq_expr(map: &mut ByteToIndexMap, node: &SeqExpr) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_static_block(map: &mut ByteToIndexMap, node: &StaticBlock) {
    map.register_by_span(&node.span);
    // TODO
  }

  fn register_stmt(map: &mut ByteToIndexMap, node: &Stmt) {
    match node {
      Stmt::Decl(decl) => register_decl(map, decl),
      _ => {}
    };
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
      .map(|ts_type_param_instantiation| register_ts_type_param_instantiation(map, &ts_type_param_instantiation));
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
    node
      .decls
      .iter()
      .for_each(|var_declarator| register_var_declarator(map, var_declarator));
  }

  fn register_var_decl(map: &mut ByteToIndexMap, node: &VarDecl) {
    map.register_by_span(&node.span);
    node
      .decls
      .iter()
      .for_each(|var_declarator| register_var_declarator(map, var_declarator));
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
      Expr::Ident(ident) => create_ident(env, map, &ident),
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
      Pat::Ident(bingding_ident) => create_binding_ident(env, map, &bingding_ident),
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
      Some(arc_program) => match arc_program.as_ref() {
        Program::Module(module) => create_module(env, map, module),
        Program::Script(script) => create_script(env, map, script),
      },
      None => Default::default(),
    }
  }

  fn create_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, statement: &Stmt) -> JObject<'a>
  where
    'local: 'a,
  {
    match statement {
      Stmt::Decl(decl) => create_stmt_decl(env, map, &decl),
      _ => Default::default(),
    }
  }

  fn create_stmt_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, decl: &Decl) -> JObject<'a>
  where
    'local: 'a,
  {
    match decl {
      Decl::Var(box_var_decl) => create_var_decl(env, map, &box_var_decl),
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
    body.into_iter().for_each(|stmt| {
      let java_stmt = create_stmt(env, map, stmt);
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
    var_decl.decls.iter().for_each(|var_declarator| {
      let java_var_declarator = create_var_declarator(env, map, var_declarator);
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
      .map(|box_expr| create_expr(env, map, box_expr.as_ref()));
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
