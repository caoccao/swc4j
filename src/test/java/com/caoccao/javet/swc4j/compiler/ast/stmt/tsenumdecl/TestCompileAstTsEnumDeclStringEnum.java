package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals("single", getValueMethod.invoke(single));
        assertEquals("double", getValueMethod.invoke(doubleQuote));
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

        assertTrue(Enum.class.isAssignableFrom(enumClass));
        Object[] constants = enumClass.getEnumConstants();
        assertEquals(3, constants.length);

        assertEquals("ACTIVE", ((Enum<?>) constants[0]).name());
        assertEquals("INACTIVE", ((Enum<?>) constants[1]).name());
        assertEquals("PENDING", ((Enum<?>) constants[2]).name());
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

        assertEquals("WARN", ((Enum<?>) warn).name());
        assertEquals("WARN", enumClass.getMethod("getValue").invoke(warn));

        // Test exception for invalid value
        assertThrows(Exception.class, () -> {
            try {
                fromValueMethod.invoke(null, "INVALID");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
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

        assertEquals("GET", getValueMethod.invoke(get));
        assertEquals("POST", getValueMethod.invoke(post));
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

        assertEquals(3, values.length);
        assertEquals("SMALL", ((Enum<?>) values[0]).name());
        assertEquals("MEDIUM", ((Enum<?>) values[1]).name());
        assertEquals("LARGE", ((Enum<?>) values[2]).name());
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

        assertEquals("ADMIN", ((Enum<?>) admin).name());
        assertEquals("admin", enumClass.getMethod("getValue").invoke(admin));
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

        assertEquals("RED", ((Enum<?>) red).name());
        assertEquals("GREEN", ((Enum<?>) green).name());
        assertEquals("BLUE", ((Enum<?>) blue).name());

        assertEquals(0, ((Enum<?>) red).ordinal());
        assertEquals(1, ((Enum<?>) green).ordinal());
        assertEquals(2, ((Enum<?>) blue).ordinal());
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

        assertEquals("😊", getValueMethod.invoke(happy));
    }
}
