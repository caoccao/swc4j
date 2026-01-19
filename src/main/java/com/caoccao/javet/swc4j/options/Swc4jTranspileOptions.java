/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

import com.caoccao.javet.swc4j.enums.*;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.net.URL;

/**
 * The type Swc4j transpile options.
 *
 * @since 0.1.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public class Swc4jTranspileOptions extends Swc4jParseOptions {
    /**
     * Kind of decorators to use.
     *
     * @since 1.7.0
     */
    protected Swc4jDecoratorsTranspileOption decorators;
    /**
     * What to do with import statements that only import types i.e. whether to
     * remove them (`Remove`), keep them as side-effect imports (`Preserve`)
     * or error (`Error`). Defaults to `Remove`.
     *
     * @since 0.1.0
     */
    protected Swc4jImportsNotUsedAsValues importsNotUsedAsValues;
    /**
     * Should the sources be inlined in the source map.
     * Defaults to `true`.
     *
     * @since 0.1.0
     */
    protected boolean inlineSources;
    /**
     * Options for transforming JSX. Will not transform when `None`.
     * Defaults to null.
     *
     * @since 1.7.0
     */
    protected Swc4jJsxRuntimeOption jsx;
    /**
     * Whether to keep comments in the output. Defaults to `false`.
     *
     * @since 0.3.0
     */
    protected boolean keepComments;
    /**
     * The kind of module being transpiled.
     * Defaults to being derived from the media type of the parsed source.
     *
     * @since 1.2.0
     */
    protected Swc4jModuleKind moduleKind;
    /**
     * How and if source maps should be generated.
     *
     * @since 0.1.0
     */
    protected Swc4jSourceMapOption sourceMap;
    /**
     * Should import declarations be transformed to variable declarations using
     * a dynamic import. This is useful for import and export declaration support
     * in script contexts such as the Deno REPL.  Defaults to `false`.
     *
     * @since 0.1.0
     */
    protected boolean varDeclImports;
    /**
     * `true` changes type stripping behaviour so that _only_ `type` imports are stripped.
     *
     * @since 1.2.0
     */
    protected boolean verbatimModuleSyntax;

    /**
     * Instantiates a new Swc4j transpile options.
     *
     * @since 0.1.0
     */
    public Swc4jTranspileOptions() {
        super();
        setDecorators(Swc4jDecoratorsTranspileOption.None());
        setImportsNotUsedAsValues(Swc4jImportsNotUsedAsValues.Remove);
        setJsx(null);
        setInlineSources(true);
        setKeepComments(false);
        setModuleKind(Swc4jModuleKind.Auto);
        setSourceMap(Swc4jSourceMapOption.Inline);
        setVarDeclImports(false);
        setVerbatimModuleSyntax(false);
    }

    /**
     * Gets decorators.
     *
     * @return the decorators
     * @since 1.7.0
     */
    @Jni2RustMethod
    public Swc4jDecoratorsTranspileOption getDecorators() {
        return decorators;
    }

    /**
     * Gets imports not used as values.
     *
     * @return the imports not used as values
     * @since 0.1.0
     */
    @Jni2RustMethod
    public Swc4jImportsNotUsedAsValues getImportsNotUsedAsValues() {
        return importsNotUsedAsValues;
    }

    /**
     * Gets jsx.
     *
     * @return the jsx
     * @since 1.7.0
     */
    @Jni2RustMethod(optional = true)
    public Swc4jJsxRuntimeOption getJsx() {
        return jsx;
    }

    /**
     * Gets module kind.
     *
     * @return the module kind
     * @since 1.2.0
     */
    @Jni2RustMethod
    public Swc4jModuleKind getModuleKind() {
        return moduleKind;
    }

    /**
     * Gets source map.
     *
     * @return the source map
     * @since 0.3.0
     */
    @Jni2RustMethod
    public Swc4jSourceMapOption getSourceMap() {
        return sourceMap;
    }

    /**
     * Is inline sources.
     *
     * @return true : yes, false : no
     * @since 0.1.0
     */
    @Jni2RustMethod
    public boolean isInlineSources() {
        return inlineSources;
    }

    /**
     * Is keep comments.
     *
     * @return true : yes, false : no
     * @since 0.3.0
     */
    @Jni2RustMethod
    public boolean isKeepComments() {
        return keepComments;
    }

    /**
     * Is var decl imports.
     *
     * @return true : yes, false : no
     * @since 0.1.0
     */
    @Jni2RustMethod
    public boolean isVarDeclImports() {
        return varDeclImports;
    }

    /**
     * Is verbatim module syntax.
     *
     * @return true : yes, false : no
     * @since 1.2.0
     */
    @Jni2RustMethod
    public boolean isVerbatimModuleSyntax() {
        return verbatimModuleSyntax;
    }

    @Override
    public Swc4jTranspileOptions setCaptureAst(boolean captureAst) {
        super.setCaptureAst(captureAst);
        return this;
    }

    @Override
    public Swc4jTranspileOptions setCaptureComments(boolean captureComments) {
        super.setCaptureComments(captureComments);
        return this;
    }

    @Override
    public Swc4jTranspileOptions setCaptureTokens(boolean captureTokens) {
        super.setCaptureTokens(captureTokens);
        return this;
    }

    /**
     * Sets decorators.
     *
     * @param decorators the decorators
     * @return the self
     * @since 1.7.0
     */
    public Swc4jTranspileOptions setDecorators(Swc4jDecoratorsTranspileOption decorators) {
        this.decorators = decorators;
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
     * Sets jsx.
     *
     * @param jsx the jsx
     * @return the self
     * @since 1.7.0
     */
    public Swc4jTranspileOptions setJsx(Swc4jJsxRuntimeOption jsx) {
        this.jsx = jsx;
        return this;
    }

    /**
     * Sets keep comments.
     *
     * @param keepComments the keep comments
     * @return the self
     * @since 0.3.0
     */
    public Swc4jTranspileOptions setKeepComments(boolean keepComments) {
        this.keepComments = keepComments;
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
     * Sets module kind.
     *
     * @param moduleKind the module kind
     * @return the self
     * @since 1.2.0
     */
    public Swc4jTranspileOptions setModuleKind(Swc4jModuleKind moduleKind) {
        this.moduleKind = AssertionUtils.notNull(moduleKind, "Module kind");
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

    @Override
    public Swc4jTranspileOptions setPluginHost(ISwc4jPluginHost pluginHost) {
        super.setPluginHost(pluginHost);
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
    public Swc4jTranspileOptions setSourceMap(Swc4jSourceMapOption sourceMap) {
        this.sourceMap = AssertionUtils.notNull(sourceMap, "Source map");
        return this;
    }

    /**
     * Sets Specifier of the source text.
     *
     * @param specifier the Specifier of the source text
     * @return the self
     * @since 0.1.0
     */
    public Swc4jTranspileOptions setSpecifier(URL specifier) {
        super.setSpecifier(specifier);
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

    /**
     * Sets verbatim module syntax.
     *
     * @param verbatimModuleSyntax the verbatim module syntax
     * @return the self
     * @since 1.2.0
     */
    public Swc4jTranspileOptions setVerbatimModuleSyntax(boolean verbatimModuleSyntax) {
        this.verbatimModuleSyntax = verbatimModuleSyntax;
        return this;
    }
}
