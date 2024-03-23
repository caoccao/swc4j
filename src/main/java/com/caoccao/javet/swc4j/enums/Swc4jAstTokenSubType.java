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

package com.caoccao.javet.swc4j.enums;

public enum Swc4jAstTokenSubType {
    AssignOperator,
    BiAtom,
    BinaryOperator,
    GenericOperator,
    Keyword,
    ReservedWord,
    TriAtom,
    UniAtom,
    Unknown,
    ;

    public boolean isAssignOperator() {
        return this == AssignOperator;
    }

    public boolean isBiAtom() {
        return this == BiAtom;
    }

    public boolean isBinaryOperator() {
        return this == BinaryOperator;
    }

    public boolean isGenericOperator() {
        return this == GenericOperator;
    }

    public boolean isKeyword() {
        return this == Keyword;
    }

    public boolean isOperator() {
        return isAssignOperator() || isBinaryOperator() || isGenericOperator();
    }

    public boolean isReservedWord() {
        return this == ReservedWord;
    }

    public boolean isTriAtom() {
        return this == TriAtom;
    }

    public boolean isUniAtom() {
        return this == UniAtom;
    }

    public boolean isUnknown() {
        return this == Unknown;
    }
}
