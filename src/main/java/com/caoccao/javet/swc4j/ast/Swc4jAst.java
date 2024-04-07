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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.ReflectionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The type Swc4j ast.
 *
 * @since 0.2.0
 */
public abstract class Swc4jAst implements ISwc4jAst {
    /**
     * The constant EMPTY_CHILDREN.
     *
     * @since 0.2.0
     */
    protected static final List<ISwc4jAst> EMPTY_CHILD_NODES = SimpleList.immutableOf();
    /**
     * The constant INDENT_STRING.
     *
     * @since 0.2.0
     */
    protected static final String INDENT_STRING = "  ";
    /**
     * The Span.
     *
     * @since 0.2.0
     */
    @Jni2RustField(ignore = true)
    protected final Swc4jAstSpan span;
    /**
     * The Children.
     *
     * @since 0.2.0
     */
    @Jni2RustField(ignore = true)
    protected List<ISwc4jAst> childNodes;
    /**
     * The Parent.
     *
     * @since 0.2.0
     */
    @Jni2RustField(ignore = true)
    protected ISwc4jAst parent;

    /**
     * Instantiates a new Swc4j ast.
     *
     * @param span the span
     * @since 0.2.0
     */
    protected Swc4jAst(
            Swc4jAstSpan span) {
        childNodes = EMPTY_CHILD_NODES;
        parent = null;
        this.span = AssertionUtils.notNull(span, "Span");
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return childNodes;
    }

    @Override
    public ISwc4jAst getParent() {
        return parent;
    }

    @Override
    public Swc4jAstSpan getSpan() {
        return span;
    }

    @Override
    public void setParent(ISwc4jAst parent) {
        this.parent = parent;
    }

    /**
     * To debug string.
     *
     * @param lines  the lines
     * @param indent the indent
     * @since 0.2.0
     */
    protected void toDebugString(List<String> lines, int indent) {
        lines.add(String.format("%s%s (%d,%d) [%d]",
                StringUtils.repeat(INDENT_STRING, indent),
                getType().name(),
                span.getStart(),
                span.getEnd(),
                getChildNodes().size()));
        final int newIndent = indent + 1;
        ReflectionUtils.getDeclaredFields(getClass()).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Optional.ofNullable(field.getAnnotation(Jni2RustField.class))
                        .map(Jni2RustField::ignore)
                        .orElse(false))
                .filter(field -> !ISwc4jAst.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    field.setAccessible(true);
                    Object value;
                    try {
                        value = field.get(this);
                    } catch (IllegalAccessException e) {
                        value = e.getMessage();
                    }
                    if (value instanceof List) {
                        List<?> listValue = (List<?>) value;
                        int i = 0;
                        for (Object o : listValue) {
                            if (!(o instanceof ISwc4jAst)) {
                                value = String.valueOf(o);
                                lines.add(String.format("%s%s[%d] = %s",
                                        StringUtils.repeat(INDENT_STRING, newIndent),
                                        field.getName(),
                                        i,
                                        value));
                            }
                            ++i;
                        }
                    } else if (value instanceof Optional) {
                        Optional<?> optionalValue = (Optional<?>) value;
                        if (optionalValue.isPresent()) {
                            if (!(optionalValue.get() instanceof ISwc4jAst)) {
                                value = String.valueOf(optionalValue.get());
                                lines.add(String.format("%s%s? = %s",
                                        StringUtils.repeat(INDENT_STRING, newIndent),
                                        field.getName(),
                                        value));
                            }
                        }
                    } else {
                        lines.add(String.format("%s%s = %s",
                                StringUtils.repeat(INDENT_STRING, newIndent),
                                field.getName(),
                                value));
                    }
                });
        if (!getChildNodes().isEmpty()) {
            getChildNodes().forEach(node -> ((Swc4jAst) node).toDebugString(lines, newIndent));
        }
    }

    @Override
    public String toDebugString() {
        List<String> lines = new ArrayList<>();
        toDebugString(lines, 0);
        return StringUtils.join("\n", lines);
    }

    /**
     * Update parent.
     *
     * @since 0.2.0
     */
    protected void updateParent() {
        getChildNodes().stream()
                .filter(Objects::nonNull)
                .forEach(node -> node.setParent(this));
    }
}
