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

package com.caoccao.javet.swc4j.compiler.asm;


import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to build method bytecode.
 */
public class CodeBuilder {
    private final List<Byte> code = new ArrayList<>(4096);
    private final List<ClassWriter.ExceptionTableEntry> exceptionTable = new ArrayList<>();
    private final List<ClassWriter.LineNumberEntry> lineNumbers = new ArrayList<>(1024);
    private int currentLine = -1;

    /**
     * Constructs a new CodeBuilder.
     */
    public CodeBuilder() {
    }

    /**
     * Loads object reference from array (aaload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder aaload() {
        code.add((byte) (0x32)); // aaload
        return this;
    }

    /**
     * Stores object reference into array (aastore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder aastore() {
        code.add((byte) (0x53)); // aastore
        return this;
    }

    /**
     * Pushes null onto the stack (aconst_null).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder aconst_null() {
        code.add((byte) (0x01)); // aconst_null
        return this;
    }

    /**
     * Add an exception handler entry.
     *
     * @param startPc   start of the try block (inclusive)
     * @param endPc     end of the try block (exclusive)
     * @param handlerPc start of the exception handler
     * @param catchType constant pool index of the exception class, or 0 for any exception
     */
    public void addExceptionHandler(int startPc, int endPc, int handlerPc, int catchType) {
        exceptionTable.add(new ClassWriter.ExceptionTableEntry(startPc, endPc, handlerPc, catchType));
    }

    /**
     * Loads object reference from local variable (aload).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder aload(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x2A)); // aload_0
            case 1 -> code.add((byte) (0x2B)); // aload_1
            case 2 -> code.add((byte) (0x2C)); // aload_2
            case 3 -> code.add((byte) (0x2D)); // aload_3
            default -> {
                code.add((byte) (0x19)); // aload
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Creates new array of reference type (anewarray).
     *
     * @param classRefIndex constant pool index of the class
     * @return this CodeBuilder
     */
    public CodeBuilder anewarray(int classRefIndex) {
        code.add((byte) (0xBD)); // anewarray
        writeShort(classRefIndex);
        return this;
    }

    /**
     * Append bytes from another code segment.
     *
     * @param bytes the bytes to append
     * @return this CodeBuilder
     */
    public CodeBuilder append(byte[] bytes) {
        for (byte b : bytes) {
            code.add(b);
        }
        return this;
    }

    /**
     * Returns object reference from method (areturn).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder areturn() {
        code.add((byte) (0xB0)); // areturn
        return this;
    }

    /**
     * Gets length of array (arraylength).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder arraylength() {
        code.add((byte) (0xBE)); // arraylength
        return this;
    }

    /**
     * Stores object reference into local variable (astore).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder astore(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x4B));
            // astore_0
            case 1 -> code.add((byte) (0x4C));
            // astore_1
            case 2 -> code.add((byte) (0x4D));
            // astore_2
            case 3 -> code.add((byte) (0x4E));
            // astore_3
            default -> {
                code.add((byte) (0x3A)); // astore
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Throws exception (athrow).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder athrow() {
        code.add((byte) (0xBF)); // athrow
        return this;
    }

    /**
     * Loads byte or boolean from array (baload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder baload() {
        code.add((byte) (0x33)); // baload
        return this;
    }

    /**
     * Stores byte or boolean into array (bastore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder bastore() {
        code.add((byte) (0x54)); // bastore
        return this;
    }

    /**
     * Pushes byte value onto stack (bipush).
     *
     * @param value the byte value (-128 to 127)
     * @return this CodeBuilder
     */
    public CodeBuilder bipush(int value) {
        if (value < -128 || value > 127) {
            throw new IllegalArgumentException("bipush value must be in range -128 to 127: " + value);
        }
        code.add((byte) (0x10)); // bipush
        code.add((byte) (value));
        return this;
    }

    /**
     * Loads char from array (caload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder caload() {
        code.add((byte) (0x34)); // caload
        return this;
    }

    /**
     * Stores char into array (castore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder castore() {
        code.add((byte) (0x55)); // castore
        return this;
    }

    /**
     * Checks cast of object reference (checkcast).
     *
     * @param classRefIndex constant pool index of the class
     * @return this CodeBuilder
     */
    public CodeBuilder checkcast(int classRefIndex) {
        code.add((byte) (0xC0)); // checkcast
        code.add((byte) (classRefIndex >> 8)); // high byte
        code.add((byte) (classRefIndex & 0xFF)); // low byte
        return this;
    }

    /**
     * Converts double to float (d2f).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder d2f() {
        code.add((byte) (0x90)); // d2f
        return this;
    }

    /**
     * Converts double to int (d2i).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder d2i() {
        code.add((byte) (0x8E)); // d2i
        return this;
    }

    /**
     * Converts double to long (d2l).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder d2l() {
        code.add((byte) (0x8F)); // d2l
        return this;
    }

    /**
     * Adds double (dadd).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dadd() {
        code.add((byte) (0x63)); // dadd
        return this;
    }

    /**
     * Loads double from array (daload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder daload() {
        code.add((byte) (0x31)); // daload
        return this;
    }

    /**
     * Stores double into array (dastore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dastore() {
        code.add((byte) (0x52)); // dastore
        return this;
    }

    /**
     * Compares double (dcmpl).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dcmpl() {
        code.add((byte) (0x97)); // dcmpl
        return this;
    }

    /**
     * Pushes double constant onto stack (dconst).
     *
     * @param value the double value
     * @return this CodeBuilder
     */
    public CodeBuilder dconst(double value) {
        if (value == 0.0) {
            code.add((byte) (0x0E)); // dconst_0
        } else if (value == 1.0) {
            code.add((byte) (0x0F)); // dconst_1
        } else {
            throw new IllegalArgumentException("Double value " + value + " requires ldc2_w, use ldc2_w() instead");
        }
        return this;
    }

    /**
     * Divides double (ddiv).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ddiv() {
        code.add((byte) (0x6F)); // ddiv
        return this;
    }

    /**
     * Loads double from local variable (dload).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder dload(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x26)); // dload_0
            case 1 -> code.add((byte) (0x27)); // dload_1
            case 2 -> code.add((byte) (0x28)); // dload_2
            case 3 -> code.add((byte) (0x29)); // dload_3
            default -> {
                code.add((byte) (0x18)); // dload
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Multiplies double (dmul).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dmul() {
        code.add((byte) (0x6B)); // dmul
        return this;
    }

    /**
     * Negates double (dneg).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dneg() {
        code.add((byte) (0x77)); // dneg
        return this;
    }

    /**
     * Remainder of double division (drem).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder drem() {
        code.add((byte) (0x73)); // drem
        return this;
    }

    /**
     * Returns double from method (dreturn).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dreturn() {
        code.add((byte) (0xAF)); // dreturn
        return this;
    }

    /**
     * Stores double into local variable (dstore).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder dstore(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x47)); // dstore_0
            case 1 -> code.add((byte) (0x48)); // dstore_1
            case 2 -> code.add((byte) (0x49)); // dstore_2
            case 3 -> code.add((byte) (0x4A)); // dstore_3
            default -> {
                code.add((byte) (0x39)); // dstore
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Subtracts double (dsub).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dsub() {
        code.add((byte) (0x67)); // dsub
        return this;
    }

    /**
     * Duplicates top value on stack (dup).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dup() {
        code.add((byte) (0x59)); // dup
        return this;
    }

    /**
     * Duplicates top two values on stack (dup2).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dup2() {
        code.add((byte) (0x5C)); // dup2
        return this;
    }

    /**
     * Duplicates top two values and inserts below third value (dup2_x1).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dup2_x1() {
        code.add((byte) (0x5D)); // dup2_x1
        return this;
    }

    /**
     * Duplicates top two values and inserts below fourth value (dup2_x2).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dup2_x2() {
        code.add((byte) (0x5E)); // dup2_x2
        return this;
    }

    /**
     * Duplicates top value and inserts below second value (dup_x1).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dup_x1() {
        code.add((byte) (0x5A)); // dup_x1
        return this;
    }

    /**
     * Duplicates top value and inserts below third value (dup_x2).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder dup_x2() {
        code.add((byte) (0x5B)); // dup_x2
        return this;
    }

    /**
     * Emit a raw byte (for placeholders).
     *
     * @param value the byte value
     * @return this CodeBuilder
     */
    public CodeBuilder emitByte(int value) {
        code.add((byte) value);
        return this;
    }

    /**
     * Converts float to double (f2d).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder f2d() {
        code.add((byte) (0x8D)); // f2d
        return this;
    }

    /**
     * Converts float to int (f2i).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder f2i() {
        code.add((byte) (0x8B)); // f2i
        return this;
    }

    /**
     * Converts float to long (f2l).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder f2l() {
        code.add((byte) (0x8C)); // f2l
        return this;
    }

    /**
     * Adds float (fadd).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder fadd() {
        code.add((byte) (0x62)); // fadd
        return this;
    }

    /**
     * Loads float from array (faload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder faload() {
        code.add((byte) (0x30)); // faload
        return this;
    }

    /**
     * Stores float into array (fastore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder fastore() {
        code.add((byte) (0x51)); // fastore
        return this;
    }

    /**
     * Compares float (fcmpl).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder fcmpl() {
        code.add((byte) (0x95)); // fcmpl
        return this;
    }

    /**
     * Pushes float constant onto stack (fconst).
     *
     * @param value the float value (0.0, 1.0, or 2.0)
     * @return this CodeBuilder
     */
    public CodeBuilder fconst(float value) {
        if (value == 0.0f) {
            code.add((byte) (0x0B)); // fconst_0
        } else if (value == 1.0f) {
            code.add((byte) (0x0C)); // fconst_1
        } else if (value == 2.0f) {
            code.add((byte) (0x0D)); // fconst_2
        } else {
            throw new IllegalArgumentException("Float value " + value + " requires ldc");
        }
        return this;
    }

    /**
     * Divides float (fdiv).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder fdiv() {
        code.add((byte) (0x6E)); // fdiv
        return this;
    }

    /**
     * Loads float from local variable (fload).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder fload(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x22));
            // fload_0
            case 1 -> code.add((byte) (0x23));
            // fload_1
            case 2 -> code.add((byte) (0x24));
            // fload_2
            case 3 -> code.add((byte) (0x25));
            // fload_3
            default -> {
                code.add((byte) (0x17)); // fload
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Multiplies float (fmul).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder fmul() {
        code.add((byte) (0x6A)); // fmul
        return this;
    }

    /**
     * Negates float (fneg).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder fneg() {
        code.add((byte) (0x76)); // fneg
        return this;
    }

    /**
     * Remainder of float division (frem).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder frem() {
        code.add((byte) (0x72)); // frem
        return this;
    }

    /**
     * Returns float from method (freturn).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder freturn() {
        code.add((byte) (0xAE)); // freturn
        return this;
    }

    /**
     * Stores float into local variable (fstore).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder fstore(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x43));
            // fstore_0
            case 1 -> code.add((byte) (0x44));
            // fstore_1
            case 2 -> code.add((byte) (0x45));
            // fstore_2
            case 3 -> code.add((byte) (0x46));
            // fstore_3
            default -> {
                code.add((byte) (0x38)); // fstore
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Subtracts float (fsub).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder fsub() {
        code.add((byte) (0x66)); // fsub
        return this;
    }

    /**
     * Generates StackMapTable entries for methods with branch instructions.
     * Uses bytecode analysis to compute verification frames.
     *
     * @param maxLocals    the maximum number of local variables
     * @param isStatic     whether the method is static
     * @param className    the class name for 'this' reference type
     * @param descriptor   the method descriptor
     * @param constantPool the constant pool for resolving types
     * @return list of stack map entries, or null if not needed
     */
    public List<ClassWriter.StackMapEntry> generateStackMapTable(int maxLocals, boolean isStatic, String className, String descriptor, ClassWriter.ConstantPool constantPool) {
        byte[] bytecode = toByteArray();
        StackMapProcessor generator = new StackMapProcessor(bytecode, maxLocals, isStatic, className, descriptor, constantPool, exceptionTable);
        return generator.generate();
    }

    /**
     * Get a copy of bytes from start (inclusive) to end (exclusive).
     *
     * @param start start offset
     * @param end   end offset
     * @return byte array
     */
    public byte[] getBytes(int start, int end) {
        if (start < 0 || end > code.size() || start > end) {
            throw new IllegalArgumentException("Invalid range: " + start + " to " + end);
        }
        byte[] bytes = new byte[end - start];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = code.get(start + i);
        }
        return bytes;
    }

    /**
     * Gets the current bytecode offset.
     *
     * @return the current offset
     */
    public int getCurrentOffset() {
        return code.size();
    }

    /**
     * Get the exception table entries for this code builder.
     *
     * @return the list of exception table entries
     */
    public List<ClassWriter.ExceptionTableEntry> getExceptionTable() {
        return exceptionTable;
    }

    /**
     * Gets the line number table entries.
     *
     * @return the list of line number entries
     */
    public List<ClassWriter.LineNumberEntry> getLineNumbers() {
        return lineNumbers;
    }

    /**
     * Gets the current bytecode offset.
     *
     * @return the current offset
     */
    public int getOffset() {
        return code.size();
    }

    /**
     * Gets instance field value (getfield).
     *
     * @param fieldRefIndex constant pool index of the field reference
     * @return this CodeBuilder
     */
    public CodeBuilder getfield(int fieldRefIndex) {
        code.add((byte) (0xB4)); // getfield
        writeShort(fieldRefIndex);
        return this;
    }

    /**
     * Gets static field value (getstatic).
     *
     * @param fieldRefIndex constant pool index of the field reference
     * @return this CodeBuilder
     */
    public CodeBuilder getstatic(int fieldRefIndex) {
        code.add((byte) (0xB2)); // getstatic
        writeShort(fieldRefIndex);
        return this;
    }

    /**
     * Unconditional jump with label offset (goto).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder gotoLabel(int offset) {
        code.add((byte) (0xA7)); // goto
        writeShort(offset);
        return this;
    }

    /**
     * Unconditional jump to target offset (goto).
     *
     * @param targetOffset the absolute target offset
     * @return this CodeBuilder
     */
    public CodeBuilder goto_(int targetOffset) {
        int gotoOffset = getOffset(); // save offset before adding opcode
        code.add((byte) (0xA7)); // goto
        writeShort(targetOffset - gotoOffset); // calculate offset from goto opcode position
        return this;
    }

    /**
     * Wide unconditional jump (goto_w).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder goto_w(int offset) {
        code.add((byte) (0xC8)); // goto_w
        writeInt(offset);
        return this;
    }

    /**
     * Converts int to byte (i2b).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder i2b() {
        code.add((byte) (0x91)); // i2b
        return this;
    }

    /**
     * Converts int to char (i2c).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder i2c() {
        code.add((byte) (0x92)); // i2c
        return this;
    }

    /**
     * Converts int to double (i2d).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder i2d() {
        code.add((byte) (0x87)); // i2d
        return this;
    }

    /**
     * Converts int to float (i2f).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder i2f() {
        code.add((byte) (0x86)); // i2f
        return this;
    }

    /**
     * Converts int to long (i2l).
     *
     * @return this CodeBuilder
     */
    // Type conversion instructions
    public CodeBuilder i2l() {
        code.add((byte) (0x85)); // i2l
        return this;
    }

    /**
     * Converts int to short (i2s).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder i2s() {
        code.add((byte) (0x93)); // i2s
        return this;
    }

    /**
     * Adds two ints (iadd).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder iadd() {
        code.add((byte) (0x60)); // iadd
        return this;
    }

    /**
     * Loads int from array (iaload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder iaload() {
        code.add((byte) (0x2E)); // iaload
        return this;
    }

    /**
     * Bitwise AND of two ints (iand).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder iand() {
        code.add((byte) (0x7E)); // iand
        return this;
    }

    /**
     * Stores int into array (iastore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder iastore() {
        code.add((byte) (0x4F)); // iastore
        return this;
    }

    /**
     * Pushes int constant onto stack (iconst/bipush/sipush).
     *
     * @param value the int value
     * @return this CodeBuilder
     */
    public CodeBuilder iconst(int value) {
        switch (value) {
            case -1 -> code.add((byte) (0x02));
            // iconst_m1
            case 0 -> code.add((byte) (0x03));
            // iconst_0
            case 1 -> code.add((byte) (0x04));
            // iconst_1
            case 2 -> code.add((byte) (0x05));
            // iconst_2
            case 3 -> code.add((byte) (0x06));
            // iconst_3
            case 4 -> code.add((byte) (0x07));
            // iconst_4
            case 5 -> code.add((byte) (0x08));
            // iconst_5
            default -> {
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                    code.add((byte) (0x10)); // bipush
                    code.add((byte) (value));
                } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                    code.add((byte) (0x11)); // sipush
                    writeShort(value);
                } else {
                    throw new IllegalArgumentException("Integer value " + value + " requires ldc");
                }
            }
        }
        return this;
    }

    /**
     * Divides two ints (idiv).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder idiv() {
        code.add((byte) (0x6C)); // idiv
        return this;
    }

    /**
     * Branch if reference comparison succeeds (if_acmpeq).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_acmpeq(int offset) {
        code.add((byte) (0xA5)); // if_acmpeq
        writeShort(offset);
        return this;
    }

    /**
     * Branch if reference comparison fails (if_acmpne).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_acmpne(int offset) {
        code.add((byte) (0xA6)); // if_acmpne
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int comparison equal (if_icmpeq).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_icmpeq(int offset) {
        code.add((byte) (0x9F)); // if_icmpeq
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int comparison greater or equal (if_icmpge).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_icmpge(int offset) {
        code.add((byte) (0xA2)); // if_icmpge
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int comparison greater than (if_icmpgt).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_icmpgt(int offset) {
        code.add((byte) (0xA3)); // if_icmpgt
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int comparison less or equal (if_icmple).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_icmple(int offset) {
        code.add((byte) (0xA4)); // if_icmple
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int comparison less than (if_icmplt).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_icmplt(int offset) {
        code.add((byte) (0xA1)); // if_icmplt
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int comparison not equal (if_icmpne).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder if_icmpne(int offset) {
        code.add((byte) (0xA0)); // if_icmpne
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int equals zero (ifeq).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder ifeq(int offset) {
        code.add((byte) (0x99)); // ifeq
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int greater or equal to zero (ifge).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder ifge(int offset) {
        code.add((byte) (0x9C)); // ifge
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int greater than zero (ifgt).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder ifgt(int offset) {
        code.add((byte) (0x9D)); // ifgt
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int less or equal to zero (ifle).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder ifle(int offset) {
        code.add((byte) (0x9E)); // ifle
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int less than zero (iflt).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder iflt(int offset) {
        code.add((byte) (0x9B)); // iflt
        writeShort(offset);
        return this;
    }

    /**
     * Branch if int not equal to zero (ifne).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder ifne(int offset) {
        code.add((byte) (0x9A)); // ifne
        writeShort(offset);
        return this;
    }

    /**
     * Branch if reference not null (ifnonnull).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder ifnonnull(int offset) {
        code.add((byte) (0xC7)); // ifnonnull
        writeShort(offset);
        return this;
    }

    /**
     * Branch if reference null (ifnull).
     *
     * @param offset the branch offset
     * @return this CodeBuilder
     */
    public CodeBuilder ifnull(int offset) {
        code.add((byte) (0xC6)); // ifnull
        writeShort(offset);
        return this;
    }

    /**
     * Increment local variable by constant.
     * Generates iinc instruction (opcode 0x84).
     *
     * @param index the local variable index (0-255)
     * @param delta the increment value (-128 to 127)
     * @return this CodeBuilder
     */
    public CodeBuilder iinc(int index, int delta) {
        code.add((byte) (0x84)); // iinc
        code.add((byte) (index));
        code.add((byte) (delta));
        return this;
    }

    /**
     * Loads int from local variable (iload).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder iload(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x1A));
            // iload_0
            case 1 -> code.add((byte) (0x1B));
            // iload_1
            case 2 -> code.add((byte) (0x1C));
            // iload_2
            case 3 -> code.add((byte) (0x1D));
            // iload_3
            default -> {
                code.add((byte) (0x15)); // iload
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Multiplies two ints (imul).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder imul() {
        code.add((byte) (0x68)); // imul
        return this;
    }

    /**
     * Negates int (ineg).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ineg() {
        code.add((byte) (0x74)); // ineg
        return this;
    }

    /**
     * Checks if object is of given type (instanceof).
     *
     * @param classRefIndex constant pool index of the class
     * @return this CodeBuilder
     */
    public CodeBuilder instanceof_(int classRefIndex) {
        code.add((byte) (0xC1)); // instanceof
        code.add((byte) (classRefIndex >> 8)); // high byte
        code.add((byte) (classRefIndex & 0xFF)); // low byte
        return this;
    }

    /**
     * Invokes interface method (invokeinterface).
     *
     * @param methodRefIndex constant pool index of the method reference
     * @param count          the number of arguments (including this)
     * @return this CodeBuilder
     */
    public CodeBuilder invokeinterface(int methodRefIndex, int count) {
        code.add((byte) (0xB9)); // invokeinterface
        writeShort(methodRefIndex);
        code.add((byte) (count)); // count parameter
        code.add((byte) (0)); // reserved byte (must be zero)
        return this;
    }

    /**
     * Invokes instance method with special handling (invokespecial).
     *
     * @param methodRefIndex constant pool index of the method reference
     * @return this CodeBuilder
     */
    public CodeBuilder invokespecial(int methodRefIndex) {
        code.add((byte) (0xB7)); // invokespecial
        writeShort(methodRefIndex);
        return this;
    }

    /**
     * Invokes static method (invokestatic).
     *
     * @param methodRefIndex constant pool index of the method reference
     * @return this CodeBuilder
     */
    public CodeBuilder invokestatic(int methodRefIndex) {
        code.add((byte) (0xB8)); // invokestatic
        writeShort(methodRefIndex);
        return this;
    }

    /**
     * Invokes instance method (invokevirtual).
     *
     * @param methodRefIndex constant pool index of the method reference
     * @return this CodeBuilder
     */
    public CodeBuilder invokevirtual(int methodRefIndex) {
        code.add((byte) (0xB6)); // invokevirtual
        writeShort(methodRefIndex);
        return this;
    }

    /**
     * Bitwise OR of two ints (ior).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ior() {
        code.add((byte) (0x80)); // ior
        return this;
    }

    /**
     * Remainder of int division (irem).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder irem() {
        code.add((byte) (0x70)); // irem
        return this;
    }

    /**
     * Returns int from method (ireturn).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ireturn() {
        code.add((byte) (0xAC)); // ireturn
        return this;
    }

    /**
     * Shifts int left (ishl).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ishl() {
        code.add((byte) (0x78)); // ishl
        return this;
    }

    /**
     * Arithmetic shift int right (ishr).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ishr() {
        code.add((byte) (0x7A)); // ishr
        return this;
    }

    /**
     * Stores int into local variable (istore).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder istore(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x3B));
            // istore_0
            case 1 -> code.add((byte) (0x3C));
            // istore_1
            case 2 -> code.add((byte) (0x3D));
            // istore_2
            case 3 -> code.add((byte) (0x3E));
            // istore_3
            default -> {
                code.add((byte) (0x36)); // istore
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Subtracts two ints (isub).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder isub() {
        code.add((byte) (0x64)); // isub
        return this;
    }

    /**
     * Logical shift int right (iushr).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder iushr() {
        code.add((byte) (0x7C)); // iushr
        return this;
    }

    /**
     * Bitwise XOR of two ints (ixor).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ixor() {
        code.add((byte) (0x82)); // ixor
        return this;
    }

    /**
     * Converts long to double (l2d).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder l2d() {
        code.add((byte) (0x8A)); // l2d
        return this;
    }

    /**
     * Converts long to float (l2f).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder l2f() {
        code.add((byte) (0x89)); // l2f
        return this;
    }

    /**
     * Converts long to int (l2i).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder l2i() {
        code.add((byte) (0x88)); // l2i
        return this;
    }

    /**
     * Adds two longs (ladd).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ladd() {
        code.add((byte) (0x61)); // ladd
        return this;
    }

    /**
     * Loads long from array (laload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder laload() {
        code.add((byte) (0x2F)); // laload
        return this;
    }

    /**
     * Bitwise AND of two longs (land).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder land() {
        code.add((byte) (0x7F)); // land
        return this;
    }

    /**
     * Stores long into array (lastore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lastore() {
        code.add((byte) (0x50)); // lastore
        return this;
    }

    /**
     * Compares two longs (lcmp).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lcmp() {
        code.add((byte) (0x94)); // lcmp
        return this;
    }

    /**
     * Pushes long constant onto stack (lconst).
     *
     * @param value the long value (0 or 1)
     * @return this CodeBuilder
     */
    public CodeBuilder lconst(long value) {
        if (value == 0L) {
            code.add((byte) (0x09)); // lconst_0
        } else if (value == 1L) {
            code.add((byte) (0x0A)); // lconst_1
        } else {
            throw new IllegalArgumentException("Long value " + value + " requires ldc2_w");
        }
        return this;
    }

    /**
     * Pushes constant from pool (ldc/ldc_w).
     *
     * @param constantIndex constant pool index
     * @return this CodeBuilder
     */
    public CodeBuilder ldc(int constantIndex) {
        if (constantIndex <= 255) {
            code.add((byte) (0x12)); // ldc
            code.add((byte) (constantIndex));
        } else {
            code.add((byte) (0x13)); // ldc_w
            writeShort(constantIndex);
        }
        return this;
    }

    /**
     * Pushes long/double constant from pool (ldc2_w).
     *
     * @param constantIndex constant pool index
     * @return this CodeBuilder
     */
    public CodeBuilder ldc2_w(int constantIndex) {
        code.add((byte) (0x14)); // ldc2_w
        writeShort(constantIndex);
        return this;
    }

    /**
     * Divides two longs (ldiv).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder ldiv() {
        code.add((byte) (0x6D)); // ldiv
        return this;
    }

    /**
     * Loads long from local variable (lload).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder lload(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x1E));
            // lload_0
            case 1 -> code.add((byte) (0x1F));
            // lload_1
            case 2 -> code.add((byte) (0x20));
            // lload_2
            case 3 -> code.add((byte) (0x21));
            // lload_3
            default -> {
                code.add((byte) (0x16)); // lload
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Multiplies two longs (lmul).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lmul() {
        code.add((byte) (0x69)); // lmul
        return this;
    }

    /**
     * Negates long (lneg).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lneg() {
        code.add((byte) (0x75)); // lneg
        return this;
    }

    /**
     * lookupswitch instruction for sparse case values.
     *
     * @param defaultOffset offset to default label
     * @param matchOffsets  array of [key, offset] pairs, must be sorted by key
     * @return this CodeBuilder
     */
    public CodeBuilder lookupswitch(int defaultOffset, int[][] matchOffsets) {
        int lookupSwitchStart = code.size();
        code.add((byte) (0xAB)); // lookupswitch opcode

        // Add padding to align to 4-byte boundary
        int padding = (4 - ((lookupSwitchStart + 1) % 4)) % 4;
        for (int i = 0; i < padding; i++) {
            code.add((byte) 0);
        }

        // Write default offset
        writeInt(defaultOffset - lookupSwitchStart);

        // Write npairs
        writeInt(matchOffsets.length);

        // Write match-offset pairs (must be sorted by match value)
        for (int[] pair : matchOffsets) {
            if (pair.length != 2) {
                throw new IllegalArgumentException("Each match-offset pair must have exactly 2 elements");
            }
            writeInt(pair[0]); // match value (key)
            writeInt(pair[1] - lookupSwitchStart); // offset
        }

        return this;
    }

    /**
     * Bitwise OR of two longs (lor).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lor() {
        code.add((byte) (0x81)); // lor
        return this;
    }

    /**
     * Remainder of long division (lrem).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lrem() {
        code.add((byte) (0x71)); // lrem
        return this;
    }

    /**
     * Returns long from method (lreturn).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lreturn() {
        code.add((byte) (0xAD)); // lreturn
        return this;
    }

    /**
     * Shifts long left (lshl).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lshl() {
        code.add((byte) (0x79)); // lshl
        return this;
    }

    /**
     * Arithmetic shift long right (lshr).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lshr() {
        code.add((byte) (0x7B)); // lshr
        return this;
    }

    /**
     * Stores long into local variable (lstore).
     *
     * @param index the local variable index
     * @return this CodeBuilder
     */
    public CodeBuilder lstore(int index) {
        switch (index) {
            case 0 -> code.add((byte) (0x3F));
            // lstore_0
            case 1 -> code.add((byte) (0x40));
            // lstore_1
            case 2 -> code.add((byte) (0x41));
            // lstore_2
            case 3 -> code.add((byte) (0x42));
            // lstore_3
            default -> {
                code.add((byte) (0x37)); // lstore
                code.add((byte) (index));
            }
        }
        return this;
    }

    /**
     * Subtracts two longs (lsub).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lsub() {
        code.add((byte) (0x65)); // lsub
        return this;
    }

    /**
     * Logical shift long right (lushr).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lushr() {
        code.add((byte) (0x7D)); // lushr
        return this;
    }

    /**
     * Bitwise XOR of two longs (lxor).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder lxor() {
        code.add((byte) (0x83)); // lxor
        return this;
    }

    /**
     * Creates new object instance (new).
     *
     * @param classRefIndex constant pool index of the class
     * @return this CodeBuilder
     */
    public CodeBuilder newInstance(int classRefIndex) {
        code.add((byte) (0xBB)); // new
        writeShort(classRefIndex);
        return this;
    }

    /**
     * Creates new primitive array (newarray).
     *
     * @param typeCode the primitive type code
     * @return this CodeBuilder
     */
    public CodeBuilder newarray(int typeCode) {
        code.add((byte) (0xBC)); // newarray
        code.add((byte) (typeCode));
        return this;
    }

    /**
     * Patches a sequence of bytes at the specified position.
     * This is used for complex instructions like switch that need
     * to be rewritten after target offsets are known.
     *
     * @param position the byte position where patching starts
     * @param bytes    the bytes to write
     */
    public void patchBytes(int position, byte[] bytes) {
        if (position < 0 || position + bytes.length > code.size()) {
            throw new IllegalArgumentException(
                    "Patch position out of bounds: " + position + " + " + bytes.length + " > " + code.size());
        }
        for (int i = 0; i < bytes.length; i++) {
            code.set(position + i, bytes[i]);
        }
    }

    /**
     * Patches a 32-bit signed offset at the specified position.
     * This is used for wide branching instructions like goto_w.
     *
     * @param position the byte position where the offset starts
     * @param offset   the 32-bit signed offset value to write
     */
    public void patchInt(int position, int offset) {
        if (position < 0 || position + 3 >= code.size()) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        code.set(position, (byte) ((offset >> 24) & 0xFF));
        code.set(position + 1, (byte) ((offset >> 16) & 0xFF));
        code.set(position + 2, (byte) ((offset >> 8) & 0xFF));
        code.set(position + 3, (byte) (offset & 0xFF));
    }

    /**
     * Patches a 16-bit signed offset at the specified position.
     * This is used for branching instructions (if*, goto) that need
     * to be patched after the target location is known.
     *
     * @param position the byte position where the offset starts
     * @param offset   the 16-bit signed offset value to write
     */
    public void patchShort(int position, int offset) {
        if (position < 0 || position + 1 >= code.size()) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        // Write the 16-bit offset in big-endian format
        code.set(position, (byte) ((offset >> 8) & 0xFF));
        code.set(position + 1, (byte) (offset & 0xFF));
    }

    /**
     * Pops top value from stack (pop).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder pop() {
        code.add((byte) (0x57)); // pop
        return this;
    }

    /**
     * Pops top two values from stack (pop2).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder pop2() {
        code.add((byte) (0x58)); // pop2
        return this;
    }

    /**
     * Sets instance field value (putfield).
     *
     * @param fieldRefIndex constant pool index of the field reference
     * @return this CodeBuilder
     */
    public CodeBuilder putfield(int fieldRefIndex) {
        code.add((byte) (0xB5)); // putfield
        writeShort(fieldRefIndex);
        return this;
    }

    /**
     * Sets static field value (putstatic).
     *
     * @param fieldRefIndex constant pool index of the field reference
     * @return this CodeBuilder
     */
    public CodeBuilder putstatic(int fieldRefIndex) {
        code.add((byte) (0xB3)); // putstatic
        writeShort(fieldRefIndex);
        return this;
    }

    /**
     * Returns void from method (return).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder returnVoid() {
        code.add((byte) (0xB1)); // return
        return this;
    }

    /**
     * Loads short from array (saload).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder saload() {
        code.add((byte) (0x35)); // saload
        return this;
    }

    /**
     * Stores short into array (sastore).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder sastore() {
        code.add((byte) (0x56)); // sastore
        return this;
    }

    /**
     * Sets the current source line number for debugging.
     *
     * @param lineNumber the source line number
     */
    public void setLineNumber(int lineNumber) {
        if (lineNumber != currentLine && lineNumber > 0) {
            currentLine = lineNumber;
            lineNumbers.add(new ClassWriter.LineNumberEntry(getCurrentOffset(), lineNumber));
        }
    }

    /**
     * Pushes short value onto stack (sipush).
     *
     * @param value the short value (-32768 to 32767)
     * @return this CodeBuilder
     */
    public CodeBuilder sipush(int value) {
        if (value < -32768 || value > 32767) {
            throw new IllegalArgumentException("sipush value must be in range -32768 to 32767: " + value);
        }
        code.add((byte) (0x11)); // sipush
        writeShort(value);
        return this;
    }

    /**
     * Swaps top two stack values (swap).
     *
     * @return this CodeBuilder
     */
    public CodeBuilder swap() {
        code.add((byte) (0x5F)); // swap
        return this;
    }

    /**
     * tableswitch instruction for dense case values.
     *
     * @param defaultOffset offset to default label
     * @param low           minimum case value
     * @param high          maximum case value
     * @param jumpOffsets   array of jump offsets for each case from low to high
     * @return this CodeBuilder
     */
    public CodeBuilder tableswitch(int defaultOffset, int low, int high, int[] jumpOffsets) {
        if (jumpOffsets.length != (high - low + 1)) {
            throw new IllegalArgumentException(
                    "jumpOffsets length must equal (high - low + 1): expected " +
                            (high - low + 1) + " but got " + jumpOffsets.length);
        }

        int tableSwitchStart = code.size();
        code.add((byte) (0xAA)); // tableswitch opcode

        // Add padding to align to 4-byte boundary
        int padding = (4 - ((tableSwitchStart + 1) % 4)) % 4;
        for (int i = 0; i < padding; i++) {
            code.add((byte) 0);
        }

        // Write default offset
        writeInt(defaultOffset - tableSwitchStart);

        // Write low and high
        writeInt(low);
        writeInt(high);

        // Write jump offsets
        for (int offset : jumpOffsets) {
            writeInt(offset - tableSwitchStart);
        }

        return this;
    }

    /**
     * Converts the code to a byte array.
     *
     * @return the bytecode as a byte array
     */
    public byte[] toByteArray() {
        byte[] bytes = new byte[code.size()];
        for (int i = 0; i < code.size(); i++) {
            bytes[i] = code.get(i);
        }
        return bytes;
    }

    /**
     * Truncate the code to the specified offset.
     * Warning: This discards all bytes after the offset.
     *
     * @param offset the offset to truncate to
     */
    public void truncate(int offset) {
        if (offset < 0 || offset > code.size()) {
            throw new IllegalArgumentException("Offset out of bounds: " + offset);
        }
        while (code.size() > offset) {
            code.remove(code.size() - 1);
        }
    }

    private void writeInt(int value) {
        code.add((byte) ((value >> 24) & 0xFF));
        code.add((byte) ((value >> 16) & 0xFF));
        code.add((byte) ((value >> 8) & 0xFF));
        code.add((byte) (value & 0xFF));
    }

    private void writeShort(int value) {
        code.add((byte) ((value >> 8) & 0xFF));
        code.add((byte) (value & 0xFF));
    }
}
