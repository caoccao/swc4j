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

use jni::objects::{GlobalRef, JStaticMethodID, JThrowable};
use jni::signature::ReturnType;
use jni::sys::{jvalue};
use jni::JNIEnv;

use crate::converter;

struct JniCalls {
  pub jclass_core_exception: GlobalRef,
  pub jmethod_id_core_exception_transpile_error: JStaticMethodID,
}
unsafe impl Send for JniCalls {}
unsafe impl Sync for JniCalls {}

static mut JNI_CALLS: Option<JniCalls> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  let jclass_core_exception = env
    .find_class("com/caoccao/javet/swc4j/exceptions/Swc4jCoreException")
    .expect("Couldn't find class Swc4jCoreException");
  let jclass_core_exception = env
    .new_global_ref(jclass_core_exception)
    .expect("Couldn't globalize class Swc4jCoreException");
  let jmethod_id_core_exception_transpile_error = env
    .get_static_method_id(
      &jclass_core_exception,
      "transpileError",
      "(Ljava/lang/String;)Lcom/caoccao/javet/swc4j/exceptions/Swc4jCoreException;",
    )
    .expect("Couldn't find static method Swc4jCoreException.transpileError");
  unsafe {
    JNI_CALLS = Some(JniCalls {
      jclass_core_exception,
      jmethod_id_core_exception_transpile_error,
    });
  }
}

pub fn throw_transpile_error<'local, 'a>(env: &mut JNIEnv<'local>, message: &'a str) {
  let message = jvalue {
    l: converter::string_to_jstring(env, message).as_raw(),
  };
  let exception = unsafe {
    JThrowable::from_raw(
      env
        .call_static_method_unchecked(
          &JNI_CALLS.as_ref().unwrap().jclass_core_exception,
          &JNI_CALLS.as_ref().unwrap().jmethod_id_core_exception_transpile_error,
          ReturnType::Object,
          &[message],
        )
        .expect("Couldn't create transpile error")
        .as_jni()
        .l,
    )
  };
  let _ = env.throw(exception);
}
