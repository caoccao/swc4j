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

use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::sys::jobject;
use jni::JNIEnv;

use deno_ast::MediaType;

use crate::{converter, jni_utils};

struct JavaMediaType {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
}
unsafe impl Send for JavaMediaType {}
unsafe impl Sync for JavaMediaType {}

impl JavaMediaType {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jMediaType")
      .expect("Couldn't find class Swc4jMediaType");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jMediaType");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jMediaType.getId");
    JavaMediaType { class, method_get_id }
  }

  pub fn get_media_type<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> MediaType {
    converter::media_type_id_to_media_type(jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id))
  }
}

struct JavaTranspileOptions {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_media_type: JMethodID,
  method_get_specifier: JMethodID,
  method_get_jsx_factory: JMethodID,
  method_get_jsx_fragment_factory: JMethodID,
  method_get_jsx_import_source: JMethodID,
  method_is_emit_metadata: JMethodID,
  method_is_inline_source_map: JMethodID,
  method_is_inline_sources: JMethodID,
  method_is_jsx_automatic: JMethodID,
  method_is_jsx_development: JMethodID,
  method_is_precompile_jsx: JMethodID,
  method_is_source_map: JMethodID,
  method_is_transform_jsx: JMethodID,
  method_is_var_decl_imports: JMethodID,
}
unsafe impl Send for JavaTranspileOptions {}
unsafe impl Sync for JavaTranspileOptions {}

impl JavaTranspileOptions {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/options/Swc4jTranspileOptions")
      .expect("Couldn't find class Swc4jTranspileOptions");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTranspileOptions");
    let method_get_media_type = env
      .get_method_id(
        &class,
        "getMediaType",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getMediaType");
    let method_get_specifier = env
      .get_method_id(&class, "getSpecifier", "()Ljava/lang/String;")
      .expect("Couldn't find method Swc4jTranspileOptions.getSpecifier");
    let method_get_jsx_factory = env
      .get_method_id(&class, "getJsxFactory", "()Ljava/lang/String;")
      .expect("Couldn't find method Swc4jTranspileOptions.getJsxFactory");
    let method_get_jsx_fragment_factory = env
      .get_method_id(&class, "getJsxFragmentFactory", "()Ljava/lang/String;")
      .expect("Couldn't find method Swc4jTranspileOptions.getJsxFragmentFactory");
    let method_get_jsx_import_source = env
      .get_method_id(&class, "getJsxImportSource", "()Ljava/lang/String;")
      .expect("Couldn't find method Swc4jTranspileOptions.getJsxImportSource");
    let method_is_emit_metadata = env
      .get_method_id(&class, "isEmitMetadata", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isEmitMetadata");
    let method_is_inline_source_map = env
      .get_method_id(&class, "isInlineSourceMap", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isInlineSourceMap");
    let method_is_inline_sources = env
      .get_method_id(&class, "isInlineSources", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isInlineSources");
    let method_is_jsx_automatic = env
      .get_method_id(&class, "isJsxAutomatic", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isJsxAutomatic");
    let method_is_jsx_development = env
      .get_method_id(&class, "isJsxDevelopment", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isJsxDevelopment");
    let method_is_precompile_jsx = env
      .get_method_id(&class, "isPrecompileJsx", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isPrecompileJsx");
    let method_is_source_map = env
      .get_method_id(&class, "isSourceMap", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isSourceMap");
    let method_is_transform_jsx = env
      .get_method_id(&class, "isTransformJsx", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isTransformJsx");
    let method_is_var_decl_imports = env
      .get_method_id(&class, "isVarDeclImports", "()Z")
      .expect("Couldn't find method Swc4jTranspileOptions.isVarDeclImports");
    JavaTranspileOptions {
      class,
      method_get_media_type,
      method_get_specifier,
      method_get_jsx_factory,
      method_get_jsx_fragment_factory,
      method_get_jsx_import_source,
      method_is_emit_metadata,
      method_is_inline_source_map,
      method_is_inline_sources,
      method_is_jsx_automatic,
      method_is_jsx_development,
      method_is_precompile_jsx,
      method_is_source_map,
      method_is_transform_jsx,
      method_is_var_decl_imports,
    }
  }

  pub fn get_jsx_factory<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> String {
    jni_utils::get_as_string(env, obj, self.method_get_jsx_factory)
  }

  pub fn get_jsx_fragment_factory<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> String {
    jni_utils::get_as_string(env, obj, self.method_get_jsx_fragment_factory)
  }

  pub fn get_jsx_import_source<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> Option<String> {
    jni_utils::get_as_optional_string(env, obj, self.method_get_jsx_import_source)
  }

  pub fn get_media_type<'local, 'a, 'b>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> JObject<'b> {
    jni_utils::get_as_jobject(env, obj, self.method_get_media_type)
  }

  pub fn get_specifier<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> String {
    jni_utils::get_as_string(env, obj, self.method_get_specifier)
  }

  pub fn is_emit_metadata<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_emit_metadata)
  }

  pub fn is_inline_source_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_inline_source_map)
  }

  pub fn is_inline_sources<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_inline_sources)
  }

  pub fn is_jsx_automatic<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_jsx_automatic)
  }

  pub fn is_jsx_development<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_jsx_development)
  }

  pub fn is_source_map<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_source_map)
  }

  pub fn is_transform_jsx<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_transform_jsx)
  }

  pub fn is_precompile_jsx<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_precompile_jsx)
  }

  pub fn is_var_decl_imports<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> bool {
    jni_utils::get_as_boolean(env, obj, self.method_is_var_decl_imports)
  }
}

static mut JAVA_MEDIA_TYPE: Option<JavaMediaType> = None;
static mut JAVA_TRANSPILER_OPTIONS: Option<JavaTranspileOptions> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_MEDIA_TYPE = Some(JavaMediaType::new(env));
    JAVA_TRANSPILER_OPTIONS = Some(JavaTranspileOptions::new(env));
  }
}

pub trait FromJniType {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, o: jobject) -> Self;
}

#[derive(Debug)]
pub struct TranspileOptions {
  /// When emitting a legacy decorator, also emit experimental decorator meta
  /// data.  Defaults to `false`.
  pub emit_metadata: bool,
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
  /// Should import declarations be transformed to variable declarations using
  /// a dynamic import. This is useful for import & export declaration support
  /// in script contexts such as the Deno REPL.  Defaults to `false`.
  pub var_decl_imports: bool,
}

impl Default for TranspileOptions {
  fn default() -> Self {
    TranspileOptions {
      emit_metadata: false,
      inline_source_map: true,
      inline_sources: true,
      jsx_automatic: false,
      jsx_development: false,
      jsx_factory: "React.createElement".into(),
      jsx_fragment_factory: "React.Fragment".into(),
      jsx_import_source: None,
      media_type: MediaType::TypeScript,
      precompile_jsx: false,
      source_map: false,
      specifier: "file:///main.js".to_owned(),
      transform_jsx: true,
      var_decl_imports: false,
    }
  }
}

impl FromJniType for TranspileOptions {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, obj: jobject) -> TranspileOptions {
    let obj = unsafe { JObject::from_raw(obj) };
    let obj = obj.as_ref();
    let java_transpiler_options = unsafe { JAVA_TRANSPILER_OPTIONS.as_ref().unwrap() };
    let emit_metadata = java_transpiler_options.is_emit_metadata(env, obj);
    let inline_source_map = java_transpiler_options.is_inline_source_map(env, obj);
    let inline_sources = java_transpiler_options.is_inline_sources(env, obj);
    let jsx_automatic = java_transpiler_options.is_jsx_automatic(env, obj);
    let jsx_development = java_transpiler_options.is_jsx_development(env, obj);
    let jsx_factory = java_transpiler_options.get_jsx_factory(env, obj);
    let jsx_fragment_factory = java_transpiler_options.get_jsx_fragment_factory(env, obj);
    let jsx_import_source = java_transpiler_options.get_jsx_import_source(env, obj);
    let media_type = java_transpiler_options.get_media_type(env, obj);
    let media_type = media_type.as_ref();
    let java_media_type = unsafe { JAVA_MEDIA_TYPE.as_ref().unwrap() };
    let media_type = java_media_type.get_media_type(env, media_type);
    let source_map = java_transpiler_options.is_source_map(env, obj);
    let specifier = java_transpiler_options.get_specifier(env, obj);
    let transform_jsx = java_transpiler_options.is_transform_jsx(env, obj);
    let precompile_jsx = java_transpiler_options.is_precompile_jsx(env, obj);
    let var_decl_imports =java_transpiler_options.is_var_decl_imports(env, obj); 
    TranspileOptions {
      emit_metadata,
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
      var_decl_imports,
    }
  }
}
