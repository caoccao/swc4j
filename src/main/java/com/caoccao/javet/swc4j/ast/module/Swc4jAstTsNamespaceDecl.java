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

package com.caoccao.javet.swc4j.ast.module;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsNamespaceBody;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type swc4j ast ts namespace decl.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsNamespaceDecl
        extends Swc4jAst
        implements ISwc4jAstTsNamespaceBody {
    /**
     * The Body.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstTsNamespaceBody body;
    /**
     * The Declare.
     */
    protected boolean declare;
    /**
     * The Global.
     */
    protected boolean global;
    /**
     * The Id.
     */
    protected Swc4jAstIdent id;

    /**
     * Instantiates a new swc4j ast ts namespace decl.
     *
     * @param declare the declare
     * @param global  the global
     * @param id      the id
     * @param body    the body
     * @param span    the span
     */
    @Jni2RustMethod
    public Swc4jAstTsNamespaceDecl(
            boolean declare,
            boolean global,
            Swc4jAstIdent id,
            ISwc4jAstTsNamespaceBody body,
            Swc4jSpan span) {
        super(span);
        setBody(body);
        setDeclare(declare);
        setGlobal(global);
        setId(id);
    }

    /**
     * Create swc4j ast ts namespace decl.
     *
     * @param id   the id
     * @param body the body
     * @return the swc4j ast ts namespace decl
     */
    public static Swc4jAstTsNamespaceDecl create(Swc4jAstIdent id, ISwc4jAstTsNamespaceBody body) {
        return create(false, id, body);
    }

    /**
     * Create swc4j ast ts namespace decl.
     *
     * @param declare the declare
     * @param id      the id
     * @param body    the body
     * @return the swc4j ast ts namespace decl
     */
    public static Swc4jAstTsNamespaceDecl create(boolean declare, Swc4jAstIdent id, ISwc4jAstTsNamespaceBody body) {
        return create(declare, false, id, body);
    }

    /**
     * Create swc4j ast ts namespace decl.
     *
     * @param declare the declare
     * @param global  the global
     * @param id      the id
     * @param body    the body
     * @return the swc4j ast ts namespace decl
     */
    public static Swc4jAstTsNamespaceDecl create(
            boolean declare,
            boolean global,
            Swc4jAstIdent id,
            ISwc4jAstTsNamespaceBody body) {
        return new Swc4jAstTsNamespaceDecl(declare, global, id, body, Swc4jSpan.DUMMY);
    }

    /**
     * Gets body.
     *
     * @return the body
     */
    @Jni2RustMethod
    public ISwc4jAstTsNamespaceBody getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(id, body);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Jni2RustMethod
    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsNamespaceDecl;
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

    /**
     * Is global boolean.
     *
     * @return the boolean
     */
    @Jni2RustMethod
    public boolean isGlobal() {
        return global;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (body == oldNode && newNode instanceof ISwc4jAstTsNamespaceBody newBody) {
            setBody(newBody);
            return true;
        }
        if (id == oldNode && newNode instanceof Swc4jAstIdent newId) {
            setId(newId);
            return true;
        }
        return false;
    }

    /**
     * Sets body.
     *
     * @param body the body
     * @return the body
     */
    public Swc4jAstTsNamespaceDecl setBody(ISwc4jAstTsNamespaceBody body) {
        this.body = AssertionUtils.notNull(body, "Body");
        this.body.setParent(this);
        return this;
    }

    /**
     * Sets declare.
     *
     * @param declare the declare
     * @return the declare
     */
    public Swc4jAstTsNamespaceDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    /**
     * Sets global.
     *
     * @param global the global
     * @return the global
     */
    public Swc4jAstTsNamespaceDecl setGlobal(boolean global) {
        this.global = global;
        return this;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public Swc4jAstTsNamespaceDecl setId(Swc4jAstIdent id) {
        this.id = AssertionUtils.notNull(id, "Id");
        this.id.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsNamespaceDecl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
