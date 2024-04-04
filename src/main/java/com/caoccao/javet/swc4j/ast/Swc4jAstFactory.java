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
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstTplElement;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.*;
import com.caoccao.javet.swc4j.jni2rust.*;

import java.util.List;

/**
 * The type Swc4j ast factory.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = "rust/src/ast_utils.rs")
public final class Swc4jAstFactory {
    private Swc4jAstFactory() {
    }

    @Jni2RustMethod
    public static Swc4jAstArrayLit createArrayLit(
            List<Swc4jAstExprOrSpread> elems,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstArrayLit(elems, span);
    }

    @Jni2RustMethod
    public static Swc4jAstArrayPat createArrayPat(
            List<ISwc4jAstPat> elems,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jAstSpan span) {
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
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstArrowExpr(params, body, isAsync, generator, typeParams, returnType, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignExpr createAssignExpr(
            int op,
            ISwc4jAstPatOrExpr left,
            ISwc4jAstExpr right,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstAssignExpr(Swc4jAstAssignOp.parse(op), left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignPat createAssignPat(
            ISwc4jAstPat left,
            ISwc4jAstExpr right,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstAssignPat(left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignPatProp createAssignPatProp(
            Swc4jAstIdent key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstAssignPatProp(key, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstAssignProp createAssignProp(
            Swc4jAstIdent key,
            ISwc4jAstExpr value,
            @Jni2RustParam Swc4jAstSpan span) {
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
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstAutoAccessor(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                span);
    }

    @Jni2RustMethod
    public static Swc4jAstAwaitExpr createAwaitExpr(
            ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstAwaitExpr(arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBigInt createBigInt(
            int sign,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstBigInt(Swc4jAstBigIntSign.parse(sign), raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBinExpr createBinExpr(
            int op,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstBinExpr(Swc4jAstBinaryOp.parse(op), left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBindingIdent createBindingIdent(
            Swc4jAstIdent id,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstBindingIdent(id, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBlockStmt createBlockStmt(
            List<ISwc4jAstStmt> stmts,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstBlockStmt(stmts, span);
    }

    @Jni2RustMethod
    public static Swc4jAstBool createBool(
            boolean value,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstBool(value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstCallExpr createCallExpr(
            ISwc4jAstCallee callee,
            List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstCallExpr(callee, args, typeArgs, span);
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
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstClass(decorators, body, superClass, isAbstract, typeParams,
                superTypeParams, _implements, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClassDecl createClassDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstClass clazz,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstClassDecl(ident, declare, clazz, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClassExpr createClassExpr(
            @Jni2RustParam(optional = true) Swc4jAstIdent ident,
            Swc4jAstClass clazz,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstClassExpr(ident, clazz, span);
    }

    @Jni2RustMethod
    public static Swc4jAstClassMethod createClassMethod(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            int kind,
            boolean isStatic,
            int accessibilityId,
            boolean isAbstract,
            boolean optional,
            boolean isOverride,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstClassMethod(
                key, function, Swc4jAstMethodKind.parse(kind), isStatic,
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
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstClassProp(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isAbstract, isOptional, isOverride, readonly, declare, definite,
                span);
    }

    @Jni2RustMethod
    public static Swc4jAstComputedPropName createComputedPropName(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstComputedPropName(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstCondExpr createCondExpr(
            ISwc4jAstExpr test,
            ISwc4jAstExpr cons,
            ISwc4jAstExpr alt,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstCondExpr(test, cons, alt, span);
    }

    @Jni2RustMethod
    public static Swc4jAstConstructor createConstructor(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            int accessibilityId,
            boolean optional,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstConstructor(
                key, params, body,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                optional, span);
    }

    @Jni2RustMethod
    public static Swc4jAstDebuggerStmt createDebuggerStmt(
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstDebuggerStmt(span);
    }

    @Jni2RustMethod
    public static Swc4jAstDecorator createDecorator(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstDecorator(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstEmptyStmt createEmptyStmt(
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstEmptyStmt(span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportAll createExportAll(
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstExportAll(src, typeOnly, with, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDecl createExportDecl(
            ISwc4jAstDecl decl,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstExportDecl(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDefaultDecl createExportDefaultDecl(
            ISwc4jAstDefaultDecl decl,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstExportDefaultDecl(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDefaultExpr createExportDefaultExpr(
            ISwc4jAstExpr decl,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstExportDefaultExpr(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExprOrSpread createExprOrSpread(
            @Jni2RustParam(optional = true) Swc4jAstSpan spread,
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstExprOrSpread(spread, expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstExprStmt createExprStmt(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstExprStmt(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstFnDecl createFnDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstFunction function,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstFnDecl(ident, declare, function, span);
    }

    @Jni2RustMethod
    public static Swc4jAstFnExpr createFnExpr(
            @Jni2RustParam(optional = true) Swc4jAstIdent ident,
            Swc4jAstFunction function,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstFnExpr(ident, function, span);
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
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstFunction(
                params, decorators, body, generator, isAsync, typeParams, returnType, span);
    }

    @Jni2RustMethod
    public static Swc4jAstGetterProp createGetterProp(
            ISwc4jAstPropName key,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstGetterProp(key, typeAnn, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstIdent createIdent(
            String sym,
            boolean optional,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstIdent(sym, optional, span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportDecl createImportDecl(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstImportDecl(specifiers, src, typeOnly, with, span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportDefaultSpecifier createImportDefaultSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstImportDefaultSpecifier(local, span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportNamedSpecifier createImportNamedSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam(optional = true) ISwc4jAstModuleExportName imported,
            boolean typeOnly,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstImportNamedSpecifier(local, imported, typeOnly, span);
    }

    @Jni2RustMethod
    public static Swc4jAstImportStarAsSpecifier createImportStarAsSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstImportStarAsSpecifier(local, span);
    }

    @Jni2RustMethod
    public static Swc4jAstInvalid createInvalid(
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstInvalid(span);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxText createJsxText(
            String value,
            String raw,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstJsxText(value, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstKeyValuePatProp createKeyValuePatProp(
            ISwc4jAstPropName key,
            ISwc4jAstPat value,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstKeyValuePatProp(key, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstKeyValueProp createKeyValueProp(
            ISwc4jAstPropName key,
            ISwc4jAstExpr value,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstKeyValueProp(key, value, span);
    }

    @Jni2RustMethod
    public static Swc4jAstMemberExpr createMemberExpr(
            ISwc4jAstExpr obj,
            ISwc4jAstMemberProp prop,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstMemberExpr(obj, prop, span);
    }

    @Jni2RustMethod
    public static Swc4jAstMetaPropExpr createMetaPropExpr(
            int kind,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstMetaPropExpr(Swc4jAstMetaPropKind.parse(kind), span);
    }

    @Jni2RustMethod
    public static Swc4jAstMethodProp createMethodProp(
            ISwc4jAstPropName key,
            Swc4jAstFunction function,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstMethodProp(key, function, span);
    }

    @Jni2RustMethod
    public static Swc4jAstModule createModule(
            List<ISwc4jAstModuleItem> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstModule(body, shebang, span);
    }

    @Jni2RustMethod
    public static Swc4jAstNamedExport createNamedExport(
            List<ISwc4jAstExportSpecifier> specifiers,
            @Jni2RustParam(optional = true) Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstNamedExport(specifiers, src, typeOnly, with, span);
    }

    @Jni2RustMethod
    public static Swc4jAstNewExpr createNewExpr(
            ISwc4jAstExpr callee,
            @Jni2RustParam(optional = true) List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstNewExpr(callee, args, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstNull createNull(
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstNull(span);
    }

    @Jni2RustMethod
    public static Swc4jAstNumber createNumber(
            double value,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstNumber(value, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstObjectLit createObjectLit(
            List<ISwc4jAstPropOrSpread> props,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstObjectLit(props, span);
    }

    @Jni2RustMethod
    public static Swc4jAstObjectPat createObjectPat(
            List<ISwc4jAstObjectPatProp> props,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstObjectPat(props, optional, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstParam createParam(
            List<Swc4jAstDecorator> decorators,
            ISwc4jAstPat pat,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstParam(decorators, pat, span);
    }

    @Jni2RustMethod
    public static Swc4jAstParenExpr createParenExpr(
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstParenExpr(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstPrivateMethod createPrivateMethod(
            Swc4jAstPrivateName key,
            Swc4jAstFunction function,
            int kind,
            boolean isStatic,
            int accessibilityId,
            boolean isAbstract,
            boolean optional,
            boolean isOverride,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstPrivateMethod(
                key, function, Swc4jAstMethodKind.parse(kind), isStatic,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isAbstract, optional, isOverride, span);
    }

    @Jni2RustMethod
    public static Swc4jAstPrivateName createPrivateName(
            Swc4jAstIdent id,
            @Jni2RustParam Swc4jAstSpan span) {
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
            @Jni2RustParam Swc4jAstSpan span) {
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
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstRegex(exp, flags, span);
    }

    @Jni2RustMethod
    public static Swc4jAstRestPat createRestPat(
            Swc4jAstSpan dot3Token,
            ISwc4jAstPat arg,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstRestPat(dot3Token, arg, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstScript createScript(
            List<ISwc4jAstStmt> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstScript(body, shebang, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSetterProp createSetterProp(
            ISwc4jAstPropName key,
            ISwc4jAstPat param,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstSetterProp(key, param, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSpan createSpan(
            @Jni2RustParamSpanStart int start,
            @Jni2RustParamSpanEnd int end) {
        return new Swc4jAstSpan(start, end);
    }

    @Jni2RustMethod
    public static Swc4jAstSpreadElement createSpreadElement(
            Swc4jAstSpan dot3Token,
            ISwc4jAstExpr expr,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstSpreadElement(dot3Token, expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstStaticBlock createStaticBlock(
            Swc4jAstBlockStmt body,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstStaticBlock(body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstStr createStr(
            String value,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstStr(value, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstSuper createSuper(
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstSuper(span);
    }

    @Jni2RustMethod
    public static Swc4jAstSuperPropExpr createSuperPropExpr(
            Swc4jAstSuper obj,
            ISwc4jAstSuperProp prop,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstSuperPropExpr(obj, prop, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTaggedTpl createTaggedTpl(
            ISwc4jAstExpr tag,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeParams,
            Swc4jAstTpl tpl,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTaggedTpl(tag, typeParams, tpl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstThisExpr createThisExpr(
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstThisExpr(span);
    }

    @Jni2RustMethod
    public static Swc4jAstTpl createTpl(
            List<ISwc4jAstExpr> exprs,
            List<Swc4jAstTplElement> quasis,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTpl(exprs, quasis, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTplElement createTplElement(
            boolean tail,
            @Jni2RustParam(optional = true) String cooked,
            String raw,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTplElement(tail, cooked, raw, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsEnumDecl createTsEnumDecl(
            boolean declare,
            boolean isConst,
            Swc4jAstIdent id,
            List<Swc4jAstTsEnumMember> members,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsEnumDecl(declare, isConst, id, members, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsEnumMember createTsEnumMember(
            ISwc4jAstTsEnumMemberId id,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsEnumMember(id, init, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExportAssignment createTsExportAssignment(
            ISwc4jAstExpr decl,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsExportAssignment(decl, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExprWithTypeArgs createTsExprWithTypeArgs(
            ISwc4jAstExpr expr,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsExprWithTypeArgs(expr, typeArgs, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExternalModuleRef createTsExternalModuleRef(
            Swc4jAstStr expr,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsExternalModuleRef(expr, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsImportEqualsDecl createTsImportEqualsDecl(
            boolean export,
            boolean typeOnly,
            Swc4jAstIdent id,
            ISwc4jAstTsModuleRef moduleRef,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsImportEqualsDecl(export, typeOnly, id, moduleRef, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsIndexSignature createTsIndexSignature(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean readonly,
            boolean isStatic,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsIndexSignature(params, typeAnn, readonly, isStatic, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsInterfaceBody createTsInterfaceBody(
            List<ISwc4jAstTsTypeElement> body,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsInterfaceBody(body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsInterfaceDecl createTsInterfaceDecl(
            Swc4jAstIdent id,
            boolean declare,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            List<Swc4jAstTsExprWithTypeArgs> _extends,
            Swc4jAstTsInterfaceBody body,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsInterfaceDecl(id, declare, typeParams, _extends, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsModuleDecl createTsModuleDecl(
            boolean declare,
            boolean global,
            ISwc4jAstTsModuleName id,
            @Jni2RustParam(optional = true) ISwc4jAstTsNamespaceBody body,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsModuleDecl(declare, global, id, body, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsNamespaceExportDecl createTsNamespaceExportDecl(
            Swc4jAstIdent id,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsNamespaceExportDecl(id, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsQualifiedName createTsQualifiedName(
            ISwc4jAstTsEntityName left,
            Swc4jAstIdent right,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsQualifiedName(left, right, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeAliasDecl createTsTypeAliasDecl(
            Swc4jAstIdent id,
            boolean declare,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamDecl typeParams,
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsTypeAliasDecl(id, declare, typeParams, typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeAnn createTsTypeAnn(
            ISwc4jAstTsType typeAnn,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsTypeAnn(typeAnn, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParam createTsTypeParam(
            Swc4jAstIdent name,
            boolean isIn,
            boolean isOut,
            boolean isConst,
            @Jni2RustParam(optional = true) ISwc4jAstTsType constraint,
            @Jni2RustParam(optional = true) ISwc4jAstTsType _default,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsTypeParam(name, isIn, isOut, isConst, constraint, _default, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParamDecl createTsTypeParamDecl(
            List<Swc4jAstTsTypeParam> params,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsTypeParamDecl(params, span);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParamInstantiation createTsTypeParamInstantiation(
            List<ISwc4jAstTsType> params,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstTsTypeParamInstantiation(params, span);
    }

    @Jni2RustMethod
    public static Swc4jAstUnaryExpr createUnaryExpr(
            int op,
            ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstUnaryExpr(Swc4jAstUnaryOp.parse(op), arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstUpdateExpr createUpdateExpr(
            int op,
            boolean prefix,
            ISwc4jAstExpr arg,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstUpdateExpr(Swc4jAstUpdateOp.parse(op), prefix, arg, span);
    }

    @Jni2RustMethod
    public static Swc4jAstUsingDecl createUsingDecl(
            boolean isAwait,
            List<Swc4jAstVarDeclarator> decls,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstUsingDecl(isAwait, decls, span);
    }

    @Jni2RustMethod
    public static Swc4jAstVarDecl createVarDecl(
            int kindId,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstVarDecl(Swc4jAstVarDeclKind.parse(kindId), declare, decls, span);
    }

    @Jni2RustMethod
    public static Swc4jAstVarDeclarator createVarDeclarator(
            ISwc4jAstPat name,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            boolean definite,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstVarDeclarator(name, init, definite, span);
    }

    @Jni2RustMethod
    public static Swc4jAstYieldExpr createYieldExpr(
            @Jni2RustParam(optional = true) ISwc4jAstExpr arg,
            boolean delegate,
            @Jni2RustParam Swc4jAstSpan span) {
        return new Swc4jAstYieldExpr(arg, delegate, span);
    }
}
