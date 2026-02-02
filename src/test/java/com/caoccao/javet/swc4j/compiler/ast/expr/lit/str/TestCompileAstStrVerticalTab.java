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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.str;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for vertical tab (\v) escape sequence conversion.
 * JavaScript supports \v, but Java does not. This tests the conversion from \v to \u000B.
 */
public class TestCompileAstStrVerticalTab extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabAtEnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "hello\\v"
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("hello\u000B");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabAtStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "\\vhello"
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("\u000Bhello");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "line1\\vline2"
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("line1\u000Bline2");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabInMiddle(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "start\\vmiddle\\vend"
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("start\u000Bmiddle\u000Bend");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const s: String = "a\\vb"
                      return s.length
                    }
                  }
                }""");
        int result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(3);  // 'a', '\u000B', 'b'
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "a\\vb\\vc"
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("a\u000Bb\u000Bc");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabOnly(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "\\v"
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("\u000B");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabToChar(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): char {
                      return '\\v'
                    }
                  }
                }""");
        char result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo('\u000B');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabToCharacter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return '\\v'
                    }
                  }
                }""");
        Character result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo('\u000B');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testVerticalTabWithOtherEscapes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      return "a\\nb\\tc\\vd"
                    }
                  }
                }""");
        String result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo("a\nb\tc\u000Bd");
    }
}
