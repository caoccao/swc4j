/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.utils.json;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for JSON provider injection (edge cases #53-57).
 */
public class TestJsonUtilsProvider {

    @AfterEach
    public void resetProvider() {
        JsonUtils.setProvider(null);
    }

    @Test
    public void testEdgeCase53NullProviderFallback() {
        // #53: setProvider(null) falls back to DefaultJsonProvider
        JsonUtils.setProvider(null);
        assertThat(JsonUtils.getProvider()).isEqualTo(DefaultJsonProvider.INSTANCE);
        assertThat(JsonUtils.stringify(42)).isEqualTo("42");
    }

    @Test
    public void testEdgeCase54ProviderSwap() {
        // #54: Provider can be swapped
        JsonProvider custom = new JsonProvider() {
            @Override
            public Object parse(String json) {
                return "custom-parsed";
            }

            @Override
            public String stringify(Object value) {
                return "custom-" + value;
            }

            @Override
            public String stringify(Object value, Object replacer, Object space) {
                return "custom-" + value;
            }
        };
        JsonUtils.setProvider(custom);
        assertThat(JsonUtils.getProvider()).isSameAs(custom);
        assertThat(JsonUtils.stringify(42)).isEqualTo("custom-42");
        assertThat(JsonUtils.parse("anything")).isEqualTo("custom-parsed");
    }

    @Test
    public void testEdgeCase55CustomProviderReturnsNull() {
        // #55: Custom provider returning null from stringify
        JsonUtils.setProvider(new JsonProvider() {
            @Override
            public Object parse(String json) {
                return null;
            }

            @Override
            public String stringify(Object value) {
                return null;
            }

            @Override
            public String stringify(Object value, Object replacer, Object space) {
                return null;
            }
        });
        assertThat(JsonUtils.stringify(42)).isNull();
        assertThat(JsonUtils.parse("42")).isNull();
    }

    @Test
    public void testEdgeCase56CustomProviderThrows() {
        // #56: Custom provider throwing exceptions
        JsonUtils.setProvider(new JsonProvider() {
            @Override
            public Object parse(String json) {
                throw new IllegalArgumentException("parse error");
            }

            @Override
            public String stringify(Object value) {
                throw new IllegalArgumentException("stringify error");
            }

            @Override
            public String stringify(Object value, Object replacer, Object space) {
                throw new IllegalArgumentException("stringify error");
            }
        });
        assertThatThrownBy(() -> JsonUtils.stringify(42))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("stringify error");
        assertThatThrownBy(() -> JsonUtils.parse("42"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("parse error");
    }

    @Test
    public void testEdgeCase57CustomProviderReturnType() {
        // #57: Custom provider returning non-standard types from parse
        JsonUtils.setProvider(new JsonProvider() {
            @Override
            public Object parse(String json) {
                return 12345;
            }

            @Override
            public String stringify(Object value) {
                return "ok";
            }

            @Override
            public String stringify(Object value, Object replacer, Object space) {
                return "ok";
            }
        });
        assertThat(JsonUtils.parse("anything")).isEqualTo(12345);
    }
}
