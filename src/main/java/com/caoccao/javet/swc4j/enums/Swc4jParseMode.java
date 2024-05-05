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
 * Parse mode determines how the source code is parsed.
 * There are actually 3 modes: Module, Script, Program.
 * For now, only Module and Script are supported.
 *
 * @since 0.1.0
 */
public enum Swc4jParseMode implements ISwc4jEnumId {
    Program(0),
    Module(1),
    Script(2),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jParseMode[] TYPES = new Swc4jParseMode[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jParseMode(int id) {
        this.id = id;
    }

    /**
     * Parse swc4j parse mode.
     *
     * @param id the id
     * @return the swc4j parse mode
     * @since 0.1.0
     */
    public static Swc4jParseMode parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Program;
    }

    @Override
    public int getId() {
        return id;
    }
}
