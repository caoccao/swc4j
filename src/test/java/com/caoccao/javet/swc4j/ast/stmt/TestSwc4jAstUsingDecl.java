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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestSwc4jAstUsingDecl extends BaseTestSuiteSwc4jAst {
    @Test
    public void testUsingInTypeScript() throws Swc4jCoreException {
        String code = "using a";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstUsingDecl usingDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstUsingDecl.class), Swc4jAstType.UsingDecl, 0, 7);
        assertFalse(usingDecl.isAwait());
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                usingDecl, usingDecl.getDecls().get(0), Swc4jAstType.VarDeclarator, 6, 7);
        Swc4jAstBindingIdent bindingIdent = assertAst(
                varDeclarator, varDeclarator.getName().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 6, 7);
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 6, 7);
        assertEquals("a", ident.getSym());
        assertSpan(code, script);
    }
}
