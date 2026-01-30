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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstFnDecl extends BaseTestSuiteSwc4jAst {
    @Test
    public void testEmptyFunction() throws Swc4jCoreException {
        String code = "function a() {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstFnDecl fnDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstFnDecl.class), Swc4jAstType.FnDecl, 0, 15);
        assertThat(fnDecl.isDeclare()).isFalse();
        Swc4jAstFunction function = assertAst(
                fnDecl, fnDecl.getFunction(), Swc4jAstType.Function, 0, 15);
        assertThat(function.isAsync()).isFalse();
        assertThat(function.isGenerator()).isFalse();
        assertThat(function.getReturnType().isPresent()).isFalse();
        assertThat(function.getTypeParams().isPresent()).isFalse();
        Swc4jAstIdent ident = assertAst(
                fnDecl, fnDecl.getIdent(), Swc4jAstType.Ident, 9, 10);
        assertThat(ident.getSym()).isEqualTo("a");
        assertSpan(code, script);
    }
}
