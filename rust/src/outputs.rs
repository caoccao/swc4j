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

use jni::objects::{GlobalRef, JMethodID};
use jni::sys::{jobject, jvalue};
use jni::JNIEnv;

use std::ptr::null_mut;

use crate::converter;

struct JniCalls {
  pub jclass_transpile_output: GlobalRef,
  pub jmethod_id_transpile_output_constructor: JMethodID,
}
unsafe impl Send for JniCalls {}
unsafe impl Sync for JniCalls {}

static mut JNI_CALLS: Option<JniCalls> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  let jclass_transpile_output = env
    .find_class("com/caoccao/javet/swc4j/outputs/Swc4jTranspileOutput")
    .expect("Couldn't find class Swc4jTranspileOutput");
  let jclass_transpile_output = env
    .new_global_ref(jclass_transpile_output)
    .expect("Couldn't globalize class Swc4jTranspileOutput");
  let jmethod_id_transpile_output_constructor = env
    .get_method_id(&jclass_transpile_output, "<init>", "(Ljava/lang/String;Ljava/lang/String;Z)V")
    .expect("Couldn't find method Swc4jTranspileOutput.Swc4jTranspileOutput");
  unsafe {
    JNI_CALLS = Some(JniCalls {
      jclass_transpile_output,
      jmethod_id_transpile_output_constructor,
    });
  }
}

pub trait ToJniType {
  fn to_jni_type<'local>(&self, env: &mut JNIEnv<'local>) -> jobject;
}

#[derive(Debug)]
pub struct TranspileOutput {
  pub code: String,
  pub module: bool,
  pub source_map: Option<String>,
}

impl ToJniType for TranspileOutput {
  fn to_jni_type<'local>(&self, env: &mut JNIEnv<'local>) -> jobject {
    let code = jvalue {
      l: converter::string_to_jstring(env, &self.code),
    };
    let module = jvalue {
      z: if self.module { 1u8 } else { 0u8 },
    };
    let source_map = jvalue {
      l: match &self.source_map {
        Some(s) => converter::string_to_jstring(env, &s),
        None => null_mut(),
      },
    };
    unsafe {
      env
        .new_object_unchecked(
          &JNI_CALLS.as_ref().unwrap().jclass_transpile_output,
          JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_output_constructor,
          &[code, source_map, module],
        )
        .expect("Couldn't create Swc4jTranspileOutput")
        .as_raw()
    }
  }
}
