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
    /**
     * The constant DEFAULT_JSX_FACTORY.
     *
     * @since 0.1.0
     */
    public static final String DEFAULT_JSX_FACTORY = "React.createElement";
    /**
     * The constant DEFAULT_JSX_FRAGMENT_FACTORY.
     *
     * @since 0.1.0
     */
    public static final String DEFAULT_JSX_FRAGMENT_FACTORY = "React.Fragment";
    private boolean inlineSourceMap;
    private boolean inlineSources;
    private boolean jsxAutomatic;
    private boolean jsxDevelopment;
    private String jsxFactory;
    private String jsxFragmentFactory;
    private String jsxImportSource;
    private Swc4jMediaType mediaType;
    private boolean precompileJsx;
    private boolean sourceMap;
    private String specifier;
    private boolean transformJsx;

    /**
     * Instantiates a new Swc4j transpile options.
     *
     * @since 0.1.0
     */
    public Swc4jTranspileOptions() {
        setJsxAutomatic(false);
        setJsxDevelopment(false);
        setJsxFactory(DEFAULT_JSX_FACTORY);
        setJsxFragmentFactory(DEFAULT_JSX_FRAGMENT_FACTORY);
        setJsxImportSource(null);
        setInlineSourceMap(true);
        setInlineSources(true);
        setMediaType(Swc4jMediaType.JavaScript);
        setPrecompileJsx(false);
        setSourceMap(false);
        setSpecifier(DEFAULT_SPECIFIER);
        setTransformJsx(true);
    }

    /**
     * When transforming JSX, what value should be used for the JSX factory.
     * Defaults to `React.createElement`.
     *
     * @return the jsx factory
     * @since 0.1.0
     */
    public String getJsxFactory() {
        return jsxFactory;
    }

    /**
     * When transforming JSX, what value should be used for the JSX fragment.
     * Defaults to `React.Fragment`.
     *
     * @return the jsx fragment factory
     * @since 0.1.0
     */
    public String getJsxFragmentFactory() {
        return jsxFragmentFactory;
    }

    /**
     * The string module specifier to implicitly import JSX factories from when transpiling JSX.
     *
     * @return the jsx import source
     * @since 0.1.0
     */
    public String getJsxImportSource() {
        return jsxImportSource;
    }

    /**
     * Gets Media type of the source text.
     *
     * @return the media type
     * @since 0.1.0
     */
    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Gets Specifier of the source text.
     *
     * @return the specifier
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
     * `true` if the program should use an implicit JSX import source/the "new" JSX transforms.
     *
     * @return true : automatic, false : not automatic
     * @since 0.1.0
     */
    public boolean isJsxAutomatic() {
        return jsxAutomatic;
    }

    /**
     * If JSX is automatic, if it is in development mode, meaning that it should
     * import `jsx-dev-runtime` and transform JSX using `jsxDEV` import from the
     * JSX import source as well as provide additional debug information to the
     * JSX factory.
     *
     * @return true : development mode, false : not development mode
     * @since 0.1.0
     */
    public boolean isJsxDevelopment() {
        return jsxDevelopment;
    }

    /**
     * Should JSX be precompiled into static strings that need to be concatenated
     * with dynamic content. Defaults to `false`, mutually exclusive with `transform_jsx`.
     *
     * @return true : be precompiled, false : not be precompiled
     * @since 0.1.0
     */
    public boolean isPrecompileJsx() {
        return precompileJsx;
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
     * Should JSX be transformed. Defaults to `true`.
     *
     * @return true : be transformed, false : not be transformed
     * @since 0.1.0
     */
    public boolean isTransformJsx() {
        return transformJsx;
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
     * Sets jsx automatic.
     *
     * @param jsxAutomatic the jsx automatic
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setJsxAutomatic(boolean jsxAutomatic) {
        this.jsxAutomatic = jsxAutomatic;
        return this;
    }

    /**
     * Sets jsx development.
     *
     * @param jsxDevelopment the jsx development
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setJsxDevelopment(boolean jsxDevelopment) {
        this.jsxDevelopment = jsxDevelopment;
        return this;
    }

    /**
     * Sets jsx factory.
     *
     * @param jsxFactory the jsx factory
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setJsxFactory(String jsxFactory) {
        this.jsxFactory = AssertionUtils.notNull(jsxFactory, "Jsx factory");
        return this;
    }

    /**
     * Sets jsx fragment factory.
     *
     * @param jsxFragmentFactory the jsx fragment factory
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setJsxFragmentFactory(String jsxFragmentFactory) {
        this.jsxFragmentFactory = AssertionUtils.notNull(jsxFragmentFactory, "Jsx fragment factory");
        return this;
    }

    /**
     * Sets jsx import source.
     *
     * @param jsxImportSource the jsx import source
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setJsxImportSource(String jsxImportSource) {
        this.jsxImportSource = jsxImportSource;
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
     * Sets precompile jsx.
     *
     * @param precompileJsx the precompile jsx
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setPrecompileJsx(boolean precompileJsx) {
        this.precompileJsx = precompileJsx;
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

    /**
     * Sets transform jsx.
     *
     * @param transformJsx the transform jsx
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setTransformJsx(boolean transformJsx) {
        this.transformJsx = transformJsx;
        return this;
    }
}
