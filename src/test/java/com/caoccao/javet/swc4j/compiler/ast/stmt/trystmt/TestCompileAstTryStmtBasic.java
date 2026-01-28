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
import com.caoccao.javet.swc4j.exceptions.JsError;
import com.caoccao.javet.swc4j.exceptions.JsReferenceError;
import com.caoccao.javet.swc4j.exceptions.JsTypeError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for basic try-catch statements (Phase 1).
 * <p>
 * Tests cover:
 * - Basic try-catch with empty blocks
 * - Try-catch with exception variable access
 * - Try-catch with throw in try
 * - Try-catch with throw in catch
 * - Try-catch accessing error.message
 */
public class TestCompileAstTryStmtBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicTryCatch(JdkVersion jdkVersion) throws Exception {
        // Basic try-catch - no exception thrown
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        result = 42
                      } catch (e) {
                        result = -1
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchAndReturnError(JdkVersion jdkVersion) throws Exception {
        // Catch Error and return it for validation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private throwError(): void {
                      throw new Error()
                    }
                
                    test(): Error {
                      try {
                        this.throwError()
                        return null
                      } catch (e: Error) {
                        return e
                      }
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");

        // Validate the error is JsError type
        assertInstanceOf(JsError.class, result);
        JsError error = (JsError) result;
        assertEquals("Error", error.getName());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchAndReturnReferenceError(JdkVersion jdkVersion) throws Exception {
        // Catch ReferenceError and return it
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): ReferenceError {
                      try {
                        throw new ReferenceError()
                      } catch (e: ReferenceError) {
                        return e
                      }
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");

        assertInstanceOf(JsReferenceError.class, result);
        JsReferenceError error = (JsReferenceError) result;
        assertEquals("ReferenceError", error.getName());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchAndReturnTypeError(JdkVersion jdkVersion) throws Exception {
        // Catch TypeError and return it
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): TypeError {
                      try {
                        throw new TypeError()
                      } catch (e: TypeError) {
                        return e
                      }
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");

        assertInstanceOf(JsTypeError.class, result);
        JsTypeError error = (JsTypeError) result;
        assertEquals("TypeError", error.getName());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCatchErrorInVariable(JdkVersion jdkVersion) throws Exception {
        // Catch error and store in local variable
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Error {
                      let caughtError: Error = null
                      try {
                        throw new Error()
                      } catch (e: Error) {
                        caughtError = e
                      }
                      return caughtError
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");

        assertInstanceOf(JsError.class, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testErrorInVariableBeforeThrow(JdkVersion jdkVersion) throws Exception {
        // Create error in variable, then throw it
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Error {
                      try {
                        let err: Error = new Error()
                        throw err
                      } catch (e: Error) {
                        return e
                      }
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");

        assertInstanceOf(JsError.class, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPassErrorAsParameter(JdkVersion jdkVersion) throws Exception {
        // Pass caught error as method parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private handleError(error: Error): Error {
                      return error
                    }
                
                    test(): Error {
                      try {
                        throw new Error()
                      } catch (e: Error) {
                        return this.handleError(e)
                      }
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");

        assertInstanceOf(JsError.class, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStoreErrorInField(JdkVersion jdkVersion) throws Exception {
        // Store caught error in a field
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private lastError: Error = null
                
                    test(): Error {
                      try {
                        throw new Error()
                      } catch (e: Error) {
                        this.lastError = e
                      }
                      return this.lastError
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");

        assertInstanceOf(JsError.class, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchAccessException(JdkVersion jdkVersion) throws Exception {
        // Access exception in catch block
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): String {
                      let msg: String = ""
                      try {
                        throw new Exception("test message")
                      } catch (e) {
                        msg = e.getMessage()
                      }
                      return msg
                    }
                  }
                }""");
        assertEquals("test message", runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchEmptyCatch(JdkVersion jdkVersion) throws Exception {
        // Empty catch block (silent error swallowing)
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 1
                      try {
                        throw new Exception()
                      } catch (e) {
                      }
                      result = 2
                      return result
                    }
                  }
                }""");
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchExceptionPropagates(JdkVersion jdkVersion) throws Exception {
        // Exception propagates if not caught
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        throw new Exception("uncaught")
                      } catch (e) {
                        // Different exception type, re-throw
                        throw e
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThrows(Exception.class, () -> {
            instanceRunner.invoke("test");
        });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchMultipleStatements(JdkVersion jdkVersion) throws Exception {
        // Multiple statements in try and catch
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let a: int = 1
                      let b: int = 2
                      try {
                        a = 10
                        b = 20
                        throw new Exception()
                      } catch (e) {
                        a = a + 1
                        b = b + 2
                      }
                      return a + b
                    }
                  }
                }""");
        assertEquals(33, (int) runner.createInstanceRunner("com.A").invoke("test"));  // (10+1) + (20+2) = 33
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchNestedExpression(JdkVersion jdkVersion) throws Exception {
        // Try-catch with nested expressions
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let x: int = 5
                      try {
                        x = x * 2 + 3
                      } catch (e) {
                        x = 0
                      }
                      return x
                    }
                  }
                }""");
        assertEquals(13, (int) runner.createInstanceRunner("com.A").invoke("test"));  // 5 * 2 + 3 = 13
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchNoParameter(JdkVersion jdkVersion) throws Exception {
        // Catch without parameter (ES2019+ syntax)
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        throw new Exception()
                      } catch {
                        result = 77
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(77, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchReturnFromCatch(JdkVersion jdkVersion) throws Exception {
        // Return from catch block
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        throw new Exception()
                      } catch (e) {
                        return 99
                      }
                    }
                  }
                }""");
        assertEquals(99, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchWithException(JdkVersion jdkVersion) throws Exception {
        // Try-catch catches an exception
        var runner = getCompiler(jdkVersion).compile("""
                import { Exception } from 'java.lang'
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      try {
                        throw new Exception("test error")
                      } catch (e) {
                        result = 100
                      }
                      return result
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTryCatchWithReturn(JdkVersion jdkVersion) throws Exception {
        // Return from try block
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      try {
                        return 42
                      } catch (e) {
                        return -1
                      }
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
