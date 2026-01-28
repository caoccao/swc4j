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

package com.caoccao.javet.swc4j.compiler.ast.expr.binexpr;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestCompileBinExprInstanceOf extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testDoubleInstanceOfNumber(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Double, Number } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const num: Double = 3.14
                      return num instanceof Number
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceOfInIfStatement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Integer, String } from 'java.lang'
                namespace com {
                  export class A {
                    testWithString() {
                      const str: String = "hello"
                      if (str instanceof String) {
                        return 1
                      }
                      return 0
                    }
                    testWithInteger() {
                      const num: Integer = 42
                      if (num instanceof String) {
                        return 1
                      }
                      return 0
                    }
                  }
                }""");
        // String instanceof String should be true, returns 1
        assertEquals(1, (int) runner.createInstanceRunner("com.A").invoke("testWithString"));

        // Integer instanceof String should be false, returns 0
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("testWithInteger"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceOfStoreInVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Integer, String } from 'java.lang'
                namespace com {
                  export class A {
                    testTrue() {
                      const str: String = "hello"
                      const result = str instanceof String
                      return result
                    }
                    testFalse() {
                      const num: Integer = 42
                      const result = num instanceof String
                      return result
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("testTrue"));
        assertFalse((boolean) runner.createInstanceRunner("com.A").invoke("testFalse"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceOfWithNegation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Integer, String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const num: Integer = 42
                      const isNotString = !(num instanceof String)
                      return isNotString
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInstanceOfWithOr(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Integer, Number, String } from 'java.lang'
                namespace com {
                  export class A {
                    testStringOrNumber() {
                      const str: String = "hello"
                      return str instanceof String || str instanceof Number
                    }
                    testNumberOrString() {
                      const num: Integer = 42
                      return num instanceof String || num instanceof Number
                    }
                  }
                }""");
        // String is instanceof String, so true
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("testStringOrNumber"));

        // Integer is instanceof Number, so true
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("testNumberOrString"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testIntegerInstanceOfNumber(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Integer, Number } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const num: Integer = 42
                      return num instanceof Number
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testLongInstanceOfNumber(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Long, Number } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const num: Long = 1234567890
                      return num instanceof Number
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleInstanceOfChecks(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Integer, Number, String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const s: String = "hello"
                      const n: Integer = 42
                      const a = s instanceof String
                      const b = n instanceof Number
                      // All should be true, so AND them together
                      return a && b
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNullInstanceOfFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const obj: String = null
                      return obj instanceof String
                    }
                  }
                }""");
        // null instanceof X always returns false
        assertFalse((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testObjectInstanceOfObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Object } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const obj: Object = "any object"
                      return obj instanceof Object
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringInstanceOfObject(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Object, String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return str instanceof Object
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringInstanceOfStringFalse(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Integer, String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const num: Integer = 42
                      return num instanceof String
                    }
                  }
                }""");
        assertFalse((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringInstanceOfStringTrue(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return str instanceof String
                    }
                  }
                }""");
        assertTrue((boolean) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
