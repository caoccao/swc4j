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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.enums.Swc4jAstType;

import java.util.List;

/**
 * The type Swc4j ast script.
 *
 * @since 0.2.0
 */
public class Swc4jAstScript extends Swc4jAstProgram<ISwc4jAstStmt> {
    /**
     * Instantiates a new Swc4j ast script.
     *
     * @param body          the body
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    public Swc4jAstScript(List<ISwc4jAstStmt> body, String shebang, int startPosition, int endPosition) {
        super(body, shebang, startPosition, endPosition);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Script;
    }
}
