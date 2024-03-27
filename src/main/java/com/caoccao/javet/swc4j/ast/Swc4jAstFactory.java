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
import com.caoccao.javet.swc4j.jni2rust.*;

import java.util.List;

/**
 * The type Swc4j ast factory.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = "rust/src/ast_utils.rs")
public final class Swc4jAstFactory {
    private Swc4jAstFactory() {
    }

    /**
     * Create module ast module.
     *
     * @param body          the body
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast module
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jAstModule createModule(
            List<Swc4jAst> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstModule(body, shebang, startPosition, endPosition);
    }

    /**
     * Create module ast script.
     *
     * @param body          the body
     * @param shebang       the shebang
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast script
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jAstScript createScript(
            List<Swc4jAst> body,
            @Jni2RustParam(optional = true) String shebang,
            @Jni2RustParamStartPosition int startPosition,
            @Jni2RustParamEndPosition int endPosition) {
        return new Swc4jAstScript(body, shebang, startPosition, endPosition);
    }
}
