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

package com.caoccao.javet.swc4j.compiler.ast.clazz.function;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstFunctionTypeInference extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferBooleanFromLiteral(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return true
                    }
                  }
                }""");
        assertEquals(true, runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferDoubleFromLiteral(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 3.14
                    }
                  }
                }""");
        assertEquals(3.14, runner.createInstanceRunner("com.A").invoke("test"), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferFromConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(cond: boolean) {
                      return cond ? 1 : 2
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(1, (int) instanceRunner.invoke("test", true));
        assertEquals(2, (int) instanceRunner.invoke("test", false));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferFromExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int) {
                      return a + b
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(30, (int) instanceRunner.invoke("test", 10, 20));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferFromFunctionCall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    helper(): int {
                      return 10
                    }
                    test() {
                      return this.helper()
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(10, (int) instanceRunner.invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferFromParameter(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int) {
                      return a
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(100, (int) instanceRunner.invoke("test", 100));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferFromVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 42
                      return x
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(42, (int) instanceRunner.invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferIntFromLiteral(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return 42
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(42, (int) instanceRunner.invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInferStringFromLiteral(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return "hello"
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals("hello", instanceRunner.invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParameterTypeInference(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int) {
                      const c: int = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(15, (int) instanceRunner.invoke("test", 5, 10));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testParameterUsedInExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(a: int, b: int): int {
                      const c = a + b
                      return c
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertEquals(30, (int) instanceRunner.invoke("test", 10, 20));
    }
}
