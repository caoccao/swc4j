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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.arraylit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCompileAstArrayLitIteratorsAndStatics extends BaseTestCompileSuite {
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayKeysValuesEntries(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const keys = arr.keys()
                      const values = arr.values()
                      const entries = arr.entries()
                      const empty = []
                      return [
                        keys,
                        values,
                        entries,
                        empty.keys(),
                        empty.values(),
                        empty.entries()
                      ]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(
                        List.of(0, 1, 2),
                        List.of(1, 2, 3),
                        List.of(
                                List.of(0, 1),
                                List.of(1, 2),
                                List.of(2, 3)),
                        List.of(),
                        List.of(),
                        List.of()));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayStaticIsArrayFromOf(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const base = [1, 2]
                      const typed: int[] = [3, 4]
                      const fromList = Array.from(base)
                      const fromTyped = Array.from(typed)
                      const fromString = Array.from("hi")
                      const fromObject = Array.from({a: 1, b: 2})
                      const ofEmpty = Array.of()
                      const ofMixed = Array.of(1, "a", true)
                      const isBase = Array.isArray(base)
                      const isTyped = Array.isArray(typed)
                      const isFromObject = Array.isArray(fromObject)
                      const isString = Array.isArray("hi")
                      const isObject = Array.isArray({a: 1})
                      return [
                        fromList,
                        fromTyped,
                        fromString,
                        fromObject,
                        ofEmpty,
                        ofMixed,
                        isBase,
                        isTyped,
                        isFromObject,
                        isString,
                        isObject
                      ]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(
                        List.of(1, 2),
                        List.of(3, 4),
                        List.of("h", "i"),
                        List.of(List.of("a", 1), List.of("b", 2)),
                        List.of(),
                        List.of(1, "a", true),
                        true,
                        true,
                        true,
                        false,
                        false));
    }
}
