/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.ast.module;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


/**
 * Tests for Java class imports and static method calls.
 */
public class TestImport extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMathAbs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Math } from 'java.lang'
                namespace com {
                  export class A {
                    public test(x: double): double {
                      return Math.abs(x)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Double>invoke("test", -5.0)).isCloseTo(5.0, within(0.001));
        assertThat(instanceRunner.<Double>invoke("test", 3.7)).isCloseTo(3.7, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMathFloor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Math } from 'java.lang'
                namespace com {
                  export class A {
                    public test(x: double): double {
                      return Math.floor(x)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Double>invoke("test", 3.7)).isCloseTo(3.0, within(0.001));
        assertThat(instanceRunner.<Double>invoke("test", 5.2)).isCloseTo(5.0, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMathMax(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Math } from 'java.lang'
                namespace com {
                  export class A {
                    public test(a: double, b: double): double {
                      return Math.max(a, b)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Double>invoke("test", 3.0, 5.0)).isCloseTo(5.0, within(0.001));
        assertThat(instanceRunner.<Double>invoke("test", 10.0, 2.0)).isCloseTo(10.0, within(0.001));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMathMaxWithIntegerWidening(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Math } from 'java.lang'
                namespace com {
                  export class A {
                    public test(a: int, b: int): int {
                      return Math.max(a, b)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.invoke("test", 3, 5)).isEqualTo(5);
        assertThat((int) instanceRunner.invoke("test", 10, 2)).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMathMinWithExactTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Math } from 'java.lang'
                namespace com {
                  export class A {
                    public testInt(a: int, b: int): int {
                      return Math.min(a, b)
                    }
                
                    public testDouble(a: double, b: double): double {
                      return Math.min(a, b)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        // Test that int args select min(int, int)
        assertThat((int) instanceRunner.invoke("testInt", 3, 5)).isEqualTo(3);

        // Test that double args select min(double, double)
        assertThat(instanceRunner.<Double>invoke("testDouble", 3.5, 5.2)).isCloseTo(3.5, within(0.001));
    }
}
