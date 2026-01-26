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

import static org.junit.jupiter.api.Assertions.*;

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
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  declare interface External {
                    value: int
                  }
                }""");

        // Ambient declarations should not generate bytecode
        assertNull(map.get("com.External"));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testBasicInterfaceWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Basic interface with a single property and implementing class
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.Person");
        Class<?> implClass = classes.get("com.PersonImpl");

        // Verify it's an interface
        assertTrue(interfaceClass.isInterface());

        // Verify getter and setter methods exist on interface
        Method getName = interfaceClass.getMethod("getName");
        assertNotNull(getName);
        assertTrue(Modifier.isAbstract(getName.getModifiers()));
        assertEquals(String.class, getName.getReturnType());

        Method setName = interfaceClass.getMethod("setName", String.class);
        assertNotNull(setName);
        assertTrue(Modifier.isAbstract(setName.getModifiers()));
        assertEquals(void.class, setName.getReturnType());

        // Test the implementation - create instance and verify getter/setter
        Object instance = implClass.getConstructor().newInstance();

        // Initial value should be empty string
        assertEquals("", implClass.getMethod("getName").invoke(instance));

        // Set a new value
        implClass.getMethod("setName", String.class).invoke(instance, "John");

        // Get the value back
        assertEquals("John", implClass.getMethod("getName").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testEmptyInterface(JdkVersion jdkVersion) throws Exception {
        // Test: Empty interface (marker interface)
        var map = getCompiler(jdkVersion).compile("""
                namespace com {
                  export interface Marker {
                  }
                }""");
        Class<?> interfaceClass = loadClass(map.get("com.Marker"));

        assertTrue(interfaceClass.isInterface());
        // Only inherited methods from Object
        assertEquals(0, interfaceClass.getDeclaredMethods().length);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceAllPrimitiveTypes(JdkVersion jdkVersion) throws Exception {
        // Test: Interface declares all primitive types (no implementation needed)
        var map = getCompiler(jdkVersion).compile("""
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
        Class<?> interfaceClass = loadClass(map.get("com.AllPrimitives"));

        assertTrue(interfaceClass.isInterface());

        // Verify all getter return types
        assertEquals(byte.class, interfaceClass.getMethod("getByteVal").getReturnType());
        assertEquals(short.class, interfaceClass.getMethod("getShortVal").getReturnType());
        assertEquals(int.class, interfaceClass.getMethod("getIntVal").getReturnType());
        assertEquals(long.class, interfaceClass.getMethod("getLongVal").getReturnType());
        assertEquals(float.class, interfaceClass.getMethod("getFloatVal").getReturnType());
        assertEquals(double.class, interfaceClass.getMethod("getDoubleVal").getReturnType());
        assertEquals(char.class, interfaceClass.getMethod("getCharVal").getReturnType());
        assertEquals(boolean.class, interfaceClass.getMethod("isBoolVal").getReturnType());

        // Verify all setters exist
        assertNotNull(interfaceClass.getMethod("setByteVal", byte.class));
        assertNotNull(interfaceClass.getMethod("setShortVal", short.class));
        assertNotNull(interfaceClass.getMethod("setIntVal", int.class));
        assertNotNull(interfaceClass.getMethod("setLongVal", long.class));
        assertNotNull(interfaceClass.getMethod("setFloatVal", float.class));
        assertNotNull(interfaceClass.getMethod("setDoubleVal", double.class));
        assertNotNull(interfaceClass.getMethod("setCharVal", char.class));
        assertNotNull(interfaceClass.getMethod("setBoolVal", boolean.class));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceBooleanPropertyWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with boolean property (should use 'is' prefix) and implementation
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.Toggleable");
        Class<?> implClass = classes.get("com.Switch");

        assertTrue(interfaceClass.isInterface());

        // Boolean getter should use 'is' prefix
        Method isEnabled = interfaceClass.getMethod("isEnabled");
        assertNotNull(isEnabled);
        assertEquals(boolean.class, isEnabled.getReturnType());

        // Setter should still use 'set' prefix
        Method setEnabled = interfaceClass.getMethod("setEnabled", boolean.class);
        assertNotNull(setEnabled);

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Initial value should be false
        assertEquals(false, implClass.getMethod("isEnabled").invoke(instance));

        // Set to true
        implClass.getMethod("setEnabled", boolean.class).invoke(instance, true);

        // Verify change
        assertEquals(true, implClass.getMethod("isEnabled").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceMultiplePropertiesWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with multiple properties and implementing class
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.User");
        Class<?> implClass = classes.get("com.UserImpl");

        assertTrue(interfaceClass.isInterface());

        // Check all getters on interface
        assertEquals(int.class, interfaceClass.getMethod("getId").getReturnType());
        assertEquals(String.class, interfaceClass.getMethod("getName").getReturnType());
        assertEquals(boolean.class, interfaceClass.getMethod("isActive").getReturnType());

        // Check all setters on interface
        assertNotNull(interfaceClass.getMethod("setId", int.class));
        assertNotNull(interfaceClass.getMethod("setName", String.class));
        assertNotNull(interfaceClass.getMethod("setActive", boolean.class));

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Verify initial values
        assertEquals(0, implClass.getMethod("getId").invoke(instance));
        assertEquals("", implClass.getMethod("getName").invoke(instance));
        assertEquals(false, implClass.getMethod("isActive").invoke(instance));

        // Set new values
        implClass.getMethod("setId", int.class).invoke(instance, 42);
        implClass.getMethod("setName", String.class).invoke(instance, "Alice");
        implClass.getMethod("setActive", boolean.class).invoke(instance, true);

        // Verify changes
        assertEquals(42, implClass.getMethod("getId").invoke(instance));
        assertEquals("Alice", implClass.getMethod("getName").invoke(instance));
        assertEquals(true, implClass.getMethod("isActive").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfacePrimitiveTypesWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with primitive types and simple implementation
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.PrimitiveHolder");
        Class<?> implClass = classes.get("com.PrimitiveImpl");

        assertTrue(interfaceClass.isInterface());

        // Check interface method return types
        assertEquals(int.class, interfaceClass.getMethod("getIntVal").getReturnType());
        assertEquals(boolean.class, interfaceClass.getMethod("isBoolVal").getReturnType());

        // Check interface setters exist
        assertNotNull(interfaceClass.getMethod("setIntVal", int.class));
        assertNotNull(interfaceClass.getMethod("setBoolVal", boolean.class));

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Set values
        implClass.getMethod("setIntVal", int.class).invoke(instance, 1000);
        implClass.getMethod("setBoolVal", boolean.class).invoke(instance, true);

        // Verify values
        assertEquals(1000, implClass.getMethod("getIntVal").invoke(instance));
        assertEquals(true, implClass.getMethod("isBoolVal").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceReadonlyPropertyWithImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with readonly property (no setter) and implementation
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.Immutable");
        Class<?> implClass = classes.get("com.ImmutableImpl");

        assertTrue(interfaceClass.isInterface());

        // readonly property should only have getter on interface
        Method getId = interfaceClass.getMethod("getId");
        assertNotNull(getId);
        assertEquals(int.class, getId.getReturnType());

        // readonly property should NOT have setter on interface
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("setId", int.class));

        // Non-readonly property should have both getter and setter
        assertNotNull(interfaceClass.getMethod("getName"));
        assertNotNull(interfaceClass.getMethod("setName", String.class));

        // Test implementation
        Object instance = implClass.getConstructor(int.class).newInstance(42);

        // Readonly value should be set from constructor
        assertEquals(42, implClass.getMethod("getId").invoke(instance));

        // Mutable property can be changed
        implClass.getMethod("setName", String.class).invoke(instance, "Test");
        assertEquals("Test", implClass.getMethod("getName").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithMethodImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with a method signature and implementation
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.Calculator");
        Class<?> implClass = classes.get("com.SimpleCalculator");

        assertTrue(interfaceClass.isInterface());

        // Check method exists on interface
        Method add = interfaceClass.getMethod("add", int.class, int.class);
        assertNotNull(add);
        assertTrue(Modifier.isAbstract(add.getModifiers()));
        assertEquals(int.class, add.getReturnType());

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Test method
        assertEquals(5, implClass.getMethod("add", int.class, int.class).invoke(instance, 2, 3));
        assertEquals(100, implClass.getMethod("add", int.class, int.class).invoke(instance, 40, 60));
        assertEquals(-5, implClass.getMethod("add", int.class, int.class).invoke(instance, -10, 5));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithMixedMembersImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with both properties and methods, with implementation
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.Entity");
        Class<?> implClass = classes.get("com.EntityImpl");

        assertTrue(interfaceClass.isInterface());

        // Property getters/setters on interface
        assertNotNull(interfaceClass.getMethod("getId"));
        assertNotNull(interfaceClass.getMethod("setId", int.class));
        assertNotNull(interfaceClass.getMethod("getVersion"));
        assertThrows(NoSuchMethodException.class, () ->
                interfaceClass.getMethod("setVersion", int.class)); // readonly

        // Methods on interface
        assertNotNull(interfaceClass.getMethod("save"));
        Method update = interfaceClass.getMethod("update", String.class);
        assertEquals(boolean.class, update.getReturnType());

        // Test implementation
        Object instance = implClass.getConstructor(int.class).newInstance(1);

        // Test properties
        assertEquals(0, implClass.getMethod("getId").invoke(instance));
        assertEquals(1, implClass.getMethod("getVersion").invoke(instance));

        implClass.getMethod("setId", int.class).invoke(instance, 42);
        assertEquals(42, implClass.getMethod("getId").invoke(instance));

        // Test methods
        assertEquals(false, implClass.getMethod("isSaved").invoke(instance));
        implClass.getMethod("save").invoke(instance);
        assertEquals(true, implClass.getMethod("isSaved").invoke(instance));

        assertEquals(true, implClass.getMethod("update", String.class).invoke(instance, "test data"));
        assertEquals("test data", implClass.getMethod("getLastData").invoke(instance));
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testInterfaceWithVoidMethodImplementation(JdkVersion jdkVersion) throws Exception {
        // Test: Interface with void method and implementation
        var map = getCompiler(jdkVersion).compile("""
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
        var classes = loadClasses(map);
        Class<?> interfaceClass = classes.get("com.Logger");
        Class<?> implClass = classes.get("com.SimpleLogger");

        assertTrue(interfaceClass.isInterface());

        Method log = interfaceClass.getMethod("log", String.class);
        assertNotNull(log);
        assertEquals(void.class, log.getReturnType());

        // Test implementation
        Object instance = implClass.getConstructor().newInstance();

        // Initial state
        assertEquals("", implClass.getMethod("getLastMessage").invoke(instance));

        // Call log method
        implClass.getMethod("log", String.class).invoke(instance, "Hello World");

        // Verify the message was logged
        assertEquals("Hello World", implClass.getMethod("getLastMessage").invoke(instance));
    }
}
