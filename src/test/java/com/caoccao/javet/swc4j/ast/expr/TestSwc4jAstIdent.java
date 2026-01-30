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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstIdent extends BaseTestSuiteSwc4jAst {
    @Test
    public void testValidNonOptional() {
        SimpleList.of("a", "x0", "_a", "$abc").forEach(code -> {
            Swc4jParseOutput output;
            try {
                output = swc4j.parse(code, jsScriptParseOptions);
            } catch (Throwable t) {
                throw new AssertionError(t);
            }
            Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
            Swc4jAstExprStmt exprStmt = assertAst(
                    script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, code.length());
            Swc4jAstIdent ident = assertAst(
                    exprStmt, exprStmt.getExpr().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 0, code.length());
            assertThat(ident.getSym()).isEqualTo(code);
            assertThat(ident.isOptional()).isFalse();
            assertSpan(code, script);
        });
    }
}
