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
 * The type Js range error.
 * <p>
 * Corresponds to the JavaScript {@code RangeError} built-in object.
 * Thrown when a numeric variable or parameter is outside its valid range.
 *
 * @since 1.4.0
 */
public class JsRangeError extends JsError {
    /**
     * Instantiates a new Js range error.
     *
     * @since 1.4.0
     */
    public JsRangeError() {
        super();
    }

    /**
     * Instantiates a new Js range error.
     *
     * @param message the message
     * @since 1.4.0
     */
    public JsRangeError(String message) {
        super(message);
    }

    /**
     * Instantiates a new Js range error.
     *
     * @param message the message
     * @param cause   the cause
     * @since 1.4.0
     */
    public JsRangeError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Js range error.
     *
     * @param cause the cause
     * @since 1.4.0
     */
    public JsRangeError(Throwable cause) {
        super(cause);
    }

    @Override
    public String getName() {
        return "RangeError";
    }
}
