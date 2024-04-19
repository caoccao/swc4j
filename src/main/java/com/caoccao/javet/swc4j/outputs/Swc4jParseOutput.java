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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.comments.Swc4jComments;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethodMode;
import com.caoccao.javet.swc4j.tokens.Swc4jToken;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

/**
 * The type Swc4j parse output.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Outputs)
public class Swc4jParseOutput {
    /**
     * The Comments.
     *
     * @since 0.4.0
     */
    protected final Swc4jComments comments;
    /**
     * The Media type.
     *
     * @since 0.2.0
     */
    protected final Swc4jMediaType mediaType;
    /**
     * The Parse mode.
     *
     * @since 0.2.0
     */
    protected final Swc4jParseMode parseMode;
    /**
     * The Ast program.
     *
     * @since 0.2.0
     */
    protected final ISwc4jAstProgram<? extends ISwc4jAst> program;
    /**
     * The Source text.
     *
     * @since 0.2.0
     */
    protected final String sourceText;
    /**
     * The Tokens.
     *
     * @since 0.2.0
     */
    protected final List<Swc4jToken> tokens;

    /**
     * Instantiates a new Swc4j parse output.
     *
     * @param program    the program
     * @param mediaType  the media type
     * @param parseMode  the parse mode
     * @param sourceText the source text
     * @param tokens     the tokens
     * @param comments   the comments
     * @since 0.2.0
     */
    @Jni2RustMethod(mode = Jni2RustMethodMode.DefinitionOnly)
    public Swc4jParseOutput(
            ISwc4jAstProgram<? extends ISwc4jAst> program,
            Swc4jMediaType mediaType,
            Swc4jParseMode parseMode,
            String sourceText,
            List<Swc4jToken> tokens,
            Swc4jComments comments) {
        this.comments = comments;
        this.mediaType = AssertionUtils.notNull(mediaType, "Media type");
        this.parseMode = AssertionUtils.notNull(parseMode, "Parse mode");
        this.program = program;
        this.sourceText = AssertionUtils.notNull(sourceText, "Source text");
        this.tokens = tokens;
    }

    /**
     * Gets comments.
     *
     * @return the comments
     * @since 0.4.0
     */
    public Swc4jComments getComments() {
        return comments;
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
     * Gets parse mode.
     *
     * @return the parse mode
     * @since 0.2.0
     */
    public Swc4jParseMode getParseMode() {
        return parseMode;
    }

    /**
     * Gets ast program.
     *
     * @return the ast program
     * @since 0.2.0
     */
    public ISwc4jAstProgram<? extends ISwc4jAst> getProgram() {
        return program;
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
    public List<Swc4jToken> getTokens() {
        return tokens;
    }
}
