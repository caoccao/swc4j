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

package com.caoccao.javet.swc4j.compiler.ast.expr.arrow;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for function type syntax in arrow expressions.
 * Supports defining arbitrary functional interfaces inline:
 * const add: (a: int, b: int) => int = (a, b) => a + b
 */
public class TestCompileAstArrowFnType extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeBlockBody(JdkVersion jdkVersion) throws Exception {
        // Function type with block body
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const compute: (a: int, b: int) => int = (a, b) => {
                        const sum: int = a + b
                        const product: int = a * b
                        return sum + product
                      }
                      return compute(3, 4)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(19, result);  // (3+4) + (3*4) = 7 + 12 = 19
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeBooleanReturn(JdkVersion jdkVersion) throws Exception {
        // Function type with boolean return
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const isPositive: (x: int) => boolean = (x) => x > 0
                      return isPositive(5)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(true, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeDoubleParams(JdkVersion jdkVersion) throws Exception {
        // Function type with double parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const addDouble: (a: double, b: double) => double = (a, b) => a + b
                      return addDouble(1.5, 2.5)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(4.0, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeLongParams(JdkVersion jdkVersion) throws Exception {
        // Function type with long parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const addLong: (a: long, b: long) => long = (a, b) => a + b
                      return addLong(100, 200)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(300L, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeNoParams(JdkVersion jdkVersion) throws Exception {
        // Function type with no parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const getAnswer: () => int = () => 42
                      return getAnswer()
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(42, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeOneParam(JdkVersion jdkVersion) throws Exception {
        // Function type with one parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const double: (x: int) => int = (x) => x * 2
                      return double(21)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(42, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeThreeParams(JdkVersion jdkVersion) throws Exception {
        // Function type with three parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const sum3: (a: int, b: int, c: int) => int = (a, b, c) => a + b + c
                      return sum3(1, 2, 3)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(6, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeTwoParams(JdkVersion jdkVersion) throws Exception {
        // const add: (a: int, b: int) => int = (a, b) => a + b
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const add: (a: int, b: int) => int = (a, b) => a + b
                      return add(1, 2)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(3, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeVoidReturn(JdkVersion jdkVersion) throws Exception {
        // Function type with void return - just test that it compiles and runs
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      let result: int = 0
                      const doNothing: () => void = () => { }
                      doNothing()
                      return 42
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(42, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeWithCapture(JdkVersion jdkVersion) throws Exception {
        // Function type with variable capture
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const factor: int = 10
                      const multiply: (a: int) => int = (a) => a * factor
                      return multiply(5)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(50, result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFnTypeWithObjectParams(JdkVersion jdkVersion) throws Exception {
        // Function type with object parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const concat: (a: String, b: String) => String = (a, b) => a + b
                      return concat("Hello, ", "World!")
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals("Hello, World!", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleFnTypes(JdkVersion jdkVersion) throws Exception {
        // Multiple function types in same method
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const add: (a: int, b: int) => int = (a, b) => a + b
                      const mul: (a: int, b: int) => int = (a, b) => a * b
                      return add(2, 3) + mul(4, 5)
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var result = classA.getMethod("test").invoke(instance);
        assertEquals(25, result);  // (2+3) + (4*5) = 5 + 20 = 25
    }
}
