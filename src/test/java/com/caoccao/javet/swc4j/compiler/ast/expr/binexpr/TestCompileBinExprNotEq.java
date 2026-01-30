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

package com.caoccao.javet.swc4j.compiler.ast.expr.binexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileBinExprNotEq extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteIntInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 50
                      const b: int = 50
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteShortStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 100
                      const b: short = 100
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 127
                      const b: byte = 127
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 127
                      const b: byte = 126
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: int = 10
                      const c: int = 10
                      const eq1 = a != b
                      const eq2 = b != c
                      const result = eq1 != eq2
                      return result
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); //Both comparisons are false (0 != 0 = 0)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: int = 5
                      const c: int = 5
                      const eq1 = a !== b
                      const eq2 = b !== c
                      const result = eq1 !== eq2
                      return result
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Both are 0, so 0 !== 0 = 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.14
                      const b: double = 3.14
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 3.14
                      const b: double = 3.15
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 2.71828
                      const b: double = 2.71828
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 2.71828
                      const b: double = 2.71829
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyStringStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = ""
                      const b: String = ""
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal strings should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDoubleStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.5
                      const b: double = 1.5
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 2.5
                      const b: float = 2.5
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.5
                      const b: float = 1.5
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.5
                      const b: float = 1.6
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 42
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 43
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: long = 100
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: long = 101
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 250
                      const b: long = 250
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntLongStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 250
                      const b: long = 251
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 42
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 43
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerWrapperInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 42
                      const b: Integer = 42
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerWrapperInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 42
                      const b: Integer = 43
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerWrapperStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 100
                      const b: Integer = 100
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerWrapperStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 100
                      const b: Integer = 101
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeDoubleStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 1.7976931348623157E308
                      const b: double = 1.7976931348623157E308
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567890
                      const b: long = 1234567890
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1234567890
                      const b: long = 1234567891
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 9876543210
                      const b: long = 9876543210
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 9876543210
                      const b: long = 9876543211
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongStringStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = "This is a longer string for testing equality"
                      const b: String = "This is a longer string for testing equality"
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal strings should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMaxIntValueStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2147483647
                      const b: int = 2147483647
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMaxValueInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2147483647
                      const b: int = 2147483647
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMinValueInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -2147483648
                      const b: int = -2147483648
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleMixedTypeStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 10
                      const b: short = 10
                      const c: int = 10
                      const eq1 = a !== b
                      const eq2 = b !== c
                      const result = eq1 !== eq2
                      return result
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Both are 0, so 0 !== 0 = 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeDoubleStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = -3.14159
                      const b: double = -3.14159
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeFloatStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = -2.5
                      const b: float = -2.5
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeIntInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -42
                      const b: int = -42
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeLongInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = -1234567890
                      const b: long = -1234567890
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 32000
                      const b: short = 32000
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 32000
                      const b: short = 32001
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = "hello"
                      const b: String = "hello"
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal strings should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = "hello"
                      const b: String = "world"
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal strings should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringStrictInequalityFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = "test"
                      const b: String = "test"
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal strings should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringStrictInequalityTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = "test"
                      const b: String = "other"
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal strings should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroDoubleStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 0.0
                      const b: double = 0.0
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 0
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 0
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse(); // Inverted: equal values should return 0
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsNonZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 1
                      const c = a != b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testZeroVsNonZeroStrictInequality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 0
                      const b: int = 1
                      const c = a !== b
                      return c
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue(); // Inverted: unequal values should return 1
    }
}
