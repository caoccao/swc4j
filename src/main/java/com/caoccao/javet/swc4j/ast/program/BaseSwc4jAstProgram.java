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

/**
 * The type Swc4j ast program.
 *
 * @since 0.2.0
 */
public class BaseSwc4jAstProgram extends BaseSwc4jAst {
    /**
     * The Shebang.
     *
     * @since 0.2.0
     */
    protected final String shebang;

    /**
     * Instantiates a new Swc4j ast program.
     *
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    public BaseSwc4jAstProgram(Swc4jAstType type, String shebang, int startPosition, int endPosition) {
        super(type, startPosition, endPosition);
        this.shebang = shebang;
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
