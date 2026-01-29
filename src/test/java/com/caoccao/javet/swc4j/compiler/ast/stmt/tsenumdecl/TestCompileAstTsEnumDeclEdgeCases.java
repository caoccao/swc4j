package com.caoccao.javet.swc4j.compiler.ast.stmt.tsenumdecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Phase 6: Edge Cases (21 tests)
 * Tests edge cases, error handling, and boundary conditions
 */
public class TestCompileAstTsEnumDeclEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAmbientEnum(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  declare enum External {
                    Value
                  }
                }""");

        // Ambient enum should not generate bytecode
        assertThatThrownBy(() -> runner.getClass("com.External")).isInstanceOf(ClassNotFoundException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedEnumRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Computed {
                        A = 1,
                        B = A * 2
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class);;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyEnumRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Empty {
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class);;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumFromValueNotFound(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Num {
                    One = 1,
                    Two = 2
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Num");

        var fromValueMethod = enumClass.getMethod("fromValue", int.class);

        assertThatThrownBy(() -> {
            try {
                fromValueMethod.invoke(null, 999);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(Exception.class);;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumFromValueWithDuplicates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Dup2 {
                    First = 1,
                    Second = 1
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Dup2");

        var fromValueMethod = enumClass.getMethod("fromValue", int.class);
        Object result = fromValueMethod.invoke(null, 1);

        // Should return the first enum member with that value
        assertThat(((Enum<?>) result).name()).isEqualTo("FIRST");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumValueOfNotFound(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Letter {
                    A,
                    B
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Letter");

        var valueOfMethod = enumClass.getMethod("valueOf", String.class);

        assertThatThrownBy(() -> {
            try {
                valueOfMethod.invoke(null, "Z");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(Exception.class);;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithCamelCaseName(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Case {
                    camelCase,
                    PascalCase,
                    UPPER_CASE
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Case");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("CAMELCASE");
        assertThat(((Enum<?>) constants[1]).name()).isEqualTo("PASCALCASE");
        assertThat(((Enum<?>) constants[2]).name()).isEqualTo("UPPER_CASE");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithDuplicateValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Dup {
                    A = 1,
                    B = 1,
                    C = 2
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Dup");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        // Different enum members can have same value
        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(1);
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(1);
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithEmptyString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Empty {
                    Value = ""
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Empty");

        var getValueMethod = enumClass.getMethod("getValue");
        Object value = enumClass.getEnumConstants()[0];

        assertThat(getValueMethod.<Object>invoke(value)).isEqualTo("");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithLongString(JdkVersion jdkVersion) throws Exception {
        String longValue = "a".repeat(1000);
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum LongStr {
                    Value = "%s"
                  }
                }""".formatted(longValue));
        Class<?> enumClass = runner.getClass("com.LongStr");

        var getValueMethod = enumClass.getMethod("getValue");
        Object value = enumClass.getEnumConstants()[0];

        assertThat(getValueMethod.<Object>invoke(value)).isEqualTo(longValue);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithMaxIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum MaxInt {
                    Max = 2147483647
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.MaxInt");

        var getValueMethod = enumClass.getMethod("getValue");
        Object max = enumClass.getEnumConstants()[0];

        assertThat(getValueMethod.<Object>invoke(max)).isEqualTo(Integer.MAX_VALUE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithMinIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum MinInt {
                    Min = -2147483647
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.MinInt");

        var getValueMethod = enumClass.getMethod("getValue");
        Object min = enumClass.getEnumConstants()[0];

        assertThat(getValueMethod.<Object>invoke(min)).isEqualTo(-2147483647);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithNumberInName(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Numeric {
                    Option1,
                    Option2,
                    Option3
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Numeric");

        assertThat(enumClass.getEnumConstants().length).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithReservedKeywordNames(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Reserved {
                    Class = 1,
                    Public = 2,
                    Static = 3
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Reserved");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("CLASS");
        assertThat(((Enum<?>) constants[1]).name()).isEqualTo("PUBLIC");
        assertThat(((Enum<?>) constants[2]).name()).isEqualTo("STATIC");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithSingleMember(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Single {
                    Only
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Single");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(constants.length).isEqualTo(1);
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("ONLY");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithSpecialCharsInString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Special {
                    Tab = "\\t",
                    Newline = "\\n",
                    Quote = "\\""
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Special");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo("\t");
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo("\n");
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo("\"");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithUnderscoreNames(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Underscore {
                    _private,
                    __internal,
                    value_with_underscore
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Underscore");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo("_PRIVATE");
        assertThat(((Enum<?>) constants[1]).name()).isEqualTo("__INTERNAL");
        assertThat(((Enum<?>) constants[2]).name()).isEqualTo("VALUE_WITH_UNDERSCORE");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithVeryLongName(JdkVersion jdkVersion) throws Exception {
        String longName = "VeryLongEnumMemberNameThatExceedsNormalLength";
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum LongNames {
                    %s = 1
                  }
                }""".formatted(longName));
        Class<?> enumClass = runner.getClass("com.LongNames");

        Object[] constants = enumClass.getEnumConstants();
        assertThat(((Enum<?>) constants[0]).name()).isEqualTo(longName.toUpperCase());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEnumWithZeroAndNegativeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum ZeroNeg {
                    Zero = 0,
                    NegativeOne = -1,
                    NegativeHundred = -100
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.ZeroNeg");

        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(getValueMethod.<Object>invoke(constants[0])).isEqualTo(0);
        assertThat(getValueMethod.<Object>invoke(constants[1])).isEqualTo(-1);
        assertThat(getValueMethod.<Object>invoke(constants[2])).isEqualTo(-100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatingPointEnumRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Float {
                        Pi = 3.14
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class);;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testHeterogeneousEnumRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Mixed {
                        Num = 1,
                        Str = "string"
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class);;
    }
}
