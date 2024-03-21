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

use jni::objects::{GlobalRef, JObject, JStaticMethodID};
use jni::signature::ReturnType;
use jni::sys::jvalue;
use jni::JNIEnv;

use deno_ast::swc::{
  atoms::JsWord,
  common::source_map::Pos,
  common::BytePos,
  common::Spanned,
  parser::token::{IdentLike, Keyword, Token, TokenAndSpan, Word},
};

use crate::jni_utils::{ToJniType, JAVA_ARRAY_LIST};
use crate::{converter, enums::*};

use std::ops::Range;
use std::ptr::null_mut;
use std::slice::SliceIndex;
use std::sync::Arc;

pub struct JavaAstTokenFactory {
  #[allow(dead_code)]
  class: GlobalRef,
  method_create_keyword: JStaticMethodID,
  method_create_unknown: JStaticMethodID,
}
unsafe impl Send for JavaAstTokenFactory {}
unsafe impl Sync for JavaAstTokenFactory {}

impl JavaAstTokenFactory {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/ast/Swc4jAstTokenFactory")
      .expect("Couldn't find class Swc4jAstTokenFactory");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jAstTokenFactory");
    let method_create_keyword = env
      .get_static_method_id(
        &class,
        "createKeyword",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;II)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenKeyword;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createKeyword");
    let method_create_unknown = env
      .get_static_method_id(
        &class,
        "createUnknown",
        "(Ljava/lang/String;II)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenUnknown;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createUnknown");
    JavaAstTokenFactory {
      class,
      method_create_keyword,
      method_create_unknown,
    }
  }

  pub fn create_keyword<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ast_token_type: AstTokenType,
    range: Range<usize>,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_token_type = unsafe { JAVA_AST_TOKEN_TYPE.as_ref().unwrap() };
    let ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_keyword,
          ReturnType::Object,
          &[ast_token_type, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstTokenKeyword")
        .l()
        .expect("Couldn't convert Swc4jAstTokenKeyword")
    }
  }

  pub fn create_unknown<'local, 'a>(&self, env: &mut JNIEnv<'local>, text: &str, range: Range<usize>) -> JObject<'a>
  where
    'local: 'a,
  {
    let text = jvalue {
      l: converter::string_to_jstring(env, &text).as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_unknown,
          ReturnType::Object,
          &[text, start_position, end_position],
        )
        .expect("Couldn't create Swc4jAstTokenUnknown")
        .l()
        .expect("Couldn't convert Swc4jAstTokenUnknown")
    }
  }
}

pub static mut JAVA_AST_TOKEN_FACTORY: Option<JavaAstTokenFactory> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_AST_TOKEN_FACTORY = Some(JavaAstTokenFactory::new(env));
  }
}

pub fn token_and_spans_to_java_list<'local>(
  env: &mut JNIEnv<'local>,
  source_text: &str,
  token_and_spans: Option<Arc<Vec<TokenAndSpan>>>,
) -> jvalue {
  jvalue {
    l: match token_and_spans {
      Some(token_and_spans) => {
        let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
        let java_ast_token_factory = unsafe { JAVA_AST_TOKEN_FACTORY.as_ref().unwrap() };
        let list = java_array_list.create(env, token_and_spans.len());
        token_and_spans.iter().for_each(|token_and_span| {
          let range = Range {
            start: token_and_span.span.lo().to_u32() as usize - 1,
            end: token_and_span.span.hi().to_u32() as usize - 1,
          };
          let ast_token = match &token_and_span.token {
            Token::Word(Word::Keyword(keyword)) => {
              java_ast_token_factory.create_keyword(env, AstTokenType::parse_by_keyword(&keyword), range)
            }
            _ => java_ast_token_factory.create_unknown(env, &source_text[range.to_owned()], range),
          };
          java_array_list.add(env, &list, &ast_token);
        });
        list.as_raw()
      }
      None => null_mut(),
    },
  }
}
