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

use std::sync::OnceLock;

use anyhow::{Error, Result};
use deno_ast::{
  DecoratorsTranspileOption, JsxAutomaticOptions, JsxClassicOptions, JsxPrecompileOptions, JsxRuntime, ModuleSpecifier,
};
use jni::objects::{Global, JClass, JMethodID, JObject, JString};
use jni::strings::JNIString;
use jni::signature::RuntimeMethodSignature;
use jni::Env;

use crate::enums::*;
use crate::jni_utils::*;
use crate::plugin_utils::PluginHost;

/* JavaSwc4jParseOptions Begin */
#[allow(dead_code)]
struct JavaSwc4jParseOptions {
  class: Global<JClass<'static>>,
  method_get_media_type: JMethodID,
  method_get_parse_mode: JMethodID,
  method_get_plugin_host: JMethodID,
  method_get_specifier: JMethodID,
  method_is_capture_ast: JMethodID,
  method_is_capture_comments: JMethodID,
  method_is_capture_tokens: JMethodID,
  method_is_scope_analysis: JMethodID,
}

#[allow(dead_code)]
impl JavaSwc4jParseOptions {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jParseOptions"))
      .expect("Couldn't find class Swc4jParseOptions");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jParseOptions");
    let method_get_media_type = env
      .get_method_id(
        &class,
        JNIString::from("getMediaType"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jParseOptions.getMediaType");
    let method_get_parse_mode = env
      .get_method_id(
        &class,
        JNIString::from("getParseMode"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jParseOptions.getParseMode");
    let method_get_plugin_host = env
      .get_method_id(
        &class,
        JNIString::from("getPluginHost"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/plugins/ISwc4jPluginHost;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jParseOptions.getPluginHost");
    let method_get_specifier = env
      .get_method_id(
        &class,
        JNIString::from("getSpecifier"),
        RuntimeMethodSignature::from_str("()Ljava/net/URL;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jParseOptions.getSpecifier");
    let method_is_capture_ast = env
      .get_method_id(
        &class,
        JNIString::from("isCaptureAst"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jParseOptions.isCaptureAst");
    let method_is_capture_comments = env
      .get_method_id(
        &class,
        JNIString::from("isCaptureComments"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jParseOptions.isCaptureComments");
    let method_is_capture_tokens = env
      .get_method_id(
        &class,
        JNIString::from("isCaptureTokens"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jParseOptions.isCaptureTokens");
    let method_is_scope_analysis = env
      .get_method_id(
        &class,
        JNIString::from("isScopeAnalysis"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
  class: Global<JClass<'static>>,
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

#[allow(dead_code)]
impl JavaSwc4jTransformOptions {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jTransformOptions"))
      .expect("Couldn't find class Swc4jTransformOptions");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTransformOptions");
    let method_get_media_type = env
      .get_method_id(
        &class,
        JNIString::from("getMediaType"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.getMediaType");
    let method_get_parse_mode = env
      .get_method_id(
        &class,
        JNIString::from("getParseMode"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.getParseMode");
    let method_get_plugin_host = env
      .get_method_id(
        &class,
        JNIString::from("getPluginHost"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/plugins/ISwc4jPluginHost;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.getPluginHost");
    let method_get_source_map = env
      .get_method_id(
        &class,
        JNIString::from("getSourceMap"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jSourceMapOption;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.getSourceMap");
    let method_get_specifier = env
      .get_method_id(
        &class,
        JNIString::from("getSpecifier"),
        RuntimeMethodSignature::from_str("()Ljava/net/URL;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.getSpecifier");
    let method_get_target = env
      .get_method_id(
        &class,
        JNIString::from("getTarget"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jEsVersion;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.getTarget");
    let method_is_ascii_only = env
      .get_method_id(
        &class,
        JNIString::from("isAsciiOnly"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.isAsciiOnly");
    let method_is_emit_assert_for_import_attributes = env
      .get_method_id(
        &class,
        JNIString::from("isEmitAssertForImportAttributes"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.isEmitAssertForImportAttributes");
    let method_is_inline_sources = env
      .get_method_id(
        &class,
        JNIString::from("isInlineSources"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.isInlineSources");
    let method_is_keep_comments = env
      .get_method_id(
        &class,
        JNIString::from("isKeepComments"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.isKeepComments");
    let method_is_minify = env
      .get_method_id(
        &class,
        JNIString::from("isMinify"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTransformOptions.isMinify");
    let method_is_omit_last_semi = env
      .get_method_id(
        &class,
        JNIString::from("isOmitLastSemi"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
  class: Global<JClass<'static>>,
  method_get_decorators: JMethodID,
  method_get_imports_not_used_as_values: JMethodID,
  method_get_jsx: JMethodID,
  method_get_media_type: JMethodID,
  method_get_module_kind: JMethodID,
  method_get_parse_mode: JMethodID,
  method_get_plugin_host: JMethodID,
  method_get_source_map: JMethodID,
  method_get_specifier: JMethodID,
  method_is_capture_ast: JMethodID,
  method_is_capture_comments: JMethodID,
  method_is_capture_tokens: JMethodID,
  method_is_inline_sources: JMethodID,
  method_is_keep_comments: JMethodID,
  method_is_scope_analysis: JMethodID,
  method_is_var_decl_imports: JMethodID,
  method_is_verbatim_module_syntax: JMethodID,
}

#[allow(dead_code)]
impl JavaSwc4jTranspileOptions {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jTranspileOptions"))
      .expect("Couldn't find class Swc4jTranspileOptions");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTranspileOptions");
    let method_get_decorators = env
      .get_method_id(
        &class,
        JNIString::from("getDecorators"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/options/Swc4jDecoratorsTranspileOption;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getDecorators");
    let method_get_imports_not_used_as_values = env
      .get_method_id(
        &class,
        JNIString::from("getImportsNotUsedAsValues"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jImportsNotUsedAsValues;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getImportsNotUsedAsValues");
    let method_get_jsx = env
      .get_method_id(
        &class,
        JNIString::from("getJsx"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/options/Swc4jJsxRuntimeOption;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getJsx");
    let method_get_media_type = env
      .get_method_id(
        &class,
        JNIString::from("getMediaType"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getMediaType");
    let method_get_module_kind = env
      .get_method_id(
        &class,
        JNIString::from("getModuleKind"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jModuleKind;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getModuleKind");
    let method_get_parse_mode = env
      .get_method_id(
        &class,
        JNIString::from("getParseMode"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getParseMode");
    let method_get_plugin_host = env
      .get_method_id(
        &class,
        JNIString::from("getPluginHost"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/plugins/ISwc4jPluginHost;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getPluginHost");
    let method_get_source_map = env
      .get_method_id(
        &class,
        JNIString::from("getSourceMap"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/enums/Swc4jSourceMapOption;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getSourceMap");
    let method_get_specifier = env
      .get_method_id(
        &class,
        JNIString::from("getSpecifier"),
        RuntimeMethodSignature::from_str("()Ljava/net/URL;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.getSpecifier");
    let method_is_capture_ast = env
      .get_method_id(
        &class,
        JNIString::from("isCaptureAst"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isCaptureAst");
    let method_is_capture_comments = env
      .get_method_id(
        &class,
        JNIString::from("isCaptureComments"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isCaptureComments");
    let method_is_capture_tokens = env
      .get_method_id(
        &class,
        JNIString::from("isCaptureTokens"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isCaptureTokens");
    let method_is_inline_sources = env
      .get_method_id(
        &class,
        JNIString::from("isInlineSources"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isInlineSources");
    let method_is_keep_comments = env
      .get_method_id(
        &class,
        JNIString::from("isKeepComments"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isKeepComments");
    let method_is_scope_analysis = env
      .get_method_id(
        &class,
        JNIString::from("isScopeAnalysis"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isScopeAnalysis");
    let method_is_var_decl_imports = env
      .get_method_id(
        &class,
        JNIString::from("isVarDeclImports"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isVarDeclImports");
    let method_is_verbatim_module_syntax = env
      .get_method_id(
        &class,
        JNIString::from("isVerbatimModuleSyntax"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jTranspileOptions.isVerbatimModuleSyntax");
    JavaSwc4jTranspileOptions {
      class,
      method_get_decorators,
      method_get_imports_not_used_as_values,
      method_get_jsx,
      method_get_media_type,
      method_get_module_kind,
      method_get_parse_mode,
      method_get_plugin_host,
      method_get_source_map,
      method_get_specifier,
      method_is_capture_ast,
      method_is_capture_comments,
      method_is_capture_tokens,
      method_is_inline_sources,
      method_is_keep_comments,
      method_is_scope_analysis,
      method_is_var_decl_imports,
      method_is_verbatim_module_syntax,
    }
  }

  pub fn get_decorators<'local, 'a>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_decorators,
        &[],
        "Swc4jDecoratorsTranspileOption get_decorators()"
      )?;
    Ok(return_value)
  }

  pub fn get_imports_not_used_as_values<'local, 'a>(
    &self,
    env: &mut Env<'local>,
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

  pub fn get_jsx<'local, 'a>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_jsx,
        &[],
        "Swc4jJsxRuntimeOption get_jsx()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }

  pub fn get_media_type<'local, 'a>(
    &self,
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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

  pub fn is_inline_sources<'local>(
    &self,
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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

  pub fn is_scope_analysis<'local>(
    &self,
    env: &mut Env<'local>,
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

  pub fn is_var_decl_imports<'local>(
    &self,
    env: &mut Env<'local>,
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
    env: &mut Env<'local>,
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

/* JavaSwc4jDecoratorsTranspileOptionNone Begin */
#[allow(dead_code)]
struct JavaSwc4jDecoratorsTranspileOptionNone {
  class: Global<JClass<'static>>,
}

#[allow(dead_code)]
impl JavaSwc4jDecoratorsTranspileOptionNone {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jDecoratorsTranspileOptionNone"))
      .expect("Couldn't find class Swc4jDecoratorsTranspileOptionNone");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jDecoratorsTranspileOptionNone");
    JavaSwc4jDecoratorsTranspileOptionNone {
      class,
    }
  }
}
/* JavaSwc4jDecoratorsTranspileOptionNone End */

/* JavaSwc4jDecoratorsTranspileOptionEcma Begin */
#[allow(dead_code)]
struct JavaSwc4jDecoratorsTranspileOptionEcma {
  class: Global<JClass<'static>>,
}

#[allow(dead_code)]
impl JavaSwc4jDecoratorsTranspileOptionEcma {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jDecoratorsTranspileOptionEcma"))
      .expect("Couldn't find class Swc4jDecoratorsTranspileOptionEcma");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jDecoratorsTranspileOptionEcma");
    JavaSwc4jDecoratorsTranspileOptionEcma {
      class,
    }
  }
}
/* JavaSwc4jDecoratorsTranspileOptionEcma End */

/* JavaSwc4jDecoratorsTranspileOptionLegacyTypeScript Begin */
#[allow(dead_code)]
struct JavaSwc4jDecoratorsTranspileOptionLegacyTypeScript {
  class: Global<JClass<'static>>,
  method_is_emit_metadata: JMethodID,
}

#[allow(dead_code)]
impl JavaSwc4jDecoratorsTranspileOptionLegacyTypeScript {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jDecoratorsTranspileOptionLegacyTypeScript"))
      .expect("Couldn't find class Swc4jDecoratorsTranspileOptionLegacyTypeScript");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jDecoratorsTranspileOptionLegacyTypeScript");
    let method_is_emit_metadata = env
      .get_method_id(
        &class,
        JNIString::from("isEmitMetadata"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jDecoratorsTranspileOptionLegacyTypeScript.isEmitMetadata");
    JavaSwc4jDecoratorsTranspileOptionLegacyTypeScript {
      class,
      method_is_emit_metadata,
    }
  }

  pub fn is_emit_metadata<'local>(
    &self,
    env: &mut Env<'local>,
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
}
/* JavaSwc4jDecoratorsTranspileOptionLegacyTypeScript End */

/* JavaSwc4jJsxRuntimeOptionAutomatic Begin */
#[allow(dead_code)]
struct JavaSwc4jJsxRuntimeOptionAutomatic {
  class: Global<JClass<'static>>,
  method_get_import_source: JMethodID,
  method_is_development: JMethodID,
}

#[allow(dead_code)]
impl JavaSwc4jJsxRuntimeOptionAutomatic {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jJsxRuntimeOptionAutomatic"))
      .expect("Couldn't find class Swc4jJsxRuntimeOptionAutomatic");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jJsxRuntimeOptionAutomatic");
    let method_get_import_source = env
      .get_method_id(
        &class,
        JNIString::from("getImportSource"),
        RuntimeMethodSignature::from_str("()Ljava/lang/String;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jJsxRuntimeOptionAutomatic.getImportSource");
    let method_is_development = env
      .get_method_id(
        &class,
        JNIString::from("isDevelopment"),
        RuntimeMethodSignature::from_str("()Z").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jJsxRuntimeOptionAutomatic.isDevelopment");
    JavaSwc4jJsxRuntimeOptionAutomatic {
      class,
      method_get_import_source,
      method_is_development,
    }
  }

  pub fn get_import_source<'local>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<String>>
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_import_source,
        &[],
        "String get_import_source()"
      )?;
    let java_return_value = return_value;
    let return_value = jstring_to_optional_string!(env, java_return_value.as_raw())?;
    delete_local_ref!(env, java_return_value);
    Ok(return_value)
  }

  pub fn is_development<'local>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<bool>
  {
    let return_value = call_as_boolean!(
        env,
        obj,
        self.method_is_development,
        &[],
        "boolean is_development()"
      )?;
    Ok(return_value)
  }
}
/* JavaSwc4jJsxRuntimeOptionAutomatic End */

/* JavaSwc4jJsxRuntimeOptionClassic Begin */
#[allow(dead_code)]
struct JavaSwc4jJsxRuntimeOptionClassic {
  class: Global<JClass<'static>>,
  method_get_factory: JMethodID,
  method_get_fragment_factory: JMethodID,
}

#[allow(dead_code)]
impl JavaSwc4jJsxRuntimeOptionClassic {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jJsxRuntimeOptionClassic"))
      .expect("Couldn't find class Swc4jJsxRuntimeOptionClassic");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jJsxRuntimeOptionClassic");
    let method_get_factory = env
      .get_method_id(
        &class,
        JNIString::from("getFactory"),
        RuntimeMethodSignature::from_str("()Ljava/lang/String;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jJsxRuntimeOptionClassic.getFactory");
    let method_get_fragment_factory = env
      .get_method_id(
        &class,
        JNIString::from("getFragmentFactory"),
        RuntimeMethodSignature::from_str("()Ljava/lang/String;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jJsxRuntimeOptionClassic.getFragmentFactory");
    JavaSwc4jJsxRuntimeOptionClassic {
      class,
      method_get_factory,
      method_get_fragment_factory,
    }
  }

  pub fn get_factory<'local>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<String>
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_factory,
        &[],
        "String get_factory()"
      )?;
    let java_return_value = return_value;
    let return_value: Result<String> = jstring_to_string!(env, java_return_value.as_raw());
    let return_value = return_value?;
    delete_local_ref!(env, java_return_value);
    Ok(return_value)
  }

  pub fn get_fragment_factory<'local>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<String>
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_fragment_factory,
        &[],
        "String get_fragment_factory()"
      )?;
    let java_return_value = return_value;
    let return_value: Result<String> = jstring_to_string!(env, java_return_value.as_raw());
    let return_value = return_value?;
    delete_local_ref!(env, java_return_value);
    Ok(return_value)
  }
}
/* JavaSwc4jJsxRuntimeOptionClassic End */

/* JavaSwc4jJsxRuntimeOptionPrecompile Begin */
#[allow(dead_code)]
struct JavaSwc4jJsxRuntimeOptionPrecompile {
  class: Global<JClass<'static>>,
  method_get_automatic: JMethodID,
  method_get_dynamic_props: JMethodID,
  method_get_skip_elements: JMethodID,
}

#[allow(dead_code)]
impl JavaSwc4jJsxRuntimeOptionPrecompile {
  pub fn new<'local>(env: &mut Env<'local>) -> Self {
    let class = env
      .find_class(JNIString::from("com/caoccao/javet/swc4j/options/Swc4jJsxRuntimeOptionPrecompile"))
      .expect("Couldn't find class Swc4jJsxRuntimeOptionPrecompile");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jJsxRuntimeOptionPrecompile");
    let method_get_automatic = env
      .get_method_id(
        &class,
        JNIString::from("getAutomatic"),
        RuntimeMethodSignature::from_str("()Lcom/caoccao/javet/swc4j/options/Swc4jJsxRuntimeOptionAutomatic;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jJsxRuntimeOptionPrecompile.getAutomatic");
    let method_get_dynamic_props = env
      .get_method_id(
        &class,
        JNIString::from("getDynamicProps"),
        RuntimeMethodSignature::from_str("()Ljava/util/List;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jJsxRuntimeOptionPrecompile.getDynamicProps");
    let method_get_skip_elements = env
      .get_method_id(
        &class,
        JNIString::from("getSkipElements"),
        RuntimeMethodSignature::from_str("()Ljava/util/List;").unwrap().method_signature(),
      )
      .expect("Couldn't find method Swc4jJsxRuntimeOptionPrecompile.getSkipElements");
    JavaSwc4jJsxRuntimeOptionPrecompile {
      class,
      method_get_automatic,
      method_get_dynamic_props,
      method_get_skip_elements,
    }
  }

  pub fn get_automatic<'local, 'a>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_automatic,
        &[],
        "Swc4jJsxRuntimeOptionAutomatic get_automatic()"
      )?;
    Ok(return_value)
  }

  pub fn get_dynamic_props<'local, 'a>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_dynamic_props,
        &[],
        "List get_dynamic_props()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }

  pub fn get_skip_elements<'local, 'a>(
    &self,
    env: &mut Env<'local>,
    obj: &JObject<'_>,
  ) -> Result<Option<JObject<'a>>>
  where
    'local: 'a,
  {
    let return_value = call_as_object!(
        env,
        obj,
        self.method_get_skip_elements,
        &[],
        "List get_skip_elements()"
      )?;
    let return_value = if return_value.is_null() {
      None
    } else {
      Some(return_value)
    };
    Ok(return_value)
  }
}
/* JavaSwc4jJsxRuntimeOptionPrecompile End */

static JAVA_PARSE_OPTIONS: OnceLock<JavaSwc4jParseOptions> = OnceLock::new();
static JAVA_TRANSFORM_OPTIONS: OnceLock<JavaSwc4jTransformOptions> = OnceLock::new();
static JAVA_TRANSPILE_OPTIONS: OnceLock<JavaSwc4jTranspileOptions> = OnceLock::new();
static JAVA_DECORATORS_TRANSPILE_OPTION_NONE: OnceLock<JavaSwc4jDecoratorsTranspileOptionNone> = OnceLock::new();
static JAVA_DECORATORS_TRANSPILE_OPTION_ECMA: OnceLock<JavaSwc4jDecoratorsTranspileOptionEcma> = OnceLock::new();
static JAVA_DECORATORS_TRANSPILE_OPTION_LEGACY_TYPE_SCRIPT: OnceLock<
  JavaSwc4jDecoratorsTranspileOptionLegacyTypeScript,
> = OnceLock::new();
static JAVA_JSX_RUNTIME_OPTION_AUTOMATIC: OnceLock<JavaSwc4jJsxRuntimeOptionAutomatic> = OnceLock::new();
static JAVA_JSX_RUNTIME_OPTION_CLASSIC: OnceLock<JavaSwc4jJsxRuntimeOptionClassic> = OnceLock::new();
static JAVA_JSX_RUNTIME_OPTION_PRECOMPILE: OnceLock<JavaSwc4jJsxRuntimeOptionPrecompile> = OnceLock::new();

pub fn init<'local>(env: &mut Env<'local>) {
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
    JAVA_DECORATORS_TRANSPILE_OPTION_NONE
      .set(JavaSwc4jDecoratorsTranspileOptionNone::new(env))
      .unwrap_unchecked();
    JAVA_DECORATORS_TRANSPILE_OPTION_ECMA
      .set(JavaSwc4jDecoratorsTranspileOptionEcma::new(env))
      .unwrap_unchecked();
    JAVA_DECORATORS_TRANSPILE_OPTION_LEGACY_TYPE_SCRIPT
      .set(JavaSwc4jDecoratorsTranspileOptionLegacyTypeScript::new(env))
      .unwrap_unchecked();
    JAVA_JSX_RUNTIME_OPTION_AUTOMATIC
      .set(JavaSwc4jJsxRuntimeOptionAutomatic::new(env))
      .unwrap_unchecked();
    JAVA_JSX_RUNTIME_OPTION_CLASSIC
      .set(JavaSwc4jJsxRuntimeOptionClassic::new(env))
      .unwrap_unchecked();
    JAVA_JSX_RUNTIME_OPTION_PRECOMPILE
      .set(JavaSwc4jJsxRuntimeOptionPrecompile::new(env))
      .unwrap_unchecked();
  }
}

#[derive(Debug)]
pub struct ParseOptions {
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
  pub plugin_host: Option<PluginHost>,
  /// Whether to apply swc's scope analysis.
  pub scope_analysis: bool,
  /// Specifier of the source text.
  pub specifier: String,
}

impl ParseOptions {
  pub fn get_specifier(&self) -> Result<ModuleSpecifier> {
    ModuleSpecifier::parse(self.specifier.as_str()).map_err(Error::msg)
  }
}

impl Default for ParseOptions {
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

impl<'local> FromJava<'local> for ParseOptions {
  fn from_java(env: &mut Env<'local>, obj: &JObject<'_>) -> Result<Box<ParseOptions>> {
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
      PluginHost::new(host)
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
pub struct TransformOptions {
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
  pub plugin_host: Option<PluginHost>,
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

impl TransformOptions {
  pub fn get_specifier(&self) -> Result<ModuleSpecifier> {
    ModuleSpecifier::parse(self.specifier.as_str()).map_err(Error::msg)
  }
}

impl Default for TransformOptions {
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

impl<'local> FromJava<'local> for TransformOptions {
  fn from_java(env: &mut Env<'local>, obj: &JObject<'_>) -> Result<Box<TransformOptions>> {
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
      PluginHost::new(host)
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
pub struct TranspileOptions {
  /// Whether to capture ast or not.
  pub capture_ast: bool,
  /// Whether to capture comments or not.
  pub capture_comments: bool,
  /// Whether to capture tokens or not.
  pub capture_tokens: bool,
  /// Kind of decorators to use.
  pub decorators: DecoratorsTranspileOption,
  /// What to do with import statements that only import types i.e. whether to
  /// remove them (`Remove`), keep them as side-effect imports (`Preserve`)
  /// or error (`Error`). Defaults to `Remove`.
  pub imports_not_used_as_values: ImportsNotUsedAsValues,
  /// Should the sources be inlined in the source map.  Defaults to `true`.
  pub inline_sources: bool,
  /// Options for transforming JSX. Will not transform when `None`.
  pub jsx: Option<JsxRuntime>,
  /// Whether to keep comments in the output. Defaults to `false`.
  pub keep_comments: bool,
  /// Media type of the source text.
  pub media_type: MediaType,
  /// Module kind.
  pub module_kind: ModuleKind,
  /// Should the code to be parsed as Module or Script,
  pub parse_mode: ParseMode,
  /// AST plugin host.
  pub plugin_host: Option<PluginHost>,
  /// Whether to apply swc's scope analysis.
  pub scope_analysis: bool,
  /// How and if source maps should be generated.
  pub source_map: SourceMapOption,
  /// Specifier of the source text.
  pub specifier: String,
  /// Should import declarations be transformed to variable declarations using
  /// a dynamic import. This is useful for import & export declaration support
  /// in script contexts such as the Deno REPL.  Defaults to `false`.
  pub var_decl_imports: bool,
  /// `true` changes type stripping behaviour so that _only_ `type` imports
  /// are stripped.
  pub verbatim_module_syntax: bool,
}

impl TranspileOptions {
  pub fn get_specifier(&self) -> Result<ModuleSpecifier> {
    ModuleSpecifier::parse(self.specifier.as_str()).map_err(Error::msg)
  }
}

impl Default for TranspileOptions {
  fn default() -> Self {
    TranspileOptions {
      capture_ast: false,
      capture_comments: false,
      capture_tokens: false,
      decorators: DecoratorsTranspileOption::None,
      imports_not_used_as_values: ImportsNotUsedAsValues::Remove,
      inline_sources: true,
      jsx: None,
      keep_comments: false,
      media_type: MediaType::TypeScript,
      module_kind: ModuleKind::Auto,
      parse_mode: ParseMode::Program,
      plugin_host: None,
      scope_analysis: false,
      source_map: SourceMapOption::Inline,
      specifier: "file:///main.js".to_owned(),
      var_decl_imports: false,
      verbatim_module_syntax: false,
    }
  }
}

impl<'local> FromJava<'local> for TranspileOptions {
  fn from_java(env: &mut Env<'local>, obj: &JObject<'_>) -> Result<Box<TranspileOptions>> {
    let java_transpile_options = JAVA_TRANSPILE_OPTIONS.get().unwrap();
    let java_decorators = java_transpile_options.get_decorators(env, obj)?;
    let java_decorators_transpile_option_ecma = JAVA_DECORATORS_TRANSPILE_OPTION_ECMA.get().unwrap();
    let java_decorators_transpile_option_legacy_type_script =
      JAVA_DECORATORS_TRANSPILE_OPTION_LEGACY_TYPE_SCRIPT.get().unwrap();
    let decorators = if env
      .is_instance_of(&java_decorators, &(java_decorators_transpile_option_ecma.class))
      .unwrap_or(false)
    {
      DecoratorsTranspileOption::Ecma
    } else if env
      .is_instance_of(
        &java_decorators,
        &(java_decorators_transpile_option_legacy_type_script.class),
      )
      .unwrap_or(false)
    {
      let emit_metadata =
        java_decorators_transpile_option_legacy_type_script.is_emit_metadata(env, &java_decorators)?;
      DecoratorsTranspileOption::LegacyTypeScript { emit_metadata }
    } else {
      DecoratorsTranspileOption::None
    };
    delete_local_ref!(env, java_decorators);
    let capture_ast = java_transpile_options.is_capture_ast(env, obj)?;
    let capture_comments = java_transpile_options.is_capture_comments(env, obj)?;
    let capture_tokens = java_transpile_options.is_capture_tokens(env, obj)?;
    let java_imports_not_used_as_values = java_transpile_options.get_imports_not_used_as_values(env, obj)?;
    let imports_not_used_as_values = *ImportsNotUsedAsValues::from_java(env, &java_imports_not_used_as_values)?;
    delete_local_ref!(env, java_imports_not_used_as_values);
    let inline_sources = java_transpile_options.is_inline_sources(env, obj)?;
    let java_jsx_runtime_option_automatic = JAVA_JSX_RUNTIME_OPTION_AUTOMATIC.get().unwrap();
    let java_jsx_runtime_option_classic = JAVA_JSX_RUNTIME_OPTION_CLASSIC.get().unwrap();
    let java_jsx_runtime_option_precompile = JAVA_JSX_RUNTIME_OPTION_PRECOMPILE.get().unwrap();
    let optional_java_jsx = java_transpile_options.get_jsx(env, obj)?;
    let mut jsx: Option<JsxRuntime> = None;
    if let Some(java_jsx) = optional_java_jsx {
      if env.is_instance_of(&java_jsx, &(java_jsx_runtime_option_automatic.class))? {
        let development = java_jsx_runtime_option_automatic.is_development(env, &java_jsx)?;
        let import_source = java_jsx_runtime_option_automatic.get_import_source(env, &java_jsx)?;
        jsx = Some(JsxRuntime::Automatic(JsxAutomaticOptions {
          development,
          import_source,
        }));
      } else if env.is_instance_of(&java_jsx, &(java_jsx_runtime_option_classic.class))? {
        let factory = java_jsx_runtime_option_classic.get_factory(env, &java_jsx)?;
        let fragment_factory = java_jsx_runtime_option_classic.get_fragment_factory(env, &java_jsx)?;
        jsx = Some(JsxRuntime::Classic(JsxClassicOptions {
          factory,
          fragment_factory,
        }));
      } else if env.is_instance_of(&java_jsx, &(java_jsx_runtime_option_precompile.class))? {
        let java_automatic = java_jsx_runtime_option_precompile.get_automatic(env, &java_jsx)?;
        let development = java_jsx_runtime_option_automatic.is_development(env, &java_automatic)?;
        let import_source = java_jsx_runtime_option_automatic.get_import_source(env, &java_automatic)?;
        delete_local_ref!(env, java_automatic);
        let automatic = JsxAutomaticOptions {
          development,
          import_source,
        };
        let optional_java_dynamic_props = java_jsx_runtime_option_precompile.get_dynamic_props(env, &java_jsx)?;
        let mut dynamic_props = None;
        if let Some(java_dynamic_props) = optional_java_dynamic_props {
          let length = list_size(env, &java_dynamic_props)?;
          let mut results: Vec<String> = Vec::with_capacity(length);
          for i in 0..length {
            let java_item = list_get(env, &java_dynamic_props, i)?;
            let item: Result<String> = jstring_to_string!(env, java_item.as_raw());
            results.push(item?);
            delete_local_ref!(env, java_item);
          }
          dynamic_props = Some(results);
          delete_local_ref!(env, java_dynamic_props);
        };
        let optional_java_skip_elements = java_jsx_runtime_option_precompile.get_skip_elements(env, &java_jsx)?;
        let mut skip_elements = None;
        if let Some(java_skip_elements) = optional_java_skip_elements {
          let length = list_size(env, &java_skip_elements)?;
          let mut results: Vec<String> = Vec::with_capacity(length);
          for i in 0..length {
            let java_item = list_get(env, &java_skip_elements, i)?;
            let item: Result<String> = jstring_to_string!(env, java_item.as_raw());
            results.push(item?);
            delete_local_ref!(env, java_item);
          }
          skip_elements = Some(results);
          delete_local_ref!(env, java_skip_elements);
        }
        jsx = Some(JsxRuntime::Precompile(JsxPrecompileOptions {
          automatic,
          dynamic_props,
          skip_elements,
        }));
      };
      delete_local_ref!(env, java_jsx);
    }
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
      PluginHost::new(host)
    });
    delete_local_optional_ref!(env, java_optional_plugin_host);
    let scope_analysis = java_transpile_options.is_scope_analysis(env, obj)?;
    let java_source_map = java_transpile_options.get_source_map(env, obj)?;
    let source_map = *SourceMapOption::from_java(env, &java_source_map)?;
    let specifier = java_transpile_options.get_specifier(env, obj)?;
    let specifier = url_to_string(env, &specifier)?;
    let var_decl_imports = java_transpile_options.is_var_decl_imports(env, obj)?;
    let verbatim_module_syntax = java_transpile_options.is_verbatim_module_syntax(env, obj)?;
    delete_local_ref!(env, java_source_map);
    Ok(Box::new(TranspileOptions {
      capture_ast,
      capture_comments,
      capture_tokens,
      decorators,
      imports_not_used_as_values,
      inline_sources,
      jsx,
      keep_comments,
      media_type,
      module_kind,
      parse_mode,
      plugin_host,
      scope_analysis,
      source_map,
      specifier,
      var_decl_imports,
      verbatim_module_syntax,
    }))
  }
}
