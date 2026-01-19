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
 * What to do with import statements that only import types i.e. whether to
 * remove them (`Remove`), keep them as side-effect imports (`Preserve`)
 * or error (`Error`). Defaults to `Remove`.
 *
 * @since 0.1.0
 */
public enum Swc4jImportsNotUsedAsValues implements ISwc4jEnumId {
    Remove(0),
    Preserve(1),
    Error(2);

    private static final int LENGTH = values().length;
    private static final Swc4jImportsNotUsedAsValues[] TYPES = new Swc4jImportsNotUsedAsValues[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jImportsNotUsedAsValues(int id) {
        this.id = id;
    }

    /**
     * Parse swc4j imports not used as values.
     *
     * @param id the id
     * @return the swc4j media type
     * @since 0.1.0
     */
    public static Swc4jImportsNotUsedAsValues parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Remove;
    }

    @Override
    public int getId() {
        return id;
    }
}
