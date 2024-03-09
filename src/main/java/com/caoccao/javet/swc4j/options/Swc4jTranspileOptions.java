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

package com.caoccao.javet.swc4j.options;

import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

/**
 * The type Swc4j transpile options.
 *
 * @since 0.1.0
 */
public final class Swc4jTranspileOptions {
    /**
     * The constant DEFAULT_SPECIFIER.
     *
     * @since 0.1.0
     */
    public static final String DEFAULT_SPECIFIER = "file:///main.js";
    private Swc4jMediaType mediaType;
    private String specifier;

    /**
     * Instantiates a new Swc4j transpile options.
     *
     * @since 0.1.0
     */
    public Swc4jTranspileOptions() {
        setSpecifier(DEFAULT_SPECIFIER);
        setMediaType(Swc4jMediaType.JavaScript);
    }

    /**
     * Gets Media type of the source text.
     *
     * @return the Media type of the source text
     * @since 0.1.0
     */
    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Gets Specifier of the source text.
     *
     * @return the Specifier of the source text
     * @since 0.1.0
     */
    public String getSpecifier() {
        return specifier;
    }

    /**
     * Sets Media type of the source text.
     *
     * @param mediaType the Media type of the source text
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setMediaType(Swc4jMediaType mediaType) {
        this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        return this;
    }

    /**
     * Sets Specifier of the source text.
     *
     * @param specifier the Specifier of the source text
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setSpecifier(String specifier) {
        this.specifier = AssertionUtils.notNull(specifier, "Specifier");
        return this;
    }
}
