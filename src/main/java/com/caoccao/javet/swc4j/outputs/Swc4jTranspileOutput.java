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

package com.caoccao.javet.swc4j.outputs;

import com.caoccao.javet.swc4j.ast.program.Swc4jAstProgram;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.tokens.Swc4jToken;

import java.util.List;

/**
 * The type Swc4j transpile output.
 *
 * @since 0.1.0
 */
public class Swc4jTranspileOutput extends Swc4jParseOutput {
    /**
     * The Code.
     *
     * @since 0.1.0
     */
    protected String code;
    /**
     * The Source map.
     *
     * @since 0.1.0
     */
    protected String sourceMap;

    /**
     * Instantiates a new Swc4j transpile output.
     *
     * @param code       the code
     * @param mediaType  the media type
     * @param module     the module
     * @param script     the script
     * @param sourceMap  the source map
     * @param sourceText the source text
     * @param tokens     the tokens
     * @since 0.1.0
     */
    public Swc4jTranspileOutput(
            Swc4jAstProgram<?> program,
            String code,
            Swc4jMediaType mediaType,
            boolean module,
            boolean script,
            String sourceMap,
            String sourceText,
            List<Swc4jToken> tokens) {
        super(program, mediaType, module, script, sourceText, tokens);
        this.code = code;
        this.sourceMap = sourceMap;
    }

    /**
     * Transpiled text.
     *
     * @return the code
     * @since 0.1.0
     */
    public String getCode() {
        return code;
    }

    /**
     * Source map back to the original file.
     *
     * @return the source map
     * @since 0.1.0
     */
    public String getSourceMap() {
        return sourceMap;
    }
}
