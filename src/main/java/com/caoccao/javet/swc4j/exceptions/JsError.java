/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.exceptions;

/**
 * The type Js error.
 * <p>
 * This is the base class for all JavaScript error types.
 * It corresponds to the JavaScript {@code Error} built-in object.
 *
 * @since 1.4.0
 */
public class JsError extends Exception {
    /**
     * The constant NAME - JavaScript error type name.
     */
    public static final String NAME = "Error";

    /**
     * Instantiates a new Js error.
     *
     * @since 1.4.0
     */
    public JsError() {
        super();
    }

    /**
     * Instantiates a new Js error.
     *
     * @param message the message
     * @since 1.4.0
     */
    public JsError(String message) {
        super(message);
    }

    /**
     * Instantiates a new Js error.
     *
     * @param message the message
     * @param cause   the cause
     * @since 1.4.0
     */
    public JsError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Js error.
     *
     * @param cause the cause
     * @since 1.4.0
     */
    public JsError(Throwable cause) {
        super(cause);
    }

    /**
     * Gets the name of this error type.
     * <p>
     * Corresponds to JavaScript's {@code Error.prototype.name}.
     *
     * @return the name
     * @since 1.4.0
     */
    public String getName() {
        return "Error";
    }
}
