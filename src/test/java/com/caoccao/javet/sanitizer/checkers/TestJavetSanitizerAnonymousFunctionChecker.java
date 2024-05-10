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

package com.caoccao.javet.sanitizer.checkers;

import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerError;
import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@SuppressWarnings("ThrowableNotThrown")
public class TestJavetSanitizerAnonymousFunctionChecker extends BaseTestSuiteCheckers {
    @BeforeEach
    public void beforeEach() {
        checker = new JavetSanitizerAnonymousFunctionChecker();
    }

    @Test
    public void testInvalidCases() {
        assertException(
                "function() {}",
                JavetSanitizerError.ParsingError,
                "Expected ident at file:///main.js:1:9\n" +
                        "\n" +
                        "  function() {}\n" +
                        "          ~");
        assertException(
                "function(a, b) {}",
                JavetSanitizerError.ParsingError,
                "Expected ident at file:///main.js:1:9\n" +
                        "\n" +
                        "  function(a, b) {}\n" +
                        "          ~");
        SimpleList.of("", "   ", null).forEach(code ->
                assertException(
                        code,
                        JavetSanitizerError.EmptyCodeString,
                        JavetSanitizerError.EmptyCodeString.getFormat()));
        assertException(
                "function a() {}",
                JavetSanitizerError.InvalidNode,
                "AST node FnDecl is unexpected. Expecting ExprStmt in Anonymous Function.",
                0, 15, 1, 1);
        assertException(
                "const a;",
                JavetSanitizerError.InvalidNode,
                "AST node VarDecl is unexpected. Expecting ExprStmt in Anonymous Function.",
                0, 8, 1, 1);
        assertException(
                "(() => {})()",
                JavetSanitizerError.InvalidNode,
                "AST node CallExpr is unexpected. Expecting ArrowExpr in Anonymous Function.",
                0, 12, 1, 1);
        assertException(
                "() => {}; const a;",
                JavetSanitizerError.NodeCountTooLarge,
                "AST node count 2 is greater than the maximal AST node count 1.",
                0, 18, 1, 1);
    }

    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = SimpleList.of(
                "() => 1", "() => {}", "(a, b) => { a + b; }");
        for (String statement : statements) {
            checker.check(statement);
        }
    }
}
