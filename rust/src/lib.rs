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
use jni::sys::jstring;
use jni::JNIEnv;

pub const VERSION: &'static str = "0.1.0";

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_SwcNative_getVersion<'local>(
  jni_env: JNIEnv<'local>,
  _: JClass<'local>,
) -> jstring {
  let output = jni_env.new_string(VERSION).expect("Couldn't get version!");
  output.into_raw()
}
