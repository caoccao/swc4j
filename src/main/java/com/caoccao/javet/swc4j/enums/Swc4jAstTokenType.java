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
    Unknown(0, null, false),
    // Keyword
    Await(1, "await", true),
    Break(2, "break", true),
    Case(3, "case", true),
    Catch(4, "catch", true),
    Class(5, "class", true),
    Const(6, "const", true),
    Continue(7, "continue", true),
    Debugger(8, "debugger", true),
    Default(9, "default", true),
    Delete(10, "delete", true),
    Do(11, "do", true),
    Else(12, "else", true),
    Export(13, "export", true),
    Extends(14, "extends", true),
    Finally(15, "finally", true),
    For(16, "for", true),
    Function(17, "function", true),
    If(18, "if", true),
    Import(19, "import", true),
    In(20, "in", true),
    InstanceOf(21, "instanceOf", true),
    Let(22, "let", true),
    New(23, "new", true),
    Return(24, "return", true),
    Super(25, "super", true),
    Switch(26, "switch", true),
    This(27, "this", true),
    Throw(28, "throw", true),
    Try(29, "try", true),
    TypeOf(30, "typeof", true),
    Var(31, "var", true),
    Void(32, "void", true),
    While(33, "while", true),
    With(34, "with", true),
    Yield(35, "yield", true),
    // Word
    Null(36, "null", false),
    True(37, "true", false),
    False(38, "false", false),
    IdentKnown(39, "$IdentKnown", false),
    IdentOther(40, "$IdentOther", false),
    // Operator
    Arrow(41, "=>", false, true),
    Hash(42, "#", false, true),
    At(43, "@", false, true),
    Dot(44, ".", false, true),
    DotDotDot(45, "...", false, true),
    Bang(46, "!", false, true),
    LParen(47, "(", false, true),
    RParen(48, ")", false, true),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstTokenType[] TYPES = new Swc4jAstTokenType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final boolean keyword;
    private final String name;
    private final boolean operator;

    Swc4jAstTokenType(int id, String name, boolean keyword) {
        this(id, name, keyword, false);
    }

    Swc4jAstTokenType(int id, String name, boolean keyword, boolean operator) {
        this.id = id;
        this.keyword = keyword;
        this.name = name;
        this.operator = operator;
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

    public boolean isKeyword() {
        return keyword;
    }

    public boolean isOperator() {
        return operator;
    }
}
