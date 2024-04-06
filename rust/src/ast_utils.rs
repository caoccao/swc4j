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

use crate::jni_utils::*;

use std::ops::Range;
use std::ptr::null_mut;

/* JavaSwc4jAstFactory Begin */
struct JavaSwc4jAstFactory {
  #[allow(dead_code)]
  class: GlobalRef,
  method_create_array_lit: JStaticMethodID,
  method_create_array_pat: JStaticMethodID,
  method_create_arrow_expr: JStaticMethodID,
  method_create_assign_expr: JStaticMethodID,
  method_create_assign_pat: JStaticMethodID,
  method_create_assign_pat_prop: JStaticMethodID,
  method_create_assign_prop: JStaticMethodID,
  method_create_auto_accessor: JStaticMethodID,
  method_create_await_expr: JStaticMethodID,
  method_create_big_int: JStaticMethodID,
  method_create_bin_expr: JStaticMethodID,
  method_create_binding_ident: JStaticMethodID,
  method_create_block_stmt: JStaticMethodID,
  method_create_bool: JStaticMethodID,
  method_create_break_stmt: JStaticMethodID,
  method_create_call_expr: JStaticMethodID,
  method_create_catch_clause: JStaticMethodID,
  method_create_class: JStaticMethodID,
  method_create_class_decl: JStaticMethodID,
  method_create_class_expr: JStaticMethodID,
  method_create_class_method: JStaticMethodID,
  method_create_class_prop: JStaticMethodID,
  method_create_computed_prop_name: JStaticMethodID,
  method_create_cond_expr: JStaticMethodID,
  method_create_constructor: JStaticMethodID,
  method_create_continue_stmt: JStaticMethodID,
  method_create_debugger_stmt: JStaticMethodID,
  method_create_decorator: JStaticMethodID,
  method_create_do_while_stmt: JStaticMethodID,
  method_create_empty_stmt: JStaticMethodID,
  method_create_export_all: JStaticMethodID,
  method_create_export_decl: JStaticMethodID,
  method_create_export_default_decl: JStaticMethodID,
  method_create_export_default_expr: JStaticMethodID,
  method_create_export_default_specifier: JStaticMethodID,
  method_create_export_named_specifier: JStaticMethodID,
  method_create_export_namespace_specifier: JStaticMethodID,
  method_create_expr_or_spread: JStaticMethodID,
  method_create_expr_stmt: JStaticMethodID,
  method_create_fn_decl: JStaticMethodID,
  method_create_fn_expr: JStaticMethodID,
  method_create_for_in_stmt: JStaticMethodID,
  method_create_for_of_stmt: JStaticMethodID,
  method_create_for_stmt: JStaticMethodID,
  method_create_function: JStaticMethodID,
  method_create_getter_prop: JStaticMethodID,
  method_create_ident: JStaticMethodID,
  method_create_if_stmt: JStaticMethodID,
  method_create_import: JStaticMethodID,
  method_create_import_decl: JStaticMethodID,
  method_create_import_default_specifier: JStaticMethodID,
  method_create_import_named_specifier: JStaticMethodID,
  method_create_import_star_as_specifier: JStaticMethodID,
  method_create_invalid: JStaticMethodID,
  method_create_jsx_closing_element: JStaticMethodID,
  method_create_jsx_closing_fragment: JStaticMethodID,
  method_create_jsx_element: JStaticMethodID,
  method_create_jsx_empty_expr: JStaticMethodID,
  method_create_jsx_expr_container: JStaticMethodID,
  method_create_jsx_fragment: JStaticMethodID,
  method_create_jsx_member_expr: JStaticMethodID,
  method_create_jsx_namespaced_name: JStaticMethodID,
  method_create_jsx_opening_element: JStaticMethodID,
  method_create_jsx_opening_fragment: JStaticMethodID,
  method_create_jsx_spread_child: JStaticMethodID,
  method_create_jsx_text: JStaticMethodID,
  method_create_key_value_pat_prop: JStaticMethodID,
  method_create_key_value_prop: JStaticMethodID,
  method_create_labeled_stmt: JStaticMethodID,
  method_create_member_expr: JStaticMethodID,
  method_create_meta_prop_expr: JStaticMethodID,
  method_create_method_prop: JStaticMethodID,
  method_create_module: JStaticMethodID,
  method_create_named_export: JStaticMethodID,
  method_create_new_expr: JStaticMethodID,
  method_create_null: JStaticMethodID,
  method_create_number: JStaticMethodID,
  method_create_object_lit: JStaticMethodID,
  method_create_object_pat: JStaticMethodID,
  method_create_opt_call: JStaticMethodID,
  method_create_opt_chain_expr: JStaticMethodID,
  method_create_param: JStaticMethodID,
  method_create_paren_expr: JStaticMethodID,
  method_create_private_method: JStaticMethodID,
  method_create_private_name: JStaticMethodID,
  method_create_private_prop: JStaticMethodID,
  method_create_regex: JStaticMethodID,
  method_create_rest_pat: JStaticMethodID,
  method_create_return_stmt: JStaticMethodID,
  method_create_script: JStaticMethodID,
  method_create_seq_expr: JStaticMethodID,
  method_create_setter_prop: JStaticMethodID,
  method_create_span: JStaticMethodID,
  method_create_spread_element: JStaticMethodID,
  method_create_static_block: JStaticMethodID,
  method_create_str: JStaticMethodID,
  method_create_super: JStaticMethodID,
  method_create_super_prop_expr: JStaticMethodID,
  method_create_switch_case: JStaticMethodID,
  method_create_switch_stmt: JStaticMethodID,
  method_create_tagged_tpl: JStaticMethodID,
  method_create_this_expr: JStaticMethodID,
  method_create_throw_stmt: JStaticMethodID,
  method_create_tpl: JStaticMethodID,
  method_create_tpl_element: JStaticMethodID,
  method_create_try_stmt: JStaticMethodID,
  method_create_ts_as_expr: JStaticMethodID,
  method_create_ts_const_assertion: JStaticMethodID,
  method_create_ts_enum_decl: JStaticMethodID,
  method_create_ts_enum_member: JStaticMethodID,
  method_create_ts_export_assignment: JStaticMethodID,
  method_create_ts_expr_with_type_args: JStaticMethodID,
  method_create_ts_external_module_ref: JStaticMethodID,
  method_create_ts_import_equals_decl: JStaticMethodID,
  method_create_ts_index_signature: JStaticMethodID,
  method_create_ts_instantiation: JStaticMethodID,
  method_create_ts_interface_body: JStaticMethodID,
  method_create_ts_interface_decl: JStaticMethodID,
  method_create_ts_module_decl: JStaticMethodID,
  method_create_ts_namespace_export_decl: JStaticMethodID,
  method_create_ts_non_null_expr: JStaticMethodID,
  method_create_ts_param_prop: JStaticMethodID,
  method_create_ts_qualified_name: JStaticMethodID,
  method_create_ts_satisfies_expr: JStaticMethodID,
  method_create_ts_type_alias_decl: JStaticMethodID,
  method_create_ts_type_ann: JStaticMethodID,
  method_create_ts_type_assertion: JStaticMethodID,
  method_create_ts_type_param: JStaticMethodID,
  method_create_ts_type_param_decl: JStaticMethodID,
  method_create_ts_type_param_instantiation: JStaticMethodID,
  method_create_unary_expr: JStaticMethodID,
  method_create_update_expr: JStaticMethodID,
  method_create_using_decl: JStaticMethodID,
  method_create_var_decl: JStaticMethodID,
  method_create_var_declarator: JStaticMethodID,
  method_create_while_stmt: JStaticMethodID,
  method_create_with_stmt: JStaticMethodID,
  method_create_yield_expr: JStaticMethodID,
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
    let method_create_array_lit = env
      .get_static_method_id(
        &class,
        "createArrayLit",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstArrayLit;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createArrayLit");
    let method_create_array_pat = env
      .get_static_method_id(
        &class,
        "createArrayPat",
        "(Ljava/util/List;ZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstArrayPat;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createArrayPat");
    let method_create_arrow_expr = env
      .get_static_method_id(
        &class,
        "createArrowExpr",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstBlockStmtOrExpr;ZZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstArrowExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createArrowExpr");
    let method_create_assign_expr = env
      .get_static_method_id(
        &class,
        "createAssignExpr",
        "(ILcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPatOrExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstAssignExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createAssignExpr");
    let method_create_assign_pat = env
      .get_static_method_id(
        &class,
        "createAssignPat",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstAssignPat;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createAssignPat");
    let method_create_assign_pat_prop = env
      .get_static_method_id(
        &class,
        "createAssignPatProp",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstAssignPatProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createAssignPatProp");
    let method_create_assign_prop = env
      .get_static_method_id(
        &class,
        "createAssignProp",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstAssignProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createAssignProp");
    let method_create_auto_accessor = env
      .get_static_method_id(
        &class,
        "createAutoAccessor",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstKey;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;ZLjava/util/List;ILcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstAutoAccessor;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createAutoAccessor");
    let method_create_await_expr = env
      .get_static_method_id(
        &class,
        "createAwaitExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstAwaitExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createAwaitExpr");
    let method_create_big_int = env
      .get_static_method_id(
        &class,
        "createBigInt",
        "(ILjava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstBigInt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBigInt");
    let method_create_bin_expr = env
      .get_static_method_id(
        &class,
        "createBinExpr",
        "(ILcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstBinExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBinExpr");
    let method_create_binding_ident = env
      .get_static_method_id(
        &class,
        "createBindingIdent",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstBindingIdent;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBindingIdent");
    let method_create_block_stmt = env
      .get_static_method_id(
        &class,
        "createBlockStmt",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBlockStmt");
    let method_create_bool = env
      .get_static_method_id(
        &class,
        "createBool",
        "(ZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstBool;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBool");
    let method_create_break_stmt = env
      .get_static_method_id(
        &class,
        "createBreakStmt",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBreakStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBreakStmt");
    let method_create_call_expr = env
      .get_static_method_id(
        &class,
        "createCallExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstCallee;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstCallExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createCallExpr");
    let method_create_catch_clause = env
      .get_static_method_id(
        &class,
        "createCatchClause",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstCatchClause;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createCatchClause");
    let method_create_class = env
      .get_static_method_id(
        &class,
        "createClass",
        "(Ljava/util/List;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;ZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstClass;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createClass");
    let method_create_class_decl = env
      .get_static_method_id(
        &class,
        "createClassDecl",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;ZLcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstClass;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstClassDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createClassDecl");
    let method_create_class_expr = env
      .get_static_method_id(
        &class,
        "createClassExpr",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstClass;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstClassExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createClassExpr");
    let method_create_class_method = env
      .get_static_method_id(
        &class,
        "createClassMethod",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstFunction;IZIZZZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstClassMethod;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createClassMethod");
    let method_create_class_prop = env
      .get_static_method_id(
        &class,
        "createClassProp",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;ZLjava/util/List;IZZZZZZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstClassProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createClassProp");
    let method_create_computed_prop_name = env
      .get_static_method_id(
        &class,
        "createComputedPropName",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstComputedPropName;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createComputedPropName");
    let method_create_cond_expr = env
      .get_static_method_id(
        &class,
        "createCondExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstCondExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createCondExpr");
    let method_create_constructor = env
      .get_static_method_id(
        &class,
        "createConstructor",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;IZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstConstructor;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createConstructor");
    let method_create_continue_stmt = env
      .get_static_method_id(
        &class,
        "createContinueStmt",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstContinueStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createContinueStmt");
    let method_create_debugger_stmt = env
      .get_static_method_id(
        &class,
        "createDebuggerStmt",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstDebuggerStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createDebuggerStmt");
    let method_create_decorator = env
      .get_static_method_id(
        &class,
        "createDecorator",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstDecorator;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createDecorator");
    let method_create_do_while_stmt = env
      .get_static_method_id(
        &class,
        "createDoWhileStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstDoWhileStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createDoWhileStmt");
    let method_create_empty_stmt = env
      .get_static_method_id(
        &class,
        "createEmptyStmt",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstEmptyStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createEmptyStmt");
    let method_create_export_all = env
      .get_static_method_id(
        &class,
        "createExportAll",
        "(Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;ZLcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstObjectLit;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportAll;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportAll");
    let method_create_export_decl = env
      .get_static_method_id(
        &class,
        "createExportDecl",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstDecl;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportDecl");
    let method_create_export_default_decl = env
      .get_static_method_id(
        &class,
        "createExportDefaultDecl",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstDefaultDecl;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportDefaultDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportDefaultDecl");
    let method_create_export_default_expr = env
      .get_static_method_id(
        &class,
        "createExportDefaultExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportDefaultExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportDefaultExpr");
    let method_create_export_default_specifier = env
      .get_static_method_id(
        &class,
        "createExportDefaultSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportDefaultSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportDefaultSpecifier");
    let method_create_export_named_specifier = env
      .get_static_method_id(
        &class,
        "createExportNamedSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstModuleExportName;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstModuleExportName;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportNamedSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportNamedSpecifier");
    let method_create_export_namespace_specifier = env
      .get_static_method_id(
        &class,
        "createExportNamespaceSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstModuleExportName;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportNamespaceSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportNamespaceSpecifier");
    let method_create_expr_or_spread = env
      .get_static_method_id(
        &class,
        "createExprOrSpread",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstExprOrSpread;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExprOrSpread");
    let method_create_expr_stmt = env
      .get_static_method_id(
        &class,
        "createExprStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstExprStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExprStmt");
    let method_create_fn_decl = env
      .get_static_method_id(
        &class,
        "createFnDecl",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;ZLcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstFunction;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstFnDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createFnDecl");
    let method_create_fn_expr = env
      .get_static_method_id(
        &class,
        "createFnExpr",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstFunction;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstFnExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createFnExpr");
    let method_create_for_in_stmt = env
      .get_static_method_id(
        &class,
        "createForInStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstForHead;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstForInStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createForInStmt");
    let method_create_for_of_stmt = env
      .get_static_method_id(
        &class,
        "createForOfStmt",
        "(ZLcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstForHead;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstForOfStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createForOfStmt");
    let method_create_for_stmt = env
      .get_static_method_id(
        &class,
        "createForStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstVarDeclOrExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstForStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createForStmt");
    let method_create_function = env
      .get_static_method_id(
        &class,
        "createFunction",
        "(Ljava/util/List;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;ZZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstFunction;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createFunction");
    let method_create_getter_prop = env
      .get_static_method_id(
        &class,
        "createGetterProp",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstGetterProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createGetterProp");
    let method_create_ident = env
      .get_static_method_id(
        &class,
        "createIdent",
        "(Ljava/lang/String;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createIdent");
    let method_create_if_stmt = env
      .get_static_method_id(
        &class,
        "createIfStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstIfStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createIfStmt");
    let method_create_import = env
      .get_static_method_id(
        &class,
        "createImport",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImport;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImport");
    let method_create_import_decl = env
      .get_static_method_id(
        &class,
        "createImportDecl",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;ZLcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstObjectLit;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportDecl");
    let method_create_import_default_specifier = env
      .get_static_method_id(
        &class,
        "createImportDefaultSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportDefaultSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportDefaultSpecifier");
    let method_create_import_named_specifier = env
      .get_static_method_id(
        &class,
        "createImportNamedSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstModuleExportName;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportNamedSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportNamedSpecifier");
    let method_create_import_star_as_specifier = env
      .get_static_method_id(
        &class,
        "createImportStarAsSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportStarAsSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportStarAsSpecifier");
    let method_create_invalid = env
      .get_static_method_id(
        &class,
        "createInvalid",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstInvalid;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createInvalid");
    let method_create_jsx_closing_element = env
      .get_static_method_id(
        &class,
        "createJsxClosingElement",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstJsxElementName;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxClosingElement;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxClosingElement");
    let method_create_jsx_closing_fragment = env
      .get_static_method_id(
        &class,
        "createJsxClosingFragment",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxClosingFragment;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxClosingFragment");
    let method_create_jsx_element = env
      .get_static_method_id(
        &class,
        "createJsxElement",
        "(Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxOpeningElement;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxClosingElement;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstJsxElement;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxElement");
    let method_create_jsx_empty_expr = env
      .get_static_method_id(
        &class,
        "createJsxEmptyExpr",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstJsxEmptyExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxEmptyExpr");
    let method_create_jsx_expr_container = env
      .get_static_method_id(
        &class,
        "createJsxExprContainer",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstJsxExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstJsxExprContainer;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxExprContainer");
    let method_create_jsx_fragment = env
      .get_static_method_id(
        &class,
        "createJsxFragment",
        "(Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxOpeningFragment;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxClosingFragment;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstJsxFragment;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxFragment");
    let method_create_jsx_member_expr = env
      .get_static_method_id(
        &class,
        "createJsxMemberExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstJsxObject;Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstJsxMemberExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxMemberExpr");
    let method_create_jsx_namespaced_name = env
      .get_static_method_id(
        &class,
        "createJsxNamespacedName",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstJsxNamespacedName;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxNamespacedName");
    let method_create_jsx_opening_element = env
      .get_static_method_id(
        &class,
        "createJsxOpeningElement",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstJsxElementName;Ljava/util/List;ZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxOpeningElement;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxOpeningElement");
    let method_create_jsx_opening_fragment = env
      .get_static_method_id(
        &class,
        "createJsxOpeningFragment",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstJsxOpeningFragment;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxOpeningFragment");
    let method_create_jsx_spread_child = env
      .get_static_method_id(
        &class,
        "createJsxSpreadChild",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstJsxSpreadChild;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxSpreadChild");
    let method_create_jsx_text = env
      .get_static_method_id(
        &class,
        "createJsxText",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstJsxText;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxText");
    let method_create_key_value_pat_prop = env
      .get_static_method_id(
        &class,
        "createKeyValuePatProp",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstKeyValuePatProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createKeyValuePatProp");
    let method_create_key_value_prop = env
      .get_static_method_id(
        &class,
        "createKeyValueProp",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstKeyValueProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createKeyValueProp");
    let method_create_labeled_stmt = env
      .get_static_method_id(
        &class,
        "createLabeledStmt",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstLabeledStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createLabeledStmt");
    let method_create_member_expr = env
      .get_static_method_id(
        &class,
        "createMemberExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstMemberProp;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstMemberExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createMemberExpr");
    let method_create_meta_prop_expr = env
      .get_static_method_id(
        &class,
        "createMetaPropExpr",
        "(ILcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstMetaPropExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createMetaPropExpr");
    let method_create_method_prop = env
      .get_static_method_id(
        &class,
        "createMethodProp",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstFunction;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstMethodProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createMethodProp");
    let method_create_module = env
      .get_static_method_id(
        &class,
        "createModule",
        "(Ljava/util/List;Ljava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstModule;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createModule");
    let method_create_named_export = env
      .get_static_method_id(
        &class,
        "createNamedExport",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;ZLcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstObjectLit;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstNamedExport;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createNamedExport");
    let method_create_new_expr = env
      .get_static_method_id(
        &class,
        "createNewExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstNewExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createNewExpr");
    let method_create_null = env
      .get_static_method_id(
        &class,
        "createNull",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstNull;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createNull");
    let method_create_number = env
      .get_static_method_id(
        &class,
        "createNumber",
        "(DLjava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstNumber;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createNumber");
    let method_create_object_lit = env
      .get_static_method_id(
        &class,
        "createObjectLit",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstObjectLit;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createObjectLit");
    let method_create_object_pat = env
      .get_static_method_id(
        &class,
        "createObjectPat",
        "(Ljava/util/List;ZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstObjectPat;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createObjectPat");
    let method_create_opt_call = env
      .get_static_method_id(
        &class,
        "createOptCall",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstOptCall;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createOptCall");
    let method_create_opt_chain_expr = env
      .get_static_method_id(
        &class,
        "createOptChainExpr",
        "(ZLcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstOptChainBase;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstOptChainExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createOptChainExpr");
    let method_create_param = env
      .get_static_method_id(
        &class,
        "createParam",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstParam;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createParam");
    let method_create_paren_expr = env
      .get_static_method_id(
        &class,
        "createParenExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstParenExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createParenExpr");
    let method_create_private_method = env
      .get_static_method_id(
        &class,
        "createPrivateMethod",
        "(Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstPrivateName;Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstFunction;IZIZZZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstPrivateMethod;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createPrivateMethod");
    let method_create_private_name = env
      .get_static_method_id(
        &class,
        "createPrivateName",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstPrivateName;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createPrivateName");
    let method_create_private_prop = env
      .get_static_method_id(
        &class,
        "createPrivateProp",
        "(Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstPrivateName;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;ZLjava/util/List;IZZZZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstPrivateProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createPrivateProp");
    let method_create_regex = env
      .get_static_method_id(
        &class,
        "createRegex",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstRegex;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createRegex");
    let method_create_rest_pat = env
      .get_static_method_id(
        &class,
        "createRestPat",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstRestPat;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createRestPat");
    let method_create_return_stmt = env
      .get_static_method_id(
        &class,
        "createReturnStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstReturnStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createReturnStmt");
    let method_create_script = env
      .get_static_method_id(
        &class,
        "createScript",
        "(Ljava/util/List;Ljava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstScript;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createScript");
    let method_create_seq_expr = env
      .get_static_method_id(
        &class,
        "createSeqExpr",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstSeqExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSeqExpr");
    let method_create_setter_prop = env
      .get_static_method_id(
        &class,
        "createSetterProp",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstSetterProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSetterProp");
    let method_create_span = env
      .get_static_method_id(
        &class,
        "createSpan",
        "(II)Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSpan");
    let method_create_spread_element = env
      .get_static_method_id(
        &class,
        "createSpreadElement",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstSpreadElement;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSpreadElement");
    let method_create_static_block = env
      .get_static_method_id(
        &class,
        "createStaticBlock",
        "(Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstStaticBlock;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createStaticBlock");
    let method_create_str = env
      .get_static_method_id(
        &class,
        "createStr",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createStr");
    let method_create_super = env
      .get_static_method_id(
        &class,
        "createSuper",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstSuper;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSuper");
    let method_create_super_prop_expr = env
      .get_static_method_id(
        &class,
        "createSuperPropExpr",
        "(Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstSuper;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstSuperProp;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstSuperPropExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSuperPropExpr");
    let method_create_switch_case = env
      .get_static_method_id(
        &class,
        "createSwitchCase",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstSwitchCase;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSwitchCase");
    let method_create_switch_stmt = env
      .get_static_method_id(
        &class,
        "createSwitchStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstSwitchStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSwitchStmt");
    let method_create_tagged_tpl = env
      .get_static_method_id(
        &class,
        "createTaggedTpl",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTpl;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTaggedTpl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTaggedTpl");
    let method_create_this_expr = env
      .get_static_method_id(
        &class,
        "createThisExpr",
        "(Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstThisExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createThisExpr");
    let method_create_throw_stmt = env
      .get_static_method_id(
        &class,
        "createThrowStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstThrowStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createThrowStmt");
    let method_create_tpl = env
      .get_static_method_id(
        &class,
        "createTpl",
        "(Ljava/util/List;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTpl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTpl");
    let method_create_tpl_element = env
      .get_static_method_id(
        &class,
        "createTplElement",
        "(ZLjava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstTplElement;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTplElement");
    let method_create_try_stmt = env
      .get_static_method_id(
        &class,
        "createTryStmt",
        "(Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;Lcom/caoccao/javet/swc4j/ast/miscs/Swc4jAstCatchClause;Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstTryStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTryStmt");
    let method_create_ts_as_expr = env
      .get_static_method_id(
        &class,
        "createTsAsExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTsAsExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsAsExpr");
    let method_create_ts_const_assertion = env
      .get_static_method_id(
        &class,
        "createTsConstAssertion",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTsConstAssertion;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsConstAssertion");
    let method_create_ts_enum_decl = env
      .get_static_method_id(
        &class,
        "createTsEnumDecl",
        "(ZZLcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstTsEnumDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsEnumDecl");
    let method_create_ts_enum_member = env
      .get_static_method_id(
        &class,
        "createTsEnumMember",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsEnumMemberId;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsEnumMember;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsEnumMember");
    let method_create_ts_export_assignment = env
      .get_static_method_id(
        &class,
        "createTsExportAssignment",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsExportAssignment;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsExportAssignment");
    let method_create_ts_expr_with_type_args = env
      .get_static_method_id(
        &class,
        "createTsExprWithTypeArgs",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsExprWithTypeArgs;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsExprWithTypeArgs");
    let method_create_ts_external_module_ref = env
      .get_static_method_id(
        &class,
        "createTsExternalModuleRef",
        "(Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsExternalModuleRef;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsExternalModuleRef");
    let method_create_ts_import_equals_decl = env
      .get_static_method_id(
        &class,
        "createTsImportEqualsDecl",
        "(ZZLcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsModuleRef;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsImportEqualsDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsImportEqualsDecl");
    let method_create_ts_index_signature = env
      .get_static_method_id(
        &class,
        "createTsIndexSignature",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;ZZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstTsIndexSignature;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsIndexSignature");
    let method_create_ts_instantiation = env
      .get_static_method_id(
        &class,
        "createTsInstantiation",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTsInstantiation;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsInstantiation");
    let method_create_ts_interface_body = env
      .get_static_method_id(
        &class,
        "createTsInterfaceBody",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsInterfaceBody;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsInterfaceBody");
    let method_create_ts_interface_decl = env
      .get_static_method_id(
        &class,
        "createTsInterfaceDecl",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;ZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsInterfaceBody;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstTsInterfaceDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsInterfaceDecl");
    let method_create_ts_module_decl = env
      .get_static_method_id(
        &class,
        "createTsModuleDecl",
        "(ZZLcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsModuleName;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsNamespaceBody;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstTsModuleDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsModuleDecl");
    let method_create_ts_namespace_export_decl = env
      .get_static_method_id(
        &class,
        "createTsNamespaceExportDecl",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsNamespaceExportDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsNamespaceExportDecl");
    let method_create_ts_non_null_expr = env
      .get_static_method_id(
        &class,
        "createTsNonNullExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTsNonNullExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsNonNullExpr");
    let method_create_ts_param_prop = env
      .get_static_method_id(
        &class,
        "createTsParamProp",
        "(Ljava/util/List;IZZLcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsParamPropParam;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsParamProp;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsParamProp");
    let method_create_ts_qualified_name = env
      .get_static_method_id(
        &class,
        "createTsQualifiedName",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsEntityName;Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsQualifiedName;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsQualifiedName");
    let method_create_ts_satisfies_expr = env
      .get_static_method_id(
        &class,
        "createTsSatisfiesExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTsSatisfiesExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsSatisfiesExpr");
    let method_create_ts_type_alias_decl = env
      .get_static_method_id(
        &class,
        "createTsTypeAliasDecl",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;ZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstTsTypeAliasDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeAliasDecl");
    let method_create_ts_type_ann = env
      .get_static_method_id(
        &class,
        "createTsTypeAnn",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeAnn");
    let method_create_ts_type_assertion = env
      .get_static_method_id(
        &class,
        "createTsTypeAssertion",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstTsTypeAssertion;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeAssertion");
    let method_create_ts_type_param = env
      .get_static_method_id(
        &class,
        "createTsTypeParam",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;ZZZLcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParam;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeParam");
    let method_create_ts_type_param_decl = env
      .get_static_method_id(
        &class,
        "createTsTypeParamDecl",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeParamDecl");
    let method_create_ts_type_param_instantiation = env
      .get_static_method_id(
        &class,
        "createTsTypeParamInstantiation",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeParamInstantiation");
    let method_create_unary_expr = env
      .get_static_method_id(
        &class,
        "createUnaryExpr",
        "(ILcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstUnaryExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createUnaryExpr");
    let method_create_update_expr = env
      .get_static_method_id(
        &class,
        "createUpdateExpr",
        "(IZLcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstUpdateExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createUpdateExpr");
    let method_create_using_decl = env
      .get_static_method_id(
        &class,
        "createUsingDecl",
        "(ZLjava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstUsingDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createUsingDecl");
    let method_create_var_decl = env
      .get_static_method_id(
        &class,
        "createVarDecl",
        "(IZLjava/util/List;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstVarDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createVarDecl");
    let method_create_var_declarator = env
      .get_static_method_id(
        &class,
        "createVarDeclarator",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstVarDeclarator;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createVarDeclarator");
    let method_create_while_stmt = env
      .get_static_method_id(
        &class,
        "createWhileStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstWhileStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createWhileStmt");
    let method_create_with_stmt = env
      .get_static_method_id(
        &class,
        "createWithStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstStmt;Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstWithStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createWithStmt");
    let method_create_yield_expr = env
      .get_static_method_id(
        &class,
        "createYieldExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstSpan;)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstYieldExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createYieldExpr");
    JavaSwc4jAstFactory {
      class,
      method_create_array_lit,
      method_create_array_pat,
      method_create_arrow_expr,
      method_create_assign_expr,
      method_create_assign_pat,
      method_create_assign_pat_prop,
      method_create_assign_prop,
      method_create_auto_accessor,
      method_create_await_expr,
      method_create_big_int,
      method_create_bin_expr,
      method_create_binding_ident,
      method_create_block_stmt,
      method_create_bool,
      method_create_break_stmt,
      method_create_call_expr,
      method_create_catch_clause,
      method_create_class,
      method_create_class_decl,
      method_create_class_expr,
      method_create_class_method,
      method_create_class_prop,
      method_create_computed_prop_name,
      method_create_cond_expr,
      method_create_constructor,
      method_create_continue_stmt,
      method_create_debugger_stmt,
      method_create_decorator,
      method_create_do_while_stmt,
      method_create_empty_stmt,
      method_create_export_all,
      method_create_export_decl,
      method_create_export_default_decl,
      method_create_export_default_expr,
      method_create_export_default_specifier,
      method_create_export_named_specifier,
      method_create_export_namespace_specifier,
      method_create_expr_or_spread,
      method_create_expr_stmt,
      method_create_fn_decl,
      method_create_fn_expr,
      method_create_for_in_stmt,
      method_create_for_of_stmt,
      method_create_for_stmt,
      method_create_function,
      method_create_getter_prop,
      method_create_ident,
      method_create_if_stmt,
      method_create_import,
      method_create_import_decl,
      method_create_import_default_specifier,
      method_create_import_named_specifier,
      method_create_import_star_as_specifier,
      method_create_invalid,
      method_create_jsx_closing_element,
      method_create_jsx_closing_fragment,
      method_create_jsx_element,
      method_create_jsx_empty_expr,
      method_create_jsx_expr_container,
      method_create_jsx_fragment,
      method_create_jsx_member_expr,
      method_create_jsx_namespaced_name,
      method_create_jsx_opening_element,
      method_create_jsx_opening_fragment,
      method_create_jsx_spread_child,
      method_create_jsx_text,
      method_create_key_value_pat_prop,
      method_create_key_value_prop,
      method_create_labeled_stmt,
      method_create_member_expr,
      method_create_meta_prop_expr,
      method_create_method_prop,
      method_create_module,
      method_create_named_export,
      method_create_new_expr,
      method_create_null,
      method_create_number,
      method_create_object_lit,
      method_create_object_pat,
      method_create_opt_call,
      method_create_opt_chain_expr,
      method_create_param,
      method_create_paren_expr,
      method_create_private_method,
      method_create_private_name,
      method_create_private_prop,
      method_create_regex,
      method_create_rest_pat,
      method_create_return_stmt,
      method_create_script,
      method_create_seq_expr,
      method_create_setter_prop,
      method_create_span,
      method_create_spread_element,
      method_create_static_block,
      method_create_str,
      method_create_super,
      method_create_super_prop_expr,
      method_create_switch_case,
      method_create_switch_stmt,
      method_create_tagged_tpl,
      method_create_this_expr,
      method_create_throw_stmt,
      method_create_tpl,
      method_create_tpl_element,
      method_create_try_stmt,
      method_create_ts_as_expr,
      method_create_ts_const_assertion,
      method_create_ts_enum_decl,
      method_create_ts_enum_member,
      method_create_ts_export_assignment,
      method_create_ts_expr_with_type_args,
      method_create_ts_external_module_ref,
      method_create_ts_import_equals_decl,
      method_create_ts_index_signature,
      method_create_ts_instantiation,
      method_create_ts_interface_body,
      method_create_ts_interface_decl,
      method_create_ts_module_decl,
      method_create_ts_namespace_export_decl,
      method_create_ts_non_null_expr,
      method_create_ts_param_prop,
      method_create_ts_qualified_name,
      method_create_ts_satisfies_expr,
      method_create_ts_type_alias_decl,
      method_create_ts_type_ann,
      method_create_ts_type_assertion,
      method_create_ts_type_param,
      method_create_ts_type_param_decl,
      method_create_ts_type_param_instantiation,
      method_create_unary_expr,
      method_create_update_expr,
      method_create_using_decl,
      method_create_var_decl,
      method_create_var_declarator,
      method_create_while_stmt,
      method_create_with_stmt,
      method_create_yield_expr,
    }
  }

  pub fn create_array_lit<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    elems: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let elems = object_to_jvalue!(elems);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_array_lit,
        &[elems, span],
        "Swc4jAstArrayLit create_array_lit()"
      );
    return_value
  }

  pub fn create_array_pat<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    elems: &JObject<'_>,
    optional: bool,
    type_ann: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let elems = object_to_jvalue!(elems);
    let optional = boolean_to_jvalue!(optional);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_array_pat,
        &[elems, optional, type_ann, span],
        "Swc4jAstArrayPat create_array_pat()"
      );
    return_value
  }

  pub fn create_arrow_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    params: &JObject<'_>,
    body: &JObject<'_>,
    is_async: bool,
    generator: bool,
    type_params: &Option<JObject>,
    return_type: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let params = object_to_jvalue!(params);
    let body = object_to_jvalue!(body);
    let is_async = boolean_to_jvalue!(is_async);
    let generator = boolean_to_jvalue!(generator);
    let type_params = optional_object_to_jvalue!(type_params);
    let return_type = optional_object_to_jvalue!(return_type);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_arrow_expr,
        &[params, body, is_async, generator, type_params, return_type, span],
        "Swc4jAstArrowExpr create_arrow_expr()"
      );
    return_value
  }

  pub fn create_assign_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    op: i32,
    left: &JObject<'_>,
    right: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let op = int_to_jvalue!(op);
    let left = object_to_jvalue!(left);
    let right = object_to_jvalue!(right);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_assign_expr,
        &[op, left, right, span],
        "Swc4jAstAssignExpr create_assign_expr()"
      );
    return_value
  }

  pub fn create_assign_pat<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    left: &JObject<'_>,
    right: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let left = object_to_jvalue!(left);
    let right = object_to_jvalue!(right);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_assign_pat,
        &[left, right, span],
        "Swc4jAstAssignPat create_assign_pat()"
      );
    return_value
  }

  pub fn create_assign_pat_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    value: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let value = optional_object_to_jvalue!(value);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_assign_pat_prop,
        &[key, value, span],
        "Swc4jAstAssignPatProp create_assign_pat_prop()"
      );
    return_value
  }

  pub fn create_assign_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    value: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let value = object_to_jvalue!(value);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_assign_prop,
        &[key, value, span],
        "Swc4jAstAssignProp create_assign_prop()"
      );
    return_value
  }

  pub fn create_auto_accessor<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    value: &Option<JObject>,
    type_ann: &Option<JObject>,
    is_static: bool,
    decorators: &JObject<'_>,
    accessibility_id: i32,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let value = optional_object_to_jvalue!(value);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let is_static = boolean_to_jvalue!(is_static);
    let decorators = object_to_jvalue!(decorators);
    let accessibility_id = int_to_jvalue!(accessibility_id);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_auto_accessor,
        &[key, value, type_ann, is_static, decorators, accessibility_id, span],
        "Swc4jAstAutoAccessor create_auto_accessor()"
      );
    return_value
  }

  pub fn create_await_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    arg: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let arg = object_to_jvalue!(arg);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_await_expr,
        &[arg, span],
        "Swc4jAstAwaitExpr create_await_expr()"
      );
    return_value
  }

  pub fn create_big_int<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    sign: i32,
    raw: &Option<String>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let sign = int_to_jvalue!(sign);
    let java_raw = optional_string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_big_int,
        &[sign, raw, span],
        "Swc4jAstBigInt create_big_int()"
      );
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_bin_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    op: i32,
    left: &JObject<'_>,
    right: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let op = int_to_jvalue!(op);
    let left = object_to_jvalue!(left);
    let right = object_to_jvalue!(right);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_bin_expr,
        &[op, left, right, span],
        "Swc4jAstBinExpr create_bin_expr()"
      );
    return_value
  }

  pub fn create_binding_ident<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    type_ann: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_binding_ident,
        &[id, type_ann, span],
        "Swc4jAstBindingIdent create_binding_ident()"
      );
    return_value
  }

  pub fn create_block_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    stmts: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let stmts = object_to_jvalue!(stmts);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_block_stmt,
        &[stmts, span],
        "Swc4jAstBlockStmt create_block_stmt()"
      );
    return_value
  }

  pub fn create_bool<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let value = boolean_to_jvalue!(value);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_bool,
        &[value, span],
        "Swc4jAstBool create_bool()"
      );
    return_value
  }

  pub fn create_break_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    label: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let label = optional_object_to_jvalue!(label);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_break_stmt,
        &[label, span],
        "Swc4jAstBreakStmt create_break_stmt()"
      );
    return_value
  }

  pub fn create_call_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    callee: &JObject<'_>,
    args: &JObject<'_>,
    type_args: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let callee = object_to_jvalue!(callee);
    let args = object_to_jvalue!(args);
    let type_args = optional_object_to_jvalue!(type_args);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_call_expr,
        &[callee, args, type_args, span],
        "Swc4jAstCallExpr create_call_expr()"
      );
    return_value
  }

  pub fn create_catch_clause<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    param: &Option<JObject>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let param = optional_object_to_jvalue!(param);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_catch_clause,
        &[param, body, span],
        "Swc4jAstCatchClause create_catch_clause()"
      );
    return_value
  }

  pub fn create_class<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decorators: &JObject<'_>,
    body: &JObject<'_>,
    super_class: &Option<JObject>,
    is_abstract: bool,
    type_params: &Option<JObject>,
    super_type_params: &Option<JObject>,
    implements: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decorators = object_to_jvalue!(decorators);
    let body = object_to_jvalue!(body);
    let super_class = optional_object_to_jvalue!(super_class);
    let is_abstract = boolean_to_jvalue!(is_abstract);
    let type_params = optional_object_to_jvalue!(type_params);
    let super_type_params = optional_object_to_jvalue!(super_type_params);
    let implements = object_to_jvalue!(implements);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_class,
        &[decorators, body, super_class, is_abstract, type_params, super_type_params, implements, span],
        "Swc4jAstClass create_class()"
      );
    return_value
  }

  pub fn create_class_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ident: &JObject<'_>,
    declare: bool,
    clazz: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let ident = object_to_jvalue!(ident);
    let declare = boolean_to_jvalue!(declare);
    let clazz = object_to_jvalue!(clazz);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_class_decl,
        &[ident, declare, clazz, span],
        "Swc4jAstClassDecl create_class_decl()"
      );
    return_value
  }

  pub fn create_class_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ident: &Option<JObject>,
    clazz: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let ident = optional_object_to_jvalue!(ident);
    let clazz = object_to_jvalue!(clazz);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_class_expr,
        &[ident, clazz, span],
        "Swc4jAstClassExpr create_class_expr()"
      );
    return_value
  }

  pub fn create_class_method<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    function: &JObject<'_>,
    kind: i32,
    is_static: bool,
    accessibility_id: i32,
    is_abstract: bool,
    optional: bool,
    is_override: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let function = object_to_jvalue!(function);
    let kind = int_to_jvalue!(kind);
    let is_static = boolean_to_jvalue!(is_static);
    let accessibility_id = int_to_jvalue!(accessibility_id);
    let is_abstract = boolean_to_jvalue!(is_abstract);
    let optional = boolean_to_jvalue!(optional);
    let is_override = boolean_to_jvalue!(is_override);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_class_method,
        &[key, function, kind, is_static, accessibility_id, is_abstract, optional, is_override, span],
        "Swc4jAstClassMethod create_class_method()"
      );
    return_value
  }

  pub fn create_class_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    value: &Option<JObject>,
    type_ann: &Option<JObject>,
    is_static: bool,
    decorators: &JObject<'_>,
    accessibility_id: i32,
    is_abstract: bool,
    is_optional: bool,
    is_override: bool,
    readonly: bool,
    declare: bool,
    definite: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let value = optional_object_to_jvalue!(value);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let is_static = boolean_to_jvalue!(is_static);
    let decorators = object_to_jvalue!(decorators);
    let accessibility_id = int_to_jvalue!(accessibility_id);
    let is_abstract = boolean_to_jvalue!(is_abstract);
    let is_optional = boolean_to_jvalue!(is_optional);
    let is_override = boolean_to_jvalue!(is_override);
    let readonly = boolean_to_jvalue!(readonly);
    let declare = boolean_to_jvalue!(declare);
    let definite = boolean_to_jvalue!(definite);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_class_prop,
        &[key, value, type_ann, is_static, decorators, accessibility_id, is_abstract, is_optional, is_override, readonly, declare, definite, span],
        "Swc4jAstClassProp create_class_prop()"
      );
    return_value
  }

  pub fn create_computed_prop_name<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_computed_prop_name,
        &[expr, span],
        "Swc4jAstComputedPropName create_computed_prop_name()"
      );
    return_value
  }

  pub fn create_cond_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    test: &JObject<'_>,
    cons: &JObject<'_>,
    alt: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let test = object_to_jvalue!(test);
    let cons = object_to_jvalue!(cons);
    let alt = object_to_jvalue!(alt);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_cond_expr,
        &[test, cons, alt, span],
        "Swc4jAstCondExpr create_cond_expr()"
      );
    return_value
  }

  pub fn create_constructor<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    params: &JObject<'_>,
    body: &Option<JObject>,
    accessibility_id: i32,
    optional: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let params = object_to_jvalue!(params);
    let body = optional_object_to_jvalue!(body);
    let accessibility_id = int_to_jvalue!(accessibility_id);
    let optional = boolean_to_jvalue!(optional);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_constructor,
        &[key, params, body, accessibility_id, optional, span],
        "Swc4jAstConstructor create_constructor()"
      );
    return_value
  }

  pub fn create_continue_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    label: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let label = optional_object_to_jvalue!(label);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_continue_stmt,
        &[label, span],
        "Swc4jAstContinueStmt create_continue_stmt()"
      );
    return_value
  }

  pub fn create_debugger_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_debugger_stmt,
        &[span],
        "Swc4jAstDebuggerStmt create_debugger_stmt()"
      );
    return_value
  }

  pub fn create_decorator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_decorator,
        &[expr, span],
        "Swc4jAstDecorator create_decorator()"
      );
    return_value
  }

  pub fn create_do_while_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    test: &JObject<'_>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let test = object_to_jvalue!(test);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_do_while_stmt,
        &[test, body, span],
        "Swc4jAstDoWhileStmt create_do_while_stmt()"
      );
    return_value
  }

  pub fn create_empty_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_empty_stmt,
        &[span],
        "Swc4jAstEmptyStmt create_empty_stmt()"
      );
    return_value
  }

  pub fn create_export_all<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    src: &JObject<'_>,
    type_only: bool,
    with: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let src = object_to_jvalue!(src);
    let type_only = boolean_to_jvalue!(type_only);
    let with = optional_object_to_jvalue!(with);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_all,
        &[src, type_only, with, span],
        "Swc4jAstExportAll create_export_all()"
      );
    return_value
  }

  pub fn create_export_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_decl,
        &[decl, span],
        "Swc4jAstExportDecl create_export_decl()"
      );
    return_value
  }

  pub fn create_export_default_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_default_decl,
        &[decl, span],
        "Swc4jAstExportDefaultDecl create_export_default_decl()"
      );
    return_value
  }

  pub fn create_export_default_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_default_expr,
        &[decl, span],
        "Swc4jAstExportDefaultExpr create_export_default_expr()"
      );
    return_value
  }

  pub fn create_export_default_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    exported: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let exported = object_to_jvalue!(exported);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_default_specifier,
        &[exported, span],
        "Swc4jAstExportDefaultSpecifier create_export_default_specifier()"
      );
    return_value
  }

  pub fn create_export_named_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    orig: &JObject<'_>,
    exported: &Option<JObject>,
    type_only: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let orig = object_to_jvalue!(orig);
    let exported = optional_object_to_jvalue!(exported);
    let type_only = boolean_to_jvalue!(type_only);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_named_specifier,
        &[orig, exported, type_only, span],
        "Swc4jAstExportNamedSpecifier create_export_named_specifier()"
      );
    return_value
  }

  pub fn create_export_namespace_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    name: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let name = object_to_jvalue!(name);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_namespace_specifier,
        &[name, span],
        "Swc4jAstExportNamespaceSpecifier create_export_namespace_specifier()"
      );
    return_value
  }

  pub fn create_expr_or_spread<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    spread: &Option<JObject>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let spread = optional_object_to_jvalue!(spread);
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_expr_or_spread,
        &[spread, expr, span],
        "Swc4jAstExprOrSpread create_expr_or_spread()"
      );
    return_value
  }

  pub fn create_expr_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_expr_stmt,
        &[expr, span],
        "Swc4jAstExprStmt create_expr_stmt()"
      );
    return_value
  }

  pub fn create_fn_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ident: &JObject<'_>,
    declare: bool,
    function: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let ident = object_to_jvalue!(ident);
    let declare = boolean_to_jvalue!(declare);
    let function = object_to_jvalue!(function);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_fn_decl,
        &[ident, declare, function, span],
        "Swc4jAstFnDecl create_fn_decl()"
      );
    return_value
  }

  pub fn create_fn_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ident: &Option<JObject>,
    function: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let ident = optional_object_to_jvalue!(ident);
    let function = object_to_jvalue!(function);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_fn_expr,
        &[ident, function, span],
        "Swc4jAstFnExpr create_fn_expr()"
      );
    return_value
  }

  pub fn create_for_in_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    left: &JObject<'_>,
    right: &JObject<'_>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let left = object_to_jvalue!(left);
    let right = object_to_jvalue!(right);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_for_in_stmt,
        &[left, right, body, span],
        "Swc4jAstForInStmt create_for_in_stmt()"
      );
    return_value
  }

  pub fn create_for_of_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    is_await: bool,
    left: &JObject<'_>,
    right: &JObject<'_>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let is_await = boolean_to_jvalue!(is_await);
    let left = object_to_jvalue!(left);
    let right = object_to_jvalue!(right);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_for_of_stmt,
        &[is_await, left, right, body, span],
        "Swc4jAstForOfStmt create_for_of_stmt()"
      );
    return_value
  }

  pub fn create_for_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    init: &Option<JObject>,
    test: &Option<JObject>,
    update: &Option<JObject>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let init = optional_object_to_jvalue!(init);
    let test = optional_object_to_jvalue!(test);
    let update = optional_object_to_jvalue!(update);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_for_stmt,
        &[init, test, update, body, span],
        "Swc4jAstForStmt create_for_stmt()"
      );
    return_value
  }

  pub fn create_function<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    params: &JObject<'_>,
    decorators: &JObject<'_>,
    body: &Option<JObject>,
    generator: bool,
    is_async: bool,
    type_params: &Option<JObject>,
    return_type: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let params = object_to_jvalue!(params);
    let decorators = object_to_jvalue!(decorators);
    let body = optional_object_to_jvalue!(body);
    let generator = boolean_to_jvalue!(generator);
    let is_async = boolean_to_jvalue!(is_async);
    let type_params = optional_object_to_jvalue!(type_params);
    let return_type = optional_object_to_jvalue!(return_type);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_function,
        &[params, decorators, body, generator, is_async, type_params, return_type, span],
        "Swc4jAstFunction create_function()"
      );
    return_value
  }

  pub fn create_getter_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    type_ann: &Option<JObject>,
    body: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let body = optional_object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_getter_prop,
        &[key, type_ann, body, span],
        "Swc4jAstGetterProp create_getter_prop()"
      );
    return_value
  }

  pub fn create_ident<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    sym: &str,
    optional: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_sym = string_to_jstring!(env, &sym);
    let sym = object_to_jvalue!(java_sym);
    let optional = boolean_to_jvalue!(optional);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ident,
        &[sym, optional, span],
        "Swc4jAstIdent create_ident()"
      );
    delete_local_ref!(env, java_sym);
    return_value
  }

  pub fn create_if_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    test: &JObject<'_>,
    cons: &JObject<'_>,
    alt: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let test = object_to_jvalue!(test);
    let cons = object_to_jvalue!(cons);
    let alt = optional_object_to_jvalue!(alt);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_if_stmt,
        &[test, cons, alt, span],
        "Swc4jAstIfStmt create_if_stmt()"
      );
    return_value
  }

  pub fn create_import<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_import,
        &[span],
        "Swc4jAstImport create_import()"
      );
    return_value
  }

  pub fn create_import_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    specifiers: &JObject<'_>,
    src: &JObject<'_>,
    type_only: bool,
    with: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let specifiers = object_to_jvalue!(specifiers);
    let src = object_to_jvalue!(src);
    let type_only = boolean_to_jvalue!(type_only);
    let with = optional_object_to_jvalue!(with);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_decl,
        &[specifiers, src, type_only, with, span],
        "Swc4jAstImportDecl create_import_decl()"
      );
    return_value
  }

  pub fn create_import_default_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    local: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let local = object_to_jvalue!(local);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_default_specifier,
        &[local, span],
        "Swc4jAstImportDefaultSpecifier create_import_default_specifier()"
      );
    return_value
  }

  pub fn create_import_named_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    local: &JObject<'_>,
    imported: &Option<JObject>,
    type_only: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let local = object_to_jvalue!(local);
    let imported = optional_object_to_jvalue!(imported);
    let type_only = boolean_to_jvalue!(type_only);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_named_specifier,
        &[local, imported, type_only, span],
        "Swc4jAstImportNamedSpecifier create_import_named_specifier()"
      );
    return_value
  }

  pub fn create_import_star_as_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    local: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let local = object_to_jvalue!(local);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_star_as_specifier,
        &[local, span],
        "Swc4jAstImportStarAsSpecifier create_import_star_as_specifier()"
      );
    return_value
  }

  pub fn create_invalid<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_invalid,
        &[span],
        "Swc4jAstInvalid create_invalid()"
      );
    return_value
  }

  pub fn create_jsx_closing_element<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    name: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let name = object_to_jvalue!(name);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_closing_element,
        &[name, span],
        "Swc4jAstJsxClosingElement create_jsx_closing_element()"
      );
    return_value
  }

  pub fn create_jsx_closing_fragment<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_closing_fragment,
        &[span],
        "Swc4jAstJsxClosingFragment create_jsx_closing_fragment()"
      );
    return_value
  }

  pub fn create_jsx_element<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    opening: &JObject<'_>,
    children: &JObject<'_>,
    closing: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let opening = object_to_jvalue!(opening);
    let children = object_to_jvalue!(children);
    let closing = optional_object_to_jvalue!(closing);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_element,
        &[opening, children, closing, span],
        "Swc4jAstJsxElement create_jsx_element()"
      );
    return_value
  }

  pub fn create_jsx_empty_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_empty_expr,
        &[span],
        "Swc4jAstJsxEmptyExpr create_jsx_empty_expr()"
      );
    return_value
  }

  pub fn create_jsx_expr_container<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_expr_container,
        &[expr, span],
        "Swc4jAstJsxExprContainer create_jsx_expr_container()"
      );
    return_value
  }

  pub fn create_jsx_fragment<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    opening: &JObject<'_>,
    children: &JObject<'_>,
    closing: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let opening = object_to_jvalue!(opening);
    let children = object_to_jvalue!(children);
    let closing = object_to_jvalue!(closing);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_fragment,
        &[opening, children, closing, span],
        "Swc4jAstJsxFragment create_jsx_fragment()"
      );
    return_value
  }

  pub fn create_jsx_member_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
    prop: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let obj = object_to_jvalue!(obj);
    let prop = object_to_jvalue!(prop);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_member_expr,
        &[obj, prop, span],
        "Swc4jAstJsxMemberExpr create_jsx_member_expr()"
      );
    return_value
  }

  pub fn create_jsx_namespaced_name<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ns: &JObject<'_>,
    name: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let ns = object_to_jvalue!(ns);
    let name = object_to_jvalue!(name);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_namespaced_name,
        &[ns, name, span],
        "Swc4jAstJsxNamespacedName create_jsx_namespaced_name()"
      );
    return_value
  }

  pub fn create_jsx_opening_element<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    name: &JObject<'_>,
    attrs: &JObject<'_>,
    self_closing: bool,
    type_args: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let name = object_to_jvalue!(name);
    let attrs = object_to_jvalue!(attrs);
    let self_closing = boolean_to_jvalue!(self_closing);
    let type_args = optional_object_to_jvalue!(type_args);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_opening_element,
        &[name, attrs, self_closing, type_args, span],
        "Swc4jAstJsxOpeningElement create_jsx_opening_element()"
      );
    return_value
  }

  pub fn create_jsx_opening_fragment<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_opening_fragment,
        &[span],
        "Swc4jAstJsxOpeningFragment create_jsx_opening_fragment()"
      );
    return_value
  }

  pub fn create_jsx_spread_child<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_spread_child,
        &[expr, span],
        "Swc4jAstJsxSpreadChild create_jsx_spread_child()"
      );
    return_value
  }

  pub fn create_jsx_text<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: &str,
    raw: &str,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let java_raw = string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_text,
        &[value, raw, span],
        "Swc4jAstJsxText create_jsx_text()"
      );
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_key_value_pat_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    value: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let value = object_to_jvalue!(value);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_key_value_pat_prop,
        &[key, value, span],
        "Swc4jAstKeyValuePatProp create_key_value_pat_prop()"
      );
    return_value
  }

  pub fn create_key_value_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    value: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let value = object_to_jvalue!(value);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_key_value_prop,
        &[key, value, span],
        "Swc4jAstKeyValueProp create_key_value_prop()"
      );
    return_value
  }

  pub fn create_labeled_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    label: &JObject<'_>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let label = object_to_jvalue!(label);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_labeled_stmt,
        &[label, body, span],
        "Swc4jAstLabeledStmt create_labeled_stmt()"
      );
    return_value
  }

  pub fn create_member_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
    prop: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let obj = object_to_jvalue!(obj);
    let prop = object_to_jvalue!(prop);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_member_expr,
        &[obj, prop, span],
        "Swc4jAstMemberExpr create_member_expr()"
      );
    return_value
  }

  pub fn create_meta_prop_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    kind: i32,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let kind = int_to_jvalue!(kind);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_meta_prop_expr,
        &[kind, span],
        "Swc4jAstMetaPropExpr create_meta_prop_expr()"
      );
    return_value
  }

  pub fn create_method_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    function: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let function = object_to_jvalue!(function);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_method_prop,
        &[key, function, span],
        "Swc4jAstMethodProp create_method_prop()"
      );
    return_value
  }

  pub fn create_module<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    shebang: &Option<String>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let body = object_to_jvalue!(body);
    let java_shebang = optional_string_to_jstring!(env, &shebang);
    let shebang = object_to_jvalue!(java_shebang);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_module,
        &[body, shebang, span],
        "Swc4jAstModule create_module()"
      );
    delete_local_ref!(env, java_shebang);
    return_value
  }

  pub fn create_named_export<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    specifiers: &JObject<'_>,
    src: &Option<JObject>,
    type_only: bool,
    with: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let specifiers = object_to_jvalue!(specifiers);
    let src = optional_object_to_jvalue!(src);
    let type_only = boolean_to_jvalue!(type_only);
    let with = optional_object_to_jvalue!(with);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_named_export,
        &[specifiers, src, type_only, with, span],
        "Swc4jAstNamedExport create_named_export()"
      );
    return_value
  }

  pub fn create_new_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    callee: &JObject<'_>,
    args: &Option<JObject>,
    type_args: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let callee = object_to_jvalue!(callee);
    let args = optional_object_to_jvalue!(args);
    let type_args = optional_object_to_jvalue!(type_args);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_new_expr,
        &[callee, args, type_args, span],
        "Swc4jAstNewExpr create_new_expr()"
      );
    return_value
  }

  pub fn create_null<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_null,
        &[span],
        "Swc4jAstNull create_null()"
      );
    return_value
  }

  pub fn create_number<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: f64,
    raw: &Option<String>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let value = double_to_jvalue!(value);
    let java_raw = optional_string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_number,
        &[value, raw, span],
        "Swc4jAstNumber create_number()"
      );
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_object_lit<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    props: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let props = object_to_jvalue!(props);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_object_lit,
        &[props, span],
        "Swc4jAstObjectLit create_object_lit()"
      );
    return_value
  }

  pub fn create_object_pat<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    props: &JObject<'_>,
    optional: bool,
    type_ann: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let props = object_to_jvalue!(props);
    let optional = boolean_to_jvalue!(optional);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_object_pat,
        &[props, optional, type_ann, span],
        "Swc4jAstObjectPat create_object_pat()"
      );
    return_value
  }

  pub fn create_opt_call<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    callee: &JObject<'_>,
    args: &JObject<'_>,
    type_args: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let callee = object_to_jvalue!(callee);
    let args = object_to_jvalue!(args);
    let type_args = optional_object_to_jvalue!(type_args);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_opt_call,
        &[callee, args, type_args, span],
        "Swc4jAstOptCall create_opt_call()"
      );
    return_value
  }

  pub fn create_opt_chain_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    optional: bool,
    base: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let optional = boolean_to_jvalue!(optional);
    let base = object_to_jvalue!(base);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_opt_chain_expr,
        &[optional, base, span],
        "Swc4jAstOptChainExpr create_opt_chain_expr()"
      );
    return_value
  }

  pub fn create_param<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decorators: &JObject<'_>,
    pat: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decorators = object_to_jvalue!(decorators);
    let pat = object_to_jvalue!(pat);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_param,
        &[decorators, pat, span],
        "Swc4jAstParam create_param()"
      );
    return_value
  }

  pub fn create_paren_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_paren_expr,
        &[expr, span],
        "Swc4jAstParenExpr create_paren_expr()"
      );
    return_value
  }

  pub fn create_private_method<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    function: &JObject<'_>,
    kind: i32,
    is_static: bool,
    accessibility_id: i32,
    is_abstract: bool,
    optional: bool,
    is_override: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let function = object_to_jvalue!(function);
    let kind = int_to_jvalue!(kind);
    let is_static = boolean_to_jvalue!(is_static);
    let accessibility_id = int_to_jvalue!(accessibility_id);
    let is_abstract = boolean_to_jvalue!(is_abstract);
    let optional = boolean_to_jvalue!(optional);
    let is_override = boolean_to_jvalue!(is_override);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_private_method,
        &[key, function, kind, is_static, accessibility_id, is_abstract, optional, is_override, span],
        "Swc4jAstPrivateMethod create_private_method()"
      );
    return_value
  }

  pub fn create_private_name<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_private_name,
        &[id, span],
        "Swc4jAstPrivateName create_private_name()"
      );
    return_value
  }

  pub fn create_private_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    value: &Option<JObject>,
    type_ann: &Option<JObject>,
    is_static: bool,
    decorators: &JObject<'_>,
    accessibility_id: i32,
    is_optional: bool,
    is_override: bool,
    readonly: bool,
    definite: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let value = optional_object_to_jvalue!(value);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let is_static = boolean_to_jvalue!(is_static);
    let decorators = object_to_jvalue!(decorators);
    let accessibility_id = int_to_jvalue!(accessibility_id);
    let is_optional = boolean_to_jvalue!(is_optional);
    let is_override = boolean_to_jvalue!(is_override);
    let readonly = boolean_to_jvalue!(readonly);
    let definite = boolean_to_jvalue!(definite);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_private_prop,
        &[key, value, type_ann, is_static, decorators, accessibility_id, is_optional, is_override, readonly, definite, span],
        "Swc4jAstPrivateProp create_private_prop()"
      );
    return_value
  }

  pub fn create_regex<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    exp: &str,
    flags: &str,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_exp = string_to_jstring!(env, &exp);
    let exp = object_to_jvalue!(java_exp);
    let java_flags = string_to_jstring!(env, &flags);
    let flags = object_to_jvalue!(java_flags);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_regex,
        &[exp, flags, span],
        "Swc4jAstRegex create_regex()"
      );
    delete_local_ref!(env, java_exp);
    delete_local_ref!(env, java_flags);
    return_value
  }

  pub fn create_rest_pat<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    dot3_token: &JObject<'_>,
    arg: &JObject<'_>,
    type_ann: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let dot3_token = object_to_jvalue!(dot3_token);
    let arg = object_to_jvalue!(arg);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_rest_pat,
        &[dot3_token, arg, type_ann, span],
        "Swc4jAstRestPat create_rest_pat()"
      );
    return_value
  }

  pub fn create_return_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    arg: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let arg = optional_object_to_jvalue!(arg);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_return_stmt,
        &[arg, span],
        "Swc4jAstReturnStmt create_return_stmt()"
      );
    return_value
  }

  pub fn create_script<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    shebang: &Option<String>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let body = object_to_jvalue!(body);
    let java_shebang = optional_string_to_jstring!(env, &shebang);
    let shebang = object_to_jvalue!(java_shebang);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_script,
        &[body, shebang, span],
        "Swc4jAstScript create_script()"
      );
    delete_local_ref!(env, java_shebang);
    return_value
  }

  pub fn create_seq_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    exprs: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let exprs = object_to_jvalue!(exprs);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_seq_expr,
        &[exprs, span],
        "Swc4jAstSeqExpr create_seq_expr()"
      );
    return_value
  }

  pub fn create_setter_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    param: &JObject<'_>,
    body: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let param = object_to_jvalue!(param);
    let body = optional_object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_setter_prop,
        &[key, param, body, span],
        "Swc4jAstSetterProp create_setter_prop()"
      );
    return_value
  }

  pub fn create_span<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let start = int_to_jvalue!(range.start);
    let end = int_to_jvalue!(range.end);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_span,
        &[start, end],
        "Swc4jAstSpan create_span()"
      );
    return_value
  }

  pub fn create_spread_element<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    dot3_token: &JObject<'_>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let dot3_token = object_to_jvalue!(dot3_token);
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_spread_element,
        &[dot3_token, expr, span],
        "Swc4jAstSpreadElement create_spread_element()"
      );
    return_value
  }

  pub fn create_static_block<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_static_block,
        &[body, span],
        "Swc4jAstStaticBlock create_static_block()"
      );
    return_value
  }

  pub fn create_str<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: &str,
    raw: &Option<String>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let java_raw = optional_string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_str,
        &[value, raw, span],
        "Swc4jAstStr create_str()"
      );
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_super<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_super,
        &[span],
        "Swc4jAstSuper create_super()"
      );
    return_value
  }

  pub fn create_super_prop_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
    prop: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let obj = object_to_jvalue!(obj);
    let prop = object_to_jvalue!(prop);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_super_prop_expr,
        &[obj, prop, span],
        "Swc4jAstSuperPropExpr create_super_prop_expr()"
      );
    return_value
  }

  pub fn create_switch_case<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    test: &Option<JObject>,
    cons: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let test = optional_object_to_jvalue!(test);
    let cons = object_to_jvalue!(cons);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_switch_case,
        &[test, cons, span],
        "Swc4jAstSwitchCase create_switch_case()"
      );
    return_value
  }

  pub fn create_switch_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    discriminant: &JObject<'_>,
    cases: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let discriminant = object_to_jvalue!(discriminant);
    let cases = object_to_jvalue!(cases);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_switch_stmt,
        &[discriminant, cases, span],
        "Swc4jAstSwitchStmt create_switch_stmt()"
      );
    return_value
  }

  pub fn create_tagged_tpl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    tag: &JObject<'_>,
    type_params: &Option<JObject>,
    tpl: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let tag = object_to_jvalue!(tag);
    let type_params = optional_object_to_jvalue!(type_params);
    let tpl = object_to_jvalue!(tpl);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_tagged_tpl,
        &[tag, type_params, tpl, span],
        "Swc4jAstTaggedTpl create_tagged_tpl()"
      );
    return_value
  }

  pub fn create_this_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_this_expr,
        &[span],
        "Swc4jAstThisExpr create_this_expr()"
      );
    return_value
  }

  pub fn create_throw_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    arg: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let arg = object_to_jvalue!(arg);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_throw_stmt,
        &[arg, span],
        "Swc4jAstThrowStmt create_throw_stmt()"
      );
    return_value
  }

  pub fn create_tpl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    exprs: &JObject<'_>,
    quasis: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let exprs = object_to_jvalue!(exprs);
    let quasis = object_to_jvalue!(quasis);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_tpl,
        &[exprs, quasis, span],
        "Swc4jAstTpl create_tpl()"
      );
    return_value
  }

  pub fn create_tpl_element<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    tail: bool,
    cooked: &Option<String>,
    raw: &str,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let tail = boolean_to_jvalue!(tail);
    let java_cooked = optional_string_to_jstring!(env, &cooked);
    let cooked = object_to_jvalue!(java_cooked);
    let java_raw = string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_tpl_element,
        &[tail, cooked, raw, span],
        "Swc4jAstTplElement create_tpl_element()"
      );
    delete_local_ref!(env, java_cooked);
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_try_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    block: &JObject<'_>,
    handler: &Option<JObject>,
    finalizer: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let block = object_to_jvalue!(block);
    let handler = optional_object_to_jvalue!(handler);
    let finalizer = optional_object_to_jvalue!(finalizer);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_try_stmt,
        &[block, handler, finalizer, span],
        "Swc4jAstTryStmt create_try_stmt()"
      );
    return_value
  }

  pub fn create_ts_as_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    type_ann: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let type_ann = object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_as_expr,
        &[expr, type_ann, span],
        "Swc4jAstTsAsExpr create_ts_as_expr()"
      );
    return_value
  }

  pub fn create_ts_const_assertion<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_const_assertion,
        &[expr, span],
        "Swc4jAstTsConstAssertion create_ts_const_assertion()"
      );
    return_value
  }

  pub fn create_ts_enum_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    declare: bool,
    is_const: bool,
    id: &JObject<'_>,
    members: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let declare = boolean_to_jvalue!(declare);
    let is_const = boolean_to_jvalue!(is_const);
    let id = object_to_jvalue!(id);
    let members = object_to_jvalue!(members);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_enum_decl,
        &[declare, is_const, id, members, span],
        "Swc4jAstTsEnumDecl create_ts_enum_decl()"
      );
    return_value
  }

  pub fn create_ts_enum_member<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    init: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let init = optional_object_to_jvalue!(init);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_enum_member,
        &[id, init, span],
        "Swc4jAstTsEnumMember create_ts_enum_member()"
      );
    return_value
  }

  pub fn create_ts_export_assignment<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_export_assignment,
        &[decl, span],
        "Swc4jAstTsExportAssignment create_ts_export_assignment()"
      );
    return_value
  }

  pub fn create_ts_expr_with_type_args<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    type_args: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let type_args = optional_object_to_jvalue!(type_args);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_expr_with_type_args,
        &[expr, type_args, span],
        "Swc4jAstTsExprWithTypeArgs create_ts_expr_with_type_args()"
      );
    return_value
  }

  pub fn create_ts_external_module_ref<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_external_module_ref,
        &[expr, span],
        "Swc4jAstTsExternalModuleRef create_ts_external_module_ref()"
      );
    return_value
  }

  pub fn create_ts_import_equals_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    export: bool,
    type_only: bool,
    id: &JObject<'_>,
    module_ref: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let export = boolean_to_jvalue!(export);
    let type_only = boolean_to_jvalue!(type_only);
    let id = object_to_jvalue!(id);
    let module_ref = object_to_jvalue!(module_ref);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_import_equals_decl,
        &[export, type_only, id, module_ref, span],
        "Swc4jAstTsImportEqualsDecl create_ts_import_equals_decl()"
      );
    return_value
  }

  pub fn create_ts_index_signature<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    params: &JObject<'_>,
    type_ann: &Option<JObject>,
    readonly: bool,
    is_static: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let params = object_to_jvalue!(params);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let readonly = boolean_to_jvalue!(readonly);
    let is_static = boolean_to_jvalue!(is_static);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_index_signature,
        &[params, type_ann, readonly, is_static, span],
        "Swc4jAstTsIndexSignature create_ts_index_signature()"
      );
    return_value
  }

  pub fn create_ts_instantiation<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    type_args: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let type_args = object_to_jvalue!(type_args);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_instantiation,
        &[expr, type_args, span],
        "Swc4jAstTsInstantiation create_ts_instantiation()"
      );
    return_value
  }

  pub fn create_ts_interface_body<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_interface_body,
        &[body, span],
        "Swc4jAstTsInterfaceBody create_ts_interface_body()"
      );
    return_value
  }

  pub fn create_ts_interface_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    declare: bool,
    type_params: &Option<JObject>,
    extends: &JObject<'_>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let declare = boolean_to_jvalue!(declare);
    let type_params = optional_object_to_jvalue!(type_params);
    let extends = object_to_jvalue!(extends);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_interface_decl,
        &[id, declare, type_params, extends, body, span],
        "Swc4jAstTsInterfaceDecl create_ts_interface_decl()"
      );
    return_value
  }

  pub fn create_ts_module_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    declare: bool,
    global: bool,
    id: &JObject<'_>,
    body: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let declare = boolean_to_jvalue!(declare);
    let global = boolean_to_jvalue!(global);
    let id = object_to_jvalue!(id);
    let body = optional_object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_module_decl,
        &[declare, global, id, body, span],
        "Swc4jAstTsModuleDecl create_ts_module_decl()"
      );
    return_value
  }

  pub fn create_ts_namespace_export_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_namespace_export_decl,
        &[id, span],
        "Swc4jAstTsNamespaceExportDecl create_ts_namespace_export_decl()"
      );
    return_value
  }

  pub fn create_ts_non_null_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_non_null_expr,
        &[expr, span],
        "Swc4jAstTsNonNullExpr create_ts_non_null_expr()"
      );
    return_value
  }

  pub fn create_ts_param_prop<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decorators: &JObject<'_>,
    accessibility_id: i32,
    is_override: bool,
    readonly: bool,
    param: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decorators = object_to_jvalue!(decorators);
    let accessibility_id = int_to_jvalue!(accessibility_id);
    let is_override = boolean_to_jvalue!(is_override);
    let readonly = boolean_to_jvalue!(readonly);
    let param = object_to_jvalue!(param);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_param_prop,
        &[decorators, accessibility_id, is_override, readonly, param, span],
        "Swc4jAstTsParamProp create_ts_param_prop()"
      );
    return_value
  }

  pub fn create_ts_qualified_name<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    left: &JObject<'_>,
    right: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let left = object_to_jvalue!(left);
    let right = object_to_jvalue!(right);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_qualified_name,
        &[left, right, span],
        "Swc4jAstTsQualifiedName create_ts_qualified_name()"
      );
    return_value
  }

  pub fn create_ts_satisfies_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    type_ann: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let type_ann = object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_satisfies_expr,
        &[expr, type_ann, span],
        "Swc4jAstTsSatisfiesExpr create_ts_satisfies_expr()"
      );
    return_value
  }

  pub fn create_ts_type_alias_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    declare: bool,
    type_params: &Option<JObject>,
    type_ann: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let declare = boolean_to_jvalue!(declare);
    let type_params = optional_object_to_jvalue!(type_params);
    let type_ann = object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_alias_decl,
        &[id, declare, type_params, type_ann, span],
        "Swc4jAstTsTypeAliasDecl create_ts_type_alias_decl()"
      );
    return_value
  }

  pub fn create_ts_type_ann<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    type_ann: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let type_ann = object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_ann,
        &[type_ann, span],
        "Swc4jAstTsTypeAnn create_ts_type_ann()"
      );
    return_value
  }

  pub fn create_ts_type_assertion<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    type_ann: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let type_ann = object_to_jvalue!(type_ann);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_assertion,
        &[expr, type_ann, span],
        "Swc4jAstTsTypeAssertion create_ts_type_assertion()"
      );
    return_value
  }

  pub fn create_ts_type_param<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    name: &JObject<'_>,
    is_in: bool,
    is_out: bool,
    is_const: bool,
    constraint: &Option<JObject>,
    default: &Option<JObject>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let name = object_to_jvalue!(name);
    let is_in = boolean_to_jvalue!(is_in);
    let is_out = boolean_to_jvalue!(is_out);
    let is_const = boolean_to_jvalue!(is_const);
    let constraint = optional_object_to_jvalue!(constraint);
    let default = optional_object_to_jvalue!(default);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_param,
        &[name, is_in, is_out, is_const, constraint, default, span],
        "Swc4jAstTsTypeParam create_ts_type_param()"
      );
    return_value
  }

  pub fn create_ts_type_param_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    params: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let params = object_to_jvalue!(params);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_param_decl,
        &[params, span],
        "Swc4jAstTsTypeParamDecl create_ts_type_param_decl()"
      );
    return_value
  }

  pub fn create_ts_type_param_instantiation<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    params: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let params = object_to_jvalue!(params);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_param_instantiation,
        &[params, span],
        "Swc4jAstTsTypeParamInstantiation create_ts_type_param_instantiation()"
      );
    return_value
  }

  pub fn create_unary_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    op: i32,
    arg: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let op = int_to_jvalue!(op);
    let arg = object_to_jvalue!(arg);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_unary_expr,
        &[op, arg, span],
        "Swc4jAstUnaryExpr create_unary_expr()"
      );
    return_value
  }

  pub fn create_update_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    op: i32,
    prefix: bool,
    arg: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let op = int_to_jvalue!(op);
    let prefix = boolean_to_jvalue!(prefix);
    let arg = object_to_jvalue!(arg);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_update_expr,
        &[op, prefix, arg, span],
        "Swc4jAstUpdateExpr create_update_expr()"
      );
    return_value
  }

  pub fn create_using_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    is_await: bool,
    decls: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let is_await = boolean_to_jvalue!(is_await);
    let decls = object_to_jvalue!(decls);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_using_decl,
        &[is_await, decls, span],
        "Swc4jAstUsingDecl create_using_decl()"
      );
    return_value
  }

  pub fn create_var_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    kind_id: i32,
    declare: bool,
    decls: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let kind_id = int_to_jvalue!(kind_id);
    let declare = boolean_to_jvalue!(declare);
    let decls = object_to_jvalue!(decls);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_var_decl,
        &[kind_id, declare, decls, span],
        "Swc4jAstVarDecl create_var_decl()"
      );
    return_value
  }

  pub fn create_var_declarator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    name: &JObject<'_>,
    init: &Option<JObject>,
    definite: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let name = object_to_jvalue!(name);
    let init = optional_object_to_jvalue!(init);
    let definite = boolean_to_jvalue!(definite);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_var_declarator,
        &[name, init, definite, span],
        "Swc4jAstVarDeclarator create_var_declarator()"
      );
    return_value
  }

  pub fn create_while_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    test: &JObject<'_>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let test = object_to_jvalue!(test);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_while_stmt,
        &[test, body, span],
        "Swc4jAstWhileStmt create_while_stmt()"
      );
    return_value
  }

  pub fn create_with_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
    body: &JObject<'_>,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let obj = object_to_jvalue!(obj);
    let body = object_to_jvalue!(body);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_with_stmt,
        &[obj, body, span],
        "Swc4jAstWithStmt create_with_stmt()"
      );
    return_value
  }

  pub fn create_yield_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    arg: &Option<JObject>,
    delegate: bool,
    span: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let arg = optional_object_to_jvalue!(arg);
    let delegate = boolean_to_jvalue!(delegate);
    let span = object_to_jvalue!(span);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_yield_expr,
        &[arg, delegate, span],
        "Swc4jAstYieldExpr create_yield_expr()"
      );
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
  use deno_ast::swc::{ast::*, common::Spanned};

  fn enum_register_block_stmt_or_expr(map: &mut ByteToIndexMap, node: &BlockStmtOrExpr) {
    match node {
      BlockStmtOrExpr::BlockStmt(node) => register_block_stmt(map, node),
      BlockStmtOrExpr::Expr(node) => enum_register_expr(map, node),
    }
  }

  fn enum_register_callee(map: &mut ByteToIndexMap, node: &Callee) {
    match node {
      Callee::Expr(node) => enum_register_expr(map, node),
      Callee::Import(node) => register_import(map, node),
      Callee::Super(node) => register_super(map, node),
    }
  }

  fn enum_register_class_member(map: &mut ByteToIndexMap, node: &ClassMember) {
    match node {
      ClassMember::AutoAccessor(node) => register_auto_accessor(map, node),
      ClassMember::ClassProp(node) => register_class_prop(map, node),
      ClassMember::Constructor(node) => register_constructor(map, node),
      ClassMember::Empty(node) => register_empty_stmt(map, node),
      ClassMember::Method(node) => register_class_method(map, node),
      ClassMember::PrivateMethod(node) => register_private_method(map, node),
      ClassMember::PrivateProp(node) => register_private_prop(map, node),
      ClassMember::StaticBlock(node) => register_static_block(map, node),
      ClassMember::TsIndexSignature(node) => register_ts_index_signature(map, node),
    }
  }

  fn enum_register_decl(map: &mut ByteToIndexMap, node: &Decl) {
    match node {
      Decl::Class(node) => register_class_decl(map, &node),
      Decl::Fn(node) => register_fn_decl(map, &node),
      Decl::TsEnum(node) => register_ts_enum_decl(map, &node.as_ref()),
      Decl::TsInterface(node) => register_ts_interface_decl(map, &node.as_ref()),
      Decl::TsModule(node) => register_ts_module_decl(map, &node.as_ref()),
      Decl::TsTypeAlias(node) => register_ts_type_alias_decl(map, &node.as_ref()),
      Decl::Using(node) => register_using_decl(map, &node.as_ref()),
      Decl::Var(node) => register_var_decl(map, node.as_ref()),
    };
  }

  fn enum_register_default_decl(map: &mut ByteToIndexMap, node: &DefaultDecl) {
    match node {
      DefaultDecl::Class(node) => register_class_expr(map, node),
      DefaultDecl::Fn(node) => register_fn_expr(map, node),
      DefaultDecl::TsInterfaceDecl(node) => register_ts_interface_decl(map, node),
    }
  }

  fn enum_register_export_specifier(map: &mut ByteToIndexMap, node: &ExportSpecifier) {
    match node {
      ExportSpecifier::Default(node) => register_export_default_specifier(map, node),
      ExportSpecifier::Named(node) => register_export_named_specifier(map, node),
      ExportSpecifier::Namespace(node) => register_export_namespace_specifier(map, node),
    }
  }

  fn enum_register_expr(map: &mut ByteToIndexMap, node: &Expr) {
    match node {
      Expr::Array(node) => register_array_lit(map, node),
      Expr::Arrow(node) => register_arrow_expr(map, node),
      Expr::Assign(node) => register_assign_expr(map, node),
      Expr::Await(node) => register_await_expr(map, node),
      Expr::Bin(node) => register_bin_expr(map, node),
      Expr::Call(node) => register_call_expr(map, node),
      Expr::Class(node) => register_class_expr(map, node),
      Expr::Cond(node) => register_cond_expr(map, node),
      Expr::Fn(node) => register_fn_expr(map, node),
      Expr::Ident(node) => register_ident(map, node),
      Expr::Invalid(node) => register_invalid(map, node),
      Expr::JSXElement(node) => register_jsx_element(map, node),
      Expr::JSXEmpty(node) => register_jsx_empty_expr(map, node),
      Expr::JSXFragment(node) => register_jsx_fragment(map, node),
      Expr::JSXMember(node) => register_jsx_member_expr(map, node),
      Expr::JSXNamespacedName(node) => register_jsx_namespaced_name(map, node),
      Expr::Lit(node) => enum_register_lit(map, node),
      Expr::Member(node) => register_member_expr(map, node),
      Expr::MetaProp(node) => register_meta_prop_expr(map, node),
      Expr::New(node) => register_new_expr(map, node),
      Expr::Object(node) => register_object_lit(map, node),
      Expr::OptChain(node) => register_opt_chain_expr(map, node),
      Expr::Paren(node) => register_paren_expr(map, node),
      Expr::PrivateName(node) => register_private_name(map, node),
      Expr::Seq(node) => register_seq_expr(map, node),
      Expr::SuperProp(node) => register_super_prop_expr(map, node),
      Expr::TaggedTpl(node) => register_tagged_tpl(map, node),
      Expr::This(node) => register_this_expr(map, node),
      Expr::Tpl(node) => register_tpl(map, node),
      Expr::TsAs(node) => register_ts_as_expr(map, node),
      Expr::TsConstAssertion(node) => register_ts_const_assertion(map, node),
      Expr::TsInstantiation(node) => register_ts_instantiation(map, node),
      Expr::TsNonNull(node) => register_ts_non_null_expr(map, node),
      Expr::TsSatisfies(node) => register_ts_satisfies_expr(map, node),
      Expr::TsTypeAssertion(node) => register_ts_type_assertion(map, node),
      Expr::Unary(node) => register_unary_expr(map, node),
      Expr::Update(node) => register_update_expr(map, node),
      Expr::Yield(node) => register_yield_expr(map, node),
    }
  }

  fn enum_register_for_head(map: &mut ByteToIndexMap, node: &ForHead) {
    match node {
      ForHead::Pat(node) => enum_register_pat(map, node),
      ForHead::UsingDecl(node) => register_using_decl(map, node),
      ForHead::VarDecl(node) => register_var_decl(map, node),
    }
  }

  fn enum_register_import_specifier(map: &mut ByteToIndexMap, node: &ImportSpecifier) {
    match node {
      ImportSpecifier::Default(node) => register_import_default_specifier(map, node),
      ImportSpecifier::Named(node) => register_import_named_specifier(map, node),
      ImportSpecifier::Namespace(node) => register_import_star_as_specifier(map, node),
    }
  }

  fn enum_register_jsx_attr_name(map: &mut ByteToIndexMap, node: &JSXAttrName) {
    match node {
      JSXAttrName::Ident(node) => register_ident(map, node),
      JSXAttrName::JSXNamespacedName(node) => register_jsx_namespaced_name(map, node),
    }
  }

  fn enum_register_jsx_attr_or_spread(map: &mut ByteToIndexMap, node: &JSXAttrOrSpread) {
    match node {
      JSXAttrOrSpread::JSXAttr(node) => register_jsx_attr(map, node),
      JSXAttrOrSpread::SpreadElement(node) => register_spread_element(map, node),
    }
  }

  fn enum_register_jsx_attr_value(map: &mut ByteToIndexMap, node: &JSXAttrValue) {
    match node {
      JSXAttrValue::JSXElement(node) => register_jsx_element(map, node),
      JSXAttrValue::JSXExprContainer(node) => register_jsx_expr_container(map, node),
      JSXAttrValue::JSXFragment(node) => register_jsx_fragment(map, node),
      JSXAttrValue::Lit(node) => enum_register_lit(map, node),
    }
  }

  fn enum_register_jsx_element_child(map: &mut ByteToIndexMap, node: &JSXElementChild) {
    match node {
      JSXElementChild::JSXElement(node) => register_jsx_element(map, node),
      JSXElementChild::JSXExprContainer(node) => register_jsx_expr_container(map, node),
      JSXElementChild::JSXFragment(node) => register_jsx_fragment(map, node),
      JSXElementChild::JSXSpreadChild(node) => register_jsx_spread_child(map, node),
      JSXElementChild::JSXText(node) => register_jsx_text(map, node),
    }
  }

  fn enum_register_jsx_element_name(map: &mut ByteToIndexMap, node: &JSXElementName) {
    match node {
      JSXElementName::Ident(node) => register_ident(map, node),
      JSXElementName::JSXMemberExpr(node) => register_jsx_member_expr(map, node),
      JSXElementName::JSXNamespacedName(node) => register_jsx_namespaced_name(map, node),
    }
  }

  fn enum_register_jsx_expr(map: &mut ByteToIndexMap, node: &JSXExpr) {
    match node {
      JSXExpr::Expr(node) => enum_register_expr(map, node),
      JSXExpr::JSXEmptyExpr(node) => register_jsx_empty_expr(map, node),
    }
  }

  fn enum_register_jsx_object(map: &mut ByteToIndexMap, node: &JSXObject) {
    match node {
      JSXObject::Ident(node) => register_ident(map, node),
      JSXObject::JSXMemberExpr(node) => register_jsx_member_expr(map, node),
    }
  }

  fn enum_register_key(map: &mut ByteToIndexMap, node: &Key) {
    match node {
      Key::Private(node) => register_private_name(map, node),
      Key::Public(node) => enum_register_prop_name(map, node),
    }
  }

  fn enum_register_lit(map: &mut ByteToIndexMap, node: &Lit) {
    match node {
      Lit::BigInt(node) => register_big_int(map, node),
      Lit::Bool(node) => register_bool(map, node),
      Lit::JSXText(node) => register_jsx_text(map, node),
      Lit::Null(node) => register_null(map, node),
      Lit::Num(node) => register_number(map, node),
      Lit::Regex(node) => register_regex(map, node),
      Lit::Str(node) => register_str(map, node),
    }
  }

  fn enum_register_member_prop(map: &mut ByteToIndexMap, node: &MemberProp) {
    match node {
      MemberProp::Computed(node) => register_computed_prop_name(map, node),
      MemberProp::Ident(node) => register_ident(map, node),
      MemberProp::PrivateName(node) => register_private_name(map, node),
    }
  }

  fn enum_register_module_decl(map: &mut ByteToIndexMap, node: &ModuleDecl) {
    match node {
      ModuleDecl::ExportAll(node) => register_export_all(map, node),
      ModuleDecl::ExportDecl(node) => register_export_decl(map, node),
      ModuleDecl::ExportDefaultDecl(node) => register_export_default_decl(map, node),
      ModuleDecl::ExportDefaultExpr(node) => register_export_default_expr(map, node),
      ModuleDecl::ExportNamed(node) => register_named_export(map, node),
      ModuleDecl::Import(node) => register_import_decl(map, node),
      ModuleDecl::TsExportAssignment(node) => register_ts_export_assignment(map, node),
      ModuleDecl::TsImportEquals(node) => register_ts_import_equals_decl(map, node),
      ModuleDecl::TsNamespaceExport(node) => register_ts_namespace_export_decl(map, node),
    }
  }

  fn enum_register_module_export_name(map: &mut ByteToIndexMap, node: &ModuleExportName) {
    match node {
      ModuleExportName::Ident(node) => register_ident(map, node),
      ModuleExportName::Str(node) => register_str(map, node),
    }
  }

  fn enum_register_module_item(map: &mut ByteToIndexMap, node: &ModuleItem) {
    match node {
      ModuleItem::ModuleDecl(node) => enum_register_module_decl(map, &node),
      ModuleItem::Stmt(node) => enum_register_stmt(map, &node),
    }
  }

  fn enum_register_object_pat_prop(map: &mut ByteToIndexMap, node: &ObjectPatProp) {
    match node {
      ObjectPatProp::Assign(node) => register_assign_pat_prop(map, node),
      ObjectPatProp::KeyValue(node) => register_key_value_pat_prop(map, node),
      ObjectPatProp::Rest(node) => register_rest_pat(map, node),
    }
  }

  fn enum_register_opt_chain_base(map: &mut ByteToIndexMap, node: &OptChainBase) {
    match node {
      OptChainBase::Call(node) => register_opt_call(map, node),
      OptChainBase::Member(node) => register_member_expr(map, node),
    }
  }

  fn enum_register_param_or_ts_param_prop(map: &mut ByteToIndexMap, node: &ParamOrTsParamProp) {
    match node {
      ParamOrTsParamProp::Param(node) => register_param(map, node),
      ParamOrTsParamProp::TsParamProp(node) => register_ts_param_prop(map, node),
    }
  }

  fn enum_register_pat(map: &mut ByteToIndexMap, node: &Pat) {
    match &node {
      Pat::Array(node) => register_array_pat(map, node),
      Pat::Assign(node) => register_assign_pat(map, node),
      Pat::Expr(node) => enum_register_expr(map, node),
      Pat::Ident(node) => register_binding_ident(map, node),
      Pat::Invalid(node) => register_invalid(map, node),
      Pat::Object(node) => register_object_pat(map, node),
      Pat::Rest(node) => register_rest_pat(map, node),
    }
  }

  fn enum_register_pat_or_expr(map: &mut ByteToIndexMap, node: &PatOrExpr) {
    match node {
      PatOrExpr::Expr(node) => enum_register_expr(map, node),
      PatOrExpr::Pat(node) => enum_register_pat(map, node),
    }
  }

  pub fn enum_register_program(map: &mut ByteToIndexMap, node: &Program) {
    match node {
      Program::Module(node) => register_module(map, node),
      Program::Script(node) => register_script(map, node),
    }
  }

  fn enum_register_prop(map: &mut ByteToIndexMap, node: &Prop) {
    match node {
      Prop::Assign(node) => register_assign_prop(map, node),
      Prop::Getter(node) => register_getter_prop(map, node),
      Prop::KeyValue(node) => register_key_value_prop(map, node),
      Prop::Method(node) => register_method_prop(map, node),
      Prop::Setter(node) => register_setter_prop(map, node),
      Prop::Shorthand(node) => register_ident(map, node),
    }
  }

  fn enum_register_prop_name(map: &mut ByteToIndexMap, node: &PropName) {
    match node {
      PropName::BigInt(node) => register_big_int(map, node),
      PropName::Computed(node) => register_computed_prop_name(map, node),
      PropName::Ident(node) => register_ident(map, node),
      PropName::Num(node) => register_number(map, node),
      PropName::Str(node) => register_str(map, node),
    }
  }

  fn enum_register_prop_or_spread(map: &mut ByteToIndexMap, node: &PropOrSpread) {
    match node {
      PropOrSpread::Prop(node) => enum_register_prop(map, node),
      PropOrSpread::Spread(node) => register_spread_element(map, node),
    }
  }

  fn enum_register_stmt(map: &mut ByteToIndexMap, node: &Stmt) {
    match node {
      Stmt::Block(node) => register_block_stmt(map, node),
      Stmt::Break(node) => register_break_stmt(map, node),
      Stmt::Continue(node) => register_continue_stmt(map, node),
      Stmt::Debugger(node) => register_debugger_stmt(map, node),
      Stmt::Decl(node) => enum_register_decl(map, node),
      Stmt::DoWhile(node) => register_do_while_stmt(map, node),
      Stmt::Empty(node) => register_empty_stmt(map, node),
      Stmt::Expr(node) => register_expr_stmt(map, node),
      Stmt::For(node) => register_for_stmt(map, node),
      Stmt::ForIn(node) => register_for_in_stmt(map, node),
      Stmt::ForOf(node) => register_for_of_stmt(map, node),
      Stmt::If(node) => register_if_stmt(map, node),
      Stmt::Labeled(node) => register_labeled_stmt(map, node),
      Stmt::Return(node) => register_return_stmt(map, node),
      Stmt::Switch(node) => register_switch_stmt(map, node),
      Stmt::Throw(node) => register_throw_stmt(map, node),
      Stmt::Try(node) => register_try_stmt(map, node),
      Stmt::While(node) => register_while_stmt(map, node),
      Stmt::With(node) => register_with_stmt(map, node),
    };
  }

  fn enum_register_super_prop(map: &mut ByteToIndexMap, node: &SuperProp) {
    match node {
      SuperProp::Computed(node) => register_computed_prop_name(map, node),
      SuperProp::Ident(node) => register_ident(map, node),
    }
  }

  fn enum_register_ts_entity_name(map: &mut ByteToIndexMap, node: &TsEntityName) {
    match node {
      TsEntityName::Ident(node) => register_ident(map, node),
      TsEntityName::TsQualifiedName(node) => register_ts_qualified_name(map, node),
    }
  }

  fn enum_register_ts_enum_member_id(map: &mut ByteToIndexMap, node: &TsEnumMemberId) {
    match node {
      TsEnumMemberId::Ident(node) => register_ident(map, node),
      TsEnumMemberId::Str(node) => register_str(map, node),
    }
  }

  fn enum_register_ts_fn_or_constructor_type(map: &mut ByteToIndexMap, node: &TsFnOrConstructorType) {
    match node {
      TsFnOrConstructorType::TsConstructorType(node) => register_ts_constructor_type(map, node),
      TsFnOrConstructorType::TsFnType(node) => register_ts_fn_type(map, node),
    }
  }

  fn enum_register_ts_fn_param(map: &mut ByteToIndexMap, node: &TsFnParam) {
    match node {
      TsFnParam::Array(node) => register_array_pat(map, node),
      TsFnParam::Ident(node) => register_binding_ident(map, node),
      TsFnParam::Object(node) => register_object_pat(map, node),
      TsFnParam::Rest(node) => register_rest_pat(map, node),
    }
  }

  fn enum_register_ts_lit(map: &mut ByteToIndexMap, node: &TsLit) {
    match node {
      TsLit::BigInt(node) => register_big_int(map, node),
      TsLit::Bool(node) => register_bool(map, node),
      TsLit::Number(node) => register_number(map, node),
      TsLit::Str(node) => register_str(map, node),
      TsLit::Tpl(node) => register_ts_tpl_lit_type(map, node),
    }
  }

  fn enum_register_ts_module_name(map: &mut ByteToIndexMap, node: &TsModuleName) {
    match node {
      TsModuleName::Ident(node) => register_ident(map, node),
      TsModuleName::Str(node) => register_str(map, node),
    }
  }

  fn enum_register_ts_module_ref(map: &mut ByteToIndexMap, node: &TsModuleRef) {
    match node {
      TsModuleRef::TsEntityName(node) => enum_register_ts_entity_name(map, node),
      TsModuleRef::TsExternalModuleRef(node) => register_ts_external_module_ref(map, node),
    }
  }

  fn enum_register_ts_namespace_body(map: &mut ByteToIndexMap, node: &TsNamespaceBody) {
    match node {
      TsNamespaceBody::TsModuleBlock(node) => register_ts_module_block(map, node),
      TsNamespaceBody::TsNamespaceDecl(node) => register_ts_namespace_decl(map, node),
    }
  }

  fn enum_register_ts_param_prop_param(map: &mut ByteToIndexMap, node: &TsParamPropParam) {
    match node {
      TsParamPropParam::Assign(node) => register_assign_pat(map, node),
      TsParamPropParam::Ident(node) => register_binding_ident(map, node),
    }
  }

  fn enum_register_ts_this_type_or_ident(map: &mut ByteToIndexMap, node: &TsThisTypeOrIdent) {
    match node {
      TsThisTypeOrIdent::Ident(node) => register_ident(map, node),
      TsThisTypeOrIdent::TsThisType(node) => register_ts_this_type(map, node),
    }
  }

  fn enum_register_ts_type(map: &mut ByteToIndexMap, node: &TsType) {
    match node {
      TsType::TsArrayType(node) => register_ts_array_type(map, node),
      TsType::TsConditionalType(node) => register_ts_conditional_type(map, node),
      TsType::TsFnOrConstructorType(node) => enum_register_ts_fn_or_constructor_type(map, node),
      TsType::TsImportType(node) => register_ts_import_type(map, node),
      TsType::TsIndexedAccessType(node) => register_ts_indexed_access_type(map, node),
      TsType::TsInferType(node) => register_ts_infer_type(map, node),
      TsType::TsKeywordType(node) => register_ts_keyword_type(map, node),
      TsType::TsLitType(node) => register_ts_lit_type(map, node),
      TsType::TsMappedType(node) => register_ts_mapped_type(map, node),
      TsType::TsOptionalType(node) => register_ts_optional_type(map, node),
      TsType::TsParenthesizedType(node) => register_ts_parenthesized_type(map, node),
      TsType::TsRestType(node) => register_ts_rest_type(map, node),
      TsType::TsThisType(node) => register_ts_this_type(map, node),
      TsType::TsTupleType(node) => register_ts_tuple_type(map, node),
      TsType::TsTypeLit(node) => register_ts_type_lit(map, node),
      TsType::TsTypeOperator(node) => register_ts_type_operator(map, node),
      TsType::TsTypePredicate(node) => register_ts_type_predicate(map, node),
      TsType::TsTypeQuery(node) => register_ts_type_query(map, node),
      TsType::TsTypeRef(node) => register_ts_type_ref(map, node),
      TsType::TsUnionOrIntersectionType(node) => enum_register_ts_union_or_intersection_type(map, node),
    }
  }

  fn enum_register_ts_type_element(map: &mut ByteToIndexMap, node: &TsTypeElement) {
    match node {
      TsTypeElement::TsCallSignatureDecl(node) => register_ts_call_signature_decl(map, node),
      TsTypeElement::TsConstructSignatureDecl(node) => register_ts_construct_signature_decl(map, node),
      TsTypeElement::TsGetterSignature(node) => register_ts_getter_signature(map, node),
      TsTypeElement::TsIndexSignature(node) => register_ts_index_signature(map, node),
      TsTypeElement::TsMethodSignature(node) => register_ts_method_signature(map, node),
      TsTypeElement::TsPropertySignature(node) => register_ts_property_signature(map, node),
      TsTypeElement::TsSetterSignature(node) => register_ts_setter_signature(map, node),
    }
  }

  fn enum_register_ts_type_query_expr(map: &mut ByteToIndexMap, node: &TsTypeQueryExpr) {
    match node {
      TsTypeQueryExpr::Import(node) => register_ts_import_type(map, node),
      TsTypeQueryExpr::TsEntityName(node) => enum_register_ts_entity_name(map, node),
    }
  }

  fn enum_register_ts_union_or_intersection_type(map: &mut ByteToIndexMap, node: &TsUnionOrIntersectionType) {
    match node {
      TsUnionOrIntersectionType::TsIntersectionType(node) => register_ts_intersection_type(map, node),
      TsUnionOrIntersectionType::TsUnionType(node) => register_ts_union_type(map, node),
    }
  }

  fn enum_register_var_decl_or_expr(map: &mut ByteToIndexMap, node: &VarDeclOrExpr) {
    match node {
      VarDeclOrExpr::Expr(node) => enum_register_expr(map, node),
      VarDeclOrExpr::VarDecl(node) => register_var_decl(map, node),
    }
  }

  fn register_array_lit(map: &mut ByteToIndexMap, node: &ArrayLit) {
    map.register_by_span(&node.span);
    node.elems.iter().for_each(|node| {
      node.as_ref().map(|node| {
        node.spread.as_ref().map(|node| map.register_by_span(node));
        enum_register_expr(map, &node.expr);
      });
    });
  }

  fn register_array_pat(map: &mut ByteToIndexMap, node: &ArrayPat) {
    map.register_by_span(&node.span);
    node.elems.iter().for_each(|node| {
      node.as_ref().map(|node| enum_register_pat(map, node));
    });
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_arrow_expr(map: &mut ByteToIndexMap, node: &ArrowExpr) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| enum_register_pat(map, node));
    enum_register_block_stmt_or_expr(map, node.body.as_ref());
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
    node.return_type.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_assign_expr(map: &mut ByteToIndexMap, node: &AssignExpr) {
    map.register_by_span(&node.span);
    enum_register_pat_or_expr(map, &node.left);
    enum_register_expr(map, &node.right);
  }

  fn register_assign_pat(map: &mut ByteToIndexMap, node: &AssignPat) {
    map.register_by_span(&node.span);
    enum_register_pat(map, &node.left);
    enum_register_expr(map, &node.right);
  }

  fn register_assign_pat_prop(map: &mut ByteToIndexMap, node: &AssignPatProp) {
    map.register_by_span(&node.span);
    register_ident(map, &node.key);
    node.value.as_ref().map(|node| enum_register_expr(map, node));
  }

  fn register_assign_prop(map: &mut ByteToIndexMap, node: &AssignProp) {
    register_ident(map, &node.key);
    enum_register_expr(map, &node.value);
  }

  fn register_auto_accessor(map: &mut ByteToIndexMap, node: &AutoAccessor) {
    map.register_by_span(&node.span);
    enum_register_key(map, &node.key);
    node.value.as_ref().map(|node| enum_register_expr(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node.decorators.iter().for_each(|node| register_decorator(map, node));
  }

  fn register_await_expr(map: &mut ByteToIndexMap, node: &AwaitExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.arg);
  }

  fn register_big_int(map: &mut ByteToIndexMap, node: &BigInt) {
    map.register_by_span(&node.span);
  }

  fn register_bin_expr(map: &mut ByteToIndexMap, node: &BinExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.left);
    enum_register_expr(map, &node.right);
  }

  fn register_binding_ident(map: &mut ByteToIndexMap, node: &BindingIdent) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_block_stmt(map: &mut ByteToIndexMap, node: &BlockStmt) {
    map.register_by_span(&node.span);
    node.stmts.iter().for_each(|node| enum_register_stmt(map, node));
  }

  fn register_bool(map: &mut ByteToIndexMap, node: &Bool) {
    map.register_by_span(&node.span);
  }

  fn register_break_stmt(map: &mut ByteToIndexMap, node: &BreakStmt) {
    map.register_by_span(&node.span);
    node.label.as_ref().map(|node| register_ident(map, node));
  }

  fn register_call_expr(map: &mut ByteToIndexMap, node: &CallExpr) {
    map.register_by_span(&node.span);
    enum_register_callee(map, &node.callee);
    node.args.iter().for_each(|node| register_expr_or_spread(map, node));
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_catch_clause(map: &mut ByteToIndexMap, node: &CatchClause) {
    map.register_by_span(&node.span);
    node.param.as_ref().map(|node| enum_register_pat(map, node));
    register_block_stmt(map, &node.body);
  }

  fn register_class(map: &mut ByteToIndexMap, node: &Class) {
    map.register_by_span(&node.span);
    node.decorators.iter().for_each(|node| register_decorator(map, node));
    node.body.iter().for_each(|node| enum_register_class_member(map, node));
    node
      .super_class
      .as_ref()
      .map(|node| enum_register_expr(map, &node.as_ref()));
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
    map.register_by_span(&node.span());
    register_ident(map, &node.ident);
    register_class(map, &node.class);
  }

  fn register_class_expr(map: &mut ByteToIndexMap, node: &ClassExpr) {
    map.register_by_span(&node.span());
    node.ident.as_ref().map(|node| register_ident(map, node));
    register_class(map, &node.class);
  }

  fn register_class_method(map: &mut ByteToIndexMap, node: &ClassMethod) {
    map.register_by_span(&node.span);
    enum_register_prop_name(map, &node.key);
    register_function(map, &node.function);
  }

  fn register_class_prop(map: &mut ByteToIndexMap, node: &ClassProp) {
    map.register_by_span(&node.span);
    enum_register_prop_name(map, &node.key);
    node.value.as_ref().map(|node| enum_register_expr(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node.decorators.iter().for_each(|node| register_decorator(map, node));
  }

  fn register_computed_prop_name(map: &mut ByteToIndexMap, node: &ComputedPropName) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
  }

  fn register_cond_expr(map: &mut ByteToIndexMap, node: &CondExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.test);
    enum_register_expr(map, &node.cons);
    enum_register_expr(map, &node.alt);
  }

  fn register_constructor(map: &mut ByteToIndexMap, node: &Constructor) {
    map.register_by_span(&node.span);
    enum_register_prop_name(map, &node.key);
    node
      .params
      .iter()
      .for_each(|node| enum_register_param_or_ts_param_prop(map, node));
    node.body.as_ref().map(|node| register_block_stmt(map, node));
  }

  fn register_continue_stmt(map: &mut ByteToIndexMap, node: &ContinueStmt) {
    map.register_by_span(&node.span);
    node.label.as_ref().map(|node| register_ident(map, node));
  }

  fn register_debugger_stmt(map: &mut ByteToIndexMap, node: &DebuggerStmt) {
    map.register_by_span(&node.span);
  }

  fn register_decorator(map: &mut ByteToIndexMap, node: &Decorator) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr.as_ref());
  }

  fn register_do_while_stmt(map: &mut ByteToIndexMap, node: &DoWhileStmt) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.test);
    enum_register_stmt(map, &node.body);
  }

  fn register_empty_stmt(map: &mut ByteToIndexMap, node: &EmptyStmt) {
    map.register_by_span(&node.span);
  }

  fn register_export_all(map: &mut ByteToIndexMap, node: &ExportAll) {
    map.register_by_span(&node.span);
    register_str(map, &node.src);
    node.with.as_ref().map(|node| register_object_lit(map, node));
  }

  fn register_export_decl(map: &mut ByteToIndexMap, node: &ExportDecl) {
    map.register_by_span(&node.span);
    enum_register_decl(map, &node.decl);
  }

  fn register_export_default_decl(map: &mut ByteToIndexMap, node: &ExportDefaultDecl) {
    map.register_by_span(&node.span);
    enum_register_default_decl(map, &node.decl);
  }

  fn register_export_default_expr(map: &mut ByteToIndexMap, node: &ExportDefaultExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
  }

  fn register_export_default_specifier(map: &mut ByteToIndexMap, node: &ExportDefaultSpecifier) {
    register_ident(map, &node.exported);
  }

  fn register_export_named_specifier(map: &mut ByteToIndexMap, node: &ExportNamedSpecifier) {
    map.register_by_span(&node.span);
    enum_register_module_export_name(map, &node.orig);
    node
      .exported
      .as_ref()
      .map(|node| enum_register_module_export_name(map, node));
  }

  fn register_export_namespace_specifier(map: &mut ByteToIndexMap, node: &ExportNamespaceSpecifier) {
    map.register_by_span(&node.span);
    enum_register_module_export_name(map, &node.name);
  }

  fn register_expr_or_spread(map: &mut ByteToIndexMap, node: &ExprOrSpread) {
    node.spread.as_ref().map(|node| map.register_by_span(node));
    enum_register_expr(map, &node.expr);
  }

  fn register_expr_stmt(map: &mut ByteToIndexMap, node: &ExprStmt) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
  }

  fn register_fn_decl(map: &mut ByteToIndexMap, node: &FnDecl) {
    map.register_by_span(&node.span());
    register_ident(map, &node.ident);
    register_function(map, &node.function);
  }

  fn register_fn_expr(map: &mut ByteToIndexMap, node: &FnExpr) {
    map.register_by_span(&node.span());
    node.ident.as_ref().map(|node| register_ident(map, node));
    register_function(map, &node.function);
  }

  fn register_for_in_stmt(map: &mut ByteToIndexMap, node: &ForInStmt) {
    map.register_by_span(&node.span);
    enum_register_for_head(map, &node.left);
    enum_register_expr(map, &node.right);
    enum_register_stmt(map, &node.body);
  }

  fn register_for_of_stmt(map: &mut ByteToIndexMap, node: &ForOfStmt) {
    map.register_by_span(&node.span);
    enum_register_for_head(map, &node.left);
    enum_register_expr(map, &node.right);
    enum_register_stmt(map, &node.body);
  }

  fn register_for_stmt(map: &mut ByteToIndexMap, node: &ForStmt) {
    map.register_by_span(&node.span);
    node.init.as_ref().map(|node| enum_register_var_decl_or_expr(map, node));
    node.test.as_ref().map(|node| enum_register_expr(map, node));
    node.update.as_ref().map(|node| enum_register_expr(map, node));
    enum_register_stmt(map, &node.body);
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

  fn register_getter_prop(map: &mut ByteToIndexMap, node: &GetterProp) {
    map.register_by_span(&node.span);
    enum_register_prop_name(map, &node.key);
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node.body.as_ref().map(|node| register_block_stmt(map, node));
  }

  fn register_ident(map: &mut ByteToIndexMap, node: &Ident) {
    map.register_by_span(&node.span);
  }

  fn register_if_stmt(map: &mut ByteToIndexMap, node: &IfStmt) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.test);
    enum_register_stmt(map, &node.cons);
    node.alt.as_ref().map(|node| enum_register_stmt(map, node));
  }

  fn register_import(map: &mut ByteToIndexMap, node: &Import) {
    map.register_by_span(&node.span);
  }

  fn register_import_decl(map: &mut ByteToIndexMap, node: &ImportDecl) {
    map.register_by_span(&node.span);
    node
      .specifiers
      .iter()
      .for_each(|node| enum_register_import_specifier(map, node));
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
      .map(|node| enum_register_module_export_name(map, node));
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
    enum_register_jsx_attr_name(map, &node.name);
    node.value.as_ref().map(|node| enum_register_jsx_attr_value(map, node));
  }

  fn register_jsx_closing_element(map: &mut ByteToIndexMap, node: &JSXClosingElement) {
    map.register_by_span(&node.span);
    enum_register_jsx_element_name(map, &node.name);
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
      .for_each(|node| enum_register_jsx_element_child(map, node));
    node
      .closing
      .as_ref()
      .map(|node| register_jsx_closing_element(map, node));
  }

  fn register_jsx_empty_expr(map: &mut ByteToIndexMap, node: &JSXEmptyExpr) {
    map.register_by_span(&node.span);
  }

  fn register_jsx_expr_container(map: &mut ByteToIndexMap, node: &JSXExprContainer) {
    map.register_by_span(&node.span);
    enum_register_jsx_expr(map, &node.expr);
  }

  fn register_jsx_fragment(map: &mut ByteToIndexMap, node: &JSXFragment) {
    map.register_by_span(&node.span);
    register_jsx_opening_fragment(map, &node.opening);
    node
      .children
      .iter()
      .for_each(|node| enum_register_jsx_element_child(map, node));
    register_jsx_closing_fragment(map, &node.closing);
  }

  fn register_jsx_member_expr(map: &mut ByteToIndexMap, node: &JSXMemberExpr) {
    map.register_by_span(&node.span());
    enum_register_jsx_object(map, &node.obj);
    register_ident(map, &node.prop);
  }

  fn register_jsx_namespaced_name(map: &mut ByteToIndexMap, node: &JSXNamespacedName) {
    map.register_by_span(&node.span());
    register_ident(map, &node.name);
    register_ident(map, &node.ns);
  }

  fn register_jsx_opening_element(map: &mut ByteToIndexMap, node: &JSXOpeningElement) {
    map.register_by_span(&node.span);
    enum_register_jsx_element_name(map, &node.name);
    node
      .attrs
      .iter()
      .for_each(|node| enum_register_jsx_attr_or_spread(map, node));
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
    enum_register_expr(map, &node.expr);
  }

  fn register_jsx_text(map: &mut ByteToIndexMap, node: &JSXText) {
    map.register_by_span(&node.span);
  }

  fn register_key_value_pat_prop(map: &mut ByteToIndexMap, node: &KeyValuePatProp) {
    enum_register_prop_name(map, &node.key);
    enum_register_pat(map, &node.value);
  }

  fn register_key_value_prop(map: &mut ByteToIndexMap, node: &KeyValueProp) {
    enum_register_prop_name(map, &node.key);
    enum_register_expr(map, &node.value);
  }

  fn register_labeled_stmt(map: &mut ByteToIndexMap, node: &LabeledStmt) {
    map.register_by_span(&node.span);
    register_ident(map, &node.label);
    enum_register_stmt(map, &node.body);
  }

  fn register_member_expr(map: &mut ByteToIndexMap, node: &MemberExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.obj);
    enum_register_member_prop(map, &node.prop);
  }

  fn register_meta_prop_expr(map: &mut ByteToIndexMap, node: &MetaPropExpr) {
    map.register_by_span(&node.span);
  }

  fn register_method_prop(map: &mut ByteToIndexMap, node: &MethodProp) {
    map.register_by_span(&node.span());
    enum_register_prop_name(map, &node.key);
    register_function(map, &node.function);
  }

  fn register_module(map: &mut ByteToIndexMap, node: &Module) {
    map.register_by_span(&node.span);
    node.body.iter().for_each(|node| enum_register_module_item(map, &node));
  }

  fn register_named_export(map: &mut ByteToIndexMap, node: &NamedExport) {
    map.register_by_span(&node.span);
    node
      .specifiers
      .iter()
      .for_each(|node| enum_register_export_specifier(map, node));
    node.src.as_ref().map(|node| register_str(map, node));
    node.with.as_ref().map(|node| register_object_lit(map, node));
  }

  fn register_new_expr(map: &mut ByteToIndexMap, node: &NewExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.callee);
    node
      .args
      .as_ref()
      .map(|nodes| nodes.iter().for_each(|node| register_expr_or_spread(map, node)));
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_null(map: &mut ByteToIndexMap, node: &Null) {
    map.register_by_span(&node.span);
  }

  fn register_number(map: &mut ByteToIndexMap, node: &Number) {
    map.register_by_span(&node.span);
  }

  fn register_object_lit(map: &mut ByteToIndexMap, node: &ObjectLit) {
    map.register_by_span(&node.span);
    node
      .props
      .iter()
      .for_each(|node| enum_register_prop_or_spread(map, node));
  }

  fn register_object_pat(map: &mut ByteToIndexMap, node: &ObjectPat) {
    map.register_by_span(&node.span);
    node
      .props
      .iter()
      .for_each(|node| enum_register_object_pat_prop(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_opt_call(map: &mut ByteToIndexMap, node: &OptCall) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.callee);
    node.args.iter().for_each(|node| register_expr_or_spread(map, node));
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_opt_chain_expr(map: &mut ByteToIndexMap, node: &OptChainExpr) {
    map.register_by_span(&node.span);
    enum_register_opt_chain_base(map, &node.base);
  }

  fn register_param(map: &mut ByteToIndexMap, node: &Param) {
    map.register_by_span(&node.span);
    node.decorators.iter().for_each(|node| register_decorator(map, node));
    enum_register_pat(map, &node.pat);
  }

  fn register_paren_expr(map: &mut ByteToIndexMap, node: &ParenExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
  }

  fn register_private_method(map: &mut ByteToIndexMap, node: &PrivateMethod) {
    map.register_by_span(&node.span);
    register_private_name(map, &node.key);
    register_function(map, &node.function);
  }

  fn register_private_name(map: &mut ByteToIndexMap, node: &PrivateName) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
  }

  fn register_private_prop(map: &mut ByteToIndexMap, node: &PrivateProp) {
    map.register_by_span(&node.span);
    register_private_name(map, &node.key);
    node.value.as_ref().map(|node| enum_register_expr(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node.decorators.iter().for_each(|node| register_decorator(map, node));
  }

  fn register_regex(map: &mut ByteToIndexMap, node: &Regex) {
    map.register_by_span(&node.span);
  }

  fn register_rest_pat(map: &mut ByteToIndexMap, node: &RestPat) {
    map.register_by_span(&node.span);
    map.register_by_span(&node.dot3_token);
    enum_register_pat(map, &node.arg);
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_return_stmt(map: &mut ByteToIndexMap, node: &ReturnStmt) {
    map.register_by_span(&node.span);
    node.arg.as_ref().map(|node| enum_register_expr(map, node));
  }

  fn register_script(map: &mut ByteToIndexMap, node: &Script) {
    map.register_by_span(&node.span);
    node.body.iter().for_each(|node| enum_register_stmt(map, node))
  }

  fn register_seq_expr(map: &mut ByteToIndexMap, node: &SeqExpr) {
    map.register_by_span(&node.span);
    node.exprs.iter().for_each(|node| enum_register_expr(map, node));
  }

  fn register_setter_prop(map: &mut ByteToIndexMap, node: &SetterProp) {
    map.register_by_span(&node.span);
    enum_register_prop_name(map, &node.key);
    enum_register_pat(map, &node.param);
    node.body.as_ref().map(|node| register_block_stmt(map, node));
  }

  fn register_spread_element(map: &mut ByteToIndexMap, node: &SpreadElement) {
    map.register_by_span(&node.dot3_token);
    enum_register_expr(map, &node.expr);
  }

  fn register_static_block(map: &mut ByteToIndexMap, node: &StaticBlock) {
    map.register_by_span(&node.span);
    register_block_stmt(map, &node.body);
  }

  fn register_str(map: &mut ByteToIndexMap, node: &Str) {
    map.register_by_span(&node.span);
  }

  fn register_super(map: &mut ByteToIndexMap, node: &Super) {
    map.register_by_span(&node.span);
  }

  fn register_super_prop_expr(map: &mut ByteToIndexMap, node: &SuperPropExpr) {
    map.register_by_span(&node.span);
    register_super(map, &node.obj);
    enum_register_super_prop(map, &node.prop);
  }

  fn register_switch_case(map: &mut ByteToIndexMap, node: &SwitchCase) {
    map.register_by_span(&node.span);
    node.test.as_ref().map(|node| enum_register_expr(map, node));
    node.cons.iter().for_each(|node| enum_register_stmt(map, node));
  }

  fn register_switch_stmt(map: &mut ByteToIndexMap, node: &SwitchStmt) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.discriminant);
    node.cases.iter().for_each(|node| register_switch_case(map, node));
  }

  fn register_tagged_tpl(map: &mut ByteToIndexMap, node: &TaggedTpl) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.tag);
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
    register_tpl(map, &node.tpl);
  }

  fn register_this_expr(map: &mut ByteToIndexMap, node: &ThisExpr) {
    map.register_by_span(&node.span);
  }

  fn register_throw_stmt(map: &mut ByteToIndexMap, node: &ThrowStmt) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.arg);
  }

  fn register_tpl(map: &mut ByteToIndexMap, node: &Tpl) {
    map.register_by_span(&node.span);
    node.exprs.iter().for_each(|node| enum_register_expr(map, node));
    node.quasis.iter().for_each(|node| register_tpl_element(map, node));
  }

  fn register_tpl_element(map: &mut ByteToIndexMap, node: &TplElement) {
    map.register_by_span(&node.span);
  }

  fn register_ts_array_type(map: &mut ByteToIndexMap, node: &TsArrayType) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.elem_type);
  }

  fn register_ts_as_expr(map: &mut ByteToIndexMap, node: &TsAsExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_call_signature_decl(map: &mut ByteToIndexMap, node: &TsCallSignatureDecl) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| enum_register_ts_fn_param(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
  }

  fn register_ts_conditional_type(map: &mut ByteToIndexMap, node: &TsConditionalType) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.check_type);
    enum_register_ts_type(map, &node.extends_type);
    enum_register_ts_type(map, &node.true_type);
    enum_register_ts_type(map, &node.false_type);
  }

  fn register_ts_const_assertion(map: &mut ByteToIndexMap, node: &TsConstAssertion) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
  }

  fn register_ts_construct_signature_decl(map: &mut ByteToIndexMap, node: &TsConstructSignatureDecl) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| enum_register_ts_fn_param(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
  }

  fn register_ts_constructor_type(map: &mut ByteToIndexMap, node: &TsConstructorType) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| enum_register_ts_fn_param(map, node));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
    register_ts_type_ann(map, &node.type_ann);
  }

  fn register_ts_enum_decl(map: &mut ByteToIndexMap, node: &TsEnumDecl) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
    node.members.iter().for_each(|node| register_ts_enum_member(map, node));
  }

  fn register_ts_enum_member(map: &mut ByteToIndexMap, node: &TsEnumMember) {
    map.register_by_span(&node.span);
    enum_register_ts_enum_member_id(map, &node.id);
    node.init.as_ref().map(|node| enum_register_expr(map, node));
  }

  fn register_ts_export_assignment(map: &mut ByteToIndexMap, node: &TsExportAssignment) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
  }

  fn register_ts_external_module_ref(map: &mut ByteToIndexMap, node: &TsExternalModuleRef) {
    map.register_by_span(&node.span);
    register_str(map, &node.expr);
  }

  fn register_ts_expr_with_type_args(map: &mut ByteToIndexMap, node: &TsExprWithTypeArgs) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_ts_fn_type(map: &mut ByteToIndexMap, node: &TsFnType) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| enum_register_ts_fn_param(map, node));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
    register_ts_type_ann(map, &node.type_ann);
  }

  fn register_ts_getter_signature(map: &mut ByteToIndexMap, node: &TsGetterSignature) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.key);
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_ts_import_equals_decl(map: &mut ByteToIndexMap, node: &TsImportEqualsDecl) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
    enum_register_ts_module_ref(map, &node.module_ref);
  }

  fn register_ts_import_type(map: &mut ByteToIndexMap, node: &TsImportType) {
    map.register_by_span(&node.span);
    register_str(map, &node.arg);
    node
      .qualifier
      .as_ref()
      .map(|node| enum_register_ts_entity_name(map, node));
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, node));
  }

  fn register_ts_index_signature(map: &mut ByteToIndexMap, node: &TsIndexSignature) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| enum_register_ts_fn_param(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_ts_indexed_access_type(map: &mut ByteToIndexMap, node: &TsIndexedAccessType) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.obj_type);
    enum_register_ts_type(map, &node.index_type);
  }

  fn register_ts_infer_type(map: &mut ByteToIndexMap, node: &TsInferType) {
    map.register_by_span(&node.span);
    register_ts_type_param(map, &node.type_param);
  }

  fn register_ts_interface_body(map: &mut ByteToIndexMap, node: &TsInterfaceBody) {
    map.register_by_span(&node.span);
    node
      .body
      .iter()
      .for_each(|node| enum_register_ts_type_element(map, node));
  }

  fn register_ts_interface_decl(map: &mut ByteToIndexMap, node: &TsInterfaceDecl) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
    node
      .extends
      .iter()
      .for_each(|node| register_ts_expr_with_type_args(map, node));
    register_ts_interface_body(map, &node.body);
  }

  fn register_ts_intersection_type(map: &mut ByteToIndexMap, node: &TsIntersectionType) {
    map.register_by_span(&node.span);
    node.types.iter().for_each(|node| enum_register_ts_type(map, node));
  }

  fn register_ts_instantiation(map: &mut ByteToIndexMap, node: &TsInstantiation) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
    register_ts_type_param_instantiation(map, &node.type_args);
  }

  fn register_ts_keyword_type(map: &mut ByteToIndexMap, node: &TsKeywordType) {
    map.register_by_span(&node.span);
  }

  fn register_ts_lit_type(map: &mut ByteToIndexMap, node: &TsLitType) {
    map.register_by_span(&node.span);
    enum_register_ts_lit(map, &node.lit);
  }

  fn register_ts_mapped_type(map: &mut ByteToIndexMap, node: &TsMappedType) {
    map.register_by_span(&node.span);
    register_ts_type_param(map, &node.type_param);
    node.name_type.as_ref().map(|node| enum_register_ts_type(map, node));
    node.type_ann.as_ref().map(|node| enum_register_ts_type(map, node));
  }

  fn register_ts_method_signature(map: &mut ByteToIndexMap, node: &TsMethodSignature) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.key);
    node.params.iter().for_each(|node| enum_register_ts_fn_param(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
  }

  fn register_ts_module_block(map: &mut ByteToIndexMap, node: &TsModuleBlock) {
    map.register_by_span(&node.span);
    node.body.iter().for_each(|node| enum_register_module_item(map, node));
  }

  fn register_ts_module_decl(map: &mut ByteToIndexMap, node: &TsModuleDecl) {
    map.register_by_span(&node.span);
    enum_register_ts_module_name(map, &node.id);
    node
      .body
      .as_ref()
      .map(|node| enum_register_ts_namespace_body(map, node));
  }

  fn register_ts_namespace_decl(map: &mut ByteToIndexMap, node: &TsNamespaceDecl) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
    enum_register_ts_namespace_body(map, &node.body);
  }

  fn register_ts_namespace_export_decl(map: &mut ByteToIndexMap, node: &TsNamespaceExportDecl) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
  }

  fn register_ts_non_null_expr(map: &mut ByteToIndexMap, node: &TsNonNullExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
  }

  fn register_ts_optional_type(map: &mut ByteToIndexMap, node: &TsOptionalType) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_param_prop(map: &mut ByteToIndexMap, node: &TsParamProp) {
    map.register_by_span(&node.span);
    node.decorators.iter().for_each(|node| register_decorator(map, node));
    enum_register_ts_param_prop_param(map, &node.param);
  }

  fn register_ts_parenthesized_type(map: &mut ByteToIndexMap, node: &TsParenthesizedType) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_property_signature(map: &mut ByteToIndexMap, node: &TsPropertySignature) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.key);
    node.init.as_ref().map(|node| enum_register_expr(map, node));
    node.params.iter().for_each(|node| enum_register_ts_fn_param(map, node));
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node));
  }

  fn register_ts_qualified_name(map: &mut ByteToIndexMap, node: &TsQualifiedName) {
    map.register_by_span(&node.span());
    enum_register_ts_entity_name(map, &node.left);
    register_ident(map, &node.right);
  }

  fn register_ts_rest_type(map: &mut ByteToIndexMap, node: &TsRestType) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_satisfies_expr(map: &mut ByteToIndexMap, node: &TsSatisfiesExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_setter_signature(map: &mut ByteToIndexMap, node: &TsSetterSignature) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.key);
    enum_register_ts_fn_param(map, &node.param);
  }

  fn register_ts_this_type(map: &mut ByteToIndexMap, node: &TsThisType) {
    map.register_by_span(&node.span);
  }

  fn register_ts_tpl_lit_type(map: &mut ByteToIndexMap, node: &TsTplLitType) {
    map.register_by_span(&node.span);
    node.types.iter().for_each(|node| enum_register_ts_type(map, node));
    node.quasis.iter().for_each(|node| register_tpl_element(map, node));
  }

  fn register_ts_type_alias_decl(map: &mut ByteToIndexMap, node: &TsTypeAliasDecl) {
    map.register_by_span(&node.span);
    register_ident(map, &node.id);
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_decl(map, node.as_ref()));
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_type_ann(map: &mut ByteToIndexMap, node: &TsTypeAnn) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_type_assertion(map: &mut ByteToIndexMap, node: &TsTypeAssertion) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.expr);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_type_lit(map: &mut ByteToIndexMap, node: &TsTypeLit) {
    map.register_by_span(&node.span);
    node
      .members
      .iter()
      .for_each(|node| enum_register_ts_type_element(map, node));
  }

  fn register_ts_type_operator(map: &mut ByteToIndexMap, node: &TsTypeOperator) {
    map.register_by_span(&node.span);
    enum_register_ts_type(map, &node.type_ann);
  }

  fn register_ts_type_param(map: &mut ByteToIndexMap, node: &TsTypeParam) {
    map.register_by_span(&node.span);
    register_ident(map, &node.name);
    node.constraint.as_ref().map(|node| enum_register_ts_type(map, node));
    node.default.as_ref().map(|node| enum_register_ts_type(map, node));
  }

  fn register_ts_type_param_decl(map: &mut ByteToIndexMap, node: &TsTypeParamDecl) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| register_ts_type_param(map, node));
  }

  fn register_ts_type_param_instantiation(map: &mut ByteToIndexMap, node: &TsTypeParamInstantiation) {
    map.register_by_span(&node.span);
    node.params.iter().for_each(|node| enum_register_ts_type(map, node));
  }

  fn register_ts_type_predicate(map: &mut ByteToIndexMap, node: &TsTypePredicate) {
    map.register_by_span(&node.span);
    enum_register_ts_this_type_or_ident(map, &node.param_name);
    node.type_ann.as_ref().map(|node| register_ts_type_ann(map, node));
  }

  fn register_ts_type_query(map: &mut ByteToIndexMap, node: &TsTypeQuery) {
    map.register_by_span(&node.span);
    enum_register_ts_type_query_expr(map, &node.expr_name);
    node
      .type_args
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, &node));
  }

  fn register_ts_type_ref(map: &mut ByteToIndexMap, node: &TsTypeRef) {
    map.register_by_span(&node.span);
    enum_register_ts_entity_name(map, &node.type_name);
    node
      .type_params
      .as_ref()
      .map(|node| register_ts_type_param_instantiation(map, node));
  }

  fn register_ts_tuple_element(map: &mut ByteToIndexMap, node: &TsTupleElement) {
    map.register_by_span(&node.span);
    node.label.as_ref().map(|node| enum_register_pat(map, node));
    enum_register_ts_type(map, &node.ty);
  }

  fn register_ts_tuple_type(map: &mut ByteToIndexMap, node: &TsTupleType) {
    map.register_by_span(&node.span);
    node
      .elem_types
      .iter()
      .for_each(|node| register_ts_tuple_element(map, node));
  }

  fn register_ts_union_type(map: &mut ByteToIndexMap, node: &TsUnionType) {
    map.register_by_span(&node.span);
    node.types.iter().for_each(|node| enum_register_ts_type(map, node));
  }

  fn register_try_stmt(map: &mut ByteToIndexMap, node: &TryStmt) {
    map.register_by_span(&node.span);
    register_block_stmt(map, &node.block);
    node.handler.as_ref().map(|node| register_catch_clause(map, node));
    node.finalizer.as_ref().map(|node| register_block_stmt(map, node));
  }

  fn register_unary_expr(map: &mut ByteToIndexMap, node: &UnaryExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.arg);
  }

  fn register_update_expr(map: &mut ByteToIndexMap, node: &UpdateExpr) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.arg);
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
    enum_register_pat(map, &node.name);
  }

  fn register_while_stmt(map: &mut ByteToIndexMap, node: &WhileStmt) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.test);
    enum_register_stmt(map, &node.body);
  }

  fn register_with_stmt(map: &mut ByteToIndexMap, node: &WithStmt) {
    map.register_by_span(&node.span);
    enum_register_expr(map, &node.obj);
    enum_register_stmt(map, &node.body);
  }

  fn register_yield_expr(map: &mut ByteToIndexMap, node: &YieldExpr) {
    map.register_by_span(&node.span);
    node.arg.as_ref().map(|node| enum_register_expr(map, node));
  }
}

pub mod program {
  use jni::objects::JObject;
  use jni::JNIEnv;

  use crate::ast_utils::JAVA_AST_FACTORY;
  use crate::enums::IdentifiableEnum;
  use crate::jni_utils::*;
  use crate::position_utils::ByteToIndexMap;

  use std::sync::Arc;

  use deno_ast::swc::ast::*;
  use deno_ast::swc::common::Spanned;

  fn create_array_lit<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ArrayLit) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_elems = java_array_list.construct(env, node.elems.len());
    node.elems.iter().for_each(|node| {
      let java_node = node
        .as_ref()
        .map_or_else(|| Default::default(), |node| create_expr_or_spread(env, map, node));
      java_array_list.add(env, &java_elems, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_type = java_ast_factory.create_array_lit(env, &java_elems, &java_range);
    delete_local_ref!(env, java_elems);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_array_pat<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ArrayPat) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_elems = java_array_list.construct(env, node.elems.len());
    node.elems.iter().for_each(|node| {
      let java_node = node
        .as_ref()
        .map_or_else(|| Default::default(), |node| enum_create_pat(env, map, node));
      java_array_list.add(env, &java_elems, &java_node);
      delete_local_ref!(env, java_node);
    });
    let optional = node.optional;
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let return_type =
      java_ast_factory.create_array_pat(env, &java_elems, optional, &java_optional_type_ann, &java_range);
    delete_local_ref!(env, java_elems);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_arrow_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ArrowExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = enum_create_pat(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_body = enum_create_block_stmt_or_expr(env, map, &node.body);
    let is_async = node.is_async;
    let is_generator = node.is_generator;
    let java_optional_type_params = node
      .type_params
      .as_ref()
      .map(|node| create_ts_type_param_decl(env, map, node));
    let java_optional_return_type = node.return_type.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let return_type = java_ast_factory.create_arrow_expr(
      env,
      &java_params,
      &java_body,
      is_async,
      is_generator,
      &java_optional_type_params,
      &java_optional_return_type,
      &java_range,
    );
    delete_local_ref!(env, java_params);
    delete_local_ref!(env, java_body);
    delete_local_optional_ref!(env, java_optional_type_params);
    delete_local_optional_ref!(env, java_optional_return_type);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_assign_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &AssignExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let op = node.op.get_id();
    let java_left = enum_create_pat_or_expr(env, map, &node.left);
    let java_right = enum_create_expr(env, map, &node.right);
    let return_type = java_ast_factory.create_assign_expr(env, op, &java_left, &java_right, &java_range);
    delete_local_ref!(env, java_left);
    delete_local_ref!(env, java_right);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_assign_pat<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &AssignPat) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_left = enum_create_pat(env, map, &node.left);
    let java_right = enum_create_expr(env, map, &node.right);
    let return_type = java_ast_factory.create_assign_pat(env, &java_left, &java_right, &java_range);
    delete_local_ref!(env, java_left);
    delete_local_ref!(env, java_right);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_assign_pat_prop<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &AssignPatProp,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = create_ident(env, map, &node.key);
    let java_optional_value = node.value.as_ref().map(|node| enum_create_expr(env, map, node));
    let return_type = java_ast_factory.create_assign_pat_prop(env, &java_key, &java_optional_value, &java_range);
    delete_local_ref!(env, java_key);
    delete_local_optional_ref!(env, java_optional_value);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_assign_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &AssignProp) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_key = create_ident(env, map, &node.key);
    let java_value = enum_create_expr(env, map, &node.value);
    let return_type = java_ast_factory.create_assign_prop(env, &java_key, &java_value, &java_range);
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_auto_accessor<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &AutoAccessor,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = enum_create_key(env, map, &node.key);
    let java_optional_value = node.value.as_ref().map(|node| enum_create_expr(env, map, node));
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let is_static = node.is_static;
    let java_decorators = java_array_list.construct(env, node.decorators.len());
    node.decorators.iter().for_each(|node| {
      let java_node = create_decorator(env, map, node);
      java_array_list.add(env, &java_decorators, &java_node);
      delete_local_ref!(env, java_node);
    });
    let accessibility = node.accessibility.map_or(-1, |node| node.get_id());
    let return_type = java_ast_factory.create_auto_accessor(
      env,
      &java_key,
      &java_optional_value,
      &java_optional_type_ann,
      is_static,
      &java_decorators,
      accessibility,
      &java_range,
    );
    delete_local_ref!(env, java_key);
    delete_local_optional_ref!(env, java_optional_value);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_decorators);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_await_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &AwaitExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_arg = enum_create_expr(env, map, &node.arg);
    let return_type = java_ast_factory.create_await_expr(env, &java_arg, &java_range);
    delete_local_ref!(env, java_arg);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_big_int<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &BigInt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let sign = node.value.sign().get_id();
    let optional_raw = node.raw.as_ref().map(|node| node.as_str().to_owned());
    let return_value = java_ast_factory.create_big_int(env, sign, &optional_raw, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_bin_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &BinExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let op = node.op.get_id();
    let java_left = enum_create_expr(env, map, &node.left);
    let java_right = enum_create_expr(env, map, &node.right);
    let return_type = java_ast_factory.create_bin_expr(env, op, &java_left, &java_right, &java_range);
    delete_local_ref!(env, java_left);
    delete_local_ref!(env, java_right);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_binding_ident<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &BindingIdent,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_id = create_ident(env, map, &node.id);
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let return_value = java_ast_factory.create_binding_ident(env, &java_id, &java_optional_type_ann, &java_range);
    delete_local_ref!(env, java_id);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_block_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &BlockStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_stmts = java_array_list.construct(env, node.stmts.len());
    node.stmts.iter().for_each(|node| {
      let java_node = enum_create_stmt(env, map, node);
      java_array_list.add(env, &java_stmts, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_block_stmt(env, &java_stmts, &java_range);
    delete_local_ref!(env, java_stmts);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_bool<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Bool) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let value = node.value;
    let return_value = java_ast_factory.create_bool(env, value, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_break_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &BreakStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_optional_label = node.label.as_ref().map(|node| create_ident(env, map, node));
    let return_value = java_ast_factory.create_break_stmt(env, &java_optional_label, &java_range);
    delete_local_optional_ref!(env, java_optional_label);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_call_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &CallExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_callee = enum_create_callee(env, map, &node.callee);
    let java_args = java_array_list.construct(env, node.args.len());
    node.args.iter().for_each(|node| {
      let java_node = create_expr_or_spread(env, map, node);
      java_array_list.add(env, &java_args, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_type_args = node
      .type_args
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let return_type =
      java_ast_factory.create_call_expr(env, &java_callee, &java_args, &java_optional_type_args, &java_range);
    delete_local_ref!(env, java_callee);
    delete_local_ref!(env, java_args);
    delete_local_optional_ref!(env, java_optional_type_args);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_catch_clause<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &CatchClause) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_optional_param = node.param.as_ref().map(|node| enum_create_pat(env, map, node));
    let java_body = create_block_stmt(env, map, &node.body);
    let return_type = java_ast_factory.create_catch_clause(env, &java_optional_param, &java_body, &java_range);
    delete_local_optional_ref!(env, java_optional_param);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_class<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Class) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_decorators = java_array_list.construct(env, node.decorators.len());
    node.decorators.iter().for_each(|node| {
      let java_node = create_decorator(env, map, node);
      java_array_list.add(env, &java_decorators, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_body = java_array_list.construct(env, node.body.len());
    node.body.iter().for_each(|node| {
      let java_node = enum_create_class_member(env, map, node);
      java_array_list.add(env, &java_body, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_super_class = node.super_class.as_ref().map(|node| enum_create_expr(env, map, node));
    let is_abstract = node.is_abstract;
    let java_optional_type_params = node
      .type_params
      .as_ref()
      .map(|node| create_ts_type_param_decl(env, map, node));
    let java_optional_super_type_params = node
      .super_type_params
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let java_implements = java_array_list.construct(env, node.implements.len());
    node.implements.iter().for_each(|node| {
      let java_node = create_ts_expr_with_type_args(env, map, node);
      java_array_list.add(env, &java_implements, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_class(
      env,
      &java_decorators,
      &java_body,
      &java_optional_super_class,
      is_abstract,
      &java_optional_type_params,
      &java_optional_super_type_params,
      &java_implements,
      &java_range,
    );
    delete_local_ref!(env, java_decorators);
    delete_local_ref!(env, java_body);
    delete_local_optional_ref!(env, java_optional_super_class);
    delete_local_optional_ref!(env, java_optional_type_params);
    delete_local_optional_ref!(env, java_optional_super_type_params);
    delete_local_ref!(env, java_implements);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_class_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ClassDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_ident = create_ident(env, map, &node.ident);
    let declare = node.declare;
    let java_class = create_class(env, map, &node.class);
    let return_value = java_ast_factory.create_class_decl(env, &java_ident, declare, &java_class, &java_range);
    delete_local_ref!(env, java_ident);
    delete_local_ref!(env, java_class);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_class_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ClassExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_optional_ident = node.ident.as_ref().map(|node| create_ident(env, map, node));
    let java_class = create_class(env, map, &node.class);
    let return_type = java_ast_factory.create_class_expr(env, &java_optional_ident, &java_class, &java_range);
    delete_local_optional_ref!(env, java_optional_ident);
    delete_local_ref!(env, java_class);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_class_method<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ClassMethod) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_function = create_function(env, map, &node.function);
    let kind = node.kind.get_id();
    let is_static = node.is_static;
    let accessibility = node.accessibility.map_or(-1, |node| node.get_id());
    let is_abstract = node.is_abstract;
    let is_optional = node.is_optional;
    let is_override = node.is_override;
    let return_type = java_ast_factory.create_class_method(
      env,
      &java_key,
      &java_function,
      kind,
      is_static,
      accessibility,
      is_abstract,
      is_optional,
      is_override,
      &java_range,
    );
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_function);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_class_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ClassProp) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_optional_value = node.value.as_ref().map(|node| enum_create_expr(env, map, node));
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let is_static = node.is_static;
    let java_decorators = java_array_list.construct(env, node.decorators.len());
    node.decorators.iter().for_each(|node| {
      let java_node = create_decorator(env, map, node);
      java_array_list.add(env, &java_decorators, &java_node);
      delete_local_ref!(env, java_node);
    });
    let accessibility = node.accessibility.map_or(-1, |node| node.get_id());
    let is_abstract = node.is_abstract;
    let is_optional = node.is_optional;
    let is_override = node.is_override;
    let readonly = node.readonly;
    let declare = node.declare;
    let definite = node.definite;
    let return_type = java_ast_factory.create_class_prop(
      env,
      &java_key,
      &java_optional_value,
      &java_optional_type_ann,
      is_static,
      &java_decorators,
      accessibility,
      is_abstract,
      is_optional,
      is_override,
      readonly,
      declare,
      definite,
      &java_range,
    );
    delete_local_ref!(env, java_key);
    delete_local_optional_ref!(env, java_optional_value);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_decorators);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_computed_prop_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ComputedPropName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_type = java_ast_factory.create_computed_prop_name(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_cond_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &CondExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_test = enum_create_expr(env, map, &node.test);
    let java_cons = enum_create_expr(env, map, &node.cons);
    let java_alt = enum_create_expr(env, map, &node.alt);
    let return_type = java_ast_factory.create_cond_expr(env, &java_test, &java_cons, &java_alt, &java_range);
    delete_local_ref!(env, java_test);
    delete_local_ref!(env, java_cons);
    delete_local_ref!(env, java_alt);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_constructor<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Constructor) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = enum_create_param_or_ts_param_prop(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_body = node.body.as_ref().map(|node| create_block_stmt(env, map, node));
    let accessibility = node.accessibility.map_or(-1, |node| node.get_id());
    let is_optional = node.is_optional;
    let return_type = java_ast_factory.create_constructor(
      env,
      &java_key,
      &java_params,
      &java_optional_body,
      accessibility,
      is_optional,
      &java_range,
    );
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_params);
    delete_local_optional_ref!(env, java_optional_body);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_continue_stmt<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ContinueStmt,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_optional_label = node.label.as_ref().map(|node| create_ident(env, map, node));
    let return_value = java_ast_factory.create_continue_stmt(env, &java_optional_label, &java_range);
    delete_local_optional_ref!(env, java_optional_label);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_decorator<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Decorator) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_type = java_ast_factory.create_decorator(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_debugger_stmt<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &DebuggerStmt,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_type = java_ast_factory.create_debugger_stmt(env, &java_range);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_do_while_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &DoWhileStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.test);
    let java_body = enum_create_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_do_while_stmt(env, &java_expr, &java_body, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_empty_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &EmptyStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_type = java_ast_factory.create_empty_stmt(env, &java_range);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_export_all<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ExportAll) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_src = create_str(env, map, &node.src);
    let type_only = node.type_only;
    let java_optional_with = node.with.as_ref().map(|node| create_object_lit(env, map, node));
    let return_value = java_ast_factory.create_export_all(env, &java_src, type_only, &java_optional_with, &java_range);
    delete_local_ref!(env, java_src);
    delete_local_optional_ref!(env, java_optional_with);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_export_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ExportDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_decl = enum_create_decl(env, map, &node.decl);
    let return_value = java_ast_factory.create_export_decl(env, &java_decl, &java_range);
    delete_local_ref!(env, java_decl);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_export_default_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ExportDefaultDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_decl = enum_create_default_decl(env, map, &node.decl);
    let return_value = java_ast_factory.create_export_default_decl(env, &java_decl, &java_range);
    delete_local_ref!(env, java_decl);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_export_default_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ExportDefaultExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_export_default_expr(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_export_default_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ExportDefaultSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_exported = create_ident(env, map, &node.exported);
    let return_value = java_ast_factory.create_export_default_specifier(env, &java_exported, &java_range);
    delete_local_ref!(env, java_exported);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_export_named_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ExportNamedSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_orig = enum_create_module_export_name(env, map, &node.orig);
    let java_optional_exported = node
      .exported
      .as_ref()
      .map(|node| enum_create_module_export_name(env, map, node));
    let is_type_only = node.is_type_only;
    let return_value = java_ast_factory.create_export_named_specifier(
      env,
      &java_orig,
      &java_optional_exported,
      is_type_only,
      &java_range,
    );
    delete_local_ref!(env, java_orig);
    delete_local_optional_ref!(env, java_optional_exported);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_export_namespace_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ExportNamespaceSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_name = enum_create_module_export_name(env, map, &node.name);
    let return_value = java_ast_factory.create_export_namespace_specifier(env, &java_name, &java_range);
    delete_local_ref!(env, java_name);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_expr_or_spread<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ExprOrSpread,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_optional_spread = node
      .spread
      .as_ref()
      .map(|node| java_ast_factory.create_span(env, &map.get_range_by_span(node)));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_expr_or_spread(env, &java_optional_spread, &java_expr, &java_range);
    delete_local_optional_ref!(env, java_optional_spread);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_expr_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ExprStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_expr_stmt(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_fn_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &FnDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_ident = create_ident(env, map, &node.ident);
    let declare = node.declare;
    let java_function = create_function(env, map, &node.function);
    let return_value = java_ast_factory.create_fn_decl(env, &java_ident, declare, &java_function, &java_range);
    delete_local_ref!(env, java_ident);
    delete_local_ref!(env, java_function);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_fn_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &FnExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_optional_ident = node.ident.as_ref().map(|node| create_ident(env, map, node));
    let java_function = create_function(env, map, &node.function);
    let return_type = java_ast_factory.create_fn_expr(env, &java_optional_ident, &java_function, &java_range);
    delete_local_optional_ref!(env, java_optional_ident);
    delete_local_ref!(env, java_function);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_for_in_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ForInStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_left = enum_create_for_head(env, map, &node.left);
    let java_right = enum_create_expr(env, map, &node.right);
    let java_body = enum_create_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_for_in_stmt(env, &java_left, &java_right, &java_body, &java_range);
    delete_local_ref!(env, java_left);
    delete_local_ref!(env, java_right);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_for_of_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ForOfStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let is_await = node.is_await;
    let java_left = enum_create_for_head(env, map, &node.left);
    let java_right = enum_create_expr(env, map, &node.right);
    let java_body = enum_create_stmt(env, map, &node.body);
    let return_value =
      java_ast_factory.create_for_of_stmt(env, is_await, &java_left, &java_right, &java_body, &java_range);
    delete_local_ref!(env, java_left);
    delete_local_ref!(env, java_right);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_for_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ForStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_optional_init = node
      .init
      .as_ref()
      .map(|node| enum_create_var_decl_or_expr(env, map, node));
    let java_optional_test = node.test.as_ref().map(|node| enum_create_expr(env, map, node));
    let java_optional_update = node.update.as_ref().map(|node| enum_create_expr(env, map, node));
    let java_body = enum_create_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_for_stmt(
      env,
      &java_optional_init,
      &java_optional_test,
      &java_optional_update,
      &java_body,
      &java_range,
    );
    delete_local_optional_ref!(env, java_optional_init);
    delete_local_optional_ref!(env, java_optional_test);
    delete_local_optional_ref!(env, java_optional_update);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_function<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Function) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = create_param(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_decorators = java_array_list.construct(env, node.decorators.len());
    node.decorators.iter().for_each(|node| {
      let java_node = create_decorator(env, map, node);
      java_array_list.add(env, &java_decorators, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_body = node.body.as_ref().map(|node| create_block_stmt(env, map, node));
    let is_generator = node.is_generator;
    let is_async = node.is_async;
    let java_optional_type_params = node
      .type_params
      .as_ref()
      .map(|node| create_ts_type_param_decl(env, map, node));
    let java_optional_return_type = node.return_type.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let return_value = java_ast_factory.create_function(
      env,
      &java_params,
      &java_decorators,
      &java_optional_body,
      is_generator,
      is_async,
      &java_optional_type_params,
      &java_optional_return_type,
      &java_range,
    );
    delete_local_ref!(env, java_params);
    delete_local_ref!(env, java_decorators);
    delete_local_optional_ref!(env, java_optional_body);
    delete_local_optional_ref!(env, java_optional_type_params);
    delete_local_optional_ref!(env, java_optional_return_type);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_getter_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &GetterProp) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let java_optional_body = node.body.as_ref().map(|node| create_block_stmt(env, map, node));
    let return_type = java_ast_factory.create_getter_prop(
      env,
      &java_key,
      &java_optional_type_ann,
      &java_optional_body,
      &java_range,
    );
    delete_local_ref!(env, java_key);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_optional_ref!(env, java_optional_body);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_if_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &IfStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_test = enum_create_expr(env, map, &node.test);
    let java_cons = enum_create_stmt(env, map, &node.cons);
    let java_optional_alt = node.alt.as_ref().map(|node| enum_create_stmt(env, map, node));
    let return_value = java_ast_factory.create_if_stmt(env, &java_test, &java_cons, &java_optional_alt, &java_range);
    delete_local_ref!(env, java_test);
    delete_local_ref!(env, java_cons);
    delete_local_optional_ref!(env, java_optional_alt);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_import<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Import) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_type = java_ast_factory.create_import(env, &java_range);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_import_default_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ImportDefaultSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_local = create_ident(env, map, &node.local);
    let return_value = java_ast_factory.create_import_default_specifier(env, &java_local, &java_range);
    delete_local_ref!(env, java_local);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_import_named_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ImportNamedSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_local = create_ident(env, map, &node.local);
    let java_optional_imported = node
      .imported
      .as_ref()
      .map(|node| enum_create_module_export_name(env, map, node));
    let is_type_only = node.is_type_only;
    let return_value = java_ast_factory.create_import_named_specifier(
      env,
      &java_local,
      &java_optional_imported,
      is_type_only,
      &java_range,
    );
    delete_local_ref!(env, java_local);
    delete_local_optional_ref!(env, java_optional_imported);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_import_star_as_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ImportStarAsSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_local = create_ident(env, map, &node.local);
    let return_value = java_ast_factory.create_import_star_as_specifier(env, &java_local, &java_range);
    delete_local_ref!(env, java_local);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_invalid<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Invalid) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_value = java_ast_factory.create_invalid(env, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_jsx_closing_element<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXClosingElement,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_name = enum_create_jsx_element_name(env, map, &node.name);
    let return_value = java_ast_factory.create_jsx_closing_element(env, &java_name, &java_range);
    delete_local_ref!(env, java_name);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_jsx_closing_fragment<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXClosingFragment,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_value = java_ast_factory.create_jsx_closing_fragment(env, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_jsx_element<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXElement) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_opening = create_jsx_opening_element(env, map, &node.opening);
    let java_children = java_array_list.construct(env, node.children.len());
    node.children.iter().for_each(|node| {
      let java_node = enum_create_jsx_element_child(env, map, node);
      java_array_list.add(env, &java_children, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_closing = node
      .closing
      .as_ref()
      .map(|node| create_jsx_closing_element(env, map, node));
    let return_type =
      java_ast_factory.create_jsx_element(env, &java_opening, &java_children, &java_optional_closing, &java_range);
    delete_local_ref!(env, java_opening);
    delete_local_ref!(env, java_children);
    delete_local_optional_ref!(env, java_optional_closing);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_jsx_expr_container<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXExprContainer,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_jsx_expr(env, map, &node.expr);
    let return_type = java_ast_factory.create_jsx_expr_container(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_jsx_empty_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXEmptyExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_type = java_ast_factory.create_jsx_empty_expr(env, &java_range);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_jsx_fragment<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXFragment) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_opening = create_jsx_opening_fragment(env, map, &node.opening);
    let java_children = java_array_list.construct(env, node.children.len());
    node.children.iter().for_each(|node| {
      let java_node = enum_create_jsx_element_child(env, map, node);
      java_array_list.add(env, &java_children, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_closing = create_jsx_closing_fragment(env, map, &node.closing);
    let return_type =
      java_ast_factory.create_jsx_fragment(env, &java_opening, &java_children, &java_closing, &java_range);
    delete_local_ref!(env, java_opening);
    delete_local_ref!(env, java_children);
    delete_local_ref!(env, java_closing);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_jsx_member_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXMemberExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_obj = enum_create_jsx_object(env, map, &node.obj);
    let java_prop = create_ident(env, map, &node.prop);
    let return_type = java_ast_factory.create_jsx_member_expr(env, &java_obj, &java_prop, &java_range);
    delete_local_ref!(env, java_obj);
    delete_local_ref!(env, java_prop);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_jsx_namespaced_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXNamespacedName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_ns = create_ident(env, map, &node.ns);
    let java_name = create_ident(env, map, &node.name);
    let return_type = java_ast_factory.create_jsx_namespaced_name(env, &java_ns, &java_name, &java_range);
    delete_local_ref!(env, java_ns);
    delete_local_ref!(env, java_name);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_jsx_opening_element<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXOpeningElement,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_name = enum_create_jsx_element_name(env, map, &node.name);
    let java_attrs = java_array_list.construct(env, node.attrs.len());
    node.attrs.iter().for_each(|node| {
      let java_node = enum_create_jsx_attr_or_spread(env, map, node);
      java_array_list.add(env, &java_attrs, &java_node);
      delete_local_ref!(env, java_node);
    });
    let self_closing = node.self_closing;
    let java_optional_type_args = node
      .type_args
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let return_value = java_ast_factory.create_jsx_opening_element(
      env,
      &java_name,
      &java_attrs,
      self_closing,
      &java_optional_type_args,
      &java_range,
    );
    delete_local_ref!(env, java_name);
    delete_local_ref!(env, java_attrs);
    delete_local_optional_ref!(env, java_optional_type_args);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_jsx_opening_fragment<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXOpeningFragment,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_value = java_ast_factory.create_jsx_opening_fragment(env, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_jsx_spread_child<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXSpreadChild,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_type = java_ast_factory.create_jsx_spread_child(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_jsx_text<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXText) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let value = node.value.as_str();
    let raw = node.raw.as_str();
    let return_value = java_ast_factory.create_jsx_text(env, value, raw, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ident<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Ident) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let sym = node.sym.as_str();
    let optional = node.optional;
    let return_value = java_ast_factory.create_ident(env, sym, optional, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_import_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ImportDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_specifiers = java_array_list.construct(env, node.specifiers.len());
    node.specifiers.iter().for_each(|node| {
      let java_node = enum_create_import_specifier(env, map, node);
      java_array_list.add(env, &java_specifiers, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_src = create_str(env, map, &node.src);
    let type_only = node.type_only;
    let java_optional_with = node.with.as_ref().map(|node| create_object_lit(env, map, node));
    let return_value = java_ast_factory.create_import_decl(
      env,
      &java_specifiers,
      &java_src,
      type_only,
      &java_optional_with,
      &java_range,
    );
    delete_local_ref!(env, java_specifiers);
    delete_local_ref!(env, java_src);
    delete_local_optional_ref!(env, java_optional_with);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_key_value_pat_prop<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &KeyValuePatProp,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_value = enum_create_pat(env, map, &node.value);
    let return_type = java_ast_factory.create_key_value_pat_prop(env, &java_key, &java_value, &java_range);
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_key_value_prop<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &KeyValueProp,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_value = enum_create_expr(env, map, &node.value);
    let return_type = java_ast_factory.create_key_value_prop(env, &java_key, &java_value, &java_range);
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_labeled_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &LabeledStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_label = create_ident(env, map, &node.label);
    let java_body = enum_create_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_labeled_stmt(env, &java_label, &java_body, &java_range);
    delete_local_ref!(env, java_label);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_member_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &MemberExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_obj = enum_create_expr(env, map, &node.obj);
    let java_prop = enum_create_member_prop(env, map, &node.prop);
    let return_type = java_ast_factory.create_member_expr(env, &java_obj, &java_prop, &java_range);
    delete_local_ref!(env, java_obj);
    delete_local_ref!(env, java_prop);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_meta_prop_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &MetaPropExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let kind = node.kind.get_id();
    let return_type = java_ast_factory.create_meta_prop_expr(env, kind, &java_range);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_method_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &MethodProp) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_function = create_function(env, map, &node.function);
    let return_type = java_ast_factory.create_method_prop(env, &java_key, &java_function, &java_range);
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_function);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_module<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Module) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let optional_shebang: Option<String> = node.shebang.to_owned().map(|s| s.to_string());
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_body = java_array_list.construct(env, node.body.len());
    node.body.iter().for_each(|node| {
      let java_node = enum_create_module_item(env, map, node);
      java_array_list.add(env, &java_body, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_module(env, &java_body, &optional_shebang, &java_range);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_named_export<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &NamedExport) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_specifiers = java_array_list.construct(env, node.specifiers.len());
    node.specifiers.iter().for_each(|node| {
      let java_node = enum_create_export_specifier(env, map, node);
      java_array_list.add(env, &java_specifiers, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_src = node.src.as_ref().map(|node| create_str(env, map, node));
    let type_only = node.type_only;
    let java_optional_with = node.with.as_ref().map(|node| create_object_lit(env, map, node));
    let return_value = java_ast_factory.create_named_export(
      env,
      &java_specifiers,
      &java_optional_src,
      type_only,
      &java_optional_with,
      &java_range,
    );
    delete_local_ref!(env, java_specifiers);
    delete_local_optional_ref!(env, java_optional_src);
    delete_local_optional_ref!(env, java_optional_with);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_new_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &NewExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_callee = enum_create_expr(env, map, &node.callee);
    let java_optional_args = node.args.as_ref().map(|node| {
      let java_args = java_array_list.construct(env, node.len());
      node.iter().for_each(|node| {
        let java_node = create_expr_or_spread(env, map, node);
        java_array_list.add(env, &java_args, &java_node);
        delete_local_ref!(env, java_node);
      });
      java_args
    });
    let java_optional_type_args = node
      .type_args
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let return_type = java_ast_factory.create_new_expr(
      env,
      &java_callee,
      &java_optional_args,
      &java_optional_type_args,
      &java_range,
    );
    delete_local_ref!(env, java_callee);
    delete_local_optional_ref!(env, java_optional_args);
    delete_local_optional_ref!(env, java_optional_type_args);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_null<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Null) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_value = java_ast_factory.create_null(env, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_number<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Number) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let value = node.value;
    let optional_raw = node.raw.as_ref().map(|node| node.as_str().to_owned());
    let return_value = java_ast_factory.create_number(env, value, &optional_raw, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_object_lit<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ObjectLit) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_props = java_array_list.construct(env, node.props.len());
    node.props.iter().for_each(|node| {
      let java_node = enum_create_prop_or_spread(env, map, node);
      java_array_list.add(env, &java_props, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_object_lit(env, &java_props, &java_range);
    delete_local_ref!(env, java_props);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_object_pat<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ObjectPat) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_props = java_array_list.construct(env, node.props.len());
    node.props.iter().for_each(|node| {
      let java_node = enum_create_object_pat_prop(env, map, node);
      java_array_list.add(env, &java_props, &java_node);
      delete_local_ref!(env, java_node);
    });
    let optional = node.optional;
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let return_type =
      java_ast_factory.create_object_pat(env, &java_props, optional, &java_optional_type_ann, &java_range);
    delete_local_ref!(env, java_props);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_opt_call<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &OptCall) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_callee = enum_create_expr(env, map, &node.callee);
    let java_args = java_array_list.construct(env, node.args.len());
    node.args.iter().for_each(|node| {
      let java_node = create_expr_or_spread(env, map, node);
      java_array_list.add(env, &java_args, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_type_args = node
      .type_args
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let return_value =
      java_ast_factory.create_opt_call(env, &java_callee, &java_args, &java_optional_type_args, &java_range);
    delete_local_ref!(env, java_callee);
    delete_local_ref!(env, java_args);
    delete_local_optional_ref!(env, java_optional_type_args);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_opt_chain_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &OptChainExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let optional = node.optional;
    let java_base = enum_create_opt_chain_base(env, map, &node.base);
    let return_value = java_ast_factory.create_opt_chain_expr(env, optional, &java_base, &java_range);
    delete_local_ref!(env, java_base);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_param<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Param) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_decorators = java_array_list.construct(env, node.decorators.len());
    node.decorators.iter().for_each(|node| {
      let java_node = create_decorator(env, map, node);
      java_array_list.add(env, &java_decorators, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_pat = enum_create_pat(env, map, &node.pat);
    let return_type = java_ast_factory.create_param(env, &java_decorators, &java_pat, &java_range);
    delete_local_ref!(env, java_decorators);
    delete_local_ref!(env, java_pat);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_paren_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ParenExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_type = java_ast_factory.create_paren_expr(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_private_method<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &PrivateMethod,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = create_private_name(env, map, &node.key);
    let java_function = create_function(env, map, &node.function);
    let kind = node.kind.get_id();
    let is_static = node.is_static;
    let accessibility = node.accessibility.map_or(-1, |node| node.get_id());
    let is_abstract = node.is_abstract;
    let is_optional = node.is_optional;
    let is_override = node.is_override;
    let return_type = java_ast_factory.create_private_method(
      env,
      &java_key,
      &java_function,
      kind,
      is_static,
      accessibility,
      is_abstract,
      is_optional,
      is_override,
      &java_range,
    );
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_function);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_private_name<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &PrivateName) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_id = create_ident(env, map, &node.id);
    let return_type = java_ast_factory.create_private_name(env, &java_id, &java_range);
    delete_local_ref!(env, java_id);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_private_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &PrivateProp) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = create_private_name(env, map, &node.key);
    let java_optional_value = node.value.as_ref().map(|node| enum_create_expr(env, map, node));
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let is_static = node.is_static;
    let java_decorators = java_array_list.construct(env, node.decorators.len());
    node.decorators.iter().for_each(|node| {
      let java_node = create_decorator(env, map, node);
      java_array_list.add(env, &java_decorators, &java_node);
      delete_local_ref!(env, java_node);
    });
    let accessibility = node.accessibility.map_or(-1, |node| node.get_id());
    let is_optional = node.is_optional;
    let is_override = node.is_override;
    let readonly = node.readonly;
    let definite = node.definite;
    let return_type = java_ast_factory.create_private_prop(
      env,
      &java_key,
      &java_optional_value,
      &java_optional_type_ann,
      is_static,
      &java_decorators,
      accessibility,
      is_optional,
      is_override,
      readonly,
      definite,
      &java_range,
    );
    delete_local_ref!(env, java_key);
    delete_local_optional_ref!(env, java_optional_value);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_decorators);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_regex<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Regex) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let exp = node.exp.as_str();
    let flags = node.flags.as_str();
    let return_value = java_ast_factory.create_regex(env, exp, flags, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_rest_pat<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &RestPat) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_dot3_token = java_ast_factory.create_span(env, &map.get_range_by_span(&node.dot3_token));
    let java_arg = enum_create_pat(env, map, &node.arg);
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let return_value =
      java_ast_factory.create_rest_pat(env, &java_dot3_token, &java_arg, &java_optional_type_ann, &java_range);
    delete_local_ref!(env, java_dot3_token);
    delete_local_ref!(env, java_arg);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_return_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ReturnStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_optional_arg = node.arg.as_ref().map(|node| enum_create_expr(env, map, node));
    let return_value = java_ast_factory.create_return_stmt(env, &java_optional_arg, &java_range);
    delete_local_optional_ref!(env, java_optional_arg);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_script<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Script) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let optional_shebang: Option<String> = node.shebang.to_owned().map(|s| s.to_string());
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_body = java_array_list.construct(env, node.body.len());
    node.body.iter().for_each(|node| {
      let java_node = enum_create_stmt(env, map, node);
      java_array_list.add(env, &java_body, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_script(env, &java_body, &optional_shebang, &java_range);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_seq_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &SeqExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_exprs = java_array_list.construct(env, node.exprs.len());
    node.exprs.iter().for_each(|node| {
      let java_node = enum_create_expr(env, map, node);
      java_array_list.add(env, &java_exprs, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_type = java_ast_factory.create_seq_expr(env, &java_exprs, &java_range);
    delete_local_ref!(env, java_exprs);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_setter_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &SetterProp) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_param = enum_create_pat(env, map, &node.param);
    let java_optional_body = node.body.as_ref().map(|node| create_block_stmt(env, map, node));
    let return_type =
      java_ast_factory.create_setter_prop(env, &java_key, &java_param, &java_optional_body, &java_range);
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_param);
    delete_local_optional_ref!(env, java_optional_body);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_spread_element<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &SpreadElement,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_dot3_token = java_ast_factory.create_span(env, &map.get_range_by_span(&node.dot3_token));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_spread_element(env, &java_dot3_token, &java_expr, &java_range);
    delete_local_ref!(env, java_dot3_token);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_static_block<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &StaticBlock) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_body = create_block_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_static_block(env, &java_body, &java_range);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_str<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Str) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let value = node.value.as_str();
    let optional_raw = node.raw.as_ref().map(|node| node.as_str().to_owned());
    let return_value = java_ast_factory.create_str(env, value, &optional_raw, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_super<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Super) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_type = java_ast_factory.create_super(env, &java_range);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_super_prop_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &SuperPropExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_obj = create_super(env, map, &node.obj);
    let java_prop = enum_create_super_prop(env, map, &node.prop);
    let return_type = java_ast_factory.create_super_prop_expr(env, &java_obj, &java_prop, &java_range);
    delete_local_ref!(env, java_obj);
    delete_local_ref!(env, java_prop);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_switch_case<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &SwitchCase) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_optional_test = node.test.as_ref().map(|node| enum_create_expr(env, map, node));
    let java_cons = java_array_list.construct(env, node.cons.len());
    node.cons.iter().for_each(|node| {
      let java_node = enum_create_stmt(env, map, node);
      java_array_list.add(env, &java_cons, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_switch_case(env, &java_optional_test, &java_cons, &java_range);
    delete_local_optional_ref!(env, java_optional_test);
    delete_local_ref!(env, java_cons);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_switch_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &SwitchStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_discriminant = enum_create_expr(env, map, &node.discriminant);
    let java_cases = java_array_list.construct(env, node.cases.len());
    node.cases.iter().for_each(|node| {
      let java_node = create_switch_case(env, map, node);
      java_array_list.add(env, &java_cases, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_switch_stmt(env, &java_discriminant, &java_cases, &java_range);
    delete_local_ref!(env, java_discriminant);
    delete_local_ref!(env, java_cases);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_tagged_tpl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TaggedTpl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_tag = enum_create_expr(env, map, &node.tag);
    let java_optional_type_params = node
      .type_params
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let java_tpl = create_tpl(env, map, &node.tpl);
    let return_value =
      java_ast_factory.create_tagged_tpl(env, &java_tag, &java_optional_type_params, &java_tpl, &java_range);
    delete_local_ref!(env, java_tag);
    delete_local_optional_ref!(env, java_optional_type_params);
    delete_local_ref!(env, java_tpl);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_this_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ThisExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_value = java_ast_factory.create_this_expr(env, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_throw_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ThrowStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_arg = enum_create_expr(env, map, &node.arg);
    let return_value = java_ast_factory.create_throw_stmt(env, &java_arg, &java_range);
    delete_local_ref!(env, java_arg);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_tpl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Tpl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_exprs = java_array_list.construct(env, node.exprs.len());
    node.exprs.iter().for_each(|node| {
      let java_node = enum_create_expr(env, map, node);
      java_array_list.add(env, &java_exprs, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_quasis = java_array_list.construct(env, node.quasis.len());
    node.quasis.iter().for_each(|node| {
      let java_node = create_tpl_element(env, map, node);
      java_array_list.add(env, &java_quasis, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_tpl(env, &java_exprs, &java_quasis, &java_range);
    delete_local_ref!(env, java_exprs);
    delete_local_ref!(env, java_quasis);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_tpl_element<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TplElement) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let tail = node.tail;
    let optional_cooked = node.cooked.as_ref().map(|node| node.to_string());
    let raw = node.raw.as_str();
    let return_value = java_ast_factory.create_tpl_element(env, tail, &optional_cooked, raw, &java_range);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_try_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TryStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_block = create_block_stmt(env, map, &node.block);
    let java_optional_handler = node.handler.as_ref().map(|node| create_catch_clause(env, map, node));
    let java_optional_finalizer = node.finalizer.as_ref().map(|node| create_block_stmt(env, map, node));
    let return_value = java_ast_factory.create_try_stmt(
      env,
      &java_block,
      &java_optional_handler,
      &java_optional_finalizer,
      &java_range,
    );
    delete_local_ref!(env, java_block);
    delete_local_optional_ref!(env, java_optional_handler);
    delete_local_optional_ref!(env, java_optional_finalizer);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_as_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsAsExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let java_type_ann = enum_create_ts_type(env, map, &node.type_ann);
    let return_value = java_ast_factory.create_ts_as_expr(env, &java_expr, &java_type_ann, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_type_ann);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_const_assertion<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsConstAssertion,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_ts_const_assertion(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_enum_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsEnumDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let declare = node.declare;
    let is_const = node.is_const;
    let java_id = create_ident(env, map, &node.id);
    let java_members = java_array_list.construct(env, node.members.len());
    node.members.iter().for_each(|node| {
      let java_node = create_ts_enum_member(env, map, node);
      java_array_list.add(env, &java_members, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_type =
      java_ast_factory.create_ts_enum_decl(env, declare, is_const, &java_id, &java_members, &java_range);
    delete_local_ref!(env, java_id);
    delete_local_ref!(env, java_members);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_ts_enum_member<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsEnumMember,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_id = enum_create_ts_enum_member_id(env, map, &node.id);
    let java_optional_init = node.init.as_ref().map(|node| enum_create_expr(env, map, node));
    let return_value = java_ast_factory.create_ts_enum_member(env, &java_id, &java_optional_init, &java_range);
    delete_local_ref!(env, java_id);
    delete_local_optional_ref!(env, java_optional_init);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_export_assignment<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsExportAssignment,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_ts_export_assignment(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_expr_with_type_args<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsExprWithTypeArgs,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let java_optional_type_args = node
      .type_args
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let return_value =
      java_ast_factory.create_ts_expr_with_type_args(env, &java_expr, &java_optional_type_args, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_optional_ref!(env, java_optional_type_args);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_external_module_ref<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsExternalModuleRef,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = create_str(env, map, &node.expr);
    let return_value = java_ast_factory.create_ts_external_module_ref(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_import_equals_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsImportEqualsDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let is_export = node.is_export;
    let is_type_only = node.is_type_only;
    let java_id = create_ident(env, map, &node.id);
    let java_module_ref = enum_create_ts_module_ref(env, map, &node.module_ref);
    let return_value = java_ast_factory.create_ts_import_equals_decl(
      env,
      is_export,
      is_type_only,
      &java_id,
      &java_module_ref,
      &java_range,
    );
    delete_local_ref!(env, java_id);
    delete_local_ref!(env, java_module_ref);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_index_signature<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsIndexSignature,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = enum_create_ts_fn_param(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_optional_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let readonly = node.readonly;
    let is_static = node.is_static;
    let return_value = java_ast_factory.create_ts_index_signature(
      env,
      &java_params,
      &java_optional_type_ann,
      readonly,
      is_static,
      &java_range,
    );
    delete_local_ref!(env, java_params);
    delete_local_optional_ref!(env, java_optional_type_ann);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_instantiation<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsInstantiation,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let java_type_args = create_ts_type_param_instantiation(env, map, &node.type_args);
    let return_type = java_ast_factory.create_ts_instantiation(env, &java_expr, &java_type_args, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_type_args);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_ts_interface_body<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsInterfaceBody,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_body = java_array_list.construct(env, node.body.len());
    node.body.iter().for_each(|node| {
      let java_node = enum_create_ts_type_element(env, map, node);
      java_array_list.add(env, &java_body, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_type = java_ast_factory.create_ts_interface_body(env, &java_body, &java_range);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_ts_interface_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsInterfaceDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_id = create_ident(env, map, &node.id);
    let declare = node.declare;
    let java_optional_type_params = node
      .type_params
      .as_ref()
      .map(|node| create_ts_type_param_decl(env, map, node));
    let java_extends = java_array_list.construct(env, node.extends.len());
    node.extends.iter().for_each(|node| {
      let java_node = create_ts_expr_with_type_args(env, map, node);
      java_array_list.add(env, &java_extends, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_body = create_ts_interface_body(env, map, &node.body);
    let return_type = java_ast_factory.create_ts_interface_decl(
      env,
      &java_id,
      declare,
      &java_optional_type_params,
      &java_extends,
      &java_body,
      &java_range,
    );
    delete_local_ref!(env, java_id);
    delete_local_optional_ref!(env, java_optional_type_params);
    delete_local_ref!(env, java_extends);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_ts_module_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsModuleDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let declare = node.declare;
    let global = node.global;
    let java_id = enum_create_ts_module_name(env, map, &node.id);
    let java_optional_body = node
      .body
      .as_ref()
      .map(|node| enum_create_ts_namespace_body(env, map, node));
    let return_type =
      java_ast_factory.create_ts_module_decl(env, declare, global, &java_id, &java_optional_body, &java_range);
    delete_local_ref!(env, java_id);
    delete_local_optional_ref!(env, java_optional_body);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_ts_namespace_export_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsNamespaceExportDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_id = create_ident(env, map, &node.id);
    let return_value = java_ast_factory.create_ts_namespace_export_decl(env, &java_id, &java_range);
    delete_local_ref!(env, java_id);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_non_null_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsNonNullExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_ts_non_null_expr(env, &java_expr, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_param_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsParamProp) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_decorators = java_array_list.construct(env, node.decorators.len());
    node.decorators.iter().for_each(|node| {
      let java_node = create_decorator(env, map, node);
      java_array_list.add(env, &java_decorators, &java_node);
      delete_local_ref!(env, java_node);
    });
    let accessibility = node.accessibility.map_or(-1, |node| node.get_id());
    let is_override = node.is_override;
    let readonly = node.readonly;
    let java_param = enum_create_ts_param_prop_param(env, map, &node.param);
    let return_type = java_ast_factory.create_ts_param_prop(
      env,
      &java_decorators,
      accessibility,
      is_override,
      readonly,
      &java_param,
      &java_range,
    );
    delete_local_ref!(env, java_decorators);
    delete_local_ref!(env, java_param);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_ts_qualified_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsQualifiedName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_left = enum_create_ts_entity_name(env, map, &node.left);
    let java_right = create_ident(env, map, &node.right);
    let return_value = java_ast_factory.create_ts_qualified_name(env, &java_left, &java_right, &java_range);
    delete_local_ref!(env, java_left);
    delete_local_ref!(env, java_right);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_satisfies_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsSatisfiesExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let java_type_ann = enum_create_ts_type(env, map, &node.type_ann);
    let return_value = java_ast_factory.create_ts_satisfies_expr(env, &java_expr, &java_type_ann, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_type_ann);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_type_alias_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsTypeAliasDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span()));
    let java_id = create_ident(env, map, &node.id);
    let declare = node.declare;
    let java_optional_type_params = node
      .type_params
      .as_ref()
      .map(|node| create_ts_type_param_decl(env, map, node));
    let java_type_ann = enum_create_ts_type(env, map, &node.type_ann);
    let return_type = java_ast_factory.create_ts_type_alias_decl(
      env,
      &java_id,
      declare,
      &java_optional_type_params,
      &java_type_ann,
      &java_range,
    );
    delete_local_ref!(env, java_id);
    delete_local_optional_ref!(env, java_optional_type_params);
    delete_local_ref!(env, java_type_ann);
    delete_local_ref!(env, java_range);
    return_type
  }

  fn create_ts_type_ann<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsTypeAnn) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_type_ann = enum_create_ts_type(env, map, &node.type_ann);
    let return_value = java_ast_factory.create_ts_type_ann(env, &java_type_ann, &java_range);
    delete_local_ref!(env, java_type_ann);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_type_assertion<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsTypeAssertion,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_expr = enum_create_expr(env, map, &node.expr);
    let java_type_ann = enum_create_ts_type(env, map, &node.type_ann);
    let return_value = java_ast_factory.create_ts_type_assertion(env, &java_expr, &java_type_ann, &java_range);
    delete_local_ref!(env, java_expr);
    delete_local_ref!(env, java_type_ann);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_type_param<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsTypeParam) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_name = create_ident(env, map, &node.name);
    let is_in = node.is_in;
    let is_out = node.is_out;
    let is_const = node.is_const;
    let java_optional_constraint = node.constraint.as_ref().map(|node| enum_create_ts_type(env, map, node));
    let java_optional_default = node.default.as_ref().map(|node| enum_create_ts_type(env, map, node));
    let return_value = java_ast_factory.create_ts_type_param(
      env,
      &java_name,
      is_in,
      is_out,
      is_const,
      &java_optional_constraint,
      &java_optional_default,
      &java_range,
    );
    delete_local_ref!(env, java_name);
    delete_local_optional_ref!(env, java_optional_constraint);
    delete_local_optional_ref!(env, java_optional_default);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_type_param_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsTypeParamDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = create_ts_type_param(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_ts_type_param_decl(env, &java_params, &java_range);
    delete_local_ref!(env, java_params);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_ts_type_param_instantiation<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsTypeParamInstantiation,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = enum_create_ts_type(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_ts_type_param_instantiation(env, &java_params, &java_range);
    delete_local_ref!(env, java_params);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_unary_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &UnaryExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let op = node.op.get_id();
    let java_arg = enum_create_expr(env, map, &node.arg);
    let return_value = java_ast_factory.create_unary_expr(env, op, &java_arg, &java_range);
    delete_local_ref!(env, java_arg);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_update_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &UpdateExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let op = node.op.get_id();
    let prefix = node.prefix;
    let java_arg = enum_create_expr(env, map, &node.arg);
    let return_value = java_ast_factory.create_update_expr(env, op, prefix, &java_arg, &java_range);
    delete_local_ref!(env, java_arg);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_using_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &UsingDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let is_await = node.is_await;
    let java_decls = java_array_list.construct(env, node.decls.len());
    node.decls.iter().for_each(|node| {
      let java_node = create_var_declarator(env, map, node);
      java_array_list.add(env, &java_decls, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_using_decl(env, is_await, &java_decls, &java_range);
    delete_local_ref!(env, java_decls);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_var_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &VarDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let declare = node.declare;
    let kind_id = node.kind.get_id();
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_decls = java_array_list.construct(env, node.decls.len());
    node.decls.iter().for_each(|node| {
      let java_node = create_var_declarator(env, map, node);
      java_array_list.add(env, &java_decls, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_var_decl(env, kind_id, declare, &java_decls, &java_range);
    delete_local_ref!(env, java_decls);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_var_declarator<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &VarDeclarator,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_name = enum_create_pat(env, map, &node.name);
    let java_optional_init: Option<JObject> = node.init.as_ref().map(|node| enum_create_expr(env, map, node.as_ref()));
    let definite = node.definite;
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let return_value =
      java_ast_factory.create_var_declarator(env, &java_name, &java_optional_init, definite, &java_range);
    delete_local_ref!(env, java_name);
    delete_local_optional_ref!(env, java_optional_init);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_while_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &WhileStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_test = enum_create_expr(env, map, &node.test);
    let java_body = enum_create_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_while_stmt(env, &java_test, &java_body, &java_range);
    delete_local_ref!(env, java_test);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_with_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &WithStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_obj = enum_create_expr(env, map, &node.obj);
    let java_body = enum_create_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_with_stmt(env, &java_obj, &java_body, &java_range);
    delete_local_ref!(env, java_obj);
    delete_local_ref!(env, java_body);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn create_yield_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &YieldExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_range = java_ast_factory.create_span(env, &map.get_range_by_span(&node.span));
    let java_optional_arg = node.arg.as_ref().map(|node| enum_create_expr(env, map, node));
    let java_delegate = node.delegate;
    let return_value = java_ast_factory.create_yield_expr(env, &java_optional_arg, java_delegate, &java_range);
    delete_local_optional_ref!(env, java_optional_arg);
    delete_local_ref!(env, java_range);
    return_value
  }

  fn enum_create_block_stmt_or_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &BlockStmtOrExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      BlockStmtOrExpr::BlockStmt(node) => create_block_stmt(env, map, node),
      BlockStmtOrExpr::Expr(node) => enum_create_expr(env, map, node),
    }
  }

  fn enum_create_callee<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Callee) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Callee::Expr(node) => enum_create_expr(env, map, node),
      Callee::Import(node) => create_import(env, map, node),
      Callee::Super(node) => create_super(env, map, node),
    }
  }

  fn enum_create_class_member<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ClassMember,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ClassMember::AutoAccessor(node) => create_auto_accessor(env, map, node),
      ClassMember::Constructor(node) => create_constructor(env, map, node),
      ClassMember::ClassProp(node) => create_class_prop(env, map, node),
      ClassMember::Empty(node) => create_empty_stmt(env, map, node),
      ClassMember::Method(node) => create_class_method(env, map, node),
      ClassMember::PrivateMethod(node) => create_private_method(env, map, node),
      ClassMember::PrivateProp(node) => create_private_prop(env, map, node),
      ClassMember::StaticBlock(node) => create_static_block(env, map, node),
      ClassMember::TsIndexSignature(node) => create_ts_index_signature(env, map, node),
    }
  }

  fn enum_create_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Decl) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Decl::Class(node) => create_class_decl(env, map, node),
      Decl::Fn(node) => create_fn_decl(env, map, node),
      Decl::TsEnum(node) => create_ts_enum_decl(env, map, node),
      Decl::TsInterface(node) => create_ts_interface_decl(env, map, node),
      Decl::TsModule(node) => create_ts_module_decl(env, map, node),
      Decl::TsTypeAlias(node) => create_ts_type_alias_decl(env, map, node),
      Decl::Using(node) => create_using_decl(env, map, node),
      Decl::Var(node) => create_var_decl(env, map, node),
    }
  }

  fn enum_create_default_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &DefaultDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      DefaultDecl::Class(node) => create_class_expr(env, map, node),
      DefaultDecl::Fn(node) => create_fn_expr(env, map, node),
      DefaultDecl::TsInterfaceDecl(node) => create_ts_interface_decl(env, map, node),
    }
  }

  fn enum_create_export_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ExportSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ExportSpecifier::Default(node) => create_export_default_specifier(env, map, node),
      ExportSpecifier::Named(node) => create_export_named_specifier(env, map, node),
      ExportSpecifier::Namespace(node) => create_export_namespace_specifier(env, map, node),
    }
  }

  fn enum_create_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Expr) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Expr::Array(node) => create_array_lit(env, map, node),
      Expr::Arrow(node) => create_arrow_expr(env, map, node),
      Expr::Assign(node) => create_assign_expr(env, map, node),
      Expr::Await(node) => create_await_expr(env, map, node),
      Expr::Bin(node) => create_bin_expr(env, map, node),
      Expr::Call(node) => create_call_expr(env, map, node),
      Expr::Class(node) => create_class_expr(env, map, node),
      Expr::Cond(node) => create_cond_expr(env, map, node),
      Expr::Fn(node) => create_fn_expr(env, map, node),
      Expr::Ident(node) => create_ident(env, map, node),
      Expr::Invalid(node) => create_invalid(env, map, node),
      Expr::JSXElement(node) => create_jsx_element(env, map, node),
      Expr::JSXEmpty(node) => create_jsx_empty_expr(env, map, node),
      Expr::JSXFragment(node) => create_jsx_fragment(env, map, node),
      Expr::JSXMember(node) => create_jsx_member_expr(env, map, node),
      Expr::JSXNamespacedName(node) => create_jsx_namespaced_name(env, map, node),
      Expr::Lit(node) => enum_create_lit(env, map, node),
      Expr::Member(node) => create_member_expr(env, map, node),
      Expr::MetaProp(node) => create_meta_prop_expr(env, map, node),
      Expr::New(node) => create_new_expr(env, map, node),
      Expr::Object(node) => create_object_lit(env, map, node),
      Expr::OptChain(node) => create_opt_chain_expr(env, map, node),
      Expr::Paren(node) => create_paren_expr(env, map, node),
      Expr::PrivateName(node) => create_private_name(env, map, node),
      Expr::Seq(node) => create_seq_expr(env, map, node),
      Expr::SuperProp(node) => create_super_prop_expr(env, map, node),
      Expr::TaggedTpl(node) => create_tagged_tpl(env, map, node),
      Expr::This(node) => create_this_expr(env, map, node),
      Expr::Tpl(node) => create_tpl(env, map, node),
      Expr::TsAs(node) => create_ts_as_expr(env, map, node),
      Expr::TsConstAssertion(node) => create_ts_const_assertion(env, map, node),
      Expr::TsInstantiation(node) => create_ts_instantiation(env, map, node),
      Expr::TsNonNull(node) => create_ts_non_null_expr(env, map, node),
      Expr::TsSatisfies(node) => create_ts_satisfies_expr(env, map, node),
      Expr::TsTypeAssertion(node) => create_ts_type_assertion(env, map, node),
      Expr::Unary(node) => create_unary_expr(env, map, node),
      Expr::Update(node) => create_update_expr(env, map, node),
      Expr::Yield(node) => create_yield_expr(env, map, node),
    }
  }

  fn enum_create_for_head<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ForHead) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_import_specifier<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ImportSpecifier,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ImportSpecifier::Default(node) => create_import_default_specifier(env, map, node),
      ImportSpecifier::Named(node) => create_import_named_specifier(env, map, node),
      ImportSpecifier::Namespace(node) => create_import_star_as_specifier(env, map, node),
    }
  }

  fn enum_create_jsx_attr_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXAttrName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_jsx_attr_or_spread<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXAttrOrSpread,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_jsx_attr_value<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXAttrValue,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_jsx_element_child<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXElementChild,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      JSXElementChild::JSXElement(node) => create_jsx_element(env, map, node),
      JSXElementChild::JSXExprContainer(node) => create_jsx_expr_container(env, map, node),
      JSXElementChild::JSXFragment(node) => create_jsx_fragment(env, map, node),
      JSXElementChild::JSXSpreadChild(node) => create_jsx_spread_child(env, map, node),
      JSXElementChild::JSXText(node) => create_jsx_text(env, map, node),
    }
  }

  fn enum_create_jsx_element_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &JSXElementName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      JSXElementName::Ident(node) => create_ident(env, map, node),
      JSXElementName::JSXMemberExpr(node) => create_jsx_member_expr(env, map, node),
      JSXElementName::JSXNamespacedName(node) => create_jsx_namespaced_name(env, map, node),
    }
  }

  fn enum_create_jsx_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      JSXExpr::Expr(node) => enum_create_expr(env, map, node),
      JSXExpr::JSXEmptyExpr(node) => create_jsx_empty_expr(env, map, node),
    }
  }

  fn enum_create_jsx_object<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXObject) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      JSXObject::Ident(node) => create_ident(env, map, node),
      JSXObject::JSXMemberExpr(node) => create_jsx_member_expr(env, map, node),
    }
  }

  fn enum_create_key<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Key) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Key::Private(node) => create_private_name(env, map, node),
      Key::Public(node) => enum_create_prop_name(env, map, node),
    }
  }

  fn enum_create_lit<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Lit) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Lit::BigInt(node) => create_big_int(env, map, node),
      Lit::Bool(node) => create_bool(env, map, node),
      Lit::JSXText(node) => create_jsx_text(env, map, node),
      Lit::Null(node) => create_null(env, map, node),
      Lit::Num(node) => create_number(env, map, node),
      Lit::Regex(node) => create_regex(env, map, node),
      Lit::Str(node) => create_str(env, map, node),
    }
  }

  fn enum_create_member_prop<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &MemberProp,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      MemberProp::Computed(node) => create_computed_prop_name(env, map, node),
      MemberProp::Ident(node) => create_ident(env, map, node),
      MemberProp::PrivateName(node) => create_private_name(env, map, node),
    }
  }

  fn enum_create_module_decl<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ModuleDecl,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ModuleDecl::ExportAll(node) => create_export_all(env, map, node),
      ModuleDecl::ExportDecl(node) => create_export_decl(env, map, node),
      ModuleDecl::ExportDefaultDecl(node) => create_export_default_decl(env, map, node),
      ModuleDecl::ExportDefaultExpr(node) => create_export_default_expr(env, map, node),
      ModuleDecl::ExportNamed(node) => create_named_export(env, map, node),
      ModuleDecl::Import(node) => create_import_decl(env, map, node),
      ModuleDecl::TsExportAssignment(node) => create_ts_export_assignment(env, map, node),
      ModuleDecl::TsImportEquals(node) => create_ts_import_equals_decl(env, map, node),
      ModuleDecl::TsNamespaceExport(node) => create_ts_namespace_export_decl(env, map, node),
    }
  }

  fn enum_create_module_export_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ModuleExportName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ModuleExportName::Ident(node) => create_ident(env, map, node),
      ModuleExportName::Str(node) => create_str(env, map, node),
    }
  }

  fn enum_create_module_item<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ModuleItem,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ModuleItem::ModuleDecl(node) => enum_create_module_decl(env, map, node),
      ModuleItem::Stmt(node) => enum_create_stmt(env, map, node),
    }
  }

  fn enum_create_object_pat_prop<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ObjectPatProp,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ObjectPatProp::Assign(node) => create_assign_pat_prop(env, map, node),
      ObjectPatProp::KeyValue(node) => create_key_value_pat_prop(env, map, node),
      ObjectPatProp::Rest(node) => create_rest_pat(env, map, node),
    }
  }

  fn enum_create_opt_chain_base<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &OptChainBase,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      OptChainBase::Call(node) => create_opt_call(env, map, node),
      OptChainBase::Member(node) => create_member_expr(env, map, node),
    }
  }

  fn enum_create_param_or_ts_param_prop<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &ParamOrTsParamProp,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      ParamOrTsParamProp::Param(node) => create_param(env, map, node),
      ParamOrTsParamProp::TsParamProp(node) => create_ts_param_prop(env, map, node),
    }
  }

  fn enum_create_pat<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Pat) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Pat::Array(node) => create_array_pat(env, map, node),
      Pat::Assign(node) => create_assign_pat(env, map, node),
      Pat::Expr(node) => enum_create_expr(env, map, node),
      Pat::Ident(node) => create_binding_ident(env, map, node),
      Pat::Invalid(node) => create_invalid(env, map, node),
      Pat::Object(node) => create_object_pat(env, map, node),
      Pat::Rest(node) => create_rest_pat(env, map, node),
    }
  }

  fn enum_create_pat_or_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &PatOrExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      PatOrExpr::Expr(node) => enum_create_expr(env, map, node),
      PatOrExpr::Pat(node) => enum_create_pat(env, map, node),
    }
  }

  pub fn enum_create_program<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &Option<Arc<Program>>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Some(node) => match node.as_ref() {
        Program::Module(node) => create_module(env, map, node),
        Program::Script(node) => create_script(env, map, node),
      },
      None => Default::default(),
    }
  }

  fn enum_create_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Prop) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Prop::Assign(node) => create_assign_prop(env, map, node),
      Prop::Getter(node) => create_getter_prop(env, map, node),
      Prop::KeyValue(node) => create_key_value_prop(env, map, node),
      Prop::Method(node) => create_method_prop(env, map, node),
      Prop::Setter(node) => create_setter_prop(env, map, node),
      Prop::Shorthand(node) => create_ident(env, map, node),
    }
  }

  fn enum_create_prop_name<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &PropName) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      PropName::BigInt(node) => create_big_int(env, map, node),
      PropName::Computed(node) => create_computed_prop_name(env, map, node),
      PropName::Ident(node) => create_ident(env, map, node),
      PropName::Num(node) => create_number(env, map, node),
      PropName::Str(node) => create_str(env, map, node),
    }
  }

  fn enum_create_prop_or_spread<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &PropOrSpread,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      PropOrSpread::Spread(node) => create_spread_element(env, map, node),
      PropOrSpread::Prop(node) => enum_create_prop(env, map, node),
    }
  }

  fn enum_create_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Stmt) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Stmt::Block(node) => create_block_stmt(env, map, node),
      Stmt::Break(node) => create_break_stmt(env, map, node),
      Stmt::Continue(node) => create_continue_stmt(env, map, node),
      Stmt::Debugger(node) => create_debugger_stmt(env, map, node),
      Stmt::DoWhile(node) => create_do_while_stmt(env, map, node),
      Stmt::Decl(node) => enum_create_decl(env, map, node),
      Stmt::Empty(node) => create_empty_stmt(env, map, node),
      Stmt::Expr(node) => create_expr_stmt(env, map, node),
      Stmt::For(node) => create_for_stmt(env, map, node),
      Stmt::ForIn(node) => create_for_in_stmt(env, map, node),
      Stmt::ForOf(node) => create_for_of_stmt(env, map, node),
      Stmt::If(node) => create_if_stmt(env, map, node),
      Stmt::Labeled(node) => create_labeled_stmt(env, map, node),
      Stmt::Return(node) => create_return_stmt(env, map, node),
      Stmt::Switch(node) => create_switch_stmt(env, map, node),
      Stmt::Throw(node) => create_throw_stmt(env, map, node),
      Stmt::Try(node) => create_try_stmt(env, map, node),
      Stmt::While(node) => create_while_stmt(env, map, node),
      Stmt::With(node) => create_with_stmt(env, map, node),
    }
  }

  fn enum_create_super_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &SuperProp) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      SuperProp::Computed(node) => create_computed_prop_name(env, map, node),
      SuperProp::Ident(node) => create_ident(env, map, node),
    }
  }

  fn enum_create_ts_entity_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsEntityName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      TsEntityName::Ident(node) => create_ident(env, map, node),
      TsEntityName::TsQualifiedName(node) => create_ts_qualified_name(env, map, node),
    }
  }

  fn enum_create_ts_enum_member_id<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsEnumMemberId,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_fn_or_constructor_type<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsFnOrConstructorType,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_fn_param<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsFnParam,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      TsFnParam::Array(node) => create_array_pat(env, map, node),
      TsFnParam::Ident(node) => create_binding_ident(env, map, node),
      TsFnParam::Object(node) => create_object_pat(env, map, node),
      TsFnParam::Rest(node) => create_rest_pat(env, map, node),
    }
  }

  fn enum_create_ts_lit<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsLit) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_module_name<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsModuleName,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      TsModuleName::Ident(node) => create_ident(env, map, node),
      TsModuleName::Str(node) => create_str(env, map, node),
    }
  }

  fn enum_create_ts_module_ref<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsModuleRef,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      TsModuleRef::TsEntityName(node) => enum_create_ts_entity_name(env, map, node),
      TsModuleRef::TsExternalModuleRef(node) => create_ts_external_module_ref(env, map, node),
    }
  }

  fn enum_create_ts_namespace_body<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsNamespaceBody,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_param_prop_param<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsParamPropParam,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      TsParamPropParam::Assign(node) => create_assign_pat(env, map, node),
      TsParamPropParam::Ident(node) => create_binding_ident(env, map, node),
    }
  }

  fn enum_create_ts_this_type_or_ident<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsThisTypeOrIdent,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_type_element<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsTypeElement,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_type_query_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsTypeQueryExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_type<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsType) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_ts_union_or_intersection_type<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &TsUnionOrIntersectionType,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_var_decl_or_expr<'local, 'a>(
    env: &mut JNIEnv<'local>,
    map: &ByteToIndexMap,
    node: &VarDeclOrExpr,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      VarDeclOrExpr::Expr(node) => enum_create_expr(env, map, node),
      VarDeclOrExpr::VarDecl(node) => create_var_decl(env, map, node),
    }
  }
}
