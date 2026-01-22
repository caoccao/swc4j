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
 * Tests for escape sequences.
 * Phase 4: Escape Sequences (12 tests)
 */
public class TestCompileAstRegexEscapes extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexBackslashEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\\\/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\\\", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexCaretEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\^/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\^", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDollarEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\$/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\$", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexDotEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\./
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\.", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexNullCharacter(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\x00/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        // Hex escape for null character
        assertEquals("\\x00", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexPipeEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\|/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\|", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexPlusEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\+/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\+", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexQuestionEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\?/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\?", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexSpecialCharEscapes(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\[\\]\\(\\)\\{\\}/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\[\\]\\(\\)\\{\\}", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexStarEscape(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\*/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("\\*", pattern.pattern());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRegexVerticalTab(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Pattern {
                      return /\\v/
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        // \v should be converted to \x0B
        assertEquals("\\x0B", pattern.pattern());
    }
}
