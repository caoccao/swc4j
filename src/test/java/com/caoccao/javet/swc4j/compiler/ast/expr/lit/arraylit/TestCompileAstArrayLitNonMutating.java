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


/**
 * Tests for non-mutating array methods (toReversed, toSorted, toSpliced, with).
 */
public class TestCompileAstArrayLitNonMutating extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedAfterModification(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.push(4)
                      arr.unshift(0)
                      return arr.toReversed()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(4, 3, 2, 1, 0));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.toReversed()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(5, 4, 3, 2, 1));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedDoesNotMutateOriginal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const reversed = arr.toReversed()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = []
                      return arr.toReversed()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedMethodChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 2]
                      return arr.toReversed().sort()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, "hello", true]
                      return arr.toReversed()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(true, "hello", 1));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedReturnsNewArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      return arr.toReversed()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(30, 20, 10));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      return arr.toReversed()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(42));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToReversedStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["hello", "world", "test"]
                      return arr.toReversed()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("test", "world", "hello"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedAlreadySorted(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 4, 2, 5]
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedDoesNotMutateOriginal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 2]
                      const sorted = arr.toSorted()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(3, 1, 2));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = []
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedMethodChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 2]
                      return arr.toSorted().reverse()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(3, 2, 1));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedReturnsNewArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [5, 3, 1, 4, 2]
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedReverseSorted(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [5, 4, 3, 2, 1]
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(42));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["cherry", "apple", "banana"]
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("apple", "banana", "cherry"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSortedWithDuplicates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 4, 1, 5, 9, 2, 6, 5]
                      return arr.toSorted()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 1, 2, 3, 4, 5, 5, 6, 9));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.toSpliced(2, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedDoesNotMutateOriginal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      const spliced = arr.toSpliced(1, 2)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = []
                      return arr.toSpliced(0, 0)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedInsertItems(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 5]
                      return arr.toSpliced(2, 0, 3, 4)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedInsertWhileDeleting(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.toSpliced(2, 2, 99, 100)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 99, 100, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedMethodChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 4, 2, 5]
                      return arr.toSpliced(1, 2).sort()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(2, 3, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedNegativeStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.toSpliced(-2, 1)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedNoArguments(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      return arr.toSpliced()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedOnlyStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.toSpliced(2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      return arr.toSpliced(10, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedReturnsNewArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30, 40]
                      return arr.toSpliced(1, 2, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(10, 99, 40));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      return arr.toSpliced(0, 1, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(99));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayToSplicedStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["hello", "world", "test"]
                      return arr.toSpliced(1, 1, "beautiful")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("hello", "beautiful", "test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithBasic(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.with(2, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 99, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithDoesNotMutateOriginal(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const modified = arr.with(1, 99)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = []
                      return arr.with(0, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithFirstElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      return arr.with(0, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(99, 20, 30));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithLastElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30, 40]
                      return arr.with(3, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(10, 20, 30, 99));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithMethodChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 2]
                      return arr.with(0, 10).sort()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 10));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, "hello", true]
                      return arr.with(1, "world")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, "world", true));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithNegativeIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.with(-2, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 99, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithNegativeIndexLast(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      return arr.with(-1, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(10, 20, 99));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      return arr.with(10, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithReturnsNewArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      return arr.with(1, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(10, 99, 30));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      return arr.with(0, 99)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(99));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayWithStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["hello", "world", "test"]
                      return arr.with(1, "universe")
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("hello", "universe", "test"));
    }
}
