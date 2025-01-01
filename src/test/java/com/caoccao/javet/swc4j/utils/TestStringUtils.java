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

package com.caoccao.javet.swc4j.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStringUtils {
    @Test
    public void testToSnakeCase() {
        assertEquals("abc_abc_abc", StringUtils.toSnakeCase("abc abc  abc  "));
        assertEquals("abc_abc_abc", StringUtils.toSnakeCase("AbcAbcAbc"));
        assertEquals("abc_abc_abc", StringUtils.toSnakeCase("abcAbcAbc"));
        assertEquals("abc_abc_abc", StringUtils.toSnakeCase("ABCAbcAbc"));
        assertEquals("abc_abc_abc", StringUtils.toSnakeCase("abcABCAbc"));
        assertEquals("abc_abc_abc", StringUtils.toSnakeCase("abc&abc$$abc"));
    }
}
