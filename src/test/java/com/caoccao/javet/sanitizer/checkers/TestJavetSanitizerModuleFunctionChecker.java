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
public class TestJavetSanitizerModuleFunctionChecker extends BaseTestSuiteCheckers {
    @BeforeEach
    public void beforeEach() {
        checker = new JavetSanitizerModuleFunctionChecker();
    }

    @Test
    public void testInvalidCases() {
        SimpleList.of("", "   ", null).forEach(code ->
                assertException(
                        code,
                        JavetSanitizerError.EmptyCodeString,
                        JavetSanitizerError.EmptyCodeString.getFormat()));
        assertException(
                "import a from 'a'; a;",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword import is not allowed.\n" +
                        "Source: import a from 'a';\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 18");
        assertException(
                "a; import b from 'b';",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword import is not allowed.\n" +
                        "Source: import b from 'b';\n" +
                        "Line: 1\n" +
                        "Column: 4\n" +
                        "Start: 3\n" +
                        "End: 21");
        assertException(
                "a; export const b = a;",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword export is not allowed.\n" +
                        "Source: export const b = a;\n" +
                        "Line: 1\n" +
                        "Column: 4\n" +
                        "Start: 3\n" +
                        "End: 22");
        assertException(
                "() => {}",
                JavetSanitizerError.InvalidNode,
                "Expression Statement is unexpected. Expecting Function Declaration in Module Function.\n" +
                        "Source: () => {}\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 8");
        assertException(
                "const a = 0;",
                JavetSanitizerError.InvalidNode,
                "Var Declaration is unexpected. Expecting Function Declaration in Module Function.\n" +
                        "Source: const a = 0;\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 12");
        assertException(
                "function a() {}\n() => {}",
                JavetSanitizerError.InvalidNode,
                "Expression Statement is unexpected. Expecting Function Declaration in Module Function.\n" +
                        "Source: () => {}\n" +
                        "Line: 2\n" +
                        "Column: 1\n" +
                        "Start: 16\n" +
                        "End: 24");
    }

    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = SimpleList.of(
                "function a() {}", "function a(b, c) { return b + c; }",
                "function a() {}\nfunction b() {}");
        for (String statement : statements) {
            checker.check(statement);
        }
    }
}
