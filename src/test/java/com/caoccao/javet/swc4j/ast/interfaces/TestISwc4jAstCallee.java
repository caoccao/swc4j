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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstSuper;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstJsxEmptyExpr;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstImport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4jAstCallee {
    @Test
    public void testAssignable() {
        assertTrue(ISwc4jAstCallee.class.isAssignableFrom(ISwc4jAstExpr.class));
        assertTrue(ISwc4jAstCallee.class.isAssignableFrom(Swc4jAstImport.class));
        assertTrue(ISwc4jAstCallee.class.isAssignableFrom(Swc4jAstSuper.class));
    }
}
