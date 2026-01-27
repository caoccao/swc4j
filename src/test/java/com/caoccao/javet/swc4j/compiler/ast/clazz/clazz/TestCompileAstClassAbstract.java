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

import static org.junit.jupiter.api.Assertions.*;

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
        assertTrue(Modifier.isAbstract(classA.getModifiers()), "A should be abstract");
        assertTrue(Modifier.isAbstract(classB.getModifiers()), "B should be abstract");
        assertFalse(Modifier.isAbstract(classC.getModifiers()), "C should not be abstract");

        // Create a C instance and test
        var c = classC.getConstructor().newInstance();
        assertEquals(
                List.of(1, 2),
                List.of(
                        classC.getMethod("f").invoke(c),
                        classC.getMethod("g").invoke(c)
                )
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
        assertTrue(Modifier.isAbstract(classShape.getModifiers()), "Shape should be abstract");

        // Verify Circle is not abstract
        assertFalse(Modifier.isAbstract(classCircle.getModifiers()), "Circle should not be abstract");

        // Verify area method is abstract in Shape
        var areaMethod = classShape.getDeclaredMethod("area");
        assertTrue(Modifier.isAbstract(areaMethod.getModifiers()), "area() should be abstract in Shape");

        // Create a Circle instance and test
        var circle = classCircle.getConstructor(double.class).newInstance(5.0);
        double area = (double) classCircle.getMethod("area").invoke(circle);
        assertEquals(78.53975, area, 0.00001);
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
        assertTrue(Modifier.isAbstract(classBase.getModifiers()), "Base should be abstract");

        // Create a Derived instance and test
        var derived = classDerived.getConstructor().newInstance();
        assertEquals(
                List.of(101, 100),
                List.of(
                        classDerived.getMethod("compute").invoke(derived),
                        classDerived.getMethod("helper").invoke(derived)
                )
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
        assertTrue(Modifier.isAbstract(classAnimal.getModifiers()), "Animal should be abstract");

        // Create a Dog instance and test
        var dog = classDog.getConstructor(String.class).newInstance("Buddy");
        assertEquals(
                List.of("Buddy", "Woof!"),
                List.of(
                        classDog.getMethod("getName").invoke(dog),
                        classDog.getMethod("speak").invoke(dog)
                )
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
        assertTrue(Modifier.isAbstract(classCalculator.getModifiers()), "Calculator should be abstract");

        // Verify abstract methods
        var addMethod = classCalculator.getDeclaredMethod("add", int.class, int.class);
        var multiplyMethod = classCalculator.getDeclaredMethod("multiply", int.class, int.class);
        assertTrue(Modifier.isAbstract(addMethod.getModifiers()), "add() should be abstract");
        assertTrue(Modifier.isAbstract(multiplyMethod.getModifiers()), "multiply() should be abstract");

        // Create a SimpleCalculator instance and test
        var calc = classSimple.getConstructor().newInstance();
        assertEquals(
                List.of(8, 15),
                List.of(
                        classSimple.getMethod("add", int.class, int.class).invoke(calc, 3, 5),
                        classSimple.getMethod("multiply", int.class, int.class).invoke(calc, 3, 5)
                )
        );
    }
}
