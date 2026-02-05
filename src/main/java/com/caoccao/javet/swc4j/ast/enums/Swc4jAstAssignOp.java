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

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumIdName;

import java.util.stream.Stream;

/**
 * The enum swc4j ast assign op.
 */
public enum Swc4jAstAssignOp implements ISwc4jEnumIdName {
    /**
     * Add assign swc4j ast assign op.
     */
    AddAssign(0, "+="),
    /**
     * And assign swc4j ast assign op.
     */
    AndAssign(1, "&&="),
    /**
     * Assign swc4j ast assign op.
     */
    Assign(2, "="),
    /**
     * Bit and assign swc4j ast assign op.
     */
    BitAndAssign(3, "&="),
    /**
     * Bit or assign swc4j ast assign op.
     */
    BitOrAssign(4, "|="),
    /**
     * Bit xor assign swc4j ast assign op.
     */
    BitXorAssign(5, "^="),
    /**
     * Div assign swc4j ast assign op.
     */
    DivAssign(6, "/="),
    /**
     * Exp assign swc4j ast assign op.
     */
    ExpAssign(7, "**="),
    /**
     * L shift assign swc4j ast assign op.
     */
    LShiftAssign(8, "<<="),
    /**
     * Mod assign swc4j ast assign op.
     */
    ModAssign(9, "%="),
    /**
     * Mul assign swc4j ast assign op.
     */
    MulAssign(10, "*="),
    /**
     * Nullish assign swc4j ast assign op.
     */
    NullishAssign(11, "??="),
    /**
     * Or assign swc4j ast assign op.
     */
    OrAssign(12, "||="),
    /**
     * R shift assign swc4j ast assign op.
     */
    RShiftAssign(13, ">>="),
    /**
     * Sub assign swc4j ast assign op.
     */
    SubAssign(14, "-="),
    /**
     * Zero fill r shift assign swc4j ast assign op.
     */
    ZeroFillRShiftAssign(15, ">>>="),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstAssignOp[] TYPES = new Swc4jAstAssignOp[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    Swc4jAstAssignOp(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse swc4j ast assign op.
     *
     * @param id the id
     * @return the swc4j ast assign op
     */
    public static Swc4jAstAssignOp parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : AddAssign;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
