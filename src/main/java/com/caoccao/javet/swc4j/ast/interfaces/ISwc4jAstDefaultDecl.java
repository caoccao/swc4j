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

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstClassExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstFnExpr;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsInterfaceDecl;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        getDefault = "DefaultDecl::Class(ClassExpr::get_default())",
        mappings = {
                @Jni2RustEnumMapping(name = "Class", type = Swc4jAstClassExpr.class),
                @Jni2RustEnumMapping(name = "Fn", type = Swc4jAstFnExpr.class),
                @Jni2RustEnumMapping(name = "TsInterfaceDecl", type = Swc4jAstTsInterfaceDecl.class, box = true),
        }
)
public interface ISwc4jAstDefaultDecl extends ISwc4jAst {
}
