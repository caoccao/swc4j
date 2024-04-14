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

package com.caoccao.javet.swc4j.ast.interfaces;

import com.caoccao.javet.swc4j.ast.module.Swc4jAstImportDefaultSpecifier;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstImportNamedSpecifier;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstImportStarAsSpecifier;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        getDefault = "ImportSpecifier::Default(ImportDefaultSpecifier::get_default())",
        mappings = {
                @Jni2RustEnumMapping(name = "Default", type = Swc4jAstImportDefaultSpecifier.class),
                @Jni2RustEnumMapping(name = "Named", type = Swc4jAstImportNamedSpecifier.class),
                @Jni2RustEnumMapping(name = "Namespace", type = Swc4jAstImportStarAsSpecifier.class),
        }
)
public interface ISwc4jAstImportSpecifier extends ISwc4jAst {
}
