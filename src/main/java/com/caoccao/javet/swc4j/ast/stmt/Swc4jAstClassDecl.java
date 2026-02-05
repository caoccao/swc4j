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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDecl;
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
 * The type swc4j ast class decl.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, span = false)
public class Swc4jAstClassDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    /**
     * The Clazz.
     */
    @Jni2RustField(name = "class", box = true)
    protected Swc4jAstClass clazz;
    /**
     * The Declare.
     */
    protected boolean declare;
    /**
     * The Ident.
     */
    protected Swc4jAstIdent ident;

    /**
     * Instantiates a new swc4j ast class decl.
     *
     * @param ident   the ident
     * @param declare the declare
     * @param clazz   the clazz
     * @param span    the span
     */
    @Jni2RustMethod
    public Swc4jAstClassDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstClass clazz,
            Swc4jSpan span) {
        super(span);
        setClazz(clazz);
        setDeclare(declare);
        setIdent(ident);
    }

    /**
     * Create swc4j ast class decl.
     *
     * @param ident the ident
     * @param clazz the clazz
     * @return the swc4j ast class decl
     */
    public static Swc4jAstClassDecl create(Swc4jAstIdent ident, Swc4jAstClass clazz) {
        return create(ident, false, clazz);
    }

    /**
     * Create swc4j ast class decl.
     *
     * @param ident   the ident
     * @param declare the declare
     * @param clazz   the clazz
     * @return the swc4j ast class decl
     */
    public static Swc4jAstClassDecl create(Swc4jAstIdent ident, boolean declare, Swc4jAstClass clazz) {
        return new Swc4jAstClassDecl(ident, declare, clazz, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(clazz, ident);
    }

    /**
     * Gets clazz.
     *
     * @return the clazz
     */
    @Jni2RustMethod
    public Swc4jAstClass getClazz() {
        return clazz;
    }

    /**
     * Gets ident.
     *
     * @return the ident
     */
    @Jni2RustMethod
    public Swc4jAstIdent getIdent() {
        return ident;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ClassDecl;
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
        if (clazz == oldNode && newNode instanceof Swc4jAstClass newClazz) {
            setClazz(newClazz);
            return true;
        }
        if (ident == oldNode && newNode instanceof Swc4jAstIdent newIdent) {
            setIdent(newIdent);
            return true;
        }
        return false;
    }

    /**
     * Sets clazz.
     *
     * @param clazz the clazz
     * @return the clazz
     */
    public Swc4jAstClassDecl setClazz(Swc4jAstClass clazz) {
        this.clazz = AssertionUtils.notNull(clazz, "Class");
        this.clazz.setParent(this);
        return this;
    }

    /**
     * Sets declare.
     *
     * @param declare the declare
     * @return the declare
     */
    public Swc4jAstClassDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    /**
     * Sets ident.
     *
     * @param ident the ident
     * @return the ident
     */
    public Swc4jAstClassDecl setIdent(Swc4jAstIdent ident) {
        this.ident = AssertionUtils.notNull(ident, "Ident");
        this.ident.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitClassDecl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
