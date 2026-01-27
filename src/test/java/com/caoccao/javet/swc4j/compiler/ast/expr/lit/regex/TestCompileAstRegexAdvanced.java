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

import static org.junit.jupiter.api.Assertions.*;

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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(?>abc)", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(a)\\1", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("test@example.com").matches());
        assertFalse(pattern.matcher("invalid@").matches());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern startPattern = (Pattern) classA.getMethod("testStart").invoke(instance);
        Pattern endPattern = (Pattern) classA.getMethod("testEnd").invoke(instance);
        assertEquals("^", startPattern.pattern());
        assertEquals("$", endPattern.pattern());
        assertEquals(Pattern.MULTILINE, startPattern.flags());
        assertEquals(Pattern.MULTILINE, endPattern.flags());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(?=pattern)", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(?<=pattern)", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(?<name>a)\\k<name>", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(?<name>\\d+)", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(?!pattern)", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("(?<!pattern)", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\B", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("a++", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\p{L}", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\b", pattern.pattern());
    }
}
