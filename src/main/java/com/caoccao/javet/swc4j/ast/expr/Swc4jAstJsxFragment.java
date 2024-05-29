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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrValue;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementChild;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxClosingFragment;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxOpeningFragment;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, name = "JSXFragment")
public class Swc4jAstJsxFragment
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstJsxElementChild, ISwc4jAstJsxAttrValue {
    protected final List<ISwc4jAstJsxElementChild> children;
    protected Swc4jAstJsxClosingFragment closing;
    protected Swc4jAstJsxOpeningFragment opening;

    @Jni2RustMethod
    public Swc4jAstJsxFragment(
            Swc4jAstJsxOpeningFragment opening,
            List<ISwc4jAstJsxElementChild> children,
            Swc4jAstJsxClosingFragment closing,
            Swc4jSpan span) {
        super(span);
        setClosing(closing);
        setOpening(opening);
        this.children = AssertionUtils.notNull(children, "Children");
        this.children.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstJsxFragment create(Swc4jAstJsxOpeningFragment opening) {
        return create(opening, SimpleList.of());
    }

    public static Swc4jAstJsxFragment create(
            Swc4jAstJsxOpeningFragment opening,
            List<ISwc4jAstJsxElementChild> children) {
        return create(opening, children, null);
    }

    public static Swc4jAstJsxFragment create(
            Swc4jAstJsxOpeningFragment opening,
            List<ISwc4jAstJsxElementChild> children,
            Swc4jAstJsxClosingFragment closing) {
        return new Swc4jAstJsxFragment(opening, children, closing, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(children);
        childNodes.add(opening);
        childNodes.add(closing);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstJsxElementChild> getChildren() {
        return children;
    }

    @Jni2RustMethod
    public Swc4jAstJsxClosingFragment getClosing() {
        return closing;
    }

    @Jni2RustMethod
    public Swc4jAstJsxOpeningFragment getOpening() {
        return opening;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxFragment;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!children.isEmpty() && newNode instanceof ISwc4jAstJsxElementChild) {
            final int size = children.size();
            for (int i = 0; i < size; i++) {
                if (children.get(i) == oldNode) {
                    children.set(i, (ISwc4jAstJsxElementChild) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (closing == oldNode && newNode instanceof Swc4jAstJsxClosingFragment) {
            setClosing((Swc4jAstJsxClosingFragment) newNode);
            return true;
        }
        if (opening == oldNode && newNode instanceof Swc4jAstJsxOpeningFragment) {
            setOpening((Swc4jAstJsxOpeningFragment) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstJsxFragment setClosing(Swc4jAstJsxClosingFragment closing) {
        this.closing = AssertionUtils.notNull(closing, "Closing");
        this.closing.setParent(this);
        return this;
    }

    public Swc4jAstJsxFragment setOpening(Swc4jAstJsxOpeningFragment opening) {
        this.opening = AssertionUtils.notNull(opening, "Opening");
        this.opening.setParent(this);
        return this;
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
