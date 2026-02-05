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
 * The interface swc4j ast ts type.
 */
@Jni2RustClass(
        mappings = {
                @Jni2RustEnumMapping(name = "TsArrayType", type = Swc4jAstTsArrayType.class),
                @Jni2RustEnumMapping(name = "TsConditionalType", type = Swc4jAstTsConditionalType.class),
                @Jni2RustEnumMapping(name = "TsFnOrConstructorType", type = ISwc4jAstTsFnOrConstructorType.class),
                @Jni2RustEnumMapping(name = "TsImportType", type = Swc4jAstTsImportType.class),
                @Jni2RustEnumMapping(name = "TsIndexedAccessType", type = Swc4jAstTsIndexedAccessType.class),
                @Jni2RustEnumMapping(name = "TsInferType", type = Swc4jAstTsInferType.class),
                @Jni2RustEnumMapping(name = "TsKeywordType", type = Swc4jAstTsKeywordType.class),
                @Jni2RustEnumMapping(name = "TsLitType", type = Swc4jAstTsLitType.class),
                @Jni2RustEnumMapping(name = "TsMappedType", type = Swc4jAstTsMappedType.class),
                @Jni2RustEnumMapping(name = "TsOptionalType", type = Swc4jAstTsOptionalType.class),
                @Jni2RustEnumMapping(name = "TsParenthesizedType", type = Swc4jAstTsParenthesizedType.class),
                @Jni2RustEnumMapping(name = "TsRestType", type = Swc4jAstTsRestType.class),
                @Jni2RustEnumMapping(name = "TsThisType", type = Swc4jAstTsThisType.class),
                @Jni2RustEnumMapping(name = "TsTupleType", type = Swc4jAstTsTupleType.class),
                @Jni2RustEnumMapping(name = "TsTypeLit", type = Swc4jAstTsTypeLit.class),
                @Jni2RustEnumMapping(name = "TsTypeOperator", type = Swc4jAstTsTypeOperator.class),
                @Jni2RustEnumMapping(name = "TsTypePredicate", type = Swc4jAstTsTypePredicate.class),
                @Jni2RustEnumMapping(name = "TsTypeQuery", type = Swc4jAstTsTypeQuery.class),
                @Jni2RustEnumMapping(name = "TsTypeRef", type = Swc4jAstTsTypeRef.class),
                @Jni2RustEnumMapping(name = "TsUnionOrIntersectionType", type = ISwc4jAstTsUnionOrIntersectionType.class),
        }
)
public interface ISwc4jAstTsType extends ISwc4jAst {
}
