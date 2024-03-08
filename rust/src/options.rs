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

use jni::objects::JObject;
use jni::sys::jobject;
use jni::JNIEnv;

use deno_ast::MediaType;

use crate::utils;

const METHOD_TRANSPILE_OPTIONS_GET_FILE_NAME: &'static str = "getFileName";
const SIG_TRANSPILE_OPTIONS_GET_FILE_NAME: &'static str = "()Ljava/lang/String;";

const METHOD_TRANSPILE_OPTIONS_GET_MEDIA_TYPE: &'static str = "getMediaType";
const SIG_TRANSPILE_OPTIONS_GET_MEDIA_TYPE: &'static str = "()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;";

const METHOD_MEDIA_TYPE_GET_ID: &'static str = "getId";
const SIG_MEDIA_TYPE_GET_ID: &'static str = "()I";

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
    // file_name
    let file_name = env.call_method(
      o.as_ref(),
      METHOD_TRANSPILE_OPTIONS_GET_FILE_NAME,
      SIG_TRANSPILE_OPTIONS_GET_FILE_NAME,
      &[],
    );
    let file_name = unsafe { file_name.unwrap().as_jni().l };
    let file_name = utils::converter::jstring_to_string(env, file_name);
    // media_type
    let media_type = env.call_method(
      o.as_ref(),
      METHOD_TRANSPILE_OPTIONS_GET_MEDIA_TYPE,
      SIG_TRANSPILE_OPTIONS_GET_MEDIA_TYPE,
      &[],
    );
    let media_type = unsafe { JObject::from_raw(media_type.unwrap().as_jni().l) };
    let media_type = env.call_method(
      media_type.as_ref(),
      METHOD_MEDIA_TYPE_GET_ID,
      SIG_MEDIA_TYPE_GET_ID,
      &[],
    );
    let media_type = unsafe { media_type.unwrap().as_jni().i };
    let media_type = utils::converter::media_type_id_to_media_type(media_type);
    // construct
    TranspileOptions { file_name, media_type }
  }
}
