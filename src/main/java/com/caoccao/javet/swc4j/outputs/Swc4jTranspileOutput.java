/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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
     * @since 0.1.0
     */
    public Swc4jTranspileOutput() {
        this(null);
    }

    /**
     * Instantiates a new Swc4j transpile output.
     *
     * @param code the code
     * @since 0.1.0
     */
    public Swc4jTranspileOutput(String code) {
        this(code, false, false, null, null);
    }

    /**
     * Instantiates a new Swc4j transpile output.
     *
     * @param code       the code
     * @param module     the module
     * @param script     the script
     * @param sourceMap  the source map
     * @param sourceText the source text
     * @since 0.1.0
     */
    public Swc4jTranspileOutput(String code, boolean module, boolean script, String sourceMap, String sourceText) {
        super(module, script, sourceText);
        setCode(code);
        setSourceMap(sourceMap);
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

    /**
     * Sets code.
     *
     * @param code the code
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOutput setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public Swc4jTranspileOutput setModule(boolean module) {
        super.setModule(module);
        return this;
    }

    @Override
    public Swc4jTranspileOutput setScript(boolean script) {
        super.setScript(script);
        return this;
    }

    /**
     * Sets source map.
     *
     * @param sourceMap the source map
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOutput setSourceMap(String sourceMap) {
        this.sourceMap = sourceMap;
        return this;
    }

    @Override
    public Swc4jTranspileOutput setSourceText(String sourceText) {
        super.setSourceText(sourceText);
        return this;
    }
}
