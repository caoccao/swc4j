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

import com.caoccao.javet.swc4j.ast.atom.bi.*;
import com.caoccao.javet.swc4j.ast.atom.tri.Swc4jAstTokenRegex;
import com.caoccao.javet.swc4j.ast.atom.uni.Swc4jAstTokenError;
import com.caoccao.javet.swc4j.ast.atom.uni.Swc4jAstTokenShebang;
import com.caoccao.javet.swc4j.ast.atom.uni.Swc4jAstTokenUnknown;
import com.caoccao.javet.swc4j.ast.operators.Swc4jAstTokenAssignOperator;
import com.caoccao.javet.swc4j.ast.operators.Swc4jAstTokenBinaryOperator;
import com.caoccao.javet.swc4j.ast.operators.Swc4jAstTokenGenericOperator;
import com.caoccao.javet.swc4j.ast.words.*;
import com.caoccao.javet.swc4j.enums.Swc4jAstTokenType;

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
    public static Swc4jAstTokenAssignOperator createAssignOperator(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenAssignOperator(type, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenBigInt createBigInt(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenBigInt(text, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenBinaryOperator createBinaryOperator(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenBinaryOperator(type, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenError createError(
            String text, String error, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenError(text, error, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenFalse createFalse(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenFalse(startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenGenericOperator createGenericOperator(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenGenericOperator(type, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenIdentKnown createIdentKnown(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenIdentKnown(text, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenIdentOther createIdentOther(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenIdentOther(text, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenKeyword createKeyword(
            Swc4jAstTokenType type, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenKeyword(type, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenNull createNull(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenNull(startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenNumber createNumber(
            String text, double value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenNumber(text, value, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenRegex createRegex(
            String text, String value, String flags, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenRegex(text, value, flags, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenShebang createShebang(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenShebang(text, value, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenString createString(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenString(text, value, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenTemplate createTemplate(
            String text, String value, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTemplate(text, value, startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenTrue createTrue(
            int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenTrue(startPosition, endPosition, lineBreakAhead);
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
    public static Swc4jAstTokenUnknown createUnknown(
            String text, int startPosition, int endPosition, boolean lineBreakAhead) {
        return new Swc4jAstTokenUnknown(text, startPosition, endPosition, lineBreakAhead);
    }
}
