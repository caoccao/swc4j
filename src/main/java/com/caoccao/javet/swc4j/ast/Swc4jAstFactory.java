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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.enums.*;
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
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.utils.Swc4jSpan;

import java.util.List;

/**
 * The type Swc4j ast factory.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public final class Swc4jAstFactory {
    private Swc4jAstFactory() {
    }

    @Jni2RustMethod
    public static Swc4jAstArrayLit createArrayLit(
            List<Swc4jAstExprOrSpread> elems,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstArrayLit(elems, span);
    }

    @Jni2RustMethod
    public static Swc4jAstArrayPat createArrayPat(
            List<ISwc4jAstPat> elems,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstArrayPat(elems, optional, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstArrowExpr createArrowExpr(
            List<ISwc4jAstPat> params,
            ISwc4jAstBlockStmtOrExpr body,
            boolean isAsync,
            boolean generator,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn returnType,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstArrowExpr(params, body, isAsync, generator, typeParams, returnType, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignExpr createAssignExpr(
            int opId,
            ISwc4jAstAssignTarget left,
            ISwc4jAstExpr right,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstAssignExpr(Swc4jAstAssignOp.parse(opId), left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignPat createAssignPat(
            ISwc4jAstPat left,
            ISwc4jAstExpr right,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstAssignPat(left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignPatProp createAssignPatProp(
            Swc4jAstBindingIdent key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstAssignPatProp(key, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignProp createAssignProp(
            Swc4jAstIdent key,
            ISwc4jAstExpr value,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstAssignProp(key, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAutoAccessor createAutoAccessor(
            ISwc4jAstKey key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean isStatic,
            List<Swc4jAstDecorator> decorators,
            int accessibilityId,
            boolean isOverride,
            boolean definite,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstAutoAccessor(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isOverride, definite, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAwaitExpr createAwaitExpr(
            ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstAwaitExpr(arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBigInt createBigInt(
            int signId,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstBigInt(Swc4jAstBigIntSign.parse(signId), raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBinExpr createBinExpr(
            int opId,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstBinExpr(Swc4jAstBinaryOp.parse(opId), left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBindingIdent createBindingIdent(
            Swc4jAstIdent id,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstBindingIdent(id, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBlockStmt createBlockStmt(
            List<ISwc4jAstStmt> stmts,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstBlockStmt(stmts, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBool createBool(
            boolean value,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstBool(value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBreakStmt createBreakStmt(
            @Jni2RustParam(optional = true) Swc4jAstIdent label,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstBreakStmt(label, span);
    }

    @Jni2RustMethod
    public static Swc4jAstCallExpr createCallExpr(
            ISwc4jAstCallee callee,
            List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstCallExpr(callee, args, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstCatchClause createCatchClause(
            @Jni2RustParam(optional = true) ISwc4jAstPat param,
            Swc4jAstBlockStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstCatchClause(param, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClass createClass(
            List<Swc4jAstDecorator> decorators,
            List<ISwc4jAstClassMember> body,
            @Jni2RustParam(optional = true) ISwc4jAstExpr superClass,
            boolean isAbstract,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation superTypeParams,
            List<Swc4jAstTsExprWithTypeArgs> _implements,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstClass(decorators, body, superClass, isAbstract, typeParams,
                superTypeParams, _implements, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClassDecl createClassDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstClass clazz,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstClassDecl(ident, declare, clazz, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClassExpr createClassExpr(
            @Jni2RustParam(optional = true) Swc4jAstIdent ident,
            Swc4jAstClass clazz,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstClassExpr(ident, clazz, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClassMethod createClassMethod(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            int kindId,
            boolean isStatic,
            int accessibilityId,
            boolean isAbstract,
            boolean optional,
            boolean isOverride,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstClassMethod(
                key, function, Swc4jAstMethodKind.parse(kindId), isStatic,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isAbstract, optional, isOverride, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClassProp createClassProp(
            ISwc4jAstPropName key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean isStatic,
            List<Swc4jAstDecorator> decorators,
            int accessibilityId,
            boolean isAbstract,
            boolean isOptional,
            boolean isOverride,
            boolean readonly,
            boolean declare,
            boolean definite,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstClassProp(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isAbstract, isOptional, isOverride, readonly, declare, definite,
                span);
    }

    @Jni2RustMethod
    public static Swc4jAstComputedPropName createComputedPropName(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstComputedPropName(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstCondExpr createCondExpr(
            ISwc4jAstExpr test,
            ISwc4jAstExpr cons,
            ISwc4jAstExpr alt,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstCondExpr(test, cons, alt, span);
    }

    @Jni2RustMethod
    public static Swc4jAstConstructor createConstructor(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            int accessibilityId,
            boolean optional,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstConstructor(
                key, params, body,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                optional, span);
    }

    @Jni2RustMethod
    public static Swc4jAstContinueStmt createContinueStmt(
            @Jni2RustParam(optional = true) Swc4jAstIdent label,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstContinueStmt(label, span);
    }

    @Jni2RustMethod
    public static Swc4jAstDebuggerStmt createDebuggerStmt(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstDebuggerStmt(span);
    }

    @Jni2RustMethod
    public static Swc4jAstDecorator createDecorator(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstDecorator(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstDoWhileStmt createDoWhileStmt(
            ISwc4jAstExpr test,
            ISwc4jAstStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstDoWhileStmt(test, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstEmptyStmt createEmptyStmt(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstEmptyStmt(span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportAll createExportAll(
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExportAll(src, typeOnly, with, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDecl createExportDecl(
            ISwc4jAstDecl decl,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExportDecl(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDefaultDecl createExportDefaultDecl(
            ISwc4jAstDefaultDecl decl,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExportDefaultDecl(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDefaultExpr createExportDefaultExpr(
            ISwc4jAstExpr decl,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExportDefaultExpr(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDefaultSpecifier createExportDefaultSpecifier(
            Swc4jAstIdent exported,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExportDefaultSpecifier(exported, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportNamedSpecifier createExportNamedSpecifier(
            ISwc4jAstModuleExportName orig,
            @Jni2RustParam(optional = true) ISwc4jAstModuleExportName exported,
            boolean typeOnly,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExportNamedSpecifier(orig, exported, typeOnly, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportNamespaceSpecifier createExportNamespaceSpecifier(
            ISwc4jAstModuleExportName name,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExportNamespaceSpecifier(name, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExprOrSpread createExprOrSpread(
            @Jni2RustParam(optional = true) Swc4jSpan spread,
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExprOrSpread(spread, expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExprStmt createExprStmt(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstExprStmt(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstFnDecl createFnDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstFunction function,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstFnDecl(ident, declare, function, span);
    }

    @Jni2RustMethod
    public static Swc4jAstFnExpr createFnExpr(
            @Jni2RustParam(optional = true) Swc4jAstIdent ident,
            Swc4jAstFunction function,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstFnExpr(ident, function, span);
    }

    @Jni2RustMethod
    public static Swc4jAstForInStmt createForInStmt(
            ISwc4jAstForHead left,
            ISwc4jAstExpr right,
            ISwc4jAstStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstForInStmt(left, right, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstForOfStmt createForOfStmt(
            boolean isAwait,
            ISwc4jAstForHead left,
            ISwc4jAstExpr right,
            ISwc4jAstStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstForOfStmt(isAwait, left, right, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstForStmt createForStmt(
            @Jni2RustParam(optional = true) ISwc4jAstVarDeclOrExpr init,
            @Jni2RustParam(optional = true) ISwc4jAstExpr test,
            @Jni2RustParam(optional = true) ISwc4jAstExpr update,
            ISwc4jAstStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstForStmt(init, test, update, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstFunction createFunction(
            List<Swc4jAstParam> params,
            List<Swc4jAstDecorator> decorators,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            boolean generator,
            boolean isAsync,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn returnType,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstFunction(
                params, decorators, body, generator, isAsync, typeParams, returnType, span);
    }

    @Jni2RustMethod
    public static Swc4jAstGetterProp createGetterProp(
            ISwc4jAstPropName key,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstGetterProp(key, typeAnn, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstIdent createIdent(
            String sym,
            boolean optional,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstIdent(sym, optional, span);
    }

    @Jni2RustMethod
    public static Swc4jAstIfStmt createIfStmt(
            ISwc4jAstExpr test,
            ISwc4jAstStmt cons,
            @Jni2RustParam(optional = true) ISwc4jAstStmt alt,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstIfStmt(test, cons, alt, span);
    }

    @Jni2RustMethod
    public static Swc4jAstImport createImport(
            int phaseId,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstImport(Swc4jAstImportPhase.parse(phaseId), span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportDecl createImportDecl(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            int phaseId,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstImportDecl(specifiers, src, typeOnly, with, Swc4jAstImportPhase.parse(phaseId), span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportDefaultSpecifier createImportDefaultSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstImportDefaultSpecifier(local, span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportNamedSpecifier createImportNamedSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam(optional = true) ISwc4jAstModuleExportName imported,
            boolean typeOnly,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstImportNamedSpecifier(local, imported, typeOnly, span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportStarAsSpecifier createImportStarAsSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstImportStarAsSpecifier(local, span);
    }

    @Jni2RustMethod
    public static Swc4jAstInvalid createInvalid(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstInvalid(span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxAttr createJsxAttr(
            ISwc4jAstJsxAttrName name,
            @Jni2RustParam(optional = true) ISwc4jAstJsxAttrValue value,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxAttr(name, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxClosingElement createJsxClosingElement(
            ISwc4jAstJsxElementName name,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxClosingElement(name, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxClosingFragment createJsxClosingFragment(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxClosingFragment(span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxElement createJsxElement(
            Swc4jAstJsxOpeningElement opening,
            List<ISwc4jAstJsxElementChild> children,
            @Jni2RustParam(optional = true) Swc4jAstJsxClosingElement closing,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxElement(opening, children, closing, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxEmptyExpr createJsxEmptyExpr(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxEmptyExpr(span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxExprContainer createJsxExprContainer(
            ISwc4jAstJsxExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxExprContainer(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxFragment createJsxFragment(
            Swc4jAstJsxOpeningFragment opening,
            List<ISwc4jAstJsxElementChild> children,
            Swc4jAstJsxClosingFragment closing,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxFragment(opening, children, closing, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxMemberExpr createJsxMemberExpr(
            ISwc4jAstJsxObject obj,
            Swc4jAstIdent prop,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxMemberExpr(obj, prop, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxNamespacedName createJsxNamespacedName(
            Swc4jAstIdent ns,
            Swc4jAstIdent name,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxNamespacedName(ns, name, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxOpeningElement createJsxOpeningElement(
            ISwc4jAstJsxElementName name,
            List<ISwc4jAstJsxAttrOrSpread> attrs,
            boolean selfClosing,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxOpeningElement(name, attrs, selfClosing, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxOpeningFragment createJsxOpeningFragment(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxOpeningFragment(span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxSpreadChild createJsxSpreadChild(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxSpreadChild(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxText createJsxText(
            String value,
            String raw,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstJsxText(value, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstKeyValuePatProp createKeyValuePatProp(
            ISwc4jAstPropName key,
            ISwc4jAstPat value,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstKeyValuePatProp(key, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstKeyValueProp createKeyValueProp(
            ISwc4jAstPropName key,
            ISwc4jAstExpr value,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstKeyValueProp(key, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstLabeledStmt createLabeledStmt(
            Swc4jAstIdent label,
            ISwc4jAstStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstLabeledStmt(label, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstMemberExpr createMemberExpr(
            ISwc4jAstExpr obj,
            ISwc4jAstMemberProp prop,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstMemberExpr(obj, prop, span);
    }

    @Jni2RustMethod
    public static Swc4jAstMetaPropExpr createMetaPropExpr(
            int kindId,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstMetaPropExpr(Swc4jAstMetaPropKind.parse(kindId), span);
    }

    @Jni2RustMethod
    public static Swc4jAstMethodProp createMethodProp(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstMethodProp(key, function, span);
    }

    @Jni2RustMethod
    public static Swc4jAstModule createModule(
            List<ISwc4jAstModuleItem> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstModule(body, shebang, span);
    }

    @Jni2RustMethod
    public static Swc4jAstNamedExport createNamedExport(
            List<ISwc4jAstExportSpecifier> specifiers,
            @Jni2RustParam(optional = true) Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstNamedExport(specifiers, src, typeOnly, with, span);
    }

    @Jni2RustMethod
    public static Swc4jAstNewExpr createNewExpr(
            ISwc4jAstExpr callee,
            @Jni2RustParam(optional = true) List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstNewExpr(callee, args, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstNull createNull(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstNull(span);
    }

    @Jni2RustMethod
    public static Swc4jAstNumber createNumber(
            double value,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstNumber(value, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstObjectLit createObjectLit(
            List<ISwc4jAstPropOrSpread> props,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstObjectLit(props, span);
    }

    @Jni2RustMethod
    public static Swc4jAstObjectPat createObjectPat(
            List<ISwc4jAstObjectPatProp> props,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstObjectPat(props, optional, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstOptCall createOptCall(
            ISwc4jAstExpr callee,
            List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstOptCall(callee, args, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstOptChainExpr createOptChainExpr(
            boolean optional,
            ISwc4jAstOptChainBase base,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstOptChainExpr(optional, base, span);
    }

    @Jni2RustMethod
    public static Swc4jAstParam createParam(
            List<Swc4jAstDecorator> decorators,
            ISwc4jAstPat pat,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstParam(decorators, pat, span);
    }

    @Jni2RustMethod
    public static Swc4jAstParenExpr createParenExpr(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstParenExpr(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstPrivateMethod createPrivateMethod(
            Swc4jAstPrivateName key,
            Swc4jAstFunction function,
            int kindId,
            boolean isStatic,
            int accessibilityId,
            boolean isAbstract,
            boolean optional,
            boolean isOverride,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstPrivateMethod(
                key, function, Swc4jAstMethodKind.parse(kindId), isStatic,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isAbstract, optional, isOverride, span);
    }

    @Jni2RustMethod
    public static Swc4jAstPrivateName createPrivateName(
            Swc4jAstIdent id,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstPrivateName(id, span);
    }

    @Jni2RustMethod
    public static Swc4jAstPrivateProp createPrivateProp(
            Swc4jAstPrivateName key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean isStatic,
            List<Swc4jAstDecorator> decorators,
            int accessibilityId,
            boolean isOptional,
            boolean isOverride,
            boolean readonly,
            boolean definite,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstPrivateProp(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isOptional, isOverride, readonly, definite,
                span);
    }

    @Jni2RustMethod
    public static Swc4jAstRegex createRegex(
            String exp,
            String flags,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstRegex(exp, flags, span);
    }

    @Jni2RustMethod
    public static Swc4jAstRestPat createRestPat(
            Swc4jSpan dot3Token,
            ISwc4jAstPat arg,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstRestPat(dot3Token, arg, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstReturnStmt createReturnStmt(
            @Jni2RustParam(optional = true) ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstReturnStmt(arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstScript createScript(
            List<ISwc4jAstStmt> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstScript(body, shebang, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSeqExpr createSeqExpr(
            List<ISwc4jAstExpr> exprs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstSeqExpr(exprs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSetterProp createSetterProp(
            ISwc4jAstPropName key,
            @Jni2RustParam(optional = true) ISwc4jAstPat thisParam,
            ISwc4jAstPat param,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstSetterProp(key, thisParam, param, body, span);
    }

    @Jni2RustMethod
    public static Swc4jSpan createSpan(
            @Jni2RustParamSpanStart int start,
            @Jni2RustParamSpanEnd int end,
            @Jni2RustParamSpanLine int line,
            @Jni2RustParamSpanColumn int column) {
        return new Swc4jSpan(start, end, line, column);
    }

    @Jni2RustMethod
    public static Swc4jAstSpreadElement createSpreadElement(
            Swc4jSpan dot3Token,
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstSpreadElement(dot3Token, expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstStaticBlock createStaticBlock(
            Swc4jAstBlockStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstStaticBlock(body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstStr createStr(
            String value,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstStr(value, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSuper createSuper(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstSuper(span);
    }

    @Jni2RustMethod
    public static Swc4jAstSuperPropExpr createSuperPropExpr(
            Swc4jAstSuper obj,
            ISwc4jAstSuperProp prop,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstSuperPropExpr(obj, prop, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSwitchCase createSwitchCase(
            @Jni2RustParam(optional = true) ISwc4jAstExpr test,
            List<ISwc4jAstStmt> cons,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstSwitchCase(test, cons, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSwitchStmt createSwitchStmt(
            ISwc4jAstExpr discriminant,
            List<Swc4jAstSwitchCase> cases,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstSwitchStmt(discriminant, cases, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTaggedTpl createTaggedTpl(
            ISwc4jAstExpr tag,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeParams,
            Swc4jAstTpl tpl,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTaggedTpl(tag, typeParams, tpl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstThisExpr createThisExpr(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstThisExpr(span);
    }

    @Jni2RustMethod
    public static Swc4jAstThrowStmt createThrowStmt(
            ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstThrowStmt(arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTpl createTpl(
            List<ISwc4jAstExpr> exprs,
            List<Swc4jAstTplElement> quasis,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTpl(exprs, quasis, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTplElement createTplElement(
            boolean tail,
            @Jni2RustParam(optional = true) String cooked,
            String raw,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTplElement(tail, cooked, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTryStmt createTryStmt(
            Swc4jAstBlockStmt block,
            @Jni2RustParam(optional = true) Swc4jAstCatchClause handler,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt finalizer,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTryStmt(block, handler, finalizer, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsArrayType createTsArrayType(
            ISwc4jAstTsType elemType,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsArrayType(elemType, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsAsExpr createTsAsExpr(
            ISwc4jAstExpr expr,
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsAsExpr(expr, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsCallSignatureDecl createTsCallSignatureDecl(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsCallSignatureDecl(params, typeAnn, typeParams, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsConditionalType createTsConditionalType(
            ISwc4jAstTsType checkType,
            ISwc4jAstTsType extendsType,
            ISwc4jAstTsType trueType,
            ISwc4jAstTsType falseType,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsConditionalType(checkType, extendsType, trueType, falseType, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsConstAssertion createTsConstAssertion(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsConstAssertion(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsConstructSignatureDecl createTsConstructSignatureDecl(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsConstructSignatureDecl(params, typeAnn, typeParams, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsConstructorType createTsConstructorType(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn typeAnn,
            boolean isAbstract,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsConstructorType(params, typeParams, typeAnn, isAbstract, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsEnumDecl createTsEnumDecl(
            boolean declare,
            boolean isConst,
            Swc4jAstIdent id,
            List<Swc4jAstTsEnumMember> members,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsEnumDecl(declare, isConst, id, members, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsEnumMember createTsEnumMember(
            ISwc4jAstTsEnumMemberId id,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsEnumMember(id, init, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExportAssignment createTsExportAssignment(
            ISwc4jAstExpr decl,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsExportAssignment(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExprWithTypeArgs createTsExprWithTypeArgs(
            ISwc4jAstExpr expr,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsExprWithTypeArgs(expr, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExternalModuleRef createTsExternalModuleRef(
            Swc4jAstStr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsExternalModuleRef(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsFnType createTsFnType(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsFnType(params, typeParams, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsGetterSignature createTsGetterSignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsGetterSignature(readonly, key, computed, optional, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsImportEqualsDecl createTsImportEqualsDecl(
            boolean export,
            boolean typeOnly,
            Swc4jAstIdent id,
            ISwc4jAstTsModuleRef moduleRef,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsImportEqualsDecl(export, typeOnly, id, moduleRef, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsImportType createTsImportType(
            Swc4jAstStr arg,
            @Jni2RustParam(optional = true) ISwc4jAstTsEntityName qualifier,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsImportType(arg, qualifier, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsIndexSignature createTsIndexSignature(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean readonly,
            boolean isStatic,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsIndexSignature(params, typeAnn, readonly, isStatic, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsIndexedAccessType createTsIndexedAccessType(
            boolean readonly,
            ISwc4jAstTsType objType,
            ISwc4jAstTsType indexType,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsIndexedAccessType(readonly, objType, indexType, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsInferType createTsInferType(
            Swc4jAstTsTypeParam typeParam,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsInferType(typeParam, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsInstantiation createTsInstantiation(
            ISwc4jAstExpr expr,
            Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsInstantiation(expr, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsInterfaceBody createTsInterfaceBody(
            List<ISwc4jAstTsTypeElement> body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsInterfaceBody(body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsInterfaceDecl createTsInterfaceDecl(
            Swc4jAstIdent id,
            boolean declare,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            List<Swc4jAstTsExprWithTypeArgs> _extends,
            Swc4jAstTsInterfaceBody body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsInterfaceDecl(id, declare, typeParams, _extends, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsIntersectionType createTsIntersectionType(
            List<ISwc4jAstTsType> types,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsIntersectionType(types, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsKeywordType createTsKeywordType(
            int kindId,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsKeywordType(Swc4jAstTsKeywordTypeKind.parse(kindId), span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsLitType createTsLitType(
            ISwc4jAstTsLit lit,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsLitType(lit, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsMappedType createTsMappedType(
            int readonlyId,
            Swc4jAstTsTypeParam typeParam,
            @Jni2RustParam(optional = true) ISwc4jAstTsType nameType,
            int optionalId,
            @Jni2RustParam(optional = true) ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsMappedType(
                readonlyId >= 0 ? Swc4jAstTruePlusMinus.parse(readonlyId) : null,
                typeParam, nameType,
                optionalId >= 0 ? Swc4jAstTruePlusMinus.parse(optionalId) : null,
                typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsMethodSignature createTsMethodSignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsMethodSignature(readonly, key, computed, optional, params, typeAnn, typeParams, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsModuleBlock createTsModuleBlock(
            List<ISwc4jAstModuleItem> body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsModuleBlock(body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsModuleDecl createTsModuleDecl(
            boolean declare,
            boolean global,
            ISwc4jAstTsModuleName id,
            @Jni2RustParam(optional = true) ISwc4jAstTsNamespaceBody body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsModuleDecl(declare, global, id, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsNamespaceDecl createTsNamespaceDecl(
            boolean declare,
            boolean global,
            Swc4jAstIdent id,
            ISwc4jAstTsNamespaceBody body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsNamespaceDecl(declare, global, id, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsNamespaceExportDecl createTsNamespaceExportDecl(
            Swc4jAstIdent id,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsNamespaceExportDecl(id, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsNonNullExpr createTsNonNullExpr(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsNonNullExpr(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsOptionalType createTsOptionalType(
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsOptionalType(typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsParamProp createTsParamProp(
            List<Swc4jAstDecorator> decorators,
            int accessibilityId,
            boolean isOverride,
            boolean readonly,
            ISwc4jAstTsParamPropParam param,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsParamProp(
                decorators, accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isOverride, readonly, param, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsParenthesizedType createTsParenthesizedType(
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsParenthesizedType(typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsPropertySignature createTsPropertySignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsPropertySignature(readonly, key, computed, optional, init, params, typeAnn, typeParams, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsQualifiedName createTsQualifiedName(
            ISwc4jAstTsEntityName left,
            Swc4jAstIdent right,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsQualifiedName(left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsRestType createTsRestType(
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsRestType(typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsSatisfiesExpr createTsSatisfiesExpr(
            ISwc4jAstExpr expr,
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsSatisfiesExpr(expr, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsSetterSignature createTsSetterSignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            ISwc4jAstTsFnParam param,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsSetterSignature(readonly, key, computed, optional, param, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsThisType createTsThisType(
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsThisType(span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTplLitType createTsTplLitType(
            List<ISwc4jAstTsType> types,
            List<Swc4jAstTplElement> quasis,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTplLitType(types, quasis, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTupleElement createTsTupleElement(
            @Jni2RustParam(optional = true) ISwc4jAstPat label,
            ISwc4jAstTsType ty,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTupleElement(label, ty, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTupleType createTsTupleType(
            List<Swc4jAstTsTupleElement> elemTypes,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTupleType(elemTypes, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeAliasDecl createTsTypeAliasDecl(
            Swc4jAstIdent id,
            boolean declare,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeAliasDecl(id, declare, typeParams, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeAnn createTsTypeAnn(
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeAnn(typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeAssertion createTsTypeAssertion(
            ISwc4jAstExpr expr,
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeAssertion(expr, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeLit createTsTypeLit(
            List<ISwc4jAstTsTypeElement> members,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeLit(members, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeOperator createTsTypeOperator(
            int opId,
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeOperator(Swc4jAstTsTypeOperatorOp.parse(opId), typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParam createTsTypeParam(
            Swc4jAstIdent name,
            boolean isIn,
            boolean isOut,
            boolean isConst,
            @Jni2RustParam(optional = true) ISwc4jAstTsType constraint,
            @Jni2RustParam(optional = true) ISwc4jAstTsType _default,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeParam(name, isIn, isOut, isConst, constraint, _default, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParamDecl createTsTypeParamDecl(
            List<Swc4jAstTsTypeParam> params,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeParamDecl(params, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParamInstantiation createTsTypeParamInstantiation(
            List<ISwc4jAstTsType> params,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeParamInstantiation(params, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypePredicate createTsTypePredicate(
            boolean asserts,
            ISwc4jAstTsThisTypeOrIdent paramName,
            @Jni2RustParam(optional = true) ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypePredicate(asserts, paramName, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeQuery createTsTypeQuery(
            ISwc4jAstTsTypeQueryExpr exprName,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeQuery(exprName, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeRef createTsTypeRef(
            ISwc4jAstTsEntityName typeName,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeParams,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsTypeRef(typeName, typeParams, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsUnionType createTsUnionType(
            List<ISwc4jAstTsType> types,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstTsUnionType(types, span);
    }

    @Jni2RustMethod
    public static Swc4jAstUnaryExpr createUnaryExpr(
            int opId,
            ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstUnaryExpr(Swc4jAstUnaryOp.parse(opId), arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstUpdateExpr createUpdateExpr(
            int opId,
            boolean prefix,
            ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstUpdateExpr(Swc4jAstUpdateOp.parse(opId), prefix, arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstUsingDecl createUsingDecl(
            boolean isAwait,
            List<Swc4jAstVarDeclarator> decls,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstUsingDecl(isAwait, decls, span);
    }

    @Jni2RustMethod
    public static Swc4jAstVarDecl createVarDecl(
            int kindId,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstVarDecl(Swc4jAstVarDeclKind.parse(kindId), declare, decls, span);
    }

    @Jni2RustMethod
    public static Swc4jAstVarDeclarator createVarDeclarator(
            ISwc4jAstPat name,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            boolean definite,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstVarDeclarator(name, init, definite, span);
    }

    @Jni2RustMethod
    public static Swc4jAstWhileStmt createWhileStmt(
            ISwc4jAstExpr test,
            ISwc4jAstStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstWhileStmt(test, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstWithStmt createWithStmt(
            ISwc4jAstExpr obj,
            ISwc4jAstStmt body,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstWithStmt(obj, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstYieldExpr createYieldExpr(
            @Jni2RustParam(optional = true) ISwc4jAstExpr arg,
            boolean delegate,
            @Jni2RustParam Swc4jSpan span) {
        return new Swc4jAstYieldExpr(arg, delegate, span);
    }
}
