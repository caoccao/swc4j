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

package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 1: Basic Numeric Enums (10 test cases)
 * Tests simple numeric enums with auto-increment and explicit values.
 */
public class TestCompileAstTsEnumDeclBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumAllExplicit(JdkVersion jdkVersion) throws Exception {
        // Test: All values explicit
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Priority {
                    Low = 10,
                    Medium = 20,
                    High = 30
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Priority"));

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertEquals(10, getValueMethod.invoke(constants[0]));  // Low
        assertEquals(20, getValueMethod.invoke(constants[1]));  // Medium
        assertEquals(30, getValueMethod.invoke(constants[2]));  // High
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumBasic(JdkVersion jdkVersion) throws Exception {
        // Test: Simple auto-increment from 0
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up,    // 0
                    Down,  // 1
                    Left,  // 2
                    Right  // 3
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Direction"));
        assertTrue(Enum.class.isAssignableFrom(enumClass));

        Object[] constants = enumClass.getEnumConstants();
        assertEquals(4, constants.length);

        // Check getValue() method returns correct values
        var getValueMethod = enumClass.getMethod("getValue");
        assertEquals(0, getValueMethod.invoke(constants[0]));
        assertEquals(1, getValueMethod.invoke(constants[1]));
        assertEquals(2, getValueMethod.invoke(constants[2]));
        assertEquals(3, getValueMethod.invoke(constants[3]));

        // Check name() method
        assertEquals("UP", ((Enum<?>) constants[0]).name());
        assertEquals("DOWN", ((Enum<?>) constants[1]).name());
        assertEquals("LEFT", ((Enum<?>) constants[2]).name());
        assertEquals("RIGHT", ((Enum<?>) constants[3]).name());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumExplicitStart(JdkVersion jdkVersion) throws Exception {
        // Test: Explicit starting value
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    Pending = 1,
                    Active,    // 2
                    Done       // 3
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Status"));

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertEquals(1, getValueMethod.invoke(constants[0]));  // Pending
        assertEquals(2, getValueMethod.invoke(constants[1]));  // Active
        assertEquals(3, getValueMethod.invoke(constants[2]));  // Done
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumFromValue(JdkVersion jdkVersion) throws Exception {
        // Test: Reverse lookup with fromValue()
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up, Down, Left, Right
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Direction"));

        var fromValueMethod = enumClass.getMethod("fromValue", int.class);
        Object left = fromValueMethod.invoke(null, 2);

        assertEquals("LEFT", ((Enum<?>) left).name());
        assertEquals(2, enumClass.getMethod("getValue").invoke(left));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumNegativeValues(JdkVersion jdkVersion) throws Exception {
        // Test: Negative values
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Temperature {
                    Cold = -10,
                    Warm = 0,
                    Hot = 10
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Temperature"));

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertEquals(-10, getValueMethod.invoke(constants[0]));  // Cold
        assertEquals(0, getValueMethod.invoke(constants[1]));    // Warm
        assertEquals(10, getValueMethod.invoke(constants[2]));   // Hot
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumNonContiguous(JdkVersion jdkVersion) throws Exception {
        // Test: Non-sequential values
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Priority {
                    Low = 1,
                    Medium = 5,
                    High = 100
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Priority"));

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertEquals(1, getValueMethod.invoke(constants[0]));
        assertEquals(5, getValueMethod.invoke(constants[1]));
        assertEquals(100, getValueMethod.invoke(constants[2]));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumOrdinal(JdkVersion jdkVersion) throws Exception {
        // Test: Ordinal vs value
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    A = 10,
                    B = 20
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Status"));

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        // ordinal() is declaration order (0-indexed)
        assertEquals(0, ((Enum<?>) constants[0]).ordinal());
        assertEquals(10, getValueMethod.invoke(constants[0]));  // getValue() is the custom value

        assertEquals(1, ((Enum<?>) constants[1]).ordinal());
        assertEquals(20, getValueMethod.invoke(constants[1]));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumSingleMember(JdkVersion jdkVersion) throws Exception {
        // Test: Enum with one member
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Single {
                    Only
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Single"));

        Object[] constants = enumClass.getEnumConstants();
        assertEquals(1, constants.length);

        var getValueMethod = enumClass.getMethod("getValue");
        assertEquals(0, getValueMethod.invoke(constants[0]));
        assertEquals(0, ((Enum<?>) constants[0]).ordinal());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumValueOf(JdkVersion jdkVersion) throws Exception {
        // Test: valueOf(String) method
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up, Down
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Direction"));

        var valueOfMethod = enumClass.getMethod("valueOf", String.class);
        Object down = valueOfMethod.invoke(null, "DOWN");

        assertEquals("DOWN", ((Enum<?>) down).name());
        assertEquals(1, enumClass.getMethod("getValue").invoke(down));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumValues(JdkVersion jdkVersion) throws Exception {
        // Test: values() method
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up, Down
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Direction"));

        var valuesMethod = enumClass.getMethod("values");
        Object[] values = (Object[]) valuesMethod.invoke(null);

        assertEquals(2, values.length);
        assertEquals("UP", ((Enum<?>) values[0]).name());
        assertEquals("DOWN", ((Enum<?>) values[1]).name());
    }
}
