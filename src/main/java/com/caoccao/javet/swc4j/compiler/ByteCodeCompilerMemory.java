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

import com.caoccao.javet.swc4j.compiler.memory.CompilationContext;
import com.caoccao.javet.swc4j.compiler.memory.ScopedTypeRegistry;
import com.caoccao.javet.swc4j.compiler.memory.TypeRegistry;

import java.util.HashMap;
import java.util.Map;

public final class ByteCodeCompilerMemory {
    private final CompilationContext compilationContext;
    private final ScopedTypeRegistry scopedTypeRegistry;
    private final Map<String, String> typeAliasMap;
    private final TypeRegistry typeRegistry;

    public ByteCodeCompilerMemory() {
        compilationContext = new CompilationContext();
        scopedTypeRegistry = new ScopedTypeRegistry();
        typeRegistry = new TypeRegistry(scopedTypeRegistry);
        typeAliasMap = new HashMap<>();
    }

    /**
     * Get the current CompilationContext.
     *
     * @return the current compilation context
     */
    public CompilationContext getCompilationContext() {
        return compilationContext;
    }

    public ScopedTypeRegistry getScopedTypeRegistry() {
        return scopedTypeRegistry;
    }

    public Map<String, String> getTypeAliasMap() {
        return typeAliasMap;
    }

    public TypeRegistry getTypeRegistry() {
        return typeRegistry;
    }

    public void reset() {
        compilationContext.reset();
        scopedTypeRegistry.clear();
        typeRegistry.clear();
        typeAliasMap.clear();
    }

    public void resetCompilationContext() {
        compilationContext.reset();
    }
}
