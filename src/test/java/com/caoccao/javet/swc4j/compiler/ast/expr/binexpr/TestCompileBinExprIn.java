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


public class TestCompileBinExprIn extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayInvalidFloatFractionalIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return 1.1 in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayInvalidFloatNegativeWholeIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return -1.0 in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayInvalidIntIndexOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return 3 in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayInvalidLongStringIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return "123456789123456789001234567890" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayInvalidStringIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return "1.0" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayInvalidStringIndexNonNumeric(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return "abc" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayInvalidStringIndexOutOfBounds(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return "3" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayNegativeIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return "-1" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayValidFloatWholeIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return 1.0 in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayValidIntIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return 1 in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayValidLastIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return "2" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInArrayValidStringIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = [1, 2, 3]
                      return "0" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInEmptyArray(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const arr: ArrayList = []
                      return "0" in arr
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInEmptyMap(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const obj: LinkedHashMap = {}
                      return "key" in obj
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInEmptyString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = ""
                      return "0" in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInMapExistingKey(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const obj: LinkedHashMap = { a: 1, b: 2, c: 3 }
                      return "a" in obj
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInMapNonExistingKey(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const obj: LinkedHashMap = { a: 1, b: 2, c: 3 }
                      return "d" in obj
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStoreInVariable(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const obj: LinkedHashMap = { key: "value" }
                      const exists = "key" in obj
                      return exists
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringInvalidFloatFractionalIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return 2.5 in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringInvalidFloatNegativeWholeIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return -1.0 in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringInvalidIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return "5" in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringInvalidIndexNonNumeric(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return "xyz" in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringLastIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return "4" in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringNegativeIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return "-1" in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringValidFloatWholeIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return 2.0 in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringValidIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return "0" in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInStringValidIntIndex(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    test() {
                      const str: String = "hello"
                      return 2 in str
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInWithConditional(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    testTrue() {
                      const obj: LinkedHashMap = { name: "test" }
                      if ("name" in obj) {
                        return 1
                      }
                      return 0
                    }
                    testFalse() {
                      const obj: LinkedHashMap = { name: "test" }
                      if ("age" in obj) {
                        return 1
                      }
                      return 0
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat((int) instanceRunner.<Object>invoke("testTrue")).isEqualTo(1);
        assertThat((int) instanceRunner.<Object>invoke("testFalse")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInWithLogicalAnd(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const obj: LinkedHashMap = { a: 1, b: 2 }
                      return "a" in obj && "b" in obj
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInWithLogicalOr(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const obj: LinkedHashMap = { a: 1 }
                      return "a" in obj || "b" in obj
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInWithNegation(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    test() {
                      const obj: LinkedHashMap = { a: 1 }
                      return !("b" in obj)
                    }
                  }
                }""");
        assertThat((boolean) runner.createInstanceRunner("com.A").<Boolean>invoke("test")).isTrue();
    }
}
