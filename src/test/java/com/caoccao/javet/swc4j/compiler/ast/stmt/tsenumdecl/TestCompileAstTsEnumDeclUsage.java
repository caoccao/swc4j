package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Phase 5: Enum Usage Patterns (10 tests)
 * Tests common enum usage patterns and comparisons
 */
public class TestCompileAstTsEnumDeclUsage extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumComparison(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Priority {
                    Low,
                    Medium,
                    High
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Priority");

        Enum<?> low = (Enum<?>) enumClass.getEnumConstants()[0];
        Enum<?> high = (Enum<?>) enumClass.getEnumConstants()[2];

        // Compare ordinals instead of using compareTo with wildcards
        assertThat(low.ordinal() < high.ordinal()).isTrue();
        assertThat(high.ordinal() > low.ordinal()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumEquality(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Color {
                    Red,
                    Green,
                    Blue
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Color");

        Object red1 = enumClass.getEnumConstants()[0];
        Object red2 = enumClass.getMethod("valueOf", String.class).invoke(null, "RED");

        assertThat(red2).isSameAs(red1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumGetDeclaringClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Type {
                    First,
                    Second
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Type");

        Enum<?> first = (Enum<?>) enumClass.getEnumConstants()[0];
        assertThat(first.getDeclaringClass()).isEqualTo(enumClass);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumHashCode(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Letter {
                    A,
                    B,
                    C
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Letter");

        Object a1 = enumClass.getEnumConstants()[0];
        Object a2 = enumClass.getMethod("valueOf", String.class).invoke(null, "A");

        assertThat(a2.hashCode()).isEqualTo(a1.hashCode());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumInSwitch(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Day {
                    Monday,
                    Tuesday,
                    Wednesday
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Day");

        Object monday = enumClass.getEnumConstants()[0];
        Object tuesday = enumClass.getEnumConstants()[1];

        // Verify ordinals are different (used in switch statements)
        assertThat(
                ((Enum<?>) tuesday).ordinal()
        ).isNotEqualTo(
                ((Enum<?>) monday).ordinal()
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumReverseMapping(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Code {
                    A = 65,
                    B = 66,
                    C = 67
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Code");

        var fromValueMethod = enumClass.getMethod("fromValue", int.class);
        Object b = fromValueMethod.invoke(null, 66);

        assertThat(((Enum<?>) b).name()).isEqualTo("B");
        assertThat(enumClass.getMethod("getValue").invoke(b)).isEqualTo(66);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumToString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Status {
                    Active,
                    Inactive
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Status");

        Object active = enumClass.getEnumConstants()[0];
        assertThat(active.toString()).isEqualTo("ACTIVE");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumValuesImmutability(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Num {
                    One,
                    Two,
                    Three
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Num");

        var valuesMethod = enumClass.getMethod("values");
        Object[] values1 = (Object[]) valuesMethod.invoke(null);
        Object[] values2 = (Object[]) valuesMethod.invoke(null);

        // values() should return a new array each time
        assertThat(values2).isNotSameAs(values1);
        // but contain same elements
        assertThat(values2[0]).isSameAs(values1[0]);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithManyMembers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Month {
                    January,
                    February,
                    March,
                    April,
                    May,
                    June,
                    July,
                    August,
                    September,
                    October,
                    November,
                    December
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Month");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(constants.length).isEqualTo(12);
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("JANUARY");
        assertThat(((Enum<?>) constants[11]).name()).isEqualTo("DECEMBER");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleEnumsInSameNamespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Color {
                    Red,
                    Green
                  }
                  export enum Size {
                    Small,
                    Large
                  }
                }""");

        Class<?> colorClass = runner.getClass("com.Color");
        Class<?> sizeClass = runner.getClass("com.Size");

        assertThat(sizeClass).isNotEqualTo(colorClass);
        assertThat(colorClass.getEnumConstants().length).isEqualTo(2);
        assertThat(sizeClass.getEnumConstants().length).isEqualTo(2);
    }
}
