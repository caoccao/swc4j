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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for complex function return type inference.
 * <p>
 * Tests cover:
 * - Nested expressions with multiple operations
 * - Conditional returns with if/else blocks
 * - Method chains and nested calls
 * - Object/array literal returns
 * - Mixed arithmetic with type conversions
 * - Parenthesized expressions
 * - Chained member access
 */
public class TestCompileAstFunctionComplexInference extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayLiteralReturn(JdkVersion jdkVersion) throws Exception {
        // Return array literal - should infer ArrayList
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return [1, 2, 3]
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isInstanceOf(ArrayList.class);
        @SuppressWarnings("unchecked")
        ArrayList<Object> list = (ArrayList<Object>) result;
        assertThat(list).containsExactly(1, 2, 3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedFieldAccessExplicitType(JdkVersion jdkVersion) throws Exception {
        // Test chained field access with explicit return type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class B {
                    value: int = 100
                  }
                  export class A {
                    b: B = new B()
                    test(): int {
                      return this.b.value
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedFieldAccessInferred(JdkVersion jdkVersion) throws Exception {
        // Test chained field access with inferred return type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class B {
                    value: int = 100
                  }
                  export class A {
                    b: B = new B()
                    test() {
                      return this.b.value
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedMemberAccess(JdkVersion jdkVersion) throws Exception {
        // Chained member access - field access on returned object
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class B {
                    value: int = 100
                  }
                  export class A {
                    b: B = new B()
                    test() {
                      return this.b.value
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedMethodCalls(JdkVersion jdkVersion) throws Exception {
        // Chained method/property access - should infer from final property's type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    helper(): String {
                      return "hello"
                    }
                    test() {
                      return this.helper().length
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConditionalReturnBoolean(JdkVersion jdkVersion) throws Exception {
        // Conditional return with boolean values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int) {
                      if (x > 0) {
                        return true
                      } else {
                        return false
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.invoke("test", 5)).isTrue();
        assertThat((boolean) instanceRunner.invoke("test", -5)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConditionalReturnInt(JdkVersion jdkVersion) throws Exception {
        // Conditional return with if/else blocks
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int) {
                      if (x > 0) {
                        return x
                      } else {
                        return -x
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("test", 5)).isEqualTo(5);
        assertThat((int) instanceRunner.invoke("test", -5)).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConditionalReturnMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Conditional return with different numeric types - should widen to common type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean) {
                      if (flag) {
                        return 10
                      } else {
                        return 20.5
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((double) instanceRunner.invoke("test", true)).isCloseTo(10.0, within(0.001));
        assertThat((double) instanceRunner.invoke("test", false)).isCloseTo(20.5, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConditionalReturnString(JdkVersion jdkVersion) throws Exception {
        // Conditional return with String values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(flag: boolean) {
                      if (flag) {
                        return "yes"
                      } else {
                        return "no"
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((String) instanceRunner.invoke("test", true)).isEqualTo("yes");
        assertThat((String) instanceRunner.invoke("test", false)).isEqualTo("no");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedArithmeticDouble(JdkVersion jdkVersion) throws Exception {
        // Mixed int and double - should widen to double
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: double) {
                      return a + b
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test", 5, 2.5);
        assertThat((double) result).isCloseTo(7.5, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedArithmeticLong(JdkVersion jdkVersion) throws Exception {
        // Mixed int and long - should widen to long
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: long) {
                      return a * b
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test", 10, 100L);
        assertThat((long) result).isEqualTo(1000L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedArithmetic(JdkVersion jdkVersion) throws Exception {
        // Nested arithmetic expression - (a + b) * (c - d)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int, c: int, d: int) {
                      return (a + b) * (c - d)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test", 2, 3, 10, 4);
        // (2 + 3) * (10 - 4) = 5 * 6 = 30
        assertThat((int) result).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedConditional(JdkVersion jdkVersion) throws Exception {
        // Nested ternary expressions
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int) {
                      return x > 10 ? (x > 20 ? 2 : 1) : 0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(
                List.of(
                        instanceRunner.invoke("test", 5),
                        instanceRunner.invoke("test", 15),
                        (int) instanceRunner.invoke("test", 25)
                )
        ).isEqualTo(
                List.of(0, 1, 2)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedFunctionCalls(JdkVersion jdkVersion) throws Exception {
        // Nested function calls - helper1(helper2())
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    helper1(x: int): int {
                      return x * 2
                    }
                    helper2(): int {
                      return 10
                    }
                    test() {
                      return this.helper1(this.helper2())
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfElse(JdkVersion jdkVersion) throws Exception {
        // Nested if/else blocks
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int, y: int) {
                      if (x > 0) {
                        if (y > 0) {
                          return 1
                        } else {
                          return 2
                        }
                      } else {
                        if (y > 0) {
                          return 3
                        } else {
                          return 4
                        }
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(
                List.of(
                        instanceRunner.invoke("test", 5, 10),
                        instanceRunner.invoke("test", 5, -10),
                        instanceRunner.invoke("test", -5, 10),
                        (int) instanceRunner.invoke("test", -5, -10)
                )
        ).isEqualTo(
                List.of(1, 2, 3, 4)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectLiteralReturn(JdkVersion jdkVersion) throws Exception {
        // Return object literal - should infer LinkedHashMap
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      return { a: 1, b: 2 }
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;
        assertThat(map.get("a")).isEqualTo(1);
        assertThat(map.get("b")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParenthesizedExpression(JdkVersion jdkVersion) throws Exception {
        // Parenthesized expression - should infer through parens
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int) {
                      return ((a + b))
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test", 10, 20);
        assertThat((int) result).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringConcatenation(JdkVersion jdkVersion) throws Exception {
        // String concatenation - should infer String
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: String, b: String) {
                      return a + b
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test", "hello", "world");
        assertThat((String) result).isEqualTo("helloworld");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringConcatenationWithNumber(JdkVersion jdkVersion) throws Exception {
        // String concatenation with number - should infer String
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: String, b: int) {
                      return a + b
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test", "value:", 42);
        assertThat((String) result).isEqualTo("value:42");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThisFieldReturn(JdkVersion jdkVersion) throws Exception {
        // Return this.field - should infer from field type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 42
                    test() {
                      return this.value
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(42);
    }
}
