/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

/**
 * The enum swc4j token sub type.
 */
public enum Swc4jTokenSubType {
    /**
     * Assign operator swc4j token sub type.
     */
    AssignOperator,
    /**
     * Binary operator swc4j token sub type.
     */
    BinaryOperator,
    /**
     * Generic operator swc4j token sub type.
     */
    GenericOperator,
    /**
     * Keyword swc4j token sub type.
     */
    Keyword,
    /**
     * Reserved word swc4j token sub type.
     */
    ReservedWord,
    /**
     * Text swc4j token sub type.
     */
    Text,
    /**
     * Text value swc4j token sub type.
     */
    TextValue,
    /**
     * Text value flags swc4j token sub type.
     */
    TextValueFlags,
    /**
     * Unknown swc4j token sub type.
     */
    Unknown,
    ;

    /**
     * Is assign operator boolean.
     *
     * @return the boolean
     */
    public boolean isAssignOperator() {
        return this == AssignOperator;
    }

    /**
     * Is bi atom boolean.
     *
     * @return the boolean
     */
    public boolean isBiAtom() {
        return this == TextValue;
    }

    /**
     * Is binary operator boolean.
     *
     * @return the boolean
     */
    public boolean isBinaryOperator() {
        return this == BinaryOperator;
    }

    /**
     * Is generic operator boolean.
     *
     * @return the boolean
     */
    public boolean isGenericOperator() {
        return this == GenericOperator;
    }

    /**
     * Is keyword boolean.
     *
     * @return the boolean
     */
    public boolean isKeyword() {
        return this == Keyword;
    }

    /**
     * Is operator boolean.
     *
     * @return the boolean
     */
    public boolean isOperator() {
        return isAssignOperator() || isBinaryOperator() || isGenericOperator();
    }

    /**
     * Is reserved word boolean.
     *
     * @return the boolean
     */
    public boolean isReservedWord() {
        return this == ReservedWord;
    }

    /**
     * Is tri atom boolean.
     *
     * @return the boolean
     */
    public boolean isTriAtom() {
        return this == TextValueFlags;
    }

    /**
     * Is uni atom boolean.
     *
     * @return the boolean
     */
    public boolean isUniAtom() {
        return this == Text;
    }

    /**
     * Is unknown boolean.
     *
     * @return the boolean
     */
    public boolean isUnknown() {
        return this == Unknown;
    }
}
