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

package com.caoccao.javet.swc4j.compiler.ast.expr.lit.arraylit;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for array element access and indexing operations.
 */
public class TestCompileAstArrayLitAccess extends BaseTestCompileSuite {


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayClear(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      arr.length = 0
                      return arr.length
                    }
                  }
                }""");
        assertEquals(0, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayDelete(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      delete arr[1]
                      return arr.length
                    }
                  }
                }""");
        assertEquals(2, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayIndexGet(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      return arr[1]
                    }
                  }
                }""");
        assertEquals(20, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayIndexSet(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [10, 20, 30]
                      arr[1] = 99
                      return arr[1]
                    }
                  }
                }""");
        assertEquals(99, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }


    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testArrayLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test() {
                      const arr = [1, 2, 3]
                      return arr.length
                    }
                  }
                }""");
        assertEquals(3, (int) runner.createInstanceRunner("com.A").invoke("test"));
    }
}
