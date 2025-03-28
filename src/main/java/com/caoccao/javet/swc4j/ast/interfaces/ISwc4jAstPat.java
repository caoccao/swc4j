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

import com.caoccao.javet.swc4j.ast.pat.*;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "Array", type = Swc4jAstArrayPat.class),
                @Jni2RustEnumMapping(name = "Assign", type = Swc4jAstAssignPat.class),
                @Jni2RustEnumMapping(name = "Expr", type = ISwc4jAstExpr.class, box = true),
                @Jni2RustEnumMapping(name = "Ident", type = Swc4jAstBindingIdent.class),
                @Jni2RustEnumMapping(name = "Invalid", type = Swc4jAstInvalid.class),
                @Jni2RustEnumMapping(name = "Object", type = Swc4jAstObjectPat.class),
                @Jni2RustEnumMapping(name = "Rest", type = Swc4jAstRestPat.class),
        }
)
public interface ISwc4jAstPat extends ISwc4jAstAssignTarget, ISwc4jAstForHead {
}
