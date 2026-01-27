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
import java.util.Optional;

public record ByteCodeCompilerOptions(
        JdkVersion jdkVersion,
        Map<String, String> typeAliasMap,
        String packagePrefix,
        Optional<ClassLoader> optionalParentClassLoader,
        boolean debug) {
    private static final Map<String, String> DEFAULT_TYPE_ALIAS_MAP = new HashMap<>();

    static {
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                "boolean", "boolean",
                "byte", "byte",
                "char", "char",
                "double", "double",
                "float", "float",
                "int", "int",
                "long", "long",
                "short", "short"
        ));
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
                "BigInteger", "java.math.BigInteger",
                "Pattern", "java.util.regex.Pattern",
                "String", "java.lang.String",
                "Object", "java.lang.Object",
                "number", "java.lang.Number",
                "Number", "java.lang.Number",
                "void", "void"
        ));
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                "unknown", "java.lang.Object",
                "any", "java.lang.Object"
        ));
        // JavaScript built-in error types
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                "Error", "com.caoccao.javet.swc4j.exceptions.JsError",
                "TypeError", "com.caoccao.javet.swc4j.exceptions.JsTypeError",
                "RangeError", "com.caoccao.javet.swc4j.exceptions.JsRangeError",
                "ReferenceError", "com.caoccao.javet.swc4j.exceptions.JsReferenceError",
                "SyntaxError", "com.caoccao.javet.swc4j.exceptions.JsSyntaxError",
                "URIError", "com.caoccao.javet.swc4j.exceptions.JsURIError",
                "EvalError", "com.caoccao.javet.swc4j.exceptions.JsEvalError",
                "AggregateError", "com.caoccao.javet.swc4j.exceptions.JsAggregateError"
        ));
    }

    public ByteCodeCompilerOptions(JdkVersion jdkVersion, Map<String, String> typeAliasMap) {
        this(jdkVersion, typeAliasMap, "", Optional.empty(), false);
    }

    public ByteCodeCompilerOptions(JdkVersion jdkVersion) {
        this(jdkVersion, new HashMap<>(DEFAULT_TYPE_ALIAS_MAP));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean debug = false;
        private JdkVersion jdkVersion = JdkVersion.JDK_17;
        private String packagePrefix = "";
        private ClassLoader parentClassLoader = null;
        private Map<String, String> typeAliasMap = new HashMap<>(DEFAULT_TYPE_ALIAS_MAP);

        public ByteCodeCompilerOptions build() {
            return new ByteCodeCompilerOptions(jdkVersion, typeAliasMap, packagePrefix, Optional.ofNullable(parentClassLoader), debug);
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder jdkVersion(JdkVersion jdkVersion) {
            this.jdkVersion = jdkVersion;
            return this;
        }

        public Builder packagePrefix(String packagePrefix) {
            this.packagePrefix = packagePrefix;
            return this;
        }

        public Builder parentClassLoader(ClassLoader parentClassLoader) {
            this.parentClassLoader = parentClassLoader;
            return this;
        }

        public Builder typeAliasMap(Map<String, String> typeAliasMap) {
            this.typeAliasMap = typeAliasMap;
            return this;
        }
    }
}
