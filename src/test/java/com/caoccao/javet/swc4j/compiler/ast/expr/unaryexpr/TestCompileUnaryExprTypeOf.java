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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCompileUnaryExprTypeOf extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfArrayList(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test(): String {
                      const list: ArrayList = new ArrayList()
                      return typeof list
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("object");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfBigInteger(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const value: bigint = 123n
                      return typeof value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("number");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfCharacter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<String> {
                      const c: char = 'A'
                      const cw: Character = 'B'
                      return [typeof c, typeof cw]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("number", "number"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfIntArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const arr: int[] = [1, 2, 3]
                      return typeof arr
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("object");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfLinkedHashMap(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test(): String {
                      const map: LinkedHashMap = new LinkedHashMap()
                      return typeof map
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("object");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfNull(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): String {
                      const value: Object = null
                      return typeof value
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("object");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfNumericWrappers(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<String> {
                      const i: Integer = 1
                      const l: Long = 2
                      const f: Float = 1.5
                      const d: Double = 2.5
                      const b: Byte = 1
                      const s: Short = 2
                      return [typeof i, typeof l, typeof f, typeof d, typeof b, typeof s]
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("number", "number", "number", "number", "number", "number"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfObjectLiteral(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test(): String {
                      const map: LinkedHashMap = { a: 1 }
                      return typeof map
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("object");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfPrimitives(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<String> {
                      const result: Array<String> = [typeof 1, typeof true, typeof "hello"]
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("number", "boolean", "string"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfSideEffects(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Array<Object> {
                      let x: int = 0
                      const result: Array<Object> = [typeof (++x), x]
                      return result
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo(List.of("number", 1));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testTypeOfVoid(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    doNothing(): void {}
                    test(): String {
                      return typeof this.doNothing()
                    }
                  }
                }""");
        assertThat(runner.createInstanceRunner("com.A").<Object>invoke("test"))
                .isEqualTo("undefined");
    }
}
