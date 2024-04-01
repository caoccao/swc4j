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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBigIntSign;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.module.*;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
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
    public static Swc4jAstAutoAccessor createAutoAccessor(
            ISwc4jAstKey key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean isStatic,
            List<Swc4jAstDecorator> decorators,
            int accessibilityId,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstAutoAccessor(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstBigInt createBigInt(
            int sign,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstBigInt(Swc4jAstBigIntSign.parse(sign), raw, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstBindingIdent createBindingIdent(
            Swc4jAstIdent id,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstBindingIdent(id, typeAnn, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstBlockStmt createBlockStmt(
            List<ISwc4jAstStmt> stmts,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstBlockStmt(stmts, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstBool createBool(
            boolean value,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstBool(value, startPosition, endPosition);
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
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstClass(decorators, body, superClass, isAbstract, typeParams,
                superTypeParams, _implements, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstClassDecl createClassDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstClass clazz,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstClassDecl(ident, declare, clazz, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstClassProp createClassProp(
            ISwc4jAstKey key,
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
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstClassProp(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isAbstract, isOptional, isOverride, readonly, declare, definite,
                startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstConstructor createConstructor(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            @Jni2RustParam(optional = true) Swc4jAstBlockStmt body,
            int accessibilityId,
            boolean optional,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstConstructor(
                key, params, body,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                optional, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstDebuggerStmt createDebuggerStmt(
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstDebuggerStmt(startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstDecorator createDecorator(
            ISwc4jAstExpr expr,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstDecorator(expr, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstEmptyStmt createEmptyStmt(
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstEmptyStmt(startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstExportAll createExportAll(
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstExportAll(src, typeOnly, with, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDecl createExportDecl(
            ISwc4jAstDecl decl,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstExportDecl(decl, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDefaultDecl createExportDefaultDecl(
            ISwc4jAstDefaultDecl decl,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstExportDefaultDecl(decl, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstExportDefaultExpr createExportDefaultExpr(
            ISwc4jAstExpr decl,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstExportDefaultExpr(decl, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstExprStmt createExprStmt(
            ISwc4jAstExpr expr,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstExprStmt(expr, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstIdent createIdent(
            String sym,
            boolean optional,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstIdent(sym, optional, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstImportDecl createImportDecl(
            List<ISwc4jAstImportSpecifier> specifiers,
            Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstImportDecl(specifiers, src, typeOnly, with, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstImportDefaultSpecifier createImportDefaultSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstImportDefaultSpecifier(local, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstImportNamedSpecifier createImportNamedSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParam(optional = true) ISwc4jAstModuleExportName imported,
            boolean typeOnly,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstImportNamedSpecifier(local, imported, typeOnly, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstImportStarAsSpecifier createImportStarAsSpecifier(
            Swc4jAstIdent local,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstImportStarAsSpecifier(local, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstJsxText createJsxText(
            String value,
            String raw,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstJsxText(value, raw, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstModule createModule(
            List<ISwc4jAstModuleItem> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstModule(body, shebang, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstNamedExport createNamedExport(
            List<ISwc4jAstExportSpecifier> specifiers,
            @Jni2RustParam(optional = true) Swc4jAstStr src,
            boolean typeOnly,
            @Jni2RustParam(optional = true) Swc4jAstObjectLit with,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstNamedExport(specifiers, src, typeOnly, with, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstNull createNull(
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstNull(startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstNumber createNumber(
            double value,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstNumber(value, raw, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstObjectLit createObjectLit(
            List<ISwc4jAstPropOrSpread> props,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstObjectLit(props, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstPrivateName createPrivateName(
            Swc4jAstIdent id,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstPrivateName(id, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstPrivateProp createPrivateProp(
            ISwc4jAstKey key,
            @Jni2RustParam(optional = true) ISwc4jAstExpr value,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean isStatic,
            List<Swc4jAstDecorator> decorators,
            int accessibilityId,
            boolean isOptional,
            boolean isOverride,
            boolean readonly,
            boolean definite,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstPrivateProp(
                key, value, typeAnn, isStatic, decorators,
                accessibilityId >= 0 ? Swc4jAstAccessibility.parse(accessibilityId) : null,
                isOptional, isOverride, readonly, definite,
                startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstRegex createRegex(
            String exp,
            String flags,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstRegex(exp, flags, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstScript createScript(
            List<ISwc4jAstStmt> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstScript(body, shebang, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstSpreadElement createSpreadElement(
            int dot3TokenStartPosition,
            int dot3TokenEndPosition,
            ISwc4jAstExpr expr,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstSpreadElement(dot3TokenStartPosition, dot3TokenEndPosition, expr, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstStaticBlock createStaticBlock(
            Swc4jAstBlockStmt body,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstStaticBlock(body, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstStr createStr(
            String value,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstStr(value, raw, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExportAssignment createTsExportAssignment(
            ISwc4jAstExpr decl,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsExportAssignment(decl, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExprWithTypeArgs createTsExprWithTypeArgs(
            ISwc4jAstExpr expr,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsExprWithTypeArgs(expr, typeArgs, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsExternalModuleRef createTsExternalModuleRef(
            Swc4jAstStr expr,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsExternalModuleRef(expr, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsImportEqualsDecl createTsImportEqualsDecl(
            boolean export,
            boolean typeOnly,
            Swc4jAstIdent id,
            ISwc4jAstModuleRef moduleRef,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsImportEqualsDecl(export, typeOnly, id, moduleRef, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsIndexSignature createTsIndexSignature(
            List<ISwc4jAstTsFnParam> params,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            boolean readonly,
            boolean isStatic,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsIndexSignature(params, typeAnn, readonly, isStatic, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsNamespaceExportDecl createTsNamespaceExportDecl(
            Swc4jAstIdent id,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsNamespaceExportDecl(id, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeAnn createTsTypeAnn(
            ISwc4jAstTsType typeAnn,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsTypeAnn(typeAnn, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParam createTsTypeParam(
            Swc4jAstIdent name,
            boolean isIn,
            boolean isOut,
            boolean isConst,
            @Jni2RustParam(optional = true) ISwc4jAstTsType constraint,
            @Jni2RustParam(optional = true) ISwc4jAstTsType _default,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsTypeParam(name, isIn, isOut, isConst, constraint, _default, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParamDecl createTsTypeParamDecl(
            List<Swc4jAstTsTypeParam> params,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsTypeParamDecl(params, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstTsTypeParamInstantiation createTsTypeParamInstantiation(
            List<ISwc4jAstTsType> params,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstTsTypeParamInstantiation(params, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstUnaryExpr createUnaryExpr(
            int op,
            ISwc4jAstExpr arg,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstUnaryExpr(Swc4jAstUnaryOp.parse(op), arg, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstUsingDecl createUsingDecl(
            boolean isAwait,
            List<Swc4jAstVarDeclarator> decls,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstUsingDecl(isAwait, decls, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstVarDecl createVarDecl(
            int kindId,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstVarDecl(Swc4jAstVarDeclKind.parse(kindId), declare, decls, startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstVarDeclarator createVarDeclarator(
            ISwc4jAstPat name,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            boolean definite,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstVarDeclarator(name, init, definite, startPosition, endPosition);
    }
}
