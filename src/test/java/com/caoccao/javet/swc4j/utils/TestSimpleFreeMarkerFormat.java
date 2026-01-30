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

package com.caoccao.javet.swc4j.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSimpleFreeMarkerFormat {
    @Test
    public void testInvalid() {
        assertThat(SimpleFreeMarkerFormat.format("abc", null))
                .as("Parameters being null should pass.")
                .isEqualTo("abc");
        assertThat(SimpleFreeMarkerFormat.format("abc", new HashMap<>()))
                .as("Parameters being empty should pass.")
                .isEqualTo("abc");
        assertThat(SimpleFreeMarkerFormat.format("abc${", SimpleMap.of("d", "x")))
                .as("Open variable should pass.")
                .isEqualTo("abc${");
        assertThat(SimpleFreeMarkerFormat.format("abc${def", SimpleMap.of("d", "x")))
                .as("Open variable should pass.")
                .isEqualTo("abc${def");
        assertThat(SimpleFreeMarkerFormat.format("abc$${d}", SimpleMap.of("d", "x")))
                .as("Double dollar should pass.")
                .isEqualTo("abc$${d}");
        assertThat(SimpleFreeMarkerFormat.format("abc${e}def", SimpleMap.of("d", "x")))
                .as("Unknown variable should pass.")
                .isEqualTo("abc<null>def");
        assertThat(SimpleFreeMarkerFormat.format("abc${}def", SimpleMap.of("d", "x")))
                .as("Empty variable should pass.")
                .isEqualTo("abc<null>def");
        assertThat(SimpleFreeMarkerFormat.format("ab{def.$ghi}c", SimpleMap.of("ghi", "x")))
                .as("Dollar should pass.")
                .isEqualTo("ab{def.$ghi}c");
    }

    @Test
    public void testValid() {
        assertThat(SimpleFreeMarkerFormat.format("abc${d}", SimpleMap.of("d", "x")))
                .as("Variable at the end should pass.")
                .isEqualTo("abcx");
        assertThat(SimpleFreeMarkerFormat.format("${d}abc", SimpleMap.of("d", "x")))
                .as("Variable at the beginning should pass.")
                .isEqualTo("xabc");
        assertThat(SimpleFreeMarkerFormat.format("ab${d}c", SimpleMap.of("d", "x")))
                .as("Variable in the middle should pass.")
                .isEqualTo("abxc");
        assertThat(SimpleFreeMarkerFormat.format("ab${def.${ghi}c", SimpleMap.of("def.${ghi", "x")))
                .as("Variable with dollar should pass.")
                .isEqualTo("abxc");
        assertThat(SimpleFreeMarkerFormat.format("ab${{}c", SimpleMap.of("{", "x")))
                .as("Single open should pass.")
                .isEqualTo("abxc");
        assertThat(SimpleFreeMarkerFormat.format("ab${x}c", SimpleMap.of("x", 12345678)))
                .as("Integer should pass.")
                .isEqualTo("ab12345678c");
        assertThat(SimpleFreeMarkerFormat.format("ab${x}c", SimpleMap.of("x", 1234567890L)))
                .as("Long should pass.")
                .isEqualTo("ab1234567890c");
    }
}
