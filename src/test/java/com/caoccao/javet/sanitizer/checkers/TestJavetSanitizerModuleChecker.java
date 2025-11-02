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
                """
                        Keyword async is not allowed.
                        Source: async () => {}
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 14""");
        assertException(
                "async function a() {};",
                JavetSanitizerError.KeywordNotAllowed,
                """
                        Keyword async is not allowed.
                        Source: async function a() {}
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 21""");
        assertException(
                "await a;",
                JavetSanitizerError.KeywordNotAllowed,
                """
                        Keyword await is not allowed.
                        Source: await a
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 7""");
        assertException(
                "for await (const a of b) {}",
                JavetSanitizerError.KeywordNotAllowed,
                """
                        Keyword await is not allowed.
                        Source: for await (const a of b) {}
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 27""");
        assertException(
                "await using a = b;",
                JavetSanitizerError.KeywordNotAllowed,
                """
                        Keyword await is not allowed.
                        Source: await using a = b;
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 18""");
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
            String code = """
                    import a from 'a';
                    export const b = a;""";
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
                """
                        Identifier $b is not allowed.
                        Source: $b
                        Line: 1
                        Column: 11
                        Start: 10
                        End: 12""");
        assertException(
                "$b;",
                JavetSanitizerError.IdentifierNotAllowed,
                """
                        Identifier $b is not allowed.
                        Source: $b
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 2""");
        assertException(
                "const $a = 1;",
                JavetSanitizerError.IdentifierNotAllowed,
                """
                        Identifier $a is not allowed.
                        Source: $a
                        Line: 1
                        Column: 7
                        Start: 6
                        End: 8""");
    }

    @Test
    public void testShebang() throws JavetSanitizerException {
        String codeString = """
                #!/bin/node
                const a;""";
        assertException(
                codeString,
                JavetSanitizerError.InvalidNode,
                """
                        Shebang /bin/node is unexpected. Expecting Statement in Module.
                        Source: #!/bin/node\\nconst a;
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 20""");
        JavetSanitizerOptions options = checker.getOptions().toClone()
                .setShebangEnabled(true)
                .seal();
        checker.setOptions(options);
        checker.check(codeString);
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
