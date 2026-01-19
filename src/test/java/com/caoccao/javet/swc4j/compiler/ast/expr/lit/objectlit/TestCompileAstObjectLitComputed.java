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

/**
 * Tests for computed property names in object literals.
 */
public class TestCompileAstObjectLitComputed extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedBooleanExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {[true]: "yes", [false]: "no"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("true", "yes", "false", "no"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedKeyOverwritesDuplicate(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {a: 1, ["a"]: 2, [("a")]: 3}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("a", 3), result); // Last value wins
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedKeyWithComplexValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key = "nested"
                      const obj = {[key]: {inner: 42}}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("nested", Map.of("inner", 42)), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedNumericExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {[1 + 1]: "two", [5 * 2]: "ten"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("2", "two", "10", "ten"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedNumericLiteral(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {[42]: "answer", [0]: "zero"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("42", "answer", "0", "zero"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedStringConcat(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {["key" + 1]: "value1", ["key" + 2]: "value2"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("key1", "value1", "key2", "value2"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedStringLiteral(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const obj = {["computed"]: "value"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("computed", "value"), result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedVariableReference(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const key = "dynamic"
                      const obj = {[key]: "value"}
                      return obj
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        var result = (LinkedHashMap<?, ?>) classA.getMethod("test").invoke(instance);
        assertEquals(Map.of("dynamic", "value"), result);
    }

}
