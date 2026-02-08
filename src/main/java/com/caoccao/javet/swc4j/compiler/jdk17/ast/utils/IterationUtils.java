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
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.compiler.utils.TypeConversionUtils;

/**
 * Utility class for iteration type detection used by for-of and for-in loop processors.
 */
public final class IterationUtils {
    private IterationUtils() {
    }

    /**
     * Resolves the iteration type from a type descriptor by checking against known
     * iterable types (String, arrays, List, Set, Map).
     *
     * @param compiler       the bytecode compiler (for type registry access)
     * @param typeDescriptor the type descriptor to resolve
     * @return the resolved iteration type, or null if the type is not iterable
     */
    public static IterationType resolveIterationType(ByteCodeCompiler compiler, String typeDescriptor) {
        if (typeDescriptor == null) {
            return null;
        }

        // Check for String type
        if (ConstantJavaType.LJAVA_LANG_STRING.equals(typeDescriptor)) {
            return IterationType.STRING;
        }

        // Check for array types (both primitive and object arrays)
        if (typeDescriptor.startsWith(ConstantJavaType.ARRAY_PREFIX)) {
            return IterationType.ARRAY;
        }

        // For object types, use isAssignableTo() for unified checking
        if (TypeConversionUtils.isObjectDescriptor(typeDescriptor)) {
            String internalName = TypeConversionUtils.descriptorToInternalName(typeDescriptor);
            String qualifiedName = TypeConversionUtils.descriptorToQualifiedName(typeDescriptor);

            // Try to resolve from the registry first
            JavaTypeInfo typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(qualifiedName);
            if (typeInfo == null) {
                // Try simple name
                int lastSlash = internalName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
                typeInfo = compiler.getMemory().getScopedJavaTypeRegistry().resolve(simpleName);
            }

            if (typeInfo == null) {
                // Create a temporary JavaTypeInfo for JDK types not in registry
                int lastSlash = internalName.lastIndexOf('/');
                String simpleName = lastSlash >= 0 ? internalName.substring(lastSlash + 1) : internalName;
                int lastDot = qualifiedName.lastIndexOf('.');
                String packageName = lastDot >= 0 ? qualifiedName.substring(0, lastDot) : "";
                typeInfo = new JavaTypeInfo(simpleName, packageName, internalName);
            }

            // Check assignability using the unified type hierarchy
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_LIST)) {
                return IterationType.LIST;
            }
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_SET)) {
                return IterationType.SET;
            }
            if (typeInfo.isAssignableTo(ConstantJavaType.LJAVA_UTIL_MAP)) {
                return IterationType.MAP;
            }
        }

        return null;
    }

    /**
     * The iteration type for loop processors.
     */
    public enum IterationType {
        /**
         * List iteration type.
         */
        LIST,
        /**
         * Set iteration type.
         */
        SET,
        /**
         * Map iteration type.
         */
        MAP,
        /**
         * String iteration type.
         */
        STRING,
        /**
         * Array iteration type.
         */
        ARRAY
    }
}
