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

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBigInt;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTplLitType;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        getDefault = "TsLit::Bool(Bool::get_default())",
        mappings = {
                @Jni2RustEnumMapping(name = "BigInt", type = Swc4jAstBigInt.class),
                @Jni2RustEnumMapping(name = "Bool", type = Swc4jAstBool.class),
                @Jni2RustEnumMapping(name = "Number", type = Swc4jAstNumber.class),
                @Jni2RustEnumMapping(name = "Str", type = Swc4jAstStr.class),
                @Jni2RustEnumMapping(name = "Tpl", type = Swc4jAstTsTplLitType.class),
        }
)
public interface ISwc4jAstTsLit extends ISwc4jAst {
}
