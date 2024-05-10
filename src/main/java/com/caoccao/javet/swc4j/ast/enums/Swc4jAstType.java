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

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.miscs.*;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.*;
import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumId;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;

import java.util.stream.Stream;

@Jni2RustClass(ignore = true)
public enum Swc4jAstType implements ISwc4jEnumId {
    ArrayLit(0, Swc4jAstArrayLit.class),
    ArrayPat(1, Swc4jAstArrayPat.class),
    ArrowExpr(2, Swc4jAstArrowExpr.class),
    AssignExpr(3, Swc4jAstAssignExpr.class),
    AssignPat(4, Swc4jAstAssignPat.class),
    AssignPatProp(5, Swc4jAstAssignPatProp.class),
    AssignProp(6, Swc4jAstAssignProp.class),
    AutoAccessor(7, Swc4jAstAutoAccessor.class),
    AwaitExpr(8, Swc4jAstAwaitExpr.class),
    BigInt(9, Swc4jAstBigInt.class),
    BindingIdent(10, Swc4jAstBindingIdent.class),
    BinExpr(11, Swc4jAstBinExpr.class),
    BlockStmt(12, Swc4jAstBlockStmt.class),
    Bool(13, Swc4jAstBool.class),
    BreakStmt(14, Swc4jAstBreakStmt.class),
    CallExpr(15, Swc4jAstCallExpr.class),
    CatchClause(16, Swc4jAstCatchClause.class),
    Class(17, Swc4jAstClass.class),
    ClassDecl(18, Swc4jAstClassDecl.class),
    ClassExpr(19, Swc4jAstClassExpr.class),
    ClassMethod(20, Swc4jAstClassMethod.class),
    ClassProp(21, Swc4jAstClassProp.class),
    ComputedPropName(22, Swc4jAstComputedPropName.class),
    CondExpr(23, Swc4jAstCondExpr.class),
    Constructor(24, Swc4jAstConstructor.class),
    ContinueStmt(25, Swc4jAstContinueStmt.class),
    DebuggerStmt(26, Swc4jAstDebuggerStmt.class),
    Decorator(27, Swc4jAstDecorator.class),
    DoWhileStmt(28, Swc4jAstDoWhileStmt.class),
    EmptyStmt(29, Swc4jAstEmptyStmt.class),
    ExportAll(30, Swc4jAstExportAll.class),
    ExportDecl(31, Swc4jAstExportDecl.class),
    ExportDefaultDecl(32, Swc4jAstExportDefaultDecl.class),
    ExportDefaultExpr(33, Swc4jAstExportDefaultExpr.class),
    ExportDefaultSpecifier(34, Swc4jAstExportDefaultSpecifier.class),
    ExportNamedSpecifier(35, Swc4jAstExportNamedSpecifier.class),
    ExportNamespaceSpecifier(36, Swc4jAstExportNamespaceSpecifier.class),
    ExprOrSpread(37, Swc4jAstExprOrSpread.class),
    ExprStmt(38, Swc4jAstExprStmt.class),
    FnDecl(39, Swc4jAstFnDecl.class),
    FnExpr(40, Swc4jAstFnExpr.class),
    ForInStmt(41, Swc4jAstForInStmt.class),
    ForOfStmt(42, Swc4jAstForOfStmt.class),
    ForStmt(43, Swc4jAstForStmt.class),
    Function(44, Swc4jAstFunction.class),
    GetterProp(45, Swc4jAstGetterProp.class),
    Ident(46, Swc4jAstIdent.class),
    IfStmt(47, Swc4jAstIfStmt.class),
    Import(48, Swc4jAstImport.class),
    ImportDecl(49, Swc4jAstImportDecl.class),
    ImportDefaultSpecifier(50, Swc4jAstImportDefaultSpecifier.class),
    ImportNamedSpecifier(51, Swc4jAstImportNamedSpecifier.class),
    ImportStarAsSpecifier(52, Swc4jAstImportStarAsSpecifier.class),
    Invalid(53, Swc4jAstInvalid.class),
    JsxAttr(54, Swc4jAstJsxAttr.class),
    JsxClosingElement(55, Swc4jAstJsxClosingElement.class),
    JsxClosingFragment(56, Swc4jAstJsxClosingFragment.class),
    JsxElement(57, Swc4jAstJsxElement.class),
    JsxEmptyExpr(58, Swc4jAstJsxEmptyExpr.class),
    JsxExprContainer(59, Swc4jAstJsxExprContainer.class),
    JsxFragment(60, Swc4jAstJsxFragment.class),
    JsxMemberExpr(61, Swc4jAstJsxMemberExpr.class),
    JsxNamespacedName(62, Swc4jAstJsxNamespacedName.class),
    JsxOpeningElement(63, Swc4jAstJsxOpeningElement.class),
    JsxOpeningFragment(64, Swc4jAstJsxOpeningFragment.class),
    JsxSpreadChild(65, Swc4jAstJsxSpreadChild.class),
    JsxText(66, Swc4jAstJsxText.class),
    KeyValuePatProp(67, Swc4jAstKeyValuePatProp.class),
    KeyValueProp(68, Swc4jAstKeyValueProp.class),
    LabeledStmt(69, Swc4jAstLabeledStmt.class),
    MemberExpr(70, Swc4jAstMemberExpr.class),
    MetaPropExpr(71, Swc4jAstMetaPropExpr.class),
    MethodProp(72, Swc4jAstMethodProp.class),
    Module(73, Swc4jAstModule.class),
    NamedExport(74, Swc4jAstNamedExport.class),
    NewExpr(75, Swc4jAstNewExpr.class),
    Null(76, Swc4jAstNull.class),
    Number(77, Swc4jAstNumber.class),
    ObjectLit(78, Swc4jAstObjectLit.class),
    ObjectPat(79, Swc4jAstObjectPat.class),
    OptCall(80, Swc4jAstOptCall.class),
    OptChainExpr(81, Swc4jAstOptChainExpr.class),
    Param(82, Swc4jAstParam.class),
    ParenExpr(83, Swc4jAstParenExpr.class),
    PrivateMethod(84, Swc4jAstPrivateMethod.class),
    PrivateName(85, Swc4jAstPrivateName.class),
    PrivateProp(86, Swc4jAstPrivateProp.class),
    Regex(87, Swc4jAstRegex.class),
    RestPat(88, Swc4jAstRestPat.class),
    ReturnStmt(89, Swc4jAstReturnStmt.class),
    Script(90, Swc4jAstScript.class),
    SeqExpr(91, Swc4jAstSeqExpr.class),
    SetterProp(92, Swc4jAstSetterProp.class),
    SpreadElement(93, Swc4jAstSpreadElement.class),
    StaticBlock(94, Swc4jAstStaticBlock.class),
    Str(95, Swc4jAstStr.class),
    Super(96, Swc4jAstSuper.class),
    SuperPropExpr(97, Swc4jAstSuperPropExpr.class),
    SwitchCase(98, Swc4jAstSwitchCase.class),
    SwitchStmt(99, Swc4jAstSwitchStmt.class),
    TaggedTpl(100, Swc4jAstTaggedTpl.class),
    ThisExpr(101, Swc4jAstThisExpr.class),
    ThrowStmt(102, Swc4jAstThrowStmt.class),
    Tpl(103, Swc4jAstTpl.class),
    TplElement(104, Swc4jAstTplElement.class),
    TryStmt(105, Swc4jAstTryStmt.class),
    TsArrayType(106, Swc4jAstTsArrayType.class),
    TsAsExpr(107, Swc4jAstTsAsExpr.class),
    TsCallSignatureDecl(108, Swc4jAstTsCallSignatureDecl.class),
    TsConditionalType(109, Swc4jAstTsConditionalType.class),
    TsConstAssertion(110, Swc4jAstTsConstAssertion.class),
    TsConstructorType(111, Swc4jAstTsConstructorType.class),
    TsConstructSignatureDecl(112, Swc4jAstTsConstructSignatureDecl.class),
    TsEnumDecl(113, Swc4jAstTsEnumDecl.class),
    TsEnumMember(114, Swc4jAstTsEnumMember.class),
    TsExportAssignment(115, Swc4jAstTsExportAssignment.class),
    TsExprWithTypeArgs(116, Swc4jAstTsExprWithTypeArgs.class),
    TsExternalModuleRef(117, Swc4jAstTsExternalModuleRef.class),
    TsFnType(118, Swc4jAstTsFnType.class),
    TsGetterSignature(119, Swc4jAstTsGetterSignature.class),
    TsImportEqualsDecl(120, Swc4jAstTsImportEqualsDecl.class),
    TsImportType(121, Swc4jAstTsImportType.class),
    TsIndexedAccessType(122, Swc4jAstTsIndexedAccessType.class),
    TsIndexSignature(123, Swc4jAstTsIndexSignature.class),
    TsInferType(124, Swc4jAstTsInferType.class),
    TsInstantiation(125, Swc4jAstTsInstantiation.class),
    TsInterfaceBody(126, Swc4jAstTsInterfaceBody.class),
    TsInterfaceDecl(127, Swc4jAstTsInterfaceDecl.class),
    TsIntersectionType(128, Swc4jAstTsIntersectionType.class),
    TsKeywordType(129, Swc4jAstTsKeywordType.class),
    TsLitType(130, Swc4jAstTsLitType.class),
    TsMappedType(131, Swc4jAstTsMappedType.class),
    TsMethodSignature(132, Swc4jAstTsMethodSignature.class),
    TsModuleBlock(133, Swc4jAstTsModuleBlock.class),
    TsModuleDecl(134, Swc4jAstTsModuleDecl.class),
    TsNamespaceDecl(135, Swc4jAstTsNamespaceDecl.class),
    TsNamespaceExportDecl(136, Swc4jAstTsNamespaceExportDecl.class),
    TsNonNullExpr(137, Swc4jAstTsNonNullExpr.class),
    TsOptionalType(138, Swc4jAstTsOptionalType.class),
    TsParamProp(139, Swc4jAstTsParamProp.class),
    TsParenthesizedType(140, Swc4jAstTsParenthesizedType.class),
    TsPropertySignature(141, Swc4jAstTsPropertySignature.class),
    TsQualifiedName(142, Swc4jAstTsQualifiedName.class),
    TsRestType(143, Swc4jAstTsRestType.class),
    TsSatisfiesExpr(144, Swc4jAstTsSatisfiesExpr.class),
    TsSetterSignature(145, Swc4jAstTsSetterSignature.class),
    TsThisType(146, Swc4jAstTsThisType.class),
    TsTplLitType(147, Swc4jAstTsTplLitType.class),
    TsTupleElement(148, Swc4jAstTsTupleElement.class),
    TsTupleType(149, Swc4jAstTsTupleType.class),
    TsTypeAliasDecl(150, Swc4jAstTsTypeAliasDecl.class),
    TsTypeAnn(151, Swc4jAstTsTypeAnn.class),
    TsTypeAssertion(152, Swc4jAstTsTypeAssertion.class),
    TsTypeLit(153, Swc4jAstTsTypeLit.class),
    TsTypeOperator(154, Swc4jAstTsTypeOperator.class),
    TsTypeParam(155, Swc4jAstTsTypeParam.class),
    TsTypeParamDecl(156, Swc4jAstTsTypeParamDecl.class),
    TsTypeParamInstantiation(157, Swc4jAstTsTypeParamInstantiation.class),
    TsTypePredicate(158, Swc4jAstTsTypePredicate.class),
    TsTypeQuery(159, Swc4jAstTsTypeQuery.class),
    TsTypeRef(160, Swc4jAstTsTypeRef.class),
    TsUnionType(161, Swc4jAstTsUnionType.class),
    UnaryExpr(162, Swc4jAstUnaryExpr.class),
    UpdateExpr(163, Swc4jAstUpdateExpr.class),
    UsingDecl(164, Swc4jAstUsingDecl.class),
    VarDecl(165, Swc4jAstVarDecl.class),
    VarDeclarator(166, Swc4jAstVarDeclarator.class),
    WhileStmt(167, Swc4jAstWhileStmt.class),
    WithStmt(168, Swc4jAstWithStmt.class),
    YieldExpr(169, Swc4jAstYieldExpr.class),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstType[] TYPES = new Swc4jAstType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final Class<? extends ISwc4jAst> astClass;
    private final int id;

    Swc4jAstType(int id, Class<? extends ISwc4jAst> astClass) {
        this.astClass = astClass;
        this.id = id;
    }

    public static Swc4jAstType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Invalid;
    }

    public Class<? extends ISwc4jAst> getAstClass() {
        return astClass;
    }

    @Override
    public int getId() {
        return id;
    }
}
