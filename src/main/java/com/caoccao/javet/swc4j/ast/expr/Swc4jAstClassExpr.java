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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDefaultDecl;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, span = false)
public class Swc4jAstClassExpr
        extends Swc4jAst
        implements ISwc4jAstDefaultDecl, ISwc4jAstExpr {
    @Jni2RustField(name = "class", box = true)
    protected Swc4jAstClass clazz;
    protected Optional<Swc4jAstIdent> ident;

    @Jni2RustMethod
    public Swc4jAstClassExpr(
            @Jni2RustParam(optional = true) Swc4jAstIdent ident,
            Swc4jAstClass clazz,
            Swc4jSpan span) {
        super(span);
        setClazz(clazz);
        setIdent(ident);
    }

    public static Swc4jAstClassExpr create(Swc4jAstClass clazz) {
        return create(null, clazz);
    }

    public static Swc4jAstClassExpr create(Swc4jAstIdent ident, Swc4jAstClass clazz) {
        return new Swc4jAstClassExpr(ident, clazz, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(clazz);
        ident.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Swc4jAstClass getClazz() {
        return clazz;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstIdent> getIdent() {
        return ident;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ClassExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (clazz == oldNode && newNode instanceof Swc4jAstClass) {
            setClazz((Swc4jAstClass) newNode);
            return true;
        }
        if (ident.isPresent() && ident.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstIdent)) {
            setIdent((Swc4jAstIdent) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstClassExpr setClazz(Swc4jAstClass clazz) {
        this.clazz = AssertionUtils.notNull(clazz, "Class");
        this.clazz.setParent(this);
        return this;
    }

    public Swc4jAstClassExpr setIdent(Swc4jAstIdent ident) {
        this.ident = Optional.ofNullable(ident);
        this.ident.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitClassExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
