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

import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;

/**
 * The enum Return type.
 */
public enum ReturnType {
    /**
     * Boolean return type.
     */
    BOOLEAN(3, ConstantJavaType.ABBR_BOOLEAN, ConstantJavaType.ABBR_BOOLEAN),
    /**
     * Byte return type.
     */
    BYTE(3, ConstantJavaType.ABBR_BYTE, ConstantJavaType.ABBR_BYTE),
    /**
     * Char return type.
     */
    CHAR(3, ConstantJavaType.ABBR_CHARACTER, ConstantJavaType.ABBR_CHARACTER),
    /**
     * Double return type.
     */
    DOUBLE(4, ConstantJavaType.ABBR_DOUBLE, ConstantJavaType.ABBR_DOUBLE),
    /**
     * Float return type.
     */
    FLOAT(3, ConstantJavaType.ABBR_FLOAT, ConstantJavaType.ABBR_FLOAT),
    /**
     * Int return type.
     */
    INT(3, ConstantJavaType.ABBR_INTEGER, ConstantJavaType.ABBR_INTEGER),
    /**
     * Long return type.
     */
    LONG(4, ConstantJavaType.ABBR_LONG, ConstantJavaType.ABBR_LONG),
    /**
     * Object return type.
     */
    OBJECT(3, ConstantJavaType.ABBR_VOID, ConstantJavaType.LJAVA_LANG_OBJECT),
    /**
     * Short return type.
     */
    SHORT(3, ConstantJavaType.ABBR_SHORT, ConstantJavaType.ABBR_SHORT),
    /**
     * String return type.
     */
    STRING(3, ConstantJavaType.ABBR_VOID, ConstantJavaType.LJAVA_LANG_STRING),
    /**
     * Void return type.
     */
    VOID(0, ConstantJavaType.ABBR_VOID, ConstantJavaType.ABBR_VOID),
    ;

    private final String descriptor;
    private final int minStack;
    private final String primitiveDescriptor;

    ReturnType(int minStack, String primitiveDescriptor, String descriptor) {
        this.minStack = minStack;
        this.primitiveDescriptor = primitiveDescriptor;
        this.descriptor = descriptor;
    }

    /**
     * Gets descriptor.
     *
     * @return the descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * Gets min stack.
     *
     * @return the min stack
     */
    public int getMinStack() {
        return minStack;
    }

    /**
     * Gets primitive descriptor.
     *
     * @return the primitive descriptor
     */
    public String getPrimitiveDescriptor() {
        return primitiveDescriptor;
    }
}
