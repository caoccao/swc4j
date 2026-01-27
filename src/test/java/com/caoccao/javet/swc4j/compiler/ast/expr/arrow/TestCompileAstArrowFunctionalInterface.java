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

import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for arrow expressions with various functional interfaces.
 * Tests Phase 3 from the implementation plan.
 */
public class TestCompileAstArrowFunctionalInterface extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { BooleanSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): BooleanSupplier {
                      return () => true
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertTrue(((BooleanSupplier) fn).getAsBoolean());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { DoubleSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): DoubleSupplier {
                      return () => 3.14159
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(3.14159, ((DoubleSupplier) fn).getAsDouble(), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntSupplier {
                      return () => 42
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(42, ((IntSupplier) fn).getAsInt());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntUnaryOperator(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): IntUnaryOperator {
                      return (x: int) => x * 2
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(10, ((IntUnaryOperator) fn).applyAsInt(5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongSupplier(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LongSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): LongSupplier {
                      const value: long = 123456789012345
                      return () => value
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals(123456789012345L, ((LongSupplier) fn).getAsLong());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSupplierString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    get(): Supplier {
                      return () => "hello"
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("get").invoke(instance);
        assertNotNull(fn);
        assertEquals("hello", ((Supplier<?>) fn).get());
    }

}
