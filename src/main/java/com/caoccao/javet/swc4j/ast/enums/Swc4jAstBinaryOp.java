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
    Add(0, "+"),
    BitAnd(1, "&"),
    BitOr(2, "|"),
    BitXor(3, "^"),
    Div(4, "/"),
    EqEq(5, "=="),
    EqEqEq(6, "==="),
    Exp(7, "**"),
    Gt(8, ">"),
    GtEq(9, ">="),
    In(10, "in"),
    InstanceOf(11, "instanceof"),
    LogicalAnd(12, "&&"),
    LogicalOr(13, "||"),
    LShift(14, "<<"),
    Lt(15, "<"),
    LtEq(16, "<="),
    Mod(17, "%"),
    Mul(18, "*"),
    NotEq(19, "!="),
    NotEqEq(20, "!=="),
    NullishCoalescing(21, "??"),
    RShift(22, ">>"),
    Sub(23, "-"),
    ZeroFillRShift(24, ">>>"),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstBinaryOp[] TYPES = new Swc4jAstBinaryOp[LENGTH];

    static {
        Add.setArithmeticOperator().setOppositeOperator(Sub);
        BitAnd.setBitOperator().setOppositeOperator(BitOr);
        BitOr.setBitOperator().setOppositeOperator(BitAnd);
        BitXor.setBitOperator();
        Div.setArithmeticOperator().setOppositeOperator(Mul);
        EqEq.setLogicalOperator().setOppositeOperator(NotEq);
        EqEqEq.setLogicalOperator().setOppositeOperator(NotEqEq);
        Exp.setArithmeticOperator();
        Gt.setLogicalOperator().setOppositeOperator(LtEq);
        GtEq.setLogicalOperator().setOppositeOperator(Lt);
        In.setSpaceRequired();
        InstanceOf.setSpaceRequired();
        LogicalAnd.setLogicalOperator().setOppositeOperator(LogicalOr);
        LogicalOr.setLogicalOperator().setOppositeOperator(LogicalAnd);
        LShift.setArithmeticOperator().setOppositeOperator(RShift);
        Lt.setLogicalOperator().setOppositeOperator(GtEq);
        LtEq.setLogicalOperator().setOppositeOperator(Gt);
        Mod.setArithmeticOperator();
        Mul.setArithmeticOperator().setOppositeOperator(Div);
        NotEq.setLogicalOperator().setOppositeOperator(EqEq);
        NotEqEq.setLogicalOperator().setOppositeOperator(EqEqEq);
        RShift.setArithmeticOperator().setOppositeOperator(LShift);
        Sub.setArithmeticOperator().setOppositeOperator(Add);
        ZeroFillRShift.setArithmeticOperator();
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;
    private boolean arithmeticOperator;
    private boolean bitOperator;
    private boolean logicalOperator;
    private Swc4jAstBinaryOp oppositeOperator;
    private boolean spaceRequired;

    Swc4jAstBinaryOp(int id, String name) {
        arithmeticOperator = false;
        bitOperator = false;
        this.id = id;
        logicalOperator = false;
        this.name = name;
        oppositeOperator = null;
        spaceRequired = false;
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

    public Swc4jAstBinaryOp getOppositeOperator() {
        return oppositeOperator;
    }

    public boolean isArithmeticOperator() {
        return arithmeticOperator;
    }

    public boolean isBitOperator() {
        return bitOperator;
    }

    public boolean isLogicalOperator() {
        return logicalOperator;
    }

    public boolean isSpaceRequired() {
        return spaceRequired;
    }

    private Swc4jAstBinaryOp setArithmeticOperator() {
        this.arithmeticOperator = true;
        return this;
    }

    private Swc4jAstBinaryOp setBitOperator() {
        this.bitOperator = true;
        return this;
    }

    private Swc4jAstBinaryOp setLogicalOperator() {
        this.logicalOperator = true;
        return this;
    }

    private Swc4jAstBinaryOp setOppositeOperator(Swc4jAstBinaryOp oppositeOperator) {
        this.oppositeOperator = oppositeOperator;
        return this;
    }

    private Swc4jAstBinaryOp setSpaceRequired() {
        this.spaceRequired = true;
        return this;
    }
}
