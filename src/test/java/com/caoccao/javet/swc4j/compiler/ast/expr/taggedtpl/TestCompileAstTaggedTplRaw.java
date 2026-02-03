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
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TemplateStringsArray;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for raw string access in tagged template literals.
 * Verifies that tag functions can access both cooked and raw template strings
 * via the TemplateStringsArray class.
 */
public class TestCompileAstTaggedTplRaw extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBackwardCompatibilityWithStringArray(JdkVersion jdkVersion) throws Exception {
        // Tag function with String[] should still work (backward compatibility)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], value: String): String {
                      return strings[0] + value + strings[1]
                    }
                    test(): String {
                      return this.tag`Hello ${"World"}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRawStringAccessBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
                namespace com {
                  export class A {
                    tag(strings: TemplateStringsArray, value: String): String {
                      return strings.get(0) + "|" + strings.raw[0] + "|" + value
                    }
                    test(): String {
                      return this.tag`Hello ${"World"}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello |Hello |World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRawStringCacheFieldExists(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
                namespace com {
                  export class A {
                    tag(strings: TemplateStringsArray): String {
                      return strings.get(0)
                    }
                    test(): String {
                      return this.tag`Hello World`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World");

        // Verify that both $tpl$0 and $tpl$0$raw fields exist
        Class<?> clazz = runner.getClass("com.A");
        Field tplField = clazz.getDeclaredField("$tpl$0");
        Field tplRawField = clazz.getDeclaredField("$tpl$0$raw");
        assertThat(tplField).isNotNull();
        assertThat(tplRawField).isNotNull();
        assertThat(tplField.getType()).isEqualTo(String[].class);
        assertThat(tplRawField.getType()).isEqualTo(TemplateStringsArray.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRawStringLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
                namespace com {
                  export class A {
                    tag(strings: TemplateStringsArray, a: String, b: String): int {
                      return strings.length
                    }
                    test(): int {
                      return this.tag`start ${"mid"} end ${"last"}`
                    }
                  }
                }""");
        int result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(3);  // 3 template parts: "start ", " end ", ""
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRawStringMultipleParts(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
                namespace com {
                  export class A {
                    tag(strings: TemplateStringsArray, value: String): String {
                      return strings.raw[0] + "|" + strings.raw[1]
                    }
                    test(): String {
                      return this.tag`part1 ${"X"} part2`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("part1 | part2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRawStringStandaloneFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
                namespace com {
                  export function tag(strings: TemplateStringsArray, value: String): String {
                    return strings.get(0) + "|" + strings.raw[0] + "|" + value
                  }
                  export class A {
                    test(): String {
                      return tag`Greet ${"You"}`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Greet |Greet |You");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRawStringWithEscapeSequence(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
                namespace com {
                  export class A {
                    tag(strings: TemplateStringsArray): String {
                      // Cooked has newline processed, raw preserves \\n as two chars
                      return "cooked:[" + strings.get(0) + "],raw:[" + strings.raw[0] + "]"
                    }
                    test(): String {
                      return this.tag`line1\\nline2`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        // Cooked string has actual newline, raw has backslash-n as literal characters
        assertThat(result).isEqualTo("cooked:[line1\nline2],raw:[line1\\nline2]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRawStringWithTab(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { TemplateStringsArray } from "com.caoccao.javet.swc4j.compiler.jdk17.ast.utils"
                namespace com {
                  export class A {
                    tag(strings: TemplateStringsArray): String {
                      // Cooked has tab processed, raw preserves \\t as two chars
                      return "cooked:[" + strings.get(0) + "],raw:[" + strings.raw[0] + "]"
                    }
                    test(): String {
                      return this.tag`a\\tb`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        // Cooked string has actual tab, raw has backslash-t as literal characters
        assertThat(result).isEqualTo("cooked:[a\tb],raw:[a\\tb]");
    }
}
