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

use deno_ast::swc::atoms::Atom;
use deno_ast::swc::common::source_map::Pos;
use deno_ast::swc::parser::error::Error;
use deno_ast::swc::parser::token::{IdentLike, Token, TokenAndSpan, Word};

use crate::converter;
use crate::enums::*;
use crate::jni_utils::JAVA_ARRAY_LIST;
use crate::position_utils::ByteToIndexMap;

use std::ops::Range;
use std::ptr::null_mut;
use std::sync::Arc;

pub struct JavaAstTokenFactory {
  #[allow(dead_code)]
  class: GlobalRef,
  method_create_assign_operator: JStaticMethodID,
  method_create_binary_operator: JStaticMethodID,
  method_create_bigint: JStaticMethodID,
  method_create_error: JStaticMethodID,
  method_create_false: JStaticMethodID,
  method_create_generic_operator: JStaticMethodID,
  method_create_ident_known: JStaticMethodID,
  method_create_ident_other: JStaticMethodID,
  method_create_jsx_tag_name: JStaticMethodID,
  method_create_jsx_tag_text: JStaticMethodID,
  method_create_keyword: JStaticMethodID,
  method_create_null: JStaticMethodID,
  method_create_number: JStaticMethodID,
  method_create_regex: JStaticMethodID,
  method_create_shebang: JStaticMethodID,
  method_create_string: JStaticMethodID,
  method_create_template: JStaticMethodID,
  method_create_true: JStaticMethodID,
  method_create_unknown: JStaticMethodID,
}
unsafe impl Send for JavaAstTokenFactory {}
unsafe impl Sync for JavaAstTokenFactory {}

impl JavaAstTokenFactory {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/tokens/Swc4jTokenFactory")
      .expect("Couldn't find class Swc4jTokenFactory");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTokenFactory");
    let method_create_assign_operator = env
      .get_static_method_id(
        &class,
        "createAssignOperator",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jTokenType;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createAssignOperator");
    let method_create_binary_operator = env
      .get_static_method_id(
        &class,
        "createBinaryOperator",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jTokenType;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createBinaryOperator");
    let method_create_bigint = env
      .get_static_method_id(
        &class,
        "createBigInt",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createBigInt");
    let method_create_error = env
      .get_static_method_id(
        &class,
        "createError",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createError");
    let method_create_false = env
      .get_static_method_id(
        &class,
        "createFalse",
        "(IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createFalse");
    let method_create_generic_operator = env
      .get_static_method_id(
        &class,
        "createGenericOperator",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jTokenType;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createGenericOperator");
    let method_create_ident_known = env
      .get_static_method_id(
        &class,
        "createIdentKnown",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createIdentKnown");
    let method_create_jsx_tag_name = env
      .get_static_method_id(
        &class,
        "createJsxTagName",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createJsxTagName");
    let method_create_jsx_tag_text = env
      .get_static_method_id(
        &class,
        "createJsxTagText",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createJsxTagText");
    let method_create_keyword = env
      .get_static_method_id(
        &class,
        "createKeyword",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jTokenType;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createKeyword");
    let method_create_null = env
      .get_static_method_id(
        &class,
        "createNull",
        "(IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createNull");
    let method_create_ident_other = env
      .get_static_method_id(
        &class,
        "createIdentOther",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createIdentOther");
    let method_create_number = env
      .get_static_method_id(
        &class,
        "createNumber",
        "(Ljava/lang/String;DIIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createNumber");
    let method_create_regex = env
      .get_static_method_id(
        &class,
        "createRegex",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValueFlags;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createRegex");
    let method_create_shebang = env
      .get_static_method_id(
        &class,
        "createShebang",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createShebang");
    let method_create_string = env
      .get_static_method_id(
        &class,
        "createString",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createString");
    let method_create_template = env
      .get_static_method_id(
        &class,
        "createTemplate",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createTemplate");
    let method_create_true = env
      .get_static_method_id(
        &class,
        "createTrue",
        "(IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createTrue");
    let method_create_unknown = env
      .get_static_method_id(
        &class,
        "createUnknown",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createUnknown");
    JavaAstTokenFactory {
      class,
      method_create_assign_operator,
      method_create_binary_operator,
      method_create_bigint,
      method_create_error,
      method_create_false,
      method_create_generic_operator,
      method_create_ident_known,
      method_create_ident_other,
      method_create_jsx_tag_name,
      method_create_jsx_tag_text,
      method_create_keyword,
      method_create_null,
      method_create_number,
      method_create_regex,
      method_create_template,
      method_create_shebang,
      method_create_string,
      method_create_true,
      method_create_unknown,
    }
  }

  pub fn create_assign_operator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: TokenType,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_token_type = unsafe { JAVA_TOKEN_TYPE.as_ref().unwrap() };
    let java_token_type = java_token_type.parse(env, token_type.get_id());
    let token_type = jvalue {
      l: java_token_type.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_assign_operator,
          ReturnType::Object,
          &[token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenAssignOperator")
        .l()
        .expect("Couldn't convert Swc4jTokenAssignOperator")
    };
    env
      .delete_local_ref(java_token_type)
      .expect("Couldn't delete local ast token type");
    token
  }

  pub fn create_binary_operator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: TokenType,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_token_type = unsafe { JAVA_TOKEN_TYPE.as_ref().unwrap() };
    let java_token_type = java_token_type.parse(env, token_type.get_id());
    let token_type = jvalue {
      l: java_token_type.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_binary_operator,
          ReturnType::Object,
          &[token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenBinaryOperator")
        .l()
        .expect("Couldn't convert Swc4jTokenBinaryOperator")
    };
    env
      .delete_local_ref(java_token_type)
      .expect("Couldn't delete local ast token type");
    token
  }

  pub fn create_bigint<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string = converter::string_to_jstring(env, &text);
    let text = jvalue {
      l: java_string.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_bigint,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenBigInt")
        .l()
        .expect("Couldn't convert Swc4jTokenBigInt")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local text");
    token
  }

  pub fn create_error<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    error: &Error,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string_text = converter::string_to_jstring(env, &text);
    let java_string_syntax_error = converter::string_to_jstring(env, &format!("{:?}", error));
    let text = jvalue {
      l: java_string_text.as_raw(),
    };
    let syntax_error = jvalue {
      l: java_string_syntax_error.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_error,
          ReturnType::Object,
          &[text, syntax_error, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenError")
        .l()
        .expect("Couldn't convert Swc4jTokenError")
    };
    env
      .delete_local_ref(java_string_text)
      .expect("Couldn't delete local text");
    token
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
        .expect("Couldn't create Swc4jTokenFalse")
        .l()
        .expect("Couldn't convert Swc4jTokenFalse")
    }
  }

  pub fn create_generic_operator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: TokenType,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_token_type = unsafe { JAVA_TOKEN_TYPE.as_ref().unwrap() };
    let java_token_type = java_token_type.parse(env, token_type.get_id());
    let token_type = jvalue {
      l: java_token_type.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_generic_operator,
          ReturnType::Object,
          &[token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenGenericOperator")
        .l()
        .expect("Couldn't convert Swc4jTokenGenericOperator")
    };
    env
      .delete_local_ref(java_token_type)
      .expect("Couldn't delete local ast token type");
    token
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
    let java_string = converter::string_to_jstring(env, &text);
    let text = jvalue {
      l: java_string.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_ident_known,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenIdentKnown")
        .l()
        .expect("Couldn't convert Swc4jTokenIdentKnown")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local ident");
    token
  }

  pub fn create_jsx_tag_name<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string = converter::string_to_jstring(env, &text);
    let text = jvalue {
      l: java_string.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_jsx_tag_name,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenJsxName")
        .l()
        .expect("Couldn't convert Swc4jTokenJsxName")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local text");
    token
  }

  pub fn create_jsx_tag_text<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string = converter::string_to_jstring(env, &text);
    let text = jvalue {
      l: java_string.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_jsx_tag_text,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenJsxText")
        .l()
        .expect("Couldn't convert Swc4jTokenJsxText")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local text");
    token
  }

  pub fn create_keyword<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: TokenType,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_token_type = unsafe { JAVA_TOKEN_TYPE.as_ref().unwrap() };
    let java_token_type = java_token_type.parse(env, token_type.get_id());
    let token_type = jvalue {
      l: java_token_type.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_keyword,
          ReturnType::Object,
          &[token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenKeyword")
        .l()
        .expect("Couldn't convert Swc4jTokenKeyword")
    };
    env
      .delete_local_ref(java_token_type)
      .expect("Couldn't delete local ast token type");
    token
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
        .expect("Couldn't create Swc4jTokenNull")
        .l()
        .expect("Couldn't convert Swc4jTokenNull")
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
    let java_string = converter::string_to_jstring(env, &text);
    let text = jvalue {
      l: java_string.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_ident_other,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenIdentOther")
        .l()
        .expect("Couldn't convert Swc4jTokenIdentOther")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local ident");
    token
  }

  pub fn create_number<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: f64,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string = converter::string_to_jstring(env, &text);
    let text = jvalue {
      l: java_string.as_raw(),
    };
    let value = jvalue { d: value };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_number,
          ReturnType::Object,
          &[text, value, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenNumber")
        .l()
        .expect("Couldn't convert Swc4jTokenNumber")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local text");
    token
  }

  pub fn create_regex<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &str,
    flags: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string_text = converter::string_to_jstring(env, &text);
    let java_string_value = converter::string_to_jstring(env, &value);
    let java_string_flags = converter::string_to_jstring(env, &flags);
    let text = jvalue {
      l: java_string_text.as_raw(),
    };
    let value = jvalue {
      l: java_string_value.as_raw(),
    };
    let flags = jvalue {
      l: java_string_flags.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_regex,
          ReturnType::Object,
          &[text, value, flags, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenRegex")
        .l()
        .expect("Couldn't convert Swc4jTokenRegex")
    };
    env
      .delete_local_ref(java_string_text)
      .expect("Couldn't delete local text");
    env
      .delete_local_ref(java_string_value)
      .expect("Couldn't delete local value");
    env
      .delete_local_ref(java_string_flags)
      .expect("Couldn't delete local flags");
    token
  }

  pub fn create_shebang<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string_text = converter::string_to_jstring(env, &text);
    let java_string_value = converter::string_to_jstring(env, &value);
    let text = jvalue {
      l: java_string_text.as_raw(),
    };
    let value = jvalue {
      l: java_string_value.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_shebang,
          ReturnType::Object,
          &[text, value, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenShebang")
        .l()
        .expect("Couldn't convert Swc4jTokenShebang")
    };
    env
      .delete_local_ref(java_string_text)
      .expect("Couldn't delete local text");
    env
      .delete_local_ref(java_string_value)
      .expect("Couldn't delete local value");
    token
  }

  pub fn create_string<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &str,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string_text = converter::string_to_jstring(env, &text);
    let java_string_value = converter::string_to_jstring(env, &value);
    let text = jvalue {
      l: java_string_text.as_raw(),
    };
    let value = jvalue {
      l: java_string_value.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_string,
          ReturnType::Object,
          &[text, value, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenString")
        .l()
        .expect("Couldn't convert Swc4jTokenString")
    };
    env
      .delete_local_ref(java_string_text)
      .expect("Couldn't delete local text");
    env
      .delete_local_ref(java_string_value)
      .expect("Couldn't delete local value");
    token
  }

  pub fn create_template<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: Option<&str>,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_string_text = converter::string_to_jstring(env, &text);
    let java_string_value = match value {
      Some(value) => converter::string_to_jstring(env, &value),
      None => Default::default(),
    };
    let text = jvalue {
      l: java_string_text.as_raw(),
    };
    let value = jvalue {
      l: java_string_value.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_template,
          ReturnType::Object,
          &[text, value, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenTemplate")
        .l()
        .expect("Couldn't convert Swc4jTokenTemplate")
    };
    env
      .delete_local_ref(java_string_text)
      .expect("Couldn't delete local text");
    env
      .delete_local_ref(java_string_value)
      .expect("Couldn't delete local value");
    token
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
        .expect("Couldn't create Swc4jTokenTrue")
        .l()
        .expect("Couldn't convert Swc4jTokenTrue")
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
    let java_string = converter::string_to_jstring(env, &text);
    let text = jvalue {
      l: java_string.as_raw(),
    };
    let start_position = jvalue { i: range.start as i32 };
    let end_position = jvalue { i: range.end as i32 };
    let line_break_ahead = jvalue {
      z: line_break_ahead as u8,
    };
    let token = unsafe {
      env
        .call_static_method_unchecked(
          &self.class,
          self.method_create_unknown,
          ReturnType::Object,
          &[text, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jTokenUnknown")
        .l()
        .expect("Couldn't convert Swc4jTokenUnknown")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local text");
    token
  }
}

pub static mut JAVA_token_FACTORY: Option<JavaAstTokenFactory> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_token_FACTORY = Some(JavaAstTokenFactory::new(env));
  }
}

pub fn token_and_spans_to_java_list<'local>(
  env: &mut JNIEnv<'local>,
  byte_to_index_map: &ByteToIndexMap,
  source_text: &str,
  token_and_spans: Option<Arc<Vec<TokenAndSpan>>>,
) -> jvalue {
  jvalue {
    l: match token_and_spans {
      Some(token_and_spans) => {
        let java_array_list = unsafe { JAVA_ARRAY_LIST.as_ref().unwrap() };
        let java_token_factory = unsafe { JAVA_token_FACTORY.as_ref().unwrap() };
        let list = java_array_list.create(env, token_and_spans.len());
        token_and_spans.iter().for_each(|token_and_span| {
          let line_break_ahead = token_and_span.had_line_break;
          let text = &source_text[Range {
            start: token_and_span.span.lo().to_usize() - 1,
            end: token_and_span.span.hi().to_usize() - 1,
          }];
          let index_range = byte_to_index_map.get_range_by_span(&token_and_span.span);
          let token = match &token_and_span.token {
            Token::Word(word) => match word {
              Word::Keyword(keyword) => java_token_factory.create_keyword(
                env,
                TokenType::parse_by_keyword(&keyword),
                index_range,
                line_break_ahead,
              ),
              Word::Null => java_token_factory.create_null(env, index_range, line_break_ahead),
              Word::True => java_token_factory.create_true(env, index_range, line_break_ahead),
              Word::False => java_token_factory.create_false(env, index_range, line_break_ahead),
              Word::Ident(ident) => match ident {
                IdentLike::Known(known_ident) => java_token_factory.create_ident_known(
                  env,
                  &Atom::from(*known_ident),
                  index_range,
                  line_break_ahead,
                ),
                IdentLike::Other(js_word) => {
                  java_token_factory.create_ident_other(env, &js_word, index_range, line_break_ahead)
                }
              },
            },
            Token::BinOp(bin_op) => java_token_factory.create_binary_operator(
              env,
              TokenType::parse_by_binary_operator(bin_op),
              index_range,
              line_break_ahead,
            ),
            Token::AssignOp(assign_op) => java_token_factory.create_assign_operator(
              env,
              TokenType::parse_by_assign_operator(assign_op),
              index_range,
              line_break_ahead,
            ),
            Token::Str { value, raw } => {
              java_token_factory.create_string(env, &raw, &value, index_range, line_break_ahead)
            }
            Token::Num { value, raw } => {
              java_token_factory.create_number(env, &raw, *value, index_range, line_break_ahead)
            }
            Token::BigInt { value: _, raw } => {
              java_token_factory.create_bigint(env, &raw, index_range, line_break_ahead)
            }
            Token::Regex(value, flags) => {
              java_token_factory.create_regex(env, &text, &value, &flags, index_range, line_break_ahead)
            }
            Token::Template { raw, cooked } => {
              let cooked = match &cooked {
                Ok(atom) => Some(atom.as_str()),
                Err(_) => None,
              };
              java_token_factory.create_template(env, &raw, cooked, index_range, line_break_ahead)
            }
            Token::Shebang(shebang) => {
              java_token_factory.create_shebang(env, &text, &shebang, index_range, line_break_ahead)
            }
            Token::Error(error) => {
              java_token_factory.create_error(env, &text, &error, index_range, line_break_ahead)
            }
            Token::JSXName { name } => {
              java_token_factory.create_jsx_tag_name(env, &name, index_range, line_break_ahead)
            }
            Token::JSXText { raw } => {
              java_token_factory.create_jsx_tag_text(env, &raw, index_range, line_break_ahead)
            }
            token => match &TokenType::parse_by_generic_operator(token) {
              TokenType::Unknown => {
                eprintln!("Unknown {:?}", token);
                java_token_factory.create_unknown(env, &text, index_range, line_break_ahead)
              }
              generic_operator_type => java_token_factory.create_generic_operator(
                env,
                *generic_operator_type,
                index_range,
                line_break_ahead,
              ),
            },
          };
          java_array_list.add(env, &list, &token);
          env
            .delete_local_ref(token)
            .expect("Couldn't delete local ast token");
        });
        list.as_raw()
      }
      None => null_mut(),
    },
  }
}
