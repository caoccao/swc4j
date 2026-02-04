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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt;

import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsEnumDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;

public final class TsEnumDeclGenerator extends BaseAstProcessor<Swc4jAstTsEnumDecl> {
    public TsEnumDeclGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstTsEnumDecl tsEnumDecl, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        String currentPackage = compiler.getMemory().getScopedPackage().getCurrentPackage();
        String enumName = tsEnumDecl.getId().getSym();
        String fullClassName = currentPackage.isEmpty() ? enumName : currentPackage + "." + enumName;
        String internalClassName = fullClassName.replace('.', '/');

        try {
            byte[] bytecode = compiler.getEnumGenerator().generateBytecode(internalClassName, tsEnumDecl);
            if (bytecode != null) {  // null means ambient declaration
                compiler.getMemory().getByteCodeMap().put(fullClassName, bytecode);
            }
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), tsEnumDecl, "Failed to generate bytecode for enum: " + fullClassName, e);
        }
    }
}
