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

use anyhow::Result;
use jni::objects::{JClass, JObject, JString};
use jni::sys::{jint, jobject, jstring, JNI_VERSION_1_8};
use jni::{JNIEnv, JavaVM};
use jni_utils::FromJava;

use std::ffi::c_void;

pub mod ast_utils;
pub mod comment_utils;
pub mod core;
pub mod enums;
pub mod error;
pub mod jni_utils;
pub mod options;
pub mod outputs;
pub mod plugin_utils;
pub mod span_utils;
pub mod token_utils;

use crate::jni_utils::{jstring_to_optional_string, jstring_to_string, string_to_jstring, ToJava};

#[no_mangle]
pub extern "system" fn JNI_OnLoad<'local>(java_vm: JavaVM, _: c_void) -> jint {
  env_logger::init();
  log::debug!("JNI_OnLoad()");
  let mut env = java_vm.get_env().expect("Cannot get JNI env");
  ast_utils::init(&mut env);
  comment_utils::init(&mut env);
  enums::init(&mut env);
  error::init(&mut env);
  jni_utils::init(&mut env);
  options::init(&mut env);
  outputs::init(&mut env);
  plugin_utils::init(&mut env);
  span_utils::init(&mut env);
  token_utils::init(&mut env);
  JNI_VERSION_1_8
}

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreGetVersion<'local>(
  env: JNIEnv<'local>,
  _: JClass<'local>,
) -> jstring {
  string_to_jstring!(env, core::get_version()).as_raw()
}

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreParse<'local>(
  mut env: JNIEnv<'local>,
  _: JClass<'local>,
  code: jstring,
  options: jobject,
) -> jobject {
  match core_parse(&mut env, code, options) {
    Ok(output) => output,
    Err(err) => error::throw_parse_error(&mut env, err.to_string().as_str()),
  }
}

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreTransform<'local>(
  mut env: JNIEnv<'local>,
  _: JClass<'local>,
  code: jstring,
  options: jobject,
) -> jobject {
  match core_transform(&mut env, code, options) {
    Ok(output) => output,
    Err(err) => error::throw_transform_error(&mut env, err.to_string().as_str()),
  }
}

#[no_mangle]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreTranspile<'local>(
  mut env: JNIEnv<'local>,
  _: JClass<'local>,
  code: jstring,
  options: jobject,
) -> jobject {
  match core_transpile(&mut env, code, options) {
    Ok(output) => output,
    Err(err) => error::throw_transpile_error(&mut env, err.to_string().as_str()),
  }
}

fn core_parse<'local>(env: &mut JNIEnv<'local>, code: jstring, options: jobject) -> Result<jobject> {
  let code: Result<String> = jstring_to_string!(env, code);
  let code = code?;
  let options = unsafe { JObject::from_raw(options) };
  let options = options::ParseOptions::from_java(env, &options)?;
  let output = core::parse(code, *options)?;
  let output = output.to_java(env)?;
  Ok(output.as_raw())
}

fn core_transform<'local>(env: &mut JNIEnv<'local>, code: jstring, options: jobject) -> Result<jobject> {
  let code: Result<String> = jstring_to_string!(env, code);
  let code = code?;
  let options = unsafe { JObject::from_raw(options) };
  let options = options::TransformOptions::from_java(env, &options)?;
  let output = core::transform(code, *options)?;
  let output = output.to_java(env)?;
  Ok(output.as_raw())
}

fn core_transpile<'local>(env: &mut JNIEnv<'local>, code: jstring, options: jobject) -> Result<jobject> {
  let code: Result<String> = jstring_to_string!(env, code);
  let code = code?;
  let options = unsafe { JObject::from_raw(options) };
  let options = options::TranspileOptions::from_java(env, &options)?;
  let output = core::transpile(code, *options)?;
  let output = output.to_java(env)?;
  Ok(output.as_raw())
}
