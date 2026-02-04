/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.interfaces;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsEnumDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsInterfaceDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class StmtGenerator extends BaseAstProcessor<ISwc4jAstStmt> {
    public StmtGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter classWriter, ISwc4jAstStmt stmt, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (stmt instanceof Swc4jAstTsModuleDecl tsModuleDecl) {
            compiler.getTsModuleDeclGenerator().generate(code, classWriter, tsModuleDecl, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstClassDecl classDecl) {
            compiler.getClassDeclGenerator().generate(code, classWriter, classDecl, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstTsEnumDecl tsEnumDecl) {
            compiler.getTsEnumDeclGenerator().generate(code, classWriter, tsEnumDecl, returnTypeInfo);
        } else if (stmt instanceof Swc4jAstTsInterfaceDecl tsInterfaceDecl) {
            compiler.getTsInterfaceDeclGenerator().generate(code, classWriter, tsInterfaceDecl, returnTypeInfo);
        }
    }
}
