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

package com.caoccao.javet.swc4j.compiler.ast.module;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Java varargs method calls on imported classes.
 */
public class TestImportVarargs extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassVarargsConcat(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { MathHelper } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportVarargs'
                namespace com {
                  export class A {
                    public test(): String {
                      return MathHelper.concat("Hello", " ", "World", "!")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Hello World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassVarargsMixed(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { MathHelper } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportVarargs'
                namespace com {
                  export class A {
                    public test(): String {
                      return MathHelper.formatNumbers("Values: ", 1, 2, 3)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Values: 1, 2, 3");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassVarargsNoArgs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { MathHelper } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportVarargs'
                namespace com {
                  export class A {
                    public test(): int {
                      return MathHelper.sum()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassVarargsSum(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { MathHelper } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportVarargs'
                namespace com {
                  export class A {
                    public test(): int {
                      return MathHelper.sum(1, 2, 3, 4, 5)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("%d + %d + %d + %d = %d", 1, 2, 3, 4, 10)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("1 + 2 + 3 + 4 = 10");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("String: %s, Int: %d, Boolean: %b", "test", 42, true)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("String: test, Int: 42, Boolean: true");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatMultipleArgs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("Value: %d, Text: %s, Float: %.2f", 42, "test", 3.14)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Value: 42, Text: test, Float: 3.14");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatNoVarargs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("Plain text")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Plain text");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatSimple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("Hello %s", "World")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Hello World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatWithBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("%b and %b", true, false)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("true and false");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatWithDoubles(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("Pi: %.2f, E: %.2f", 3.14159, 2.71828)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Pi: 3.14, E: 2.72");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringFormatWithIntegers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return String.format("%d + %d = %d", 1, 2, 3)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("1 + 2 = 3");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVarargsWithPrimitives(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Math } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): int {
                      return Math.max(5, 10)
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(10);
    }

    /**
     * Static helper class for testing varargs method imports.
     */
    public static class MathHelper {
        public static String concat(String... strings) {
            StringBuilder sb = new StringBuilder();
            for (String str : strings) {
                sb.append(str);
            }
            return sb.toString();
        }

        public static String formatNumbers(String prefix, int... numbers) {
            StringBuilder sb = new StringBuilder(prefix);
            for (int i = 0; i < numbers.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(numbers[i]);
            }
            return sb.toString();
        }

        public static int sum(int... numbers) {
            int total = 0;
            for (int num : numbers) {
                total += num;
            }
            return total;
        }
    }
}
