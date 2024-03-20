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

public class Swc4jAstToken {
    protected final Swc4jAstTokenType type;
    protected int endPosition;
    protected int startPosition;

    public Swc4jAstToken(Swc4jAstTokenType type, int startPosition, int endPosition) {
        this.endPosition = endPosition;
        this.startPosition = startPosition;
        this.type = AssertionUtils.notNull(type, "Ast token type");
    }

    public int getEndPosition() {
        return endPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public Swc4jAstTokenType getType() {
        return type;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }
}
