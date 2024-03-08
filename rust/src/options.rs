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

use jni::objects::{JMethodID, JObject};
use jni::signature::{Primitive, ReturnType};
use jni::sys::jobject;
use jni::JNIEnv;

use deno_ast::MediaType;

use crate::converter;

struct JniCalls {
  pub jmethod_id_transpile_options_get_file_name: JMethodID,
  pub jmethod_id_transpile_options_get_media_type: JMethodID,
  pub jmethod_id_media_type_get_id: JMethodID,
}
unsafe impl Send for JniCalls {}
unsafe impl Sync for JniCalls {}

static mut JNI_CALLS: Option<JniCalls> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  let jclass_transpile_options = env
    .find_class("com/caoccao/javet/swc4j/options/Swc4jTranspileOptions")
    .expect("Couldn't find class Swc4jTranspileOptions");
  let jmethod_id_transpile_options_get_file_name = env
    .get_method_id(&jclass_transpile_options, "getFileName", "()Ljava/lang/String;")
    .expect("Couldn't find method Swc4jTranspileOptions.getFileName");
  let jmethod_id_transpile_options_get_media_type = env
    .get_method_id(
      &jclass_transpile_options,
      "getMediaType",
      "()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;",
    )
    .expect("Couldn't find method Swc4jTranspileOptions.getMediaType");
  let jclass_media_type = env
    .find_class("com/caoccao/javet/swc4j/enums/Swc4jMediaType")
    .expect("Couldn't find class Swc4jMediaType");
  let jmethod_id_media_type_get_id = env
    .get_method_id(&jclass_media_type, "getId", "()I")
    .expect("Couldn't find method Swc4jMediaType.getId");
  unsafe {
    JNI_CALLS = Some(JniCalls {
      jmethod_id_transpile_options_get_file_name,
      jmethod_id_transpile_options_get_media_type,
      jmethod_id_media_type_get_id,
    });
  }
}

pub trait FromJniType {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, o: jobject) -> Self;
}

#[derive(Debug)]
pub struct TranspileOptions {
  pub file_name: String,
  pub media_type: MediaType,
}

impl FromJniType for TranspileOptions {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, o: jobject) -> TranspileOptions {
    let o = unsafe { JObject::from_raw(o) };
    let file_name = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_get_file_name,
        ReturnType::Object,
        &[],
      )
    };
    let file_name = unsafe { file_name.unwrap().as_jni().l };
    let file_name = converter::jstring_to_string(env, file_name);
    // media_type
    let media_type = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_get_media_type,
        ReturnType::Object,
        &[],
      )
    };
    let media_type = unsafe { JObject::from_raw(media_type.unwrap().as_jni().l) };
    let media_type = unsafe {
      env.call_method_unchecked(
        media_type.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_media_type_get_id,
        ReturnType::Primitive(Primitive::Int),
        &[],
      )
    };
    let media_type = unsafe { media_type.unwrap().as_jni().i };
    let media_type = converter::media_type_id_to_media_type(media_type);
    // construct
    TranspileOptions { file_name, media_type }
  }
}
