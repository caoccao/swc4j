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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCompileAstClassAccessibility extends BaseTestCompileSuite {

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
        assertTrue(Modifier.isPrivate(valueField.getModifiers()), "value field should be private");

        // Test functionality through accessors
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("getValue").invoke(instance));

        classA.getMethod("setValue", int.class).invoke(instance, 100);
        assertEquals(100, classA.getMethod("getValue").invoke(instance));
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
        assertTrue(Modifier.isPrivate(helperMethod.getModifiers()), "helper() should be private");

        // Verify test is public
        var testMethod = classA.getDeclaredMethod("test");
        assertTrue(Modifier.isPublic(testMethod.getModifiers()), "test() should be public");

        // Test functionality
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, testMethod.invoke(instance));
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
        assertTrue(Modifier.isProtected(valueField.getModifiers()), "value field should be protected");

        // Test functionality through same class methods
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("getValue").invoke(instance));

        classA.getMethod("setValue", int.class).invoke(instance, 20);
        assertEquals(20, classA.getMethod("getValue").invoke(instance));
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
        assertTrue(Modifier.isProtected(helperMethod.getModifiers()), "helper() should be protected");

        // Test functionality
        var instance = classB.getConstructor().newInstance();
        assertEquals(1, classB.getMethod("test").invoke(instance));
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
        assertTrue(Modifier.isPublic(valueField.getModifiers()), "value field should be public by default");

        // Verify method is public (default)
        var testMethod = classA.getDeclaredMethod("test");
        assertTrue(Modifier.isPublic(testMethod.getModifiers()), "test() should be public by default");

        // Test functionality
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, testMethod.invoke(instance));
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
        assertTrue(Modifier.isPrivate(secretField.getModifiers()), "secret should be private");
        assertTrue(Modifier.isStatic(secretField.getModifiers()), "secret should be static");

        // Test functionality
        assertEquals(42, classA.getMethod("getSecret").invoke(null));
    }
}
