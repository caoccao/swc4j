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
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxClosingElement;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxOpeningElement;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustParam;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, name = "JSXElement")
public class Swc4jAstJsxElement
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstJsxElementChild, ISwc4jAstJsxAttrValue {
    protected final List<ISwc4jAstJsxElementChild> children;
    protected Optional<Swc4jAstJsxClosingElement> closing;
    protected Swc4jAstJsxOpeningElement opening;

    @Jni2RustMethod
    public Swc4jAstJsxElement(
            Swc4jAstJsxOpeningElement opening,
            List<ISwc4jAstJsxElementChild> children,
            @Jni2RustParam(optional = true) Swc4jAstJsxClosingElement closing,
            Swc4jSpan span) {
        super(span);
        setClosing(closing);
        setOpening(opening);
        this.children = AssertionUtils.notNull(children, "Children");
        this.children.forEach(node -> node.setParent(this));
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(children);
        childNodes.add(opening);
        closing.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstJsxElementChild> getChildren() {
        return children;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstJsxClosingElement> getClosing() {
        return closing;
    }

    @Jni2RustMethod
    public Swc4jAstJsxOpeningElement getOpening() {
        return opening;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxElement;
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
        if (closing.isPresent() && closing.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstJsxClosingElement)) {
            setClosing((Swc4jAstJsxClosingElement) newNode);
            return true;
        }
        if (opening == oldNode && newNode instanceof Swc4jAstJsxOpeningElement) {
            setOpening((Swc4jAstJsxOpeningElement) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstJsxElement setClosing(Swc4jAstJsxClosingElement closing) {
        this.closing = Optional.ofNullable(closing);
        this.closing.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstJsxElement setOpening(Swc4jAstJsxOpeningElement opening) {
        this.opening = AssertionUtils.notNull(opening, "Opening");
        this.opening.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitJsxElement(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
