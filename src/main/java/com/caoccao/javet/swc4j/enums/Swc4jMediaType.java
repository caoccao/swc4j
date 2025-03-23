/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumId;

import java.util.stream.Stream;

/**
 * The enum Swc4j media type.
 *
 * @since 0.1.0
 */
public enum Swc4jMediaType implements ISwc4jEnumId {
    JavaScript(0),
    Jsx(1),
    Mjs(2),
    Cjs(3),
    TypeScript(4),
    Mts(5),
    Cts(6),
    Dts(7),
    Dmts(8),
    Dcts(9),
    Tsx(10),
    Css(11),
    Json(12),
    Html(13),
    Sql(14),
    Wasm(15),
    SourceMap(16),
    Unknown(17),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jMediaType[] TYPES = new Swc4jMediaType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jMediaType(int id) {
        this.id = id;
    }

    /**
     * Parse swc4j media type.
     *
     * @param id the id
     * @return the swc4j media type
     * @since 0.1.0
     */
    public static Swc4jMediaType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Unknown;
    }

    @Override
    public int getId() {
        return id;
    }
}
