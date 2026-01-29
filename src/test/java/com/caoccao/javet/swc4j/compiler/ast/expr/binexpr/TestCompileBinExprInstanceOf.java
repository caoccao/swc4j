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
import static org.assertj.core.api.Assertions.assertThat;


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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
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
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("testWithString")).isEqualTo(1);

        // Integer instanceof String should be false, returns 0
        assertThat((int) runner.createInstanceRunner("com.A").<Object>invoke("testWithInteger")).isEqualTo(0);
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("testTrue")).isTrue();
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("testFalse")).isFalse();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("testStringOrNumber")).isTrue();

        // Integer is instanceof Number, so true
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("testNumberOrString")).isTrue();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
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
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }
}
