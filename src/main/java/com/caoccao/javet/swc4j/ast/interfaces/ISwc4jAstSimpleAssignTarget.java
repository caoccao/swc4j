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

import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstInvalid;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustEnumMapping;

@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "Ident", type = Swc4jAstBindingIdent.class),
                @Jni2RustEnumMapping(name = "Invalid", type = Swc4jAstInvalid.class),
                @Jni2RustEnumMapping(name = "Member", type = Swc4jAstMemberExpr.class),
                @Jni2RustEnumMapping(name = "OptChain", type = Swc4jAstOptChainExpr.class),
                @Jni2RustEnumMapping(name = "Paren", type = Swc4jAstParenExpr.class),
                @Jni2RustEnumMapping(name = "SuperProp", type = Swc4jAstSuperPropExpr.class),
                @Jni2RustEnumMapping(name = "TsAs", type = Swc4jAstTsAsExpr.class),
                @Jni2RustEnumMapping(name = "TsInstantiation", type = Swc4jAstTsInstantiation.class),
                @Jni2RustEnumMapping(name = "TsNonNull", type = Swc4jAstTsNonNullExpr.class),
                @Jni2RustEnumMapping(name = "TsSatisfies", type = Swc4jAstTsSatisfiesExpr.class),
                @Jni2RustEnumMapping(name = "TsTypeAssertion", type = Swc4jAstTsTypeAssertion.class),
        }
)
public interface ISwc4jAstSimpleAssignTarget extends ISwc4jAstAssignTarget {
}
