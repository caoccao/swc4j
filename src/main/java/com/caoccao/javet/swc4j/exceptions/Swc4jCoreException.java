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

import java.text.MessageFormat;

/**
 * The type Swc4j core exception.
 *
 * @since 0.1.0
 */
public final class Swc4jCoreException extends Swc4jException {
    private Swc4jCoreException(String message) {
        super(message);
    }

    private Swc4jCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Feature not supported.
     *
     * @param feature the feature
     * @return the swc4j core exception
     * @since 0.1.0
     */
    public static Swc4jCoreException featureNotSupported(String feature) {
        return new Swc4jCoreException(MessageFormat.format("Feature {0} is not supported", feature));
    }

    /**
     * Creates a new parse error with the given message.
     *
     * @param message the error message
     * @return a new Swc4jCoreException with the given message
     * @since 0.1.0
     */
    public static Swc4jCoreException parseError(String message) {
        return new Swc4jCoreException(message);
    }

    /**
     * Creates a new parse error with the given message and cause.
     *
     * @param message the error message
     * @param cause   the cause of the error
     * @return a new Swc4jCoreException with the given message and cause
     * @since 1.0.0
     */
    public static Swc4jCoreException parseError(String message, Throwable cause) {
        return new Swc4jCoreException(message, cause);
    }

    /**
     * Creates a new transform error with the given message.
     *
     * @param message the error message
     * @return the swc4j core exception
     * @since 1.0.0
     */
    public static Swc4jCoreException transformError(String message) {
        return new Swc4jCoreException(message);
    }

    /**
     * Creates a new transform error with the given message and cause.
     *
     * @param message the error message
     * @param cause   the cause of the error
     * @return a new Swc4jCoreException with the given message and cause
     * @since 1.0.0
     */
    public static Swc4jCoreException transformError(String message, Throwable cause) {
        return new Swc4jCoreException(message, cause);
    }

    /**
     * Creates a new transpile error with the given message.
     *
     * @param message the error message
     * @return the swc4j core exception
     * @since 0.1.0
     */
    public static Swc4jCoreException transpileError(String message) {
        return new Swc4jCoreException(message);
    }

    /**
     * Creates a new transpile error with the given message and cause.
     *
     * @param message the error message
     * @param cause   the cause of the error
     * @return a new Swc4jCoreException with the given message and cause
     * @since 1.0.0
     */
    public static Swc4jCoreException transpileError(String message, Throwable cause) {
        return new Swc4jCoreException(message, cause);
    }
}
