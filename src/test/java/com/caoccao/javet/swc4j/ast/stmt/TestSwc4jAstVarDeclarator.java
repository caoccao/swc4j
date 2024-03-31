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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstVarDeclarator extends BaseTestSuiteSwc4jAst {
    @Test
    public void testLet() throws Swc4jCoreException {
        String code = "let a變量";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        assertNotNull(output);
        assertFalse(output.isModule());
        assertTrue(output.isScript());
        assertNotNull(output.getProgram());
        Swc4jAstScript script = output.getProgram().asScript();
        assertEquals(0, script.getStartPosition());
        assertEquals(code.length(), script.getEndPosition());
        assertNotNull(script.getBody());
        Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) assertAst(
                script, script.getBody().get(0), Swc4jAstType.VarDecl, 0, 7);
        assertEquals(Swc4jAstVarDeclKind.Let, varDecl.getKind());
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(0), Swc4jAstType.VarDeclarator, 4, 7);
        Swc4jAstBindingIdent name = (Swc4jAstBindingIdent) assertAst(
                varDeclarator, varDeclarator.getName(), Swc4jAstType.BindingIdent, 4, 7);
        assertEquals("a變量", name.getId().getSym());
    }
}
