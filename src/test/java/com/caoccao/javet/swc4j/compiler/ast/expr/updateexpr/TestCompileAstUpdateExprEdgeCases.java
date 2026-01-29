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

package com.caoccao.javet.swc4j.compiler.ast.expr.updateexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for edge cases and error scenarios with update expressions (++ and --).
 * Tests overflow behavior, invalid update targets, compound updates, null pointer exceptions,
 * and other boundary conditions.
 */
public class TestCompileAstUpdateExprEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteMaxValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: byte = 127
                      let result: byte = ++x
                      return result
                    }
                  }
                }""");
        assertEquals((byte) -128, (byte) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteMinValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: byte = -128
                      let result: byte = --x
                      return result
                    }
                  }
                }""");
        assertEquals((byte) 127, (byte) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCompoundUpdateDecrementOnDecrement(JdkVersion jdkVersion) {
        assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          let x: int = 5
                          (--x)--
                          return x
                        }
                      }
                    }""");
        });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCompoundUpdatePostfixOnPostfix(JdkVersion jdkVersion) {
        assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          let x: int = 5
                          (x++)++
                          return x
                        }
                      }
                    }""");
        });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCompoundUpdatePrefixOnPrefix(JdkVersion jdkVersion) {
        assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          let x: int = 5
                          ++(++x)
                          return x
                        }
                      }
                    }""");
        });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleIncrementSeparateStatements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: int = 5
                      x++
                      x++
                      return x
                    }
                  }
                }""");
        assertEquals(7, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleSequentialIncrements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let d: double = 0.1
                      d++
                      d++
                      return d
                    }
                  }
                }""");
        assertEquals(2.1, (double) runner.createInstanceRunner("com.A").invoke("test"), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerMaxValueOverflow(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: int = 2147483647
                      x++
                      return x
                    }
                  }
                }""");
        assertEquals(Integer.MIN_VALUE, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongMaxValueOverflow(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: long = 9223372036854775807
                      x++
                      return x
                    }
                  }
                }""");
        assertEquals(Long.MIN_VALUE, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedIncrementDecrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: int = 10
                      ++x
                      x--
                      ++x
                      return x
                    }
                  }
                }""");
        assertEquals(11, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleUpdatesInExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: int = 5
                      let y: int = 10
                      let result: int = (++x) + (y++)
                      return result
                    }
                  }
                }""");
        assertEquals(16, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeNumberIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: int = -5
                      const result = ++x
                      return result
                    }
                  }
                }""");
        assertEquals(-4, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullWrapperThrowsNPE(JdkVersion jdkVersion) throws ClassNotFoundException {
        var runner = assertDoesNotThrow(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: Integer = null
                      x++
                      return x
                    }
                  }
                }"""));
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThrows(InvocationTargetException.class, () -> {
            instanceRunner.invoke("test");
        });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUpdateOnExpression(JdkVersion jdkVersion) {
        assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          let x: int = 5
                          let y: int = 10
                          (x + y)++
                          return x
                        }
                      }
                    }""");
        });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUpdateOnLiteral(JdkVersion jdkVersion) {
        assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          5++
                          return 0
                        }
                      }
                    }""");
        });
    }
}
