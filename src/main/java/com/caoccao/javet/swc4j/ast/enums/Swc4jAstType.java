/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

    public static String getName(Class<? extends ISwc4jAst> astClass) {
        return TYPE_NAME_MAP.get(astClass);
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

    public boolean isArrayLit() {
        return this == ArrayLit;
    }

    public boolean isBool() {
        return this == Bool;
    }

    public boolean isCallExpr() {
        return this == CallExpr;
    }

    public boolean isIdent() {
        return this == Ident;
    }

    public boolean isMemberExpr() {
        return this == MemberExpr;
    }

    public boolean isNumber() {
        return this == Number;
    }

    public boolean isPrimitive() {
        return this == Str || this == Number || this == Bool || this == Null || this == BigInt;
    }

    public boolean isStr() {
        return this == Str;
    }
}
