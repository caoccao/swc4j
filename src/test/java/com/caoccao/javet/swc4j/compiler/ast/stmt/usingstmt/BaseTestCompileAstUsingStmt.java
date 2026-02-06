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

package com.caoccao.javet.swc4j.compiler.ast.stmt.usingstmt;

import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompilerOptions;
import com.caoccao.javet.swc4j.compiler.JdkVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Base test suite for using declaration statement tests.
 * Provides a compiler with AutoCloseable type alias registered.
 */
public abstract class BaseTestCompileAstUsingStmt {
    /**
     * Creates a compiler with AutoCloseable type alias registered.
     *
     * @param jdkVersion the JDK version
     * @return the compiler
     */
    protected ByteCodeCompiler getCompiler(JdkVersion jdkVersion) {
        Map<String, String> typeAliases = new HashMap<>();
        typeAliases.put("AutoCloseable", "java.lang.AutoCloseable");
        typeAliases.put("Object", "java.lang.Object");
        typeAliases.put("String", "java.lang.String");
        typeAliases.put("Error", "java.lang.Error");
        return ByteCodeCompiler.of(ByteCodeCompilerOptions.builder()
                .jdkVersion(jdkVersion)
                .typeAliasMap(typeAliases)
                .debug(true)
                .build());
    }
}
