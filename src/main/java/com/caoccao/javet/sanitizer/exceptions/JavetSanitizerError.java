/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.sanitizer.exceptions;

import com.caoccao.javet.swc4j.utils.SimpleFreeMarkerFormat;

import java.util.Map;

/**
 * The enum Javet sanitizer error.
 *
 * @since 0.7.0
 */
public enum JavetSanitizerError {
    UnknownError(1, "Unknown error: ${message}"),
    EmptyCodeString(2, "The JavaScript code is empty."),
    VisitorNotFound(3, "Visitor ${name} is not found."),
    ParsingError(4, "${message}"),

    IdentifierNotAllowed(100, "Identifier ${identifier} is not allowed."),
    KeywordNotAllowed(101, "Keyword ${keyword} is not allowed."),

    InvalidToken(200, "Token ${actualToken} is invalid. Expecting ${expectedToken}."),
    ArgumentCountMismatch(210, "Argument count ${actualCount} mismatches the expected argument count ${expectedCount}."),
    SyntaxCountMismatch(220, "Syntax count ${actualCount} mismatches the expected syntax count ${expectedCount}."),
    SyntaxCountTooSmall(221, "Syntax count ${actualCount} is less than the minimal syntax count ${minCount}."),
    SyntaxCountTooLarger(222, "Syntax count ${actualCount} is greater than the maximal syntax count ${maxCount}."),

    FunctionNotFound(300, "Function ${name} is not found."),
    ;

    private final int code;
    private final String format;

    JavetSanitizerError(int code, String format) {
        this.code = code;
        this.format = format;
    }

    /**
     * Gets code.
     *
     * @return the code
     * @since 0.7.0
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets format.
     *
     * @return the format
     * @since 0.7.0
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets message.
     *
     * @param parameters the parameters
     * @return the message
     * @since 0.7.0
     */
    public String getMessage(Map<String, Object> parameters) {
        return SimpleFreeMarkerFormat.format(format, parameters);
    }
}
