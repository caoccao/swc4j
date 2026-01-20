package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 4: Const Enums (7 tests)
 * Tests const enums - should behave same as regular enums at runtime
 */
public class TestCompileAstTsEnumDeclConst extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumFromValue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Code {
                    Success = 200,
                    NotFound = 404,
                    Error = 500
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Code"));

        var fromValueMethod = enumClass.getMethod("fromValue", int.class);
        Object notFound = fromValueMethod.invoke(null, 404);

        assertEquals("NOTFOUND", ((Enum<?>) notFound).name());
        assertEquals(404, enumClass.getMethod("getValue").invoke(notFound));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumOrdinal(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Level {
                    Debug = 10,
                    Info = 20,
                    Warn = 30
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Level"));

        Object[] constants = enumClass.getEnumConstants();

        assertEquals(0, ((Enum<?>) constants[0]).ordinal());
        assertEquals(1, ((Enum<?>) constants[1]).ordinal());
        assertEquals(2, ((Enum<?>) constants[2]).ordinal());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumSingleMember(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Single {
                    Value = "value"
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Single"));

        Object[] constants = enumClass.getEnumConstants();
        assertEquals(1, constants.length);
        assertEquals("VALUE", ((Enum<?>) constants[0]).name());
        assertEquals("value", enumClass.getMethod("getValue").invoke(constants[0]));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumValueOf(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum State {
                    Active = 1,
                    Inactive = 0
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.State"));

        var valueOfMethod = enumClass.getMethod("valueOf", String.class);
        Object active = valueOfMethod.invoke(null, "ACTIVE");

        assertEquals("ACTIVE", ((Enum<?>) active).name());
        assertEquals(1, enumClass.getMethod("getValue").invoke(active));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstEnumValues(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Priority {
                    Low,
                    Medium,
                    High
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Priority"));

        var valuesMethod = enumClass.getMethod("values");
        Object[] values = (Object[]) valuesMethod.invoke(null);

        assertEquals(3, values.length);
        assertEquals("LOW", ((Enum<?>) values[0]).name());
        assertEquals("MEDIUM", ((Enum<?>) values[1]).name());
        assertEquals("HIGH", ((Enum<?>) values[2]).name());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstNumericEnum(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Numbers {
                    One = 1,
                    Two,
                    Three
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Numbers"));

        assertTrue(Enum.class.isAssignableFrom(enumClass));
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertEquals(1, getValueMethod.invoke(constants[0]));
        assertEquals(2, getValueMethod.invoke(constants[1]));
        assertEquals(3, getValueMethod.invoke(constants[2]));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstStringEnum(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export const enum Colors {
                    Red = "red",
                    Green = "green",
                    Blue = "blue"
                  }
                }""");
        Class<?> enumClass = loadClass(map.get("com.Colors"));

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertEquals("red", getValueMethod.invoke(constants[0]));
        assertEquals("green", getValueMethod.invoke(constants[1]));
        assertEquals("blue", getValueMethod.invoke(constants[2]));
    }
}
