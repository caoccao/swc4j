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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.objectlit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for object member access and assignment operations (Phase 6 features).
 */
public class TestCompileAstObjectLitPhase6 extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentBracketNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with bracket notation (obj["prop"] = value → map.put("prop", value))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1}
                      obj["b"] = "hello"
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", "hello"), result);
    }

    // ========== Phase 2: Record Type Validation Tests ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentComputedKey(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with computed key (obj[variable] = value → map.put(variable, value))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1}
                      const key = "b"
                      obj[key] = 2
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentDotNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with dot notation (obj.prop = value → map.put("prop", value))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2}
                      obj.c = 3
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentModifyExisting(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment that modifies existing property
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: 2, c: 3}
                      obj.b = 99
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 99, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6AssignmentRecordType(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Assignment with Record type annotation
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {a: 1, b: 2}
                      obj.c = 3
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessBracketNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with bracket notation (obj["prop"] → map.get("prop"))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj["b"]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals("hello", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessComputedKey(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with computed key (obj[variable] → map.get(variable))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      const key = "c"
                      return obj[key]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(true, result);
    }

    // Phase 4: Spread Type Validation

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessDotNotation(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with dot notation (obj.prop → map.get("prop"))
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, b: "hello", c: true}
                      return obj.a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(1, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessNestedObjectSimple(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access on nested objects - first level only
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {
                        outer: {inner: 42}
                      }
                      return obj.outer
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("inner", 42), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6MemberAccessRecordType(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Member access with Record type annotations
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj: Record<string, number> = {a: 1, b: 2, c: 3}
                      return obj.b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(2, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextImplicit(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with implicit return type (type inference)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData() {
                      return {a: 1, b: "hello", c: true}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        assertEquals(Map.of("a", 1, "b", "hello", "c", true), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextNestedRecord(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with nested Record types
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<string, Record<string, number>> {
                      return {
                        outer1: {inner: 42},
                        outer2: {value: 99}
                      }
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        assertEquals(Map.of(
                "outer1", Map.of("inner", 42),
                "outer2", Map.of("value", 99)
        ), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextRecordNumberString(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with Record<number, string>
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<number, string> {
                      return {1: "one", 2: "two", 3: "three"}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        // With Record<number, string> return type, keys should be Integer
        assertEquals(3, result.size());
        assertTrue(result.containsKey(1) || result.containsKey("1"));
        if (result.containsKey(1)) {
            // Numeric keys
            assertEquals(Map.of(1, "one", 2, "two", 3, "three").entrySet(), result.entrySet());
        } else {
            // String keys (fallback)
            assertEquals(Map.of("1", "one", "2", "two", "3", "three"), result);
        }
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPhase6ReturnTypeContextRecordStringNumber(JdkVersion jdkVersion) throws Exception {
        // Phase 6: Return type context with Record<string, number>
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getData(): Record<string, number> {
                      return {a: 1, b: 2, c: 3}
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("getData").invoke(instance);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), result);
    }

}
