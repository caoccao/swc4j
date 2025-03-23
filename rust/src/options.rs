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

use std::sync::OnceLock;

use anyhow::{Error, Result};
use deno_ast::ModuleSpecifier;
use jni::objects::{GlobalRef, JMethodID, JObject, JString};
use jni::JNIEnv;

use crate::enums::*;
use crate::jni_utils::*;
use crate::plugin_utils::PluginHost;

/* JavaSwc4jParseOptions Begin */
#[allow(dead_code)]
struct JavaSwc4jParseOptions {
  class: GlobalRef,
  method_get_media_type: JMethodID,
  method_get_parse_mode: JMethodID,
  method_get_plugin_host: JMethodID,
  method_get_specifier: JMethodID,
  method_is_capture_ast: JMethodID,
  method_is_capture_comments: JMethodID,
  method_is_capture_tokens: JMethodID,
  method_is_scope_analysis: JMethodID,
}
unsafe impl Send for JavaSwc4jParseOptions {}
unsafe impl Sync for JavaSwc4jParseOptions {}

#[allow(dead_code)]
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
    let method_get_plugin_host = env
      .get_method_id(
        &class,
        "getPluginHost",
        "()Lcom/caoccao/javet/swc4j/plugins/ISwc4jPluginHost;",
      )
      .expect("Couldn't find method Swc4jParseOptions.getPluginHost");
    let method_get_specifier = env
      .get_method_id(
        &class,
        "getSpecifier",
        "()Ljava/net/URL;",
      )
      .expect("Couldn't find method Swc4jParseOptions.getSpecifier");
    let method_is_capture_ast = env
      .get_method_id(
        &class,
        "isCaptureAst",
        "()Z",
      )
      .expect("Couldn't find method Swc4jParseOptions.isCaptureAst");
    let method_is_capture_comments = env
      .get_method_id(
        &class,
        "isCaptureComments",
        "()Z",
      )
      .expect("Couldn't find method Swc4jParseOptions.isCaptureComments");
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
      method_get_plugin_host,
      method_get_specifier,
      method_is_capture_ast,
      method_is_capture_comments,
      method_is_capture_tokens,
      method_is_scope_analysis,
    }
  }

  pub fn get_media_type<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_media_type,
        &[],
        "Swc4jMediaType get_media_type()"
      )?;
    Ok(return_value)
  }

  pub fn get_parse_mode<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_parse_mode,
        &[],
        "Swc4jParseMode get_parse_mode()"
      )?;
    Ok(return_value)
  }

  pub fn get_plugin_host<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_plugin_host,
        &[],
        "ISwc4jPluginHost get_plugin_host()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }

  pub fn get_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_specifier,
        &[],
        "URL get_specifier()"
      )?;
    Ok(return_value)
  }

  pub fn is_capture_ast<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_capture_ast,
        &[],
        "boolean is_capture_ast()"
      )?;
    Ok(return_value)
  }

  pub fn is_capture_comments<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_capture_comments,
        &[],
        "boolean is_capture_comments()"
      )?;
    Ok(return_value)
  }

  pub fn is_capture_tokens<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_capture_tokens,
        &[],
        "boolean is_capture_tokens()"
      )?;
    Ok(return_value)
  }

  pub fn is_scope_analysis<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_scope_analysis,
        &[],
        "boolean is_scope_analysis()"
      )?;
    Ok(return_value)
  }
}
/* JavaSwc4jParseOptions End */

/* JavaSwc4jTransformOptions Begin */
#[allow(dead_code)]
struct JavaSwc4jTransformOptions {
  class: GlobalRef,
  method_get_media_type: JMethodID,
  method_get_parse_mode: JMethodID,
  method_get_plugin_host: JMethodID,
  method_get_source_map: JMethodID,
  method_get_specifier: JMethodID,
  method_get_target: JMethodID,
  method_is_ascii_only: JMethodID,
  method_is_emit_assert_for_import_attributes: JMethodID,
  method_is_inline_sources: JMethodID,
  method_is_keep_comments: JMethodID,
  method_is_minify: JMethodID,
  method_is_omit_last_semi: JMethodID,
}
unsafe impl Send for JavaSwc4jTransformOptions {}
unsafe impl Sync for JavaSwc4jTransformOptions {}

#[allow(dead_code)]
impl JavaSwc4jTransformOptions {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/options/Swc4jTransformOptions")
      .expect("Couldn't find class Swc4jTransformOptions");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTransformOptions");
    let method_get_media_type = env
      .get_method_id(
        &class,
        "getMediaType",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;",
      )
      .expect("Couldn't find method Swc4jTransformOptions.getMediaType");
    let method_get_parse_mode = env
      .get_method_id(
        &class,
        "getParseMode",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;",
      )
      .expect("Couldn't find method Swc4jTransformOptions.getParseMode");
    let method_get_plugin_host = env
      .get_method_id(
        &class,
        "getPluginHost",
        "()Lcom/caoccao/javet/swc4j/plugins/ISwc4jPluginHost;",
      )
      .expect("Couldn't find method Swc4jTransformOptions.getPluginHost");
    let method_get_source_map = env
      .get_method_id(
        &class,
        "getSourceMap",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jSourceMapOption;",
      )
      .expect("Couldn't find method Swc4jTransformOptions.getSourceMap");
    let method_get_specifier = env
      .get_method_id(
        &class,
        "getSpecifier",
        "()Ljava/net/URL;",
      )
      .expect("Couldn't find method Swc4jTransformOptions.getSpecifier");
    let method_get_target = env
      .get_method_id(
        &class,
        "getTarget",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jEsVersion;",
      )
      .expect("Couldn't find method Swc4jTransformOptions.getTarget");
    let method_is_ascii_only = env
      .get_method_id(
        &class,
        "isAsciiOnly",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTransformOptions.isAsciiOnly");
    let method_is_emit_assert_for_import_attributes = env
      .get_method_id(
        &class,
        "isEmitAssertForImportAttributes",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTransformOptions.isEmitAssertForImportAttributes");
    let method_is_inline_sources = env
      .get_method_id(
        &class,
        "isInlineSources",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTransformOptions.isInlineSources");
    let method_is_keep_comments = env
      .get_method_id(
        &class,
        "isKeepComments",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTransformOptions.isKeepComments");
    let method_is_minify = env
      .get_method_id(
        &class,
        "isMinify",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTransformOptions.isMinify");
    let method_is_omit_last_semi = env
      .get_method_id(
        &class,
        "isOmitLastSemi",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTransformOptions.isOmitLastSemi");
    JavaSwc4jTransformOptions {
      class,
      method_get_media_type,
      method_get_parse_mode,
      method_get_plugin_host,
      method_get_source_map,
      method_get_specifier,
      method_get_target,
      method_is_ascii_only,
      method_is_emit_assert_for_import_attributes,
      method_is_inline_sources,
      method_is_keep_comments,
      method_is_minify,
      method_is_omit_last_semi,
    }
  }

  pub fn get_media_type<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_media_type,
        &[],
        "Swc4jMediaType get_media_type()"
      )?;
    Ok(return_value)
  }

  pub fn get_parse_mode<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_parse_mode,
        &[],
        "Swc4jParseMode get_parse_mode()"
      )?;
    Ok(return_value)
  }

  pub fn get_plugin_host<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_plugin_host,
        &[],
        "ISwc4jPluginHost get_plugin_host()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }

  pub fn get_source_map<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_source_map,
        &[],
        "Swc4jSourceMapOption get_source_map()"
      )?;
    Ok(return_value)
  }

  pub fn get_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_specifier,
        &[],
        "URL get_specifier()"
      )?;
    Ok(return_value)
  }

  pub fn get_target<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_target,
        &[],
        "Swc4jEsVersion get_target()"
      )?;
    Ok(return_value)
  }

  pub fn is_ascii_only<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_ascii_only,
        &[],
        "boolean is_ascii_only()"
      )?;
    Ok(return_value)
  }

  pub fn is_emit_assert_for_import_attributes<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_emit_assert_for_import_attributes,
        &[],
        "boolean is_emit_assert_for_import_attributes()"
      )?;
    Ok(return_value)
  }

  pub fn is_inline_sources<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_inline_sources,
        &[],
        "boolean is_inline_sources()"
      )?;
    Ok(return_value)
  }

  pub fn is_keep_comments<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_keep_comments,
        &[],
        "boolean is_keep_comments()"
      )?;
    Ok(return_value)
  }

  pub fn is_minify<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_minify,
        &[],
        "boolean is_minify()"
      )?;
    Ok(return_value)
  }

  pub fn is_omit_last_semi<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_omit_last_semi,
        &[],
        "boolean is_omit_last_semi()"
      )?;
    Ok(return_value)
  }
}
/* JavaSwc4jTransformOptions End */

/* JavaSwc4jTranspileOptions Begin */
#[allow(dead_code)]
struct JavaSwc4jTranspileOptions {
  class: GlobalRef,
  method_get_imports_not_used_as_values: JMethodID,
  method_get_jsx_factory: JMethodID,
  method_get_jsx_fragment_factory: JMethodID,
  method_get_jsx_import_source: JMethodID,
  method_get_media_type: JMethodID,
  method_get_module_kind: JMethodID,
  method_get_parse_mode: JMethodID,
  method_get_plugin_host: JMethodID,
  method_get_precompile_jsx_dynamic_props: JMethodID,
  method_get_precompile_jsx_skip_elements: JMethodID,
  method_get_source_map: JMethodID,
  method_get_specifier: JMethodID,
  method_is_capture_ast: JMethodID,
  method_is_capture_comments: JMethodID,
  method_is_capture_tokens: JMethodID,
  method_is_emit_metadata: JMethodID,
  method_is_inline_sources: JMethodID,
  method_is_jsx_automatic: JMethodID,
  method_is_jsx_development: JMethodID,
  method_is_keep_comments: JMethodID,
  method_is_precompile_jsx: JMethodID,
  method_is_scope_analysis: JMethodID,
  method_is_transform_jsx: JMethodID,
  method_is_use_decorators_proposal: JMethodID,
  method_is_use_ts_decorators: JMethodID,
  method_is_var_decl_imports: JMethodID,
  method_is_verbatim_module_syntax: JMethodID,
}
unsafe impl Send for JavaSwc4jTranspileOptions {}
unsafe impl Sync for JavaSwc4jTranspileOptions {}

#[allow(dead_code)]
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
    let method_get_module_kind = env
      .get_method_id(
        &class,
        "getModuleKind",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jModuleKind;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getModuleKind");
    let method_get_parse_mode = env
      .get_method_id(
        &class,
        "getParseMode",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getParseMode");
    let method_get_plugin_host = env
      .get_method_id(
        &class,
        "getPluginHost",
        "()Lcom/caoccao/javet/swc4j/plugins/ISwc4jPluginHost;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getPluginHost");
    let method_get_precompile_jsx_dynamic_props = env
      .get_method_id(
        &class,
        "getPrecompileJsxDynamicProps",
        "()Ljava/util/List;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getPrecompileJsxDynamicProps");
    let method_get_precompile_jsx_skip_elements = env
      .get_method_id(
        &class,
        "getPrecompileJsxSkipElements",
        "()Ljava/util/List;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getPrecompileJsxSkipElements");
    let method_get_source_map = env
      .get_method_id(
        &class,
        "getSourceMap",
        "()Lcom/caoccao/javet/swc4j/enums/Swc4jSourceMapOption;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getSourceMap");
    let method_get_specifier = env
      .get_method_id(
        &class,
        "getSpecifier",
        "()Ljava/net/URL;",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getSpecifier");
    let method_is_capture_ast = env
      .get_method_id(
        &class,
        "isCaptureAst",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isCaptureAst");
    let method_is_capture_comments = env
      .get_method_id(
        &class,
        "isCaptureComments",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isCaptureComments");
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
    let method_is_keep_comments = env
      .get_method_id(
        &class,
        "isKeepComments",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isKeepComments");
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
    let method_is_transform_jsx = env
      .get_method_id(
        &class,
        "isTransformJsx",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isTransformJsx");
    let method_is_use_decorators_proposal = env
      .get_method_id(
        &class,
        "isUseDecoratorsProposal",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isUseDecoratorsProposal");
    let method_is_use_ts_decorators = env
      .get_method_id(
        &class,
        "isUseTsDecorators",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isUseTsDecorators");
    let method_is_var_decl_imports = env
      .get_method_id(
        &class,
        "isVarDeclImports",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isVarDeclImports");
    let method_is_verbatim_module_syntax = env
      .get_method_id(
        &class,
        "isVerbatimModuleSyntax",
        "()Z",
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isVerbatimModuleSyntax");
    JavaSwc4jTranspileOptions {
      class,
      method_get_imports_not_used_as_values,
      method_get_jsx_factory,
      method_get_jsx_fragment_factory,
      method_get_jsx_import_source,
      method_get_media_type,
      method_get_module_kind,
      method_get_parse_mode,
      method_get_plugin_host,
      method_get_precompile_jsx_dynamic_props,
      method_get_precompile_jsx_skip_elements,
      method_get_source_map,
      method_get_specifier,
      method_is_capture_ast,
      method_is_capture_comments,
      method_is_capture_tokens,
      method_is_emit_metadata,
      method_is_inline_sources,
      method_is_jsx_automatic,
      method_is_jsx_development,
      method_is_keep_comments,
      method_is_precompile_jsx,
      method_is_scope_analysis,
      method_is_transform_jsx,
      method_is_use_decorators_proposal,
      method_is_use_ts_decorators,
      method_is_var_decl_imports,
      method_is_verbatim_module_syntax,
    }
  }

  pub fn get_imports_not_used_as_values<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_imports_not_used_as_values,
        &[],
        "Swc4jImportsNotUsedAsValues get_imports_not_used_as_values()"
      )?;
    Ok(return_value)
  }

  pub fn get_jsx_factory<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<String>
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_jsx_factory,
        &[],
        "String get_jsx_factory()"
      )?;
    let java_return_value = return_value;
    let return_value: Result<String> = jstring_to_string!(env, java_return_value.as_raw());
    let return_value = return_value?;
    delete_local_ref!(env, java_return_value);
    Ok(return_value)
  }

  pub fn get_jsx_fragment_factory<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<String>
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_jsx_fragment_factory,
        &[],
        "String get_jsx_fragment_factory()"
      )?;
    let java_return_value = return_value;
    let return_value: Result<String> = jstring_to_string!(env, java_return_value.as_raw());
    let return_value = return_value?;
    delete_local_ref!(env, java_return_value);
    Ok(return_value)
  }

  pub fn get_jsx_import_source<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<String>>
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_jsx_import_source,
        &[],
        "String get_jsx_import_source()"
      )?;
    let java_return_value = return_value;
    let return_value = jstring_to_optional_string!(env, java_return_value.as_raw())?;
    delete_local_ref!(env, java_return_value);
    Ok(return_value)
  }

  pub fn get_media_type<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_media_type,
        &[],
        "Swc4jMediaType get_media_type()"
      )?;
    Ok(return_value)
  }

  pub fn get_module_kind<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_module_kind,
        &[],
        "Swc4jModuleKind get_module_kind()"
      )?;
    Ok(return_value)
  }

  pub fn get_parse_mode<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_parse_mode,
        &[],
        "Swc4jParseMode get_parse_mode()"
      )?;
    Ok(return_value)
  }

  pub fn get_plugin_host<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_plugin_host,
        &[],
        "ISwc4jPluginHost get_plugin_host()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }

  pub fn get_precompile_jsx_dynamic_props<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_precompile_jsx_dynamic_props,
        &[],
        "List get_precompile_jsx_dynamic_props()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }

  pub fn get_precompile_jsx_skip_elements<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_precompile_jsx_skip_elements,
        &[],
        "List get_precompile_jsx_skip_elements()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }

  pub fn get_source_map<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_source_map,
        &[],
        "Swc4jSourceMapOption get_source_map()"
      )?;
    Ok(return_value)
  }

  pub fn get_specifier<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_specifier,
        &[],
        "URL get_specifier()"
      )?;
    Ok(return_value)
  }

  pub fn is_capture_ast<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_capture_ast,
        &[],
        "boolean is_capture_ast()"
      )?;
    Ok(return_value)
  }

  pub fn is_capture_comments<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_capture_comments,
        &[],
        "boolean is_capture_comments()"
      )?;
    Ok(return_value)
  }

  pub fn is_capture_tokens<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_capture_tokens,
        &[],
        "boolean is_capture_tokens()"
      )?;
    Ok(return_value)
  }

  pub fn is_emit_metadata<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_emit_metadata,
        &[],
        "boolean is_emit_metadata()"
      )?;
    Ok(return_value)
  }

  pub fn is_inline_sources<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_inline_sources,
        &[],
        "boolean is_inline_sources()"
      )?;
    Ok(return_value)
  }

  pub fn is_jsx_automatic<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_jsx_automatic,
        &[],
        "boolean is_jsx_automatic()"
      )?;
    Ok(return_value)
  }

  pub fn is_jsx_development<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_jsx_development,
        &[],
        "boolean is_jsx_development()"
      )?;
    Ok(return_value)
  }

  pub fn is_keep_comments<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_keep_comments,
        &[],
        "boolean is_keep_comments()"
      )?;
    Ok(return_value)
  }

  pub fn is_precompile_jsx<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_precompile_jsx,
        &[],
        "boolean is_precompile_jsx()"
      )?;
    Ok(return_value)
  }

  pub fn is_scope_analysis<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_scope_analysis,
        &[],
        "boolean is_scope_analysis()"
      )?;
    Ok(return_value)
  }

  pub fn is_transform_jsx<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_transform_jsx,
        &[],
        "boolean is_transform_jsx()"
      )?;
    Ok(return_value)
  }

  pub fn is_use_decorators_proposal<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_use_decorators_proposal,
        &[],
        "boolean is_use_decorators_proposal()"
      )?;
    Ok(return_value)
  }

  pub fn is_use_ts_decorators<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_use_ts_decorators,
        &[],
        "boolean is_use_ts_decorators()"
      )?;
    Ok(return_value)
  }

  pub fn is_var_decl_imports<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_var_decl_imports,
        &[],
        "boolean is_var_decl_imports()"
      )?;
    Ok(return_value)
  }

  pub fn is_verbatim_module_syntax<'local>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_verbatim_module_syntax,
        &[],
        "boolean is_verbatim_module_syntax()"
      )?;
    Ok(return_value)
  }
}
/* JavaSwc4jTranspileOptions End */

static JAVA_PARSE_OPTIONS: OnceLock<JavaSwc4jParseOptions> = OnceLock::new();
static JAVA_TRANSFORM_OPTIONS: OnceLock<JavaSwc4jTransformOptions> = OnceLock::new();
static JAVA_TRANSPILE_OPTIONS: OnceLock<JavaSwc4jTranspileOptions> = OnceLock::new();

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  log::debug!("init()");
  unsafe {
    JAVA_PARSE_OPTIONS
      .set(JavaSwc4jParseOptions::new(env))
      .unwrap_unchecked();
    JAVA_TRANSFORM_OPTIONS
      .set(JavaSwc4jTransformOptions::new(env))
      .unwrap_unchecked();
    JAVA_TRANSPILE_OPTIONS
      .set(JavaSwc4jTranspileOptions::new(env))
      .unwrap_unchecked();
  }
}

#[derive(Debug)]
pub struct ParseOptions<'a> {
  /// Whether to capture ast or not.
  pub capture_ast: bool,
  /// Whether to capture comments or not.
  pub capture_comments: bool,
  /// Whether to capture tokens or not.
  pub capture_tokens: bool,
  /// Media type of the source text.
  pub media_type: MediaType,
  /// Should the code to be parsed as Module or Script.
  pub parse_mode: ParseMode,
  /// AST plugin host.
  pub plugin_host: Option<PluginHost<'a>>,
  /// Whether to apply swc's scope analysis.
  pub scope_analysis: bool,
  /// Specifier of the source text.
  pub specifier: String,
}

impl<'a> ParseOptions<'a> {
  pub fn get_specifier(&self) -> Result<ModuleSpecifier> {
    ModuleSpecifier::parse(self.specifier.as_str()).map_err(Error::msg)
  }
}

impl<'a> Default for ParseOptions<'a> {
  fn default() -> Self {
    ParseOptions {
      capture_ast: false,
      capture_comments: false,
      capture_tokens: false,
      media_type: MediaType::TypeScript,
      parse_mode: ParseMode::Program,
      plugin_host: None,
      scope_analysis: false,
      specifier: "file:///main.js".to_owned(),
    }
  }
}

impl<'local> FromJava<'local> for ParseOptions<'local> {
  fn from_java(env: &mut JNIEnv<'local>, obj: &JObject<'_>) -> Result<Box<ParseOptions<'local>>> {
    let java_parse_options = JAVA_PARSE_OPTIONS.get().unwrap();
    let capture_ast = java_parse_options.is_capture_ast(env, obj)?;
    let capture_comments = java_parse_options.is_capture_comments(env, obj)?;
    let capture_tokens = java_parse_options.is_capture_tokens(env, obj)?;
    let java_media_type = java_parse_options.get_media_type(env, obj)?;
    let media_type = *MediaType::from_java(env, &java_media_type)?;
    let scope_analysis = java_parse_options.is_scope_analysis(env, obj)?;
    let specifier = java_parse_options.get_specifier(env, obj)?;
    let specifier = url_to_string(env, &specifier)?;
    let java_optional_plugin_host = java_parse_options.get_plugin_host(env, obj)?;
    let plugin_host = java_optional_plugin_host.map(|host| {
      let host = env
        .new_global_ref(host)
        .expect("Failed to create global reference for plugin host");
      PluginHost::new(env, host)
    });
    let java_parse_mode = java_parse_options.get_parse_mode(env, obj)?;
    let parse_mode = *ParseMode::from_java(env, &java_parse_mode)?;
    delete_local_ref!(env, java_media_type);
    delete_local_ref!(env, java_parse_mode);
    Ok(Box::new(ParseOptions {
      capture_ast,
      capture_comments,
      capture_tokens,
      media_type,
      parse_mode,
      plugin_host,
      scope_analysis,
      specifier,
    }))
  }
}

#[derive(Debug)]
pub struct TransformOptions<'a> {
  /// Forces the code generator to use only ascii characters.
  ///
  /// This is useful for environments that do not support unicode.
  pub ascii_only: bool,
  /// Whether to emit assert for import attributes. Defaults to `false`.
  pub emit_assert_for_import_attributes: bool,
  /// Should the sources be inlined in the source map. Defaults to `true`.
  pub inline_sources: bool,
  /// Whether to keep comments in the output. Defaults to `false`.
  pub keep_comments: bool,
  /// Media type of the source text.
  pub media_type: MediaType,
  /// Whether to minify the code. Defaults to `true`.
  pub minify: bool,
  /// If true, the code generator will emit the latest semicolon.
  ///
  /// Defaults to `false`.
  pub omit_last_semi: bool,
  /// Should the code to be parsed as Module or Script.
  pub parse_mode: ParseMode,
  /// AST plugin host.
  pub plugin_host: Option<PluginHost<'a>>,
  /// How and if source maps should be generated.
  pub source_map: SourceMapOption,
  /// Specifier of the source text.
  pub specifier: String,
  /// The target runtime environment.
  ///
  /// This defaults to [EsVersion::latest] because it preserves input as much
  /// as possible.
  ///
  /// Note: This does not verify if output is valid for the target runtime.
  /// e.g. `const foo = 1;` with [EsVersion::Es3] will emit as `const foo =
  /// 1` without verification.
  /// This is because it's not a concern of the code generator.
  pub target: EsVersion,
}

impl<'a> TransformOptions<'a> {
  pub fn get_specifier(&self) -> Result<ModuleSpecifier> {
    ModuleSpecifier::parse(self.specifier.as_str()).map_err(Error::msg)
  }
}

impl<'a> Default for TransformOptions<'a> {
  fn default() -> Self {
    TransformOptions {
      ascii_only: false,
      emit_assert_for_import_attributes: false,
      inline_sources: true,
      keep_comments: false,
      media_type: MediaType::TypeScript,
      minify: true,
      omit_last_semi: false,
      parse_mode: ParseMode::Program,
      plugin_host: None,
      source_map: SourceMapOption::Inline,
      specifier: "file:///main.js".to_owned(),
      target: EsVersion::latest(),
    }
  }
}

impl<'local> FromJava<'local> for TransformOptions<'local> {
  fn from_java(env: &mut JNIEnv<'local>, obj: &JObject<'_>) -> Result<Box<TransformOptions<'local>>> {
    let java_transform_options = JAVA_TRANSFORM_OPTIONS.get().unwrap();
    let ascii_only = java_transform_options.is_ascii_only(env, obj)?;
    let emit_assert_for_import_attributes = java_transform_options.is_emit_assert_for_import_attributes(env, obj)?;
    let inline_sources = java_transform_options.is_inline_sources(env, obj)?;
    let keep_comments = java_transform_options.is_keep_comments(env, obj)?;
    let java_media_type = java_transform_options.get_media_type(env, obj)?;
    let media_type = *MediaType::from_java(env, &java_media_type)?;
    let minify = java_transform_options.is_minify(env, obj)?;
    let omit_last_semi = java_transform_options.is_omit_last_semi(env, obj)?;
    let java_source_map = java_transform_options.get_source_map(env, obj)?;
    let source_map = *SourceMapOption::from_java(env, &java_source_map)?;
    let java_parse_mode = java_transform_options.get_parse_mode(env, obj)?;
    let parse_mode = *ParseMode::from_java(env, &java_parse_mode)?;
    let java_optional_plugin_host = java_transform_options.get_plugin_host(env, obj)?;
    let plugin_host = java_optional_plugin_host.map(|host| {
      let host = env
        .new_global_ref(host)
        .expect("Failed to create global reference for plugin host");
      PluginHost::new(env, host)
    });
    let specifier = java_transform_options.get_specifier(env, obj)?;
    let specifier = url_to_string(env, &specifier)?;
    let java_target = java_transform_options.get_target(env, obj)?;
    let target = *EsVersion::from_java(env, &java_target)?;
    delete_local_ref!(env, java_media_type);
    delete_local_ref!(env, java_source_map);
    delete_local_ref!(env, java_parse_mode);
    delete_local_ref!(env, java_target);
    Ok(Box::new(TransformOptions {
      ascii_only,
      emit_assert_for_import_attributes,
      inline_sources,
      keep_comments,
      media_type,
      minify,
      omit_last_semi,
      parse_mode,
      plugin_host,
      source_map,
      specifier,
      target,
    }))
  }
}

#[derive(Debug)]
pub struct TranspileOptions<'a> {
  /// Whether to capture ast or not.
  pub capture_ast: bool,
  /// Whether to capture comments or not.
  pub capture_comments: bool,
  /// Whether to capture tokens or not.
  pub capture_tokens: bool,
  /// When emitting a legacy decorator, also emit experimental decorator meta
  /// data.  Defaults to `false`.
  pub emit_metadata: bool,
  /// What to do with import statements that only import types i.e. whether to
  /// remove them (`Remove`), keep them as side-effect imports (`Preserve`)
  /// or error (`Error`). Defaults to `Remove`.
  pub imports_not_used_as_values: ImportsNotUsedAsValues,
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
  /// Whether to keep comments in the output. Defaults to `false`.
  pub keep_comments: bool,
  /// Media type of the source text.
  pub media_type: MediaType,
  /// Module kind.
  pub module_kind: ModuleKind,
  /// Should the code to be parsed as Module or Script,
  pub parse_mode: ParseMode,
  /// AST plugin host.
  pub plugin_host: Option<PluginHost<'a>>,
  /// Should JSX be precompiled into static strings that need to be concatenated
  /// with dynamic content. Defaults to `false`, mutually exclusive with
  /// `transform_jsx`.
  pub precompile_jsx: bool,
  /// List of properties/attributes that should always be treated as
  /// dynamic.
  pub precompile_jsx_dynamic_props: Option<Vec<String>>,
  /// List of elements that should not be precompiled when the JSX precompile
  /// transform is used.
  pub precompile_jsx_skip_elements: Option<Vec<String>>,
  /// Whether to apply swc's scope analysis.
  pub scope_analysis: bool,
  /// How and if source maps should be generated.
  pub source_map: SourceMapOption,
  /// Specifier of the source text.
  pub specifier: String,
  /// Should JSX be transformed. Defaults to `true`.
  pub transform_jsx: bool,
  /// TC39 Decorators Proposal - https://github.com/tc39/proposal-decorators
  pub use_decorators_proposal: bool,
  /// TypeScript experimental decorators.
  pub use_ts_decorators: bool,
  /// Should import declarations be transformed to variable declarations using
  /// a dynamic import. This is useful for import & export declaration support
  /// in script contexts such as the Deno REPL.  Defaults to `false`.
  pub var_decl_imports: bool,
  /// `true` changes type stripping behaviour so that _only_ `type` imports
  /// are stripped.
  pub verbatim_module_syntax: bool,
}

impl<'a> TranspileOptions<'a> {
  pub fn get_specifier(&self) -> Result<ModuleSpecifier> {
    ModuleSpecifier::parse(self.specifier.as_str()).map_err(Error::msg)
  }
}

impl<'a> Default for TranspileOptions<'a> {
  fn default() -> Self {
    TranspileOptions {
      capture_ast: false,
      capture_comments: false,
      capture_tokens: false,
      emit_metadata: false,
      imports_not_used_as_values: ImportsNotUsedAsValues::Remove,
      inline_sources: true,
      jsx_automatic: false,
      jsx_development: false,
      jsx_factory: "React.createElement".into(),
      jsx_fragment_factory: "React.Fragment".into(),
      jsx_import_source: None,
      keep_comments: false,
      media_type: MediaType::TypeScript,
      module_kind: ModuleKind::Auto,
      parse_mode: ParseMode::Program,
      plugin_host: None,
      precompile_jsx: false,
      precompile_jsx_dynamic_props: None,
      precompile_jsx_skip_elements: None,
      scope_analysis: false,
      source_map: SourceMapOption::Inline,
      specifier: "file:///main.js".to_owned(),
      transform_jsx: true,
      var_decl_imports: false,
      verbatim_module_syntax: false,
      use_decorators_proposal: false,
      use_ts_decorators: false,
    }
  }
}

impl<'local> FromJava<'local> for TranspileOptions<'local> {
  fn from_java(env: &mut JNIEnv<'local>, obj: &JObject<'_>) -> Result<Box<TranspileOptions<'local>>> {
    let java_transpile_options = JAVA_TRANSPILE_OPTIONS.get().unwrap();
    let capture_ast = java_transpile_options.is_capture_ast(env, obj)?;
    let capture_comments = java_transpile_options.is_capture_comments(env, obj)?;
    let capture_tokens = java_transpile_options.is_capture_tokens(env, obj)?;
    let emit_metadata = java_transpile_options.is_emit_metadata(env, obj)?;
    let java_imports_not_used_as_values = java_transpile_options.get_imports_not_used_as_values(env, obj)?;
    let imports_not_used_as_values = *ImportsNotUsedAsValues::from_java(env, &java_imports_not_used_as_values)?;
    delete_local_ref!(env, java_imports_not_used_as_values);
    let inline_sources = java_transpile_options.is_inline_sources(env, obj)?;
    let jsx_automatic = java_transpile_options.is_jsx_automatic(env, obj)?;
    let jsx_development = java_transpile_options.is_jsx_development(env, obj)?;
    let jsx_factory = java_transpile_options.get_jsx_factory(env, obj)?;
    let jsx_fragment_factory = java_transpile_options.get_jsx_fragment_factory(env, obj)?;
    let jsx_import_source = java_transpile_options.get_jsx_import_source(env, obj)?;
    let keep_comments = java_transpile_options.is_keep_comments(env, obj)?;
    let java_media_type = java_transpile_options.get_media_type(env, obj)?;
    let media_type = *MediaType::from_java(env, &java_media_type)?;
    delete_local_ref!(env, java_media_type);
    let java_module_kind = java_transpile_options.get_module_kind(env, obj)?;
    let module_kind = *ModuleKind::from_java(env, &java_module_kind)?;
    delete_local_ref!(env, java_module_kind);
    let java_parse_mode = java_transpile_options.get_parse_mode(env, obj)?;
    let parse_mode = *ParseMode::from_java(env, &java_parse_mode)?;
    delete_local_ref!(env, java_parse_mode);
    let java_optional_plugin_host = java_transpile_options.get_plugin_host(env, obj)?;
    let plugin_host = java_optional_plugin_host.as_ref().map(|host| {
      let host = env
        .new_global_ref(host)
        .expect("Failed to create global reference for plugin host");
      PluginHost::new(env, host)
    });
    delete_local_optional_ref!(env, java_optional_plugin_host);
    let precompile_jsx = java_transpile_options.is_precompile_jsx(env, obj)?;
    let java_optional_precompile_jsx_dynamic_props =
      java_transpile_options.get_precompile_jsx_dynamic_props(env, obj)?;
    let precompile_jsx_dynamic_props = if let Some(elements) = java_optional_precompile_jsx_dynamic_props {
      let length = list_size(env, &elements)?;
      let mut results: Vec<String> = Vec::with_capacity(length);
      for i in 0..length {
        let java_item = list_get(env, &elements, i)?;
        let element: Result<String> = jstring_to_string!(env, java_item.as_raw());
        delete_local_ref!(env, java_item);
        results.push(element?);
      }
      delete_local_ref!(env, elements);
      Some(results)
    } else {
      None
    };
    let java_optional_precompile_jsx_skip_elements =
      java_transpile_options.get_precompile_jsx_skip_elements(env, obj)?;
    let precompile_jsx_skip_elements = if let Some(elements) = java_optional_precompile_jsx_skip_elements {
      let length = list_size(env, &elements)?;
      let mut results: Vec<String> = Vec::with_capacity(length);
      for i in 0..length {
        let java_item = list_get(env, &elements, i)?;
        let element: Result<String> = jstring_to_string!(env, java_item.as_raw());
        delete_local_ref!(env, java_item);
        results.push(element?);
      }
      delete_local_ref!(env, elements);
      Some(results)
    } else {
      None
    };
    let scope_analysis = java_transpile_options.is_scope_analysis(env, obj)?;
    let java_source_map = java_transpile_options.get_source_map(env, obj)?;
    let source_map = *SourceMapOption::from_java(env, &java_source_map)?;
    let specifier = java_transpile_options.get_specifier(env, obj)?;
    let specifier = url_to_string(env, &specifier)?;
    let transform_jsx = java_transpile_options.is_transform_jsx(env, obj)?;
    let var_decl_imports = java_transpile_options.is_var_decl_imports(env, obj)?;
    let verbatim_module_syntax = java_transpile_options.is_verbatim_module_syntax(env, obj)?;
    let use_decorators_proposal = java_transpile_options.is_use_decorators_proposal(env, obj)?;
    let use_ts_decorators = java_transpile_options.is_use_ts_decorators(env, obj)?;
    delete_local_ref!(env, java_source_map);
    Ok(Box::new(TranspileOptions {
      capture_ast,
      capture_comments,
      capture_tokens,
      emit_metadata,
      imports_not_used_as_values,
      inline_sources,
      jsx_automatic,
      jsx_development,
      jsx_factory,
      jsx_fragment_factory,
      jsx_import_source,
      keep_comments,
      media_type,
      module_kind,
      parse_mode,
      plugin_host,
      precompile_jsx,
      precompile_jsx_dynamic_props,
      precompile_jsx_skip_elements,
      scope_analysis,
      source_map,
      specifier,
      transform_jsx,
      var_decl_imports,
      verbatim_module_syntax,
      use_decorators_proposal,
      use_ts_decorators,
    }))
  }
}
