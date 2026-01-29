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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Tests for Record type validation error cases and type mismatches.
 */
public class TestCompileAstObjectLitRecordValidation extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordInvalidNestedKey(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected nested key type mismatch error, got: " + causeMessage).contains("Key 'a'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedRecordInvalidNestedValue(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected nested type mismatch error, got: " + causeMessage).contains("Property 'inner'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedKeyNumberMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected computed key type mismatch error, got: " + causeMessage).contains("Key").contains("String");
    }

    // Phase 7: Mixed Scenarios (Spread + Shorthand + Computed)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordComputedKeyStringMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected computed key type mismatch error, got: " + causeMessage).contains("Key").contains("double");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerKeyTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected key type mismatch error, got: " + causeMessage).contains("Key 'a'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordLongKeyTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected key type mismatch error, got: " + causeMessage).contains("Key 'a'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordMixedValidAndInvalid(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        // Check the cause contains the validation error
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected type mismatch error for property 'c', got: " + causeMessage).contains("Property 'c'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberKeyTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        // Check the cause contains the validation error
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected key type mismatch error, got: " + causeMessage).contains("Key 'a'").contains("String");
    }

    // Phase 7: 3-Level Nested Record Types

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordNumberValueTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        // Check the cause contains the validation error
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected value type mismatch error, got: " + causeMessage).contains("Property '1'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandKeyTypeMismatch(JdkVersion jdkVersion) {
        // Shorthand keys are always strings, so Record<number, V> should fail
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected shorthand key type mismatch error, got: " + causeMessage).contains("Key 'a'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordShorthandValueTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected shorthand value type mismatch error, got: " + causeMessage).contains("Property 'a'").contains("String");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadKeyTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected spread key type mismatch error, got: " + causeMessage).contains("incompatible key type");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadNestedTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected nested spread value type mismatch error, got: " + causeMessage).contains("incompatible value type");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordSpreadValueTypeMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected spread value type mismatch error, got: " + causeMessage).contains("incompatible value type");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringKeyNumericMismatch(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected key type mismatch error, got: " + causeMessage).contains("Key '1'").contains("Integer");
    }

    // Phase 6: Integration - Return Type Context

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordValueTypeMismatchBoolean(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        // Check the cause contains the validation error
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage)
                .as("Expected type mismatch error for boolean value, got: " + causeMessage)
                .contains("Property 'flag'")
                .contains("has type")
                .contains("but Record requires");
    }

    // Phase 7: Edge Cases 33-34 (Computed Property Type Validation)

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordValueTypeMismatchString(JdkVersion jdkVersion) {
        Throwable exception = catchThrowable(() -> {
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
        assertThat(exception).isInstanceOf(Exception.class);
        // Check the cause contains the validation error
        assertThat(exception.getCause()).as("Expected wrapped exception").isNotNull();
        String causeMessage = exception.getCause().getMessage();
        assertThat(causeMessage).as("Expected type mismatch error for value type, got: " + causeMessage).contains("Property 'a' has type String").contains("but Record requires");
    }

}
