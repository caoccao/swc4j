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

public final class Swc4jAstTokenFactory {
    private Swc4jAstTokenFactory() {
    }

    public static Swc4jAstTokenKeyword createKeyword(Swc4jAstTokenType type, int startPosition, int endPosition) {
        return new Swc4jAstTokenKeyword(type, startPosition, endPosition);
    }

    public static Swc4jAstTokenUnknown createUnknown(String text, int startPosition, int endPosition) {
        return new Swc4jAstTokenUnknown(text, startPosition, endPosition);
    }
}
