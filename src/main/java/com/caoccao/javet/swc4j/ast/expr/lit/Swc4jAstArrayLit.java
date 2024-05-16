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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstArrayLit
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final List<Optional<Swc4jAstExprOrSpread>> elems;

    @Jni2RustMethod
    public Swc4jAstArrayLit(
            List<Swc4jAstExprOrSpread> elems,
            Swc4jSpan span) {
        super(span);
        this.elems = AssertionUtils.notNull(elems, "Elems").stream()
                .map(Optional::ofNullable)
                .collect(Collectors.toList());
        this.elems.stream().filter(Optional::isPresent).map(Optional::get).forEach(node -> node.setParent(this));
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of();
        elems.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<Optional<Swc4jAstExprOrSpread>> getElems() {
        return elems;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ArrayLit;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!elems.isEmpty() && (newNode == null || newNode instanceof Swc4jAstExprOrSpread)) {
            final int size = elems.size();
            for (int i = 0; i < size; i++) {
                Optional<Swc4jAstExprOrSpread> optionalOldElem = elems.get(i);
                if (optionalOldElem.isPresent() && optionalOldElem.get() == oldNode) {
                    Optional<Swc4jAstExprOrSpread> optionalNewElem = Optional.ofNullable((Swc4jAstExprOrSpread) newNode);
                    optionalNewElem.ifPresent(node -> node.setParent(this));
                    elems.set(i, optionalNewElem);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitArrayLit(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
