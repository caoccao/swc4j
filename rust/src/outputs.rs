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
use deno_ast::swc::ast::*;
use deno_ast::swc::parser::token::TokenAndSpan;
use deno_ast::{MultiThreadedComments, ParsedSource, TranspileResult};
use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::JNIEnv;

use std::sync::Arc;

use crate::comment_utils::*;
use crate::enums::*;
use crate::jni_utils::*;
use crate::options::*;
use crate::span_utils::{ByteToIndexMap, RegisterWithMap, ToJavaWithMap};
use crate::token_utils;

/* JavaSwc4jParseOutput Begin */
#[allow(dead_code)]
struct JavaSwc4jParseOutput {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jParseOutput {}
unsafe impl Sync for JavaSwc4jParseOutput {}

#[allow(dead_code)]
impl JavaSwc4jParseOutput {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/outputs/Swc4jParseOutput")
      .expect("Couldn't find class Swc4jParseOutput");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jParseOutput");
    let method_construct = env
      .get_method_id(
        &class,
        "<init>",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstProgram;Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;Ljava/lang/String;Ljava/util/List;Lcom/caoccao/javet/swc4j/comments/Swc4jComments;)V",
      )
      .expect("Couldn't find method Swc4jParseOutput::new");
    JavaSwc4jParseOutput {
      class,
      method_construct,
    }
  }

  pub fn construct<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    program: &JObject<'_>,
    media_type: &JObject<'_>,
    parse_mode: &JObject<'_>,
    source_text: &str,
    tokens: &JObject<'_>,
    comments: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let program = object_to_jvalue!(program);
    let media_type = object_to_jvalue!(media_type);
    let parse_mode = object_to_jvalue!(parse_mode);
    let java_source_text = string_to_jstring!(env, &source_text);
    let source_text = object_to_jvalue!(java_source_text);
    let tokens = object_to_jvalue!(tokens);
    let comments = object_to_jvalue!(comments);
    let return_value = call_as_construct!(
        env,
        &self.class,
        self.method_construct,
        &[program, media_type, parse_mode, source_text, tokens, comments],
        "Swc4jParseOutput construct()"
      )?;
    delete_local_ref!(env, java_source_text);
    Ok(return_value)
  }
}
/* JavaSwc4jParseOutput End */

/* JavaSwc4jTransformOutput Begin */
#[allow(dead_code)]
struct JavaSwc4jTransformOutput {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jTransformOutput {}
unsafe impl Sync for JavaSwc4jTransformOutput {}

#[allow(dead_code)]
impl JavaSwc4jTransformOutput {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/outputs/Swc4jTransformOutput")
      .expect("Couldn't find class Swc4jTransformOutput");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTransformOutput");
    let method_construct = env
      .get_method_id(
        &class,
        "<init>",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;Ljava/lang/String;)V",
      )
      .expect("Couldn't find method Swc4jTransformOutput::new");
    JavaSwc4jTransformOutput {
      class,
      method_construct,
    }
  }

  pub fn construct<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    code: &str,
    media_type: &JObject<'_>,
    parse_mode: &JObject<'_>,
    source_map: &Option<String>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let java_code = string_to_jstring!(env, &code);
    let code = object_to_jvalue!(java_code);
    let media_type = object_to_jvalue!(media_type);
    let parse_mode = object_to_jvalue!(parse_mode);
    let java_source_map = optional_string_to_jstring!(env, &source_map);
    let source_map = object_to_jvalue!(java_source_map);
    let return_value = call_as_construct!(
        env,
        &self.class,
        self.method_construct,
        &[code, media_type, parse_mode, source_map],
        "Swc4jTransformOutput construct()"
      )?;
    delete_local_ref!(env, java_code);
    delete_local_ref!(env, java_source_map);
    Ok(return_value)
  }
}
/* JavaSwc4jTransformOutput End */

/* JavaSwc4jTranspileOutput Begin */
#[allow(dead_code)]
struct JavaSwc4jTranspileOutput {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jTranspileOutput {}
unsafe impl Sync for JavaSwc4jTranspileOutput {}

#[allow(dead_code)]
impl JavaSwc4jTranspileOutput {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/outputs/Swc4jTranspileOutput")
      .expect("Couldn't find class Swc4jTranspileOutput");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTranspileOutput");
    let method_construct = env
      .get_method_id(
        &class,
        "<init>",
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstProgram;Ljava/lang/String;Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lcom/caoccao/javet/swc4j/comments/Swc4jComments;)V",
      )
      .expect("Couldn't find method Swc4jTranspileOutput::new");
    JavaSwc4jTranspileOutput {
      class,
      method_construct,
    }
  }

  pub fn construct<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    program: &JObject<'_>,
    code: &str,
    media_type: &JObject<'_>,
    parse_mode: &JObject<'_>,
    source_map: &Option<String>,
    source_text: &str,
    tokens: &JObject<'_>,
    comments: &JObject<'_>,
  ) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let program = object_to_jvalue!(program);
    let java_code = string_to_jstring!(env, &code);
    let code = object_to_jvalue!(java_code);
    let media_type = object_to_jvalue!(media_type);
    let parse_mode = object_to_jvalue!(parse_mode);
    let java_source_map = optional_string_to_jstring!(env, &source_map);
    let source_map = object_to_jvalue!(java_source_map);
    let java_source_text = string_to_jstring!(env, &source_text);
    let source_text = object_to_jvalue!(java_source_text);
    let tokens = object_to_jvalue!(tokens);
    let comments = object_to_jvalue!(comments);
    let return_value = call_as_construct!(
        env,
        &self.class,
        self.method_construct,
        &[program, code, media_type, parse_mode, source_map, source_text, tokens, comments],
        "Swc4jTranspileOutput construct()"
      )?;
    delete_local_ref!(env, java_code);
    delete_local_ref!(env, java_source_map);
    delete_local_ref!(env, java_source_text);
    Ok(return_value)
  }
}
/* JavaSwc4jTranspileOutput End */

static mut JAVA_PARSE_OUTPUT: Option<JavaSwc4jParseOutput> = None;
static mut JAVA_TRANSFORM_OUTPUT: Option<JavaSwc4jTransformOutput> = None;
static mut JAVA_TRANSPILE_OUTPUT: Option<JavaSwc4jTranspileOutput> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_PARSE_OUTPUT = Some(JavaSwc4jParseOutput::new(env));
    JAVA_TRANSFORM_OUTPUT = Some(JavaSwc4jTransformOutput::new(env));
    JAVA_TRANSPILE_OUTPUT = Some(JavaSwc4jTranspileOutput::new(env));
  }
}

#[derive(Debug)]
pub struct ParseOutput {
  pub comments: Option<MultiThreadedComments>,
  pub media_type: MediaType,
  pub parse_mode: ParseMode,
  pub program: Option<Arc<Program>>,
  pub source_text: String,
  pub tokens: Option<Arc<Vec<TokenAndSpan>>>,
}

impl ParseOutput {
  pub fn new(parse_options: &ParseOptions, parsed_source: &ParsedSource) -> Self {
    let comments = if parse_options.capture_comments {
      Some(parsed_source.comments().clone())
    } else {
      None
    };
    let media_type = parsed_source.media_type();
    let parse_mode = if parsed_source.is_module() {
      ParseMode::Module
    } else {
      ParseMode::Script
    };
    let program = if parse_options.capture_ast {
      Some(parsed_source.program())
    } else {
      None
    };
    let source_text = parsed_source.text().to_string();
    let tokens = if parse_options.capture_tokens {
      Some(Arc::new(parsed_source.tokens().to_vec()))
    } else {
      None
    };
    ParseOutput {
      comments,
      media_type,
      parse_mode,
      program,
      source_text,
      tokens,
    }
  }

  pub fn get_byte_to_index_map(&self) -> ByteToIndexMap {
    // Register the keys
    let mut map = ByteToIndexMap::new();
    self.comments.as_ref().map(|comments| {
      comments.leading_map().iter().for_each(|(key, value)| {
        map.register_by_byte_pos(&key);
        value.iter().for_each(|comment| map.register_by_span(&comment.span));
      });
      comments.trailing_map().iter().for_each(|(key, value)| {
        map.register_by_byte_pos(&key);
        value.iter().for_each(|comment| map.register_by_span(&comment.span));
      });
    });
    self.program.as_ref().map(|program| program.register_with_map(&mut map));
    self.tokens.as_ref().map(|token_and_spans| {
      token_and_spans.iter().for_each(|token_and_span| {
        map.register_by_span(&token_and_span.span);
      })
    });
    map.update_by_str(self.source_text.as_str());
    map
  }
}

impl ToJava for ParseOutput {
  fn to_java<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let byte_to_index_map = self.get_byte_to_index_map();
    let java_program = self.program.as_ref().map_or(Ok(Default::default()), |program| {
      program.to_java_with_map(env, &byte_to_index_map)
    })?;
    let java_media_type = self.media_type.to_java(env)?;
    let java_parse_mode = self.parse_mode.to_java(env)?;
    let source_text = self.source_text.as_str();
    let java_tokens =
      token_utils::token_and_spans_to_java_list(env, &byte_to_index_map, &source_text, self.tokens.clone())?;
    let java_comments = self.comments.as_ref().map_or(Ok(Default::default()), |comments| {
      comments_new(env, comments, &byte_to_index_map)
    })?;
    let return_value = unsafe { JAVA_PARSE_OUTPUT.as_ref().unwrap() }.construct(
      env,
      &java_program,
      &java_media_type,
      &java_parse_mode,
      source_text,
      &java_tokens,
      &java_comments,
    );
    delete_local_ref!(env, java_program);
    delete_local_ref!(env, java_media_type);
    delete_local_ref!(env, java_parse_mode);
    delete_local_ref!(env, java_tokens);
    delete_local_ref!(env, java_comments);
    return_value
  }
}

#[derive(Debug)]
pub struct TransformOutput {
  pub code: String,
  pub media_type: MediaType,
  pub parse_mode: ParseMode,
  pub source_map: Option<String>,
}

impl TransformOutput {
  pub fn new(parsed_source: &ParsedSource, code: String, source_map: Option<String>) -> Self {
    let media_type = parsed_source.media_type();
    let parse_mode = if parsed_source.is_module() {
      ParseMode::Module
    } else {
      ParseMode::Script
    };
    TransformOutput {
      code,
      media_type,
      parse_mode,
      source_map,
    }
  }
}

impl ToJava for TransformOutput {
  fn to_java<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let code = self.code.as_str();
    let java_media_type = self.media_type.to_java(env)?;
    let java_parse_mode = self.parse_mode.to_java(env)?;
    let optional_source_map = &self.source_map;
    let return_value = unsafe { JAVA_TRANSFORM_OUTPUT.as_ref().unwrap() }.construct(
      env,
      code,
      &java_media_type,
      &java_parse_mode,
      &optional_source_map,
    );
    delete_local_ref!(env, java_media_type);
    delete_local_ref!(env, java_parse_mode);
    return_value
  }
}

#[derive(Debug)]
pub struct TranspileOutput {
  pub code: String,
  pub parse_output: ParseOutput,
  pub source_map: Option<String>,
}

impl TranspileOutput {
  pub fn new(
    transpile_options: &TranspileOptions,
    parsed_source: &ParsedSource,
    transpile_result: &TranspileResult,
  ) -> Self {
    let comments = if transpile_options.capture_comments {
      Some(parsed_source.comments().clone())
    } else {
      None
    };
    let emitted_source = transpile_result.clone().into_source();
    let code = String::from_utf8(emitted_source.source).unwrap_or_default();
    let media_type = parsed_source.media_type();
    let parse_mode = if parsed_source.is_module() {
      ParseMode::Module
    } else {
      ParseMode::Script
    };
    let program = if transpile_options.capture_ast {
      Some(parsed_source.program())
    } else {
      None
    };
    let source_map = emitted_source
      .source_map
      .map(|source_map| String::from_utf8(source_map).unwrap_or_default());
    let source_text = parsed_source.text().to_string();
    let tokens = if transpile_options.capture_tokens {
      Some(Arc::new(parsed_source.tokens().to_vec()))
    } else {
      None
    };
    let parse_output = ParseOutput {
      comments,
      media_type,
      parse_mode,
      program,
      source_text,
      tokens,
    };
    TranspileOutput {
      code,
      parse_output,
      source_map,
    }
  }
}

impl ToJava for TranspileOutput {
  fn to_java<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> Result<JObject<'a>>
  where
    'local: 'a,
  {
    let byte_to_index_map = self.parse_output.get_byte_to_index_map();
    let java_program = self
      .parse_output
      .program
      .as_ref()
      .map_or(Ok(Default::default()), |program| {
        program.to_java_with_map(env, &byte_to_index_map)
      })?;
    let code = self.code.as_str();
    let java_media_type = self.parse_output.media_type.to_java(env)?;
    let java_parse_mode = self.parse_output.parse_mode.to_java(env)?;
    let optional_source_map = &self.source_map;
    let source_text = self.parse_output.source_text.as_str();
    let java_tokens = token_utils::token_and_spans_to_java_list(
      env,
      &byte_to_index_map,
      &source_text,
      self.parse_output.tokens.clone(),
    )?;
    let java_comments = self
      .parse_output
      .comments
      .as_ref()
      .map_or(Ok(Default::default()), |comments| {
        comments_new(env, comments, &byte_to_index_map)
      })?;
    let return_value = unsafe { JAVA_TRANSPILE_OUTPUT.as_ref().unwrap() }.construct(
      env,
      &java_program,
      code,
      &java_media_type,
      &java_parse_mode,
      &optional_source_map,
      source_text,
      &java_tokens,
      &java_comments,
    );
    delete_local_ref!(env, java_program);
    delete_local_ref!(env, java_media_type);
    delete_local_ref!(env, java_parse_mode);
    delete_local_ref!(env, java_tokens);
    delete_local_ref!(env, java_comments);
    return_value
  }
}
