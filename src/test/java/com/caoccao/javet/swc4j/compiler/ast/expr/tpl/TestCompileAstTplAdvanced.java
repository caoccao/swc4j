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

package com.caoccao.javet.swc4j.compiler.ast.expr.tpl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Advanced tests for template literal compilation.
 * Covers edge cases: nested templates, conditionals, null, boolean, complex expressions.
 */
public class TestCompileAstTplAdvanced extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateInConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const flag = true
                      return flag ? `yes` : `no`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("yes");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateManyInterpolations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const a = "A"
                      const b = "B"
                      const c = "C"
                      const d = "D"
                      return `${a}-${b}-${c}-${d}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("A-B-C-D");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateNestedTemplate(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const x = "inner"
                      return `Outer ${`nested ${x}`} end`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Outer nested inner end");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateReturnedFromMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    greet(name: String): String {
                      return `Hello ${name}!`
                    }
                    test(): String {
                      return this.greet("World")
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateWithBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const flag = true
                      return `Flag: ${flag}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Flag: true");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateWithConditionalExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const x = 10
                      return `Result: ${x > 5 ? "big" : "small"}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Result: big");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateWithDoubleValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const pi: double = 3.14
                      return `Pi: ${pi}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Pi: 3.14");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateWithLongValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const big: long = 9999999999
                      return `Big: ${big}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Big: 9999999999");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateWithMethodCall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    greet(): String {
                      return "World"
                    }
                    test(): String {
                      return `Hello ${this.greet()}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateWithNull(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const value: String = null
                      return `Value: ${value}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Value: null");
    }
}
