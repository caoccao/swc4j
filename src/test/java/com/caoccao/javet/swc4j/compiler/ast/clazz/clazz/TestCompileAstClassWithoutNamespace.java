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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCompileAstClassWithoutNamespace extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespace(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                export class A {
                  test(): int {
                    return 42
                  }
                }""");
        Class<?> classA = loadClass(map.get("A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceAndField(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                export class Counter {
                  count: int = 0
                  increment(): void {
                    this.count = this.count + 1
                  }
                  getCount(): int {
                    return this.count
                  }
                }""");
        Class<?> counterClass = loadClass(map.get("Counter"));
        var instance = counterClass.getConstructor().newInstance();
        counterClass.getMethod("increment").invoke(instance);
        counterClass.getMethod("increment").invoke(instance);
        assertEquals(2, counterClass.getMethod("getCount").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceCallingAnotherClass(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                export class Calculator {
                  add(a: int, b: int): int {
                    return a + b
                  }
                }
                export class User {
                  compute(): int {
                    const calc = new Calculator()
                    return calc.add(10, 20)
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> userClass = classes.get("User");
        var instance = userClass.getConstructor().newInstance();
        assertEquals(30, userClass.getMethod("compute").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceEmpty(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                export class Empty {
                }""");
        Class<?> emptyClass = loadClass(map.get("Empty"));
        var instance = emptyClass.getConstructor().newInstance();
        assertNotNull(instance);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithoutNamespaceMultipleMethods(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                export class Math {
                  add(a: int, b: int): int { return a + b }
                  sub(a: int, b: int): int { return a - b }
                  mul(a: int, b: int): int { return a * b }
                }""");
        Class<?> mathClass = loadClass(map.get("Math"));
        var instance = mathClass.getConstructor().newInstance();
        assertEquals(5, mathClass.getMethod("add", int.class, int.class).invoke(instance, 2, 3));
        assertEquals(7, mathClass.getMethod("sub", int.class, int.class).invoke(instance, 10, 3));
        assertEquals(12, mathClass.getMethod("mul", int.class, int.class).invoke(instance, 3, 4));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesWithoutNamespace(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                export class A {
                  value(): int { return 1 }
                }
                export class B {
                  value(): int { return 2 }
                }
                export class C {
                  value(): int { return 3 }
                }""");
        var classes = loadClasses(map);
        assertEquals(1, classes.get("A").getMethod("value").invoke(classes.get("A").getConstructor().newInstance()));
        assertEquals(2, classes.get("B").getMethod("value").invoke(classes.get("B").getConstructor().newInstance()));
        assertEquals(3, classes.get("C").getMethod("value").invoke(classes.get("C").getConstructor().newInstance()));
    }

}
