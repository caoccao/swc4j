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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.bool;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for Boolean (boxed) conversion.
 * Phase 2: Boolean (Boxed) Conversion (10 tests)
 */
public class TestCompileAstBoolBoxed extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedAnnotationOnConst(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: Boolean = true
                      return flag
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedAnnotationOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      return true
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedConstFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      const flag: Boolean = false
                      return flag
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedConstTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      const flag: Boolean = true
                      return flag
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      return false
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedFalseMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      const a: Boolean = false
                      const b: Boolean = false
                      return a
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedMixed(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      const a: Boolean = true
                      const b: Boolean = false
                      return a
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedReturnFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      const a: Boolean = true
                      const b: Boolean = false
                      return b
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      return true
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedTrueMultiple(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      const a: Boolean = true
                      const b: Boolean = true
                      return a
                    }
                  }
                }""");
        assertThat((Boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }
}
