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

package com.caoccao.javet.swc4j.jni2rust;

public enum Jni2RustFilePath {
    AstUtils("rust/src/ast_utils.rs"),
    None(null),
    Options("rust/src/options.rs"),
    Outputs("rust/src/outputs.rs"),
    SpanUtils("rust/src/span_utils.rs"),
    TokenUtils("rust/src/token_utils.rs"),
    ;

    private final String filePath;

    Jni2RustFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
