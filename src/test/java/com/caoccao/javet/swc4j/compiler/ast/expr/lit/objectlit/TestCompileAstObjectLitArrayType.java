/*
 * Copyright (c) 2026-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.objectlit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCompileAstObjectLitArrayType extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordIntegerArrayString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<int, Array<String>> {
                      const data: Record<int, Array<String>> = {
                        0: ["first", "entry"],
                        1: ["second", "entry"],
                        2: ["third"]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<Integer, ArrayList<String>> map = (Map<Integer, ArrayList<String>>) result;
        assertThat(map).containsKeys(0, 1, 2);
        assertThat(map.get(0)).containsExactly("first", "entry");
        assertThat(map.get(1)).containsExactly("second", "entry");
        assertThat(map.get(2)).containsExactly("third");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringArrayBoolean(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<boolean>> {
                      const data: Record<string, Array<boolean>> = {
                        flags: [true, false, true],
                        states: [false, false]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Boolean>> map = (Map<String, ArrayList<Boolean>>) result;
        assertThat(map).containsKeys("flags", "states");
        assertThat(map.get("flags")).containsExactly(true, false, true);
        assertThat(map.get("states")).containsExactly(false, false);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringArrayDouble(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<double>> {
                      const data: Record<string, Array<double>> = {
                        prices: [19.99, 29.99, 39.99],
                        rates: [0.05, 0.10, 0.15]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Double>> map = (Map<String, ArrayList<Double>>) result;
        assertThat(map).containsKeys("prices", "rates");
        assertThat(map.get("prices")).containsExactly(19.99, 29.99, 39.99);
        assertThat(map.get("rates")).containsExactly(0.05, 0.10, 0.15);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringArrayLong(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<long>> {
                      const val1: long = 100
                      const val2: long = 200
                      const val3: long = 300
                      const val4: long = 400
                      const data: Record<string, Array<long>> = {
                        ids: [val1, val2],
                        values: [val3, val4]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Long>> map = (Map<String, ArrayList<Long>>) result;
        assertThat(map).containsKeys("ids", "values");
        assertThat(map.get("ids")).containsExactly(100L, 200L);
        assertThat(map.get("values")).containsExactly(300L, 400L);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringArrayNumber(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<number>> {
                      const data: Record<string, Array<number>> = {
                        numbers: [1, 2, 3],
                        scores: [95, 87, 92]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Integer>> map = (Map<String, ArrayList<Integer>>) result;
        assertThat(map).containsKeys("numbers", "scores");
        assertThat(map.get("numbers")).containsExactly(1, 2, 3);
        assertThat(map.get("scores")).containsExactly(95, 87, 92);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringArrayString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<String>> {
                      const data: Record<string, Array<String>> = {
                        names: ["Alice", "Bob", "Charlie"],
                        cities: ["New York", "London"]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<String>> map = (Map<String, ArrayList<String>>) result;
        assertThat(map).containsKeys("names", "cities");
        assertThat(map.get("names")).containsExactly("Alice", "Bob", "Charlie");
        assertThat(map.get("cities")).containsExactly("New York", "London");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringArrayWithComputedValues(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<number>> {
                      const base: int = 10
                      const data: Record<string, Array<number>> = {
                        computed: [base * 1, base * 2, base * 3]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Integer>> map = (Map<String, ArrayList<Integer>>) result;
        assertThat(map).containsKeys("computed");
        assertThat(map.get("computed")).containsExactly(10, 20, 30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringArrayWithVariables(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<number>> {
                      const value1: int = 10
                      const value2: int = 20
                      const value3: int = 30
                      const data: Record<string, Array<number>> = {
                        values: [value1, value2, value3]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Integer>> map = (Map<String, ArrayList<Integer>>) result;
        assertThat(map).containsKeys("values");
        assertThat(map.get("values")).containsExactly(10, 20, 30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringEmptyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<number>> {
                      const data: Record<string, Array<number>> = {
                        empty: [],
                        filled: [42]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Integer>> map = (Map<String, ArrayList<Integer>>) result;
        assertThat(map).containsKeys("empty", "filled");
        assertThat(map.get("empty")).isEmpty();
        assertThat(map.get("filled")).containsExactly(42);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testRecordStringNestedArrays(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Record<string, Array<Array<number>>> {
                      const data: Record<string, Array<Array<number>>> = {
                        matrix: [[1, 2], [3, 4], [5, 6]],
                        grid: [[10, 20], [30, 40]]
                      }
                      return data
                    }
                  }
                }""");

        var instanceRunner = runner.createInstanceRunner("com.A");
        Object result = instanceRunner.invoke("test");
        assertThat(result).isInstanceOf(LinkedHashMap.class);

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<ArrayList<Integer>>> map = (Map<String, ArrayList<ArrayList<Integer>>>) result;
        assertThat(map).containsKeys("matrix", "grid");
        assertThat(map.get("matrix")).hasSize(3);
        assertThat(map.get("matrix").get(0)).containsExactly(1, 2);
        assertThat(map.get("matrix").get(1)).containsExactly(3, 4);
        assertThat(map.get("matrix").get(2)).containsExactly(5, 6);
        assertThat(map.get("grid").get(0)).containsExactly(10, 20);
        assertThat(map.get("grid").get(1)).containsExactly(30, 40);
    }
}
