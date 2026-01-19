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

package com.caoccao.javet.swc4j.ast.interfaces;

import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "Block", type = Swc4jAstBlockStmt.class),
                @Jni2RustEnumMapping(name = "Break", type = Swc4jAstBreakStmt.class),
                @Jni2RustEnumMapping(name = "Continue", type = Swc4jAstContinueStmt.class),
                @Jni2RustEnumMapping(name = "Debugger", type = Swc4jAstDebuggerStmt.class),
                @Jni2RustEnumMapping(name = "Decl", type = ISwc4jAstDecl.class),
                @Jni2RustEnumMapping(name = "DoWhile", type = Swc4jAstDoWhileStmt.class),
                @Jni2RustEnumMapping(name = "Empty", type = Swc4jAstEmptyStmt.class),
                @Jni2RustEnumMapping(name = "Expr", type = Swc4jAstExprStmt.class),
                @Jni2RustEnumMapping(name = "For", type = Swc4jAstForStmt.class),
                @Jni2RustEnumMapping(name = "ForIn", type = Swc4jAstForInStmt.class),
                @Jni2RustEnumMapping(name = "ForOf", type = Swc4jAstForOfStmt.class),
                @Jni2RustEnumMapping(name = "If", type = Swc4jAstIfStmt.class),
                @Jni2RustEnumMapping(name = "Labeled", type = Swc4jAstLabeledStmt.class),
                @Jni2RustEnumMapping(name = "Return", type = Swc4jAstReturnStmt.class),
                @Jni2RustEnumMapping(name = "Switch", type = Swc4jAstSwitchStmt.class),
                @Jni2RustEnumMapping(name = "Throw", type = Swc4jAstThrowStmt.class),
                @Jni2RustEnumMapping(name = "Try", type = Swc4jAstTryStmt.class, box = true),
                @Jni2RustEnumMapping(name = "While", type = Swc4jAstWhileStmt.class),
                @Jni2RustEnumMapping(name = "With", type = Swc4jAstWithStmt.class),
        }
)
public interface ISwc4jAstStmt extends ISwc4jAstModuleItem {
    static ISwc4jAstStmt createDefault() {
        return Swc4jAstEmptyStmt.create();
    }
}
