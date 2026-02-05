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
 * The enum swc4j ast unary op.
 */
public enum Swc4jAstUnaryOp implements ISwc4jEnumIdName {
    /**
     * Bang swc4j ast unary op.
     */
    Bang(1, "!"),
    /**
     * Delete swc4j ast unary op.
     */
    Delete(2, "delete"),
    /**
     * Minus swc4j ast unary op.
     */
    Minus(3, "-"),
    /**
     * Plus swc4j ast unary op.
     */
    Plus(4, "+"),
    /**
     * Tilde swc4j ast unary op.
     */
    Tilde(5, "~"),
    /**
     * Type of swc4j ast unary op.
     */
    TypeOf(6, "typeof"),
    /**
     * Void swc4j ast unary op.
     */
    Void(0, "void"),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstUnaryOp[] TYPES = new Swc4jAstUnaryOp[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    Swc4jAstUnaryOp(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse swc4j ast unary op.
     *
     * @param id the id
     * @return the swc4j ast unary op
     */
    public static Swc4jAstUnaryOp parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Void;
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
