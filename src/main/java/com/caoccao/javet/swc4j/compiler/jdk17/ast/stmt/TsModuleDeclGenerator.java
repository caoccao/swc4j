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

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsModuleName;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstTsModuleBlock;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsModuleDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class TsModuleDeclGenerator extends BaseAstProcessor<Swc4jAstTsModuleDecl> {
    public TsModuleDeclGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter.ConstantPool cp, Swc4jAstTsModuleDecl tsModuleDecl, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        ISwc4jAstTsModuleName moduleName = tsModuleDecl.getId();
        String namespaceName = getModuleName(moduleName);

        // Enter the new package scope
        compiler.getMemory().getScopedPackage().enterScope(namespaceName);

        try {
            var bodyOpt = tsModuleDecl.getBody();
            if (bodyOpt.isPresent() && bodyOpt.get() instanceof Swc4jAstTsModuleBlock moduleBlock) {
                for (var moduleItem : moduleBlock.getBody()) {
                    compiler.getModuleItemGenerator().generate(code, cp, moduleItem, returnTypeInfo);
                }
            }
        } finally {
            // Exit the package scope
            compiler.getMemory().getScopedPackage().exitScope();
        }
    }

    private String getModuleName(ISwc4jAstTsModuleName moduleName) {
        if (moduleName instanceof Swc4jAstStr str) {
            return str.getValue();
        }
        return moduleName.toString();
    }
}
