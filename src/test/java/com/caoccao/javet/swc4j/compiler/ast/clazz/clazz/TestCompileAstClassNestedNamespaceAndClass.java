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

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for nested namespaces and classes across namespaces.
 */
public class TestCompileAstClassNestedNamespaceAndClass extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassesInDifferentNestedNamespaces(JdkVersion jdkVersion) throws Exception {
        // Classes at different nesting levels
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class RootClass {
                    getValue(): int { return 1 }
                  }
                  namespace util {
                    export class UtilClass {
                      getValue(): int { return 2 }
                    }
                  }
                  namespace model {
                    export class ModelClass {
                      getValue(): int { return 3 }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.RootClass").invoke("getValue")).isEqualTo(1);
        assertThat((int) runner.createInstanceRunner("com.util.UtilClass").invoke("getValue")).isEqualTo(2);
        assertThat((int) runner.createInstanceRunner("com.model.ModelClass").invoke("getValue")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCrossNamespaceClassReference(JdkVersion jdkVersion) throws Exception {
        // A class in one namespace creates an instance of a class in a sibling namespace
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  namespace math {
                    export class Adder {
                      add(a: int, b: int): int { return a + b }
                    }
                  }
                  namespace app {
                    export class Calculator {
                      compute(): int {
                        const adder = new com.math.Adder()
                        return adder.add(10, 20)
                      }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.app.Calculator").invoke("compute")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeeplyNestedClasses(JdkVersion jdkVersion) throws Exception {
        // Three levels of nested classes via namespaces
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  namespace a {
                    namespace b {
                      export class Deep {
                        depth(): int { return 3 }
                      }
                    }
                    export class Mid {
                      depth(): int { return 2 }
                    }
                  }
                  export class Top {
                    depth(): int { return 1 }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.Top").invoke("depth")).isEqualTo(1);
        assertThat((int) runner.createInstanceRunner("com.a.Mid").invoke("depth")).isEqualTo(2);
        assertThat((int) runner.createInstanceRunner("com.a.b.Deep").invoke("depth")).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleNestedClasses(JdkVersion jdkVersion) throws Exception {
        // Multiple classes inside a nested namespace
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  namespace shapes {
                    export class Circle {
                      name(): String { return "circle" }
                    }
                    export class Square {
                      name(): String { return "square" }
                    }
                  }
                }""");
        assertThat((String) runner.createInstanceRunner("com.shapes.Circle").invoke("name")).isEqualTo("circle");
        assertThat((String) runner.createInstanceRunner("com.shapes.Square").invoke("name")).isEqualTo("square");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedClassExtendsOuterClass(JdkVersion jdkVersion) throws Exception {
        // A nested class extends a class from the parent namespace
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Base {
                    getBase(): int { return 10 }
                  }
                  namespace Base {
                    export class Extended extends com.Base {
                      getExtended(): int { return this.getBase() + 5 }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.Base.Extended");
        assertThat((int) instanceRunner.invoke("getBase")).isEqualTo(10);
        assertThat((int) instanceRunner.invoke("getExtended")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedClassInstantiatedFromOuter(JdkVersion jdkVersion) throws Exception {
        // Outer class creates an instance of its nested class
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  namespace Types {
                    export class Point {
                      x: int
                      y: int
                      constructor(x: int, y: int) { this.x = x; this.y = y }
                      getX(): int { return this.x }
                      getY(): int { return this.y }
                    }
                  }
                  export class Factory {
                    create(): int {
                      const p = new com.Types.Point(3, 4)
                      return p.getX() + p.getY()
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.Factory").invoke("create")).isEqualTo(7);
    }

    /*
     * TypeScript doesn't support class declarations inside a class body,
     * but a namespace with the same name as a class creates a "companion namespace"
     * whose classes behave like Java static nested classes (Outer.Inner).
     */
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedClassViaCompanionNamespace(JdkVersion jdkVersion) throws Exception {
        // A class and a companion namespace produce Outer and Outer.Inner
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Outer {
                    getValue(): int { return 1 }
                  }
                  namespace Outer {
                    export class Inner {
                      getValue(): int { return 2 }
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.Outer").invoke("getValue")).isEqualTo(1);
        assertThat((int) runner.createInstanceRunner("com.Outer.Inner").invoke("getValue")).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedNamespace(JdkVersion jdkVersion) throws Exception {
        // Nested namespaces produce dotted package names: com.example
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  namespace example {
                    export class Hello {
                      greet(): String { return "hello" }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.example.Hello");
        assertThat((String) instanceRunner.invoke("greet")).isEqualTo("hello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedNamespaceInheritance(JdkVersion jdkVersion) throws Exception {
        // A class in a nested namespace extends a class in a parent namespace
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Base {
                    getBase(): int { return 100 }
                  }
                  namespace sub {
                    export class Child extends com.Base {
                      getChild(): int { return this.getBase() + 1 }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.sub.Child");
        assertThat((int) instanceRunner.invoke("getBase")).isEqualTo(100);
        assertThat((int) instanceRunner.invoke("getChild")).isEqualTo(101);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedNamespaceWithInterface(JdkVersion jdkVersion) throws Exception {
        // Interface in one nested namespace, implementation in another
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  namespace api {
                    export interface Service {
                      execute(): String
                    }
                  }
                  namespace impl {
                    export class ServiceImpl implements com.api.Service {
                      execute(): String { return "done" }
                    }
                  }
                }""");
        Class<?> serviceInterface = runner.getClass("com.api.Service");
        Class<?> implClass = runner.getClass("com.impl.ServiceImpl");
        assertThat(serviceInterface.isInterface()).isTrue();
        assertThat(serviceInterface.isAssignableFrom(implClass)).isTrue();
        assertThat((String) runner.createInstanceRunner("com.impl.ServiceImpl").invoke("execute")).isEqualTo("done");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTripleNestedNamespace(JdkVersion jdkVersion) throws Exception {
        // Three levels of nesting: com.example.app
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  namespace example {
                    namespace app {
                      export class Config {
                        getVersion(): int { return 1 }
                      }
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.example.app.Config");
        assertThat((int) instanceRunner.invoke("getVersion")).isEqualTo(1);
    }
}
