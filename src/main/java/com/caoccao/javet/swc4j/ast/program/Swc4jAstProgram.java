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

package com.caoccao.javet.swc4j.ast.program;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

/**
 * The type Swc4j ast program.
 *
 * @param <AST> the type parameter
 * @since 0.2.0
 */
public abstract class Swc4jAstProgram<AST extends Swc4jAst> extends Swc4jAst {
    /**
     * The Body.
     *
     * @since 0.2.0
     */
    protected final List<AST> body;
    /**
     * The Shebang.
     *
     * @since 0.2.0
     */
    protected final String shebang;

    /**
     * Instantiates a new Swc4j ast program.
     *
     * @param type          the type
     * @param body          the body
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    protected Swc4jAstProgram(Swc4jAstType type, List<AST> body, String shebang, int startPosition, int endPosition) {
        super(type, startPosition, endPosition);
        this.body = AssertionUtils.notNull(body, "Body");
        this.shebang = shebang;
        body.forEach(node -> node.setParent(this));
    }

    /**
     * Gets body.
     *
     * @return the body
     * @since 0.2.0
     */
    public List<AST> getBody() {
        return body;
    }

    /**
     * Gets shebang.
     *
     * @return the shebang
     * @since 0.2.0
     */
    public String getShebang() {
        return shebang;
    }
}
