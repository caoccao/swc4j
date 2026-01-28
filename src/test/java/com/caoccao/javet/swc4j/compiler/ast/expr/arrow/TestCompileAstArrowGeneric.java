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
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for generic type parameters in arrow expressions.
 * Generic arrows use type erasure to Object.
 * Edge cases 68-71 from the implementation plan.
 */
public class TestCompileAstArrowGeneric extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericArrowChained(JdkVersion jdkVersion) throws Exception {
        // Chained generic arrows
        var runner = getCompiler(jdkVersion).compile("""
                import { UnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Object {
                      const identity: UnaryOperator<Object> = <T>(x: T): T => x
                      const result: Object = identity(identity(identity("nested")))
                      return result
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("nested", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericArrowInMethod(JdkVersion jdkVersion) throws Exception {
        // Generic arrow returned from method
        var runner = getCompiler(jdkVersion).compile("""
                import { UnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getIdentity(): UnaryOperator<Object> {
                      return <T>(x: T): T => x
                    }
                    test(): Object {
                      const fn: UnaryOperator<Object> = this.getIdentity()
                      return fn("test")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("test", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericArrowWithCapture(JdkVersion jdkVersion) throws Exception {
        // Generic arrow capturing outer variable - use String to avoid boxing
        var runner = getCompiler(jdkVersion).compile("""
                import { Function } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Object {
                      const prefix: String = "Value: "
                      const format: Function<Object, String> = <T>(x: T): String => prefix + x.toString()
                      return format("42")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("Value: 42", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericFunctionWithTransformation(JdkVersion jdkVersion) throws Exception {
        // Generic function that transforms the input - use String to avoid boxing
        var runner = getCompiler(jdkVersion).compile("""
                import { Function } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Object {
                      const toString: Function<Object, String> = <T>(x: T): String => x.toString()
                      return toString("value123")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("value123", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericIdentityFunction(JdkVersion jdkVersion) throws Exception {
        // Edge case 68: Generic Arrow Function
        // const identity = <T>(x: T): T => x
        var runner = getCompiler(jdkVersion).compile("""
                import { UnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Object {
                      const identity: UnaryOperator<Object> = <T>(x: T): T => x
                      return identity("hello")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("hello", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericIdentityWithObject(JdkVersion jdkVersion) throws Exception {
        // Generic identity with Object type - use String to avoid boxing issues
        var runner = getCompiler(jdkVersion).compile("""
                import { UnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Object {
                      const identity: UnaryOperator<Object> = <T>(x: T): T => x
                      return identity("value42")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("value42", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testGenericWithBlockBody(JdkVersion jdkVersion) throws Exception {
        // Generic arrow with block body
        var runner = getCompiler(jdkVersion).compile("""
                import { UnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Object {
                      const identity: UnaryOperator<Object> = <T>(x: T): T => {
                        const result: T = x
                        return result
                      }
                      return identity("block")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("block", result);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleGenericTypeParams(JdkVersion jdkVersion) throws Exception {
        // Edge case 70: Multiple Type Parameters
        // Due to type erasure, both T and U become Object
        var runner = getCompiler(jdkVersion).compile("""
                import { Function } from 'java.util.function'
                namespace com {
                  export class A {
                    test(): Object {
                      const first: Function<Object, Object> = <T, U>(x: T): T => x
                      return first("first")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertEquals("first", result);
    }
}
