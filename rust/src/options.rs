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
use jni::signature::{Primitive, ReturnType};
use jni::sys::jobject;
use jni::JNIEnv;

use crate::converter;
use crate::enums::*;
use crate::jni_utils;

/* JavaSwc4jParseOptions Begin */
struct JavaSwc4jParseOptions {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_media_type: JMethodID,
  method_get_parse_mode: JMethodID,
  method_get_specifier: JMethodID,
  method_is_capture_ast: JMethodID,
  method_is_capture_tokens: JMethodID,
  method_is_scope_analysis: JMethodID,
}
unsafe impl Send for JavaSwc4jParseOptions {}
unsafe impl Sync for JavaSwc4jParseOptions {}

impl JavaSwc4jParseOptions {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/options/Swc4jParseOptions")
      .expect("Couldn't find class Swc4jParseOptions");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jParseOptions");
    let method_get_media_type = env
      .get_method_id(
        &class,
        "getMediaType",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;",
      )
      .expect("Couldn't find method Swc4jParseOptions.getMediaType");
    let method_get_parse_mode = env
      .get_method_id(
        &class,
        "getParseMode",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;",
      )
      .expect("Couldn't find method Swc4jParseOptions.getParseMode");
    let method_get_specifier = env
      .get_method_id(
        &class,
        "getSpecifier",
        "()Ljava/lang/String;",
      )
      .expect("Couldn't find method Swc4jParseOptions.getSpecifier");
    let method_is_capture_ast = env
      .get_method_id(
        &class,
        "isCaptureAst",
        "()Z",
      )
      .expect("Couldn't find method Swc4jParseOptions.isCaptureAst");
    let method_is_capture_tokens = env
      .get_method_id(
        &class,
        "isCaptureTokens",
        "()Z",
      )
      .expect("Couldn't find method Swc4jParseOptions.isCaptureTokens");
    let method_is_scope_analysis = env
      .get_method_id(
        &class,
        "isScopeAnalysis",
        "()Z",
      )
      .expect("Couldn't find method Swc4jParseOptions.isScopeAnalysis");
    JavaSwc4jParseOptions {
      class,
      method_get_media_type,
      method_get_parse_mode,
      method_get_specifier,
      method_is_capture_ast,
      method_is_capture_tokens,
      method_is_scope_analysis,
    }
  }

  pub fn get_media_type<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_media_type,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create Swc4jMediaType by get_media_type()")
        .l()
        .expect("Couldn't convert Swc4jMediaType by get_media_type()")
    };
    return_value
  }

  pub fn get_parse_mode<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_parse_mode,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create Swc4jParseMode by get_parse_mode()")
        .l()
        .expect("Couldn't convert Swc4jParseMode by get_parse_mode()")
    };
    return_value
  }

  pub fn get_specifier<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> String
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_specifier,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create String by get_specifier()")
        .l()
        .expect("Couldn't convert String by get_specifier()")
    };
    let return_value = converter::jstring_to_string(env, return_value.as_raw());
    return_value
  }

  pub fn is_capture_ast<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_capture_ast,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_capture_ast()")
        .z()
        .expect("Couldn't convert boolean by is_capture_ast()")
    };
    return_value
  }

  pub fn is_capture_tokens<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_capture_tokens,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_capture_tokens()")
        .z()
        .expect("Couldn't convert boolean by is_capture_tokens()")
    };
    return_value
  }

  pub fn is_scope_analysis<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_scope_analysis,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_scope_analysis()")
        .z()
        .expect("Couldn't convert boolean by is_scope_analysis()")
    };
    return_value
  }
}
/* JavaSwc4jParseOptions End */

/* JavaSwc4jTranspileOptions Begin */
struct JavaSwc4jTranspileOptions {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_imports_not_used_as_values: JMethodID,
  method_get_jsx_factory: JMethodID,
  method_get_jsx_fragment_factory: JMethodID,
  method_get_jsx_import_source: JMethodID,
  method_get_media_type: JMethodID,
  method_get_parse_mode: JMethodID,
  method_get_specifier: JMethodID,
  method_is_capture_ast: JMethodID,
  method_is_capture_tokens: JMethodID,
  method_is_emit_metadata: JMethodID,
  method_is_inline_source_map: JMethodID,
  method_is_inline_sources: JMethodID,
  method_is_jsx_automatic: JMethodID,
  method_is_jsx_development: JMethodID,
  method_is_precompile_jsx: JMethodID,
  method_is_scope_analysis: JMethodID,
  method_is_source_map: JMethodID,
  method_is_transform_jsx: JMethodID,
  method_is_var_decl_imports: JMethodID,
}
unsafe impl Send for JavaSwc4jTranspileOptions {}
unsafe impl Sync for JavaSwc4jTranspileOptions {}

impl JavaSwc4jTranspileOptions {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/options/Swc4jTranspileOptions")
      .expect("Couldn't find class Swc4jTranspileOptions");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTranspileOptions");
    let method_get_imports_not_used_as_values = env
      .get_method_id(
        &class,
        "getImportsNotUsedAsValues",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jImportsNotUsedAsValues;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getImportsNotUsedAsValues");
    let method_get_jsx_factory = env
      .get_method_id(
        &class,
        "getJsxFactory",
        "()Ljava/lang/String;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getJsxFactory");
    let method_get_jsx_fragment_factory = env
      .get_method_id(
        &class,
        "getJsxFragmentFactory",
        "()Ljava/lang/String;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getJsxFragmentFactory");
    let method_get_jsx_import_source = env
      .get_method_id(
        &class,
        "getJsxImportSource",
        "()Ljava/lang/String;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getJsxImportSource");
    let method_get_media_type = env
      .get_method_id(
        &class,
        "getMediaType",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getMediaType");
    let method_get_parse_mode = env
      .get_method_id(
        &class,
        "getParseMode",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getParseMode");
    let method_get_specifier = env
      .get_method_id(
        &class,
        "getSpecifier",
        "()Ljava/lang/String;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getSpecifier");
    let method_is_capture_ast = env
      .get_method_id(
        &class,
        "isCaptureAst",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isCaptureAst");
    let method_is_capture_tokens = env
      .get_method_id(
        &class,
        "isCaptureTokens",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isCaptureTokens");
    let method_is_emit_metadata = env
      .get_method_id(
        &class,
        "isEmitMetadata",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isEmitMetadata");
    let method_is_inline_source_map = env
      .get_method_id(
        &class,
        "isInlineSourceMap",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isInlineSourceMap");
    let method_is_inline_sources = env
      .get_method_id(
        &class,
        "isInlineSources",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isInlineSources");
    let method_is_jsx_automatic = env
      .get_method_id(
        &class,
        "isJsxAutomatic",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isJsxAutomatic");
    let method_is_jsx_development = env
      .get_method_id(
        &class,
        "isJsxDevelopment",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isJsxDevelopment");
    let method_is_precompile_jsx = env
      .get_method_id(
        &class,
        "isPrecompileJsx",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isPrecompileJsx");
    let method_is_scope_analysis = env
      .get_method_id(
        &class,
        "isScopeAnalysis",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isScopeAnalysis");
    let method_is_source_map = env
      .get_method_id(
        &class,
        "isSourceMap",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isSourceMap");
    let method_is_transform_jsx = env
      .get_method_id(
        &class,
        "isTransformJsx",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isTransformJsx");
    let method_is_var_decl_imports = env
      .get_method_id(
        &class,
        "isVarDeclImports",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isVarDeclImports");
    JavaSwc4jTranspileOptions {
      class,
      method_get_imports_not_used_as_values,
      method_get_jsx_factory,
      method_get_jsx_fragment_factory,
      method_get_jsx_import_source,
      method_get_media_type,
      method_get_parse_mode,
      method_get_specifier,
      method_is_capture_ast,
      method_is_capture_tokens,
      method_is_emit_metadata,
      method_is_inline_source_map,
      method_is_inline_sources,
      method_is_jsx_automatic,
      method_is_jsx_development,
      method_is_precompile_jsx,
      method_is_scope_analysis,
      method_is_source_map,
      method_is_transform_jsx,
      method_is_var_decl_imports,
    }
  }

  pub fn get_imports_not_used_as_values<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_imports_not_used_as_values,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create Swc4jImportsNotUsedAsValues by get_imports_not_used_as_values()")
        .l()
        .expect("Couldn't convert Swc4jImportsNotUsedAsValues by get_imports_not_used_as_values()")
    };
    return_value
  }

  pub fn get_jsx_factory<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> String
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_jsx_factory,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create String by get_jsx_factory()")
        .l()
        .expect("Couldn't convert String by get_jsx_factory()")
    };
    let return_value = converter::jstring_to_string(env, return_value.as_raw());
    return_value
  }

  pub fn get_jsx_fragment_factory<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> String
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_jsx_fragment_factory,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create String by get_jsx_fragment_factory()")
        .l()
        .expect("Couldn't convert String by get_jsx_fragment_factory()")
    };
    let return_value = converter::jstring_to_string(env, return_value.as_raw());
    return_value
  }

  pub fn get_jsx_import_source<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Option<String>
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_jsx_import_source,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create String by get_jsx_import_source()")
        .l()
        .expect("Couldn't convert String by get_jsx_import_source()")
    };
    let return_value = converter::jstring_to_optional_string(env, return_value.as_raw());
    return_value
  }

  pub fn get_media_type<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_media_type,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create Swc4jMediaType by get_media_type()")
        .l()
        .expect("Couldn't convert Swc4jMediaType by get_media_type()")
    };
    return_value
  }

  pub fn get_parse_mode<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_parse_mode,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create Swc4jParseMode by get_parse_mode()")
        .l()
        .expect("Couldn't convert Swc4jParseMode by get_parse_mode()")
    };
    return_value
  }

  pub fn get_specifier<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> String
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_get_specifier,
          ReturnType::Object,
          &[],
        )
        .expect("Couldn't create String by get_specifier()")
        .l()
        .expect("Couldn't convert String by get_specifier()")
    };
    let return_value = converter::jstring_to_string(env, return_value.as_raw());
    return_value
  }

  pub fn is_capture_ast<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_capture_ast,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_capture_ast()")
        .z()
        .expect("Couldn't convert boolean by is_capture_ast()")
    };
    return_value
  }

  pub fn is_capture_tokens<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_capture_tokens,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_capture_tokens()")
        .z()
        .expect("Couldn't convert boolean by is_capture_tokens()")
    };
    return_value
  }

  pub fn is_emit_metadata<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_emit_metadata,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_emit_metadata()")
        .z()
        .expect("Couldn't convert boolean by is_emit_metadata()")
    };
    return_value
  }

  pub fn is_inline_source_map<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_inline_source_map,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_inline_source_map()")
        .z()
        .expect("Couldn't convert boolean by is_inline_source_map()")
    };
    return_value
  }

  pub fn is_inline_sources<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_inline_sources,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_inline_sources()")
        .z()
        .expect("Couldn't convert boolean by is_inline_sources()")
    };
    return_value
  }

  pub fn is_jsx_automatic<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_jsx_automatic,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_jsx_automatic()")
        .z()
        .expect("Couldn't convert boolean by is_jsx_automatic()")
    };
    return_value
  }

  pub fn is_jsx_development<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_jsx_development,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_jsx_development()")
        .z()
        .expect("Couldn't convert boolean by is_jsx_development()")
    };
    return_value
  }

  pub fn is_precompile_jsx<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_precompile_jsx,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_precompile_jsx()")
        .z()
        .expect("Couldn't convert boolean by is_precompile_jsx()")
    };
    return_value
  }

  pub fn is_scope_analysis<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_scope_analysis,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_scope_analysis()")
        .z()
        .expect("Couldn't convert boolean by is_scope_analysis()")
    };
    return_value
  }

  pub fn is_source_map<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_source_map,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_source_map()")
        .z()
        .expect("Couldn't convert boolean by is_source_map()")
    };
    return_value
  }

  pub fn is_transform_jsx<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_transform_jsx,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_transform_jsx()")
        .z()
        .expect("Couldn't convert boolean by is_transform_jsx()")
    };
    return_value
  }

  pub fn is_var_decl_imports<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> bool
  {
    let return_value = unsafe {
      env
        .call_method_unchecked(
          obj,
          self.method_is_var_decl_imports,
          ReturnType::Primitive(Primitive::Boolean),
          &[],
        )
        .expect("Couldn't create boolean by is_var_decl_imports()")
        .z()
        .expect("Couldn't convert boolean by is_var_decl_imports()")
    };
    return_value
  }
}
/* JavaSwc4jTranspileOptions End */

static mut JAVA_PARSE_OPTIONS: Option<JavaSwc4jParseOptions> = None;
static mut JAVA_TRANSPILE_OPTIONS: Option<JavaSwc4jTranspileOptions> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_PARSE_OPTIONS = Some(JavaSwc4jParseOptions::new(env));
    JAVA_TRANSPILE_OPTIONS = Some(JavaSwc4jTranspileOptions::new(env));
  }
}

pub trait FromJniType {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, o: jobject) -> Self;
}

#[derive(Debug)]
pub struct ParseOptions {
  /// Whether to capture ast or not.
  pub capture_ast: bool,
  /// Whether to capture tokens or not.
  pub capture_tokens: bool,
  /// Media type of the source text.
  pub media_type: MediaType,
  /// Should the code to be parsed as Module or Script,
  pub parse_mode: ParseMode,
  /// Whether to apply swc's scope analysis.
  pub scope_analysis: bool,
  /// Specifier of the source text.
  pub specifier: String,
}

impl Default for ParseOptions {
  fn default() -> Self {
    ParseOptions {
      capture_ast: false,
      capture_tokens: false,
      media_type: MediaType::TypeScript,
      parse_mode: ParseMode::Module,
      scope_analysis: false,
      specifier: "file:///main.js".to_owned(),
    }
  }
}

impl FromJniType for ParseOptions {
  fn from_jni_type<'local>(env: &mut JNIEnv<'local>, obj: jobject) -> ParseOptions {
    let obj = unsafe { JObject::from_raw(obj) };
    let obj = obj.as_ref();
    let java_media_type = unsafe { JAVA_MEDIA_TYPE.as_ref().unwrap() };
    let java_parse_mode = unsafe { JAVA_PARSE_MODE.as_ref().unwrap() };
    let java_parse_options = unsafe { JAVA_PARSE_OPTIONS.as_ref().unwrap() };
    let capture_ast = java_parse_options.is_capture_ast(env, obj);
    let capture_tokens = java_parse_options.is_capture_tokens(env, obj);
    let media_type = java_parse_options.get_media_type(env, obj);
    let media_type = media_type.as_ref();
    let media_type = java_media_type.get_media_type(env, media_type);
    let scope_analysis = java_parse_options.is_scope_analysis(env, obj);
    let specifier = java_parse_options.get_specifier(env, obj);
    let parse_mode = java_parse_options.get_parse_mode(env, obj);
    let parse_mode = parse_mode.as_ref();
    let parse_mode = java_parse_mode.get_parse_mode(env, parse_mode);
    ParseOptions {
      capture_ast,
      capture_tokens,
      media_type,
      parse_mode,
      scope_analysis,
      specifier,
    }
  }
}

#[derive(Debug)]
pub struct TranspileOptions {
  /// Whether to capture ast or not.
  pub capture_ast: bool,
  /// Whether to capture tokens or not.
  pub capture_tokens: bool,
  /// When emitting a legacy decorator, also emit experimental decorator meta
  /// data.  Defaults to `false`.
  pub emit_metadata: bool,
  /// What to do with import statements that only import types i.e. whether to
  /// remove them (`Remove`), keep them as side-effect imports (`Preserve`)
  /// or error (`Error`). Defaults to `Remove`.
  pub imports_not_used_as_values: ImportsNotUsedAsValues,
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
  /// Should the code to be parsed as Module or Script,
  pub parse_mode: ParseMode,
  /// Should JSX be precompiled into static strings that need to be concatenated
  /// with dynamic content. Defaults to `false`, mutually exclusive with
  /// `transform_jsx`.
  pub precompile_jsx: bool,
  /// Whether to apply swc's scope analysis.
  pub scope_analysis: bool,
  /// Should a corresponding .map file be created for the output. This should be
  /// false if inline_source_map is true. Defaults to `false`.
  pub source_map: bool,
  /// Specifier of the source text.
  pub specifier: String,
  /// Should JSX be transformed. Defaults to `true`.
  pub transform_jsx: bool,
  /// Should import declarations be transformed to variable declarations using
  /// a dynamic import. This is useful for import & export declaration support
  /// in script contexts such as the Deno REPL.  Defaults to `false`.
  pub var_decl_imports: bool,
}

impl Default for TranspileOptions {
  fn default() -> Self {
    TranspileOptions {
      capture_ast: false,
      capture_tokens: false,
      emit_metadata: false,
      imports_not_used_as_values: ImportsNotUsedAsValues::Remove,
      inline_source_map: true,
      inline_sources: true,
      jsx_automatic: false,
      jsx_development: false,
      jsx_factory: "React.createElement".into(),
      jsx_fragment_factory: "React.Fragment".into(),
      jsx_import_source: None,
      media_type: MediaType::TypeScript,
      parse_mode: ParseMode::Module,
      precompile_jsx: false,
      scope_analysis: false,
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
    let java_imports_not_used_as_values = unsafe { JAVA_IMPORTS_NOT_USED_AS_VALUES.as_ref().unwrap() };
    let java_media_type = unsafe { JAVA_MEDIA_TYPE.as_ref().unwrap() };
    let java_parse_mode = unsafe { JAVA_PARSE_MODE.as_ref().unwrap() };
    let java_transpiler_options = unsafe { JAVA_TRANSPILE_OPTIONS.as_ref().unwrap() };
    let capture_ast = java_transpiler_options.is_capture_ast(env, obj);
    let capture_tokens = java_transpiler_options.is_capture_tokens(env, obj);
    let emit_metadata = java_transpiler_options.is_emit_metadata(env, obj);
    let imports_not_used_as_values = java_transpiler_options.get_imports_not_used_as_values(env, obj);
    let imports_not_used_as_values = imports_not_used_as_values.as_ref();
    let imports_not_used_as_values =
      java_imports_not_used_as_values.get_imports_not_used_as_values(env, imports_not_used_as_values);
    let inline_source_map = java_transpiler_options.is_inline_source_map(env, obj);
    let inline_sources = java_transpiler_options.is_inline_sources(env, obj);
    let jsx_automatic = java_transpiler_options.is_jsx_automatic(env, obj);
    let jsx_development = java_transpiler_options.is_jsx_development(env, obj);
    let jsx_factory = java_transpiler_options.get_jsx_factory(env, obj);
    let jsx_fragment_factory = java_transpiler_options.get_jsx_fragment_factory(env, obj);
    let jsx_import_source = java_transpiler_options.get_jsx_import_source(env, obj);
    let media_type = java_transpiler_options.get_media_type(env, obj);
    let media_type = media_type.as_ref();
    let media_type = java_media_type.get_media_type(env, media_type);
    let parse_mode = java_transpiler_options.get_parse_mode(env, obj);
    let parse_mode = parse_mode.as_ref();
    let parse_mode = java_parse_mode.get_parse_mode(env, parse_mode);
    let precompile_jsx = java_transpiler_options.is_precompile_jsx(env, obj);
    let scope_analysis = java_transpiler_options.is_scope_analysis(env, obj);
    let source_map = java_transpiler_options.is_source_map(env, obj);
    let specifier = java_transpiler_options.get_specifier(env, obj);
    let transform_jsx = java_transpiler_options.is_transform_jsx(env, obj);
    let var_decl_imports = java_transpiler_options.is_var_decl_imports(env, obj);
    TranspileOptions {
      capture_ast,
      capture_tokens,
      emit_metadata,
      imports_not_used_as_values,
      inline_source_map,
      inline_sources,
      jsx_automatic,
      jsx_development,
      jsx_factory,
      jsx_fragment_factory,
      jsx_import_source,
      media_type,
      parse_mode,
      precompile_jsx,
      scope_analysis,
      source_map,
      specifier,
      transform_jsx,
      var_decl_imports,
    }
  }
}
