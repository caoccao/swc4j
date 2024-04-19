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

package com.caoccao.javet.swc4j.tokens;

import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.span.Swc4jSpan;

import java.math.BigInteger;

/**
 * The type Swc4j token factory.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.TokenUtils)
public final class Swc4jTokenFactory {
    private Swc4jTokenFactory() {
    }

    /**
     * Create token assign operator.
     *
     * @param typeId         the type id
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token assign operator
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jToken createAssignOperator(
            @Jni2RustParamTokenType int typeId,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isAssignOperator(), "Assign operator is expected");
        return new Swc4jToken(type, span, lineBreakAhead);
    }

    /**
     * Create token big int.
     *
     * @param text           the text
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token big int
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenTextValue<BigInteger> createBigInt(
            String text,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.BigInt,
                text,
                new BigInteger(text.substring(0, text.length() - 1)),
                span,
                lineBreakAhead);
    }

    /**
     * Create token binary operator.
     *
     * @param typeId         the type id
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token binary operator
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jToken createBinaryOperator(
            @Jni2RustParamTokenType int typeId,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isBinaryOperator(), "Binary operator is expected");
        return new Swc4jToken(type, span, lineBreakAhead);
    }

    /**
     * Create token error.
     *
     * @param text           the text
     * @param error          the error
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token error
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenTextValue<String> createError(
            String text,
            @Jni2RustParam(
                    rustType = "error: &Error",
                    preCalls = {
                            "    let java_error = string_to_jstring!(env, &format!(\"{:?}\", error));",
                            "    let error = jvalue {",
                            "      l: java_error.as_raw(),",
                            "    };",
                    }) String error,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Error,
                text,
                error,
                span,
                lineBreakAhead);
    }

    /**
     * Create token false.
     *
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token false
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jToken createFalse(
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jToken(Swc4jTokenType.False, span, lineBreakAhead);
    }

    /**
     * Create token generic operator.
     *
     * @param typeId         the type id
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token generic operator
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jToken createGenericOperator(
            @Jni2RustParamTokenType int typeId,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isGenericOperator(), "Generic operator is expected");
        return new Swc4jToken(type, span, lineBreakAhead);
    }

    /**
     * Create token ident known.
     *
     * @param text           the text
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token ident known
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenText createIdentKnown(
            String text,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.IdentKnown, text, span, lineBreakAhead);
    }

    /**
     * Create token ident other.
     *
     * @param text           the text
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token ident other
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenText createIdentOther(
            String text,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.IdentOther, text, span, lineBreakAhead);
    }

    /**
     * Create token jsx tag name.
     *
     * @param text           the text
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token jsx tag name
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenText createJsxTagName(
            String text,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.JsxTagName, text, span, lineBreakAhead);
    }

    /**
     * Create token jsx tag text.
     *
     * @param text           the text
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token jsx tag text
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenText createJsxTagText(
            String text,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.JsxTagText, text, span, lineBreakAhead);
    }

    /**
     * Create token keyword.
     *
     * @param typeId         the type id
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token keyword
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jToken createKeyword(
            @Jni2RustParamTokenType int typeId,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        Swc4jTokenType type = Swc4jTokenType.parse(typeId);
        AssertionUtils.notTrue(type.getSubType().isKeyword(), "Keyword is expected");
        return new Swc4jToken(type, span, lineBreakAhead);
    }

    /**
     * Create token null.
     *
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token null
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jToken createNull(
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jToken(Swc4jTokenType.Null, span, lineBreakAhead);
    }

    /**
     * Create token number.
     *
     * @param text           the text
     * @param value          the value
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token number
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenTextValue<Double> createNumber(
            String text,
            double value,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Num,
                text,
                value,
                span,
                lineBreakAhead);
    }

    /**
     * Create token regex.
     *
     * @param text           the text
     * @param value          the value
     * @param flags          the flags
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token regex
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenTextValueFlags<String> createRegex(
            String text,
            String value,
            String flags,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenTextValueFlags<>(
                Swc4jTokenType.Regex, text, value, flags, span, lineBreakAhead);
    }

    /**
     * Create token shebang.
     *
     * @param text           the text
     * @param value          the value
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token shebang
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenTextValue<String> createShebang(
            String text,
            String value,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Shebang,
                text,
                value,
                span,
                lineBreakAhead);
    }

    /**
     * Create ast span.
     *
     * @param start the start
     * @param end   the end
     * @return the ast span
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jSpan createSpan(
            @Jni2RustParamSpanStart int start,
            @Jni2RustParamSpanEnd int end,
            @Jni2RustParamSpanLine int line,
            @Jni2RustParamSpanColumn int column) {
        return new Swc4jSpan(start, end, line, column);
    }

    /**
     * Create token string.
     *
     * @param text           the text
     * @param value          the value
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token string
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenTextValue<String> createString(
            String text,
            String value,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Str,
                text,
                value,
                span,
                lineBreakAhead);
    }

    /**
     * Create token template.
     *
     * @param text           the text
     * @param value          the value
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token template
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenTextValue<String> createTemplate(
            String text,
            @Jni2RustParam(optional = true) String value,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenTextValue<>(
                Swc4jTokenType.Template,
                text,
                value,
                span,
                lineBreakAhead);
    }

    /**
     * Create token true.
     *
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token true
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jToken createTrue(
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jToken(Swc4jTokenType.True, span, lineBreakAhead);
    }

    /**
     * Create token unknown.
     *
     * @param text           the text
     * @param span           the span
     * @param lineBreakAhead the line break ahead
     * @return the token unknown
     * @since 0.2.0
     */
    @Jni2RustMethod
    public static Swc4jTokenText createUnknown(
            String text,
            @Jni2RustParam Swc4jSpan span,
            boolean lineBreakAhead) {
        return new Swc4jTokenText(Swc4jTokenType.Unknown, text, span, lineBreakAhead);
    }
}
