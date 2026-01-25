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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestCompileAstFunctionReturnTypes extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEarlyReturn(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(x: int): int {
                      if (x < 0) return 0
                      return x * 2
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test", int.class).invoke(instance, -5));
        assertEquals(10, classA.getMethod("test", int.class).invoke(instance, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleReturnStatements(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    sign(x: int): int {
                      if (x < 0) return -1
                      if (x == 0) return 0
                      return 1
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(-1, classA.getMethod("sign", int.class).invoke(instance, -10));
        assertEquals(0, classA.getMethod("sign", int.class).invoke(instance, 0));
        assertEquals(1, classA.getMethod("sign", int.class).invoke(instance, 10));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnBoolean(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    isTrue(): boolean {
                      return true
                    }
                    isFalse(): boolean {
                      return false
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(true, classA.getMethod("isTrue").invoke(instance));
        assertEquals(false, classA.getMethod("isFalse").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnDouble(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      return 3.14159
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(3.14159, (double) classA.getMethod("test").invoke(instance), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnInt(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return 42
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnLong(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      return 9999999999
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(9999999999L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnString(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello world"
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("hello world", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnVoid(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): void {
                      const x: int = 1
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertNull(classA.getMethod("test").invoke(instance));
    }

    // TODO: This test requires proper for-loop iteration support on native int[] which is hanging
    // @ParameterizedTest
    // @EnumSource(JdkVersion.class)
    // public void testReturnInLoop(JdkVersion jdkVersion) throws Exception {
    //     var map = getCompiler(jdkVersion).compile("""
    //             namespace com {
    //               export class A {
    //                 findFirst(arr: int[], target: int): int {
    //                   for (let i: int = 0; i < arr.length; i++) {
    //                     if (arr[i] == target) return i
    //                   }
    //                   return -1
    //                 }
    //               }
    //             }""");
    //     Class<?> classA = loadClass(map.get("com.A"));
    //     var instance = classA.getConstructor().newInstance();
    //     assertEquals(2, classA.getMethod("findFirst", int[].class, int.class).invoke(instance, new int[]{1, 2, 3, 4, 5}, 3));
    //     assertEquals(-1, classA.getMethod("findFirst", int[].class, int.class).invoke(instance, new int[]{1, 2, 3, 4, 5}, 10));
    // }
}
