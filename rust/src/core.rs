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

use jni::sys::{jboolean, jint, jobject, jstring};
use jni::JNIEnv;
use std::ptr::null_mut;

use deno_ast::*;

use crate::utils::converter;

pub const VERSION: &'static str = "0.1.0";

pub fn transpile<'local>(env: &mut JNIEnv<'local>, code: jstring, media_type_id: jint, file_name: jstring) -> jobject {
  let url = ModuleSpecifier::parse(&format!("file:///{}", converter::jstring_to_string(env, file_name))).unwrap();
  let media_type = converter::media_type_id_to_media_type(media_type_id);
  let source_code = converter::jstring_to_string(env, code);
  println!("url: {}", url.to_string());
  println!("source: {}", source_code.to_string());
  println!("media_type: {}", media_type.to_string());
  let parsed_source = parse_module(ParseParams {
    specifier: url.to_string(),
    text_info: SourceTextInfo::from_string(source_code.to_string()),
    media_type: media_type,
    capture_tokens: false,
    maybe_syntax: None,
    scope_analysis: false,
  })
  .unwrap();
  println!("module: {}", parsed_source.is_module());
  println!("script: {}", parsed_source.is_script());
  let transpiled_js_code = parsed_source.transpile(&EmitOptions::default()).unwrap();
  println!("{}", transpiled_js_code.text);
  null_mut()
}

pub fn get_version<'local>(env: &JNIEnv<'local>) -> jstring {
  converter::string_to_jstring(&env, VERSION)
}
