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

package com.caoccao.javet.swc4j.compiler.ast.expr.unaryexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class TestCompileUnaryExprDelete extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeleteArrayListElement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test(): ArrayList {
                      const arr: ArrayList = [1, 2, 3]
                      delete arr[1]
                      return arr
                    }
                  }
                }""");
        var result = (ArrayList<?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(List.of(1, 3));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeleteArrayListReturn(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test(): boolean {
                      const arr: ArrayList = [1, 2, 3]
                      return delete arr[1]
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeleteMapEntry(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test(): LinkedHashMap {
                      const map: LinkedHashMap = { a: 1, b: 2 }
                      delete map["a"]
                      return map
                    }
                  }
                }""");
        var result = (LinkedHashMap<?, ?>) runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isEqualTo(Map.of("b", 2));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeleteMapMissingKey(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test(): boolean {
                      const map: LinkedHashMap = { a: 1 }
                      return delete map["missing"]
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeleteArrayThrows(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> {
            getCompiler(jdkVersion).compile("""
                    namespace com {
                      export class A {
                        test() {
                          const arr: int[] = [1, 2, 3]
                          return delete arr[0]
                        }
                      }
                    }""");
        }).isInstanceOf(Exception.class);
    }
}
