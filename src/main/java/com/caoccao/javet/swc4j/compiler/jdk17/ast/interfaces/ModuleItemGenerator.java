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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class ModuleItemGenerator extends BaseAstProcessor<ISwc4jAstModuleItem> {
    public ModuleItemGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter classWriter, ISwc4jAstModuleItem moduleItem, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        if (moduleItem instanceof Swc4jAstTsModuleDecl moduleDecl) {
            compiler.getTsModuleDeclGenerator().generate(code, classWriter, moduleDecl, returnTypeInfo);
        } else if (moduleItem instanceof Swc4jAstExportDecl exportDecl) {
            compiler.getExportDeclGenerator().generate(code, classWriter, exportDecl, returnTypeInfo);
        } else if (moduleItem instanceof ISwc4jAstStmt stmt) {
            compiler.getStmtGenerator().generate(code, classWriter, stmt, returnTypeInfo);
        }
    }
}
