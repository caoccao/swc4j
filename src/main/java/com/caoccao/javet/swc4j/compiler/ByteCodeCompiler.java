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

package com.caoccao.javet.swc4j.compiler;

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.compiler.jdk17.AstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeAliasCollector;
import com.caoccao.javet.swc4j.compiler.jdk17.TypeResolver;
import com.caoccao.javet.swc4j.compiler.jdk17.VariableAnalyzer;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.ClassGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.MethodGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit.*;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.Map;

public sealed abstract class ByteCodeCompiler permits
        ByteCodeCompiler17 {
    protected final ArrayLiteralGenerator arrayLiteralGenerator;
    protected final AssignExpressionGenerator assignExpressionGenerator;
    protected final AstProcessor astProcessor;
    protected final BinaryExpressionGenerator binaryExpressionGenerator;
    protected final BoolLiteralGenerator boolLiteralGenerator;
    protected final CallExpressionGenerator callExpressionGenerator;
    protected final ClassGenerator classGenerator;
    protected final ConditionalExpressionGenerator conditionalExpressionGenerator;
    protected final ExpressionGenerator expressionGenerator;
    protected final IdentifierGenerator identifierGenerator;
    protected final MemberExpressionGenerator memberExpressionGenerator;
    protected final ByteCodeCompilerMemory memory;
    protected final MethodGenerator methodGenerator;
    protected final NullLiteralGenerator nullLiteralGenerator;
    protected final NumberLiteralGenerator numberLiteralGenerator;
    protected final ObjectLiteralGenerator objectLiteralGenerator;
    protected final ByteCodeCompilerOptions options;
    protected final ParenExpressionGenerator parenExpressionGenerator;
    protected final Swc4jParseOptions parseOptions;
    protected final SeqExpressionGenerator seqExpressionGenerator;
    protected final StringLiteralGenerator stringLiteralGenerator;
    protected final Swc4j swc4j;
    protected final TypeAliasCollector typeAliasCollector;
    protected final TypeResolver typeResolver;
    protected final UnaryExpressionGenerator unaryExpressionGenerator;
    protected final UpdateExpressionGenerator updateExpressionGenerator;
    protected final VariableAnalyzer variableAnalyzer;


    ByteCodeCompiler(ByteCodeCompilerOptions options) {
        this.options = AssertionUtils.notNull(options, "options");
        memory = new ByteCodeCompilerMemory();
        memory.getTypeAliasMap().putAll(options.typeAliasMap());
        parseOptions = new Swc4jParseOptions()
                .setCaptureAst(true);
        swc4j = new Swc4j();

        arrayLiteralGenerator = new ArrayLiteralGenerator(this);
        assignExpressionGenerator = new AssignExpressionGenerator(this);
        astProcessor = new AstProcessor(this);
        binaryExpressionGenerator = new BinaryExpressionGenerator(this);
        boolLiteralGenerator = new BoolLiteralGenerator(this);
        callExpressionGenerator = new CallExpressionGenerator(this);
        classGenerator = new ClassGenerator(this);
        conditionalExpressionGenerator = new ConditionalExpressionGenerator(this);
        expressionGenerator = new ExpressionGenerator(this);
        identifierGenerator = new IdentifierGenerator(this);
        memberExpressionGenerator = new MemberExpressionGenerator(this);
        methodGenerator = new MethodGenerator(this);
        nullLiteralGenerator = new NullLiteralGenerator(this);
        numberLiteralGenerator = new NumberLiteralGenerator(this);
        objectLiteralGenerator = new ObjectLiteralGenerator(this);
        parenExpressionGenerator = new ParenExpressionGenerator(this);
        seqExpressionGenerator = new SeqExpressionGenerator(this);
        stringLiteralGenerator = new StringLiteralGenerator(this);
        typeAliasCollector = new TypeAliasCollector(this);
        typeResolver = new TypeResolver(this);
        unaryExpressionGenerator = new UnaryExpressionGenerator(this);
        updateExpressionGenerator = new UpdateExpressionGenerator(this);
        variableAnalyzer = new VariableAnalyzer(this);
    }

    public static ByteCodeCompiler of(ByteCodeCompilerOptions options) {
        AssertionUtils.notNull(options, "options");
        return switch (options.jdkVersion()) {
            case JDK_17 -> new ByteCodeCompiler17(options);
        };
    }

    public Map<String, byte[]> compile(String code) throws Swc4jCoreException, Swc4jByteCodeCompilerException {
        Swc4jParseOutput output = swc4j.parse(code, parseOptions);
        return compileProgram(output.getProgram());
    }

    abstract Map<String, byte[]> compileProgram(ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException;

    public ArrayLiteralGenerator getArrayLiteralGenerator() {
        return arrayLiteralGenerator;
    }

    public AssignExpressionGenerator getAssignExpressionGenerator() {
        return assignExpressionGenerator;
    }

    public AstProcessor getAstProcessor() {
        return astProcessor;
    }

    public BinaryExpressionGenerator getBinaryExpressionGenerator() {
        return binaryExpressionGenerator;
    }

    public BoolLiteralGenerator getBoolLiteralGenerator() {
        return boolLiteralGenerator;
    }

    public CallExpressionGenerator getCallExpressionGenerator() {
        return callExpressionGenerator;
    }

    public ClassGenerator getClassGenerator() {
        return classGenerator;
    }

    public ConditionalExpressionGenerator getConditionalExpressionGenerator() {
        return conditionalExpressionGenerator;
    }

    public ExpressionGenerator getExpressionGenerator() {
        return expressionGenerator;
    }

    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
    }

    public MemberExpressionGenerator getMemberExpressionGenerator() {
        return memberExpressionGenerator;
    }

    public ByteCodeCompilerMemory getMemory() {
        return memory;
    }

    public MethodGenerator getMethodGenerator() {
        return methodGenerator;
    }

    public NullLiteralGenerator getNullLiteralGenerator() {
        return nullLiteralGenerator;
    }

    public NumberLiteralGenerator getNumberLiteralGenerator() {
        return numberLiteralGenerator;
    }

    public ObjectLiteralGenerator getObjectLiteralGenerator() {
        return objectLiteralGenerator;
    }

    public ByteCodeCompilerOptions getOptions() {
        return options;
    }

    public ParenExpressionGenerator getParenExpressionGenerator() {
        return parenExpressionGenerator;
    }

    public SeqExpressionGenerator getSeqExpressionGenerator() {
        return seqExpressionGenerator;
    }

    public StringLiteralGenerator getStringLiteralGenerator() {
        return stringLiteralGenerator;
    }

    public TypeAliasCollector getTypeAliasCollector() {
        return typeAliasCollector;
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public UnaryExpressionGenerator getUnaryExpressionGenerator() {
        return unaryExpressionGenerator;
    }

    public UpdateExpressionGenerator getUpdateExpressionGenerator() {
        return updateExpressionGenerator;
    }

    public VariableAnalyzer getVariableAnalyzer() {
        return variableAnalyzer;
    }
}
