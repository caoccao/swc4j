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
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTplElement
        extends Swc4jAst {
    @Jni2RustField(componentAtom = true)
    protected Optional<String> cooked;
    @Jni2RustField(atom = true)
    protected String raw;
    protected boolean tail;

    @Jni2RustMethod
    public Swc4jAstTplElement(
            boolean tail,
            @Jni2RustParam(optional = true) String cooked,
            String raw,
            Swc4jSpan span) {
        super(span);
        setCooked(cooked);
        setRaw(raw);
        setTail(tail);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    @Jni2RustMethod
    public Optional<String> getCooked() {
        return cooked;
    }

    @Jni2RustMethod
    public String getRaw() {
        return raw;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TplElement;
    }

    @Jni2RustMethod
    public boolean isTail() {
        return tail;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    public Swc4jAstTplElement setCooked(String cooked) {
        this.cooked = Optional.ofNullable(cooked);
        return this;
    }

    public Swc4jAstTplElement setRaw(String raw) {
        this.raw = AssertionUtils.notNull(raw, "Raw");
        return this;
    }

    public Swc4jAstTplElement setTail(boolean tail) {
        this.tail = tail;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTplElement(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
