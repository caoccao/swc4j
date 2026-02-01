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

package com.caoccao.javet.swc4j.compiler.ast.pat.restpat;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Test suite for rest patterns in function parameters (Phase 1)
 * Tests function parameter varargs using ...rest syntax
 */
public class TestCompileAstRestPatFunctionParam extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicVarargsIntArray(JdkVersion jdkVersion) throws Exception {
        // Basic varargs with int array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    sum(...numbers: int[]): int {
                      let total: int = 0
                      for (let i: int = 0; i < numbers.length; i++) {
                        total += numbers[i]
                      }
                      return total
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("sum", new int[]{1, 2, 3, 4, 5})).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsAfterRegularParams(JdkVersion jdkVersion) throws Exception {
        // Varargs after regular parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    concat(prefix: String, ...parts: String[]): String {
                      let result: String = prefix
                      for (let i: int = 0; i < parts.length; i++) {
                        result += parts[i]
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<String>invoke("concat", "Hello", new String[]{" ", "World", "!"})).isEqualTo("Hello World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsAfterRegularParamsEmpty(JdkVersion jdkVersion) throws Exception {
        // Varargs after regular parameters with empty varargs
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    format(template: String, ...args: Object[]): String {
                      return template + args.length
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<String>invoke("format", "Count: ", new Object[]{})).isEqualTo("Count: 0");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsAsOnlyParameter(JdkVersion jdkVersion) throws Exception {
        // Varargs as the only parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    max(...values: double[]): double {
                      let maxVal: double = values[0]
                      for (let i: int = 1; i < values.length; i++) {
                        if (values[i] > maxVal) {
                          maxVal = values[i]
                        }
                      }
                      return maxVal
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("max", new double[]{1.5, 3.7, 2.1, 0.9})).isEqualTo(3.7);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsBooleanArray(JdkVersion jdkVersion) throws Exception {
        // Varargs with boolean array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    allTrue(...flags: boolean[]): boolean {
                      for (let i: int = 0; i < flags.length; i++) {
                        if (!flags[i]) return false
                      }
                      return true
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Boolean>invoke("allTrue", new boolean[]{true, true, true})).isTrue();
        assertThat(runner.createInstanceRunner("com.A").<Boolean>invoke("allTrue", new boolean[]{true, false, true})).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsDoubleArray(JdkVersion jdkVersion) throws Exception {
        // Varargs with double array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    average(...values: double[]): double {
                      if (values.length === 0) return 0.0
                      let sum: double = 0.0
                      for (let i: int = 0; i < values.length; i++) {
                        sum += values[i]
                      }
                      return sum / values.length
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Double>invoke("average", new double[]{1.0, 2.0, 3.0, 4.0})).isEqualTo(2.5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsEmpty(JdkVersion jdkVersion) throws Exception {
        // Empty varargs call
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    isEmpty(...values: int[]): boolean {
                      return values.length === 0
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Boolean>invoke("isEmpty", new int[]{})).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsInStaticMethod(JdkVersion jdkVersion) throws Exception {
        // Varargs in static method
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    public static sum(...numbers: int[]): int {
                      let total: int = 0
                      for (let i: int = 0; i < numbers.length; i++) {
                        total += numbers[i]
                      }
                      return total
                    }
                  }
                }""");
        assertThat(runner.createStaticRunner("com.A").<Integer>invoke("sum", new int[]{10, 20, 30})).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsIndexAccess(JdkVersion jdkVersion) throws Exception {
        // Accessing elements by index
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getSecond(...values: int[]): int {
                      return values[1]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("getSecond", new int[]{10, 20, 30})).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsIterationWithForOf(JdkVersion jdkVersion) throws Exception {
        // Varargs iteration with for-of loop
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    sum(...numbers: int[]): int {
                      let total: int = 0
                      for (const num of numbers) {
                        total += num
                      }
                      return total
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("sum", new int[]{5, 10, 15, 20})).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsLengthProperty(JdkVersion jdkVersion) throws Exception {
        // Accessing length property of varargs
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    count(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("count", new int[]{1, 2, 3, 4, 5, 6, 7})).isEqualTo(7);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsMultipleCallsSameMethod(JdkVersion jdkVersion) throws Exception {
        // Multiple calls to the same varargs method passing arrays explicitly
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    sum(...numbers: int[]): int {
                      let total: int = 0
                      for (let i: int = 0; i < numbers.length; i++) {
                        total += numbers[i]
                      }
                      return total
                    }
                    test(): int {
                      const arr1: int[] = [1, 2, 3]
                      const arr2: int[] = [10, 20]
                      const a: int = this.sum(arr1)
                      const b: int = this.sum(arr2)
                      return a + b
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("test")).isEqualTo(36);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsNestedCall(JdkVersion jdkVersion) throws Exception {
        // Varargs function calling another varargs function
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    sum(...numbers: int[]): int {
                      let total: int = 0
                      for (let i: int = 0; i < numbers.length; i++) {
                        total += numbers[i]
                      }
                      return total
                    }
                    double(...numbers: int[]): int {
                      return this.sum(numbers) * 2
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("double", new int[]{1, 2, 3})).isEqualTo(12);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsObjectArray(JdkVersion jdkVersion) throws Exception {
        // Varargs with Object array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    count(...items: Object[]): int {
                      return items.length
                    }
                  }
                }""");
        // Wrap the Object[] in another Object[] to prevent Method.invoke from unpacking it
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("count", (Object) new Object[]{1, "test", 3.0, true})).isEqualTo(4);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsReturnArray(JdkVersion jdkVersion) throws Exception {
        // Varargs returning the array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    identity(...values: int[]): int[] {
                      return values
                    }
                  }
                }""");
        assertArrayEquals(new int[]{1, 2, 3}, runner.createInstanceRunner("com.A").invoke("identity", new int[]{1, 2, 3}));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsSingleElement(JdkVersion jdkVersion) throws Exception {
        // Single element in varargs
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    identity(...values: int[]): int {
                      return values[0]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("identity", new int[]{42})).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsStringArray(JdkVersion jdkVersion) throws Exception {
        // Varargs with String array
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    join(separator: String, ...parts: String[]): String {
                      if (parts.length === 0) return ""
                      let result: String = parts[0]
                      for (let i: int = 1; i < parts.length; i++) {
                        result += separator + parts[i]
                      }
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<String>invoke("join", ",", new String[]{"a", "b", "c"})).isEqualTo("a,b,c");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsWithManyElements(JdkVersion jdkVersion) throws Exception {
        // Varargs with many elements
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    count(...values: int[]): int {
                      return values.length
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("count", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsWithNoTypeAnnotation(JdkVersion jdkVersion) throws Exception {
        // Varargs without type annotation defaults to Object[]
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    count(...args): int {
                      return args.length
                    }
                  }
                }""");
        // Wrap the Object[] in another Object[] to prevent Method.invoke from unpacking it
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("count", (Object) new Object[]{1, "test", 3.0})).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsWithTwoRegularParams(JdkVersion jdkVersion) throws Exception {
        // Varargs after two regular parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    calc(a: int, b: int, ...rest: int[]): int {
                      let total: int = a + b
                      for (let i: int = 0; i < rest.length; i++) {
                        total += rest[i]
                      }
                      return total
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Integer>invoke("calc", 10, 20, new int[]{5, 15})).isEqualTo(50);
    }
}
