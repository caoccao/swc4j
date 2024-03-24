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

use std::collections::BTreeMap;
use std::ops::Range;

use deno_ast::swc::common::source_map::Pos;
use deno_ast::swc::common::Span;

pub struct ByteToIndexMap {
  map: BTreeMap<usize, usize>,
}

impl ByteToIndexMap {
  pub fn new() -> Self {
    ByteToIndexMap { map: BTreeMap::new() }
  }

  pub fn get_range_by_span(&self, span: &Span) -> Range<usize> {
    Range {
      start: *self
        .map
        .get(&(span.lo().to_usize() - 1))
        .expect("Couldn't find start index"),
      end: *self
        .map
        .get(&(span.hi().to_usize() - 1))
        .expect("Couldn't find end index"),
    }
  }

  pub fn register_by_span(&mut self, span: &Span) {
    [span.lo().to_usize() - 1, span.hi().to_usize() - 1]
      .into_iter()
      .for_each(|position| {
        if !self.map.contains_key(&position) {
          self.map.insert(position, 0);
        }
      });
  }

  pub fn update(&mut self, key: &usize, value: usize) {
    self.map.get_mut(&key).map(|v| *v = value);
  }
}

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
        "(Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstModule;",
      )
      .expect("Couldn't find method Swc4jAstFactory.createModule");
    let method_create_script = env
      .get_static_method_id(
        &class,
        "createScript",
        "(Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstScript;",
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
    shebang: Option<String>,
    range: Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
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
          &[shebang, start_position, end_position],
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
    shebang: Option<String>,
    range: Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
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
          &[shebang, start_position, end_position],
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
