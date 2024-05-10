/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.checkers;

import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;

/**
 * The type Base javet sanitizer checker.
 *
 * @since 0.7.0
 */
public abstract class BaseJavetSanitizerChecker implements IJavetSanitizerChecker {
    /**
     * The constant swc4j.
     *
     * @since 0.7.0
     */
    protected static final Swc4j swc4j = new Swc4j();
    /**
     * The Option.
     *
     * @since 0.7.0
     */
    protected JavetSanitizerOptions options;
    /**
     * The Root parser.
     *
     * @since 0.7.0
     */
    protected ISwc4jAstProgram<? extends ISwc4jAst> program;

    /**
     * Instantiates a new Base javet sanitizer checker.
     *
     * @since 0.7.0
     */
    public BaseJavetSanitizerChecker() {
        this(JavetSanitizerOptions.Default);
    }

    /**
     * Instantiates a new Base javet sanitizer checker.
     *
     * @param options the option
     * @since 0.7.0
     */
    public BaseJavetSanitizerChecker(JavetSanitizerOptions options) {
        this.options = AssertionUtils.notNull(options, "Options");
        reset();
    }

    @Override
    public void check(String codeString) throws JavetSanitizerException {
        reset();
        validateBlank(codeString);
        Swc4jParseOptions parseOptions = new Swc4jParseOptions()
                .setMediaType(options.getMediaType())
                .setParseMode(options.getParseMode())
                .setSpecifier(options.getSpecifier())
                .setCaptureAst(true);
        try {
            Swc4jParseOutput output = swc4j.parse(codeString, parseOptions);
            program = output.getProgram();
        } catch (Swc4jCoreException e) {
            throw JavetSanitizerException.parsingError(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends ISwc4jAst> T expectNode(ISwc4jAst node, Swc4jAstType expectedType) throws JavetSanitizerException {
        if (node.getType() != expectedType) {
            throw JavetSanitizerException.invalidNode(
                    getName(),
                    expectedType.name(),
                    node.getType().name()).setNode(node);
        }
        return (T) node.as(expectedType.getAstClass());
    }

    public abstract String getName();

    @Override
    public JavetSanitizerOptions getOptions() {
        return options;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <AST extends ISwc4jAst> ISwc4jAstProgram<AST> getProgram() {
        return (ISwc4jAstProgram<AST>) program;
    }

    /**
     * Reset.
     *
     * @since 0.7.0
     */
    protected void reset() {
        program = null;
    }

    /**
     * Validate blank.
     *
     * @param codeString the code string
     * @throws JavetSanitizerException the javet sanitizer exception
     * @since 0.7.0
     */
    protected void validateBlank(String codeString) throws JavetSanitizerException {
        if (StringUtils.isBlank(codeString)) {
            throw JavetSanitizerException.emptyCodeString();
        }
    }
}
