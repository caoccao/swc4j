/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.ast.stmt.trystmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for instanceof checks on different JS error types.
 * <p>
 * Takes an int, throws corresponding error type, catches it,
 * and uses instanceof to verify the error type.
 */
public class TestCompileAstTryStmtInstanceof extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testErrorIsNotTypeError(JdkVersion jdkVersion) throws Exception {
        // Error should not be instanceof TypeError
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new Error("base error")
                      } catch (e: Error) {
                        return e instanceof TypeError
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new Error("base error")
                      } catch (e: Error) {
                        return e instanceof Error
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofEvalError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new EvalError("eval error")
                      } catch (e: Error) {
                        return e instanceof EvalError
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofRangeError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new RangeError("range error")
                      } catch (e: Error) {
                        return e instanceof RangeError
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofReferenceError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new ReferenceError("reference error")
                      } catch (e: Error) {
                        return e instanceof ReferenceError
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofSyntaxError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new SyntaxError("syntax error")
                      } catch (e: Error) {
                        return e instanceof SyntaxError
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofTypeError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new TypeError("type error")
                      } catch (e: Error) {
                        return e instanceof TypeError
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofURIError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new URIError("uri error")
                      } catch (e: Error) {
                        return e instanceof URIError
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceofWithBitmask(JdkVersion jdkVersion) throws Exception {
        // Return bitmask of which instanceof checks pass
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(errorType: int): int {
                      try {
                        if (errorType == 0) {
                          throw new Error("e")
                        }
                        if (errorType == 1) {
                          throw new TypeError("t")
                        }
                        if (errorType == 2) {
                          throw new RangeError("r")
                        }
                        throw new ReferenceError("ref")
                      } catch (e: Error) {
                        let result: int = 0
                        let isError: boolean = e instanceof Error
                        let isType: boolean = e instanceof TypeError
                        let isRange: boolean = e instanceof RangeError
                        let isRef: boolean = e instanceof ReferenceError
                        if (isError) result = result + 1
                        if (isType) result = result + 10
                        if (isRange) result = result + 100
                        if (isRef) result = result + 1000
                        return result
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        // Error: only Error instanceof passes
        assertThat((int) instanceRunner.invoke("test", 0)).isEqualTo(1);
        // TypeError: both Error and TypeError pass (TypeError extends Error)
        assertThat((int) instanceRunner.invoke("test", 1)).isEqualTo(11);
        // RangeError: both Error and RangeError pass
        assertThat((int) instanceRunner.invoke("test", 2)).isEqualTo(101);
        // ReferenceError: both Error and ReferenceError pass
        assertThat((int) instanceRunner.invoke("test", 3)).isEqualTo(1001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegatedInstanceof(JdkVersion jdkVersion) throws Exception {
        // Test negated instanceof
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(throwType: int): boolean {
                      try {
                        if (throwType == 0) {
                          throw new Error("base")
                        }
                        throw new TypeError("type")
                      } catch (e: Error) {
                        let isType: boolean = e instanceof TypeError
                        return !isType
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        // Error (not TypeError) -> true
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 0)).isTrue();
        // TypeError -> false (negated)
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 1)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThrowByIntAndCheckRangeError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private createError(t: int): Error {
                      if (t == 1) {
                        return new RangeError("r")
                      }
                      return new Error("e")
                    }
                
                    test(t: int): boolean {
                      try {
                        throw this.createError(t)
                      } catch (e: Error) {
                        return e instanceof RangeError
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 1)).isTrue();
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 0)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThrowByIntAndCheckReferenceError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private createError(t: int): Error {
                      if (t == 1) {
                        return new ReferenceError("ref")
                      }
                      return new Error("e")
                    }
                
                    test(t: int): boolean {
                      try {
                        throw this.createError(t)
                      } catch (e: Error) {
                        return e instanceof ReferenceError
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 1)).isTrue();
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 0)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testThrowByIntAndCheckTypeError(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private createError(t: int): Error {
                      if (t == 1) {
                        return new TypeError("t")
                      }
                      return new Error("e")
                    }
                
                    test(t: int): boolean {
                      try {
                        throw this.createError(t)
                      } catch (e: Error) {
                        return e instanceof TypeError
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 1)).isTrue();
        assertThat((boolean) instanceRunner.<Boolean>invoke("test", 0)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeErrorIsAlsoError(JdkVersion jdkVersion) throws Exception {
        // TypeError should also be instanceof Error (inheritance)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new TypeError("type error")
                      } catch (e: Error) {
                        return e instanceof Error
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }
}
