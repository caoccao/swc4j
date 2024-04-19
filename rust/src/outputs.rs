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

use deno_ast::swc::ast::*;
use deno_ast::swc::common::comments::Comment;
use deno_ast::swc::parser::token::TokenAndSpan;
use deno_ast::{MultiThreadedComments, ParsedSource, TranspileResult};

use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::sys::jvalue;
use jni::JNIEnv;

use std::ptr::null_mut;
use std::sync::Arc;

use crate::ast_utils;
use crate::enums::*;
use crate::jni_utils::*;
use crate::options::*;
use crate::span_utils::ByteToIndexMap;
use crate::token_utils;

/* JavaSwc4jComment Begin */
struct JavaSwc4jComment {
  #[allow(dead_code)]
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jComment {}
unsafe impl Sync for JavaSwc4jComment {}

impl JavaSwc4jComment {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/comments/Swc4jComment")
      .expect("Couldn't find class Swc4jComment");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jComment");
    let method_construct = env
      .get_method_id(
        &class,
        "<init>",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/comments/Swc4jCommentKind;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;)V",
      )
      .expect("Couldn't find method Swc4jComment::new");
    JavaSwc4jComment {
      class,
      method_construct,
    }
  }
/* JavaSwc4jComment End */

  pub fn construct<'local, 'a>(&self, env: &mut JNIEnv<'local>, comment: &Comment, map: &ByteToIndexMap) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_class_comment_kind = unsafe { JAVA_COMMENT_KIND.as_ref().unwrap() };
    let java_text = string_to_jstring!(env, &comment.text);
    let text = object_to_jvalue!(java_text);
    let java_comment_kind = java_class_comment_kind.parse(env, comment.kind.get_id());
    let comment_kind = object_to_jvalue!(java_comment_kind);
    let java_span_ex = map.get_span_ex_by_span(&comment.span).to_jni_type(env);
    let span_ex = object_to_jvalue!(java_span_ex);
    let return_value = call_as_construct!(
      env,
      &self.class,
      self.method_construct,
      &[text, comment_kind, span_ex],
      "Swc4jComment"
    );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_comment_kind);
    delete_local_ref!(env, java_span_ex);
    return_value
  }
}

/* JavaSwc4jParseOutput Begin */
struct JavaSwc4jParseOutput {
  #[allow(dead_code)]
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jParseOutput {}
unsafe impl Sync for JavaSwc4jParseOutput {}

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
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstProgram;Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;Ljava/lang/String;Ljava/util/List;)V",
      )
      .expect("Couldn't find method Swc4jParseOutput::new");
    JavaSwc4jParseOutput {
      class,
      method_construct,
    }
  }
}
/* JavaSwc4jParseOutput End */

/* JavaSwc4jTranspileOutput Begin */
struct JavaSwc4jTranspileOutput {
  #[allow(dead_code)]
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaSwc4jTranspileOutput {}
unsafe impl Sync for JavaSwc4jTranspileOutput {}

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
        "(Lcom/caoccao/javet/swc4j/ast/interfaces/ISwc4jAstProgram;Ljava/lang/String;Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;Lcom/caoccao/javet/swc4j/enums/Swc4jParseMode;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
      )
      .expect("Couldn't find method Swc4jTranspileOutput::new");
    JavaSwc4jTranspileOutput {
      class,
      method_construct,
    }
  }
}
/* JavaSwc4jTranspileOutput End */

static mut JAVA_COMMENT: Option<JavaSwc4jComment> = None;
static mut JAVA_PARSE_OUTPUT: Option<JavaSwc4jParseOutput> = None;
static mut JAVA_TRANSPILE_OUTPUT: Option<JavaSwc4jTranspileOutput> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_COMMENT = Some(JavaSwc4jComment::new(env));
    JAVA_PARSE_OUTPUT = Some(JavaSwc4jParseOutput::new(env));
    JAVA_TRANSPILE_OUTPUT = Some(JavaSwc4jTranspileOutput::new(env));
  }
}

#[derive(Debug)]
pub struct ParseOutput {
  pub comments: MultiThreadedComments,
  pub media_type: MediaType,
  pub module: bool,
  pub program: Option<Arc<Program>>,
  pub script: bool,
  pub source_text: String,
  pub tokens: Option<Arc<Vec<TokenAndSpan>>>,
}

impl ParseOutput {
  pub fn new(parse_options: &ParseOptions, parsed_source: &ParsedSource) -> Self {
    let comments = parsed_source.comments().clone();
    let media_type = parsed_source.media_type();
    let module = parsed_source.is_module();
    let program = if parse_options.capture_ast {
      Some(parsed_source.program())
    } else {
      None
    };
    let script = parsed_source.is_script();
    let source_text = parsed_source.text_info().text().to_string();
    let tokens = if parse_options.capture_tokens {
      Some(Arc::new(parsed_source.tokens().to_vec()))
    } else {
      None
    };
    ParseOutput {
      comments,
      media_type,
      module,
      program,
      script,
      source_text,
      tokens,
    }
  }

  pub fn get_byte_to_index_map(&self) -> ByteToIndexMap {
    // Register the keys
    let mut byte_to_index_map = ByteToIndexMap::new();
    self
      .comments
      .get_vec()
      .iter()
      .for_each(|comment| byte_to_index_map.register_by_span(&comment.span));
    self
      .program
      .as_ref()
      .map(|program| ast_utils::span::enum_register_program(&mut byte_to_index_map, program));
    self.tokens.as_ref().map(|token_and_spans| {
      token_and_spans.iter().for_each(|token_and_span| {
        byte_to_index_map.register_by_span(&token_and_span.span);
      })
    });
    // Fill the values
    let mut utf8_byte_length: usize = 0;
    let chars = self.source_text.chars();
    let mut char_count = 0u32;
    let mut line = 1u32;
    let mut column = 1u32;
    chars.for_each(|c| {
      byte_to_index_map.update(&utf8_byte_length, char_count, line, column);
      utf8_byte_length += c.len_utf8();
      char_count += 1;
      column = if c == '\n' {
        line += 1;
        1
      } else {
        column + 1
      }
    });
    column = 1;
    byte_to_index_map.update(&utf8_byte_length, char_count, line, column);
    byte_to_index_map
  }
}

impl ToJniType for ParseOutput {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_class_parse_output = unsafe { JAVA_PARSE_OUTPUT.as_ref().unwrap() };
    let java_class_media_type = unsafe { JAVA_MEDIA_TYPE.as_ref().unwrap() };
    let java_class_parse_mode = unsafe { JAVA_PARSE_MODE.as_ref().unwrap() };
    let byte_to_index_map = self.get_byte_to_index_map();
    let java_optional_program = self
      .program
      .as_ref()
      .map(|program| ast_utils::program::enum_create_program(env, &byte_to_index_map, &program));
    let program = optional_object_to_jvalue!(&java_optional_program);
    let java_media_type = java_class_media_type.parse(env, self.media_type.get_id());
    let media_type = object_to_jvalue!(&java_media_type);
    let java_parse_mode = java_class_parse_mode.parse(env, if self.module { 0 } else { 1 });
    let parse_mode = object_to_jvalue!(&java_parse_mode);
    let java_source_text = string_to_jstring!(env, &self.source_text);
    let source_text = object_to_jvalue!(&java_source_text);
    let tokens = token_utils::token_and_spans_to_java_list(
      env,
      &byte_to_index_map,
      self.source_text.as_str(),
      self.tokens.clone(),
    );
    let return_value = call_as_construct!(
      env,
      &java_class_parse_output.class,
      java_class_parse_output.method_construct,
      &[program, media_type, parse_mode, source_text, tokens],
      "Swc4jParseOutput"
    );
    delete_local_optional_ref!(env, java_optional_program);
    delete_local_ref!(env, java_media_type);
    delete_local_ref!(env, java_parse_mode);
    delete_local_ref!(env, java_source_text);
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
    let comments = parsed_source.comments().clone();
    let emitted_source = transpile_result.clone().into_source();
    let code = emitted_source.text.to_owned();
    let media_type = parsed_source.media_type();
    let module = parsed_source.is_module();
    let program = if transpile_options.capture_ast {
      Some(parsed_source.program())
    } else {
      None
    };
    let script = parsed_source.is_script();
    let source_map = emitted_source.source_map.to_owned();
    let source_text = parsed_source.text_info().text().to_string();
    let tokens = if transpile_options.capture_tokens {
      Some(Arc::new(parsed_source.tokens().to_vec()))
    } else {
      None
    };
    let parse_output = ParseOutput {
      comments,
      media_type,
      module,
      program,
      script,
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

impl ToJniType for TranspileOutput {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_class_transpile_output = unsafe { JAVA_TRANSPILE_OUTPUT.as_ref().unwrap() };
    let java_class_media_type = unsafe { JAVA_MEDIA_TYPE.as_ref().unwrap() };
    let java_class_parse_mode = unsafe { JAVA_PARSE_MODE.as_ref().unwrap() };
    let byte_to_index_map = self.parse_output.get_byte_to_index_map();
    let java_optional_program = self
      .parse_output
      .program
      .as_ref()
      .map(|program| ast_utils::program::enum_create_program(env, &byte_to_index_map, &program));
    let program = optional_object_to_jvalue!(&java_optional_program);
    let java_code = string_to_jstring!(env, &self.code);
    let code = object_to_jvalue!(&java_code);
    let java_media_type = java_class_media_type.parse(env, self.parse_output.media_type.get_id());
    let media_type = object_to_jvalue!(&java_media_type);
    let java_parse_mode = java_class_parse_mode.parse(env, if self.parse_output.module { 0 } else { 1 });
    let parse_mode = object_to_jvalue!(&java_parse_mode);
    let java_source_map = optional_string_to_jstring!(env, &self.source_map);
    let source_map = object_to_jvalue!(&java_source_map);
    let java_source_text = string_to_jstring!(env, &self.parse_output.source_text);
    let source_text = object_to_jvalue!(&java_source_text);
    let tokens = token_utils::token_and_spans_to_java_list(
      env,
      &byte_to_index_map,
      self.parse_output.source_text.as_str(),
      self.parse_output.tokens.clone(),
    );
    let return_value = call_as_construct!(
      env,
      &java_class_transpile_output.class,
      java_class_transpile_output.method_construct,
      &[program, code, media_type, parse_mode, source_map, source_text, tokens,],
      "Swc4jTranspileOutput"
    );
    delete_local_optional_ref!(env, java_optional_program);
    delete_local_ref!(env, java_code);
    delete_local_ref!(env, java_media_type);
    delete_local_ref!(env, java_parse_mode);
    delete_local_ref!(env, java_source_map);
    delete_local_ref!(env, java_source_text);
    return_value
  }
}
