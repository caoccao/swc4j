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
        SimpleList.of("", "   ", null).forEach(code ->
                assertException(
                        code,
                        JavetSanitizerError.EmptyCodeString,
                        JavetSanitizerError.EmptyCodeString.getFormat()));
        assertException(
                "function() {}",
                JavetSanitizerError.ParsingError,
                """
                        Expected ident at file:///main.js:1:9
                        
                          function() {}
                                  ~""");
        assertException(
                "function(a, b) {}",
                JavetSanitizerError.ParsingError,
                """
                        Expected ident at file:///main.js:1:9
                        
                          function(a, b) {}
                                  ~""");
        assertException(
                "function a() {}",
                JavetSanitizerError.InvalidNode,
                """
                        Function Declaration is unexpected. Expecting Expression Statement in Anonymous Function.
                        Source: function a() {}
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 15""");
        assertException(
                "const a;",
                JavetSanitizerError.InvalidNode,
                """
                        Var Declaration is unexpected. Expecting Expression Statement in Anonymous Function.
                        Source: const a;
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 8""");
        assertException(
                "(() => {})()",
                JavetSanitizerError.InvalidNode,
                """
                        Call Expression is unexpected. Expecting Arrow Expression in Anonymous Function.
                        Source: (() => {})()
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 12""");
        assertException(
                "#!/bin/node\n() => {}",
                JavetSanitizerError.InvalidNode,
                """
                        Shebang /bin/node is unexpected. Expecting Arrow Expression in Anonymous Function.
                        Source: #!/bin/node\\n() => {}
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 20""");
        assertException(
                "() => {}; const a;",
                JavetSanitizerError.NodeCountTooLarge,
                """
                        AST node count 2 is greater than the maximal AST node count 1.
                        Source: () => {}; const a;
                        Line: 1
                        Column: 1
                        Start: 0
                        End: 18""");
    }

    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = List.of(
                "() => 1", "() => {}", "(a, b) => { a + b; }");
        for (String statement : statements) {
            checker.check(statement);
        }
    }
}
