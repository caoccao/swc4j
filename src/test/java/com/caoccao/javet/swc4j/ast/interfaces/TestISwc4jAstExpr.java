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

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstPrivateName;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstObjectLit;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstInvalid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4jAstExpr {
    @Test
    public void testAssignable() {
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(ISwc4jAstLit.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstArrayLit.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstArrowExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstAssignExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstAwaitExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstCallExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstClassExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstCondExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstFnExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstIdent.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstInvalid.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstMemberExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstMetaPropExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstNewExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstObjectLit.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstPrivateName.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstSuperPropExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstThisExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstTaggedTpl.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstTpl.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstUnaryExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstUpdateExpr.class));
        assertTrue(ISwc4jAstExpr.class.isAssignableFrom(Swc4jAstYieldExpr.class));
    }
}
