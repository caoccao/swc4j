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

use deno_ast::swc::ast::AssignOp;
use deno_ast::swc::parser::token::{BinOpToken, Keyword, Token};
pub use deno_ast::{ImportsNotUsedAsValues, MediaType};

use crate::jni_utils;

pub trait IdentifiableEnum<T> {
  fn get_id(&self) -> i32;
  fn parse_by_id(id: i32) -> T;
}

#[derive(Debug, Copy, Clone)]
pub enum AstTokenType {
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
      AstTokenType::BackQuote => 55,
      AstTokenType::Colon => 56,
      AstTokenType::DollarLBrace => 57,
      AstTokenType::QuestionMark => 58,
      AstTokenType::PlusPlus => 59,
      AstTokenType::MinusMinus => 60,
      AstTokenType::Tilde => 61,
      AstTokenType::EqEq => 62,
      AstTokenType::NotEq => 63,
      AstTokenType::EqEqEq => 64,
      AstTokenType::NotEqEq => 65,
      AstTokenType::Lt => 66,
      AstTokenType::LtEq => 67,
      AstTokenType::Gt => 68,
      AstTokenType::GtEq => 69,
      AstTokenType::LShift => 70,
      AstTokenType::RShift => 71,
      AstTokenType::ZeroFillRShift => 72,
      AstTokenType::Add => 73,
      AstTokenType::Sub => 74,
      AstTokenType::Mul => 75,
      AstTokenType::Div => 76,
      AstTokenType::Mod => 77,
      AstTokenType::BitOr => 78,
      AstTokenType::BitXor => 79,
      AstTokenType::BitAnd => 80,
      AstTokenType::Exp => 81,
      AstTokenType::LogicalOr => 82,
      AstTokenType::LogicalAnd => 83,
      AstTokenType::NullishCoalescing => 84,
      AstTokenType::Assign => 85,
      AstTokenType::AddAssign => 86,
      AstTokenType::SubAssign => 87,
      AstTokenType::MulAssign => 88,
      AstTokenType::DivAssign => 89,
      AstTokenType::ModAssign => 90,
      AstTokenType::LShiftAssign => 91,
      AstTokenType::RShiftAssign => 92,
      AstTokenType::ZeroFillRShiftAssign => 93,
      AstTokenType::BitOrAssign => 94,
      AstTokenType::BitXorAssign => 95,
      AstTokenType::BitAndAssign => 96,
      AstTokenType::ExpAssign => 97,
      AstTokenType::AndAssign => 98,
      AstTokenType::OrAssign => 99,
      AstTokenType::NullishAssign => 100,
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
      55 => AstTokenType::BackQuote,
      56 => AstTokenType::Colon,
      57 => AstTokenType::DollarLBrace,
      58 => AstTokenType::QuestionMark,
      59 => AstTokenType::PlusPlus,
      60 => AstTokenType::MinusMinus,
      61 => AstTokenType::Tilde,
      62 => AstTokenType::EqEq,
      63 => AstTokenType::NotEq,
      64 => AstTokenType::EqEqEq,
      65 => AstTokenType::NotEqEq,
      66 => AstTokenType::Lt,
      67 => AstTokenType::LtEq,
      68 => AstTokenType::Gt,
      69 => AstTokenType::GtEq,
      70 => AstTokenType::LShift,
      71 => AstTokenType::RShift,
      72 => AstTokenType::ZeroFillRShift,
      73 => AstTokenType::Add,
      74 => AstTokenType::Sub,
      75 => AstTokenType::Mul,
      76 => AstTokenType::Div,
      77 => AstTokenType::Mod,
      78 => AstTokenType::BitOr,
      79 => AstTokenType::BitXor,
      80 => AstTokenType::BitAnd,
      81 => AstTokenType::Exp,
      82 => AstTokenType::LogicalOr,
      83 => AstTokenType::LogicalAnd,
      84 => AstTokenType::NullishCoalescing,
      85 => AstTokenType::Assign,
      86 => AstTokenType::AddAssign,
      87 => AstTokenType::SubAssign,
      88 => AstTokenType::MulAssign,
      89 => AstTokenType::DivAssign,
      90 => AstTokenType::ModAssign,
      91 => AstTokenType::LShiftAssign,
      92 => AstTokenType::RShiftAssign,
      93 => AstTokenType::ZeroFillRShiftAssign,
      94 => AstTokenType::BitOrAssign,
      95 => AstTokenType::BitXorAssign,
      96 => AstTokenType::BitAndAssign,
      97 => AstTokenType::ExpAssign,
      98 => AstTokenType::AndAssign,
      99 => AstTokenType::OrAssign,
      100 => AstTokenType::NullishAssign,
      _ => AstTokenType::Unknown,
    }
  }
}

impl AstTokenType {
  pub fn parse_by_assign_operator(token: &AssignOp) -> AstTokenType {
    match token {
      AssignOp::Assign => AstTokenType::Assign,
      AssignOp::AddAssign => AstTokenType::AddAssign,
      AssignOp::SubAssign => AstTokenType::SubAssign,
      AssignOp::MulAssign => AstTokenType::MulAssign,
      AssignOp::DivAssign => AstTokenType::DivAssign,
      AssignOp::ModAssign => AstTokenType::ModAssign,
      AssignOp::LShiftAssign => AstTokenType::LShiftAssign,
      AssignOp::RShiftAssign => AstTokenType::RShiftAssign,
      AssignOp::ZeroFillRShiftAssign => AstTokenType::ZeroFillRShiftAssign,
      AssignOp::BitOrAssign => AstTokenType::BitOrAssign,
      AssignOp::BitXorAssign => AstTokenType::BitXorAssign,
      AssignOp::BitAndAssign => AstTokenType::BitAndAssign,
      AssignOp::ExpAssign => AstTokenType::ExpAssign,
      AssignOp::AndAssign => AstTokenType::AndAssign,
      AssignOp::OrAssign => AstTokenType::OrAssign,
      AssignOp::NullishAssign => AstTokenType::NullishAssign,
    }
  }

  pub fn parse_by_binary_operator(token: &BinOpToken) -> AstTokenType {
    match token {
      BinOpToken::EqEq => AstTokenType::EqEq,
      BinOpToken::NotEq => AstTokenType::NotEq,
      BinOpToken::EqEqEq => AstTokenType::EqEqEq,
      BinOpToken::NotEqEq => AstTokenType::NotEqEq,
      BinOpToken::Lt => AstTokenType::Lt,
      BinOpToken::LtEq => AstTokenType::LtEq,
      BinOpToken::Gt => AstTokenType::Gt,
      BinOpToken::GtEq => AstTokenType::GtEq,
      BinOpToken::LShift => AstTokenType::LShift,
      BinOpToken::RShift => AstTokenType::RShift,
      BinOpToken::ZeroFillRShift => AstTokenType::ZeroFillRShift,
      BinOpToken::Add => AstTokenType::Add,
      BinOpToken::Sub => AstTokenType::Sub,
      BinOpToken::Mul => AstTokenType::Mul,
      BinOpToken::Div => AstTokenType::Div,
      BinOpToken::Mod => AstTokenType::Mod,
      BinOpToken::BitOr => AstTokenType::BitOr,
      BinOpToken::BitXor => AstTokenType::BitXor,
      BinOpToken::BitAnd => AstTokenType::BitAnd,
      BinOpToken::Exp => AstTokenType::Exp,
      BinOpToken::LogicalOr => AstTokenType::LogicalOr,
      BinOpToken::LogicalAnd => AstTokenType::LogicalAnd,
      BinOpToken::NullishCoalescing => AstTokenType::NullishCoalescing,
    }
  }

  pub fn parse_by_generic_operator(token: &Token) -> AstTokenType {
    match token {
      Token::Arrow => AstTokenType::Arrow,
      Token::Hash => AstTokenType::Hash,
      Token::At => AstTokenType::At,
      Token::Dot => AstTokenType::Dot,
      Token::DotDotDot => AstTokenType::DotDotDot,
      Token::Bang => AstTokenType::Bang,
      Token::LParen => AstTokenType::LParen,
      Token::RParen => AstTokenType::RParen,
      Token::LBracket => AstTokenType::LBracket,
      Token::RBracket => AstTokenType::RBracket,
      Token::LBrace => AstTokenType::LBrace,
      Token::RBrace => AstTokenType::RBrace,
      Token::Semi => AstTokenType::Semi,
      Token::Comma => AstTokenType::Comma,
      Token::BackQuote => AstTokenType::BackQuote,
      Token::Colon => AstTokenType::Colon,
      Token::DollarLBrace => AstTokenType::DollarLBrace,
      Token::QuestionMark => AstTokenType::QuestionMark,
      Token::PlusPlus => AstTokenType::PlusPlus,
      Token::MinusMinus => AstTokenType::MinusMinus,
      Token::Tilde => AstTokenType::Tilde,
      _ => panic!("Unexpected token {:?}", token),
    }
  }

  pub fn parse_by_keyword(token: &Keyword) -> AstTokenType {
    match token {
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

  pub fn parse<'local, 'a>(&self, env: &mut JNIEnv<'local>, id: i32) -> JObject<'a>
  where
    'local: 'a,
  {
    let id = jvalue { i: id };
    unsafe {
      env
        .call_static_method_unchecked(&self.class, self.method_parse, ReturnType::Object, &[id])
        .expect("Object is expected")
        .l()
        .expect("Couldn't convert to JObject")
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
