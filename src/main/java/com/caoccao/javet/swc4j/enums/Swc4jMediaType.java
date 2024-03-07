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

    private final int id;

    Swc4jMediaType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
