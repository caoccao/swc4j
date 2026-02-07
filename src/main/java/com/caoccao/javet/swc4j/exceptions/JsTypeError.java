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
 * The type Js type error.
 * <p>
 * Corresponds to the JavaScript {@code TypeError} built-in object.
 * Thrown when an operation could not be performed, typically (but not exclusively)
 * when a value is not of the expected type.
 *
 * @since 1.4.0
 */
public class JsTypeError extends JsError {
    /**
     * The constant NAME - JavaScript error type name.
     */
    public static final String NAME = "TypeError";

    /**
     * Instantiates a new Js type error.
     *
     * @since 1.4.0
     */
    public JsTypeError() {
        super();
    }

    /**
     * Instantiates a new Js type error.
     *
     * @param message the message
     * @since 1.4.0
     */
    public JsTypeError(String message) {
        super(message);
    }

    /**
     * Instantiates a new Js type error.
     *
     * @param message the message
     * @param cause   the cause
     * @since 1.4.0
     */
    public JsTypeError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Js type error.
     *
     * @param cause the cause
     * @since 1.4.0
     */
    public JsTypeError(Throwable cause) {
        super(cause);
    }

    @Override
    public String getName() {
        return "TypeError";
    }
}
