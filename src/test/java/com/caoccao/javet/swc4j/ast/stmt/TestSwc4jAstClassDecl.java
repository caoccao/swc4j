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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSwc4jAstClassDecl extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEmptyClass() throws Swc4jCoreException {
        String code = "class A {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptOptions);
        Swc4jAstScript script = output.getProgram().asScript();
        Swc4jAstClassDecl classDecl = (Swc4jAstClassDecl) assertAst(
                script, script.getBody().get(0), Swc4jAstType.ClassDecl, 0, 10);
        Swc4jAstClass clazz = assertAst(
                classDecl, classDecl.getClazz(), Swc4jAstType.Class, 0, 10);
        assertTrue(clazz.getBody().isEmpty());
        assertTrue(clazz.getDecorators().isEmpty());
        assertTrue(clazz.getImplements().isEmpty());
        Swc4jAstIdent ident = assertAst(
                classDecl, classDecl.getIdent(), Swc4jAstType.Ident, 6, 7);
        assertEquals("A", ident.getSym());
    }
}
