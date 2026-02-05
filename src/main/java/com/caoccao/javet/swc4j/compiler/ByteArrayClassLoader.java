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

/**
 * Custom class loader that loads classes from in-memory bytecode.
 */
public class ByteArrayClassLoader extends ClassLoader {
    /**
     * Map of class names to bytecode.
     */
    protected final Map<String, byte[]> byteCodeMap;
    /**
     * Cache of loaded classes.
     */
    protected final Map<String, Class<?>> classMap;
    /**
     * Lock for thread-safe class loading.
     */
    protected final Object lock = new Object();

    /**
     * Constructs a new bytecode class loader.
     *
     * @param byteCodeMap map of class names to bytecode
     * @param parent      the parent class loader
     */
    public ByteArrayClassLoader(Map<String, byte[]> byteCodeMap, ClassLoader parent) {
        super(parent);
        this.byteCodeMap = new HashMap<>(byteCodeMap);
        classMap = new HashMap<>();
    }

    /**
     * Finds and loads a class by name.
     *
     * @param name the fully qualified class name
     * @return the loaded class
     * @throws ClassNotFoundException if the class cannot be found
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (classMap.containsKey(name)) {
            return classMap.get(name);
        }
        Class<?> clazz;
        synchronized (lock) {
            if (classMap.containsKey(name)) {
                clazz = classMap.get(name);
            } else {
                byte[] bytes = byteCodeMap.get(name);
                if (bytes != null) {
                    clazz = defineClass(name, bytes, 0, bytes.length);
                } else {
                    clazz = super.findClass(name);
                }
                classMap.put(name, clazz);
            }
        }
        return clazz;
    }

    /**
     * Gets a class by name.
     *
     * @param name the fully qualified class name
     * @return the loaded class
     * @throws ClassNotFoundException if the class cannot be found
     */
    public Class<?> getClass(String name) throws ClassNotFoundException {
        return findClass(name);
    }
}
