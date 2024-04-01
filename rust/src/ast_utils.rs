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
  method_create_big_int: JStaticMethodID,
  method_create_binding_ident: JStaticMethodID,
  method_create_block_stmt: JStaticMethodID,
  method_create_bool: JStaticMethodID,
  method_create_class: JStaticMethodID,
  method_create_class_decl: JStaticMethodID,
  method_create_constructor: JStaticMethodID,
  method_create_debugger_stmt: JStaticMethodID,
  method_create_decorator: JStaticMethodID,
  method_create_empty_stmt: JStaticMethodID,
  method_create_export_all: JStaticMethodID,
  method_create_export_decl: JStaticMethodID,
  method_create_export_default_decl: JStaticMethodID,
  method_create_export_default_expr: JStaticMethodID,
  method_create_expr_stmt: JStaticMethodID,
  method_create_ident: JStaticMethodID,
  method_create_import_decl: JStaticMethodID,
  method_create_import_default_specifier: JStaticMethodID,
  method_create_import_named_specifier: JStaticMethodID,
  method_create_import_star_as_specifier: JStaticMethodID,
  method_create_jsx_text: JStaticMethodID,
  method_create_module: JStaticMethodID,
  method_create_named_export: JStaticMethodID,
  method_create_null: JStaticMethodID,
  method_create_number: JStaticMethodID,
  method_create_object_lit: JStaticMethodID,
  method_create_regex: JStaticMethodID,
  method_create_script: JStaticMethodID,
  method_create_spread_element: JStaticMethodID,
  method_create_static_block: JStaticMethodID,
  method_create_str: JStaticMethodID,
  method_create_ts_export_assignment: JStaticMethodID,
  method_create_ts_expr_with_type_args: JStaticMethodID,
  method_create_ts_external_module_ref: JStaticMethodID,
  method_create_ts_import_equals_decl: JStaticMethodID,
  method_create_ts_namespace_export_decl: JStaticMethodID,
  method_create_ts_type_ann: JStaticMethodID,
  method_create_ts_type_param: JStaticMethodID,
  method_create_ts_type_param_decl: JStaticMethodID,
  method_create_ts_type_param_instantiation: JStaticMethodID,
  method_create_unary_expr: JStaticMethodID,
  method_create_using_decl: JStaticMethodID,
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
    let method_create_big_int = env
      .get_static_method_id(
        &class,
        "createBigInt",
        "(ILjava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstBigInt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBigInt");
    let method_create_binding_ident = env
      .get_static_method_id(
        &class,
        "createBindingIdent",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;II)Lcom/caoccao/javet/swc4j/ast/pat/Swc4jAstBindingIdent;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBindingIdent");
    let method_create_block_stmt = env
      .get_static_method_id(
        &class,
        "createBlockStmt",
        "(Ljava/util/List;II)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBlockStmt");
    let method_create_bool = env
      .get_static_method_id(
        &class,
        "createBool",
        "(ZII)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstBool;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createBool");
    let method_create_class = env
      .get_static_method_id(
        &class,
        "createClass",
        "(Ljava/util/List;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;ZLcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;Ljava/util/List;II)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstClass;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createClass");
    let method_create_class_decl = env
      .get_static_method_id(
        &class,
        "createClassDecl",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;ZLcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstClass;II)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstClassDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createClassDecl");
    let method_create_constructor = env
      .get_static_method_id(
        &class,
        "createConstructor",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPropName;Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;IZII)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstConstructor;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createConstructor");
    let method_create_debugger_stmt = env
      .get_static_method_id(
        &class,
        "createDebuggerStmt",
        "(II)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstDebuggerStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createDebuggerStmt");
    let method_create_decorator = env
      .get_static_method_id(
        &class,
        "createDecorator",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;II)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstDecorator;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createDecorator");
    let method_create_empty_stmt = env
      .get_static_method_id(
        &class,
        "createEmptyStmt",
        "(II)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstEmptyStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createEmptyStmt");
    let method_create_export_all = env
      .get_static_method_id(
        &class,
        "createExportAll",
        "(Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstObjectLit;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportAll;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportAll");
    let method_create_export_decl = env
      .get_static_method_id(
        &class,
        "createExportDecl",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstDecl;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportDecl");
    let method_create_export_default_decl = env
      .get_static_method_id(
        &class,
        "createExportDefaultDecl",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstDefaultDecl;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportDefaultDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportDefaultDecl");
    let method_create_export_default_expr = env
      .get_static_method_id(
        &class,
        "createExportDefaultExpr",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstExportDefaultExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExportDefaultExpr");
    let method_create_expr_stmt = env
      .get_static_method_id(
        &class,
        "createExprStmt",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;II)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstExprStmt;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createExprStmt");
    let method_create_ident = env
      .get_static_method_id(
        &class,
        "createIdent",
        "(Ljava/lang/String;ZII)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createIdent");
    let method_create_import_decl = env
      .get_static_method_id(
        &class,
        "createImportDecl",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstObjectLit;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportDecl");
    let method_create_import_default_specifier = env
      .get_static_method_id(
        &class,
        "createImportDefaultSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportDefaultSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportDefaultSpecifier");
    let method_create_import_named_specifier = env
      .get_static_method_id(
        &class,
        "createImportNamedSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstModuleExportName;ZII)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportNamedSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportNamedSpecifier");
    let method_create_import_star_as_specifier = env
      .get_static_method_id(
        &class,
        "createImportStarAsSpecifier",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstImportStarAsSpecifier;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createImportStarAsSpecifier");
    let method_create_jsx_text = env
      .get_static_method_id(
        &class,
        "createJsxText",
        "(Ljava/lang/String;Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstJsxText;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createJsxText");
    let method_create_module = env
      .get_static_method_id(
        &class,
        "createModule",
        "(Ljava/util/List;Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstModule;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createModule");
    let method_create_named_export = env
      .get_static_method_id(
        &class,
        "createNamedExport",
        "(Ljava/util/List;Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;ZLcom/caoccao/javet/swc4j/ast/Swc4jAstObjectLit;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstNamedExport;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createNamedExport");
    let method_create_null = env
      .get_static_method_id(
        &class,
        "createNull",
        "(II)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstNull;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createNull");
    let method_create_number = env
      .get_static_method_id(
        &class,
        "createNumber",
        "(DLjava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstNumber;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createNumber");
    let method_create_object_lit = env
      .get_static_method_id(
        &class,
        "createObjectLit",
        "(Ljava/util/List;II)Lcom/caoccao/javet/swc4j/ast/Swc4jAstObjectLit;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createObjectLit");
    let method_create_regex = env
      .get_static_method_id(
        &class,
        "createRegex",
        "(Ljava/lang/String;Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstRegex;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createRegex");
    let method_create_script = env
      .get_static_method_id(
        &class,
        "createScript",
        "(Ljava/util/List;Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstScript;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createScript");
    let method_create_spread_element = env
      .get_static_method_id(
        &class,
        "createSpreadElement",
        "(IILcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;II)Lcom/caoccao/javet/swc4j/ast/Swc4jAstSpreadElement;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createSpreadElement");
    let method_create_static_block = env
      .get_static_method_id(
        &class,
        "createStaticBlock",
        "(Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstBlockStmt;II)Lcom/caoccao/javet/swc4j/ast/clazz/Swc4jAstStaticBlock;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createStaticBlock");
    let method_create_str = env
      .get_static_method_id(
        &class,
        "createStr",
        "(Ljava/lang/String;Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createStr");
    let method_create_ts_export_assignment = env
      .get_static_method_id(
        &class,
        "createTsExportAssignment",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsExportAssignment;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsExportAssignment");
    let method_create_ts_expr_with_type_args = env
      .get_static_method_id(
        &class,
        "createTsExprWithTypeArgs",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;II)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsExprWithTypeArgs;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsExprWithTypeArgs");
    let method_create_ts_external_module_ref = env
      .get_static_method_id(
        &class,
        "createTsExternalModuleRef",
        "(Lcom/caoccao/javet/swc4j/ast/expr/lit/Swc4jAstStr;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsExternalModuleRef;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsExternalModuleRef");
    let method_create_ts_import_equals_decl = env
      .get_static_method_id(
        &class,
        "createTsImportEqualsDecl",
        "(ZZLcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstModuleRef;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsImportEqualsDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsImportEqualsDecl");
    let method_create_ts_namespace_export_decl = env
      .get_static_method_id(
        &class,
        "createTsNamespaceExportDecl",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;II)Lcom/caoccao/javet/swc4j/ast/module/Swc4jAstTsNamespaceExportDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsNamespaceExportDecl");
    let method_create_ts_type_ann = env
      .get_static_method_id(
        &class,
        "createTsTypeAnn",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;II)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeAnn;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeAnn");
    let method_create_ts_type_param = env
      .get_static_method_id(
        &class,
        "createTsTypeParam",
        "(Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstIdent;ZZZLcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstTsType;II)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParam;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeParam");
    let method_create_ts_type_param_decl = env
      .get_static_method_id(
        &class,
        "createTsTypeParamDecl",
        "(Ljava/util/List;II)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeParamDecl");
    let method_create_ts_type_param_instantiation = env
      .get_static_method_id(
        &class,
        "createTsTypeParamInstantiation",
        "(Ljava/util/List;II)Lcom/caoccao/javet/swc4j/ast/ts/Swc4jAstTsTypeParamInstantiation;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createTsTypeParamInstantiation");
    let method_create_unary_expr = env
      .get_static_method_id(
        &class,
        "createUnaryExpr",
        "(ILcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;II)Lcom/caoccao/javet/swc4j/ast/expr/Swc4jAstUnaryExpr;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createUnaryExpr");
    let method_create_using_decl = env
      .get_static_method_id(
        &class,
        "createUsingDecl",
        "(ZLjava/util/List;II)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstUsingDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createUsingDecl");
    let method_create_var_decl = env
      .get_static_method_id(
        &class,
        "createVarDecl",
        "(IZLjava/util/List;II)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstVarDecl;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createVarDecl");
    let method_create_var_declarator = env
      .get_static_method_id(
        &class,
        "createVarDeclarator",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstPat;Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstExpr;ZII)Lcom/caoccao/javet/swc4j/ast/stmt/Swc4jAstVarDeclarator;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createVarDeclarator");
    JavaSwc4jAstFactory {
      class,
      method_create_big_int,
      method_create_binding_ident,
      method_create_block_stmt,
      method_create_bool,
      method_create_class,
      method_create_class_decl,
      method_create_constructor,
      method_create_debugger_stmt,
      method_create_decorator,
      method_create_empty_stmt,
      method_create_export_all,
      method_create_export_decl,
      method_create_export_default_decl,
      method_create_export_default_expr,
      method_create_expr_stmt,
      method_create_ident,
      method_create_import_decl,
      method_create_import_default_specifier,
      method_create_import_named_specifier,
      method_create_import_star_as_specifier,
      method_create_jsx_text,
      method_create_module,
      method_create_named_export,
      method_create_null,
      method_create_number,
      method_create_object_lit,
      method_create_regex,
      method_create_script,
      method_create_spread_element,
      method_create_static_block,
      method_create_str,
      method_create_ts_export_assignment,
      method_create_ts_expr_with_type_args,
      method_create_ts_external_module_ref,
      method_create_ts_import_equals_decl,
      method_create_ts_namespace_export_decl,
      method_create_ts_type_ann,
      method_create_ts_type_param,
      method_create_ts_type_param_decl,
      method_create_ts_type_param_instantiation,
      method_create_unary_expr,
      method_create_using_decl,
      method_create_var_decl,
      method_create_var_declarator,
    }
  }

  pub fn create_big_int<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    sign: i32,
    raw: &Option<String>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let sign = int_to_jvalue!(sign);
    let java_raw = optional_string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_big_int,
        &[sign, raw, start_position, end_position],
        "Swc4jAstBigInt create_big_int()"
      );
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_binding_ident<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    type_ann: &Option<JObject>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let type_ann = optional_object_to_jvalue!(type_ann);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_binding_ident,
        &[id, type_ann, start_position, end_position],
        "Swc4jAstBindingIdent create_binding_ident()"
      );
    return_value
  }

  pub fn create_block_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    stmts: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let stmts = object_to_jvalue!(stmts);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_block_stmt,
        &[stmts, start_position, end_position],
        "Swc4jAstBlockStmt create_block_stmt()"
      );
    return_value
  }

  pub fn create_bool<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: bool,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let value = boolean_to_jvalue!(value);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_bool,
        &[value, start_position, end_position],
        "Swc4jAstBool create_bool()"
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
    range: &Range<usize>,
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
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_class,
        &[decorators, body, super_class, is_abstract, type_params, super_type_params, implements, start_position, end_position],
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
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let ident = object_to_jvalue!(ident);
    let declare = boolean_to_jvalue!(declare);
    let clazz = object_to_jvalue!(clazz);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_class_decl,
        &[ident, declare, clazz, start_position, end_position],
        "Swc4jAstClassDecl create_class_decl()"
      );
    return_value
  }

  pub fn create_constructor<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    key: &JObject<'_>,
    params: &JObject<'_>,
    body: &Option<JObject>,
    accessibility: i32,
    optional: bool,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let key = object_to_jvalue!(key);
    let params = object_to_jvalue!(params);
    let body = optional_object_to_jvalue!(body);
    let accessibility = int_to_jvalue!(accessibility);
    let optional = boolean_to_jvalue!(optional);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_constructor,
        &[key, params, body, accessibility, optional, start_position, end_position],
        "Swc4jAstConstructor create_constructor()"
      );
    return_value
  }

  pub fn create_debugger_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_debugger_stmt,
        &[start_position, end_position],
        "Swc4jAstDebuggerStmt create_debugger_stmt()"
      );
    return_value
  }

  pub fn create_decorator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_decorator,
        &[expr, start_position, end_position],
        "Swc4jAstDecorator create_decorator()"
      );
    return_value
  }

  pub fn create_empty_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_empty_stmt,
        &[start_position, end_position],
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
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let src = object_to_jvalue!(src);
    let type_only = boolean_to_jvalue!(type_only);
    let with = optional_object_to_jvalue!(with);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_all,
        &[src, type_only, with, start_position, end_position],
        "Swc4jAstExportAll create_export_all()"
      );
    return_value
  }

  pub fn create_export_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_decl,
        &[decl, start_position, end_position],
        "Swc4jAstExportDecl create_export_decl()"
      );
    return_value
  }

  pub fn create_export_default_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_default_decl,
        &[decl, start_position, end_position],
        "Swc4jAstExportDefaultDecl create_export_default_decl()"
      );
    return_value
  }

  pub fn create_export_default_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_export_default_expr,
        &[decl, start_position, end_position],
        "Swc4jAstExportDefaultExpr create_export_default_expr()"
      );
    return_value
  }

  pub fn create_expr_stmt<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_expr_stmt,
        &[expr, start_position, end_position],
        "Swc4jAstExprStmt create_expr_stmt()"
      );
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
    let java_sym = string_to_jstring!(env, &sym);
    let sym = object_to_jvalue!(java_sym);
    let optional = boolean_to_jvalue!(optional);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ident,
        &[sym, optional, start_position, end_position],
        "Swc4jAstIdent create_ident()"
      );
    delete_local_ref!(env, java_sym);
    return_value
  }

  pub fn create_import_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    specifiers: &JObject<'_>,
    src: &JObject<'_>,
    type_only: bool,
    with: &Option<JObject>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let specifiers = object_to_jvalue!(specifiers);
    let src = object_to_jvalue!(src);
    let type_only = boolean_to_jvalue!(type_only);
    let with = optional_object_to_jvalue!(with);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_decl,
        &[specifiers, src, type_only, with, start_position, end_position],
        "Swc4jAstImportDecl create_import_decl()"
      );
    return_value
  }

  pub fn create_import_default_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    local: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let local = object_to_jvalue!(local);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_default_specifier,
        &[local, start_position, end_position],
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
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let local = object_to_jvalue!(local);
    let imported = optional_object_to_jvalue!(imported);
    let type_only = boolean_to_jvalue!(type_only);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_named_specifier,
        &[local, imported, type_only, start_position, end_position],
        "Swc4jAstImportNamedSpecifier create_import_named_specifier()"
      );
    return_value
  }

  pub fn create_import_star_as_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    local: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let local = object_to_jvalue!(local);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_import_star_as_specifier,
        &[local, start_position, end_position],
        "Swc4jAstImportStarAsSpecifier create_import_star_as_specifier()"
      );
    return_value
  }

  pub fn create_jsx_text<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: &str,
    raw: &str,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let java_raw = string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_text,
        &[value, raw, start_position, end_position],
        "Swc4jAstJsxText create_jsx_text()"
      );
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_raw);
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
    let body = object_to_jvalue!(body);
    let java_shebang = optional_string_to_jstring!(env, &shebang);
    let shebang = object_to_jvalue!(java_shebang);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_module,
        &[body, shebang, start_position, end_position],
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
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let specifiers = object_to_jvalue!(specifiers);
    let src = optional_object_to_jvalue!(src);
    let type_only = boolean_to_jvalue!(type_only);
    let with = optional_object_to_jvalue!(with);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_named_export,
        &[specifiers, src, type_only, with, start_position, end_position],
        "Swc4jAstNamedExport create_named_export()"
      );
    return_value
  }

  pub fn create_null<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_null,
        &[start_position, end_position],
        "Swc4jAstNull create_null()"
      );
    return_value
  }

  pub fn create_number<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: f64,
    raw: &Option<String>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let value = double_to_jvalue!(value);
    let java_raw = optional_string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_number,
        &[value, raw, start_position, end_position],
        "Swc4jAstNumber create_number()"
      );
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_object_lit<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    props: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let props = object_to_jvalue!(props);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_object_lit,
        &[props, start_position, end_position],
        "Swc4jAstObjectLit create_object_lit()"
      );
    return_value
  }

  pub fn create_regex<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    exp: &str,
    flags: &str,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_exp = string_to_jstring!(env, &exp);
    let exp = object_to_jvalue!(java_exp);
    let java_flags = string_to_jstring!(env, &flags);
    let flags = object_to_jvalue!(java_flags);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_regex,
        &[exp, flags, start_position, end_position],
        "Swc4jAstRegex create_regex()"
      );
    delete_local_ref!(env, java_exp);
    delete_local_ref!(env, java_flags);
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
    let body = object_to_jvalue!(body);
    let java_shebang = optional_string_to_jstring!(env, &shebang);
    let shebang = object_to_jvalue!(java_shebang);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_script,
        &[body, shebang, start_position, end_position],
        "Swc4jAstScript create_script()"
      );
    delete_local_ref!(env, java_shebang);
    return_value
  }

  pub fn create_spread_element<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    dot3_token_start_position: i32,
    dot3_token_end_position: i32,
    expr: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let dot3_token_start_position = int_to_jvalue!(dot3_token_start_position);
    let dot3_token_end_position = int_to_jvalue!(dot3_token_end_position);
    let expr = object_to_jvalue!(expr);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_spread_element,
        &[dot3_token_start_position, dot3_token_end_position, expr, start_position, end_position],
        "Swc4jAstSpreadElement create_spread_element()"
      );
    return_value
  }

  pub fn create_static_block<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let body = object_to_jvalue!(body);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_static_block,
        &[body, start_position, end_position],
        "Swc4jAstStaticBlock create_static_block()"
      );
    return_value
  }

  pub fn create_str<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    value: &str,
    raw: &Option<String>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let java_raw = optional_string_to_jstring!(env, &raw);
    let raw = object_to_jvalue!(java_raw);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_str,
        &[value, raw, start_position, end_position],
        "Swc4jAstStr create_str()"
      );
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_raw);
    return_value
  }

  pub fn create_ts_export_assignment<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    decl: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let decl = object_to_jvalue!(decl);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_export_assignment,
        &[decl, start_position, end_position],
        "Swc4jAstTsExportAssignment create_ts_export_assignment()"
      );
    return_value
  }

  pub fn create_ts_expr_with_type_args<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    type_args: &Option<JObject>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let type_args = optional_object_to_jvalue!(type_args);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_expr_with_type_args,
        &[expr, type_args, start_position, end_position],
        "Swc4jAstTsExprWithTypeArgs create_ts_expr_with_type_args()"
      );
    return_value
  }

  pub fn create_ts_external_module_ref<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    expr: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let expr = object_to_jvalue!(expr);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_external_module_ref,
        &[expr, start_position, end_position],
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
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let export = boolean_to_jvalue!(export);
    let type_only = boolean_to_jvalue!(type_only);
    let id = object_to_jvalue!(id);
    let module_ref = object_to_jvalue!(module_ref);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_import_equals_decl,
        &[export, type_only, id, module_ref, start_position, end_position],
        "Swc4jAstTsImportEqualsDecl create_ts_import_equals_decl()"
      );
    return_value
  }

  pub fn create_ts_namespace_export_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    id: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = object_to_jvalue!(id);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_namespace_export_decl,
        &[id, start_position, end_position],
        "Swc4jAstTsNamespaceExportDecl create_ts_namespace_export_decl()"
      );
    return_value
  }

  pub fn create_ts_type_ann<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    type_ann: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let type_ann = object_to_jvalue!(type_ann);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_ann,
        &[type_ann, start_position, end_position],
        "Swc4jAstTsTypeAnn create_ts_type_ann()"
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
    range: &Range<usize>,
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
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_param,
        &[name, is_in, is_out, is_const, constraint, default, start_position, end_position],
        "Swc4jAstTsTypeParam create_ts_type_param()"
      );
    return_value
  }

  pub fn create_ts_type_param_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    params: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let params = object_to_jvalue!(params);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_param_decl,
        &[params, start_position, end_position],
        "Swc4jAstTsTypeParamDecl create_ts_type_param_decl()"
      );
    return_value
  }

  pub fn create_ts_type_param_instantiation<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    params: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let params = object_to_jvalue!(params);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_ts_type_param_instantiation,
        &[params, start_position, end_position],
        "Swc4jAstTsTypeParamInstantiation create_ts_type_param_instantiation()"
      );
    return_value
  }

  pub fn create_unary_expr<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    op: i32,
    arg: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let op = int_to_jvalue!(op);
    let arg = object_to_jvalue!(arg);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_unary_expr,
        &[op, arg, start_position, end_position],
        "Swc4jAstUnaryExpr create_unary_expr()"
      );
    return_value
  }

  pub fn create_using_decl<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    is_await: bool,
    decls: &JObject<'_>,
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let is_await = boolean_to_jvalue!(is_await);
    let decls = object_to_jvalue!(decls);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_using_decl,
        &[is_await, decls, start_position, end_position],
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
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let kind_id = int_to_jvalue!(kind_id);
    let declare = boolean_to_jvalue!(declare);
    let decls = object_to_jvalue!(decls);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_var_decl,
        &[kind_id, declare, decls, start_position, end_position],
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
    range: &Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let name = object_to_jvalue!(name);
    let init = optional_object_to_jvalue!(init);
    let definite = boolean_to_jvalue!(definite);
    let start_position = int_to_jvalue!(range.start);
    let end_position = int_to_jvalue!(range.end);
    let return_value = 
      call_static_as_object!(
        env,
        &self.class,
        self.method_create_var_declarator,
        &[name, init, definite, start_position, end_position],
        "Swc4jAstVarDeclarator create_var_declarator()"
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
      Callee::Super(node) => register_super(map, node),
      Callee::Import(node) => register_import(map, node),
      Callee::Expr(node) => enum_register_expr(map, node),
    }
  }

  fn enum_register_class_member(map: &mut ByteToIndexMap, node: &ClassMember) {
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

  fn enum_register_decl(map: &mut ByteToIndexMap, node: &Decl) {
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

  fn enum_register_default_decl(map: &mut ByteToIndexMap, node: &DefaultDecl) {
    match node {
      DefaultDecl::Class(node) => register_class_expr(map, node),
      DefaultDecl::Fn(node) => register_fn_expr(map, node),
      DefaultDecl::TsInterfaceDecl(node) => register_ts_interface_decl(map, node),
    }
  }

  fn enum_register_export_specifier(map: &mut ByteToIndexMap, node: &ExportSpecifier) {
    match node {
      ExportSpecifier::Namespace(node) => register_export_namespace_specifier(map, node),
      ExportSpecifier::Default(node) => register_export_default_specifier(map, node),
      ExportSpecifier::Named(node) => register_export_named_specifier(map, node),
    }
  }

  fn enum_register_expr(map: &mut ByteToIndexMap, node: &Expr) {
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
      Expr::Lit(node) => enum_register_lit(map, node),
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

  fn enum_register_for_head(map: &mut ByteToIndexMap, node: &ForHead) {
    match node {
      ForHead::VarDecl(node) => register_var_decl(map, node),
      ForHead::UsingDecl(node) => register_using_decl(map, node),
      ForHead::Pat(node) => enum_register_pat(map, node),
    }
  }

  fn enum_register_import_specifier(map: &mut ByteToIndexMap, node: &ImportSpecifier) {
    match node {
      ImportSpecifier::Named(node) => register_import_named_specifier(map, node),
      ImportSpecifier::Default(node) => register_import_default_specifier(map, node),
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
      JSXAttrValue::Lit(node) => enum_register_lit(map, node),
      JSXAttrValue::JSXExprContainer(node) => register_jsx_expr_container(map, node),
      JSXAttrValue::JSXElement(node) => register_jsx_element(map, node),
      JSXAttrValue::JSXFragment(node) => register_jsx_fragment(map, node),
    }
  }

  fn enum_register_jsx_element_child(map: &mut ByteToIndexMap, node: &JSXElementChild) {
    match node {
      JSXElementChild::JSXText(node) => register_jsx_text(map, node),
      JSXElementChild::JSXExprContainer(node) => register_jsx_expr_container(map, node),
      JSXElementChild::JSXSpreadChild(node) => register_jsx_spread_child(map, node),
      JSXElementChild::JSXElement(node) => register_jsx_element(map, node),
      JSXElementChild::JSXFragment(node) => register_jsx_fragment(map, node),
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
      JSXExpr::JSXEmptyExpr(node) => register_jsx_empty_expr(map, node),
      JSXExpr::Expr(node) => enum_register_expr(map, node),
    }
  }

  fn enum_register_jsx_object(map: &mut ByteToIndexMap, node: &JSXObject) {
    match node {
      JSXObject::JSXMemberExpr(node) => register_jsx_member_expr(map, node),
      JSXObject::Ident(node) => register_ident(map, node),
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
      Lit::Str(node) => register_str(map, node),
      Lit::Bool(node) => register_bool(map, node),
      Lit::Null(node) => register_null(map, node),
      Lit::Num(node) => register_number(map, node),
      Lit::BigInt(node) => register_big_int(map, node),
      Lit::Regex(node) => register_regex(map, node),
      Lit::JSXText(node) => register_jsx_text(map, node),
    }
  }

  fn enum_register_member_prop(map: &mut ByteToIndexMap, node: &MemberProp) {
    match node {
      MemberProp::Ident(node) => register_ident(map, node),
      MemberProp::PrivateName(node) => register_private_name(map, node),
      MemberProp::Computed(node) => register_computed_prop_name(map, node),
    }
  }

  fn enum_register_module_decl(map: &mut ByteToIndexMap, node: &ModuleDecl) {
    match node {
      ModuleDecl::Import(node) => register_import_decl(map, node),
      ModuleDecl::ExportDecl(node) => register_export_decl(map, node),
      ModuleDecl::ExportNamed(node) => register_named_export(map, node),
      ModuleDecl::ExportDefaultDecl(node) => register_export_default_decl(map, node),
      ModuleDecl::ExportDefaultExpr(node) => register_export_default_expr(map, node),
      ModuleDecl::ExportAll(node) => register_export_all(map, node),
      ModuleDecl::TsImportEquals(node) => register_ts_import_equals_decl(map, node),
      ModuleDecl::TsExportAssignment(node) => register_ts_export_assignment(map, node),
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
      ObjectPatProp::KeyValue(node) => register_key_value_pat_prop(map, node),
      ObjectPatProp::Assign(node) => register_assign_pat_prop(map, node),
      ObjectPatProp::Rest(node) => register_rest_pat(map, node),
    }
  }

  fn enum_register_opt_chain_base(map: &mut ByteToIndexMap, node: &OptChainBase) {
    match node {
      OptChainBase::Member(node) => register_member_expr(map, node),
      OptChainBase::Call(node) => register_opt_call(map, node),
    }
  }

  fn enum_register_param_or_ts_param_prop(map: &mut ByteToIndexMap, node: &ParamOrTsParamProp) {
    match node {
      ParamOrTsParamProp::TsParamProp(node) => register_ts_param_prop(map, node),
      ParamOrTsParamProp::Param(node) => register_param(map, node),
    }
  }

  fn enum_register_pat(map: &mut ByteToIndexMap, node: &Pat) {
    match &node {
      Pat::Ident(node) => register_binding_ident(map, node),
      Pat::Array(node) => register_array_pat(map, node),
      Pat::Rest(node) => register_rest_pat(map, node),
      Pat::Object(node) => register_object_pat(map, node),
      Pat::Assign(node) => register_assign_pat(map, node),
      Pat::Invalid(node) => register_invalid(map, node),
      Pat::Expr(node) => enum_register_expr(map, node),
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
      Prop::Shorthand(node) => register_ident(map, node),
      Prop::KeyValue(node) => register_key_value_prop(map, node),
      Prop::Assign(node) => register_assign_prop(map, node),
      Prop::Getter(node) => register_getter_prop(map, node),
      Prop::Setter(node) => register_setter_prop(map, node),
      Prop::Method(node) => register_method_prop(map, node),
    }
  }

  fn enum_register_prop_name(map: &mut ByteToIndexMap, node: &PropName) {
    match node {
      PropName::Ident(node) => register_ident(map, node),
      PropName::Str(node) => register_str(map, node),
      PropName::Num(node) => register_number(map, node),
      PropName::Computed(node) => register_computed_prop_name(map, node),
      PropName::BigInt(node) => register_big_int(map, node),
    }
  }

  fn enum_register_prop_or_spread(map: &mut ByteToIndexMap, node: &PropOrSpread) {
    match node {
      PropOrSpread::Spread(node) => register_spread_element(map, node),
      PropOrSpread::Prop(node) => enum_register_prop(map, node),
    }
  }

  fn enum_register_stmt(map: &mut ByteToIndexMap, node: &Stmt) {
    match node {
      Stmt::Block(node) => register_block_stmt(map, node),
      Stmt::Empty(node) => register_empty_stmt(map, node),
      Stmt::Debugger(node) => register_debugger_stmt(map, node),
      Stmt::With(node) => register_with_stmt(map, node),
      Stmt::Return(node) => register_return_stmt(map, node),
      Stmt::Labeled(node) => register_labeled_stmt(map, node),
      Stmt::Break(node) => register_break_stmt(map, node),
      Stmt::Continue(node) => register_continue_stmt(map, node),
      Stmt::If(node) => register_if_stmt(map, node),
      Stmt::Switch(node) => register_switch_stmt(map, node),
      Stmt::Throw(node) => register_throw_stmt(map, node),
      Stmt::Try(node) => register_try_stmt(map, node),
      Stmt::While(node) => register_while_stmt(map, node),
      Stmt::DoWhile(node) => register_do_while_stmt(map, node),
      Stmt::For(node) => register_for_stmt(map, node),
      Stmt::ForIn(node) => register_for_in_stmt(map, node),
      Stmt::ForOf(node) => register_for_of_stmt(map, node),
      Stmt::Decl(node) => enum_register_decl(map, node),
      Stmt::Expr(node) => register_expr_stmt(map, node),
    };
  }

  fn enum_register_super_prop(map: &mut ByteToIndexMap, node: &SuperProp) {
    match node {
      SuperProp::Ident(node) => register_ident(map, node),
      SuperProp::Computed(node) => register_computed_prop_name(map, node),
    }
  }

  fn enum_register_ts_entity_name(map: &mut ByteToIndexMap, node: &TsEntityName) {
    match node {
      TsEntityName::TsQualifiedName(node) => register_ts_qualified_name(map, node),
      TsEntityName::Ident(node) => register_ident(map, node),
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
      TsFnOrConstructorType::TsFnType(node) => register_ts_fn_type(map, node),
      TsFnOrConstructorType::TsConstructorType(node) => register_ts_constructor_type(map, node),
    }
  }

  fn enum_register_ts_fn_param(map: &mut ByteToIndexMap, node: &TsFnParam) {
    match node {
      TsFnParam::Ident(node) => register_binding_ident(map, node),
      TsFnParam::Array(node) => register_array_pat(map, node),
      TsFnParam::Rest(node) => register_rest_pat(map, node),
      TsFnParam::Object(node) => register_object_pat(map, node),
    }
  }

  fn enum_register_ts_lit(map: &mut ByteToIndexMap, node: &TsLit) {
    match node {
      TsLit::Number(node) => register_number(map, node),
      TsLit::Str(node) => register_str(map, node),
      TsLit::Bool(node) => register_bool(map, node),
      TsLit::BigInt(node) => register_big_int(map, node),
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
      TsParamPropParam::Ident(node) => register_binding_ident(map, node),
      TsParamPropParam::Assign(node) => register_assign_pat(map, node),
    }
  }

  fn enum_register_ts_this_type_or_ident(map: &mut ByteToIndexMap, node: &TsThisTypeOrIdent) {
    match node {
      TsThisTypeOrIdent::TsThisType(node) => register_ts_this_type(map, node),
      TsThisTypeOrIdent::Ident(node) => register_ident(map, node),
    }
  }

  fn enum_register_ts_type(map: &mut ByteToIndexMap, node: &TsType) {
    match node {
      TsType::TsKeywordType(node) => register_ts_keyword_type(map, node),
      TsType::TsThisType(node) => register_ts_this_type(map, node),
      TsType::TsFnOrConstructorType(node) => enum_register_ts_fn_or_constructor_type(map, node),
      TsType::TsTypeRef(node) => register_ts_type_ref(map, node),
      TsType::TsTypeQuery(node) => register_ts_type_query(map, node),
      TsType::TsTypeLit(node) => register_ts_type_lit(map, node),
      TsType::TsArrayType(node) => register_ts_array_type(map, node),
      TsType::TsTupleType(node) => register_ts_tuple_type(map, node),
      TsType::TsOptionalType(node) => register_ts_optional_type(map, node),
      TsType::TsRestType(node) => register_ts_rest_type(map, node),
      TsType::TsUnionOrIntersectionType(node) => enum_register_ts_union_or_intersection_type(map, node),
      TsType::TsConditionalType(node) => register_ts_conditional_type(map, node),
      TsType::TsInferType(node) => register_ts_infer_type(map, node),
      TsType::TsParenthesizedType(node) => register_ts_parenthesized_type(map, node),
      TsType::TsTypeOperator(node) => register_ts_type_operator(map, node),
      TsType::TsIndexedAccessType(node) => register_ts_indexed_access_type(map, node),
      TsType::TsMappedType(node) => register_ts_mapped_type(map, node),
      TsType::TsLitType(node) => register_ts_lit_type(map, node),
      TsType::TsTypePredicate(node) => register_ts_type_predicate(map, node),
      TsType::TsImportType(node) => register_ts_import_type(map, node),
    }
  }

  fn enum_register_ts_type_element(map: &mut ByteToIndexMap, node: &TsTypeElement) {
    match node {
      TsTypeElement::TsCallSignatureDecl(node) => register_ts_call_signature_decl(map, node),
      TsTypeElement::TsConstructSignatureDecl(node) => register_ts_construct_signature_decl(map, node),
      TsTypeElement::TsPropertySignature(node) => register_ts_property_signature(map, node),
      TsTypeElement::TsGetterSignature(node) => register_ts_getter_signature(map, node),
      TsTypeElement::TsSetterSignature(node) => register_ts_setter_signature(map, node),
      TsTypeElement::TsMethodSignature(node) => register_ts_method_signature(map, node),
      TsTypeElement::TsIndexSignature(node) => register_ts_index_signature(map, node),
    }
  }

  fn enum_register_ts_type_query_expr(map: &mut ByteToIndexMap, node: &TsTypeQueryExpr) {
    match node {
      TsTypeQueryExpr::TsEntityName(node) => enum_register_ts_entity_name(map, node),
      TsTypeQueryExpr::Import(node) => register_ts_import_type(map, node),
    }
  }

  fn enum_register_ts_union_or_intersection_type(map: &mut ByteToIndexMap, node: &TsUnionOrIntersectionType) {
    match node {
      TsUnionOrIntersectionType::TsUnionType(node) => register_ts_union_type(map, node),
      TsUnionOrIntersectionType::TsIntersectionType(node) => register_ts_intersection_type(map, node),
    }
  }

  fn enum_register_var_decl_or_expr(map: &mut ByteToIndexMap, node: &VarDeclOrExpr) {
    match node {
      VarDeclOrExpr::VarDecl(node) => register_var_decl(map, node),
      VarDeclOrExpr::Expr(node) => enum_register_expr(map, node),
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
  use deno_ast::swc::common::source_map::Pos;
  use deno_ast::swc::common::Spanned;
  use jni::objects::JObject;
  use jni::JNIEnv;

  use crate::ast_utils::JAVA_AST_FACTORY;
  use crate::enums::IdentifiableEnum;
  use crate::jni_utils::*;
  use crate::position_utils::ByteToIndexMap;

  use std::sync::Arc;

  use deno_ast::swc::ast::*;

  fn create_big_int<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &BigInt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let sign = node.value.sign().get_id();
    let raw = node.raw.as_ref().map(|node| node.as_str().to_owned());
    java_ast_factory.create_big_int(env, sign, &raw, &range)
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
    let range = map.get_range_by_span(&node.span);
    let java_id = create_ident(env, map, &node.id);
    let java_type_ann = node.type_ann.as_ref().map(|node| create_ts_type_ann(env, map, node));
    let return_value = java_ast_factory.create_binding_ident(env, &java_id, &java_type_ann, &range);
    delete_local_ref!(env, java_id);
    delete_local_optional_ref!(env, java_type_ann);
    return_value
  }

  fn create_block_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &BlockStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_stmts = java_array_list.construct(env, node.stmts.len());
    node.stmts.iter().for_each(|node| {
      let java_node = enum_create_stmt(env, map, node);
      java_array_list.add(env, &java_stmts, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_block_stmt(env, &java_stmts, &range);
    delete_local_ref!(env, java_stmts);
    return_value
  }

  fn create_bool<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Bool) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let value = node.value;
    java_ast_factory.create_bool(env, value, &range)
  }

  fn create_class<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Class) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
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
    let java_option_super_class = node.super_class.as_ref().map(|node| enum_create_expr(env, map, node));
    let is_abstract = node.is_abstract;
    let java_type_params = node
      .type_params
      .as_ref()
      .map(|node| create_ts_type_param_decl(env, map, node));
    let java_super_type_params = node
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
      &java_option_super_class,
      is_abstract,
      &java_type_params,
      &java_super_type_params,
      &java_implements,
      &range,
    );
    delete_local_ref!(env, java_decorators);
    delete_local_ref!(env, java_body);
    delete_local_optional_ref!(env, java_option_super_class);
    delete_local_optional_ref!(env, java_type_params);
    delete_local_optional_ref!(env, java_super_type_params);
    delete_local_ref!(env, java_implements);
    return_value
  }

  fn create_class_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ClassDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span());
    let java_ident = create_ident(env, map, &node.ident);
    let declare = node.declare;
    let java_class = create_class(env, map, &node.class);
    let return_value = java_ast_factory.create_class_decl(env, &java_ident, declare, &java_class, &range);
    delete_local_ref!(env, java_ident);
    delete_local_ref!(env, java_class);
    return_value
  }

  fn create_constructor<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Constructor) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_key = enum_create_prop_name(env, map, &node.key);
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = enum_create_param_or_ts_param_prop(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_body = node.body.as_ref().map(|node| create_block_stmt(env, map, node));
    let accessibility = node
      .accessibility
      .map_or_else(|| Accessibility::Public.get_id(), |node| node.get_id());
    let is_optional = node.is_optional;
    let return_type = java_ast_factory.create_constructor(
      env,
      &java_key,
      &java_params,
      &java_body,
      accessibility,
      is_optional,
      &range,
    );
    delete_local_ref!(env, java_key);
    delete_local_ref!(env, java_params);
    return_type
  }

  fn create_decorator<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Decorator) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_type = java_ast_factory.create_decorator(env, &java_expr, &range);
    delete_local_ref!(env, java_expr);
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
    let range = map.get_range_by_span(&node.span);
    java_ast_factory.create_debugger_stmt(env, &range)
  }

  fn create_empty_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &EmptyStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    java_ast_factory.create_empty_stmt(env, &range)
  }

  fn create_export_all<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ExportAll) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_src = create_str(env, map, &node.src);
    let type_only = node.type_only;
    let java_option_with = node.with.as_ref().map(|node| create_object_lit(env, map, node));
    let return_value = java_ast_factory.create_export_all(env, &java_src, type_only, &java_option_with, &range);
    delete_local_ref!(env, java_src);
    delete_local_optional_ref!(env, java_option_with);
    return_value
  }

  fn create_export_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ExportDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_decl = enum_create_decl(env, map, &node.decl);
    let return_value = java_ast_factory.create_export_decl(env, &java_decl, &range);
    delete_local_ref!(env, java_decl);
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
    let range = map.get_range_by_span(&node.span);
    let java_decl = enum_create_default_decl(env, map, &node.decl);
    let return_value = java_ast_factory.create_export_default_decl(env, &java_decl, &range);
    delete_local_ref!(env, java_decl);
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
    let range = map.get_range_by_span(&node.span);
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_export_default_expr(env, &java_expr, &range);
    delete_local_ref!(env, java_expr);
    return_value
  }

  fn create_expr_stmt<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ExprStmt) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_expr_stmt(env, &java_expr, &range);
    delete_local_ref!(env, java_expr);
    return_value
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
    let range = map.get_range_by_span(&node.span);
    let java_local = create_ident(env, map, &node.local);
    let return_value = java_ast_factory.create_import_default_specifier(env, &java_local, &range);
    delete_local_ref!(env, java_local);
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
    let range = map.get_range_by_span(&node.span);
    let java_local = create_ident(env, map, &node.local);
    let java_imported = node
      .imported
      .as_ref()
      .map(|node| enum_create_module_export_name(env, map, node));
    let is_type_only = node.is_type_only;
    let return_value =
      java_ast_factory.create_import_named_specifier(env, &java_local, &java_imported, is_type_only, &range);
    delete_local_ref!(env, java_local);
    delete_local_optional_ref!(env, java_imported);
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
    let range = map.get_range_by_span(&node.span);
    let java_local = create_ident(env, map, &node.local);
    let return_value = java_ast_factory.create_import_star_as_specifier(env, &java_local, &range);
    delete_local_ref!(env, java_local);
    return_value
  }

  fn create_jsx_text<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXText) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let value = node.value.as_str();
    let raw = node.raw.as_str();
    java_ast_factory.create_jsx_text(env, value, raw, &range)
  }

  fn create_ident<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Ident) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let sym = node.sym.as_str();
    let optional = node.optional;
    java_ast_factory.create_ident(env, sym, optional, &range)
  }

  fn create_import_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ImportDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_specifiers = java_array_list.construct(env, node.specifiers.len());
    node.specifiers.iter().for_each(|node| {
      let java_node = enum_create_import_specifier(env, map, node);
      java_array_list.add(env, &java_specifiers, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_src = create_str(env, map, &node.src);
    let type_only = node.type_only;
    let java_option_with = node.with.as_ref().map(|node| create_object_lit(env, map, node));
    let return_value =
      java_ast_factory.create_import_decl(env, &java_specifiers, &java_src, type_only, &java_option_with, &range);
    delete_local_ref!(env, java_specifiers);
    delete_local_ref!(env, java_src);
    delete_local_optional_ref!(env, java_option_with);
    return_value
  }

  fn create_module<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Module) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let shebang: Option<String> = node.shebang.to_owned().map(|s| s.to_string());
    let range = map.get_range_by_span(&node.span);
    let java_body = java_array_list.construct(env, node.body.len());
    node.body.iter().for_each(|node| {
      let java_node = enum_create_module_item(env, map, node);
      java_array_list.add(env, &java_body, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_module(env, &java_body, &shebang, &range);
    delete_local_ref!(env, java_body);
    return_value
  }

  fn create_named_export<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &NamedExport) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_specifiers = java_array_list.construct(env, node.specifiers.len());
    node.specifiers.iter().for_each(|node| {
      let java_node = enum_create_export_specifier(env, map, node);
      java_array_list.add(env, &java_specifiers, &java_node);
      delete_local_ref!(env, java_node);
    });
    let java_option_src = node.src.as_ref().map(|node| create_str(env, map, node));
    let type_only = node.type_only;
    let java_option_with = node.with.as_ref().map(|node| create_object_lit(env, map, node));
    let return_value = java_ast_factory.create_named_export(
      env,
      &java_specifiers,
      &java_option_src,
      type_only,
      &java_option_with,
      &range,
    );
    delete_local_ref!(env, java_specifiers);
    delete_local_optional_ref!(env, java_option_src);
    delete_local_optional_ref!(env, java_option_with);
    return_value
  }

  fn create_null<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Null) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    java_ast_factory.create_null(env, &range)
  }

  fn create_number<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Number) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let value = node.value;
    let raw = node.raw.as_ref().map(|node| node.as_str().to_owned());
    java_ast_factory.create_number(env, value, &raw, &range)
  }

  fn create_object_lit<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &ObjectLit) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_props = java_array_list.construct(env, node.props.len());
    node.props.iter().for_each(|node| {
      let java_node = enum_create_prop_or_spread(env, map, node);
      java_array_list.add(env, &java_props, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_object_lit(env, &java_props, &range);
    delete_local_ref!(env, java_props);
    return_value
  }

  fn create_regex<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Regex) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let exp = node.exp.as_str();
    let flags = node.flags.as_str();
    java_ast_factory.create_regex(env, exp, flags, &range)
  }

  fn create_script<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Script) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let shebang: Option<String> = node.shebang.to_owned().map(|s| s.to_string());
    let range = map.get_range_by_span(&node.span);
    let java_body = java_array_list.construct(env, node.body.len());
    node.body.iter().for_each(|node| {
      let java_node = enum_create_stmt(env, map, node);
      java_array_list.add(env, &java_body, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_script(env, &java_body, &shebang, &range);
    delete_local_ref!(env, java_body);
    return_value
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
    let range = map.get_range_by_span(&node.span());
    let dot3_token_start_position = node.dot3_token.lo().to_usize() as i32;
    let dot3_token_end_position = node.dot3_token.hi().to_usize() as i32;
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_spread_element(
      env,
      dot3_token_start_position,
      dot3_token_end_position,
      &java_expr,
      &range,
    );
    delete_local_ref!(env, java_expr);
    return_value
  }

  fn create_static_block<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &StaticBlock) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_body = create_block_stmt(env, map, &node.body);
    let return_value = java_ast_factory.create_static_block(env, &java_body, &range);
    delete_local_ref!(env, java_body);
    return_value
  }

  fn create_str<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Str) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let value = node.value.as_str();
    let raw = node.raw.as_ref().map(|node| node.as_str().to_owned());
    java_ast_factory.create_str(env, value, &raw, &range)
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
    let range = map.get_range_by_span(&node.span);
    let java_expr = enum_create_expr(env, map, &node.expr);
    let return_value = java_ast_factory.create_ts_export_assignment(env, &java_expr, &range);
    delete_local_ref!(env, java_expr);
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
    let range = map.get_range_by_span(&node.span);
    let java_expr = enum_create_expr(env, map, &node.expr);
    let java_type_args = node
      .type_args
      .as_ref()
      .map(|node| create_ts_type_param_instantiation(env, map, node));
    let return_value = java_ast_factory.create_ts_expr_with_type_args(env, &java_expr, &java_type_args, &range);
    delete_local_ref!(env, java_expr);
    delete_local_optional_ref!(env, java_type_args);
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
    let range = map.get_range_by_span(&node.span);
    let java_expr = create_str(env, map, &node.expr);
    let return_value = java_ast_factory.create_ts_external_module_ref(env, &java_expr, &range);
    delete_local_ref!(env, java_expr);
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
    let range = map.get_range_by_span(&node.span);
    let is_export = node.is_export;
    let is_type_only = node.is_type_only;
    let java_id = create_ident(env, map, &node.id);
    let java_module_ref = enum_create_ts_module_ref(env, map, &node.module_ref);
    let return_value =
      java_ast_factory.create_ts_import_equals_decl(env, is_export, is_type_only, &java_id, &java_module_ref, &range);
    delete_local_ref!(env, java_id);
    delete_local_ref!(env, java_module_ref);
    return_value
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
    let range = map.get_range_by_span(&node.span);
    let java_id = create_ident(env, map, &node.id);
    let return_value = java_ast_factory.create_ts_namespace_export_decl(env, &java_id, &range);
    delete_local_ref!(env, java_id);
    return_value
  }

  fn create_ts_type_ann<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsTypeAnn) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_type_ann = enum_create_ts_type(env, map, &node.type_ann);
    let return_value = java_ast_factory.create_ts_type_ann(env, &java_type_ann, &range);
    delete_local_ref!(env, java_type_ann);
    return_value
  }

  fn create_ts_type_param<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &TsTypeParam) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let java_name = create_ident(env, map, &node.name);
    let is_in = node.is_in;
    let is_out = node.is_out;
    let is_const = node.is_const;
    let java_constraint = node.constraint.as_ref().map(|node| enum_create_ts_type(env, map, node));
    let java_default = node.default.as_ref().map(|node| enum_create_ts_type(env, map, node));
    let return_value = java_ast_factory.create_ts_type_param(
      env,
      &java_name,
      is_in,
      is_out,
      is_const,
      &java_constraint,
      &java_default,
      &range,
    );
    delete_local_ref!(env, java_name);
    delete_local_optional_ref!(env, java_constraint);
    delete_local_optional_ref!(env, java_default);
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
    let range = map.get_range_by_span(&node.span);
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = create_ts_type_param(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_ts_type_param_decl(env, &java_params, &range);
    delete_local_ref!(env, java_params);
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
    let range = map.get_range_by_span(&node.span);
    let java_params = java_array_list.construct(env, node.params.len());
    node.params.iter().for_each(|node| {
      let java_node = enum_create_ts_type(env, map, node);
      java_array_list.add(env, &java_params, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_ts_type_param_instantiation(env, &java_params, &range);
    delete_local_ref!(env, java_params);
    return_value
  }

  fn create_unary_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &UnaryExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let op = node.op.get_id();
    let java_arg = enum_create_expr(env, map, &node.arg);
    let return_value = java_ast_factory.create_unary_expr(env, op, &java_arg, &range);
    delete_local_ref!(env, java_arg);
    return_value
  }

  fn create_using_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &UsingDecl) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
    let range = map.get_range_by_span(&node.span);
    let is_await = node.is_await;
    let java_decls = java_array_list.construct(env, node.decls.len());
    node.decls.iter().for_each(|node| {
      let java_node = create_var_declarator(env, map, node);
      java_array_list.add(env, &java_decls, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_using_decl(env, is_await, &java_decls, &range);
    delete_local_ref!(env, java_decls);
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
    let range = map.get_range_by_span(&node.span);
    let java_decls = java_array_list.construct(env, node.decls.len());
    node.decls.iter().for_each(|node| {
      let java_node = create_var_declarator(env, map, node);
      java_array_list.add(env, &java_decls, &java_node);
      delete_local_ref!(env, java_node);
    });
    let return_value = java_ast_factory.create_var_decl(env, kind_id, declare, &java_decls, &range);
    delete_local_ref!(env, java_decls);
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
    let definite = node.definite;
    let java_option_init: Option<JObject> = node.init.as_ref().map(|node| enum_create_expr(env, map, node.as_ref()));
    let java_name = enum_create_pat(env, map, &node.name);
    let range = map.get_range_by_span(&node.span);
    let return_value = java_ast_factory.create_var_declarator(env, &java_name, &java_option_init, definite, &range);
    delete_local_optional_ref!(env, java_option_init);
    delete_local_ref!(env, java_name);
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
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_callee<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Callee) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
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
      ClassMember::Constructor(node) => create_constructor(env, map, node),
      ClassMember::StaticBlock(node) => create_static_block(env, map, node),
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_decl<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Decl) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Decl::Class(node) => create_class_decl(env, map, node),
      Decl::Var(node) => create_var_decl(env, map, node),
      Decl::Using(node) => create_using_decl(env, map, node),
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Expr) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Expr::Unary(node) => create_unary_expr(env, map, node),
      Expr::Ident(node) => create_ident(env, map, node),
      Expr::Lit(node) => enum_create_lit(env, map, node),
      default => panic!("{:?}", default),
      // TODO
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
      ImportSpecifier::Named(node) => create_import_named_specifier(env, map, node),
      ImportSpecifier::Default(node) => create_import_default_specifier(env, map, node),
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
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_jsx_expr<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXExpr) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_jsx_object<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &JSXObject) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_key<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Key) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_lit<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Lit) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Lit::Str(node) => create_str(env, map, node),
      Lit::Bool(node) => create_bool(env, map, node),
      Lit::Null(node) => create_null(env, map, node),
      Lit::Num(node) => create_number(env, map, node),
      Lit::BigInt(node) => create_big_int(env, map, node),
      Lit::Regex(node) => create_regex(env, map, node),
      Lit::JSXText(node) => create_jsx_text(env, map, node),
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
      default => panic!("{:?}", default),
      // TODO
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
      ModuleDecl::Import(node) => create_import_decl(env, map, node),
      ModuleDecl::ExportDecl(node) => create_export_decl(env, map, node),
      ModuleDecl::ExportNamed(node) => create_named_export(env, map, node),
      ModuleDecl::ExportDefaultDecl(node) => create_export_default_decl(env, map, node),
      ModuleDecl::ExportDefaultExpr(node) => create_export_default_expr(env, map, node),
      ModuleDecl::ExportAll(node) => create_export_all(env, map, node),
      ModuleDecl::TsImportEquals(node) => create_ts_import_equals_decl(env, map, node),
      ModuleDecl::TsExportAssignment(node) => create_ts_export_assignment(env, map, node),
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
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_pat<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &Pat) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      Pat::Ident(node) => create_binding_ident(env, map, node),
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
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
      Prop::Shorthand(node) => create_ident(env, map, node),
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_prop_name<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &PropName) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
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
      Stmt::Empty(node) => create_empty_stmt(env, map, node),
      Stmt::Debugger(node) => create_debugger_stmt(env, map, node),
      Stmt::Decl(node) => enum_create_decl(env, map, node),
      Stmt::Expr(node) => create_expr_stmt(env, map, node),
      default => panic!("{:?}", default),
      // TODO
    }
  }

  fn enum_create_super_prop<'local, 'a>(env: &mut JNIEnv<'local>, map: &ByteToIndexMap, node: &SuperProp) -> JObject<'a>
  where
    'local: 'a,
  {
    match node {
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
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
      default => panic!("{:?}", default),
      // TODO
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
      VarDeclOrExpr::VarDecl(node) => create_var_decl(env, map, node),
      VarDeclOrExpr::Expr(node) => enum_create_expr(env, map, node),
      // TODO
    }
  }
}
