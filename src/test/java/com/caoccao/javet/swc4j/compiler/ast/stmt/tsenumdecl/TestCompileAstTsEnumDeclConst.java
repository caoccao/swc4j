package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Phase 4: Const Enums (7 tests)
 * Tests const enums - should behave same as regular enums at runtime
 */
public class TestCompileAstTsEnumDeclConst extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumFromValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Code {
                    Success = 200,
                    NotFound = 404,
                    Error = 500
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Code");

        var fromValueMethod = enumClass.getMethod("fromValue", int.class);
        Object notFound = fromValueMethod.invoke(null, 404);

        assertThat(((Enum<?>) notFound).name()).isEqualTo("NOTFOUND");
        assertThat(enumClass.getMethod("getValue").<Object>invoke(notFound)).isEqualTo(404);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumOrdinal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Level {
                    Debug = 10,
                    Info = 20,
                    Warn = 30
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Level");

        Object[] constants = enumClass.getEnumConstants();

        assertThat(((Enum<?>) constants[0]).ordinal()).isEqualTo(0);
        assertThat(((Enum<?>) constants[1]).ordinal()).isEqualTo(1);
        assertThat(((Enum<?>) constants[2]).ordinal()).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumSingleMember(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Single {
                    Value = "value"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Single");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(constants.length).isEqualTo(1);
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("VALUE");
        assertThat(enumClass.getMethod("getValue").<Object>invoke(constants[0])).isEqualTo("value");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumValueOf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum State {
                    Active = 1,
                    Inactive = 0
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.State");

        var valueOfMethod = enumClass.getMethod("valueOf", String.class);
        Object active = valueOfMethod.invoke(null, "ACTIVE");

        assertThat(((Enum<?>) active).name()).isEqualTo("ACTIVE");
        assertThat(enumClass.getMethod("getValue").<Object>invoke(active)).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Priority {
                    Low,
                    Medium,
                    High
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Priority");

        var valuesMethod = enumClass.getMethod("values");
        Object[] values = (Object[]) valuesMethod.invoke(null);

        assertThat(values.length).isEqualTo(3);
        assertThat(((Enum<?>) values[0]).name()).isEqualTo("LOW");
        assertThat(((Enum<?>) values[1]).name()).isEqualTo("MEDIUM");
        assertThat(((Enum<?>) values[2]).name()).isEqualTo("HIGH");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstNumericEnum(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Numbers {
                    One = 1,
                    Two,
                    Three
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Numbers");

        assertThat(Enum.class.isAssignableFrom(enumClass)).isTrue();
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(1);
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(2);
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstStringEnum(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Colors {
                    Red = "red",
                    Green = "green",
                    Blue = "blue"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Colors");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo("red");
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo("green");
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo("blue");
    }
}
