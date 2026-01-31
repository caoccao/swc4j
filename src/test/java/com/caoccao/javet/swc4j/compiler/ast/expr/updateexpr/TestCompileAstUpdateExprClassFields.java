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

package com.caoccao.javet.swc4j.compiler.ast.expr.updateexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstUpdateExprClassFields extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceFieldPostfixIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private value: int = 5

                    test() {
                      const result = this.value++
                      return [result, this.value]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(5, 6));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceFieldPrefixIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    private value: int = 5

                    test() {
                      const result = ++this.value
                      return [result, this.value]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(6, 6));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticFieldPostfixDecrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    static value: int = 3

                    test() {
                      const result = A.value--
                      return [result, A.value]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(3, 2));
    }
}
