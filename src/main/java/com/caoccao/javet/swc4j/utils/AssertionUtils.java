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

package com.caoccao.javet.swc4j.utils;

import com.caoccao.javet.swc4j.interfaces.ISwc4jLogger;

public final class AssertionUtils {
    private static final ISwc4jLogger LOGGER = new Swc4jDefaultLogger(AssertionUtils.class.getName());

    private static final String VALUE = "Value";

    private AssertionUtils() {
    }

    public static <T> T notNull(T value) {
        return notNull(value, VALUE);
    }

    public static <T> T notNull(T value, String name) {
        if (value == null) {
            String message = name + " should not be null";
            NullPointerException exception = new NullPointerException(message);
            LOGGER.error(message, exception);
            throw exception;
        }
        return value;
    }

    public static void notTrue(boolean b, String message) {
        if (!b) {
            IllegalArgumentException exception = new IllegalArgumentException(message);
            LOGGER.error(message, exception);
            throw exception;
        }
    }
}
