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

package com.caoccao.javet.swc4j.compiler.ast.clazz.clazz;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCompileAstClassInheritance extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testClassHierarchyCheck(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Animal {
                    speak(): String { return "..." }
                  }
                  export class Dog extends Animal {
                    speak(): String { return "Woof" }
                  }
                  export class Cat extends Animal {
                    speak(): String { return "Meow" }
                  }
                }""");
        Class<?> animalClass = runner.getClass("com.Animal");
        Class<?> dogClass = runner.getClass("com.Dog");
        Class<?> catClass = runner.getClass("com.Cat");

        // Check class hierarchy
        assertTrue(animalClass.isAssignableFrom(dogClass));
        assertTrue(animalClass.isAssignableFrom(catClass));

        assertEquals(
                Map.of("Animal", "...", "Dog", "Woof", "Cat", "Meow"),
                Map.of(
                        "Animal", runner.createInstanceRunner("com.Animal").invoke("speak"),
                        "Dog", runner.createInstanceRunner("com.Dog").invoke("speak"),
                        "Cat", runner.createInstanceRunner("com.Cat").invoke("speak")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExtendArrayList(JdkVersion jdkVersion) throws Exception {
        // Test that we can extend ArrayList and the class hierarchy is correct
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class MyList extends java.util.ArrayList<Object> {
                    customField: int = 42
                    getCustomField(): int {
                      return this.customField
                    }
                  }
                }""");
        Class<?> myListClass = runner.getClass("com.MyList");
        assertNotNull(myListClass);

        // Verify it extends ArrayList
        assertTrue(ArrayList.class.isAssignableFrom(myListClass));

        // Test custom field and method work
        var instanceRunner = runner.createInstanceRunner("com.MyList");
        assertEquals(42, (int) instanceRunner.invoke("getCustomField"));

        // Inherited methods from ArrayList should be available via reflection
        // Using ArrayList's add method directly (which is inherited)
        @SuppressWarnings("unchecked")
        ArrayList<Object> list = (ArrayList<Object>) instanceRunner.getInstance();
        list.add("Hello");
        assertEquals(1, list.size());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExtendException(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class MyException extends java.lang.Exception {
                    errorCode: int = 0
                    constructor(message: String, code: int) {
                      super(message)
                      this.errorCode = code
                    }
                    getErrorCode(): int {
                      return this.errorCode
                    }
                  }
                }""");
        Class<?> myExceptionClass = runner.getClass("com.MyException");
        assertNotNull(myExceptionClass);

        // Verify it extends Exception
        assertTrue(Exception.class.isAssignableFrom(myExceptionClass));

        // Test functionality
        var instanceRunner = runner.createInstanceRunner("com.MyException", "Test error", 500);
        assertInstanceOf(Exception.class, instanceRunner.getInstance());
        assertEquals(500, (int) instanceRunner.invoke("getErrorCode"));
        assertEquals("Test error", instanceRunner.invoke("getMessage"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExtendHashMap(JdkVersion jdkVersion) throws Exception {
        // Test that we can extend HashMap and the class hierarchy is correct
        var runner = getCompiler(jdkVersion).compile("""
                import { HashMap } from "java.util"
                namespace com {
                  export class MyMap extends HashMap<Object, Object> {
                    customField: String = "custom"
                    getCustomField(): String {
                      return this.customField
                    }
                  }
                }""");
        Class<?> myMapClass = runner.getClass("com.MyMap");
        assertNotNull(myMapClass);

        // Verify it extends HashMap
        assertTrue(HashMap.class.isAssignableFrom(myMapClass));

        // Test custom field and method work
        var instanceRunner = runner.createInstanceRunner("com.MyMap");
        assertEquals("custom", instanceRunner.invoke("getCustomField"));

        // Inherited methods from HashMap should be available via casting
        @SuppressWarnings("unchecked")
        HashMap<Object, Object> hashMap = (HashMap<Object, Object>) instanceRunner.getInstance();
        hashMap.put("key", "value");
        assertEquals("value", hashMap.get("key"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExtendLinkedHashMap(JdkVersion jdkVersion) throws Exception {
        // Test that we can extend LinkedHashMap and the class hierarchy is correct
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class MyLinkedHashMap extends java.util.LinkedHashMap<Object, Object> {
                    customField: int = 100
                    getCustomField(): int {
                      return this.customField
                    }
                  }
                }""");
        Class<?> myLinkedHashMapClass = runner.getClass("com.MyLinkedHashMap");
        assertNotNull(myLinkedHashMapClass);

        // Verify it extends LinkedHashMap
        assertTrue(LinkedHashMap.class.isAssignableFrom(myLinkedHashMapClass));

        // Test custom field and method work
        var instanceRunner = runner.createInstanceRunner("com.MyLinkedHashMap");
        assertEquals(100, (int) instanceRunner.invoke("getCustomField"));

        // Inherited methods from LinkedHashMap should be available via casting
        @SuppressWarnings("unchecked")
        LinkedHashMap<Object, Object> linkedHashMap = (LinkedHashMap<Object, Object>) instanceRunner.getInstance();
        linkedHashMap.put("a", 1);
        linkedHashMap.put("b", 2);
        assertEquals(1, linkedHashMap.get("a"));
        assertEquals(2, linkedHashMap.size());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExtendNumber(JdkVersion jdkVersion) throws Exception {
        // Test that we can extend Number and the class hierarchy is correct
        // Note: Number is abstract, so we need to implement its abstract methods
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class MyNumber extends java.lang.Number {
                    value: int = 0
                    constructor(v: int) {
                      super()
                      this.value = v
                    }
                    intValue(): int { return this.value }
                    longValue(): long { return (this.value as long) }
                    floatValue(): float { return (this.value as float) }
                    doubleValue(): double { return (this.value as double) }
                  }
                }""");
        Class<?> myNumberClass = runner.getClass("com.MyNumber");
        assertNotNull(myNumberClass);

        // Verify it extends Number
        assertTrue(Number.class.isAssignableFrom(myNumberClass));

        // Test functionality
        var instanceRunner = runner.createInstanceRunner("com.MyNumber", 42);
        Number number = (Number) instanceRunner.getInstance();

        assertEquals(42, number.intValue());
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExtendRuntimeException(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class ValidationException extends java.lang.RuntimeException {
                    fieldName: String = ""
                    constructor(field: String, message: String) {
                      super(message)
                      this.fieldName = field
                    }
                    getFieldName(): String {
                      return this.fieldName
                    }
                  }
                }""");
        Class<?> validationExceptionClass = runner.getClass("com.ValidationException");
        assertNotNull(validationExceptionClass);

        // Verify it extends RuntimeException
        assertTrue(RuntimeException.class.isAssignableFrom(validationExceptionClass));

        var instanceRunner = runner.createInstanceRunner("com.ValidationException", "email", "Invalid email format");
        assertEquals(
                List.of("email", "Invalid email format"),
                List.of(
                        instanceRunner.invoke("getFieldName"),
                        instanceRunner.invoke("getMessage")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testExtendThread(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class MyThread extends java.lang.Thread {
                    result: int = 0
                    run(): void {
                      this.result = 42
                    }
                    getResult(): int {
                      return this.result
                    }
                  }
                }""");
        Class<?> myThreadClass = runner.getClass("com.MyThread");
        assertNotNull(myThreadClass);

        // Verify it extends Thread
        assertTrue(Thread.class.isAssignableFrom(myThreadClass));

        // Test functionality
        var instanceRunner = runner.createInstanceRunner("com.MyThread");
        Thread thread = (Thread) instanceRunner.getInstance();
        thread.start();
        thread.join(); // Wait for thread to complete

        assertEquals(42, (int) instanceRunner.invoke("getResult"));
    }

    // ==================== JDK Built-in Class Extension Tests ====================

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInheritedField(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 42
                  }
                  export class B extends A {
                    getValue(): int {
                      return this.value
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.B");
        assertEquals(42, (int) instanceRunner.invoke("getValue"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInheritedFieldWithOverride(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    value: int = 10
                    getValue(): int { return this.value }
                  }
                  export class B extends A {
                    getValue(): int { return this.value * 2 }
                  }
                }""");
        assertEquals(
                Map.of("A", 10, "B", 20),
                Map.of(
                        "A", (int) runner.createInstanceRunner("com.A").invoke("getValue"),
                        "B", (int) runner.createInstanceRunner("com.B").invoke("getValue")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMethodOverride(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getValue(): int {
                      return 100
                    }
                  }
                  export class B extends A {
                    getValue(): int {
                      return 200
                    }
                  }
                }""");
        assertEquals(
                Map.of("A", 100, "B", 200),
                Map.of(
                        "A", (int) runner.createInstanceRunner("com.A").invoke("getValue"),
                        "B", (int) runner.createInstanceRunner("com.B").invoke("getValue")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMultiLevelInheritance(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getA(): int { return 1 }
                  }
                  export class B extends A {
                    getB(): int { return 2 }
                  }
                  export class C extends B {
                    getC(): int { return 3 }
                    getSum(): int { return this.getA() + this.getB() + this.getC() }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.C");
        assertEquals(
                List.of(1, 2, 3, 6),
                List.of(
                        (int) instanceRunner.invoke("getA"),
                        (int) instanceRunner.invoke("getB"),
                        (int) instanceRunner.invoke("getC"),
                        (int) instanceRunner.invoke("getSum")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSimpleInheritance(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getValue(): int {
                      return 100
                    }
                  }
                  export class B extends A {
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.B");
        // B inherits getValue() from A
        assertEquals(100, (int) instanceRunner.invoke("getValue"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperCallInChain(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    compute(): int { return 10 }
                  }
                  export class B extends A {
                    compute(): int { return super.compute() + 20 }
                  }
                  export class C extends B {
                    compute(): int { return super.compute() + 30 }
                  }
                }""");
        assertEquals(
                List.of(10, 30, 60),
                List.of(
                        (int) runner.createInstanceRunner("com.A").invoke("compute"),
                        (int) runner.createInstanceRunner("com.B").invoke("compute"),
                        (int) runner.createInstanceRunner("com.C").invoke("compute")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testSuperMethodCall(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    getValue(): int {
                      return 100
                    }
                  }
                  export class B extends A {
                    getValue(): int {
                      return super.getValue() + 50
                    }
                  }
                }""");
        var instanceRunner = runner.createInstanceRunner("com.B");
        assertEquals(150, (int) instanceRunner.invoke("getValue"));
    }
}
