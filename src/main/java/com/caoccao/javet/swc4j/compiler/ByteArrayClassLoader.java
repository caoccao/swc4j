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

public class ByteArrayClassLoader extends ClassLoader {
    protected final Map<String, byte[]> byteCodeMap;
    protected final Map<String, Class<?>> classMap;
    protected final Object lock = new Object();

    public ByteArrayClassLoader(Map<String, byte[]> byteCodeMap, ClassLoader parent) {
        super(parent);
        this.byteCodeMap = new HashMap<>(byteCodeMap);
        classMap = new HashMap<>();
    }

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

    public Class<?> getClass(String name) throws ClassNotFoundException {
        return findClass(name);
    }
}
