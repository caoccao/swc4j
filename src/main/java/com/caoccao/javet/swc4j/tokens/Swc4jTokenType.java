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

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumIdName;

import java.util.stream.Stream;

public enum Swc4jTokenType implements ISwc4jEnumIdName {
    Unknown(0, null, Swc4jTokenSubType.Unknown),
    // Keyword
    Await(1, "await", Swc4jTokenSubType.Keyword),
    Break(2, "break", Swc4jTokenSubType.Keyword),
    Case(3, "case", Swc4jTokenSubType.Keyword),
    Catch(4, "catch", Swc4jTokenSubType.Keyword),
    Class(5, "class", Swc4jTokenSubType.Keyword),
    Const(6, "const", Swc4jTokenSubType.Keyword),
    Continue(7, "continue", Swc4jTokenSubType.Keyword),
    Debugger(8, "debugger", Swc4jTokenSubType.Keyword),
    Default(9, "default", Swc4jTokenSubType.Keyword),
    Delete(10, "delete", Swc4jTokenSubType.Keyword),
    Do(11, "do", Swc4jTokenSubType.Keyword),
    Else(12, "else", Swc4jTokenSubType.Keyword),
    Export(13, "export", Swc4jTokenSubType.Keyword),
    Extends(14, "extends", Swc4jTokenSubType.Keyword),
    Finally(15, "finally", Swc4jTokenSubType.Keyword),
    For(16, "for", Swc4jTokenSubType.Keyword),
    Function(17, "function", Swc4jTokenSubType.Keyword),
    If(18, "if", Swc4jTokenSubType.Keyword),
    Import(19, "import", Swc4jTokenSubType.Keyword),
    In(20, "in", Swc4jTokenSubType.Keyword),
    InstanceOf(21, "instanceof", Swc4jTokenSubType.Keyword),
    Let(22, "let", Swc4jTokenSubType.Keyword),
    New(23, "new", Swc4jTokenSubType.Keyword),
    Return(24, "return", Swc4jTokenSubType.Keyword),
    Super(25, "super", Swc4jTokenSubType.Keyword),
    Switch(26, "switch", Swc4jTokenSubType.Keyword),
    This(27, "this", Swc4jTokenSubType.Keyword),
    Throw(28, "throw", Swc4jTokenSubType.Keyword),
    Try(29, "try", Swc4jTokenSubType.Keyword),
    TypeOf(30, "typeof", Swc4jTokenSubType.Keyword),
    Var(31, "var", Swc4jTokenSubType.Keyword),
    Void(32, "void", Swc4jTokenSubType.Keyword),
    While(33, "while", Swc4jTokenSubType.Keyword),
    With(34, "with", Swc4jTokenSubType.Keyword),
    Yield(35, "yield", Swc4jTokenSubType.Keyword),
    // Word
    Null(36, "null", Swc4jTokenSubType.ReservedWord),
    True(37, "true", Swc4jTokenSubType.ReservedWord),
    False(38, "false", Swc4jTokenSubType.ReservedWord),
    IdentKnown(39, "$IdentKnown", Swc4jTokenSubType.ReservedWord),
    IdentOther(40, "$IdentOther", Swc4jTokenSubType.ReservedWord),
    // Operator - Generic
    Arrow(41, "=>", Swc4jTokenSubType.GenericOperator),
    Hash(42, "#", Swc4jTokenSubType.GenericOperator),
    At(43, "@", Swc4jTokenSubType.GenericOperator),
    Dot(44, ".", Swc4jTokenSubType.GenericOperator),
    DotDotDot(45, "...", Swc4jTokenSubType.GenericOperator),
    Bang(46, "!", Swc4jTokenSubType.GenericOperator),
    LParen(47, "(", Swc4jTokenSubType.GenericOperator),
    RParen(48, ")", Swc4jTokenSubType.GenericOperator),
    LBracket(49, "[", Swc4jTokenSubType.GenericOperator),
    RBracket(50, "]", Swc4jTokenSubType.GenericOperator),
    LBrace(51, "{", Swc4jTokenSubType.GenericOperator),
    RBrace(52, "}", Swc4jTokenSubType.GenericOperator),
    Semi(53, ";", Swc4jTokenSubType.GenericOperator),
    Comma(54, ",", Swc4jTokenSubType.GenericOperator),
    BackQuote(55, "`", Swc4jTokenSubType.GenericOperator),
    Colon(56, ":", Swc4jTokenSubType.GenericOperator),
    DollarLBrace(57, "${", Swc4jTokenSubType.GenericOperator),
    QuestionMark(58, "?", Swc4jTokenSubType.GenericOperator),
    PlusPlus(59, "++", Swc4jTokenSubType.GenericOperator),
    MinusMinus(60, "--", Swc4jTokenSubType.GenericOperator),
    Tilde(61, "~", Swc4jTokenSubType.GenericOperator),
    // Operator - Binary
    EqEq(62, "==", Swc4jTokenSubType.BinaryOperator),
    NotEq(63, "!=", Swc4jTokenSubType.BinaryOperator),
    EqEqEq(64, "===", Swc4jTokenSubType.BinaryOperator),
    NotEqEq(65, "!==", Swc4jTokenSubType.BinaryOperator),
    Lt(66, "<", Swc4jTokenSubType.BinaryOperator),
    LtEq(67, "<=", Swc4jTokenSubType.BinaryOperator),
    Gt(68, ">", Swc4jTokenSubType.BinaryOperator),
    GtEq(69, ">=", Swc4jTokenSubType.BinaryOperator),
    LShift(70, "<<", Swc4jTokenSubType.BinaryOperator),
    RShift(71, ">>", Swc4jTokenSubType.BinaryOperator),
    ZeroFillRShift(72, ">>>", Swc4jTokenSubType.BinaryOperator),
    Add(73, "+", Swc4jTokenSubType.BinaryOperator),
    Sub(74, "-", Swc4jTokenSubType.BinaryOperator),
    Mul(75, "*", Swc4jTokenSubType.BinaryOperator),
    Div(76, "/", Swc4jTokenSubType.BinaryOperator),
    Mod(77, "%", Swc4jTokenSubType.BinaryOperator),
    BitOr(78, "|", Swc4jTokenSubType.BinaryOperator),
    BitXor(79, "^", Swc4jTokenSubType.BinaryOperator),
    BitAnd(80, "&", Swc4jTokenSubType.BinaryOperator),
    Exp(81, "**", Swc4jTokenSubType.BinaryOperator),
    LogicalOr(82, "||", Swc4jTokenSubType.BinaryOperator),
    LogicalAnd(83, "&&", Swc4jTokenSubType.BinaryOperator),
    NullishCoalescing(84, "??", Swc4jTokenSubType.BinaryOperator),
    // Operator - Assign
    Assign(85, "=", Swc4jTokenSubType.AssignOperator),
    AddAssign(86, "+=", Swc4jTokenSubType.AssignOperator),
    SubAssign(87, "-=", Swc4jTokenSubType.AssignOperator),
    MulAssign(88, "*=", Swc4jTokenSubType.AssignOperator),
    DivAssign(89, "/=", Swc4jTokenSubType.AssignOperator),
    ModAssign(90, "%=", Swc4jTokenSubType.AssignOperator),
    LShiftAssign(91, "<<=", Swc4jTokenSubType.AssignOperator),
    RShiftAssign(92, ">>=", Swc4jTokenSubType.AssignOperator),
    ZeroFillRShiftAssign(93, ">>>=", Swc4jTokenSubType.AssignOperator),
    BitOrAssign(94, "|=", Swc4jTokenSubType.AssignOperator),
    BitXorAssign(95, "^=", Swc4jTokenSubType.AssignOperator),
    BitAndAssign(96, "&=", Swc4jTokenSubType.AssignOperator),
    ExpAssign(97, "**=", Swc4jTokenSubType.AssignOperator),
    AndAssign(98, "&&=", Swc4jTokenSubType.AssignOperator),
    OrAssign(99, "||=", Swc4jTokenSubType.AssignOperator),
    NullishAssign(100, "??=", Swc4jTokenSubType.AssignOperator),
    // TextValue
    Shebang(101, "$Shebang", Swc4jTokenSubType.Text),
    Error(102, "$Error", Swc4jTokenSubType.Text),
    Str(103, "$Str", Swc4jTokenSubType.TextValue),
    Num(104, "$Num", Swc4jTokenSubType.TextValue),
    BigInt(105, "$BigInt", Swc4jTokenSubType.TextValue),
    Template(106, "$Template", Swc4jTokenSubType.TextValue),
    // TextValueFlags
    Regex(107, "$Regex", Swc4jTokenSubType.TextValueFlags),
    // Jsx
    JsxTagStart(108, "<", Swc4jTokenSubType.GenericOperator),
    JsxTagEnd(109, ">", Swc4jTokenSubType.GenericOperator),
    JsxTagName(110, "$JsxTagName", Swc4jTokenSubType.Text),
    JsxTagText(111, "$JsxTagText", Swc4jTokenSubType.Text),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jTokenType[] TYPES = new Swc4jTokenType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;
    private final Swc4jTokenSubType subType;

    Swc4jTokenType(
            int id,
            String name,
            Swc4jTokenSubType subType) {
        this.id = id;
        this.name = name;
        this.subType = subType;
    }

    public static Swc4jTokenType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Unknown;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public Swc4jTokenSubType getSubType() {
        return subType;
    }
}
