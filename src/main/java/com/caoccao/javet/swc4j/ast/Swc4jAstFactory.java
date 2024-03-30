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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
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
    public static Swc4jAstDebuggerStmt createDebuggerStmt(
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstDebuggerStmt(startPosition, endPosition);
    }

    @Jni2RustMethod
    public static Swc4jAstEmptyStmt createEmptyStmt(
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstEmptyStmt(startPosition, endPosition);
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
    public static Swc4jAstModule createModule(
            List<ISwc4jAstModuleItem> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstModule(body, shebang, startPosition, endPosition);
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
    public static Swc4jAstStr createStr(
            String value,
            @Jni2RustParam(optional = true) String raw,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstStr(value, raw, startPosition, endPosition);
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
