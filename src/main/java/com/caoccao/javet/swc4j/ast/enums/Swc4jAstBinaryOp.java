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

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumIdName;

import java.util.stream.Stream;

public enum Swc4jAstBinaryOp implements ISwc4jEnumIdName {
    Add(0, "+", false),
    BitAnd(1, "&", false),
    BitOr(2, "|", false),
    BitXor(3, "^", false),
    Div(4, "/", false),
    EqEq(5, "==", false),
    EqEqEq(6, "===", false),
    Exp(7, "**", false),
    Gt(8, ">", false),
    GtEq(9, ">=", false),
    In(10, "in", true),
    InstanceOf(11, "instanceof", true),
    LogicalAnd(12, "&&", false),
    LogicalOr(13, "||", false),
    LShift(14, "<<", false),
    Lt(15, "<", false),
    LtEq(16, "<=", false),
    Mod(17, "%", false),
    Mul(18, "*", false),
    NotEq(19, "!=", false),
    NotEqEq(20, "!==", false),
    NullishCoalescing(21, "??", false),
    RShift(22, ">>", false),
    Sub(23, "-", false),
    ZeroFillRShift(24, ">>>", false),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstBinaryOp[] TYPES = new Swc4jAstBinaryOp[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;
    private final boolean spaceRequired;

    Swc4jAstBinaryOp(int id, String name, boolean spaceRequired) {
        this.id = id;
        this.name = name;
        this.spaceRequired = spaceRequired;
    }

    public static Swc4jAstBinaryOp parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Add;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isSpaceRequired() {
        return spaceRequired;
    }
}
