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

import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsNamespaceDecl;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        getDefault = "TsNamespaceBody::TsModuleBlock(TsModuleBlock::get_default())",
        mappings = {
                @Jni2RustEnumMapping(name = "TsModuleBlock", type = Swc4jAstTsModuleBlock.class),
                @Jni2RustEnumMapping(name = "TsNamespaceDecl", type = Swc4jAstTsNamespaceDecl.class),
        }
)
public interface ISwc4jAstTsNamespaceBody extends ISwc4jAst {
}
