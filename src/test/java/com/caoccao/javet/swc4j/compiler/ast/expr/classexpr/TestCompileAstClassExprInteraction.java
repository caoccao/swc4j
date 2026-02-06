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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests for class expression instances interacting with each other.
 * Covers: result chaining between instances, pipeline chains, feeding output across instances.
 */
public class TestCompileAstClassExprInteraction extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprAccumulateAcrossInstances(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a = new (class {
                        value: int = 10
                        get() { return this.value }
                      })()
                      const b = new (class {
                        value: int = 20
                        get() { return this.value }
                      })()
                      const c = new (class {
                        value: int = 30
                        get() { return this.value }
                      })()
                      return a.get() + b.get() + c.get()
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(60);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprCrossInstanceFieldAccess(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const point = new (class {
                        x: int = 3
                        y: int = 4
                      })()
                      const math = new (class {
                        square(n: int) { return n * n }
                        add(a: int, b: int) { return a + b }
                      })()
                      return [
                        math.square(point.x),
                        math.square(point.y),
                        math.add(math.square(point.x), math.square(point.y))
                      ]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(9, 16, 25));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprInstanceFeedingResults(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const calc = new (class {
                        add(a: int, b: int): int { return a + b }
                        mul(a: int, b: int): int { return a * b }
                      })()
                      const fmt = new (class {
                        negate(x: int): int { return -x }
                        double(x: int): int { return x * 2 }
                      })()
                      return [
                        fmt.double(calc.add(3, 4)),
                        fmt.negate(calc.mul(5, 6))
                      ]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(14, -30));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprNestedMethodCalls(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const encoder = new (class {
                        encode(x: int): int { return x * 2 + 1 }
                      })()
                      const decoder = new (class {
                        decode(x: int): int { return (x - 1) / 2 }
                      })()
                      const original: int = 42
                      const encoded: int = encoder.encode(original)
                      const decoded: int = decoder.decode(encoded)
                      return [original, encoded, decoded]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(42, 85, 42));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprPipelineChain(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const step1 = new (class {
                        process(x: int): int { return x + 10 }
                      })()
                      const step2 = new (class {
                        process(x: int): int { return x * 2 }
                      })()
                      const step3 = new (class {
                        process(x: int): int { return x - 3 }
                      })()
                      return step3.process(step2.process(step1.process(5)))
                    }
                  }
                }""");
        // 5 + 10 = 15, 15 * 2 = 30, 30 - 3 = 27
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(27);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprResultChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      const adder = new (class {
                        add(a: int, b: int): int { return a + b }
                      })()
                      const multiplier = new (class {
                        mul(a: int, b: int): int { return a * b }
                      })()
                      return multiplier.mul(adder.add(2, 3), adder.add(4, 6))
                    }
                  }
                }""");
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(50);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassExprResultStoredAndReused(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<int> {
                      const producer = new (class {
                        produce(seed: int): int { return seed * 7 }
                      })()
                      const consumer = new (class {
                        consume(value: int): int { return value + 1 }
                      })()
                      const v1: int = producer.produce(1)
                      const v2: int = producer.produce(2)
                      return [consumer.consume(v1), consumer.consume(v2)]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of(8, 15));
    }
}
