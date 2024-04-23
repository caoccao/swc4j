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

import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethodMode;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

/**
 * The type Swc4j transform output.
 *
 * @since 0.5.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Outputs)
public class Swc4jTransformOutput {
    /**
     * The Code.
     *
     * @since 0.5.0
     */
    protected final String code;
    /**
     * The Media type.
     *
     * @since 0.5.0
     */
    protected final Swc4jMediaType mediaType;
    /**
     * The Parse mode.
     *
     * @since 0.5.0
     */
    protected final Swc4jParseMode parseMode;
    /**
     * The Source map.
     *
     * @since 0.5.0
     */
    protected final String sourceMap;

    /**
     * Instantiates a new Swc4j transform output.
     *
     * @param code      the code
     * @param mediaType the media type
     * @param parseMode the parse mode
     * @param sourceMap the source map
     * @since 0.5.0
     */
    @Jni2RustMethod(mode = Jni2RustMethodMode.DefinitionOnly)
    public Swc4jTransformOutput(
            String code,
            Swc4jMediaType mediaType,
            Swc4jParseMode parseMode,
            String sourceMap) {
        this.code = code;
        this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        this.parseMode = AssertionUtils.notNull(parseMode, "Parse mode");
        this.sourceMap = sourceMap;
    }

    /**
     * Gets code.
     *
     * @return the code
     * @since 0.5.0
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets media type.
     *
     * @return the media type
     * @since 0.5.0
     */
    public Swc4jMediaType getMediaType() {
        return mediaType;
    }

    /**
     * Gets parse mode.
     *
     * @return the parse mode
     * @since 0.5.0
     */
    public Swc4jParseMode getParseMode() {
        return parseMode;
    }

    /**
     * Gets source map.
     *
     * @return the source map
     * @since 0.5.0
     */
    public String getSourceMap() {
        return sourceMap;
    }
}
