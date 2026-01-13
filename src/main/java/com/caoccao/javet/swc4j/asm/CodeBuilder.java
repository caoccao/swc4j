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

package com.caoccao.javet.swc4j.asm;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to build method bytecode.
 */
public class CodeBuilder {
    private final ByteArrayOutputStream code = new ByteArrayOutputStream();
    private final List<ClassWriter.LineNumberEntry> lineNumbers = new ArrayList<>();
    private int currentLine = -1;

    public CodeBuilder aaload() {
        code.write(0x32); // aaload
        return this;
    }

    public CodeBuilder aastore() {
        code.write(0x53); // aastore
        return this;
    }

    public CodeBuilder aconst_null() {
        code.write(0x01); // aconst_null
        return this;
    }

    public CodeBuilder aload(int index) {
        switch (index) {
            case 0 -> code.write(0x2A); // aload_0
            case 1 -> code.write(0x2B); // aload_1
            case 2 -> code.write(0x2C); // aload_2
            case 3 -> code.write(0x2D); // aload_3
            default -> {
                code.write(0x19); // aload
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder anewarray(int classRefIndex) {
        code.write(0xBD); // anewarray
        writeShort(classRefIndex);
        return this;
    }

    public CodeBuilder areturn() {
        code.write(0xB0); // areturn
        return this;
    }

    public CodeBuilder arraylength() {
        code.write(0xBE); // arraylength
        return this;
    }

    public CodeBuilder astore(int index) {
        switch (index) {
            case 0 -> code.write(0x4B);
            // astore_0
            case 1 -> code.write(0x4C);
            // astore_1
            case 2 -> code.write(0x4D);
            // astore_2
            case 3 -> code.write(0x4E);
            // astore_3
            default -> {
                code.write(0x3A); // astore
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder baload() {
        code.write(0x33); // baload
        return this;
    }

    public CodeBuilder bastore() {
        code.write(0x54); // bastore
        return this;
    }

    public CodeBuilder caload() {
        code.write(0x34); // caload
        return this;
    }

    public CodeBuilder castore() {
        code.write(0x55); // castore
        return this;
    }

    public CodeBuilder d2f() {
        code.write(0x90); // d2f
        return this;
    }

    public CodeBuilder d2i() {
        code.write(0x8E); // d2i
        return this;
    }

    public CodeBuilder d2l() {
        code.write(0x8F); // d2l
        return this;
    }

    public CodeBuilder dadd() {
        code.write(0x63); // dadd
        return this;
    }

    public CodeBuilder daload() {
        code.write(0x31); // daload
        return this;
    }

    public CodeBuilder dastore() {
        code.write(0x52); // dastore
        return this;
    }

    public CodeBuilder dconst(double value) {
        if (value == 0.0) {
            code.write(0x0E); // dconst_0
        } else if (value == 1.0) {
            code.write(0x0F); // dconst_1
        } else {
            throw new IllegalArgumentException("Double value " + value + " requires ldc2_w, use ldc2_w() instead");
        }
        return this;
    }

    public CodeBuilder ddiv() {
        code.write(0x6F); // ddiv
        return this;
    }

    public CodeBuilder dload(int index) {
        switch (index) {
            case 0 -> code.write(0x26); // dload_0
            case 1 -> code.write(0x27); // dload_1
            case 2 -> code.write(0x28); // dload_2
            case 3 -> code.write(0x29); // dload_3
            default -> {
                code.write(0x18); // dload
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder dmul() {
        code.write(0x6B); // dmul
        return this;
    }

    public CodeBuilder dneg() {
        code.write(0x77); // dneg
        return this;
    }

    public CodeBuilder drem() {
        code.write(0x73); // drem
        return this;
    }

    public CodeBuilder dreturn() {
        code.write(0xAF); // dreturn
        return this;
    }

    public CodeBuilder dstore(int index) {
        switch (index) {
            case 0 -> code.write(0x47); // dstore_0
            case 1 -> code.write(0x48); // dstore_1
            case 2 -> code.write(0x49); // dstore_2
            case 3 -> code.write(0x4A); // dstore_3
            default -> {
                code.write(0x39); // dstore
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder dsub() {
        code.write(0x67); // dsub
        return this;
    }

    public CodeBuilder dup() {
        code.write(0x59); // dup
        return this;
    }

    public CodeBuilder dup2() {
        code.write(0x5C); // dup2
        return this;
    }

    public CodeBuilder dup2_x2() {
        code.write(0x5E); // dup2_x2
        return this;
    }

    public CodeBuilder dup_x2() {
        code.write(0x5B); // dup_x2
        return this;
    }

    public CodeBuilder f2d() {
        code.write(0x8D); // f2d
        return this;
    }

    public CodeBuilder f2i() {
        code.write(0x8B); // f2i
        return this;
    }

    public CodeBuilder f2l() {
        code.write(0x8C); // f2l
        return this;
    }

    public CodeBuilder fadd() {
        code.write(0x62); // fadd
        return this;
    }

    public CodeBuilder faload() {
        code.write(0x30); // faload
        return this;
    }

    public CodeBuilder fastore() {
        code.write(0x51); // fastore
        return this;
    }

    public CodeBuilder fconst(float value) {
        if (value == 0.0f) {
            code.write(0x0B); // fconst_0
        } else if (value == 1.0f) {
            code.write(0x0C); // fconst_1
        } else if (value == 2.0f) {
            code.write(0x0D); // fconst_2
        } else {
            throw new IllegalArgumentException("Float value " + value + " requires ldc");
        }
        return this;
    }

    public CodeBuilder fdiv() {
        code.write(0x6E); // fdiv
        return this;
    }

    public CodeBuilder fload(int index) {
        switch (index) {
            case 0 -> code.write(0x22);
            // fload_0
            case 1 -> code.write(0x23);
            // fload_1
            case 2 -> code.write(0x24);
            // fload_2
            case 3 -> code.write(0x25);
            // fload_3
            default -> {
                code.write(0x17); // fload
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder fmul() {
        code.write(0x6A); // fmul
        return this;
    }

    public CodeBuilder fneg() {
        code.write(0x76); // fneg
        return this;
    }

    public CodeBuilder frem() {
        code.write(0x72); // frem
        return this;
    }

    public CodeBuilder freturn() {
        code.write(0xAE); // freturn
        return this;
    }

    public CodeBuilder fstore(int index) {
        switch (index) {
            case 0 -> code.write(0x43);
            // fstore_0
            case 1 -> code.write(0x44);
            // fstore_1
            case 2 -> code.write(0x45);
            // fstore_2
            case 3 -> code.write(0x46);
            // fstore_3
            default -> {
                code.write(0x38); // fstore
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder fsub() {
        code.write(0x66); // fsub
        return this;
    }

    public int getCurrentOffset() {
        return code.size();
    }

    public List<ClassWriter.LineNumberEntry> getLineNumbers() {
        return lineNumbers;
    }

    public CodeBuilder i2b() {
        code.write(0x91); // i2b
        return this;
    }

    public CodeBuilder i2c() {
        code.write(0x92); // i2c
        return this;
    }

    public CodeBuilder i2d() {
        code.write(0x87); // i2d
        return this;
    }

    public CodeBuilder i2f() {
        code.write(0x86); // i2f
        return this;
    }

    // Type conversion instructions
    public CodeBuilder i2l() {
        code.write(0x85); // i2l
        return this;
    }

    public CodeBuilder i2s() {
        code.write(0x93); // i2s
        return this;
    }

    public CodeBuilder iadd() {
        code.write(0x60); // iadd
        return this;
    }

    public CodeBuilder iaload() {
        code.write(0x2E); // iaload
        return this;
    }

    public CodeBuilder iastore() {
        code.write(0x4F); // iastore
        return this;
    }

    public CodeBuilder iconst(int value) {
        switch (value) {
            case -1 -> code.write(0x02);
            // iconst_m1
            case 0 -> code.write(0x03);
            // iconst_0
            case 1 -> code.write(0x04);
            // iconst_1
            case 2 -> code.write(0x05);
            // iconst_2
            case 3 -> code.write(0x06);
            // iconst_3
            case 4 -> code.write(0x07);
            // iconst_4
            case 5 -> code.write(0x08);
            // iconst_5
            default -> {
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                    code.write(0x10); // bipush
                    code.write(value);
                } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                    code.write(0x11); // sipush
                    writeShort(value);
                } else {
                    throw new IllegalArgumentException("Integer value " + value + " requires ldc");
                }
            }
        }
        return this;
    }

    public CodeBuilder idiv() {
        code.write(0x6C); // idiv
        return this;
    }

    public CodeBuilder iload(int index) {
        switch (index) {
            case 0 -> code.write(0x1A);
            // iload_0
            case 1 -> code.write(0x1B);
            // iload_1
            case 2 -> code.write(0x1C);
            // iload_2
            case 3 -> code.write(0x1D);
            // iload_3
            default -> {
                code.write(0x15); // iload
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder imul() {
        code.write(0x68); // imul
        return this;
    }

    public CodeBuilder ineg() {
        code.write(0x74); // ineg
        return this;
    }

    public CodeBuilder invokeinterface(int methodRefIndex, int count) {
        code.write(0xB9); // invokeinterface
        writeShort(methodRefIndex);
        code.write(count); // count parameter
        code.write(0); // reserved byte (must be zero)
        return this;
    }

    public CodeBuilder invokespecial(int methodRefIndex) {
        code.write(0xB7); // invokespecial
        writeShort(methodRefIndex);
        return this;
    }

    public CodeBuilder invokestatic(int methodRefIndex) {
        code.write(0xB8); // invokestatic
        writeShort(methodRefIndex);
        return this;
    }

    public CodeBuilder invokevirtual(int methodRefIndex) {
        code.write(0xB6); // invokevirtual
        writeShort(methodRefIndex);
        return this;
    }

    public CodeBuilder irem() {
        code.write(0x70); // irem
        return this;
    }

    public CodeBuilder ireturn() {
        code.write(0xAC); // ireturn
        return this;
    }

    public CodeBuilder ishl() {
        code.write(0x78); // ishl
        return this;
    }

    public CodeBuilder ishr() {
        code.write(0x7A); // ishr
        return this;
    }

    public CodeBuilder istore(int index) {
        switch (index) {
            case 0 -> code.write(0x3B);
            // istore_0
            case 1 -> code.write(0x3C);
            // istore_1
            case 2 -> code.write(0x3D);
            // istore_2
            case 3 -> code.write(0x3E);
            // istore_3
            default -> {
                code.write(0x36); // istore
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder isub() {
        code.write(0x64); // isub
        return this;
    }

    public CodeBuilder l2d() {
        code.write(0x8A); // l2d
        return this;
    }

    public CodeBuilder l2f() {
        code.write(0x89); // l2f
        return this;
    }

    public CodeBuilder l2i() {
        code.write(0x88); // l2i
        return this;
    }

    public CodeBuilder ladd() {
        code.write(0x61); // ladd
        return this;
    }

    public CodeBuilder laload() {
        code.write(0x2F); // laload
        return this;
    }

    public CodeBuilder lastore() {
        code.write(0x50); // lastore
        return this;
    }

    public CodeBuilder lconst(long value) {
        if (value == 0L) {
            code.write(0x09); // lconst_0
        } else if (value == 1L) {
            code.write(0x0A); // lconst_1
        } else {
            throw new IllegalArgumentException("Long value " + value + " requires ldc2_w");
        }
        return this;
    }

    public CodeBuilder ldc(int constantIndex) {
        if (constantIndex <= 255) {
            code.write(0x12); // ldc
            code.write(constantIndex);
        } else {
            code.write(0x13); // ldc_w
            writeShort(constantIndex);
        }
        return this;
    }

    public CodeBuilder ldc2_w(int constantIndex) {
        code.write(0x14); // ldc2_w
        writeShort(constantIndex);
        return this;
    }

    public CodeBuilder ldiv() {
        code.write(0x6D); // ldiv
        return this;
    }

    public CodeBuilder lload(int index) {
        switch (index) {
            case 0 -> code.write(0x1E);
            // lload_0
            case 1 -> code.write(0x1F);
            // lload_1
            case 2 -> code.write(0x20);
            // lload_2
            case 3 -> code.write(0x21);
            // lload_3
            default -> {
                code.write(0x16); // lload
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder lmul() {
        code.write(0x69); // lmul
        return this;
    }

    public CodeBuilder lneg() {
        code.write(0x75); // lneg
        return this;
    }

    public CodeBuilder lrem() {
        code.write(0x71); // lrem
        return this;
    }

    public CodeBuilder lreturn() {
        code.write(0xAD); // lreturn
        return this;
    }

    public CodeBuilder lshl() {
        code.write(0x79); // lshl
        return this;
    }

    public CodeBuilder lshr() {
        code.write(0x7B); // lshr
        return this;
    }

    public CodeBuilder lstore(int index) {
        switch (index) {
            case 0 -> code.write(0x3F);
            // lstore_0
            case 1 -> code.write(0x40);
            // lstore_1
            case 2 -> code.write(0x41);
            // lstore_2
            case 3 -> code.write(0x42);
            // lstore_3
            default -> {
                code.write(0x37); // lstore
                code.write(index);
            }
        }
        return this;
    }

    public CodeBuilder lsub() {
        code.write(0x65); // lsub
        return this;
    }

    public CodeBuilder newInstance(int classRefIndex) {
        code.write(0xBB); // new
        writeShort(classRefIndex);
        return this;
    }

    public CodeBuilder newarray(int typeCode) {
        code.write(0xBC); // newarray
        code.write(typeCode);
        return this;
    }

    public CodeBuilder pop() {
        code.write(0x57); // pop
        return this;
    }

    public CodeBuilder pop2() {
        code.write(0x58); // pop2
        return this;
    }

    public CodeBuilder returnVoid() {
        code.write(0xB1); // return
        return this;
    }

    public CodeBuilder saload() {
        code.write(0x35); // saload
        return this;
    }

    public CodeBuilder sastore() {
        code.write(0x56); // sastore
        return this;
    }

    public void setLineNumber(int lineNumber) {
        if (lineNumber != currentLine && lineNumber > 0) {
            currentLine = lineNumber;
            lineNumbers.add(new ClassWriter.LineNumberEntry(getCurrentOffset(), lineNumber));
        }
    }

    public byte[] toByteArray() {
        return code.toByteArray();
    }

    private void writeShort(int value) {
        code.write((value >> 8) & 0xFF);
        code.write(value & 0xFF);
    }
}
