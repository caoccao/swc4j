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

package com.caoccao.javet.swc4j.ast.program;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstCounterVisitor;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.OSUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jAstModule extends BaseTestSuiteSwc4jAst {
    @Test
    public void testChangeSwc4jVersion() throws Swc4jCoreException, IOException {
        File scriptFile = new File(OSUtils.WORKING_DIRECTORY, "scripts/ts/change_swc4j_version.ts");
        String code = new String(Files.readAllBytes(scriptFile.toPath()));
        Swc4jParseOutput output = swc4j.parse(code, tsModuleOptions);
        Swc4jAstModule module = output.getProgram().asModule();
        Swc4jAstCounterVisitor visitor = new Swc4jAstCounterVisitor();
        module.visit(visitor);
        int totalNodeCount = visitor.getCounterMap().values().stream().mapToInt(AtomicInteger::get).sum();
        assertTrue(totalNodeCount > 500);
        SimpleList.of(Swc4jAstType.ImportDecl,
                        Swc4jAstType.ImportStarAsSpecifier,
                        Swc4jAstType.ClassMethod,
                        Swc4jAstType.ExprStmt,
                        Swc4jAstType.ExprOrSpread,
                        Swc4jAstType.CallExpr,
                        Swc4jAstType.Str,
                        Swc4jAstType.Number,
                        Swc4jAstType.MemberExpr,
                        Swc4jAstType.ThisExpr,
                        Swc4jAstType.Regex,
                        Swc4jAstType.Ident,
                        Swc4jAstType.BindingIdent,
                        Swc4jAstType.ArrayLit,
                        Swc4jAstType.VarDecl,
                        Swc4jAstType.VarDeclarator)
                .forEach(type -> assertTrue(visitor.get(type) > 1));
        SimpleList.of(Swc4jAstType.ClassDecl,
                        Swc4jAstType.Constructor)
                .forEach(type -> assertEquals(1, visitor.get(type)));
        assertSpan(code, module);
    }
}
