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
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Phase 1: Basic Numeric Enums (10 test cases)
 * Tests simple numeric enums with auto-increment and explicit values.
 */
public class TestCompileAstTsEnumDeclBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumAllExplicit(JdkVersion jdkVersion) throws Exception {
        // Test: All values explicit
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Priority {
                    Low = 10,
                    Medium = 20,
                    High = 30
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Priority");

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(10);  // Low
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(20);  // Medium
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(30);  // High
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumBasic(JdkVersion jdkVersion) throws Exception {
        // Test: Simple auto-increment from 0
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up,    // 0
                    Down,  // 1
                    Left,  // 2
                    Right  // 3
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Direction");
        assertThat(Enum.class.isAssignableFrom(enumClass)).isTrue();

        Object[] constants = enumClass.getEnumConstants();
        assertThat(constants.length).isEqualTo(4);

        // Check getValue() method returns correct values
        var getValueMethod = enumClass.getMethod("getValue");
        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(0);
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(1);
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(2);
        assertThat(getValueMethod.<Object>invoke(constants[3])).isEqualTo(3);

        // Check name() method
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("UP");
        assertThat(((Enum<?>) constants[1]).name()).isEqualTo("DOWN");
        assertThat(((Enum<?>) constants[2]).name()).isEqualTo("LEFT");
        assertThat(((Enum<?>) constants[3]).name()).isEqualTo("RIGHT");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumExplicitStart(JdkVersion jdkVersion) throws Exception {
        // Test: Explicit starting value
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    Pending = 1,
                    Active,    // 2
                    Done       // 3
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Status");

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(1);  // Pending
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(2);  // Active
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(3);  // Done
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumFromValue(JdkVersion jdkVersion) throws Exception {
        // Test: Reverse lookup with fromValue()
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up, Down, Left, Right
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Direction");

        var fromValueMethod = enumClass.getMethod("fromValue", int.class);
        Object left = fromValueMethod.invoke(null, 2);

        assertThat(((Enum<?>) left).name()).isEqualTo("LEFT");
        assertThat(enumClass.getMethod("getValue").<Object>invoke(left)).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumNegativeValues(JdkVersion jdkVersion) throws Exception {
        // Test: Negative values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Temperature {
                    Cold = -10,
                    Warm = 0,
                    Hot = 10
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Temperature");

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(-10);  // Cold
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(0);    // Warm
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(10);   // Hot
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumNonContiguous(JdkVersion jdkVersion) throws Exception {
        // Test: Non-sequential values
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Priority {
                    Low = 1,
                    Medium = 5,
                    High = 100
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Priority");

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(1);
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(5);
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumOrdinal(JdkVersion jdkVersion) throws Exception {
        // Test: Ordinal vs value
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    A = 10,
                    B = 20
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Status");

        Object[] constants = enumClass.getEnumConstants();
        var getValueMethod = enumClass.getMethod("getValue");

        // ordinal() is declaration order (0-indexed)
        assertThat(((Enum<?>) constants[0]).ordinal()).isEqualTo(0);
        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(10); //getValue() is the custom value

        assertThat(((Enum<?>) constants[1]).ordinal()).isEqualTo(1);
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumSingleMember(JdkVersion jdkVersion) throws Exception {
        // Test: Enum with one member
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Single {
                    Only
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Single");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(constants.length).isEqualTo(1);

        var getValueMethod = enumClass.getMethod("getValue");
        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(0);
        assertThat(((Enum<?>) constants[0]).ordinal()).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumValueOf(JdkVersion jdkVersion) throws Exception {
        // Test: valueOf(String) method
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up, Down
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Direction");

        var valueOfMethod = enumClass.getMethod("valueOf", String.class);
        Object down = valueOfMethod.invoke(null, "DOWN");

        assertThat(((Enum<?>) down).name()).isEqualTo("DOWN");
        assertThat(enumClass.getMethod("getValue").<Object>invoke(down)).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNumericEnumValues(JdkVersion jdkVersion) throws Exception {
        // Test: values() method
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Direction {
                    Up, Down
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Direction");

        var valuesMethod = enumClass.getMethod("values");
        Object[] values = (Object[]) valuesMethod.invoke(null);

        assertThat(values.length).isEqualTo(2);
        assertThat(((Enum<?>) values[0]).name()).isEqualTo("UP");
        assertThat(((Enum<?>) values[1]).name()).isEqualTo("DOWN");
    }
}
