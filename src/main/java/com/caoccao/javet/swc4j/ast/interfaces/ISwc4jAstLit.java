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

import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "BigInt", type = Swc4jAstBigInt.class),
                @Jni2RustEnumMapping(name = "Bool", type = Swc4jAstBool.class),
                @Jni2RustEnumMapping(name = "JSXText", type = Swc4jAstJsxText.class),
                @Jni2RustEnumMapping(name = "Null", type = Swc4jAstNull.class),
                @Jni2RustEnumMapping(name = "Num", type = Swc4jAstNumber.class),
                @Jni2RustEnumMapping(name = "Regex", type = Swc4jAstRegex.class),
                @Jni2RustEnumMapping(name = "Str", type = Swc4jAstStr.class),
        }
)
public interface ISwc4jAstLit
        extends ISwc4jAstExpr, ISwc4jAstJsxAttrValue {
}
