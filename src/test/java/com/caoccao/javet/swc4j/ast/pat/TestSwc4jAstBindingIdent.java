/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstParam;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVarDeclKind;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstBindingIdent extends BaseTestSuiteSwc4jAst {
    @Test
    public void testWithTsTypeAnn() throws Swc4jCoreException {
        String code = "const a: number";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstVarDecl varDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstVarDecl.class), Swc4jAstType.VarDecl, 0, 15);
        assertThat(varDecl.isDeclare()).isFalse();
        assertThat(varDecl.getKind()).isEqualTo(Swc4jAstVarDeclKind.Const);
        assertThat(varDecl.getDecls().size()).isEqualTo(1);
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(0), Swc4jAstType.VarDeclarator, 6, 15);
        Swc4jAstBindingIdent bindingIdent = assertAst(
                varDeclarator, varDeclarator.getName().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 6, 15);
        Swc4jAstIdent ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 6, 7);
        assertThat(ident.getSym()).isEqualTo("a");
        assertSpan(code, script);
    }

    @Test
    public void testWithTsTypeAnnAndDefaultValue() throws Exception {
        String code = "function b(a: number = 1) {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstFnDecl fnDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstFnDecl.class), Swc4jAstType.FnDecl, 0, 28);
        assertThat(fnDecl.isDeclare()).isFalse();
        Swc4jAstIdent ident = assertAst(
                fnDecl, fnDecl.getIdent(), Swc4jAstType.Ident, 9, 10);
        assertThat(ident.getSym()).isEqualTo("b");
        Swc4jAstFunction function = assertAst(
                fnDecl, fnDecl.getFunction(), Swc4jAstType.Function, 0, 28);
        assertThat(function.getParams().size()).isEqualTo(1);
        Swc4jAstParam param = assertAst(
                function, function.getParams().get(0), Swc4jAstType.Param, 11, 24);
        Swc4jAstAssignPat assignPat = assertAst(
                param, param.getPat().as(Swc4jAstAssignPat.class), Swc4jAstType.AssignPat, 11, 24);
        Swc4jAstBindingIdent bindingIdent = assertAst(
                assignPat, assignPat.getLeft().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 11, 20);
        ident = assertAst(
                bindingIdent, bindingIdent.getId(), Swc4jAstType.Ident, 11, 12);
        assertThat(ident.getSym()).isEqualTo("a");
        assertThat(bindingIdent.getTypeAnn().isPresent()).isTrue();
        assertAst(bindingIdent, bindingIdent.getTypeAnn().get(), Swc4jAstType.TsTypeAnn, 12, 20);
        assertSpan(code, script);
    }
}
