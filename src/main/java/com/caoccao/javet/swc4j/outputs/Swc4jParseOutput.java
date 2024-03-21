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

package com.caoccao.javet.swc4j.outputs;

import com.caoccao.javet.swc4j.ast.BaseSwc4jAstToken;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

/**
 * The type Swc4j parse output.
 *
 * @since 0.2.0
 */
public class Swc4jParseOutput {
    /**
     * The Media type.
     *
     * @since 0.2.0
     */
    protected Swc4jMediaType mediaType;
    /**
     * The Module.
     *
     * @since 0.2.0
     */
    protected boolean module;
    /**
     * The Script.
     *
     * @since 0.2.0
     */
    protected boolean script;
    /**
     * The Source text.
     *
     * @since 0.2.0
     */
    protected String sourceText;
    /**
     * The Tokens.
     *
     * @since 0.2.0
     */
    protected List<BaseSwc4jAstToken> tokens;

    /**
     * Instantiates a new Swc4j parse output.
     *
     * @param mediaType  the media type
     * @param module     the module
     * @param script     the script
     * @param sourceText the source text
     * @param tokens     the tokens
     * @since 0.2.0
     */
    public Swc4jParseOutput(
            Swc4jMediaType mediaType,
            boolean module,
            boolean script,
            String sourceText,
            List<BaseSwc4jAstToken> tokens) {
        setMediaType(mediaType);
        setModule(module);
        setScript(script);
        setSourceText(sourceText);
        this.tokens = tokens;
    }

    /**
     * Gets media type.
     *
     * @return the media type
     * @since 0.2.0
     */
    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Gets source text.
     *
     * @return the source text
     * @since 0.2.0
     */
    public String getSourceText() {
        return sourceText;
    }

    /**
     * Gets tokens.
     *
     * @return the tokens
     * @since 0.2.0
     */
    public List<BaseSwc4jAstToken> getTokens() {
        return tokens;
    }

    /**
     * Gets if this source is a module.
     *
     * @return true : module, false : not module
     * @since 0.2.0
     */
    public boolean isModule() {
        return module;
    }

    /**
     * Gets if this source is a script.
     *
     * @return true : script, false : not script
     * @since 0.2.0
     */
    public boolean isScript() {
        return script;
    }

    /**
     * Sets media type.
     *
     * @param mediaType the media type
     * @return the media type
     * @since 0.2.0
     */
    public Swc4jParseOutput setMediaType(Swc4jMediaType mediaType) {
        this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        return this;
    }

    /**
     * Sets module.
     *
     * @param module the module
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOutput setModule(boolean module) {
        this.module = module;
        return this;
    }

    /**
     * Sets script.
     *
     * @param script the script
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOutput setScript(boolean script) {
        this.script = script;
        return this;
    }

    /**
     * Sets source text.
     *
     * @param sourceText the source text
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOutput setSourceText(String sourceText) {
        this.sourceText = sourceText;
        return this;
    }
}
