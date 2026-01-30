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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCompileAstArrayLitFunctionalMethods extends BaseTestCompileSuite {
    private List<Object> buildExpectedFindList() {
        List<Object> expected = new ArrayList<>(List.of(3, 0, 1, -1, true, true, false, true));
        expected.set(1, null);
        return expected;
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFindFindIndexSomeEvery(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const found = arr.find(x => x > 2)
                      const notFound = arr.find(x => x > 5)
                      const index = arr.findIndex(x => x == 2)
                      const missing = arr.findIndex(x => x == 9)
                      const some = arr.some(x => x > 2)
                      const every = arr.every(x => x > 0)
                      const empty = []
                      const emptySome = empty.some(x => x == 1)
                      const emptyEvery = empty.every(x => x == 1)
                      return [found, notFound, index, missing, some, every, emptySome, emptyEvery]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(buildExpectedFindList());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayFlatFlatMap(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const nested = [1, [2, [3]], 4]
                      const flat1 = nested.flat()
                      const flat2 = nested.flat(2)
                      const flatMap = [1, 2].flatMap(x => [x, x + 10])
                      return [flat1, flat2, flatMap]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(
                        List.of(1, 2, List.of(3), 4),
                        List.of(1, 2, 3, 4),
                        List.of(1, 11, 2, 12)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayForEachMapFilter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      let sum = 0
                      arr.forEach(x => {
                        sum = sum + x
                      })
                      const mapped = arr.map(x => x * 2)
                      const filtered = arr.filter(x => x % 2 == 1)
                      return [sum, mapped, filtered]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(6, List.of(2, 4, 6), List.of(1, 3)));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReduceReduceRight(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const sum = arr.reduce((acc, x) => acc + x, 0)
                      const sumNoInit = arr.reduce((acc, x) => acc + x)
                      const right = arr.reduceRight((acc, x) => acc - x, 0)
                      return [sum, sumNoInit, right]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(6, 6, -6));
    }
}
