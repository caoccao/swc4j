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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for tagged template literal caching optimization.
 * Verifies that template String[] arrays are cached as static final fields
 * and that identical templates share the same cached field (deduplication).
 */
public class TestCompileAstTaggedTplCaching extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateCacheDeduplication(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], value: String): String {
                      return strings[0] + value + strings[1]
                    }
                    method1(): String {
                      return this.tag`prefix ${"A"} suffix`
                    }
                    method2(): String {
                      return this.tag`prefix ${"B"} suffix`
                    }
                    test(): String {
                      return this.method1() + "|" + this.method2()
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("prefix A suffix|prefix B suffix");

        // Both methods use the same quasis ("prefix ", " suffix")
        // so they should share the same cached field $tpl$0
        Class<?> clazz = runner.getClass("com.A");
        Field tpl0 = clazz.getDeclaredField("$tpl$0");
        assertThat(tpl0).isNotNull();

        // There should be no $tpl$1 field since deduplication should have occurred
        try {
            clazz.getDeclaredField("$tpl$1");
            // If we get here, deduplication didn't work
            assertThat(false).as("Expected $tpl$1 to not exist due to deduplication").isTrue();
        } catch (NoSuchFieldException e) {
            // Expected - deduplication worked correctly
        }
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateCacheEmptyTemplate(JdkVersion jdkVersion) throws Exception {
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

        // Empty template should still create a cache entry
        Class<?> clazz = runner.getClass("com.A");
        Field tplField = clazz.getDeclaredField("$tpl$0");
        tplField.setAccessible(true);
        String[] cachedArray = (String[]) tplField.get(null);
        assertThat(cachedArray).containsExactly("");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateCacheFieldExists(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], name: String): String {
                      return strings[0] + name + strings[1]
                    }
                    test(): String {
                      return this.tag`Hello ${"World"}!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World!");

        // Verify that a static final $tpl$ field was generated
        Class<?> clazz = runner.getClass("com.A");
        Field tplField = clazz.getDeclaredField("$tpl$0");
        assertThat(tplField).isNotNull();
        assertThat(Modifier.isStatic(tplField.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(tplField.getModifiers())).isTrue();
        assertThat(tplField.getType()).isEqualTo(String[].class);

        // Verify the cached array content
        tplField.setAccessible(true);
        String[] cachedArray = (String[]) tplField.get(null);
        assertThat(cachedArray).containsExactly("Hello ", "!");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateCacheManyInterpolations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], a: String, b: String, c: String): String {
                      return strings[0] + a + strings[1] + b + strings[2] + c + strings[3]
                    }
                    test(): String {
                      return this.tag`[${"A"}|${"B"}|${"C"}]`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("[A|B|C]");

        // Template with many interpolations should have correct cache
        Class<?> clazz = runner.getClass("com.A");
        Field tplField = clazz.getDeclaredField("$tpl$0");
        tplField.setAccessible(true);
        String[] cachedArray = (String[]) tplField.get(null);
        assertThat(cachedArray).containsExactly("[", "|", "|", "]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateCacheMultipleDifferentTemplates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[], value: String): String {
                      return strings[0] + value + strings[1]
                    }
                    greet(): String {
                      return this.tag`Hello ${"World"}!`
                    }
                    farewell(): String {
                      return this.tag`Goodbye ${"World"}!`
                    }
                    test(): String {
                      return this.greet() + " " + this.farewell()
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("Hello World! Goodbye World!");

        // Different quasis should create different cache entries
        Class<?> clazz = runner.getClass("com.A");
        Field tpl0 = clazz.getDeclaredField("$tpl$0");
        Field tpl1 = clazz.getDeclaredField("$tpl$1");
        assertThat(tpl0).isNotNull();
        assertThat(tpl1).isNotNull();

        tpl0.setAccessible(true);
        tpl1.setAccessible(true);
        String[] array0 = (String[]) tpl0.get(null);
        String[] array1 = (String[]) tpl1.get(null);

        // One should be ["Hello ", "!"], the other ["Goodbye ", "!"]
        assertThat(array0).isNotEqualTo(array1);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTemplateCacheNoInterpolation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    tag(strings: String[]): String {
                      return "[" + strings[0] + "]"
                    }
                    test(): String {
                      return this.tag`Hello World!`
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("[Hello World!]");

        // Template with no interpolation should have single-element cache
        Class<?> clazz = runner.getClass("com.A");
        Field tplField = clazz.getDeclaredField("$tpl$0");
        tplField.setAccessible(true);
        String[] cachedArray = (String[]) tplField.get(null);
        assertThat(cachedArray).containsExactly("Hello World!");
    }
}
