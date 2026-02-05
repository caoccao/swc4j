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
 * The enum swc4j ast binary op.
 */
public enum Swc4jAstBinaryOp implements ISwc4jEnumIdName {
    /**
     * Add swc4j ast binary op.
     */
    Add(0, "+"),
    /**
     * Bit and swc4j ast binary op.
     */
    BitAnd(1, "&"),
    /**
     * Bit or swc4j ast binary op.
     */
    BitOr(2, "|"),
    /**
     * Bit xor swc4j ast binary op.
     */
    BitXor(3, "^"),
    /**
     * Div swc4j ast binary op.
     */
    Div(4, "/"),
    /**
     * Eq eq swc4j ast binary op.
     */
    EqEq(5, "=="),
    /**
     * Eq eq eq swc4j ast binary op.
     */
    EqEqEq(6, "==="),
    /**
     * Exp swc4j ast binary op.
     */
    Exp(7, "**"),
    /**
     * Gt swc4j ast binary op.
     */
    Gt(8, ">"),
    /**
     * Gt eq swc4j ast binary op.
     */
    GtEq(9, ">="),
    /**
     * In swc4j ast binary op.
     */
    In(10, "in"),
    /**
     * Instance of swc4j ast binary op.
     */
    InstanceOf(11, "instanceof"),
    /**
     * Logical and swc4j ast binary op.
     */
    LogicalAnd(12, "&&"),
    /**
     * Logical or swc4j ast binary op.
     */
    LogicalOr(13, "||"),
    /**
     * L shift swc4j ast binary op.
     */
    LShift(14, "<<"),
    /**
     * Lt swc4j ast binary op.
     */
    Lt(15, "<"),
    /**
     * Lt eq swc4j ast binary op.
     */
    LtEq(16, "<="),
    /**
     * Mod swc4j ast binary op.
     */
    Mod(17, "%"),
    /**
     * Mul swc4j ast binary op.
     */
    Mul(18, "*"),
    /**
     * Not eq swc4j ast binary op.
     */
    NotEq(19, "!="),
    /**
     * Not eq eq swc4j ast binary op.
     */
    NotEqEq(20, "!=="),
    /**
     * Nullish coalescing swc4j ast binary op.
     */
    NullishCoalescing(21, "??"),
    /**
     * R shift swc4j ast binary op.
     */
    RShift(22, ">>"),
    /**
     * Sub swc4j ast binary op.
     */
    Sub(23, "-"),
    /**
     * Zero fill r shift swc4j ast binary op.
     */
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
        EqEq.setLogicalCompareOperator().setOppositeOperator(NotEq);
        EqEqEq.setLogicalCompareOperator().setOppositeOperator(NotEqEq);
        Exp.setArithmeticOperator();
        Gt.setLogicalCompareOperator().setOppositeOperator(LtEq);
        GtEq.setLogicalCompareOperator().setOppositeOperator(Lt);
        In.setSpaceRequired();
        InstanceOf.setSpaceRequired();
        LogicalAnd.setLogicalConditionOperator().setOppositeOperator(LogicalOr);
        LogicalOr.setLogicalConditionOperator().setOppositeOperator(LogicalAnd);
        LShift.setArithmeticOperator().setOppositeOperator(RShift);
        Lt.setLogicalCompareOperator().setOppositeOperator(GtEq);
        LtEq.setLogicalCompareOperator().setOppositeOperator(Gt);
        Mod.setArithmeticOperator();
        Mul.setArithmeticOperator().setOppositeOperator(Div);
        NotEq.setLogicalCompareOperator().setOppositeOperator(EqEq);
        NotEqEq.setLogicalCompareOperator().setOppositeOperator(EqEqEq);
        RShift.setArithmeticOperator().setOppositeOperator(LShift);
        Sub.setArithmeticOperator().setOppositeOperator(Add);
        ZeroFillRShift.setArithmeticOperator();
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;
    private boolean arithmeticOperator;
    private boolean bitOperator;
    private boolean logicalCompareOperator;
    private boolean logicalConditionOperator;
    private Swc4jAstBinaryOp oppositeOperator;
    private boolean spaceRequired;

    Swc4jAstBinaryOp(int id, String name) {
        arithmeticOperator = false;
        bitOperator = false;
        this.id = id;
        logicalCompareOperator = false;
        logicalConditionOperator = false;
        this.name = name;
        oppositeOperator = null;
        spaceRequired = false;
    }

    /**
     * Parse swc4j ast binary op.
     *
     * @param id the id
     * @return the swc4j ast binary op
     */
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

    /**
     * Gets opposite operator.
     *
     * @return the opposite operator
     */
    public Swc4jAstBinaryOp getOppositeOperator() {
        return oppositeOperator;
    }

    /**
     * Is arithmetic operator boolean.
     *
     * @return the boolean
     */
    public boolean isArithmeticOperator() {
        return arithmeticOperator;
    }

    /**
     * Is bit operator boolean.
     *
     * @return the boolean
     */
    public boolean isBitOperator() {
        return bitOperator;
    }

    /**
     * Is logical compare operator boolean.
     *
     * @return the boolean
     */
    public boolean isLogicalCompareOperator() {
        return logicalCompareOperator;
    }

    /**
     * Is logical condition operator boolean.
     *
     * @return the boolean
     */
    public boolean isLogicalConditionOperator() {
        return logicalConditionOperator;
    }

    /**
     * Is logical operator boolean.
     *
     * @return the boolean
     */
    public boolean isLogicalOperator() {
        return isLogicalConditionOperator() || isLogicalCompareOperator();
    }

    /**
     * Is space required boolean.
     *
     * @return the boolean
     */
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

    private Swc4jAstBinaryOp setLogicalCompareOperator() {
        logicalCompareOperator = true;
        return this;
    }

    private Swc4jAstBinaryOp setLogicalConditionOperator() {
        logicalConditionOperator = true;
        return this;
    }

    private Swc4jAstBinaryOp setOppositeOperator(Swc4jAstBinaryOp oppositeOperator) {
        this.oppositeOperator = oppositeOperator;
        return this;
    }

    private Swc4jAstBinaryOp setSpaceRequired() {
        spaceRequired = true;
        return this;
    }
}
