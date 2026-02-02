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
 * Tests for tagged template literal compilation.
 * Tagged templates (e.g., {@code tag`Hello ${name}!`}) call a tag function
 * with a String[] of template strings and the interpolated values.
 */
public class TestCompileAstTaggedTplBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], name: String): String {
                      return strings[0] + name.toUpperCase() + strings[1]
                    }
                    test(): String {
                      const name = "world"
                      return this.tag`Hello ${name}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello WORLD!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateCustomJoin(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    highlight(strings: String[], a: String, b: String): String {
                      return strings[0] + "[" + a + "]" + strings[1] + "[" + b + "]" + strings[2]
                    }
                    test(): String {
                      const name = "Alice"
                      const age = "30"
                      return this.highlight`Name: ${name}, Age: ${age}.`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Name: [Alice], Age: [30].");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateEmptyTemplate(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[]): String {
                      return strings[0]
                    }
                    test(): String {
                      return this.tag``
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateMultipleValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], a: String, b: String): String {
                      return strings[0] + a + strings[1] + b + strings[2]
                    }
                    test(): String {
                      const first = "John"
                      const last = "Doe"
                      return this.tag`Name: ${first} ${last}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Name: John Doe!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateNoInterpolation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[]): String {
                      return strings[0]
                    }
                    test(): String {
                      return this.tag`Hello World!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateReturnInt(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    addValues(strings: String[], a: int, b: int): int {
                      return a + b
                    }
                    test(): int {
                      return this.addValues`${10} plus ${20}`
                    }
                  }
                }""");
        int result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateStringFirstElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getFirst(strings: String[], a: int, b: int): String {
                      return strings[0]
                    }
                    test(): String {
                      return this.getFirst`first${1}second${2}third`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("first");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTaggedTemplateWithIntValue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], value: int): String {
                      return strings[0] + (value * 2) + strings[1]
                    }
                    test(): String {
                      const x = 21
                      return this.tag`Double: ${x}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Double: 42!");
    }
}
