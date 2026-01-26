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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Js aggregate error.
 * <p>
 * Corresponds to the JavaScript {@code AggregateError} built-in object (ES2021).
 * Represents an error when several errors need to be wrapped in a single error,
 * for example when multiple errors are thrown by {@code Promise.any()}.
 *
 * @since 1.4.0
 */
public class JsAggregateError extends JsError {
    private final List<Throwable> errors;

    /**
     * Instantiates a new Js aggregate error.
     *
     * @since 1.4.0
     */
    public JsAggregateError() {
        super();
        this.errors = new ArrayList<>();
    }

    /**
     * Instantiates a new Js aggregate error.
     *
     * @param message the message
     * @since 1.4.0
     */
    public JsAggregateError(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    /**
     * Instantiates a new Js aggregate error.
     *
     * @param errors  the list of errors
     * @param message the message
     * @since 1.4.0
     */
    public JsAggregateError(List<Throwable> errors, String message) {
        super(message);
        this.errors = new ArrayList<>(errors);
    }

    /**
     * Instantiates a new Js aggregate error.
     *
     * @param message the message
     * @param cause   the cause
     * @since 1.4.0
     */
    public JsAggregateError(String message, Throwable cause) {
        super(message, cause);
        this.errors = new ArrayList<>();
    }

    /**
     * Instantiates a new Js aggregate error.
     *
     * @param cause the cause
     * @since 1.4.0
     */
    public JsAggregateError(Throwable cause) {
        super(cause);
        this.errors = new ArrayList<>();
    }

    /**
     * Gets the list of errors wrapped by this aggregate error.
     * <p>
     * Corresponds to JavaScript's {@code AggregateError.prototype.errors}.
     *
     * @return an unmodifiable list of errors
     * @since 1.4.0
     */
    public List<Throwable> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public String getName() {
        return "AggregateError";
    }
}
