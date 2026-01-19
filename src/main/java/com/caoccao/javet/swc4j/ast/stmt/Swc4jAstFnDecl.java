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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, span = false)
public class Swc4jAstFnDecl
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected boolean declare;
    @Jni2RustField(box = true)
    protected Swc4jAstFunction function;
    protected Swc4jAstIdent ident;

    @Jni2RustMethod
    public Swc4jAstFnDecl(
            Swc4jAstIdent ident,
            boolean declare,
            Swc4jAstFunction function,
            Swc4jSpan span) {
        super(span);
        setDeclare(declare);
        setFunction(function);
        setIdent(ident);
    }

    public static Swc4jAstFnDecl create(Swc4jAstIdent ident, Swc4jAstFunction function) {
        return create(ident, false, function);
    }

    public static Swc4jAstFnDecl create(Swc4jAstIdent ident, boolean declare, Swc4jAstFunction function) {
        return new Swc4jAstFnDecl(ident, declare, function, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(function, ident);
    }

    @Jni2RustMethod
    public Swc4jAstFunction getFunction() {
        return function;
    }

    @Jni2RustMethod
    public Swc4jAstIdent getIdent() {
        return ident;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.FnDecl;
    }

    @Jni2RustMethod
    public boolean isDeclare() {
        return declare;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (function == oldNode && newNode instanceof Swc4jAstFunction newFunction) {
            setFunction(newFunction);
            return true;
        }
        if (ident == oldNode && newNode instanceof Swc4jAstIdent newIdent) {
            setIdent(newIdent);
            return true;
        }
        return false;
    }

    public Swc4jAstFnDecl setDeclare(boolean declare) {
        this.declare = declare;
        return this;
    }

    public Swc4jAstFnDecl setFunction(Swc4jAstFunction function) {
        this.function = AssertionUtils.notNull(function, "Function");
        this.function.setParent(this);
        return this;
    }

    public Swc4jAstFnDecl setIdent(Swc4jAstIdent ident) {
        this.ident = AssertionUtils.notNull(ident, "Ident");
        this.ident.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitFnDecl(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
