/*
 * Copyright (c) 2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumId;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;

import java.util.stream.Stream;

@Jni2RustClass(ignore = true)
public enum Swc4jAstType implements ISwc4jEnumId {
    ArrayLit(0),
    ArrayPat(1),
    ArrowExpr(2),
    AssignExpr(3),
    AssignPat(4),
    AssignPatProp(5),
    AssignProp(6),
    AutoAccessor(7),
    AwaitExpr(8),
    BigInt(9),
    BindingIdent(10),
    BinExpr(11),
    BlockStmt(12),
    Bool(13),
    BreakStmt(14),
    CallExpr(15),
    CatchClause(16),
    Class(17),
    ClassDecl(18),
    ClassExpr(19),
    ClassMethod(20),
    ClassProp(21),
    ComputedPropName(22),
    CondExpr(23),
    Constructor(24),
    ContinueStmt(25),
    DebuggerStmt(26),
    Decorator(27),
    DoWhileStmt(28),
    EmptyStmt(29),
    ExportAll(30),
    ExportDecl(31),
    ExportDefaultDecl(32),
    ExportDefaultExpr(33),
    ExportDefaultSpecifier(34),
    ExportNamedSpecifier(35),
    ExportNamespaceSpecifier(36),
    ExprOrSpread(37),
    ExprStmt(38),
    FnDecl(39),
    FnExpr(40),
    ForInStmt(41),
    ForOfStmt(42),
    ForStmt(43),
    Function(44),
    GetterProp(45),
    Ident(46),
    IfStmt(47),
    Import(48),
    ImportDecl(49),
    ImportDefaultSpecifier(50),
    ImportNamedSpecifier(51),
    ImportStarAsSpecifier(52),
    Invalid(53),
    JsxAttr(54),
    JsxClosingElement(55),
    JsxClosingFragment(56),
    JsxElement(57),
    JsxEmptyExpr(58),
    JsxExprContainer(59),
    JsxFragment(60),
    JsxMemberExpr(61),
    JsxNamespacedName(62),
    JsxOpeningElement(63),
    JsxOpeningFragment(64),
    JsxSpreadChild(65),
    JsxText(66),
    KeyValuePatProp(67),
    KeyValueProp(68),
    LabeledStmt(69),
    MemberExpr(70),
    MetaPropExpr(71),
    MethodProp(72),
    Module(73),
    NamedExport(74),
    NewExpr(75),
    Null(76),
    Number(77),
    ObjectLit(78),
    ObjectPat(79),
    OptCall(80),
    OptChainExpr(81),
    Param(82),
    ParenExpr(83),
    PrivateMethod(84),
    PrivateName(85),
    PrivateProp(86),
    Regex(87),
    RestPat(88),
    ReturnStmt(89),
    Script(90),
    SeqExpr(91),
    SetterProp(92),
    SpreadElement(93),
    StaticBlock(94),
    Str(95),
    Super(96),
    SuperPropExpr(97),
    SwitchCase(98),
    SwitchStmt(99),
    TaggedTpl(100),
    ThisExpr(101),
    ThrowStmt(102),
    Tpl(103),
    TplElement(104),
    TryStmt(105),
    TsArrayType(106),
    TsAsExpr(107),
    TsCallSignatureDecl(108),
    TsConditionalType(109),
    TsConstAssertion(110),
    TsConstructorType(111),
    TsConstructSignatureDecl(112),
    TsEnumDecl(113),
    TsEnumMember(114),
    TsExportAssignment(115),
    TsExprWithTypeArgs(116),
    TsExternalModuleRef(117),
    TsFnType(118),
    TsGetterSignature(119),
    TsImportEqualsDecl(120),
    TsImportType(121),
    TsIndexedAccessType(122),
    TsIndexSignature(123),
    TsInferType(124),
    TsInstantiation(125),
    TsInterfaceBody(126),
    TsInterfaceDecl(127),
    TsIntersectionType(128),
    TsKeywordType(129),
    TsLitType(130),
    TsMappedType(131),
    TsMethodSignature(132),
    TsModuleBlock(133),
    TsModuleDecl(134),
    TsNamespaceDecl(135),
    TsNamespaceExportDecl(136),
    TsNonNullExpr(137),
    TsOptionalType(138),
    TsParamProp(139),
    TsParenthesizedType(140),
    TsPropertySignature(141),
    TsQualifiedName(142),
    TsRestType(143),
    TsSatisfiesExpr(144),
    TsSetterSignature(145),
    TsThisType(146),
    TsTplLitType(147),
    TsTupleElement(148),
    TsTupleType(149),
    TsTypeAliasDecl(150),
    TsTypeAnn(151),
    TsTypeAssertion(152),
    TsTypeLit(153),
    TsTypeOperator(154),
    TsTypeParam(155),
    TsTypeParamDecl(156),
    TsTypeParamInstantiation(157),
    TsTypePredicate(158),
    TsTypeQuery(159),
    TsTypeRef(160),
    TsUnionType(161),
    UnaryExpr(162),
    UpdateExpr(163),
    UsingDecl(164),
    VarDecl(165),
    VarDeclarator(166),
    WhileStmt(167),
    WithStmt(168),
    YieldExpr(169),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstType[] TYPES = new Swc4jAstType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jAstType(int id) {
        this.id = id;
    }

    public static Swc4jAstType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Invalid;
    }

    @Override
    public int getId() {
        return id;
    }
}
