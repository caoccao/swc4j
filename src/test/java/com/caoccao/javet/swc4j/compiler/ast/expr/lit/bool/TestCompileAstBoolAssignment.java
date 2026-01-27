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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.bool;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for boolean constants and variable assignments.
 * Phase 3: Boolean Constants and Assignment (8 tests)
 */
public class TestCompileAstBoolAssignment extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBoxedCopy(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): Boolean {
                      const original: Boolean = true
                      const copy: Boolean = original
                      return copy
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertTrue((Boolean) classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanConstChain(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const a = true
                      const b = a
                      return b
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertTrue((Boolean) classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanConstantFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const FALSE = false
                      return FALSE
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertFalse((Boolean) classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanConstantTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const TRUE = true
                      return TRUE
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertTrue((Boolean) classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanCopyFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const original = false
                      const copy = original
                      return copy
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertFalse((Boolean) classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanCopyTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      const original = true
                      const copy = original
                      return copy
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertTrue((Boolean) classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanMultipleAssignments(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      let flag = true
                      flag = false
                      flag = true
                      return flag
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertTrue((Boolean) classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanReassignment(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): boolean {
                      let flag = true
                      flag = false
                      return flag
                    }
                  }
                }""");
        Class<?> classA = runner.getClass("com.A");
        var instance = classA.getConstructor().newInstance();
        assertFalse((Boolean) classA.getMethod("test").invoke(instance));
    }
}
