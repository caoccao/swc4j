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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstJsxElement;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxClosingElement;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstJsxOpeningElement;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstExprStmt;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestSwc4jAstJsxText extends BaseTestSuiteSwc4jAst {
    @Test
    public void testWithNewLines() throws Swc4jCoreException {
        String code = "<h1>    \n    test    \n    </h1>";
        Swc4jParseOutput output = swc4j.parse(code, jsxScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 31);
        Swc4jAstJsxElement jsxElement = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstJsxElement.class), Swc4jAstType.JsxElement, 0, 31);
        assertThat(jsxElement.getClosing().isPresent()).isTrue();
        Swc4jAstJsxClosingElement jsxClosingElement = assertAst(
                jsxElement, jsxElement.getClosing().get().as(Swc4jAstJsxClosingElement.class), Swc4jAstType.JsxClosingElement, 26, 31, 3, 5);
        Swc4jAstIdent ident = assertAst(
                jsxClosingElement, jsxClosingElement.getName().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 28, 30, 3, 7);
        assertThat(ident.getSym()).isEqualTo("h1");
        Swc4jAstJsxOpeningElement jsxOpeningElement = assertAst(
                jsxElement, jsxElement.getOpening().as(Swc4jAstJsxOpeningElement.class), Swc4jAstType.JsxOpeningElement, 0, 4);
        assertThat(jsxOpeningElement.isSelfClosing()).isFalse();
        assertThat(jsxOpeningElement.getTypeArgs().isPresent()).isFalse();
        ident = assertAst(
                jsxOpeningElement, jsxOpeningElement.getName().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 1, 3);
        assertThat(ident.getSym()).isEqualTo("h1");
        Swc4jAstJsxText jsxText = assertAst(
                jsxElement, jsxElement.getChildren().get(0).as(Swc4jAstJsxText.class), Swc4jAstType.JsxText, 4, 26);
        assertThat(jsxText.getValue()).isEqualTo("    \n    test    \n    ");
        assertThat(jsxText.getRaw()).isEqualTo("    \n    test    \n    ");
        assertSpan(code, script);
    }

    @Test
    public void testWithoutNewLines() throws Swc4jCoreException {
        String code = "<h1>test</h1>";
        Swc4jParseOutput output = swc4j.parse(code, jsxScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstExprStmt exprStmt = assertAst(
                script, script.getBody().get(0).as(Swc4jAstExprStmt.class), Swc4jAstType.ExprStmt, 0, 13);
        Swc4jAstJsxElement jsxElement = assertAst(
                exprStmt, exprStmt.getExpr().as(Swc4jAstJsxElement.class), Swc4jAstType.JsxElement, 0, 13);
        assertThat(jsxElement.getClosing().isPresent()).isTrue();
        Swc4jAstJsxClosingElement jsxClosingElement = assertAst(
                jsxElement, jsxElement.getClosing().get().as(Swc4jAstJsxClosingElement.class), Swc4jAstType.JsxClosingElement, 8, 13);
        Swc4jAstIdent ident = assertAst(
                jsxClosingElement, jsxClosingElement.getName().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 10, 12);
        assertThat(ident.getSym()).isEqualTo("h1");
        Swc4jAstJsxOpeningElement jsxOpeningElement = assertAst(
                jsxElement, jsxElement.getOpening().as(Swc4jAstJsxOpeningElement.class), Swc4jAstType.JsxOpeningElement, 0, 4);
        assertThat(jsxOpeningElement.isSelfClosing()).isFalse();
        assertThat(jsxOpeningElement.getTypeArgs().isPresent()).isFalse();
        ident = assertAst(
                jsxOpeningElement, jsxOpeningElement.getName().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 1, 3);
        assertThat(ident.getSym()).isEqualTo("h1");
        Swc4jAstJsxText jsxText = assertAst(
                jsxElement, jsxElement.getChildren().get(0).as(Swc4jAstJsxText.class), Swc4jAstType.JsxText, 4, 8);
        assertThat(jsxText.getValue()).isEqualTo("test");
        assertThat(jsxText.getRaw()).isEqualTo("test");
        assertSpan(code, script);
    }
}
