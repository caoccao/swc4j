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
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The type Swc4j parse options.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public class Swc4jParseOptions {
    /**
     * The constant DEFAULT_SPECIFIER.
     *
     * @since 0.2.0
     */
    public static final URL DEFAULT_SPECIFIER;

    static {
        try {
            DEFAULT_SPECIFIER = new URL("file://main.js");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The Capture ast.
     *
     * @since 0.2.0
     */
    protected boolean captureAst;
    /**
     * The Capture tokens.
     *
     * @since 0.2.0
     */
    protected boolean captureTokens;
    /**
     * The Media type.
     *
     * @since 0.2.0
     */
    protected Swc4jMediaType mediaType;
    /**
     * The Parse mode.
     *
     * @since 0.2.0
     */
    protected Swc4jParseMode parseMode;
    /**
     * The Scope analysis.
     *
     * @since 0.2.0
     */
    protected boolean scopeAnalysis;
    /**
     * The Specifier.
     *
     * @since 0.2.0
     */
    protected URL specifier;

    /**
     * Instantiates a new Swc4j parse options.
     *
     * @since 0.2.0
     */
    public Swc4jParseOptions() {
        setCaptureAst(false);
        setCaptureTokens(false);
        setMediaType(Swc4jMediaType.JavaScript);
        setParseMode(Swc4jParseMode.Module);
        setScopeAnalysis(false);
        setSpecifier(DEFAULT_SPECIFIER);
    }

    /**
     * Gets Media type of the source text.
     *
     * @return the media type
     * @since 0.2.0
     */
    @Jni2RustMethod
    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Gets parse mode.
     *
     * @return the parse mode
     * @since 0.2.0
     */
    @Jni2RustMethod
    public Swc4jParseMode getParseMode() {
        return parseMode;
    }

    /**
     * Gets Specifier of the source text.
     *
     * @return the specifier
     * @since 0.2.0
     */
    @Jni2RustMethod
    public URL getSpecifier() {
        return specifier;
    }

    /**
     * Is capture ast boolean.
     *
     * @return the boolean
     * @since 0.2.0
     */
    @Jni2RustMethod
    public boolean isCaptureAst() {
        return captureAst;
    }

    /**
     * Whether to capture tokens or not.
     *
     * @return true : capture tokens, false : not capture tokens
     * @since 0.2.0
     */
    @Jni2RustMethod
    public boolean isCaptureTokens() {
        return captureTokens;
    }

    /**
     * Whether to apply swc's scope analysis.
     *
     * @return true : scope analysis, false : not scope analysis
     * @since 0.2.0
     */
    @Jni2RustMethod
    public boolean isScopeAnalysis() {
        return scopeAnalysis;
    }

    /**
     * Sets capture ast.
     *
     * @param captureAst the capture ast
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOptions setCaptureAst(boolean captureAst) {
        this.captureAst = captureAst;
        return this;
    }

    /**
     * Sets capture tokens.
     *
     * @param captureTokens the capture tokens
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOptions setCaptureTokens(boolean captureTokens) {
        this.captureTokens = captureTokens;
        return this;
    }

    /**
     * Sets Media type of the source text.
     *
     * @param mediaType the Media type of the source text
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOptions setMediaType(Swc4jMediaType mediaType) {
        this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        return this;
    }

    /**
     * Sets parse mode.
     *
     * @param parseMode the parse mode
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOptions setParseMode(Swc4jParseMode parseMode) {
        this.parseMode = AssertionUtils.notNull(parseMode, "Parse mode");
        return this;
    }

    /**
     * Sets scope analysis.
     *
     * @param scopeAnalysis the scope analysis
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOptions setScopeAnalysis(boolean scopeAnalysis) {
        this.scopeAnalysis = scopeAnalysis;
        return this;
    }

    /**
     * Sets Specifier of the source text.
     *
     * @param specifier the Specifier of the source text
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOptions setSpecifier(URL specifier) {
        this.specifier = AssertionUtils.notNull(specifier, "Specifier");
        return this;
    }
}
