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
 * The type swc4j ast using decl.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstUsingDecl
        extends Swc4jAst
        implements ISwc4jAstDecl, ISwc4jAstVarDeclOrExpr, ISwc4jAstForHead {
    /**
     * The Decls.
     */
    protected final List<Swc4jAstVarDeclarator> decls;
    /**
     * The Await.
     */
    @Jni2RustField(name = "is_await")
    protected boolean _await;

    /**
     * Instantiates a new swc4j ast using decl.
     *
     * @param _await the await
     * @param decls  the decls
     * @param span   the span
     */
    @Jni2RustMethod
    public Swc4jAstUsingDecl(
            @Jni2RustParam(name = "is_await") boolean _await,
            List<Swc4jAstVarDeclarator> decls,
            Swc4jSpan span) {
        super(span);
        setAwait(_await);
        this.decls = AssertionUtils.notNull(decls, "Decls");
        this.decls.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast using decl.
     *
     * @return the swc4j ast using decl
     */
    public static Swc4jAstUsingDecl create() {
        return create(SimpleList.of());
    }

    /**
     * Create swc4j ast using decl.
     *
     * @param decls the decls
     * @return the swc4j ast using decl
     */
    public static Swc4jAstUsingDecl create(List<Swc4jAstVarDeclarator> decls) {
        return create(false, decls);
    }

    /**
     * Create swc4j ast using decl.
     *
     * @param _await the await
     * @param decls  the decls
     * @return the swc4j ast using decl
     */
    public static Swc4jAstUsingDecl create(boolean _await, List<Swc4jAstVarDeclarator> decls) {
        return new Swc4jAstUsingDecl(_await, decls, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(decls);
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

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.UsingDecl;
    }

    /**
     * Is await boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isAwait() {
        return _await;
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
     * Sets await.
     *
     * @param _await the await
     * @return the await
     */
    public Swc4jAstUsingDecl setAwait(boolean _await) {
        this._await = _await;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitUsingDecl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
