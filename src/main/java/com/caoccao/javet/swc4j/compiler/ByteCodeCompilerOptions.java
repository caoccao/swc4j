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

/**
 * Represents compilation options for the bytecode compiler.
 *
 * @param jdkVersion                the target JDK version
 * @param typeAliasMap              a map of type aliases for resolving JavaScript/TypeScript types to Java types
 * @param packagePrefix             the package prefix for generated classes
 * @param optionalParentClassLoader the optional parent class loader
 * @param debug                     whether to enable debug mode
 */
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

    /**
     * Constructs compiler options with JDK version and type alias map.
     *
     * @param jdkVersion   the target JDK version
     * @param typeAliasMap the type alias map
     */
    public ByteCodeCompilerOptions(JdkVersion jdkVersion, Map<String, String> typeAliasMap) {
        this(jdkVersion, typeAliasMap, "", Optional.empty(), false);
    }

    /**
     * Constructs compiler options with JDK version and default type aliases.
     *
     * @param jdkVersion the target JDK version
     */
    public ByteCodeCompilerOptions(JdkVersion jdkVersion) {
        this(jdkVersion, new HashMap<>(DEFAULT_TYPE_ALIAS_MAP));
    }

    /**
     * Creates a new builder for ByteCodeCompilerOptions.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing ByteCodeCompilerOptions.
     */
    public static class Builder {
        private boolean debug = false;
        private JdkVersion jdkVersion = JdkVersion.JDK_17;
        private String packagePrefix = "";
        private ClassLoader parentClassLoader = null;
        private Map<String, String> typeAliasMap = new HashMap<>(DEFAULT_TYPE_ALIAS_MAP);

        /**
         * Constructs a new Builder with default settings.
         */
        public Builder() {
        }

        /**
         * Builds the ByteCodeCompilerOptions instance.
         *
         * @return a new ByteCodeCompilerOptions instance
         */
        public ByteCodeCompilerOptions build() {
            return new ByteCodeCompilerOptions(jdkVersion, typeAliasMap, packagePrefix, Optional.ofNullable(parentClassLoader), debug);
        }

        /**
         * Sets the debug mode.
         *
         * @param debug whether to enable debug mode
         * @return this Builder instance
         */
        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * Sets the JDK version.
         *
         * @param jdkVersion the target JDK version
         * @return this Builder instance
         */
        public Builder jdkVersion(JdkVersion jdkVersion) {
            this.jdkVersion = jdkVersion;
            return this;
        }

        /**
         * Sets the package prefix for generated classes.
         *
         * @param packagePrefix the package prefix
         * @return this Builder instance
         */
        public Builder packagePrefix(String packagePrefix) {
            this.packagePrefix = packagePrefix;
            return this;
        }

        /**
         * Sets the parent class loader.
         *
         * @param parentClassLoader the parent class loader
         * @return this Builder instance
         */
        public Builder parentClassLoader(ClassLoader parentClassLoader) {
            this.parentClassLoader = parentClassLoader;
            return this;
        }

        /**
         * Sets the type alias map.
         *
         * @param typeAliasMap the type alias map
         * @return this Builder instance
         */
        public Builder typeAliasMap(Map<String, String> typeAliasMap) {
            this.typeAliasMap = typeAliasMap;
            return this;
        }
    }
}
