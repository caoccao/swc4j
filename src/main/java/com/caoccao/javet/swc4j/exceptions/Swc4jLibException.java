/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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
 * The type Swc4j lib exception.
 *
 * @since 0.1.0
 */
public final class Swc4jLibException extends Swc4jException {
    private Swc4jLibException(String message) {
        super(message);
    }

    private Swc4jLibException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Arch not supported.
     *
     * @param archName the arch name
     * @return the swc4j lib exception
     * @since 0.1.0
     */
    public static Swc4jLibException archNotSupported(String archName) {
        return new Swc4jLibException(MessageFormat.format("Arch {0} is not supported", archName));
    }

    /**
     * Lib not created.
     *
     * @param libFilePath the lib file path
     * @return the swc4j lib exception
     * @since 0.1.0
     */
    public static Swc4jLibException libNotCreated(String libFilePath) {
        return new Swc4jLibException(MessageFormat.format("Failed to create lib {0}", libFilePath));
    }

    /**
     * Lib not found.
     *
     * @param resourceFileName the resource file name
     * @return the swc4j lib exception
     * @since 0.1.0
     */
    public static Swc4jLibException libNotFound(String resourceFileName) {
        return new Swc4jLibException(MessageFormat.format("Lib {0} is not found", resourceFileName));
    }

    /**
     * OS not supported.
     *
     * @param osName the OS name
     * @return the swc4j lib exception
     * @since 0.1.0
     */
    public static Swc4jLibException osNotSupported(String osName) {
        return new Swc4jLibException(MessageFormat.format("OS {0} is not supported", osName));
    }
}
