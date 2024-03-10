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

use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::sys::jvalue;
use jni::JNIEnv;

use std::ptr::null_mut;

use crate::converter;

struct JavaTranspileOutput {
  class: GlobalRef,
  method_constructor: JMethodID,
}
unsafe impl Send for JavaTranspileOutput {}
unsafe impl Sync for JavaTranspileOutput {}

impl JavaTranspileOutput {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/outputs/Swc4jTranspileOutput")
      .expect("Couldn't find class Swc4jTranspileOutput");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTranspileOutput");
    let method_constructor = env
      .get_method_id(&class, "<init>", "(Ljava/lang/String;Ljava/lang/String;Z)V")
      .expect("Couldn't find method Swc4jTranspileOutput.Swc4jTranspileOutput");
    JavaTranspileOutput {
      class,
      method_constructor,
    }
  }

  pub fn create<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    code: jvalue,
    module: jvalue,
    source_map: jvalue,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    unsafe {
      env
        .new_object_unchecked(&self.class, self.method_constructor, &[code, source_map, module])
        .expect("Couldn't create Swc4jTranspileOutput")
    }
  }
}

static mut JAVA_TRANSPILE_OUTPUT: Option<JavaTranspileOutput> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_TRANSPILE_OUTPUT = Some(JavaTranspileOutput::new(env));
  }
}

pub trait ToJniType {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a;
}

#[derive(Debug)]
pub struct TranspileOutput {
  pub code: String,
  pub module: bool,
  pub source_map: Option<String>,
}

impl ToJniType for TranspileOutput {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a,
  {
    let code = jvalue {
      l: converter::string_to_jstring(env, &self.code).as_raw(),
    };
    let module = jvalue {
      z: if self.module { 1u8 } else { 0u8 },
    };
    let source_map = jvalue {
      l: match &self.source_map {
        Some(s) => converter::string_to_jstring(env, &s).as_raw(),
        None => null_mut(),
      },
    };
    unsafe { JAVA_TRANSPILE_OUTPUT.as_ref().unwrap() }.create(env, code, module, source_map)
  }
}