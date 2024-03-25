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

use std::ops::Range;

/* JavaSwc4jAstFactory Begin */
struct JavaSwc4jAstFactory {
  #[allow(dead_code)]
  class: GlobalRef,
  method_create_module: JStaticMethodID,
  method_create_script: JStaticMethodID,
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
    JavaSwc4jAstFactory {
      class,
      method_create_module,
      method_create_script,
    }
  }
/* JavaSwc4jAstFactory End */

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

static mut JAVA_AST_FACTORY: Option<JavaSwc4jAstFactory> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_AST_FACTORY = Some(JavaSwc4jAstFactory::new(env));
  }
}

pub mod span {
  use crate::position_utils::ByteToIndexMap;
  use deno_ast::swc::ast::*;

  fn register_decl(byte_to_index_map: &mut ByteToIndexMap, decl: &Decl) {
    match decl {
      Decl::Var(var_decl) => register_var_decl(byte_to_index_map, var_decl.as_ref()),
      _ => {}
    };
  }

  fn register_module(byte_to_index_map: &mut ByteToIndexMap, module: &Module) {
    byte_to_index_map.register_by_span(&module.span);
  }

  fn register_script(byte_to_index_map: &mut ByteToIndexMap, script: &Script) {
    byte_to_index_map.register_by_span(&script.span);
    script
      .body
      .iter()
      .for_each(|stmt| register_stmt(byte_to_index_map, stmt))
  }

  fn register_stmt(byte_to_index_map: &mut ByteToIndexMap, stmt: &Stmt) {
    match stmt {
      Stmt::Decl(decl) => register_decl(byte_to_index_map, decl),
      _ => {}
    };
  }

  pub fn register_program(byte_to_index_map: &mut ByteToIndexMap, program: &Program) {
    if program.is_module() {
      register_module(byte_to_index_map, program.as_module().unwrap());
    } else if program.is_script() {
      register_script(byte_to_index_map, program.as_script().unwrap());
    }
  }

  fn register_var_decl(byte_to_index_map: &mut ByteToIndexMap, var_decl: &VarDecl) {
    byte_to_index_map.register_by_span(&var_decl.span);
    var_decl
      .decls
      .iter()
      .for_each(|var_declarator| register_var_declarator(byte_to_index_map, var_declarator));
  }

  fn register_var_declarator(byte_to_index_map: &mut ByteToIndexMap, var_declarator: &VarDeclarator) {
    byte_to_index_map.register_by_span(&var_declarator.span);
    // TODO
  }
}

pub mod program {
  use jni::objects::JObject;
  use jni::JNIEnv;

  use crate::ast_utils::JAVA_AST_FACTORY;
  use crate::jni_utils::JAVA_ARRAY_LIST;
  use crate::position_utils::ByteToIndexMap;

  use std::sync::Arc;

  use deno_ast::swc::ast::*;
  use deno_ast::swc::common::Spanned;

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

  fn create_ident<'local, 'a>(
    env: &mut JNIEnv<'local>,
    byte_to_index_map: &ByteToIndexMap,
    ident: &Ident,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let range = byte_to_index_map.get_range_by_span(&ident.span());
    let sym = ident.sym.as_str();
    let optional = ident.optional;
    Default::default()
  }

  pub fn create_program<'local, 'a>(
    env: &mut JNIEnv<'local>,
    byte_to_index_map: &ByteToIndexMap,
    program: &Option<Arc<Program>>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    match program {
      Some(arc_program) => {
        if arc_program.is_module() {
          create_module(
            env,
            byte_to_index_map,
            arc_program.as_module().expect("Couldn't get module"),
          )
        } else if arc_program.is_script() {
          create_script(
            env,
            byte_to_index_map,
            arc_program.as_script().expect("Couldn't get script"),
          )
        } else {
          Default::default()
        }
      }
      None => Default::default(),
    }
  }

  fn create_module<'local, 'a>(
    env: &mut JNIEnv<'local>,
    byte_to_index_map: &ByteToIndexMap,
    module: &Module,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let shebang: Option<String> = module.shebang.to_owned().map(|s| s.to_string());
    let range = byte_to_index_map.get_range_by_span(&module.span());
    let body = create_module_body(env, byte_to_index_map, &module.body);
    let java_module = java_ast_factory.create_module(env, &body, shebang, range);
    env.delete_local_ref(body).expect("Couldn't delete local module body");
    java_module
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

  fn create_script<'local, 'a>(
    env: &mut JNIEnv<'local>,
    byte_to_index_map: &ByteToIndexMap,
    script: &Script,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_factory = unsafe { JAVA_AST_FACTORY.as_ref().unwrap() };
    let shebang: Option<String> = script.shebang.to_owned().map(|s| s.to_string());
    let range = byte_to_index_map.get_range_by_span(&script.span());
    let body = create_script_body(env, byte_to_index_map, &script.body);
    let java_script = java_ast_factory.create_script(env, &body, shebang, range);
    env.delete_local_ref(body).expect("Couldn't delete local script body");
    java_script
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
}
