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
 * Tests for mutating array methods (pop, push, shift, unshift, reverse, sort, splice).
 */
public class TestCompileAstArrayLitMutating extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const removed = arr.pop()
                      return removed
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPopChangesLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30, 40]
                      arr.pop()
                      return arr.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPopMultipleTimes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [5, 10, 15, 20, 25]
                      arr.pop()
                      arr.pop()
                      const last = arr.pop()
                      return last
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPopReturnsCorrectType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["hello", "world", "test"]
                      return arr.pop()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("test");
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPopSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      const val = arr.pop()
                      return val
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPopSingleElementLeavesEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [99]
                      arr.pop()
                      return arr.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPopWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, "hello", true]
                      arr.pop()
                      arr.pop()
                      return arr.pop()
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPush(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2]
                      arr.push(3)
                      return arr.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPushAndPop(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2]
                      arr.push(3)
                      arr.push(4)
                      const last = arr.pop()
                      return last
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(4);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayPushAndShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2]
                      arr.push(3)
                      arr.push(4)
                      const first = arr.shift()
                      return first
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.reverse()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(5, 4, 3, 2, 1));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverseAfterModification(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2]
                      arr.push(3)
                      arr.unshift(0)
                      arr.reverse()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(3, 2, 1, 0));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverseEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = []
                      arr.reverse()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverseMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, "hello", true, 42]
                      arr.reverse()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(42, true, "hello", 1));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverseReturnsArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      const result = arr.reverse()
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(30, 20, 10));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverseSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      arr.reverse()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(42));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverseStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["apple", "banana", "cherry"]
                      arr.reverse()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("cherry", "banana", "apple"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayReverseTwice(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [100, 200, 300, 400]
                      arr.reverse()
                      arr.reverse()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(100, 200, 300, 400));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      const removed = arr.shift()
                      return removed
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShiftAndRemainingElements(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [100, 200, 300]
                      arr.shift()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(200, 300));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShiftChangesLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30, 40]
                      arr.shift()
                      return arr.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(3);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShiftMultipleTimes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [5, 10, 15, 20, 25]
                      arr.shift()
                      arr.shift()
                      const first = arr.shift()
                      return first
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(15);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShiftReturnsCorrectType(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["hello", "world", "test"]
                      return arr.shift()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo("hello");
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShiftSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      const val = arr.shift()
                      return val
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(42);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShiftSingleElementLeavesEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [99]
                      arr.shift()
                      return arr.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(0);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShiftWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, "hello", true]
                      arr.shift()
                      arr.shift()
                      return arr.shift()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayShrink(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      arr.length = 2
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(10, 20));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySort(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [5, 2, 8, 1, 9]
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 5, 8, 9));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortAfterModification(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 4]
                      arr.push(2)
                      arr.unshift(5)
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortAlreadySorted(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.pop()
                      arr.pop()
                      arr.pop()
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortReturnsArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 4, 1, 5]
                      const result = arr.sort()
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 1, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortReverseSorted(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [5, 4, 3, 2, 1]
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortSingleElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [42]
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(42));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["zebra", "apple", "mango", "banana"]
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("apple", "banana", "mango", "zebra"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySortWithDuplicates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [3, 1, 4, 1, 5, 9, 2, 6, 5]
                      arr.sort()
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 1, 2, 3, 4, 5, 5, 6, 9));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySplice(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.splice(1, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceDeleteAll(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      const removed = arr.splice(0, 10)
                      return removed
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceEmpty(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.pop()
                      arr.pop()
                      arr.pop()
                      return arr.splice(0, 1)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceInsertWithoutDelete(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.splice(1, 0, 10, 20)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 10, 20, 2, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceMutates(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.splice(1, 2)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceNegativeStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.splice(-3, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(3, 4));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceNoArgs(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      return arr.splice()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceOnlyStart(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      return arr.splice(2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      return arr.splice(10, 2)
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of());
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceReturnsRemoved(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      const removed = arr.splice(2, 2)
                      return removed
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(3, 4));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceSingleItem(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.splice(1, 1, 99)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 99, 3));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceStrings(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["a", "b", "c", "d", "e"]
                      arr.splice(1, 2, "x", "y")
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("a", "x", "y", "d", "e"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArraySpliceWithItems(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3, 4, 5]
                      arr.splice(1, 2, 10, 20, 30)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 10, 20, 30, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [2, 3, 4]
                      arr.unshift(1)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftAndPush(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [2, 3]
                      arr.unshift(1)
                      arr.push(4)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftAndShift(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [2, 3, 4]
                      arr.unshift(1)
                      const first = arr.shift()
                      return first
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(1);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftChangesLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      arr.unshift(0)
                      return arr.length
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(4);
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftMultipleTimes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [4, 5]
                      arr.unshift(3)
                      arr.unshift(2)
                      arr.unshift(1)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(1, 2, 3, 4, 5));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftOnEmptyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = []
                      arr.unshift(42)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(42));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftPreservesOrder(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [100, 200, 300]
                      arr.unshift(50)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(50, 100, 200, 300));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["world"]
                      arr.unshift("hello")
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of("hello", "world"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayUnshiftWithMixedTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = ["test", true]
                      arr.unshift(99)
                      return arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test")).isEqualTo(List.of(99, "test", true));
    }
}
