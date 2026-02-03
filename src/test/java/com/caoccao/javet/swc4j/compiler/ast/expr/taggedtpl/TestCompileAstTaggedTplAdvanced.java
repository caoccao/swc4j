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

package com.caoccao.javet.swc4j.compiler.ast.expr.taggedtpl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Advanced tests for tagged template literal compilation.
 * Covers edge cases: nested templates, primitives, null, boolean, method calls, conditionals.
 */
public class TestCompileAstTaggedTplAdvanced extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateInConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    upper(strings: String[], value: String): String {
                      return strings[0] + value.toUpperCase() + strings[1]
                    }
                    lower(strings: String[], value: String): String {
                      return strings[0] + value.toLowerCase() + strings[1]
                    }
                    test(): String {
                      const flag = true
                      const name = "World"
                      return flag ? this.upper`Hello ${name}!` : this.lower`Hello ${name}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello WORLD!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateManyInterpolations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    join(strings: String[], a: String, b: String, c: String, d: String): String {
                      return strings[0] + a + strings[1] + b + strings[2] + c + strings[3] + d + strings[4]
                    }
                    test(): String {
                      return this.join`[${\"A\"}|${\"B\"}|${\"C\"}|${\"D\"}]`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("[A|B|C|D]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateNestedRegularTemplate(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    wrap(strings: String[], inner: String): String {
                      return "[" + strings[0] + inner + strings[1] + "]"
                    }
                    test(): String {
                      const x = "value"
                      return this.wrap`prefix ${`nested ${x}`} suffix`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("[prefix nested value suffix]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateNestedTaggedTemplate(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    inner(strings: String[], x: String): String {
                      return "<" + strings[0] + x + strings[1] + ">"
                    }
                    outer(strings: String[], nested: String): String {
                      return "{" + strings[0] + nested + strings[1] + "}"
                    }
                    test(): String {
                      const val = "X"
                      const innerResult: String = this.inner`mid ${val} end`
                      return this.outer`start ${innerResult} finish`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("{start <mid X end> finish}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateReturnedFromMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    format(strings: String[], name: String): String {
                      return strings[0] + name.toUpperCase() + strings[1]
                    }
                    greet(name: String): String {
                      return this.format`Hello ${name}!`
                    }
                    test(): String {
                      return this.greet("world")
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello WORLD!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateWithBooleanValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    format(strings: String[], flag: boolean): String {
                      return strings[0] + (flag ? "YES" : "NO") + strings[1]
                    }
                    test(): String {
                      const active = true
                      return this.format`Status: ${active}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Status: YES!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateWithConditionalExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], value: String): String {
                      return strings[0] + value + strings[1]
                    }
                    test(): String {
                      const x = 10
                      const size = x > 5 ? "big" : "small"
                      return this.tag`Result: ${size}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Result: big!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateWithDoubleValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    format(strings: String[], value: double): String {
                      return strings[0] + (value * 2) + strings[1]
                    }
                    test(): String {
                      const pi: double = 3.14
                      return this.format`Double: ${pi}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Double: 6.28!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateWithLongValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    format(strings: String[], value: long): String {
                      return strings[0] + (value + 1) + strings[1]
                    }
                    test(): String {
                      const big: long = 9999999999
                      return this.format`Next: ${big}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Next: 10000000000!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateWithMethodCallExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getName(): String {
                      return "Alice"
                    }
                    format(strings: String[], name: String): String {
                      return strings[0] + name + strings[1]
                    }
                    test(): String {
                      return this.format`Hello ${this.getName()}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello Alice!");
    }
}
