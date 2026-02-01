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

package com.caoccao.javet.swc4j.compiler.ast.module;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Java constructor calls on imported classes.
 */
public class TestImportConstructors extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorInExpression(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      return new StringBuilder("Direct").append(" Result").toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Direct Result");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorOverloadResolution(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { ArrayList } from 'java.util'
                namespace com {
                  export class A {
                    public testNoArg(): int {
                      const list = new ArrayList()
                      return list.size()
                    }
                
                    public testWithCapacity(): int {
                      const list = new ArrayList(10)
                      return list.size()
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");
        assertThat(instanceRunner.<Integer>invoke("testNoArg")).isEqualTo(0);
        assertThat(instanceRunner.<Integer>invoke("testWithCapacity")).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithIntArg(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): int {
                      const sb = new StringBuilder(16)
                      return sb.capacity()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(16);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testConstructorWithStringArg(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      const sb = new StringBuilder("Initial")
                      return sb.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Initial");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassCopyConstructor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Point } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportConstructors'
                namespace com {
                  export class A {
                    public test(): String {
                      const p1 = new Point(5, 15)
                      const p2 = new Point(p1)
                      return p2.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Point(5, 15)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassNoArgConstructor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Point } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportConstructors'
                namespace com {
                  export class A {
                    public test(): String {
                      const p = new Point()
                      return p.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Point(0, 0)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassTwoArgConstructor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Point } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportConstructors'
                namespace com {
                  export class A {
                    public test(): String {
                      const p = new Point(10, 20)
                      return p.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Point(10, 20)");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMapConstructor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { LinkedHashMap } from 'java.util'
                namespace com {
                  export class A {
                    public test(): int {
                      const map = new LinkedHashMap()
                      map.put("key1", 1)
                      map.put("key2", 2)
                      return map.size()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(2);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleConstructorCalls(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      const sb1 = new StringBuilder("First")
                      const sb2 = new StringBuilder("Second")
                      const sb3 = new StringBuilder()
                      sb3.append(sb1.toString())
                      sb3.append(" ")
                      sb3.append(sb2.toString())
                      return sb3.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("First Second");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultipleStringBuilders(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      const sb1 = new StringBuilder()
                      const sb2 = new StringBuilder("Middle")
                      const sb3 = new StringBuilder("End")
                      sb1.append("Start ")
                      sb1.append(sb2.toString())
                      sb1.append(" ")
                      sb1.append(sb3.toString())
                      return sb1.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Start Middle End");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testNoArgConstructor(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): StringBuilder {
                      return new StringBuilder()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").invoke("test");
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(StringBuilder.class);
    }

    /**
         * Static helper class for testing custom class imports.
         */
        public record Point(int x, int y) {
            public Point() {
                this(0, 0);
            }

        public Point(Point other) {
                this(other.x, other.y);
            }

            @Override
            public String toString() {
                return "Point(" + x + ", " + y + ")";
            }
        }
}
