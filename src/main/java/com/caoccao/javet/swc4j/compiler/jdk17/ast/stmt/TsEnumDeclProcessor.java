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

import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstParenExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEnumMemberId;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstTsEnumDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsEnumMember;
import com.caoccao.javet.swc4j.compiler.ByteCodeCompiler;
import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaDescriptor;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaMethod;
import com.caoccao.javet.swc4j.compiler.constants.ConstantJavaType;
import com.caoccao.javet.swc4j.compiler.jdk17.ReturnTypeInfo;
import com.caoccao.javet.swc4j.compiler.jdk17.ast.BaseAstProcessor;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Ts enum decl processor.
 */
public final class TsEnumDeclProcessor extends BaseAstProcessor<Swc4jAstTsEnumDecl> {
    /**
     * Instantiates a new Ts enum decl processor.
     *
     * @param compiler the compiler
     */
    public TsEnumDeclProcessor(ByteCodeCompiler compiler) {
        super(compiler);
    }

    private EnumInfo analyzeEnumMembers(List<Swc4jAstTsEnumMember> members)
            throws Swc4jByteCodeCompilerException {
        List<EnumMemberInfo> memberInfos = new ArrayList<>();
        Map<String, Integer> definedMembers = new HashMap<>(); // Track defined members for computed values
        Boolean isStringEnum = null;
        int autoIncrementValue = 0;

        for (Swc4jAstTsEnumMember member : members) {
            ISwc4jAstTsEnumMemberId memberId = member.getId();
            String tsName = memberId instanceof Swc4jAstIdent ident ? ident.getSym() : memberId.toString();
            String javaName = convertMemberNameToJavaConvention(tsName);

            var initOpt = member.getInit();
            EnumMemberInfo memberInfo;

            if (initOpt.isPresent()) {
                ISwc4jAstExpr initExpr = initOpt.get();

                if (initExpr instanceof Swc4jAstStr strLit) {
                    // String value
                    if (isStringEnum != null && !isStringEnum) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), strLit,
                                "Heterogeneous enums (mixed numeric and string values) are not supported");
                    }
                    isStringEnum = true;

                    String stringValue = strLit.getValue();
                    memberInfo = new EnumMemberInfo(tsName, javaName, 0, stringValue);
                } else {
                    // Try to evaluate as a constant numeric expression
                    if (isStringEnum != null && isStringEnum) {
                        throw new Swc4jByteCodeCompilerException(getSourceCode(), initExpr,
                                "Heterogeneous enums (mixed numeric and string values) are not supported");
                    }
                    isStringEnum = false;

                    int intValue = evaluateConstantExpression(initExpr, definedMembers);
                    autoIncrementValue = intValue + 1;

                    memberInfo = new EnumMemberInfo(tsName, javaName, intValue, null);
                }
            } else {
                // No explicit value - use auto-increment (only valid for numeric enums)
                if (isStringEnum != null && isStringEnum) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), member,
                            "String enum members must have explicit values");
                }
                isStringEnum = false;

                memberInfo = new EnumMemberInfo(tsName, javaName, autoIncrementValue, null);
                autoIncrementValue++;
            }

            // Track this member for computed references
            if (!isStringEnum) {
                definedMembers.put(tsName, memberInfo.intValue);
            }
            memberInfos.add(memberInfo);
        }

        return new EnumInfo(memberInfos, Boolean.TRUE.equals(isStringEnum));
    }

    private String convertMemberNameToJavaConvention(String tsName) {
        // Convert TypeScript enum member names to Java enum constant naming convention
        // TypeScript: Up, Down → Java: UP, DOWN
        return tsName.toUpperCase();
    }

    private int evaluateBinaryExpression(Swc4jAstBinExpr binExpr, Map<String, Integer> definedMembers)
            throws Swc4jByteCodeCompilerException {
        Swc4jAstBinaryOp op = binExpr.getOp();
        int left = evaluateConstantExpression(binExpr.getLeft(), definedMembers);
        int right = evaluateConstantExpression(binExpr.getRight(), definedMembers);

        return switch (op) {
            case Add -> left + right;
            case Sub -> left - right;
            case Mul -> left * right;
            case Div -> {
                if (right == 0) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), binExpr,
                            "Division by zero in enum initializer");
                }
                yield left / right;
            }
            case Mod -> {
                if (right == 0) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), binExpr,
                            "Division by zero in enum initializer");
                }
                yield left % right;
            }
            case Exp -> (int) Math.pow(left, right);
            case BitAnd -> left & right;
            case BitOr -> left | right;
            case BitXor -> left ^ right;
            case LShift -> left << right;
            case RShift -> left >> right;
            case ZeroFillRShift -> left >>> right;
            default -> throw new Swc4jByteCodeCompilerException(getSourceCode(), binExpr,
                    "Unsupported binary operator '" + op.getName() + "' in enum initializer. " +
                            "Only arithmetic (+, -, *, /, %, **) and bitwise (&, |, ^, <<, >>, >>>) operators are supported.");
        };
    }

    /**
     * Evaluates a constant expression at compile time.
     * Supports: numeric literals, unary operations (-, +, ~), binary operations (+, -, *, /, %, **, &, |, ^, <<, >>, >>>),
     * parenthesized expressions, and references to previously defined enum members.
     *
     * @param expr           the expression to evaluate
     * @param definedMembers map of already defined enum member names to their values
     * @return the evaluated integer value
     * @throws Swc4jByteCodeCompilerException if the expression cannot be evaluated at compile time
     */
    private int evaluateConstantExpression(ISwc4jAstExpr expr, Map<String, Integer> definedMembers)
            throws Swc4jByteCodeCompilerException {
        if (expr instanceof Swc4jAstNumber numLit) {
            return parseIntValue(numLit);
        } else if (expr instanceof Swc4jAstIdent ident) {
            // Reference to another enum member
            String refName = ident.getSym();
            if (definedMembers.containsKey(refName)) {
                return definedMembers.get(refName);
            }
            throw new Swc4jByteCodeCompilerException(getSourceCode(), ident,
                    "Cannot reference enum member '" + refName + "' before it is defined. " +
                            "Enum members can only reference previously defined members.");
        } else if (expr instanceof Swc4jAstParenExpr parenExpr) {
            return evaluateConstantExpression(parenExpr.getExpr(), definedMembers);
        } else if (expr instanceof Swc4jAstUnaryExpr unaryExpr) {
            return evaluateUnaryExpression(unaryExpr, definedMembers);
        } else if (expr instanceof Swc4jAstBinExpr binExpr) {
            return evaluateBinaryExpression(binExpr, definedMembers);
        } else {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), expr,
                    "Unsupported expression type in enum initializer: " + expr.getClass().getSimpleName() +
                            ". Only constant expressions (literals, arithmetic, bitwise, and references to previous members) are supported.");
        }
    }

    private int evaluateUnaryExpression(Swc4jAstUnaryExpr unaryExpr, Map<String, Integer> definedMembers)
            throws Swc4jByteCodeCompilerException {
        Swc4jAstUnaryOp op = unaryExpr.getOp();
        int operand = evaluateConstantExpression(unaryExpr.getArg(), definedMembers);

        return switch (op) {
            case Minus -> -operand;
            case Plus -> operand;
            case Tilde -> ~operand;
            default -> throw new Swc4jByteCodeCompilerException(getSourceCode(), unaryExpr,
                    "Unsupported unary operator '" + op.getName() + "' in enum initializer. " +
                            "Only -, +, and ~ are supported.");
        };
    }

    @Override
    public void generate(CodeBuilder code, ClassWriter classWriter, Swc4jAstTsEnumDecl tsEnumDecl, ReturnTypeInfo returnTypeInfo) throws Swc4jByteCodeCompilerException {
        String currentPackage = compiler.getMemory().getScopedPackage().getCurrentPackage();
        String enumName = tsEnumDecl.getId().getSym();
        String fullClassName = currentPackage.isEmpty() ? enumName : currentPackage + "." + enumName;
        String internalClassName = fullClassName.replace('.', '/');

        try {
            byte[] bytecode = generateBytecode(internalClassName, tsEnumDecl);
            if (bytecode != null) {  // null means ambient declaration
                compiler.getMemory().getByteCodeMap().put(fullClassName, bytecode);
            }
        } catch (IOException e) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), tsEnumDecl, "Failed to generate bytecode for enum: " + fullClassName, e);
        }
    }

    private byte[] generateBytecode(
            String internalClassName,
            Swc4jAstTsEnumDecl enumDecl) throws IOException, Swc4jByteCodeCompilerException {
        // Check if ambient declaration (no bytecode generation)
        if (enumDecl.isDeclare()) {
            return null;
        }

        List<Swc4jAstTsEnumMember> members = enumDecl.getMembers();
        if (members.isEmpty()) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), enumDecl, "Empty enums are not supported");
        }

        // Analyze enum members to determine type
        EnumInfo enumInfo = analyzeEnumMembers(members);

        // Create ClassWriter with java.lang.Enum as superclass
        ClassWriter classWriter = new ClassWriter(internalClassName, ConstantJavaType.JAVA_LANG_ENUM);
        ClassWriter.ConstantPool cp = classWriter.getConstantPool();

        // Set enum class flags: ACC_PUBLIC | ACC_FINAL | ACC_SUPER | ACC_ENUM
        classWriter.setAccessFlags(0x0001 | 0x0010 | 0x0020 | 0x4000);

        // Generate enum constant fields
        for (EnumMemberInfo memberInfo : enumInfo.members) {
            // public static final EnumType CONSTANT_NAME
            String fieldDescriptor = "L" + internalClassName + ";";
            classWriter.addField(
                    0x0001 | 0x0008 | 0x0010, // ACC_PUBLIC | ACC_STATIC | ACC_FINAL
                    memberInfo.javaName,
                    fieldDescriptor
            );
        }

        // Generate value field (private final int/String value)
        String valueFieldDescriptor = enumInfo.isStringEnum ? ConstantJavaType.LJAVA_LANG_STRING : ConstantJavaType.ABBR_INTEGER;
        classWriter.addField(
                0x0002 | 0x0010, // ACC_PRIVATE | ACC_FINAL
                "value",
                valueFieldDescriptor
        );

        // Generate $VALUES array field (private static final EnumType[] $VALUES)
        String arrayDescriptor = "[L" + internalClassName + ";";
        classWriter.addField(
                0x0002 | 0x0008 | 0x0010, // ACC_PRIVATE | ACC_STATIC | ACC_FINAL
                "$VALUES",
                arrayDescriptor
        );

        // Generate private constructor
        generateConstructor(classWriter, internalClassName, enumInfo.isStringEnum);

        // Generate static initializer <clinit>
        generateStaticInitializer(classWriter, internalClassName, enumInfo);

        // Generate values() method
        generateValuesMethod(classWriter, internalClassName);

        // Generate valueOf(String) method
        generateValueOfMethod(classWriter, internalClassName);

        // Generate getValue() method
        generateGetValueMethod(classWriter, internalClassName, enumInfo.isStringEnum);

        // Generate fromValue() method
        generateFromValueMethod(classWriter, internalClassName, enumInfo);

        return classWriter.toByteArray();
    }

    private void generateConstructor(
            ClassWriter classWriter,
            String internalClassName,
            boolean isStringEnum) {
        var cp = classWriter.getConstantPool();
        // Generate: private EnumType(String name, int ordinal, <type> value)
        String descriptor = isStringEnum ? "(Ljava/lang/String;ILjava/lang/String;)V" : "(Ljava/lang/String;II)V";
        int superCtorRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_ENUM, ConstantJavaMethod.METHOD_INIT, "(Ljava/lang/String;I)V");

        CodeBuilder code = new CodeBuilder();
        code.aload(0)           // load this
                .aload(1)           // load name (String)
                .iload(2)           // load ordinal (int)
                .invokespecial(superCtorRef) // call super(name, ordinal)
                .aload(0)           // load this
                .iload(3);          // load value (int or String - both at slot 3)

        if (isStringEnum) {
            // For string enum: aload instead of iload at slot 3
            code = new CodeBuilder();
            code.aload(0)       // load this
                    .aload(1)       // load name (String)
                    .iload(2)       // load ordinal (int)
                    .invokespecial(superCtorRef) // call super(name, ordinal)
                    .aload(0)       // load this
                    .aload(3);      // load value (String at slot 3)
        }

        String valueFieldDescriptor = isStringEnum ? ConstantJavaType.LJAVA_LANG_STRING : ConstantJavaType.ABBR_INTEGER;
        int valueFieldRef = cp.addFieldRef(internalClassName, "value", valueFieldDescriptor);

        code.putfield(valueFieldRef) // this.value = value
                .returnVoid();

        classWriter.addMethod(
                0x0002, // ACC_PRIVATE
                ConstantJavaMethod.METHOD_INIT,
                descriptor,
                code.toByteArray(),
                4, // max stack
                4  // max locals (this + String + int + int/String)
        );
    }

    private void generateFromValueMethod(
            ClassWriter classWriter,
            String internalClassName,
            EnumInfo enumInfo) {
        var cp = classWriter.getConstantPool();
        // Generate: public static EnumType fromValue(<type> value)
        String valueType = enumInfo.isStringEnum ? ConstantJavaType.LJAVA_LANG_STRING : ConstantJavaType.ABBR_INTEGER;
        String enumDescriptor = "L" + internalClassName + ";";
        String methodDescriptor = "(" + valueType + ")" + enumDescriptor;

        int valuesRef = cp.addMethodRef(internalClassName, "values", "()[L" + internalClassName + ";");
        int getValueRef = cp.addMethodRef(internalClassName, ConstantJavaMethod.METHOD_GET_VALUE, "()" + valueType);
        int exceptionClassRef = cp.addClass(ConstantJavaType.JAVA_LANG_ILLEGALARGUMENTEXCEPTION);
        int exceptionCtorRef = cp.addMethodRef(
                ConstantJavaType.JAVA_LANG_ILLEGALARGUMENTEXCEPTION,
                ConstantJavaMethod.METHOD_INIT,
                "(Ljava/lang/String;)V");

        CodeBuilder code = new CodeBuilder();
        List<ClassWriter.StackMapEntry> stackMapTable = new ArrayList<>();

        // EnumType[] arr = values();
        code.invokestatic(valuesRef)    // call values()
                .astore(1);                 // store in local var 1 (arr)

        // for (EnumType e : arr)
        code.aload(1)                   // load arr
                .arraylength()              // get length
                .istore(2);                 // store length in var 2

        code.iconst(0)                  // i = 0
                .istore(3);                 // store in var 3

        int loopStart = code.getOffset();

        // Add stack map frame at loop start
        // Frame type: 255 = FULL_FRAME
        // Locals: [value, array, int length, int i]
        // Stack: []
        String arrayClassName = "[L" + internalClassName + ";";
        stackMapTable.add(new ClassWriter.StackMapEntry(
                loopStart,
                255, // FULL_FRAME
                enumInfo.isStringEnum
                        // [Object (String), Object (array), int, int]
                        ? List.of(7, 7, 1, 1)
                        // [int, Object (array), int, int]
                        : List.of(1, 7, 1, 1),
                List.of(), // empty stack
                enumInfo.isStringEnum
                        ? List.of(ConstantJavaType.JAVA_LANG_STRING, arrayClassName)  // class names for the two Object types
                        : List.of(arrayClassName),  // class name for the one Object type
                null  // no stack class names
        ));

        // Loop condition: if (i >= length) goto loopEnd
        code.iload(3)                   // load i
                .iload(2)                   // load length
                .if_icmpge(0);              // if i >= length, jump (will be patched)

        int loopEndJump = code.getOffset() - 2; // remember jump offset for patching

        // EnumType e = arr[i];
        code.aload(1)                   // load arr
                .iload(3)                   // load i
                .aaload()                   // arr[i]
                .astore(4);                 // store in var 4 (e)

        // if (e.getValue() == value) return e;
        code.aload(4)                   // load e
                .invokevirtual(getValueRef); // e.getValue()

        int continueTarget; // save for later use in loopEnd frame calculation
        if (enumInfo.isStringEnum) {
            // String comparison: if (e.getValue().equals(value))
            int equalsRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRING, ConstantJavaMethod.METHOD_EQUALS, ConstantJavaDescriptor.DESCRIPTOR_LJAVA_LANG_OBJECT__Z);
            code.aload(0)               // load value parameter
                    .invokevirtual(equalsRef) // call equals()
                    .ifeq(0);               // if false, continue loop

            int continueJump1 = code.getOffset() - 2;

            code.aload(4)               // load e
                    .areturn();             // return e

            // Patch continue jump
            continueTarget = code.getOffset();
            code.patchShort(continueJump1, continueTarget - continueJump1 + 1);

            // Add stack map frame at continue target (branch target)
            // Locals: [String value, array, int length, int i, enum e]
            // Stack: []
            stackMapTable.add(new ClassWriter.StackMapEntry(
                    continueTarget - loopStart - 1, // offset_delta
                    255, // FULL_FRAME
                    List.of(7, 7, 1, 1, 7), // [Object, Object, int, int, Object]
                    List.of(), // empty stack
                    List.of(ConstantJavaType.JAVA_LANG_STRING, arrayClassName, internalClassName), // class names for the three Object types
                    null // no stack class names
            ));
        } else {
            // int comparison: if (e.getValue() == value)
            code.iload(0)               // load value parameter
                    .if_icmpne(0);          // if not equal, continue loop

            int continueJump2 = code.getOffset() - 2;

            code.aload(4)               // load e
                    .areturn();             // return e

            // Patch continue jump
            continueTarget = code.getOffset();
            code.patchShort(continueJump2, continueTarget - continueJump2 + 1);

            // Add stack map frame at continue target (branch target)
            // Locals: [int value, array, int length, int i, enum e]
            // Stack: []
            stackMapTable.add(new ClassWriter.StackMapEntry(
                    continueTarget - loopStart - 1, // offset_delta
                    255, // FULL_FRAME
                    List.of(1, 7, 1, 1, 7), // [int, Object, int, int, Object]
                    List.of(), // empty stack
                    List.of(arrayClassName, internalClassName), // class names for the two Object types
                    null // no stack class names
            ));
        }

        // i++
        code.iinc(3, 1)                 // i++
                .goto_(loopStart);          // goto loop start

        // Loop end
        int loopEnd = code.getOffset();
        code.patchShort(loopEndJump, loopEnd - loopEndJump + 1);

        // Add stack map frame at loop end (branch target)
        // Frame type: 255 = FULL_FRAME
        // Locals: [value, array, int length, int i]
        // Stack: []
        // offset_delta = loopEnd - continueTarget - 1
        stackMapTable.add(new ClassWriter.StackMapEntry(
                loopEnd - continueTarget - 1, // offset_delta from previous frame
                255, // FULL_FRAME
                enumInfo.isStringEnum
                        // [Object (String), Object (array), int, int]
                        ? List.of(7, 7, 1, 1)
                        // [int, Object (array), int, int]
                        : List.of(1, 7, 1, 1),
                List.of(), // empty stack
                enumInfo.isStringEnum
                        ? List.of(ConstantJavaType.JAVA_LANG_STRING, arrayClassName)  // class names for the two Object types
                        : List.of(arrayClassName),  // class name for the one Object type
                null  // no stack class names
        ));

        // throw new IllegalArgumentException("Invalid value: " + value)
        code.newInstance(exceptionClassRef) // new IllegalArgumentException
                .dup();                     // duplicate

        // Build error message
        int sbClassRef = cp.addClass(ConstantJavaType.JAVA_LANG_STRINGBUILDER);
        int sbCtorRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_INIT, ConstantJavaDescriptor.DESCRIPTOR___V);
        int sbAppendStringRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_APPEND, "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        int sbAppendIntRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_APPEND, ConstantJavaDescriptor.DESCRIPTOR_I__LJAVA_LANG_STRINGBUILDER);
        int sbToStringRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_STRINGBUILDER, ConstantJavaMethod.METHOD_TO_STRING, ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_STRING);

        code.newInstance(sbClassRef)    // new StringBuilder
                .dup()                      // duplicate
                .invokespecial(sbCtorRef)   // call StringBuilder()
                .ldc(cp.addString("Invalid value: ")) // load prefix
                .invokevirtual(sbAppendStringRef); // append prefix

        if (enumInfo.isStringEnum) {
            code.aload(0)               // load value
                    .invokevirtual(sbAppendStringRef); // append value
        } else {
            code.iload(0)               // load value
                    .invokevirtual(sbAppendIntRef); // append value
        }

        code.invokevirtual(sbToStringRef) // toString()
                .invokespecial(exceptionCtorRef) // call exception constructor
                .athrow();                  // throw exception

        classWriter.addMethod(
                0x0001 | 0x0008, // ACC_PUBLIC | ACC_STATIC
                "fromValue",
                methodDescriptor,
                code.toByteArray(),
                5, // max stack
                5, // max locals (value, arr, length, i, e)
                null, // line number table
                null, // local variable table
                stackMapTable // stack map table
        );
    }

    private void generateGetValueMethod(
            ClassWriter classWriter,
            String internalClassName,
            boolean isStringEnum) {
        var cp = classWriter.getConstantPool();
        // Generate: public <type> getValue()
        String valueFieldDescriptor = isStringEnum ? ConstantJavaType.LJAVA_LANG_STRING : ConstantJavaType.ABBR_INTEGER;
        String methodDescriptor = "()" + valueFieldDescriptor;
        int valueFieldRef = cp.addFieldRef(internalClassName, "value", valueFieldDescriptor);

        CodeBuilder code = new CodeBuilder();
        code.aload(0)                   // load this
                .getfield(valueFieldRef);   // get this.value

        if (isStringEnum) {
            code.areturn();             // return String
        } else {
            code.ireturn();             // return int
        }

        classWriter.addMethod(
                0x0001, // ACC_PUBLIC
                ConstantJavaMethod.METHOD_GET_VALUE,
                methodDescriptor,
                code.toByteArray(),
                1, // max stack
                1  // max locals (this)
        );
    }

    private void generateStaticInitializer(
            ClassWriter classWriter,
            String internalClassName,
            EnumInfo enumInfo) {
        var cp = classWriter.getConstantPool();
        // Generate: static { ... }
        CodeBuilder code = new CodeBuilder();
        int enumClassRef = cp.addClass(internalClassName);
        String enumDescriptor = "L" + internalClassName + ";";
        String ctorDescriptor = enumInfo.isStringEnum ? "(Ljava/lang/String;ILjava/lang/String;)V" : "(Ljava/lang/String;II)V";
        int ctorRef = cp.addMethodRef(internalClassName, ConstantJavaMethod.METHOD_INIT, ctorDescriptor);

        // Create each enum constant instance
        int ordinal = 0;
        for (EnumMemberInfo memberInfo : enumInfo.members) {
            int fieldRef = cp.addFieldRef(internalClassName, memberInfo.javaName, enumDescriptor);

            code.newInstance(enumClassRef)  // new EnumType
                    .dup()                      // duplicate reference
                    .ldc(cp.addString(memberInfo.javaName)); // load name

            // Load ordinal
            if (ordinal <= 5) {
                code.iconst(ordinal);
            } else if (ordinal <= 127) {
                code.bipush(ordinal);
            } else if (ordinal <= 32767) {
                code.sipush(ordinal);
            } else {
                code.ldc(cp.addInteger(ordinal));
            }

            // Load value
            if (enumInfo.isStringEnum) {
                code.ldc(cp.addString(memberInfo.stringValue));
            } else {
                int value = memberInfo.intValue;
                if (value >= -1 && value <= 5) {
                    code.iconst(value);
                } else if (value >= -128 && value <= 127) {
                    code.bipush(value);
                } else if (value >= -32768 && value <= 32767) {
                    code.sipush(value);
                } else {
                    code.ldc(cp.addInteger(value));
                }
            }

            code.invokespecial(ctorRef)     // call constructor
                    .putstatic(fieldRef);       // EnumType.CONSTANT = instance

            ordinal++;
        }

        // Initialize $VALUES array
        int arrayFieldRef = cp.addFieldRef(internalClassName, "$VALUES", "[L" + internalClassName + ";");
        int arraySize = enumInfo.members.size();

        if (arraySize <= 5) {
            code.iconst(arraySize);
        } else if (arraySize <= 127) {
            code.bipush(arraySize);
        } else if (arraySize <= 32767) {
            code.sipush(arraySize);
        } else {
            code.ldc(cp.addInteger(arraySize));
        }

        code.anewarray(enumClassRef); // create array

        // Fill array with enum constants
        for (int i = 0; i < enumInfo.members.size(); i++) {
            EnumMemberInfo memberInfo = enumInfo.members.get(i);
            int fieldRef = cp.addFieldRef(internalClassName, memberInfo.javaName, enumDescriptor);

            code.dup(); // duplicate array reference

            if (i <= 5) {
                code.iconst(i);
            } else if (i <= 127) {
                code.bipush(i);
            } else if (i <= 32767) {
                code.sipush(i);
            } else {
                code.ldc(cp.addInteger(i));
            }

            code.getstatic(fieldRef)    // load enum constant
                    .aastore();             // array[i] = constant
        }

        code.putstatic(arrayFieldRef)   // $VALUES = array
                .returnVoid();

        // Calculate max stack:
        // - Constructor call: newInstance + dup + name + ordinal + value = 5
        // - Array creation and filling: max(arraySize + 2, 3)
        // Total max stack needed
        int maxStack = Math.max(5, Math.max(arraySize + 2, 3));

        classWriter.addMethod(
                0x0008, // ACC_STATIC
                "<clinit>",
                ConstantJavaDescriptor.DESCRIPTOR___V,
                code.toByteArray(),
                maxStack,
                0  // max locals (static method)
        );
    }

    private void generateValueOfMethod(
            ClassWriter classWriter,
            String internalClassName) {
        var cp = classWriter.getConstantPool();
        // Generate: public static EnumType valueOf(String name)
        int enumClassRef = cp.addClass(internalClassName);
        String enumDescriptor = "L" + internalClassName + ";";
        int valueOfRef = cp.addMethodRef(
                ConstantJavaType.JAVA_LANG_ENUM,
                ConstantJavaMethod.METHOD_VALUE_OF,
                "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;");

        CodeBuilder code = new CodeBuilder();
        code.ldc(enumClassRef)          // load EnumType.class
                .aload(0)                   // load name parameter
                .invokestatic(valueOfRef)   // call Enum.valueOf()
                .checkcast(enumClassRef)    // cast to EnumType
                .areturn();                 // return value

        classWriter.addMethod(
                0x0001 | 0x0008, // ACC_PUBLIC | ACC_STATIC
                ConstantJavaMethod.METHOD_VALUE_OF,
                "(Ljava/lang/String;)" + enumDescriptor,
                code.toByteArray(),
                2, // max stack
                1  // max locals (name parameter)
        );
    }

    private void generateValuesMethod(
            ClassWriter classWriter,
            String internalClassName) {
        var cp = classWriter.getConstantPool();
        // Generate: public static EnumType[] values()
        String arrayDescriptor = "[L" + internalClassName + ";";
        int arrayFieldRef = cp.addFieldRef(internalClassName, "$VALUES", arrayDescriptor);
        int cloneRef = cp.addMethodRef(ConstantJavaType.JAVA_LANG_OBJECT, "clone", ConstantJavaDescriptor.DESCRIPTOR___LJAVA_LANG_OBJECT);

        CodeBuilder code = new CodeBuilder();
        code.getstatic(arrayFieldRef)   // load $VALUES
                .invokevirtual(cloneRef)    // call clone()
                .checkcast(cp.addClass("[L" + internalClassName + ";")) // cast to EnumType[]
                .areturn();                 // return array

        classWriter.addMethod(
                0x0001 | 0x0008, // ACC_PUBLIC | ACC_STATIC
                "values",
                "()" + arrayDescriptor,
                code.toByteArray(),
                2, // max stack
                0  // max locals
        );
    }

    private int parseIntValue(Swc4jAstNumber numLit) throws Swc4jByteCodeCompilerException {
        String raw = numLit.getRaw().orElse("0");
        try {
            // Handle different number formats
            if (raw.startsWith("0x") || raw.startsWith("0X")) {
                return Integer.parseInt(raw.substring(2), 16);
            } else if (raw.startsWith("0o") || raw.startsWith("0O")) {
                return Integer.parseInt(raw.substring(2), 8);
            } else if (raw.startsWith("0b") || raw.startsWith("0B")) {
                return Integer.parseInt(raw.substring(2), 2);
            } else {
                // Check for floating point
                if (raw.contains(".") || raw.contains("e") || raw.contains("E")) {
                    throw new Swc4jByteCodeCompilerException(getSourceCode(), numLit,
                            "Floating-point enum values are not supported. Enum values must be integers or strings.");
                }
                return Integer.parseInt(raw);
            }
        } catch (NumberFormatException e) {
            throw new Swc4jByteCodeCompilerException(getSourceCode(), numLit, "Invalid enum numeric value: " + raw, e);
        }
    }

    private record EnumInfo(List<EnumMemberInfo> members, boolean isStringEnum) {
    }

    private record EnumMemberInfo(String tsName, String javaName, int intValue, String stringValue) {
    }
}
