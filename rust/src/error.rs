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
use jni::sys::jvalue;
use jni::JNIEnv;

use crate::converter;
use crate::jni_utils::call_static_as_object;

struct JavaCoreException {
  pub class: GlobalRef,
  pub method_parse_error: JStaticMethodID,
  pub method_transpile_error: JStaticMethodID,
}
unsafe impl Send for JavaCoreException {}
unsafe impl Sync for JavaCoreException {}

impl JavaCoreException {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/exceptions/Swc4jCoreException")
      .expect("Couldn't find class Swc4jCoreException");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jCoreException");
    let method_parse_error = env
      .get_static_method_id(
        &class,
        "parseError",
        "(Ljava/lang/String;)Lcom/caoccao/javet/swc4j/exceptions/Swc4jCoreException;",
      )
      .expect("Couldn't find static method Swc4jCoreException.parseError");
    let method_transpile_error = env
      .get_static_method_id(
        &class,
        "transpileError",
        "(Ljava/lang/String;)Lcom/caoccao/javet/swc4j/exceptions/Swc4jCoreException;",
      )
      .expect("Couldn't find static method Swc4jCoreException.transpileError");
    JavaCoreException {
      class,
      method_parse_error,
      method_transpile_error,
    }
  }

  pub fn throw_parse_error<'local, 'a>(&self, env: &mut JNIEnv<'local>, message: &'a str) {
    let message = jvalue {
      l: converter::string_to_jstring(env, message).as_raw(),
    };
    let exception = call_static_as_object(env, &self.class, &self.method_parse_error, &[message], "parseError()");
    let exception = unsafe { JThrowable::from_raw(exception.as_raw()) };
    let _ = env.throw(exception);
  }

  pub fn throw_transpile_error<'local, 'a>(&self, env: &mut JNIEnv<'local>, message: &'a str) {
    let message = jvalue {
      l: converter::string_to_jstring(env, message).as_raw(),
    };
    let exception = call_static_as_object(
      env,
      &self.class,
      &self.method_transpile_error,
      &[message],
      "transpileError()",
    );
    let exception = unsafe { JThrowable::from_raw(exception.as_raw()) };
    let _ = env.throw(exception);
  }
}

static mut JAVA_CORE_EXCEPTION: Option<JavaCoreException> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_CORE_EXCEPTION = Some(JavaCoreException::new(env));
  }
}

pub fn throw_parse_error<'local, 'a>(env: &mut JNIEnv<'local>, message: &'a str) {
  unsafe {
    JAVA_CORE_EXCEPTION.as_ref().unwrap().throw_parse_error(env, message);
  }
}

pub fn throw_transpile_error<'local, 'a>(env: &mut JNIEnv<'local>, message: &'a str) {
  unsafe {
    JAVA_CORE_EXCEPTION
      .as_ref()
      .unwrap()
      .throw_transpile_error(env, message);
  }
}
