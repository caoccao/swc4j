/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSwc4jAstVarDecl extends BaseTestSuiteSwc4jAst {
    @Test
    public void test() throws Swc4jCoreException {
        String code = "const a, b, c;";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstVarDecl varDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstVarDecl.class), Swc4jAstType.VarDecl, 0, 14);
        assertEquals(3, varDecl.getDecls().size());
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(0).as(Swc4jAstVarDeclarator.class), Swc4jAstType.VarDeclarator, 6, 7);
        Swc4jAstBindingIdent bindingIdent = assertAst(
                varDeclarator, varDeclarator.getName().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 6, 7);
        assertEquals("a", bindingIdent.getId().getSym());
        varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(1).as(Swc4jAstVarDeclarator.class), Swc4jAstType.VarDeclarator, 9, 10);
        bindingIdent = assertAst(
                varDeclarator, varDeclarator.getName().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 9, 10);
        assertEquals("b", bindingIdent.getId().getSym());
        varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(2).as(Swc4jAstVarDeclarator.class), Swc4jAstType.VarDeclarator, 12, 13);
        bindingIdent = assertAst(
                varDeclarator, varDeclarator.getName().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 12, 13);
        assertEquals("c", bindingIdent.getId().getSym());
        assertSpan(code, script);
    }
}
