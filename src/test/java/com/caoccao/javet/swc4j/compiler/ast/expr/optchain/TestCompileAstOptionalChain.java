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

package com.caoccao.javet.swc4j.compiler.ast.expr.optchain;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstOptionalChain extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOptionalCallShortCircuit(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test(): Array<Object> {
                      const list: ArrayList = null
                      const list2: ArrayList = [1]
                      const r1 = list?.add(1)
                      const r2 = list2?.add(2)
                      const len = list2.length
                      return [r1 == null, r2, len]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test"))
                .isEqualTo(List.of(true, true, 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOptionalDirectCall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Array<Object> {
                      const fn: IntUnaryOperator = null
                      const fn2: IntUnaryOperator = (x: int) => x + 1
                      const r1 = fn?.(2)
                      const r2 = fn2?.(2)
                      return [r1 == null, r2]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test"))
                .isEqualTo(List.of(true, 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOptionalMemberAccess(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<Object> {
                      const obj: Record<string, int> = { x: 1, y: 2 }
                      const none: Record<string, int> = null
                      const r1 = none?.x
                      const r2 = obj?.x
                      const r3 = obj?.y
                      return [r1 == null, r2, r3]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test"))
                .isEqualTo(List.of(true, 1, 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOptionalMethodCall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test(): Array<Object> {
                      const list: ArrayList = null
                      const list2: ArrayList = [1, 2, 3]
                      const r1 = list?.size()
                      const r2 = list2?.size()
                      return [r1 == null, r2]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test"))
                .isEqualTo(List.of(true, 3));
    }
}
