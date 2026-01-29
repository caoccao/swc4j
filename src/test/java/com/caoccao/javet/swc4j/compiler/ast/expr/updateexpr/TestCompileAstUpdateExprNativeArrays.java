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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals((byte) 11, (byte) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(2.5, (Double) runner.createInstanceRunner("com.A").invoke("test"), 0.001);
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
        assertEquals(10.5f, (Float) runner.createInstanceRunner("com.A").invoke("test"), 0.001f);
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
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test")); // arr[2] before increment
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
        assertEquals(12, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(9, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(4, (int) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals(201L, (long) runner.createInstanceRunner("com.A").invoke("test"));
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
        assertEquals((short) 299, (short) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
