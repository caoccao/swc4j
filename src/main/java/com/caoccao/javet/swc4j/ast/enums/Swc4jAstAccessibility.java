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
 * The enum swc4j ast accessibility.
 */
public enum Swc4jAstAccessibility implements ISwc4jEnumIdName {
    /**
     * Public swc4j ast accessibility.
     */
    Public(0, "public"),
    /**
     * Protected swc4j ast accessibility.
     */
    Protected(1, "protected"),
    /**
     * Private swc4j ast accessibility.
     */
    Private(2, "private"),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstAccessibility[] TYPES = new Swc4jAstAccessibility[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    Swc4jAstAccessibility(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse swc4j ast accessibility.
     *
     * @param id the id
     * @return the swc4j ast accessibility
     */
    public static Swc4jAstAccessibility parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Public;
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
