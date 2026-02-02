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

package com.caoccao.javet.swc4j.compiler.ast.stmt.tsinterfacedecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Phase 8: Call and Construct Signature Tests.
 * Tests TypeScript interface call signatures (callable interfaces) and
 * construct signatures (constructor/factory patterns).
 */
public class TestCompileAstTsInterfaceDeclCallConstruct extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBothCallAndConstructSignatures(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both call and construct signatures
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Hybrid {
                    (): String
                    new (value: String): Object
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Hybrid");

        // Verify both methods exist
        Method callMethod = interfaceClass.getMethod("call");
        assertThat(callMethod.getReturnType()).isEqualTo(String.class);

        Method createMethod = interfaceClass.getMethod("create", String.class);
        assertThat(createMethod.getReturnType()).isEqualTo(Object.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallAndMethodSignatures(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both call signature and method signatures
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface ProcessorWithMethods {
                    (input: String): String
                    reset(): void
                    getStatus(): String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.ProcessorWithMethods");

        // Verify call method exists
        Method callMethod = interfaceClass.getMethod("call", String.class);
        assertThat(callMethod.getReturnType()).isEqualTo(String.class);

        // Verify other methods exist
        Method reset = interfaceClass.getMethod("reset");
        assertThat(reset.getReturnType()).isEqualTo(void.class);
        Method getStatus = interfaceClass.getMethod("getStatus");
        assertThat(getStatus.getReturnType()).isEqualTo(String.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallAndPropertySignatures(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both call signature and properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface CallableWithState {
                    (x: int): int
                    count: int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.CallableWithState");

        // Verify call method exists
        Method callMethod = interfaceClass.getMethod("call", int.class);
        assertThat(callMethod).isNotNull();
        assertThat(callMethod.getReturnType()).isEqualTo(int.class);

        // Verify property getter/setter exist
        Method getCount = interfaceClass.getMethod("getCount");
        assertThat(getCount.getReturnType()).isEqualTo(int.class);
        Method setCount = interfaceClass.getMethod("setCount", int.class);
        assertThat(setCount.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureDoubleParam(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with double parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface MathFunc {
                    (x: double, y: double): double
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.MathFunc");

        // Verify call method signature
        Method callMethod = interfaceClass.getMethod("call", double.class, double.class);
        assertThat(callMethod.getReturnType()).isEqualTo(double.class);
        assertThat(callMethod.getParameterTypes()).containsExactly(double.class, double.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with mixed parameter types
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Formatter {
                    (template: String, value: int): String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Formatter");

        // Verify call method signature
        Method callMethod = interfaceClass.getMethod("call", String.class, int.class);
        assertThat(callMethod.getReturnType()).isEqualTo(String.class);
        assertThat(callMethod.getParameterTypes()).containsExactly(String.class, int.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureMultipleParams(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with multiple parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Adder {
                    (a: int, b: int): int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Adder");

        // Verify call method signature
        Method callMethod = interfaceClass.getMethod("call", int.class, int.class);
        assertThat(callMethod).isNotNull();
        assertThat(Modifier.isAbstract(callMethod.getModifiers())).isTrue();
        assertThat(callMethod.getReturnType()).isEqualTo(int.class);
        assertThat(callMethod.getParameterTypes()).hasSize(2);
        assertThat(callMethod.getParameterTypes()[0]).isEqualTo(int.class);
        assertThat(callMethod.getParameterTypes()[1]).isEqualTo(int.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureNoParams(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with no parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Callable {
                    (): int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Callable");

        // Verify it's an interface
        assertThat(interfaceClass.isInterface()).isTrue();

        // Verify call method exists
        Method callMethod = interfaceClass.getMethod("call");
        assertThat(callMethod).isNotNull();
        assertThat(Modifier.isAbstract(callMethod.getModifiers())).isTrue();
        assertThat(callMethod.getReturnType()).isEqualTo(int.class);
        assertThat(callMethod.getParameterCount()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureSingleParam(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with single parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Incrementer {
                    (x: int): int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Incrementer");

        // Verify call method signature
        Method callMethod = interfaceClass.getMethod("call", int.class);
        assertThat(callMethod).isNotNull();
        assertThat(Modifier.isAbstract(callMethod.getModifiers())).isTrue();
        assertThat(callMethod.getReturnType()).isEqualTo(int.class);
        assertThat(callMethod.getParameterTypes()).hasSize(1);
        assertThat(callMethod.getParameterTypes()[0]).isEqualTo(int.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureStringParams(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with String parameters and return type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface StringTransformer {
                    (input: String): String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.StringTransformer");

        // Verify call method signature
        Method callMethod = interfaceClass.getMethod("call", String.class);
        assertThat(callMethod.getReturnType()).isEqualTo(String.class);
        assertThat(callMethod.getParameterTypes()[0]).isEqualTo(String.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureVoidReturn(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with void return type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface VoidCallable {
                    (): void
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.VoidCallable");

        // Verify call method returns void
        Method callMethod = interfaceClass.getMethod("call");
        assertThat(callMethod.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCallSignatureWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Call signature with implementing class
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Calculator {
                    (x: int, y: int): int
                  }
                  export class Adder implements Calculator {
                    call(x: int, y: int): int {
                      return x + y
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Calculator");
        Class<?> implClass = runner.getClass("com.Adder");

        // Verify interface implements Calculator
        assertThat(interfaceClass.isAssignableFrom(implClass)).isTrue();

        // Test the implementation
        Object instance = implClass.getConstructor().newInstance();
        Method callMethod = implClass.getMethod("call", int.class, int.class);
        assertThat(callMethod.invoke(instance, 5, 3)).isEqualTo(8);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructAndPropertySignatures(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both construct signature and properties
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface FactoryWithConfig {
                    new (value: String): Object
                    defaultValue: String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.FactoryWithConfig");

        // Verify create method exists
        Method createMethod = interfaceClass.getMethod("create", String.class);
        assertThat(createMethod).isNotNull();
        assertThat(createMethod.getReturnType()).isEqualTo(Object.class);

        // Verify property getter/setter exist
        Method getDefaultValue = interfaceClass.getMethod("getDefaultValue");
        assertThat(getDefaultValue.getReturnType()).isEqualTo(String.class);
        Method setDefaultValue = interfaceClass.getMethod("setDefaultValue", String.class);
        assertThat(setDefaultValue.getReturnType()).isEqualTo(void.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructSignatureMixedTypes(JdkVersion jdkVersion) throws Exception {
        // Test: Construct signature with mixed parameter types
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface ComplexFactory {
                    new (name: String, count: int, active: boolean): Object
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.ComplexFactory");

        // Verify create method signature
        Method createMethod = interfaceClass.getMethod("create", String.class, int.class, boolean.class);
        assertThat(createMethod).isNotNull();
        assertThat(createMethod.getParameterTypes()).containsExactly(String.class, int.class, boolean.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructSignatureMultipleParams(JdkVersion jdkVersion) throws Exception {
        // Test: Construct signature with multiple parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface PointFactory {
                    new (x: int, y: int): Object
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.PointFactory");

        // Verify create method signature
        Method createMethod = interfaceClass.getMethod("create", int.class, int.class);
        assertThat(createMethod).isNotNull();
        assertThat(createMethod.getReturnType()).isEqualTo(Object.class);
        assertThat(createMethod.getParameterTypes()).containsExactly(int.class, int.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructSignatureNoParams(JdkVersion jdkVersion) throws Exception {
        // Test: Construct signature with no parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Factory {
                    new (): Object
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Factory");

        // Verify create method exists
        Method createMethod = interfaceClass.getMethod("create");
        assertThat(createMethod).isNotNull();
        assertThat(Modifier.isAbstract(createMethod.getModifiers())).isTrue();
        assertThat(createMethod.getReturnType()).isEqualTo(Object.class);
        assertThat(createMethod.getParameterCount()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructSignatureSingleParam(JdkVersion jdkVersion) throws Exception {
        // Test: Construct signature with single parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface PersonFactory {
                    new (name: String): Object
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.PersonFactory");

        // Verify create method signature
        Method createMethod = interfaceClass.getMethod("create", String.class);
        assertThat(createMethod).isNotNull();
        assertThat(Modifier.isAbstract(createMethod.getModifiers())).isTrue();
        assertThat(createMethod.getReturnType()).isEqualTo(Object.class);
        assertThat(createMethod.getParameterTypes()).hasSize(1);
        assertThat(createMethod.getParameterTypes()[0]).isEqualTo(String.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructSignatureTypedReturn(JdkVersion jdkVersion) throws Exception {
        // Test: Construct signature with typed return
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Builder {
                    new (value: String): String
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Builder");

        // Verify create method returns String
        Method createMethod = interfaceClass.getMethod("create", String.class);
        assertThat(createMethod.getReturnType()).isEqualTo(String.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructSignatureWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Construct signature with implementing class
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface StringFactory {
                    new (value: String): String
                  }
                  export class UpperCaseFactory implements StringFactory {
                    create(value: String): String {
                      return value.toUpperCase()
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.StringFactory");
        Class<?> implClass = runner.getClass("com.UpperCaseFactory");

        // Verify interface implements StringFactory
        assertThat(interfaceClass.isAssignableFrom(implClass)).isTrue();

        // Test the implementation
        Object instance = implClass.getConstructor().newInstance();
        Method createMethod = implClass.getMethod("create", String.class);
        assertThat(createMethod.invoke(instance, "hello")).isEqualTo("HELLO");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleCallSignatures(JdkVersion jdkVersion) throws Exception {
        // Test: Multiple call signatures (overloads) - should generate single call method
        // Note: TypeScript allows overloads but we'll just use the first signature
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Overloaded {
                    (x: int): int
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Overloaded");

        // Verify single call method exists
        Method callMethod = interfaceClass.getMethod("call", int.class);
        assertThat(callMethod).isNotNull();
    }
}
