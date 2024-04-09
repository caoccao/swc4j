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

import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;

import java.util.List;

/**
 * The interface Swc4j ast.
 *
 * @since 0.2.0
 */
public interface ISwc4jAst {
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
     * Gets span.
     *
     * @return the span
     * @since 0.2.0
     */
    Swc4jAstSpan getSpan();

    /**
     * Gets type.
     *
     * @return the type
     * @since 0.2.0
     */
    Swc4jAstType getType();

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
     * Visit the ast.
     *
     * @param visitor the visitor
     * @return the ast visitor response
     * @since 0.2.0
     */
    default Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        for (ISwc4jAst node : getChildNodes()) {
            switch (node.visit(visitor)) {
                case Error:
                    return Swc4jAstVisitorResponse.Error;
                case OkAndBreak:
                    return Swc4jAstVisitorResponse.OkAndContinue;
                default:
                    break;
            }
        }
        return Swc4jAstVisitorResponse.OkAndContinue;
    }
}
