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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstSuperPropExpr;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.memory.JavaTypeInfo;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Utility class for generating bytecode for super property access expressions.
 */
public final class SuperPropertyUtils {

    private SuperPropertyUtils() {
    }

    /**
     * Generate bytecode for super property reads (e.g. super.value).
     *
     * @param compiler       the bytecode compiler
     * @param code           the code builder
     * @param classWriter    the class writer
     * @param superPropExpr  the super property expression
     * @param returnTypeInfo the return type info
     * @throws Swc4jByteCodeCompilerException if generation fails
     */
    public static void generateSuperProperty(
            ByteCodeCompiler compiler,
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstSuperPropExpr superPropExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        String sourceCode = compiler.getMemory().getScopedSourceCode().getSourceCode();
        String fieldName = AstUtils.extractSuperPropertyName(sourceCode, superPropExpr);
        String currentClassName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        if (currentClassName == null) {
            throw new Swc4jByteCodeCompilerException(
                    sourceCode,
                    superPropExpr,
                    "super property access outside of class context");
        }
        String superClassInternalName = ClassHierarchyUtils.resolveSuperClassInternalName(compiler, currentClassName);
        if (superClassInternalName == null) {
            throw new Swc4jByteCodeCompilerException(
                    sourceCode,
                    superPropExpr,
                    "Cannot resolve superclass for " + currentClassName);
        }
        JavaTypeInfo superTypeInfo = FieldLookupUtils.resolveTypeInfoByInternalName(compiler, superClassInternalName);
        if (superTypeInfo == null) {
            throw new Swc4jByteCodeCompilerException(
                    sourceCode,
                    superPropExpr,
                    "Cannot resolve superclass type info for " + superClassInternalName);
        }
        FieldLookupUtils.FieldLookupResult lookupResult = FieldLookupUtils.lookupFieldInHierarchy(superTypeInfo, fieldName);
        if (lookupResult == null) {
            throw new Swc4jByteCodeCompilerException(
                    sourceCode,
                    superPropExpr,
                    "Field not found in super hierarchy: " + fieldName);
        }
        var cp = classWriter.getConstantPool();
        code.aload(0);
        int fieldRef = cp.addFieldRef(lookupResult.ownerInternalName(), fieldName, lookupResult.fieldInfo().descriptor());
        code.getfield(fieldRef);
    }
}
