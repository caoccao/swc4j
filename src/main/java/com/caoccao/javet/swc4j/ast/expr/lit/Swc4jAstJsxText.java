/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementChild;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

/**
 * The type swc4j ast jsx text.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, name = "JSXText")
public class Swc4jAstJsxText
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstJsxElementChild {
    /**
     * The Raw.
     */
    @Jni2RustField(atom = true)
    protected String raw;
    /**
     * The Value.
     */
    @Jni2RustField(atom = true)
    protected String value;

    /**
     * Instantiates a new swc4j ast jsx text.
     *
     * @param value the value
     * @param raw   the raw
     * @param span  the span
     */
    @Jni2RustMethod
    public Swc4jAstJsxText(
            String value,
            String raw,
            Swc4jSpan span) {
        super(span);
        setRaw(raw);
        setValue(value);
    }

    /**
     * Create swc4j ast jsx text.
     *
     * @param value the value
     * @param raw   the raw
     * @return the swc4j ast jsx text
     */
    public static Swc4jAstJsxText create(String value, String raw) {
        return new Swc4jAstJsxText(value, raw, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    /**
     * Gets raw.
     *
     * @return the raw
     */
    @Jni2RustMethod
    public String getRaw() {
        return raw;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxText;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @Jni2RustMethod
    public String getValue() {
        return value;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    /**
     * Sets raw.
     *
     * @param raw the raw
     * @return the raw
     */
    public Swc4jAstJsxText setRaw(String raw) {
        this.raw = AssertionUtils.notNull(raw, "Raw");
        return this;
    }

    /**
     * Sets value.
     *
     * @param value the value
     * @return the value
     */
    public Swc4jAstJsxText setValue(String value) {
        this.value = AssertionUtils.notNull(value, "Value");
        return this;
    }

    @Override
    public String toString() {
        return raw;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitJsxText(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
