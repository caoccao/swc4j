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

package com.caoccao.javet.swc4j.compiler.ast.expr.updateexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Test suite for update expressions (++ and --) on native Java typed arrays.
 * Tests increment/decrement operations on elements of native Java arrays (int[], long[], byte[], etc.).
 */
public class TestCompileAstUpdateExprNativeArrays extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeByteArrayIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: byte[] = [10, 20, 30]
                      const result = ++arr[0]
                      return result
                    }
                  }
                }""");
        assertThat((byte) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo((byte) 11);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeDoubleArrayIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: double[] = [1.5, 2.5, 3.5]
                      const result = ++arr[0]
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(2.5, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeDoubleArrayPostfixDecrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: double[] = [2.5, 1.5]
                      const result = arr[0]--
                      return [result, arr[0]]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(2.5, 1.5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeFloatArrayDecrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: float[] = [5.5, 10.5, 15.5]
                      const result = arr[1]--
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Float>invoke("test")).isCloseTo(10.5f, within(0.001f));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntArrayComputedIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: int[] = [10, 20, 30, 40]
                      const i: int = 1
                      const result = arr[i + 1]++
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(30); // arr[2] before increment
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntArrayModifiesValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: int[] = [5, 10, 15]
                      arr[1]++
                      arr[1]++
                      return arr[1]
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(12);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntArrayPostfixDecrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: int[] = [10, 20, 30]
                      const result = arr[0]--
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntArrayPostfixIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: int[] = [1, 2, 3]
                      const result = arr[1]++
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntArrayPrefixDecrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: int[] = [10, 20, 30]
                      const result = --arr[0]
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(9);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntArrayPrefixIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: int[] = [1, 2, 3]
                      const result = ++arr[1]
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntArrayWithVariableIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: int[] = [1, 2, 3, 4, 5]
                      const i: int = 2
                      const result = ++arr[i]
                      return result
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeIntMatrixUpdate(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(matrix: int[][]) {
                      const result = matrix[1][0]++
                      return [result, matrix[1][0]]
                    }
                  }
                }""");
        int[][] matrix = new int[][]{{1, 2}, {3, 4}};
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test", (Object) matrix))
                .isEqualTo(List.of(3, 4));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeLongArrayIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: long[] = [100, 200, 300]
                      const result = ++arr[1]
                      return result
                    }
                  }
                }""");
        assertThat((long) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(201L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeLongArrayPostfixIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: long[] = [100, 200]
                      const result = arr[0]++
                      return [result, arr[0]]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(100L, 101L));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNativeShortArrayDecrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr: short[] = [100, 200, 300]
                      const result = --arr[2]
                      return result
                    }
                  }
                }""");
        assertThat((short) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo((short) 299);
    }
}
