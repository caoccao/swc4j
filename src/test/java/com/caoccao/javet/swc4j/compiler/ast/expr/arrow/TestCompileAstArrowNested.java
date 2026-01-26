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

import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for nested arrow expressions.
 * Tests Phase 6 from the implementation plan.
 */
public class TestCompileAstArrowNested extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowChainedWithCapture(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                import { IntSupplier, IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    getDoubler(): IntUnaryOperator {
                      const factor: int = 2
                      return (x: int) => x * factor
                    }
                    getIncrementer(): IntUnaryOperator {
                      const offset: int = 1
                      return (x: int) => x + offset
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();

        var doubler = (IntUnaryOperator) classA.getMethod("getDoubler").invoke(instance);
        var incrementer = (IntUnaryOperator) classA.getMethod("getIncrementer").invoke(instance);

        assertEquals(10, doubler.applyAsInt(5));   // 5 * 2 = 10
        assertEquals(6, incrementer.applyAsInt(5)); // 5 + 1 = 6
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithOffsetCapture(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                import { IntUnaryOperator } from 'java.util.function'
                namespace com {
                  export class A {
                    createAdder(offset: int): IntUnaryOperator {
                      return (x: int) => x + offset
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("createAdder", int.class).invoke(instance, 5);
        assertNotNull(fn);
        assertEquals(15, ((IntUnaryOperator) fn).applyAsInt(10));  // 10 + 5 = 15
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithStringCapture(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                import { Supplier } from 'java.util.function'
                namespace com {
                  export class A {
                    createGreeter(name: String): Supplier {
                      const greeting: String = "Hello, "
                      return () => greeting + name
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("createGreeter", String.class).invoke(instance, "World");
        assertNotNull(fn);
        assertEquals("Hello, World", ((java.util.function.Supplier<?>) fn).get());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrowWithThisCapture(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                import { IntSupplier } from 'java.util.function'
                namespace com {
                  export class A {
                    value: int = 100
                    createValueGetter(): IntSupplier {
                      return () => this.value
                    }
                  }
                }""");
        var classes = loadClasses(map);
        Class<?> classA = classes.get("com.A");
        var instance = classA.getConstructor().newInstance();
        var fn = classA.getMethod("createValueGetter").invoke(instance);
        assertNotNull(fn);
        assertEquals(100, ((IntSupplier) fn).getAsInt());
    }
}
