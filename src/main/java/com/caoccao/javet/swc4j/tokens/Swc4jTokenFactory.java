/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.tokens;

import com.caoccao.javet.swc4j.enums.Swc4jTokenType;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.math.BigInteger;

/**
 * The type Swc4j token factory.
 *
 * @since 0.2.0
 */
public final class Swc4jTokenFactory {
    private Swc4jTokenFactory() {
    }

    /**
     * Create token assign operator.
     *
     * @param typeId         the type id
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token assign operator
     * @since 0.2.0
     */
    public static Swc4jToken createAssignOperator(
            int typeId, int startPosition, int endPosition, boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isAssignOperator(), "Assign operator is expected");
        return new Swc4jToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token big int.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token big int
     * @since 0.2.0
     */
    public static Swc4jTokenTextValue<BigInteger> createBigInt(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.BigInt,
                text,
                new BigInteger(text.substring(0, text.length() - 1)),
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create token binary operator.
     *
     * @param typeId         the type id
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token binary operator
     * @since 0.2.0
     */
    public static Swc4jToken createBinaryOperator(
            int typeId, int startPosition, int endPosition, boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isBinaryOperator(), "Binary operator is expected");
        return new Swc4jToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token error.
     *
     * @param text           the text
     * @param error          the error
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token error
     * @since 0.2.0
     */
    public static Swc4jTokenTextValue<String> createError(
            String text, String error, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Error,
                text,
                error,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create token false.
     *
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token false
     * @since 0.2.0
     */
    public static Swc4jToken createFalse(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jToken(Swc4jTokenType.False, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token generic operator.
     *
     * @param typeId         the type id
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token generic operator
     * @since 0.2.0
     */
    public static Swc4jToken createGenericOperator(
            int typeId, int startPosition, int endPosition, boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isGenericOperator(), "Generic operator is expected");
        return new Swc4jToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token ident known.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token ident known
     * @since 0.2.0
     */
    public static Swc4jTokenText createIdentKnown(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.IdentKnown, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token ident other.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token ident other
     * @since 0.2.0
     */
    public static Swc4jTokenText createIdentOther(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.IdentOther, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token jsx tag name.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token jsx tag name
     * @since 0.2.0
     */
    public static Swc4jTokenText createJsxTagName(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.JsxTagName, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token jsx tag text.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token jsx tag text
     * @since 0.2.0
     */
    public static Swc4jTokenText createJsxTagText(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.JsxTagText, text, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token keyword.
     *
     * @param typeId         the type id
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token keyword
     * @since 0.2.0
     */
    public static Swc4jToken createKeyword(
            int typeId, int startPosition, int endPosition, boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isKeyword(), "Keyword is expected");
        return new Swc4jToken(type, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token null.
     *
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token null
     * @since 0.2.0
     */
    public static Swc4jToken createNull(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jToken(Swc4jTokenType.Null, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token number.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token number
     * @since 0.2.0
     */
    public static Swc4jTokenTextValue<Double> createNumber(
            String text, double value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Num,
                text,
                value,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create token regex.
     *
     * @param text           the text
     * @param value          the value
     * @param flags          the flags
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token regex
     * @since 0.2.0
     */
    public static Swc4jTokenTextValueFlags<String> createRegex(
            String text, String value, String flags, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenTextValueFlags<>(
                Swc4jTokenType.Regex, text, value, flags, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token shebang.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token shebang
     * @since 0.2.0
     */
    public static Swc4jTokenTextValue<String> createShebang(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Shebang,
                text,
                value,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create token string.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token string
     * @since 0.2.0
     */
    public static Swc4jTokenTextValue<String> createString(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Str,
                text,
                value,
                startPosition, endPosition,
                lineBreakAhead);
    }

    /**
     * Create token template.
     *
     * @param text           the text
     * @param value          the value
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token template
     * @since 0.2.0
     */
    public static Swc4jTokenTextValue<String> createTemplate(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Template,
                text,
                value,
                startPosition,
                endPosition,
                lineBreakAhead);
    }

    /**
     * Create token true.
     *
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token true
     * @since 0.2.0
     */
    public static Swc4jToken createTrue(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jToken(Swc4jTokenType.True, startPosition, endPosition, lineBreakAhead);
    }

    /**
     * Create token unknown.
     *
     * @param text           the text
     * @param startPosition  the start position
     * @param endPosition    the end position
     * @param lineBreakAhead the line break ahead
     * @return the token unknown
     * @since 0.2.0
     */
    public static Swc4jTokenText createUnknown(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.Unknown, text, startPosition, endPosition, lineBreakAhead);
    }
}
