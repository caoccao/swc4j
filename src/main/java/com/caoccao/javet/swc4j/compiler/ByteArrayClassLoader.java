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

public class ByteArrayClassLoader extends ClassLoader {
    public ByteArrayClassLoader(String name, ClassLoader parent) {
        super(name, parent);
    }

    public ByteArrayClassLoader(ClassLoader parent) {
        super(parent);
    }

    public ByteArrayClassLoader() {
        super();
    }

    public Class<?> loadClassFromBytes(byte[] bytes) {
        return defineClass(null, bytes, 0, bytes.length);
    }
}
