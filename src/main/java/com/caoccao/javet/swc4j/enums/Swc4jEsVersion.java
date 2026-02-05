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

import com.caoccao.javet.swc4j.interfaces.ISwc4jEnumIdName;

import java.util.stream.Stream;

/**
 * The enum swc4j es version.
 */
public enum Swc4jEsVersion implements ISwc4jEnumIdName {
    /**
     * Es 3 swc4j es version.
     */
    ES3(1, "es3"),
    /**
     * Es 5 swc4j es version.
     */
    ES5(2, "es5"),
    /**
     * Es 2015 swc4j es version.
     */
    ES2015(3, "es2015"),
    /**
     * Es 2016 swc4j es version.
     */
    ES2016(4, "es2016"),
    /**
     * Es 2017 swc4j es version.
     */
    ES2017(5, "es2017"),
    /**
     * Es 2018 swc4j es version.
     */
    ES2018(6, "es2018"),
    /**
     * Es 2019 swc4j es version.
     */
    ES2019(7, "es2019"),
    /**
     * Es 2020 swc4j es version.
     */
    ES2020(8, "es2020"),
    /**
     * Es 2021 swc4j es version.
     */
    ES2021(9, "es2021"),
    /**
     * Es 2022 swc4j es version.
     */
    ES2022(10, "es2022"),
    /**
     * Es 2023 swc4j es version.
     */
    ES2023(11, "es2023"),
    /**
     * Es 2024 swc4j es version.
     */
    ES2024(12, "es2024"),
    /**
     * Es next swc4j es version.
     */
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

    /**
     * Parse swc4j es version.
     *
     * @param id the id
     * @return the swc4j es version
     */
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
