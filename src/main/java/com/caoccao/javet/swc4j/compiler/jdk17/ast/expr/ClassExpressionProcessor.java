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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.expr;

import com.caoccao.javet.swc4j.ast.expr.Swc4jAstClassExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstClassDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ClassCollector;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

/**
 * Generates bytecode for class expressions.
 */
public final class ClassExpressionProcessor extends BaseAstProcessor<Swc4jAstClassExpr> {
    private int classExprCounter = 0;

    /**
     * Constructs a new ClassExpressionProcessor.
     *
     * @param compiler the bytecode compiler instance
     */
    public ClassExpressionProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Ensure class generated class expr info.
     *
     * @param classExpr   the class expr
     * @param classWriter the class writer
     * @return the class expr info
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public ClassExprInfo ensureClassGenerated(Swc4jAstClassExpr classExpr, ClassWriter classWriter) throws Swc4jByteCodeCompilerException {
        ClassExprInfo info = prepareClassExpr(classExpr);
        var scopedContext = compiler.getMemory().getScopedCompilationContext();
        scopedContext.enterScope(true);
        try {
            compiler.getClassProcessor().generate(new CodeBuilder(), classWriter, classExpr.getClazz(), null);
        } finally {
            scopedContext.exitScope();
        }
        return info;
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter classWriter,
            Swc4jAstClassExpr classExpr,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        ClassExprInfo info = ensureClassGenerated(classExpr, classWriter);

        int classIndex = classWriter.getConstantPool().addClass(info.internalName());
        code.ldc(classIndex);
    }

    /**
     * Prepare class expr class expr info.
     *
     * @param classExpr the class expr
     * @return the class expr info
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    public ClassExprInfo prepareClassExpr(Swc4jAstClassExpr classExpr) throws Swc4jByteCodeCompilerException {
        String className = classExpr.getIdent().map(Swc4jAstIdent::getSym)
                .orElseGet(() -> "$ClassExpr" + (++classExprCounter));
        if (classExpr.getIdent().isEmpty()) {
            classExpr.setIdent(Swc4jAstIdent.create(className));
        }
        Swc4jAstClassDecl classDecl = Swc4jAstClassDecl.create(classExpr.getIdent().get(), classExpr.getClazz());

        String currentPackage = compiler.getMemory().getScopedPackage().getCurrentPackage();
        String qualifiedName = currentPackage.isEmpty() ? className : currentPackage + "." + className;
        String internalName = qualifiedName.replace('.', '/');

        new ClassCollector(compiler).registerClassExpr(classDecl, currentPackage);
        return new ClassExprInfo(className, qualifiedName, internalName);
    }

    /**
     * The type Class expr info.
     *
     * @param className     the simple class name
     * @param qualifiedName the fully qualified class name
     * @param internalName  the JVM internal name
     */
    public record ClassExprInfo(String className, String qualifiedName, String internalName) {
    }
}
