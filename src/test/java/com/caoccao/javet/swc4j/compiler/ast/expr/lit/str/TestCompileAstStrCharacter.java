/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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
 * Tests for string-to-Character (boxed) conversion.
 * Phase 5: Character (Boxed) Conversion (8 tests)
 */
public class TestCompileAstStrCharacter extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterConstAssignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      const c: Character = 'Z'
                      return c
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('Z');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterDigit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return '9'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('9');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterEmptyString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return ''
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('\0');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterMultiCharacterString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return 'XYZ'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('X');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterNull(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return '\\0'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('\0');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCharacterSingleCharacter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return 'A'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('A');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnCharacterObjectWithAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Character = 'a'
                      return a
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('a');
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testReturnCharacterObjectWithAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Character {
                      return 'a'
                    }
                  }
                }""");
        assertThat((char) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo('a');
    }
}
