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

package com.caoccao.javet.swc4j.ast.interfaces;

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The interface Swc4j ast.
 *
 * @since 0.2.0
 */
public interface ISwc4jAst {
    /**
     * As a given type.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the given type
     * @since 0.4.0
     */
    @SuppressWarnings("unchecked")
    default <T> T as(Class<T> clazz) {
        return clazz.isAssignableFrom(getClass()) ? (T) this : null;
    }

    /**
     * Eval to a new optional AST node.
     *
     * @return the optional AST node
     * @since 0.8.0
     */
    default Optional<ISwc4jAst> eval() {
        return Optional.empty();
    }

    /**
     * Find by class without depth limit.
     *
     * @param <T>   the type parameter
     * @param clazz the class
     * @return the list of AST nodes
     * @since 1.3.0
     */
    default <T extends ISwc4jAst> List<ISwc4jAst> find(Class<T> clazz) {
        return find(clazz, -1);
    }

    /**
     * Find by class with depth limit.
     *
     * @param <T>   the type parameter
     * @param clazz the class
     * @param depth the depth
     * @return the list of AST nodes
     * @since 1.3.0
     */
    default <T extends ISwc4jAst> List<ISwc4jAst> find(Class<T> clazz, int depth) {
        AssertionUtils.notNull(clazz, "Class");
        List<ISwc4jAst> nodes = SimpleList.of();
        getChildNodes().forEach((childNode) -> {
            if (clazz.isAssignableFrom(childNode.getClass())) {
                nodes.add(childNode);
            }
            if (depth != 0) {
                final int newDepth = depth > 0 ? depth - 1 : depth;
                nodes.addAll(childNode.find(clazz, newDepth));
            }
        });
        return nodes;
    }

    /**
     * Gets children.
     *
     * @return the children
     * @since 0.2.0
     */
    List<ISwc4jAst> getChildNodes();

    /**
     * Gets parent.
     *
     * @return the parent
     * @since 0.2.0
     */
    ISwc4jAst getParent();

    /**
     * Gets parent.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the parent
     * @since 0.2.0
     */
    @SuppressWarnings("unchecked")
    default <T extends ISwc4jAst> T getParent(Class<T> clazz) {
        ISwc4jAst parent = getParent();
        while (parent != null && !clazz.isAssignableFrom(parent.getClass())) {
            parent = parent.getParent();
        }
        return (T) parent;
    }

    /**
     * Gets span.
     *
     * @return the span
     * @since 0.2.0
     */
    Swc4jSpan getSpan();

    /**
     * Gets type.
     *
     * @return the type
     * @since 0.2.0
     */
    @Jni2RustMethod
    Swc4jAstType getType();

    /**
     * Is infinity.
     *
     * @return true : yes, false : no
     * @since 0.8.0
     */
    default boolean isInfinity() {
        return false;
    }

    /**
     * Is NaN.
     *
     * @return true : yes, false : no
     * @since 0.8.0
     */
    default boolean isNaN() {
        return false;
    }

    /**
     * Is undefined.
     *
     * @return true : yes, false : no
     * @since 0.8.0
     */
    default boolean isUndefined() {
        return false;
    }

    /**
     * Replace the old node with the new node.
     *
     * @param oldNode the old node
     * @param newNode the new node
     * @return true : replaced, false : not replaced
     * @since 0.8.0
     */
    boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode);

    /**
     * Sets parent.
     *
     * @param parent the parent
     * @since 0.2.0
     */
    void setParent(ISwc4jAst parent);

    /**
     * To debug string.
     *
     * @return the string
     * @since 0.2.0
     */
    String toDebugString();

    /**
     * Update parent.
     *
     * @since 0.8.0
     */
    void updateParent();

    /**
     * Visit the ast.
     *
     * @param visitor the visitor
     * @return the ast visitor response
     * @since 0.2.0
     */
    default Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        AssertionUtils.notNull(visitor, "Visitor");
        for (ISwc4jAst node : getChildNodes()) {
            if (node != null) {
                switch (node.visit(visitor)) {
                    case Error:
                        return Swc4jAstVisitorResponse.Error;
                    case OkAndBreak:
                        return Swc4jAstVisitorResponse.OkAndContinue;
                    default:
                        break;
                }
            }
        }
        return Swc4jAstVisitorResponse.OkAndContinue;
    }
}
