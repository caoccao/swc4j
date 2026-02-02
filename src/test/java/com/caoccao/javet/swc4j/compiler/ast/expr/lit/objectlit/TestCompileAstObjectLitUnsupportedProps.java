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
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestCompileAstObjectLitUnsupportedProps extends BaseTestCompileSuite {
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAsyncMethodRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        async fetchData() {
                          return "data"
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Method properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGetterAndSetterPairRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        get value(): int {
                          return 42
                        },
                        set value(v: int) {
                          // setter
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGetterPropertyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        get name() {
                          return "Alice"
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Getter properties are not supported in object literals")
                .hasMessageContaining("LinkedHashMap cannot implement property descriptors");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGetterWithReturnTypeRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        get value(): int {
                          return 42
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Getter properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodInRecordTypeRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Object> {
                      const obj: Record<string, Object> = {
                        compute() {
                          return 42
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Method properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodPropertyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        greet() {
                          return "hello"
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasRootCauseInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasRootCauseMessage("Method properties are not supported in object literals. " +
                        "Object literals compile to LinkedHashMap which cannot store executable methods. " +
                        "Consider using a class with methods instead.");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodPropertyWithNameRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        getName(): String {
                          return "Alice"
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Method properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodPropertyWithParametersRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        add(a: int, b: int): int {
                          return a + b
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Method properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedSymbolAndValidKeysRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        name: "Alice",
                        age: 30,
                        [Symbol.toStringTag]: "Person"
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedValidAndGetterRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        firstName: "Alice",
                        lastName: "Smith",
                        get fullName(): String {
                          return "Alice Smith"
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Getter properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedValidAndMethodPropertiesRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        name: "Alice",
                        age: 30,
                        greet() {
                          return "hello"
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Method properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleMethodsRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        greet() { return "hello" },
                        farewell() { return "goodbye" }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Method properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleSymbolKeysRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        [Symbol.iterator]: function* () { yield 1 },
                        [Symbol.toStringTag]: "Custom"
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedObjectWithMethodRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        outer: {
                          inner() {
                            return "nested"
                          }
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Method properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSetterPropertyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        set name(value: String) {
                          // setter body
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Setter properties are not supported in object literals")
                .hasMessageContaining("LinkedHashMap cannot implement property descriptors");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSetterWithTypeRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        set count(value: int) {
                          // setter body
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Setter properties are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSymbolCallKeyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        [Symbol("myKey")]: "value"
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported in object literals")
                .hasMessageContaining("Consider using string keys instead");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSymbolForKeyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        [Symbol.for("globalKey")]: "value"
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported in object literals");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSymbolHasInstanceKeyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        [Symbol.hasInstance]: function(obj: Object): boolean {
                          return true
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSymbolInNestedObjectRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        outer: {
                          [Symbol.iterator]: function* () { yield 1 }
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSymbolInRecordTypeRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Object> {
                      const obj: Record<string, Object> = {
                        [Symbol("key")]: "value"
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSymbolIteratorKeyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        [Symbol.iterator]: function* () {
                          yield 1
                          yield 2
                        }
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported in object literals")
                .hasMessageContaining("JavaScript Symbols have no equivalent in Java");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSymbolToStringTagKeyRejected(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Object {
                      const obj = {
                        [Symbol.toStringTag]: "CustomObject"
                      }
                      return obj
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .cause()
                .hasMessageContaining("Symbol keys are not supported");
    }
}
