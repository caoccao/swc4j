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
import com.caoccao.javet.swc4j.compiler.jdk17.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.AstProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.ClassGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.MethodGenerator;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.module.ImportDeclProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.ts.TsAsExpressionGenerator;
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
    protected final BigIntLiteralGenerator bigIntLiteralGenerator;
    protected final BinaryExpressionGenerator binaryExpressionGenerator;
    protected final BoolLiteralGenerator boolLiteralGenerator;
    protected final BreakStatementGenerator breakStatementGenerator;
    protected final CallExpressionGenerator callExpressionGenerator;
    protected final ClassCollector classCollector;
    protected final ClassGenerator classGenerator;
    protected final ConditionalExpressionGenerator conditionalExpressionGenerator;
    protected final ContinueStatementGenerator continueStatementGenerator;
    protected final DoWhileStatementGenerator doWhileStatementGenerator;
    protected final EnumCollector enumCollector;
    protected final EnumGenerator enumGenerator;
    protected final ExpressionGenerator expressionGenerator;
    protected final ForInStatementGenerator forInStatementGenerator;
    protected final ForStatementGenerator forStatementGenerator;
    protected final IdentifierGenerator identifierGenerator;
    protected final IfStatementGenerator ifStatementGenerator;
    protected final ImportDeclProcessor importDeclProcessor;
    protected final LabeledStatementGenerator labeledStatementGenerator;
    protected final MemberExpressionGenerator memberExpressionGenerator;
    protected final ByteCodeCompilerMemory memory;
    protected final MethodGenerator methodGenerator;
    protected final NewExpressionGenerator newExpressionGenerator;
    protected final NullLiteralGenerator nullLiteralGenerator;
    protected final NumberLiteralGenerator numberLiteralGenerator;
    protected final ObjectLiteralGenerator objectLiteralGenerator;
    protected final ByteCodeCompilerOptions options;
    protected final ParenExpressionGenerator parenExpressionGenerator;
    protected final Swc4jParseOptions parseOptions;
    protected final RegexLiteralGenerator regexLiteralGenerator;
    protected final SeqExpressionGenerator seqExpressionGenerator;
    protected final StatementGenerator statementGenerator;
    protected final StringLiteralGenerator stringLiteralGenerator;
    protected final Swc4j swc4j;
    protected final SwitchStatementGenerator switchStatementGenerator;
    protected final TsAsExpressionGenerator tsAsExpressionGenerator;
    protected final TypeAliasCollector typeAliasCollector;
    protected final TypeResolver typeResolver;
    protected final UnaryExpressionGenerator unaryExpressionGenerator;
    protected final UpdateExpressionGenerator updateExpressionGenerator;
    protected final VarDeclGenerator varDeclGenerator;
    protected final VariableAnalyzer variableAnalyzer;
    protected final WhileStatementGenerator whileStatementGenerator;


    ByteCodeCompiler(ByteCodeCompilerOptions options) {
        this.options = AssertionUtils.notNull(options, "options");
        memory = new ByteCodeCompilerMemory();
        // Inject type aliases from options into the global scope
        memory.getScopedTypeAliasRegistry().getGlobalScope().putAll(options.typeAliasMap());
        parseOptions = new Swc4jParseOptions()
                .setCaptureAst(true);
        swc4j = new Swc4j();

        arrayLiteralGenerator = new ArrayLiteralGenerator(this);
        assignExpressionGenerator = new AssignExpressionGenerator(this);
        astProcessor = new AstProcessor(this);
        binaryExpressionGenerator = new BinaryExpressionGenerator(this);
        bigIntLiteralGenerator = new BigIntLiteralGenerator(this);
        boolLiteralGenerator = new BoolLiteralGenerator(this);
        breakStatementGenerator = new BreakStatementGenerator(this);
        callExpressionGenerator = new CallExpressionGenerator(this);
        classCollector = new ClassCollector(this);
        classGenerator = new ClassGenerator(this);
        conditionalExpressionGenerator = new ConditionalExpressionGenerator(this);
        continueStatementGenerator = new ContinueStatementGenerator(this);
        doWhileStatementGenerator = new DoWhileStatementGenerator(this);
        enumCollector = new EnumCollector(this);
        enumGenerator = new EnumGenerator(this);
        expressionGenerator = new ExpressionGenerator(this);
        forInStatementGenerator = new ForInStatementGenerator(this);
        forStatementGenerator = new ForStatementGenerator(this);
        identifierGenerator = new IdentifierGenerator(this);
        ifStatementGenerator = new IfStatementGenerator(this);
        importDeclProcessor = new ImportDeclProcessor(this);
        labeledStatementGenerator = new LabeledStatementGenerator(this);
        memberExpressionGenerator = new MemberExpressionGenerator(this);
        methodGenerator = new MethodGenerator(this);
        newExpressionGenerator = new NewExpressionGenerator(this);
        nullLiteralGenerator = new NullLiteralGenerator(this);
        numberLiteralGenerator = new NumberLiteralGenerator(this);
        objectLiteralGenerator = new ObjectLiteralGenerator(this);
        parenExpressionGenerator = new ParenExpressionGenerator(this);
        regexLiteralGenerator = new RegexLiteralGenerator(this);
        seqExpressionGenerator = new SeqExpressionGenerator(this);
        statementGenerator = new StatementGenerator(this);
        stringLiteralGenerator = new StringLiteralGenerator(this);
        switchStatementGenerator = new SwitchStatementGenerator(this);
        tsAsExpressionGenerator = new TsAsExpressionGenerator(this);
        typeAliasCollector = new TypeAliasCollector(this);
        typeResolver = new TypeResolver(this);
        unaryExpressionGenerator = new UnaryExpressionGenerator(this);
        updateExpressionGenerator = new UpdateExpressionGenerator(this);
        varDeclGenerator = new VarDeclGenerator(this);
        variableAnalyzer = new VariableAnalyzer(this);
        whileStatementGenerator = new WhileStatementGenerator(this);
    }

    public static ByteCodeCompiler of(ByteCodeCompilerOptions options) {
        AssertionUtils.notNull(options, "options");
        return switch (options.jdkVersion()) {
            case JDK_17 -> new ByteCodeCompiler17(options);
        };
    }

    public Map<String, byte[]> compile(String code) throws Swc4jCoreException, Swc4jByteCodeCompilerException {
        memory.getByteCodeMap().clear();
        Swc4jParseOutput output = swc4j.parse(code, parseOptions);
        compileProgram(output.getProgram());
        return memory.getByteCodeMap();
    }

    abstract void compileProgram(ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException;

    public ArrayLiteralGenerator getArrayLiteralGenerator() {
        return arrayLiteralGenerator;
    }

    public AssignExpressionGenerator getAssignExpressionGenerator() {
        return assignExpressionGenerator;
    }

    public AstProcessor getAstProcessor() {
        return astProcessor;
    }

    public BigIntLiteralGenerator getBigIntLiteralGenerator() {
        return bigIntLiteralGenerator;
    }

    public BinaryExpressionGenerator getBinaryExpressionGenerator() {
        return binaryExpressionGenerator;
    }

    public BoolLiteralGenerator getBoolLiteralGenerator() {
        return boolLiteralGenerator;
    }

    public BreakStatementGenerator getBreakStatementGenerator() {
        return breakStatementGenerator;
    }

    public CallExpressionGenerator getCallExpressionGenerator() {
        return callExpressionGenerator;
    }

    public ClassCollector getClassCollector() {
        return classCollector;
    }

    public ClassGenerator getClassGenerator() {
        return classGenerator;
    }

    public ConditionalExpressionGenerator getConditionalExpressionGenerator() {
        return conditionalExpressionGenerator;
    }

    public ContinueStatementGenerator getContinueStatementGenerator() {
        return continueStatementGenerator;
    }

    public DoWhileStatementGenerator getDoWhileStatementGenerator() {
        return doWhileStatementGenerator;
    }

    public EnumCollector getEnumCollector() {
        return enumCollector;
    }

    public EnumGenerator getEnumGenerator() {
        return enumGenerator;
    }

    public ExpressionGenerator getExpressionGenerator() {
        return expressionGenerator;
    }

    public ForInStatementGenerator getForInStatementGenerator() {
        return forInStatementGenerator;
    }

    public ForStatementGenerator getForStatementGenerator() {
        return forStatementGenerator;
    }

    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
    }

    public IfStatementGenerator getIfStatementGenerator() {
        return ifStatementGenerator;
    }

    public ImportDeclProcessor getImportDeclProcessor() {
        return importDeclProcessor;
    }

    public LabeledStatementGenerator getLabeledStatementGenerator() {
        return labeledStatementGenerator;
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

    public NewExpressionGenerator getNewExpressionGenerator() {
        return newExpressionGenerator;
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

    public RegexLiteralGenerator getRegexLiteralGenerator() {
        return regexLiteralGenerator;
    }

    public SeqExpressionGenerator getSeqExpressionGenerator() {
        return seqExpressionGenerator;
    }

    public StatementGenerator getStatementGenerator() {
        return statementGenerator;
    }

    public StringLiteralGenerator getStringLiteralGenerator() {
        return stringLiteralGenerator;
    }

    public SwitchStatementGenerator getSwitchStatementGenerator() {
        return switchStatementGenerator;
    }

    public TsAsExpressionGenerator getTsAsExpressionGenerator() {
        return tsAsExpressionGenerator;
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

    public VarDeclGenerator getVarDeclGenerator() {
        return varDeclGenerator;
    }

    public VariableAnalyzer getVariableAnalyzer() {
        return variableAnalyzer;
    }

    public WhileStatementGenerator getWhileStatementGenerator() {
        return whileStatementGenerator;
    }
}
