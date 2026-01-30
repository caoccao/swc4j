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

package com.caoccao.javet.swc4j.compiler.ast.expr.unaryexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileUnaryExprBang extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBangFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const c = !a
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // !false = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBangLiteralFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = !false
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // !false = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBangLiteralTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const c = !true
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // !true = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBangTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const c = !a
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // !true = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComparisonFalseNegated(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const c = !(x > y)
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !(5 > 10) = !(false).isTrue() = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComparisonTrueNegated(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 3
                      const c = !(x > y)
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !(5 > 3) = !(true).isFalse() = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleBang(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 3
                      const c = !!(x > y)
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !!(5 > 3) = !!(true).isTrue() = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleBangFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const c = !!a
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // !!false = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleBangTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const c = !!a
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // !!true = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEqualityNegated(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 5
                      const c = !(x == y)
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !(5 == 5) = !(true).isFalse() = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInequalityNegated(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const c = !(x != y)
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !(5 != 10) = !(true).isFalse() = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLogicalAndNegated(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = false
                      const c = !(a && b)
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !(true && false) = !(false).isTrue() = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLogicalOrNegated(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = false
                      const b: boolean = false
                      const c = !(a || b)
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !(false || false) = !(false).isTrue() = true
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleBang(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const c = !!!a
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !!!true = !!(false) = !(true).isFalse() = false
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedComparison(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 5
                      const y: int = 10
                      const z: int = 15
                      const c = !((x < y) && (y < z))
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")); // !((5 < 10) && (10 < 15)) = !(true && true) = !(true).isFalse() = false
    }
}
