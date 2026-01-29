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

import java.math.BigInteger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


public class TestCompileBinExprAdd extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPlusBigInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a: BigInteger = 100n
                      const b: BigInteger = 200n
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("300"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPlusBigIntLarge(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 999999999999999999n + 1n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("1000000000000000000"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPlusInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a: BigInteger = 100n
                      const b: int = 50
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("150"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPlusNegative(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 500n + (-200n)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("300"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBigIntPlusZero(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      return 123n + 0n
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("123"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectPlusLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 10
                      const b: Long = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((Long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testByteObjectPlusString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 12
                      const b: String = 'user'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("12user");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBytePlusInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 10
                      const b: int = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((Integer) instanceRunner.<Object>invoke("test")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBytePlusString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 12
                      const b: String = 'user'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("12user");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharPlusChar(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: char = 'A'
                      const b: char = 'B'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((Integer) instanceRunner.<Object>invoke("test")).isEqualTo(131); //'A' (65) + 'B' (66) = 131
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharPlusString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: char = 'X'
                      const b: String = 'YZ'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("XYZ");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterPlusCharacter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Character = '1'
                      const b: Character = '2'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((Integer) instanceRunner.<Object>invoke("test")).isEqualTo(99); //'1' (49) + '2' (50) = 99
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterPlusString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Character = '@'
                      const b: String = 'user'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("@user");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexExpressionWithMultipleAdditions(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 1
                      const b: int = 2
                      const c: int = 3
                      const sum = a + b
                      const total = sum + c
                      return total
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((Integer) instanceRunner.<Object>invoke("test")).isEqualTo(6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDirectNullPlusString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return null + "test"
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("nulltest");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDirectStringPlusNull(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "test" + null
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("testnull");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleObjectPlusDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 50.5
                      const b: Double = 25.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(76.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 7
                      const b: byte = 13
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(20.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 8
                      const b: byte = 12
                      return (a as float) + (b as float)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(20.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 15
                      const b: byte = 25
                      return (a as int) + (b as int)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(40);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 20
                      const b: Byte = 30
                      return (a as int) + (b as int)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 12
                      const b: byte = 18
                      return (a as long) + (b as long)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToLongWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 18
                      const b: Byte = 22
                      return (a as Long) + (b as Long)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(40L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastByteToShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 10
                      const b: byte = 20
                      return (a as short) + (b as short)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 10.5
                      const b: double = 20.5
                      return (a as float) + (b as float)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(31.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 15.7
                      const b: double = 25.9
                      return (a as int) + (b as int)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(40);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 10.8
                      const b: double = 20.3
                      return (a as long) + (b as long)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastDoubleWrapperToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 15.5
                      const b: Double = 20.5
                      return (a as float) + (b as float)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(36.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 10.5
                      const b: float = 20.5
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        // Note: float to double conversion may have precision differences
        assertThat(instanceRunner.<Double>invoke("test")).isCloseTo(31.0, within(0.0001));
    }

    // Explicit cast tests - Narrowing conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 8.5
                      const b: Float = 11.5
                      return (a as Double) + (b as Double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Double>invoke("test")).isCloseTo(20.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 8.9
                      const b: float = 12.1
                      return (a as int) + (b as int)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 12.6
                      const b: float = 18.4
                      return (a as long) + (b as long)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastFloatWrapperToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 12.5
                      const b: Float = 18.5
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Double>invoke("test")).isCloseTo(31.0, within(0.0001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 1
                      const b: float = 2
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(3.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: int = 10
                      return (a as float) + (b as float)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(15.0f);
    }

    // Explicit cast tests - Wrapper to primitive conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: int = 20
                      return ((((a as long)))) + (((b as long)))
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 25
                      const b: Integer = 35
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(60.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 15
                      const b: Integer = 25
                      return (a as Double) + (b as Double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(40.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 50
                      const b: Integer = 100
                      return (a as long) + (b as long)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(150L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastIntegerToLongWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 40
                      const b: Integer = 60
                      return (a as Long) + (b as Long)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(100L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000
                      const b: long = 2000
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(3000.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 100
                      const b: int = 23
                      return (a as float) + (b as float)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(123.0f);
    }

    // Explicit cast tests - Wrapper to Wrapper conversions

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToFloatWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 200
                      const b: Long = 300
                      return (a as Float) + (b as Float)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(500.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 500
                      const b: long = 700
                      return (a as int) + (b as int)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(1200);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastLongWrapperToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 1000
                      const b: Long = 2000
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(3000.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 5
                      const b: short = 10
                      const c: int = 15
                      return (a as double) + (b as double) + (c as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(30.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 80
                      const b: short = 120
                      return (a as double) + (b as double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(200.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToDoubleWrapper(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 45
                      const b: Short = 55
                      return (a as Double) + (b as Double)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(100.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 50
                      const b: short = 75
                      return (a as float) + (b as float)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(125.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 100
                      const b: short = 200
                      return (a as int) + (b as int)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(300);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 100
                      const b: Short = 200
                      return (a as int) + (b as int)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(300);
    }

    // Mixed primitive type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExplicitCastShortToLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 150
                      const b: short = 250
                      return (a as long) + (b as long)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(400L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectPlusDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 10.5
                      const b: double = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(31.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectPlusDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 10.5
                      const b: Double = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(31.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatObjectPlusFloatObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 50.5
                      const b: Float = 25.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(76.0f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatPlusDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 10.5
                      const b: double = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(31.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatPlusDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 10.5
                      const b: Double = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(31.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntPlusBigInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): BigInteger {
                      const a: int = 50
                      const b: BigInteger = 100n
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(new BigInteger("150"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntPlusDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: double = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(30.5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntPlusFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: float = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(30.5f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntPlusLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: long = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntPlusLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 10
                      const b: Long = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectPlusIntegerObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 5
                      const b: Integer = 10
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerObjectPlusLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 10
                      const b: long = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerPlusDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 10
                      const b: Double = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(30.5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerPlusFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 10
                      const b: Float = 20.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(30.5f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerPlusInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: int = 5
                      const b: int = 10
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(15);
    }

    // Mixed primitive and wrapper type tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerPlusLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Integer = 10
                      const b: Long = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectPlusDoubleObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 100
                      const b: Double = 23.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(123.5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectPlusFloatObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 100
                      const b: Float = 23.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(123.5f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongObjectPlusLongObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 100
                      const b: Long = 23
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(123L);
    }

    // Explicit type cast tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongPlusDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 100
                      const b: double = 23.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(123.5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongPlusFloat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 100
                      const b: float = 23.5
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((float) instanceRunner.<Object>invoke("test")).isEqualTo(123.5f);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullStringConcatenation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a = null
                      const b = "hello"
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("nullhello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortObjectPlusShortObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 50
                      const b: Short = 25
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(75);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortPlusInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 10
                      const b: int = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("test")).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortPlusLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 10
                      const b: Long = 20
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((long) instanceRunner.<Object>invoke("test")).isEqualTo(30L);
    }

    // BigInt tests

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testShortPlusShort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): short {
                      const a: short = 50
                      const b: short = 25
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((short) instanceRunner.<Object>invoke("test")).isEqualTo((short) 75);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringNullConcatenation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a = "hello"
                      const b = null
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("hellonull");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringPlusChar(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = 'hello'
                      const b: char = '!'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("hello!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringPlusCharacter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = 'test'
                      const b: Character = '!'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("test!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringPlusInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export type MyString = String;
                namespace com {
                  export class A {
                    test() {
                      const a: MyString = 'a'
                      const b: int = 1
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("a1");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringPlusString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: String = 'a'
                      const b = 'b'
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("ab");
    }
}
