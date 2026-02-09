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
import com.caoccao.javet.swc4j.compiler.memory.FieldInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;

/**
 * Utility class for field lookup operations in the class hierarchy.
 */
public final class FieldLookupUtils {

    private FieldLookupUtils() {
    }

    /**
     * Looks up a field in the class hierarchy, starting from the given class and traversing up to parent classes.
     *
     * @param typeInfo  the starting class type info
     * @param fieldName the field name to look up
     * @return the lookup result containing the field info and owner class, or null if not found
     */
    public static FieldLookupResult lookupFieldInHierarchy(JavaTypeInfo typeInfo, String fieldName) {
        // First check in current class
        FieldInfo fieldInfo = typeInfo.getField(fieldName);
        if (fieldInfo != null) {
            return new FieldLookupResult(fieldInfo, typeInfo.getInternalName());
        }

        // Check in parent classes
        for (JavaTypeInfo parentInfo : typeInfo.getParentTypeInfos()) {
            FieldLookupResult result = lookupFieldInHierarchy(parentInfo, fieldName);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Resolves a {@link JavaTypeInfo} by its internal name (e.g., "com/example/MyClass").
     * First tries the qualified name (dot-separated), then falls back to the simple class name.
     *
     * @param compiler     the bytecode compiler providing access to the type registry
     * @param internalName the internal name of the class (slash-separated)
     * @return the resolved type info, or null if not found
     */
    public static JavaTypeInfo resolveTypeInfoByInternalName(ByteCodeCompiler compiler, String internalName) {
        String qualifiedName = internalName.replace('/', '.');
        JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
        if (typeInfo == null) {
            int lastSlash = internalName.lastIndexOf('/');
            String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
            typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
        }
        return typeInfo;
    }

    /**
     * Result of a field lookup in the class hierarchy.
     *
     * @param fieldInfo         the field info
     * @param ownerInternalName the internal name of the class that owns the field
     */
    public record FieldLookupResult(FieldInfo fieldInfo, String ownerInternalName) {
    }
}
