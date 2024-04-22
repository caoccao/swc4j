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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrValue;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementChild;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxClosingFragment;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxOpeningFragment;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(name = "JSXFragment")
public class Swc4jAstJsxFragment
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstJsxElementChild, ISwc4jAstJsxAttrValue {
    protected final List<ISwc4jAstJsxElementChild> children;
    protected final Swc4jAstJsxClosingFragment closing;
    protected final Swc4jAstJsxOpeningFragment opening;

    public Swc4jAstJsxFragment(
            Swc4jAstJsxOpeningFragment opening,
            List<ISwc4jAstJsxElementChild> children,
            Swc4jAstJsxClosingFragment closing,
            Swc4jSpan span) {
        super(span);
        this.children = SimpleList.immutable(AssertionUtils.notNull(children, "Children"));
        this.closing = AssertionUtils.notNull(closing, "Closing");
        this.opening = AssertionUtils.notNull(opening, "Opening");
        childNodes = SimpleList.copyOf(children);
        childNodes.add(opening);
        childNodes.add(closing);
        childNodes = SimpleList.immutable(childNodes);
    }

    public List<ISwc4jAstJsxElementChild> getChildren() {
        return children;
    }

    public Swc4jAstJsxClosingFragment getClosing() {
        return closing;
    }

    public Swc4jAstJsxOpeningFragment getOpening() {
        return opening;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxFragment;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitJsxFragment(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
