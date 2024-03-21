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

import com.caoccao.javet.swc4j.ast.word.Swc4jAstTokenFalse;
import com.caoccao.javet.swc4j.ast.word.Swc4jAstTokenKeyword;
import com.caoccao.javet.swc4j.ast.word.Swc4jAstTokenNull;
import com.caoccao.javet.swc4j.ast.word.Swc4jAstTokenTrue;
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
     * Create ast token false.
     *
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast token false
     * @since 0.2.0
     */
    public static Swc4jAstTokenFalse createFalse(int startPosition, int endPosition) {
        return new Swc4jAstTokenFalse(startPosition, endPosition);
    }

    /**
     * Create ast token keyword.
     *
     * @param type          the type
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast token keyword
     * @since 0.2.0
     */
    public static Swc4jAstTokenKeyword createKeyword(Swc4jAstTokenType type, int startPosition, int endPosition) {
        return new Swc4jAstTokenKeyword(type, startPosition, endPosition);
    }

    /**
     * Create ast token null.
     *
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast token null
     * @since 0.2.0
     */
    public static Swc4jAstTokenNull createNull(int startPosition, int endPosition) {
        return new Swc4jAstTokenNull(startPosition, endPosition);
    }

    /**
     * Create ast token true.
     *
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast token true
     * @since 0.2.0
     */
    public static Swc4jAstTokenTrue createTrue(int startPosition, int endPosition) {
        return new Swc4jAstTokenTrue(startPosition, endPosition);
    }

    /**
     * Create ast token unknown.
     *
     * @param text          the text
     * @param startPosition the start position
     * @param endPosition   the end position
     * @return the ast token unknown
     * @since 0.2.0
     */
    public static Swc4jAstTokenUnknown createUnknown(String text, int startPosition, int endPosition) {
        return new Swc4jAstTokenUnknown(text, startPosition, endPosition);
    }
}
