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

import java.net.URL;

/**
 * The type Swc4j parse options.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public class Swc4jParseOptions extends Swc4jOptions {
    /**
     * Whether to capture ast or not.
     *
     * @since 0.2.0
     */
    protected boolean captureAst;
    /**
     * Whether to capture comments or not.
     *
     * @since 0.4.0
     */
    protected boolean captureComments;
    /**
     * Whether to capture tokens or not.
     *
     * @since 0.2.0
     */
    protected boolean captureTokens;
    /**
     * Whether to apply swc's scope analysis.
     *
     * @since 0.2.0
     */
    protected boolean scopeAnalysis;

    /**
     * Instantiates a new Swc4j parse options.
     *
     * @since 0.2.0
     */
    public Swc4jParseOptions() {
        super();
        setCaptureAst(false);
        setCaptureComments(false);
        setCaptureTokens(false);
        setScopeAnalysis(false);
    }

    /**
     * Is capture ast.
     *
     * @return true : yes, false : no
     * @since 0.2.0
     */
    @Jni2RustMethod
    public boolean isCaptureAst() {
        return captureAst;
    }

    /**
     * Is capture comments.
     *
     * @return true : capture comments, false : not capture comments
     * @since 0.4.0
     */
    @Jni2RustMethod
    public boolean isCaptureComments() {
        return captureComments;
    }

    /**
     * Is capture tokens.
     *
     * @return true : yes, false : no
     * @since 0.2.0
     */
    @Jni2RustMethod
    public boolean isCaptureTokens() {
        return captureTokens;
    }

    /**
     * Is scope analysis.
     *
     * @return true : yes, false : no
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
     * Sets capture comments.
     *
     * @param captureComments the capture comments
     * @return the self
     * @since 0.4.0
     */
    public Swc4jParseOptions setCaptureComments(boolean captureComments) {
        this.captureComments = captureComments;
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

    @Override
    public Swc4jParseOptions setMediaType(Swc4jMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }

    @Override
    public Swc4jParseOptions setParseMode(Swc4jParseMode parseMode) {
        super.setParseMode(parseMode);
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

    @Override
    public Swc4jParseOptions setSpecifier(URL specifier) {
        super.setSpecifier(specifier);
        return this;
    }
}
