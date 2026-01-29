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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.arraylit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for basic array creation and type operations.
 */
public class TestCompileAstArrayLitBasic extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayAnnotation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [1, 2, 3]
                      return a
                    }
                  }
                }""");
        assertThat((int[]) runner.createInstanceRunner("com.A").<Object>invoke("test")).containsExactly(1, 2, 3);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayListOfDoubles(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Array<Double> = [1.5, 2.5, 3.5]
                      return a
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1.5, 2.5, 3.5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayListOfStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Array<String> = ["foo", "bar", "baz"]
                      return a
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("foo", "bar", "baz"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean[] = [true, false, true]
                      return a
                    }
                  }
                }""");
        assertThat((boolean[]) runner.createInstanceRunner("com.A").<Object>invoke("test")).containsExactly(true, false, true);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double[] = [1.5, 2.5, 3.5]
                      return a
                    }
                  }
                }""");
        assertThat((double[]) runner.createInstanceRunner("com.A").<Object>invoke("test")).containsExactly(1.5, 2.5, 3.5);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleArrayOperations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double[] = [1.5, 2.5, 3.5]
                      a[0] = 10.5
                      return a[0]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("test")).isCloseTo(10.5, within(0.001));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyIntArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = []
                      return a
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(new int[]{});
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float[] = [1.0, 2.0, 3.0]
                      return a
                    }
                  }
                }""");
        assertThat((float[]) runner.createInstanceRunner("com.A").<Object>invoke("test")).containsExactly(1.0f, 2.0f, 3.0f);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testListAnnotation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Array<Integer> = [1, 2, 3]
                      return a
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long[] = [100, 200, 300]
                      return a
                    }
                  }
                }""");
        assertThat((long[]) runner.createInstanceRunner("com.A").<Object>invoke("test")).containsExactly(100L, 200L, 300L);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnArrayWithElements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return [1, 2, 3]
                    }
                  }
                }""");
        Object result = runner.createInstanceRunner("com.A").invoke("test");
        ArrayList<?> list = (ArrayList<?>) result;
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(2);
        assertThat(list.get(2)).isEqualTo(3);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnEmptyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return []
                    }
                  }
                }""");
        Object result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(result.getClass()).isEqualTo(ArrayList.class);
        assertThat(((ArrayList<?>) result).size()).isEqualTo(0);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String[] = ["hello", "world"]
                      return a
                    }
                  }
                }""");
        assertThat((String[]) runner.createInstanceRunner("com.A").<Object>invoke("test")).containsExactly("hello", "world");
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringArrayOperations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String[] = ["hello", "world"]
                      a[0] = "goodbye"
                      return a[0]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("goodbye");
    }
}
