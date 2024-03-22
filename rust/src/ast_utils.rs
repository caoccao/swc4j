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
  atoms::Atom,
  common::source_map::Pos,
  parser::token::{IdentLike, Token, TokenAndSpan, Word},
};

use crate::jni_utils::JAVA_ARRAY_LIST;
use crate::{converter, enums::*};

use std::collections::BTreeMap;
use std::ops::Range;
use std::ptr::null_mut;
use std::sync::Arc;

pub struct JavaAstTokenFactory {
  #[allow(dead_code)]
  class: GlobalRef,
  method_create_false: JStaticMethodID,
  method_create_generic_operator: JStaticMethodID,
  method_create_ident_known: JStaticMethodID,
  method_create_ident_other: JStaticMethodID,
  method_create_keyword: JStaticMethodID,
  method_create_null: JStaticMethodID,
  method_create_true: JStaticMethodID,
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
    let method_create_false = env
      .get_static_method_id(
        &class,
        "createFalse",
        "(IIZ)Lcom/caoccao/javet/swc4j/ast/words/Swc4jAstTokenFalse;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createFalse");
    let method_create_generic_operator = env
      .get_static_method_id(
        &class,
        "createGenericOperator",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;IIZ)Lcom/caoccao/javet/swc4j/ast/operators/Swc4jAstTokenGenericOperator;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createGenericOperator");
    let method_create_ident_known = env
      .get_static_method_id(
        &class,
        "createIdentKnown",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/words/Swc4jAstTokenIdentKnown;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createIdentKnown");
    let method_create_keyword = env
      .get_static_method_id(
        &class,
        "createKeyword",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;IIZ)Lcom/caoccao/javet/swc4j/ast/words/Swc4jAstTokenKeyword;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createKeyword");
    let method_create_null = env
      .get_static_method_id(
        &class,
        "createNull",
        "(IIZ)Lcom/caoccao/javet/swc4j/ast/words/Swc4jAstTokenNull;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createNull");
    let method_create_ident_other = env
      .get_static_method_id(
        &class,
        "createIdentOther",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/words/Swc4jAstTokenIdentOther;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createIdentOther");
    let method_create_true = env
      .get_static_method_id(
        &class,
        "createTrue",
        "(IIZ)Lcom/caoccao/javet/swc4j/ast/words/Swc4jAstTokenTrue;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createTrue");
    let method_create_unknown = env
      .get_static_method_id(
        &class,
        "createUnknown",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenUnknown;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createUnknown");
    JavaAstTokenFactory {
      class,
      method_create_false,
      method_create_generic_operator,
      method_create_ident_known,
      method_create_ident_other,
      method_create_keyword,
      method_create_null,
      method_create_true,
      method_create_unknown,
    }
  }

  pub fn create_false<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_false,
          ReturnType::Object,
          &[start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenFalse")
        .l()
        .expect("Couldn't convert Swc4jAstTokenFalse")
    }
  }

  pub fn create_generic_operator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ast_token_type: AstTokenType,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_token_type = unsafe { JAVA_AST_TOKEN_TYPE.as_ref().unwrap() };
    let ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_generic_operator,
          ReturnType::Object,
          &[ast_token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenGenericOperator")
        .l()
        .expect("Couldn't convert Swc4jAstTokenGenericOperator")
    }
  }

  pub fn create_ident_known<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let text = jvalue {
      l: converter::string_to_jstring(env, &text).as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_ident_known,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenIdentKnown")
        .l()
        .expect("Couldn't convert Swc4jAstTokenIdentKnown")
    }
  }

  pub fn create_keyword<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    ast_token_type: AstTokenType,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_token_type = unsafe { JAVA_AST_TOKEN_TYPE.as_ref().unwrap() };
    let ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_keyword,
          ReturnType::Object,
          &[ast_token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenKeyword")
        .l()
        .expect("Couldn't convert Swc4jAstTokenKeyword")
    }
  }

  pub fn create_null<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_null,
          ReturnType::Object,
          &[start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenNull")
        .l()
        .expect("Couldn't convert Swc4jAstTokenNull")
    }
  }

  pub fn create_ident_other<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let text = jvalue {
      l: converter::string_to_jstring(env, &text).as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_ident_other,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenIdentOther")
        .l()
        .expect("Couldn't convert Swc4jAstTokenIdentOther")
    }
  }

  pub fn create_true<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_true,
          ReturnType::Object,
          &[start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenTrue")
        .l()
        .expect("Couldn't convert Swc4jAstTokenTrue")
    }
  }

  pub fn create_unknown<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let text = jvalue {
      l: converter::string_to_jstring(env, &text).as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_unknown,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
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
        // 1st pass: Prepare utf8_range map.
        let mut byte_to_index_map: BTreeMap<usize, usize> = BTreeMap::new();
        token_and_spans.iter().for_each(|token_and_span| {
          [
            token_and_span.span.lo().to_usize() - 1,
            token_and_span.span.hi().to_usize() - 1,
          ]
          .into_iter()
          .for_each(|position| {
            if !byte_to_index_map.contains_key(&position) {
              byte_to_index_map.insert(position, 0);
            }
          });
        });
        let mut utf8_byte_length: usize = 0;
        let chars = source_text.chars();
        let mut char_count = 0;
        chars.for_each(|c| {
          byte_to_index_map
            .get_mut(&utf8_byte_length)
            .map(|value| *value = char_count);
          utf8_byte_length += c.len_utf8();
          char_count += 1;
        });
        byte_to_index_map
          .get_mut(&utf8_byte_length)
          .map(|value| *value = char_count);
        // 2nd pass: Process tokens and spans.
        let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
        let java_ast_token_factory = unsafe { JAVA_AST_TOKEN_FACTORY.as_ref().unwrap() };
        let list = java_array_list.create(env, token_and_spans.len());
        token_and_spans.iter().for_each(|token_and_span| {
          let byte_range = Range {
            start: token_and_span.span.lo().to_usize() - 1,
            end: token_and_span.span.hi().to_usize() - 1,
          };
          let line_break_ahead = token_and_span.had_line_break;
          let text = &source_text[byte_range.to_owned()];
          let index_range = Range {
            start: *byte_to_index_map
              .get(&byte_range.start)
              .expect("Couldn't find start index"),
            end: *byte_to_index_map.get(&byte_range.end).expect("Couldn't find end index"),
          };
          let ast_token = match &token_and_span.token {
            Token::Word(word) => match word {
              Word::Keyword(keyword) => java_ast_token_factory.create_keyword(
                env,
                AstTokenType::parse_by_keyword(&keyword),
                index_range,
                line_break_ahead,
              ),
              Word::Null => java_ast_token_factory.create_null(env, index_range, line_break_ahead),
              Word::True => java_ast_token_factory.create_true(env, index_range, line_break_ahead),
              Word::False => java_ast_token_factory.create_false(env, index_range, line_break_ahead),
              Word::Ident(ident) => match ident {
                IdentLike::Known(known_ident) => java_ast_token_factory.create_ident_known(
                  env,
                  &Atom::from(*known_ident).as_str(),
                  index_range,
                  line_break_ahead,
                ),
                IdentLike::Other(js_word) => {
                  java_ast_token_factory.create_ident_other(env, &js_word.as_str(), index_range, line_break_ahead)
                }
              },
            },
            Token::Arrow => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Arrow, index_range, line_break_ahead)
            }
            Token::Hash => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Hash, index_range, line_break_ahead)
            }
            Token::At => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::At, index_range, line_break_ahead)
            }
            Token::Dot => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Dot, index_range, line_break_ahead)
            }
            Token::DotDotDot => java_ast_token_factory.create_generic_operator(
              env,
              AstTokenType::DotDotDot,
              index_range,
              line_break_ahead,
            ),
            Token::Bang => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Bang, index_range, line_break_ahead)
            }
            Token::LParen => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::LParen, index_range, line_break_ahead)
            }
            Token::RParen => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::RParen, index_range, line_break_ahead)
            }
            Token::LBracket => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::LBracket, index_range, line_break_ahead)
            }
            Token::RBracket => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::RBracket, index_range, line_break_ahead)
            }
            Token::LBrace => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::LBrace, index_range, line_break_ahead)
            }
            Token::RBrace => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::RBrace, index_range, line_break_ahead)
            }
            Token::Semi => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Semi, index_range, line_break_ahead)
            }
            Token::Comma => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Comma, index_range, line_break_ahead)
            }
            Token::BackQuote => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::BackQuote, index_range, line_break_ahead)
            }
            Token::Colon => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Colon, index_range, line_break_ahead)
            }
            Token::DollarLBrace => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::DollarLBrace, index_range, line_break_ahead)
            }
            Token::QuestionMark => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::QuestionMark, index_range, line_break_ahead)
            }
            Token::PlusPlus => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::PlusPlus, index_range, line_break_ahead)
            }
            Token::MinusMinus => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::MinusMinus, index_range, line_break_ahead)
            }
            Token::Tilde => {
              java_ast_token_factory.create_generic_operator(env, AstTokenType::Tilde, index_range, line_break_ahead)
            }
            _ => java_ast_token_factory.create_unknown(env, &text, index_range, line_break_ahead),
          };
          java_array_list.add(env, &list, &ast_token);
        });
        list.as_raw()
      }
      None => null_mut(),
    },
  }
}
