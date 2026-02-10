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

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for JSON.stringify edge cases (#1-20).
 */
public class TestJsonUtilsStringify {

    @Test
    public void testComplexStructure() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Alice");
        map.put("age", 30);
        ArrayList<Object> hobbies = new ArrayList<>();
        hobbies.add("reading");
        hobbies.add("coding");
        map.put("hobbies", hobbies);
        map.put("active", true);
        map.put("score", null);
        assertThat(JsonUtils.stringify(map))
                .isEqualTo("{\"name\":\"Alice\",\"age\":30,\"hobbies\":[\"reading\",\"coding\"],\"active\":true,\"score\":null}");
    }

    @Test
    public void testEdgeCase01CircularReference() {
        // #1: Circular references should throw
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("self", map);
        assertThatThrownBy(() -> JsonUtils.stringify(map))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("circular");
    }

    @Test
    public void testEdgeCase01CircularReferenceArray() {
        // #1: Circular references in ArrayList
        ArrayList<Object> list = new ArrayList<>();
        list.add(list);
        assertThatThrownBy(() -> JsonUtils.stringify(list))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("circular");
    }

    @Test
    public void testEdgeCase02UndefinedVoid() {
        // #2: null value serializes as "null"
        assertThat(JsonUtils.stringify(null)).isEqualTo("null");
    }

    @Test
    public void testEdgeCase03NaNAndInfinity() {
        // #3: NaN and Infinity serialize as null
        assertThat(JsonUtils.stringify(Double.NaN)).isEqualTo("null");
        assertThat(JsonUtils.stringify(Double.POSITIVE_INFINITY)).isEqualTo("null");
        assertThat(JsonUtils.stringify(Double.NEGATIVE_INFINITY)).isEqualTo("null");
        assertThat(JsonUtils.stringify(Float.NaN)).isEqualTo("null");
        assertThat(JsonUtils.stringify(Float.POSITIVE_INFINITY)).isEqualTo("null");
    }

    @Test
    public void testEdgeCase04NegativeZero() {
        // #4: -0.0 serializes as "0"
        assertThat(JsonUtils.stringify(-0.0)).isEqualTo("0");
        assertThat(JsonUtils.stringify(-0.0f)).isEqualTo("0");
    }

    @Test
    public void testEdgeCase05BigInteger() {
        // #5: BigInteger serializes as numeric literal
        assertThat(JsonUtils.stringify(BigInteger.valueOf(123456789012345L))).isEqualTo("123456789012345");
        assertThat(JsonUtils.stringify(new BigInteger("999999999999999999999999999999"))).isEqualTo("999999999999999999999999999999");
    }

    @Test
    public void testEdgeCase06Character() {
        // #6: Character serializes as single-char string
        assertThat(JsonUtils.stringify('A')).isEqualTo("\"A\"");
        assertThat(JsonUtils.stringify('\n')).isEqualTo("\"\\n\"");
    }

    @Test
    public void testEdgeCase07MapKeyTypes() {
        // #7: Non-string map keys converted via String.valueOf
        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        map.put(42, "value");
        assertThat(JsonUtils.stringify(map)).isEqualTo("{\"42\":\"value\"}");
    }

    @Test
    public void testEdgeCase09NullMapValues() {
        // #9: Null values in maps are serialized as null
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("key", null);
        assertThat(JsonUtils.stringify(map)).isEqualTo("{\"key\":null}");
    }

    @Test
    public void testEdgeCase10ReplacerAsArrayList() {
        // #10: Replacer as ArrayList property whitelist
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        ArrayList<Object> replacer = new ArrayList<>();
        replacer.add("a");
        replacer.add("c");
        assertThat(JsonUtils.stringify(map, replacer, null)).isEqualTo("{\"a\":1,\"c\":3}");
    }

    @Test
    public void testEdgeCase11ReplacerAsNonList() {
        // #11: Non-list, non-null replacer is ignored
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        assertThat(JsonUtils.stringify(map, "ignored", null)).isEqualTo("{\"a\":1}");
    }

    @Test
    public void testEdgeCase12SpaceClampingNegative() {
        // #12: Negative number → 0 → compact
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        assertThat(JsonUtils.stringify(map, null, -5)).isEqualTo("{\"a\":1}");
    }

    @Test
    public void testEdgeCase12SpaceClampingNumber() {
        // #12: Numeric space clamped to [0,10]
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        String result = JsonUtils.stringify(map, null, 20);
        // Should only use 10 spaces
        assertThat(result).contains("\n" + " ".repeat(10) + "\"a\"");
    }

    @Test
    public void testEdgeCase12SpaceString() {
        // #12: String space truncated to 10 chars
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        String result = JsonUtils.stringify(map, null, "\t");
        assertThat(result).isEqualTo("{\n\t\"a\": 1\n}");
    }

    @Test
    public void testEdgeCase13SpaceEmptyString() {
        // #13: Empty string space → compact
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        assertThat(JsonUtils.stringify(map, null, "")).isEqualTo("{\"a\":1}");
    }

    @Test
    public void testEdgeCase13SpaceZero() {
        // #13: Space 0 → compact output
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        assertThat(JsonUtils.stringify(map, null, 0)).isEqualTo("{\"a\":1}");
    }

    @Test
    public void testEdgeCase14NestedIndentation() {
        // #14: Nested indentation
        LinkedHashMap<String, Object> outer = new LinkedHashMap<>();
        LinkedHashMap<String, Object> inner = new LinkedHashMap<>();
        inner.put("b", 2);
        outer.put("a", inner);
        String result = JsonUtils.stringify(outer, null, 2);
        assertThat(result).isEqualTo("{\n  \"a\": {\n    \"b\": 2\n  }\n}");
    }

    @Test
    public void testEdgeCase15EmptyObject() {
        // #15: Empty object produces "{}"
        assertThat(JsonUtils.stringify(new LinkedHashMap<>())).isEqualTo("{}");
        // Even with indent
        assertThat(JsonUtils.stringify(new LinkedHashMap<>(), null, 2)).isEqualTo("{}");
    }

    @Test
    public void testEdgeCase16EmptyArray() {
        // #16: Empty array produces "[]"
        assertThat(JsonUtils.stringify(new ArrayList<>())).isEqualTo("[]");
        assertThat(JsonUtils.stringify(new ArrayList<>(), null, 2)).isEqualTo("[]");
    }

    @Test
    public void testEdgeCase17DepthLimit() {
        // #17: Deeply nested structures throw
        Object current = "leaf";
        for (int i = 0; i < 600; i++) {
            ArrayList<Object> wrapper = new ArrayList<>();
            wrapper.add(current);
            current = wrapper;
        }
        Object deepNested = current;
        assertThatThrownBy(() -> JsonUtils.stringify(deepNested))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("depth");
    }

    @Test
    public void testEdgeCase19NullInArray() {
        // #19: Null entries in arrays serialize as null
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        list.add(null);
        list.add("a");
        assertThat(JsonUtils.stringify(list)).isEqualTo("[1,null,\"a\"]");
    }

    @Test
    public void testPrimitiveTypes() {
        // Boolean
        assertThat(JsonUtils.stringify(true)).isEqualTo("true");
        assertThat(JsonUtils.stringify(false)).isEqualTo("false");
        // Integer
        assertThat(JsonUtils.stringify(42)).isEqualTo("42");
        assertThat(JsonUtils.stringify(-100)).isEqualTo("-100");
        // Long
        assertThat(JsonUtils.stringify(9876543210L)).isEqualTo("9876543210");
        // Double
        assertThat(JsonUtils.stringify(3.14)).isEqualTo("3.14");
        // Float
        assertThat(JsonUtils.stringify(2.5f)).isEqualTo("2.5");
        // Short
        assertThat(JsonUtils.stringify((short) 42)).isEqualTo("42");
        // Byte
        assertThat(JsonUtils.stringify((byte) 7)).isEqualTo("7");
    }

    @Test
    public void testStringEscaping() {
        // String escaping: quotes, backslash, control chars
        assertThat(JsonUtils.stringify("hello \"world\"")).isEqualTo("\"hello \\\"world\\\"\"");
        assertThat(JsonUtils.stringify("back\\slash")).isEqualTo("\"back\\\\slash\"");
        assertThat(JsonUtils.stringify("line\nbreak")).isEqualTo("\"line\\nbreak\"");
        assertThat(JsonUtils.stringify("tab\there")).isEqualTo("\"tab\\there\"");
        assertThat(JsonUtils.stringify("\r\b\f")).isEqualTo("\"\\r\\b\\f\"");
        assertThat(JsonUtils.stringify("\u0000")).isEqualTo("\"\\u0000\"");
        assertThat(JsonUtils.stringify("\u001f")).isEqualTo("\"\\u001f\"");
    }

    @Test
    public void testFunctionReplacerFilterKeys() {
        // BiFunction replacer: return UNDEFINED to omit properties
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Alice");
        map.put("password", "secret");
        map.put("age", 30);
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if ("password".equals(key)) return JsonUtils.UNDEFINED;
            return value;
        };
        assertThat(JsonUtils.stringify(map, replacer, null))
                .isEqualTo("{\"name\":\"Alice\",\"age\":30}");
    }

    @Test
    public void testFunctionReplacerTransformValues() {
        // BiFunction replacer: transform string values to uppercase
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("name", "alice");
        map.put("city", "paris");
        map.put("count", 5);
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if (value instanceof String && !key.isEmpty()) return ((String) value).toUpperCase();
            return value;
        };
        assertThat(JsonUtils.stringify(map, replacer, null))
                .isEqualTo("{\"name\":\"ALICE\",\"city\":\"PARIS\",\"count\":5}");
    }

    @Test
    public void testFunctionReplacerPassthrough() {
        // BiFunction replacer that returns all values unchanged
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", null);
        map.put("c", "text");
        BiFunction<String, Object, Object> replacer = (key, value) -> value;
        assertThat(JsonUtils.stringify(map, replacer, null))
                .isEqualTo("{\"a\":1,\"b\":null,\"c\":\"text\"}");
    }

    @Test
    public void testFunctionReplacerRootCall() {
        // BiFunction replacer is called with key="" for the root value
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if (key.isEmpty()) return "replaced";
            return value;
        };
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        assertThat(JsonUtils.stringify(map, replacer, null)).isEqualTo("\"replaced\"");
    }

    @Test
    public void testFunctionReplacerRootUndefined() {
        // BiFunction replacer returning UNDEFINED for root → "undefined"
        BiFunction<String, Object, Object> replacer = (key, value) -> JsonUtils.UNDEFINED;
        assertThat(JsonUtils.stringify(42, replacer, null)).isEqualTo("undefined");
    }

    @Test
    public void testFunctionReplacerOnNestedObject() {
        // Replacer applies recursively to nested objects
        LinkedHashMap<String, Object> inner = new LinkedHashMap<>();
        inner.put("x", 1);
        inner.put("secret", "hidden");
        LinkedHashMap<String, Object> outer = new LinkedHashMap<>();
        outer.put("data", inner);
        outer.put("secret", "also hidden");
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if ("secret".equals(key)) return JsonUtils.UNDEFINED;
            return value;
        };
        assertThat(JsonUtils.stringify(outer, replacer, null))
                .isEqualTo("{\"data\":{\"x\":1}}");
    }

    @Test
    public void testFunctionReplacerOnArray() {
        // Replacer on array: called with string index, UNDEFINED → "null"
        ArrayList<Object> list = new ArrayList<>();
        list.add("keep");
        list.add("remove");
        list.add("keep");
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if ("remove".equals(value)) return JsonUtils.UNDEFINED;
            return value;
        };
        assertThat(JsonUtils.stringify(list, replacer, null))
                .isEqualTo("[\"keep\",null,\"keep\"]");
    }

    @Test
    public void testFunctionReplacerWithSpace() {
        // Function replacer combined with indentation
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if ("b".equals(key)) return JsonUtils.UNDEFINED;
            return value;
        };
        assertThat(JsonUtils.stringify(map, replacer, 2))
                .isEqualTo("{\n  \"a\": 1,\n  \"c\": 3\n}");
    }

    @Test
    public void testFunctionReplacerReplaceWithDifferentType() {
        // Replace number with string
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("val", 42);
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if (value instanceof Integer) return "number:" + value;
            return value;
        };
        assertThat(JsonUtils.stringify(map, replacer, null))
                .isEqualTo("{\"val\":\"number:42\"}");
    }

    @Test
    public void testFunctionReplacerOnEmptyObject() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        BiFunction<String, Object, Object> replacer = (key, value) -> value;
        assertThat(JsonUtils.stringify(map, replacer, null)).isEqualTo("{}");
    }

    @Test
    public void testFunctionReplacerAllKeysFiltered() {
        // All keys filtered → empty object
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if (!key.isEmpty()) return JsonUtils.UNDEFINED;
            return value;
        };
        assertThat(JsonUtils.stringify(map, replacer, null)).isEqualTo("{}");
    }

    @Test
    public void testFunctionReplacerOnPrimitive() {
        // Root call transforms primitive, replacer doesn't recurse further
        BiFunction<String, Object, Object> replacer = (key, value) -> value;
        assertThat(JsonUtils.stringify(42, replacer, null)).isEqualTo("42");
        assertThat(JsonUtils.stringify("hello", replacer, null)).isEqualTo("\"hello\"");
        assertThat(JsonUtils.stringify(true, replacer, null)).isEqualTo("true");
        assertThat(JsonUtils.stringify(null, replacer, null)).isEqualTo("null");
    }

    @Test
    public void testFunctionReplacerArrayWithObjects() {
        // Replacer filters keys from objects inside arrays
        LinkedHashMap<String, Object> obj1 = new LinkedHashMap<>();
        obj1.put("id", 1);
        obj1.put("secret", "x");
        LinkedHashMap<String, Object> obj2 = new LinkedHashMap<>();
        obj2.put("id", 2);
        obj2.put("secret", "y");
        ArrayList<Object> list = new ArrayList<>();
        list.add(obj1);
        list.add(obj2);
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if ("secret".equals(key)) return JsonUtils.UNDEFINED;
            return value;
        };
        assertThat(JsonUtils.stringify(list, replacer, null))
                .isEqualTo("[{\"id\":1},{\"id\":2}]");
    }

    @Test
    public void testFunctionReplacerReplaceNullWithValue() {
        // Replace null values with a default
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", null);
        map.put("b", 1);
        BiFunction<String, Object, Object> replacer = (key, value) -> {
            if (value == null && !key.isEmpty()) return "default";
            return value;
        };
        assertThat(JsonUtils.stringify(map, replacer, null))
                .isEqualTo("{\"a\":\"default\",\"b\":1}");
    }

    @Test
    public void testUnknownObject() {
        // #20: Any other object type → "{}"
        assertThat(JsonUtils.stringify(new Object())).isEqualTo("{}");
    }

    @Test
    public void testWholeNumberDoubles() {
        // Whole-number doubles should serialize without decimal point
        assertThat(JsonUtils.stringify(1.0)).isEqualTo("1");
        assertThat(JsonUtils.stringify(100.0)).isEqualTo("100");
    }
}
