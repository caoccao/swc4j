package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Phase 3: Mixed Explicit/Implicit Values (10 tests)
 * Tests enums with mix of explicit values and auto-increment
 */
public class TestCompileAstTsEnumDeclMixed extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAutoIncrementFromZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Order {
                    First,
                    Second,
                    Third
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Order");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(0);
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(1);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBinaryValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Binary {
                    Low = 0b0,
                    Mid = 0b10,
                    High
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Binary");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(0);  // 0b0 = 0
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(2);  // 0b10 = 2
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitZeroValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    None = 0,
                    Active,
                    Inactive
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Status");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(0);
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(1);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testHexValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Hex {
                    Red = 0xFF0000,
                    Green = 0x00FF00,
                    Blue
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Hex");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(0xFF0000);
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(0x00FF00);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(0x00FF00 + 1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLargeValueNumbers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum BigNum {
                    Small = 1,
                    Medium = 1000,
                    Large = 1000000,
                    VeryLarge
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.BigNum");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(1);
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(1000);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(1000000);
        assertThat(getValueMethod.invoke(constants[3])).isEqualTo(1000001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedExplicitImplicit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Code {
                    A = 1,
                    B,
                    C = 10,
                    D
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Code");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(1);
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(2);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(10);
        assertThat(getValueMethod.invoke(constants[3])).isEqualTo(11);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeValuesWithIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Temp {
                    Freezing = -10,
                    Cold,
                    Warm = 20,
                    Hot
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Temp");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(-10);
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(-9);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(20);
        assertThat(getValueMethod.invoke(constants[3])).isEqualTo(21);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOctalValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Octal {
                    A = 0o10,
                    B,
                    C = 0o20
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Octal");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(8);  // 0o10 = 8
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(9);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(16); // 0o20 = 16
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSingleExplicitValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Single {
                    Only = 42
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Single");

        var getValueMethod = enumClass.getMethod("getValue");
        Object only = enumClass.getEnumConstants()[0];

        assertThat(getValueMethod.invoke(only)).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSparseValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Sparse {
                    A = 1,
                    B = 100,
                    C = 1000
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Sparse");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.invoke(constants[0])).isEqualTo(1);
        assertThat(getValueMethod.invoke(constants[1])).isEqualTo(100);
        assertThat(getValueMethod.invoke(constants[2])).isEqualTo(1000);
    }
}
