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
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Tests for computed enum values.
 * <p>
 * Tests cover:
 * - Basic arithmetic operations (+, -, *, /, %)
 * - Exponentiation (**)
 * - Bitwise operations (&, |, ^, <<, >>, >>>)
 * - Unary operations (-, +, ~)
 * - References to previously defined members
 * - Parenthesized expressions
 * - Complex expressions combining multiple operations
 * - Error cases (forward references, circular references)
 */
public class TestCompileAstTsEnumDeclComputed extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedAddition(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 10,
                    B = A + 5
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(10, 15));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedBitFlags(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Flags {
                    None = 0,
                    Read = 1 << 0,
                    Write = 1 << 1,
                    Execute = 1 << 2,
                    ReadWrite = Read | Write,
                    All = Read | Write | Execute
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Flags");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1]),
                getValueMethod.invoke(constants[2]),
                getValueMethod.invoke(constants[3]),
                getValueMethod.invoke(constants[4]),
                getValueMethod.invoke(constants[5])
        )).isEqualTo(List.of(0, 1, 2, 4, 3, 7));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedBitwiseAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 0b1111,
                    B = A & 0b0101
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(0b1111, 0b0101));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedBitwiseNot(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 0,
                    B = ~A
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(0, -1));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedBitwiseOr(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 0b1010,
                    B = A | 0b0101
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(0b1010, 0b1111));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedBitwiseXor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 0b1111,
                    B = A ^ 0b1010
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(0b1111, 0b0101));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedChainedReferences(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 1,
                    B = A * 2,
                    C = B * 2,
                    D = C * 2
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1]),
                getValueMethod.invoke(constants[2]),
                getValueMethod.invoke(constants[3])
        )).isEqualTo(List.of(1, 2, 4, 8));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedComplexExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 10,
                    B = 3,
                    C = (A + B) * 2 - B ** 2 + A / 2
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        // C = (10 + 3) * 2 - 3^2 + 10/2 = 26 - 9 + 5 = 22
        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1]),
                getValueMethod.invoke(constants[2])
        )).isEqualTo(List.of(10, 3, 22));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedDivision(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 100,
                    B = A / 4
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(100, 25));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedDivisionByZeroRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Computed {
                        A = 0,
                        B = 10 / A
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("Division by zero");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedExponentiation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 2,
                    B = A ** 10
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(2, 1024));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedForwardReferenceRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Computed {
                        A = B * 2,
                        B = 10
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("Cannot reference enum member 'B' before it is defined");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedLeftShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 1,
                    B = A << 4
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(1, 16));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedMixedOperators(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    Base = 8,
                    Shifted = Base << 1,
                    Masked = Shifted & 0xFF,
                    Inverted = ~Masked & 0xFF
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        // Base=8, Shifted=16, Masked=16, Inverted=~16 & 0xFF = 239
        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1]),
                getValueMethod.invoke(constants[2]),
                getValueMethod.invoke(constants[3])
        )).isEqualTo(List.of(8, 16, 16, 239));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedModulo(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 17,
                    B = A % 5
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(17, 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedModuloByZeroRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Computed {
                        A = 0,
                        B = 10 % A
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("Division by zero");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedMultiplication(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 6,
                    B = A * 7
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(6, 42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedNestedParentheses(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 2,
                    B = ((A + 1) * (A + 2)) + ((A * 2))
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        // B = ((2+1) * (2+2)) + (2*2) = (3 * 4) + 4 = 12 + 4 = 16
        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(2, 16));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedParenthesized(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 2,
                    B = 3,
                    C = (A + B) * 4
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1]),
                getValueMethod.invoke(constants[2])
        )).isEqualTo(List.of(2, 3, 20));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 64,
                    B = A >> 2
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(64, 16));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedSubtraction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 20,
                    B = A - 7
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(20, 13));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedUnaryMinus(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = 42,
                    B = -A
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(42, -42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedUnaryPlus(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = -10,
                    B = +A
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(-10, -10));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedUndefinedReferenceRejection(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export enum Computed {
                        A = Unknown + 1
                      }
                    }""");
        }).isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessageContaining("Cannot reference enum member 'Unknown' before it is defined");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedUnsignedRightShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A = -8,
                    B = A >>> 2
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1])
        )).isEqualTo(List.of(-8, -8 >>> 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComputedWithAutoIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export enum Computed {
                    A,
                    B = A * 10,
                    C,
                    D
                  }
                }""");
        Class<?> enumClass = runner.getClass("com.Computed");
        var getValueMethod = enumClass.getMethod("getValue");
        Object[] constants = enumClass.getEnumConstants();

        // A=0, B=0*10=0, C=1 (auto from B+1), D=2
        assertThat(List.of(
                getValueMethod.invoke(constants[0]),
                getValueMethod.invoke(constants[1]),
                getValueMethod.invoke(constants[2]),
                getValueMethod.invoke(constants[3])
        )).isEqualTo(List.of(0, 0, 1, 2));
    }
}
