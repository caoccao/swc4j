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

/**
 * Represents a variable captured by a lambda/arrow function.
 * Captured variables are stored as fields in the lambda class.
 *
 * @param name         the variable name in source code
 * @param fieldName    the field name in the lambda class (e.g., "captured$x")
 * @param type         the JVM type descriptor (e.g., "I", "Ljava/lang/String;", or "[I" for holder)
 * @param isHolder     true if this capture is via a holder array (for mutable captures)
 * @param originalType the original variable type before holder wrapping (e.g., "I" for int)
 */
public record CapturedVariable(String name, String fieldName, String type, boolean isHolder, String originalType) {

    /**
     * Creates a simple captured variable (backward compatible constructor).
     *
     * @param name      the variable name in source code
     * @param fieldName the field name in the lambda class
     * @param type      the JVM type descriptor
     */
    public CapturedVariable(String name, String fieldName, String type) {
        this(name, fieldName, type, false, type);
    }
}
