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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.utils.SimpleMap;

import java.util.Map;
import java.util.Objects;

/**
 * The type Javet sanitizer exception.
 *
 * @since 0.7.0
 */
public final class JavetSanitizerException extends Exception {
    private final JavetSanitizerError error;
    private final Map<String, Object> parameters;
    private ISwc4jAst node;

    private JavetSanitizerException(JavetSanitizerError error) {
        this(error, SimpleMap.of());
    }

    private JavetSanitizerException(JavetSanitizerError error, Map<String, Object> parameters) {
        super(Objects.requireNonNull(error).getMessage(Objects.requireNonNull(parameters)));
        node = null;
        this.error = error;
        this.parameters = parameters;
    }

    private JavetSanitizerException(JavetSanitizerError error, Map<String, Object> parameters, Throwable cause) {
        super(Objects.requireNonNull(error).getMessage(Objects.requireNonNull(parameters)), cause);
        node = null;
        this.error = error;
        this.parameters = parameters;
    }

    /**
     * Argument count mismatch javet sanitizer exception.
     *
     * @param expectedCount the expected count
     * @param actualCount   the actual count
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException argumentCountMismatch(int expectedCount, int actualCount) {
        return new JavetSanitizerException(
                JavetSanitizerError.ArgumentCountMismatch,
                SimpleMap.of("actualCount", actualCount, "expectedCount", expectedCount));
    }

    /**
     * Empty code string javet sanitizer exception.
     *
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException emptyCodeString() {
        return new JavetSanitizerException(JavetSanitizerError.EmptyCodeString);
    }

    /**
     * Function not found javet sanitizer exception.
     *
     * @param name the name
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException functionNotFound(String name) {
        return new JavetSanitizerException(
                JavetSanitizerError.FunctionNotFound,
                SimpleMap.of("name", name));
    }

    /**
     * Identifier not allowed javet sanitizer exception.
     *
     * @param identifier the identifier
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException identifierNotAllowed(String identifier) {
        return new JavetSanitizerException(
                JavetSanitizerError.IdentifierNotAllowed,
                SimpleMap.of("identifier", identifier));
    }

    /**
     * Keyword not allowed javet sanitizer exception.
     *
     * @param keyword the keyword
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException keywordNotAllowed(String keyword) {
        return new JavetSanitizerException(
                JavetSanitizerError.KeywordNotAllowed,
                SimpleMap.of("keyword", keyword));
    }

    /**
     * Parsing error javet sanitizer exception.
     *
     * @param e the e
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException parsingError(Swc4jCoreException e) {
        return new JavetSanitizerException(
                JavetSanitizerError.ParsingError,
                SimpleMap.of("message", e.getMessage()),
                e);
    }

    /**
     * Syntax count mismatch javet sanitizer exception.
     *
     * @param expectedCount the expected count
     * @param actualCount   the actual count
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException syntaxCountMismatch(int expectedCount, int actualCount) {
        return new JavetSanitizerException(
                JavetSanitizerError.SyntaxCountMismatch,
                SimpleMap.of("actualCount", actualCount, "expectedCount", expectedCount));
    }

    /**
     * Syntax count too large javet sanitizer exception.
     *
     * @param maxCount    the max count
     * @param actualCount the actual count
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException syntaxCountTooLarge(int maxCount, int actualCount) {
        return new JavetSanitizerException(
                JavetSanitizerError.SyntaxCountTooLarger,
                SimpleMap.of("actualCount", actualCount, "maxCount", maxCount));
    }

    /**
     * Syntax count too small javet sanitizer exception.
     *
     * @param minCount    the min count
     * @param actualCount the actual count
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException syntaxCountTooSmall(int minCount, int actualCount) {
        return new JavetSanitizerException(
                JavetSanitizerError.SyntaxCountTooSmall,
                SimpleMap.of("actualCount", actualCount, "minCount", minCount));
    }

    /**
     * Token mismatch javet sanitizer exception.
     *
     * @param expectedToken the expected token
     * @param actualToken   the actual token
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException tokenMismatch(String expectedToken, String actualToken) {
        return new JavetSanitizerException(
                JavetSanitizerError.InvalidToken,
                SimpleMap.of("expectedToken", expectedToken, "actualToken", actualToken));
    }

    /**
     * Unknown error javet sanitizer exception.
     *
     * @param message the message
     * @param t       the t
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException unknownError(String message, Throwable t) {
        return new JavetSanitizerException(
                JavetSanitizerError.UnknownError,
                SimpleMap.of("message", message));
    }

    /**
     * Visitor not found javet sanitizer exception.
     *
     * @param visitorClassName the visitor class name
     * @param cause            the cause
     * @return the javet sanitizer exception
     * @since 0.7.0
     */
    public static JavetSanitizerException visitorNotFound(String visitorClassName, Throwable cause) {
        return new JavetSanitizerException(
                JavetSanitizerError.VisitorNotFound,
                SimpleMap.of("name", visitorClassName),
                cause);
    }

    /**
     * Gets error.
     *
     * @return the error
     * @since 0.7.0
     */
    public JavetSanitizerError getError() {
        return error;
    }

    /**
     * Gets node.
     *
     * @return the node
     * @since 0.7.0
     */
    public ISwc4jAst getNode() {
        return node;
    }

    /**
     * Gets parameters.
     *
     * @return the parameters
     * @since 0.7.0
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Sets node.
     *
     * @param node the node
     * @return the self
     * @since 0.7.0
     */
    public JavetSanitizerException setNode(ISwc4jAst node) {
        this.node = node;
        return this;
    }
}
