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

import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstJsxText;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4jAstJsxElementChild {
    @Test
    public void testAssignable() {
        assertTrue(ISwc4jAstJsxElementChild.class.isAssignableFrom(Swc4jAstJsxElement.class));
        assertTrue(ISwc4jAstJsxElementChild.class.isAssignableFrom(Swc4jAstJsxExprContainer.class));
        assertTrue(ISwc4jAstJsxElementChild.class.isAssignableFrom(Swc4jAstJsxFragment.class));
        assertTrue(ISwc4jAstJsxElementChild.class.isAssignableFrom(Swc4jAstJsxText.class));
        assertTrue(ISwc4jAstJsxElementChild.class.isAssignableFrom(Swc4jAstJsxSpreadChild.class));
    }
}
