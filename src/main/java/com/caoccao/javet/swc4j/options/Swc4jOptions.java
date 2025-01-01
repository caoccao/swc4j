/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The type Swc4j options.
 *
 * @since 0.5.0
 */
public abstract class Swc4jOptions {
    /**
     * The constant DEFAULT_SPECIFIER.
     *
     * @since 0.5.0
     */
    public static final URL DEFAULT_SPECIFIER;

    static {
        try {
            DEFAULT_SPECIFIER = new URL("file:///main.js");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Media type of the source text.
     *
     * @since 0.5.0
     */
    protected Swc4jMediaType mediaType;
    /**
     * Should the code to be parsed as Module or Script.
     * Default is Program that lets SWC to determine the actual parse mode.
     *
     * @since 0.5.0
     */
    protected Swc4jParseMode parseMode;
    /**
     * The Plugin host.
     *
     * @since 0.6.0
     */
    protected ISwc4jPluginHost pluginHost;
    /**
     * Specifier of the source text.
     *
     * @since 0.5.0
     */
    protected URL specifier;

    /**
     * Instantiates a new Swc4j options.
     *
     * @since 0.5.0
     */
    public Swc4jOptions() {
        setMediaType(Swc4jMediaType.TypeScript);
        setParseMode(Swc4jParseMode.Program);
        setPluginHost(null);
        setSpecifier(DEFAULT_SPECIFIER);
    }

    /**
     * Gets media type.
     *
     * @return the media type
     * @since 0.5.0
     */
    @Jni2RustMethod
    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Gets parse mode.
     *
     * @return the parse mode
     * @since 0.5.0
     */
    @Jni2RustMethod
    public Swc4jParseMode getParseMode() {
        return parseMode;
    }

    /**
     * Gets plugin host.
     *
     * @return the plugin host
     * @since 0.6.0
     */
    @Jni2RustMethod(optional = true)
    public ISwc4jPluginHost getPluginHost() {
        return pluginHost;
    }

    /**
     * Gets specifier.
     *
     * @return the specifier
     * @since 0.5.0
     */
    @Jni2RustMethod
    public URL getSpecifier() {
        return specifier;
    }

    /**
     * Sets media type.
     *
     * @param mediaType the media type
     * @return self
     * @since 0.5.0
     */
    public Swc4jOptions setMediaType(Swc4jMediaType mediaType) {
        this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        return this;
    }

    /**
     * Sets parse mode.
     *
     * @param parseMode the parse mode
     * @return self
     * @since 0.5.0
     */
    public Swc4jOptions setParseMode(Swc4jParseMode parseMode) {
        this.parseMode = AssertionUtils.notNull(parseMode, "Parse mode");
        return this;
    }

    /**
     * Sets plugin host.
     *
     * @param pluginHost the plugin host
     * @return the self
     * @since 0.6.0
     */
    public Swc4jOptions setPluginHost(ISwc4jPluginHost pluginHost) {
        this.pluginHost = pluginHost;
        return this;
    }

    /**
     * Sets specifier.
     *
     * @param specifier the specifier
     * @return self
     * @since 0.5.0
     */
    public Swc4jOptions setSpecifier(URL specifier) {
        this.specifier = AssertionUtils.notNull(specifier, "Specifier");
        return this;
    }
}
