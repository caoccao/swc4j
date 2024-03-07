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

use jni::objects::JClass;
use jni::sys::{jint, jobject, jstring};
use jni::JNIEnv;

use std::ptr::null_mut;

mod core;
mod utils;

pub use core::VERSION;

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreGetVersion<'local>(
  env: JNIEnv<'local>,
  _: JClass<'local>,
) -> jstring {
  utils::converter::string_to_jstring(&env, core::get_version().as_str())
}

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreTranspile<'local>(
  mut env: JNIEnv<'local>,
  _: JClass<'local>,
  code: jstring,
  media_type_id: jint,
  file_name: jstring,
) -> jobject {
  let code = utils::converter::jstring_to_string(&mut env, code);
  let file_name = utils::converter::jstring_to_string(&mut env, file_name);
  let media_type = utils::converter::media_type_id_to_media_type(media_type_id);
  core::transpile(code, media_type, file_name);
  null_mut()
}
