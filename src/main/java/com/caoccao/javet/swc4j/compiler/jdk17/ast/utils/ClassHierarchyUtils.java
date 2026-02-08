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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;

/**
 * Utility class for class hierarchy resolution operations.
 */
public final class ClassHierarchyUtils {

    private ClassHierarchyUtils() {
    }

    /**
     * Resolves the super class internal name for a given class.
     * First attempts resolution using the fully qualified class name,
     * then falls back to the simple class name if the qualified lookup fails.
     *
     * @param compiler                 the bytecode compiler instance
     * @param currentClassInternalName the internal name of the current class (e.g., "com/example/MyClass")
     * @return the internal name of the super class, or null if not found
     */
    public static String resolveSuperClassInternalName(ByteCodeCompiler compiler, String currentClassInternalName) {
        String qualifiedClassName = currentClassInternalName.replace('/', '.');
        String superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveSuperClass(qualifiedClassName);
        if (superClassInternalName == null) {
            int lastSlash = currentClassInternalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0
                    ? currentClassInternalName.substring(lastSlash + 1)
                    : currentClassInternalName;
            superClassInternalName = compiler.getMemory().getScopedJavaTypeRegistry().resolveSuperClass(simpleName);
        }
        return superClassInternalName;
    }
}
