/*
* Copyright (c) 2024-2026. caoccao.com Sam Cao
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
use jni::errors::ThrowRuntimeExAndDefault;
use jni::objects::{JClass, JObject, JString};
#[cfg(target_os = "android")]
use jni::sys::JNI_VERSION_1_6;
#[cfg(not(target_os = "android"))]
use jni::sys::JNI_VERSION_1_8;
use jni::sys::{jint, jobject, jstring};
use jni::{Env, EnvUnowned, JavaVM};
use jni_utils::FromJava;

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

#[unsafe(no_mangle)]
#[allow(improper_ctypes_definitions)]
pub extern "system" fn JNI_OnLoad(java_vm: JavaVM, _: *const std::ffi::c_void) -> jint {
  env_logger::init();
  log::debug!("JNI_OnLoad()");
  java_vm
    .attach_current_thread(|env| {
      ast_utils::init(env);
      comment_utils::init(env);
      enums::init(env);
      error::init(env);
      jni_utils::init(env);
      options::init(env);
      outputs::init(env);
      plugin_utils::init(env);
      span_utils::init(env);
      token_utils::init(env);
      #[cfg(target_os = "android")]
      let jni_version = JNI_VERSION_1_6;
      #[cfg(not(target_os = "android"))]
      let jni_version = JNI_VERSION_1_8;
      Ok::<_, jni::errors::Error>(jni_version)
    })
    .expect("Failed to initialize JNI")
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreGetVersion<'local>(
  mut env: EnvUnowned<'local>,
  _: JClass<'local>,
) -> jstring {
  log::debug!("Java_com_caoccao_javet_swc4j_Swc4jNative_coreGetVersion()");
  env
    .with_env(|env| -> jni::errors::Result<jstring> {
      Ok(string_to_jstring!(env, core::get_version()).as_raw())
    })
    .resolve::<ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreParse<'local>(
  mut env: EnvUnowned<'local>,
  _: JClass<'local>,
  code: jstring,
  options: jobject,
) -> jobject {
  log::debug!("Java_com_caoccao_javet_swc4j_Swc4jNative_coreParse()");
  env
    .with_env(|env| -> jni::errors::Result<jobject> {
      Ok(match core_parse(env, code, options) {
        Ok(output) => output,
        Err(err) => error::throw_parse_error(env, err.to_string().as_str()),
      })
    })
    .resolve::<ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreTransform<'local>(
  mut env: EnvUnowned<'local>,
  _: JClass<'local>,
  code: jstring,
  options: jobject,
) -> jobject {
  log::debug!("Java_com_caoccao_javet_swc4j_Swc4jNative_coreTransform()");
  env
    .with_env(|env| -> jni::errors::Result<jobject> {
      Ok(match core_transform(env, code, options) {
        Ok(output) => output,
        Err(err) => error::throw_transform_error(env, err.to_string().as_str()),
      })
    })
    .resolve::<ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_caoccao_javet_swc4j_Swc4jNative_coreTranspile<'local>(
  mut env: EnvUnowned<'local>,
  _: JClass<'local>,
  code: jstring,
  options: jobject,
) -> jobject {
  log::debug!("Java_com_caoccao_javet_swc4j_Swc4jNative_coreTranspile()");
  env
    .with_env(|env| -> jni::errors::Result<jobject> {
      Ok(match core_transpile(env, code, options) {
        Ok(output) => output,
        Err(err) => error::throw_transpile_error(env, err.to_string().as_str()),
      })
    })
    .resolve::<ThrowRuntimeExAndDefault>()
}

fn core_parse<'local>(env: &mut Env<'local>, code: jstring, options: jobject) -> Result<jobject> {
  let code: Result<String> = jstring_to_string!(env, code);
  let code = code?;
  let options = unsafe { JObject::from_raw(env, options) };
  let mut options = *options::ParseOptions::from_java(env, &options)?;
  let mut plugin_host = options.plugin_host.take();
  let output = core::parse(Some(env), code, &options, &mut plugin_host)?;
  let output = output.to_java(env)?;
  Ok(output.as_raw())
}

fn core_transform<'local>(env: &mut Env<'local>, code: jstring, options: jobject) -> Result<jobject> {
  let code: Result<String> = jstring_to_string!(env, code);
  let code = code?;
  let options = unsafe { JObject::from_raw(env, options) };
  let mut options = *options::TransformOptions::from_java(env, &options)?;
  let mut plugin_host = options.plugin_host.take();
  let output = core::transform(Some(env), code, &options, &mut plugin_host)?;
  let output = output.to_java(env)?;
  Ok(output.as_raw())
}

fn core_transpile<'local>(env: &mut Env<'local>, code: jstring, options: jobject) -> Result<jobject> {
  let code: Result<String> = jstring_to_string!(env, code);
  let code = code?;
  let options = unsafe { JObject::from_raw(env, options) };
  let mut options = *options::TranspileOptions::from_java(env, &options)?;
  let mut plugin_host = options.plugin_host.take();
  let output = core::transpile(Some(env), code, &options, &mut plugin_host)?;
  let output = output.to_java(env)?;
  Ok(output.as_raw())
}
