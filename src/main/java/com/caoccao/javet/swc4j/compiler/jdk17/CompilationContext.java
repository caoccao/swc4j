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

package com.caoccao.javet.swc4j.compiler.jdk17;

import java.util.HashMap;
import java.util.Map;

public class CompilationContext {
    private final Map<String, GenericTypeInfo> genericTypeInfoMap = new HashMap<>();
    private final Map<String, String> inferredTypes = new HashMap<>();
    private final LocalVariableTable localVariableTable = new LocalVariableTable();

    public Map<String, GenericTypeInfo> getGenericTypeInfoMap() {
        return genericTypeInfoMap;
    }

    public Map<String, String> getInferredTypes() {
        return inferredTypes;
    }

    public LocalVariableTable getLocalVariableTable() {
        return localVariableTable;
    }
}
