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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("[a-zA-Z0-9_\\-.+]", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("pattern", pattern.pattern());
        assertEquals(Pattern.CASE_INSENSITIVE, pattern.flags());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("((((((((((a))))))))))", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        // Empty non-capturing group
        assertEquals("(?:)", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Object result = classA.getMethod("test").invoke(instance);
        assertInstanceOf(Pattern.class, result);
        Pattern pattern = (Pattern) result;
        assertEquals("pattern", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("test", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("a").matches());
        assertTrue(pattern.matcher("z").matches());
        assertFalse(pattern.matcher("1").matches());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("third", pattern.pattern());
        assertEquals(Pattern.MULTILINE, pattern.flags());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("[^a-z]", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("second", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("pattern", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("[/]", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("test", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern pattern = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(pattern);
        assertEquals("pattern", pattern.pattern());
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
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        Pattern result = (Pattern) classA.getMethod("test").invoke(instance);
        assertNotNull(result);
        assertEquals(pattern.toString(), result.pattern());
    }
}
