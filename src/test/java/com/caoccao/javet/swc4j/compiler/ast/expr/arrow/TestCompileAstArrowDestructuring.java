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
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for arrow expressions with destructuring parameters.
 * Edge cases 11-13 from the implementation plan.
 */
public class TestCompileAstArrowDestructuring extends BaseTestCompileSuite {

    @Override
    protected ByteCodeCompiler getCompiler(JdkVersion jdkVersion) {
        Map<String, String> typeAliases = new HashMap<>();
        typeAliases.put("List", "java.util.List");
        typeAliases.put("Map", "java.util.Map");
        typeAliases.put("String", "java.lang.String");
        return ByteCodeCompiler.of(ByteCodeCompilerOptions.builder()
                .jdkVersion(jdkVersion)
                .typeAliasMap(typeAliases)
                .debug(true)
                .build());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayDestructuringBasic(JdkVersion jdkVersion) throws Exception {
        // Edge case 12: Array destructuring parameter [first, second] with IIFE
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: List<int> = [10, 20]
                      const result: int = (([first, second]: List<int>): int => {
                        const a: int = first
                        const b: int = second
                        return a + b
                      })(arr)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat((int) result).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayDestructuringMultipleElements(JdkVersion jdkVersion) throws Exception {
        // Array destructuring with more than two elements
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: List<int> = [1, 2, 3]
                      const result: int = (([a, b, c]: List<int>): int => {
                        const x: int = a
                        const y: int = b
                        const z: int = c
                        return x + y + z
                      })(arr)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat((int) result).isEqualTo(6);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayDestructuringRest(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const arr: List<int> = [1, 2, 3]
                      const result = (([first, ...rest]: List<int>): Array<int> => {
                        return [first, rest.length, rest[0]]
                      })(arr)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(List.of(1, 2, 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayDestructuringWithCapture(JdkVersion jdkVersion) throws Exception {
        // Array destructuring with closure capture
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const multiplier: int = 2
                      const arr: List<int> = [10, 20]
                      const result: int = (([a, b]: List<int>): int => {
                        const x: int = a
                        const y: int = b
                        return (x + y) * multiplier
                      })(arr)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat((int) result).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDestructuringObjectStrings(JdkVersion jdkVersion) throws Exception {
        // Object destructuring with strings
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const map: Map<String, String> = { first: "John", last: "Doe" }
                      const result: String = (({first, last}: Map<String, String>): String => {
                        return first + " " + last
                      })(map)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat(result).isEqualTo("John Doe");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDestructuringStrings(JdkVersion jdkVersion) throws Exception {
        // Array destructuring with strings
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: List<String> = ["Hello", " World"]
                      const result: String = (([first, second]: List<String>): String => {
                        return first + second
                      })(arr)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat(result).isEqualTo("Hello World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedDestructuringAndRegularParams(JdkVersion jdkVersion) throws Exception {
        // Mixed: regular param + destructuring param
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const arr: List<int> = [5, 7]
                      const result: int = ((multiplier: int, [a, b]: List<int>): int => {
                        const x: int = a
                        const y: int = b
                        return multiplier * (x + y)
                      })(3, arr)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat((int) result).isEqualTo(36);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedObjectDestructuring(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const map: Map<String, Object> = { point: { x: 3, y: 4 } }
                      const result: String = (({point: {x, y}}: Map<String, Object>): String => {
                        return x + y
                      })(map)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo("34");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectDestructuringBasic(JdkVersion jdkVersion) throws Exception {
        // Edge case 11: Object destructuring parameter {x, y}
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const map: Map<String, int> = { x: 10, y: 20 }
                      const result: int = (({x, y}: Map<String, int>): int => {
                        const a: int = x
                        const b: int = y
                        return a + b
                      })(map)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat((int) result).isEqualTo(30);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectDestructuringRest(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Map } from 'java.util'
                namespace com {
                  export class A {
                    test(): Map<String, int> {
                      const map: Map<String, int> = { x: 10, y: 20, z: 30 }
                      const result = (({x, ...rest}: Map<String, int>): Map<String, int> => {
                        return rest
                      })(map)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Object>invoke("test")).isEqualTo(Map.of("y", 20, "z", 30));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectDestructuringWithCapture(JdkVersion jdkVersion) throws Exception {
        // Object destructuring with closure capture
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const offset: int = 100
                      const map: Map<String, int> = { x: 10, y: 20 }
                      const result: int = (({x, y}: Map<String, int>): int => {
                        const a: int = x
                        const b: int = y
                        return a + b + offset
                      })(map)
                      return result
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        var result = instanceRunner.invoke("test");
        assertThat((int) result).isEqualTo(130);
    }
}
