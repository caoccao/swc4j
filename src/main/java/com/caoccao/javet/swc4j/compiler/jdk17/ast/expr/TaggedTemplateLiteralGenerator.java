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

import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.miscs.Swc4jAstTplElement;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstFnDecl;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.utils.TypeConversionUtils;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Generator for tagged template literals.
 * <p>
 * Compiles tagged templates (e.g., {@code tag`Hello ${name}!`}) to method calls
 * where the tag function receives a String[] of template strings followed by
 * the interpolated expression values as individual arguments.
 * <p>
 * {@code this.tag`Hello ${name}!`} compiles to {@code this.tag($tpl$0, name)}
 * where {@code $tpl$0} is a cached static final String[] field initialized in {@code <clinit>}.
 * <p>
 * Template caching improves performance by creating the quasis String[] once at class
 * load time instead of on every method invocation. Identical quasis arrays within the
 * same class are deduplicated to share the same cached field.
 */
public final class TaggedTemplateLiteralGenerator extends BaseAstProcessor<Swc4jAstTaggedTpl> {
    public TaggedTemplateLiteralGenerator(ByteCodeCompiler compiler) {
        super(compiler);
    }

    /**
     * Check if a standalone function's first parameter is TemplateStringsArray.
     */
    private boolean checkStandaloneFunctionUsesTemplateStringsArray(Swc4jAstFnDecl fnDecl) {
        var params = fnDecl.getFunction().getParams();
        if (params.isEmpty()) {
            return false;
        }
        // Get the first parameter's type
        String firstParamType = compiler.getTypeResolver().extractParameterType(params.get(0).getPat());
        return "Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;".equals(firstParamType);
    }

    private Swc4jAstFnDecl findStandaloneFunction(String packageName, String functionName) {
        var functions = compiler.getMemory().getScopedStandaloneFunctionRegistry().getFunctions(packageName);
        for (Swc4jAstFnDecl fnDecl : functions) {
            if (fnDecl.getIdent().getSym().equals(functionName)) {
                return fnDecl;
            }
        }
        return null;
    }

    @Override
    public void generate(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTaggedTpl taggedTpl,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        ISwc4jAstExpr tag = taggedTpl.getTag();
        Swc4jAstTpl tpl = taggedTpl.getTpl();
        List<Swc4jAstTplElement> quasis = tpl.getQuasis();
        List<ISwc4jAstExpr> exprs = tpl.getExprs();

        if (tag instanceof Swc4jAstMemberExpr memberExpr) {
            generateMemberExprTagCall(code, cp, taggedTpl, memberExpr, quasis, exprs, returnTypeInfo);
        } else if (tag instanceof Swc4jAstIdent ident) {
            generateStandaloneFunctionTagCall(code, cp, taggedTpl, ident, quasis, exprs, returnTypeInfo);
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), taggedTpl,
                    "Unsupported tag expression type: " + tag.getClass().getSimpleName());
        }
    }

    private void generateMemberExprTagCall(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTaggedTpl taggedTpl,
            Swc4jAstMemberExpr memberExpr,
            List<Swc4jAstTplElement> quasis,
            List<ISwc4jAstExpr> exprs,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        // Determine the method name from the member expression property
        String methodName;
        if (memberExpr.getProp() instanceof Swc4jAstIdentName propIdentName) {
            methodName = propIdentName.getSym();
        } else if (memberExpr.getProp() instanceof Swc4jAstIdent propIdent) {
            methodName = propIdent.getSym();
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), taggedTpl,
                    "Unsupported tag property type: " + memberExpr.getProp().getClass().getSimpleName());
        }

        // Infer object type
        String objType = compiler.getTypeResolver().inferTypeFromExpr(memberExpr.getObj());
        if (objType == null || !objType.startsWith("L") || !objType.endsWith(";")) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), taggedTpl,
                    "Cannot infer object type for tagged template tag: " + objType);
        }

        String internalClassName = objType.substring(1, objType.length() - 1);
        String qualifiedClassName = internalClassName.replace('/', '.');

        // Build expression parameter descriptors (everything after the first String[] or TemplateStringsArray)
        StringBuilder exprParamDescriptors = new StringBuilder();
        for (ISwc4jAstExpr expr : exprs) {
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            if (exprType == null) {
                exprType = "Ljava/lang/Object;";
            }
            exprParamDescriptors.append(exprType);
        }

        // Check if the method accepts TemplateStringsArray (for raw string access)
        String templateStringsArrayDescriptor = "Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;";
        String tsaParamDescriptor = "(" + templateStringsArrayDescriptor + exprParamDescriptors + ")";
        String tsaReturnType = compiler.getMemory().getScopedJavaTypeRegistry()
                .resolveClassMethodReturnType(qualifiedClassName, methodName, tsaParamDescriptor);

        boolean useTemplateStringsArray = tsaReturnType != null;

        // Generate object reference
        compiler.getExpressionGenerator().generate(code, cp, memberExpr.getObj(), null);

        // Build method descriptor based on whether we use TemplateStringsArray or String[]
        String firstParamDescriptor;
        String methodReturnType;
        if (useTemplateStringsArray) {
            firstParamDescriptor = templateStringsArrayDescriptor;
            methodReturnType = tsaReturnType;
            loadCachedTemplateStringsArray(code, cp, quasis);
        } else {
            firstParamDescriptor = "[Ljava/lang/String;";
            String stringArrayParamDescriptor = "(" + firstParamDescriptor + exprParamDescriptors + ")";
            methodReturnType = compiler.getMemory().getScopedJavaTypeRegistry()
                    .resolveClassMethodReturnType(qualifiedClassName, methodName, stringArrayParamDescriptor);
            if (methodReturnType == null) {
                throw new Swc4jByteCodeCompilerException(getSourceCode(), taggedTpl,
                        "Cannot infer return type for tagged template tag function " +
                                qualifiedClassName + "." + methodName);
            }
            loadCachedStringArray(code, cp, quasis);
        }

        // Generate expression arguments
        for (ISwc4jAstExpr expr : exprs) {
            compiler.getExpressionGenerator().generate(code, cp, expr, null);
        }

        String paramDescriptor = "(" + firstParamDescriptor + exprParamDescriptors + ")";
        String methodDescriptor = paramDescriptor + methodReturnType;

        // Invoke the method
        int methodRef = cp.addMethodRef(internalClassName, methodName, methodDescriptor);
        code.invokevirtual(methodRef);

        // Handle return type conversion if needed
        if (returnTypeInfo != null && returnTypeInfo.descriptor() != null
                && !methodReturnType.equals(returnTypeInfo.descriptor())) {
            if (TypeConversionUtils.isPrimitiveType(methodReturnType)
                    && !TypeConversionUtils.isPrimitiveType(returnTypeInfo.descriptor())) {
                String wrapperType = TypeConversionUtils.getWrapperType(methodReturnType);
                TypeConversionUtils.boxPrimitiveType(code, cp, methodReturnType, wrapperType);
            } else if (!TypeConversionUtils.isPrimitiveType(methodReturnType)
                    && TypeConversionUtils.isPrimitiveType(returnTypeInfo.descriptor())) {
                TypeConversionUtils.unboxWrapperType(code, cp, "L" + methodReturnType + ";");
            }
        }
    }

    private void generateStandaloneFunctionTagCall(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            Swc4jAstTaggedTpl taggedTpl,
            Swc4jAstIdent ident,
            List<Swc4jAstTplElement> quasis,
            List<ISwc4jAstExpr> exprs,
            ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        String functionName = ident.getSym();

        // Get the current package name
        String currentClassInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();
        int lastSlash = currentClassInternalName.lastIndexOf('/');
        String packageName = lastSlash >= 0 ? currentClassInternalName.substring(0, lastSlash).replace('/', '.') : "";

        // Look up the standalone function in the registry
        Swc4jAstFnDecl fnDecl = findStandaloneFunction(packageName, functionName);
        if (fnDecl == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), taggedTpl,
                    "Standalone function not found: " + functionName);
        }

        // Get the dummy class name for this package
        String dummyClassName = compiler.getMemory().getScopedStandaloneFunctionRegistry().getDummyClassName(packageName);
        if (dummyClassName == null) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), taggedTpl,
                    "Dummy class not found for package: " + packageName);
        }

        // Build the internal class name
        String dummyClassInternalName = packageName.isEmpty() ? dummyClassName : packageName.replace('.', '/') + "/" + dummyClassName;

        // Check if the function accepts TemplateStringsArray
        boolean useTemplateStringsArray = checkStandaloneFunctionUsesTemplateStringsArray(fnDecl);

        // First argument: either TemplateStringsArray or String[]
        String firstParamDescriptor;
        if (useTemplateStringsArray) {
            firstParamDescriptor = "Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;";
            loadCachedTemplateStringsArray(code, cp, quasis);
        } else {
            firstParamDescriptor = "[Ljava/lang/String;";
            loadCachedStringArray(code, cp, quasis);
        }

        // Build method descriptor: first param is String[] or TemplateStringsArray, rest are expression types
        StringBuilder paramDescriptors = new StringBuilder();
        paramDescriptors.append(firstParamDescriptor);

        // Remaining arguments: interpolated expressions
        for (ISwc4jAstExpr expr : exprs) {
            String exprType = compiler.getTypeResolver().inferTypeFromExpr(expr);
            compiler.getExpressionGenerator().generate(code, cp, expr, null);
            if (exprType == null) {
                exprType = "Ljava/lang/Object;";
            }
            paramDescriptors.append(exprType);
        }

        // Resolve the return type from the function declaration
        String methodReturnType = resolveStandaloneFunctionReturnType(fnDecl, taggedTpl);

        String methodDescriptor = "(" + paramDescriptors + ")" + methodReturnType;

        // Invoke the static method
        int methodRef = cp.addMethodRef(dummyClassInternalName, functionName, methodDescriptor);
        code.invokestatic(methodRef);

        // Handle return type conversion if needed
        if (returnTypeInfo != null && returnTypeInfo.descriptor() != null
                && !methodReturnType.equals(returnTypeInfo.descriptor())) {
            if (TypeConversionUtils.isPrimitiveType(methodReturnType)
                    && !TypeConversionUtils.isPrimitiveType(returnTypeInfo.descriptor())) {
                String wrapperType = TypeConversionUtils.getWrapperType(methodReturnType);
                TypeConversionUtils.boxPrimitiveType(code, cp, methodReturnType, wrapperType);
            } else if (!TypeConversionUtils.isPrimitiveType(methodReturnType)
                    && TypeConversionUtils.isPrimitiveType(returnTypeInfo.descriptor())) {
                TypeConversionUtils.unboxWrapperType(code, cp, "L" + methodReturnType + ";");
            }
        }
    }

    /**
     * Load a cached String[] array onto the stack containing the template quasis (cooked values).
     * <p>
     * The array is cached as a static final field and loaded via GETSTATIC.
     * This avoids creating a new array on every invocation.
     */
    private void loadCachedStringArray(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            List<Swc4jAstTplElement> quasis) {
        // Register in cache and get field name
        String fieldName = registerQuasisInCache(quasis);

        // Get current class name
        String classInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();

        // Generate GETSTATIC to load the cached array
        int fieldRef = cp.addFieldRef(classInternalName, fieldName, "[Ljava/lang/String;");
        code.getstatic(fieldRef);
    }

    /**
     * Load a cached TemplateStringsArray onto the stack containing both cooked and raw strings.
     * <p>
     * The object is cached as a static final field and loaded via GETSTATIC.
     * This is used when the tag function accepts TemplateStringsArray as its first parameter.
     */
    private void loadCachedTemplateStringsArray(
            CodeBuilder code,
            ClassWriter.ConstantPool cp,
            List<Swc4jAstTplElement> quasis) {
        // Register in cache and get field name
        String fieldName = registerQuasisInCache(quasis);

        // Get current class name
        String classInternalName = compiler.getMemory().getCompilationContext().getCurrentClassInternalName();

        // Generate GETSTATIC to load the cached TemplateStringsArray
        int fieldRef = cp.addFieldRef(classInternalName, fieldName + "$raw",
                "Lcom/caoccao/javet/swc4j/compiler/jdk17/ast/utils/TemplateStringsArray;");
        code.getstatic(fieldRef);
    }

    /**
     * Extract cooked and raw strings from quasis and register in the cache.
     *
     * @param quasis the list of template quasis elements
     * @return the cache field name
     */
    private String registerQuasisInCache(List<Swc4jAstTplElement> quasis) {
        // Extract cooked and raw strings
        List<String> cookedStrings = new ArrayList<>(quasis.size());
        List<String> rawStrings = new ArrayList<>(quasis.size());
        for (Swc4jAstTplElement quasi : quasis) {
            cookedStrings.add(quasi.getCooked().orElse(quasi.getRaw()));
            rawStrings.add(quasi.getRaw());
        }

        // Get or create cached field in the current scope
        return compiler.getMemory().getScopedTemplateCacheRegistry()
                .getOrCreateCache(cookedStrings, rawStrings);
    }

    private String resolveStandaloneFunctionReturnType(Swc4jAstFnDecl fnDecl, Swc4jAstTaggedTpl taggedTpl) throws Swc4jByteCodeCompilerException {
        var function = fnDecl.getFunction();
        var returnTypeAnn = function.getReturnType();
        if (returnTypeAnn.isPresent()) {
            return compiler.getTypeResolver().mapTsTypeToDescriptor(returnTypeAnn.get().getTypeAnn());
        }
        // Default to String return type for tag functions
        return "Ljava/lang/String;";
    }
}
