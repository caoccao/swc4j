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

package com.caoccao.javet.swc4j.compiler;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class BaseTestCompileSuite {

    protected ByteCodeCompiler getCompiler(JdkVersion jdkVersion) {
        ByteCodeCompilerOptions options = ByteCodeCompilerOptions.builder()
                .jdkVersion(jdkVersion)
                .debug(true)
                .build();
        return ByteCodeCompiler.of(options);
    }

    protected Class<?> loadClass(byte[] bytes) {
        assertNotNull(bytes);
        return new ByteArrayClassLoader().loadClassFromBytes(bytes);
    }

    /**
     * Load all classes from a bytecode map using a shared class loader.
     * This ensures that classes can reference each other (e.g., a class can reference an enum).
     *
     * @param byteCodeMap map of fully qualified class names to bytecode
     * @return map of fully qualified class names to loaded Class objects
     */
    protected Map<String, Class<?>> loadClasses(Map<String, byte[]> byteCodeMap) throws ClassNotFoundException {
        ByteArrayClassLoader classLoader = new ByteArrayClassLoader();

        // Register all classes with the class loader
        for (Map.Entry<String, byte[]> entry : byteCodeMap.entrySet()) {
            classLoader.addClass(entry.getKey(), entry.getValue());
        }

        // Load all classes
        Map<String, Class<?>> classes = new HashMap<>();
        for (String className : byteCodeMap.keySet()) {
            classes.put(className, classLoader.loadClass(className));
        }

        return classes;
    }
}
