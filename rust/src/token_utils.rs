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
use jni::JNIEnv;

use deno_ast::swc::atoms::Atom;
use deno_ast::swc::common::source_map::SmallPos;
use deno_ast::swc::parser::error::Error;
use deno_ast::swc::parser::token::{IdentLike, Token, TokenAndSpan, Word};

use crate::enums::*;
use crate::jni_utils::*;
use crate::span_utils::ByteToIndexMap;

use std::ops::Range;
use std::sync::Arc;

/* JavaSwc4jTokenFactory Begin */
#[allow(dead_code)]
struct JavaSwc4jTokenFactory {
  class: GlobalRef,
  method_create_assign_operator: JStaticMethodID,
  method_create_big_int: JStaticMethodID,
  method_create_binary_operator: JStaticMethodID,
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
unsafe impl Send for JavaSwc4jTokenFactory {}
unsafe impl Sync for JavaSwc4jTokenFactory {}

#[allow(dead_code)]
impl JavaSwc4jTokenFactory {
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
        "(ILcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createAssignOperator");
    let method_create_big_int = env
      .get_static_method_id(
        &class,
        "createBigInt",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createBigInt");
    let method_create_binary_operator = env
      .get_static_method_id(
        &class,
        "createBinaryOperator",
        "(ILcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createBinaryOperator");
    let method_create_error = env
      .get_static_method_id(
        &class,
        "createError",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createError");
    let method_create_false = env
      .get_static_method_id(
        &class,
        "createFalse",
        "(Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createFalse");
    let method_create_generic_operator = env
      .get_static_method_id(
        &class,
        "createGenericOperator",
        "(ILcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createGenericOperator");
    let method_create_ident_known = env
      .get_static_method_id(
        &class,
        "createIdentKnown",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createIdentKnown");
    let method_create_ident_other = env
      .get_static_method_id(
        &class,
        "createIdentOther",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createIdentOther");
    let method_create_jsx_tag_name = env
      .get_static_method_id(
        &class,
        "createJsxTagName",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createJsxTagName");
    let method_create_jsx_tag_text = env
      .get_static_method_id(
        &class,
        "createJsxTagText",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createJsxTagText");
    let method_create_keyword = env
      .get_static_method_id(
        &class,
        "createKeyword",
        "(ILcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createKeyword");
    let method_create_null = env
      .get_static_method_id(
        &class,
        "createNull",
        "(Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createNull");
    let method_create_number = env
      .get_static_method_id(
        &class,
        "createNumber",
        "(Ljava/lang/String;DLcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createNumber");
    let method_create_regex = env
      .get_static_method_id(
        &class,
        "createRegex",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValueFlags;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createRegex");
    let method_create_shebang = env
      .get_static_method_id(
        &class,
        "createShebang",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createShebang");
    let method_create_string = env
      .get_static_method_id(
        &class,
        "createString",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createString");
    let method_create_template = env
      .get_static_method_id(
        &class,
        "createTemplate",
        "(Ljava/lang/String;Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenTextValue;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createTemplate");
    let method_create_true = env
      .get_static_method_id(
        &class,
        "createTrue",
        "(Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jToken;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createTrue");
    let method_create_unknown = env
      .get_static_method_id(
        &class,
        "createUnknown",
        "(Ljava/lang/String;Lcom/caoccao/javet/swc4j/span/Swc4jSpan;Z)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenText;",
      )
      .expect("Couldn't find method Swc4jTokenFactory.createUnknown");
    JavaSwc4jTokenFactory {
      class,
      method_create_assign_operator,
      method_create_big_int,
      method_create_binary_operator,
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
      method_create_shebang,
      method_create_string,
      method_create_template,
      method_create_true,
      method_create_unknown,
    }
  }

  pub fn create_assign_operator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: &TokenType,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let type_id = int_to_jvalue!(token_type.get_id());
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_assign_operator,
        &[type_id, span, line_break_ahead],
        "Swc4jToken create_assign_operator()"
      );
    return_value
  }

  pub fn create_big_int<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_big_int,
        &[text, span, line_break_ahead],
        "Swc4jTokenTextValue create_big_int()"
      );
    delete_local_ref!(env, java_text);
    return_value
  }

  pub fn create_binary_operator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: &TokenType,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let type_id = int_to_jvalue!(token_type.get_id());
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_binary_operator,
        &[type_id, span, line_break_ahead],
        "Swc4jToken create_binary_operator()"
      );
    return_value
  }

  pub fn create_error<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    error: &Error,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let java_error = string_to_jstring!(env, &format!("{:?}", error));
    let error = object_to_jvalue!(java_error);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_error,
        &[text, error, span, line_break_ahead],
        "Swc4jTokenTextValue create_error()"
      );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_error);
    return_value
  }

  pub fn create_false<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_false,
        &[span, line_break_ahead],
        "Swc4jToken create_false()"
      );
    return_value
  }

  pub fn create_generic_operator<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: &TokenType,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let type_id = int_to_jvalue!(token_type.get_id());
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_generic_operator,
        &[type_id, span, line_break_ahead],
        "Swc4jToken create_generic_operator()"
      );
    return_value
  }

  pub fn create_ident_known<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ident_known,
        &[text, span, line_break_ahead],
        "Swc4jTokenText create_ident_known()"
      );
    delete_local_ref!(env, java_text);
    return_value
  }

  pub fn create_ident_other<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_ident_other,
        &[text, span, line_break_ahead],
        "Swc4jTokenText create_ident_other()"
      );
    delete_local_ref!(env, java_text);
    return_value
  }

  pub fn create_jsx_tag_name<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_tag_name,
        &[text, span, line_break_ahead],
        "Swc4jTokenText create_jsx_tag_name()"
      );
    delete_local_ref!(env, java_text);
    return_value
  }

  pub fn create_jsx_tag_text<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_jsx_tag_text,
        &[text, value, span, line_break_ahead],
        "Swc4jTokenTextValue create_jsx_tag_text()"
      );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_value);
    return_value
  }

  pub fn create_keyword<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    token_type: &TokenType,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let type_id = int_to_jvalue!(token_type.get_id());
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_keyword,
        &[type_id, span, line_break_ahead],
        "Swc4jToken create_keyword()"
      );
    return_value
  }

  pub fn create_null<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_null,
        &[span, line_break_ahead],
        "Swc4jToken create_null()"
      );
    return_value
  }

  pub fn create_number<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: f64,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let value = double_to_jvalue!(value);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_number,
        &[text, value, span, line_break_ahead],
        "Swc4jTokenTextValue create_number()"
      );
    delete_local_ref!(env, java_text);
    return_value
  }

  pub fn create_regex<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &str,
    flags: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let java_flags = string_to_jstring!(env, &flags);
    let flags = object_to_jvalue!(java_flags);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_regex,
        &[text, value, flags, span, line_break_ahead],
        "Swc4jTokenTextValueFlags create_regex()"
      );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_value);
    delete_local_ref!(env, java_flags);
    return_value
  }

  pub fn create_shebang<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_shebang,
        &[text, value, span, line_break_ahead],
        "Swc4jTokenTextValue create_shebang()"
      );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_value);
    return_value
  }

  pub fn create_string<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let java_value = string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_string,
        &[text, value, span, line_break_ahead],
        "Swc4jTokenTextValue create_string()"
      );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_value);
    return_value
  }

  pub fn create_template<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    value: &Option<String>,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let java_value = optional_string_to_jstring!(env, &value);
    let value = object_to_jvalue!(java_value);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_template,
        &[text, value, span, line_break_ahead],
        "Swc4jTokenTextValue create_template()"
      );
    delete_local_ref!(env, java_text);
    delete_local_ref!(env, java_value);
    return_value
  }

  pub fn create_true<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_true,
        &[span, line_break_ahead],
        "Swc4jToken create_true()"
      );
    return_value
  }

  pub fn create_unknown<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    text: &str,
    span: &JObject<'_>,
    line_break_ahead: bool,
  ) -> JObject<'a>
  where
    'local: 'a,
  {
    let java_text = string_to_jstring!(env, &text);
    let text = object_to_jvalue!(java_text);
    let span = object_to_jvalue!(span);
    let line_break_ahead = boolean_to_jvalue!(line_break_ahead);
    let return_value = call_static_as_object!(
        env,
        &self.class,
        self.method_create_unknown,
        &[text, span, line_break_ahead],
        "Swc4jTokenText create_unknown()"
      );
    delete_local_ref!(env, java_text);
    return_value
  }
}
/* JavaSwc4jTokenFactory End */

static mut JAVA_TOKEN_FACTORY: Option<JavaSwc4jTokenFactory> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_TOKEN_FACTORY = Some(JavaSwc4jTokenFactory::new(env));
  }
}

pub fn token_and_spans_to_java_list<'local, 'a>(
  env: &mut JNIEnv<'local>,
  map: &ByteToIndexMap,
  source_text: &str,
  token_and_spans: Option<Arc<Vec<TokenAndSpan>>>,
) -> JObject<'a>
where
  'local: 'a,
{
  match token_and_spans {
    Some(token_and_spans) => {
      let java_token_factory = unsafe { JAVA_TOKEN_FACTORY.as_ref().unwrap() };
      let list = list_new(env, token_and_spans.len());
      token_and_spans.iter().for_each(|token_and_span| {
        let line_break_ahead = token_and_span.had_line_break;
        let text = &source_text[Range {
          start: token_and_span.span.lo().to_usize() - 1,
          end: token_and_span.span.hi().to_usize() - 1,
        }];
        let java_span_ex = map.get_span_ex_by_span(&token_and_span.span).to_java(env);
        let java_token = match &token_and_span.token {
          Token::Word(word) => match word {
            Word::Keyword(keyword) => java_token_factory.create_keyword(
              env,
              &TokenType::parse_by_keyword(&keyword),
              &java_span_ex,
              line_break_ahead,
            ),
            Word::Null => java_token_factory.create_null(env, &java_span_ex, line_break_ahead),
            Word::True => java_token_factory.create_true(env, &java_span_ex, line_break_ahead),
            Word::False => java_token_factory.create_false(env, &java_span_ex, line_break_ahead),
            Word::Ident(ident) => match ident {
              IdentLike::Known(known_ident) => {
                java_token_factory.create_ident_known(env, &Atom::from(*known_ident), &java_span_ex, line_break_ahead)
              }
              IdentLike::Other(js_word) => {
                java_token_factory.create_ident_other(env, &js_word, &java_span_ex, line_break_ahead)
              }
            },
          },
          Token::BinOp(bin_op) => java_token_factory.create_binary_operator(
            env,
            &TokenType::parse_by_binary_operator(bin_op),
            &java_span_ex,
            line_break_ahead,
          ),
          Token::AssignOp(assign_op) => java_token_factory.create_assign_operator(
            env,
            &TokenType::parse_by_assign_operator(assign_op),
            &java_span_ex,
            line_break_ahead,
          ),
          Token::Str { value, raw } => {
            java_token_factory.create_string(env, &raw, &value, &java_span_ex, line_break_ahead)
          }
          Token::Num { value, raw } => {
            java_token_factory.create_number(env, &raw, *value, &java_span_ex, line_break_ahead)
          }
          Token::BigInt { value: _, raw } => {
            java_token_factory.create_big_int(env, &raw, &java_span_ex, line_break_ahead)
          }
          Token::Regex(value, flags) => {
            java_token_factory.create_regex(env, &text, &value, &flags, &java_span_ex, line_break_ahead)
          }
          Token::Template { raw, cooked } => {
            let cooked = match &cooked {
              Ok(atom) => Some(atom.as_str().to_owned()),
              Err(_) => None,
            };
            java_token_factory.create_template(env, &raw, &cooked, &java_span_ex, line_break_ahead)
          }
          Token::Shebang(shebang) => {
            java_token_factory.create_shebang(env, &text, &shebang, &java_span_ex, line_break_ahead)
          }
          Token::Error(error) => java_token_factory.create_error(env, &text, &error, &java_span_ex, line_break_ahead),
          Token::JSXName { name } => {
            java_token_factory.create_jsx_tag_name(env, &name, &java_span_ex, line_break_ahead)
          }
          Token::JSXText { value, raw } => {
            java_token_factory.create_jsx_tag_text(env, &value, &raw, &java_span_ex, line_break_ahead)
          }
          token => match &TokenType::parse_by_generic_operator(token) {
            TokenType::Unknown => {
              eprintln!("Unknown {:?}", token);
              java_token_factory.create_unknown(env, &text, &java_span_ex, line_break_ahead)
            }
            generic_operator_type => {
              java_token_factory.create_generic_operator(env, generic_operator_type, &java_span_ex, line_break_ahead)
            }
          },
        };
        list_add(env, &list, &java_token);
        delete_local_ref!(env, java_token);
        delete_local_ref!(env, java_span_ex);
      });
      list
    }
    None => Default::default(),
  }
}
