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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.objectlit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Record type validation error cases and type mismatches.
 */
public class TestCompileAstObjectLitRecordValidation extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordInvalidNestedKey(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, Record<number, string>> = {
                            outer: {a: "invalid"}
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected nested key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordInvalidNestedValue(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, Record<string, number>> = {
                            outer: {inner: "invalid"}
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'inner'") &&
                        causeMessage.contains("String"),
                "Expected nested type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedKeyNumberMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const key: string = "stringkey"
                          const obj: Record<number, string> = {
                            [key]: "value"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key") && causeMessage.contains("String"),
                "Expected computed key type mismatch error, got: " + causeMessage);
    }

    // Phase 7: Mixed Scenarios (Spread + Shorthand + Computed)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedKeyStringMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const key: number = 42
                          const obj: Record<string, number> = {
                            [key]: 100
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key") && causeMessage.contains("double"),
                "Expected computed key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<Integer, string> = {
                            a: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordLongKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<Long, string> = {
                            a: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedValidAndInvalid(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, number> = {
                            a: 1,
                            b: 2,
                            c: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'c'") &&
                        causeMessage.contains("String"),
                "Expected type mismatch error for property 'c', got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<number, string> = {
                            a: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") &&
                        causeMessage.contains("String"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    // Phase 7: 3-Level Nested Record Types

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberValueTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<number, number> = {
                            1: "invalid"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property '1'") &&
                        causeMessage.contains("String"),
                "Expected value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandKeyTypeMismatch(JdkVersion jdkVersion) {
        // Shorthand keys are always strings, so Record<number, V> should fail
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: string = "value"
                          const obj: Record<number, string> = {a}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key 'a'") && causeMessage.contains("String"),
                "Expected shorthand key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandValueTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const a: string = "hello"
                          const obj: Record<string, number> = {a}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'a'") && causeMessage.contains("String"),
                "Expected shorthand value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadKeyTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const source: Record<number, string> = {1: "one"}
                          const obj: Record<string, string> = {...source}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("incompatible key type"),
                "Expected spread key type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadNestedTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const source: Record<string, Record<string, string>> = {
                            outer: {inner: "hello"}
                          }
                          const obj: Record<string, Record<string, number>> = {
                            ...source
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("incompatible value type"),
                "Expected nested spread value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadValueTypeMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const source: Record<string, string> = {a: "hello"}
                          const obj: Record<string, number> = {...source}
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("incompatible value type"),
                "Expected spread value type mismatch error, got: " + causeMessage);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringKeyNumericMismatch(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<String, number> = {
                            1: 42
                          }
                          return obj
                        }
                      }
                    }""");
        });
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Key '1'") &&
                        causeMessage.contains("Integer"),
                "Expected key type mismatch error, got: " + causeMessage);
    }

    // Phase 6: Integration - Return Type Context

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordValueTypeMismatchBoolean(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, number> = {
                            flag: true
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'flag'") &&
                        causeMessage.contains("has type") &&
                        causeMessage.contains("but Record requires"),
                "Expected type mismatch error for boolean value, got: " + causeMessage);
    }

    // Phase 7: Edge Cases 33-34 (Computed Property Type Validation)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordValueTypeMismatchString(JdkVersion jdkVersion) {
        var exception = assertThrows(Exception.class, () -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const obj: Record<string, number> = {
                            a: "hello"
                          }
                          return obj
                        }
                      }
                    }""");
        });
        // Check the cause contains the validation error
        assertNotNull(exception.getCause(), "Expected wrapped exception");
        String causeMessage = exception.getCause().getMessage();
        assertTrue(causeMessage.contains("Property 'a' has type String") &&
                        causeMessage.contains("but Record requires"),
                "Expected type mismatch error for value type, got: " + causeMessage);
    }

}
