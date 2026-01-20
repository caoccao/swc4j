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

import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.CompilationContext;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

public final class StringLiteralGenerator {
    private StringLiteralGenerator() {
    }

    public static void generate(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstStr str,
            ReturnTypeInfo returnTypeInfo,
            CompilationContext context) throws Swc4jByteCodeCompilerException {
        String value = str.getValue();
        // Check if we need to convert to char based on return type
        if (returnTypeInfo != null && (returnTypeInfo.type() == ReturnType.CHAR
                || (returnTypeInfo.type() == ReturnType.OBJECT && "Ljava/lang/Character;".equals(returnTypeInfo.descriptor())))) {
            // Convert string to char - use first character
            if (value.length() > 0) {
                char charValue = value.charAt(0);
                code.iconst(charValue);
                // Box to Character if needed
                if (returnTypeInfo.type() == ReturnType.OBJECT && "Ljava/lang/Character;".equals(returnTypeInfo.descriptor())) {
                    int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                    code.invokestatic(valueOfRef);
                }
            } else {
                // Empty string, use null character
                code.iconst(0);
                if (returnTypeInfo.type() == ReturnType.OBJECT && "Ljava/lang/Character;".equals(returnTypeInfo.descriptor())) {
                    int valueOfRef = cp.addMethodRef("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
                    code.invokestatic(valueOfRef);
                }
            }
        } else {
            // Regular string
            int stringIndex = cp.addString(value);
            code.ldc(stringIndex);
        }
    }
}
