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

use jni::objects::{GlobalRef, JMethodID, JObject, JStaticMethodID};
use jni::signature::ReturnType;
use jni::sys::jvalue;
use jni::JNIEnv;

use deno_ast::swc::parser::token::Keyword;
pub use deno_ast::{ImportsNotUsedAsValues, MediaType};

use crate::jni_utils;

pub trait IdentifiableEnum<T> {
  fn get_id(&self) -> i32;
  fn parse_by_id(id: i32) -> T;
}

#[derive(Debug, Copy, Clone)]
pub enum AstTokenType {
  Unknown,    // 0
  Await,      // 1
  Break,      // 2
  Case,       // 3
  Catch,      // 4
  Class,      // 5
  Const,      // 6
  Continue,   // 7
  Debugger,   // 8
  Default_,   // 9
  Delete,     // 10
  Do,         // 11
  Else,       // 12
  Export,     // 13
  Extends,    // 14
  Finally,    // 15
  For,        // 16
  Function,   // 17
  If,         // 18
  Import,     // 19
  In,         // 20
  InstanceOf, // 21
  Let,        // 22
  New,        // 23
  Return,     // 24
  Super,      // 25
  Switch,     // 26
  This,       // 27
  Throw,      // 28
  Try,        // 29
  TypeOf,     // 30
  Var,        // 31
  Void,       // 32
  While,      // 33
  With,       // 34
  Yield,      // 35
  Null,       // 36
  True,       // 37
  False,      // 38
  IdentKnown, // 39
  IdentOther, // 40
  Arrow,      // 41
  Hash,       // 42
  At,         // 43
  Dot,        // 44
  DotDotDot,  // 45
  Bang,       // 46
  LParen,     // 47
  RParen,     // 48
  LBracket,   // 49
  RBracket,   // 50
  LBrace,     // 51
  RBrace,     // 52
  Semi,       // 53
  Comma,      // 54
}

impl IdentifiableEnum<AstTokenType> for AstTokenType {
  fn get_id(&self) -> i32 {
    match self {
      AstTokenType::Await => 1,
      AstTokenType::Break => 2,
      AstTokenType::Case => 3,
      AstTokenType::Catch => 4,
      AstTokenType::Class => 5,
      AstTokenType::Const => 6,
      AstTokenType::Continue => 7,
      AstTokenType::Debugger => 8,
      AstTokenType::Default_ => 9,
      AstTokenType::Delete => 10,
      AstTokenType::Do => 11,
      AstTokenType::Else => 12,
      AstTokenType::Export => 13,
      AstTokenType::Extends => 14,
      AstTokenType::Finally => 15,
      AstTokenType::For => 16,
      AstTokenType::Function => 17,
      AstTokenType::If => 18,
      AstTokenType::Import => 19,
      AstTokenType::In => 20,
      AstTokenType::InstanceOf => 21,
      AstTokenType::Let => 22,
      AstTokenType::New => 23,
      AstTokenType::Return => 24,
      AstTokenType::Super => 25,
      AstTokenType::Switch => 26,
      AstTokenType::This => 27,
      AstTokenType::Throw => 28,
      AstTokenType::Try => 29,
      AstTokenType::TypeOf => 30,
      AstTokenType::Var => 31,
      AstTokenType::Void => 32,
      AstTokenType::While => 33,
      AstTokenType::With => 34,
      AstTokenType::Yield => 35,
      AstTokenType::Null => 36,
      AstTokenType::True => 37,
      AstTokenType::False => 38,
      AstTokenType::IdentKnown => 39,
      AstTokenType::IdentOther => 40,
      AstTokenType::Arrow => 41,
      AstTokenType::Hash => 42,
      AstTokenType::At => 43,
      AstTokenType::Dot => 44,
      AstTokenType::DotDotDot => 45,
      AstTokenType::Bang => 46,
      AstTokenType::LParen => 47,
      AstTokenType::RParen => 48,
      AstTokenType::LBracket => 49,
      AstTokenType::RBracket => 50,
      AstTokenType::LBrace => 51,
      AstTokenType::RBrace => 52,
      AstTokenType::Semi => 53,
      AstTokenType::Comma => 54,
      _ => 0,
    }
  }
  fn parse_by_id(id: i32) -> AstTokenType {
    match id {
      1 => AstTokenType::Await,
      2 => AstTokenType::Break,
      3 => AstTokenType::Case,
      4 => AstTokenType::Catch,
      5 => AstTokenType::Class,
      6 => AstTokenType::Const,
      7 => AstTokenType::Continue,
      8 => AstTokenType::Debugger,
      9 => AstTokenType::Default_,
      10 => AstTokenType::Delete,
      11 => AstTokenType::Do,
      12 => AstTokenType::Else,
      13 => AstTokenType::Export,
      14 => AstTokenType::Extends,
      15 => AstTokenType::Finally,
      16 => AstTokenType::For,
      17 => AstTokenType::Function,
      18 => AstTokenType::If,
      19 => AstTokenType::Import,
      20 => AstTokenType::In,
      21 => AstTokenType::InstanceOf,
      22 => AstTokenType::Let,
      23 => AstTokenType::New,
      24 => AstTokenType::Return,
      25 => AstTokenType::Super,
      26 => AstTokenType::Switch,
      27 => AstTokenType::This,
      28 => AstTokenType::Throw,
      29 => AstTokenType::Try,
      30 => AstTokenType::TypeOf,
      31 => AstTokenType::Var,
      32 => AstTokenType::Void,
      33 => AstTokenType::While,
      34 => AstTokenType::With,
      35 => AstTokenType::Yield,
      36 => AstTokenType::Null,
      37 => AstTokenType::True,
      38 => AstTokenType::False,
      39 => AstTokenType::IdentKnown,
      40 => AstTokenType::IdentOther,
      41 => AstTokenType::Arrow,
      42 => AstTokenType::Hash,
      43 => AstTokenType::At,
      44 => AstTokenType::Dot,
      45 => AstTokenType::DotDotDot,
      46 => AstTokenType::Bang,
      47 => AstTokenType::LParen,
      48 => AstTokenType::RParen,
      49 => AstTokenType::LBracket,
      50 => AstTokenType::RBracket,
      51 => AstTokenType::LBrace,
      52 => AstTokenType::RBrace,
      53 => AstTokenType::Semi,
      54 => AstTokenType::Comma,
      _ => AstTokenType::Unknown,
    }
  }
}

impl AstTokenType {
  pub fn parse_by_keyword(keyword: &Keyword) -> AstTokenType {
    match keyword {
      Keyword::Await => AstTokenType::Await,
      Keyword::Break => AstTokenType::Break,
      Keyword::Case => AstTokenType::Case,
      Keyword::Catch => AstTokenType::Catch,
      Keyword::Class => AstTokenType::Class,
      Keyword::Const => AstTokenType::Const,
      Keyword::Continue => AstTokenType::Continue,
      Keyword::Debugger => AstTokenType::Debugger,
      Keyword::Default_ => AstTokenType::Default_,
      Keyword::Delete => AstTokenType::Delete,
      Keyword::Do => AstTokenType::Do,
      Keyword::Else => AstTokenType::Else,
      Keyword::Export => AstTokenType::Export,
      Keyword::Extends => AstTokenType::Extends,
      Keyword::Finally => AstTokenType::Finally,
      Keyword::For => AstTokenType::For,
      Keyword::Function => AstTokenType::Function,
      Keyword::If => AstTokenType::If,
      Keyword::Import => AstTokenType::Import,
      Keyword::In => AstTokenType::In,
      Keyword::InstanceOf => AstTokenType::InstanceOf,
      Keyword::Let => AstTokenType::Let,
      Keyword::New => AstTokenType::New,
      Keyword::Return => AstTokenType::Return,
      Keyword::Super => AstTokenType::Super,
      Keyword::Switch => AstTokenType::Switch,
      Keyword::This => AstTokenType::This,
      Keyword::Throw => AstTokenType::Throw,
      Keyword::Try => AstTokenType::Try,
      Keyword::TypeOf => AstTokenType::TypeOf,
      Keyword::Var => AstTokenType::Var,
      Keyword::Void => AstTokenType::Void,
      Keyword::While => AstTokenType::While,
      Keyword::With => AstTokenType::With,
      Keyword::Yield => AstTokenType::Yield,
    }
  }
}

pub struct JavaAstTokenType {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
  method_parse: JStaticMethodID,
}
unsafe impl Send for JavaAstTokenType {}
unsafe impl Sync for JavaAstTokenType {}

impl JavaAstTokenType {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jAstTokenType")
      .expect("Couldn't find class Swc4jAstTokenType");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jAstTokenType");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jAstTokenType.getId");
    let method_parse = env
      .get_static_method_id(&class, "parse", "(I)Lcom/caoccao/javet/swc4j/enums/Swc4jAstTokenType;")
      .expect("Couldn't find static method Swc4jAstTokenType.parse");
    JavaAstTokenType {
      class,
      method_get_id,
      method_parse,
    }
  }

  pub fn get_ast_token_type<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> AstTokenType {
    let id = jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id);
    AstTokenType::parse_by_id(id)
  }

  pub fn parse<'local>(&self, env: &mut JNIEnv<'local>, id: i32) -> jvalue {
    let id = jvalue { i: id };
    unsafe {
      env
        .call_static_method_unchecked(&self.class, self.method_parse, ReturnType::Object, &[id])
        .expect("Object is expected")
        .as_jni()
    }
  }
}

impl IdentifiableEnum<ImportsNotUsedAsValues> for ImportsNotUsedAsValues {
  fn get_id(&self) -> i32 {
    match self {
      ImportsNotUsedAsValues::Remove => 0,
      ImportsNotUsedAsValues::Preserve => 1,
      ImportsNotUsedAsValues::Error => 2,
    }
  }
  fn parse_by_id(id: i32) -> ImportsNotUsedAsValues {
    match id {
      0 => ImportsNotUsedAsValues::Remove,
      1 => ImportsNotUsedAsValues::Preserve,
      _ => ImportsNotUsedAsValues::Error,
    }
  }
}

pub struct JavaImportsNotUsedAsValues {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
}
unsafe impl Send for JavaImportsNotUsedAsValues {}
unsafe impl Sync for JavaImportsNotUsedAsValues {}

impl JavaImportsNotUsedAsValues {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jImportsNotUsedAsValues")
      .expect("Couldn't find class Swc4jImportsNotUsedAsValues");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jImportsNotUsedAsValues");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jImportsNotUsedAsValues.getId");
    JavaImportsNotUsedAsValues { class, method_get_id }
  }

  pub fn get_imports_not_used_as_values<'local, 'a>(
    &self,
    env: &mut JNIEnv<'local>,
    obj: &JObject<'a>,
  ) -> ImportsNotUsedAsValues {
    let id = jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id);
    ImportsNotUsedAsValues::parse_by_id(id)
  }
}

impl IdentifiableEnum<MediaType> for MediaType {
  fn get_id(&self) -> i32 {
    match self {
      MediaType::JavaScript => 0,
      MediaType::Jsx => 1,
      MediaType::Mjs => 2,
      MediaType::Cjs => 3,
      MediaType::TypeScript => 4,
      MediaType::Mts => 5,
      MediaType::Cts => 6,
      MediaType::Dts => 7,
      MediaType::Dmts => 8,
      MediaType::Dcts => 9,
      MediaType::Tsx => 10,
      MediaType::Json => 11,
      MediaType::Wasm => 12,
      MediaType::TsBuildInfo => 13,
      MediaType::SourceMap => 14,
      MediaType::Unknown => 15,
    }
  }
  fn parse_by_id(id: i32) -> MediaType {
    match id {
      0 => MediaType::JavaScript,
      1 => MediaType::Jsx,
      2 => MediaType::Mjs,
      3 => MediaType::Cjs,
      4 => MediaType::TypeScript,
      5 => MediaType::Mts,
      6 => MediaType::Cts,
      7 => MediaType::Dts,
      8 => MediaType::Dmts,
      9 => MediaType::Dcts,
      10 => MediaType::Tsx,
      11 => MediaType::Json,
      12 => MediaType::Wasm,
      13 => MediaType::TsBuildInfo,
      14 => MediaType::SourceMap,
      _ => MediaType::Unknown,
    }
  }
}

pub struct JavaMediaType {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
  method_parse: JStaticMethodID,
}
unsafe impl Send for JavaMediaType {}
unsafe impl Sync for JavaMediaType {}

impl JavaMediaType {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jMediaType")
      .expect("Couldn't find class Swc4jMediaType");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jMediaType");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jMediaType.getId");
    let method_parse = env
      .get_static_method_id(&class, "parse", "(I)Lcom/caoccao/javet/swc4j/enums/Swc4jMediaType;")
      .expect("Couldn't find static method Swc4jMediaType.parse");
    JavaMediaType {
      class,
      method_get_id,
      method_parse,
    }
  }

  pub fn get_media_type<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> MediaType {
    let id = jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id);
    MediaType::parse_by_id(id)
  }

  pub fn parse<'local>(&self, env: &mut JNIEnv<'local>, id: i32) -> jvalue {
    let id = jvalue { i: id };
    unsafe {
      env
        .call_static_method_unchecked(&self.class, self.method_parse, ReturnType::Object, &[id])
        .expect("Object is expected")
        .as_jni()
    }
  }
}

#[derive(Debug, Copy, Clone)]
pub enum ParseMode {
  Module,
  Script,
}

impl IdentifiableEnum<ParseMode> for ParseMode {
  fn get_id(&self) -> i32 {
    match self {
      ParseMode::Module => 0,
      ParseMode::Script => 1,
    }
  }
  fn parse_by_id(id: i32) -> ParseMode {
    match id {
      0 => ParseMode::Module,
      _ => ParseMode::Script,
    }
  }
}

pub struct JavaParseMode {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
}
unsafe impl Send for JavaParseMode {}
unsafe impl Sync for JavaParseMode {}

impl JavaParseMode {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/enums/Swc4jParseMode")
      .expect("Couldn't find class Swc4jParseMode");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jParseMode");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jParseMode.getId");
    JavaParseMode { class, method_get_id }
  }

  pub fn get_parse_mode<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> ParseMode {
    let id = jni_utils::get_as_int(env, obj.as_ref(), self.method_get_id);
    ParseMode::parse_by_id(id)
  }
}

pub static mut JAVA_AST_TOKEN_TYPE: Option<JavaAstTokenType> = None;
pub static mut JAVA_IMPORTS_NOT_USED_AS_VALUES: Option<JavaImportsNotUsedAsValues> = None;
pub static mut JAVA_MEDIA_TYPE: Option<JavaMediaType> = None;
pub static mut JAVA_PARSE_MODE: Option<JavaParseMode> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_AST_TOKEN_TYPE = Some(JavaAstTokenType::new(env));
    JAVA_IMPORTS_NOT_USED_AS_VALUES = Some(JavaImportsNotUsedAsValues::new(env));
    JAVA_MEDIA_TYPE = Some(JavaMediaType::new(env));
    JAVA_PARSE_MODE = Some(JavaParseMode::new(env));
  }
}
