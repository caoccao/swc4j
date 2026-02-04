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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class BoolLiteralGenerator extends BaseAstProcessor<Swc4jAstBool> {
    public BoolLiteralGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstBool bool,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        var cp = classWriter.getConstantPool();
        boolean value = bool.isValue();
        // Check if we need to box to Boolean
        if (returnTypeInfo != null && returnTypeInfo.type() == ReturnType.OBJECT
                && "Ljava/lang/Boolean;".equals(returnTypeInfo.descriptor())) {
            // Box boolean to Boolean
            code.iconst(value ? 1 : 0);
            int valueOfRef = cp.addMethodRef("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            code.invokestatic(valueOfRef);
        } else {
            // Primitive boolean
            code.iconst(value ? 1 : 0);
        }
    }
}
