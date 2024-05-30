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

package com.caoccao.javet.swc4j.ast.miscs;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstSwitchCase
        extends Swc4jAst {
    protected final List<ISwc4jAstStmt> cons;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstExpr> test;

    @Jni2RustMethod
    public Swc4jAstSwitchCase(
            @Jni2RustParam(optional = true) ISwc4jAstExpr test,
            List<ISwc4jAstStmt> cons,
            Swc4jSpan span) {
        super(span);
        setTest(test);
        this.cons = AssertionUtils.notNull(cons, "Cons");
        this.cons.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstSwitchCase create() {
        return create(null);
    }

    public static Swc4jAstSwitchCase create(ISwc4jAstExpr test) {
        return create(test, SimpleList.of());
    }

    public static Swc4jAstSwitchCase create(ISwc4jAstExpr test, List<ISwc4jAstStmt> cons) {
        return new Swc4jAstSwitchCase(test, cons, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(cons);
        test.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstStmt> getCons() {
        return cons;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstExpr> getTest() {
        return test;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SwitchCase;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!cons.isEmpty() && newNode instanceof ISwc4jAstStmt) {
            final int size = cons.size();
            for (int i = 0; i < size; i++) {
                if (cons.get(i) == oldNode) {
                    cons.set(i, (ISwc4jAstStmt) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (test.isPresent() && test.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstExpr)) {
            setTest((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstSwitchCase setTest(ISwc4jAstExpr test) {
        this.test = Optional.ofNullable(test);
        this.test.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitSwitchCase(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
