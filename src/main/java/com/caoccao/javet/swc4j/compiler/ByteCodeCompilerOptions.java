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

import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.exceptions.*;

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
                ConstantJavaType.PRIMITIVE_BOOLEAN, ConstantJavaType.PRIMITIVE_BOOLEAN,
                ConstantJavaType.PRIMITIVE_BYTE, ConstantJavaType.PRIMITIVE_BYTE,
                ConstantJavaType.PRIMITIVE_CHAR, ConstantJavaType.PRIMITIVE_CHAR,
                ConstantJavaType.PRIMITIVE_DOUBLE, ConstantJavaType.PRIMITIVE_DOUBLE,
                ConstantJavaType.PRIMITIVE_FLOAT, ConstantJavaType.PRIMITIVE_FLOAT,
                ConstantJavaType.PRIMITIVE_INT, ConstantJavaType.PRIMITIVE_INT,
                ConstantJavaType.PRIMITIVE_LONG, ConstantJavaType.PRIMITIVE_LONG,
                ConstantJavaType.PRIMITIVE_SHORT, ConstantJavaType.PRIMITIVE_SHORT
        ));
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                ConstantJavaType.SIMPLE_BOOLEAN, ConstantJavaType.CLASS_JAVA_LANG_BOOLEAN,
                ConstantJavaType.SIMPLE_BYTE, ConstantJavaType.CLASS_JAVA_LANG_BYTE,
                ConstantJavaType.SIMPLE_CHARACTER, ConstantJavaType.CLASS_JAVA_LANG_CHARACTER,
                ConstantJavaType.SIMPLE_DOUBLE, ConstantJavaType.CLASS_JAVA_LANG_DOUBLE,
                ConstantJavaType.SIMPLE_FLOAT, ConstantJavaType.CLASS_JAVA_LANG_FLOAT,
                ConstantJavaType.SIMPLE_INTEGER, ConstantJavaType.CLASS_JAVA_LANG_INTEGER,
                ConstantJavaType.SIMPLE_LONG, ConstantJavaType.CLASS_JAVA_LANG_LONG,
                ConstantJavaType.SIMPLE_SHORT, ConstantJavaType.CLASS_JAVA_LANG_SHORT
        ));
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                ConstantJavaType.SIMPLE_BIGINTEGER, ConstantJavaType.CLASS_JAVA_MATH_BIGINTEGER,
                ConstantJavaType.SIMPLE_PATTERN, ConstantJavaType.CLASS_JAVA_UTIL_REGEX_PATTERN,
                ConstantJavaType.SIMPLE_STRING, ConstantJavaType.CLASS_JAVA_LANG_STRING,
                ConstantJavaType.SIMPLE_OBJECT, ConstantJavaType.CLASS_JAVA_LANG_OBJECT,
                ConstantJavaType.TYPE_ALIAS_NUMBER, ConstantJavaType.CLASS_JAVA_LANG_NUMBER,
                ConstantJavaType.SIMPLE_NUMBER, ConstantJavaType.CLASS_JAVA_LANG_NUMBER,
                ConstantJavaType.PRIMITIVE_VOID, ConstantJavaType.PRIMITIVE_VOID
        ));
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                ConstantJavaType.TYPE_ALIAS_UNKNOWN, ConstantJavaType.CLASS_JAVA_LANG_OBJECT,
                ConstantJavaType.TYPE_ALIAS_ANY, ConstantJavaType.CLASS_JAVA_LANG_OBJECT
        ));
        // JavaScript built-in error types
        DEFAULT_TYPE_ALIAS_MAP.putAll(Map.of(
                JsError.NAME, JsError.class.getName(),
                JsTypeError.NAME, JsTypeError.class.getName(),
                JsRangeError.NAME, JsRangeError.class.getName(),
                JsReferenceError.NAME, JsReferenceError.class.getName(),
                JsSyntaxError.NAME, JsSyntaxError.class.getName(),
                JsURIError.NAME, JsURIError.class.getName(),
                JsEvalError.NAME, JsEvalError.class.getName(),
                JsAggregateError.NAME, JsAggregateError.class.getName()
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
