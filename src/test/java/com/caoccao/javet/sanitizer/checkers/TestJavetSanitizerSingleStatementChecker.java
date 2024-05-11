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
public class TestJavetSanitizerSingleStatementChecker extends BaseTestSuiteCheckers {
    @BeforeEach
    public void beforeEach() {
        checker = new JavetSanitizerSingleStatementChecker();
    }

    @Test
    public void testInvalidCases() {
        SimpleList.of("", "   ", null).forEach(code ->
                assertException(
                        code,
                        JavetSanitizerError.EmptyCodeString,
                        JavetSanitizerError.EmptyCodeString.getFormat()));
        assertException(
                "a?.b.?.c", // SWC bug
                JavetSanitizerError.ParsingError,
                "Expected ident at file:///main.js:1:6\n" +
                        "\n" +
                        "  a?.b.?.c\n" +
                        "       ~");
        assertException(
                "1 +",
                JavetSanitizerError.ParsingError,
                "Expression expected at file:///main.js:1:3\n" +
                        "\n" +
                        "  1 +\n" +
                        "    ~");
        assertException(
                "{ a: 1, b: 2 }",
                JavetSanitizerError.ParsingError,
                "Expected ';', '}' or <eof> at file:///main.js:1:10\n" +
                        "\n" +
                        "  { a: 1, b: 2 }\n" +
                        "           ~");
        assertException(
                ";;;",
                JavetSanitizerError.NodeCountTooLarge,
                "AST node count 3 is greater than the maximal AST node count 1.",
                0, 3, 1, 1);
        assertException(
                "import a from 'a';",
                JavetSanitizerError.InvalidNode,
                "AST node Import Declaration is unexpected. Expecting AST node Statement in Single Statement.",
                0, 18, 1, 1);
    }

    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = SimpleList.of(
                "() => 1", "() => {}", "(a, b) => {}",
                "function a() {}", "{ a; b; }",
                "1", "'a'", "1 + 1", "a == b", "[1,2,3]", "x = { a: 1, b: 2, c: 3 }",
                "a?.b", "a?.['b']", "a?.b()");
        for (String statement : statements) {
            checker.check(statement);
        }
    }
}