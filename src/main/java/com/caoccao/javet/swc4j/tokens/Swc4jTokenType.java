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

/**
 * The enum swc4j token type.
 */
public enum Swc4jTokenType implements ISwc4jEnumIdName {
    /**
     * Unknown swc4j token type.
     */
    Unknown(0, null, Swc4jTokenSubType.Unknown),
    /**
     * Await swc4j token type.
     */
// Keyword
    Await(1, "await", Swc4jTokenSubType.Keyword),
    /**
     * Break swc4j token type.
     */
    Break(2, "break", Swc4jTokenSubType.Keyword),
    /**
     * Case swc4j token type.
     */
    Case(3, "case", Swc4jTokenSubType.Keyword),
    /**
     * Catch swc4j token type.
     */
    Catch(4, "catch", Swc4jTokenSubType.Keyword),
    /**
     * Class swc4j token type.
     */
    Class(5, "class", Swc4jTokenSubType.Keyword),
    /**
     * Const swc4j token type.
     */
    Const(6, "const", Swc4jTokenSubType.Keyword),
    /**
     * Continue swc4j token type.
     */
    Continue(7, "continue", Swc4jTokenSubType.Keyword),
    /**
     * Debugger swc4j token type.
     */
    Debugger(8, "debugger", Swc4jTokenSubType.Keyword),
    /**
     * Default swc4j token type.
     */
    Default(9, "default", Swc4jTokenSubType.Keyword),
    /**
     * Delete swc4j token type.
     */
    Delete(10, "delete", Swc4jTokenSubType.Keyword),
    /**
     * Do swc4j token type.
     */
    Do(11, "do", Swc4jTokenSubType.Keyword),
    /**
     * Else swc4j token type.
     */
    Else(12, "else", Swc4jTokenSubType.Keyword),
    /**
     * Export swc4j token type.
     */
    Export(13, "export", Swc4jTokenSubType.Keyword),
    /**
     * Extends swc4j token type.
     */
    Extends(14, "extends", Swc4jTokenSubType.Keyword),
    /**
     * Finally swc4j token type.
     */
    Finally(15, "finally", Swc4jTokenSubType.Keyword),
    /**
     * For swc4j token type.
     */
    For(16, "for", Swc4jTokenSubType.Keyword),
    /**
     * Function swc4j token type.
     */
    Function(17, "function", Swc4jTokenSubType.Keyword),
    /**
     * If swc4j token type.
     */
    If(18, "if", Swc4jTokenSubType.Keyword),
    /**
     * Import swc4j token type.
     */
    Import(19, "import", Swc4jTokenSubType.Keyword),
    /**
     * In swc4j token type.
     */
    In(20, "in", Swc4jTokenSubType.Keyword),
    /**
     * Instance of swc4j token type.
     */
    InstanceOf(21, "instanceof", Swc4jTokenSubType.Keyword),
    /**
     * Let swc4j token type.
     */
    Let(22, "let", Swc4jTokenSubType.Keyword),
    /**
     * New swc4j token type.
     */
    New(23, "new", Swc4jTokenSubType.Keyword),
    /**
     * Return swc4j token type.
     */
    Return(24, "return", Swc4jTokenSubType.Keyword),
    /**
     * Super swc4j token type.
     */
    Super(25, "super", Swc4jTokenSubType.Keyword),
    /**
     * Switch swc4j token type.
     */
    Switch(26, "switch", Swc4jTokenSubType.Keyword),
    /**
     * This swc4j token type.
     */
    This(27, "this", Swc4jTokenSubType.Keyword),
    /**
     * Throw swc4j token type.
     */
    Throw(28, "throw", Swc4jTokenSubType.Keyword),
    /**
     * Try swc4j token type.
     */
    Try(29, "try", Swc4jTokenSubType.Keyword),
    /**
     * Type of swc4j token type.
     */
    TypeOf(30, "typeof", Swc4jTokenSubType.Keyword),
    /**
     * Var swc4j token type.
     */
    Var(31, "var", Swc4jTokenSubType.Keyword),
    /**
     * Void swc4j token type.
     */
    Void(32, "void", Swc4jTokenSubType.Keyword),
    /**
     * While swc4j token type.
     */
    While(33, "while", Swc4jTokenSubType.Keyword),
    /**
     * With swc4j token type.
     */
    With(34, "with", Swc4jTokenSubType.Keyword),
    /**
     * Yield swc4j token type.
     */
    Yield(35, "yield", Swc4jTokenSubType.Keyword),
    /**
     * Null swc4j token type.
     */
// Word
    Null(36, "null", Swc4jTokenSubType.ReservedWord),
    /**
     * True swc4j token type.
     */
    True(37, "true", Swc4jTokenSubType.ReservedWord),
    /**
     * False swc4j token type.
     */
    False(38, "false", Swc4jTokenSubType.ReservedWord),
    /**
     * Ident known swc4j token type.
     */
    IdentKnown(39, "$IdentKnown", Swc4jTokenSubType.ReservedWord),
    /**
     * Ident other swc4j token type.
     */
    IdentOther(40, "$IdentOther", Swc4jTokenSubType.ReservedWord),
    /**
     * Arrow swc4j token type.
     */
// Operator - Generic
    Arrow(41, "=>", Swc4jTokenSubType.GenericOperator),
    /**
     * Hash swc4j token type.
     */
    Hash(42, "#", Swc4jTokenSubType.GenericOperator),
    /**
     * At swc4j token type.
     */
    At(43, "@", Swc4jTokenSubType.GenericOperator),
    /**
     * Dot swc4j token type.
     */
    Dot(44, ".", Swc4jTokenSubType.GenericOperator),
    /**
     * Dot dot dot swc4j token type.
     */
    DotDotDot(45, "...", Swc4jTokenSubType.GenericOperator),
    /**
     * Bang swc4j token type.
     */
    Bang(46, "!", Swc4jTokenSubType.GenericOperator),
    /**
     * L paren swc4j token type.
     */
    LParen(47, "(", Swc4jTokenSubType.GenericOperator),
    /**
     * R paren swc4j token type.
     */
    RParen(48, ")", Swc4jTokenSubType.GenericOperator),
    /**
     * L bracket swc4j token type.
     */
    LBracket(49, "[", Swc4jTokenSubType.GenericOperator),
    /**
     * R bracket swc4j token type.
     */
    RBracket(50, "]", Swc4jTokenSubType.GenericOperator),
    /**
     * L brace swc4j token type.
     */
    LBrace(51, "{", Swc4jTokenSubType.GenericOperator),
    /**
     * R brace swc4j token type.
     */
    RBrace(52, "}", Swc4jTokenSubType.GenericOperator),
    /**
     * Semi swc4j token type.
     */
    Semi(53, ";", Swc4jTokenSubType.GenericOperator),
    /**
     * Comma swc4j token type.
     */
    Comma(54, ",", Swc4jTokenSubType.GenericOperator),
    /**
     * Back quote swc4j token type.
     */
    BackQuote(55, "`", Swc4jTokenSubType.GenericOperator),
    /**
     * Colon swc4j token type.
     */
    Colon(56, ":", Swc4jTokenSubType.GenericOperator),
    /**
     * Dollar l brace swc4j token type.
     */
    DollarLBrace(57, "${", Swc4jTokenSubType.GenericOperator),
    /**
     * Question mark swc4j token type.
     */
    QuestionMark(58, "?", Swc4jTokenSubType.GenericOperator),
    /**
     * Plus plus swc4j token type.
     */
    PlusPlus(59, "++", Swc4jTokenSubType.GenericOperator),
    /**
     * Minus minus swc4j token type.
     */
    MinusMinus(60, "--", Swc4jTokenSubType.GenericOperator),
    /**
     * Tilde swc4j token type.
     */
    Tilde(61, "~", Swc4jTokenSubType.GenericOperator),
    /**
     * Eq eq swc4j token type.
     */
// Operator - Binary
    EqEq(62, "==", Swc4jTokenSubType.BinaryOperator),
    /**
     * Not eq swc4j token type.
     */
    NotEq(63, "!=", Swc4jTokenSubType.BinaryOperator),
    /**
     * Eq eq eq swc4j token type.
     */
    EqEqEq(64, "===", Swc4jTokenSubType.BinaryOperator),
    /**
     * Not eq eq swc4j token type.
     */
    NotEqEq(65, "!==", Swc4jTokenSubType.BinaryOperator),
    /**
     * Lt swc4j token type.
     */
    Lt(66, "<", Swc4jTokenSubType.BinaryOperator),
    /**
     * Lt eq swc4j token type.
     */
    LtEq(67, "<=", Swc4jTokenSubType.BinaryOperator),
    /**
     * Gt swc4j token type.
     */
    Gt(68, ">", Swc4jTokenSubType.BinaryOperator),
    /**
     * Gt eq swc4j token type.
     */
    GtEq(69, ">=", Swc4jTokenSubType.BinaryOperator),
    /**
     * L shift swc4j token type.
     */
    LShift(70, "<<", Swc4jTokenSubType.BinaryOperator),
    /**
     * R shift swc4j token type.
     */
    RShift(71, ">>", Swc4jTokenSubType.BinaryOperator),
    /**
     * Zero fill r shift swc4j token type.
     */
    ZeroFillRShift(72, ">>>", Swc4jTokenSubType.BinaryOperator),
    /**
     * Add swc4j token type.
     */
    Add(73, "+", Swc4jTokenSubType.BinaryOperator),
    /**
     * Sub swc4j token type.
     */
    Sub(74, "-", Swc4jTokenSubType.BinaryOperator),
    /**
     * Mul swc4j token type.
     */
    Mul(75, "*", Swc4jTokenSubType.BinaryOperator),
    /**
     * Div swc4j token type.
     */
    Div(76, "/", Swc4jTokenSubType.BinaryOperator),
    /**
     * Mod swc4j token type.
     */
    Mod(77, "%", Swc4jTokenSubType.BinaryOperator),
    /**
     * Bit or swc4j token type.
     */
    BitOr(78, "|", Swc4jTokenSubType.BinaryOperator),
    /**
     * Bit xor swc4j token type.
     */
    BitXor(79, "^", Swc4jTokenSubType.BinaryOperator),
    /**
     * Bit and swc4j token type.
     */
    BitAnd(80, "&", Swc4jTokenSubType.BinaryOperator),
    /**
     * Exp swc4j token type.
     */
    Exp(81, "**", Swc4jTokenSubType.BinaryOperator),
    /**
     * Logical or swc4j token type.
     */
    LogicalOr(82, "||", Swc4jTokenSubType.BinaryOperator),
    /**
     * Logical and swc4j token type.
     */
    LogicalAnd(83, "&&", Swc4jTokenSubType.BinaryOperator),
    /**
     * Nullish coalescing swc4j token type.
     */
    NullishCoalescing(84, "??", Swc4jTokenSubType.BinaryOperator),
    /**
     * Assign swc4j token type.
     */
// Operator - Assign
    Assign(85, "=", Swc4jTokenSubType.AssignOperator),
    /**
     * Add assign swc4j token type.
     */
    AddAssign(86, "+=", Swc4jTokenSubType.AssignOperator),
    /**
     * Sub assign swc4j token type.
     */
    SubAssign(87, "-=", Swc4jTokenSubType.AssignOperator),
    /**
     * Mul assign swc4j token type.
     */
    MulAssign(88, "*=", Swc4jTokenSubType.AssignOperator),
    /**
     * Div assign swc4j token type.
     */
    DivAssign(89, "/=", Swc4jTokenSubType.AssignOperator),
    /**
     * Mod assign swc4j token type.
     */
    ModAssign(90, "%=", Swc4jTokenSubType.AssignOperator),
    /**
     * L shift assign swc4j token type.
     */
    LShiftAssign(91, "<<=", Swc4jTokenSubType.AssignOperator),
    /**
     * R shift assign swc4j token type.
     */
    RShiftAssign(92, ">>=", Swc4jTokenSubType.AssignOperator),
    /**
     * Zero fill r shift assign swc4j token type.
     */
    ZeroFillRShiftAssign(93, ">>>=", Swc4jTokenSubType.AssignOperator),
    /**
     * Bit or assign swc4j token type.
     */
    BitOrAssign(94, "|=", Swc4jTokenSubType.AssignOperator),
    /**
     * Bit xor assign swc4j token type.
     */
    BitXorAssign(95, "^=", Swc4jTokenSubType.AssignOperator),
    /**
     * Bit and assign swc4j token type.
     */
    BitAndAssign(96, "&=", Swc4jTokenSubType.AssignOperator),
    /**
     * Exp assign swc4j token type.
     */
    ExpAssign(97, "**=", Swc4jTokenSubType.AssignOperator),
    /**
     * And assign swc4j token type.
     */
    AndAssign(98, "&&=", Swc4jTokenSubType.AssignOperator),
    /**
     * Or assign swc4j token type.
     */
    OrAssign(99, "||=", Swc4jTokenSubType.AssignOperator),
    /**
     * Nullish assign swc4j token type.
     */
    NullishAssign(100, "??=", Swc4jTokenSubType.AssignOperator),
    /**
     * Shebang swc4j token type.
     */
// TextValue
    Shebang(101, "$Shebang", Swc4jTokenSubType.Text),
    /**
     * Error swc4j token type.
     */
    Error(102, "$Error", Swc4jTokenSubType.Text),
    /**
     * Str swc4j token type.
     */
    Str(103, "$Str", Swc4jTokenSubType.TextValue),
    /**
     * Num swc4j token type.
     */
    Num(104, "$Num", Swc4jTokenSubType.TextValue),
    /**
     * Big int swc4j token type.
     */
    BigInt(105, "$BigInt", Swc4jTokenSubType.TextValue),
    /**
     * Template swc4j token type.
     */
    Template(106, "$Template", Swc4jTokenSubType.TextValue),
    /**
     * Regex swc4j token type.
     */
// TextValueFlags
    Regex(107, "$Regex", Swc4jTokenSubType.TextValueFlags),
    /**
     * Jsx tag start swc4j token type.
     */
// Jsx
    JsxTagStart(108, "<", Swc4jTokenSubType.GenericOperator),
    /**
     * Jsx tag end swc4j token type.
     */
    JsxTagEnd(109, ">", Swc4jTokenSubType.GenericOperator),
    /**
     * Jsx tag name swc4j token type.
     */
    JsxTagName(110, "$JsxTagName", Swc4jTokenSubType.Text),
    /**
     * Jsx tag text swc4j token type.
     */
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

    /**
     * Parse swc4j token type.
     *
     * @param id the id
     * @return the swc4j token type
     */
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

    /**
     * Gets sub type.
     *
     * @return the sub type
     */
    public Swc4jTokenSubType getSubType() {
        return subType;
    }
}
