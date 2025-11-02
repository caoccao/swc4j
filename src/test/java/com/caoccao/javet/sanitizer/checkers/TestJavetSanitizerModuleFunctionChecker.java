/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstImportDecl;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ThrowableNotThrown")
public class TestJavetSanitizerModuleFunctionChecker extends BaseTestSuiteCheckers {
    @BeforeEach
    public void beforeEach() {
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone();
        options.getReservedFunctionIdentifierSet().clear();
        checker = new JavetSanitizerModuleFunctionChecker(options.seal());
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
                """
                        Keyword import is not allowed.
                        Source: import a from 'a';
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 18""");
        assertException(
                "a; import b from 'b';",
                JavetSanitizerError.KeywordNotAllowed,
                """
                        Keyword import is not allowed.
                        Source: import b from 'b';
                        Line: 1
                        Column: 4
                        Start: 3
                        End: 21""");
        assertException(
                "a; export const b = a;",
                JavetSanitizerError.KeywordNotAllowed,
                """
                        Keyword export is not allowed.
                        Source: export const b = a;
                        Line: 1
                        Column: 4
                        Start: 3
                        End: 22""");
        assertException(
                "() => {}",
                JavetSanitizerError.InvalidNode,
                """
                        Expression Statement is unexpected. Expecting Function Declaration in Module Function.
                        Source: () => {}
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 8""");
        assertException(
                "const a = 0;",
                JavetSanitizerError.InvalidNode,
                """
                        Var Declaration is unexpected. Expecting Function Declaration in Module Function.
                        Source: const a = 0;
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 12""");
        assertException(
                """
                        function a() {}
                        () => {}""",
                JavetSanitizerError.InvalidNode,
                """
                        Expression Statement is unexpected. Expecting Function Declaration in Module Function.
                        Source: () => {}
                        Line: 2
                        Column: 1
                        Start: 16
                        End: 24""");
    }

    @Test
    public void testModules() {
        JavetSanitizerModuleFunctionChecker moduleFunctionChecker = (JavetSanitizerModuleFunctionChecker) checker;
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
                .setKeywordExportEnabled(true)
                .setKeywordImportEnabled(true);
        checker.setOptions(options.seal());
        {
            String code = "function main() {}";
            try {
                checker.check(code);
                assertTrue(moduleFunctionChecker.getExportNodes().isEmpty());
                assertTrue(moduleFunctionChecker.getImportNodes().isEmpty());
            } catch (JavetSanitizerException e) {
                fail(e);
            }
        }
        {
            String code = """
                    import a from 'a';
                    function main() {}
                    export const b = a;""";
            try {
                checker.check(code);
                assertEquals(1, moduleFunctionChecker.getExportNodes().size());
                assertEquals(1, moduleFunctionChecker.getImportNodes().size());
                assertInstanceOf(Swc4jAstExportDecl.class, moduleFunctionChecker.getExportNodes().get(0));
                assertInstanceOf(Swc4jAstImportDecl.class, moduleFunctionChecker.getImportNodes().get(0));
            } catch (JavetSanitizerException e) {
                fail(e);
            }
        }
    }

    @Test
    public void testReservedFunctions() {
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
                .setReservedIdentifierMatcher(name -> name.startsWith("$"))
                .setKeywordImportEnabled(true);
        checker.setOptions(options.seal());
        JavetSanitizerModuleFunctionChecker moduleFunctionChecker = (JavetSanitizerModuleFunctionChecker) checker;
        {
            String code = "function b() {}";
            assertEquals(
                    "Function main is not found.",
                    assertThrows(
                            JavetSanitizerException.class,
                            () -> checker.check(code),
                            "Failed to throw exception for [" + code + "]").getMessage());
            assertEquals(1, moduleFunctionChecker.getFunctionMap().size());
            assertTrue(moduleFunctionChecker.getFunctionMap().containsKey("b"));
        }
        {
            String code = "function main() {} function b() {}";
            try {
                checker.check(code);
                assertEquals(2, moduleFunctionChecker.getFunctionMap().size());
                assertTrue(moduleFunctionChecker.getFunctionMap().containsKey("main"));
                assertTrue(moduleFunctionChecker.getFunctionMap().containsKey("b"));
            } catch (JavetSanitizerException e) {
                fail(e);
            }
        }
    }

    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = List.of(
                "function a() {}", "function a(b, c) { return b + c; }",
                "function a() {}\nfunction b() {}");
        for (String statement : statements) {
            checker.check(statement);
        }
    }
}
