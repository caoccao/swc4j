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

import com.caoccao.javet.swc4j.enums.Swc4jEsVersion;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.net.URL;

/**
 * The type Swc4j transform options.
 *
 * @since 0.5.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public class Swc4jTransformOptions extends Swc4jOptions {
    /**
     * Forces the code generator to use only ascii characters.
     * This is useful for environments that do not support unicode.
     *
     * @since 0.5.0
     */
    protected boolean asciiOnly;
    /**
     * Whether to emit assert for import attributes. Defaults to `false`.
     *
     * @since 0.5.0
     */
    protected boolean emitAssertForImportAttributes;
    /**
     * Should the sources be inlined in the source map. Defaults to `true`.
     *
     * @since 0.5.0
     */
    protected boolean inlineSources;
    /**
     * Whether to keep comments in the output. Defaults to `false`.
     *
     * @since 0.5.0
     */
    protected boolean keepComments;
    /**
     * Whether to minify the code. Defaults to `true`.
     *
     * @since 0.5.0
     */
    protected boolean minify;
    /**
     * If true, the code generator will emit the latest semicolon. Defaults to `false`.
     *
     * @since 0.5.0
     */
    protected boolean omitLastSemi;
    /**
     * How and if source maps should be generated.
     *
     * @since 0.5.0
     */
    protected Swc4jSourceMapOption sourceMap;
    /**
     * The target runtime environment.
     * <p>
     * This defaults to `EsVersion.Latest` because it preserves input as much as possible.
     * <p>
     * Note: This does not verify if output is valid for the target runtime.
     * e.g. `const foo = 1;` with [EsVersion::Es3] will emit as `const foo = 1` without verification.
     * This is because it's not a concern of the code generator.
     *
     * @since 0.5.0
     */
    protected Swc4jEsVersion target;
    /**
     * Instantiates a new Swc4j transform options.
     *
     * @since 0.5.0
     */
    public Swc4jTransformOptions() {
        super();
        setAsciiOnly(false);
        setEmitAssertForImportAttributes(false);
        setInlineSources(true);
        setKeepComments(false);
        setMinify(true);
        setOmitLastSemi(false);
        setSourceMap(Swc4jSourceMapOption.Inline);
        setTarget(Swc4jEsVersion.ESNext);
    }

    /**
     * Gets source map.
     *
     * @return the source map
     * @since 0.5.0
     */
    @Jni2RustMethod
    public Swc4jSourceMapOption getSourceMap() {
        return sourceMap;
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    @Jni2RustMethod
    public Swc4jEsVersion getTarget() {
        return target;
    }

    /**
     * Is ascii only.
     *
     * @return true : yes, false : no
     */
    @Jni2RustMethod
    public boolean isAsciiOnly() {
        return asciiOnly;
    }

    /**
     * Is emit assert for import attributes.
     *
     * @return true : yes, false : no
     */
    @Jni2RustMethod
    public boolean isEmitAssertForImportAttributes() {
        return emitAssertForImportAttributes;
    }

    /**
     * Is inline sources.
     *
     * @return true : yes, false : no
     */
    @Jni2RustMethod
    public boolean isInlineSources() {
        return inlineSources;
    }

    /**
     * Is keep comments.
     *
     * @return true : yes, false : no
     */
    @Jni2RustMethod
    public boolean isKeepComments() {
        return keepComments;
    }

    /**
     * Is minify.
     *
     * @return true : yes, false : no
     * @since 0.5.0
     */
    @Jni2RustMethod
    public boolean isMinify() {
        return minify;
    }

    /**
     * Is omit last semi.
     *
     * @return true : yes, false : no
     */
    @Jni2RustMethod
    public boolean isOmitLastSemi() {
        return omitLastSemi;
    }

    /**
     * Sets ascii only.
     *
     * @param asciiOnly the ascii only
     * @return the self
     * @since 0.5.0
     */
    public Swc4jTransformOptions setAsciiOnly(boolean asciiOnly) {
        this.asciiOnly = asciiOnly;
        return this;
    }

    /**
     * Sets emit assert for import attributes.
     *
     * @param emitAssertForImportAttributes the emit assert for import attributes
     * @return the self
     * @since 0.5.0
     */
    public Swc4jTransformOptions setEmitAssertForImportAttributes(boolean emitAssertForImportAttributes) {
        this.emitAssertForImportAttributes = emitAssertForImportAttributes;
        return this;
    }

    /**
     * Sets inline sources.
     *
     * @param inlineSources the inline sources
     * @return the self
     * @since 0.5.0
     */
    public Swc4jTransformOptions setInlineSources(boolean inlineSources) {
        this.inlineSources = inlineSources;
        return this;
    }

    /**
     * Sets keep comments.
     *
     * @param keepComments the keep comments
     * @return the self
     * @since 0.5.0
     */
    public Swc4jTransformOptions setKeepComments(boolean keepComments) {
        this.keepComments = keepComments;
        return this;
    }

    @Override
    public Swc4jTransformOptions setMediaType(Swc4jMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }

    /**
     * Sets minify.
     *
     * @param minify the minify
     * @return the minify
     * @since 0.5.0
     */
    public Swc4jTransformOptions setMinify(boolean minify) {
        this.minify = minify;
        return this;
    }

    /**
     * Sets omit last semi.
     *
     * @param omitLastSemi the omit last semi
     * @return the self
     * @since 0.5.0
     */
    public Swc4jTransformOptions setOmitLastSemi(boolean omitLastSemi) {
        this.omitLastSemi = omitLastSemi;
        return this;
    }

    @Override
    public Swc4jTransformOptions setParseMode(Swc4jParseMode parseMode) {
        super.setParseMode(parseMode);
        return this;
    }

    @Override
    public Swc4jTransformOptions setPluginHost(ISwc4jPluginHost pluginHost) {
        super.setPluginHost(pluginHost);
        return this;
    }

    /**
     * Sets source map.
     *
     * @param sourceMap the source map
     * @return self source map
     * @since 0.5.0
     */
    public Swc4jTransformOptions setSourceMap(Swc4jSourceMapOption sourceMap) {
        this.sourceMap = AssertionUtils.notNull(sourceMap, "Source map");
        return this;
    }

    @Override
    public Swc4jTransformOptions setSpecifier(URL specifier) {
        super.setSpecifier(specifier);
        return this;
    }

    /**
     * Sets target.
     *
     * @param target the target
     * @return the self
     * @since 0.5.0
     */
    public Swc4jTransformOptions setTarget(Swc4jEsVersion target) {
        this.target = AssertionUtils.notNull(target, "Target");
        return this;
    }
}
