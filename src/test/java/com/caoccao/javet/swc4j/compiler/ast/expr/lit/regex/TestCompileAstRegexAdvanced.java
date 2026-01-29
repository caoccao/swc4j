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
 * Tests for advanced regex features.
 * Phase 3: Advanced Features (15 tests)
 */
public class TestCompileAstRegexAdvanced extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexAtomicGroup(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?>abc)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?>abc)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexBackreference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(a)\\1/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(a)\\1");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexComplexPatternEmail(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.matcher("test@example.com").matches()).isTrue();
        assertThat(pattern.matcher("invalid@").matches()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexLineAnchors(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testStart(): Pattern { return /^/m }
                    testEnd(): Pattern { return /$/m }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        Pattern startPattern = instanceRunner.invoke("testStart");
        Pattern endPattern = instanceRunner.invoke("testEnd");
        assertThat(startPattern.pattern()).isEqualTo("^");
        assertThat(endPattern.pattern()).isEqualTo("$");
        assertThat(startPattern.flags()).isEqualTo(Pattern.MULTILINE);
        assertThat(endPattern.flags()).isEqualTo(Pattern.MULTILINE);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexLookahead(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?=pattern)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?=pattern)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexLookbehind(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<=pattern)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<=pattern)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNamedBackreference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<name>a)\\k<name>/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<name>a)\\k<name>");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNamedGroup(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<name>\\d+)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<name>\\d+)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNegativeLookahead(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?!pattern)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?!pattern)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNegativeLookbehind(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?<!pattern)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("(?<!pattern)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNonWordBoundary(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\B/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\B");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexPossessiveQuantifier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /a++/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("a++");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexUnicodeProperty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\p{L}/u
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\p{L}");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexWordBoundary(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\b/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(pattern).isNotNull();
        assertThat(pattern.pattern()).isEqualTo("\\b");
    }
}
