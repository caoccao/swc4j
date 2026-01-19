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

package com.caoccao.javet.swc4j.compiler.ast.expr.condexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for type-specific branches and type widening in conditional expressions.
 * Tests different primitive types (int, long, double, float, String) and type widening scenarios.
 */
public class TestCompileAstCondExprTypes extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? 1.5 : 2.5
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1.5, (double) classA.getMethod("test").invoke(instance), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatAndDoubleWidening(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const flag: boolean = true
                      const floatVal: float = 10.0
                      const doubleVal: double = 20.0
                      return flag ? floatVal : doubleVal
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10.0, (double) classA.getMethod("test").invoke(instance), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntAndDoubleWidening(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? 10 : 20.5
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10.0, (double) classA.getMethod("test").invoke(instance), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntAndLongWidening(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const flag: boolean = false
                      const longVal: long = 20
                      return flag ? 10 : longVal
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(20L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const flag: boolean = false
                      const val1: long = 100
                      const val2: long = 200
                      return flag ? val1 : val2
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(200L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? "yes" : "no"
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("yes", classA.getMethod("test").invoke(instance));
    }
}
