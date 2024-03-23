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

import java.util.stream.Stream;

public enum Swc4jAstTokenType {
    Unknown(0, null, Swc4jAstTokenSubType.Unknown),
    // Keyword
    Await(1, "await", Swc4jAstTokenSubType.Keyword),
    Break(2, "break", Swc4jAstTokenSubType.Keyword),
    Case(3, "case", Swc4jAstTokenSubType.Keyword),
    Catch(4, "catch", Swc4jAstTokenSubType.Keyword),
    Class(5, "class", Swc4jAstTokenSubType.Keyword),
    Const(6, "const", Swc4jAstTokenSubType.Keyword),
    Continue(7, "continue", Swc4jAstTokenSubType.Keyword),
    Debugger(8, "debugger", Swc4jAstTokenSubType.Keyword),
    Default(9, "default", Swc4jAstTokenSubType.Keyword),
    Delete(10, "delete", Swc4jAstTokenSubType.Keyword),
    Do(11, "do", Swc4jAstTokenSubType.Keyword),
    Else(12, "else", Swc4jAstTokenSubType.Keyword),
    Export(13, "export", Swc4jAstTokenSubType.Keyword),
    Extends(14, "extends", Swc4jAstTokenSubType.Keyword),
    Finally(15, "finally", Swc4jAstTokenSubType.Keyword),
    For(16, "for", Swc4jAstTokenSubType.Keyword),
    Function(17, "function", Swc4jAstTokenSubType.Keyword),
    If(18, "if", Swc4jAstTokenSubType.Keyword),
    Import(19, "import", Swc4jAstTokenSubType.Keyword),
    In(20, "in", Swc4jAstTokenSubType.Keyword),
    InstanceOf(21, "instanceof", Swc4jAstTokenSubType.Keyword),
    Let(22, "let", Swc4jAstTokenSubType.Keyword),
    New(23, "new", Swc4jAstTokenSubType.Keyword),
    Return(24, "return", Swc4jAstTokenSubType.Keyword),
    Super(25, "super", Swc4jAstTokenSubType.Keyword),
    Switch(26, "switch", Swc4jAstTokenSubType.Keyword),
    This(27, "this", Swc4jAstTokenSubType.Keyword),
    Throw(28, "throw", Swc4jAstTokenSubType.Keyword),
    Try(29, "try", Swc4jAstTokenSubType.Keyword),
    TypeOf(30, "typeof", Swc4jAstTokenSubType.Keyword),
    Var(31, "var", Swc4jAstTokenSubType.Keyword),
    Void(32, "void", Swc4jAstTokenSubType.Keyword),
    While(33, "while", Swc4jAstTokenSubType.Keyword),
    With(34, "with", Swc4jAstTokenSubType.Keyword),
    Yield(35, "yield", Swc4jAstTokenSubType.Keyword),
    // Word
    Null(36, "null", Swc4jAstTokenSubType.ReservedWord),
    True(37, "true", Swc4jAstTokenSubType.ReservedWord),
    False(38, "false", Swc4jAstTokenSubType.ReservedWord),
    IdentKnown(39, "$IdentKnown", Swc4jAstTokenSubType.ReservedWord),
    IdentOther(40, "$IdentOther", Swc4jAstTokenSubType.ReservedWord),
    // Operator - Generic
    Arrow(41, "=>", Swc4jAstTokenSubType.GenericOperator),
    Hash(42, "#", Swc4jAstTokenSubType.GenericOperator),
    At(43, "@", Swc4jAstTokenSubType.GenericOperator),
    Dot(44, ".", Swc4jAstTokenSubType.GenericOperator),
    DotDotDot(45, "...", Swc4jAstTokenSubType.GenericOperator),
    Bang(46, "!", Swc4jAstTokenSubType.GenericOperator),
    LParen(47, "(", Swc4jAstTokenSubType.GenericOperator),
    RParen(48, ")", Swc4jAstTokenSubType.GenericOperator),
    LBracket(49, "[", Swc4jAstTokenSubType.GenericOperator),
    RBracket(50, "]", Swc4jAstTokenSubType.GenericOperator),
    LBrace(51, "{", Swc4jAstTokenSubType.GenericOperator),
    RBrace(52, "}", Swc4jAstTokenSubType.GenericOperator),
    Semi(53, ";", Swc4jAstTokenSubType.GenericOperator),
    Comma(54, ",", Swc4jAstTokenSubType.GenericOperator),
    BackQuote(55, "`", Swc4jAstTokenSubType.GenericOperator),
    Colon(56, ":", Swc4jAstTokenSubType.GenericOperator),
    DollarLBrace(57, "${", Swc4jAstTokenSubType.GenericOperator),
    QuestionMark(58, "?", Swc4jAstTokenSubType.GenericOperator),
    PlusPlus(59, "++", Swc4jAstTokenSubType.GenericOperator),
    MinusMinus(60, "--", Swc4jAstTokenSubType.GenericOperator),
    Tilde(61, "~", Swc4jAstTokenSubType.GenericOperator),
    // Operator - Binary
    EqEq(62, "==", Swc4jAstTokenSubType.BinaryOperator),
    NotEq(63, "!=", Swc4jAstTokenSubType.BinaryOperator),
    EqEqEq(64, "===", Swc4jAstTokenSubType.BinaryOperator),
    NotEqEq(65, "!==", Swc4jAstTokenSubType.BinaryOperator),
    Lt(66, "<", Swc4jAstTokenSubType.BinaryOperator),
    LtEq(67, "<=", Swc4jAstTokenSubType.BinaryOperator),
    Gt(68, ">", Swc4jAstTokenSubType.BinaryOperator),
    GtEq(69, ">=", Swc4jAstTokenSubType.BinaryOperator),
    LShift(70, "<<", Swc4jAstTokenSubType.BinaryOperator),
    RShift(71, ">>", Swc4jAstTokenSubType.BinaryOperator),
    ZeroFillRShift(72, ">>>", Swc4jAstTokenSubType.BinaryOperator),
    Add(73, "+", Swc4jAstTokenSubType.BinaryOperator),
    Sub(74, "-", Swc4jAstTokenSubType.BinaryOperator),
    Mul(75, "*", Swc4jAstTokenSubType.BinaryOperator),
    Div(76, "/", Swc4jAstTokenSubType.BinaryOperator),
    Mod(77, "%", Swc4jAstTokenSubType.BinaryOperator),
    BitOr(78, "|", Swc4jAstTokenSubType.BinaryOperator),
    BitXor(79, "^", Swc4jAstTokenSubType.BinaryOperator),
    BitAnd(80, "&", Swc4jAstTokenSubType.BinaryOperator),
    Exp(81, "**", Swc4jAstTokenSubType.BinaryOperator),
    LogicalOr(82, "||", Swc4jAstTokenSubType.BinaryOperator),
    LogicalAnd(83, "&&", Swc4jAstTokenSubType.BinaryOperator),
    NullishCoalescing(84, "??", Swc4jAstTokenSubType.BinaryOperator),
    // Operator - Assign
    Assign(85, "=", Swc4jAstTokenSubType.AssignOperator),
    AddAssign(86, "+=", Swc4jAstTokenSubType.AssignOperator),
    SubAssign(87, "-=", Swc4jAstTokenSubType.AssignOperator),
    MulAssign(88, "*=", Swc4jAstTokenSubType.AssignOperator),
    DivAssign(89, "/=", Swc4jAstTokenSubType.AssignOperator),
    ModAssign(90, "%=", Swc4jAstTokenSubType.AssignOperator),
    LShiftAssign(91, "<<=", Swc4jAstTokenSubType.AssignOperator),
    RShiftAssign(92, ">>=", Swc4jAstTokenSubType.AssignOperator),
    ZeroFillRShiftAssign(93, ">>>=", Swc4jAstTokenSubType.AssignOperator),
    BitOrAssign(94, "|=", Swc4jAstTokenSubType.AssignOperator),
    BitXorAssign(95, "^=", Swc4jAstTokenSubType.AssignOperator),
    BitAndAssign(96, "&=", Swc4jAstTokenSubType.AssignOperator),
    ExpAssign(97, "**=", Swc4jAstTokenSubType.AssignOperator),
    AndAssign(98, "&&=", Swc4jAstTokenSubType.AssignOperator),
    OrAssign(99, "||=", Swc4jAstTokenSubType.AssignOperator),
    NullishAssign(100, "??=", Swc4jAstTokenSubType.AssignOperator),
    // Atom - Bi
    Str(101, "$Str", Swc4jAstTokenSubType.BiAtom),
    Num(102, "$Num", Swc4jAstTokenSubType.BiAtom),
    BigInt(103, "$BigInt", Swc4jAstTokenSubType.BiAtom),
    // Atom - Tri
    Regex(104, "$Regex", Swc4jAstTokenSubType.TriAtom),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstTokenType[] TYPES = new Swc4jAstTokenType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;
    private final Swc4jAstTokenSubType subType;

    Swc4jAstTokenType(
            int id,
            String name,
            Swc4jAstTokenSubType subType) {
        this.id = id;
        this.name = name;
        this.subType = subType;
    }

    public static Swc4jAstTokenType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Unknown;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Swc4jAstTokenSubType getSubType() {
        return subType;
    }
}
