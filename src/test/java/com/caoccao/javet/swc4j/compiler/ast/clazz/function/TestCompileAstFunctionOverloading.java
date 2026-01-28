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

package com.caoccao.javet.swc4j.compiler.ast.clazz.function;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for method and constructor overloading.
 */
public class TestCompileAstFunctionOverloading extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorOverloadingByArity(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Point {
                    x: int = 0
                    y: int = 0
                    constructor() { }
                    constructor(x: int) { this.x = x }
                    constructor(x: int, y: int) { this.x = x; this.y = y }
                    getX(): int { return this.x }
                    getY(): int { return this.y }
                  }
                }""");
        Class<?> classA = runner.getClass("com.Point");

        // Test no-arg constructor
        var instance1 = classA.getConstructor().newInstance();
        assertEquals(List.of(0, 0), List.of(
                classA.getMethod("getX").invoke(instance1),
                classA.getMethod("getY").invoke(instance1)
        ));

        // Test single-arg constructor
        var instance2 = classA.getConstructor(int.class).newInstance(10);
        assertEquals(List.of(10, 0), List.of(
                classA.getMethod("getX").invoke(instance2),
                classA.getMethod("getY").invoke(instance2)
        ));

        // Test two-arg constructor
        var instance3 = classA.getConstructor(int.class, int.class).newInstance(10, 20);
        assertEquals(List.of(10, 20), List.of(
                classA.getMethod("getX").invoke(instance3),
                classA.getMethod("getY").invoke(instance3)
        ));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorOverloadingByType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Value {
                    intValue: int = 0
                    doubleValue: double = 0.0
                    constructor(v: int) { this.intValue = v }
                    constructor(v: double) { this.doubleValue = v }
                    getInt(): int { return this.intValue }
                    getDouble(): double { return this.doubleValue }
                  }
                }""");
        Class<?> classA = runner.getClass("com.Value");

        var instance1 = classA.getConstructor(int.class).newInstance(42);
        var instance2 = classA.getConstructor(double.class).newInstance(3.14);

        assertEquals(
                List.of(42, 0.0),
                List.of(
                        classA.getMethod("getInt").invoke(instance1),
                        classA.getMethod("getDouble").invoke(instance1)
                )
        );

        assertEquals(
                List.of(0, 3.14),
                List.of(
                        classA.getMethod("getInt").invoke(instance2),
                        classA.getMethod("getDouble").invoke(instance2)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodOverloadingByArity(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Math {
                    sum(a: int): int { return a }
                    sum(a: int, b: int): int { return a + b }
                    sum(a: int, b: int, c: int): int { return a + b + c }
                  }
                }""");
        Class<?> classA = runner.getClass("com.Math");
        var instance = classA.getConstructor().newInstance();

        assertEquals(
                List.of(5, 15, 60),
                List.of(
                        classA.getMethod("sum", int.class).invoke(instance, 5),
                        classA.getMethod("sum", int.class, int.class).invoke(instance, 5, 10),
                        classA.getMethod("sum", int.class, int.class, int.class).invoke(instance, 10, 20, 30)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodOverloadingByType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Calculator {
                    add(a: int, b: int): int { return a + b }
                    add(a: double, b: double): double { return a + b }
                    add(a: String, b: String): String { return a + b }
                  }
                }""");
        Class<?> classA = runner.getClass("com.Calculator");
        var instance = classA.getConstructor().newInstance();

        assertEquals(
                List.of(30, 5.5, "HelloWorld"),
                List.of(
                        classA.getMethod("add", int.class, int.class).invoke(instance, 10, 20),
                        classA.getMethod("add", double.class, double.class).invoke(instance, 2.5, 3.0),
                        classA.getMethod("add", String.class, String.class).invoke(instance, "Hello", "World")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodOverloadingMixedArityAndType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Formatter {
                    format(value: int): String { return "int:" + value }
                    format(value: double): String { return "double:" + value }
                    format(prefix: String, value: int): String { return prefix + value }
                    format(prefix: String, value: double): String { return prefix + value }
                  }
                }""");
        Class<?> classA = runner.getClass("com.Formatter");
        var instance = classA.getConstructor().newInstance();

        assertEquals(
                List.of("int:42", "double:3.14", "num:100", "pi:3.14"),
                List.of(
                        classA.getMethod("format", int.class).invoke(instance, 42),
                        classA.getMethod("format", double.class).invoke(instance, 3.14),
                        classA.getMethod("format", String.class, int.class).invoke(instance, "num:", 100),
                        classA.getMethod("format", String.class, double.class).invoke(instance, "pi:", 3.14)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOverloadingWithArrayParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class ArrayUtils {
                    getLength(values: int[]): int { return values.length }
                    getLength(values: String[]): int { return values.length }
                  }
                }""");
        Class<?> classA = runner.getClass("com.ArrayUtils");
        var instance = classA.getConstructor().newInstance();

        assertEquals(
                List.of(5, 3),
                List.of(
                        classA.getMethod("getLength", int[].class).invoke(instance, new int[]{1, 2, 3, 4, 5}),
                        classA.getMethod("getLength", String[].class).invoke(instance, (Object) new String[]{"a", "b", "c"})
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOverloadingWithReturnTypeVariation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Converter {
                    convert(value: int): String { return "" + value }
                    convert(value: String): int { return 0 }
                  }
                }""");
        Class<?> classA = runner.getClass("com.Converter");
        var instance = classA.getConstructor().newInstance();

        assertEquals(
                List.of("42", 0),
                List.of(
                        classA.getMethod("convert", int.class).invoke(instance, 42),
                        classA.getMethod("convert", String.class).invoke(instance, "hello")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodOverloading(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Utils {
                    static format(value: int): String { return "int:" + value }
                    static format(value: double): String { return "double:" + value }
                    static format(value: String): String { return "string:" + value }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.Utils");
        assertEquals(
                List.of("int:42", "double:3.14", "string:hello"),
                List.of(
                        (String) staticRunner.invoke("format", 42),
                        (String) staticRunner.invoke("format", 3.14),
                        (String) staticRunner.invoke("format", "hello")
                )
        );
    }
}
