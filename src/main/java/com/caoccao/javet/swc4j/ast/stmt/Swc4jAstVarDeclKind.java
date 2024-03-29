/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.ast.stmt;

import java.util.stream.Stream;

public enum Swc4jAstVarDeclKind {
    Const(0),
    Let(1),
    Var(2);

    private static final int LENGTH = values().length;
    private static final Swc4jAstVarDeclKind[] TYPES = new Swc4jAstVarDeclKind[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;

    Swc4jAstVarDeclKind(int id) {
        this.id = id;
    }

    public static Swc4jAstVarDeclKind parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Const;
    }

    public int getId() {
        return id;
    }
}
