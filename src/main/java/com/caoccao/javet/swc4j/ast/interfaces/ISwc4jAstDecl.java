/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.ast.interfaces;

import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "Class", type = Swc4jAstClassDecl.class),
                @Jni2RustEnumMapping(name = "Fn", type = Swc4jAstFnDecl.class),
                @Jni2RustEnumMapping(name = "TsEnum", type = Swc4jAstTsEnumDecl.class, box = true),
                @Jni2RustEnumMapping(name = "TsInterface", type = Swc4jAstTsInterfaceDecl.class, box = true),
                @Jni2RustEnumMapping(name = "TsModule", type = Swc4jAstTsModuleDecl.class, box = true),
                @Jni2RustEnumMapping(name = "TsTypeAlias", type = Swc4jAstTsTypeAliasDecl.class, box = true),
                @Jni2RustEnumMapping(name = "Using", type = Swc4jAstUsingDecl.class, box = true),
                @Jni2RustEnumMapping(name = "Var", type = Swc4jAstVarDecl.class, box = true),
        }
)
public interface ISwc4jAstDecl extends ISwc4jAstStmt {
}
