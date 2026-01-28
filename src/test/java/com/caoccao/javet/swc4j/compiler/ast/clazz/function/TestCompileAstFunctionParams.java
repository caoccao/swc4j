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

public class TestCompileAstFunctionParams extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: Array<Integer>) {
                      return b
                    }
                  }
                }""");
        assertEquals(List.of(2, 3, 4), runner.createInstanceRunner("com.A").invoke("test", 1, List.of(2, 3, 4)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean): boolean {
                      return !flag
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(false, (boolean) instanceRunner.invoke("test", true));
        assertEquals(true, (boolean) instanceRunner.invoke("test", false));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: double): double {
                      return a * 2.0
                    }
                  }
                }""");
        assertEquals(6.28, (double) runner.createInstanceRunner("com.A").invoke("test", 3.14), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(value: long): long {
                      return value + 1
                    }
                  }
                }""");
        assertEquals(9999999999L + 1, (long) runner.createInstanceRunner("com.A").invoke("test", 9999999999L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testManyParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int, c: int, d: int, e: int, f: int, g: int, h: int): int {
                      return a + b + c + d + e + f + g + h
                    }
                  }
                }""");
        assertEquals(36, (int) runner.createInstanceRunner("com.A").invoke("test", 1, 2, 3, 4, 5, 6, 7, 8));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int, c: int): int {
                      return a + b + c
                    }
                  }
                }""");
        assertEquals(60, (int) runner.createInstanceRunner("com.A").invoke("test", 10, 20, 30));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParameterWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: String, c: double): String {
                      return b
                    }
                  }
                }""");
        assertEquals("hello", runner.createInstanceRunner("com.A").invoke("test", 42, "hello", 3.14));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int): int {
                      return a
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test", 42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(s: String): String {
                      return s
                    }
                  }
                }""");
        assertEquals("test string", runner.createInstanceRunner("com.A").invoke("test", "test string"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWideTypeParameterSlotAllocation(JdkVersion jdkVersion) throws Exception {
        // Long and double take 2 local variable slots
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: long, b: double, c: int): long {
                      return a + (c as long)
                    }
                  }
                }""");
        assertEquals(110L, (long) runner.createInstanceRunner("com.A").invoke("test", 100L, 3.14, 10));
    }
}
