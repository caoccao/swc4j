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

package com.caoccao.javet.swc4j.compiler.ast.clazz.function;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstFunctionWithoutClass extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleStandaloneFunctions(JdkVersion jdkVersion) throws Exception {
        // Multiple standalone functions should all be in the same dummy class $
        var map = getCompiler(jdkVersion).compile("""
                export function add(a: int, b: int): int {
                  return a + b
                }
                export function sub(a: int, b: int): int {
                  return a - b
                }
                export function mul(a: int, b: int): int {
                  return a * b
                }""");
        Class<?> dummyClass = loadClass(map.get("$"));
        assertEquals(15, dummyClass.getMethod("add", int.class, int.class).invoke(null, 10, 5));
        assertEquals(5, dummyClass.getMethod("sub", int.class, int.class).invoke(null, 10, 5));
        assertEquals(50, dummyClass.getMethod("mul", int.class, int.class).invoke(null, 10, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionInDummyClass(JdkVersion jdkVersion) throws Exception {
        // Standalone function without a class is compiled into dummy class $
        var map = getCompiler(jdkVersion).compile("""
                export function add(a: int, b: int): int {
                  return a + b
                }""");
        Class<?> dummyClass = loadClass(map.get("$"));
        assertEquals(30, dummyClass.getMethod("add", int.class, int.class).invoke(null, 10, 20));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionInNamespace(JdkVersion jdkVersion) throws Exception {
        // Standalone function in namespace is compiled into namespace.$
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function multiply(a: int, b: int): int {
                    return a * b
                  }
                }""");
        Class<?> dummyClass = loadClass(map.get("com.$"));
        assertEquals(50, dummyClass.getMethod("multiply", int.class, int.class).invoke(null, 5, 10));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWhenDollarClassExists(JdkVersion jdkVersion) throws Exception {
        // When class $ already exists, use $1 for standalone functions
        var map = getCompiler(jdkVersion).compile("""
                export class $ {
                  getValue(): int { return 100 }
                }
                export function helper(): int {
                  return 42
                }""");
        var classes = loadClasses(map);
        // Class $ should exist with its own method
        Class<?> dollarClass = classes.get("$");
        var instance = dollarClass.getConstructor().newInstance();
        assertEquals(100, dollarClass.getMethod("getValue").invoke(instance));
        // Function should be in $1
        Class<?> dollar1Class = classes.get("$1");
        assertEquals(42, dollar1Class.getMethod("helper").invoke(null));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWhenDollarClassExistsInNamespace(JdkVersion jdkVersion) throws Exception {
        // When class $ already exists in namespace, use $1 for standalone functions
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class $ {
                    getValue(): int { return 200 }
                  }
                  export function helper(): int {
                    return 84
                  }
                }""");
        var classes = loadClasses(map);
        // Class com.$ should exist
        Class<?> dollarClass = classes.get("com.$");
        var instance = dollarClass.getConstructor().newInstance();
        assertEquals(200, dollarClass.getMethod("getValue").invoke(instance));
        // Function should be in com.$1
        Class<?> dollar1Class = classes.get("com.$1");
        assertEquals(84, dollar1Class.getMethod("helper").invoke(null));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWhenMultipleDollarClassesExist(JdkVersion jdkVersion) throws Exception {
        // When classes $ and $1 exist, use $2 for standalone functions
        var map = getCompiler(jdkVersion).compile("""
                export class $ {
                  getValue(): int { return 1 }
                }
                export class $1 {
                  getValue(): int { return 2 }
                }
                export function helper(): int {
                  return 3
                }""");
        var classes = loadClasses(map);
        assertEquals(1, classes.get("$").getMethod("getValue").invoke(classes.get("$").getConstructor().newInstance()));
        assertEquals(2, classes.get("$1").getMethod("getValue").invoke(classes.get("$1").getConstructor().newInstance()));
        assertEquals(3, classes.get("$2").getMethod("helper").invoke(null));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneFunctionWithClass(JdkVersion jdkVersion) throws Exception {
        // Standalone function alongside a regular class
        var map = getCompiler(jdkVersion).compile("""
                export class Calculator {
                  add(a: int, b: int): int {
                    return a + b
                  }
                }
                export function helper(x: int): int {
                  return x * 2
                }""");
        var classes = loadClasses(map);
        // Regular class
        Class<?> calcClass = classes.get("Calculator");
        var instance = calcClass.getConstructor().newInstance();
        assertEquals(30, calcClass.getMethod("add", int.class, int.class).invoke(instance, 10, 20));
        // Standalone function in $
        Class<?> dummyClass = classes.get("$");
        assertEquals(20, dummyClass.getMethod("helper", int.class).invoke(null, 10));
    }
}
