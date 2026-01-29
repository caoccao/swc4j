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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for basic regex patterns.
 * Phase 1: Basic Patterns (15 tests)
 */
public class TestCompileAstRegexBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexAnchors(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /^start/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("^start", pattern.pattern());
        assertEquals(0, pattern.flags());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexCharacterClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /[a-z0-9]/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("[a-z0-9]", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDigits(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\d+/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("\\d+", pattern.pattern());
        assertEquals(0, pattern.flags());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDot(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /./
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals(".", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexEscapes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\n\\t\\r/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("\\n\\t\\r", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexGroups(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(abc)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("(abc)", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexHexEscape(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\x41/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("\\x41", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNegatedClass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /[^a-z]/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("[^a-z]", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNonCapturingGroup(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /(?:abc)/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("(?:abc)", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexQuantifiers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    testStar(): Pattern { return /a*/ }
                    testPlus(): Pattern { return /a+/ }
                    testQuestion(): Pattern { return /a?/ }
                    testExact(): Pattern { return /a{3}/ }
                    testRange(): Pattern { return /a{2,5}/ }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals("a*", ((Pattern) instanceRunner.invoke("testStar")).pattern());
        assertEquals("a+", ((Pattern) instanceRunner.invoke("testPlus")).pattern());
        assertEquals("a?", ((Pattern) instanceRunner.invoke("testQuestion")).pattern());
        assertEquals("a{3}", ((Pattern) instanceRunner.invoke("testExact")).pattern());
        assertEquals("a{2,5}", ((Pattern) instanceRunner.invoke("testRange")).pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexSimple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /abc/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("abc", pattern.pattern());
        assertEquals(0, pattern.flags());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexUnicodeEscape(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\u0041/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("\\u0041", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexWhitespace(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\s+/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("\\s+", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexWords(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\w+/
                    }
                  }
                }""");
        Pattern pattern = runner.createInstanceRunner("com.A").invoke("test");
        assertNotNull(pattern);
        assertEquals("\\w+", pattern.pattern());
    }
}
