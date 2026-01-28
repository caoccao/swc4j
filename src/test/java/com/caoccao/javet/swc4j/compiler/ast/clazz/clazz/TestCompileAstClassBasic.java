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
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return 42
                    }
                  }
                }""");
        assertEquals(42, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassCallingAnotherClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        assertEquals(30, (int) runner.createInstanceRunner("com.User").invoke("compute"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithMethodCallingOwnMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithMultipleMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("first"));
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("second"));
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("third"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassWithNoMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Empty {
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.Empty");
        assertNotNull(instanceRunner.getInstance());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesInNamespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("value"));
        assertEquals(2, (int) runner.createInstanceRunner("com.B").invoke("value"));
        assertEquals(3, (int) runner.createInstanceRunner("com.C").invoke("value"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesWithTypeInfer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        assertEquals(123, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleClassesWithoutTypeInfer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
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
        assertEquals(123, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
