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

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for the Array.fill() method.
 */
public class TestCompileAstArrayLitFill extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillBothNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill(0, -4, -1)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 0, 0, 0, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillEmptyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = []
                      arr.fill(0)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillEntireArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill(0)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(0, 0, 0, 0, 0));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillMethodChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.fill(0, 1, 3).reverse()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(5, 4, 0, 0, 1));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillNegativeEnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill(0, 1, -1)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 0, 0, 0, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillNegativeStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill(0, -3)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 0, 0, 0));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillOutOfBoundsStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.fill(0, 10)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillReturnsArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.fill(0, 2, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 0, 0, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillStartGreaterThanEnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill(0, 3, 1)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillWithStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill(0, 2)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 0, 0, 0));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillWithStartAndEnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill(0, 1, 4)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 0, 0, 0, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFillWithString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.fill("x", 1, 4)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, "x", "x", "x", 5));
    }
}
