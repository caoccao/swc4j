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

package com.caoccao.javet.swc4j.compiler.ast.clazz.clazz;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Modifier;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstClassAccessibility extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateAndProtectedConstructors(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class PrivateCtor {
                    value: int = 1
                    private constructor() {
                    }
                    static create(): PrivateCtor {
                      return new PrivateCtor()
                    }
                    getValue(): int {
                      return this.value
                    }
                  }
                  export class ProtectedCtor {
                    protected constructor() {
                    }
                    static create(): ProtectedCtor {
                      return new ProtectedCtor()
                    }
                  }
                }""");

        Class<?> privateCtorClass = runner.getClass("com.PrivateCtor");
        Class<?> protectedCtorClass = runner.getClass("com.ProtectedCtor");
        assertThat(
                Map.of(
                        "privateCtor", Modifier.isPrivate(privateCtorClass.getDeclaredConstructor().getModifiers()),
                        "protectedCtor", Modifier.isProtected(protectedCtorClass.getDeclaredConstructor().getModifiers()))
        ).isEqualTo(
                Map.of("privateCtor", true, "protectedCtor", true)
        );

        Object privateInstance = privateCtorClass.getDeclaredMethod("create").invoke(null);
        assertThat(privateCtorClass.getDeclaredMethod("getValue").invoke(privateInstance)).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private value: int = 42
                    getValue(): int { return this.value }
                    setValue(v: int): void { this.value = v }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify field is private
        var valueField = classA.getDeclaredField("value");
        assertThat(Modifier.isPrivate(valueField.getModifiers())).as("value field should be private").isTrue();

        // Test functionality through accessors
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(42);

        instanceRunner.invoke("setValue", 100);
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPrivateMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private helper(): int { return 1 }
                    public test(): int { return this.helper() }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify helper is private
        var helperMethod = classA.getDeclaredMethod("helper");
        assertThat(Modifier.isPrivate(helperMethod.getModifiers())).as("helper() should be private").isTrue();

        // Verify test is public
        var testMethod = classA.getDeclaredMethod("test");
        assertThat(Modifier.isPublic(testMethod.getModifiers())).as("test() should be public").isTrue();

        // Test functionality
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testProtectedField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    protected value: int = 10
                    getValue(): int { return this.value }
                    setValue(v: int): void { this.value = v }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify field is protected
        var valueField = classA.getDeclaredField("value");
        assertThat(Modifier.isProtected(valueField.getModifiers())).as("value field should be protected").isTrue();

        // Test functionality through same class methods
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(10);

        instanceRunner.invoke("setValue", 20);
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testProtectedMethodInheritance(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    protected helper(): int { return 1 }
                  }
                  export class B extends A {
                    test(): int { return this.helper() }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        Class<?> classB = runner.getClass("com.B");

        // Verify helper is protected
        var helperMethod = classA.getDeclaredMethod("helper");
        assertThat(Modifier.isProtected(helperMethod.getModifiers())).as("helper() should be protected").isTrue();

        // Test functionality
        assertThat((int) runner.createInstanceRunner("com.B").invoke("test")).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testPublicIsDefault(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 10
                    test(): int { return this.value }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify field is public (default)
        var valueField = classA.getDeclaredField("value");
        assertThat(Modifier.isPublic(valueField.getModifiers())).as("value field should be public by default").isTrue();

        // Verify method is public (default)
        var testMethod = classA.getDeclaredMethod("test");
        assertThat(Modifier.isPublic(testMethod.getModifiers())).as("test() should be public by default").isTrue();

        // Test functionality
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticPrivateField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private static secret: int = 42
                    static getSecret(): int { return A.secret }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");

        // Verify field is private and static
        var secretField = classA.getDeclaredField("secret");
        assertThat(Modifier.isPrivate(secretField.getModifiers())).as("secret should be private").isTrue();
        assertThat(Modifier.isStatic(secretField.getModifiers())).as("secret should be static").isTrue();

        // Test functionality
        assertThat((int) runner.createStaticRunner("com.A").invoke("getSecret")).isEqualTo(42);
    }
}
