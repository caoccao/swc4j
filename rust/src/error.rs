/*
* Copyright (c) 2024-2025. caoccao.com Sam Cao
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

use std::ptr::null_mut;
use std::sync::OnceLock;

use jni::objects::{GlobalRef, JStaticMethodID, JThrowable};
use jni::sys::{jobject, jvalue};
use jni::JNIEnv;

use crate::jni_utils::*;

struct JavaCoreException {
  pub class: GlobalRef,
  pub method_parse_error: JStaticMethodID,
  pub method_transform_error: JStaticMethodID,
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
        "(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/caoccao/javet/swc4j/exceptions/Swc4jCoreException;",
      )
      .expect("Couldn't find static method Swc4jCoreException.parseError");
    let method_transform_error = env
      .get_static_method_id(
        &class,
        "transformError",
        "(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/caoccao/javet/swc4j/exceptions/Swc4jCoreException;",
      )
      .expect("Couldn't find static method Swc4jCoreException.transformError");
    let method_transpile_error = env
      .get_static_method_id(
        &class,
        "transpileError",
        "(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/caoccao/javet/swc4j/exceptions/Swc4jCoreException;",
      )
      .expect("Couldn't find static method Swc4jCoreException.transpileError");
    JavaCoreException {
      class,
      method_parse_error,
      method_transform_error,
      method_transpile_error,
    }
  }

  pub fn throw_parse_error<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    message: &'a str,
    cause: Option<JThrowable<'a>>,
  ) {
    let java_message = string_to_jstring!(env, message);
    let message = object_to_jvalue!(java_message);
    let cause = if let Some(cause) = cause {
      object_to_jvalue!(cause)
    } else {
      jvalue { l: null_mut() }
    };
    let exception = call_static_as_object!(
      env,
      &self.class,
      &self.method_parse_error,
      &[message, cause],
      "parseError()"
    )
    .expect("Couldn't call static method Swc4jCoreException.parseError()");
    let exception = unsafe { JThrowable::from_raw(exception.as_raw()) };
    env.throw(exception).expect("Couldn't call throw parse error");
  }

  pub fn throw_transform_error<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    message: &'a str,
    cause: Option<JThrowable<'a>>,
  ) {
    let java_message = string_to_jstring!(env, message);
    let message = object_to_jvalue!(java_message);
    let cause = if let Some(cause) = cause {
      object_to_jvalue!(cause)
    } else {
      jvalue { l: null_mut() }
    };
    let exception = call_static_as_object!(
      env,
      &self.class,
      &self.method_transform_error,
      &[message, cause],
      "transformError()"
    )
    .expect("Couldn't call static method Swc4jCoreException.transformError()");
    let exception = unsafe { JThrowable::from_raw(exception.as_raw()) };
    env.throw(exception).expect("Couldn't call throw transform error");
  }

  pub fn throw_transpile_error<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    message: &'a str,
    cause: Option<JThrowable<'a>>,
  ) {
    let java_message = string_to_jstring!(env, message);
    let message = object_to_jvalue!(java_message);
    let cause = if let Some(cause) = cause {
      object_to_jvalue!(cause)
    } else {
      jvalue { l: null_mut() }
    };
    let exception = call_static_as_object!(
      env,
      &self.class,
      &self.method_transpile_error,
      &[message, cause],
      "transpileError()"
    )
    .expect("Couldn't call static method Swc4jCoreException.transpileError()");
    let exception = unsafe { JThrowable::from_raw(exception.as_raw()) };
    env.throw(exception).expect("Couldn't call throw transpile error");
  }
}

static JAVA_CORE_EXCEPTION: OnceLock<JavaCoreException> = OnceLock::new();

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  log::debug!("init()");
  unsafe {
    JAVA_CORE_EXCEPTION.set(JavaCoreException::new(env)).unwrap_unchecked();
  }
}

pub fn throw_parse_error<'local, 'a>(env: &mut JNIEnv<'local>, message: &'a str) -> jobject {
  let java_core_exception = JAVA_CORE_EXCEPTION.get().unwrap();
  let cause = if env.exception_check().unwrap_or(false) {
    log::error!("Exception occurred in parse()");
    let cause = env
      .exception_occurred()
      .expect("Couldn't get exception occurred in parse()");
    env
      .exception_clear()
      .expect("Could'n clear exception occurred in parse()");
    Some(cause)
  } else {
    None
  };
  java_core_exception.throw_parse_error(env, message, cause);
  null_mut()
}

pub fn throw_transform_error<'local, 'a>(env: &mut JNIEnv<'local>, message: &'a str) -> jobject {
  let java_core_exception = JAVA_CORE_EXCEPTION.get().unwrap();
  let cause = if env.exception_check().unwrap_or(false) {
    log::error!("Exception occurred in transform()");
    let cause = env
      .exception_occurred()
      .expect("Couldn't get exception occurred in transform()");
    env
      .exception_clear()
      .expect("Could'n clear exception occurred in transform()");
    Some(cause)
  } else {
    None
  };
  java_core_exception.throw_transform_error(env, message, cause);
  null_mut()
}

pub fn throw_transpile_error<'local, 'a>(env: &mut JNIEnv<'local>, message: &'a str) -> jobject {
  let java_core_exception = JAVA_CORE_EXCEPTION.get().unwrap();
  let cause = if env.exception_check().unwrap_or(false) {
    log::error!("Exception occurred in transpile()");
    let cause = env
      .exception_occurred()
      .expect("Couldn't get exception occurred in transpile()");
    env
      .exception_clear()
      .expect("Could'n clear exception occurred in transpile()");
    Some(cause)
  } else {
    None
  };
  java_core_exception.throw_transpile_error(env, message, cause);
  null_mut()
}
