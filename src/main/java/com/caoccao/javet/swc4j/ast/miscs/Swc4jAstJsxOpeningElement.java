/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementName;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, name = "JSXOpeningElement")
public class Swc4jAstJsxOpeningElement
        extends Swc4jAst {
    protected final List<ISwc4jAstJsxAttrOrSpread> attrs;
    protected ISwc4jAstJsxElementName name;
    protected boolean selfClosing;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    @Jni2RustMethod
    public Swc4jAstJsxOpeningElement(
            ISwc4jAstJsxElementName name,
            List<ISwc4jAstJsxAttrOrSpread> attrs,
            boolean selfClosing,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jSpan span) {
        super(span);
        setName(name);
        setSelfClosing(selfClosing);
        setTypeArgs(typeArgs);
        this.attrs = AssertionUtils.notNull(attrs, "Attrs");
        this.attrs.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstJsxOpeningElement create(ISwc4jAstJsxElementName name) {
        return create(name, SimpleList.of());
    }

    public static Swc4jAstJsxOpeningElement create(
            ISwc4jAstJsxElementName name,
            List<ISwc4jAstJsxAttrOrSpread> attrs) {
        return create(name, attrs, false);
    }

    public static Swc4jAstJsxOpeningElement create(
            ISwc4jAstJsxElementName name,
            List<ISwc4jAstJsxAttrOrSpread> attrs,
            boolean selfClosing) {
        return create(name, attrs, selfClosing, null);
    }

    public static Swc4jAstJsxOpeningElement create(
            ISwc4jAstJsxElementName name,
            List<ISwc4jAstJsxAttrOrSpread> attrs,
            boolean selfClosing,
            Swc4jAstTsTypeParamInstantiation typeArgs) {
        return new Swc4jAstJsxOpeningElement(name, attrs, selfClosing, typeArgs, Swc4jSpan.DUMMY);
    }

    @Jni2RustMethod
    public List<ISwc4jAstJsxAttrOrSpread> getAttrs() {
        return attrs;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(attrs);
        childNodes.add(name);
        typeArgs.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstJsxElementName getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxOpeningElement;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    @Jni2RustMethod
    public boolean isSelfClosing() {
        return selfClosing;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!attrs.isEmpty() && newNode instanceof ISwc4jAstJsxAttrOrSpread) {
            final int size = attrs.size();
            for (int i = 0; i < size; i++) {
                if (attrs.get(i) == oldNode) {
                    attrs.set(i, (ISwc4jAstJsxAttrOrSpread) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (name == oldNode && newNode instanceof ISwc4jAstJsxElementName) {
            setName((ISwc4jAstJsxElementName) newNode);
            return true;
        }
        if (typeArgs.isPresent() && typeArgs.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setTypeArgs((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstJsxOpeningElement setName(ISwc4jAstJsxElementName name) {
        this.name = AssertionUtils.notNull(name, "Name");
        this.name.setParent(this);
        return this;
    }

    public Swc4jAstJsxOpeningElement setSelfClosing(boolean selfClosing) {
        this.selfClosing = selfClosing;
        return this;
    }

    public Swc4jAstJsxOpeningElement setTypeArgs(Swc4jAstTsTypeParamInstantiation typeArgs) {
        this.typeArgs = Optional.ofNullable(typeArgs);
        this.typeArgs.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public String toString() {
        return "<" + name + (selfClosing ? "/>" : ">");
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitJsxOpeningElement(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
