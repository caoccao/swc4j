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

package com.caoccao.javet.swc4j.compiler.memory;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;

import java.util.Optional;

/**
 * Stores metadata about a class field.
 *
 * @param name        the field name
 * @param descriptor  the JVM type descriptor (e.g., "I" for int, "Ljava/lang/String;" for String)
 * @param isStatic    whether the field is static
 * @param initializer optional initial value expression
 */
public record FieldInfo(
        String name,
        String descriptor,
        boolean isStatic,
        Optional<ISwc4jAstExpr> initializer
) {
    /**
     * Constructs field info without template cache.
     *
     * @param name       the field name
     * @param descriptor the field type descriptor
     * @param isStatic   whether the field is static
     */
    public FieldInfo(String name, String descriptor, boolean isStatic) {
        this(name, descriptor, isStatic, Optional.empty());
    }

    /**
     * Constructs non-static field info without template cache.
     *
     * @param name       the field name
     * @param descriptor the field type descriptor
     */
    public FieldInfo(String name, String descriptor) {
        this(name, descriptor, false, Optional.empty());
    }
}
