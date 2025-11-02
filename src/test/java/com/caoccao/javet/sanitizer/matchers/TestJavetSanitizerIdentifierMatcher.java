/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.matchers;

import com.caoccao.javet.sanitizer.options.JavetSanitizerOptions;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestJavetSanitizerIdentifierMatcher {
    @Test
    public void testInvalidCases() {
        List<String> identifiers = SimpleList.of("asdf", "$123", "_abc");
        identifiers.forEach(identifier -> assertFalse(
                JavetSanitizerIdentifierMatcher.getInstance().matches(JavetSanitizerOptions.Default, identifier),
                identifier + " should not match."));
    }

    @Test
    public void testValidCases() {
        JavetSanitizerOptions.Default.getDisallowedIdentifierSet().forEach(identifier ->
                assertTrue(
                        JavetSanitizerIdentifierMatcher.getInstance().matches(JavetSanitizerOptions.Default, identifier),
                        identifier + " should match."));
    }
}
