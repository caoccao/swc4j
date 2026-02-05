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

package com.caoccao.javet.swc4j.enums;

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumId;

import java.util.stream.Stream;

/**
 * The enum Swc4j media type.
 *
 * @since 0.1.0
 */
public enum Swc4jMediaType implements ISwc4jEnumId {
    /**
     * Java script swc4j media type.
     */
    JavaScript(0),
    /**
     * Jsx swc4j media type.
     */
    Jsx(1),
    /**
     * Mjs swc4j media type.
     */
    Mjs(2),
    /**
     * Cjs swc4j media type.
     */
    Cjs(3),
    /**
     * Type script swc4j media type.
     */
    TypeScript(4),
    /**
     * Mts swc4j media type.
     */
    Mts(5),
    /**
     * Cts swc4j media type.
     */
    Cts(6),
    /**
     * Dts swc4j media type.
     */
    Dts(7),
    /**
     * Dmts swc4j media type.
     */
    Dmts(8),
    /**
     * Dcts swc4j media type.
     */
    Dcts(9),
    /**
     * Tsx swc4j media type.
     */
    Tsx(10),
    /**
     * Css swc4j media type.
     */
    Css(11),
    /**
     * Json swc4j media type.
     */
    Json(12),
    /**
     * Jsonc swc4j media type.
     */
    Jsonc(13),
    /**
     * Json 5 swc4j media type.
     */
    Json5(14),
    /**
     * Html swc4j media type.
     */
    Html(15),
    /**
     * Sql swc4j media type.
     */
    Sql(16),
    /**
     * Wasm swc4j media type.
     */
    Wasm(17),
    /**
     * Source map swc4j media type.
     */
    SourceMap(18),
    /**
     * Unknown swc4j media type.
     */
    Unknown(19),
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
