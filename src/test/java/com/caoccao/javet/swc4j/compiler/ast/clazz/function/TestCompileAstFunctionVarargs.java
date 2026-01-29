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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestCompileAstFunctionVarargs extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsAfterRegularParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, ...b: int[]) {
                      return b
                    }
                  }
                }""");
        assertArrayEquals(new int[]{2, 3, 4}, runner.createInstanceRunner("com.A").invoke("test", 1, new int[]{2, 3, 4}));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsAsOnlyParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int[] {
                      return values
                    }
                  }
                }""");
        assertArrayEquals(new int[]{1, 2, 3}, runner.createInstanceRunner("com.A").invoke("test", new int[]{1, 2, 3}));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsDoubleType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: double[]): double {
                      return values[0]
                    }
                  }
                }""");
        assertThat((double) runner.createInstanceRunner("com.A").invoke("test", new double[]{1.5, 2.5})).isCloseTo(1.5, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test", new int[]{})).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsIndexAccess(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getFirst(...values: int[]): int {
                      return values[0]
                    }
                    getLast(...values: int[]): int {
                      return values[values.length - 1]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("getFirst", new int[]{1, 2, 3})).isEqualTo(1);
        assertThat((int) instanceRunner.invoke("getLast", new int[]{1, 2, 3})).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    sum(...values: int[]): int {
                      let total: int = 0
                      for (let i: int = 0; i < values.length; i++) {
                        total = total + values[i]
                      }
                      return total
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("sum", new int[]{1, 2, 3, 4, 5})).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsLengthAccess(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    count(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("count", new int[]{1, 2, 3, 4, 5})).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsManyElements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10})).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test", new int[]{42})).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsStringType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(prefix: String, ...values: String[]): String {
                      return values[0]
                    }
                  }
                }""");
        assertThat((String) runner.createInstanceRunner("com.A").invoke("test", "test", new String[]{"hello", "world"})).isEqualTo("hello");
    }
}
