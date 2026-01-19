/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@SuppressWarnings("ThrowableNotThrown")
public class TestJavetSanitizerStatementListChecker extends BaseTestSuiteCheckers {
    @BeforeEach
    public void beforeEach() {
        checker = new JavetSanitizerStatementListChecker(JavetSanitizerOptions.Default.toClone()
                .setKeywordExportEnabled(true)
                .setKeywordImportEnabled(true)
                .seal());
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
                JavetSanitizerError.InvalidNode,
                """
                        Import Declaration is unexpected. Expecting Statement in Statement List.
                        Source: import a from 'a';
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 18""");
        assertException(
                "a; import b from 'b';",
                JavetSanitizerError.InvalidNode,
                """
                        Import Declaration is unexpected. Expecting Statement in Statement List.
                        Source: import b from 'b';
                        Line: 1
                        Column: 4
                        Start: 3
                        End: 21""");
        assertException(
                "export const a = {};",
                JavetSanitizerError.InvalidNode,
                """
                        Export Declaration is unexpected. Expecting Statement in Statement List.
                        Source: export const a = {};
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 20""");
    }

    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = List.of(
                "() => 1", "() => {}", "(a, b) => {}",
                "function a() {}", "{ a; b; }", "const a;",
                "1", "'a'", "1 + 1", "a == b", "[1,2,3]", "x = { a: 1, b: 2, c: 3 }",
                "a?.b", "a?.b?.c?.d", "a?.['b']", "a?.b()",
                "a;b;", ";;;");
        for (String statement : statements) {
            checker.check(statement);
        }
    }
}
