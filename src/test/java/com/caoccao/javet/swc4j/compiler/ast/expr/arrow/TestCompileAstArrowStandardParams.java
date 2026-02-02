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

package com.caoccao.javet.swc4j.compiler.ast.expr.arrow;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileAstArrowStandardParams extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDefaultParamOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Function } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Array<String> {
                      const fn: Function<String, String> = (x: String = "default") => x + "!"
                      return [fn(), fn("hi")]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test"))
                .isEqualTo(List.of("default!", "hi!"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testOptionalParamOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Function } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Array<String> {
                      const fn: Function<String, String> = (x?: String) => x ?? "none"
                      return [fn(), fn("ok")]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test"))
                .isEqualTo(List.of("none", "ok"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRestParamOnFunction(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Function } from 'java.util.function'
                import { Object, String } from 'java.lang'
                namespace com {
                  export class A {
                    test(): Array<String> {
                      const fn: Function<Object[], String> = (...values: Object[]) => String.valueOf(values.length)
                      return [fn(), fn(1, 2, 3)]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test"))
                .isEqualTo(List.of("0", "3"));
    }
}
