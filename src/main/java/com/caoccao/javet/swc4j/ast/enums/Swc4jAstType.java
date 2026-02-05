/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
 *
 * Licensed under the Apache License,                                          Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,                                          software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,                                          either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.miscs.*;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.*;
import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumId;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The enum swc4j ast type.
 */
@Jni2RustClass(ignore = true)
public enum Swc4jAstType implements ISwc4jEnumId {
    /**
     * Array lit swc4j ast type.
     */
    ArrayLit(0, Swc4jAstArrayLit.class),
    /**
     * Array pat swc4j ast type.
     */
    ArrayPat(1, Swc4jAstArrayPat.class),
    /**
     * Arrow expr swc4j ast type.
     */
    ArrowExpr(2, Swc4jAstArrowExpr.class),
    /**
     * Assign expr swc4j ast type.
     */
    AssignExpr(3, Swc4jAstAssignExpr.class),
    /**
     * Assign pat swc4j ast type.
     */
    AssignPat(4, Swc4jAstAssignPat.class),
    /**
     * Assign pat prop swc4j ast type.
     */
    AssignPatProp(5, Swc4jAstAssignPatProp.class),
    /**
     * Assign prop swc4j ast type.
     */
    AssignProp(6, Swc4jAstAssignProp.class),
    /**
     * Auto accessor swc4j ast type.
     */
    AutoAccessor(7, Swc4jAstAutoAccessor.class),
    /**
     * Await expr swc4j ast type.
     */
    AwaitExpr(8, Swc4jAstAwaitExpr.class),
    /**
     * Big int swc4j ast type.
     */
    BigInt(9, Swc4jAstBigInt.class),
    /**
     * Binding ident swc4j ast type.
     */
    BindingIdent(10, Swc4jAstBindingIdent.class),
    /**
     * Bin expr swc4j ast type.
     */
    BinExpr(11, Swc4jAstBinExpr.class),
    /**
     * Block stmt swc4j ast type.
     */
    BlockStmt(12, Swc4jAstBlockStmt.class),
    /**
     * Bool swc4j ast type.
     */
    Bool(13, Swc4jAstBool.class),
    /**
     * Break stmt swc4j ast type.
     */
    BreakStmt(14, Swc4jAstBreakStmt.class),
    /**
     * Call expr swc4j ast type.
     */
    CallExpr(15, Swc4jAstCallExpr.class),
    /**
     * Catch clause swc4j ast type.
     */
    CatchClause(16, Swc4jAstCatchClause.class),
    /**
     * Class swc4j ast type.
     */
    Class(17, Swc4jAstClass.class),
    /**
     * Class decl swc4j ast type.
     */
    ClassDecl(18, Swc4jAstClassDecl.class),
    /**
     * Class expr swc4j ast type.
     */
    ClassExpr(19, Swc4jAstClassExpr.class),
    /**
     * Class method swc4j ast type.
     */
    ClassMethod(20, Swc4jAstClassMethod.class),
    /**
     * Class prop swc4j ast type.
     */
    ClassProp(21, Swc4jAstClassProp.class),
    /**
     * Computed prop name swc4j ast type.
     */
    ComputedPropName(22, Swc4jAstComputedPropName.class),
    /**
     * Cond expr swc4j ast type.
     */
    CondExpr(23, Swc4jAstCondExpr.class),
    /**
     * Constructor swc4j ast type.
     */
    Constructor(24, Swc4jAstConstructor.class),
    /**
     * Continue stmt swc4j ast type.
     */
    ContinueStmt(25, Swc4jAstContinueStmt.class),
    /**
     * Debugger stmt swc4j ast type.
     */
    DebuggerStmt(26, Swc4jAstDebuggerStmt.class),
    /**
     * Decorator swc4j ast type.
     */
    Decorator(27, Swc4jAstDecorator.class),
    /**
     * Do while stmt swc4j ast type.
     */
    DoWhileStmt(28, Swc4jAstDoWhileStmt.class),
    /**
     * Empty stmt swc4j ast type.
     */
    EmptyStmt(29, Swc4jAstEmptyStmt.class),
    /**
     * Export all swc4j ast type.
     */
    ExportAll(30, Swc4jAstExportAll.class),
    /**
     * Export decl swc4j ast type.
     */
    ExportDecl(31, Swc4jAstExportDecl.class),
    /**
     * Export default decl swc4j ast type.
     */
    ExportDefaultDecl(32, Swc4jAstExportDefaultDecl.class),
    /**
     * Export default expr swc4j ast type.
     */
    ExportDefaultExpr(33, Swc4jAstExportDefaultExpr.class),
    /**
     * Export default specifier swc4j ast type.
     */
    ExportDefaultSpecifier(34, Swc4jAstExportDefaultSpecifier.class),
    /**
     * Export named specifier swc4j ast type.
     */
    ExportNamedSpecifier(35, Swc4jAstExportNamedSpecifier.class),
    /**
     * Export namespace specifier swc4j ast type.
     */
    ExportNamespaceSpecifier(36, Swc4jAstExportNamespaceSpecifier.class),
    /**
     * Expr or spread swc4j ast type.
     */
    ExprOrSpread(37, Swc4jAstExprOrSpread.class),
    /**
     * Expr stmt swc4j ast type.
     */
    ExprStmt(38, Swc4jAstExprStmt.class),
    /**
     * Fn decl swc4j ast type.
     */
    FnDecl(39, Swc4jAstFnDecl.class),
    /**
     * Fn expr swc4j ast type.
     */
    FnExpr(40, Swc4jAstFnExpr.class),
    /**
     * For in stmt swc4j ast type.
     */
    ForInStmt(41, Swc4jAstForInStmt.class),
    /**
     * For of stmt swc4j ast type.
     */
    ForOfStmt(42, Swc4jAstForOfStmt.class),
    /**
     * For stmt swc4j ast type.
     */
    ForStmt(43, Swc4jAstForStmt.class),
    /**
     * Function swc4j ast type.
     */
    Function(44, Swc4jAstFunction.class),
    /**
     * Getter prop swc4j ast type.
     */
    GetterProp(45, Swc4jAstGetterProp.class),
    /**
     * Ident swc4j ast type.
     */
    Ident(46, Swc4jAstIdent.class),
    /**
     * Ident name swc4j ast type.
     */
    IdentName(47, Swc4jAstIdentName.class),
    /**
     * If stmt swc4j ast type.
     */
    IfStmt(48, Swc4jAstIfStmt.class),
    /**
     * Import swc4j ast type.
     */
    Import(49, Swc4jAstImport.class),
    /**
     * Import decl swc4j ast type.
     */
    ImportDecl(50, Swc4jAstImportDecl.class),
    /**
     * Import default specifier swc4j ast type.
     */
    ImportDefaultSpecifier(51, Swc4jAstImportDefaultSpecifier.class),
    /**
     * Import named specifier swc4j ast type.
     */
    ImportNamedSpecifier(52, Swc4jAstImportNamedSpecifier.class),
    /**
     * Import star as specifier swc4j ast type.
     */
    ImportStarAsSpecifier(53, Swc4jAstImportStarAsSpecifier.class),
    /**
     * Invalid swc4j ast type.
     */
    Invalid(54, Swc4jAstInvalid.class),
    /**
     * Jsx attr swc4j ast type.
     */
    JsxAttr(55, Swc4jAstJsxAttr.class),
    /**
     * Jsx closing element swc4j ast type.
     */
    JsxClosingElement(56, Swc4jAstJsxClosingElement.class),
    /**
     * Jsx closing fragment swc4j ast type.
     */
    JsxClosingFragment(57, Swc4jAstJsxClosingFragment.class),
    /**
     * Jsx element swc4j ast type.
     */
    JsxElement(58, Swc4jAstJsxElement.class),
    /**
     * Jsx empty expr swc4j ast type.
     */
    JsxEmptyExpr(59, Swc4jAstJsxEmptyExpr.class),
    /**
     * Jsx expr container swc4j ast type.
     */
    JsxExprContainer(60, Swc4jAstJsxExprContainer.class),
    /**
     * Jsx fragment swc4j ast type.
     */
    JsxFragment(61, Swc4jAstJsxFragment.class),
    /**
     * Jsx member expr swc4j ast type.
     */
    JsxMemberExpr(62, Swc4jAstJsxMemberExpr.class),
    /**
     * Jsx namespaced name swc4j ast type.
     */
    JsxNamespacedName(63, Swc4jAstJsxNamespacedName.class),
    /**
     * Jsx opening element swc4j ast type.
     */
    JsxOpeningElement(64, Swc4jAstJsxOpeningElement.class),
    /**
     * Jsx opening fragment swc4j ast type.
     */
    JsxOpeningFragment(65, Swc4jAstJsxOpeningFragment.class),
    /**
     * Jsx spread child swc4j ast type.
     */
    JsxSpreadChild(66, Swc4jAstJsxSpreadChild.class),
    /**
     * Jsx text swc4j ast type.
     */
    JsxText(67, Swc4jAstJsxText.class),
    /**
     * Key value pat prop swc4j ast type.
     */
    KeyValuePatProp(68, Swc4jAstKeyValuePatProp.class),
    /**
     * Key value prop swc4j ast type.
     */
    KeyValueProp(69, Swc4jAstKeyValueProp.class),
    /**
     * Labeled stmt swc4j ast type.
     */
    LabeledStmt(70, Swc4jAstLabeledStmt.class),
    /**
     * Member expr swc4j ast type.
     */
    MemberExpr(71, Swc4jAstMemberExpr.class),
    /**
     * Meta prop expr swc4j ast type.
     */
    MetaPropExpr(72, Swc4jAstMetaPropExpr.class),
    /**
     * Method prop swc4j ast type.
     */
    MethodProp(73, Swc4jAstMethodProp.class),
    /**
     * Module swc4j ast type.
     */
    Module(74, Swc4jAstModule.class),
    /**
     * Named export swc4j ast type.
     */
    NamedExport(75, Swc4jAstNamedExport.class),
    /**
     * New expr swc4j ast type.
     */
    NewExpr(76, Swc4jAstNewExpr.class),
    /**
     * Null swc4j ast type.
     */
    Null(77, Swc4jAstNull.class),
    /**
     * Number swc4j ast type.
     */
    Number(78, Swc4jAstNumber.class),
    /**
     * Object lit swc4j ast type.
     */
    ObjectLit(79, Swc4jAstObjectLit.class),
    /**
     * Object pat swc4j ast type.
     */
    ObjectPat(80, Swc4jAstObjectPat.class),
    /**
     * Opt call swc4j ast type.
     */
    OptCall(81, Swc4jAstOptCall.class),
    /**
     * Opt chain expr swc4j ast type.
     */
    OptChainExpr(82, Swc4jAstOptChainExpr.class),
    /**
     * Param swc4j ast type.
     */
    Param(83, Swc4jAstParam.class),
    /**
     * Paren expr swc4j ast type.
     */
    ParenExpr(84, Swc4jAstParenExpr.class),
    /**
     * Private method swc4j ast type.
     */
    PrivateMethod(85, Swc4jAstPrivateMethod.class),
    /**
     * Private name swc4j ast type.
     */
    PrivateName(86, Swc4jAstPrivateName.class),
    /**
     * Private prop swc4j ast type.
     */
    PrivateProp(87, Swc4jAstPrivateProp.class),
    /**
     * Regex swc4j ast type.
     */
    Regex(88, Swc4jAstRegex.class),
    /**
     * Rest pat swc4j ast type.
     */
    RestPat(89, Swc4jAstRestPat.class),
    /**
     * Return stmt swc4j ast type.
     */
    ReturnStmt(90, Swc4jAstReturnStmt.class),
    /**
     * Script swc4j ast type.
     */
    Script(91, Swc4jAstScript.class),
    /**
     * Seq expr swc4j ast type.
     */
    SeqExpr(92, Swc4jAstSeqExpr.class),
    /**
     * Setter prop swc4j ast type.
     */
    SetterProp(93, Swc4jAstSetterProp.class),
    /**
     * Spread element swc4j ast type.
     */
    SpreadElement(94, Swc4jAstSpreadElement.class),
    /**
     * Static block swc4j ast type.
     */
    StaticBlock(95, Swc4jAstStaticBlock.class),
    /**
     * Str swc4j ast type.
     */
    Str(96, Swc4jAstStr.class),
    /**
     * Super swc4j ast type.
     */
    Super(97, Swc4jAstSuper.class),
    /**
     * Super prop expr swc4j ast type.
     */
    SuperPropExpr(98, Swc4jAstSuperPropExpr.class),
    /**
     * Switch case swc4j ast type.
     */
    SwitchCase(99, Swc4jAstSwitchCase.class),
    /**
     * Switch stmt swc4j ast type.
     */
    SwitchStmt(100, Swc4jAstSwitchStmt.class),
    /**
     * Tagged tpl swc4j ast type.
     */
    TaggedTpl(101, Swc4jAstTaggedTpl.class),
    /**
     * This expr swc4j ast type.
     */
    ThisExpr(102, Swc4jAstThisExpr.class),
    /**
     * Throw stmt swc4j ast type.
     */
    ThrowStmt(103, Swc4jAstThrowStmt.class),
    /**
     * Tpl swc4j ast type.
     */
    Tpl(104, Swc4jAstTpl.class),
    /**
     * Tpl element swc4j ast type.
     */
    TplElement(105, Swc4jAstTplElement.class),
    /**
     * Try stmt swc4j ast type.
     */
    TryStmt(106, Swc4jAstTryStmt.class),
    /**
     * Ts array type swc4j ast type.
     */
    TsArrayType(107, Swc4jAstTsArrayType.class),
    /**
     * Ts as expr swc4j ast type.
     */
    TsAsExpr(108, Swc4jAstTsAsExpr.class),
    /**
     * Ts call signature decl swc4j ast type.
     */
    TsCallSignatureDecl(109, Swc4jAstTsCallSignatureDecl.class),
    /**
     * Ts conditional type swc4j ast type.
     */
    TsConditionalType(110, Swc4jAstTsConditionalType.class),
    /**
     * Ts const assertion swc4j ast type.
     */
    TsConstAssertion(111, Swc4jAstTsConstAssertion.class),
    /**
     * Ts constructor type swc4j ast type.
     */
    TsConstructorType(112, Swc4jAstTsConstructorType.class),
    /**
     * Ts construct signature decl swc4j ast type.
     */
    TsConstructSignatureDecl(113, Swc4jAstTsConstructSignatureDecl.class),
    /**
     * Ts enum decl swc4j ast type.
     */
    TsEnumDecl(114, Swc4jAstTsEnumDecl.class),
    /**
     * Ts enum member swc4j ast type.
     */
    TsEnumMember(115, Swc4jAstTsEnumMember.class),
    /**
     * Ts export assignment swc4j ast type.
     */
    TsExportAssignment(116, Swc4jAstTsExportAssignment.class),
    /**
     * Ts expr with type args swc4j ast type.
     */
    TsExprWithTypeArgs(117, Swc4jAstTsExprWithTypeArgs.class),
    /**
     * Ts external module ref swc4j ast type.
     */
    TsExternalModuleRef(118, Swc4jAstTsExternalModuleRef.class),
    /**
     * Ts fn type swc4j ast type.
     */
    TsFnType(119, Swc4jAstTsFnType.class),
    /**
     * Ts getter signature swc4j ast type.
     */
    TsGetterSignature(120, Swc4jAstTsGetterSignature.class),
    /**
     * Ts import call options swc4j ast type.
     */
    TsImportCallOptions(121, Swc4jAstTsImportCallOptions.class),
    /**
     * Ts import equals decl swc4j ast type.
     */
    TsImportEqualsDecl(122, Swc4jAstTsImportEqualsDecl.class),
    /**
     * Ts import type swc4j ast type.
     */
    TsImportType(123, Swc4jAstTsImportType.class),
    /**
     * Ts indexed access type swc4j ast type.
     */
    TsIndexedAccessType(124, Swc4jAstTsIndexedAccessType.class),
    /**
     * Ts index signature swc4j ast type.
     */
    TsIndexSignature(125, Swc4jAstTsIndexSignature.class),
    /**
     * Ts infer type swc4j ast type.
     */
    TsInferType(126, Swc4jAstTsInferType.class),
    /**
     * Ts instantiation swc4j ast type.
     */
    TsInstantiation(127, Swc4jAstTsInstantiation.class),
    /**
     * Ts interface body swc4j ast type.
     */
    TsInterfaceBody(128, Swc4jAstTsInterfaceBody.class),
    /**
     * Ts interface decl swc4j ast type.
     */
    TsInterfaceDecl(129, Swc4jAstTsInterfaceDecl.class),
    /**
     * Ts intersection type swc4j ast type.
     */
    TsIntersectionType(130, Swc4jAstTsIntersectionType.class),
    /**
     * Ts keyword type swc4j ast type.
     */
    TsKeywordType(131, Swc4jAstTsKeywordType.class),
    /**
     * Ts lit type swc4j ast type.
     */
    TsLitType(132, Swc4jAstTsLitType.class),
    /**
     * Ts mapped type swc4j ast type.
     */
    TsMappedType(133, Swc4jAstTsMappedType.class),
    /**
     * Ts method signature swc4j ast type.
     */
    TsMethodSignature(134, Swc4jAstTsMethodSignature.class),
    /**
     * Ts module block swc4j ast type.
     */
    TsModuleBlock(135, Swc4jAstTsModuleBlock.class),
    /**
     * Ts module decl swc4j ast type.
     */
    TsModuleDecl(136, Swc4jAstTsModuleDecl.class),
    /**
     * Ts namespace decl swc4j ast type.
     */
    TsNamespaceDecl(137, Swc4jAstTsNamespaceDecl.class),
    /**
     * Ts namespace export decl swc4j ast type.
     */
    TsNamespaceExportDecl(138, Swc4jAstTsNamespaceExportDecl.class),
    /**
     * Ts non null expr swc4j ast type.
     */
    TsNonNullExpr(139, Swc4jAstTsNonNullExpr.class),
    /**
     * Ts optional type swc4j ast type.
     */
    TsOptionalType(140, Swc4jAstTsOptionalType.class),
    /**
     * Ts param prop swc4j ast type.
     */
    TsParamProp(141, Swc4jAstTsParamProp.class),
    /**
     * Ts parenthesized type swc4j ast type.
     */
    TsParenthesizedType(142, Swc4jAstTsParenthesizedType.class),
    /**
     * Ts property signature swc4j ast type.
     */
    TsPropertySignature(143, Swc4jAstTsPropertySignature.class),
    /**
     * Ts qualified name swc4j ast type.
     */
    TsQualifiedName(144, Swc4jAstTsQualifiedName.class),
    /**
     * Ts rest type swc4j ast type.
     */
    TsRestType(145, Swc4jAstTsRestType.class),
    /**
     * Ts satisfies expr swc4j ast type.
     */
    TsSatisfiesExpr(146, Swc4jAstTsSatisfiesExpr.class),
    /**
     * Ts setter signature swc4j ast type.
     */
    TsSetterSignature(147, Swc4jAstTsSetterSignature.class),
    /**
     * Ts this type swc4j ast type.
     */
    TsThisType(148, Swc4jAstTsThisType.class),
    /**
     * Ts tpl lit type swc4j ast type.
     */
    TsTplLitType(149, Swc4jAstTsTplLitType.class),
    /**
     * Ts tuple element swc4j ast type.
     */
    TsTupleElement(150, Swc4jAstTsTupleElement.class),
    /**
     * Ts tuple type swc4j ast type.
     */
    TsTupleType(151, Swc4jAstTsTupleType.class),
    /**
     * Ts type alias decl swc4j ast type.
     */
    TsTypeAliasDecl(152, Swc4jAstTsTypeAliasDecl.class),
    /**
     * Ts type ann swc4j ast type.
     */
    TsTypeAnn(153, Swc4jAstTsTypeAnn.class),
    /**
     * Ts type assertion swc4j ast type.
     */
    TsTypeAssertion(154, Swc4jAstTsTypeAssertion.class),
    /**
     * Ts type lit swc4j ast type.
     */
    TsTypeLit(155, Swc4jAstTsTypeLit.class),
    /**
     * Ts type operator swc4j ast type.
     */
    TsTypeOperator(156, Swc4jAstTsTypeOperator.class),
    /**
     * Ts type param swc4j ast type.
     */
    TsTypeParam(157, Swc4jAstTsTypeParam.class),
    /**
     * Ts type param decl swc4j ast type.
     */
    TsTypeParamDecl(158, Swc4jAstTsTypeParamDecl.class),
    /**
     * Ts type param instantiation swc4j ast type.
     */
    TsTypeParamInstantiation(159, Swc4jAstTsTypeParamInstantiation.class),
    /**
     * Ts type predicate swc4j ast type.
     */
    TsTypePredicate(160, Swc4jAstTsTypePredicate.class),
    /**
     * Ts type query swc4j ast type.
     */
    TsTypeQuery(161, Swc4jAstTsTypeQuery.class),
    /**
     * Ts type ref swc4j ast type.
     */
    TsTypeRef(162, Swc4jAstTsTypeRef.class),
    /**
     * Ts union type swc4j ast type.
     */
    TsUnionType(163, Swc4jAstTsUnionType.class),
    /**
     * Unary expr swc4j ast type.
     */
    UnaryExpr(164, Swc4jAstUnaryExpr.class),
    /**
     * Update expr swc4j ast type.
     */
    UpdateExpr(165, Swc4jAstUpdateExpr.class),
    /**
     * Using decl swc4j ast type.
     */
    UsingDecl(166, Swc4jAstUsingDecl.class),
    /**
     * Var decl swc4j ast type.
     */
    VarDecl(167, Swc4jAstVarDecl.class),
    /**
     * Var declarator swc4j ast type.
     */
    VarDeclarator(168, Swc4jAstVarDeclarator.class),
    /**
     * While stmt swc4j ast type.
     */
    WhileStmt(169, Swc4jAstWhileStmt.class),
    /**
     * With stmt swc4j ast type.
     */
    WithStmt(170, Swc4jAstWithStmt.class),
    /**
     * Yield expr swc4j ast type.
     */
    YieldExpr(171, Swc4jAstYieldExpr.class),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstType[] TYPES = new Swc4jAstType[LENGTH];
    private static final Map<Class<? extends ISwc4jAst>, String> TYPE_NAME_MAP = new HashMap<>();

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
        // Enums
        TYPE_NAME_MAP.put(ISwc4jAstAssignTarget.class, "Assign Target");
        TYPE_NAME_MAP.put(ISwc4jAstAssignTargetPat.class, "Assign Target Pattern");
        TYPE_NAME_MAP.put(ISwc4jAstBlockStmtOrExpr.class, "Block Statement Or Expression");
        TYPE_NAME_MAP.put(ISwc4jAstCallee.class, "Callee");
        TYPE_NAME_MAP.put(ISwc4jAstClassMember.class, "Class Member");
        TYPE_NAME_MAP.put(ISwc4jAstDecl.class, "Declaration");
        TYPE_NAME_MAP.put(ISwc4jAstDefaultDecl.class, "Default Declaration");
        TYPE_NAME_MAP.put(ISwc4jAstExportSpecifier.class, "Export Specifier");
        TYPE_NAME_MAP.put(ISwc4jAstExpr.class, "Expression");
        TYPE_NAME_MAP.put(ISwc4jAstForHead.class, "For Head");
        TYPE_NAME_MAP.put(ISwc4jAstImportSpecifier.class, "Import Specifier");
        TYPE_NAME_MAP.put(ISwc4jAstJsxAttrName.class, "JSX Attribute Name");
        TYPE_NAME_MAP.put(ISwc4jAstJsxAttrOrSpread.class, "JSX Attribute Or Spread");
        TYPE_NAME_MAP.put(ISwc4jAstJsxAttrValue.class, "JSX Attribute Value");
        TYPE_NAME_MAP.put(ISwc4jAstJsxElementChild.class, "JSX Element Child");
        TYPE_NAME_MAP.put(ISwc4jAstJsxElementName.class, "JSX Element Name");
        TYPE_NAME_MAP.put(ISwc4jAstJsxExpr.class, "JSX Expression");
        TYPE_NAME_MAP.put(ISwc4jAstJsxObject.class, "JSX Object");
        TYPE_NAME_MAP.put(ISwc4jAstKey.class, "Key");
        TYPE_NAME_MAP.put(ISwc4jAstLit.class, "Literal");
        TYPE_NAME_MAP.put(ISwc4jAstMemberProp.class, "Member Property");
        TYPE_NAME_MAP.put(ISwc4jAstModuleDecl.class, "Module Declaration");
        TYPE_NAME_MAP.put(ISwc4jAstModuleExportName.class, "Module Export Name");
        TYPE_NAME_MAP.put(ISwc4jAstModuleItem.class, "Module Item");
        TYPE_NAME_MAP.put(ISwc4jAstObjectPatProp.class, "Object Pattern Property");
        TYPE_NAME_MAP.put(ISwc4jAstOptChainBase.class, "Optional Chain Base");
        TYPE_NAME_MAP.put(ISwc4jAstParamOrTsParamProp.class, "Parameter Or TS Parameter Property");
        TYPE_NAME_MAP.put(ISwc4jAstPat.class, "Pattern");
        TYPE_NAME_MAP.put(ISwc4jAstProgram.class, "Program");
        TYPE_NAME_MAP.put(ISwc4jAstProp.class, "Property");
        TYPE_NAME_MAP.put(ISwc4jAstPropName.class, "Property Name");
        TYPE_NAME_MAP.put(ISwc4jAstPropOrSpread.class, "Property Or Spread");
        TYPE_NAME_MAP.put(ISwc4jAstSimpleAssignTarget.class, "Simple Assign Target");
        TYPE_NAME_MAP.put(ISwc4jAstStmt.class, "Statement");
        TYPE_NAME_MAP.put(ISwc4jAstSuperProp.class, "Super Property");
        TYPE_NAME_MAP.put(ISwc4jAstTsEntityName.class, "TS Entity Name");
        TYPE_NAME_MAP.put(ISwc4jAstTsEnumMemberId.class, "TS Enum Member Id");
        TYPE_NAME_MAP.put(ISwc4jAstTsFnOrConstructorType.class, "TS Function Or Constructor Type");
        TYPE_NAME_MAP.put(ISwc4jAstTsFnParam.class, "TS Function Parameter");
        TYPE_NAME_MAP.put(ISwc4jAstTsLit.class, "TS Literal");
        TYPE_NAME_MAP.put(ISwc4jAstTsModuleName.class, "TS Module Name");
        TYPE_NAME_MAP.put(ISwc4jAstTsModuleRef.class, "TS Module Reference");
        TYPE_NAME_MAP.put(ISwc4jAstTsNamespaceBody.class, "TS Namespace Body");
        TYPE_NAME_MAP.put(ISwc4jAstTsParamPropParam.class, "TS Parameter Property Parameter");
        TYPE_NAME_MAP.put(ISwc4jAstTsThisTypeOrIdent.class, "TS This Type Or Ident");
        TYPE_NAME_MAP.put(ISwc4jAstTsType.class, "TS Type");
        TYPE_NAME_MAP.put(ISwc4jAstTsTypeElement.class, "TS Type Element");
        TYPE_NAME_MAP.put(ISwc4jAstTsTypeQueryExpr.class, "TS Type Query Expression");
        TYPE_NAME_MAP.put(ISwc4jAstTsUnionOrIntersectionType.class, "TS Union Or Intersection Type");
        TYPE_NAME_MAP.put(ISwc4jAstVarDeclOrExpr.class, "Var Declaration Or Expression");
        // Structs
        TYPE_NAME_MAP.put(Swc4jAstArrayLit.class, "Array Literal");
        TYPE_NAME_MAP.put(Swc4jAstArrayPat.class, "Array Pattern");
        TYPE_NAME_MAP.put(Swc4jAstArrowExpr.class, "Arrow Expression");
        TYPE_NAME_MAP.put(Swc4jAstAssignExpr.class, "Assign Expression");
        TYPE_NAME_MAP.put(Swc4jAstAssignPat.class, "Assign Pattern");
        TYPE_NAME_MAP.put(Swc4jAstAssignPatProp.class, "Assign Pattern Property");
        TYPE_NAME_MAP.put(Swc4jAstAssignProp.class, "Assign Property");
        TYPE_NAME_MAP.put(Swc4jAstAutoAccessor.class, "Auto Accessor");
        TYPE_NAME_MAP.put(Swc4jAstAwaitExpr.class, "Await Expression");
        TYPE_NAME_MAP.put(Swc4jAstBigInt.class, "Big Integer");
        TYPE_NAME_MAP.put(Swc4jAstBindingIdent.class, "Binding Identifier");
        TYPE_NAME_MAP.put(Swc4jAstBinExpr.class, "Bin Expression");
        TYPE_NAME_MAP.put(Swc4jAstBlockStmt.class, "Block Statement");
        TYPE_NAME_MAP.put(Swc4jAstBool.class, "Boolean");
        TYPE_NAME_MAP.put(Swc4jAstBreakStmt.class, "Break Statement");
        TYPE_NAME_MAP.put(Swc4jAstCallExpr.class, "Call Expression");
        TYPE_NAME_MAP.put(Swc4jAstCatchClause.class, "Catch Clause");
        TYPE_NAME_MAP.put(Swc4jAstClass.class, "Class");
        TYPE_NAME_MAP.put(Swc4jAstClassDecl.class, "Class Declaration");
        TYPE_NAME_MAP.put(Swc4jAstClassExpr.class, "Class Expression");
        TYPE_NAME_MAP.put(Swc4jAstClassMethod.class, "Class Method");
        TYPE_NAME_MAP.put(Swc4jAstClassProp.class, "Class Property");
        TYPE_NAME_MAP.put(Swc4jAstComputedPropName.class, "Computed Property Name");
        TYPE_NAME_MAP.put(Swc4jAstCondExpr.class, "Cond Expression");
        TYPE_NAME_MAP.put(Swc4jAstConstructor.class, "Constructor");
        TYPE_NAME_MAP.put(Swc4jAstContinueStmt.class, "Continue Statement");
        TYPE_NAME_MAP.put(Swc4jAstDebuggerStmt.class, "Debugger Statement");
        TYPE_NAME_MAP.put(Swc4jAstDecorator.class, "Decorator");
        TYPE_NAME_MAP.put(Swc4jAstDoWhileStmt.class, "Do-while Statement");
        TYPE_NAME_MAP.put(Swc4jAstEmptyStmt.class, "Empty Statement");
        TYPE_NAME_MAP.put(Swc4jAstExportAll.class, "Export All");
        TYPE_NAME_MAP.put(Swc4jAstExportDecl.class, "Export Declaration");
        TYPE_NAME_MAP.put(Swc4jAstExportDefaultDecl.class, "Export Default Declaration");
        TYPE_NAME_MAP.put(Swc4jAstExportDefaultExpr.class, "Export Default Expression");
        TYPE_NAME_MAP.put(Swc4jAstExportDefaultSpecifier.class, "Export Default Specifier");
        TYPE_NAME_MAP.put(Swc4jAstExportNamedSpecifier.class, "Export Named Specifier");
        TYPE_NAME_MAP.put(Swc4jAstExportNamespaceSpecifier.class, "Export Namespace Specifier");
        TYPE_NAME_MAP.put(Swc4jAstExprOrSpread.class, "Expression Or Spread");
        TYPE_NAME_MAP.put(Swc4jAstExprStmt.class, "Expression Statement");
        TYPE_NAME_MAP.put(Swc4jAstFnDecl.class, "Function Declaration");
        TYPE_NAME_MAP.put(Swc4jAstFnExpr.class, "Function Expression");
        TYPE_NAME_MAP.put(Swc4jAstForInStmt.class, "For-in Statement");
        TYPE_NAME_MAP.put(Swc4jAstForOfStmt.class, "For-of Statement");
        TYPE_NAME_MAP.put(Swc4jAstForStmt.class, "For Statement");
        TYPE_NAME_MAP.put(Swc4jAstFunction.class, "Function");
        TYPE_NAME_MAP.put(Swc4jAstGetterProp.class, "Getter Property");
        TYPE_NAME_MAP.put(Swc4jAstIdent.class, "Identifier");
        TYPE_NAME_MAP.put(Swc4jAstIdentName.class, "Identifier Name");
        TYPE_NAME_MAP.put(Swc4jAstIfStmt.class, "If Statement");
        TYPE_NAME_MAP.put(Swc4jAstImport.class, "Import");
        TYPE_NAME_MAP.put(Swc4jAstImportDecl.class, "Import Declaration");
        TYPE_NAME_MAP.put(Swc4jAstImportDefaultSpecifier.class, "Import Default Specifier");
        TYPE_NAME_MAP.put(Swc4jAstImportNamedSpecifier.class, "Import Named Specifier");
        TYPE_NAME_MAP.put(Swc4jAstImportStarAsSpecifier.class, "Import Star As Specifier");
        TYPE_NAME_MAP.put(Swc4jAstInvalid.class, "Invalid");
        TYPE_NAME_MAP.put(Swc4jAstJsxAttr.class, "JSX Attribute");
        TYPE_NAME_MAP.put(Swc4jAstJsxClosingElement.class, "JSX Closing Element");
        TYPE_NAME_MAP.put(Swc4jAstJsxClosingFragment.class, "JSX Closing Fragment");
        TYPE_NAME_MAP.put(Swc4jAstJsxElement.class, "JSX Element");
        TYPE_NAME_MAP.put(Swc4jAstJsxEmptyExpr.class, "JSX Empty Expression");
        TYPE_NAME_MAP.put(Swc4jAstJsxExprContainer.class, "JSX Expression Container");
        TYPE_NAME_MAP.put(Swc4jAstJsxFragment.class, "JSX Fragment");
        TYPE_NAME_MAP.put(Swc4jAstJsxMemberExpr.class, "JSX Member Expression");
        TYPE_NAME_MAP.put(Swc4jAstJsxNamespacedName.class, "JSX Namespaced Name");
        TYPE_NAME_MAP.put(Swc4jAstJsxOpeningElement.class, "JSX Opening Element");
        TYPE_NAME_MAP.put(Swc4jAstJsxOpeningFragment.class, "JSX Opening Fragment");
        TYPE_NAME_MAP.put(Swc4jAstJsxSpreadChild.class, "JSX Spread Child");
        TYPE_NAME_MAP.put(Swc4jAstJsxText.class, "JSX Text");
        TYPE_NAME_MAP.put(Swc4jAstKeyValuePatProp.class, "Key Value Pattern Property");
        TYPE_NAME_MAP.put(Swc4jAstKeyValueProp.class, "Key Value Property");
        TYPE_NAME_MAP.put(Swc4jAstLabeledStmt.class, "Labeled Statement");
        TYPE_NAME_MAP.put(Swc4jAstMemberExpr.class, "Member Expression");
        TYPE_NAME_MAP.put(Swc4jAstMetaPropExpr.class, "Meta Property Expression");
        TYPE_NAME_MAP.put(Swc4jAstMethodProp.class, "Method Property");
        TYPE_NAME_MAP.put(Swc4jAstModule.class, "Module");
        TYPE_NAME_MAP.put(Swc4jAstNamedExport.class, "Named Export");
        TYPE_NAME_MAP.put(Swc4jAstNewExpr.class, "New Expression");
        TYPE_NAME_MAP.put(Swc4jAstNull.class, "Null");
        TYPE_NAME_MAP.put(Swc4jAstNumber.class, "Number");
        TYPE_NAME_MAP.put(Swc4jAstObjectLit.class, "Object Literal");
        TYPE_NAME_MAP.put(Swc4jAstObjectPat.class, "Object Pattern");
        TYPE_NAME_MAP.put(Swc4jAstOptCall.class, "Optional Call");
        TYPE_NAME_MAP.put(Swc4jAstOptChainExpr.class, "Optional Chain Expression");
        TYPE_NAME_MAP.put(Swc4jAstParam.class, "Parameter");
        TYPE_NAME_MAP.put(Swc4jAstParenExpr.class, "Paren Expression");
        TYPE_NAME_MAP.put(Swc4jAstPrivateMethod.class, "Private Method");
        TYPE_NAME_MAP.put(Swc4jAstPrivateName.class, "Private Name");
        TYPE_NAME_MAP.put(Swc4jAstPrivateProp.class, "Private Property");
        TYPE_NAME_MAP.put(Swc4jAstRegex.class, "Regex");
        TYPE_NAME_MAP.put(Swc4jAstRestPat.class, "Rest Pattern");
        TYPE_NAME_MAP.put(Swc4jAstReturnStmt.class, "Return Statement");
        TYPE_NAME_MAP.put(Swc4jAstScript.class, "Script");
        TYPE_NAME_MAP.put(Swc4jAstSeqExpr.class, "Seq Expression");
        TYPE_NAME_MAP.put(Swc4jAstSetterProp.class, "Setter Property");
        TYPE_NAME_MAP.put(Swc4jAstSpreadElement.class, "Spread Element");
        TYPE_NAME_MAP.put(Swc4jAstStaticBlock.class, "Static Block");
        TYPE_NAME_MAP.put(Swc4jAstStr.class, "String");
        TYPE_NAME_MAP.put(Swc4jAstSuper.class, "Super");
        TYPE_NAME_MAP.put(Swc4jAstSuperPropExpr.class, "Super Property Expression");
        TYPE_NAME_MAP.put(Swc4jAstSwitchCase.class, "Switch Case");
        TYPE_NAME_MAP.put(Swc4jAstSwitchStmt.class, "Switch Statement");
        TYPE_NAME_MAP.put(Swc4jAstTaggedTpl.class, "Tagged Template");
        TYPE_NAME_MAP.put(Swc4jAstThisExpr.class, "This Expression");
        TYPE_NAME_MAP.put(Swc4jAstThrowStmt.class, "Throw Statement");
        TYPE_NAME_MAP.put(Swc4jAstTpl.class, "Template");
        TYPE_NAME_MAP.put(Swc4jAstTplElement.class, "Template Element");
        TYPE_NAME_MAP.put(Swc4jAstTryStmt.class, "Try Statement");
        TYPE_NAME_MAP.put(Swc4jAstTsArrayType.class, "TS Array Type");
        TYPE_NAME_MAP.put(Swc4jAstTsAsExpr.class, "TS As Expression");
        TYPE_NAME_MAP.put(Swc4jAstTsCallSignatureDecl.class, "TS Call Signature Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsConditionalType.class, "TS Conditional Type");
        TYPE_NAME_MAP.put(Swc4jAstTsConstAssertion.class, "TS Const Assertion");
        TYPE_NAME_MAP.put(Swc4jAstTsConstructorType.class, "TS Constructor Type");
        TYPE_NAME_MAP.put(Swc4jAstTsConstructSignatureDecl.class, "TS Construct Signature Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsEnumDecl.class, "TS Enum Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsEnumMember.class, "TS Enum Member");
        TYPE_NAME_MAP.put(Swc4jAstTsExportAssignment.class, "TS Export Assignment");
        TYPE_NAME_MAP.put(Swc4jAstTsExprWithTypeArgs.class, "TS Expression With Type Args");
        TYPE_NAME_MAP.put(Swc4jAstTsExternalModuleRef.class, "TS External Module Reference");
        TYPE_NAME_MAP.put(Swc4jAstTsFnType.class, "TS Function Type");
        TYPE_NAME_MAP.put(Swc4jAstTsGetterSignature.class, "TS Getter Signature");
        TYPE_NAME_MAP.put(Swc4jAstTsImportCallOptions.class, "TS Import Call Options");
        TYPE_NAME_MAP.put(Swc4jAstTsImportEqualsDecl.class, "TS Import Equals Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsImportType.class, "TS Import Type");
        TYPE_NAME_MAP.put(Swc4jAstTsIndexedAccessType.class, "TS Indexed Access Type");
        TYPE_NAME_MAP.put(Swc4jAstTsIndexSignature.class, "TS Index Signature");
        TYPE_NAME_MAP.put(Swc4jAstTsInferType.class, "TS Infer Type");
        TYPE_NAME_MAP.put(Swc4jAstTsInstantiation.class, "TS Instantiation");
        TYPE_NAME_MAP.put(Swc4jAstTsInterfaceBody.class, "TS Interface Body");
        TYPE_NAME_MAP.put(Swc4jAstTsInterfaceDecl.class, "TS Interface Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsIntersectionType.class, "TS Intersection Type");
        TYPE_NAME_MAP.put(Swc4jAstTsKeywordType.class, "TS Keyword Type");
        TYPE_NAME_MAP.put(Swc4jAstTsLitType.class, "TS Literal Type");
        TYPE_NAME_MAP.put(Swc4jAstTsMappedType.class, "TS Mapped Type");
        TYPE_NAME_MAP.put(Swc4jAstTsMethodSignature.class, "TS Method Signature");
        TYPE_NAME_MAP.put(Swc4jAstTsModuleBlock.class, "TS Module Block");
        TYPE_NAME_MAP.put(Swc4jAstTsModuleDecl.class, "TS Module Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsNamespaceDecl.class, "TS Namespace Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsNamespaceExportDecl.class, "TS Namespace Export Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsNonNullExpr.class, "TS Non Null Expression");
        TYPE_NAME_MAP.put(Swc4jAstTsOptionalType.class, "TS Optional Type");
        TYPE_NAME_MAP.put(Swc4jAstTsParamProp.class, "TS Parameter Property");
        TYPE_NAME_MAP.put(Swc4jAstTsParenthesizedType.class, "TS Parenthesized Type");
        TYPE_NAME_MAP.put(Swc4jAstTsPropertySignature.class, "TS Property Signature");
        TYPE_NAME_MAP.put(Swc4jAstTsQualifiedName.class, "TS Qualified Name");
        TYPE_NAME_MAP.put(Swc4jAstTsRestType.class, "TS Rest Type");
        TYPE_NAME_MAP.put(Swc4jAstTsSatisfiesExpr.class, "TS Satisfies Expression");
        TYPE_NAME_MAP.put(Swc4jAstTsSetterSignature.class, "TS Setter Signature");
        TYPE_NAME_MAP.put(Swc4jAstTsThisType.class, "TS This Type");
        TYPE_NAME_MAP.put(Swc4jAstTsTplLitType.class, "TS Template Literal Type");
        TYPE_NAME_MAP.put(Swc4jAstTsTupleElement.class, "TS Tuple Element");
        TYPE_NAME_MAP.put(Swc4jAstTsTupleType.class, "TS Tuple Type");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeAliasDecl.class, "TS Type Alias Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeAnn.class, "TS Type Annotation");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeAssertion.class, "TS Type Assertion");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeLit.class, "TS Type Literal");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeOperator.class, "TS Type Operator");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeParam.class, "TS Type Parameter");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeParamDecl.class, "TS Type Parameter Declaration");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeParamInstantiation.class, "TS Type Parameter Instantiation");
        TYPE_NAME_MAP.put(Swc4jAstTsTypePredicate.class, "TS Type Predicate");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeQuery.class, "TS Type Query");
        TYPE_NAME_MAP.put(Swc4jAstTsTypeRef.class, "TS Type Reference");
        TYPE_NAME_MAP.put(Swc4jAstTsUnionType.class, "TS Union Type");
        TYPE_NAME_MAP.put(Swc4jAstUnaryExpr.class, "Unary Expression");
        TYPE_NAME_MAP.put(Swc4jAstUpdateExpr.class, "Update Expression");
        TYPE_NAME_MAP.put(Swc4jAstUsingDecl.class, "Using Declaration");
        TYPE_NAME_MAP.put(Swc4jAstVarDecl.class, "Var Declaration");
        TYPE_NAME_MAP.put(Swc4jAstVarDeclarator.class, "Var Declarator");
        TYPE_NAME_MAP.put(Swc4jAstWhileStmt.class, "While Statement");
        TYPE_NAME_MAP.put(Swc4jAstWithStmt.class, "With Statement");
        TYPE_NAME_MAP.put(Swc4jAstYieldExpr.class, "Yield Expression");
    }

    private final Class<? extends ISwc4jAst> astClass;
    private final int id;

    Swc4jAstType(int id, Class<? extends ISwc4jAst> astClass) {
        this.astClass = astClass;
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @param astClass the ast class
     * @return the name
     */
    public static String getName(Class<? extends ISwc4jAst> astClass) {
        return TYPE_NAME_MAP.get(astClass);
    }

    /**
     * Parse swc4j ast type.
     *
     * @param id the id
     * @return the swc4j ast type
     */
    public static Swc4jAstType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Invalid;
    }

    /**
     * Gets ast class.
     *
     * @return the ast class
     */
    public Class<? extends ISwc4jAst> getAstClass() {
        return astClass;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * Is array lit boolean.
     *
     * @return the boolean
     */
    public boolean isArrayLit() {
        return this == ArrayLit;
    }

    /**
     * Is bool boolean.
     *
     * @return the boolean
     */
    public boolean isBool() {
        return this == Bool;
    }

    /**
     * Is call expr boolean.
     *
     * @return the boolean
     */
    public boolean isCallExpr() {
        return this == CallExpr;
    }

    /**
     * Is ident boolean.
     *
     * @return the boolean
     */
    public boolean isIdent() {
        return this == Ident;
    }

    /**
     * Is member expr boolean.
     *
     * @return the boolean
     */
    public boolean isMemberExpr() {
        return this == MemberExpr;
    }

    /**
     * Is number boolean.
     *
     * @return the boolean
     */
    public boolean isNumber() {
        return this == Number;
    }

    /**
     * Is primitive boolean.
     *
     * @return the boolean
     */
    public boolean isPrimitive() {
        return this == Str || this == Number || this == Bool || this == Null || this == BigInt;
    }

    /**
     * Is regex boolean.
     *
     * @return the boolean
     */
    public boolean isRegex() {
        return this == Regex;
    }

    /**
     * Is str boolean.
     *
     * @return the boolean
     */
    public boolean isStr() {
        return this == Str;
    }
}
