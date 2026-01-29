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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for Java native arrays and unsupported operations on Java arrays.
 */
public class TestCompileAstArrayLitJavaArrays extends BaseTestCompileSuite {
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayBooleanOperations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const arr: boolean[] = [true, false, true, false]
                      return arr.includes(true)
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayDoubleOperations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const arr: double[] = [3.14, 2.71, 1.41]
                      arr.sort()
                      return arr[0]
                    }
                  }
                }""");
        assertEquals(1.41, runner.createInstanceRunner("com.A").invoke("test"), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayFillInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [1, 2, 3, 4, 5]
                      arr.fill(0)
                      return arr[2]
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayFillString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: String[] = ["a", "b", "c"]
                      arr.fill("x")
                      return arr[1]
                    }
                  }
                }""");
        assertEquals("x", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIncludesFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const arr: int[] = [10, 20, 30]
                      return arr.includes(99)
                    }
                  }
                }""");
        assertEquals(false, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIncludesTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const arr: int[] = [10, 20, 30]
                      return arr.includes(20)
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexGet(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [10, 20, 30]
                      return a[1]
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexOfInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [10, 20, 30, 20, 10]
                      return arr.indexOf(20)
                    }
                  }
                }""");
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexOfNotFound(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [10, 20, 30]
                      return arr.indexOf(99)
                    }
                  }
                }""");
        assertEquals(-1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexOfString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: String[] = ["apple", "banana", "cherry"]
                      return arr.indexOf("banana")
                    }
                  }
                }""");
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexSet(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [10, 20, 30]
                      a[1] = 99
                      return a[1]
                    }
                  }
                }""");
        assertEquals(99, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayJoinEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: int[] = [1, 2, 3]
                      return arr.join("")
                    }
                  }
                }""");
        assertEquals("123", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayJoinInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: int[] = [1, 2, 3, 4, 5]
                      return arr.join(",")
                    }
                  }
                }""");
        assertEquals("1,2,3,4,5", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayJoinString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: String[] = ["apple", "banana", "cherry"]
                      return arr.join(" - ")
                    }
                  }
                }""");
        assertEquals("apple - banana - cherry", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayLastIndexOfInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [10, 20, 30, 20, 10]
                      return arr.lastIndexOf(20)
                    }
                  }
                }""");
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayLastIndexOfNotFound(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [10, 20, 30]
                      return arr.lastIndexOf(99)
                    }
                  }
                }""");
        assertEquals(-1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [1, 2, 3, 4, 5]
                      return a.length
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayReverseInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [1, 2, 3, 4, 5]
                      arr.reverse()
                      return arr[0]
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayReverseString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: String[] = ["a", "b", "c"]
                      arr.reverse()
                      return arr[0]
                    }
                  }
                }""");
        assertEquals("c", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArraySortInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [3, 1, 4, 1, 5, 9, 2, 6]
                      arr.sort()
                      return arr[0]
                    }
                  }
                }""");
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArraySortString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: String[] = ["cherry", "apple", "banana"]
                      arr.sort()
                      return arr[0]
                    }
                  }
                }""");
        assertEquals("apple", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayToReversedInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [1, 2, 3, 4, 5]
                      const reversed: int[] = arr.toReversed()
                      return reversed[0] + arr[0]
                    }
                  }
                }""");
        assertEquals(6, (int) runner.createInstanceRunner("com.A").invoke("test")); // reversed[0] is 5, arr[0] is still 1
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayToSortedInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: int[] = [3, 1, 4, 1, 5]
                      const sorted: int[] = arr.toSorted()
                      return sorted[0] + arr[0]
                    }
                  }
                }""");
        assertEquals(4, (int) runner.createInstanceRunner("com.A").invoke("test")); // sorted[0] is 1, arr[0] is still 3
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayToStringBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: boolean[] = [true, false, true]
                      return arr.toString()
                    }
                  }
                }""");
        assertEquals("true,false,true", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayToStringInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: int[] = [1, 2, 3]
                      return arr.toString()
                    }
                  }
                }""");
        assertEquals("1,2,3", runner.createInstanceRunner("com.A").invoke("test"));
    }
}
