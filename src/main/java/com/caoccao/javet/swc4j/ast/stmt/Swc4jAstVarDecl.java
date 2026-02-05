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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstForHead;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstVarDeclOrExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type swc4j ast var decl.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstVarDecl
        extends Swc4jAst
        implements ISwc4jAstDecl, ISwc4jAstVarDeclOrExpr, ISwc4jAstForHead {
    /**
     * The Decls.
     */
    protected final List<Swc4jAstVarDeclarator> decls;
    /**
     * The Ctxt.
     */
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    /**
     * The Declare.
     */
    protected boolean declare;
    /**
     * The Kind.
     */
    protected Swc4jAstVarDeclKind kind;

    /**
     * Instantiates a new swc4j ast var decl.
     *
     * @param ctxt    the ctxt
     * @param kind    the kind
     * @param declare the declare
     * @param decls   the decls
     * @param span    the span
     */
    @Jni2RustMethod
    public Swc4jAstVarDecl(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            Swc4jAstVarDeclKind kind,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls,
            Swc4jSpan span) {
        super(span);
        setCtxt(ctxt);
        setDeclare(declare);
        setKind(kind);
        this.decls = AssertionUtils.notNull(decls, "Decls");
        this.decls.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast var decl.
     *
     * @param kind the kind
     * @return the swc4j ast var decl
     */
    public static Swc4jAstVarDecl create(Swc4jAstVarDeclKind kind) {
        return create(kind, false);
    }

    /**
     * Create swc4j ast var decl.
     *
     * @param kind  the kind
     * @param decls the decls
     * @return the swc4j ast var decl
     */
    public static Swc4jAstVarDecl create(Swc4jAstVarDeclKind kind, List<Swc4jAstVarDeclarator> decls) {
        return create(kind, false, decls);
    }

    /**
     * Create swc4j ast var decl.
     *
     * @param kind    the kind
     * @param declare the declare
     * @return the swc4j ast var decl
     */
    public static Swc4jAstVarDecl create(Swc4jAstVarDeclKind kind, boolean declare) {
        return create(kind, declare, SimpleList.of());
    }

    /**
     * Create swc4j ast var decl.
     *
     * @param kind    the kind
     * @param declare the declare
     * @param decls   the decls
     * @return the swc4j ast var decl
     */
    public static Swc4jAstVarDecl create(
            Swc4jAstVarDeclKind kind,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls) {
        return create(0, kind, declare, decls);
    }

    /**
     * Create swc4j ast var decl.
     *
     * @param ctxt    the ctxt
     * @param kind    the kind
     * @param declare the declare
     * @param decls   the decls
     * @return the swc4j ast var decl
     */
    public static Swc4jAstVarDecl create(
            int ctxt,
            Swc4jAstVarDeclKind kind,
            boolean declare,
            List<Swc4jAstVarDeclarator> decls) {
        return new Swc4jAstVarDecl(ctxt, kind, declare, decls, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(decls);
    }

    /**
     * Gets ctxt.
     *
     * @return the ctxt
     */
    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

    /**
     * Gets decls.
     *
     * @return the decls
     */
    @Jni2RustMethod
    public List<Swc4jAstVarDeclarator> getDecls() {
        return decls;
    }

    /**
     * Gets kind.
     *
     * @return the kind
     */
    @Jni2RustMethod
    public Swc4jAstVarDeclKind getKind() {
        return kind;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.VarDecl;
    }

    /**
     * Is declare boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isDeclare() {
        return declare;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!decls.isEmpty() && newNode instanceof Swc4jAstVarDeclarator newDecl) {
            final int size = decls.size();
            for (int i = 0; i < size; i++) {
                if (decls.get(i) == oldNode) {
                    decls.set(i, newDecl);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets ctxt.
     *
     * @param ctxt the ctxt
     * @return the ctxt
     */
    public Swc4jAstVarDecl setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets declare.
     *
     * @param declare the declare
     * @return the declare
     */
    public Swc4jAstVarDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    /**
     * Sets kind.
     *
     * @param kind the kind
     * @return the kind
     */
    public Swc4jAstVarDecl setKind(Swc4jAstVarDeclKind kind) {
        this.kind = AssertionUtils.notNull(kind, "Kind");
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitVarDecl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
