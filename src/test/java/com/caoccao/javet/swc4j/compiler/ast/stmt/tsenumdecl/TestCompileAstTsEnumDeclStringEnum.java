package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Phase 2: String Enums (8 tests)
 * Tests string-valued enums with various initialization patterns
 */
public class TestCompileAstTsEnumDeclStringEnum extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedQuoteStyles(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Quote {
                    Single = 'single',
                    Double = "double"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Quote");

        var getValueMethod = enumClass.getMethod("getValue");
        Object single = enumClass.getEnumConstants()[0];
        Object doubleQuote = enumClass.getEnumConstants()[1];

        assertThat(getValueMethod.<Object>invoke(single)).isEqualTo("single");
        assertThat(getValueMethod.<Object>invoke(doubleQuote)).isEqualTo("double");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEnumBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    Active = "ACTIVE",
                    Inactive = "INACTIVE",
                    Pending = "PENDING"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Status");

        assertThat(Enum.class.isAssignableFrom(enumClass)).isTrue();
        Object[] constants = enumClass.getEnumConstants();
        assertThat(constants.length).isEqualTo(3);

        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("ACTIVE");
        assertThat(((Enum<?>) constants[1]).name()).isEqualTo("INACTIVE");
        assertThat(((Enum<?>) constants[2]).name()).isEqualTo("PENDING");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEnumFromValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum LogLevel {
                    Debug = "DEBUG",
                    Info = "INFO",
                    Warn = "WARN",
                    Error = "ERROR"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.LogLevel");

        var fromValueMethod = enumClass.getMethod("fromValue", String.class);
        Object warn = fromValueMethod.invoke(null, "WARN");

        assertThat(((Enum<?>) warn).name()).isEqualTo("WARN");
        assertThat(enumClass.getMethod("getValue").<Object>invoke(warn)).isEqualTo("WARN");

        // Test exception for invalid value
        assertThatThrownBy(() -> {
            try {
                fromValueMethod.invoke(null, "INVALID");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(Exception.class);;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEnumGetValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum HttpMethod {
                    Get = "GET",
                    Post = "POST",
                    Put = "PUT",
                    Delete = "DELETE"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.HttpMethod");

        var getValueMethod = enumClass.getMethod("getValue");
        Object get = enumClass.getEnumConstants()[0];
        Object post = enumClass.getEnumConstants()[1];

        assertThat(getValueMethod.<Object>invoke(get)).isEqualTo("GET");
        assertThat(getValueMethod.<Object>invoke(post)).isEqualTo("POST");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEnumIteration(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Size {
                    Small = "S",
                    Medium = "M",
                    Large = "L"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Size");

        var valuesMethod = enumClass.getMethod("values");
        Object[] values = (Object[]) valuesMethod.invoke(null);

        assertThat(values.length).isEqualTo(3);
        assertThat(((Enum<?>) values[0]).name()).isEqualTo("SMALL");
        assertThat(((Enum<?>) values[1]).name()).isEqualTo("MEDIUM");
        assertThat(((Enum<?>) values[2]).name()).isEqualTo("LARGE");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEnumValueOf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Role {
                    Admin = "admin",
                    User = "user",
                    Guest = "guest"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Role");

        var valueOfMethod = enumClass.getMethod("valueOf", String.class);
        Object admin = valueOfMethod.invoke(null, "ADMIN");

        assertThat(((Enum<?>) admin).name()).isEqualTo("ADMIN");
        assertThat(enumClass.getMethod("getValue").<Object>invoke(admin)).isEqualTo("admin");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEnumWithExplicitValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Color {
                    Red = "red",
                    Green = "green",
                    Blue = "blue"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Color");

        Object red = enumClass.getEnumConstants()[0];
        Object green = enumClass.getEnumConstants()[1];
        Object blue = enumClass.getEnumConstants()[2];

        assertThat(((Enum<?>) red).name()).isEqualTo("RED");
        assertThat(((Enum<?>) green).name()).isEqualTo("GREEN");
        assertThat(((Enum<?>) blue).name()).isEqualTo("BLUE");

        assertThat(((Enum<?>) red).ordinal()).isEqualTo(0);
        assertThat(((Enum<?>) green).ordinal()).isEqualTo(1);
        assertThat(((Enum<?>) blue).ordinal()).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringEnumWithUnicode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Emoji {
                    Happy = "😊",
                    Sad = "😢",
                    ThumbsUp = "👍"
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Emoji");

        var getValueMethod = enumClass.getMethod("getValue");
        Object happy = enumClass.getEnumConstants()[0];

        assertThat(getValueMethod.<Object>invoke(happy)).isEqualTo("😊");
    }
}
