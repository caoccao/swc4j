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

import com.caoccao.javet.swc4j.ast.BaseSwc4jAst;
import com.caoccao.javet.swc4j.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

/**
 * The type Swc4j ast module.
 *
 * @since 0.2.0
 */
public class Swc4jAstModule extends BaseSwc4jAstProgram {
    /**
     * The Body.
     *
     * @since 0.2.0
     */
    protected final List<BaseSwc4jAst> body;

    /**
     * Instantiates a new Swc4j ast module.
     *
     * @param body          the body
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    public Swc4jAstModule(List<BaseSwc4jAst> body, String shebang, int startPosition, int endPosition) {
        super(Swc4jAstType.Module, shebang, startPosition, endPosition);
        this.body = AssertionUtils.notNull(body, "Body");
    }

    /**
     * Gets body.
     *
     * @return the body
     * @since 0.2.0
     */
    public List<BaseSwc4jAst> getBody() {
        return body;
    }
}
