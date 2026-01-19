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

package com.caoccao.javet.swc4j.compiler.ast.stmt.whilestmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for while loops with complex conditions (Phase 4)
 * Tests complex boolean expressions in test condition
 */
public class TestCompileAstWhileStmtComplex extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileAndCondition(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      let j: int = 10
                      while (i < 10 && j > 0) {
                        sum += i
                        i++
                        j--
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Both conditions true for 10 iterations: sum = 0+1+2+...+9 = 45
        assertEquals(45, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileComplexBoolean(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      let j: int = 20
                      while (i < 10 && j > 10 || i < 5) {
                        sum += i
                        i++
                        j = j - 2
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // i=0,j=20: (0<10 && 20>10) || 0<5 = true || true -> sum=0, i=1, j=18
        // i=1,j=18: (1<10 && 18>10) || 1<5 = true || true -> sum=1, i=2, j=16
        // i=2,j=16: (2<10 && 16>10) || 2<5 = true || true -> sum=3, i=3, j=14
        // i=3,j=14: (3<10 && 14>10) || 3<5 = true || true -> sum=6, i=4, j=12
        // i=4,j=12: (4<10 && 12>10) || 4<5 = true || true -> sum=10, i=5, j=10
        // i=5,j=10: (5<10 && 10>10) || 5<5 = false || false -> stop
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileGreaterThanOrEqualAnd(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 10
                      let j: int = 0
                      while (i >= 5 && j <= 3) {
                        i--
                        j++
                      }
                      return i * 10 + j
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // i=10,j=0: 10>=5 && 0<=3 -> i=9,j=1
        // i=9,j=1: 9>=5 && 1<=3 -> i=8,j=2
        // i=8,j=2: 8>=5 && 2<=3 -> i=7,j=3
        // i=7,j=3: 7>=5 && 3<=3 -> i=6,j=4
        // i=6,j=4: 6>=5 && 4<=3 -> false, stop
        // Result: 6*10 + 4 = 64
        assertEquals(64, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileMultipleComparisons(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let a: int = 0
                      let b: int = 20
                      let c: int = 10
                      let d: int = 5
                      while (a < b && c < d * 3) {
                        a++
                        c++
                      }
                      return a
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // a=0,c=10: 0<20 && 10<15 -> a=1,c=11
        // a=1,c=11: 1<20 && 11<15 -> a=2,c=12
        // a=2,c=12: 2<20 && 12<15 -> a=3,c=13
        // a=3,c=13: 3<20 && 13<15 -> a=4,c=14
        // a=4,c=14: 4<20 && 14<15 -> a=5,c=15
        // a=5,c=15: 5<20 && 15<15 -> false, stop
        assertEquals(5, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileNegatedCondition(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      while (!(i >= 10)) {
                        i++
                      }
                      return i
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileNotEqualCondition(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let i: int = 0
                      let j: int = 10
                      while (i != 5 && j != 5) {
                        i++
                        j--
                      }
                      return i * 10 + j
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // i=0,j=10: 0!=5 && 10!=5 -> i=1,j=9
        // i=1,j=9: 1!=5 && 9!=5 -> i=2,j=8
        // i=2,j=8: 2!=5 && 8!=5 -> i=3,j=7
        // i=3,j=7: 3!=5 && 7!=5 -> i=4,j=6
        // i=4,j=6: 4!=5 && 6!=5 -> i=5,j=5
        // i=5,j=5: 5!=5 && 5!=5 -> false, stop
        // Result: 5*10 + 5 = 55
        assertEquals(55, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testWhileOrCondition(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let sum: int = 0
                      let i: int = 0
                      let j: int = 5
                      while (i < 3 || j < 8) {
                        sum += 1
                        i++
                        j++
                      }
                      return sum
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // i: 0,1,2,3,4,5,6,7,8 (i<3 true for first 3, then j<8 false when j=8)
        // Loop runs while i<3 (true for i=0,1,2) or j<8 (true until j=8)
        // i=0,j=5: true||true -> i=1,j=6
        // i=1,j=6: true||true -> i=2,j=7
        // i=2,j=7: true||true -> i=3,j=8
        // i=3,j=8: false||false -> stop
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }
}
