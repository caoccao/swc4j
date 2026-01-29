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

package com.caoccao.javet.swc4j.compiler.ast.stmt.trystmt;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for catch with destructuring parameters (Phase 4).
 * <p>
 * Tests cover:
 * - Catch destructuring {message}
 * - Catch destructuring {stack}
 * - Catch destructuring {message, stack}
 * - Catch destructuring {message, cause}
 * - Catch with renamed destructuring ({message: msg})
 * - Catch with default values in destructuring
 */
public class TestCompileAstTryStmtDestructuring extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringAllProperties(JdkVersion jdkVersion) throws Exception {
        // Catch destructuring with all four properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new TypeError("type mismatch")
                      } catch ({name, message, stack, cause}) {
                        let hasStack: boolean = stack != null
                        let hasCause: boolean = cause != null
                        return name + "|" + message + "|" + hasStack + "|" + hasCause
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("TypeError|type mismatch|true|false");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringCause(JdkVersion jdkVersion) throws Exception {
        // Catch with destructuring to extract cause - verify cause property is extractable
        // Note: When exception has no cause, getCause() returns null
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Error("test error")
                      } catch ({cause}) {
                        if (cause != null) {
                          return "has cause"
                        }
                        return "no cause"
                      }
                    }
                  }
                }""");
        // Error without explicit cause should have null cause
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("no cause");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringDefaultValue(JdkVersion jdkVersion) throws Exception {
        // Catch with default value when message is null
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Exception()
                      } catch ({message = "default message"}) {
                        return message
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("default message");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringDefaultValueNotUsed(JdkVersion jdkVersion) throws Exception {
        // Catch with default value - value exists, so default not used
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Error("actual message")
                      } catch ({message = "default"}) {
                        return message
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("actual message");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringJavaException(JdkVersion jdkVersion) throws Exception {
        // Catch destructuring with Java Exception (not JS Error)
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Exception("java exception")
                      } catch ({message}) {
                        return message
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("java exception");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringMessage(JdkVersion jdkVersion) throws Exception {
        // Catch with destructuring to extract message
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Error("test error message")
                      } catch ({message}) {
                        return message
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("test error message");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringMessageAndCause(JdkVersion jdkVersion) throws Exception {
        // Catch with destructuring to extract message and cause
        // Note: When exception has no cause, getCause() returns null
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Error("test message")
                      } catch ({message, cause}) {
                        let hasCause: String = cause != null ? "yes" : "no"
                        return message + ":" + hasCause
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("test message:no");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringMessageAndStack(JdkVersion jdkVersion) throws Exception {
        // Catch with destructuring to extract both message and stack
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Error("combined test")
                      } catch ({message, stack}) {
                        // Verify both are accessible
                        if (stack != null) {
                          return message
                        }
                        return "no stack"
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("combined test");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringName(JdkVersion jdkVersion) throws Exception {
        // Catch with destructuring to extract error name
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new TypeError("type error")
                      } catch ({name}) {
                        return name
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("TypeError");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringNameAndMessage(JdkVersion jdkVersion) throws Exception {
        // Catch with destructuring for name and message
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new RangeError("out of range")
                      } catch ({name, message}) {
                        return name + ": " + message
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("RangeError: out of range");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringNoCause(JdkVersion jdkVersion) throws Exception {
        // Catch destructuring when there is no cause
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Error("no cause")
                      } catch ({cause}) {
                        if (cause == null) {
                          return "no cause found"
                        }
                        return "has cause"
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("no cause found");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringRenamed(JdkVersion jdkVersion) throws Exception {
        // Catch with renamed destructuring ({message: msg})
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new Error("renamed message")
                      } catch ({message: msg}) {
                        return msg
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("renamed message");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringRenamedMultiple(JdkVersion jdkVersion) throws Exception {
        // Catch with multiple renamed destructuring properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      try {
                        throw new RangeError("error msg")
                      } catch ({message: errorMessage, name: errorName}) {
                        return errorName + "-" + errorMessage
                      }
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("RangeError-error msg");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringStack(JdkVersion jdkVersion) throws Exception {
        // Catch with destructuring to extract stack trace
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      try {
                        throw new Error("test")
                      } catch ({stack}) {
                        // Stack trace should be non-null
                        return stack != null
                      }
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchDestructuringWithFinally(JdkVersion jdkVersion) throws Exception {
        // Catch destructuring with finally block
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private cleaned: boolean = false
                    test(): String {
                      try {
                        throw new Error("test")
                      } catch ({message}) {
                        return message
                      } finally {
                        this.cleaned = true
                      }
                    }
                    wasCleaned(): boolean {
                      return this.cleaned
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("test");
        assertThat((boolean) instanceRunner.<Boolean>invoke("wasCleaned")).isTrue();
    }
}
