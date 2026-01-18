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

package com.caoccao.javet.swc4j.compiler.ast.expr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test suite for conditional expressions (ternary operator)
 * Phase 1: Basic conditionals with same-type branches
 */
public class TestCompileAstCondExpr extends BaseTestCompileSuite {

    // ========== Phase 1: Basic Conditional Tests ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicConditionFalse(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return false ? 10 : 20
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(20, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicConditionTrue(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      return true ? 10 : 20
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBooleanBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = false
                      return flag ? true : false
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(false, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testChainedTernaries(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const score: int = 85
                      return score > 90 ? "A" : score > 80 ? "B" : "C"
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("B", classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexConditionAnd(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 10
                      const y: int = 5
                      return x > 5 && y < 10 ? 100 : 200
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testComplexConditionOr(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 3
                      const y: int = 15
                      return x > 5 || y > 10 ? 100 : 200
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDeepNesting(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: boolean = false
                      const c: boolean = true
                      return a ? (b ? (c ? 1 : 2) : 3) : 4
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(3, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? 1.5 : 2.5
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(1.5, (double) classA.getMethod("test").invoke(instance), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testFloatAndDoubleWidening(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): double {
                      const flag: boolean = true
                      const floatVal: float = 10.0
                      const doubleVal: double = 20.0
                      return flag ? floatVal : doubleVal
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10.0, (double) classA.getMethod("test").invoke(instance), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInBinaryExpression(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return (flag ? 10 : 20) + 5
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(15, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInVariableDeclaration(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 10
                      const result: int = x > 5 ? 100 : 200
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntAndDoubleWidening(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? 10 : 20.5
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(10.0, (double) classA.getMethod("test").invoke(instance), 0.001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntAndLongWidening(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const flag: boolean = false
                      const longVal: long = 20
                      return flag ? 10 : longVal
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(20L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerComparison(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const x: int = 10
                      return x > 5 ? 100 : 200
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(100, classA.getMethod("test").invoke(instance));
    }

    // ========== Phase 2: Type Coercion Tests ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): long {
                      const flag: boolean = false
                      const val1: long = 100
                      const val2: long = 200
                      return flag ? val1 : val2
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(200L, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNegatedCondition(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return !flag ? 10 : 20
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(20, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedInAlternate(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag1: boolean = false
                      const flag2: boolean = true
                      return flag1 ? 1 : (flag2 ? 2 : 3)
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    // ========== Phase 3: Nested Conditionals ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNestedInConsequent(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag1: boolean = true
                      const flag2: boolean = false
                      return flag1 ? (flag2 ? 1 : 2) : 3
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals(2, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullAlternate(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = false
                      return flag ? "value" : null
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertNull(classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullConsequent(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? null : "default"
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertNull(classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSideEffectInBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let x: int = 10
                      let y: int = 20
                      const flag: boolean = true
                      const result: int = flag ? (x++) : (y++)
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // Only x is incremented (true branch)
        assertEquals(10, classA.getMethod("test").invoke(instance));
    }

    // ========== Phase 4: Edge Cases ==========

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSideEffectInCondition(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      let i: int = 5
                      const result: int = (i++) > 5 ? 100 : 200
                      return result
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        // i is incremented to 6, but comparison happens with old value (5)
        assertEquals(200, classA.getMethod("test").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBranches(JdkVersion jdkVersion) throws Exception {
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const flag: boolean = true
                      return flag ? "yes" : "no"
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("yes", classA.getMethod("test").invoke(instance));
    }
}
