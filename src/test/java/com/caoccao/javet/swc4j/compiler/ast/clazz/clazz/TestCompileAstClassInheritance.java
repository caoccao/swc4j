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

import static org.assertj.core.api.Assertions.assertThat;


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
        assertThat(animalClass.isAssignableFrom(dogClass)).isTrue();
        assertThat(animalClass.isAssignableFrom(catClass)).isTrue();

        assertThat(
                Map.of(
                        "Animal", runner.createInstanceRunner("com.Animal").invoke("speak"),
                        "Dog", runner.createInstanceRunner("com.Dog").invoke("speak"),
                        "Cat", runner.createInstanceRunner("com.Cat").invoke("speak")
                )
        ).isEqualTo(
                Map.of("Animal", "...", "Dog", "Woof", "Cat", "Meow")
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
        assertThat(myListClass).isNotNull();

        // Verify it extends ArrayList
        assertThat(ArrayList.class.isAssignableFrom(myListClass)).isTrue();

        // Test custom field and method work
        var instanceRunner = runner.createInstanceRunner("com.MyList");
        assertThat((int) instanceRunner.invoke("getCustomField")).isEqualTo(42);

        // Inherited methods from ArrayList should be available via reflection
        // Using ArrayList's add method directly (which is inherited)
        @SuppressWarnings("unchecked")
        ArrayList<Object> list = (ArrayList<Object>) instanceRunner.getInstance();
        list.add("Hello");
        assertThat(list.size()).isEqualTo(1);
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
        assertThat(myExceptionClass).isNotNull();

        // Verify it extends Exception
        assertThat(Exception.class.isAssignableFrom(myExceptionClass)).isTrue();

        // Test functionality
        var instanceRunner = runner.createInstanceRunner("com.MyException", "Test error", 500);
        assertThat(instanceRunner.getInstance()).isInstanceOf(Exception.class);
        assertThat((int) instanceRunner.invoke("getErrorCode")).isEqualTo(500);
        assertThat((String) instanceRunner.invoke("getMessage")).isEqualTo("Test error");
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
        assertThat(myMapClass).isNotNull();

        // Verify it extends HashMap
        assertThat(HashMap.class.isAssignableFrom(myMapClass)).isTrue();

        // Test custom field and method work
        var instanceRunner = runner.createInstanceRunner("com.MyMap");
        assertThat((String) instanceRunner.invoke("getCustomField")).isEqualTo("custom");

        // Inherited methods from HashMap should be available via casting
        @SuppressWarnings("unchecked")
        HashMap<Object, Object> hashMap = (HashMap<Object, Object>) instanceRunner.getInstance();
        hashMap.put("key", "value");
        assertThat(hashMap.get("key")).isEqualTo("value");
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
        assertThat(myLinkedHashMapClass).isNotNull();

        // Verify it extends LinkedHashMap
        assertThat(LinkedHashMap.class.isAssignableFrom(myLinkedHashMapClass)).isTrue();

        // Test custom field and method work
        var instanceRunner = runner.createInstanceRunner("com.MyLinkedHashMap");
        assertThat((int) instanceRunner.invoke("getCustomField")).isEqualTo(100);

        // Inherited methods from LinkedHashMap should be available via casting
        @SuppressWarnings("unchecked")
        LinkedHashMap<Object, Object> linkedHashMap = (LinkedHashMap<Object, Object>) instanceRunner.getInstance();
        linkedHashMap.put("a", 1);
        linkedHashMap.put("b", 2);
        assertThat(linkedHashMap.get("a")).isEqualTo(1);
        assertThat(linkedHashMap.size()).isEqualTo(2);
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
        assertThat(myNumberClass).isNotNull();

        // Verify it extends Number
        assertThat(Number.class.isAssignableFrom(myNumberClass)).isTrue();

        // Test functionality
        var instanceRunner = runner.createInstanceRunner("com.MyNumber", 42);
        Number number = (Number) instanceRunner.getInstance();

        assertThat(number.intValue()).isEqualTo(42);
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
        assertThat(validationExceptionClass).isNotNull();

        // Verify it extends RuntimeException
        assertThat(RuntimeException.class.isAssignableFrom(validationExceptionClass)).isTrue();

        var instanceRunner = runner.createInstanceRunner("com.ValidationException", "email", "Invalid email format");
        assertThat(
                List.of(
                        instanceRunner.invoke("getFieldName"),
                        instanceRunner.invoke("getMessage")
                )
        ).isEqualTo(
                List.of("email", "Invalid email format")
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
        assertThat(myThreadClass).isNotNull();

        // Verify it extends Thread
        assertThat(Thread.class.isAssignableFrom(myThreadClass)).isTrue();

        // Test functionality
        var instanceRunner = runner.createInstanceRunner("com.MyThread");
        Thread thread = (Thread) instanceRunner.getInstance();
        thread.start();
        thread.join(); // Wait for thread to complete

        assertThat((int) instanceRunner.invoke("getResult")).isEqualTo(42);
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
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(42);
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
        assertThat(
                Map.of(
                        "A", runner.createInstanceRunner("com.A").invoke("getValue"),
                        "B", (int) runner.createInstanceRunner("com.B").invoke("getValue")
                )
        ).isEqualTo(
                Map.of("A", 10, "B", 20)
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
        assertThat(
                Map.of(
                        "A", runner.createInstanceRunner("com.A").invoke("getValue"),
                        "B", (int) runner.createInstanceRunner("com.B").invoke("getValue")
                )
        ).isEqualTo(
                Map.of("A", 100, "B", 200)
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
        assertThat(
                List.of(
                        instanceRunner.invoke("getA"),
                        instanceRunner.invoke("getB"),
                        instanceRunner.invoke("getC"),
                        (int) instanceRunner.invoke("getSum")
                )
        ).isEqualTo(
                List.of(1, 2, 3, 6)
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
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(100);
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
        assertThat(
                List.of(
                        runner.createInstanceRunner("com.A").invoke("compute"),
                        runner.createInstanceRunner("com.B").invoke("compute"),
                        (int) runner.createInstanceRunner("com.C").invoke("compute")
                )
        ).isEqualTo(
                List.of(10, 30, 60)
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
        assertThat((int) instanceRunner.invoke("getValue")).isEqualTo(150);
    }
}
