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

struct JavaParseOutput {
  class: GlobalRef,
  method_constructor: JMethodID,
}
unsafe impl Send for JavaParseOutput {}
unsafe impl Sync for JavaParseOutput {}

impl JavaParseOutput {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/outputs/Swc4jParseOutput")
      .expect("Couldn't find class Swc4jParseOutput");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jParseOutput");
    let method_constructor = env
      .get_method_id(&class, "<init>", "(ZZ)V")
      .expect("Couldn't find method Swc4jParseOutput.Swc4jParseOutput");
    JavaParseOutput {
      class,
      method_constructor,
    }
  }

  pub fn create<'local, 'a>(&self, env: &mut JNIEnv<'local>, module: jvalue, script: jvalue) -> JObject<'a>
  where
    'local: 'a,
  {
    unsafe {
      env
        .new_object_unchecked(&self.class, self.method_constructor, &[module, script])
        .expect("Couldn't create Swc4jParseOutput")
    }
  }
}

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
      .get_method_id(&class, "<init>", "(Ljava/lang/String;ZZLjava/lang/String;)V")
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
    script: jvalue,
    source_map: jvalue,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    unsafe {
      env
        .new_object_unchecked(
          &self.class,
          self.method_constructor,
          &[code, module, script, source_map],
        )
        .expect("Couldn't create Swc4jTranspileOutput")
    }
  }
}

static mut JAVA_PARSE_OUTPUT: Option<JavaParseOutput> = None;
static mut JAVA_TRANSPILE_OUTPUT: Option<JavaTranspileOutput> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_PARSE_OUTPUT = Some(JavaParseOutput::new(env));
    JAVA_TRANSPILE_OUTPUT = Some(JavaTranspileOutput::new(env));
  }
}

pub trait ToJniType {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a;
}

#[derive(Debug)]
pub struct ParseOutput {
  pub module: bool,
  pub script: bool,
}

impl ToJniType for ParseOutput {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a,
  {
    let module = jvalue {
      z: if self.module { 1u8 } else { 0u8 },
    };
    let script = jvalue {
      z: if self.script { 1u8 } else { 0u8 },
    };
    unsafe { JAVA_PARSE_OUTPUT.as_ref().unwrap() }.create(env, module, script)
  }
}

#[derive(Debug)]
pub struct TranspileOutput {
  pub code: String,
  pub module: bool,
  pub script: bool,
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
    let script = jvalue {
      z: if self.script { 1u8 } else { 0u8 },
    };
    let source_map = jvalue {
      l: match &self.source_map {
        Some(s) => converter::string_to_jstring(env, &s).as_raw(),
        None => null_mut(),
      },
    };
    unsafe { JAVA_TRANSPILE_OUTPUT.as_ref().unwrap() }.create(env, code, module, script, source_map)
  }
}
