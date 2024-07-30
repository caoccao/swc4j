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

public abstract class Swc4jAstVisitor implements ISwc4jAstVisitor {
    @Override
    public Swc4jAstVisitorResponse visitArrayLit(Swc4jAstArrayLit node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitArrayPat(Swc4jAstArrayPat node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignPat(Swc4jAstAssignPat node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignPatProp(Swc4jAstAssignPatProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignProp(Swc4jAstAssignProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitAutoAccessor(Swc4jAstAutoAccessor node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitAwaitExpr(Swc4jAstAwaitExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitBigInt(Swc4jAstBigInt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitBindingIdent(Swc4jAstBindingIdent node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitBlockStmt(Swc4jAstBlockStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitBool(Swc4jAstBool node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitBreakStmt(Swc4jAstBreakStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitCatchClause(Swc4jAstCatchClause node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitClass(Swc4jAstClass node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitClassDecl(Swc4jAstClassDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitClassExpr(Swc4jAstClassExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitClassMethod(Swc4jAstClassMethod node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitClassProp(Swc4jAstClassProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitComputedPropName(Swc4jAstComputedPropName node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitCondExpr(Swc4jAstCondExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitConstructor(Swc4jAstConstructor node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitContinueStmt(Swc4jAstContinueStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitDebuggerStmt(Swc4jAstDebuggerStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitDecorator(Swc4jAstDecorator node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitDoWhileStmt(Swc4jAstDoWhileStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitEmptyStmt(Swc4jAstEmptyStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExportAll(Swc4jAstExportAll node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDecl(Swc4jAstExportDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultDecl(Swc4jAstExportDefaultDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultExpr(Swc4jAstExportDefaultExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultSpecifier(Swc4jAstExportDefaultSpecifier node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExportNamedSpecifier(Swc4jAstExportNamedSpecifier node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExportNamespaceSpecifier(Swc4jAstExportNamespaceSpecifier node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExprOrSpread(Swc4jAstExprOrSpread node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitExprStmt(Swc4jAstExprStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitFnDecl(Swc4jAstFnDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitFnExpr(Swc4jAstFnExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitForInStmt(Swc4jAstForInStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitForOfStmt(Swc4jAstForOfStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitForStmt(Swc4jAstForStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitFunction(Swc4jAstFunction node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitGetterProp(Swc4jAstGetterProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitIdent(Swc4jAstIdent node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitIdentName(Swc4jAstIdentName node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitIfStmt(Swc4jAstIfStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitImport(Swc4jAstImport node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitImportDecl(Swc4jAstImportDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitImportDefaultSpecifier(Swc4jAstImportDefaultSpecifier node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitImportNamedSpecifier(Swc4jAstImportNamedSpecifier node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitImportStarAsSpecifier(Swc4jAstImportStarAsSpecifier node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitInvalid(Swc4jAstInvalid node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxAttr(Swc4jAstJsxAttr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxClosingElement(Swc4jAstJsxClosingElement node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxClosingFragment(Swc4jAstJsxClosingFragment node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxElement(Swc4jAstJsxElement node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxEmptyExpr(Swc4jAstJsxEmptyExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxExprContainer(Swc4jAstJsxExprContainer node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxFragment(Swc4jAstJsxFragment node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxMemberExpr(Swc4jAstJsxMemberExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxNamespacedName(Swc4jAstJsxNamespacedName node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxOpeningElement(Swc4jAstJsxOpeningElement node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxOpeningFragment(Swc4jAstJsxOpeningFragment node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxSpreadChild(Swc4jAstJsxSpreadChild node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxText(Swc4jAstJsxText node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitKeyValuePatProp(Swc4jAstKeyValuePatProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitKeyValueProp(Swc4jAstKeyValueProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitLabeledStmt(Swc4jAstLabeledStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitMetaPropExpr(Swc4jAstMetaPropExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitMethodProp(Swc4jAstMethodProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitModule(Swc4jAstModule node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitNamedExport(Swc4jAstNamedExport node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitNewExpr(Swc4jAstNewExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitNull(Swc4jAstNull node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitNumber(Swc4jAstNumber node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitObjectLit(Swc4jAstObjectLit node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitObjectPat(Swc4jAstObjectPat node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitOptCall(Swc4jAstOptCall node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitOptChainExpr(Swc4jAstOptChainExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitParam(Swc4jAstParam node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitParenExpr(Swc4jAstParenExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitPrivateMethod(Swc4jAstPrivateMethod node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitPrivateName(Swc4jAstPrivateName node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitPrivateProp(Swc4jAstPrivateProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitRegex(Swc4jAstRegex node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitRestPat(Swc4jAstRestPat node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitReturnStmt(Swc4jAstReturnStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitScript(Swc4jAstScript node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitSeqExpr(Swc4jAstSeqExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitSetterProp(Swc4jAstSetterProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitSpreadElement(Swc4jAstSpreadElement node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitStaticBlock(Swc4jAstStaticBlock node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitStr(Swc4jAstStr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitSuper(Swc4jAstSuper node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitSuperPropExpr(Swc4jAstSuperPropExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitSwitchCase(Swc4jAstSwitchCase node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitSwitchStmt(Swc4jAstSwitchStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTaggedTpl(Swc4jAstTaggedTpl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitThisExpr(Swc4jAstThisExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitThrowStmt(Swc4jAstThrowStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTpl(Swc4jAstTpl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTplElement(Swc4jAstTplElement node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTryStmt(Swc4jAstTryStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsArrayType(Swc4jAstTsArrayType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsAsExpr(Swc4jAstTsAsExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsCallSignatureDecl(Swc4jAstTsCallSignatureDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConditionalType(Swc4jAstTsConditionalType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConstAssertion(Swc4jAstTsConstAssertion node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConstructSignatureDecl(Swc4jAstTsConstructSignatureDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConstructorType(Swc4jAstTsConstructorType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsEnumDecl(Swc4jAstTsEnumDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsEnumMember(Swc4jAstTsEnumMember node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsExportAssignment(Swc4jAstTsExportAssignment node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsExprWithTypeArgs(Swc4jAstTsExprWithTypeArgs node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsExternalModuleRef(Swc4jAstTsExternalModuleRef node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsFnType(Swc4jAstTsFnType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsGetterSignature(Swc4jAstTsGetterSignature node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsImportEqualsDecl(Swc4jAstTsImportEqualsDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsImportType(Swc4jAstTsImportType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsIndexSignature(Swc4jAstTsIndexSignature node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsIndexedAccessType(Swc4jAstTsIndexedAccessType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInferType(Swc4jAstTsInferType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInstantiation(Swc4jAstTsInstantiation node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInterfaceBody(Swc4jAstTsInterfaceBody node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInterfaceDecl(Swc4jAstTsInterfaceDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsIntersectionType(Swc4jAstTsIntersectionType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsKeywordType(Swc4jAstTsKeywordType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsLitType(Swc4jAstTsLitType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsMappedType(Swc4jAstTsMappedType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsMethodSignature(Swc4jAstTsMethodSignature node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsModuleBlock(Swc4jAstTsModuleBlock node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsModuleDecl(Swc4jAstTsModuleDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsNamespaceDecl(Swc4jAstTsNamespaceDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsNamespaceExportDecl(Swc4jAstTsNamespaceExportDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsNonNullExpr(Swc4jAstTsNonNullExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsOptionalType(Swc4jAstTsOptionalType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsParamProp(Swc4jAstTsParamProp node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsParenthesizedType(Swc4jAstTsParenthesizedType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsPropertySignature(Swc4jAstTsPropertySignature node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsQualifiedName(Swc4jAstTsQualifiedName node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsRestType(Swc4jAstTsRestType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsSatisfiesExpr(Swc4jAstTsSatisfiesExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsSetterSignature(Swc4jAstTsSetterSignature node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsThisType(Swc4jAstTsThisType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTplLitType(Swc4jAstTsTplLitType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTupleElement(Swc4jAstTsTupleElement node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTupleType(Swc4jAstTsTupleType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeAliasDecl(Swc4jAstTsTypeAliasDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeAnn(Swc4jAstTsTypeAnn node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeAssertion(Swc4jAstTsTypeAssertion node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeLit(Swc4jAstTsTypeLit node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeOperator(Swc4jAstTsTypeOperator node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeParam(Swc4jAstTsTypeParam node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeParamDecl(Swc4jAstTsTypeParamDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeParamInstantiation(Swc4jAstTsTypeParamInstantiation node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypePredicate(Swc4jAstTsTypePredicate node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeQuery(Swc4jAstTsTypeQuery node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeRef(Swc4jAstTsTypeRef node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitTsUnionType(Swc4jAstTsUnionType node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitUpdateExpr(Swc4jAstUpdateExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitUsingDecl(Swc4jAstUsingDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitVarDecl(Swc4jAstVarDecl node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitVarDeclarator(Swc4jAstVarDeclarator node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitWhileStmt(Swc4jAstWhileStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitWithStmt(Swc4jAstWithStmt node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }

    @Override
    public Swc4jAstVisitorResponse visitYieldExpr(Swc4jAstYieldExpr node) {
        return Swc4jAstVisitorResponse.OkAndContinue;
    }
}
