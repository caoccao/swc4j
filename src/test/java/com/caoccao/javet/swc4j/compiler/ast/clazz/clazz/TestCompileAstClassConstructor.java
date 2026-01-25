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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstClassConstructor extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorCallingSuperWithArgs(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int
                    constructor(v: int) {
                      this.value = v
                    }
                    getValue(): int {
                      return this.value
                    }
                  }
                  export class B extends A {
                    extra: int
                    constructor(v: int, e: int) {
                      super(v)
                      this.extra = e
                    }
                    getExtra(): int {
                      return this.extra
                    }
                    getTotal(): int {
                      return this.value + this.extra
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classB = classes.get("com.B");
        var instance = classB.getConstructor(int.class, int.class).newInstance(10, 5);
        assertEquals(
                List.of(10, 5, 15),
                List.of(
                        classB.getMethod("getValue").invoke(instance),
                        classB.getMethod("getExtra").invoke(instance),
                        classB.getMethod("getTotal").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorInitializingFields(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int
                    constructor(v: int) {
                      this.value = v
                    }
                    getValue(): int {
                      return this.value
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor(int.class).newInstance(42);
        assertEquals(42, classA.getMethod("getValue").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorMultiLevelInheritance(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    a: int
                    constructor(a: int) {
                      this.a = a
                    }
                    getA(): int { return this.a }
                  }
                  export class B extends A {
                    b: int
                    constructor(a: int, b: int) {
                      super(a)
                      this.b = b
                    }
                    getB(): int { return this.b }
                  }
                  export class C extends B {
                    c: int
                    constructor(a: int, b: int, c: int) {
                      super(a, b)
                      this.c = c
                    }
                    getC(): int { return this.c }
                    getSum(): int { return this.a + this.b + this.c }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classC = classes.get("com.C");
        var instance = classC.getConstructor(int.class, int.class, int.class).newInstance(1, 2, 3);
        assertEquals(
                List.of(1, 2, 3, 6),
                List.of(
                        classC.getMethod("getA").invoke(instance),
                        classC.getMethod("getB").invoke(instance),
                        classC.getMethod("getC").invoke(instance),
                        classC.getMethod("getSum").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Person {
                    name: String
                    age: int
                    active: boolean
                    constructor(name: String, age: int, active: boolean) {
                      this.name = name
                      this.age = age
                      this.active = active
                    }
                    getName(): String { return this.name }
                    getAge(): int { return this.age }
                    isActive(): boolean { return this.active }
                  }
                }""");
        Class<?> classPerson = loadClass(map.get("com.Person"));
        var instance = classPerson.getConstructor(String.class, int.class, boolean.class).newInstance("John", 25, true);
        assertEquals(
                Map.of("name", "John", "age", 25, "active", true),
                Map.of(
                        "name", classPerson.getMethod("getName").invoke(instance),
                        "age", classPerson.getMethod("getAge").invoke(instance),
                        "active", classPerson.getMethod("isActive").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithDoubleParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Circle {
                    radius: double
                    constructor(r: double) {
                      this.radius = r
                    }
                    getRadius(): double { return this.radius }
                    getArea(): double { return 3.14159 * this.radius * this.radius }
                  }
                }""");
        Class<?> classCircle = loadClass(map.get("com.Circle"));
        var instance = classCircle.getConstructor(double.class).newInstance(5.0);
        assertEquals(5.0, classCircle.getMethod("getRadius").invoke(instance));
        assertEquals(78.53975, (double) classCircle.getMethod("getArea").invoke(instance), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithMultipleParameters(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Point {
                    x: int
                    y: int
                    constructor(x: int, y: int) {
                      this.x = x
                      this.y = y
                    }
                    getX(): int { return this.x }
                    getY(): int { return this.y }
                    sum(): int { return this.x + this.y }
                  }
                }""");
        Class<?> classPoint = loadClass(map.get("com.Point"));
        var instance = classPoint.getConstructor(int.class, int.class).newInstance(10, 20);
        assertEquals(
                List.of(10, 20, 30),
                List.of(
                        classPoint.getMethod("getX").invoke(instance),
                        classPoint.getMethod("getY").invoke(instance),
                        classPoint.getMethod("sum").invoke(instance)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithSingleParameter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Counter {
                    count: int
                    constructor(initial: int) {
                      this.count = initial
                    }
                    getCount(): int { return this.count }
                    increment(): void { this.count = this.count + 1 }
                  }
                }""");
        Class<?> classCounter = loadClass(map.get("com.Counter"));
        var instance = classCounter.getConstructor(int.class).newInstance(100);
        assertEquals(100, classCounter.getMethod("getCount").invoke(instance));
        classCounter.getMethod("increment").invoke(instance);
        assertEquals(101, classCounter.getMethod("getCount").invoke(instance));
    }
}
