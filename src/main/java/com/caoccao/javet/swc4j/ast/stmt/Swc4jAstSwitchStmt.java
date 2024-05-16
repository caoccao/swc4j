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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstSwitchCase;
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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstSwitchStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    protected final List<Swc4jAstSwitchCase> cases;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr discriminant;

    @Jni2RustMethod
    public Swc4jAstSwitchStmt(
            ISwc4jAstExpr discriminant,
            List<Swc4jAstSwitchCase> cases,
            Swc4jSpan span) {
        super(span);
        setDiscriminant(discriminant);
        this.cases = AssertionUtils.notNull(cases, "Cases");
        this.cases.forEach(node -> node.setParent(this));
    }

    @Jni2RustMethod
    public List<Swc4jAstSwitchCase> getCases() {
        return cases;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(cases);
        childNodes.add(discriminant);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getDiscriminant() {
        return discriminant;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SwitchStmt;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!cases.isEmpty() && newNode instanceof Swc4jAstSwitchCase) {
            final int size = cases.size();
            for (int i = 0; i < size; i++) {
                if (cases.get(i) == oldNode) {
                    cases.set(i, (Swc4jAstSwitchCase) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (discriminant == oldNode && newNode instanceof ISwc4jAstExpr) {
            setDiscriminant((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstSwitchStmt setDiscriminant(ISwc4jAstExpr discriminant) {
        this.discriminant = AssertionUtils.notNull(discriminant, "Discriminant");
        this.discriminant.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitSwitchStmt(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
