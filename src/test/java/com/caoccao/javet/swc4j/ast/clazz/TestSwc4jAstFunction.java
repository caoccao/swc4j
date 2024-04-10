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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstFnExpr;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jAstFunction extends BaseTestSuiteSwc4jAst {
    @Test
    public void testAnonymousFunction() throws Swc4jCoreException {
        String code = "const a = function() {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) assertAst(
                script, script.getBody().get(0), Swc4jAstType.VarDecl, 0, 23);
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(0), Swc4jAstType.VarDeclarator, 6, 23);
        assertTrue(varDeclarator.getInit().isPresent());
        Swc4jAstFnExpr fnExpr = (Swc4jAstFnExpr) assertAst(
                varDeclarator, varDeclarator.getInit().get(), Swc4jAstType.FnExpr, 10, 23);
        assertFalse(fnExpr.getIdent().isPresent());
        Swc4jAstFunction function = assertAst(
                fnExpr, fnExpr.getFunction(), Swc4jAstType.Function, 10, 23);
        assertFalse(function.isAsync());
        assertFalse(function.isGenerator());
        assertFalse(function.getReturnType().isPresent());
        assertFalse(function.getTypeParams().isPresent());
        assertSpan(code, script);
    }
}
