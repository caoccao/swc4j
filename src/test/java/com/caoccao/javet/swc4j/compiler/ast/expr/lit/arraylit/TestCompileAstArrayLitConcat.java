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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the Array.concat() method.
 */
public class TestCompileAstArrayLitConcat extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = [1, 2, 3]
                      const arr2 = [4, 5, 6]
                      return arr1.concat(arr2)
                    }
                  }
                }""");
        assertEquals(List.of(1, 2, 3, 4, 5, 6), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = [1, 2, 3]
                      const arr2 = [4, 5, 6]
                      arr2.pop()
                      arr2.pop()
                      arr2.pop()
                      return arr1.concat(arr2)
                    }
                  }
                }""");
        assertEquals(List.of(1, 2, 3), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = [1, "hello"]
                      const arr2 = [true, 3.14]
                      return arr1.concat(arr2)
                    }
                  }
                }""");
        assertEquals(List.of(1, "hello", true, 3.14), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = [1, 2]
                      const arr2 = [3, 4]
                      const arr3 = [5, 6]
                      return arr1.concat(arr2).concat(arr3)
                    }
                  }
                }""");
        assertEquals(List.of(1, 2, 3, 4, 5, 6), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatNoArg(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const copy = arr.concat()
                      arr.push(4)
                      return copy
                    }
                  }
                }""");
        assertEquals(List.of(1, 2, 3), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatReturnsNewArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = [1, 2]
                      const arr2 = [3, 4]
                      const result = arr1.concat(arr2)
                      arr1.push(99)
                      return result
                    }
                  }
                }""");
        assertEquals(List.of(1, 2, 3, 4), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = [1]
                      const arr2 = [2]
                      return arr1.concat(arr2)
                    }
                  }
                }""");
        assertEquals(List.of(1, 2), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = ["a", "b"]
                      const arr2 = ["c", "d"]
                      return arr1.concat(arr2)
                    }
                  }
                }""");
        assertEquals(List.of("a", "b", "c", "d"), runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayConcatWithEmptyFirst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr1 = [1, 2]
                      arr1.pop()
                      arr1.pop()
                      const arr2 = [3, 4, 5]
                      return arr1.concat(arr2)
                    }
                  }
                }""");
        assertEquals(List.of(3, 4, 5), runner.createInstanceRunner("com.A").invoke("test"));
    }
}
