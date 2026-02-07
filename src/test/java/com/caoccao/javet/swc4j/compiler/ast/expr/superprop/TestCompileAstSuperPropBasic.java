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

package com.caoccao.javet.swc4j.compiler.ast.expr.superprop;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestCompileAstSuperPropBasic extends BaseTestCompileSuite {
    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperPropertyAssignmentInConstructorAndMethod(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 1
                  }
                  export class B extends A {
                    assignedInCtor: int = 0
                
                    constructor() {
                      super()
                      this.assignedInCtor = super.value = 9
                    }
                
                    readAssignedInCtor(): int {
                      return this.assignedInCtor
                    }
                
                    assignInMethod(): int {
                      return super.value = 21
                    }
                
                    readSuper(): int {
                      return super.value
                    }
                
                    readThis(): int {
                      return this.value
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.B");

        assertThat(
                SimpleMap.<String, Object>of(
                        "initial", List.of(
                                instanceRunner.invoke("readAssignedInCtor"),
                                instanceRunner.invoke("readSuper"),
                                instanceRunner.invoke("readThis")),
                        "assignResult", instanceRunner.invoke("assignInMethod"),
                        "afterAssign", List.of(
                                instanceRunner.invoke("readSuper"),
                                instanceRunner.invoke("readThis"))
                )
        ).isEqualTo(
                SimpleMap.<String, Object>of(
                        "initial", List.of(9, 9, 9),
                        "assignResult", 21,
                        "afterAssign", List.of(21, 21))
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperPropertyComputedDynamicUnsupported(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 1
                  }
                  export class B extends A {
                    test(): int {
                      let key = "value"
                      return super[key]
                    }
                  }
                }"""))
                .hasMessageContaining("Failed to generate method: test")
                .hasRootCauseMessage("Computed super property expressions not yet supported");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperPropertyComputedStringLiteral(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 4
                    count: int = 1
                  }
                  export class B extends A {
                    readComputed(): int {
                      return super["value"]
                    }
                
                    assignComputed(): int {
                      return super["value"] = 12
                    }
                
                    prefixUpdateComputed(): int {
                      return ++super["count"]
                    }
                
                    readCount(): int {
                      return super["count"]
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.B");

        assertThat(
                Map.of(
                        "valueOps", List.of(
                                instanceRunner.invoke("readComputed"),
                                instanceRunner.invoke("assignComputed"),
                                instanceRunner.invoke("readComputed")),
                        "countOps", List.of(
                                instanceRunner.invoke("prefixUpdateComputed"),
                                instanceRunner.invoke("readCount"))
                )
        ).isEqualTo(
                Map.of(
                        "valueOps", List.of(4, 12, 12),
                        "countOps", List.of(2, 2))
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperPropertyReadWithShadowing(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 10
                  }
                  export class B extends A {
                    value: int = 100
                
                    readSuper(): int {
                      return super.value
                    }
                
                    readThis(): int {
                      return this.value
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.B");

        assertThat(
                Map.of(
                        "fields", List.of(
                                instanceRunner.invoke("readSuper"),
                                instanceRunner.invoke("readThis")))
        ).isEqualTo(
                Map.of("fields", List.of(10, 100))
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperPropertyUpdatePrefixAndPostfix(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    count: int = 5
                  }
                  export class B extends A {
                    postfix(): int {
                      return super.count++
                    }
                
                    prefix(): int {
                      return ++super.count
                    }
                
                    decrement(): int {
                      return --super.count
                    }
                
                    readSuper(): int {
                      return super.count
                    }
                
                    readThis(): int {
                      return this.count
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.B");

        assertThat(
                SimpleMap.of(
                        "updates", List.of(
                                instanceRunner.invoke("postfix"),
                                instanceRunner.invoke("prefix"),
                                instanceRunner.invoke("decrement")),
                        "final", List.of(
                                instanceRunner.invoke("readSuper"),
                                instanceRunner.invoke("readThis"))
                )
        ).isEqualTo(
                SimpleMap.of(
                        "updates", List.of(5, 7, 6),
                        "final", List.of(6, 6))
        );
    }
}
