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

import com.caoccao.javet.swc4j.ast.expr.lit.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4jAstLit {
    @Test
    public void testAssignable() {
        assertTrue(ISwc4jAstLit.class.isAssignableFrom(Swc4jAstBigInt.class));
        assertTrue(ISwc4jAstLit.class.isAssignableFrom(Swc4jAstBool.class));
        assertTrue(ISwc4jAstLit.class.isAssignableFrom(Swc4jAstJsxText.class));
        assertTrue(ISwc4jAstLit.class.isAssignableFrom(Swc4jAstNull.class));
        assertTrue(ISwc4jAstLit.class.isAssignableFrom(Swc4jAstNumber.class));
        assertTrue(ISwc4jAstLit.class.isAssignableFrom(Swc4jAstRegex.class));
        assertTrue(ISwc4jAstLit.class.isAssignableFrom(Swc4jAstStr.class));
    }
}
