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
 * Statement list checker provides the following checks to validate if a script has at least one statement.
 * 1. Whether `shebang` exists or not per options.
 * 2. `body` has at least 1 node.
 * 3. The nodes are all `Stmt`.
 *
 * @since 0.7.0
 */
public class JavetSanitizerStatementListChecker extends BaseJavetSanitizerChecker {
    /**
     * Instantiates a new Javet sanitizer statement list checker.
     *
     * @since 0.7.0
     */
    public JavetSanitizerStatementListChecker() {
        this(JavetSanitizerOptions.Default);
    }

    /**
     * Instantiates a new Javet sanitizer statement list checker.
     *
     * @param options the options
     * @since 0.7.0
     */
    public JavetSanitizerStatementListChecker(JavetSanitizerOptions options) {
        super(options);
    }

    @Override
    public void check(String codeString) throws JavetSanitizerException {
        super.check(codeString);
        validateShebang(ISwc4jAstStmt.class);
        validateBodyNotEmpty();
        for (ISwc4jAst node : program.getBody()) {
            validateNode(node, ISwc4jAstStmt.class);
        }
    }

    @Override
    public String getName() {
        return "Statement List";
    }
}
