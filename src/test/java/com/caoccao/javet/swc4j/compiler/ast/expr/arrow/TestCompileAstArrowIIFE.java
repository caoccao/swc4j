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

package com.caoccao.javet.swc4j.compiler.ast.expr.arrow;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;


/**
 * Tests for IIFE (Immediately Invoked Function Expression) arrow expressions.
 * Edge case 51 from the implementation plan.
 */
public class TestCompileAstArrowIIFE extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEAsReturnValue(JdkVersion jdkVersion) throws Exception {
        // IIFE result directly returned
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return ((x: int): int => x * 3)(7)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(21);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEBasicIntParam(JdkVersion jdkVersion) throws Exception {
        // Edge case 51: Basic IIFE with int parameter
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const result: int = ((x: int): int => x * 2)(5)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEBlockBody(JdkVersion jdkVersion) throws Exception {
        // IIFE with block body
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const result: int = ((x: int): int => {
                        const doubled: int = x * 2
                        return doubled + 10
                      })(5)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEBooleanType(JdkVersion jdkVersion) throws Exception {
        // IIFE with boolean type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const result: boolean = ((x: int): boolean => x > 5)(10)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((boolean) result).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEDoubleType(JdkVersion jdkVersion) throws Exception {
        // IIFE with double type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const result: double = ((x: double): double => x * 2.5)(4.0)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((double) result).isEqualTo(10.0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEGeneratesUniqueInterfaces(JdkVersion jdkVersion) throws Exception {
        // Multiple IIFE should generate different interface names
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test1(): int {
                      return ((x: int): int => x * 2)(5)
                    }
                    test2(): int {
                      return ((x: int): int => x + 10)(5)
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(
                List.of(
                        instanceRunner.invoke("test1"),
                        instanceRunner.invoke("test2")
                )
        ).isEqualTo(
                List.of(10, 15)
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFELongType(JdkVersion jdkVersion) throws Exception {
        // IIFE with long type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const result: long = ((x: long): long => x * 2)(5000000000)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((long) result).isEqualTo(10000000000L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEMultipleCaptures(JdkVersion jdkVersion) throws Exception {
        // IIFE capturing multiple outer variables
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = 3
                      const b: int = 4
                      const result: int = ((x: int): int => x + a + b)(5)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(12);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEMultipleInMethod(JdkVersion jdkVersion) throws Exception {
        // Multiple IIFE in same method
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const a: int = ((x: int): int => x * 2)(5)
                      const b: int = ((x: int): int => x + 10)(a)
                      return b
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEMultipleParams(JdkVersion jdkVersion) throws Exception {
        // IIFE with two parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const result: int = ((x: int, y: int): int => x + y)(3, 7)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(10);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFENestedInExpression(JdkVersion jdkVersion) throws Exception {
        // IIFE result used in arithmetic expression
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const result: int = ((x: int): int => x * 2)(5) + 10
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(20);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFENoParams(JdkVersion jdkVersion) throws Exception {
        // IIFE with no parameters
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const result: int = ((): int => 42)()
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEReturnString(JdkVersion jdkVersion) throws Exception {
        // IIFE returning string
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const result: String = ((s: String): String => s + " World")("Hello")
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEWithCapture(JdkVersion jdkVersion) throws Exception {
        // IIFE capturing outer variable
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const multiplier: int = 3
                      const result: int = ((x: int): int => x * multiplier)(5)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEWithComplexArgument(JdkVersion jdkVersion) throws Exception {
        // IIFE with complex argument expression
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const x: int = 3
                      const result: int = ((n: int): int => n * n)(x + 2)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(25);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEWithThisCapture(JdkVersion jdkVersion) throws Exception {
        // IIFE capturing 'this'
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    multiplier: int = 10
                    test(): int {
                      const result: int = ((x: int): int => x * this.multiplier)(5)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIIFEWithTypeInference(JdkVersion jdkVersion) throws Exception {
        // IIFE with inferred return type
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const result: int = ((x: int) => x * 2 + 1)(5)
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat((int) result).isEqualTo(11);
    }
}
