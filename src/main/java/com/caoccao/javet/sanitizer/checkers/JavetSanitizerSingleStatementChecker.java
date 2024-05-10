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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;

/**
 * The type Javet sanitizer single statement checker.
 *
 * @since 0.7.0
 */
public class JavetSanitizerSingleStatementChecker extends BaseJavetSanitizerChecker {
    /**
     * Instantiates a new Javet sanitizer single statement checker.
     *
     * @since 0.7.0
     */
    public JavetSanitizerSingleStatementChecker() {
        this(JavetSanitizerOptions.Default);
    }

    /**
     * Instantiates a new Javet sanitizer single statement checker.
     *
     * @param options the options
     * @since 0.7.0
     */
    public JavetSanitizerSingleStatementChecker(JavetSanitizerOptions options) {
        super(options);
    }

    @Override
    public void check(String codeString) throws JavetSanitizerException {
        super.check(codeString);
        String expectedNode = Swc4jAstType.getName(ISwc4jAstStmt.class);
        if (program.getShebang().isPresent()) {
            throw JavetSanitizerException.invalidNode(getName(), expectedNode, program.getShebang().get()).setNode(program);
        }
        if (program.getBody().isEmpty()) {
            throw JavetSanitizerException.nodeCountTooSmall(1, program.getBody().size()).setNode(program);
        }
        if (program.getBody().size() > 1) {
            throw JavetSanitizerException.nodeCountTooLarge(1, program.getBody().size()).setNode(program);
        }
        ISwc4jAst node = program.getBody().get(0);
        if (!(node instanceof ISwc4jAstStmt)) {
            throw JavetSanitizerException.invalidNode(getName(), expectedNode, Swc4jAstType.getName(node.getClass())).setNode(program);
        }
    }

    @Override
    public String getName() {
        return "Single Statement";
    }
}
