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
use jni::sys::{jboolean, jint, jobject, jstring};
use jni::JNIEnv;

mod core;
mod utils;

pub use core::VERSION;

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreGetVersion<'local>(
  env: JNIEnv<'local>,
  _: JClass<'local>,
) -> jstring {
  core::get_version(&env)
}

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreTranspile<'local>(
  mut env: JNIEnv<'local>,
  _: JClass<'local>,
  code: jstring,
  media_type_id: jint,
  file_name: jstring,
) -> jobject {
  core::transpile(&mut env, code, media_type_id, file_name)
}
