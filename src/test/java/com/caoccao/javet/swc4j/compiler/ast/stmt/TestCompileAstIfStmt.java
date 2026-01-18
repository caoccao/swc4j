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

package com.caoccao.javet.swc4j.compiler.ast.stmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for if statements
 * Phase 1: Basic if without else
 */
public class TestCompileAstIfStmt extends BaseTestCompileSuite {

    // ========== Phase 1: Basic If (No Else) Tests ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (true) {
                        x = 10
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (false) {
                        x = 10
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(0, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithComparison(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 10
                      if (x > 5) {
                        result = 1
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithMultipleStatements(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      let y: int = 0
                      if (true) {
                        x = 10
                        y = 20
                      }
                      return x + y
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(30, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithLogicalAnd(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 10
                      const y: int = 5
                      if (x > 5 && y < 10) {
                        result = 100
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithLogicalOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 3
                      const y: int = 15
                      if (x > 5 || y > 10) {
                        result = 100
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfWithNegation(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const flag: boolean = false
                      if (!flag) {
                        result = 1
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSequentialIfs(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (true) {
                        x = 10
                      }
                      if (true) {
                        x = x + 5
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test").invoke(instance));
    }

    // ========== Phase 2: If-Else Tests ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfElseTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (true) {
                        x = 10
                      } else {
                        x = 20
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicIfElseFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (false) {
                        x = 10
                      } else {
                        x = 20
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(20, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseWithComparison(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 3
                      let result: int = 0
                      if (x > 5) {
                        result = 100
                      } else {
                        result = 200
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(200, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseBothBranchesModifyVariable(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 5
                      if (x > 10) {
                        x = x * 2
                      } else {
                        x = x + 10
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseWithMultipleStatements(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      let y: int = 0
                      if (false) {
                        x = 10
                        y = 20
                      } else {
                        x = 30
                        y = 40
                      }
                      return x + y
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(70, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseReturnInBothBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 8
                      if (x > 5) {
                        return 100
                      } else {
                        return 200
                      }
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIfElseReturnInOneBranch(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 0
                      if (false) {
                        return 100
                      } else {
                        x = 50
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(50, classA.getMethod("test").invoke(instance));
    }

    // ========== Phase 3: Else-If Chains ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfChainFirstTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const score: int = 95
                      let grade: int = 0
                      if (score >= 90) {
                        grade = 1
                      } else if (score >= 80) {
                        grade = 2
                      } else {
                        grade = 3
                      }
                      return grade
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfChainMiddleTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const score: int = 85
                      let grade: int = 0
                      if (score >= 90) {
                        grade = 1
                      } else if (score >= 80) {
                        grade = 2
                      } else {
                        grade = 3
                      }
                      return grade
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testElseIfChainElseTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const score: int = 70
                      let grade: int = 0
                      if (score >= 90) {
                        grade = 1
                      } else if (score >= 80) {
                        grade = 2
                      } else {
                        grade = 3
                      }
                      return grade
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleElseIf(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 15
                      let result: int = 0
                      if (x < 10) {
                        result = 1
                      } else if (x < 15) {
                        result = 2
                      } else if (x < 20) {
                        result = 3
                      } else {
                        result = 4
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    // ========== Phase 4: Nested If Statements ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInThen(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 10
                      const y: int = 5
                      if (x > 5) {
                        if (y > 3) {
                          result = 100
                        }
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfInElse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const x: int = 3
                      const y: int = 8
                      if (x > 5) {
                        result = 50
                      } else {
                        if (y > 7) {
                          result = 100
                        }
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeepNesting(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const a: boolean = true
                      const b: boolean = false
                      const c: boolean = true
                      if (a) {
                        if (b) {
                          if (c) {
                            result = 1
                          }
                        } else {
                          if (c) {
                            result = 2
                          }
                        }
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedIfElse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 10
                      const y: int = 20
                      let result: int = 0
                      if (x > 5) {
                        if (y > 15) {
                          result = 1
                        } else {
                          result = 2
                        }
                      } else {
                        if (y > 15) {
                          result = 3
                        } else {
                          result = 4
                        }
                      }
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("test").invoke(instance));
    }

    // ========== Phase 5: Edge Cases ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyIfBody(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 5
                      if (false) {
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(5, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyElseBody(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 5
                      if (true) {
                        x = 10
                      } else {
                      }
                      return x
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    // TODO: Enable when string comparison operator is fully implemented
    // @ParameterizedTest
    // @EnumSource(JdkVersion.class)
    // public void testIfWithStringComparison(JdkVersion jdkVersion) throws Exception {
    //     var map = getCompiler(jdkVersion).compile("""
    //             namespace com {
    //               export class A {
    //                 test(): int {
    //                   const str1: string = "hello"
    //                   const str2: string = "hello"
    //                   let result: int = 0
    //                   if (str1 == str2) {
    //                     result = 1
    //                   } else {
    //                     result = 2
    //                   }
    //                   return result
    //                 }
    //               }
    //             }""");
    //     Class<?> classA = loadClass(map.get("com.A"));
    //     var instance = classA.getConstructor().newInstance();
    //     assertEquals(1, classA.getMethod("test").invoke(instance));
    // }

    // TODO: Enable when assignment expression type handling is fixed
    // @ParameterizedTest
    // @EnumSource(JdkVersion.class)
    // public void testIfWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
    //     var map = getCompiler(jdkVersion).compile("""
    //             namespace com {
    //               export class A {
    //                 test(): int {
    //                   let intVar: int = 0
    //                   let longVar: long = 0
    //                   let doubleVar: double = 0.0
    //                   if (true) {
    //                     intVar = 10
    //                     longVar = 100
    //                     doubleVar = 1.5
    //                   }
    //                   return intVar
    //                 }
    //               }
    //             }""");
    //     Class<?> classA = loadClass(map.get("com.A"));
    //     var instance = classA.getConstructor().newInstance();
    //     assertEquals(10, classA.getMethod("test").invoke(instance));
    // }
}
