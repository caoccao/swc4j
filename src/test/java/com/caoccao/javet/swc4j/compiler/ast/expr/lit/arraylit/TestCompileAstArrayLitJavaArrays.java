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
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Java native arrays and unsupported operations on Java arrays.
 */
public class TestCompileAstArrayLitJavaArrays extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayConcatNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          const b: int[] = [4, 5, 6]
                          return a.concat(b)
                        }
                      }
                    }""");
            fail("Should throw exception for concat on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayCopyWithinNotSupported(JdkVersion jdkVersion) {
        assertThrows(Swc4jByteCodeCompilerException.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const arr: int[] = [1, 2, 3, 4, 5]
                          arr.copyWithin(0, 2)
                          return arr
                        }
                      }
                    }""");
        });
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayDeleteNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          delete a[1]
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for delete on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("Delete operator not supported on Java arrays"), "Expected error about delete, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayFillNotSupported(JdkVersion jdkVersion) {
        assertThrows(Swc4jByteCodeCompilerException.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const arr: int[] = [1, 2, 3, 4, 5]
                          arr.fill(0)
                          return arr
                        }
                      }
                    }""");
        });
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIncludesNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          return a.includes(2)
                        }
                      }
                    }""");
            fail("Should throw exception for includes on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexGet(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [10, 20, 30]
                      return a[1]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(20, classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexOfNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          return a.indexOf(2)
                        }
                      }
                    }""");
            fail("Should throw exception for indexOf on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayIndexSet(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [10, 20, 30]
                      a[1] = 99
                      return a[1]
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(99, classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayJoinNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          return a.join(",")
                        }
                      }
                    }""");
            fail("Should throw exception for join on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayLastIndexOfNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          return a.lastIndexOf(2)
                        }
                      }
                    }""");
            fail("Should throw exception for lastIndexOf on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayLength(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int[] = [1, 2, 3, 4, 5]
                      return a.length
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(5, classA.getMethod("test").invoke(instance));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayPopNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          a.pop()
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for pop on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayPushNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          a.push(4)
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for push on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayReverseNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          a.reverse()
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for reverse on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArraySetLengthNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          a.length = 0
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for setting length on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("Cannot set length on Java array"), "Expected error about setting length, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayShiftNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          a.shift()
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for shift on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArraySliceNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3, 4, 5]
                          return a.slice(1, 3)
                        }
                      }
                    }""");
            fail("Should throw exception for slice on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArraySortNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [3, 1, 2]
                          a.sort()
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for sort on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArraySpliceNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3, 4, 5]
                          return a.splice(1, 2)
                        }
                      }
                    }""");
            fail("Should throw exception for splice on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayToLocaleStringNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          return a.toLocaleString()
                        }
                      }
                    }""");
            fail("Should throw exception for toLocaleString on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayToReversedNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          return a.toReversed()
                        }
                      }
                    }""");
            fail("Should throw exception for toReversed on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayToSortedNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [3, 1, 2]
                          return a.toSorted()
                        }
                      }
                    }""");
            fail("Should throw exception for toSorted on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testJavaArrayUnshiftNotSupported(JdkVersion jdkVersion) {
        try {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: int[] = [1, 2, 3]
                          a.unshift(0)
                          return a
                        }
                      }
                    }""");
            fail("Should throw exception for unshift on Java array");
        } catch (Exception e) {
            String message = e.getMessage() + (e.getCause() != null ? " " + e.getCause().getMessage() : "");
            assertTrue(message.contains("not supported on Java arrays"), "Expected error about Java arrays, got: " + message);
        }
    }
}
