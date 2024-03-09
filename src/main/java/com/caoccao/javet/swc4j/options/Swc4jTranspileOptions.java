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
    private boolean inlineSourceMap;
    private boolean inlineSources;
    private Swc4jMediaType mediaType;
    private boolean sourceMap;
    private String specifier;

    /**
     * Instantiates a new Swc4j transpile options.
     *
     * @since 0.1.0
     */
    public Swc4jTranspileOptions() {
        setInlineSourceMap(true);
        setInlineSources(true);
        setSpecifier(DEFAULT_SPECIFIER);
        setMediaType(Swc4jMediaType.JavaScript);
        setSourceMap(false);
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
     * Should the source map be inlined, or provided as a separate string. Defaults to `true`.
     *
     * @return true : source map is inlined, false : source map is separated
     * @since 0.1.0
     */
    public boolean isInlineSourceMap() {
        return inlineSourceMap;
    }

    /**
     * Should the sources be inlined in the source map. Defaults to `true`.
     *
     * @return true : source is inlined, false : source is not inlined
     * @since 0.1.0
     */
    public boolean isInlineSources() {
        return inlineSources;
    }

    /**
     * Should a corresponding map string be created for the output.
     * This should be false if isInlineSourceMap() is true. Defaults to `false`.
     *
     * @return true : source map string is separated, false : source map string is not separated
     * @since 0.1.0
     */
    public boolean isSourceMap() {
        return sourceMap;
    }

    /**
     * Sets inline source map.
     *
     * @param inlineSourceMap the inline source map
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setInlineSourceMap(boolean inlineSourceMap) {
        this.inlineSourceMap = inlineSourceMap;
        return this;
    }

    /**
     * Sets inline sources.
     *
     * @param inlineSources the inline sources
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setInlineSources(boolean inlineSources) {
        this.inlineSources = inlineSources;
        return this;
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
     * Sets source map.
     *
     * @param sourceMap the source map
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setSourceMap(boolean sourceMap) {
        this.sourceMap = sourceMap;
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
