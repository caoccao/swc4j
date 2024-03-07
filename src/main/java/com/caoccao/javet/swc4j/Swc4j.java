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
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

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

    public Swc4jTranspileOutput transpile(String code) throws Swc4jCoreException {
        return transpile(code, new Swc4jTranspileOptions());
    }

    @SuppressWarnings("RedundantThrows")
    public Swc4jTranspileOutput transpile(String code, Swc4jTranspileOptions options) throws Swc4jCoreException {
        return (Swc4jTranspileOutput) Swc4jNative.coreTranspile(
                code,
                options.getMediaType().getId(),
                options.getFileName());
    }
}
