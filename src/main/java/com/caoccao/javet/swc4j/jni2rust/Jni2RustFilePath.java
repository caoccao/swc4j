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

/**
 * Enum for Rust file paths used in JNI code generation.
 */
public enum Jni2RustFilePath {
    /** AST utilities file path */
    AstUtils("rust/src/ast_utils.rs"),
    /** Comment utilities file path */
    CommentUtils("rust/src/comment_utils.rs"),
    /** No file path */
    None(null),
    /** Options file path */
    Options("rust/src/options.rs"),
    /** Outputs file path */
    Outputs("rust/src/outputs.rs"),
    /** Plugin utilities file path */
    PluginUtils("rust/src/plugin_utils.rs"),
    /** Span utilities file path */
    SpanUtils("rust/src/span_utils.rs"),
    /** Token utilities file path */
    TokenUtils("rust/src/token_utils.rs"),
    ;

    private final String filePath;

    /**
     * Constructs a Jni2RustFilePath with the given path.
     *
     * @param filePath the file path
     */
    Jni2RustFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }
}
