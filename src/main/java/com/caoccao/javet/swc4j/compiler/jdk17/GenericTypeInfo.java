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

import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import java.util.Objects;

/**
 * Holds parsed generic type information for types like {@code Record<K, V>}.
 * Used by object literal compilation to validate and generate typed LinkedHashMap instances.
 * Also supports Array value types like {@code Record<string, Array<number>>}.
 */
public final class GenericTypeInfo {
    private final String arrayElementType;
    private final boolean isArrayValue;
    private final boolean isNested;
    private final String keyType;
    private final GenericTypeInfo nestedTypeInfo;
    private final String valueType;

    private GenericTypeInfo(
            String keyType,
            String valueType,
            boolean isNested,
            GenericTypeInfo nestedTypeInfo,
            boolean isArrayValue,
            String arrayElementType) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.isNested = isNested;
        this.nestedTypeInfo = nestedTypeInfo;
        this.isArrayValue = isArrayValue;
        this.arrayElementType = arrayElementType;
    }

    /**
     * Create a simple generic type info for {@code Record<K, V>} without nested types.
     *
     * @param keyType   JVM type descriptor for key type (e.g., "Ljava/lang/String;")
     * @param valueType JVM type descriptor for value type (e.g., "Ljava/lang/Integer;")
     * @return GenericTypeInfo instance
     */
    public static GenericTypeInfo of(String keyType, String valueType) {
        return new GenericTypeInfo(keyType, valueType, false, null, false, null);
    }

    /**
     * Create an array-valued generic type info for types like {@code Record<string, Array<number>>}.
     *
     * @param keyType          JVM type descriptor for key type (e.g., "Ljava/lang/String;")
     * @param arrayElementType JVM type descriptor for array element type (e.g., "I" for int)
     * @return GenericTypeInfo instance
     */
    public static GenericTypeInfo ofArray(String keyType, String arrayElementType) {
        Objects.requireNonNull(arrayElementType, "arrayElementType cannot be null");
        return new GenericTypeInfo(keyType, ConstantJavaType.LJAVA_UTIL_ARRAYLIST, false, null, true, arrayElementType);
    }

    /**
     * Create a nested generic type info for types like {@code Record<string, Record<string, number>>}.
     *
     * @param keyType        JVM type descriptor for key type
     * @param nestedTypeInfo Nested generic type information for the value type
     * @return GenericTypeInfo instance
     */
    public static GenericTypeInfo ofNested(String keyType, GenericTypeInfo nestedTypeInfo) {
        Objects.requireNonNull(nestedTypeInfo, "nestedTypeInfo cannot be null");
        return new GenericTypeInfo(keyType, ConstantJavaType.LJAVA_UTIL_LINKEDHASHMAP, true, nestedTypeInfo, false, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GenericTypeInfo other)) return false;
        return Objects.equals(keyType, other.keyType)
                && Objects.equals(valueType, other.valueType)
                && isNested == other.isNested
                && Objects.equals(nestedTypeInfo, other.nestedTypeInfo)
                && isArrayValue == other.isArrayValue
                && Objects.equals(arrayElementType, other.arrayElementType);
    }

    /**
     * Get the JVM type descriptor for the array element type (if this is an Array value type).
     *
     * @return array element type descriptor (e.g., "I" for int, "Ljava/lang/String;" for String), or null if not an array value
     */
    public String getArrayElementType() {
        return arrayElementType;
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
        return Objects.hash(keyType, valueType, isNested, nestedTypeInfo, isArrayValue, arrayElementType);
    }

    /**
     * Check if the value type is an Array type (e.g., {@code Record<string, Array<number>>}).
     *
     * @return true if value type is an Array type
     */
    public boolean isArrayValue() {
        return isArrayValue;
    }

    /**
     * Check if the value type is itself a nested generic type (e.g., {@code Record<string, Record<...>>}).
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
        if (isArrayValue) {
            return "Record<" + keyType + ", Array<" + arrayElementType + ">>";
        }
        return "Record<" + keyType + ", " + valueType + ">";
    }
}
