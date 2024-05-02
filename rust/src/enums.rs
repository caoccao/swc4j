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

pub use deno_ast::swc::ast::EsVersion;
use deno_ast::swc::ast::*;
pub use deno_ast::swc::common::comments::CommentKind;
use deno_ast::swc::parser::token::{BinOpToken, Keyword, Token};
pub use deno_ast::{ImportsNotUsedAsValues, MediaType, SourceMapOption};
use num_bigint::Sign;

use crate::jni_utils::*;

pub trait IdentifiableEnum<T> {
  fn get_id(&self) -> i32;
  fn parse_by_id(id: i32) -> T;
}

macro_rules! declare_identifiable_enum {
  ($struct_name:ident, $static_name:ident, $enum_name:ident, $package:literal, $java_class_name:literal) => {
    pub struct $struct_name {
      #[allow(dead_code)]
      class: GlobalRef,
      method_get_id: JMethodID,
      method_parse: JStaticMethodID,
    }
    unsafe impl Send for $struct_name {}
    unsafe impl Sync for $struct_name {}

    impl $struct_name {
      pub fn new<'local>(env: &mut JNIEnv<'local>) -> Self {
        let class = env
          .find_class(format!("com/caoccao/javet/swc4j/{}{}", $package, $java_class_name))
          .expect(format!("Couldn't find class {}", $java_class_name).as_str());
        let class = env
          .new_global_ref(class)
          .expect(format!("Couldn't globalize class {}", $java_class_name).as_str());
        let method_get_id = env
          .get_method_id(&class, "getId", "()I")
          .expect(format!("Couldn't find method {}.getId", $java_class_name).as_str());
        let method_parse = env
          .get_static_method_id(
            &class,
            "parse",
            format!("(I)Lcom/caoccao/javet/swc4j/{}{};", $package, $java_class_name),
          )
          .expect(format!("Couldn't find static method {}.parse", $java_class_name).as_str());
        $struct_name {
          class,
          method_get_id,
          method_parse,
        }
      }

      pub fn parse<'local, 'a>(&self, env: &mut JNIEnv<'local>, id: i32) -> JObject<'a>
      where
        'local: 'a,
      {
        let id = int_to_jvalue!(id);
        call_static_as_object!(env, &self.class, &self.method_parse, &[id], "parse()")
      }
    }

    static mut $static_name: Option<$struct_name> = None;

    impl FromJava for $enum_name {
      fn from_java<'local, 'a>(env: &mut JNIEnv<'local>, obj: &JObject<'a>) -> $enum_name {
        let id = call_as_int!(
          env,
          obj.as_ref(),
          $static_name.as_ref().unwrap().method_get_id,
          &[],
          "getId()"
        );
        $enum_name::parse_by_id(id)
      }
    }

    impl ToJava for $enum_name {
      fn to_java<'local, 'a>(&self, env: &mut JNIEnv<'local>) -> JObject<'a>
      where
        'local: 'a,
      {
        unsafe { $static_name.as_ref().unwrap() }.parse(env, self.get_id())
      }
    }
  };
}

declare_identifiable_enum!(
  JavaAccessibility,
  JAVA_CLASS_ACCESSIBILITY,
  Accessibility,
  "ast/enums/",
  "Swc4jAstAccessibility"
);

declare_identifiable_enum!(
  JavaAssignOp,
  JAVA_CLASS_ASSIGN_OP,
  AssignOp,
  "ast/enums/",
  "Swc4jAstAssignOp"
);

declare_identifiable_enum!(
  JavaAstType,
  JAVA_CLASS_AST_TYPE,
  AstType,
  "ast/enums/",
  "Swc4jAstType"
);

declare_identifiable_enum!(
  JavaBigIntSign,
  JAVA_CLASS_BIG_INT_SIGN,
  Sign,
  "ast/enums/",
  "Swc4jAstBigIntSign"
);

declare_identifiable_enum!(
  JavaBinaryOp,
  JAVA_CLASS_BINARY_OP,
  BinaryOp,
  "ast/enums/",
  "Swc4jAstBinaryOp"
);

declare_identifiable_enum!(
  JavaCommentKind,
  JAVA_CLASS_COMMENT_KIND,
  CommentKind,
  "comments/",
  "Swc4jCommentKind"
);

declare_identifiable_enum!(
  JavaEsVersion,
  JAVA_CLASS_ES_VERSION,
  EsVersion,
  "enums/",
  "Swc4jEsVersion"
);

declare_identifiable_enum!(
  JavaImportPhase,
  JAVA_CLASS_IMPORT_PHASE,
  ImportPhase,
  "ast/enums/",
  "Swc4jAstImportPhase"
);

declare_identifiable_enum!(
  JavaImportsNotUsedAsValues,
  JAVA_CLASS_IMPORTS_NOT_USED_AS_VALUES,
  ImportsNotUsedAsValues,
  "enums/",
  "Swc4jImportsNotUsedAsValues"
);

declare_identifiable_enum!(
  JavaMediaType,
  JAVA_CLASS_MEDIA_TYPE,
  MediaType,
  "enums/",
  "Swc4jMediaType"
);

declare_identifiable_enum!(
  JavaMetaPropKind,
  JAVA_CLASS_META_PROP_KIND,
  MetaPropKind,
  "ast/enums/",
  "Swc4jAstMetaPropKind"
);

declare_identifiable_enum!(
  JavaMethodKind,
  JAVA_CLASS_METHOD_KIND,
  MethodKind,
  "ast/enums/",
  "Swc4jAstMethodKind"
);

declare_identifiable_enum!(
  JavaParseMode,
  JAVA_CLASS_PARSE_MODE,
  ParseMode,
  "enums/",
  "Swc4jParseMode"
);

declare_identifiable_enum!(
  JavaSourceMapOption,
  JAVA_CLASS_SOURCE_MAP_OPTION,
  SourceMapOption,
  "enums/",
  "Swc4jSourceMapOption"
);

declare_identifiable_enum!(
  JavaTokenType,
  JAVA_CLASS_TOKEN_TYPE,
  TokenType,
  "tokens/",
  "Swc4jTokenType"
);

declare_identifiable_enum!(
  JavaTruePlusMinus,
  JAVA_CLASS_TRUE_PLUS_MINUS,
  TruePlusMinus,
  "ast/enums/",
  "Swc4jAstTruePlusMinus"
);

declare_identifiable_enum!(
  JavaTsKeywordTypeKind,
  JAVA_CLASS_TS_KEYWORD_TYPE_KIND,
  TsKeywordTypeKind,
  "ast/enums/",
  "Swc4jAstTsKeywordTypeKind"
);

declare_identifiable_enum!(
  JavaTsTypeOperatorOp,
  JAVA_CLASS_TS_TYPE_OPERATOR_OP,
  TsTypeOperatorOp,
  "ast/enums/",
  "Swc4jAstTsTypeOperatorOp"
);

declare_identifiable_enum!(
  JavaUnaryOp,
  JAVA_CLASS_UNARY_OP,
  UnaryOp,
  "ast/enums/",
  "Swc4jAstUnaryOp"
);

declare_identifiable_enum!(
  JavaUpdateOp,
  JAVA_CLASS_UPDATE_OP,
  UpdateOp,
  "ast/enums/",
  "Swc4jAstUpdateOp"
);

declare_identifiable_enum!(
  JavaVarDeclKind,
  JAVA_CLASS_VAR_DECL_KIND,
  VarDeclKind,
  "ast/enums/",
  "Swc4jAstVarDeclKind"
);

pub fn init<'local>(env: &mut JNIEnv<'local>) {
  unsafe {
    JAVA_CLASS_ACCESSIBILITY = Some(JavaAccessibility::new(env));
    JAVA_CLASS_ASSIGN_OP = Some(JavaAssignOp::new(env));
    JAVA_CLASS_AST_TYPE = Some(JavaAstType::new(env));
    JAVA_CLASS_BIG_INT_SIGN = Some(JavaBigIntSign::new(env));
    JAVA_CLASS_BINARY_OP = Some(JavaBinaryOp::new(env));
    JAVA_CLASS_COMMENT_KIND = Some(JavaCommentKind::new(env));
    JAVA_CLASS_ES_VERSION = Some(JavaEsVersion::new(env));
    JAVA_CLASS_IMPORT_PHASE = Some(JavaImportPhase::new(env));
    JAVA_CLASS_IMPORTS_NOT_USED_AS_VALUES = Some(JavaImportsNotUsedAsValues::new(env));
    JAVA_CLASS_MEDIA_TYPE = Some(JavaMediaType::new(env));
    JAVA_CLASS_META_PROP_KIND = Some(JavaMetaPropKind::new(env));
    JAVA_CLASS_METHOD_KIND = Some(JavaMethodKind::new(env));
    JAVA_CLASS_PARSE_MODE = Some(JavaParseMode::new(env));
    JAVA_CLASS_SOURCE_MAP_OPTION = Some(JavaSourceMapOption::new(env));
    JAVA_CLASS_TOKEN_TYPE = Some(JavaTokenType::new(env));
    JAVA_CLASS_TS_KEYWORD_TYPE_KIND = Some(JavaTsKeywordTypeKind::new(env));
    JAVA_CLASS_TS_TYPE_OPERATOR_OP = Some(JavaTsTypeOperatorOp::new(env));
    JAVA_CLASS_UNARY_OP = Some(JavaUnaryOp::new(env));
    JAVA_CLASS_UPDATE_OP = Some(JavaUpdateOp::new(env));
    JAVA_CLASS_VAR_DECL_KIND = Some(JavaVarDeclKind::new(env));
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
      0 => Accessibility::Public,
      1 => Accessibility::Protected,
      2 => Accessibility::Private,
      _ => Accessibility::Public,
    }
  }
}

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
      0 => AssignOp::AddAssign,
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

#[derive(Debug)]
pub enum AstType {
  ArrayLit,
  ArrayPat,
  ArrowExpr,
  AssignExpr,
  AssignPat,
  AssignPatProp,
  AssignProp,
  AutoAccessor,
  AwaitExpr,
  BigInt,
  BindingIdent,
  BinExpr,
  BlockStmt,
  Bool,
  BreakStmt,
  CallExpr,
  CatchClause,
  Class,
  ClassDecl,
  ClassExpr,
  ClassMethod,
  ClassProp,
  ComputedPropName,
  CondExpr,
  Constructor,
  ContinueStmt,
  DebuggerStmt,
  Decorator,
  DoWhileStmt,
  EmptyStmt,
  ExportAll,
  ExportDecl,
  ExportDefaultDecl,
  ExportDefaultExpr,
  ExportDefaultSpecifier,
  ExportNamedSpecifier,
  ExportNamespaceSpecifier,
  ExprOrSpread,
  ExprStmt,
  FnDecl,
  FnExpr,
  ForInStmt,
  ForOfStmt,
  ForStmt,
  Function,
  GetterProp,
  Ident,
  IfStmt,
  Import,
  ImportDecl,
  ImportDefaultSpecifier,
  ImportNamedSpecifier,
  ImportStarAsSpecifier,
  Invalid,
  JsxAttr,
  JsxClosingElement,
  JsxClosingFragment,
  JsxElement,
  JsxEmptyExpr,
  JsxExprContainer,
  JsxFragment,
  JsxMemberExpr,
  JsxNamespacedName,
  JsxOpeningElement,
  JsxOpeningFragment,
  JsxSpreadChild,
  JsxText,
  KeyValuePatProp,
  KeyValueProp,
  LabeledStmt,
  MemberExpr,
  MetaPropExpr,
  MethodProp,
  Module,
  NamedExport,
  NewExpr,
  Null,
  Number,
  ObjectLit,
  ObjectPat,
  OptCall,
  OptChainExpr,
  Param,
  ParenExpr,
  PrivateMethod,
  PrivateName,
  PrivateProp,
  Regex,
  RestPat,
  ReturnStmt,
  Script,
  SeqExpr,
  SetterProp,
  SpreadElement,
  StaticBlock,
  Str,
  Super,
  SuperPropExpr,
  SwitchCase,
  SwitchStmt,
  TaggedTpl,
  ThisExpr,
  ThrowStmt,
  Tpl,
  TplElement,
  TryStmt,
  TsArrayType,
  TsAsExpr,
  TsCallSignatureDecl,
  TsConditionalType,
  TsConstAssertion,
  TsConstructorType,
  TsConstructSignatureDecl,
  TsEnumDecl,
  TsEnumMember,
  TsExportAssignment,
  TsExprWithTypeArgs,
  TsExternalModuleRef,
  TsFnType,
  TsGetterSignature,
  TsImportEqualsDecl,
  TsImportType,
  TsIndexedAccessType,
  TsIndexSignature,
  TsInferType,
  TsInstantiation,
  TsInterfaceBody,
  TsInterfaceDecl,
  TsIntersectionType,
  TsKeywordType,
  TsLitType,
  TsMappedType,
  TsMethodSignature,
  TsModuleBlock,
  TsModuleDecl,
  TsNamespaceDecl,
  TsNamespaceExportDecl,
  TsNonNullExpr,
  TsOptionalType,
  TsParamProp,
  TsParenthesizedType,
  TsPropertySignature,
  TsQualifiedName,
  TsRestType,
  TsSatisfiesExpr,
  TsSetterSignature,
  TsThisType,
  TsTplLitType,
  TsTupleElement,
  TsTupleType,
  TsTypeAliasDecl,
  TsTypeAnn,
  TsTypeAssertion,
  TsTypeLit,
  TsTypeOperator,
  TsTypeParam,
  TsTypeParamDecl,
  TsTypeParamInstantiation,
  TsTypePredicate,
  TsTypeQuery,
  TsTypeRef,
  TsUnionType,
  UnaryExpr,
  UpdateExpr,
  UsingDecl,
  VarDecl,
  VarDeclarator,
  WhileStmt,
  WithStmt,
  YieldExpr,
}

impl IdentifiableEnum<AstType> for AstType {
  fn get_id(&self) -> i32 {
    match self {
      AstType::ArrayLit => 0,
      AstType::ArrayPat => 1,
      AstType::ArrowExpr => 2,
      AstType::AssignExpr => 3,
      AstType::AssignPat => 4,
      AstType::AssignPatProp => 5,
      AstType::AssignProp => 6,
      AstType::AutoAccessor => 7,
      AstType::AwaitExpr => 8,
      AstType::BigInt => 9,
      AstType::BindingIdent => 10,
      AstType::BinExpr => 11,
      AstType::BlockStmt => 12,
      AstType::Bool => 13,
      AstType::BreakStmt => 14,
      AstType::CallExpr => 15,
      AstType::CatchClause => 16,
      AstType::Class => 17,
      AstType::ClassDecl => 18,
      AstType::ClassExpr => 19,
      AstType::ClassMethod => 20,
      AstType::ClassProp => 21,
      AstType::ComputedPropName => 22,
      AstType::CondExpr => 23,
      AstType::Constructor => 24,
      AstType::ContinueStmt => 25,
      AstType::DebuggerStmt => 26,
      AstType::Decorator => 27,
      AstType::DoWhileStmt => 28,
      AstType::EmptyStmt => 29,
      AstType::ExportAll => 30,
      AstType::ExportDecl => 31,
      AstType::ExportDefaultDecl => 32,
      AstType::ExportDefaultExpr => 33,
      AstType::ExportDefaultSpecifier => 34,
      AstType::ExportNamedSpecifier => 35,
      AstType::ExportNamespaceSpecifier => 36,
      AstType::ExprOrSpread => 37,
      AstType::ExprStmt => 38,
      AstType::FnDecl => 39,
      AstType::FnExpr => 40,
      AstType::ForInStmt => 41,
      AstType::ForOfStmt => 42,
      AstType::ForStmt => 43,
      AstType::Function => 44,
      AstType::GetterProp => 45,
      AstType::Ident => 46,
      AstType::IfStmt => 47,
      AstType::Import => 48,
      AstType::ImportDecl => 49,
      AstType::ImportDefaultSpecifier => 50,
      AstType::ImportNamedSpecifier => 51,
      AstType::ImportStarAsSpecifier => 52,
      AstType::Invalid => 53,
      AstType::JsxAttr => 54,
      AstType::JsxClosingElement => 55,
      AstType::JsxClosingFragment => 56,
      AstType::JsxElement => 57,
      AstType::JsxEmptyExpr => 58,
      AstType::JsxExprContainer => 59,
      AstType::JsxFragment => 60,
      AstType::JsxMemberExpr => 61,
      AstType::JsxNamespacedName => 62,
      AstType::JsxOpeningElement => 63,
      AstType::JsxOpeningFragment => 64,
      AstType::JsxSpreadChild => 65,
      AstType::JsxText => 66,
      AstType::KeyValuePatProp => 67,
      AstType::KeyValueProp => 68,
      AstType::LabeledStmt => 69,
      AstType::MemberExpr => 70,
      AstType::MetaPropExpr => 71,
      AstType::MethodProp => 72,
      AstType::Module => 73,
      AstType::NamedExport => 74,
      AstType::NewExpr => 75,
      AstType::Null => 76,
      AstType::Number => 77,
      AstType::ObjectLit => 78,
      AstType::ObjectPat => 79,
      AstType::OptCall => 80,
      AstType::OptChainExpr => 81,
      AstType::Param => 82,
      AstType::ParenExpr => 83,
      AstType::PrivateMethod => 84,
      AstType::PrivateName => 85,
      AstType::PrivateProp => 86,
      AstType::Regex => 87,
      AstType::RestPat => 88,
      AstType::ReturnStmt => 89,
      AstType::Script => 90,
      AstType::SeqExpr => 91,
      AstType::SetterProp => 92,
      AstType::SpreadElement => 93,
      AstType::StaticBlock => 94,
      AstType::Str => 95,
      AstType::Super => 96,
      AstType::SuperPropExpr => 97,
      AstType::SwitchCase => 98,
      AstType::SwitchStmt => 99,
      AstType::TaggedTpl => 100,
      AstType::ThisExpr => 101,
      AstType::ThrowStmt => 102,
      AstType::Tpl => 103,
      AstType::TplElement => 104,
      AstType::TryStmt => 105,
      AstType::TsArrayType => 106,
      AstType::TsAsExpr => 107,
      AstType::TsCallSignatureDecl => 108,
      AstType::TsConditionalType => 109,
      AstType::TsConstAssertion => 110,
      AstType::TsConstructorType => 111,
      AstType::TsConstructSignatureDecl => 112,
      AstType::TsEnumDecl => 113,
      AstType::TsEnumMember => 114,
      AstType::TsExportAssignment => 115,
      AstType::TsExprWithTypeArgs => 116,
      AstType::TsExternalModuleRef => 117,
      AstType::TsFnType => 118,
      AstType::TsGetterSignature => 119,
      AstType::TsImportEqualsDecl => 120,
      AstType::TsImportType => 121,
      AstType::TsIndexedAccessType => 122,
      AstType::TsIndexSignature => 123,
      AstType::TsInferType => 124,
      AstType::TsInstantiation => 125,
      AstType::TsInterfaceBody => 126,
      AstType::TsInterfaceDecl => 127,
      AstType::TsIntersectionType => 128,
      AstType::TsKeywordType => 129,
      AstType::TsLitType => 130,
      AstType::TsMappedType => 131,
      AstType::TsMethodSignature => 132,
      AstType::TsModuleBlock => 133,
      AstType::TsModuleDecl => 134,
      AstType::TsNamespaceDecl => 135,
      AstType::TsNamespaceExportDecl => 136,
      AstType::TsNonNullExpr => 137,
      AstType::TsOptionalType => 138,
      AstType::TsParamProp => 139,
      AstType::TsParenthesizedType => 140,
      AstType::TsPropertySignature => 141,
      AstType::TsQualifiedName => 142,
      AstType::TsRestType => 143,
      AstType::TsSatisfiesExpr => 144,
      AstType::TsSetterSignature => 145,
      AstType::TsThisType => 146,
      AstType::TsTplLitType => 147,
      AstType::TsTupleElement => 148,
      AstType::TsTupleType => 149,
      AstType::TsTypeAliasDecl => 150,
      AstType::TsTypeAnn => 151,
      AstType::TsTypeAssertion => 152,
      AstType::TsTypeLit => 153,
      AstType::TsTypeOperator => 154,
      AstType::TsTypeParam => 155,
      AstType::TsTypeParamDecl => 156,
      AstType::TsTypeParamInstantiation => 157,
      AstType::TsTypePredicate => 158,
      AstType::TsTypeQuery => 159,
      AstType::TsTypeRef => 160,
      AstType::TsUnionType => 161,
      AstType::UnaryExpr => 162,
      AstType::UpdateExpr => 163,
      AstType::UsingDecl => 164,
      AstType::VarDecl => 165,
      AstType::VarDeclarator => 166,
      AstType::WhileStmt => 167,
      AstType::WithStmt => 168,
      AstType::YieldExpr => 169,
    }
  }
  fn parse_by_id(id: i32) -> AstType {
    match id {
      0 => AstType::ArrayLit,
      1 => AstType::ArrayPat,
      2 => AstType::ArrowExpr,
      3 => AstType::AssignExpr,
      4 => AstType::AssignPat,
      5 => AstType::AssignPatProp,
      6 => AstType::AssignProp,
      7 => AstType::AutoAccessor,
      8 => AstType::AwaitExpr,
      9 => AstType::BigInt,
      10 => AstType::BindingIdent,
      11 => AstType::BinExpr,
      12 => AstType::BlockStmt,
      13 => AstType::Bool,
      14 => AstType::BreakStmt,
      15 => AstType::CallExpr,
      16 => AstType::CatchClause,
      17 => AstType::Class,
      18 => AstType::ClassDecl,
      19 => AstType::ClassExpr,
      20 => AstType::ClassMethod,
      21 => AstType::ClassProp,
      22 => AstType::ComputedPropName,
      23 => AstType::CondExpr,
      24 => AstType::Constructor,
      25 => AstType::ContinueStmt,
      26 => AstType::DebuggerStmt,
      27 => AstType::Decorator,
      28 => AstType::DoWhileStmt,
      29 => AstType::EmptyStmt,
      30 => AstType::ExportAll,
      31 => AstType::ExportDecl,
      32 => AstType::ExportDefaultDecl,
      33 => AstType::ExportDefaultExpr,
      34 => AstType::ExportDefaultSpecifier,
      35 => AstType::ExportNamedSpecifier,
      36 => AstType::ExportNamespaceSpecifier,
      37 => AstType::ExprOrSpread,
      38 => AstType::ExprStmt,
      39 => AstType::FnDecl,
      40 => AstType::FnExpr,
      41 => AstType::ForInStmt,
      42 => AstType::ForOfStmt,
      43 => AstType::ForStmt,
      44 => AstType::Function,
      45 => AstType::GetterProp,
      46 => AstType::Ident,
      47 => AstType::IfStmt,
      48 => AstType::Import,
      49 => AstType::ImportDecl,
      50 => AstType::ImportDefaultSpecifier,
      51 => AstType::ImportNamedSpecifier,
      52 => AstType::ImportStarAsSpecifier,
      53 => AstType::Invalid,
      54 => AstType::JsxAttr,
      55 => AstType::JsxClosingElement,
      56 => AstType::JsxClosingFragment,
      57 => AstType::JsxElement,
      58 => AstType::JsxEmptyExpr,
      59 => AstType::JsxExprContainer,
      60 => AstType::JsxFragment,
      61 => AstType::JsxMemberExpr,
      62 => AstType::JsxNamespacedName,
      63 => AstType::JsxOpeningElement,
      64 => AstType::JsxOpeningFragment,
      65 => AstType::JsxSpreadChild,
      66 => AstType::JsxText,
      67 => AstType::KeyValuePatProp,
      68 => AstType::KeyValueProp,
      69 => AstType::LabeledStmt,
      70 => AstType::MemberExpr,
      71 => AstType::MetaPropExpr,
      72 => AstType::MethodProp,
      73 => AstType::Module,
      74 => AstType::NamedExport,
      75 => AstType::NewExpr,
      76 => AstType::Null,
      77 => AstType::Number,
      78 => AstType::ObjectLit,
      79 => AstType::ObjectPat,
      80 => AstType::OptCall,
      81 => AstType::OptChainExpr,
      82 => AstType::Param,
      83 => AstType::ParenExpr,
      84 => AstType::PrivateMethod,
      85 => AstType::PrivateName,
      86 => AstType::PrivateProp,
      87 => AstType::Regex,
      88 => AstType::RestPat,
      89 => AstType::ReturnStmt,
      90 => AstType::Script,
      91 => AstType::SeqExpr,
      92 => AstType::SetterProp,
      93 => AstType::SpreadElement,
      94 => AstType::StaticBlock,
      95 => AstType::Str,
      96 => AstType::Super,
      97 => AstType::SuperPropExpr,
      98 => AstType::SwitchCase,
      99 => AstType::SwitchStmt,
      100 => AstType::TaggedTpl,
      101 => AstType::ThisExpr,
      102 => AstType::ThrowStmt,
      103 => AstType::Tpl,
      104 => AstType::TplElement,
      105 => AstType::TryStmt,
      106 => AstType::TsArrayType,
      107 => AstType::TsAsExpr,
      108 => AstType::TsCallSignatureDecl,
      109 => AstType::TsConditionalType,
      110 => AstType::TsConstAssertion,
      111 => AstType::TsConstructorType,
      112 => AstType::TsConstructSignatureDecl,
      113 => AstType::TsEnumDecl,
      114 => AstType::TsEnumMember,
      115 => AstType::TsExportAssignment,
      116 => AstType::TsExprWithTypeArgs,
      117 => AstType::TsExternalModuleRef,
      118 => AstType::TsFnType,
      119 => AstType::TsGetterSignature,
      120 => AstType::TsImportEqualsDecl,
      121 => AstType::TsImportType,
      122 => AstType::TsIndexedAccessType,
      123 => AstType::TsIndexSignature,
      124 => AstType::TsInferType,
      125 => AstType::TsInstantiation,
      126 => AstType::TsInterfaceBody,
      127 => AstType::TsInterfaceDecl,
      128 => AstType::TsIntersectionType,
      129 => AstType::TsKeywordType,
      130 => AstType::TsLitType,
      131 => AstType::TsMappedType,
      132 => AstType::TsMethodSignature,
      133 => AstType::TsModuleBlock,
      134 => AstType::TsModuleDecl,
      135 => AstType::TsNamespaceDecl,
      136 => AstType::TsNamespaceExportDecl,
      137 => AstType::TsNonNullExpr,
      138 => AstType::TsOptionalType,
      139 => AstType::TsParamProp,
      140 => AstType::TsParenthesizedType,
      141 => AstType::TsPropertySignature,
      142 => AstType::TsQualifiedName,
      143 => AstType::TsRestType,
      144 => AstType::TsSatisfiesExpr,
      145 => AstType::TsSetterSignature,
      146 => AstType::TsThisType,
      147 => AstType::TsTplLitType,
      148 => AstType::TsTupleElement,
      149 => AstType::TsTupleType,
      150 => AstType::TsTypeAliasDecl,
      151 => AstType::TsTypeAnn,
      152 => AstType::TsTypeAssertion,
      153 => AstType::TsTypeLit,
      154 => AstType::TsTypeOperator,
      155 => AstType::TsTypeParam,
      156 => AstType::TsTypeParamDecl,
      157 => AstType::TsTypeParamInstantiation,
      158 => AstType::TsTypePredicate,
      159 => AstType::TsTypeQuery,
      160 => AstType::TsTypeRef,
      161 => AstType::TsUnionType,
      162 => AstType::UnaryExpr,
      163 => AstType::UpdateExpr,
      164 => AstType::UsingDecl,
      165 => AstType::VarDecl,
      166 => AstType::VarDeclarator,
      167 => AstType::WhileStmt,
      168 => AstType::WithStmt,
      169 => AstType::YieldExpr,
      _ => AstType::Invalid,
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
      0 => BinaryOp::Add,
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

impl IdentifiableEnum<CommentKind> for CommentKind {
  fn get_id(&self) -> i32 {
    match self {
      CommentKind::Line => 0,
      CommentKind::Block => 1,
    }
  }
  fn parse_by_id(id: i32) -> CommentKind {
    match id {
      0 => CommentKind::Line,
      1 => CommentKind::Block,
      _ => CommentKind::Line,
    }
  }
}

impl IdentifiableEnum<EsVersion> for EsVersion {
  fn get_id(&self) -> i32 {
    match self {
      EsVersion::Es3 => 1,
      EsVersion::Es5 => 2,
      EsVersion::Es2015 => 3,
      EsVersion::Es2016 => 4,
      EsVersion::Es2017 => 5,
      EsVersion::Es2018 => 6,
      EsVersion::Es2019 => 7,
      EsVersion::Es2020 => 8,
      EsVersion::Es2021 => 9,
      EsVersion::Es2022 => 10,
      EsVersion::EsNext => 0,
    }
  }
  fn parse_by_id(id: i32) -> EsVersion {
    match id {
      1 => EsVersion::Es3,
      2 => EsVersion::Es5,
      3 => EsVersion::Es2015,
      4 => EsVersion::Es2016,
      5 => EsVersion::Es2017,
      6 => EsVersion::Es2018,
      7 => EsVersion::Es2019,
      8 => EsVersion::Es2020,
      9 => EsVersion::Es2021,
      10 => EsVersion::Es2022,
      0 => EsVersion::EsNext,
      _ => EsVersion::EsNext,
    }
  }
}

impl IdentifiableEnum<ImportPhase> for ImportPhase {
  fn get_id(&self) -> i32 {
    match self {
      ImportPhase::Defer => 0,
      ImportPhase::Evaluation => 1,
      ImportPhase::Source => 2,
    }
  }
  fn parse_by_id(id: i32) -> ImportPhase {
    match id {
      1 => ImportPhase::Evaluation,
      2 => ImportPhase::Source,
      _ => ImportPhase::Defer,
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

impl IdentifiableEnum<MetaPropKind> for MetaPropKind {
  fn get_id(&self) -> i32 {
    match self {
      MetaPropKind::NewTarget => 0,
      MetaPropKind::ImportMeta => 1,
    }
  }
  fn parse_by_id(id: i32) -> MetaPropKind {
    match id {
      0 => MetaPropKind::NewTarget,
      1 => MetaPropKind::ImportMeta,
      _ => MetaPropKind::NewTarget,
    }
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
      15 => MediaType::Unknown,
      _ => MediaType::Unknown,
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
      0 => MethodKind::Method,
      1 => MethodKind::Getter,
      2 => MethodKind::Setter,
      _ => MethodKind::Method,
    }
  }
}

#[derive(Default, Debug, Copy, Clone)]
pub enum ParseMode {
  #[default]
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
      1 => ParseMode::Script,
      _ => ParseMode::Module,
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
      0 => Sign::NoSign,
      1 => Sign::Minus,
      2 => Sign::Plus,
      _ => Sign::NoSign,
    }
  }
}

impl IdentifiableEnum<SourceMapOption> for SourceMapOption {
  fn get_id(&self) -> i32 {
    match self {
      SourceMapOption::Inline => 0,
      SourceMapOption::Separate => 1,
      SourceMapOption::None => 2,
    }
  }
  fn parse_by_id(id: i32) -> SourceMapOption {
    match id {
      0 => SourceMapOption::Inline,
      1 => SourceMapOption::Separate,
      2 => SourceMapOption::None,
      _ => SourceMapOption::Inline,
    }
  }
}

#[derive(Default, Debug, Copy, Clone)]
pub enum TokenType {
  #[default]
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
      TokenType::Unknown => 0,
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
    }
  }
  fn parse_by_id(id: i32) -> TokenType {
    match id {
      0 => TokenType::Unknown,
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
      AssignOp::AddAssign => TokenType::AddAssign,
      AssignOp::AndAssign => TokenType::AndAssign,
      AssignOp::Assign => TokenType::Assign,
      AssignOp::BitAndAssign => TokenType::BitAndAssign,
      AssignOp::BitOrAssign => TokenType::BitOrAssign,
      AssignOp::BitXorAssign => TokenType::BitXorAssign,
      AssignOp::DivAssign => TokenType::DivAssign,
      AssignOp::ExpAssign => TokenType::ExpAssign,
      AssignOp::LShiftAssign => TokenType::LShiftAssign,
      AssignOp::ModAssign => TokenType::ModAssign,
      AssignOp::MulAssign => TokenType::MulAssign,
      AssignOp::NullishAssign => TokenType::NullishAssign,
      AssignOp::OrAssign => TokenType::OrAssign,
      AssignOp::RShiftAssign => TokenType::RShiftAssign,
      AssignOp::SubAssign => TokenType::SubAssign,
      AssignOp::ZeroFillRShiftAssign => TokenType::ZeroFillRShiftAssign,
    }
  }

  pub fn parse_by_binary_operator(token: &BinOpToken) -> TokenType {
    match token {
      BinOpToken::Add => TokenType::Add,
      BinOpToken::BitAnd => TokenType::BitAnd,
      BinOpToken::BitOr => TokenType::BitOr,
      BinOpToken::BitXor => TokenType::BitXor,
      BinOpToken::Div => TokenType::Div,
      BinOpToken::EqEq => TokenType::EqEq,
      BinOpToken::EqEqEq => TokenType::EqEqEq,
      BinOpToken::Exp => TokenType::Exp,
      BinOpToken::Gt => TokenType::Gt,
      BinOpToken::GtEq => TokenType::GtEq,
      BinOpToken::LShift => TokenType::LShift,
      BinOpToken::LogicalAnd => TokenType::LogicalAnd,
      BinOpToken::LogicalOr => TokenType::LogicalOr,
      BinOpToken::Lt => TokenType::Lt,
      BinOpToken::LtEq => TokenType::LtEq,
      BinOpToken::Mod => TokenType::Mod,
      BinOpToken::Mul => TokenType::Mul,
      BinOpToken::NotEq => TokenType::NotEq,
      BinOpToken::NotEqEq => TokenType::NotEqEq,
      BinOpToken::NullishCoalescing => TokenType::NullishCoalescing,
      BinOpToken::RShift => TokenType::RShift,
      BinOpToken::Sub => TokenType::Sub,
      BinOpToken::ZeroFillRShift => TokenType::ZeroFillRShift,
    }
  }

  pub fn parse_by_generic_operator(token: &Token) -> TokenType {
    match token {
      Token::Arrow => TokenType::Arrow,
      Token::At => TokenType::At,
      Token::BackQuote => TokenType::BackQuote,
      Token::Bang => TokenType::Bang,
      Token::Colon => TokenType::Colon,
      Token::Comma => TokenType::Comma,
      Token::DollarLBrace => TokenType::DollarLBrace,
      Token::Dot => TokenType::Dot,
      Token::DotDotDot => TokenType::DotDotDot,
      Token::Hash => TokenType::Hash,
      Token::JSXTagEnd => TokenType::JSXTagEnd,
      Token::JSXTagStart => TokenType::JSXTagStart,
      Token::LBrace => TokenType::LBrace,
      Token::LBracket => TokenType::LBracket,
      Token::LParen => TokenType::LParen,
      Token::MinusMinus => TokenType::MinusMinus,
      Token::PlusPlus => TokenType::PlusPlus,
      Token::QuestionMark => TokenType::QuestionMark,
      Token::RBrace => TokenType::RBrace,
      Token::RBracket => TokenType::RBracket,
      Token::RParen => TokenType::RParen,
      Token::Semi => TokenType::Semi,
      Token::Tilde => TokenType::Tilde,
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
      0 => TsKeywordTypeKind::TsAnyKeyword,
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
      0 => TsTypeOperatorOp::KeyOf,
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
      0 => UnaryOp::Void,
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
      0 => UpdateOp::PlusPlus,
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
      0 => VarDeclKind::Const,
      1 => VarDeclKind::Let,
      2 => VarDeclKind::Var,
      _ => VarDeclKind::Const,
    }
  }
}
