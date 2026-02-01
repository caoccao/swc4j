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
 * Tests for Java instance method calls on imported classes.
 */
public class TestImportInstanceMethods extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Counter } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportInstanceMethods'
                namespace com {
                  export class A {
                    public test(): int {
                      const counter = new Counter(0)
                      return counter.increment().increment().add(10).getValue()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(12);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassInstanceMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Counter } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportInstanceMethods'
                namespace com {
                  export class A {
                    public test(): int {
                      const counter = new Counter(10)
                      counter.increment()
                      counter.increment()
                      counter.add(5)
                      return counter.getValue()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(17);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testCustomClassReset(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { Counter } from 'com.caoccao.javet.swc4j.compiler.ast.module.TestImportInstanceMethods'
                namespace com {
                  export class A {
                    public test(): int {
                      const counter = new Counter(100)
                      counter.increment()
                      counter.reset()
                      return counter.getValue()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInheritedMethodToString(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      const sb = new StringBuilder("Test")
                      return sb.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Test");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBuilderAppend(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      const sb = new StringBuilder("Hello")
                      sb.append(" ")
                      sb.append("World")
                      return sb.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Hello World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBuilderCapacity(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): int {
                      const sb = new StringBuilder(32)
                      return sb.capacity()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(32);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBuilderChaining(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      const sb = new StringBuilder()
                      return sb.append("Hello").append(" ").append("World").toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Hello World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBuilderInsert(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): String {
                      const sb = new StringBuilder("World")
                      sb.insert(0, "Hello ")
                      return sb.toString()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<String>invoke("test");
        assertThat(result).isEqualTo("Hello World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBuilderLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): int {
                      const sb = new StringBuilder("Hello")
                      return sb.length()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringBuilderSetLength(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { StringBuilder } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): int {
                      const sb = new StringBuilder("Hello")
                      sb.setLength(3)
                      return sb.length()
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Integer>invoke("test");
        assertThat(result).isEqualTo(3);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringMethods(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public testToUpperCase(s: String): String {
                      return s.toUpperCase()
                    }
                
                    public testSubstring(s: String): String {
                      return s.substring(0, 5)
                    }
                
                    public testConcat(s: String): String {
                      return s.concat(" World")
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.A");

        assertThat(instanceRunner.<String>invoke("testToUpperCase", "hello"))
                .isEqualTo("HELLO");
        assertThat(instanceRunner.<String>invoke("testSubstring", "Hello World"))
                .isEqualTo("Hello");
        assertThat(instanceRunner.<String>invoke("testConcat", "Hello"))
                .isEqualTo("Hello World");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStringStartsWith(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                import { String } from 'java.lang'
                namespace com {
                  export class A {
                    public test(): boolean {
                      const s = "Hello World"
                      return s.startsWith("Hello")
                    }
                  }
                }""");
        var result = runner.createInstanceRunner("com.A").<Boolean>invoke("test");
        assertThat(result).isTrue();
    }

    /**
     * Static helper class for testing custom class instance method imports.
     */
    public static class Counter {
        private int value;

        public Counter(int initialValue) {
            this.value = initialValue;
        }

        public Counter add(int amount) {
            value += amount;
            return this;
        }

        public int getValue() {
            return value;
        }

        public Counter increment() {
            value++;
            return this;
        }

        public void reset() {
            value = 0;
        }
    }
}
