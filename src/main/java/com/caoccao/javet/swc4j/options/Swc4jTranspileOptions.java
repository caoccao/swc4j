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

import com.caoccao.javet.swc4j.enums.Swc4jImportsNotUsedAsValues;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

/**
 * The type Swc4j transpile options.
 *
 * @since 0.1.0
 */
@Jni2RustClass(filePath = "rust/src/options.rs")
public class Swc4jTranspileOptions extends Swc4jParseOptions {
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
    /**
     * The Emit metadata.
     *
     * @since 0.1.0
     */
    protected boolean emitMetadata;
    /**
     * The Imports not used as values.
     *
     * @since 0.1.0
     */
    protected Swc4jImportsNotUsedAsValues importsNotUsedAsValues;
    /**
     * The Inline source map.
     *
     * @since 0.1.0
     */
    protected boolean inlineSourceMap;
    /**
     * The Inline sources.
     *
     * @since 0.1.0
     */
    protected boolean inlineSources;
    /**
     * The Jsx automatic.
     *
     * @since 0.1.0
     */
    protected boolean jsxAutomatic;
    /**
     * The Jsx development.
     *
     * @since 0.1.0
     */
    protected boolean jsxDevelopment;
    /**
     * The Jsx factory.
     *
     * @since 0.1.0
     */
    protected String jsxFactory;
    /**
     * The Jsx fragment factory.
     *
     * @since 0.1.0
     */
    protected String jsxFragmentFactory;
    /**
     * The Jsx import source.
     *
     * @since 0.1.0
     */
    protected String jsxImportSource;
    /**
     * The Precompile jsx.
     *
     * @since 0.1.0
     */
    protected boolean precompileJsx;
    /**
     * The Source map.
     *
     * @since 0.1.0
     */
    protected boolean sourceMap;
    /**
     * The Transform jsx.
     *
     * @since 0.1.0
     */
    protected boolean transformJsx;
    /**
     * The Var decl imports.
     *
     * @since 0.1.0
     */
    protected boolean varDeclImports;

    /**
     * Instantiates a new Swc4j transpile options.
     *
     * @since 0.1.0
     */
    public Swc4jTranspileOptions() {
        super();
        setEmitMetadata(false);
        setImportsNotUsedAsValues(Swc4jImportsNotUsedAsValues.Remove);
        setJsxAutomatic(false);
        setJsxDevelopment(false);
        setJsxFactory(DEFAULT_JSX_FACTORY);
        setJsxFragmentFactory(DEFAULT_JSX_FRAGMENT_FACTORY);
        setJsxImportSource(null);
        setInlineSourceMap(true);
        setInlineSources(true);
        setPrecompileJsx(false);
        setSourceMap(false);
        setTransformJsx(true);
        setVarDeclImports(false);
    }

    /**
     * What to do with import statements that only import types i.e. whether to
     * remove them (`Remove`), keep them as side-effect imports (`Preserve`)
     * or error (`Error`). Defaults to `Remove`.
     *
     * @return the imports not used as values
     * @since 0.1.0
     */
    @Jni2RustMethod
    public Swc4jImportsNotUsedAsValues getImportsNotUsedAsValues() {
        return importsNotUsedAsValues;
    }

    /**
     * When transforming JSX, what value should be used for the JSX factory.
     * Defaults to `React.createElement`.
     *
     * @return the jsx factory
     * @since 0.1.0
     */
    @Jni2RustMethod
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
    @Jni2RustMethod
    public String getJsxFragmentFactory() {
        return jsxFragmentFactory;
    }

    /**
     * The string module specifier to implicitly import JSX factories from when transpiling JSX.
     *
     * @return the jsx import source
     * @since 0.1.0
     */
    @Jni2RustMethod(optional = true)
    public String getJsxImportSource() {
        return jsxImportSource;
    }

    /**
     * When emitting a legacy decorator, also emit experimental decorator meta data.
     * Defaults to `false`.
     *
     * @return the boolean
     * @since 0.1.0
     */
    @Jni2RustMethod
    public boolean isEmitMetadata() {
        return emitMetadata;
    }

    /**
     * Should the source map be inlined, or provided as a separate string. Defaults to `true`.
     *
     * @return true : source map is inlined, false : source map is separated
     * @since 0.1.0
     */
    @Jni2RustMethod
    public boolean isInlineSourceMap() {
        return inlineSourceMap;
    }

    /**
     * Should the sources be inlined in the source map. Defaults to `true`.
     *
     * @return true : source is inlined, false : source is not inlined
     * @since 0.1.0
     */
    @Jni2RustMethod
    public boolean isInlineSources() {
        return inlineSources;
    }

    /**
     * `true` if the program should use an implicit JSX import source/the "new" JSX transforms.
     *
     * @return true : automatic, false : not automatic
     * @since 0.1.0
     */
    @Jni2RustMethod
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
    @Jni2RustMethod
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
    @Jni2RustMethod
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
    @Jni2RustMethod
    public boolean isSourceMap() {
        return sourceMap;
    }

    /**
     * Should JSX be transformed. Defaults to `true`.
     *
     * @return true : be transformed, false : not be transformed
     * @since 0.1.0
     */
    @Jni2RustMethod
    public boolean isTransformJsx() {
        return transformJsx;
    }

    /**
     * Should import declarations be transformed to variable declarations using
     * a dynamic import. This is useful for import and export declaration support
     * in script contexts such as the Deno REPL.
     * Defaults to `false`.
     *
     * @return true : transformed, false : not transformed
     * @since 0.1.0
     */
    @Jni2RustMethod
    public boolean isVarDeclImports() {
        return varDeclImports;
    }

    @Override
    public Swc4jTranspileOptions setCaptureAst(boolean captureAst) {
        super.setCaptureAst(captureAst);
        return this;
    }

    @Override
    public Swc4jTranspileOptions setCaptureTokens(boolean captureTokens) {
        super.setCaptureTokens(captureTokens);
        return this;
    }

    /**
     * Sets emit metadata.
     *
     * @param emitMetadata the emit metadata
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setEmitMetadata(boolean emitMetadata) {
        this.emitMetadata = emitMetadata;
        return this;
    }

    /**
     * Sets imports not used as values.
     *
     * @param importsNotUsedAsValues the imports not used as values
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setImportsNotUsedAsValues(Swc4jImportsNotUsedAsValues importsNotUsedAsValues) {
        this.importsNotUsedAsValues = AssertionUtils.notNull(importsNotUsedAsValues, "Imports not used as values");
        return this;
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
        super.setMediaType(mediaType);
        return this;
    }

    /**
     * Sets parse mode.
     *
     * @param parseMode the parse mode
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setParseMode(Swc4jParseMode parseMode) {
        super.setParseMode(parseMode);
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
     * Sets scope analysis.
     *
     * @param scopeAnalysis the scope analysis
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setScopeAnalysis(boolean scopeAnalysis) {
        super.setScopeAnalysis(scopeAnalysis);
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
        super.setSpecifier(specifier);
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

    /**
     * Sets var decl imports.
     *
     * @param varDeclImports the var decl imports
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setVarDeclImports(boolean varDeclImports) {
        this.varDeclImports = varDeclImports;
        return this;
    }
}
