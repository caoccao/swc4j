/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
 * The type Swc4j exception.
 *
 * @since 0.1.0
 */
public abstract class Swc4jException extends Exception {
    /**
     * Instantiates a new Swc4j exception.
     *
     * @param message the message
     * @since 0.1.0
     */
    public Swc4jException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Swc4j exception.
     *
     * @param message the message
     * @param cause   the cause
     * @since 0.1.0
     */
    public Swc4jException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Swc4j exception.
     *
     * @param cause the cause
     * @since 0.1.0
     */
    public Swc4jException(Throwable cause) {
        super(cause);
    }
}
