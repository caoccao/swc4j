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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrValue;
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

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, name = "JSXAttr")
public class Swc4jAstJsxAttr
        extends Swc4jAst
        implements ISwc4jAstJsxAttrOrSpread {
    protected ISwc4jAstJsxAttrName name;
    protected Optional<ISwc4jAstJsxAttrValue> value;

    @Jni2RustMethod
    public Swc4jAstJsxAttr(
            ISwc4jAstJsxAttrName name,
            @Jni2RustParam(optional = true) ISwc4jAstJsxAttrValue value,
            Swc4jSpan span) {
        super(span);
        setName(name);
        setValue(value);
    }

    public static Swc4jAstJsxAttr create(ISwc4jAstJsxAttrName name) {
        return create(name, null);
    }

    public static Swc4jAstJsxAttr create(ISwc4jAstJsxAttrName name, ISwc4jAstJsxAttrValue value) {
        return new Swc4jAstJsxAttr(name, value, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(name);
        value.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstJsxAttrName getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxAttr;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstJsxAttrValue> getValue() {
        return value;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (name == oldNode && newNode instanceof ISwc4jAstJsxAttrName) {
            setName((ISwc4jAstJsxAttrName) newNode);
            return true;
        }
        if (value.isPresent() && value.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstJsxAttrValue)) {
            setValue((ISwc4jAstJsxAttrValue) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstJsxAttr setName(ISwc4jAstJsxAttrName name) {
        this.name = AssertionUtils.notNull(name, "Name");
        this.name.setParent(this);
        return this;
    }

    public Swc4jAstJsxAttr setValue(ISwc4jAstJsxAttrValue value) {
        this.value = Optional.ofNullable(value);
        this.value.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitJsxAttr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
