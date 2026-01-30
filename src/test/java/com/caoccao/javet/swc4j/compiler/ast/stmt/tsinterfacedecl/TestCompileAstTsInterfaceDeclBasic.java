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

package com.caoccao.javet.swc4j.compiler.ast.stmt.tsinterfacedecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Phase 1: Basic Interface Declaration Tests.
 * Tests simple interface declarations with properties and methods.
 * Also tests implementing classes to verify getter/setter behavior.
 */
public class TestCompileAstTsInterfaceDeclBasic extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testAmbientInterface(JdkVersion jdkVersion) throws Exception {
        // Test: Ambient interface (declare) should not generate bytecode
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  declare interface External {
                    value: int
                  }
                }""");

        // Ambient declarations should not generate bytecode
        assertThatThrownBy(() -> runner.getClass("com.External")).isInstanceOf(ClassNotFoundException.class);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicInterfaceWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Basic interface with a single property and implementing class
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Person {
                    name: String
                  }
                  export class PersonImpl implements Person {
                    name: String = ""
                    getName(): String {
                      return this.name
                    }
                    setName(name: String): void {
                      this.name = name
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Person");
        Class<?> implClass = runner.getClass("com.PersonImpl");

        // Verify it's an interface
        assertThat(interfaceClass.isInterface()).isTrue();

        // Verify getter and setter methods exist on interface
        Method getName = interfaceClass.getMethod("getName");
        assertThat(getName).isNotNull();
        assertThat(Modifier.isAbstract(getName.getModifiers())).isTrue();
        assertThat(getName.getReturnType()).isEqualTo(String.class);

        Method setName = interfaceClass.getMethod("setName", String.class);
        assertThat(setName).isNotNull();
        assertThat(Modifier.isAbstract(setName.getModifiers())).isTrue();
        assertThat(setName.getReturnType()).isEqualTo(void.class);

        // Test the implementation - create instance and verify getter/setter
        Object instance = implClass.getConstructor().newInstance();

        // Initial value should be empty string
        assertThat(implClass.getMethod("getName").invoke(instance)).isEqualTo("");

        // Set a new value
        implClass.getMethod("setName", String.class).invoke(instance, "John");

        // Get the value back
        assertThat(implClass.getMethod("getName").invoke(instance)).isEqualTo("John");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyInterface(JdkVersion jdkVersion) throws Exception {
        // Test: Empty interface (marker interface)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Marker {
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Marker");

        assertThat(interfaceClass.isInterface()).isTrue();
        // Only inherited methods from Object
        assertThat(interfaceClass.getDeclaredMethods().length).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceAllPrimitiveTypes(JdkVersion jdkVersion) throws Exception {
        // Test: Interface declares all primitive types (no implementation needed)
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface AllPrimitives {
                    byteVal: byte
                    shortVal: short
                    intVal: int
                    longVal: long
                    floatVal: float
                    doubleVal: double
                    charVal: char
                    boolVal: boolean
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.AllPrimitives");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Verify all getter return types
        assertThat(interfaceClass.getMethod("getByteVal").getReturnType()).isEqualTo(byte.class);
        assertThat(interfaceClass.getMethod("getShortVal").getReturnType()).isEqualTo(short.class);
        assertThat(interfaceClass.getMethod("getIntVal").getReturnType()).isEqualTo(int.class);
        assertThat(interfaceClass.getMethod("getLongVal").getReturnType()).isEqualTo(long.class);
        assertThat(interfaceClass.getMethod("getFloatVal").getReturnType()).isEqualTo(float.class);
        assertThat(interfaceClass.getMethod("getDoubleVal").getReturnType()).isEqualTo(double.class);
        assertThat(interfaceClass.getMethod("getCharVal").getReturnType()).isEqualTo(char.class);
        assertThat(interfaceClass.getMethod("isBoolVal").getReturnType()).isEqualTo(boolean.class);

        // Verify all setters exist
        assertThat(interfaceClass.getMethod("setByteVal", byte.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setShortVal", short.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setIntVal", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setLongVal", long.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setFloatVal", float.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setDoubleVal", double.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setCharVal", char.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setBoolVal", boolean.class)).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceBooleanPropertyWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with boolean property (should use 'is' prefix) and implementation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Toggleable {
                    enabled: boolean
                  }
                  export class Switch implements Toggleable {
                    enabled: boolean = false
                    isEnabled(): boolean {
                      return this.enabled
                    }
                    setEnabled(enabled: boolean): void {
                      this.enabled = enabled
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Toggleable");
        Class<?> implClass = runner.getClass("com.Switch");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Boolean getter should use 'is' prefix
        Method isEnabled = interfaceClass.getMethod("isEnabled");
        assertThat(isEnabled).isNotNull();
        assertThat(isEnabled.getReturnType()).isEqualTo(boolean.class);

        // Setter should still use 'set' prefix
        Method setEnabled = interfaceClass.getMethod("setEnabled", boolean.class);
        assertThat(setEnabled).isNotNull();

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Initial value should be false
        assertThat((Boolean) implClass.getMethod("isEnabled").invoke(instance)).isFalse();

        // Set to true
        implClass.getMethod("setEnabled", boolean.class).invoke(instance, true);

        // Verify change
        assertThat((Boolean) implClass.getMethod("isEnabled").invoke(instance)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMultiplePropertiesWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with multiple properties and implementing class
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface User {
                    id: int
                    name: String
                    active: boolean
                  }
                  export class UserImpl implements User {
                    id: int = 0
                    name: String = ""
                    active: boolean = false
                    getId(): int { return this.id }
                    setId(id: int): void { this.id = id }
                    getName(): String { return this.name }
                    setName(name: String): void { this.name = name }
                    isActive(): boolean { return this.active }
                    setActive(active: boolean): void { this.active = active }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.User");
        Class<?> implClass = runner.getClass("com.UserImpl");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Check all getters on interface
        assertThat(interfaceClass.getMethod("getId").getReturnType()).isEqualTo(int.class);
        assertThat(interfaceClass.getMethod("getName").getReturnType()).isEqualTo(String.class);
        assertThat(interfaceClass.getMethod("isActive").getReturnType()).isEqualTo(boolean.class);

        // Check all setters on interface
        assertThat(interfaceClass.getMethod("setId", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setName", String.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setActive", boolean.class)).isNotNull();

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Verify initial values
        assertThat(implClass.getMethod("getId").invoke(instance)).isEqualTo(0);
        assertThat(implClass.getMethod("getName").invoke(instance)).isEqualTo("");
        assertThat((Boolean) implClass.getMethod("isActive").invoke(instance)).isFalse();

        // Set new values
        implClass.getMethod("setId", int.class).invoke(instance, 42);
        implClass.getMethod("setName", String.class).invoke(instance, "Alice");
        implClass.getMethod("setActive", boolean.class).invoke(instance, true);

        // Verify changes
        assertThat(implClass.getMethod("getId").invoke(instance)).isEqualTo(42);
        assertThat(implClass.getMethod("getName").invoke(instance)).isEqualTo("Alice");
        assertThat((Boolean) implClass.getMethod("isActive").invoke(instance)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfacePrimitiveTypesWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with primitive types and simple implementation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface PrimitiveHolder {
                    intVal: int
                    boolVal: boolean
                  }
                  export class PrimitiveImpl implements PrimitiveHolder {
                    intVal: int = 0
                    boolVal: boolean = false
                    getIntVal(): int { return this.intVal }
                    setIntVal(v: int): void { this.intVal = v }
                    isBoolVal(): boolean { return this.boolVal }
                    setBoolVal(v: boolean): void { this.boolVal = v }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.PrimitiveHolder");
        Class<?> implClass = runner.getClass("com.PrimitiveImpl");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Check interface method return types
        assertThat(interfaceClass.getMethod("getIntVal").getReturnType()).isEqualTo(int.class);
        assertThat(interfaceClass.getMethod("isBoolVal").getReturnType()).isEqualTo(boolean.class);

        // Check interface setters exist
        assertThat(interfaceClass.getMethod("setIntVal", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("setBoolVal", boolean.class)).isNotNull();

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Set values
        implClass.getMethod("setIntVal", int.class).invoke(instance, 1000);
        implClass.getMethod("setBoolVal", boolean.class).invoke(instance, true);

        // Verify values
        assertThat(implClass.getMethod("getIntVal").invoke(instance)).isEqualTo(1000);
        assertThat((Boolean) implClass.getMethod("isBoolVal").invoke(instance)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceReadonlyPropertyWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with readonly property (no setter) and implementation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Immutable {
                    readonly id: int
                    name: String
                  }
                  export class ImmutableImpl implements Immutable {
                    id: int
                    name: String = ""
                    constructor(id: int) {
                      this.id = id
                    }
                    getId(): int { return this.id }
                    getName(): String { return this.name }
                    setName(name: String): void { this.name = name }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Immutable");
        Class<?> implClass = runner.getClass("com.ImmutableImpl");

        assertThat(interfaceClass.isInterface()).isTrue();

        // readonly property should only have getter on interface
        Method getId = interfaceClass.getMethod("getId");
        assertThat(getId).isNotNull();
        assertThat(getId.getReturnType()).isEqualTo(int.class);

        // readonly property should NOT have setter on interface
        assertThatThrownBy(() ->
                interfaceClass.getMethod("setId", int.class)).isInstanceOf(NoSuchMethodException.class);

        // Non-readonly property should have both getter and setter
        assertThat(interfaceClass.getMethod("getName")).isNotNull();
        assertThat(interfaceClass.getMethod("setName", String.class)).isNotNull();

        // Test implementation
        Object instance = implClass.getConstructor(int.class).newInstance(42);

        // Readonly value should be set from constructor
        assertThat(implClass.getMethod("getId").invoke(instance)).isEqualTo(42);

        // Mutable property can be changed
        implClass.getMethod("setName", String.class).invoke(instance, "Test");
        assertThat(implClass.getMethod("getName").invoke(instance)).isEqualTo("Test");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithMethodImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with a method signature and implementation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Calculator {
                    add(a: int, b: int): int
                  }
                  export class SimpleCalculator implements Calculator {
                    add(a: int, b: int): int {
                      return a + b
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Calculator");
        Class<?> implClass = runner.getClass("com.SimpleCalculator");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Check method exists on interface
        Method add = interfaceClass.getMethod("add", int.class, int.class);
        assertThat(add).isNotNull();
        assertThat(Modifier.isAbstract(add.getModifiers())).isTrue();
        assertThat(add.getReturnType()).isEqualTo(int.class);

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Test method
        assertThat(implClass.getMethod("add", int.class, int.class).invoke(instance, 2, 3)).isEqualTo(5);
        assertThat(implClass.getMethod("add", int.class, int.class).invoke(instance, 40, 60)).isEqualTo(100);
        assertThat(implClass.getMethod("add", int.class, int.class).invoke(instance, -10, 5)).isEqualTo(-5);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithMixedMembersImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both properties and methods, with implementation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Entity {
                    id: int
                    readonly version: int
                    save(): void
                    update(data: String): boolean
                  }
                  export class EntityImpl implements Entity {
                    id: int = 0
                    version: int
                    saved: boolean = false
                    lastData: String = ""
                    constructor(version: int) {
                      this.version = version
                    }
                    getId(): int { return this.id }
                    setId(id: int): void { this.id = id }
                    getVersion(): int { return this.version }
                    save(): void { this.saved = true }
                    update(data: String): boolean {
                      this.lastData = data
                      return true
                    }
                    isSaved(): boolean { return this.saved }
                    getLastData(): String { return this.lastData }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Entity");
        Class<?> implClass = runner.getClass("com.EntityImpl");

        assertThat(interfaceClass.isInterface()).isTrue();

        // Property getters/setters on interface
        assertThat(interfaceClass.getMethod("getId")).isNotNull();
        assertThat(interfaceClass.getMethod("setId", int.class)).isNotNull();
        assertThat(interfaceClass.getMethod("getVersion")).isNotNull();
        assertThatThrownBy(() ->
                interfaceClass.getMethod("setVersion", int.class)).isInstanceOf(NoSuchMethodException.class);
        // readonly

        // Methods on interface
        assertThat(interfaceClass.getMethod("save")).isNotNull();
        Method update = interfaceClass.getMethod("update", String.class);
        assertThat(update.getReturnType()).isEqualTo(boolean.class);

        // Test implementation
        Object instance = implClass.getConstructor(int.class).newInstance(1);

        // Test properties
        assertThat(implClass.getMethod("getId").invoke(instance)).isEqualTo(0);
        assertThat(implClass.getMethod("getVersion").invoke(instance)).isEqualTo(1);

        implClass.getMethod("setId", int.class).invoke(instance, 42);
        assertThat(implClass.getMethod("getId").invoke(instance)).isEqualTo(42);

        // Test methods
        assertThat((Boolean) implClass.getMethod("isSaved").invoke(instance)).isFalse();
        implClass.getMethod("save").invoke(instance);
        assertThat((Boolean) implClass.getMethod("isSaved").invoke(instance)).isTrue();

        assertThat((Boolean) implClass.getMethod("update", String.class).invoke(instance, "test data")).isTrue();
        assertThat(implClass.getMethod("getLastData").invoke(instance)).isEqualTo("test data");
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithVoidMethodImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with void method and implementation
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Logger {
                    log(message: String): void
                  }
                  export class SimpleLogger implements Logger {
                    lastMessage: String = ""
                    log(message: String): void {
                      this.lastMessage = message
                    }
                    getLastMessage(): String {
                      return this.lastMessage
                    }
                  }
                }""");
        Class<?> interfaceClass = runner.getClass("com.Logger");
        Class<?> implClass = runner.getClass("com.SimpleLogger");

        assertThat(interfaceClass.isInterface()).isTrue();

        Method log = interfaceClass.getMethod("log", String.class);
        assertThat(log).isNotNull();
        assertThat(log.getReturnType()).isEqualTo(void.class);

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Initial state
        assertThat(implClass.getMethod("getLastMessage").invoke(instance)).isEqualTo("");

        // Call log method
        implClass.getMethod("log", String.class).invoke(instance, "Hello World");

        // Verify the message was logged
        assertThat(implClass.getMethod("getLastMessage").invoke(instance)).isEqualTo("Hello World");
    }
}
