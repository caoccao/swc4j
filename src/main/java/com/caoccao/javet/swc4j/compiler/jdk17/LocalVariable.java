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

/**
 * Represents a local variable in the compilation context.
 *
 * @param name        the variable name
 * @param type        the JVM type descriptor
 * @param index       the local variable slot index
 * @param mutable     true if declared with 'let' or 'var' (can be reassigned)
 * @param needsHolder true if this mutable variable is captured by a lambda and modified
 * @param holderIndex the slot index for the holder array (only valid if needsHolder is true)
 */
public record LocalVariable(String name, String type, int index, boolean mutable, boolean needsHolder,
                            int holderIndex) {

    /**
     * Creates a simple immutable variable (backward compatible constructor).
     *
     * @param name  the name
     * @param type  the type
     * @param index the index
     */
    public LocalVariable(String name, String type, int index) {
        this(name, type, index, false, false, -1);
    }

    /**
     * Creates a variable with mutability tracking.
     *
     * @param name    the name
     * @param type    the type
     * @param index   the index
     * @param mutable the mutable
     */
    public LocalVariable(String name, String type, int index, boolean mutable) {
        this(name, type, index, mutable, false, -1);
    }

    /**
     * Gets the holder type descriptor for this variable's type.
     * For example, "I" (int) becomes "[I" (int[]).
     *
     * @return the holder array type descriptor
     */
    public String getHolderType() {
        return TypeConversionUtils.ARRAY_PREFIX + type;
    }

    /**
     * Creates a copy of this variable with holder information.
     *
     * @param holderIndex the slot index for the holder array
     * @return a new LocalVariable with needsHolder=true and the given holder index
     */
    public LocalVariable withHolder(int holderIndex) {
        return new LocalVariable(name, type, index, mutable, true, holderIndex);
    }
}
