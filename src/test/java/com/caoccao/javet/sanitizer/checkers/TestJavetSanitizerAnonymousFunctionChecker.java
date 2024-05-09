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

import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestJavetSanitizerAnonymousFunctionChecker {
    @Test
    public void testValidCases() throws JavetSanitizerException {
        List<String> statements = SimpleList.of(
                "() => 1", "() => {}", "(a, b) => { a + b; }");
        JavetSanitizerAnonymousFunctionChecker checker = new JavetSanitizerAnonymousFunctionChecker();
        for (String statement : statements) {
            checker.check(statement);
        }
    }
}
