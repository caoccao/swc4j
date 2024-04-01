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

import com.caoccao.javet.swc4j.ast.module.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4jAstModuleDecl {
    @Test
    public void testAssignable() {
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstExportAll.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstExportDecl.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstExportDefaultDecl.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstExportDefaultExpr.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstImportDecl.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstNamedExport.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstTsExportAssignment.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstTsImportEqualsDecl.class));
        assertTrue(ISwc4jAstModuleDecl.class.isAssignableFrom(Swc4jAstTsNamespaceExportDecl.class));
    }
}
