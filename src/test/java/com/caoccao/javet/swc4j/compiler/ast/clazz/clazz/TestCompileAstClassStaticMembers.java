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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompileAstClassStaticMembers extends BaseTestCompileSuite {

    /**
     * Helper to invoke an action and then return the result of another call.
     */
    private static <T> T invokeAfter(ThrowingRunnable action, ThrowingSupplier<T> result) throws Exception {
        action.run();
        return result.get();
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testMixedStaticAndInstanceFields(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class MixedClass {
                    static instanceCount: int = 0
                    id: int
                    constructor() {
                      MixedClass.instanceCount = MixedClass.instanceCount + 1
                      this.id = MixedClass.instanceCount
                    }
                    static getInstanceCount(): int { return MixedClass.instanceCount }
                    getId(): int { return this.id }
                  }
                }""");
        var staticRunner = runner.createStaticRunner("com.MixedClass");
        assertEquals(0, (int) staticRunner.invoke("getInstanceCount"));

        var instanceRunner1 = runner.createInstanceRunner("com.MixedClass");
        assertEquals(
                Map.of("count", 1, "id", 1),
                Map.of(
                        "count", staticRunner.invoke("getInstanceCount"),
                        "id", (int) instanceRunner1.invoke("getId")
                )
        );

        var instanceRunner2 = runner.createInstanceRunner("com.MixedClass");
        assertEquals(
                Map.of("count", 2, "id", 2),
                Map.of(
                        "count", staticRunner.invoke("getInstanceCount"),
                        "id", (int) instanceRunner2.invoke("getId")
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticFieldIncrement(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Counter {
                    static count: int = 0
                    static getCount(): int {
                      return Counter.count
                    }
                    static increment(): void {
                      Counter.count = Counter.count + 1
                    }
                    static decrement(): void {
                      Counter.count = Counter.count - 1
                    }
                  }
                }""");
        Class<?> classCounter = runner.getClass("com.Counter");

        assertEquals(
                List.of(0, 1, 2, 1),
                List.of(
                        classCounter.getMethod("getCount").invoke(null),
                        invokeAfter(() -> classCounter.getMethod("increment").invoke(null),
                                () -> classCounter.getMethod("getCount").invoke(null)),
                        invokeAfter(() -> classCounter.getMethod("increment").invoke(null),
                                () -> classCounter.getMethod("getCount").invoke(null)),
                        invokeAfter(() -> classCounter.getMethod("decrement").invoke(null),
                                () -> classCounter.getMethod("getCount").invoke(null))
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticFieldWithDifferentTypes(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Settings {
                    static maxUsers: int = 1000
                    static timeout: double = 30.5
                    static enabled: boolean = true
                    static appName: String = "MyApp"
                
                    static getMaxUsers(): int { return Settings.maxUsers }
                    static getTimeout(): double { return Settings.timeout }
                    static isEnabled(): boolean { return Settings.enabled }
                    static getAppName(): String { return Settings.appName }
                  }
                }""");
        Class<?> classSettings = runner.getClass("com.Settings");
        assertEquals(
                Map.of(
                        "maxUsers", 1000,
                        "timeout", 30.5,
                        "enabled", true,
                        "appName", "MyApp"
                ),
                Map.of(
                        "maxUsers", classSettings.getMethod("getMaxUsers").invoke(null),
                        "timeout", classSettings.getMethod("getTimeout").invoke(null),
                        "enabled", classSettings.getMethod("isEnabled").invoke(null),
                        "appName", classSettings.getMethod("getAppName").invoke(null)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticFieldWithExpressionInitializer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Constants {
                    static PI: double = 3.14159
                    static DOUBLE_PI: double = 3.14159 * 2
                    static getPI(): double { return Constants.PI }
                    static getDoublePI(): double { return Constants.DOUBLE_PI }
                  }
                }""");
        Class<?> classConstants = runner.getClass("com.Constants");
        assertEquals(3.14159, classConstants.getMethod("getPI").invoke(null));
        assertEquals(6.28318, (double) classConstants.getMethod("getDoublePI").invoke(null), 0.00001);
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticFieldWithInitializer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Config {
                    static maxSize: int = 100
                    static name: String = "default"
                    static getMaxSize(): int { return Config.maxSize }
                    static getName(): String { return Config.name }
                  }
                }""");
        Class<?> classConfig = runner.getClass("com.Config");
        assertEquals(
                List.of(100, "default"),
                List.of(
                        classConfig.getMethod("getMaxSize").invoke(null),
                        classConfig.getMethod("getName").invoke(null)
                )
        );
    }

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testStaticFieldWithoutInitializer(JdkVersion jdkVersion) throws Exception {
        var runner = getCompiler(jdkVersion).compile("""
                namespace com {
                  export class Tracker {
                    static count: int
                    static getCount(): int { return Tracker.count }
                    static setCount(v: int): void { Tracker.count = v }
                  }
                }""");
        Class<?> classTracker = runner.getClass("com.Tracker");
        // Default value for uninitialized static int is 0
        assertEquals(0, classTracker.getMethod("getCount").invoke(null));
        classTracker.getMethod("setCount", int.class).invoke(null, 42);
        assertEquals(42, classTracker.getMethod("getCount").invoke(null));
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
