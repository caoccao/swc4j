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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCompileAstClassInheritance extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassHierarchyCheck(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Animal {
                    speak(): String { return "..." }
                  }
                  export class Dog extends Animal {
                    speak(): String { return "Woof" }
                  }
                  export class Cat extends Animal {
                    speak(): String { return "Meow" }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> animalClass = classes.get("com.Animal");
        Class<?> dogClass = classes.get("com.Dog");
        Class<?> catClass = classes.get("com.Cat");

        // Check class hierarchy
        assertTrue(animalClass.isAssignableFrom(dogClass));
        assertTrue(animalClass.isAssignableFrom(catClass));

        assertEquals(
                Map.of("Animal", "...", "Dog", "Woof", "Cat", "Meow"),
                Map.of(
                        "Animal", animalClass.getMethod("speak").invoke(animalClass.getConstructor().newInstance()),
                        "Dog", dogClass.getMethod("speak").invoke(dogClass.getConstructor().newInstance()),
                        "Cat", catClass.getMethod("speak").invoke(catClass.getConstructor().newInstance())
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInheritedField(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 42
                  }
                  export class B extends A {
                    getValue(): int {
                      return this.value
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classB = classes.get("com.B");
        var instance = classB.getConstructor().newInstance();
        assertEquals(42, classB.getMethod("getValue").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInheritedFieldWithOverride(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 10
                    getValue(): int { return this.value }
                  }
                  export class B extends A {
                    getValue(): int { return this.value * 2 }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        Class<?> classB = classes.get("com.B");
        assertEquals(
                Map.of("A", 10, "B", 20),
                Map.of(
                        "A", classA.getMethod("getValue").invoke(classA.getConstructor().newInstance()),
                        "B", classB.getMethod("getValue").invoke(classB.getConstructor().newInstance())
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodOverride(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getValue(): int {
                      return 100
                    }
                  }
                  export class B extends A {
                    getValue(): int {
                      return 200
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        Class<?> classB = classes.get("com.B");
        assertEquals(
                Map.of("A", 100, "B", 200),
                Map.of(
                        "A", classA.getMethod("getValue").invoke(classA.getConstructor().newInstance()),
                        "B", classB.getMethod("getValue").invoke(classB.getConstructor().newInstance())
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiLevelInheritance(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getA(): int { return 1 }
                  }
                  export class B extends A {
                    getB(): int { return 2 }
                  }
                  export class C extends B {
                    getC(): int { return 3 }
                    getSum(): int { return this.getA() + this.getB() + this.getC() }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classC = classes.get("com.C");
        var instance = classC.getConstructor().newInstance();
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
    public void testSimpleInheritance(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getValue(): int {
                      return 100
                    }
                  }
                  export class B extends A {
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classB = classes.get("com.B");
        var instance = classB.getConstructor().newInstance();
        // B inherits getValue() from A
        assertEquals(100, classB.getMethod("getValue").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperCallInChain(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    compute(): int { return 10 }
                  }
                  export class B extends A {
                    compute(): int { return super.compute() + 20 }
                  }
                  export class C extends B {
                    compute(): int { return super.compute() + 30 }
                  }
                }""");
        var classes = loadClasses(map);
        assertEquals(
                List.of(10, 30, 60),
                List.of(
                        classes.get("com.A").getMethod("compute").invoke(classes.get("com.A").getConstructor().newInstance()),
                        classes.get("com.B").getMethod("compute").invoke(classes.get("com.B").getConstructor().newInstance()),
                        classes.get("com.C").getMethod("compute").invoke(classes.get("com.C").getConstructor().newInstance())
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperMethodCall(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getValue(): int {
                      return 100
                    }
                  }
                  export class B extends A {
                    getValue(): int {
                      return super.getValue() + 50
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classB = classes.get("com.B");
        var instance = classB.getConstructor().newInstance();
        assertEquals(150, classB.getMethod("getValue").invoke(instance));
    }
}
