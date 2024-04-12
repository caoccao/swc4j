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

import com.caoccao.javet.swc4j.ast.pat.Swc4jAstArrayPat;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstInvalid;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstObjectPat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4JAstSimpleAssignTarget {
    @Test
    public void testAssignable() {
        assertTrue(ISwc4jAstSimpleAssignTarget.class.isAssignableFrom(Swc4jAstArrayPat.class));
        assertTrue(ISwc4jAstSimpleAssignTarget.class.isAssignableFrom(Swc4jAstInvalid.class));
        assertTrue(ISwc4jAstSimpleAssignTarget.class.isAssignableFrom(Swc4jAstObjectPat.class));
    }
}
