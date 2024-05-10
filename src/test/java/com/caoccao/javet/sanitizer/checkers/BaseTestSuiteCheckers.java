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

package com.caoccao.javet.sanitizer.checkers;

import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerError;
import com.caoccao.javet.sanitizer.exceptions.JavetSanitizerException;

import static org.junit.jupiter.api.Assertions.*;

public class BaseTestSuiteCheckers {
    protected IJavetSanitizerChecker checker;

    protected JavetSanitizerException assertException(
            String code,
            JavetSanitizerError expectedError,
            String expectedErrorMessage) {
        JavetSanitizerException exception = assertThrows(
                JavetSanitizerException.class,
                () -> checker.check(code),
                "Failed to throw exception for [" + code + "]");
        assertEquals(expectedError.getCode(), exception.getError().getCode());
        assertEquals(expectedErrorMessage, exception.getMessage());
        return exception;
    }

    protected JavetSanitizerException assertException(
            String code,
            JavetSanitizerError expectedError,
            String expectedErrorMessage,
            int start,
            int end,
            int line,
            int column) {
        JavetSanitizerException exception = assertException(code, expectedError, expectedErrorMessage);
        assertNotNull(exception.getNode());
        assertEquals(start, exception.getNode().getSpan().getStart());
        assertEquals(end, exception.getNode().getSpan().getEnd());
        assertEquals(line, exception.getNode().getSpan().getLine());
        assertEquals(column, exception.getNode().getSpan().getColumn());
        return exception;
    }
}
