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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstVarDeclarator
        extends Swc4jAst
        implements ISwc4jAstDecl {
    protected boolean definite;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> init;
    protected ISwc4jAstPat name;

    @Jni2RustMethod
    public Swc4jAstVarDeclarator(
            ISwc4jAstPat name,
            @Jni2RustParam(optional = true) ISwc4jAstExpr init,
            boolean definite,
            Swc4jSpan span) {
        super(span);
        setDefinite(definite);
        setInit(init);
        setName(name);
    }

    public static Swc4jAstVarDeclarator create(ISwc4jAstPat name) {
        return create(name, null);
    }

    public static Swc4jAstVarDeclarator create(ISwc4jAstPat name, ISwc4jAstExpr init) {
        return create(name, init, false);
    }

    public static Swc4jAstVarDeclarator create(ISwc4jAstPat name, ISwc4jAstExpr init, boolean definite) {
        return new Swc4jAstVarDeclarator(name, init, definite, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(name);
        init.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getInit() {
        return init;
    }

    @Jni2RustMethod
    public ISwc4jAstPat getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.VarDeclarator;
    }

    @Jni2RustMethod
    public boolean isDefinite() {
        return definite;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (init.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setInit((ISwc4jAstExpr) newNode);
            return true;
        }
        if (name == oldNode && newNode instanceof ISwc4jAstPat newName) {
            setName(newName);
            return true;
        }
        return false;
    }

    public Swc4jAstVarDeclarator setDefinite(boolean definite) {
        this.definite = definite;
        return this;
    }

    public Swc4jAstVarDeclarator setInit(ISwc4jAstExpr init) {
        this.init = Optional.ofNullable(init);
        this.init.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstVarDeclarator setName(ISwc4jAstPat name) {
        this.name = AssertionUtils.notNull(name, "Name");
        this.name.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitVarDeclarator(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
