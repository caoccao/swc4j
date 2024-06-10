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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstDecorator;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstCallExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAstClassDecl extends BaseTestSuiteSwc4jAst {
    @Test
    public void testDecoratorsExportClass() throws Swc4jCoreException {
        String code = "@x() export class A {}";
        Swc4jParseOutput output = swc4j.parse(code, tsModuleParseOptions);
        Swc4jAstModule module = output.getProgram().as(Swc4jAstModule.class);
        Swc4jAstExportDecl exportDecl = assertAst(
                module, module.getBody().get(0).as(Swc4jAstExportDecl.class), Swc4jAstType.ExportDecl, 5, 22);
        Swc4jAstClassDecl classDecl = assertAst(
                exportDecl, exportDecl.getDecl().as(Swc4jAstClassDecl.class), Swc4jAstType.ClassDecl, 12, 22);
        assertFalse(classDecl.isDeclare());
        Swc4jAstClass clazz = assertAst(
                classDecl, classDecl.getClazz(), Swc4jAstType.Class, 12, 22);
        assertTrue(clazz.getBody().isEmpty());
        assertFalse(clazz.isAbstract());
        assertFalse(clazz.getSuperClass().isPresent());
        assertFalse(clazz.getSuperTypeParams().isPresent());
        assertFalse(clazz.getTypeParams().isPresent());
        assertEquals(1, clazz.getDecorators().size());
        Swc4jAstDecorator decorator = assertAst(
                clazz, clazz.getDecorators().get(0), Swc4jAstType.Decorator, 0, 4);
        Swc4jAstCallExpr callExpr = assertAst(
                decorator, decorator.getExpr().as(Swc4jAstCallExpr.class), Swc4jAstType.CallExpr, 1, 4);
        assertTrue(callExpr.getArgs().isEmpty());
        assertFalse(callExpr.getTypeArgs().isPresent());
        Swc4jAstIdent ident = assertAst(
                callExpr, callExpr.getCallee().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 1, 2);
        assertEquals("x", ident.getSym());
        ident = assertAst(
                classDecl, classDecl.getIdent(), Swc4jAstType.Ident, 18, 19);
        assertEquals("A", ident.getSym());
        assertSpan(code, module);
    }

    @Test
    public void testEmptyClass() throws Swc4jCoreException {
        String code = "class A {}";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        Swc4jAstScript script = output.getProgram().as(Swc4jAstScript.class);
        Swc4jAstClassDecl classDecl = assertAst(
                script, script.getBody().get(0).as(Swc4jAstClassDecl.class), Swc4jAstType.ClassDecl, 0, 10);
        Swc4jAstClass clazz = assertAst(
                classDecl, classDecl.getClazz(), Swc4jAstType.Class, 0, 10);
        assertTrue(clazz.getBody().isEmpty());
        assertTrue(clazz.getDecorators().isEmpty());
        assertTrue(clazz.getImplements().isEmpty());
        Swc4jAstIdent ident = assertAst(
                classDecl, classDecl.getIdent(), Swc4jAstType.Ident, 6, 7);
        assertEquals("A", ident.getSym());
        assertSpan(code, script);
    }

    @Test
    public void testExportDecoratorsClass() throws Swc4jCoreException {
        String code = "export @x() class A {}";
        Swc4jParseOutput output = swc4j.parse(code, tsModuleParseOptions);
        Swc4jAstModule module = output.getProgram().as(Swc4jAstModule.class);
        Swc4jAstExportDecl exportDecl = assertAst(
                module, module.getBody().get(0).as(Swc4jAstExportDecl.class), Swc4jAstType.ExportDecl, 0, 22);
        Swc4jAstClassDecl classDecl = assertAst(
                exportDecl, exportDecl.getDecl().as(Swc4jAstClassDecl.class), Swc4jAstType.ClassDecl, 12, 22);
        assertFalse(classDecl.isDeclare());
        Swc4jAstClass clazz = assertAst(
                classDecl, classDecl.getClazz(), Swc4jAstType.Class, 12, 22);
        assertTrue(clazz.getBody().isEmpty());
        assertFalse(clazz.isAbstract());
        assertFalse(clazz.getSuperClass().isPresent());
        assertFalse(clazz.getSuperTypeParams().isPresent());
        assertFalse(clazz.getTypeParams().isPresent());
        assertEquals(1, clazz.getDecorators().size());
        Swc4jAstDecorator decorator = assertAst(
                clazz, clazz.getDecorators().get(0), Swc4jAstType.Decorator, 7, 11);
        Swc4jAstCallExpr callExpr = assertAst(
                decorator, decorator.getExpr().as(Swc4jAstCallExpr.class), Swc4jAstType.CallExpr, 8, 11);
        assertTrue(callExpr.getArgs().isEmpty());
        assertFalse(callExpr.getTypeArgs().isPresent());
        Swc4jAstIdent ident = assertAst(
                callExpr, callExpr.getCallee().as(Swc4jAstIdent.class), Swc4jAstType.Ident, 8, 9);
        assertEquals("x", ident.getSym());
        ident = assertAst(
                classDecl, classDecl.getIdent(), Swc4jAstType.Ident, 18, 19);
        assertEquals("A", ident.getSym());
        assertSpan(code, module);
    }
}
