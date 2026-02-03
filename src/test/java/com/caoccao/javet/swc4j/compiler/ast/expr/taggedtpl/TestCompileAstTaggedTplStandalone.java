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
 * Tests for standalone function tags in tagged template literals.
 * Verifies that standalone functions (not member expressions) can be used as template tags.
 */
public class TestCompileAstTaggedTplStandalone extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function tag(strings: String[], value: String): String {
                    return strings[0] + value + strings[1]
                  }
                  export class A {
                    test(): String {
                      return tag`Hello ${"World"}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagInDefaultPackage(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                export function tag(strings: String[], value: String): String {
                  return "[" + strings[0] + value + strings[1] + "]"
                }
                export class A {
                  test(): String {
                    return tag`Hi ${"there"}!`
                  }
                }""");
        String result = runner.createInstanceRunner("A").invoke("test");
        assertThat(result).isEqualTo("[Hi there!]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagMultipleInterpolations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function join(strings: String[], a: String, b: String): String {
                    return strings[0] + a + strings[1] + b + strings[2]
                  }
                  export class A {
                    test(): String {
                      return join`[${"X"}|${"Y"}]`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("[X|Y]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagNoInterpolation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function wrap(strings: String[]): String {
                    return "[" + strings[0] + "]"
                  }
                  export class A {
                    test(): String {
                      return wrap`Hello World`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("[Hello World]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagReturnsInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function count(strings: String[], value: int): int {
                    return strings.length + value
                  }
                  export class A {
                    test(): int {
                      return count`a ${10} d`
                    }
                  }
                }""");
        int result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(12);  // strings.length=2, value=10
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagWithIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function format(strings: String[], num: int): String {
                    return strings[0] + (num * 2) + strings[1]
                  }
                  export class A {
                    test(): String {
                      const x: int = 21
                      return format`Result: ${x}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Result: 42!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagWithMethodCall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function format(strings: String[], name: String): String {
                    return strings[0] + name + strings[1]
                  }
                  export class A {
                    getName(): String {
                      return "Bob"
                    }
                    test(): String {
                      return format`Hello ${this.getName()}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello Bob!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStandaloneTagWithVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export function greet(strings: String[], name: String): String {
                    return strings[0] + name.toUpperCase() + strings[1]
                  }
                  export class A {
                    test(): String {
                      const name = "alice"
                      return greet`Hello ${name}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello ALICE!");
    }
}
