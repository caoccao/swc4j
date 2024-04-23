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

package com.caoccao.javet.swc4j;

import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

/**
 * The type Swc4j.
 *
 * @since 0.1.0
 */
public final class Swc4j {

    static {
        new Swc4jLibLoader().load();
    }

    /**
     * Instantiates a new Swc4j.
     *
     * @since 0.1.0
     */
    public Swc4j() {
    }

    /**
     * Gets version.
     *
     * @return the version
     * @since 0.1.0
     */
    public String getVersion() {
        return Swc4jNative.coreGetVersion();
    }

    /**
     * Parse with default options.
     *
     * @param code the code
     * @return the swc4j parse output
     * @throws Swc4jCoreException the swc4j core exception
     * @since 0.1.0
     */
    public Swc4jParseOutput parse(String code) throws Swc4jCoreException {
        return parse(code, new Swc4jParseOptions());
    }

    /**
     * Parse.
     *
     * @param code    the code
     * @param options the options
     * @return the swc4j parse output
     * @throws Swc4jCoreException the swc4j core exception
     * @since 0.1.0
     */
    @SuppressWarnings("RedundantThrows")
    public Swc4jParseOutput parse(String code, Swc4jParseOptions options) throws Swc4jCoreException {
        return (Swc4jParseOutput) Swc4jNative.coreParse(
                code,
                AssertionUtils.notNull(options, "Options"));
    }

    /**
     * Transform with default options.
     *
     * @param code the code
     * @return the swc4j transform output
     * @throws Swc4jCoreException the swc4j core exception
     * @since 0.5.0
     */
    public Swc4jTransformOutput transform(String code) throws Swc4jCoreException {
        return transform(code, new Swc4jTransformOptions());
    }

    /**
     * Transform.
     *
     * @param code    the code
     * @param options the options
     * @return the swc4j transform output
     * @throws Swc4jCoreException the swc4j core exception
     * @since 0.5.0
     */
    @SuppressWarnings("RedundantThrows")
    public Swc4jTransformOutput transform(String code, Swc4jTransformOptions options) throws Swc4jCoreException {
        return (Swc4jTransformOutput) Swc4jNative.coreTransform(
                code,
                AssertionUtils.notNull(options, "Options"));
    }

    /**
     * Transpile with default options.
     *
     * @param code the code
     * @return the swc4j transpile output
     * @throws Swc4jCoreException the swc4j core exception
     * @since 0.1.0
     */
    public Swc4jTranspileOutput transpile(String code) throws Swc4jCoreException {
        return transpile(code, new Swc4jTranspileOptions());
    }

    /**
     * Transpile.
     *
     * @param code    the code
     * @param options the options
     * @return the swc4j transpile output
     * @throws Swc4jCoreException the swc4j core exception
     * @since 0.1.0
     */
    @SuppressWarnings("RedundantThrows")
    public Swc4jTranspileOutput transpile(String code, Swc4jTranspileOptions options) throws Swc4jCoreException {
        return (Swc4jTranspileOutput) Swc4jNative.coreTranspile(
                code,
                AssertionUtils.notNull(options, "Options"));
    }
}
