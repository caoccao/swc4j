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
 * The type Js URI error.
 * <p>
 * Corresponds to the JavaScript {@code URIError} built-in object.
 * Thrown when a global URI handling function was used in a way that is incompatible
 * with its definition.
 *
 * @since 1.4.0
 */
public class JsURIError extends JsError {
    /**
     * The constant NAME - JavaScript error type name.
     */
    public static final String NAME = "URIError";

    /**
     * Instantiates a new Js URI error.
     *
     * @since 1.4.0
     */
    public JsURIError() {
        super();
    }

    /**
     * Instantiates a new Js URI error.
     *
     * @param message the message
     * @since 1.4.0
     */
    public JsURIError(String message) {
        super(message);
    }

    /**
     * Instantiates a new Js URI error.
     *
     * @param message the message
     * @param cause   the cause
     * @since 1.4.0
     */
    public JsURIError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Js URI error.
     *
     * @param cause the cause
     * @since 1.4.0
     */
    public JsURIError(Throwable cause) {
        super(cause);
    }

    @Override
    public String getName() {
        return "URIError";
    }
}
