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

package com.caoccao.javet.swc4j.compiler.jdk17;

import java.util.Objects;

/**
 * Holds parsed generic type information for types like Record<K, V>.
 * Used by object literal compilation to validate and generate typed LinkedHashMap instances.
 */
public final class GenericTypeInfo {
    private final boolean isNested;
    private final String keyType;
    private final GenericTypeInfo nestedTypeInfo;
    private final String valueType;

    private GenericTypeInfo(
            String keyType,
            String valueType,
            boolean isNested,
            GenericTypeInfo nestedTypeInfo) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.isNested = isNested;
        this.nestedTypeInfo = nestedTypeInfo;
    }

    /**
     * Create a simple generic type info for Record<K, V> without nested types.
     *
     * @param keyType   JVM type descriptor for key type (e.g., "Ljava/lang/String;")
     * @param valueType JVM type descriptor for value type (e.g., "Ljava/lang/Integer;")
     * @return GenericTypeInfo instance
     */
    public static GenericTypeInfo of(String keyType, String valueType) {
        return new GenericTypeInfo(keyType, valueType, false, null);
    }

    /**
     * Create a nested generic type info for types like Record<string, Record<string, number>>.
     *
     * @param keyType        JVM type descriptor for key type
     * @param nestedTypeInfo Nested generic type information for the value type
     * @return GenericTypeInfo instance
     */
    public static GenericTypeInfo ofNested(String keyType, GenericTypeInfo nestedTypeInfo) {
        Objects.requireNonNull(nestedTypeInfo, "nestedTypeInfo cannot be null");
        return new GenericTypeInfo(keyType, "Ljava/util/LinkedHashMap;", true, nestedTypeInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GenericTypeInfo other)) return false;
        return Objects.equals(keyType, other.keyType)
                && Objects.equals(valueType, other.valueType)
                && isNested == other.isNested
                && Objects.equals(nestedTypeInfo, other.nestedTypeInfo);
    }

    /**
     * Get the JVM type descriptor for the key type.
     *
     * @return key type descriptor (e.g., "Ljava/lang/String;" or "Ljava/lang/Integer;")
     */
    public String getKeyType() {
        return keyType;
    }

    /**
     * Get nested type information if this is a nested Record type.
     *
     * @return nested type info, or null if not nested
     */
    public GenericTypeInfo getNestedTypeInfo() {
        return nestedTypeInfo;
    }

    /**
     * Get the JVM type descriptor for the value type.
     *
     * @return value type descriptor (e.g., "Ljava/lang/Integer;" or "Ljava/util/LinkedHashMap;")
     */
    public String getValueType() {
        return valueType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyType, valueType, isNested, nestedTypeInfo);
    }

    /**
     * Check if the value type is itself a nested generic type (e.g., Record<string, Record<...>>).
     *
     * @return true if value type is a nested Record type
     */
    public boolean isNested() {
        return isNested;
    }

    @Override
    public String toString() {
        if (isNested) {
            return "Record<" + keyType + ", " + nestedTypeInfo + ">";
        }
        return "Record<" + keyType + ", " + valueType + ">";
    }
}
