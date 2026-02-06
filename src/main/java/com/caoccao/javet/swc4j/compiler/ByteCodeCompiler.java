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
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.ClassMethodProcessor;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.clazz.ClassProcessor;
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

/**
 * Abstract bytecode compiler for compiling JavaScript/TypeScript to JVM bytecode.
 */
public sealed abstract class ByteCodeCompiler permits
        ByteCodeCompiler17 {
    /**
     * Processor for array literal expressions.
     */
    protected final ArrayLiteralProcessor arrayLiteralProcessor;
    /**
     * Processor for arrow function expressions.
     */
    protected final ArrowExpressionProcessor arrowExpressionProcessor;
    /**
     * Processor for assignment expressions.
     */
    protected final AssignExpressionProcessor assignExpressionProcessor;
    /**
     * Processor for BigInt literal expressions.
     */
    protected final BigIntLiteralProcessor bigIntLiteralProcessor;
    /**
     * Processor for binary expressions.
     */
    protected final BinaryExpressionProcessor binaryExpressionProcessor;
    /**
     * Processor for boolean literal expressions.
     */
    protected final BoolLiteralProcessor boolLiteralProcessor;
    /**
     * Processor for break statements.
     */
    protected final BreakStatementProcessor breakStatementProcessor;
    /**
     * Processor for call expressions.
     */
    protected final CallExpressionProcessor callExpressionProcessor;
    /**
     * Collector for class declarations.
     */
    protected final ClassCollector classCollector;
    /**
     * Processor for class declarations.
     */
    protected final ClassDeclProcessor classDeclProcessor;
    /**
     * Processor for class expressions.
     */
    protected final ClassExpressionProcessor classExpressionProcessor;
    /**
     * Processor for class methods.
     */
    protected final ClassMethodProcessor classMethodProcessor;
    /**
     * The Class processor.
     */
    protected final ClassProcessor classProcessor;
    /**
     * Processor for conditional (ternary) expressions.
     */
    protected final ConditionalExpressionProcessor conditionalExpressionProcessor;
    /**
     * Processor for continue statements.
     */
    protected final ContinueStatementProcessor continueStatementProcessor;
    /**
     * Processor for declarations.
     */
    protected final DeclProcessor declProcessor;
    /**
     * Processor for do-while statements.
     */
    protected final DoWhileStatementProcessor doWhileStatementProcessor;
    /**
     * Collector for enum declarations.
     */
    protected final EnumCollector enumCollector;
    /**
     * Processor for export declarations.
     */
    protected final ExportDeclProcessor exportDeclProcessor;
    /**
     * Processor for general expressions.
     */
    protected final ExpressionProcessor expressionProcessor;
    /**
     * Processor for for-in statements.
     */
    protected final ForInStatementProcessor forInStatementProcessor;
    /**
     * Processor for for-of statements.
     */
    protected final ForOfStatementProcessor forOfStatementProcessor;
    /**
     * Processor for for statements.
     */
    protected final ForStatementProcessor forStatementProcessor;
    /**
     * Processor for function declarations.
     */
    protected final FunctionDeclarationProcessor functionDeclarationProcessor;
    /**
     * Processor for function expressions.
     */
    protected final FunctionExpressionProcessor functionExpressionProcessor;
    /**
     * Processor for identifier expressions.
     */
    protected final IdentifierProcessor identifierProcessor;
    /**
     * Processor for if statements.
     */
    protected final IfStatementProcessor ifStatementProcessor;
    /**
     * Processor for import declarations.
     */
    protected final ImportDeclProcessor importDeclProcessor;
    /**
     * Processor for labeled statements.
     */
    protected final LabeledStatementProcessor labeledStatementProcessor;
    /**
     * Processor for member access expressions.
     */
    protected final MemberExpressionProcessor memberExpressionProcessor;
    /**
     * Memory manager for compilation state.
     */
    protected final ByteCodeCompilerMemory memory;
    /**
     * Processor for module items.
     */
    protected final ModuleItemProcessor moduleItemProcessor;
    /**
     * Analyzer for mutable variable captures in closures.
     */
    protected final MutableCaptureAnalyzer mutableCaptureAnalyzer;
    /**
     * Processor for new expressions.
     */
    protected final NewExpressionProcessor newExpressionProcessor;
    /**
     * Processor for null literal expressions.
     */
    protected final NullLiteralProcessor nullLiteralProcessor;
    /**
     * Processor for number literal expressions.
     */
    protected final NumberLiteralProcessor numberLiteralProcessor;
    /**
     * Processor for object literal expressions.
     */
    protected final ObjectLiteralProcessor objectLiteralProcessor;
    /**
     * Processor for optional chaining expressions.
     */
    protected final OptionalChainExpressionProcessor optionalChainExpressionProcessor;
    /**
     * Compiler options.
     */
    protected final ByteCodeCompilerOptions options;
    /**
     * Processor for parenthesized expressions.
     */
    protected final ParenExpressionProcessor parenExpressionProcessor;
    /**
     * Parse options for SWC parser.
     */
    protected final Swc4jParseOptions parseOptions;
    /**
     * Processor for private methods.
     */
    protected final PrivateMethodProcessor privateMethodProcessor;
    /**
     * Processor for regex literal expressions.
     */
    protected final RegexLiteralProcessor regexLiteralProcessor;
    /**
     * Processor for sequence expressions.
     */
    protected final SeqExpressionProcessor seqExpressionProcessor;
    /**
     * Processor for general statements.
     */
    protected final StatementProcessor statementProcessor;
    /**
     * Processor for statement nodes.
     */
    protected final StmtProcessor stmtProcessor;
    /**
     * Processor for string literal expressions.
     */
    protected final StringLiteralProcessor stringLiteralProcessor;
    /**
     * SWC4J instance for parsing.
     */
    protected final Swc4j swc4j;
    /**
     * Processor for switch statements.
     */
    protected final SwitchStatementProcessor switchStatementProcessor;
    /**
     * Processor for tagged template literal expressions.
     */
    protected final TaggedTemplateLiteralProcessor taggedTemplateLiteralProcessor;
    /**
     * Processor for template literal expressions.
     */
    protected final TemplateLiteralProcessor templateLiteralProcessor;
    /**
     * Processor for this expressions.
     */
    protected final ThisExpressionProcessor thisExpressionProcessor;
    /**
     * Processor for throw statements.
     */
    protected final ThrowStatementProcessor throwStatementProcessor;
    /**
     * Processor for try statements.
     */
    protected final TryStatementProcessor tryStatementProcessor;
    /**
     * Processor for TypeScript as expressions.
     */
    protected final TsAsExpressionProcessor tsAsExpressionProcessor;
    /**
     * Processor for TypeScript enum declarations.
     */
    protected final TsEnumDeclProcessor tsEnumDeclProcessor;
    /**
     * Collector for TypeScript interface declarations.
     */
    protected final TsInterfaceCollector tsInterfaceCollector;
    /**
     * Processor for TypeScript interface declarations.
     */
    protected final TsInterfaceDeclProcessor tsInterfaceDeclProcessor;
    /**
     * Processor for TypeScript module declarations.
     */
    protected final TsModuleDeclProcessor tsModuleDeclProcessor;
    /**
     * Collector for type alias declarations.
     */
    protected final TypeAliasCollector typeAliasCollector;
    /**
     * Type resolver for inferring types.
     */
    protected final TypeResolver typeResolver;
    /**
     * Processor for unary expressions.
     */
    protected final UnaryExpressionProcessor unaryExpressionProcessor;
    /**
     * Processor for update expressions.
     */
    protected final UpdateExpressionProcessor updateExpressionProcessor;
    /**
     * Processor for variable declarations.
     */
    protected final VarDeclProcessor varDeclProcessor;
    /**
     * Analyzer for variable scoping.
     */
    protected final VariableAnalyzer variableAnalyzer;
    /**
     * Processor for while statements.
     */
    protected final WhileStatementProcessor whileStatementProcessor;

    /**
     * Instantiates a new Byte code compiler.
     *
     * @param options the options
     */
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
        classExpressionProcessor = new ClassExpressionProcessor(this);
        conditionalExpressionProcessor = new ConditionalExpressionProcessor(this);
        continueStatementProcessor = new ContinueStatementProcessor(this);
        declProcessor = new DeclProcessor(this);
        doWhileStatementProcessor = new DoWhileStatementProcessor(this);
        enumCollector = new EnumCollector(this);
        exportDeclProcessor = new ExportDeclProcessor(this);
        expressionProcessor = new ExpressionProcessor(this);
        functionExpressionProcessor = new FunctionExpressionProcessor(this);
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

    /**
     * Creates a bytecode compiler with the given options.
     *
     * @param options the compiler options
     * @return a new bytecode compiler instance
     */
    public static ByteCodeCompiler of(ByteCodeCompilerOptions options) {
        AssertionUtils.notNull(options, "options");
        return switch (options.jdkVersion()) {
            case JDK_17 -> new ByteCodeCompiler17(options);
        };
    }

    /**
     * Compiles JavaScript/TypeScript code to bytecode.
     *
     * @param code the source code to compile
     * @return a bytecode runner for executing the compiled code
     * @throws Swc4jCoreException             if parsing fails
     * @throws Swc4jByteCodeCompilerException if compilation fails
     */
    public ByteCodeRunner compile(String code) throws Swc4jCoreException, Swc4jByteCodeCompilerException {
        Swc4jParseOutput output = swc4j.parse(code, parseOptions);
        compileProgram(code, output.getProgram());
        return new ByteCodeRunner(memory.getByteCodeMap(), options.optionalParentClassLoader().orElse(getClass().getClassLoader()));
    }

    /**
     * Compile program.
     *
     * @param code    the code
     * @param program the program
     * @throws Swc4jByteCodeCompilerException the swc4j byte code compiler exception
     */
    abstract void compileProgram(String code, ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException;

    /**
     * Gets the array literal processor.
     *
     * @return the array literal processor
     */
    public ArrayLiteralProcessor getArrayLiteralProcessor() {
        return arrayLiteralProcessor;
    }

    /**
     * Gets the arrow expression processor.
     *
     * @return the arrow expression processor
     */
    public ArrowExpressionProcessor getArrowExpressionProcessor() {
        return arrowExpressionProcessor;
    }

    /**
     * Gets the assignment expression processor.
     *
     * @return the assignment expression processor
     */
    public AssignExpressionProcessor getAssignExpressionProcessor() {
        return assignExpressionProcessor;
    }

    /**
     * Gets the BigInt literal processor.
     *
     * @return the BigInt literal processor
     */
    public BigIntLiteralProcessor getBigIntLiteralProcessor() {
        return bigIntLiteralProcessor;
    }

    /**
     * Gets the binary expression processor.
     *
     * @return the binary expression processor
     */
    public BinaryExpressionProcessor getBinaryExpressionProcessor() {
        return binaryExpressionProcessor;
    }

    /**
     * Gets the boolean literal processor.
     *
     * @return the boolean literal processor
     */
    public BoolLiteralProcessor getBoolLiteralProcessor() {
        return boolLiteralProcessor;
    }

    /**
     * Gets the break statement processor.
     *
     * @return the break statement processor
     */
    public BreakStatementProcessor getBreakStatementProcessor() {
        return breakStatementProcessor;
    }

    /**
     * Gets the call expression processor.
     *
     * @return the call expression processor
     */
    public CallExpressionProcessor getCallExpressionProcessor() {
        return callExpressionProcessor;
    }

    /**
     * Gets the class collector.
     *
     * @return the class collector
     */
    public ClassCollector getClassCollector() {
        return classCollector;
    }

    /**
     * Gets the class declaration processor.
     *
     * @return the class declaration processor
     */
    public ClassDeclProcessor getClassDeclProcessor() {
        return classDeclProcessor;
    }

    /**
     * Gets class expression processor.
     *
     * @return the class expression processor
     */
    public ClassExpressionProcessor getClassExpressionProcessor() {
        return classExpressionProcessor;
    }

    /**
     * Gets the class method processor.
     *
     * @return the class method processor
     */
    public ClassMethodProcessor getClassMethodProcessor() {
        return classMethodProcessor;
    }

    /**
     * Gets the class processor.
     *
     * @return the class processor
     */
    public ClassProcessor getClassProcessor() {
        return classProcessor;
    }

    /**
     * Gets the conditional expression processor.
     *
     * @return the conditional expression processor
     */
    public ConditionalExpressionProcessor getConditionalExpressionProcessor() {
        return conditionalExpressionProcessor;
    }

    /**
     * Gets the continue statement processor.
     *
     * @return the continue statement processor
     */
    public ContinueStatementProcessor getContinueStatementProcessor() {
        return continueStatementProcessor;
    }

    /**
     * Gets the declaration processor.
     *
     * @return the declaration processor
     */
    public DeclProcessor getDeclProcessor() {
        return declProcessor;
    }

    /**
     * Gets the do-while statement processor.
     *
     * @return the do-while statement processor
     */
    public DoWhileStatementProcessor getDoWhileStatementProcessor() {
        return doWhileStatementProcessor;
    }

    /**
     * Gets the enum collector.
     *
     * @return the enum collector
     */
    public EnumCollector getEnumCollector() {
        return enumCollector;
    }

    /**
     * Gets the export declaration processor.
     *
     * @return the export declaration processor
     */
    public ExportDeclProcessor getExportDeclProcessor() {
        return exportDeclProcessor;
    }

    /**
     * Gets the expression processor.
     *
     * @return the expression processor
     */
    public ExpressionProcessor getExpressionProcessor() {
        return expressionProcessor;
    }

    /**
     * Gets the for-in statement processor.
     *
     * @return the for-in statement processor
     */
    public ForInStatementProcessor getForInStatementProcessor() {
        return forInStatementProcessor;
    }

    /**
     * Gets the for-of statement processor.
     *
     * @return the for-of statement processor
     */
    public ForOfStatementProcessor getForOfStatementProcessor() {
        return forOfStatementProcessor;
    }

    /**
     * Gets the for statement processor.
     *
     * @return the for statement processor
     */
    public ForStatementProcessor getForStatementProcessor() {
        return forStatementProcessor;
    }

    /**
     * Gets the function declaration processor.
     *
     * @return the function declaration processor
     */
    public FunctionDeclarationProcessor getFunctionDeclarationProcessor() {
        return functionDeclarationProcessor;
    }

    /**
     * Gets function expression processor.
     *
     * @return the function expression processor
     */
    public FunctionExpressionProcessor getFunctionExpressionProcessor() {
        return functionExpressionProcessor;
    }

    /**
     * Gets the identifier processor.
     *
     * @return the identifier processor
     */
    public IdentifierProcessor getIdentifierProcessor() {
        return identifierProcessor;
    }

    /**
     * Gets the if statement processor.
     *
     * @return the if statement processor
     */
    public IfStatementProcessor getIfStatementProcessor() {
        return ifStatementProcessor;
    }

    /**
     * Gets the import declaration processor.
     *
     * @return the import declaration processor
     */
    public ImportDeclProcessor getImportDeclProcessor() {
        return importDeclProcessor;
    }

    /**
     * Gets the labeled statement processor.
     *
     * @return the labeled statement processor
     */
    public LabeledStatementProcessor getLabeledStatementProcessor() {
        return labeledStatementProcessor;
    }

    /**
     * Gets the member expression processor.
     *
     * @return the member expression processor
     */
    public MemberExpressionProcessor getMemberExpressionProcessor() {
        return memberExpressionProcessor;
    }

    /**
     * Gets the compiler memory.
     *
     * @return the compiler memory
     */
    public ByteCodeCompilerMemory getMemory() {
        return memory;
    }

    /**
     * Gets the module item processor.
     *
     * @return the module item processor
     */
    public ModuleItemProcessor getModuleItemProcessor() {
        return moduleItemProcessor;
    }

    /**
     * Gets the mutable capture analyzer.
     *
     * @return the mutable capture analyzer
     */
    public MutableCaptureAnalyzer getMutableCaptureAnalyzer() {
        return mutableCaptureAnalyzer;
    }

    /**
     * Gets the new expression processor.
     *
     * @return the new expression processor
     */
    public NewExpressionProcessor getNewExpressionProcessor() {
        return newExpressionProcessor;
    }

    /**
     * Gets the null literal processor.
     *
     * @return the null literal processor
     */
    public NullLiteralProcessor getNullLiteralProcessor() {
        return nullLiteralProcessor;
    }

    /**
     * Gets the number literal processor.
     *
     * @return the number literal processor
     */
    public NumberLiteralProcessor getNumberLiteralProcessor() {
        return numberLiteralProcessor;
    }

    /**
     * Gets the object literal processor.
     *
     * @return the object literal processor
     */
    public ObjectLiteralProcessor getObjectLiteralProcessor() {
        return objectLiteralProcessor;
    }

    /**
     * Gets the optional chain expression processor.
     *
     * @return the optional chain expression processor
     */
    public OptionalChainExpressionProcessor getOptionalChainExpressionProcessor() {
        return optionalChainExpressionProcessor;
    }

    /**
     * Gets the compiler options.
     *
     * @return the compiler options
     */
    public ByteCodeCompilerOptions getOptions() {
        return options;
    }

    /**
     * Gets the parenthesized expression processor.
     *
     * @return the parenthesized expression processor
     */
    public ParenExpressionProcessor getParenExpressionProcessor() {
        return parenExpressionProcessor;
    }

    /**
     * Gets the private method processor.
     *
     * @return the private method processor
     */
    public PrivateMethodProcessor getPrivateMethodProcessor() {
        return privateMethodProcessor;
    }

    /**
     * Gets the regex literal processor.
     *
     * @return the regex literal processor
     */
    public RegexLiteralProcessor getRegexLiteralProcessor() {
        return regexLiteralProcessor;
    }

    /**
     * Gets the sequence expression processor.
     *
     * @return the sequence expression processor
     */
    public SeqExpressionProcessor getSeqExpressionProcessor() {
        return seqExpressionProcessor;
    }

    /**
     * Gets the statement processor.
     *
     * @return the statement processor
     */
    public StatementProcessor getStatementProcessor() {
        return statementProcessor;
    }

    /**
     * Gets the statement node processor.
     *
     * @return the statement node processor
     */
    public StmtProcessor getStmtProcessor() {
        return stmtProcessor;
    }

    /**
     * Gets the string literal processor.
     *
     * @return the string literal processor
     */
    public StringLiteralProcessor getStringLiteralProcessor() {
        return stringLiteralProcessor;
    }

    /**
     * Gets the switch statement processor.
     *
     * @return the switch statement processor
     */
    public SwitchStatementProcessor getSwitchStatementProcessor() {
        return switchStatementProcessor;
    }

    /**
     * Gets the tagged template literal processor.
     *
     * @return the tagged template literal processor
     */
    public TaggedTemplateLiteralProcessor getTaggedTemplateLiteralProcessor() {
        return taggedTemplateLiteralProcessor;
    }

    /**
     * Gets the template literal processor.
     *
     * @return the template literal processor
     */
    public TemplateLiteralProcessor getTemplateLiteralProcessor() {
        return templateLiteralProcessor;
    }

    /**
     * Gets the this expression processor.
     *
     * @return the this expression processor
     */
    public ThisExpressionProcessor getThisExpressionProcessor() {
        return thisExpressionProcessor;
    }

    /**
     * Gets the throw statement processor.
     *
     * @return the throw statement processor
     */
    public ThrowStatementProcessor getThrowStatementProcessor() {
        return throwStatementProcessor;
    }

    /**
     * Gets the try statement processor.
     *
     * @return the try statement processor
     */
    public TryStatementProcessor getTryStatementProcessor() {
        return tryStatementProcessor;
    }

    /**
     * Gets the TypeScript as expression processor.
     *
     * @return the TypeScript as expression processor
     */
    public TsAsExpressionProcessor getTsAsExpressionProcessor() {
        return tsAsExpressionProcessor;
    }

    /**
     * Gets the TypeScript enum declaration processor.
     *
     * @return the TypeScript enum declaration processor
     */
    public TsEnumDeclProcessor getTsEnumDeclProcessor() {
        return tsEnumDeclProcessor;
    }

    /**
     * Gets the TypeScript interface collector.
     *
     * @return the TypeScript interface collector
     */
    public TsInterfaceCollector getTsInterfaceCollector() {
        return tsInterfaceCollector;
    }

    /**
     * Gets the TypeScript interface declaration processor.
     *
     * @return the TypeScript interface declaration processor
     */
    public TsInterfaceDeclProcessor getTsInterfaceDeclProcessor() {
        return tsInterfaceDeclProcessor;
    }

    /**
     * Gets the TypeScript module declaration processor.
     *
     * @return the TypeScript module declaration processor
     */
    public TsModuleDeclProcessor getTsModuleDeclProcessor() {
        return tsModuleDeclProcessor;
    }

    /**
     * Gets the type alias collector.
     *
     * @return the type alias collector
     */
    public TypeAliasCollector getTypeAliasCollector() {
        return typeAliasCollector;
    }

    /**
     * Gets the type resolver.
     *
     * @return the type resolver
     */
    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    /**
     * Gets the unary expression processor.
     *
     * @return the unary expression processor
     */
    public UnaryExpressionProcessor getUnaryExpressionProcessor() {
        return unaryExpressionProcessor;
    }

    /**
     * Gets the update expression processor.
     *
     * @return the update expression processor
     */
    public UpdateExpressionProcessor getUpdateExpressionProcessor() {
        return updateExpressionProcessor;
    }

    /**
     * Gets the variable declaration processor.
     *
     * @return the variable declaration processor
     */
    public VarDeclProcessor getVarDeclProcessor() {
        return varDeclProcessor;
    }

    /**
     * Gets the variable analyzer.
     *
     * @return the variable analyzer
     */
    public VariableAnalyzer getVariableAnalyzer() {
        return variableAnalyzer;
    }

    /**
     * Gets the while statement processor.
     *
     * @return the while statement processor
     */
    public WhileStatementProcessor getWhileStatementProcessor() {
        return whileStatementProcessor;
    }
}
