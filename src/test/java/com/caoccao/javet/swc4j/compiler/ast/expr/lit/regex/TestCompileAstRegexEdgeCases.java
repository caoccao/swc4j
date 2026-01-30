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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.regex;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for edge cases and variable contexts.
 * Phase 5: Edge Cases + Phase 6: Variable Contexts
 */
public class TestCompileAstRegexEdgeCases extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexComplexCharClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /[a-zA-Z0-9_\\-.+]/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("[a-zA-Z0-9_\\-.+]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      const r: Pattern = /pattern/i
                      return r
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("pattern");
        assertThat(pattern.flags()).isEqualTo(Pattern.CASE_INSENSITIVE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDeeplyNestedGroups(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /((((((((((a))))))))))/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("((((((((((a))))))))))");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexEmptyPattern(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?:)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        // Empty non-capturing group
        assertThat(pattern.pattern()).isEqualTo("(?:)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexInferredType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return /pattern/
                    }
                  }
                }""");
        Object result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isInstanceOf(Pattern.class);
        Pattern pattern = (Pattern) result;
        assertThat(pattern.pattern()).isEqualTo("pattern");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexLet(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      let r: Pattern = /test/
                      return r
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("test");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexManyAlternations(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.matcher("a").matches()).isTrue();
        assertThat(pattern.matcher("z").matches()).isTrue();
        assertThat(pattern.matcher("1").matches()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexMultipleRegex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      const r1 = /first/
                      const r2 = /second/i
                      const r3 = /third/m
                      return r3
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("third");
        assertThat(pattern.flags()).isEqualTo(Pattern.MULTILINE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNegatedCharClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /[^a-z]/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("[^a-z]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexReassignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      let r = /first/
                      r = /second/
                      return r
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("second");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /pattern/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("pattern");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexSlashInPattern(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /[/]/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("[/]");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexTypeAnnotation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      const r: Pattern = /test/
                      return r
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("test");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      const r = /pattern/
                      return r
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("pattern");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVeryLongPattern(JdkVersion jdkVersion) throws Exception {
        // Create a very long pattern (100+ characters)
        StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            pattern.append("a");
        }
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /%s/
                    }
                  }
                }""".formatted(pattern.toString()));
        Pattern result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(result.pattern()).isEqualTo(pattern.toString());
    }
}
