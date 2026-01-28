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

package com.caoccao.javet.swc4j.compiler.ast.clazz.clazz;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstClassStatic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleStaticMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Math {
                    static add(a: int, b: int): int {
                      return a + b
                    }
                    static subtract(a: int, b: int): int {
                      return a - b
                    }
                    static multiply(a: int, b: int): int {
                      return a * b
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.Math");
        assertEquals(15, (int) staticRunner.invoke("add", 10, 5));
        assertEquals(5, (int) staticRunner.invoke("subtract", 10, 5));
        assertEquals(50, (int) staticRunner.invoke("multiply", 10, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticAndInstanceMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static staticMethod(): int {
                      return 100
                    }
                    instanceMethod(): int {
                      return 200
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createStaticRunner("com.A").invoke("staticMethod"));
        assertEquals(200, (int) runner.createInstanceRunner("com.A").invoke("instanceMethod"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodNoParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static getValue(): int {
                      return 42
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createStaticRunner("com.A").invoke("getValue"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodReturningBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static isPositive(x: int): boolean {
                      return x > 0
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.A");
        assertEquals(true, (boolean) staticRunner.invoke("isPositive", 5));
        assertEquals(false, (boolean) staticRunner.invoke("isPositive", -5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodReturningDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static multiply(a: double, b: double): double {
                      return a * b
                    }
                  }
                }""");
        assertEquals(6.28, (double) runner.createStaticRunner("com.A").invoke("multiply", 2.0, 3.14), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodReturningString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static greet(name: String): String {
                      return "Hello, " + name
                    }
                  }
                }""");
        assertEquals("Hello, World", (String) runner.createStaticRunner("com.A").invoke("greet", "World"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodWithConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static max(a: int, b: int): int {
                      if (a > b) return a
                      return b
                    }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.A");
        assertEquals(20, (int) staticRunner.invoke("max", 10, 20));
        assertEquals(30, (int) staticRunner.invoke("max", 30, 20));
    }

    // TODO: This test requires static method call resolution (A.helper())
    // @ParameterizedTest
    // @EnumSource(JdkVersion.class)
    // public void testStaticMethodCallingAnotherStaticMethod(JdkVersion jdkVersion) throws Exception {
    //     var runner = getCompiler(jdkVersion).compile("""
    //             namespace com {
    //               export class A {
    //                 static helper(): int {
    //                   return 10
    //                 }
    //                 static test(): int {
    //                   return A.helper() * 2
    //                 }
    //               }
    //             }""");
    //     Class<?> classA = runner.getClass("com.A");
    //     assertEquals(20, classA.getMethod("test").invoke(null));
    // }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticMethodWithParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static add(a: int, b: int): int {
                      return a + b
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createStaticRunner("com.A").invoke("add", 10, 20));
    }

    // TODO: This test requires static method call resolution from different class (Helper.compute())
    // @ParameterizedTest
    // @EnumSource(JdkVersion.class)
    // public void testStaticMethodFromDifferentClass(JdkVersion jdkVersion) throws Exception {
    //     var runner = getCompiler(jdkVersion).compile("""
    //             namespace com {
    //               export class Helper {
    //                 static compute(x: int): int {
    //                   return x * x
    //                 }
    //               }
    //               export class User {
    //                 calculate(value: int): int {
    //                   return Helper.compute(value) + 1
    //                 }
    //               }
    //             }""");
    //     Class<?> userClass = runner.getClass("com.User");
    //     var instance = userClass.getConstructor().newInstance();
    //     assertEquals(26, userClass.getMethod("calculate", int.class).invoke(instance, 5));
    // }
}
