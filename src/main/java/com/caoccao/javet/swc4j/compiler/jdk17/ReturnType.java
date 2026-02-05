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

/**
 * The enum Return type.
 */
public enum ReturnType {
    /**
     * Boolean return type.
     */
    BOOLEAN(3),
    /**
     * Byte return type.
     */
    BYTE(3),
    /**
     * Char return type.
     */
    CHAR(3),
    /**
     * Double return type.
     */
    DOUBLE(4),
    /**
     * Float return type.
     */
    FLOAT(3),
    /**
     * Int return type.
     */
    INT(3),
    /**
     * Long return type.
     */
    LONG(4),
    /**
     * Object return type.
     */
    OBJECT(3),
    /**
     * Short return type.
     */
    SHORT(3),
    /**
     * String return type.
     */
    STRING(3),
    /**
     * Void return type.
     */
    VOID(0),
    ;

    private final int minStack;

    ReturnType(int minStack) {
        this.minStack = minStack;
    }

    /**
     * Gets min stack.
     *
     * @return the min stack
     */
    public int getMinStack() {
        return minStack;
    }
}
