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

use jni::objects::{JMethodID, JObject};
use jni::signature::{Primitive, ReturnType};
use jni::sys::jobject;
use jni::JNIEnv;

use deno_ast::MediaType;

use crate::converter;

struct JniCalls {
  pub jmethod_id_media_type_get_id: JMethodID,
  pub jmethod_id_transpile_options_get_media_type: JMethodID,
  pub jmethod_id_transpile_options_get_specifier: JMethodID,
  pub jmethod_id_transpile_options_get_jsx_factory: JMethodID,
  pub jmethod_id_transpile_options_get_jsx_fragment_factory: JMethodID,
  pub jmethod_id_transpile_options_get_jsx_import_source: JMethodID,
  pub jmethod_id_transpile_options_is_inline_source_map: JMethodID,
  pub jmethod_id_transpile_options_is_inline_sources: JMethodID,
  pub jmethod_id_transpile_options_is_jsx_automatic: JMethodID,
  pub jmethod_id_transpile_options_is_jsx_development: JMethodID,
  pub jmethod_id_transpile_options_is_precompile_jsx: JMethodID,
  pub jmethod_id_transpile_options_is_source_map: JMethodID,
  pub jmethod_id_transpile_options_is_transform_jsx: JMethodID,
}
unsafe impl Send for JniCalls {}
unsafe impl Sync for JniCalls {}

static mut JNI_CALLS: Option<JniCalls> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  let jclass_media_type = env
    .find_class("com/caoccao/javet/swc4j/enums/Swc4jMediaType")
    .expect("Couldn't find class Swc4jMediaType");
  let jmethod_id_media_type_get_id = env
    .get_method_id(&jclass_media_type, "getId", "()I")
    .expect("Couldn't find method Swc4jMediaType.getId");
  let jclass_transpile_options = env
    .find_class("com/caoccao/javet/swc4j/options/Swc4jTranspileOptions")
    .expect("Couldn't find class Swc4jTranspileOptions");
  let jmethod_id_transpile_options_get_media_type = env
    .get_method_id(
      &jclass_transpile_options,
      "getMediaType",
      "()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;",
    )
    .expect("Couldn't find method Swc4jTranspileOptions.getMediaType");
  let jmethod_id_transpile_options_get_specifier = env
    .get_method_id(&jclass_transpile_options, "getSpecifier", "()Ljava/lang/String;")
    .expect("Couldn't find method Swc4jTranspileOptions.getSpecifier");
  let jmethod_id_transpile_options_get_jsx_factory = env
    .get_method_id(&jclass_transpile_options, "getJsxFactory", "()Ljava/lang/String;")
    .expect("Couldn't find method Swc4jTranspileOptions.getJsxFactory");
  let jmethod_id_transpile_options_get_jsx_fragment_factory = env
    .get_method_id(&jclass_transpile_options, "getJsxFragmentFactory", "()Ljava/lang/String;")
    .expect("Couldn't find method Swc4jTranspileOptions.getJsxFragmentFactory");
  let jmethod_id_transpile_options_get_jsx_import_source = env
    .get_method_id(&jclass_transpile_options, "getJsxImportSource", "()Ljava/lang/String;")
    .expect("Couldn't find method Swc4jTranspileOptions.getJsxImportSource");
  let jmethod_id_transpile_options_is_inline_source_map = env
    .get_method_id(&jclass_transpile_options, "isInlineSourceMap", "()Z")
    .expect("Couldn't find method Swc4jTranspileOptions.isInlineSourceMap");
  let jmethod_id_transpile_options_is_inline_sources = env
    .get_method_id(&jclass_transpile_options, "isInlineSources", "()Z")
    .expect("Couldn't find method Swc4jTranspileOptions.isInlineSources");
  let jmethod_id_transpile_options_is_jsx_automatic = env
    .get_method_id(&jclass_transpile_options, "isJsxAutomatic", "()Z")
    .expect("Couldn't find method Swc4jTranspileOptions.isJsxAutomatic");
  let jmethod_id_transpile_options_is_jsx_development = env
    .get_method_id(&jclass_transpile_options, "isJsxDevelopment", "()Z")
    .expect("Couldn't find method Swc4jTranspileOptions.isJsxDevelopment");
  let jmethod_id_transpile_options_is_precompile_jsx = env
    .get_method_id(&jclass_transpile_options, "isPrecompileJsx", "()Z")
    .expect("Couldn't find method Swc4jTranspileOptions.isPrecompileJsx");
  let jmethod_id_transpile_options_is_source_map = env
    .get_method_id(&jclass_transpile_options, "isSourceMap", "()Z")
    .expect("Couldn't find method Swc4jTranspileOptions.isSourceMap");
  let jmethod_id_transpile_options_is_transform_jsx = env
    .get_method_id(&jclass_transpile_options, "isTransformJsx", "()Z")
    .expect("Couldn't find method Swc4jTranspileOptions.isTransformJsx");
  unsafe {
    JNI_CALLS = Some(JniCalls {
      jmethod_id_media_type_get_id,
      jmethod_id_transpile_options_get_media_type,
      jmethod_id_transpile_options_get_specifier,
      jmethod_id_transpile_options_get_jsx_factory,
      jmethod_id_transpile_options_get_jsx_fragment_factory,
      jmethod_id_transpile_options_get_jsx_import_source,
      jmethod_id_transpile_options_is_inline_source_map,
      jmethod_id_transpile_options_is_inline_sources,
      jmethod_id_transpile_options_is_jsx_automatic,
      jmethod_id_transpile_options_is_jsx_development,
      jmethod_id_transpile_options_is_precompile_jsx,
      jmethod_id_transpile_options_is_source_map,
      jmethod_id_transpile_options_is_transform_jsx,
    });
  }
}

pub trait FromJniType {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, o: jobject) -> Self;
}

#[derive(Debug)]
pub struct TranspileOptions {
  /// Should the source map be inlined in the emitted code file, or provided
  /// as a separate file.  Defaults to `true`.
  pub inline_source_map: bool,
  /// Should the sources be inlined in the source map.  Defaults to `true`.
  pub inline_sources: bool,
  /// `true` if the program should use an implicit JSX import source/the "new"
  /// JSX transforms.
  pub jsx_automatic: bool,
  /// If JSX is automatic, if it is in development mode, meaning that it should
  /// import `jsx-dev-runtime` and transform JSX using `jsxDEV` import from the
  /// JSX import source as well as provide additional debug information to the
  /// JSX factory.
  pub jsx_development: bool,
  /// When transforming JSX, what value should be used for the JSX factory.
  /// Defaults to `React.createElement`.
  pub jsx_factory: String,
  /// When transforming JSX, what value should be used for the JSX fragment
  /// factory.  Defaults to `React.Fragment`.
  pub jsx_fragment_factory: String,
  /// The string module specifier to implicitly import JSX factories from when
  /// transpiling JSX.
  pub jsx_import_source: Option<String>,
  /// Media type of the source text.
  pub media_type: MediaType,
  /// Should a corresponding .map file be created for the output. This should be
  /// false if inline_source_map is true. Defaults to `false`.
  pub source_map: bool,
  /// Specifier of the source text.
  pub specifier: String,
  /// Should JSX be transformed. Defaults to `true`.
  pub transform_jsx: bool,
  /// Should JSX be precompiled into static strings that need to be concatenated
  /// with dynamic content. Defaults to `false`, mutually exclusive with
  /// `transform_jsx`.
  pub precompile_jsx: bool,
}

impl FromJniType for TranspileOptions {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, o: jobject) -> TranspileOptions {
    let o = unsafe { JObject::from_raw(o) };
    // inline_source_map
    let inline_source_map = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS
          .as_ref()
          .unwrap()
          .jmethod_id_transpile_options_is_inline_source_map,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
      )
    };
    let inline_source_map = unsafe { inline_source_map.unwrap().as_jni().z };
    let inline_source_map = converter::jboolean_to_bool(inline_source_map);
    // inline_sources
    let inline_sources = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS
          .as_ref()
          .unwrap()
          .jmethod_id_transpile_options_is_inline_sources,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
      )
    };
    let inline_sources = unsafe { inline_sources.unwrap().as_jni().z };
    let inline_sources = converter::jboolean_to_bool(inline_sources);
    // jsx_automatic
    let jsx_automatic = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS
          .as_ref()
          .unwrap()
          .jmethod_id_transpile_options_is_jsx_automatic,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
      )
    };
    let jsx_automatic = unsafe { jsx_automatic.unwrap().as_jni().z };
    let jsx_automatic = converter::jboolean_to_bool(jsx_automatic);
    // jsx_development
    let jsx_development = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS
          .as_ref()
          .unwrap()
          .jmethod_id_transpile_options_is_jsx_development,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
      )
    };
    let jsx_development = unsafe { jsx_development.unwrap().as_jni().z };
    let jsx_development = converter::jboolean_to_bool(jsx_development);
    // jsx_factory
    let jsx_factory = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_get_jsx_factory,
        ReturnType::Object,
        &[],
      )
    };
    let jsx_factory = unsafe { jsx_factory.unwrap().as_jni().l };
    let jsx_factory = converter::jstring_to_string(env, jsx_factory);
    // jsx_fragment_factory
    let jsx_fragment_factory = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_get_jsx_fragment_factory,
        ReturnType::Object,
        &[],
      )
    };
    let jsx_fragment_factory = unsafe { jsx_fragment_factory.unwrap().as_jni().l };
    let jsx_fragment_factory = converter::jstring_to_string(env, jsx_fragment_factory);
    // jsx_import_source
    let jsx_import_source = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_get_jsx_import_source,
        ReturnType::Object,
        &[],
      )
    };
    let jsx_import_source = unsafe { jsx_import_source.unwrap().as_jni().l };
    let jsx_import_source = converter::jstring_to_option_string(env, jsx_import_source);
    // media_type
    let media_type = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_get_media_type,
        ReturnType::Object,
        &[],
      )
    };
    let media_type = unsafe { JObject::from_raw(media_type.unwrap().as_jni().l) };
    let media_type = unsafe {
      env.call_method_unchecked(
        media_type.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_media_type_get_id,
        ReturnType::Primitive(Primitive::Int),
        &[],
      )
    };
    let media_type = unsafe { media_type.unwrap().as_jni().i };
    let media_type = converter::media_type_id_to_media_type(media_type);
    // source_map
    let source_map = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_is_source_map,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
      )
    };
    let source_map = unsafe { source_map.unwrap().as_jni().z };
    let source_map = converter::jboolean_to_bool(source_map);
    // specifier
    let specifier = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS.as_ref().unwrap().jmethod_id_transpile_options_get_specifier,
        ReturnType::Object,
        &[],
      )
    };
    let specifier = unsafe { specifier.unwrap().as_jni().l };
    let specifier = converter::jstring_to_string(env, specifier);
    // transform_jsx
    let transform_jsx = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS
          .as_ref()
          .unwrap()
          .jmethod_id_transpile_options_is_transform_jsx,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
      )
    };
    let transform_jsx = unsafe { transform_jsx.unwrap().as_jni().z };
    let transform_jsx = converter::jboolean_to_bool(transform_jsx);
    // precompile_jsx
    let precompile_jsx = unsafe {
      env.call_method_unchecked(
        o.as_ref(),
        JNI_CALLS
          .as_ref()
          .unwrap()
          .jmethod_id_transpile_options_is_precompile_jsx,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
      )
    };
    let precompile_jsx = unsafe { precompile_jsx.unwrap().as_jni().z };
    let precompile_jsx = converter::jboolean_to_bool(precompile_jsx);
    // construct
    TranspileOptions {
      inline_source_map,
      inline_sources,
      jsx_automatic,
      jsx_development,
      jsx_factory,
      jsx_fragment_factory,
      jsx_import_source,
      media_type,
      source_map,
      specifier,
      transform_jsx,
      precompile_jsx,
    }
  }
}
