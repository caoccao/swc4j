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

import com.caoccao.javet.swc4j.ast.ts.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4jAstTsType {
    @Test
    public void testAssignable() {
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(ISwc4jAstTsFnOrConstructorType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(ISwc4jAstTsUnionOrIntersectionType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsArrayType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsConditionalType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsImportType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsIndexedAccessType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsInferType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsKeywordType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsLitType.class));
        assertTrue(ISwc4jAstTsType.class.isAssignableFrom(Swc4jAstTsMappedType.class));
    }
}
