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

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumIdName;

import java.util.stream.Stream;

public enum Swc4jEsVersion implements ISwc4jEnumIdName {
    ES3(1, "es3"),
    ES5(2, "es5"),
    ES2015(3, "es2015"),
    ES2016(4, "es2016"),
    ES2017(5, "es2017"),
    ES2018(6, "es2018"),
    ES2019(7, "es2019"),
    ES2020(8, "es2020"),
    ES2021(9, "es2021"),
    ES2022(10, "es2022"),
    ES2023(11, "es2023"),
    ES2024(12, "es2024"),
    ESNext(0, "esnext"),
    ;

    private static final int LENGTH = values().length;
    private static final Swc4jEsVersion[] TYPES = new Swc4jEsVersion[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    Swc4jEsVersion(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Swc4jEsVersion parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : ESNext;
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
