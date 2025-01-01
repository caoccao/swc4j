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
 * The kind of module being transpiled.
 * Defaults to being derived from the media type of the parsed source.
 *
 * @since 1.2.0
 */
public enum Swc4jModuleKind implements ISwc4jEnumId {
    /**
     * Auto.
     *
     * @since 1.2.0
     */
    Auto(0),
    /**
     * Es Module.
     *
     * @since 1.2.0
     */
    Esm(1),
    /**
     * CommonJS Module.
     *
     * @since 1.2.0
     */
    Cjs(2),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jModuleKind[] TYPES = new Swc4jModuleKind[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jModuleKind(int id) {
        this.id = id;
    }

    /**
     * Parse swc4j module kind.
     *
     * @param id the id
     * @return the swc4j module kind
     * @since 1.2.0
     */
    public static Swc4jModuleKind parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Auto;
    }

    @Override
    public int getId() {
        return id;
    }
}
