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
  parser::error::Error,
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
      .find_class("com/caoccao/javet/swc4j/ast/Swc4jAstTokenFactory")
      .expect("Couldn't find class Swc4jAstTokenFactory");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jAstTokenFactory");
    let method_create_assign_operator = env
      .get_static_method_id(
        &class,
        "createAssignOperator",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstToken;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createAssignOperator");
    let method_create_binary_operator = env
      .get_static_method_id(
        &class,
        "createBinaryOperator",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstToken;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createBinaryOperator");
    let method_create_bigint = env
      .get_static_method_id(
        &class,
        "createBigInt",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createBigInt");
    let method_create_error = env
      .get_static_method_id(
        &class,
        "createError",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createError");
    let method_create_false = env
      .get_static_method_id(
        &class,
        "createFalse",
        "(IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstToken;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createFalse");
    let method_create_generic_operator = env
      .get_static_method_id(
        &class,
        "createGenericOperator",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstToken;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createGenericOperator");
    let method_create_ident_known = env
      .get_static_method_id(
        &class,
        "createIdentKnown",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenText;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createIdentKnown");
    let method_create_jsx_tag_name = env
      .get_static_method_id(
        &class,
        "createJsxTagName",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenText;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createJsxTagName");
    let method_create_jsx_tag_text = env
      .get_static_method_id(
        &class,
        "createJsxTagText",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenText;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createJsxTagText");
    let method_create_keyword = env
      .get_static_method_id(
        &class,
        "createKeyword",
        "(Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstToken;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createKeyword");
    let method_create_null = env
      .get_static_method_id(
        &class,
        "createNull",
        "(IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstToken;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createNull");
    let method_create_ident_other = env
      .get_static_method_id(
        &class,
        "createIdentOther",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenText;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createIdentOther");
    let method_create_number = env
      .get_static_method_id(
        &class,
        "createNumber",
        "(Ljava/lang/String;DIIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createNumber");
    let method_create_regex = env
      .get_static_method_id(
        &class,
        "createRegex",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenTextValueFlags;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createRegex");
    let method_create_shebang = env
      .get_static_method_id(
        &class,
        "createShebang",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createShebang");
    let method_create_string = env
      .get_static_method_id(
        &class,
        "createString",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createString");
    let method_create_template = env
      .get_static_method_id(
        &class,
        "createTemplate",
        "(Ljava/lang/String;Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createTemplate");
    let method_create_true = env
      .get_static_method_id(
        &class,
        "createTrue",
        "(IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstToken;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createTrue");
    let method_create_unknown = env
      .get_static_method_id(
        &class,
        "createUnknown",
        "(Ljava/lang/String;IIZ)Lcom/caoccao/javet/swc4j/ast/Swc4jAstTokenText;",
      )
      .expect("Couldn't find method Swc4jAstTokenFactory.createUnknown");
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
    ast_token_type: AstTokenType,
    range: Range<usize>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_ast_token_type = unsafe { JAVA_AST_TOKEN_TYPE.as_ref().unwrap() };
    let java_ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let ast_token_type = jvalue {
      l: java_ast_token_type.as_raw(),
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
          &[ast_token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenAssignOperator")
        .l()
        .expect("Couldn't convert Swc4jAstTokenAssignOperator")
    };
    env
      .delete_local_ref(java_ast_token_type)
      .expect("Couldn't delete local ast token type");
    token
  }

  pub fn create_binary_operator<'local, 'a>(
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
    let java_ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let ast_token_type = jvalue {
      l: java_ast_token_type.as_raw(),
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
          &[ast_token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenBinaryOperator")
        .l()
        .expect("Couldn't convert Swc4jAstTokenBinaryOperator")
    };
    env
      .delete_local_ref(java_ast_token_type)
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
        .expect("Couldn't create Swc4jAstTokenBigInt")
        .l()
        .expect("Couldn't convert Swc4jAstTokenBigInt")
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
        .expect("Couldn't create Swc4jAstTokenError")
        .l()
        .expect("Couldn't convert Swc4jAstTokenError")
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
    let java_ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let ast_token_type = jvalue {
      l: java_ast_token_type.as_raw(),
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
          &[ast_token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenGenericOperator")
        .l()
        .expect("Couldn't convert Swc4jAstTokenGenericOperator")
    };
    env
      .delete_local_ref(java_ast_token_type)
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
        .expect("Couldn't create Swc4jAstTokenIdentKnown")
        .l()
        .expect("Couldn't convert Swc4jAstTokenIdentKnown")
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
        .expect("Couldn't create Swc4jAstTokenJsxName")
        .l()
        .expect("Couldn't convert Swc4jAstTokenJsxName")
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
        .expect("Couldn't create Swc4jAstTokenJsxText")
        .l()
        .expect("Couldn't convert Swc4jAstTokenJsxText")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local text");
    token
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
    let java_ast_token_type = java_ast_token_type.parse(env, ast_token_type.get_id());
    let ast_token_type = jvalue {
      l: java_ast_token_type.as_raw(),
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
          &[ast_token_type, start_position, end_position, line_break_ahead],
        )
        .expect("Couldn't create Swc4jAstTokenKeyword")
        .l()
        .expect("Couldn't convert Swc4jAstTokenKeyword")
    };
    env
      .delete_local_ref(java_ast_token_type)
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
        .expect("Couldn't create Swc4jAstTokenIdentOther")
        .l()
        .expect("Couldn't convert Swc4jAstTokenIdentOther")
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
        .expect("Couldn't create Swc4jAstTokenNumber")
        .l()
        .expect("Couldn't convert Swc4jAstTokenNumber")
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
        .expect("Couldn't create Swc4jAstTokenRegex")
        .l()
        .expect("Couldn't convert Swc4jAstTokenRegex")
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
        .expect("Couldn't create Swc4jAstTokenShebang")
        .l()
        .expect("Couldn't convert Swc4jAstTokenShebang")
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
        .expect("Couldn't create Swc4jAstTokenString")
        .l()
        .expect("Couldn't convert Swc4jAstTokenString")
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
        .expect("Couldn't create Swc4jAstTokenTemplate")
        .l()
        .expect("Couldn't convert Swc4jAstTokenTemplate")
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
        .expect("Couldn't create Swc4jAstTokenUnknown")
        .l()
        .expect("Couldn't convert Swc4jAstTokenUnknown")
    };
    env.delete_local_ref(java_string).expect("Couldn't delete local text");
    token
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
                  &Atom::from(*known_ident),
                  index_range,
                  line_break_ahead,
                ),
                IdentLike::Other(js_word) => {
                  java_ast_token_factory.create_ident_other(env, &js_word, index_range, line_break_ahead)
                }
              },
            },
            Token::BinOp(bin_op) => java_ast_token_factory.create_binary_operator(
              env,
              AstTokenType::parse_by_binary_operator(bin_op),
              index_range,
              line_break_ahead,
            ),
            Token::AssignOp(assign_op) => java_ast_token_factory.create_assign_operator(
              env,
              AstTokenType::parse_by_assign_operator(assign_op),
              index_range,
              line_break_ahead,
            ),
            Token::Str { value, raw } => {
              java_ast_token_factory.create_string(env, &raw, &value, index_range, line_break_ahead)
            }
            Token::Num { value, raw } => {
              java_ast_token_factory.create_number(env, &raw, *value, index_range, line_break_ahead)
            }
            Token::BigInt { value: _, raw } => {
              java_ast_token_factory.create_bigint(env, &raw, index_range, line_break_ahead)
            }
            Token::Regex(value, flags) => {
              java_ast_token_factory.create_regex(env, &text, &value, &flags, index_range, line_break_ahead)
            }
            Token::Template { raw, cooked } => {
              let cooked = match &cooked {
                Ok(atom) => Some(atom.as_str()),
                Err(_) => None,
              };
              java_ast_token_factory.create_template(env, &raw, cooked, index_range, line_break_ahead)
            }
            Token::Shebang(shebang) => {
              java_ast_token_factory.create_shebang(env, &text, &shebang, index_range, line_break_ahead)
            }
            Token::Error(error) => {
              java_ast_token_factory.create_error(env, &text, &error, index_range, line_break_ahead)
            }
            Token::JSXName { name } => {
              java_ast_token_factory.create_jsx_tag_name(env, &name, index_range, line_break_ahead)
            }
            Token::JSXText { raw } => {
              java_ast_token_factory.create_jsx_tag_text(env, &raw, index_range, line_break_ahead)
            }
            token => match &AstTokenType::parse_by_generic_operator(token) {
              AstTokenType::Unknown => {
                eprintln!("Unknown {:?}", token);
                java_ast_token_factory.create_unknown(env, &text, index_range, line_break_ahead)
              }
              generic_operator_type => java_ast_token_factory.create_generic_operator(
                env,
                *generic_operator_type,
                index_range,
                line_break_ahead,
              ),
            },
          };
          java_array_list.add(env, &list, &ast_token);
          env
            .delete_local_ref(ast_token)
            .expect("Couldn't delete local ast token");
        });
        list.as_raw()
      }
      None => null_mut(),
    },
  }
}
