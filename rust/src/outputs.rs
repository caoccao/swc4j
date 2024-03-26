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
use deno_ast::swc::parser::token::TokenAndSpan;
use deno_ast::{ParsedSource, TranspiledSource};

use jni::objects::{GlobalRef, JMethodID, JObject};
use jni::sys::jvalue;
use jni::JNIEnv;

use std::ptr::null_mut;
use std::sync::Arc;

use crate::ast_utils;
use crate::converter;
use crate::enums::*;
use crate::jni_utils::ToJniType;
use crate::options::*;
use crate::position_utils::ByteToIndexMap;
use crate::token_utils;

struct JavaParseOutput {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaParseOutput {}
unsafe impl Sync for JavaParseOutput {}

impl JavaParseOutput {
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
        "(Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstProgram;Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;ZZLjava/lang/String;Ljava/util/List;)V",
      )
      .expect("Couldn't find method Swc4jParseOutput::new");
    JavaParseOutput {
      class,
      method_construct,
    }
  }

  pub fn construct<'local, 'a>(&self, env: &mut JNIEnv<'local>, parse_output: &ParseOutput) -> JObject<'a>
  where
    'local: 'a,
  {
    let byte_to_index_map = parse_output.get_byte_to_index_map();
    let program = ast_utils::program::create_program(env, &byte_to_index_map, &parse_output.program);
    let program = jvalue { l: program.as_raw() };
    let java_media_type = unsafe { JAVA_MEDIA_TYPE.as_ref().unwrap() };
    let media_type = java_media_type.parse(env, parse_output.media_type.get_id());
    let module = jvalue {
      z: if parse_output.module { 1u8 } else { 0u8 },
    };
    let script = jvalue {
      z: if parse_output.script { 1u8 } else { 0u8 },
    };
    let source_text = jvalue {
      l: converter::string_to_jstring(env, &parse_output.source_text).as_raw(),
    };
    let tokens = token_utils::token_and_spans_to_java_list(
      env,
      &byte_to_index_map,
      &parse_output.source_text.to_string(),
      parse_output.tokens.clone(),
    );
    unsafe {
      env
        .new_object_unchecked(
          &self.class,
          self.method_construct,
          &[program, media_type, module, script, source_text, tokens],
        )
        .expect("Couldn't construct Swc4jParseOutput")
    }
  }
}

struct JavaTranspileOutput {
  class: GlobalRef,
  method_construct: JMethodID,
}
unsafe impl Send for JavaTranspileOutput {}
unsafe impl Sync for JavaTranspileOutput {}

impl JavaTranspileOutput {
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
        "(Lcom/caoccao/javet/swc4j/ast/program/Swc4jAstProgram;Ljava/lang/String;Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;ZZLjava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
      )
      .expect("Couldn't find method Swc4jTranspileOutput::new");
    JavaTranspileOutput {
      class,
      method_construct,
    }
  }

  pub fn construct<'local, 'a>(&self, env: &mut JNIEnv<'local>, transpile_output: &TranspileOutput) -> JObject<'a>
  where
    'local: 'a,
  {
    let byte_to_index_map = transpile_output.parse_output.get_byte_to_index_map();
    let program = ast_utils::program::create_program(env, &byte_to_index_map, &transpile_output.parse_output.program);
    let program = jvalue { l: program.as_raw() };
    let code = jvalue {
      l: converter::string_to_jstring(env, &transpile_output.code).as_raw(),
    };
    let java_media_type = unsafe { JAVA_MEDIA_TYPE.as_ref().unwrap() };
    let media_type = java_media_type.parse(env, transpile_output.parse_output.media_type.get_id());
    let module = jvalue {
      z: if transpile_output.parse_output.module { 1u8 } else { 0u8 },
    };
    let script = jvalue {
      z: if transpile_output.parse_output.script { 1u8 } else { 0u8 },
    };
    let source_map = jvalue {
      l: match &transpile_output.source_map {
        Some(s) => converter::string_to_jstring(env, &s).as_raw(),
        None => null_mut(),
      },
    };
    let source_text = jvalue {
      l: converter::string_to_jstring(env, &transpile_output.parse_output.source_text).as_raw(),
    };
    let tokens = token_utils::token_and_spans_to_java_list(
      env,
      &byte_to_index_map,
      &transpile_output.parse_output.source_text.to_string(),
      transpile_output.parse_output.tokens.clone(),
    );
    unsafe {
      env
        .new_object_unchecked(
          &self.class,
          self.method_construct,
          &[
            program,
            code,
            media_type,
            module,
            script,
            source_map,
            source_text,
            tokens,
          ],
        )
        .expect("Couldn't construct Swc4jTranspileOutput")
    }
  }
}

static mut JAVA_PARSE_OUTPUT: Option<JavaParseOutput> = None;
static mut JAVA_TRANSPILE_OUTPUT: Option<JavaTranspileOutput> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_PARSE_OUTPUT = Some(JavaParseOutput::new(env));
    JAVA_TRANSPILE_OUTPUT = Some(JavaTranspileOutput::new(env));
  }
}

#[derive(Debug)]
pub struct ParseOutput {
  pub media_type: MediaType,
  pub module: bool,
  pub program: Option<Arc<Program>>,
  pub script: bool,
  pub source_text: String,
  pub tokens: Option<Arc<Vec<TokenAndSpan>>>,
}

impl ParseOutput {
  pub fn new(parse_options: &ParseOptions, parsed_source: &ParsedSource) -> Self {
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
    match &self.program {
      Some(program) => {
        ast_utils::span::register_program(&mut byte_to_index_map, program);
      }
      None => {}
    }
    match &self.tokens {
      Some(token_and_spans) => {
        token_and_spans.iter().for_each(|token_and_span| {
          byte_to_index_map.register_by_span(&token_and_span.span);
        });
      }
      None => {}
    }
    // Fill the values
    let mut utf8_byte_length: usize = 0;
    let chars = self.source_text.chars();
    let mut char_count = 0;
    chars.for_each(|c| {
      byte_to_index_map.update(&utf8_byte_length, char_count);
      utf8_byte_length += c.len_utf8();
      char_count += 1;
    });
    byte_to_index_map.update(&utf8_byte_length, char_count);
    byte_to_index_map
  }
}

impl ToJniType for ParseOutput {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a,
  {
    unsafe { JAVA_PARSE_OUTPUT.as_ref().unwrap() }.construct(env, &self)
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
    transpiled_source: &TranspiledSource,
  ) -> Self {
    let code = transpiled_source.text.to_owned();
    let media_type = parsed_source.media_type();
    let module = parsed_source.is_module();
    let program = if transpile_options.capture_ast {
      Some(parsed_source.program())
    } else {
      None
    };
    let script = parsed_source.is_script();
    let source_map = transpiled_source.source_map.to_owned();
    let source_text = parsed_source.text_info().text().to_string();
    let tokens = if transpile_options.capture_tokens {
      Some(Arc::new(parsed_source.tokens().to_vec()))
    } else {
      None
    };
    let parse_output = ParseOutput {
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
    unsafe { JAVA_TRANSPILE_OUTPUT.as_ref().unwrap() }.construct(env, &self)
  }
}
