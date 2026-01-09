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

package com.caoccao.javet.temp;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStringConcatFix extends BaseTestCompileSuite {

    @Test
    public void testBytePrimitiveConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: byte = 42
                      const b: String = 'answer'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("42answer", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testByteWrapperConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Byte = 42
                      const b: String = 'answer'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("42answer", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testShortPrimitiveConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: short = 100
                      const b: String = 'value'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("100value", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testShortWrapperConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Short = 100
                      const b: String = 'value'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("100value", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testLongPrimitiveConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: long = 1000
                      const b: String = 'ms'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("1000ms", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testLongWrapperConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Long = 1000
                      const b: String = 'ms'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("1000ms", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testFloatPrimitiveConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: float = 3.14
                      const b: String = 'pi'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("3.14pi", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testFloatWrapperConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Float = 3.14
                      const b: String = 'pi'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("3.14pi", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testDoublePrimitiveConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: double = 2.718
                      const b: String = 'e'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("2.718e", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testDoubleWrapperConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Double = 2.718
                      const b: String = 'e'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("2.718e", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testBooleanPrimitiveConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: boolean = true
                      const b: String = ' value'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("true value", classA.getMethod("test").invoke(instance));
    }

    @Test
    public void testBooleanWrapperConcat() throws Exception {
        var map = getCompiler(JdkVersion.JDK_17).compile("""
                namespace com {
                  export class A {
                    test() {
                      const a: Boolean = false
                      const b: String = ' value'
                      return a + b
                    }
                  }
                }""");
        Class<?> classA = loadClass(map.get("com.A"));
        var instance = classA.getConstructor().newInstance();
        assertEquals("false value", classA.getMethod("test").invoke(instance));
    }
}
