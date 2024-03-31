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

import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;

import java.util.List;

/**
 * The interface Swc4j ast program.
 *
 * @param <AST> the type parameter
 * @since 0.2.0
 */
public interface ISwc4jAstProgram<AST extends ISwc4jAst> extends ISwc4jAst {
    /**
     * As module ast module.
     *
     * @return the ast module
     * @since 0.2.0
     */
    default Swc4jAstModule asModule() {
        return (this instanceof Swc4jAstModule) ? (Swc4jAstModule) this : null;
    }

    /**
     * As script ast script.
     *
     * @return the ast script
     * @since 0.2.0
     */
    default Swc4jAstScript asScript() {
        return (this instanceof Swc4jAstScript) ? (Swc4jAstScript) this : null;
    }

    /**
     * Gets body.
     *
     * @return the body
     * @since 0.2.0
     */
    List<AST> getBody();

    /**
     * Gets shebang.
     *
     * @return the shebang
     * @since 0.2.0
     */
    String getShebang();
}
