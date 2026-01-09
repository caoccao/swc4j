/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler;

import java.util.HashMap;
import java.util.Map;

public record ByteCodeCompilerOptions(JdkVersion jdkVersion, Map<String, String> typeAliasMap, String packagePrefix) {
    private static final Map<String, String> DEFAULT_TYPE_ALIAS_MAP = new HashMap<>();

    static {
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                "Boolean", "java.lang.Boolean",
                "Byte", "java.lang.Byte",
                "Character", "java.lang.Character",
                "Double", "java.lang.Double",
                "Float", "java.lang.Float",
                "Integer", "java.lang.Integer",
                "Long", "java.lang.Long",
                "Short", "java.lang.Short"
        ));
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                "BigInteger", "java.lang.BigInteger",
                "String", "java.lang.String",
                "Object", "java.lang.Object",
                "void", "void"
        ));
    }

    public ByteCodeCompilerOptions(JdkVersion jdkVersion, Map<String, String> typeAliasMap) {
        this(jdkVersion, typeAliasMap, "");
    }

    public ByteCodeCompilerOptions(JdkVersion jdkVersion) {
        this(jdkVersion, new HashMap<>(DEFAULT_TYPE_ALIAS_MAP));
    }
}
