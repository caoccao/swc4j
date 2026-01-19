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

package com.caoccao.javet.swc4j.jni2rust;

import java.util.HashMap;
import java.util.Map;

public class Jni2RustOptions {
    protected static final Map<String, String> DEFAULT_JAVA_TYPE_TO_JNI_SIMPLE_TYPE_MAP = Map.of(
            "int", "I",
            "long", "J",
            "short", "S",
            "char", "C",
            "byte", "B",
            "boolean", "Z",
            "float", "F",
            "double", "D");
    protected static final Map<String, String> DEFAULT_JAVA_TYPE_TO_RUST_TYPE_MAP = Map.of(
            "int", "i32",
            "long", "i64",
            "short", "i16",
            "char", "char",
            "byte", "i8",
            "boolean", "bool",
            "float", "f32",
            "double", "f64");
    protected final Map<String, String> javaTypeToJniSimpleTypeMap;
    protected final Map<String, String> javaTypeToRustTypeMap;

    public Jni2RustOptions() {
        javaTypeToJniSimpleTypeMap = new HashMap<>(DEFAULT_JAVA_TYPE_TO_JNI_SIMPLE_TYPE_MAP);
        javaTypeToRustTypeMap = new HashMap<>(DEFAULT_JAVA_TYPE_TO_RUST_TYPE_MAP);
    }

    public Map<String, String> getJavaTypeToJniSimpleTypeMap() {
        return javaTypeToJniSimpleTypeMap;
    }

    public Map<String, String> getJavaTypeToRustTypeMap() {
        return javaTypeToRustTypeMap;
    }
}
