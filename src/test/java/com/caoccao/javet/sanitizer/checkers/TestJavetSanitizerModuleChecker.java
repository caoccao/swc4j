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
import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstImportDecl;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ThrowableNotThrown")
public class TestJavetSanitizerModuleChecker extends BaseTestSuiteCheckers {
    @BeforeEach
    public void beforeEach() {
        checker = new JavetSanitizerModuleChecker(JavetSanitizerOptions.Default.toClone()
                .setMediaType(Swc4jMediaType.TypeScript)
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
                "async () => {};",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword async is not allowed.\n" +
                        "Source: async () => {}\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 14");
        assertException(
                "async function a() {};",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword async is not allowed.\n" +
                        "Source: async function a() {}\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 21");
        assertException(
                "await a;",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword await is not allowed.\n" +
                        "Source: await a\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 7");
        assertException(
                "for await (const a of b) {}",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword await is not allowed.\n" +
                        "Source: for await (const a of b) {}\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 27");
        assertException(
                "await using a = b;",
                JavetSanitizerError.KeywordNotAllowed,
                "Keyword await is not allowed.\n" +
                        "Source: await using a = b\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 17");
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
    }

    @Test
    public void testInvalidIdentifiers() {
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
                .setReservedIdentifierMatcher(identifier -> identifier.startsWith("$"));
        checker.setOptions(options.seal());
        invalidIdentifierCodeStringMap.forEach((key, value) -> {
            String statement = "function main() { " + key + " }";
            assertException(
                    statement,
                    JavetSanitizerError.IdentifierNotAllowed,
                    "Identifier " + value + " is not allowed.",
                    false);
        });
    }

    @Test
    public void testModules() {
        JavetSanitizerModuleChecker moduleChecker = (JavetSanitizerModuleChecker) checker;
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
                .setKeywordExportEnabled(true)
                .setKeywordImportEnabled(true);
        checker.setOptions(options.seal());
        {
            String code = ";;";
            try {
                checker.check(code);
                assertTrue(moduleChecker.getExportNodes().isEmpty());
                assertTrue(moduleChecker.getImportNodes().isEmpty());
            } catch (JavetSanitizerException e) {
                fail(e);
            }
        }
        {
            String code = "import a from 'a';\nexport const b = a;";
            try {
                checker.check(code);
                assertEquals(1, moduleChecker.getExportNodes().size());
                assertEquals(1, moduleChecker.getImportNodes().size());
                assertInstanceOf(Swc4jAstExportDecl.class, moduleChecker.getExportNodes().get(0));
                assertInstanceOf(Swc4jAstImportDecl.class, moduleChecker.getImportNodes().get(0));
            } catch (JavetSanitizerException e) {
                fail(e);
            }
        }
    }

    @Test
    public void testReservedIdentifiers() {
        JavetSanitizerOptions options = JavetSanitizerOptions.Default.toClone()
                .setReservedIdentifierMatcher(name -> name.startsWith("$"));
        options.getReservedIdentifierSet().add("$a");
        checker.setOptions(options.seal());
        assertException(
                "$a; const $b = 1;",
                JavetSanitizerError.IdentifierNotAllowed,
                "Identifier $b is not allowed.\n" +
                        "Source: $b\n" +
                        "Line: 1\n" +
                        "Column: 11\n" +
                        "Start: 10\n" +
                        "End: 12");
        assertException(
                "$b;",
                JavetSanitizerError.IdentifierNotAllowed,
                "Identifier $b is not allowed.\n" +
                        "Source: $b\n" +
                        "Line: 1\n" +
                        "Column: 1\n" +
                        "Start: 0\n" +
                        "End: 2");
        assertException(
                "const $a = 1;",
                JavetSanitizerError.IdentifierNotAllowed,
                "Identifier $a is not allowed.\n" +
                        "Source: $a\n" +
                        "Line: 1\n" +
                        "Column: 7\n" +
                        "Start: 6\n" +
                        "End: 8");
    }

    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = SimpleList.of(
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
