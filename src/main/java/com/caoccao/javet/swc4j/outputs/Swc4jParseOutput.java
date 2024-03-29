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
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethodMode;
import com.caoccao.javet.swc4j.tokens.Swc4jToken;

import java.util.List;

/**
 * The type Swc4j parse output.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = "rust/src/outputs.rs")
public class Swc4jParseOutput {
    /**
     * The Media type.
     *
     * @since 0.2.0
     */
    protected final Swc4jMediaType mediaType;
    /**
     * The Module.
     *
     * @since 0.2.0
     */
    protected final boolean module;
    /**
     * The Ast program.
     *
     * @since 0.2.0
     */
    protected final ISwc4jAstProgram<? extends ISwc4jAst> program;
    /**
     * The Script.
     *
     * @since 0.2.0
     */
    protected final boolean script;
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
     * @param module     the module
     * @param script     the script
     * @param sourceText the source text
     * @param tokens     the tokens
     * @since 0.2.0
     */
    @Jni2RustMethod(mode = Jni2RustMethodMode.Manual)
    public Swc4jParseOutput(
            ISwc4jAstProgram<? extends ISwc4jAst> program,
            Swc4jMediaType mediaType,
            boolean module,
            boolean script,
            String sourceText,
            List<Swc4jToken> tokens) {
        this.mediaType = mediaType;
        this.module = module;
        this.program = program;
        this.script = script;
        this.sourceText = sourceText;
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
}
