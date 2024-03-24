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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.math.BigInteger;

/**
 * The type Swc4j ast token factory.
 *
 * @since 0.2.0
 */
public final class Swc4jAstTokenFactory {
    private Swc4jAstTokenFactory() {
    }

    /**
     * Create ast token assign operator.
     *
     * @param type           the type
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token assign operator
     * @since 0.2.0
     */
    public static Swc4jAstToken createAssignOperator(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        AssertionUtils.notTrue(type.getSubType().isAssignOperator(), "Assign operator is expected");
        return new Swc4jAstToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token big int.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token big int
     * @since 0.2.0
     */
    public static Swc4jAstTokenTextValue<BigInteger> createBigInt(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTextValue<>(
                Swc4jAstTokenType.BigInt,
                text,
                new BigInteger(text.substring(0, text.length() - 1)),
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create ast token binary operator.
     *
     * @param type           the type
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token binary operator
     * @since 0.2.0
     */
    public static Swc4jAstToken createBinaryOperator(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        AssertionUtils.notTrue(type.getSubType().isBinaryOperator(), "Binary operator is expected");
        return new Swc4jAstToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token error.
     *
     * @param text           the text
     * @param error          the error
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token error
     * @since 0.2.0
     */
    public static Swc4jAstTokenTextValue<String> createError(
            String text, String error, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTextValue<>(
                Swc4jAstTokenType.Error,
                text,
                error,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create ast token false.
     *
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token false
     * @since 0.2.0
     */
    public static Swc4jAstToken createFalse(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstToken(Swc4jAstTokenType.False, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token generic operator.
     *
     * @param type           the type
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token generic operator
     * @since 0.2.0
     */
    public static Swc4jAstToken createGenericOperator(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        AssertionUtils.notTrue(type.getSubType().isGenericOperator(), "Generic operator is expected");
        return new Swc4jAstToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token ident known.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token ident known
     * @since 0.2.0
     */
    public static Swc4jAstTokenText createIdentKnown(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenText(Swc4jAstTokenType.IdentKnown, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token ident other.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token ident other
     * @since 0.2.0
     */
    public static Swc4jAstTokenText createIdentOther(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenText(Swc4jAstTokenType.IdentOther, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token jsx tag name.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token jsx tag name
     * @since 0.2.0
     */
    public static Swc4jAstTokenText createJsxTagName(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenText(Swc4jAstTokenType.JsxTagName, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token jsx tag text.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token jsx tag text
     * @since 0.2.0
     */
    public static Swc4jAstTokenText createJsxTagText(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenText(Swc4jAstTokenType.JsxTagText, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token keyword.
     *
     * @param type           the type
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token keyword
     * @since 0.2.0
     */
    public static Swc4jAstToken createKeyword(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        AssertionUtils.notTrue(type.getSubType().isKeyword(), "Keyword is expected");
        return new Swc4jAstToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token null.
     *
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token null
     * @since 0.2.0
     */
    public static Swc4jAstToken createNull(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstToken(Swc4jAstTokenType.Null, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token number.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token number
     * @since 0.2.0
     */
    public static Swc4jAstTokenTextValue<Double> createNumber(
            String text, double value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTextValue<>(
                Swc4jAstTokenType.Num,
                text,
                value,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create ast token regex.
     *
     * @param text           the text
     * @param value          the value
     * @param flags          the flags
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token regex
     * @since 0.2.0
     */
    public static Swc4jAstTokenTextValueFlags createRegex(
            String text, String value, String flags, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTextValueFlags(
                Swc4jAstTokenType.Regex, text, value, flags, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token shebang.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token shebang
     * @since 0.2.0
     */
    public static Swc4jAstTokenTextValue<String> createShebang(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTextValue<>(
                Swc4jAstTokenType.Shebang,
                text,
                value,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create ast token string.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token string
     * @since 0.2.0
     */
    public static Swc4jAstTokenTextValue<String> createString(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTextValue<>(
                Swc4jAstTokenType.Str,
                text,
                value,
                startPosition, endPosition,
                lineBreakAhead);
    }

    /**
     * Create ast token template.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token template
     * @since 0.2.0
     */
    public static Swc4jAstTokenTextValue<String> createTemplate(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTextValue<>(
                Swc4jAstTokenType.Template,
                text,
                value,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create ast token true.
     *
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token true
     * @since 0.2.0
     */
    public static Swc4jAstToken createTrue(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstToken(Swc4jAstTokenType.True, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create ast token unknown.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the ast token unknown
     * @since 0.2.0
     */
    public static Swc4jAstTokenText createUnknown(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenText(Swc4jAstTokenType.Unknown, text, startPosition, endPosition, lineBreakAhead);
    }
}
