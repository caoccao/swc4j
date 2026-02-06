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

package com.caoccao.javet.swc4j.compiler.ast.expr.classexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for class expression inheritance behavior.
 * Covers: inheriting superclass methods, overriding superclass methods.
 */
public class TestCompileAstClassExprInheritance extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprInheritsSuperclass(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Base {
                    getValue(): int { return 100 }
                  }
                
                  export class A {
                    test(): int {
                      const obj = new (class extends Base {
                      })()
                      return obj.getValue()
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(100);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprOverridesSuperMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Base {
                    getValue(): int { return 100 }
                  }
                
                  export class A {
                    test(): int {
                      const obj = new (class extends Base {
                        getValue(): int { return 200 }
                      })()
                      return obj.getValue()
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(200);
    }
}
