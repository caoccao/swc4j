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

package com.caoccao.javet.swc4j.compiler.ast.expr.binexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestCompileBinExprDiv extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteDivideInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 30
                      const b: int = 10
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteDivisionPromotion(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 127
                      const b: byte = 2
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(63, (int) runner.createInstanceRunner("com.A").invoke("test")); // byte promotes to int, 127/2 = 63
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectDivideLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 20
                      const b: Long = 4
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(5L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedDivision(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 100
                      const b: int = 5
                      const c: int = 2
                      const d: int = 2
                      return a / b / c / d
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test")); // 100/5/2/2 = 20/2/2 = 10/2 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharDivideChar(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: char = 'Z'
                      const b: char = '\\u0002'
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(45, (int) runner.createInstanceRunner("com.A").invoke("test")); // 'Z' (90) / 2 = 45
    }

    // Mixed primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterDivideCharacter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Character = '\\u0014'
                      const b: Character = '\\u0004'
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test")); // 20 / 4 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexExpressionWithMultipleDivisions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 100
                      const b: int = 5
                      const c: int = 4
                      const quot = a / b
                      const result = quot / c
                      return result
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivisionResultingInOne(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 42
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivisionWithLargeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 30000
                      const b: int = 10
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(3000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivisionWithNegativeOperands(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -30
                      const b: int = -3
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test")); // -30 / -3 = 10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivisionWithParentheses(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 60
                      const b: int = 2
                      const c: int = 3
                      return a / (b * c)
                    }
                  }
                }""");
        assertEquals(10, (int) runner.createInstanceRunner("com.A").invoke("test")); // 60 / (2 * 3) = 60 / 6 = 10
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDivisionWithSmallNegativeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -30000
                      const b: int = 10
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(-3000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleDivisionByZeroReturnsInfinity(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 10.0
                      const b: double = 0.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(Double.POSITIVE_INFINITY, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleDivisionPrecision(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 1.0
                      const b: double = 3.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(0.333333333, (double) runner.createInstanceRunner("com.A").invoke("test"), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleObjectDivideDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 10.5
                      const b: Double = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(4.2, (double) runner.createInstanceRunner("com.A").invoke("test"), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 35
                      const b: byte = 7
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(5.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 24
                      const b: byte = 4
                      return (a as float) / (b as float)
                    }
                  }
                }""");
        assertEquals(6.0f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 40
                      const b: byte = 8
                      return (a as int) / (b as int)
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Mixed primitive and wrapper tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 40
                      const b: Byte = 8
                      return (a as int) / (b as int)
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 27
                      const b: byte = 3
                      return (a as long) / (b as long)
                    }
                  }
                }""");
        assertEquals(9L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToLongWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 36
                      const b: Byte = 3
                      return (a as Long) / (b as Long)
                    }
                  }
                }""");
        assertEquals(12L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 19.5
                      const b: double = 3.0
                      return (a as float) / (b as float)
                    }
                  }
                }""");
        assertEquals(6.5f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 20.9
                      const b: double = 4.3
                      return (a as int) / (b as int)
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test")); // 20 / 4 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 20.8
                      const b: double = 4.3
                      return (a as long) / (b as long)
                    }
                  }
                }""");
        assertEquals(5L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 20 / 4 = 5
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleWrapperToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 16.25
                      const b: Double = 2.5
                      return (a as float) / (b as float)
                    }
                  }
                }""");
        assertEquals(6.5f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 13.75
                      const b: float = 2.5
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(5.5, (double) runner.createInstanceRunner("com.A").invoke("test"), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 3.75
                      const b: Float = 1.5
                      return (a as Double) / (b as Double)
                    }
                  }
                }""");
        assertEquals(2.5, (double) runner.createInstanceRunner("com.A").invoke("test"), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 12.9
                      const b: float = 3.1
                      return (a as int) / (b as int)
                    }
                  }
                }""");
        assertEquals(4, (int) runner.createInstanceRunner("com.A").invoke("test")); // 12 / 3 = 4
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 24.6
                      const b: float = 4.4
                      return (a as long) / (b as long)
                    }
                  }
                }""");
        assertEquals(6L, (long) runner.createInstanceRunner("com.A").invoke("test")); // 24 / 4 = 6
    }

    // Explicit type cast tests - Widening conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatWrapperToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 8.25
                      const b: Float = 1.5
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(5.5, (double) runner.createInstanceRunner("com.A").invoke("test"), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 24
                      const b: float = 3
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(8.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 15
                      const b: int = 3
                      return (a as float) / (b as float)
                    }
                  }
                }""");
        assertEquals(5.0f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 30
                      const b: int = 3
                      return ((((a as long)))) / (((b as long)))
                    }
                  }
                }""");
        assertEquals(10L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 75
                      const b: Integer = 15
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(5.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 65
                      const b: Integer = 13
                      return (a as Double) / (b as Double)
                    }
                  }
                }""");
        assertEquals(5.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 200
                      const b: Integer = 20
                      return (a as long) / (b as long)
                    }
                  }
                }""");
        assertEquals(10L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToLongWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 108
                      const b: Integer = 6
                      return (a as Long) / (b as Long)
                    }
                  }
                }""");
        assertEquals(18L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 700
                      const b: long = 100
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(7.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 220
                      const b: int = 11
                      return (a as float) / (b as float)
                    }
                  }
                }""");
        assertEquals(20.0f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Explicit cast tests - Narrowing conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 400
                      const b: long = 4
                      return (a as int) / (b as int)
                    }
                  }
                }""");
        assertEquals(100, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongWrapperToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 3000
                      const b: Long = 100
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(30.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongWrapperToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 210
                      const b: Long = 3
                      return (a as Float) / (b as Float)
                    }
                  }
                }""");
        assertEquals(70.0f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 60
                      const b: short = 3
                      const c: int = 2
                      return (a as double) / (b as double) / (c as double)
                    }
                  }
                }""");
        assertEquals(10.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 120
                      const b: short = 5
                      return (a as double) / (b as double)
                    }
                  }
                }""");
        assertEquals(24.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 90
                      const b: Short = 5
                      return (a as Double) / (b as Double)
                    }
                  }
                }""");
        assertEquals(18.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Explicit cast tests - Wrapper to primitive conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 75
                      const b: short = 5
                      return (a as float) / (b as float)
                    }
                  }
                }""");
        assertEquals(15.0f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 300
                      const b: short = 10
                      return (a as int) / (b as int)
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 300
                      const b: Short = 10
                      return (a as int) / (b as int)
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 250
                      const b: short = 5
                      return (a as long) / (b as long)
                    }
                  }
                }""");
        assertEquals(50L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDivideDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 7.0
                      const b: double = 2.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(3.5, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDivideDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 9.0
                      const b: Double = 2.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(4.5, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDivisionByZeroReturnsInfinity(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 10.0
                      const b: float = 0.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(Float.POSITIVE_INFINITY, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatDivisionPrecision(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 1.0
                      const b: float = 3.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(0.333333f, (float) runner.createInstanceRunner("com.A").invoke("test"), 0.00001f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectDivideDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 7.0
                      const b: double = 2.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(3.5, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectDivideDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 16.5
                      const b: Double = 3.0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(5.5, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectDivideFloatObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 26.25
                      const b: Float = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(10.5f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntDivideDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 12
                      const b: double = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(4.8, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntDivideFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 7
                      const b: float = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(2.8f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntDivideInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 30
                      const b: int = 10
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntDivideLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 20
                      const b: long = 4
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(5L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntDivideLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 24
                      const b: Long = 4
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(6L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerDivideDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 15
                      const b: Double = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(6.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerDivideFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 12
                      const b: Float = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(4.8f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Edge case tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerDivideLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 40
                      const b: Long = 8
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(5L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerDivisionByZeroThrowsException(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: int = 0
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertThrows(InvocationTargetException.class, () -> {
            runner.createInstanceRunner("com.A").invoke("test");
        });
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerDivisionTruncation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 7
                      const b: int = 2
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test")); // 7/2 = 3 (truncated)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectDivideIntegerObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 25
                      const b: Integer = 5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(5, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectDivideLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 70
                      const b: long = 7
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(10L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongDivideDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 25
                      const b: double = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(10.0, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongDivideFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 25
                      const b: float = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(10.0f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectDivideDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 52
                      const b: Double = 3.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(14.857142857, (double) runner.createInstanceRunner("com.A").invoke("test"), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectDivideFloatObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 37
                      const b: Float = 2.5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(14.8f, (Float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectDivideLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 220
                      const b: Long = 11
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(20L, (Long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegativeDivisionTruncation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -7
                      const b: int = 2
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(-3, (Integer) runner.createInstanceRunner("com.A").invoke("test")); // -7/2 = -3 (truncated toward zero)
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortDivideInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 20
                      const b: int = 4
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(5, (Integer) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortDivideLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 48
                      const b: Long = 6
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(8L, (Long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortDivideShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const a: short = 50
                      const b: short = 5
                      return a / b
                    }
                  }
                }""");
        assertEquals(10, (short) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortObjectDivideShortObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 50
                      const b: Short = 5
                      const c = a / b
                      return c
                    }
                  }
                }""");
        assertEquals(10, (Integer) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
