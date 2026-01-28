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

public class TestCompileAstFunctionDefaultParams extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDefaultBooleanParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Formatter {
                    format(value: int, uppercase: boolean = false): String {
                      if (uppercase) {
                        return "VALUE: " + value
                      }
                      return "value: " + value
                    }
                  }
                }""");
        Class<?> classFormatter = runner.getClass("com.Formatter");
        var instance = classFormatter.getConstructor().newInstance();

        assertEquals(
                List.of("VALUE: 42", "value: 42"),
                List.of(
                        classFormatter.getMethod("format", int.class, boolean.class).invoke(instance, 42, true),
                        classFormatter.getMethod("format", int.class).invoke(instance, 42)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDefaultDoubleParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Math {
                    multiply(a: double, factor: double = 2.0): double {
                      return a * factor
                    }
                  }
                }""");
        Class<?> classMath = runner.getClass("com.Math");
        var instance = classMath.getConstructor().newInstance();

        assertEquals(
                List.of(15.0, 10.0),
                List.of(
                        classMath.getMethod("multiply", double.class, double.class).invoke(instance, 5.0, 3.0),
                        classMath.getMethod("multiply", double.class).invoke(instance, 5.0)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDefaultIntParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Calculator {
                    add(a: int, b: int = 10): int {
                      return a + b
                    }
                  }
                }""");
        Class<?> classCalc = runner.getClass("com.Calculator");
        var instance = classCalc.getConstructor().newInstance();

        assertEquals(
                List.of(15, 15),
                List.of(
                        classCalc.getMethod("add", int.class, int.class).invoke(instance, 5, 10),
                        classCalc.getMethod("add", int.class).invoke(instance, 5)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDefaultStringParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Greeter {
                    greet(name: String = "World"): String {
                      return "Hello, " + name + "!"
                    }
                  }
                }""");
        Class<?> classGreeter = runner.getClass("com.Greeter");
        var instance = classGreeter.getConstructor().newInstance();

        assertEquals(
                List.of("Hello, Alice!", "Hello, World!"),
                List.of(
                        classGreeter.getMethod("greet", String.class).invoke(instance, "Alice"),
                        classGreeter.getMethod("greet").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleDefaultParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Calculator {
                    compute(a: int, b: int = 10, c: int = 20): int {
                      return a + b + c
                    }
                  }
                }""");
        Class<?> classCalc = runner.getClass("com.Calculator");
        var instance = classCalc.getConstructor().newInstance();

        assertEquals(
                List.of(36, 35, 31),
                List.of(
                        classCalc.getMethod("compute", int.class, int.class, int.class).invoke(instance, 1, 15, 20),
                        classCalc.getMethod("compute", int.class, int.class).invoke(instance, 5, 10),
                        classCalc.getMethod("compute", int.class).invoke(instance, 1)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodWithDefaultParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Utils {
                    static max(a: int, b: int = 0): int {
                      if (a > b) {
                        return a
                      }
                      return b
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.Utils");
        assertEquals(
                List.of(10, 5, 0),
                List.of(
                        staticRunner.invoke("max", 5, 10),
                        staticRunner.invoke("max", 5),
                        (int) staticRunner.invoke("max", -5) // max(-5, 0) = 0
                )
        );
    }
}
