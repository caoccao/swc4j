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

use jni::objects::{GlobalRef, JMethodID, JObject, JStaticMethodID};
use jni::signature::ReturnType;
use jni::sys::jvalue;
use jni::JNIEnv;

pub use deno_ast::{ImportsNotUsedAsValues, MediaType};

use crate::jni_utils;

pub trait IdentifiableEnum<T> {
  fn get_id(&self) -> i32;
  fn parse_by_id(id: i32) -> T;
}

impl IdentifiableEnum<ImportsNotUsedAsValues> for ImportsNotUsedAsValues {
  fn get_id(&self) -> i32 {
    match self {
      ImportsNotUsedAsValues::Remove => 0,
      ImportsNotUsedAsValues::Preserve => 1,
      ImportsNotUsedAsValues::Error => 2,
    }
  }
  fn parse_by_id(id: i32) -> ImportsNotUsedAsValues {
    match id {
      0 => ImportsNotUsedAsValues::Remove,
      1 => ImportsNotUsedAsValues::Preserve,
      _ => ImportsNotUsedAsValues::Error,
    }
  }
}

impl IdentifiableEnum<MediaType> for MediaType {
  fn get_id(&self) -> i32 {
    match self {
      MediaType::JavaScript => 0,
      MediaType::Jsx => 1,
      MediaType::Mjs => 2,
      MediaType::Cjs => 3,
      MediaType::TypeScript => 4,
      MediaType::Mts => 5,
      MediaType::Cts => 6,
      MediaType::Dts => 7,
      MediaType::Dmts => 8,
      MediaType::Dcts => 9,
      MediaType::Tsx => 10,
      MediaType::Json => 11,
      MediaType::Wasm => 12,
      MediaType::TsBuildInfo => 13,
      MediaType::SourceMap => 14,
      MediaType::Unknown => 15,
    }
  }
  fn parse_by_id(id: i32) -> MediaType {
    match id {
      0 => MediaType::JavaScript,
      1 => MediaType::Jsx,
      2 => MediaType::Mjs,
      3 => MediaType::Cjs,
      4 => MediaType::TypeScript,
      5 => MediaType::Mts,
      6 => MediaType::Cts,
      7 => MediaType::Dts,
      8 => MediaType::Dmts,
      9 => MediaType::Dcts,
      10 => MediaType::Tsx,
      11 => MediaType::Json,
      12 => MediaType::Wasm,
      13 => MediaType::TsBuildInfo,
      14 => MediaType::SourceMap,
      _ => MediaType::Unknown,
    }
  }
}

#[derive(Debug, Copy, Clone)]
pub enum ParseMode {
  Module,
  Script,
}

impl IdentifiableEnum<ParseMode> for ParseMode {
  fn get_id(&self) -> i32 {
    match self {
      ParseMode::Module => 0,
      ParseMode::Script => 1,
    }
  }
  fn parse_by_id(id: i32) -> ParseMode {
    match id {
      0 => ParseMode::Module,
      _ => ParseMode::Script,
    }
  }
}

pub struct JavaImportsNotUsedAsValues {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
}
unsafe impl Send for JavaImportsNotUsedAsValues {}
unsafe impl Sync for JavaImportsNotUsedAsValues {}

impl JavaImportsNotUsedAsValues {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jImportsNotUsedAsValues")
      .expect("Couldn't find class Swc4jImportsNotUsedAsValues");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jImportsNotUsedAsValues");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jImportsNotUsedAsValues.getId");
    JavaImportsNotUsedAsValues { class, method_get_id }
  }

  pub fn get_imports_not_used_as_values<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'a>,
  ) -> ImportsNotUsedAsValues {
    let id = jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id);
    ImportsNotUsedAsValues::parse_by_id(id)
  }
}

pub struct JavaMediaType {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
  method_parse: JStaticMethodID,
}
unsafe impl Send for JavaMediaType {}
unsafe impl Sync for JavaMediaType {}

impl JavaMediaType {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jMediaType")
      .expect("Couldn't find class Swc4jMediaType");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jMediaType");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jMediaType.getId");
    let method_parse = env
      .get_static_method_id(&class, "parse", "(I)Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;")
      .expect("Couldn't find method Swc4jMediaType.parse");
    JavaMediaType {
      class,
      method_get_id,
      method_parse,
    }
  }

  pub fn get_media_type<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> MediaType {
    let id = jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id);
    MediaType::parse_by_id(id)
  }

  pub fn parse<'local>(&self, env: &mut JNIEnv<'local>, id: i32) -> jvalue {
    let id = jvalue { i: id };
    unsafe {
      env
        .call_static_method_unchecked(&self.class, self.method_parse, ReturnType::Object, &[id])
        .expect("Object is expected")
        .as_jni()
    }
  }
}

pub struct JavaParseMode {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
}
unsafe impl Send for JavaParseMode {}
unsafe impl Sync for JavaParseMode {}

impl JavaParseMode {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jParseMode")
      .expect("Couldn't find class Swc4jParseMode");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jParseMode");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jParseMode.getId");
    JavaParseMode { class, method_get_id }
  }

  pub fn get_parse_mode<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> ParseMode {
    let id = jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id);
    ParseMode::parse_by_id(id)
  }
}

pub static mut JAVA_IMPORTS_NOT_USED_AS_VALUES: Option<JavaImportsNotUsedAsValues> = None;
pub static mut JAVA_MEDIA_TYPE: Option<JavaMediaType> = None;
pub static mut JAVA_PARSE_MODE: Option<JavaParseMode> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_IMPORTS_NOT_USED_AS_VALUES = Some(JavaImportsNotUsedAsValues::new(env));
    JAVA_MEDIA_TYPE = Some(JavaMediaType::new(env));
    JAVA_PARSE_MODE = Some(JavaParseMode::new(env));
  }
}
