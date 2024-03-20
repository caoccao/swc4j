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
use jni::sys::jvalue;
use jni::JNIEnv;

use deno_ast::swc::{
  atoms::JsWord,
  common::source_map::Pos,
  common::BytePos,
  common::Spanned,
  parser::token::{IdentLike, Keyword, Token, TokenAndSpan, Word},
};

use crate::enums::*;
use crate::jni_utils::{ToJniType, JAVA_ARRAY_LIST};

use std::ptr::null_mut;
use std::sync::Arc;

#[derive(Debug)]
pub struct AstToken {
  pub ast_token_type: AstTokenType,
  pub end_position: i32,
  pub start_position: i32,
}

impl ToJniType for AstToken {
  fn to_jni_type<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
  where
    'local: 'a,
  {
    unsafe { JAVA_AST_TOKEN.as_ref().unwrap() }.create(env, self.ast_token_type, self.start_position, self.end_position)
  }
}

pub struct JavaAstToken {
  #[allow(dead_code)]
  class: GlobalRef,
  method_constructor: JMethodID,
}
unsafe impl Send for JavaAstToken {}
unsafe impl Sync for JavaAstToken {}

impl JavaAstToken {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/ast/Swc4jAstToken")
      .expect("Couldn't find class Swc4jAstToken");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jAstToken");
    let method_constructor = env
      .get_method_id(
        &class,
        "<init>",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;II)V",
      )
      .expect("Couldn't find method Swc4jAstTokenType.Swc4jAstTokenType");
    JavaAstToken {
      class,
      method_constructor,
    }
  }

  pub fn create<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ast_token_type: AstTokenType,
    start_position: i32,
    end_position: i32,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_token_type = unsafe { JAVA_AST_TOKEN_TYPE.as_ref().unwrap() };
    let ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let start_position = jvalue {
      i: start_position as i32,
    };
    let end_position = jvalue { i: end_position as i32 };
    unsafe {
      env
        .new_object_unchecked(
          &self.class,
          self.method_constructor,
          &[ast_token_type, start_position, end_position],
        )
        .expect("Couldn't create Swc4jTranspileOutput")
    }
  }
}

pub static mut JAVA_AST_TOKEN: Option<JavaAstToken> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_AST_TOKEN = Some(JavaAstToken::new(env));
  }
}

pub fn token_and_spans_to_java_list<'local>(
  env: &mut JNIEnv<'local>,
  token_and_spans: Option<Arc<Vec<TokenAndSpan>>>,
) -> jvalue {
  jvalue {
    l: match token_and_spans {
      Some(token_and_spans) => {
        let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
        let java_ast_token = unsafe { JAVA_AST_TOKEN.as_ref().unwrap() };
        let list = java_array_list.create(env, token_and_spans.len());
        token_and_spans.iter().for_each(|token_and_span| {
          let ast_token_type = match &token_and_span.token {
            Token::Word(Word::Keyword(keyword)) => AstTokenType::parse_by_keyword(&keyword),
            _ => AstTokenType::Unknown,
          };
          let ast_token = java_ast_token.create(
            env,
            ast_token_type,
            token_and_span.span.lo().to_u32() as i32,
            token_and_span.span.hi().to_u32() as i32,
          );
          java_array_list.add(env, &list, &ast_token);
        });
        list.as_raw()
      }
      None => null_mut(),
    },
  }
}
