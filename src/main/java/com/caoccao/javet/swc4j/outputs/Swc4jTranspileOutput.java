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
public final class Swc4jTranspileOutput {
    private String code;
    private boolean module;
    private String sourceMap;

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
        this(code, null, false);
    }

    /**
     * Instantiates a new Swc4j transpile output.
     *
     * @param code      the code
     * @param sourceMap the source map
     * @param module    the module
     * @since 0.1.0
     */
    public Swc4jTranspileOutput(String code, String sourceMap, boolean module) {
        setCode(code);
        setModule(module);
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
     * Gets if this source is a module.
     *
     * @return the boolean
     * @since 0.1.0
     */
    public boolean isModule() {
        return module;
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

    /**
     * Sets module.
     *
     * @param module the module
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOutput setModule(boolean module) {
        this.module = module;
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
}
