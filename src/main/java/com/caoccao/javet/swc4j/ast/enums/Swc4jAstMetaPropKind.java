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

package com.caoccao.javet.swc4j.ast.enums;

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumIdName;

import java.util.stream.Stream;

public enum Swc4jAstMetaPropKind implements ISwc4jEnumIdName {
    NewTarget(0, "new.target"),
    ImportMeta(1, "import.meta"),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jAstMetaPropKind[] TYPES = new Swc4jAstMetaPropKind[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    Swc4jAstMetaPropKind(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Swc4jAstMetaPropKind parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : NewTarget;
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
