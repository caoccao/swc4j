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
                        "Animal", animalClass.getMethod("speak").invoke(animalClass.getConstructor().newInstance()),
                        "Dog", dogClass.getMethod("speak").invoke(dogClass.getConstructor().newInstance()),
                        "Cat", catClass.getMethod("speak").invoke(catClass.getConstructor().newInstance())
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
        var instance = myListClass.getConstructor().newInstance();
        assertEquals(42, myListClass.getMethod("getCustomField").invoke(instance));

        // Inherited methods from ArrayList should be available via reflection
        // Using ArrayList's add method directly (which is inherited)
        @SuppressWarnings("unchecked")
        ArrayList<Object> list = (ArrayList<Object>) instance;
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
        var instance = myExceptionClass.getConstructor(String.class, int.class).newInstance("Test error", 500);
        assertInstanceOf(Exception.class, instance);
        assertEquals(500, myExceptionClass.getMethod("getErrorCode").invoke(instance));
        assertEquals("Test error", myExceptionClass.getMethod("getMessage").invoke(instance));
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
        var instance = myMapClass.getConstructor().newInstance();
        assertEquals("custom", myMapClass.getMethod("getCustomField").invoke(instance));

        // Inherited methods from HashMap should be available via casting
        @SuppressWarnings("unchecked")
        HashMap<Object, Object> hashMap = (HashMap<Object, Object>) instance;
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
        var instance = myLinkedHashMapClass.getConstructor().newInstance();
        assertEquals(100, myLinkedHashMapClass.getMethod("getCustomField").invoke(instance));

        // Inherited methods from LinkedHashMap should be available via casting
        @SuppressWarnings("unchecked")
        LinkedHashMap<Object, Object> linkedHashMap = (LinkedHashMap<Object, Object>) instance;
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
        var instance = myNumberClass.getConstructor(int.class).newInstance(42);
        Number number = (Number) instance;

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

        var instance = validationExceptionClass.getConstructor(String.class, String.class)
                .newInstance("email", "Invalid email format");
        assertEquals(
                List.of("email", "Invalid email format"),
                List.of(
                        validationExceptionClass.getMethod("getFieldName").invoke(instance),
                        validationExceptionClass.getMethod("getMessage").invoke(instance)
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
        var instance = myThreadClass.getConstructor().newInstance();
        Thread thread = (Thread) instance;
        thread.start();
        thread.join(); // Wait for thread to complete

        assertEquals(42, myThreadClass.getMethod("getResult").invoke(instance));
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
        Class<?> classB = runner.getClass("com.B");
        var instance = classB.getConstructor().newInstance();
        assertEquals(42, classB.getMethod("getValue").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        Class<?> classB = runner.getClass("com.B");
        assertEquals(
                Map.of("A", 10, "B", 20),
                Map.of(
                        "A", classA.getMethod("getValue").invoke(classA.getConstructor().newInstance()),
                        "B", classB.getMethod("getValue").invoke(classB.getConstructor().newInstance())
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
        Class<?> classA = runner.getClass("com.A");
        Class<?> classB = runner.getClass("com.B");
        assertEquals(
                Map.of("A", 100, "B", 200),
                Map.of(
                        "A", classA.getMethod("getValue").invoke(classA.getConstructor().newInstance()),
                        "B", classB.getMethod("getValue").invoke(classB.getConstructor().newInstance())
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
        Class<?> classC = runner.getClass("com.C");
        var instance = classC.getConstructor().newInstance();
        assertEquals(
                List.of(1, 2, 3, 6),
                List.of(
                        classC.getMethod("getA").invoke(instance),
                        classC.getMethod("getB").invoke(instance),
                        classC.getMethod("getC").invoke(instance),
                        classC.getMethod("getSum").invoke(instance)
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
        Class<?> classB = runner.getClass("com.B");
        var instance = classB.getConstructor().newInstance();
        // B inherits getValue() from A
        assertEquals(100, classB.getMethod("getValue").invoke(instance));
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
        Class<?> classA = runner.getClass("com.A");
        Class<?> classB = runner.getClass("com.B");
        Class<?> classC = runner.getClass("com.C");
        assertEquals(
                List.of(10, 30, 60),
                List.of(
                        classA.getMethod("compute").invoke(classA.getConstructor().newInstance()),
                        classB.getMethod("compute").invoke(classB.getConstructor().newInstance()),
                        classC.getMethod("compute").invoke(classC.getConstructor().newInstance())
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
        Class<?> classB = runner.getClass("com.B");
        var instance = classB.getConstructor().newInstance();
        assertEquals(150, classB.getMethod("getValue").invoke(instance));
    }
}
