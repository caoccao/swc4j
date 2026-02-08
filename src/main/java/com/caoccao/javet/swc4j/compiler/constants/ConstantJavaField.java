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

package com.caoccao.javet.swc4j.compiler.constants;

/**
 * Java field name constants for bytecode generation.
 * <p>
 * This class contains constants for field names and property names used in bytecode operations,
 * including exception destructuring and other runtime operations.
 *
 * @since 1.7.0
 */
public final class ConstantJavaField {
    /**
     * The constant FIELD_RAW - raw field name for TemplateStringsArray.
     *
     * @since 1.7.0
     */
    public static final String FIELD_RAW = "raw";
    /**
     * The constant PROPERTY_CAUSE - exception cause property name.
     *
     * @since 1.7.0
     */
    public static final String PROPERTY_CAUSE = "cause";
    /**
     * The constant PROPERTY_MESSAGE - exception message property name.
     *
     * @since 1.7.0
     */
    public static final String PROPERTY_MESSAGE = "message";
    /**
     * The constant PROPERTY_NAME - exception name property name.
     *
     * @since 1.7.0
     */
    public static final String PROPERTY_NAME = "name";
    /**
     * The constant PROPERTY_STACK - exception stack property name.
     *
     * @since 1.7.0
     */
    public static final String PROPERTY_STACK = "stack";
    /**
     * The constant TEMP_VAR_CATCH_EXCEPTION - temporary variable prefix for catch exception.
     *
     * @since 1.7.0
     */
    public static final String TEMP_VAR_CATCH_EXCEPTION = "$catchException$";
    /**
     * The constant TEMP_VAR_FINALLY_EXCEPTION - temporary variable prefix for finally exception.
     *
     * @since 1.7.0
     */
    public static final String TEMP_VAR_FINALLY_EXCEPTION = "$finallyException$";

    private ConstantJavaField() {
    }
}
