/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.miscs.*;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Swc4jAstCounterVisitor extends Swc4jAstVisitor {
    protected Map<Swc4jAstType, AtomicInteger> counterMap;

    public Swc4jAstCounterVisitor() {
        counterMap = Stream.of(Swc4jAstType.values())
                .collect(Collectors.toMap(Function.identity(), value -> new AtomicInteger()));
    }

    public int get(Swc4jAstType type) {
        return counterMap.get(type).get();
    }

    public Map<Swc4jAstType, AtomicInteger> getCounterMap() {
        return counterMap;
    }

    public int incrementAndGet(Swc4jAstType type) {
        return counterMap.get(type).incrementAndGet();
    }

    public void reset(Swc4jAstType type) {
        counterMap.get(type).set(0);
    }

    @Override
    public Swc4jAstVisitorResponse visitArrayLit(Swc4jAstArrayLit node) {
        incrementAndGet(Swc4jAstType.ArrayLit);
        return super.visitArrayLit(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitArrayPat(Swc4jAstArrayPat node) {
        incrementAndGet(Swc4jAstType.ArrayPat);
        return super.visitArrayPat(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node) {
        incrementAndGet(Swc4jAstType.ArrowExpr);
        return super.visitArrowExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node) {
        incrementAndGet(Swc4jAstType.AssignExpr);
        return super.visitAssignExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignPat(Swc4jAstAssignPat node) {
        incrementAndGet(Swc4jAstType.AssignPat);
        return super.visitAssignPat(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignPatProp(Swc4jAstAssignPatProp node) {
        incrementAndGet(Swc4jAstType.AssignPatProp);
        return super.visitAssignPatProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitAssignProp(Swc4jAstAssignProp node) {
        incrementAndGet(Swc4jAstType.AssignProp);
        return super.visitAssignProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitAutoAccessor(Swc4jAstAutoAccessor node) {
        incrementAndGet(Swc4jAstType.AutoAccessor);
        return super.visitAutoAccessor(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitAwaitExpr(Swc4jAstAwaitExpr node) {
        incrementAndGet(Swc4jAstType.AwaitExpr);
        return super.visitAwaitExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitBigInt(Swc4jAstBigInt node) {
        incrementAndGet(Swc4jAstType.BigInt);
        return super.visitBigInt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
        incrementAndGet(Swc4jAstType.BinExpr);
        return super.visitBinExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitBindingIdent(Swc4jAstBindingIdent node) {
        incrementAndGet(Swc4jAstType.BindingIdent);
        return super.visitBindingIdent(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitBlockStmt(Swc4jAstBlockStmt node) {
        incrementAndGet(Swc4jAstType.BlockStmt);
        return super.visitBlockStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitBool(Swc4jAstBool node) {
        incrementAndGet(Swc4jAstType.Bool);
        return super.visitBool(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitBreakStmt(Swc4jAstBreakStmt node) {
        incrementAndGet(Swc4jAstType.BreakStmt);
        return super.visitBreakStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node) {
        incrementAndGet(Swc4jAstType.CallExpr);
        return super.visitCallExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitCatchClause(Swc4jAstCatchClause node) {
        incrementAndGet(Swc4jAstType.CatchClause);
        return super.visitCatchClause(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitClass(Swc4jAstClass node) {
        incrementAndGet(Swc4jAstType.Class);
        return super.visitClass(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitClassDecl(Swc4jAstClassDecl node) {
        incrementAndGet(Swc4jAstType.ClassDecl);
        return super.visitClassDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitClassExpr(Swc4jAstClassExpr node) {
        incrementAndGet(Swc4jAstType.ClassExpr);
        return super.visitClassExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitClassMethod(Swc4jAstClassMethod node) {
        incrementAndGet(Swc4jAstType.ClassMethod);
        return super.visitClassMethod(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitClassProp(Swc4jAstClassProp node) {
        incrementAndGet(Swc4jAstType.ClassProp);
        return super.visitClassProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitComputedPropName(Swc4jAstComputedPropName node) {
        incrementAndGet(Swc4jAstType.ComputedPropName);
        return super.visitComputedPropName(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitCondExpr(Swc4jAstCondExpr node) {
        incrementAndGet(Swc4jAstType.CondExpr);
        return super.visitCondExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitConstructor(Swc4jAstConstructor node) {
        incrementAndGet(Swc4jAstType.Constructor);
        return super.visitConstructor(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitContinueStmt(Swc4jAstContinueStmt node) {
        incrementAndGet(Swc4jAstType.ContinueStmt);
        return super.visitContinueStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitDebuggerStmt(Swc4jAstDebuggerStmt node) {
        incrementAndGet(Swc4jAstType.DebuggerStmt);
        return super.visitDebuggerStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitDecorator(Swc4jAstDecorator node) {
        incrementAndGet(Swc4jAstType.Decorator);
        return super.visitDecorator(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitDoWhileStmt(Swc4jAstDoWhileStmt node) {
        incrementAndGet(Swc4jAstType.DoWhileStmt);
        return super.visitDoWhileStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitEmptyStmt(Swc4jAstEmptyStmt node) {
        incrementAndGet(Swc4jAstType.EmptyStmt);
        return super.visitEmptyStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportAll(Swc4jAstExportAll node) {
        incrementAndGet(Swc4jAstType.ExportAll);
        return super.visitExportAll(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDecl(Swc4jAstExportDecl node) {
        incrementAndGet(Swc4jAstType.ExportDecl);
        return super.visitExportDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultDecl(Swc4jAstExportDefaultDecl node) {
        incrementAndGet(Swc4jAstType.ExportDefaultDecl);
        return super.visitExportDefaultDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultExpr(Swc4jAstExportDefaultExpr node) {
        incrementAndGet(Swc4jAstType.ExportDefaultExpr);
        return super.visitExportDefaultExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportDefaultSpecifier(Swc4jAstExportDefaultSpecifier node) {
        incrementAndGet(Swc4jAstType.ExportDefaultSpecifier);
        return super.visitExportDefaultSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportNamedSpecifier(Swc4jAstExportNamedSpecifier node) {
        incrementAndGet(Swc4jAstType.ExportNamedSpecifier);
        return super.visitExportNamedSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExportNamespaceSpecifier(Swc4jAstExportNamespaceSpecifier node) {
        incrementAndGet(Swc4jAstType.ExportNamespaceSpecifier);
        return super.visitExportNamespaceSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExprOrSpread(Swc4jAstExprOrSpread node) {
        incrementAndGet(Swc4jAstType.ExprOrSpread);
        return super.visitExprOrSpread(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitExprStmt(Swc4jAstExprStmt node) {
        incrementAndGet(Swc4jAstType.ExprStmt);
        return super.visitExprStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitFnDecl(Swc4jAstFnDecl node) {
        incrementAndGet(Swc4jAstType.FnDecl);
        return super.visitFnDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitFnExpr(Swc4jAstFnExpr node) {
        incrementAndGet(Swc4jAstType.FnExpr);
        return super.visitFnExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitForInStmt(Swc4jAstForInStmt node) {
        incrementAndGet(Swc4jAstType.ForInStmt);
        return super.visitForInStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitForOfStmt(Swc4jAstForOfStmt node) {
        incrementAndGet(Swc4jAstType.ForOfStmt);
        return super.visitForOfStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitForStmt(Swc4jAstForStmt node) {
        incrementAndGet(Swc4jAstType.ForStmt);
        return super.visitForStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitFunction(Swc4jAstFunction node) {
        incrementAndGet(Swc4jAstType.Function);
        return super.visitFunction(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitGetterProp(Swc4jAstGetterProp node) {
        incrementAndGet(Swc4jAstType.GetterProp);
        return super.visitGetterProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitIdent(Swc4jAstIdent node) {
        incrementAndGet(Swc4jAstType.Ident);
        return super.visitIdent(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitIdentName(Swc4jAstIdentName node) {
        incrementAndGet(Swc4jAstType.IdentName);
        return super.visitIdentName(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitIfStmt(Swc4jAstIfStmt node) {
        incrementAndGet(Swc4jAstType.IfStmt);
        return super.visitIfStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImport(Swc4jAstImport node) {
        incrementAndGet(Swc4jAstType.Import);
        return super.visitImport(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportDecl(Swc4jAstImportDecl node) {
        incrementAndGet(Swc4jAstType.ImportDecl);
        return super.visitImportDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportDefaultSpecifier(Swc4jAstImportDefaultSpecifier node) {
        incrementAndGet(Swc4jAstType.ImportDefaultSpecifier);
        return super.visitImportDefaultSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportNamedSpecifier(Swc4jAstImportNamedSpecifier node) {
        incrementAndGet(Swc4jAstType.ImportNamedSpecifier);
        return super.visitImportNamedSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitImportStarAsSpecifier(Swc4jAstImportStarAsSpecifier node) {
        incrementAndGet(Swc4jAstType.ImportStarAsSpecifier);
        return super.visitImportStarAsSpecifier(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitInvalid(Swc4jAstInvalid node) {
        incrementAndGet(Swc4jAstType.Invalid);
        return super.visitInvalid(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxAttr(Swc4jAstJsxAttr node) {
        incrementAndGet(Swc4jAstType.JsxAttr);
        return super.visitJsxAttr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxClosingElement(Swc4jAstJsxClosingElement node) {
        incrementAndGet(Swc4jAstType.JsxClosingElement);
        return super.visitJsxClosingElement(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxClosingFragment(Swc4jAstJsxClosingFragment node) {
        incrementAndGet(Swc4jAstType.JsxClosingFragment);
        return super.visitJsxClosingFragment(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxElement(Swc4jAstJsxElement node) {
        incrementAndGet(Swc4jAstType.JsxElement);
        return super.visitJsxElement(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxEmptyExpr(Swc4jAstJsxEmptyExpr node) {
        incrementAndGet(Swc4jAstType.JsxEmptyExpr);
        return super.visitJsxEmptyExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxExprContainer(Swc4jAstJsxExprContainer node) {
        incrementAndGet(Swc4jAstType.JsxExprContainer);
        return super.visitJsxExprContainer(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxFragment(Swc4jAstJsxFragment node) {
        incrementAndGet(Swc4jAstType.JsxFragment);
        return super.visitJsxFragment(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxMemberExpr(Swc4jAstJsxMemberExpr node) {
        incrementAndGet(Swc4jAstType.JsxMemberExpr);
        return super.visitJsxMemberExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxNamespacedName(Swc4jAstJsxNamespacedName node) {
        incrementAndGet(Swc4jAstType.JsxNamespacedName);
        return super.visitJsxNamespacedName(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxOpeningElement(Swc4jAstJsxOpeningElement node) {
        incrementAndGet(Swc4jAstType.JsxOpeningElement);
        return super.visitJsxOpeningElement(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxOpeningFragment(Swc4jAstJsxOpeningFragment node) {
        incrementAndGet(Swc4jAstType.JsxOpeningFragment);
        return super.visitJsxOpeningFragment(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxSpreadChild(Swc4jAstJsxSpreadChild node) {
        incrementAndGet(Swc4jAstType.JsxSpreadChild);
        return super.visitJsxSpreadChild(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitJsxText(Swc4jAstJsxText node) {
        incrementAndGet(Swc4jAstType.JsxText);
        return super.visitJsxText(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitKeyValuePatProp(Swc4jAstKeyValuePatProp node) {
        incrementAndGet(Swc4jAstType.KeyValuePatProp);
        return super.visitKeyValuePatProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitKeyValueProp(Swc4jAstKeyValueProp node) {
        incrementAndGet(Swc4jAstType.KeyValueProp);
        return super.visitKeyValueProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitLabeledStmt(Swc4jAstLabeledStmt node) {
        incrementAndGet(Swc4jAstType.LabeledStmt);
        return super.visitLabeledStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
        incrementAndGet(Swc4jAstType.MemberExpr);
        return super.visitMemberExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitMetaPropExpr(Swc4jAstMetaPropExpr node) {
        incrementAndGet(Swc4jAstType.MetaPropExpr);
        return super.visitMetaPropExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitMethodProp(Swc4jAstMethodProp node) {
        incrementAndGet(Swc4jAstType.MethodProp);
        return super.visitMethodProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitModule(Swc4jAstModule node) {
        incrementAndGet(Swc4jAstType.Module);
        return super.visitModule(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitNamedExport(Swc4jAstNamedExport node) {
        incrementAndGet(Swc4jAstType.NamedExport);
        return super.visitNamedExport(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitNewExpr(Swc4jAstNewExpr node) {
        incrementAndGet(Swc4jAstType.NewExpr);
        return super.visitNewExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitNull(Swc4jAstNull node) {
        incrementAndGet(Swc4jAstType.Null);
        return super.visitNull(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitNumber(Swc4jAstNumber node) {
        incrementAndGet(Swc4jAstType.Number);
        return super.visitNumber(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitObjectLit(Swc4jAstObjectLit node) {
        incrementAndGet(Swc4jAstType.ObjectLit);
        return super.visitObjectLit(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitObjectPat(Swc4jAstObjectPat node) {
        incrementAndGet(Swc4jAstType.ObjectPat);
        return super.visitObjectPat(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitOptCall(Swc4jAstOptCall node) {
        incrementAndGet(Swc4jAstType.OptCall);
        return super.visitOptCall(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitOptChainExpr(Swc4jAstOptChainExpr node) {
        incrementAndGet(Swc4jAstType.OptChainExpr);
        return super.visitOptChainExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitParam(Swc4jAstParam node) {
        incrementAndGet(Swc4jAstType.Param);
        return super.visitParam(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitParenExpr(Swc4jAstParenExpr node) {
        incrementAndGet(Swc4jAstType.ParenExpr);
        return super.visitParenExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitPrivateMethod(Swc4jAstPrivateMethod node) {
        incrementAndGet(Swc4jAstType.PrivateMethod);
        return super.visitPrivateMethod(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitPrivateName(Swc4jAstPrivateName node) {
        incrementAndGet(Swc4jAstType.PrivateName);
        return super.visitPrivateName(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitPrivateProp(Swc4jAstPrivateProp node) {
        incrementAndGet(Swc4jAstType.PrivateProp);
        return super.visitPrivateProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitRegex(Swc4jAstRegex node) {
        incrementAndGet(Swc4jAstType.Regex);
        return super.visitRegex(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitRestPat(Swc4jAstRestPat node) {
        incrementAndGet(Swc4jAstType.RestPat);
        return super.visitRestPat(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitReturnStmt(Swc4jAstReturnStmt node) {
        incrementAndGet(Swc4jAstType.ReturnStmt);
        return super.visitReturnStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitScript(Swc4jAstScript node) {
        incrementAndGet(Swc4jAstType.Script);
        return super.visitScript(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitSeqExpr(Swc4jAstSeqExpr node) {
        incrementAndGet(Swc4jAstType.SeqExpr);
        return super.visitSeqExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitSetterProp(Swc4jAstSetterProp node) {
        incrementAndGet(Swc4jAstType.SetterProp);
        return super.visitSetterProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitSpreadElement(Swc4jAstSpreadElement node) {
        incrementAndGet(Swc4jAstType.SpreadElement);
        return super.visitSpreadElement(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitStaticBlock(Swc4jAstStaticBlock node) {
        incrementAndGet(Swc4jAstType.StaticBlock);
        return super.visitStaticBlock(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitStr(Swc4jAstStr node) {
        incrementAndGet(Swc4jAstType.Str);
        return super.visitStr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitSuper(Swc4jAstSuper node) {
        incrementAndGet(Swc4jAstType.Super);
        return super.visitSuper(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitSuperPropExpr(Swc4jAstSuperPropExpr node) {
        incrementAndGet(Swc4jAstType.SuperPropExpr);
        return super.visitSuperPropExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitSwitchCase(Swc4jAstSwitchCase node) {
        incrementAndGet(Swc4jAstType.SwitchCase);
        return super.visitSwitchCase(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitSwitchStmt(Swc4jAstSwitchStmt node) {
        incrementAndGet(Swc4jAstType.SwitchStmt);
        return super.visitSwitchStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTaggedTpl(Swc4jAstTaggedTpl node) {
        incrementAndGet(Swc4jAstType.TaggedTpl);
        return super.visitTaggedTpl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitThisExpr(Swc4jAstThisExpr node) {
        incrementAndGet(Swc4jAstType.ThisExpr);
        return super.visitThisExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitThrowStmt(Swc4jAstThrowStmt node) {
        incrementAndGet(Swc4jAstType.ThrowStmt);
        return super.visitThrowStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTpl(Swc4jAstTpl node) {
        incrementAndGet(Swc4jAstType.Tpl);
        return super.visitTpl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTplElement(Swc4jAstTplElement node) {
        incrementAndGet(Swc4jAstType.TplElement);
        return super.visitTplElement(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTryStmt(Swc4jAstTryStmt node) {
        incrementAndGet(Swc4jAstType.TryStmt);
        return super.visitTryStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsArrayType(Swc4jAstTsArrayType node) {
        incrementAndGet(Swc4jAstType.TsArrayType);
        return super.visitTsArrayType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsAsExpr(Swc4jAstTsAsExpr node) {
        incrementAndGet(Swc4jAstType.TsAsExpr);
        return super.visitTsAsExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsCallSignatureDecl(Swc4jAstTsCallSignatureDecl node) {
        incrementAndGet(Swc4jAstType.TsCallSignatureDecl);
        return super.visitTsCallSignatureDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConditionalType(Swc4jAstTsConditionalType node) {
        incrementAndGet(Swc4jAstType.TsConditionalType);
        return super.visitTsConditionalType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConstAssertion(Swc4jAstTsConstAssertion node) {
        incrementAndGet(Swc4jAstType.TsConstAssertion);
        return super.visitTsConstAssertion(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConstructSignatureDecl(Swc4jAstTsConstructSignatureDecl node) {
        incrementAndGet(Swc4jAstType.TsConstructSignatureDecl);
        return super.visitTsConstructSignatureDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsConstructorType(Swc4jAstTsConstructorType node) {
        incrementAndGet(Swc4jAstType.TsConstructorType);
        return super.visitTsConstructorType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsEnumDecl(Swc4jAstTsEnumDecl node) {
        incrementAndGet(Swc4jAstType.TsEnumDecl);
        return super.visitTsEnumDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsEnumMember(Swc4jAstTsEnumMember node) {
        incrementAndGet(Swc4jAstType.TsEnumMember);
        return super.visitTsEnumMember(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsExportAssignment(Swc4jAstTsExportAssignment node) {
        incrementAndGet(Swc4jAstType.TsExportAssignment);
        return super.visitTsExportAssignment(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsExprWithTypeArgs(Swc4jAstTsExprWithTypeArgs node) {
        incrementAndGet(Swc4jAstType.TsExprWithTypeArgs);
        return super.visitTsExprWithTypeArgs(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsExternalModuleRef(Swc4jAstTsExternalModuleRef node) {
        incrementAndGet(Swc4jAstType.TsExternalModuleRef);
        return super.visitTsExternalModuleRef(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsFnType(Swc4jAstTsFnType node) {
        incrementAndGet(Swc4jAstType.TsFnType);
        return super.visitTsFnType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsGetterSignature(Swc4jAstTsGetterSignature node) {
        incrementAndGet(Swc4jAstType.TsGetterSignature);
        return super.visitTsGetterSignature(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsImportEqualsDecl(Swc4jAstTsImportEqualsDecl node) {
        incrementAndGet(Swc4jAstType.TsImportEqualsDecl);
        return super.visitTsImportEqualsDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsImportType(Swc4jAstTsImportType node) {
        incrementAndGet(Swc4jAstType.TsImportType);
        return super.visitTsImportType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsIndexSignature(Swc4jAstTsIndexSignature node) {
        incrementAndGet(Swc4jAstType.TsIndexSignature);
        return super.visitTsIndexSignature(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsIndexedAccessType(Swc4jAstTsIndexedAccessType node) {
        incrementAndGet(Swc4jAstType.TsIndexedAccessType);
        return super.visitTsIndexedAccessType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInferType(Swc4jAstTsInferType node) {
        incrementAndGet(Swc4jAstType.TsInferType);
        return super.visitTsInferType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInstantiation(Swc4jAstTsInstantiation node) {
        incrementAndGet(Swc4jAstType.TsInstantiation);
        return super.visitTsInstantiation(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInterfaceBody(Swc4jAstTsInterfaceBody node) {
        incrementAndGet(Swc4jAstType.TsInterfaceBody);
        return super.visitTsInterfaceBody(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsInterfaceDecl(Swc4jAstTsInterfaceDecl node) {
        incrementAndGet(Swc4jAstType.TsInterfaceDecl);
        return super.visitTsInterfaceDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsIntersectionType(Swc4jAstTsIntersectionType node) {
        incrementAndGet(Swc4jAstType.TsIntersectionType);
        return super.visitTsIntersectionType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsKeywordType(Swc4jAstTsKeywordType node) {
        incrementAndGet(Swc4jAstType.TsKeywordType);
        return super.visitTsKeywordType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsLitType(Swc4jAstTsLitType node) {
        incrementAndGet(Swc4jAstType.TsLitType);
        return super.visitTsLitType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsMappedType(Swc4jAstTsMappedType node) {
        incrementAndGet(Swc4jAstType.TsMappedType);
        return super.visitTsMappedType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsMethodSignature(Swc4jAstTsMethodSignature node) {
        incrementAndGet(Swc4jAstType.TsMethodSignature);
        return super.visitTsMethodSignature(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsModuleBlock(Swc4jAstTsModuleBlock node) {
        incrementAndGet(Swc4jAstType.TsModuleBlock);
        return super.visitTsModuleBlock(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsModuleDecl(Swc4jAstTsModuleDecl node) {
        incrementAndGet(Swc4jAstType.TsModuleDecl);
        return super.visitTsModuleDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsNamespaceDecl(Swc4jAstTsNamespaceDecl node) {
        incrementAndGet(Swc4jAstType.TsNamespaceDecl);
        return super.visitTsNamespaceDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsNamespaceExportDecl(Swc4jAstTsNamespaceExportDecl node) {
        incrementAndGet(Swc4jAstType.TsNamespaceExportDecl);
        return super.visitTsNamespaceExportDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsNonNullExpr(Swc4jAstTsNonNullExpr node) {
        incrementAndGet(Swc4jAstType.TsNonNullExpr);
        return super.visitTsNonNullExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsOptionalType(Swc4jAstTsOptionalType node) {
        incrementAndGet(Swc4jAstType.TsOptionalType);
        return super.visitTsOptionalType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsParamProp(Swc4jAstTsParamProp node) {
        incrementAndGet(Swc4jAstType.TsParamProp);
        return super.visitTsParamProp(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsParenthesizedType(Swc4jAstTsParenthesizedType node) {
        incrementAndGet(Swc4jAstType.TsParenthesizedType);
        return super.visitTsParenthesizedType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsPropertySignature(Swc4jAstTsPropertySignature node) {
        incrementAndGet(Swc4jAstType.TsPropertySignature);
        return super.visitTsPropertySignature(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsQualifiedName(Swc4jAstTsQualifiedName node) {
        incrementAndGet(Swc4jAstType.TsQualifiedName);
        return super.visitTsQualifiedName(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsRestType(Swc4jAstTsRestType node) {
        incrementAndGet(Swc4jAstType.TsRestType);
        return super.visitTsRestType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsSatisfiesExpr(Swc4jAstTsSatisfiesExpr node) {
        incrementAndGet(Swc4jAstType.TsSatisfiesExpr);
        return super.visitTsSatisfiesExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsSetterSignature(Swc4jAstTsSetterSignature node) {
        incrementAndGet(Swc4jAstType.TsSetterSignature);
        return super.visitTsSetterSignature(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsThisType(Swc4jAstTsThisType node) {
        incrementAndGet(Swc4jAstType.TsThisType);
        return super.visitTsThisType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTplLitType(Swc4jAstTsTplLitType node) {
        incrementAndGet(Swc4jAstType.TsTplLitType);
        return super.visitTsTplLitType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTupleElement(Swc4jAstTsTupleElement node) {
        incrementAndGet(Swc4jAstType.TsTupleElement);
        return super.visitTsTupleElement(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTupleType(Swc4jAstTsTupleType node) {
        incrementAndGet(Swc4jAstType.TsTupleType);
        return super.visitTsTupleType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeAliasDecl(Swc4jAstTsTypeAliasDecl node) {
        incrementAndGet(Swc4jAstType.TsTypeAliasDecl);
        return super.visitTsTypeAliasDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeAnn(Swc4jAstTsTypeAnn node) {
        incrementAndGet(Swc4jAstType.TsTypeAnn);
        return super.visitTsTypeAnn(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeAssertion(Swc4jAstTsTypeAssertion node) {
        incrementAndGet(Swc4jAstType.TsTypeAssertion);
        return super.visitTsTypeAssertion(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeLit(Swc4jAstTsTypeLit node) {
        incrementAndGet(Swc4jAstType.TsTypeLit);
        return super.visitTsTypeLit(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeOperator(Swc4jAstTsTypeOperator node) {
        incrementAndGet(Swc4jAstType.TsTypeOperator);
        return super.visitTsTypeOperator(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeParam(Swc4jAstTsTypeParam node) {
        incrementAndGet(Swc4jAstType.TsTypeParam);
        return super.visitTsTypeParam(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeParamDecl(Swc4jAstTsTypeParamDecl node) {
        incrementAndGet(Swc4jAstType.TsTypeParamDecl);
        return super.visitTsTypeParamDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeParamInstantiation(Swc4jAstTsTypeParamInstantiation node) {
        incrementAndGet(Swc4jAstType.TsTypeParamInstantiation);
        return super.visitTsTypeParamInstantiation(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypePredicate(Swc4jAstTsTypePredicate node) {
        incrementAndGet(Swc4jAstType.TsTypePredicate);
        return super.visitTsTypePredicate(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeQuery(Swc4jAstTsTypeQuery node) {
        incrementAndGet(Swc4jAstType.TsTypeQuery);
        return super.visitTsTypeQuery(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsTypeRef(Swc4jAstTsTypeRef node) {
        incrementAndGet(Swc4jAstType.TsTypeRef);
        return super.visitTsTypeRef(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitTsUnionType(Swc4jAstTsUnionType node) {
        incrementAndGet(Swc4jAstType.TsUnionType);
        return super.visitTsUnionType(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
        incrementAndGet(Swc4jAstType.UnaryExpr);
        return super.visitUnaryExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitUpdateExpr(Swc4jAstUpdateExpr node) {
        incrementAndGet(Swc4jAstType.UpdateExpr);
        return super.visitUpdateExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitUsingDecl(Swc4jAstUsingDecl node) {
        incrementAndGet(Swc4jAstType.UsingDecl);
        return super.visitUsingDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitVarDecl(Swc4jAstVarDecl node) {
        incrementAndGet(Swc4jAstType.VarDecl);
        return super.visitVarDecl(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitVarDeclarator(Swc4jAstVarDeclarator node) {
        incrementAndGet(Swc4jAstType.VarDeclarator);
        return super.visitVarDeclarator(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitWhileStmt(Swc4jAstWhileStmt node) {
        incrementAndGet(Swc4jAstType.WhileStmt);
        return super.visitWhileStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitWithStmt(Swc4jAstWithStmt node) {
        incrementAndGet(Swc4jAstType.WithStmt);
        return super.visitWithStmt(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitYieldExpr(Swc4jAstYieldExpr node) {
        incrementAndGet(Swc4jAstType.YieldExpr);
        return super.visitYieldExpr(node);
    }
}
