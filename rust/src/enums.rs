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
use jni::signature::{Primitive, ReturnType};
use jni::sys::jvalue;
use jni::JNIEnv;

use deno_ast::swc::ast::AssignOp;
use deno_ast::swc::parser::token::{BinOpToken, Keyword, Token};
pub use deno_ast::{ImportsNotUsedAsValues, MediaType};

use crate::jni_utils::*;

pub trait IdentifiableEnum<T> {
  fn get_id(&self) -> i32;
  fn parse_by_id(id: i32) -> T;
}

#[derive(Debug, Copy, Clone)]
pub enum TokenType {
  Unknown, // 0
  // Keyword
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
  // Word
  Null,       // 36
  True,       // 37
  False,      // 38
  IdentKnown, // 39
  IdentOther, // 40
  // Operator - Generic
  Arrow,        // 41
  Hash,         // 42
  At,           // 43
  Dot,          // 44
  DotDotDot,    // 45
  Bang,         // 46
  LParen,       // 47
  RParen,       // 48
  LBracket,     // 49
  RBracket,     // 50
  LBrace,       // 51
  RBrace,       // 52
  Semi,         // 53
  Comma,        // 54
  BackQuote,    // 55
  Colon,        // 56
  DollarLBrace, // 57
  QuestionMark, // 58
  PlusPlus,     // 59
  MinusMinus,   // 60
  Tilde,        // 61
  // Operator - Binary
  EqEq,              // 62
  NotEq,             // 63
  EqEqEq,            // 64
  NotEqEq,           // 65
  Lt,                // 66
  LtEq,              // 67
  Gt,                // 68
  GtEq,              // 69
  LShift,            // 70
  RShift,            // 71
  ZeroFillRShift,    // 72
  Add,               // 73
  Sub,               // 74
  Mul,               // 75
  Div,               // 76
  Mod,               // 77
  BitOr,             // 78
  BitXor,            // 79
  BitAnd,            // 80
  Exp,               // 81
  LogicalOr,         // 82
  LogicalAnd,        // 83
  NullishCoalescing, // 84
  // Operator - Assign
  Assign,               // 85
  AddAssign,            // 86
  SubAssign,            // 87
  MulAssign,            // 88
  DivAssign,            // 89
  ModAssign,            // 90
  LShiftAssign,         // 91
  RShiftAssign,         // 92
  ZeroFillRShiftAssign, // 93
  BitOrAssign,          // 94
  BitXorAssign,         // 95
  BitAndAssign,         // 96
  ExpAssign,            // 97
  AndAssign,            // 98
  OrAssign,             // 99
  NullishAssign,        // 100
  // TextValue
  Shebang,  // 101
  Error,    // 102
  Str,      // 103
  Num,      // 104
  BigInt,   // 105
  Template, // 106
  // TextValueFlags
  Regex, // 107
  // Jsx
  JSXTagStart, // 108
  JSXTagEnd,   // 109
  JSXTagName,  // 110
  JSXTagText,  // 111
}

impl IdentifiableEnum<TokenType> for TokenType {
  fn get_id(&self) -> i32 {
    match self {
      TokenType::Await => 1,
      TokenType::Break => 2,
      TokenType::Case => 3,
      TokenType::Catch => 4,
      TokenType::Class => 5,
      TokenType::Const => 6,
      TokenType::Continue => 7,
      TokenType::Debugger => 8,
      TokenType::Default_ => 9,
      TokenType::Delete => 10,
      TokenType::Do => 11,
      TokenType::Else => 12,
      TokenType::Export => 13,
      TokenType::Extends => 14,
      TokenType::Finally => 15,
      TokenType::For => 16,
      TokenType::Function => 17,
      TokenType::If => 18,
      TokenType::Import => 19,
      TokenType::In => 20,
      TokenType::InstanceOf => 21,
      TokenType::Let => 22,
      TokenType::New => 23,
      TokenType::Return => 24,
      TokenType::Super => 25,
      TokenType::Switch => 26,
      TokenType::This => 27,
      TokenType::Throw => 28,
      TokenType::Try => 29,
      TokenType::TypeOf => 30,
      TokenType::Var => 31,
      TokenType::Void => 32,
      TokenType::While => 33,
      TokenType::With => 34,
      TokenType::Yield => 35,
      TokenType::Null => 36,
      TokenType::True => 37,
      TokenType::False => 38,
      TokenType::IdentKnown => 39,
      TokenType::IdentOther => 40,
      TokenType::Arrow => 41,
      TokenType::Hash => 42,
      TokenType::At => 43,
      TokenType::Dot => 44,
      TokenType::DotDotDot => 45,
      TokenType::Bang => 46,
      TokenType::LParen => 47,
      TokenType::RParen => 48,
      TokenType::LBracket => 49,
      TokenType::RBracket => 50,
      TokenType::LBrace => 51,
      TokenType::RBrace => 52,
      TokenType::Semi => 53,
      TokenType::Comma => 54,
      TokenType::BackQuote => 55,
      TokenType::Colon => 56,
      TokenType::DollarLBrace => 57,
      TokenType::QuestionMark => 58,
      TokenType::PlusPlus => 59,
      TokenType::MinusMinus => 60,
      TokenType::Tilde => 61,
      TokenType::EqEq => 62,
      TokenType::NotEq => 63,
      TokenType::EqEqEq => 64,
      TokenType::NotEqEq => 65,
      TokenType::Lt => 66,
      TokenType::LtEq => 67,
      TokenType::Gt => 68,
      TokenType::GtEq => 69,
      TokenType::LShift => 70,
      TokenType::RShift => 71,
      TokenType::ZeroFillRShift => 72,
      TokenType::Add => 73,
      TokenType::Sub => 74,
      TokenType::Mul => 75,
      TokenType::Div => 76,
      TokenType::Mod => 77,
      TokenType::BitOr => 78,
      TokenType::BitXor => 79,
      TokenType::BitAnd => 80,
      TokenType::Exp => 81,
      TokenType::LogicalOr => 82,
      TokenType::LogicalAnd => 83,
      TokenType::NullishCoalescing => 84,
      TokenType::Assign => 85,
      TokenType::AddAssign => 86,
      TokenType::SubAssign => 87,
      TokenType::MulAssign => 88,
      TokenType::DivAssign => 89,
      TokenType::ModAssign => 90,
      TokenType::LShiftAssign => 91,
      TokenType::RShiftAssign => 92,
      TokenType::ZeroFillRShiftAssign => 93,
      TokenType::BitOrAssign => 94,
      TokenType::BitXorAssign => 95,
      TokenType::BitAndAssign => 96,
      TokenType::ExpAssign => 97,
      TokenType::AndAssign => 98,
      TokenType::OrAssign => 99,
      TokenType::NullishAssign => 100,
      TokenType::Shebang => 101,
      TokenType::Error => 102,
      TokenType::Str => 103,
      TokenType::Num => 104,
      TokenType::BigInt => 105,
      TokenType::Template => 106,
      TokenType::Regex => 107,
      TokenType::JSXTagStart => 108,
      TokenType::JSXTagEnd => 109,
      TokenType::JSXTagName => 110,
      TokenType::JSXTagText => 111,
      _ => 0,
    }
  }
  fn parse_by_id(id: i32) -> TokenType {
    match id {
      1 => TokenType::Await,
      2 => TokenType::Break,
      3 => TokenType::Case,
      4 => TokenType::Catch,
      5 => TokenType::Class,
      6 => TokenType::Const,
      7 => TokenType::Continue,
      8 => TokenType::Debugger,
      9 => TokenType::Default_,
      10 => TokenType::Delete,
      11 => TokenType::Do,
      12 => TokenType::Else,
      13 => TokenType::Export,
      14 => TokenType::Extends,
      15 => TokenType::Finally,
      16 => TokenType::For,
      17 => TokenType::Function,
      18 => TokenType::If,
      19 => TokenType::Import,
      20 => TokenType::In,
      21 => TokenType::InstanceOf,
      22 => TokenType::Let,
      23 => TokenType::New,
      24 => TokenType::Return,
      25 => TokenType::Super,
      26 => TokenType::Switch,
      27 => TokenType::This,
      28 => TokenType::Throw,
      29 => TokenType::Try,
      30 => TokenType::TypeOf,
      31 => TokenType::Var,
      32 => TokenType::Void,
      33 => TokenType::While,
      34 => TokenType::With,
      35 => TokenType::Yield,
      36 => TokenType::Null,
      37 => TokenType::True,
      38 => TokenType::False,
      39 => TokenType::IdentKnown,
      40 => TokenType::IdentOther,
      41 => TokenType::Arrow,
      42 => TokenType::Hash,
      43 => TokenType::At,
      44 => TokenType::Dot,
      45 => TokenType::DotDotDot,
      46 => TokenType::Bang,
      47 => TokenType::LParen,
      48 => TokenType::RParen,
      49 => TokenType::LBracket,
      50 => TokenType::RBracket,
      51 => TokenType::LBrace,
      52 => TokenType::RBrace,
      53 => TokenType::Semi,
      54 => TokenType::Comma,
      55 => TokenType::BackQuote,
      56 => TokenType::Colon,
      57 => TokenType::DollarLBrace,
      58 => TokenType::QuestionMark,
      59 => TokenType::PlusPlus,
      60 => TokenType::MinusMinus,
      61 => TokenType::Tilde,
      62 => TokenType::EqEq,
      63 => TokenType::NotEq,
      64 => TokenType::EqEqEq,
      65 => TokenType::NotEqEq,
      66 => TokenType::Lt,
      67 => TokenType::LtEq,
      68 => TokenType::Gt,
      69 => TokenType::GtEq,
      70 => TokenType::LShift,
      71 => TokenType::RShift,
      72 => TokenType::ZeroFillRShift,
      73 => TokenType::Add,
      74 => TokenType::Sub,
      75 => TokenType::Mul,
      76 => TokenType::Div,
      77 => TokenType::Mod,
      78 => TokenType::BitOr,
      79 => TokenType::BitXor,
      80 => TokenType::BitAnd,
      81 => TokenType::Exp,
      82 => TokenType::LogicalOr,
      83 => TokenType::LogicalAnd,
      84 => TokenType::NullishCoalescing,
      85 => TokenType::Assign,
      86 => TokenType::AddAssign,
      87 => TokenType::SubAssign,
      88 => TokenType::MulAssign,
      89 => TokenType::DivAssign,
      90 => TokenType::ModAssign,
      91 => TokenType::LShiftAssign,
      92 => TokenType::RShiftAssign,
      93 => TokenType::ZeroFillRShiftAssign,
      94 => TokenType::BitOrAssign,
      95 => TokenType::BitXorAssign,
      96 => TokenType::BitAndAssign,
      97 => TokenType::ExpAssign,
      98 => TokenType::AndAssign,
      99 => TokenType::OrAssign,
      100 => TokenType::NullishAssign,
      101 => TokenType::Shebang,
      102 => TokenType::Error,
      103 => TokenType::Str,
      104 => TokenType::Num,
      105 => TokenType::BigInt,
      106 => TokenType::Template,
      107 => TokenType::Regex,
      108 => TokenType::JSXTagStart,
      109 => TokenType::JSXTagEnd,
      110 => TokenType::JSXTagName,
      111 => TokenType::JSXTagText,
      _ => TokenType::Unknown,
    }
  }
}

impl TokenType {
  pub fn parse_by_assign_operator(token: &AssignOp) -> TokenType {
    match token {
      AssignOp::Assign => TokenType::Assign,
      AssignOp::AddAssign => TokenType::AddAssign,
      AssignOp::SubAssign => TokenType::SubAssign,
      AssignOp::MulAssign => TokenType::MulAssign,
      AssignOp::DivAssign => TokenType::DivAssign,
      AssignOp::ModAssign => TokenType::ModAssign,
      AssignOp::LShiftAssign => TokenType::LShiftAssign,
      AssignOp::RShiftAssign => TokenType::RShiftAssign,
      AssignOp::ZeroFillRShiftAssign => TokenType::ZeroFillRShiftAssign,
      AssignOp::BitOrAssign => TokenType::BitOrAssign,
      AssignOp::BitXorAssign => TokenType::BitXorAssign,
      AssignOp::BitAndAssign => TokenType::BitAndAssign,
      AssignOp::ExpAssign => TokenType::ExpAssign,
      AssignOp::AndAssign => TokenType::AndAssign,
      AssignOp::OrAssign => TokenType::OrAssign,
      AssignOp::NullishAssign => TokenType::NullishAssign,
    }
  }

  pub fn parse_by_binary_operator(token: &BinOpToken) -> TokenType {
    match token {
      BinOpToken::EqEq => TokenType::EqEq,
      BinOpToken::NotEq => TokenType::NotEq,
      BinOpToken::EqEqEq => TokenType::EqEqEq,
      BinOpToken::NotEqEq => TokenType::NotEqEq,
      BinOpToken::Lt => TokenType::Lt,
      BinOpToken::LtEq => TokenType::LtEq,
      BinOpToken::Gt => TokenType::Gt,
      BinOpToken::GtEq => TokenType::GtEq,
      BinOpToken::LShift => TokenType::LShift,
      BinOpToken::RShift => TokenType::RShift,
      BinOpToken::ZeroFillRShift => TokenType::ZeroFillRShift,
      BinOpToken::Add => TokenType::Add,
      BinOpToken::Sub => TokenType::Sub,
      BinOpToken::Mul => TokenType::Mul,
      BinOpToken::Div => TokenType::Div,
      BinOpToken::Mod => TokenType::Mod,
      BinOpToken::BitOr => TokenType::BitOr,
      BinOpToken::BitXor => TokenType::BitXor,
      BinOpToken::BitAnd => TokenType::BitAnd,
      BinOpToken::Exp => TokenType::Exp,
      BinOpToken::LogicalOr => TokenType::LogicalOr,
      BinOpToken::LogicalAnd => TokenType::LogicalAnd,
      BinOpToken::NullishCoalescing => TokenType::NullishCoalescing,
    }
  }

  pub fn parse_by_generic_operator(token: &Token) -> TokenType {
    match token {
      Token::Arrow => TokenType::Arrow,
      Token::Hash => TokenType::Hash,
      Token::At => TokenType::At,
      Token::Dot => TokenType::Dot,
      Token::DotDotDot => TokenType::DotDotDot,
      Token::Bang => TokenType::Bang,
      Token::LParen => TokenType::LParen,
      Token::RParen => TokenType::RParen,
      Token::LBracket => TokenType::LBracket,
      Token::RBracket => TokenType::RBracket,
      Token::LBrace => TokenType::LBrace,
      Token::RBrace => TokenType::RBrace,
      Token::Semi => TokenType::Semi,
      Token::Comma => TokenType::Comma,
      Token::BackQuote => TokenType::BackQuote,
      Token::Colon => TokenType::Colon,
      Token::DollarLBrace => TokenType::DollarLBrace,
      Token::QuestionMark => TokenType::QuestionMark,
      Token::PlusPlus => TokenType::PlusPlus,
      Token::MinusMinus => TokenType::MinusMinus,
      Token::Tilde => TokenType::Tilde,
      Token::JSXTagStart => TokenType::JSXTagStart,
      Token::JSXTagEnd => TokenType::JSXTagEnd,
      _ => TokenType::Unknown,
    }
  }

  pub fn parse_by_keyword(token: &Keyword) -> TokenType {
    match token {
      Keyword::Await => TokenType::Await,
      Keyword::Break => TokenType::Break,
      Keyword::Case => TokenType::Case,
      Keyword::Catch => TokenType::Catch,
      Keyword::Class => TokenType::Class,
      Keyword::Const => TokenType::Const,
      Keyword::Continue => TokenType::Continue,
      Keyword::Debugger => TokenType::Debugger,
      Keyword::Default_ => TokenType::Default_,
      Keyword::Delete => TokenType::Delete,
      Keyword::Do => TokenType::Do,
      Keyword::Else => TokenType::Else,
      Keyword::Export => TokenType::Export,
      Keyword::Extends => TokenType::Extends,
      Keyword::Finally => TokenType::Finally,
      Keyword::For => TokenType::For,
      Keyword::Function => TokenType::Function,
      Keyword::If => TokenType::If,
      Keyword::Import => TokenType::Import,
      Keyword::In => TokenType::In,
      Keyword::InstanceOf => TokenType::InstanceOf,
      Keyword::Let => TokenType::Let,
      Keyword::New => TokenType::New,
      Keyword::Return => TokenType::Return,
      Keyword::Super => TokenType::Super,
      Keyword::Switch => TokenType::Switch,
      Keyword::This => TokenType::This,
      Keyword::Throw => TokenType::Throw,
      Keyword::Try => TokenType::Try,
      Keyword::TypeOf => TokenType::TypeOf,
      Keyword::Var => TokenType::Var,
      Keyword::Void => TokenType::Void,
      Keyword::While => TokenType::While,
      Keyword::With => TokenType::With,
      Keyword::Yield => TokenType::Yield,
    }
  }
}

pub struct JavaTokenType {
  #[allow(dead_code)]
  class: GlobalRef,
  method_get_id: JMethodID,
  method_parse: JStaticMethodID,
}
unsafe impl Send for JavaTokenType {}
unsafe impl Sync for JavaTokenType {}

impl JavaTokenType {
  pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
    let class = env
      .find_class("com/caoccao/javet/swc4j/tokens/Swc4jTokenType")
      .expect("Couldn't find class Swc4jTokenType");
    let class = env
      .new_global_ref(class)
      .expect("Couldn't globalize class Swc4jTokenType");
    let method_get_id = env
      .get_method_id(&class, "getId", "()I")
      .expect("Couldn't find method Swc4jTokenType.getId");
    let method_parse = env
      .get_static_method_id(&class, "parse", "(I)Lcom/caoccao/javet/swc4j/tokens/Swc4jTokenType;")
      .expect("Couldn't find static method Swc4jTokenType.parse");
    JavaTokenType {
      class,
      method_get_id,
      method_parse,
    }
  }

  pub fn get_token_type<'local, 'a>(&self, env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> TokenType {
    let id = call_as_int!(env, obj.as_ref(), self.method_get_id, &[], "getId()");
    TokenType::parse_by_id(id)
  }

  pub fn parse<'local, 'a>(&self, env: &mut JNIEnv<'local>, id: i32) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = int_to_jvalue!(id);
    call_static_as_object!(env, &self.class, &self.method_parse, &[id], "parse()")
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
    let id = call_as_int!(env, obj.as_ref(), self.method_get_id, &[], "getId()");
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
    let id = call_as_int!(env, obj.as_ref(), self.method_get_id, &[], "getId()");
    MediaType::parse_by_id(id)
  }

  pub fn parse<'local, 'a>(&self, env: &mut JNIEnv<'local>, id: i32) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = int_to_jvalue!(id);
    call_static_as_object!(env, &self.class, &self.method_parse, &[id], "parse()")
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
    let id = call_as_int!(env, obj.as_ref(), self.method_get_id, &[], "getId()");
    ParseMode::parse_by_id(id)
  }
}

pub static mut JAVA_TOKEN_TYPE: Option<JavaTokenType> = None;
pub static mut JAVA_IMPORTS_NOT_USED_AS_VALUES: Option<JavaImportsNotUsedAsValues> = None;
pub static mut JAVA_MEDIA_TYPE: Option<JavaMediaType> = None;
pub static mut JAVA_PARSE_MODE: Option<JavaParseMode> = None;

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_TOKEN_TYPE = Some(JavaTokenType::new(env));
    JAVA_IMPORTS_NOT_USED_AS_VALUES = Some(JavaImportsNotUsedAsValues::new(env));
    JAVA_MEDIA_TYPE = Some(JavaMediaType::new(env));
    JAVA_PARSE_MODE = Some(JavaParseMode::new(env));
  }
}

pub mod swc_enums {
  use crate::enums::IdentifiableEnum;
  use deno_ast::swc::ast::*;
  use num_bigint::Sign;

  impl IdentifiableEnum<AssignOp> for AssignOp {
    fn get_id(&self) -> i32 {
      match self {
        AssignOp::AddAssign => 0,
        AssignOp::AndAssign => 1,
        AssignOp::Assign => 2,
        AssignOp::BitAndAssign => 3,
        AssignOp::BitOrAssign => 4,
        AssignOp::BitXorAssign => 5,
        AssignOp::DivAssign => 6,
        AssignOp::ExpAssign => 7,
        AssignOp::LShiftAssign => 8,
        AssignOp::ModAssign => 9,
        AssignOp::MulAssign => 10,
        AssignOp::NullishAssign => 11,
        AssignOp::OrAssign => 12,
        AssignOp::RShiftAssign => 13,
        AssignOp::SubAssign => 14,
        AssignOp::ZeroFillRShiftAssign => 15,
      }
    }
    fn parse_by_id(id: i32) -> AssignOp {
      match id {
        1 => AssignOp::AndAssign,
        2 => AssignOp::Assign,
        3 => AssignOp::BitAndAssign,
        4 => AssignOp::BitOrAssign,
        5 => AssignOp::BitXorAssign,
        6 => AssignOp::DivAssign,
        7 => AssignOp::ExpAssign,
        8 => AssignOp::LShiftAssign,
        9 => AssignOp::ModAssign,
        10 => AssignOp::MulAssign,
        11 => AssignOp::NullishAssign,
        12 => AssignOp::OrAssign,
        13 => AssignOp::RShiftAssign,
        14 => AssignOp::SubAssign,
        15 => AssignOp::ZeroFillRShiftAssign,
        _ => AssignOp::AddAssign,
      }
    }
  }

  impl IdentifiableEnum<BinaryOp> for BinaryOp {
    fn get_id(&self) -> i32 {
      match self {
        BinaryOp::Add => 0,
        BinaryOp::BitAnd => 1,
        BinaryOp::BitOr => 2,
        BinaryOp::BitXor => 3,
        BinaryOp::Div => 4,
        BinaryOp::EqEq => 5,
        BinaryOp::EqEqEq => 6,
        BinaryOp::Exp => 7,
        BinaryOp::Gt => 8,
        BinaryOp::GtEq => 9,
        BinaryOp::In => 10,
        BinaryOp::InstanceOf => 11,
        BinaryOp::LogicalAnd => 12,
        BinaryOp::LogicalOr => 13,
        BinaryOp::LShift => 14,
        BinaryOp::Lt => 15,
        BinaryOp::LtEq => 16,
        BinaryOp::Mod => 17,
        BinaryOp::Mul => 18,
        BinaryOp::NotEq => 19,
        BinaryOp::NotEqEq => 20,
        BinaryOp::NullishCoalescing => 21,
        BinaryOp::RShift => 22,
        BinaryOp::Sub => 23,
        BinaryOp::ZeroFillRShift => 24,
      }
    }
    fn parse_by_id(id: i32) -> BinaryOp {
      match id {
        1 => BinaryOp::BitAnd,
        2 => BinaryOp::BitOr,
        3 => BinaryOp::BitXor,
        4 => BinaryOp::Div,
        5 => BinaryOp::EqEq,
        6 => BinaryOp::EqEqEq,
        7 => BinaryOp::Exp,
        8 => BinaryOp::Gt,
        9 => BinaryOp::GtEq,
        10 => BinaryOp::In,
        11 => BinaryOp::InstanceOf,
        12 => BinaryOp::LogicalAnd,
        13 => BinaryOp::LogicalOr,
        14 => BinaryOp::LShift,
        15 => BinaryOp::Lt,
        16 => BinaryOp::LtEq,
        17 => BinaryOp::Mod,
        18 => BinaryOp::Mul,
        19 => BinaryOp::NotEq,
        20 => BinaryOp::NotEqEq,
        21 => BinaryOp::NullishCoalescing,
        22 => BinaryOp::RShift,
        23 => BinaryOp::Sub,
        24 => BinaryOp::ZeroFillRShift,
        _ => BinaryOp::Add,
      }
    }
  }

  impl IdentifiableEnum<Accessibility> for Accessibility {
    fn get_id(&self) -> i32 {
      match self {
        Accessibility::Public => 0,
        Accessibility::Protected => 1,
        Accessibility::Private => 2,
      }
    }
    fn parse_by_id(id: i32) -> Accessibility {
      match id {
        1 => Accessibility::Protected,
        2 => Accessibility::Private,
        _ => Accessibility::Public,
      }
    }
  }

  impl IdentifiableEnum<MetaPropKind> for MetaPropKind {
    fn get_id(&self) -> i32 {
      match self {
        MetaPropKind::NewTarget => 0,
        MetaPropKind::ImportMeta => 1,
      }
    }
    fn parse_by_id(id: i32) -> MetaPropKind {
      match id {
        1 => MetaPropKind::ImportMeta,
        _ => MetaPropKind::NewTarget,
      }
    }
  }

  impl IdentifiableEnum<MethodKind> for MethodKind {
    fn get_id(&self) -> i32 {
      match self {
        MethodKind::Method => 0,
        MethodKind::Getter => 1,
        MethodKind::Setter => 2,
      }
    }
    fn parse_by_id(id: i32) -> MethodKind {
      match id {
        1 => MethodKind::Getter,
        2 => MethodKind::Setter,
        _ => MethodKind::Method,
      }
    }
  }

  impl IdentifiableEnum<Sign> for Sign {
    fn get_id(&self) -> i32 {
      match self {
        Sign::NoSign => 0,
        Sign::Minus => 1,
        Sign::Plus => 2,
      }
    }
    fn parse_by_id(id: i32) -> Sign {
      match id {
        1 => Sign::Minus,
        2 => Sign::Plus,
        _ => Sign::NoSign,
      }
    }
  }

  impl IdentifiableEnum<TsKeywordTypeKind> for TsKeywordTypeKind {
    fn get_id(&self) -> i32 {
      match self {
        TsKeywordTypeKind::TsAnyKeyword => 0,
        TsKeywordTypeKind::TsBigIntKeyword => 1,
        TsKeywordTypeKind::TsBooleanKeyword => 2,
        TsKeywordTypeKind::TsIntrinsicKeyword => 3,
        TsKeywordTypeKind::TsNeverKeyword => 4,
        TsKeywordTypeKind::TsNullKeyword => 5,
        TsKeywordTypeKind::TsNumberKeyword => 6,
        TsKeywordTypeKind::TsObjectKeyword => 7,
        TsKeywordTypeKind::TsStringKeyword => 8,
        TsKeywordTypeKind::TsSymbolKeyword => 9,
        TsKeywordTypeKind::TsUndefinedKeyword => 10,
        TsKeywordTypeKind::TsUnknownKeyword => 11,
        TsKeywordTypeKind::TsVoidKeyword => 12,
      }
    }
    fn parse_by_id(id: i32) -> TsKeywordTypeKind {
      match id {
        1 => TsKeywordTypeKind::TsBigIntKeyword,
        2 => TsKeywordTypeKind::TsBooleanKeyword,
        3 => TsKeywordTypeKind::TsIntrinsicKeyword,
        4 => TsKeywordTypeKind::TsNeverKeyword,
        5 => TsKeywordTypeKind::TsNullKeyword,
        6 => TsKeywordTypeKind::TsNumberKeyword,
        7 => TsKeywordTypeKind::TsObjectKeyword,
        8 => TsKeywordTypeKind::TsStringKeyword,
        9 => TsKeywordTypeKind::TsSymbolKeyword,
        10 => TsKeywordTypeKind::TsUndefinedKeyword,
        11 => TsKeywordTypeKind::TsUnknownKeyword,
        12 => TsKeywordTypeKind::TsVoidKeyword,
        _ => TsKeywordTypeKind::TsAnyKeyword,
      }
    }
  }

  impl IdentifiableEnum<TruePlusMinus> for TruePlusMinus {
    fn get_id(&self) -> i32 {
      match self {
        TruePlusMinus::True => 0,
        TruePlusMinus::Plus => 1,
        TruePlusMinus::Minus => 2,
      }
    }
    fn parse_by_id(id: i32) -> TruePlusMinus {
      match id {
        1 => TruePlusMinus::Plus,
        2 => TruePlusMinus::Minus,
        _ => TruePlusMinus::True,
      }
    }
  }

  impl IdentifiableEnum<TsTypeOperatorOp> for TsTypeOperatorOp {
    fn get_id(&self) -> i32 {
      match self {
        TsTypeOperatorOp::KeyOf => 0,
        TsTypeOperatorOp::ReadOnly => 1,
        TsTypeOperatorOp::Unique => 2,
      }
    }
    fn parse_by_id(id: i32) -> TsTypeOperatorOp {
      match id {
        1 => TsTypeOperatorOp::ReadOnly,
        2 => TsTypeOperatorOp::Unique,
        _ => TsTypeOperatorOp::KeyOf,
      }
    }
  }

  impl IdentifiableEnum<UnaryOp> for UnaryOp {
    fn get_id(&self) -> i32 {
      match self {
        UnaryOp::Void => 0,
        UnaryOp::Bang => 1,
        UnaryOp::Delete => 2,
        UnaryOp::Minus => 3,
        UnaryOp::Plus => 4,
        UnaryOp::Tilde => 5,
        UnaryOp::TypeOf => 6,
      }
    }
    fn parse_by_id(id: i32) -> UnaryOp {
      match id {
        1 => UnaryOp::Bang,
        2 => UnaryOp::Delete,
        3 => UnaryOp::Minus,
        4 => UnaryOp::Plus,
        5 => UnaryOp::Tilde,
        6 => UnaryOp::TypeOf,
        _ => UnaryOp::Void,
      }
    }
  }

  impl IdentifiableEnum<UpdateOp> for UpdateOp {
    fn get_id(&self) -> i32 {
      match self {
        UpdateOp::PlusPlus => 0,
        UpdateOp::MinusMinus => 1,
      }
    }
    fn parse_by_id(id: i32) -> UpdateOp {
      match id {
        1 => UpdateOp::MinusMinus,
        _ => UpdateOp::PlusPlus,
      }
    }
  }

  impl IdentifiableEnum<VarDeclKind> for VarDeclKind {
    fn get_id(&self) -> i32 {
      match self {
        VarDeclKind::Const => 0,
        VarDeclKind::Let => 1,
        VarDeclKind::Var => 2,
      }
    }
    fn parse_by_id(id: i32) -> VarDeclKind {
      match id {
        1 => VarDeclKind::Let,
        2 => VarDeclKind::Var,
        _ => VarDeclKind::Const,
      }
    }
  }
}
