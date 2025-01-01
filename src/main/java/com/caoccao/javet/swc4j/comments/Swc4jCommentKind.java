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

package com.caoccao.javet.swc4j.comments;

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumId;

import java.util.stream.Stream;

public enum Swc4jCommentKind implements ISwc4jEnumId {
    Line(0),
    Block(1),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jCommentKind[] TYPES = new Swc4jCommentKind[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jCommentKind(int id) {
        this.id = id;
    }

    public static Swc4jCommentKind parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Line;
    }

    @Override
    public int getId() {
        return id;
    }
}
