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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileBinExprMul extends BaseTestCompileSuite {

    // Basic primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteMultiplicationPromotion(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 127
                      const b: byte = 2
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(254, (int) runner.createInstanceRunner("com.A").invoke("test")); // byte promotes to int for operations
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteMultiplyInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 3
                      const b: int = 10
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectMultiplyLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 5
                      const b: Long = 4
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(20L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedMultiplication(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 2
                      const b: int = 3
                      const c: int = 4
                      const d: int = 5
                      return a * b * c * d
                    }
                  }
                }""");
        assertEquals(120, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharMultiplyChar(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: char = 'C'
                      const b: char = '\\u0002'
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(134, (int) runner.createInstanceRunner("com.A").invoke("test")); // 'C' (67) * 2 = 134
    }

    // Mixed primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterMultiplyCharacter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Character = '\\u0005'
                      const b: Character = '\\u0003'
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(15, (int) runner.createInstanceRunner("com.A").invoke("test")); // 5 * 3 = 15
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexExpressionWithMultipleMultiplications(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 5
                      const b: int = 4
                      const c: int = 3
                      const prod = a * b
                      const result = prod * c
                      return result
                    }
                  }
                }""");
        assertEquals(60, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleMultiplicationPrecision(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 0.1
                      const b: double = 0.2
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(0.02, runner.createInstanceRunner("com.A").invoke("test"), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleObjectMultiplyDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 10.5
                      const b: Double = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(26.25, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 7
                      const b: byte = 5
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(35.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 6
                      const b: byte = 4
                      return (a as float) * (b as float)
                    }
                  }
                }""");
        assertEquals(24.0f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 8
                      const b: byte = 5
                      return (a as int) * (b as int)
                    }
                  }
                }""");
        assertEquals(40, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 10
                      const b: Byte = 4
                      return (a as int) * (b as int)
                    }
                  }
                }""");
        assertEquals(40, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 9
                      const b: byte = 3
                      return (a as long) * (b as long)
                    }
                  }
                }""");
        assertEquals(27L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToLongWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 12
                      const b: Byte = 3
                      return (a as Long) * (b as Long)
                    }
                  }
                }""");
        assertEquals(36L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 6.5
                      const b: double = 3.0
                      return (a as float) * (b as float)
                    }
                  }
                }""");
        assertEquals(19.5f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 5.9
                      const b: double = 4.3
                      return (a as int) * (b as int)
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 5.8
                      const b: double = 4.3
                      return (a as long) * (b as long)
                    }
                  }
                }""");
        assertEquals(20L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Mixed primitive and wrapper tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleWrapperToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 6.5
                      const b: Double = 2.5
                      return (a as float) * (b as float)
                    }
                  }
                }""");
        assertEquals(16.25f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 5.5
                      const b: float = 2.5
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(13.75, runner.createInstanceRunner("com.A").invoke("test"), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 2.5
                      const b: Float = 1.5
                      return (a as Double) * (b as Double)
                    }
                  }
                }""");
        assertEquals(3.75, runner.createInstanceRunner("com.A").invoke("test"), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 4.9
                      const b: float = 3.1
                      return (a as int) * (b as int)
                    }
                  }
                }""");
        assertEquals(12, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 6.6
                      const b: float = 4.4
                      return (a as long) * (b as long)
                    }
                  }
                }""");
        assertEquals(24L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatWrapperToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 5.5
                      const b: Float = 1.5
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(8.25, runner.createInstanceRunner("com.A").invoke("test"), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 8
                      const b: float = 3
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(24.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: int = 3
                      return (a as float) * (b as float)
                    }
                  }
                }""");
        assertEquals(15.0f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: int = 3
                      return ((((a as long)))) * (((b as long)))
                    }
                  }
                }""");
        assertEquals(30L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 15
                      const b: Integer = 5
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(75.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 13
                      const b: Integer = 5
                      return (a as Double) * (b as Double)
                    }
                  }
                }""");
        assertEquals(65.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Explicit type cast tests - Widening conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 20
                      const b: Integer = 10
                      return (a as long) * (b as long)
                    }
                  }
                }""");
        assertEquals(200L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToLongWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 18
                      const b: Integer = 6
                      return (a as Long) * (b as Long)
                    }
                  }
                }""");
        assertEquals(108L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 100
                      const b: long = 7
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(700.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 20
                      const b: int = 11
                      return (a as float) * (b as float)
                    }
                  }
                }""");
        assertEquals(220.0f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 100
                      const b: long = 4
                      return (a as int) * (b as int)
                    }
                  }
                }""");
        assertEquals(400, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongWrapperToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 300
                      const b: Long = 10
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(3000.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongWrapperToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 70
                      const b: Long = 3
                      return (a as Float) * (b as Float)
                    }
                  }
                }""");
        assertEquals(210.0f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 10
                      const b: short = 3
                      const c: int = 2
                      return (a as double) * (b as double) * (c as double)
                    }
                  }
                }""");
        assertEquals(60.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 24
                      const b: short = 5
                      return (a as double) * (b as double)
                    }
                  }
                }""");
        assertEquals(120.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 18
                      const b: Short = 5
                      return (a as Double) * (b as Double)
                    }
                  }
                }""");
        assertEquals(90.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Explicit cast tests - Narrowing conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 15
                      const b: short = 5
                      return (a as float) * (b as float)
                    }
                  }
                }""");
        assertEquals(75.0f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 30
                      const b: short = 10
                      return (a as int) * (b as int)
                    }
                  }
                }""");
        assertEquals(300, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 30
                      const b: Short = 10
                      return (a as int) * (b as int)
                    }
                  }
                }""");
        assertEquals(300, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 50
                      const b: short = 5
                      return (a as long) * (b as long)
                    }
                  }
                }""");
        assertEquals(250L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatMultiplicationPrecision(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 0.3
                      const b: float = 0.3
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(0.09f, runner.createInstanceRunner("com.A").invoke("test"), 0.00001f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatMultiplyDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 3.5
                      const b: double = 2.0
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(7.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Explicit cast tests - Wrapper to primitive conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatMultiplyDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 4.5
                      const b: Double = 2.0
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(9.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectMultiplyDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 3.5
                      const b: double = 2.0
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(7.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectMultiplyDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 5.5
                      const b: Double = 3.0
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(16.5, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectMultiplyFloatObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 10.5
                      const b: Float = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(26.25f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMultiplyDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: double = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(12.5, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMultiplyFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 3
                      const b: float = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(7.5f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMultiplyInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 3
                      const b: int = 10
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMultiplyLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: long = 4
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(20L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntMultiplyLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 6
                      const b: Long = 4
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(24L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerMultiplyDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 6
                      const b: Double = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(15.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerMultiplyFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 5
                      const b: Float = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(12.5f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerMultiplyLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 10
                      const b: Long = 4
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(40L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectMultiplyIntegerObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 5
                      const b: Integer = 5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(25, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectMultiplyLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 10
                      const b: long = 7
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(70L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongMultiplyDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 10
                      const b: double = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(25.0, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongMultiplyFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 10
                      const b: float = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(25.0f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectMultiplyDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 15
                      const b: Double = 3.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(52.5, (double) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectMultiplyFloatObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 15
                      const b: Float = 2.5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(37.5f, (float) runner.createInstanceRunner("com.A").invoke("test"));
    }

    // Edge case tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectMultiplyLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 20
                      const b: Long = 11
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(220L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiplicationResultingInZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 42
                      const b: int = 0
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiplicationWithLargeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 30000
                      const b: int = 1
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(30000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiplicationWithNegativeOperands(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -10
                      const b: int = -3
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(30, (int) runner.createInstanceRunner("com.A").invoke("test")); // -10 * -3 = 30
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiplicationWithParentheses(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: int = 2
                      const c: int = 3
                      return a * (b * c)
                    }
                  }
                }""");
        assertEquals(60, (int) runner.createInstanceRunner("com.A").invoke("test")); // 10 * (2 * 3) = 10 * 6 = 60
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiplicationWithSmallNegativeValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = -30000
                      const b: int = 1
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(-30000, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortMultiplyInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 5
                      const b: int = 4
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortMultiplyLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 8
                      const b: Long = 6
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(48L, (long) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortMultiplyShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const a: short = 10
                      const b: short = 5
                      return a * b
                    }
                  }
                }""");
        assertEquals(50, (short) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortObjectMultiplyShortObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 10
                      const b: Short = 5
                      const c = a * b
                      return c
                    }
                  }
                }""");
        assertEquals(50, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
