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
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.ReflectionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    protected final Swc4jSpan span;
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
            Swc4jSpan span) {
        parent = null;
        this.span = AssertionUtils.notNull(span, "Span");
    }

    @Override
    public ISwc4jAst getParent() {
        return parent;
    }

    @Override
    public Swc4jSpan getSpan() {
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
     * @param name   the name
     * @param indent the indent
     * @since 0.2.0
     */
    protected void toDebugString(List<String> lines, String name, int indent) {
        if (StringUtils.isEmpty(name)) {
            name = StringUtils.EMPTY;
        } else {
            name = name.trim() + " ";
        }
        lines.add(String.format("%s%s%s (%d,%d,%d,%d)",
                StringUtils.repeat(INDENT_STRING, indent),
                name,
                getType().name(),
                span.getStart(),
                span.getEnd(),
                span.getLine(),
                span.getColumn()));
        final int newIndent = indent + 1;
        ReflectionUtils.getDeclaredFields(getClass()).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Optional.ofNullable(field.getAnnotation(Jni2RustField.class))
                        .map(Jni2RustField::ignore)
                        .orElse(false))
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
                            if (o instanceof Swc4jAst) {
                                ((Swc4jAst) o).toDebugString(
                                        lines,
                                        String.format("%s[%d]", field.getName(), i),
                                        newIndent);
                            } else if (o instanceof Optional) {
                                Optional<?> optionalValue = (Optional<?>) o;
                                if (optionalValue.isPresent()) {
                                    value = optionalValue.get();
                                    if (value instanceof Swc4jAst) {
                                        ((Swc4jAst) value).toDebugString(
                                                lines,
                                                String.format("%s[%d]", field.getName(), i),
                                                newIndent);
                                    } else {
                                        value = String.valueOf(value);
                                        lines.add(String.format("%s%s[%d]? = %s",
                                                StringUtils.repeat(INDENT_STRING, newIndent),
                                                field.getName(),
                                                i,
                                                value));
                                    }
                                } else {
                                    lines.add(String.format("%s%s[%d]? = null",
                                            StringUtils.repeat(INDENT_STRING, newIndent),
                                            field.getName(),
                                            i));
                                }
                            } else {
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
                            value = optionalValue.get();
                            if (value instanceof Swc4jAst) {
                                ((Swc4jAst) value).toDebugString(lines, field.getName() + "?", newIndent);
                            } else if (value instanceof List) {
                                List<?> listValue = (List<?>) value;
                                int i = 0;
                                for (Object o : listValue) {
                                    if (o instanceof Swc4jAst) {
                                        ((Swc4jAst) o).toDebugString(
                                                lines,
                                                String.format("%s?[%d]", field.getName(), i),
                                                newIndent);
                                    } else {
                                        value = String.valueOf(o);
                                        lines.add(String.format("%s%s?[%d] = %s",
                                                StringUtils.repeat(INDENT_STRING, newIndent),
                                                field.getName(),
                                                i,
                                                value));
                                    }
                                    ++i;
                                }
                            } else {
                                lines.add(String.format("%s%s? = %s",
                                        StringUtils.repeat(INDENT_STRING, newIndent),
                                        field.getName(),
                                        value));
                            }
                        } else {
                            lines.add(String.format("%s%s? = null",
                                    StringUtils.repeat(INDENT_STRING, newIndent),
                                    field.getName()));
                        }
                    } else if (value instanceof Swc4jAst) {
                        ((Swc4jAst) value).toDebugString(lines, field.getName(), newIndent);
                    } else {
                        lines.add(String.format("%s%s = %s",
                                StringUtils.repeat(INDENT_STRING, newIndent),
                                field.getName(),
                                value));
                    }
                });
    }

    @Override
    public String toDebugString() {
        List<String> lines = new ArrayList<>();
        toDebugString(lines, null, 0);
        return StringUtils.join("\n", lines);
    }

    /**
     * Update parent.
     *
     * @since 0.2.0
     */
    protected void updateParent() {
        getChildNodes().forEach(node -> node.setParent(this));
    }
}
