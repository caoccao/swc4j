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
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.ClassDeclProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.ClassProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.ClassMethodProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.PrivateMethodProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.expr.lit.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.interfaces.DeclProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.interfaces.ModuleItemProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.interfaces.StmtProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.module.ExportDeclProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.module.ImportDeclProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.stmt.*;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.ts.TsAsExpressionProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

public sealed abstract class ByteCodeCompiler permits
        ByteCodeCompiler17 {
    protected final ArrayLiteralProcessor arrayLiteralProcessor;
    protected final ArrowExpressionProcessor arrowExpressionProcessor;
    protected final AssignExpressionProcessor assignExpressionProcessor;
    protected final BigIntLiteralProcessor bigIntLiteralProcessor;
    protected final BinaryExpressionProcessor binaryExpressionProcessor;
    protected final BoolLiteralProcessor boolLiteralProcessor;
    protected final BreakStatementProcessor breakStatementProcessor;
    protected final CallExpressionProcessor callExpressionProcessor;
    protected final ClassCollector classCollector;
    protected final ClassDeclProcessor classDeclProcessor;
    protected final ClassProcessor classProcessor;
    protected final ClassMethodProcessor classMethodProcessor;
    protected final ConditionalExpressionProcessor conditionalExpressionProcessor;
    protected final ContinueStatementProcessor continueStatementProcessor;
    protected final DeclProcessor declProcessor;
    protected final DoWhileStatementProcessor doWhileStatementProcessor;
    protected final EnumCollector enumCollector;
    protected final ExportDeclProcessor exportDeclProcessor;
    protected final ExpressionProcessor expressionProcessor;
    protected final ForInStatementProcessor forInStatementProcessor;
    protected final ForOfStatementProcessor forOfStatementProcessor;
    protected final ForStatementProcessor forStatementProcessor;
    protected final FunctionDeclarationProcessor functionDeclarationProcessor;
    protected final IdentifierProcessor identifierProcessor;
    protected final IfStatementProcessor ifStatementProcessor;
    protected final ImportDeclProcessor importDeclProcessor;
    protected final LabeledStatementProcessor labeledStatementProcessor;
    protected final MemberExpressionProcessor memberExpressionProcessor;
    protected final ByteCodeCompilerMemory memory;
    protected final ModuleItemProcessor moduleItemProcessor;
    protected final MutableCaptureAnalyzer mutableCaptureAnalyzer;
    protected final NewExpressionProcessor newExpressionProcessor;
    protected final NullLiteralProcessor nullLiteralProcessor;
    protected final NumberLiteralProcessor numberLiteralProcessor;
    protected final ObjectLiteralProcessor objectLiteralProcessor;
    protected final OptionalChainExpressionProcessor optionalChainExpressionProcessor;
    protected final ByteCodeCompilerOptions options;
    protected final ParenExpressionProcessor parenExpressionProcessor;
    protected final Swc4jParseOptions parseOptions;
    protected final PrivateMethodProcessor privateMethodProcessor;
    protected final RegexLiteralProcessor regexLiteralProcessor;
    protected final SeqExpressionProcessor seqExpressionProcessor;
    protected final StatementProcessor statementProcessor;
    protected final StmtProcessor stmtProcessor;
    protected final StringLiteralProcessor stringLiteralProcessor;
    protected final Swc4j swc4j;
    protected final SwitchStatementProcessor switchStatementProcessor;
    protected final TaggedTemplateLiteralProcessor taggedTemplateLiteralProcessor;
    protected final TemplateLiteralProcessor templateLiteralProcessor;
    protected final ThisExpressionProcessor thisExpressionProcessor;
    protected final ThrowStatementProcessor throwStatementProcessor;
    protected final TryStatementProcessor tryStatementProcessor;
    protected final TsAsExpressionProcessor tsAsExpressionProcessor;
    protected final TsEnumDeclProcessor tsEnumDeclProcessor;
    protected final TsInterfaceCollector tsInterfaceCollector;
    protected final TsInterfaceDeclProcessor tsInterfaceDeclProcessor;
    protected final TsModuleDeclProcessor tsModuleDeclProcessor;
    protected final TypeAliasCollector typeAliasCollector;
    protected final TypeResolver typeResolver;
    protected final UnaryExpressionProcessor unaryExpressionProcessor;
    protected final UpdateExpressionProcessor updateExpressionProcessor;
    protected final VarDeclProcessor varDeclProcessor;
    protected final VariableAnalyzer variableAnalyzer;
    protected final WhileStatementProcessor whileStatementProcessor;

    ByteCodeCompiler(ByteCodeCompilerOptions options) {
        this.options = AssertionUtils.notNull(options, "options");
        memory = new ByteCodeCompilerMemory();
        // Inject type aliases from options into the global scope
        memory.getScopedTypeAliasRegistry().getGlobalScope().putAll(options.typeAliasMap());
        parseOptions = new Swc4jParseOptions()
                .setCaptureAst(true);
        swc4j = new Swc4j();

        arrayLiteralProcessor = new ArrayLiteralProcessor(this);
        arrowExpressionProcessor = new ArrowExpressionProcessor(this);
        assignExpressionProcessor = new AssignExpressionProcessor(this);
        bigIntLiteralProcessor = new BigIntLiteralProcessor(this);
        binaryExpressionProcessor = new BinaryExpressionProcessor(this);
        boolLiteralProcessor = new BoolLiteralProcessor(this);
        breakStatementProcessor = new BreakStatementProcessor(this);
        callExpressionProcessor = new CallExpressionProcessor(this);
        classCollector = new ClassCollector(this);
        classDeclProcessor = new ClassDeclProcessor(this);
        classProcessor = new ClassProcessor(this);
        classMethodProcessor = new ClassMethodProcessor(this);
        conditionalExpressionProcessor = new ConditionalExpressionProcessor(this);
        continueStatementProcessor = new ContinueStatementProcessor(this);
        declProcessor = new DeclProcessor(this);
        doWhileStatementProcessor = new DoWhileStatementProcessor(this);
        enumCollector = new EnumCollector(this);
        exportDeclProcessor = new ExportDeclProcessor(this);
        expressionProcessor = new ExpressionProcessor(this);
        forInStatementProcessor = new ForInStatementProcessor(this);
        forOfStatementProcessor = new ForOfStatementProcessor(this);
        forStatementProcessor = new ForStatementProcessor(this);
        functionDeclarationProcessor = new FunctionDeclarationProcessor(this);
        identifierProcessor = new IdentifierProcessor(this);
        ifStatementProcessor = new IfStatementProcessor(this);
        importDeclProcessor = new ImportDeclProcessor(this);
        labeledStatementProcessor = new LabeledStatementProcessor(this);
        memberExpressionProcessor = new MemberExpressionProcessor(this);
        moduleItemProcessor = new ModuleItemProcessor(this);
        mutableCaptureAnalyzer = new MutableCaptureAnalyzer(this);
        newExpressionProcessor = new NewExpressionProcessor(this);
        nullLiteralProcessor = new NullLiteralProcessor(this);
        numberLiteralProcessor = new NumberLiteralProcessor(this);
        objectLiteralProcessor = new ObjectLiteralProcessor(this);
        optionalChainExpressionProcessor = new OptionalChainExpressionProcessor(this);
        parenExpressionProcessor = new ParenExpressionProcessor(this);
        privateMethodProcessor = new PrivateMethodProcessor(this);
        regexLiteralProcessor = new RegexLiteralProcessor(this);
        seqExpressionProcessor = new SeqExpressionProcessor(this);
        statementProcessor = new StatementProcessor(this);
        stmtProcessor = new StmtProcessor(this);
        stringLiteralProcessor = new StringLiteralProcessor(this);
        switchStatementProcessor = new SwitchStatementProcessor(this);
        taggedTemplateLiteralProcessor = new TaggedTemplateLiteralProcessor(this);
        templateLiteralProcessor = new TemplateLiteralProcessor(this);
        thisExpressionProcessor = new ThisExpressionProcessor(this);
        throwStatementProcessor = new ThrowStatementProcessor(this);
        tryStatementProcessor = new TryStatementProcessor(this);
        tsAsExpressionProcessor = new TsAsExpressionProcessor(this);
        tsEnumDeclProcessor = new TsEnumDeclProcessor(this);
        tsInterfaceCollector = new TsInterfaceCollector(this);
        tsInterfaceDeclProcessor = new TsInterfaceDeclProcessor(this);
        tsModuleDeclProcessor = new TsModuleDeclProcessor(this);
        typeAliasCollector = new TypeAliasCollector(this);
        typeResolver = new TypeResolver(this);
        unaryExpressionProcessor = new UnaryExpressionProcessor(this);
        updateExpressionProcessor = new UpdateExpressionProcessor(this);
        varDeclProcessor = new VarDeclProcessor(this);
        variableAnalyzer = new VariableAnalyzer(this);
        whileStatementProcessor = new WhileStatementProcessor(this);
    }

    public static ByteCodeCompiler of(ByteCodeCompilerOptions options) {
        AssertionUtils.notNull(options, "options");
        return switch (options.jdkVersion()) {
            case JDK_17 -> new ByteCodeCompiler17(options);
        };
    }

    public ByteCodeRunner compile(String code) throws Swc4jCoreException, Swc4jByteCodeCompilerException {
        Swc4jParseOutput output = swc4j.parse(code, parseOptions);
        compileProgram(code, output.getProgram());
        return new ByteCodeRunner(memory.getByteCodeMap(), options.optionalParentClassLoader().orElse(getClass().getClassLoader()));
    }

    abstract void compileProgram(String code, ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException;

    public ArrayLiteralProcessor getArrayLiteralProcessor() {
        return arrayLiteralProcessor;
    }

    public ArrowExpressionProcessor getArrowExpressionProcessor() {
        return arrowExpressionProcessor;
    }

    public AssignExpressionProcessor getAssignExpressionProcessor() {
        return assignExpressionProcessor;
    }

    public BigIntLiteralProcessor getBigIntLiteralProcessor() {
        return bigIntLiteralProcessor;
    }

    public BinaryExpressionProcessor getBinaryExpressionProcessor() {
        return binaryExpressionProcessor;
    }

    public BoolLiteralProcessor getBoolLiteralProcessor() {
        return boolLiteralProcessor;
    }

    public BreakStatementProcessor getBreakStatementProcessor() {
        return breakStatementProcessor;
    }

    public CallExpressionProcessor getCallExpressionProcessor() {
        return callExpressionProcessor;
    }

    public ClassCollector getClassCollector() {
        return classCollector;
    }

    public ClassDeclProcessor getClassDeclProcessor() {
        return classDeclProcessor;
    }

    public ClassProcessor getClassProcessor() {
        return classProcessor;
    }

    public ClassMethodProcessor getClassMethodProcessor() {
        return classMethodProcessor;
    }

    public ConditionalExpressionProcessor getConditionalExpressionProcessor() {
        return conditionalExpressionProcessor;
    }

    public ContinueStatementProcessor getContinueStatementProcessor() {
        return continueStatementProcessor;
    }

    public DeclProcessor getDeclProcessor() {
        return declProcessor;
    }

    public DoWhileStatementProcessor getDoWhileStatementProcessor() {
        return doWhileStatementProcessor;
    }

    public EnumCollector getEnumCollector() {
        return enumCollector;
    }

    public ExportDeclProcessor getExportDeclProcessor() {
        return exportDeclProcessor;
    }

    public ExpressionProcessor getExpressionProcessor() {
        return expressionProcessor;
    }

    public ForInStatementProcessor getForInStatementProcessor() {
        return forInStatementProcessor;
    }

    public ForOfStatementProcessor getForOfStatementProcessor() {
        return forOfStatementProcessor;
    }

    public ForStatementProcessor getForStatementProcessor() {
        return forStatementProcessor;
    }

    public FunctionDeclarationProcessor getFunctionDeclarationProcessor() {
        return functionDeclarationProcessor;
    }

    public IdentifierProcessor getIdentifierProcessor() {
        return identifierProcessor;
    }

    public IfStatementProcessor getIfStatementProcessor() {
        return ifStatementProcessor;
    }

    public ImportDeclProcessor getImportDeclProcessor() {
        return importDeclProcessor;
    }

    public LabeledStatementProcessor getLabeledStatementProcessor() {
        return labeledStatementProcessor;
    }

    public MemberExpressionProcessor getMemberExpressionProcessor() {
        return memberExpressionProcessor;
    }

    public ByteCodeCompilerMemory getMemory() {
        return memory;
    }

    public ModuleItemProcessor getModuleItemProcessor() {
        return moduleItemProcessor;
    }

    public MutableCaptureAnalyzer getMutableCaptureAnalyzer() {
        return mutableCaptureAnalyzer;
    }

    public NewExpressionProcessor getNewExpressionProcessor() {
        return newExpressionProcessor;
    }

    public NullLiteralProcessor getNullLiteralProcessor() {
        return nullLiteralProcessor;
    }

    public NumberLiteralProcessor getNumberLiteralProcessor() {
        return numberLiteralProcessor;
    }

    public ObjectLiteralProcessor getObjectLiteralProcessor() {
        return objectLiteralProcessor;
    }

    public OptionalChainExpressionProcessor getOptionalChainExpressionProcessor() {
        return optionalChainExpressionProcessor;
    }

    public ByteCodeCompilerOptions getOptions() {
        return options;
    }

    public ParenExpressionProcessor getParenExpressionProcessor() {
        return parenExpressionProcessor;
    }

    public PrivateMethodProcessor getPrivateMethodProcessor() {
        return privateMethodProcessor;
    }

    public RegexLiteralProcessor getRegexLiteralProcessor() {
        return regexLiteralProcessor;
    }

    public SeqExpressionProcessor getSeqExpressionProcessor() {
        return seqExpressionProcessor;
    }

    public StatementProcessor getStatementProcessor() {
        return statementProcessor;
    }

    public StmtProcessor getStmtProcessor() {
        return stmtProcessor;
    }

    public StringLiteralProcessor getStringLiteralProcessor() {
        return stringLiteralProcessor;
    }

    public SwitchStatementProcessor getSwitchStatementProcessor() {
        return switchStatementProcessor;
    }

    public TaggedTemplateLiteralProcessor getTaggedTemplateLiteralProcessor() {
        return taggedTemplateLiteralProcessor;
    }

    public TemplateLiteralProcessor getTemplateLiteralProcessor() {
        return templateLiteralProcessor;
    }

    public ThisExpressionProcessor getThisExpressionProcessor() {
        return thisExpressionProcessor;
    }

    public ThrowStatementProcessor getThrowStatementProcessor() {
        return throwStatementProcessor;
    }

    public TryStatementProcessor getTryStatementProcessor() {
        return tryStatementProcessor;
    }

    public TsAsExpressionProcessor getTsAsExpressionProcessor() {
        return tsAsExpressionProcessor;
    }

    public TsEnumDeclProcessor getTsEnumDeclProcessor() {
        return tsEnumDeclProcessor;
    }

    public TsInterfaceCollector getTsInterfaceCollector() {
        return tsInterfaceCollector;
    }

    public TsInterfaceDeclProcessor getTsInterfaceDeclProcessor() {
        return tsInterfaceDeclProcessor;
    }

    public TsModuleDeclProcessor getTsModuleDeclProcessor() {
        return tsModuleDeclProcessor;
    }

    public TypeAliasCollector getTypeAliasCollector() {
        return typeAliasCollector;
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public UnaryExpressionProcessor getUnaryExpressionProcessor() {
        return unaryExpressionProcessor;
    }

    public UpdateExpressionProcessor getUpdateExpressionProcessor() {
        return updateExpressionProcessor;
    }

    public VarDeclProcessor getVarDeclProcessor() {
        return varDeclProcessor;
    }

    public VariableAnalyzer getVariableAnalyzer() {
        return variableAnalyzer;
    }

    public WhileStatementProcessor getWhileStatementProcessor() {
        return whileStatementProcessor;
    }
}
