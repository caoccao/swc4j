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

/**
 * The enum Swc4j media type.
 *
 * @since 0.1.0
 */
public enum Swc4jMediaType {
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
    Json(11),
    Wasm(12),
    TsBuildInfo(13),
    SourceMap(14),
    Unknown(15);

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

    /**
     * Gets id.
     *
     * @return the id
     * @since 0.1.0
     */
    public int getId() {
        return id;
    }
}
