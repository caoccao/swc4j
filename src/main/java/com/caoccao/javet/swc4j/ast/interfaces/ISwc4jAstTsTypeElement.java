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

import com.caoccao.javet.swc4j.ast.ts.*;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

/**
 * The interface swc4j ast ts type element.
 */
@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "TsCallSignatureDecl", type = Swc4jAstTsCallSignatureDecl.class),
                @Jni2RustEnumMapping(name = "TsConstructSignatureDecl", type = Swc4jAstTsConstructSignatureDecl.class),
                @Jni2RustEnumMapping(name = "TsGetterSignature", type = Swc4jAstTsGetterSignature.class),
                @Jni2RustEnumMapping(name = "TsIndexSignature", type = Swc4jAstTsIndexSignature.class),
                @Jni2RustEnumMapping(name = "TsMethodSignature", type = Swc4jAstTsMethodSignature.class),
                @Jni2RustEnumMapping(name = "TsPropertySignature", type = Swc4jAstTsPropertySignature.class),
                @Jni2RustEnumMapping(name = "TsSetterSignature", type = Swc4jAstTsSetterSignature.class),
        }
)
public interface ISwc4jAstTsTypeElement extends ISwc4jAst {
}
