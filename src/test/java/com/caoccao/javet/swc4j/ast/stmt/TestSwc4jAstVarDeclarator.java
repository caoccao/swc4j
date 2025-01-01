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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstVarDeclarator extends BaseTestSuiteSwc4jAst {
    @Test
    public void testVisitor() {
        assertVisitor(tsScriptParseOptions, SimpleList.of(
                new VisitorCase("const a = 1", SimpleMap.of(
                        Swc4jAstType.Script, 1,
                        Swc4jAstType.VarDecl, 1,
                        Swc4jAstType.VarDeclarator, 1,
                        Swc4jAstType.Number, 1,
                        Swc4jAstType.BindingIdent, 1)),
                new VisitorCase("let a", SimpleMap.of(
                        Swc4jAstType.Script, 1,
                        Swc4jAstType.VarDecl, 1,
                        Swc4jAstType.VarDeclarator, 1,
                        Swc4jAstType.Number, 0,
                        Swc4jAstType.BindingIdent, 1))));
    }

    @Test
    public void testWithInit() throws Swc4jCoreException {
        String code = "let a變量=1";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Script, output.getParseMode());
        assertNotNull(output.getProgram());
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        assertEquals(0, script.getSpan().getStart());
        assertEquals(code.length(), script.getSpan().getEnd());
        assertNotNull(script.getBody());
        Swc4jAstVarDecl varDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstVarDecl.class), Swc4jAstType.VarDecl, 0, 9);
        assertEquals(Swc4jAstVarDeclKind.Let, varDecl.getKind());
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(0), Swc4jAstType.VarDeclarator, 4, 9);
        Swc4jAstBindingIdent name = assertAst(
                varDeclarator, varDeclarator.getName().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 4, 7);
        assertEquals("a變量", name.getId().getSym());
        assertTrue(varDeclarator.getInit().isPresent());
        Swc4jAstNumber number = assertAst(
                varDeclarator, varDeclarator.getInit().get().as(Swc4jAstNumber.class), Swc4jAstType.Number, 8, 9);
        assertEquals(1, number.getValue());
        assertSpan(code, script);
    }

    @Test
    public void testWithoutInit() throws Swc4jCoreException {
        String code = "let a變量";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Script, output.getParseMode());
        assertNotNull(output.getProgram());
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        assertEquals(0, script.getSpan().getStart());
        assertEquals(code.length(), script.getSpan().getEnd());
        assertNotNull(script.getBody());
        Swc4jAstVarDecl varDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstVarDecl.class), Swc4jAstType.VarDecl, 0, 7);
        assertEquals(Swc4jAstVarDeclKind.Let, varDecl.getKind());
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(0), Swc4jAstType.VarDeclarator, 4, 7);
        assertFalse(varDeclarator.isDefinite());
        Swc4jAstBindingIdent name = assertAst(
                varDeclarator, varDeclarator.getName().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 4, 7);
        assertEquals("a變量", name.getId().getSym());
        assertFalse(varDeclarator.getInit().isPresent());
        assertSpan(code, script);
    }
}
