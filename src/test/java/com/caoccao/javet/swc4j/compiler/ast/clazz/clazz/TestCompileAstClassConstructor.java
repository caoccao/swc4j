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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


public class TestCompileAstClassConstructor extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorCallingSuperWithArgs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var instanceRunner = runner.createInstanceRunner("com.B", 10, 5);
        assertThat(
                List.of(
                        instanceRunner.invoke("getValue"),
                        instanceRunner.invoke("getExtra"),
                        (int) instanceRunner.invoke("getTotal")
                )
        ).isEqualTo(
                List.of(10, 5, 15)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Point {
                    x: int
                    y: int
                    constructor(x: int, y: int) {
                      this.x = x
                      this.y = y
                    }
                    constructor(v: int) {
                      this(v, v)
                    }
                    constructor() {
                      this(0, 0)
                    }
                    getX(): int { return this.x }
                    getY(): int { return this.y }
                  }
                }""");
        Class<?> classPoint = runner.getClass("com.Point");

        // Test two-parameter constructor
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.Point", 10, 20).invoke("getX"),
                        (int) runner.createInstanceRunner("com.Point", 10, 20).invoke("getY")
                )
        ).isEqualTo(
                List.of(10, 20)
        );

        // Test single-parameter constructor which calls this(v, v)
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.Point", 5).invoke("getX"),
                        (int) runner.createInstanceRunner("com.Point", 5).invoke("getY")
                )
        ).isEqualTo(
                List.of(5, 5)
        );

        // Test no-parameter constructor which calls this(0, 0)
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.Point").invoke("getX"),
                        (int) runner.createInstanceRunner("com.Point").invoke("getY")
                )
        ).isEqualTo(
                List.of(0, 0)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorChainingWithInheritance(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
                  }
                  export class Point3D extends Point {
                    z: int
                    constructor(x: int, y: int, z: int) {
                      super(x, y)
                      this.z = z
                    }
                    constructor(v: int) {
                      this(v, v, v)
                    }
                    getZ(): int { return this.z }
                  }
                }""");
        Class<?> classPoint3D = runner.getClass("com.Point3D");

        // Test three-parameter constructor
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.Point3D", 1, 2, 3).invoke("getX"),
                        runner.createInstanceRunner("com.Point3D", 1, 2, 3).invoke("getY"),
                        (int) runner.createInstanceRunner("com.Point3D", 1, 2, 3).invoke("getZ")
                )
        ).isEqualTo(
                List.of(1, 2, 3)
        );

        // Test single-parameter constructor which calls this(v, v, v)
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.Point3D", 5).invoke("getX"),
                        runner.createInstanceRunner("com.Point3D", 5).invoke("getY"),
                        (int) runner.createInstanceRunner("com.Point3D", 5).invoke("getZ")
                )
        ).isEqualTo(
                List.of(5, 5, 5)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorInitializingFields(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        assertThat((int) runner.createInstanceRunner("com.A", 42).invoke("getValue")).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorMultiLevelInheritance(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var instanceRunner = runner.createInstanceRunner("com.C", 1, 2, 3);
        assertThat(
                List.of(
                        instanceRunner.invoke("getA"),
                        instanceRunner.invoke("getB"),
                        instanceRunner.invoke("getC"),
                        (int) instanceRunner.invoke("getSum")
                )
        ).isEqualTo(
                List.of(1, 2, 3, 6)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorOverloading(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Rectangle {
                    width: int
                    height: int
                    constructor(w: int, h: int) {
                      this.width = w
                      this.height = h
                    }
                    constructor(side: int) {
                      this.width = side
                      this.height = side
                    }
                    getArea(): int { return this.width * this.height }
                    getWidth(): int { return this.width }
                    getHeight(): int { return this.height }
                  }
                }""");
        Class<?> classRectangle = runner.getClass("com.Rectangle");

        // Test two-parameter constructor
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.Rectangle", 10, 5).invoke("getWidth"),
                        runner.createInstanceRunner("com.Rectangle", 10, 5).invoke("getHeight"),
                        (int) runner.createInstanceRunner("com.Rectangle", 10, 5).invoke("getArea")
                )
        ).isEqualTo(
                List.of(10, 5, 50)
        );

        // Test single-parameter constructor (square)
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.Rectangle", 7).invoke("getWidth"),
                        runner.createInstanceRunner("com.Rectangle", 7).invoke("getHeight"),
                        (int) runner.createInstanceRunner("com.Rectangle", 7).invoke("getArea")
                )
        ).isEqualTo(
                List.of(7, 7, 49)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var instanceRunner = runner.createInstanceRunner("com.Person", "John", 25, true);
        assertThat(
                Map.of(
                        "name", instanceRunner.invoke("getName"),
                        "age", (int) instanceRunner.invoke("getAge"),
                        "active", (boolean) instanceRunner.invoke("isActive")
                )
        ).isEqualTo(
                Map.of("name", "John", "age", 25, "active", true)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithDoubleParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var instanceRunner = runner.createInstanceRunner("com.Circle", 5.0);
        assertThat((double) instanceRunner.invoke("getRadius")).isEqualTo(5.0);
        assertThat((double) instanceRunner.invoke("getArea")).isCloseTo(78.53975, within(0.00001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithMultipleParameters(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var instanceRunner = runner.createInstanceRunner("com.Point", 10, 20);
        assertThat(
                List.of(
                        instanceRunner.invoke("getX"),
                        instanceRunner.invoke("getY"),
                        (int) instanceRunner.invoke("sum")
                )
        ).isEqualTo(
                List.of(10, 20, 30)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithSingleParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        var instanceRunner = runner.createInstanceRunner("com.Counter", 100);
        assertThat((int) instanceRunner.invoke("getCount")).isEqualTo(100);
        instanceRunner.invoke("increment");
        assertThat((int) instanceRunner.invoke("getCount")).isEqualTo(101);
    }
}
