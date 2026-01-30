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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstFnExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstFunction extends BaseTestSuiteSwc4jAst {
    @Test
    public void testAnonymousEmptyFunction() throws Swc4jCoreException {
        String code = "const a = function() {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstVarDecl varDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstVarDecl.class), Swc4jAstType.VarDecl, 0, 23);
        Swc4jAstVarDeclarator varDeclarator = assertAst(
                varDecl, varDecl.getDecls().get(0), Swc4jAstType.VarDeclarator, 6, 23);
        assertThat(varDeclarator.getInit().isPresent()).isTrue();
        Swc4jAstFnExpr fnExpr = assertAst(
                varDeclarator, varDeclarator.getInit().get().as(Swc4jAstFnExpr.class), Swc4jAstType.FnExpr, 10, 23);
        assertThat(fnExpr.getIdent().isPresent()).isFalse();
        Swc4jAstFunction function = assertAst(
                fnExpr, fnExpr.getFunction(), Swc4jAstType.Function, 10, 23);
        assertThat(function.isAsync()).isFalse();
        assertThat(function.isGenerator()).isFalse();
        assertThat(function.getReturnType().isPresent()).isFalse();
        assertThat(function.getTypeParams().isPresent()).isFalse();
        assertSpan(code, script);
    }

    @Test
    public void testNamedEmptyFunction() throws Swc4jCoreException {
        String code = "function a() {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstFnDecl fnDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstFnDecl.class), Swc4jAstType.FnDecl, 0, 15);
        assertThat(fnDecl.isDeclare()).isFalse();
        Swc4jAstIdent ident = assertAst(
                fnDecl, fnDecl.getIdent().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 9, 10);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstFunction function = assertAst(
                fnDecl, fnDecl.getFunction(), Swc4jAstType.Function, 0, 15);
        assertThat(function.isAsync()).isFalse();
        assertThat(function.isGenerator()).isFalse();
        assertThat(function.getReturnType().isPresent()).isFalse();
        assertThat(function.getTypeParams().isPresent()).isFalse();
        assertThat(function.getBody().isPresent()).isTrue();
        Swc4jAstBlockStmt blockStmt = assertAst(
                function, function.getBody().get(), Swc4jAstType.BlockStmt, 13, 15);
        assertThat(blockStmt.getStmts()).isEmpty();
        assertSpan(code, script);
    }

    @Test
    public void testNestedFunction() throws Swc4jCoreException {
        String code = "function a(x, y) { function b(z) {} }";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstFnDecl fnDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstFnDecl.class), Swc4jAstType.FnDecl, 0, 37);
        assertThat(fnDecl.isDeclare()).isFalse();
        Swc4jAstIdent ident = assertAst(
                fnDecl, fnDecl.getIdent().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 9, 10);
        assertThat(ident.getSym()).isEqualTo("a");
        Swc4jAstFunction function = assertAst(
                fnDecl, fnDecl.getFunction(), Swc4jAstType.Function, 0, 37);
        assertThat(function.isAsync()).isFalse();
        assertThat(function.isGenerator()).isFalse();
        assertThat(function.getReturnType().isPresent()).isFalse();
        assertThat(function.getTypeParams().isPresent()).isFalse();
        assertThat(function.getBody().isPresent()).isTrue();
        assertThat(function.getParams().size()).isEqualTo(2);
        Swc4jAstParam param = assertAst(
                function, function.getParams().get(0).as(Swc4jAstParam.class), Swc4jAstType.Param, 11, 12);
        assertThat(param.getDecorators()).isEmpty();
        Swc4jAstBindingIdent bindingIdent = assertAst(
                param, param.getPat().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 11, 12);
        ident = assertAst(
                bindingIdent, bindingIdent.getId().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 11, 12);
        assertThat(ident.getSym()).isEqualTo("x");
        param = assertAst(
                function, function.getParams().get(1).as(Swc4jAstParam.class), Swc4jAstType.Param, 14, 15);
        assertThat(param.getDecorators()).isEmpty();
        bindingIdent = assertAst(
                param, param.getPat().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 14, 15);
        ident = assertAst(
                bindingIdent, bindingIdent.getId().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 14, 15);
        assertThat(ident.getSym()).isEqualTo("y");
        Swc4jAstBlockStmt blockStmt = assertAst(
                function, function.getBody().get(), Swc4jAstType.BlockStmt, 17, 37);
        fnDecl = assertAst(
                blockStmt, blockStmt.getStmts().get(0).as(Swc4jAstFnDecl.class), Swc4jAstType.FnDecl, 19, 35);
        assertThat(fnDecl.isDeclare()).isFalse();
        ident = assertAst(
                fnDecl, fnDecl.getIdent().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 28, 29);
        assertThat(ident.getSym()).isEqualTo("b");
        function = assertAst(
                fnDecl, fnDecl.getFunction(), Swc4jAstType.Function, 19, 35);
        assertThat(function.isAsync()).isFalse();
        assertThat(function.isGenerator()).isFalse();
        assertThat(function.getReturnType().isPresent()).isFalse();
        assertThat(function.getTypeParams().isPresent()).isFalse();
        assertThat(function.getBody().isPresent()).isTrue();
        assertThat(function.getParams().size()).isEqualTo(1);
        param = assertAst(
                function, function.getParams().get(0).as(Swc4jAstParam.class), Swc4jAstType.Param, 30, 31);
        assertThat(param.getDecorators()).isEmpty();
        bindingIdent = assertAst(
                param, param.getPat().as(Swc4jAstBindingIdent.class), Swc4jAstType.BindingIdent, 30, 31);
        ident = assertAst(
                bindingIdent, bindingIdent.getId().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 30, 31);
        assertThat(ident.getSym()).isEqualTo("z");
        assertSpan(code, script);
    }
}
