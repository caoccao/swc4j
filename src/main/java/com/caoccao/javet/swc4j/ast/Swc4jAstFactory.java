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

import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;

/**
 * The type Swc4j ast factory.
 *
 * @since 0.2.0
 */
public final class Swc4jAstFactory {
    private Swc4jAstFactory() {
    }

    /**
     * Create module ast module.
     *
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast module
     * @since 0.2.0
     */
    public static Swc4jAstModule createModule(String shebang, int startPosition, int endPosition) {
        return new Swc4jAstModule(shebang, startPosition, endPosition);
    }

    /**
     * Create module ast script.
     *
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast script
     * @since 0.2.0
     */
    public static Swc4jAstScript createScript(String shebang, int startPosition, int endPosition) {
        return new Swc4jAstScript(shebang, startPosition, endPosition);
    }
}
