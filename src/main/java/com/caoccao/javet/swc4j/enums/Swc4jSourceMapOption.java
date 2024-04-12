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

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumId;

import java.util.stream.Stream;

/**
 * The enum Swc4j source map option.
 *
 * @since 0.3.0
 */
public enum Swc4jSourceMapOption implements ISwc4jEnumId {
    /**
     * Source map should be inlined into the source (default)
     *
     * @since 0.3.0
     */
    Inline(0),
    /**
     * Source map should be generated as a separate file.
     *
     * @since 0.3.0
     */
    Separate(1),
    /**
     * Source map should not be generated at all.
     *
     * @since 0.3.0
     */
    None(2),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jSourceMapOption[] TYPES = new Swc4jSourceMapOption[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jSourceMapOption(int id) {
        this.id = id;
    }

    /**
     * Parse swc4j source map option.
     *
     * @param id the id
     * @return the swc4j source map option
     * @since 0.3.0
     */
    public static Swc4jSourceMapOption parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Inline;
    }

    @Override
    public int getId() {
        return id;
    }
}
