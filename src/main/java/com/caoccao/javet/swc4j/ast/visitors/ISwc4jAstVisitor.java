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

package com.caoccao.javet.swc4j.ast.visitors;

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.miscs.*;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.*;

public interface ISwc4jAstVisitor {
    Swc4jAstVisitorResponse visitArrayLit(Swc4jAstArrayLit node);

    Swc4jAstVisitorResponse visitArrayPat(Swc4jAstArrayPat node);

    Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node);

    Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node);

    Swc4jAstVisitorResponse visitAssignPat(Swc4jAstAssignPat node);

    Swc4jAstVisitorResponse visitAssignPatProp(Swc4jAstAssignPatProp node);

    Swc4jAstVisitorResponse visitAssignProp(Swc4jAstAssignProp node);

    Swc4jAstVisitorResponse visitAutoAccessor(Swc4jAstAutoAccessor node);

    Swc4jAstVisitorResponse visitAwaitExpr(Swc4jAstAwaitExpr node);

    Swc4jAstVisitorResponse visitBigInt(Swc4jAstBigInt node);

    Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node);

    Swc4jAstVisitorResponse visitBindingIdent(Swc4jAstBindingIdent node);

    Swc4jAstVisitorResponse visitBlockStmt(Swc4jAstBlockStmt node);

    Swc4jAstVisitorResponse visitBool(Swc4jAstBool node);

    Swc4jAstVisitorResponse visitBreakStmt(Swc4jAstBreakStmt node);

    Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node);

    Swc4jAstVisitorResponse visitCatchClause(Swc4jAstCatchClause node);

    Swc4jAstVisitorResponse visitClass(Swc4jAstClass node);

    Swc4jAstVisitorResponse visitClassDecl(Swc4jAstClassDecl node);

    Swc4jAstVisitorResponse visitClassExpr(Swc4jAstClassExpr node);

    Swc4jAstVisitorResponse visitClassMethod(Swc4jAstClassMethod node);

    Swc4jAstVisitorResponse visitClassProp(Swc4jAstClassProp node);

    Swc4jAstVisitorResponse visitComputedPropName(Swc4jAstComputedPropName node);

    Swc4jAstVisitorResponse visitCondExpr(Swc4jAstCondExpr node);

    Swc4jAstVisitorResponse visitConstructor(Swc4jAstConstructor node);

    Swc4jAstVisitorResponse visitContinueStmt(Swc4jAstContinueStmt node);

    Swc4jAstVisitorResponse visitDebuggerStmt(Swc4jAstDebuggerStmt node);

    Swc4jAstVisitorResponse visitDecorator(Swc4jAstDecorator node);

    Swc4jAstVisitorResponse visitDoWhileStmt(Swc4jAstDoWhileStmt node);

    Swc4jAstVisitorResponse visitEmptyStmt(Swc4jAstEmptyStmt node);

    Swc4jAstVisitorResponse visitExportAll(Swc4jAstExportAll node);

    Swc4jAstVisitorResponse visitExportDecl(Swc4jAstExportDecl node);

    Swc4jAstVisitorResponse visitExportDefaultDecl(Swc4jAstExportDefaultDecl node);

    Swc4jAstVisitorResponse visitExportDefaultExpr(Swc4jAstExportDefaultExpr node);

    Swc4jAstVisitorResponse visitExportDefaultSpecifier(Swc4jAstExportDefaultSpecifier node);

    Swc4jAstVisitorResponse visitExportNamedSpecifier(Swc4jAstExportNamedSpecifier node);

    Swc4jAstVisitorResponse visitExportNamespaceSpecifier(Swc4jAstExportNamespaceSpecifier node);

    Swc4jAstVisitorResponse visitExprOrSpread(Swc4jAstExprOrSpread node);

    Swc4jAstVisitorResponse visitExprStmt(Swc4jAstExprStmt node);

    Swc4jAstVisitorResponse visitFnDecl(Swc4jAstFnDecl node);

    Swc4jAstVisitorResponse visitFnExpr(Swc4jAstFnExpr node);

    Swc4jAstVisitorResponse visitForInStmt(Swc4jAstForInStmt node);

    Swc4jAstVisitorResponse visitForOfStmt(Swc4jAstForOfStmt node);

    Swc4jAstVisitorResponse visitForStmt(Swc4jAstForStmt node);

    Swc4jAstVisitorResponse visitFunction(Swc4jAstFunction node);

    Swc4jAstVisitorResponse visitGetterProp(Swc4jAstGetterProp node);

    Swc4jAstVisitorResponse visitIdent(Swc4jAstIdent node);

    Swc4jAstVisitorResponse visitIdentName(Swc4jAstIdentName node);

    Swc4jAstVisitorResponse visitIfStmt(Swc4jAstIfStmt node);

    Swc4jAstVisitorResponse visitImport(Swc4jAstImport node);

    Swc4jAstVisitorResponse visitImportDecl(Swc4jAstImportDecl node);

    Swc4jAstVisitorResponse visitImportDefaultSpecifier(Swc4jAstImportDefaultSpecifier node);

    Swc4jAstVisitorResponse visitImportNamedSpecifier(Swc4jAstImportNamedSpecifier node);

    Swc4jAstVisitorResponse visitImportStarAsSpecifier(Swc4jAstImportStarAsSpecifier node);

    Swc4jAstVisitorResponse visitInvalid(Swc4jAstInvalid node);

    Swc4jAstVisitorResponse visitJsxAttr(Swc4jAstJsxAttr node);

    Swc4jAstVisitorResponse visitJsxClosingElement(Swc4jAstJsxClosingElement node);

    Swc4jAstVisitorResponse visitJsxClosingFragment(Swc4jAstJsxClosingFragment node);

    Swc4jAstVisitorResponse visitJsxElement(Swc4jAstJsxElement node);

    Swc4jAstVisitorResponse visitJsxEmptyExpr(Swc4jAstJsxEmptyExpr node);

    Swc4jAstVisitorResponse visitJsxExprContainer(Swc4jAstJsxExprContainer node);

    Swc4jAstVisitorResponse visitJsxFragment(Swc4jAstJsxFragment node);

    Swc4jAstVisitorResponse visitJsxMemberExpr(Swc4jAstJsxMemberExpr node);

    Swc4jAstVisitorResponse visitJsxNamespacedName(Swc4jAstJsxNamespacedName node);

    Swc4jAstVisitorResponse visitJsxOpeningElement(Swc4jAstJsxOpeningElement node);

    Swc4jAstVisitorResponse visitJsxOpeningFragment(Swc4jAstJsxOpeningFragment node);

    Swc4jAstVisitorResponse visitJsxSpreadChild(Swc4jAstJsxSpreadChild node);

    Swc4jAstVisitorResponse visitJsxText(Swc4jAstJsxText node);

    Swc4jAstVisitorResponse visitKeyValuePatProp(Swc4jAstKeyValuePatProp node);

    Swc4jAstVisitorResponse visitKeyValueProp(Swc4jAstKeyValueProp node);

    Swc4jAstVisitorResponse visitLabeledStmt(Swc4jAstLabeledStmt node);

    Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node);

    Swc4jAstVisitorResponse visitMetaPropExpr(Swc4jAstMetaPropExpr node);

    Swc4jAstVisitorResponse visitMethodProp(Swc4jAstMethodProp node);

    Swc4jAstVisitorResponse visitModule(Swc4jAstModule node);

    Swc4jAstVisitorResponse visitNamedExport(Swc4jAstNamedExport node);

    Swc4jAstVisitorResponse visitNewExpr(Swc4jAstNewExpr node);

    Swc4jAstVisitorResponse visitNull(Swc4jAstNull node);

    Swc4jAstVisitorResponse visitNumber(Swc4jAstNumber node);

    Swc4jAstVisitorResponse visitObjectLit(Swc4jAstObjectLit node);

    Swc4jAstVisitorResponse visitObjectPat(Swc4jAstObjectPat node);

    Swc4jAstVisitorResponse visitOptCall(Swc4jAstOptCall node);

    Swc4jAstVisitorResponse visitOptChainExpr(Swc4jAstOptChainExpr node);

    Swc4jAstVisitorResponse visitParam(Swc4jAstParam node);

    Swc4jAstVisitorResponse visitParenExpr(Swc4jAstParenExpr node);

    Swc4jAstVisitorResponse visitPrivateMethod(Swc4jAstPrivateMethod node);

    Swc4jAstVisitorResponse visitPrivateName(Swc4jAstPrivateName node);

    Swc4jAstVisitorResponse visitPrivateProp(Swc4jAstPrivateProp node);

    Swc4jAstVisitorResponse visitRegex(Swc4jAstRegex node);

    Swc4jAstVisitorResponse visitRestPat(Swc4jAstRestPat node);

    Swc4jAstVisitorResponse visitReturnStmt(Swc4jAstReturnStmt node);

    Swc4jAstVisitorResponse visitScript(Swc4jAstScript node);

    Swc4jAstVisitorResponse visitSeqExpr(Swc4jAstSeqExpr node);

    Swc4jAstVisitorResponse visitSetterProp(Swc4jAstSetterProp node);

    Swc4jAstVisitorResponse visitSpreadElement(Swc4jAstSpreadElement node);

    Swc4jAstVisitorResponse visitStaticBlock(Swc4jAstStaticBlock node);

    Swc4jAstVisitorResponse visitStr(Swc4jAstStr node);

    Swc4jAstVisitorResponse visitSuper(Swc4jAstSuper node);

    Swc4jAstVisitorResponse visitSuperPropExpr(Swc4jAstSuperPropExpr node);

    Swc4jAstVisitorResponse visitSwitchCase(Swc4jAstSwitchCase node);

    Swc4jAstVisitorResponse visitSwitchStmt(Swc4jAstSwitchStmt node);

    Swc4jAstVisitorResponse visitTaggedTpl(Swc4jAstTaggedTpl node);

    Swc4jAstVisitorResponse visitThisExpr(Swc4jAstThisExpr node);

    Swc4jAstVisitorResponse visitThrowStmt(Swc4jAstThrowStmt node);

    Swc4jAstVisitorResponse visitTpl(Swc4jAstTpl node);

    Swc4jAstVisitorResponse visitTplElement(Swc4jAstTplElement node);

    Swc4jAstVisitorResponse visitTryStmt(Swc4jAstTryStmt node);

    Swc4jAstVisitorResponse visitTsArrayType(Swc4jAstTsArrayType node);

    Swc4jAstVisitorResponse visitTsAsExpr(Swc4jAstTsAsExpr node);

    Swc4jAstVisitorResponse visitTsCallSignatureDecl(Swc4jAstTsCallSignatureDecl node);

    Swc4jAstVisitorResponse visitTsConditionalType(Swc4jAstTsConditionalType node);

    Swc4jAstVisitorResponse visitTsConstAssertion(Swc4jAstTsConstAssertion node);

    Swc4jAstVisitorResponse visitTsConstructSignatureDecl(Swc4jAstTsConstructSignatureDecl node);

    Swc4jAstVisitorResponse visitTsConstructorType(Swc4jAstTsConstructorType node);

    Swc4jAstVisitorResponse visitTsEnumDecl(Swc4jAstTsEnumDecl node);

    Swc4jAstVisitorResponse visitTsEnumMember(Swc4jAstTsEnumMember node);

    Swc4jAstVisitorResponse visitTsExportAssignment(Swc4jAstTsExportAssignment node);

    Swc4jAstVisitorResponse visitTsExprWithTypeArgs(Swc4jAstTsExprWithTypeArgs node);

    Swc4jAstVisitorResponse visitTsExternalModuleRef(Swc4jAstTsExternalModuleRef node);

    Swc4jAstVisitorResponse visitTsFnType(Swc4jAstTsFnType node);

    Swc4jAstVisitorResponse visitTsGetterSignature(Swc4jAstTsGetterSignature node);

    Swc4jAstVisitorResponse visitTsImportEqualsDecl(Swc4jAstTsImportEqualsDecl node);

    Swc4jAstVisitorResponse visitTsImportType(Swc4jAstTsImportType node);

    Swc4jAstVisitorResponse visitTsIndexSignature(Swc4jAstTsIndexSignature node);

    Swc4jAstVisitorResponse visitTsIndexedAccessType(Swc4jAstTsIndexedAccessType node);

    Swc4jAstVisitorResponse visitTsInferType(Swc4jAstTsInferType node);

    Swc4jAstVisitorResponse visitTsInstantiation(Swc4jAstTsInstantiation node);

    Swc4jAstVisitorResponse visitTsInterfaceBody(Swc4jAstTsInterfaceBody node);

    Swc4jAstVisitorResponse visitTsInterfaceDecl(Swc4jAstTsInterfaceDecl node);

    Swc4jAstVisitorResponse visitTsIntersectionType(Swc4jAstTsIntersectionType node);

    Swc4jAstVisitorResponse visitTsKeywordType(Swc4jAstTsKeywordType node);

    Swc4jAstVisitorResponse visitTsLitType(Swc4jAstTsLitType node);

    Swc4jAstVisitorResponse visitTsMappedType(Swc4jAstTsMappedType node);

    Swc4jAstVisitorResponse visitTsMethodSignature(Swc4jAstTsMethodSignature node);

    Swc4jAstVisitorResponse visitTsModuleBlock(Swc4jAstTsModuleBlock node);

    Swc4jAstVisitorResponse visitTsModuleDecl(Swc4jAstTsModuleDecl node);

    Swc4jAstVisitorResponse visitTsNamespaceDecl(Swc4jAstTsNamespaceDecl node);

    Swc4jAstVisitorResponse visitTsNamespaceExportDecl(Swc4jAstTsNamespaceExportDecl node);

    Swc4jAstVisitorResponse visitTsNonNullExpr(Swc4jAstTsNonNullExpr node);

    Swc4jAstVisitorResponse visitTsOptionalType(Swc4jAstTsOptionalType node);

    Swc4jAstVisitorResponse visitTsParamProp(Swc4jAstTsParamProp node);

    Swc4jAstVisitorResponse visitTsParenthesizedType(Swc4jAstTsParenthesizedType node);

    Swc4jAstVisitorResponse visitTsPropertySignature(Swc4jAstTsPropertySignature node);

    Swc4jAstVisitorResponse visitTsQualifiedName(Swc4jAstTsQualifiedName node);

    Swc4jAstVisitorResponse visitTsRestType(Swc4jAstTsRestType node);

    Swc4jAstVisitorResponse visitTsSatisfiesExpr(Swc4jAstTsSatisfiesExpr node);

    Swc4jAstVisitorResponse visitTsSetterSignature(Swc4jAstTsSetterSignature node);

    Swc4jAstVisitorResponse visitTsThisType(Swc4jAstTsThisType node);

    Swc4jAstVisitorResponse visitTsTplLitType(Swc4jAstTsTplLitType node);

    Swc4jAstVisitorResponse visitTsTupleElement(Swc4jAstTsTupleElement node);

    Swc4jAstVisitorResponse visitTsTupleType(Swc4jAstTsTupleType node);

    Swc4jAstVisitorResponse visitTsTypeAliasDecl(Swc4jAstTsTypeAliasDecl node);

    Swc4jAstVisitorResponse visitTsTypeAnn(Swc4jAstTsTypeAnn node);

    Swc4jAstVisitorResponse visitTsTypeAssertion(Swc4jAstTsTypeAssertion node);

    Swc4jAstVisitorResponse visitTsTypeLit(Swc4jAstTsTypeLit node);

    Swc4jAstVisitorResponse visitTsTypeOperator(Swc4jAstTsTypeOperator node);

    Swc4jAstVisitorResponse visitTsTypeParam(Swc4jAstTsTypeParam node);

    Swc4jAstVisitorResponse visitTsTypeParamDecl(Swc4jAstTsTypeParamDecl node);

    Swc4jAstVisitorResponse visitTsTypeParamInstantiation(Swc4jAstTsTypeParamInstantiation node);

    Swc4jAstVisitorResponse visitTsTypePredicate(Swc4jAstTsTypePredicate node);

    Swc4jAstVisitorResponse visitTsTypeQuery(Swc4jAstTsTypeQuery node);

    Swc4jAstVisitorResponse visitTsTypeRef(Swc4jAstTsTypeRef node);

    Swc4jAstVisitorResponse visitTsUnionType(Swc4jAstTsUnionType node);

    Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node);

    Swc4jAstVisitorResponse visitUpdateExpr(Swc4jAstUpdateExpr node);

    Swc4jAstVisitorResponse visitUsingDecl(Swc4jAstUsingDecl node);

    Swc4jAstVisitorResponse visitVarDecl(Swc4jAstVarDecl node);

    Swc4jAstVisitorResponse visitVarDeclarator(Swc4jAstVarDeclarator node);

    Swc4jAstVisitorResponse visitWhileStmt(Swc4jAstWhileStmt node);

    Swc4jAstVisitorResponse visitWithStmt(Swc4jAstWithStmt node);

    Swc4jAstVisitorResponse visitYieldExpr(Swc4jAstYieldExpr node);
}
