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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


public class TestCompileAstClassAbstract extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAbstractClassExtendingAbstract(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export abstract class A {
                    abstract f(): int
                  }
                  export abstract class B extends A {
                    abstract g(): int
                  }
                  export class C extends B {
                    f(): int { return 1 }
                    g(): int { return 2 }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        Class<?> classB = runner.getClass("com.B");
        Class<?> classC = runner.getClass("com.C");

        // Verify A and B are abstract
        assertThat(Modifier.isAbstract(classA.getModifiers())).as("A should be abstract").isTrue();
        assertThat(Modifier.isAbstract(classB.getModifiers())).as("B should be abstract").isTrue();
        assertThat(Modifier.isAbstract(classC.getModifiers())).as("C should not be abstract").isFalse();

        // Create a C instance and test
        var instanceRunner = runner.createInstanceRunner("com.C");
        assertThat(
                List.of(
                        instanceRunner.invoke("f"),
                        (int) instanceRunner.invoke("g")
                )
        ).isEqualTo(
                List.of(1, 2)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAbstractClassWithAbstractMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export abstract class Shape {
                    abstract area(): double
                  }
                  export class Circle extends Shape {
                    radius: double
                    constructor(r: double) {
                      this.radius = r
                    }
                    area(): double { return 3.14159 * this.radius * this.radius }
                  }
                }""");
        Class<?> classShape = runner.getClass("com.Shape");
        Class<?> classCircle = runner.getClass("com.Circle");

        // Verify Shape is abstract
        assertThat(Modifier.isAbstract(classShape.getModifiers())).as("Shape should be abstract").isTrue();

        // Verify Circle is not abstract
        assertThat(Modifier.isAbstract(classCircle.getModifiers())).as("Circle should not be abstract").isFalse();

        // Verify area method is abstract in Shape
        var areaMethod = classShape.getDeclaredMethod("area");
        assertThat(Modifier.isAbstract(areaMethod.getModifiers())).as("area() should be abstract in Shape").isTrue();

        // Create a Circle instance and test
        double area = runner.createInstanceRunner("com.Circle", 5.0).invoke("area");
        assertThat(area).isCloseTo(78.53975, within(0.00001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAbstractClassWithConcreteMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export abstract class Base {
                    abstract compute(): int
                    helper(): int { return 100 }
                  }
                  export class Derived extends Base {
                    compute(): int { return this.helper() + 1 }
                  }
                }""");
        Class<?> classBase = runner.getClass("com.Base");
        Class<?> classDerived = runner.getClass("com.Derived");

        // Verify Base is abstract
        assertThat(Modifier.isAbstract(classBase.getModifiers())).as("Base should be abstract").isTrue();

        // Create a Derived instance and test
        var instanceRunner = runner.createInstanceRunner("com.Derived");
        assertThat(
                List.of(
                        instanceRunner.invoke("compute"),
                        (int) instanceRunner.invoke("helper")
                )
        ).isEqualTo(
                List.of(101, 100)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAbstractClassWithField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export abstract class Animal {
                    name: String
                    constructor(name: String) {
                      this.name = name
                    }
                    abstract speak(): String
                    getName(): String { return this.name }
                  }
                  export class Dog extends Animal {
                    constructor(name: String) {
                      super(name)
                    }
                    speak(): String { return "Woof!" }
                  }
                }""");
        Class<?> classAnimal = runner.getClass("com.Animal");
        Class<?> classDog = runner.getClass("com.Dog");

        // Verify Animal is abstract
        assertThat(Modifier.isAbstract(classAnimal.getModifiers())).as("Animal should be abstract").isTrue();

        // Create a Dog instance and test
        var instanceRunner = runner.createInstanceRunner("com.Dog", "Buddy");
        assertThat(
                List.of(
                        instanceRunner.invoke("getName"),
                        (String) instanceRunner.invoke("speak")
                )
        ).isEqualTo(
                List.of("Buddy", "Woof!")
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAbstractClassWithMultipleAbstractMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export abstract class Calculator {
                    abstract add(a: int, b: int): int
                    abstract multiply(a: int, b: int): int
                  }
                  export class SimpleCalculator extends Calculator {
                    add(a: int, b: int): int { return a + b }
                    multiply(a: int, b: int): int { return a * b }
                  }
                }""");
        Class<?> classCalculator = runner.getClass("com.Calculator");
        Class<?> classSimple = runner.getClass("com.SimpleCalculator");

        // Verify Calculator is abstract
        assertThat(Modifier.isAbstract(classCalculator.getModifiers())).as("Calculator should be abstract").isTrue();

        // Verify abstract methods
        var addMethod = classCalculator.getDeclaredMethod("add", int.class, int.class);
        var multiplyMethod = classCalculator.getDeclaredMethod("multiply", int.class, int.class);
        assertThat(Modifier.isAbstract(addMethod.getModifiers())).as("add() should be abstract").isTrue();
        assertThat(Modifier.isAbstract(multiplyMethod.getModifiers())).as("multiply() should be abstract").isTrue();

        // Create a SimpleCalculator instance and test
        var instanceRunner = runner.createInstanceRunner("com.SimpleCalculator");
        assertThat(
                List.of(
                        instanceRunner.invoke("add", 3, 5),
                        (int) instanceRunner.invoke("multiply", 3, 5)
                )
        ).isEqualTo(
                List.of(8, 15)
        );
    }
}
