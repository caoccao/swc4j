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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExpr;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstPat;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.decl.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.decl.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.stmt.decl.Swc4jAstVarDeclarator;
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

    /**
     * Create module ast ident.
     *
     * @param sym           the shebang
     * @param optional      the optional
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast ident
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jAstIdent createIdent(
            String sym,
            boolean optional,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstIdent(sym, optional, startPosition, endPosition);
    }

    /**
     * Create module ast module.
     *
     * @param body          the body
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast module
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jAstModule createModule(
            List<Swc4jAst> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstModule(body, shebang, startPosition, endPosition);
    }

    /**
     * Create module ast script.
     *
     * @param body          the body
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast script
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jAstScript createScript(
            List<Swc4jAst> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstScript(body, shebang, startPosition, endPosition);
    }

    /**
     * Create module ast var decl.
     *
     * @param kindId        the kind id
     * @param declare       the declare
     * @param decls         the decls
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast var decl
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jAstVarDecl createVarDecl(
            int kindId,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstVarDecl(Swc4jAstVarDeclKind.parse(kindId), declare, decls, startPosition, endPosition);
    }

    /**
     * Create module ast var declarator.
     *
     * @param name          the name
     * @param init          the init
     * @param definite      the definite
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast var declarator
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jAstVarDeclarator createVarDeclarator(
            Swc4jAstPat name,
            @Jni2RustParam(optional = true) Swc4jAstExpr init,
            boolean definite,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstVarDeclarator(name, init, definite, startPosition, endPosition);
    }
}
