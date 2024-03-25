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
use crate::jni_utils::JAVA_ARRAY_LIST;
use crate::position_utils::ByteToIndexMap;

use std::ops::Range;
use std::sync::Arc;

use deno_ast::swc::ast::*;
use deno_ast::swc::common::Spanned;

pub struct JavaAstFactory {
  #[allow(dead_code)]
  class: GlobalRef,
  method_create_module: JStaticMethodID,
  method_create_script: JStaticMethodID,
}
unsafe impl Send for JavaAstFactory {}
unsafe impl Sync for JavaAstFactory {}

impl JavaAstFactory {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/ast/Swc4jAstFactory")
      .expect("Couldn't find class Swc4jAstFactory");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jAstFactory");
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
    JavaAstFactory {
      class,
      method_create_module,
      method_create_script,
    }
  }

  pub fn create_module<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    shebang: Option<String>,
    range: Range<usize>,
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
    let ast = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_module,
          ReturnType::Object,
          &[body, shebang, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstModule")
        .l()
        .expect("Couldn't convert Swc4jAstModule")
    };
    env
      .delete_local_ref(java_shebang)
      .expect("Couldn't delete local shebang");
    ast
  }

  pub fn create_script<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    body: &JObject<'_>,
    shebang: Option<String>,
    range: Range<usize>,
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
    let ast = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_script,
          ReturnType::Object,
          &[body, shebang, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstScript")
        .l()
        .expect("Couldn't convert Swc4jAstScript")
    };
    env
      .delete_local_ref(java_shebang)
      .expect("Couldn't delete local shebang");
    ast
  }
}

pub static mut JAVA_AST_FACTORY: Option<JavaAstFactory> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_AST_FACTORY = Some(JavaAstFactory::new(env));
  }
}

fn create_decl_var<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  var_decl: &VarDecl,
) -> JObject<'a>
where
  'local: 'a,
{
  let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
  let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
  let declare = var_decl.declare;
  let kind = var_decl.kind;
  let range = byte_to_index_map.get_range_by_span(&var_decl.span());
  let java_decls = java_array_list.create(env, var_decl.decls.len());
  var_decl.decls.iter().for_each(|var_declarator| {
    let java_var_declarator = create_var_declarator(env, byte_to_index_map, var_declarator);
    java_array_list.add(env, &java_decls, &java_var_declarator);
    env
      .delete_local_ref(java_var_declarator)
      .expect("Couldn't delete local var declarator");
  });
  Default::default()
}

fn create_ident<'local, 'a>(env: &mut JNIEnv<'local>, byte_to_index_map: &ByteToIndexMap, ident: &Ident) -> JObject<'a>
where
  'local: 'a,
{
  let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
  let range = byte_to_index_map.get_range_by_span(&ident.span());
  let sym = ident.sym.as_str();
  let optional = ident.optional;
  Default::default()
}

pub fn create_module<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  program: &Option<Arc<Program>>,
) -> JObject<'a>
where
  'local: 'a,
{
  match program {
    Some(arc_program) => match arc_program.as_module() {
      Some(module) => {
        let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
        let shebang: Option<String> = module.shebang.to_owned().map(|s| s.to_string());
        let range = byte_to_index_map.get_range_by_span(&module.span());
        let body = create_module_body(env, byte_to_index_map, &module.body);
        let java_module = java_ast_factory.create_module(env, &body, shebang, range);
        env.delete_local_ref(body).expect("Couldn't delete local module body");
        java_module
      }
      None => Default::default(),
    },
    None => Default::default(),
  }
}

fn create_module_body<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  body: &Vec<ModuleItem>,
) -> JObject<'a>
where
  'local: 'a,
{
  let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
  let java_body = java_array_list.create(env, body.len());
  java_body
}

fn create_pat<'local, 'a>(env: &mut JNIEnv<'local>, byte_to_index_map: &ByteToIndexMap, pat: &Pat) -> JObject<'a>
where
  'local: 'a,
{
  let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
  let range = byte_to_index_map.get_range_by_span(&pat.span());
  match pat {
    Pat::Ident(bingding_ident) => {
      let java_id = create_ident(env, byte_to_index_map, &bingding_ident.id);
    }
    _ => {}
  }
  Default::default()
}

fn create_stmt<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  statement: &Stmt,
) -> JObject<'a>
where
  'local: 'a,
{
  match statement {
    Stmt::Decl(decl) => create_stmt_decl(env, byte_to_index_map, &decl),
    _ => Default::default(),
  }
}

fn create_stmt_decl<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  decl: &Decl,
) -> JObject<'a>
where
  'local: 'a,
{
  match decl {
    Decl::Var(box_var_decl) => create_decl_var(env, byte_to_index_map, &box_var_decl),
    _ => Default::default(),
  }
}

pub fn create_script<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  program: &Option<Arc<Program>>,
) -> JObject<'a>
where
  'local: 'a,
{
  match program {
    Some(arc_program) => match arc_program.as_script() {
      Some(script) => {
        let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
        let shebang: Option<String> = script.shebang.to_owned().map(|s| s.to_string());
        let range = byte_to_index_map.get_range_by_span(&script.span());
        let body = create_script_body(env, byte_to_index_map, &script.body);
        let java_script = java_ast_factory.create_script(env, &body, shebang, range);
        env.delete_local_ref(body).expect("Couldn't delete local script body");
        java_script
      }
      None => Default::default(),
    },
    None => Default::default(),
  }
}

fn create_script_body<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  body: &Vec<Stmt>,
) -> JObject<'a>
where
  'local: 'a,
{
  let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
  let java_body = java_array_list.create(env, body.len());
  body.into_iter().for_each(|stmt| {
    let java_stmt = create_stmt(env, byte_to_index_map, stmt);
    java_array_list.add(env, &java_body, &java_stmt);
    env.delete_local_ref(java_stmt).expect("Couldn't delete local stmt");
  });
  java_body
}

fn create_var_declarator<'local, 'a>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  var_declarator: &VarDeclarator,
) -> JObject<'a>
where
  'local: 'a,
{
  let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
  let range = byte_to_index_map.get_range_by_span(&var_declarator.span());
  let java_name = create_pat(env, byte_to_index_map, &var_declarator.name);
  Default::default()
}
