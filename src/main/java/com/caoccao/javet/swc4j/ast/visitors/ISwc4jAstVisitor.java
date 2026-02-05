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
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.miscs.*;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.*;

/**
 * Interface for visiting AST nodes.
 */
public interface ISwc4jAstVisitor {
    /**
     * Visits an array literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitArrayLit(Swc4jAstArrayLit node);

    /**
     * Visits an array pattern node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitArrayPat(Swc4jAstArrayPat node);

    /**
     * Visits an arrow expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitArrowExpr(Swc4jAstArrowExpr node);

    /**
     * Visits an assignment expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitAssignExpr(Swc4jAstAssignExpr node);

    /**
     * Visits an assignment pattern node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitAssignPat(Swc4jAstAssignPat node);

    /**
     * Visits an assignment pattern property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitAssignPatProp(Swc4jAstAssignPatProp node);

    /**
     * Visits an assignment property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitAssignProp(Swc4jAstAssignProp node);

    /**
     * Visits an auto accessor node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitAutoAccessor(Swc4jAstAutoAccessor node);

    /**
     * Visits an await expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitAwaitExpr(Swc4jAstAwaitExpr node);

    /**
     * Visits a big integer literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitBigInt(Swc4jAstBigInt node);

    /**
     * Visits a binary expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node);

    /**
     * Visits a binding identifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitBindingIdent(Swc4jAstBindingIdent node);

    /**
     * Visits a block statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitBlockStmt(Swc4jAstBlockStmt node);

    /**
     * Visits a boolean literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitBool(Swc4jAstBool node);

    /**
     * Visits a break statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitBreakStmt(Swc4jAstBreakStmt node);

    /**
     * Visits a call expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitCallExpr(Swc4jAstCallExpr node);

    /**
     * Visits a catch clause node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitCatchClause(Swc4jAstCatchClause node);

    /**
     * Visits a class node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitClass(Swc4jAstClass node);

    /**
     * Visits a class declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitClassDecl(Swc4jAstClassDecl node);

    /**
     * Visits a class expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitClassExpr(Swc4jAstClassExpr node);

    /**
     * Visits a class method node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitClassMethod(Swc4jAstClassMethod node);

    /**
     * Visits a class property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitClassProp(Swc4jAstClassProp node);

    /**
     * Visits a computed property name node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitComputedPropName(Swc4jAstComputedPropName node);

    /**
     * Visits a conditional expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitCondExpr(Swc4jAstCondExpr node);

    /**
     * Visits a constructor node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitConstructor(Swc4jAstConstructor node);

    /**
     * Visits a continue statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitContinueStmt(Swc4jAstContinueStmt node);

    /**
     * Visits a debugger statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitDebuggerStmt(Swc4jAstDebuggerStmt node);

    /**
     * Visits a decorator node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitDecorator(Swc4jAstDecorator node);

    /**
     * Visits a do-while statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitDoWhileStmt(Swc4jAstDoWhileStmt node);

    /**
     * Visits an empty statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitEmptyStmt(Swc4jAstEmptyStmt node);

    /**
     * Visits an export all node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits an export all node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExportAll(Swc4jAstExportAll node);

    /**
     * Visits an export declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExportDecl(Swc4jAstExportDecl node);

    /**
     * Visits an export default declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExportDefaultDecl(Swc4jAstExportDefaultDecl node);

    /**
     * Visits an export default expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExportDefaultExpr(Swc4jAstExportDefaultExpr node);

    /**
     * Visits an export default specifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExportDefaultSpecifier(Swc4jAstExportDefaultSpecifier node);

    /**
     * Visits an export named specifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExportNamedSpecifier(Swc4jAstExportNamedSpecifier node);

    /**
     * Visits an export namespace specifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExportNamespaceSpecifier(Swc4jAstExportNamespaceSpecifier node);

    /**
     * Visits an expression or spread node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExprOrSpread(Swc4jAstExprOrSpread node);

    /**
     * Visits an expression statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitExprStmt(Swc4jAstExprStmt node);

    /**
     * Visits a function declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a function declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitFnDecl(Swc4jAstFnDecl node);

    /**
     * Visits a function expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitFnExpr(Swc4jAstFnExpr node);

    /**
     * Visits a for-in statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitForInStmt(Swc4jAstForInStmt node);

    /**
     * Visits a for-of statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitForOfStmt(Swc4jAstForOfStmt node);

    /**
     * Visits a for statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitForStmt(Swc4jAstForStmt node);

    /**
     * Visits a function node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitFunction(Swc4jAstFunction node);

    /**
     * Visits a getter property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitGetterProp(Swc4jAstGetterProp node);

    /**
     * Visits an identifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitIdent(Swc4jAstIdent node);

    /**
     * Visits an identifier name node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitIdentName(Swc4jAstIdentName node);

    /**
     * Visits an if statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits an if statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitIfStmt(Swc4jAstIfStmt node);

    /**
     * Visits an import node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitImport(Swc4jAstImport node);

    /**
     * Visits an import declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitImportDecl(Swc4jAstImportDecl node);

    /**
     * Visits an import default specifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitImportDefaultSpecifier(Swc4jAstImportDefaultSpecifier node);

    /**
     * Visits an import named specifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitImportNamedSpecifier(Swc4jAstImportNamedSpecifier node);

    /**
     * Visits an import star-as specifier node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitImportStarAsSpecifier(Swc4jAstImportStarAsSpecifier node);

    /**
     * Visits an invalid node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitInvalid(Swc4jAstInvalid node);

    /**
     * Visits a JSX attribute node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxAttr(Swc4jAstJsxAttr node);

    /**
     * Visits a JSX closing element node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxClosingElement(Swc4jAstJsxClosingElement node);

    /**
     * Visits a JSX closing fragment node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a JSX closing fragment node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxClosingFragment(Swc4jAstJsxClosingFragment node);

    /**
     * Visits a JSX element node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxElement(Swc4jAstJsxElement node);

    /**
     * Visits a JSX empty expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxEmptyExpr(Swc4jAstJsxEmptyExpr node);

    /**
     * Visits a JSX expression container node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxExprContainer(Swc4jAstJsxExprContainer node);

    /**
     * Visits a JSX fragment node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxFragment(Swc4jAstJsxFragment node);

    /**
     * Visits a JSX member expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxMemberExpr(Swc4jAstJsxMemberExpr node);

    /**
     * Visits a JSX namespaced name node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxNamespacedName(Swc4jAstJsxNamespacedName node);

    /**
     * Visits a JSX opening element node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxOpeningElement(Swc4jAstJsxOpeningElement node);

    /**
     * Visits a JSX opening fragment node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxOpeningFragment(Swc4jAstJsxOpeningFragment node);

    /**
     * Visits a JSX spread child node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a JSX spread child node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxSpreadChild(Swc4jAstJsxSpreadChild node);

    /**
     * Visits a JSX text node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitJsxText(Swc4jAstJsxText node);

    /**
     * Visits a key-value pattern property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitKeyValuePatProp(Swc4jAstKeyValuePatProp node);

    /**
     * Visits a key-value property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitKeyValueProp(Swc4jAstKeyValueProp node);

    /**
     * Visits a labeled statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitLabeledStmt(Swc4jAstLabeledStmt node);

    /**
     * Visits a member expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node);

    /**
     * Visits a meta property expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitMetaPropExpr(Swc4jAstMetaPropExpr node);

    /**
     * Visits a method property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitMethodProp(Swc4jAstMethodProp node);

    /**
     * Visits a module node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitModule(Swc4jAstModule node);

    /**
     * Visits a named export node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a named export node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitNamedExport(Swc4jAstNamedExport node);

    /**
     * Visits a new expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitNewExpr(Swc4jAstNewExpr node);

    /**
     * Visits a null literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitNull(Swc4jAstNull node);

    /**
     * Visits a number literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitNumber(Swc4jAstNumber node);

    /**
     * Visits an object literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitObjectLit(Swc4jAstObjectLit node);

    /**
     * Visits an object pattern node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitObjectPat(Swc4jAstObjectPat node);

    /**
     * Visits an optional call node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitOptCall(Swc4jAstOptCall node);

    /**
     * Visits an optional chain expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitOptChainExpr(Swc4jAstOptChainExpr node);

    /**
     * Visits a parameter node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitParam(Swc4jAstParam node);

    /**
     * Visits a parenthesized expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a parenthesized expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitParenExpr(Swc4jAstParenExpr node);

    /**
     * Visits a private method node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitPrivateMethod(Swc4jAstPrivateMethod node);

    /**
     * Visits a private name node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitPrivateName(Swc4jAstPrivateName node);

    /**
     * Visits a private property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitPrivateProp(Swc4jAstPrivateProp node);

    /**
     * Visits a regular expression literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitRegex(Swc4jAstRegex node);

    /**
     * Visits a rest pattern node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitRestPat(Swc4jAstRestPat node);

    /**
     * Visits a return statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitReturnStmt(Swc4jAstReturnStmt node);

    /**
     * Visits a script node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitScript(Swc4jAstScript node);

    /**
     * Visits a sequence expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitSeqExpr(Swc4jAstSeqExpr node);

    /**
     * Visits a setter property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a setter property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitSetterProp(Swc4jAstSetterProp node);

    /**
     * Visits a spread element node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitSpreadElement(Swc4jAstSpreadElement node);

    /**
     * Visits a static block node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitStaticBlock(Swc4jAstStaticBlock node);

    /**
     * Visits a string literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitStr(Swc4jAstStr node);

    /**
     * Visits a super node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitSuper(Swc4jAstSuper node);

    /**
     * Visits a super property expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitSuperPropExpr(Swc4jAstSuperPropExpr node);

    /**
     * Visits a switch case node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitSwitchCase(Swc4jAstSwitchCase node);

    /**
     * Visits a switch statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitSwitchStmt(Swc4jAstSwitchStmt node);

    /**
     * Visits a tagged template literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTaggedTpl(Swc4jAstTaggedTpl node);

    /**
     * Visits a this expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a this expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitThisExpr(Swc4jAstThisExpr node);

    /**
     * Visits a throw statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitThrowStmt(Swc4jAstThrowStmt node);

    /**
     * Visits a template literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTpl(Swc4jAstTpl node);

    /**
     * Visits a template element node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTplElement(Swc4jAstTplElement node);

    /**
     * Visits a try statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTryStmt(Swc4jAstTryStmt node);

    /**
     * Visits a TypeScript array type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsArrayType(Swc4jAstTsArrayType node);

    /**
     * Visits a TypeScript as expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsAsExpr(Swc4jAstTsAsExpr node);

    /**
     * Visits a TypeScript call signature declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsCallSignatureDecl(Swc4jAstTsCallSignatureDecl node);

    /**
     * Visits a TypeScript conditional type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsConditionalType(Swc4jAstTsConditionalType node);

    /**
     * Visits a TypeScript const assertion node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a TypeScript const assertion node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsConstAssertion(Swc4jAstTsConstAssertion node);

    /**
     * Visits a TypeScript construct signature declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsConstructSignatureDecl(Swc4jAstTsConstructSignatureDecl node);

    /**
     * Visits a TypeScript constructor type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsConstructorType(Swc4jAstTsConstructorType node);

    /**
     * Visits a TypeScript enum declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsEnumDecl(Swc4jAstTsEnumDecl node);

    /**
     * Visits a TypeScript enum member node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsEnumMember(Swc4jAstTsEnumMember node);

    /**
     * Visits a TypeScript export assignment node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsExportAssignment(Swc4jAstTsExportAssignment node);

    /**
     * Visits a TypeScript expression with type arguments node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsExprWithTypeArgs(Swc4jAstTsExprWithTypeArgs node);

    /**
     * Visits a TypeScript external module reference node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsExternalModuleRef(Swc4jAstTsExternalModuleRef node);

    /**
     * Visits a TypeScript function type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsFnType(Swc4jAstTsFnType node);

    /**
     * Visits a TypeScript getter signature node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a TypeScript getter signature node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsGetterSignature(Swc4jAstTsGetterSignature node);

    /**
     * Visits a TypeScript import call options node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsImportCallOptions(Swc4jAstTsImportCallOptions node);

    /**
     * Visits a TypeScript import equals declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsImportEqualsDecl(Swc4jAstTsImportEqualsDecl node);

    /**
     * Visits a TypeScript import type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsImportType(Swc4jAstTsImportType node);

    /**
     * Visits a TypeScript index signature node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsIndexSignature(Swc4jAstTsIndexSignature node);

    /**
     * Visits a TypeScript indexed access type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsIndexedAccessType(Swc4jAstTsIndexedAccessType node);

    /**
     * Visits a TypeScript infer type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsInferType(Swc4jAstTsInferType node);

    /**
     * Visits a TypeScript instantiation node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsInstantiation(Swc4jAstTsInstantiation node);

    /**
     * Visits a TypeScript interface body node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsInterfaceBody(Swc4jAstTsInterfaceBody node);

    /**
     * Visits a TypeScript interface declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a TypeScript interface declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsInterfaceDecl(Swc4jAstTsInterfaceDecl node);

    /**
     * Visits a TypeScript intersection type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsIntersectionType(Swc4jAstTsIntersectionType node);

    /**
     * Visits a TypeScript keyword type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsKeywordType(Swc4jAstTsKeywordType node);

    /**
     * Visits a TypeScript literal type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsLitType(Swc4jAstTsLitType node);

    /**
     * Visits a TypeScript mapped type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsMappedType(Swc4jAstTsMappedType node);

    /**
     * Visits a TypeScript method signature node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsMethodSignature(Swc4jAstTsMethodSignature node);

    /**
     * Visits a TypeScript module block node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsModuleBlock(Swc4jAstTsModuleBlock node);

    /**
     * Visits a TypeScript module declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsModuleDecl(Swc4jAstTsModuleDecl node);

    /**
     * Visits a TypeScript namespace declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsNamespaceDecl(Swc4jAstTsNamespaceDecl node);

    /**
     * Visits a TypeScript namespace export declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a TypeScript namespace export declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsNamespaceExportDecl(Swc4jAstTsNamespaceExportDecl node);

    /**
     * Visits a TypeScript non-null expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsNonNullExpr(Swc4jAstTsNonNullExpr node);

    /**
     * Visits a TypeScript optional type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsOptionalType(Swc4jAstTsOptionalType node);

    /**
     * Visits a TypeScript parameter property node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsParamProp(Swc4jAstTsParamProp node);

    /**
     * Visits a TypeScript parenthesized type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsParenthesizedType(Swc4jAstTsParenthesizedType node);

    /**
     * Visits a TypeScript property signature node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsPropertySignature(Swc4jAstTsPropertySignature node);

    /**
     * Visits a TypeScript qualified name node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsQualifiedName(Swc4jAstTsQualifiedName node);

    /**
     * Visits a TypeScript rest type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsRestType(Swc4jAstTsRestType node);

    /**
     * Visits a TypeScript satisfies expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsSatisfiesExpr(Swc4jAstTsSatisfiesExpr node);

    /**
     * Visits a TypeScript setter signature node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a TypeScript setter signature node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsSetterSignature(Swc4jAstTsSetterSignature node);

    /**
     * Visits a TypeScript this type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsThisType(Swc4jAstTsThisType node);

    /**
     * Visits a TypeScript template literal type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTplLitType(Swc4jAstTsTplLitType node);

    /**
     * Visits a TypeScript tuple element node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTupleElement(Swc4jAstTsTupleElement node);

    /**
     * Visits a TypeScript tuple type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTupleType(Swc4jAstTsTupleType node);

    /**
     * Visits a TypeScript type alias declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeAliasDecl(Swc4jAstTsTypeAliasDecl node);

    /**
     * Visits a TypeScript type annotation node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeAnn(Swc4jAstTsTypeAnn node);

    /**
     * Visits a TypeScript type assertion node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeAssertion(Swc4jAstTsTypeAssertion node);

    /**
     * Visits a TypeScript type literal node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeLit(Swc4jAstTsTypeLit node);

    /**
     * Visits a TypeScript type operator node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits a TypeScript type operator node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeOperator(Swc4jAstTsTypeOperator node);

    /**
     * Visits a TypeScript type parameter node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeParam(Swc4jAstTsTypeParam node);

    /**
     * Visits a TypeScript type parameter declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeParamDecl(Swc4jAstTsTypeParamDecl node);

    /**
     * Visits a TypeScript type parameter instantiation node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeParamInstantiation(Swc4jAstTsTypeParamInstantiation node);

    /**
     * Visits a TypeScript type predicate node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypePredicate(Swc4jAstTsTypePredicate node);

    /**
     * Visits a TypeScript type query node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeQuery(Swc4jAstTsTypeQuery node);

    /**
     * Visits a TypeScript type reference node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsTypeRef(Swc4jAstTsTypeRef node);

    /**
     * Visits a TypeScript union type node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitTsUnionType(Swc4jAstTsUnionType node);

    /**
     * Visits a unary expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node);

    /**
     * Visits an update expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    /**
     * Visits an update expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitUpdateExpr(Swc4jAstUpdateExpr node);

    /**
     * Visits a using declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitUsingDecl(Swc4jAstUsingDecl node);

    /**
     * Visits a variable declaration node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitVarDecl(Swc4jAstVarDecl node);

    /**
     * Visits a variable declarator node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitVarDeclarator(Swc4jAstVarDeclarator node);

    /**
     * Visits a while statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitWhileStmt(Swc4jAstWhileStmt node);

    /**
     * Visits a with statement node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitWithStmt(Swc4jAstWithStmt node);

    /**
     * Visits a yield expression node.
     *
     * @param node the AST node
     * @return the visitor response
     */
    Swc4jAstVisitorResponse visitYieldExpr(Swc4jAstYieldExpr node);
}
