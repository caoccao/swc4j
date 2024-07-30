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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstBlockStmtOrExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstBlockStmt
        extends Swc4jAst
        implements ISwc4jAstStmt, ISwc4jAstBlockStmtOrExpr {
    protected final List<ISwc4jAstStmt> stmts;
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;

    @Jni2RustMethod
    public Swc4jAstBlockStmt(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            List<ISwc4jAstStmt> stmts,
            Swc4jSpan span) {
        super(span);
        setCtxt(ctxt);
        this.stmts = AssertionUtils.notNull(stmts, "Stmts");
        this.stmts.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstBlockStmt create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstBlockStmt create(List<ISwc4jAstStmt> stmts) {
        return create(0, stmts);
    }

    public static Swc4jAstBlockStmt create(int ctxt, List<ISwc4jAstStmt> stmts) {
        return new Swc4jAstBlockStmt(ctxt, stmts, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(stmts);
    }

    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

    @Jni2RustMethod
    public List<ISwc4jAstStmt> getStmts() {
        return stmts;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BlockStmt;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!stmts.isEmpty() && newNode instanceof ISwc4jAstStmt) {
            final int size = stmts.size();
            for (int i = 0; i < size; i++) {
                if (stmts.get(i) == oldNode) {
                    stmts.set(i, (ISwc4jAstStmt) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        return false;
    }

    public Swc4jAstBlockStmt setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitBlockStmt(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
