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

public class TestCompileAstClassBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicClassDefinition(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return 42
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(42, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassCallingAnotherClass(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
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
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> userClass = classes.get("com.User");
        var instance = userClass.getConstructor().newInstance();
        assertEquals(30, userClass.getMethod("compute").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithMethodCallingOwnMethod(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    helper(): int {
                      return 10
                    }
                    test(): int {
                      return this.helper() + 5
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithMultipleMethods(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    first(): int {
                      return 1
                    }
                    second(): int {
                      return 2
                    }
                    third(): int {
                      return 3
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1, classA.getMethod("first").invoke(instance));
        assertEquals(2, classA.getMethod("second").invoke(instance));
        assertEquals(3, classA.getMethod("third").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithNoMethods(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Empty {
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.Empty"));
        var instance = classA.getConstructor().newInstance();
        assertNotNull(instance);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesInNamespace(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value(): int { return 1 }
                  }
                  export class B {
                    value(): int { return 2 }
                  }
                  export class C {
                    value(): int { return 3 }
                  }
                }""");
        var classes = loadClasses(map);
        assertEquals(1, classes.get("com.A").getMethod("value").invoke(classes.get("com.A").getConstructor().newInstance()));
        assertEquals(2, classes.get("com.B").getMethod("value").invoke(classes.get("com.B").getConstructor().newInstance()));
        assertEquals(3, classes.get("com.C").getMethod("value").invoke(classes.get("com.C").getConstructor().newInstance()));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesWithTypeInfer(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return new B().test()
                    }
                  }
                  export class B {
                    test() {
                      return 123
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(123, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesWithoutTypeInfer(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return new B().test()
                    }
                  }
                  export class B {
                    test(): int {
                      return 123
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        assertEquals(123, classA.getMethod("test").invoke(instance));
    }
}
