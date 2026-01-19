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

import com.caoccao.javet.swc4j.ast.clazz.*;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstEmptyStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsIndexSignature;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "AutoAccessor", type = Swc4jAstAutoAccessor.class),
                @Jni2RustEnumMapping(name = "ClassProp", type = Swc4jAstClassProp.class),
                @Jni2RustEnumMapping(name = "Constructor", type = Swc4jAstConstructor.class),
                @Jni2RustEnumMapping(name = "Empty", type = Swc4jAstEmptyStmt.class),
                @Jni2RustEnumMapping(name = "Method", type = Swc4jAstClassMethod.class),
                @Jni2RustEnumMapping(name = "PrivateMethod", type = Swc4jAstPrivateMethod.class),
                @Jni2RustEnumMapping(name = "PrivateProp", type = Swc4jAstPrivateProp.class),
                @Jni2RustEnumMapping(name = "StaticBlock", type = Swc4jAstStaticBlock.class),
                @Jni2RustEnumMapping(name = "TsIndexSignature", type = Swc4jAstTsIndexSignature.class),
        }
)
public interface ISwc4jAstClassMember extends ISwc4jAst {
}
