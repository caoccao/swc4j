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


import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to build method bytecode.
 */
public class CodeBuilder {
    private final List<Byte> code = new ArrayList<>(4096);
    private final List<ClassWriter.LineNumberEntry> lineNumbers = new ArrayList<>(1024);
    private int currentLine = -1;

    public CodeBuilder aaload() {
        code.add((byte) (0x32)); // aaload
        return this;
    }

    public CodeBuilder aastore() {
        code.add((byte) (0x53)); // aastore
        return this;
    }

    public CodeBuilder aconst_null() {
        code.add((byte) (0x01)); // aconst_null
        return this;
    }

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

    public CodeBuilder anewarray(int classRefIndex) {
        code.add((byte) (0xBD)); // anewarray
        writeShort(classRefIndex);
        return this;
    }

    public CodeBuilder areturn() {
        code.add((byte) (0xB0)); // areturn
        return this;
    }

    public CodeBuilder arraylength() {
        code.add((byte) (0xBE)); // arraylength
        return this;
    }

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

    public CodeBuilder baload() {
        code.add((byte) (0x33)); // baload
        return this;
    }

    public CodeBuilder bastore() {
        code.add((byte) (0x54)); // bastore
        return this;
    }

    public CodeBuilder caload() {
        code.add((byte) (0x34)); // caload
        return this;
    }

    public CodeBuilder castore() {
        code.add((byte) (0x55)); // castore
        return this;
    }

    public CodeBuilder d2f() {
        code.add((byte) (0x90)); // d2f
        return this;
    }

    public CodeBuilder d2i() {
        code.add((byte) (0x8E)); // d2i
        return this;
    }

    public CodeBuilder d2l() {
        code.add((byte) (0x8F)); // d2l
        return this;
    }

    public CodeBuilder dadd() {
        code.add((byte) (0x63)); // dadd
        return this;
    }

    public CodeBuilder daload() {
        code.add((byte) (0x31)); // daload
        return this;
    }

    public CodeBuilder dastore() {
        code.add((byte) (0x52)); // dastore
        return this;
    }

    public CodeBuilder dcmpl() {
        code.add((byte) (0x97)); // dcmpl
        return this;
    }

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

    public CodeBuilder ddiv() {
        code.add((byte) (0x6F)); // ddiv
        return this;
    }

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

    public CodeBuilder dmul() {
        code.add((byte) (0x6B)); // dmul
        return this;
    }

    public CodeBuilder dneg() {
        code.add((byte) (0x77)); // dneg
        return this;
    }

    public CodeBuilder drem() {
        code.add((byte) (0x73)); // drem
        return this;
    }

    public CodeBuilder dreturn() {
        code.add((byte) (0xAF)); // dreturn
        return this;
    }

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

    public CodeBuilder dsub() {
        code.add((byte) (0x67)); // dsub
        return this;
    }

    public CodeBuilder dup() {
        code.add((byte) (0x59)); // dup
        return this;
    }

    public CodeBuilder dup2() {
        code.add((byte) (0x5C)); // dup2
        return this;
    }

    public CodeBuilder dup2_x2() {
        code.add((byte) (0x5E)); // dup2_x2
        return this;
    }

    public CodeBuilder dup_x2() {
        code.add((byte) (0x5B)); // dup_x2
        return this;
    }

    public CodeBuilder f2d() {
        code.add((byte) (0x8D)); // f2d
        return this;
    }

    public CodeBuilder f2i() {
        code.add((byte) (0x8B)); // f2i
        return this;
    }

    public CodeBuilder f2l() {
        code.add((byte) (0x8C)); // f2l
        return this;
    }

    public CodeBuilder fadd() {
        code.add((byte) (0x62)); // fadd
        return this;
    }

    public CodeBuilder faload() {
        code.add((byte) (0x30)); // faload
        return this;
    }

    public CodeBuilder fastore() {
        code.add((byte) (0x51)); // fastore
        return this;
    }

    public CodeBuilder fcmpl() {
        code.add((byte) (0x95)); // fcmpl
        return this;
    }

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

    public CodeBuilder fdiv() {
        code.add((byte) (0x6E)); // fdiv
        return this;
    }

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

    public CodeBuilder fmul() {
        code.add((byte) (0x6A)); // fmul
        return this;
    }

    public CodeBuilder fneg() {
        code.add((byte) (0x76)); // fneg
        return this;
    }

    public CodeBuilder frem() {
        code.add((byte) (0x72)); // frem
        return this;
    }

    public CodeBuilder freturn() {
        code.add((byte) (0xAE)); // freturn
        return this;
    }

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

    public CodeBuilder fsub() {
        code.add((byte) (0x66)); // fsub
        return this;
    }

    /**
     * Generates StackMapTable entries for methods with branch instructions.
     * Uses bytecode analysis to compute verification frames.
     *
     * @param maxLocals the maximum number of local variables
     * @param isStatic  whether the method is static
     * @param className the class name for 'this' reference type
     * @return list of stack map entries, or null if not needed
     */
    public java.util.List<ClassWriter.StackMapEntry> generateStackMapTable(int maxLocals, boolean isStatic, String className) {
        byte[] bytecode = toByteArray();
        StackMapGenerator generator = new StackMapGenerator(bytecode, maxLocals, isStatic, className);
        return generator.generate();
    }

    public int getCurrentOffset() {
        return code.size();
    }

    public List<ClassWriter.LineNumberEntry> getLineNumbers() {
        return lineNumbers;
    }

    public CodeBuilder gotoLabel(int offset) {
        code.add((byte) (0xA7)); // goto
        writeShort(offset);
        return this;
    }

    public CodeBuilder i2b() {
        code.add((byte) (0x91)); // i2b
        return this;
    }

    public CodeBuilder i2c() {
        code.add((byte) (0x92)); // i2c
        return this;
    }

    public CodeBuilder i2d() {
        code.add((byte) (0x87)); // i2d
        return this;
    }

    public CodeBuilder i2f() {
        code.add((byte) (0x86)); // i2f
        return this;
    }

    // Type conversion instructions
    public CodeBuilder i2l() {
        code.add((byte) (0x85)); // i2l
        return this;
    }

    public CodeBuilder i2s() {
        code.add((byte) (0x93)); // i2s
        return this;
    }

    public CodeBuilder iadd() {
        code.add((byte) (0x60)); // iadd
        return this;
    }

    public CodeBuilder iaload() {
        code.add((byte) (0x2E)); // iaload
        return this;
    }

    public CodeBuilder iand() {
        code.add((byte) (0x7E)); // iand
        return this;
    }

    public CodeBuilder iastore() {
        code.add((byte) (0x4F)); // iastore
        return this;
    }

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

    public CodeBuilder idiv() {
        code.add((byte) (0x6C)); // idiv
        return this;
    }

    public CodeBuilder if_acmpeq(int offset) {
        code.add((byte) (0xA5)); // if_acmpeq
        writeShort(offset);
        return this;
    }

    public CodeBuilder if_acmpne(int offset) {
        code.add((byte) (0xA6)); // if_acmpne
        writeShort(offset);
        return this;
    }

    public CodeBuilder if_icmpeq(int offset) {
        code.add((byte) (0x9F)); // if_icmpeq
        writeShort(offset);
        return this;
    }

    public CodeBuilder if_icmpge(int offset) {
        code.add((byte) (0xA2)); // if_icmpge
        writeShort(offset);
        return this;
    }

    public CodeBuilder if_icmpgt(int offset) {
        code.add((byte) (0xA3)); // if_icmpgt
        writeShort(offset);
        return this;
    }

    public CodeBuilder if_icmple(int offset) {
        code.add((byte) (0xA4)); // if_icmple
        writeShort(offset);
        return this;
    }

    public CodeBuilder if_icmplt(int offset) {
        code.add((byte) (0xA1)); // if_icmplt
        writeShort(offset);
        return this;
    }

    public CodeBuilder if_icmpne(int offset) {
        code.add((byte) (0xA0)); // if_icmpne
        writeShort(offset);
        return this;
    }

    public CodeBuilder ifeq(int offset) {
        code.add((byte) (0x99)); // ifeq
        writeShort(offset);
        return this;
    }

    public CodeBuilder ifge(int offset) {
        code.add((byte) (0x9C)); // ifge
        writeShort(offset);
        return this;
    }

    public CodeBuilder ifgt(int offset) {
        code.add((byte) (0x9D)); // ifgt
        writeShort(offset);
        return this;
    }

    public CodeBuilder ifle(int offset) {
        code.add((byte) (0x9E)); // ifle
        writeShort(offset);
        return this;
    }

    public CodeBuilder iflt(int offset) {
        code.add((byte) (0x9B)); // iflt
        writeShort(offset);
        return this;
    }

    public CodeBuilder ifne(int offset) {
        code.add((byte) (0x9A)); // ifne
        writeShort(offset);
        return this;
    }

    public CodeBuilder ifnonnull(int offset) {
        code.add((byte) (0xC7)); // ifnonnull
        writeShort(offset);
        return this;
    }

    public CodeBuilder ifnull(int offset) {
        code.add((byte) (0xC6)); // ifnull
        writeShort(offset);
        return this;
    }

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

    public CodeBuilder imul() {
        code.add((byte) (0x68)); // imul
        return this;
    }

    public CodeBuilder ineg() {
        code.add((byte) (0x74)); // ineg
        return this;
    }

    public CodeBuilder invokeinterface(int methodRefIndex, int count) {
        code.add((byte) (0xB9)); // invokeinterface
        writeShort(methodRefIndex);
        code.add((byte) (count)); // count parameter
        code.add((byte) (0)); // reserved byte (must be zero)
        return this;
    }

    public CodeBuilder invokespecial(int methodRefIndex) {
        code.add((byte) (0xB7)); // invokespecial
        writeShort(methodRefIndex);
        return this;
    }

    public CodeBuilder invokestatic(int methodRefIndex) {
        code.add((byte) (0xB8)); // invokestatic
        writeShort(methodRefIndex);
        return this;
    }

    public CodeBuilder invokevirtual(int methodRefIndex) {
        code.add((byte) (0xB6)); // invokevirtual
        writeShort(methodRefIndex);
        return this;
    }

    public CodeBuilder ior() {
        code.add((byte) (0x80)); // ior
        return this;
    }

    public CodeBuilder irem() {
        code.add((byte) (0x70)); // irem
        return this;
    }

    public CodeBuilder ireturn() {
        code.add((byte) (0xAC)); // ireturn
        return this;
    }

    public CodeBuilder ishl() {
        code.add((byte) (0x78)); // ishl
        return this;
    }

    public CodeBuilder ishr() {
        code.add((byte) (0x7A)); // ishr
        return this;
    }

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

    public CodeBuilder isub() {
        code.add((byte) (0x64)); // isub
        return this;
    }

    public CodeBuilder iushr() {
        code.add((byte) (0x7C)); // iushr
        return this;
    }

    public CodeBuilder ixor() {
        code.add((byte) (0x82)); // ixor
        return this;
    }

    public CodeBuilder l2d() {
        code.add((byte) (0x8A)); // l2d
        return this;
    }

    public CodeBuilder l2f() {
        code.add((byte) (0x89)); // l2f
        return this;
    }

    public CodeBuilder l2i() {
        code.add((byte) (0x88)); // l2i
        return this;
    }

    public CodeBuilder ladd() {
        code.add((byte) (0x61)); // ladd
        return this;
    }

    public CodeBuilder laload() {
        code.add((byte) (0x2F)); // laload
        return this;
    }

    public CodeBuilder land() {
        code.add((byte) (0x7F)); // land
        return this;
    }

    public CodeBuilder lastore() {
        code.add((byte) (0x50)); // lastore
        return this;
    }

    public CodeBuilder lcmp() {
        code.add((byte) (0x94)); // lcmp
        return this;
    }

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

    public CodeBuilder ldc2_w(int constantIndex) {
        code.add((byte) (0x14)); // ldc2_w
        writeShort(constantIndex);
        return this;
    }

    public CodeBuilder ldiv() {
        code.add((byte) (0x6D)); // ldiv
        return this;
    }

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

    public CodeBuilder lmul() {
        code.add((byte) (0x69)); // lmul
        return this;
    }

    public CodeBuilder lneg() {
        code.add((byte) (0x75)); // lneg
        return this;
    }

    public CodeBuilder lor() {
        code.add((byte) (0x81)); // lor
        return this;
    }

    public CodeBuilder lrem() {
        code.add((byte) (0x71)); // lrem
        return this;
    }

    public CodeBuilder lreturn() {
        code.add((byte) (0xAD)); // lreturn
        return this;
    }

    public CodeBuilder lshl() {
        code.add((byte) (0x79)); // lshl
        return this;
    }

    public CodeBuilder lshr() {
        code.add((byte) (0x7B)); // lshr
        return this;
    }

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

    public CodeBuilder lsub() {
        code.add((byte) (0x65)); // lsub
        return this;
    }

    public CodeBuilder lushr() {
        code.add((byte) (0x7D)); // lushr
        return this;
    }

    public CodeBuilder lxor() {
        code.add((byte) (0x83)); // lxor
        return this;
    }

    public CodeBuilder newInstance(int classRefIndex) {
        code.add((byte) (0xBB)); // new
        writeShort(classRefIndex);
        return this;
    }

    public CodeBuilder newarray(int typeCode) {
        code.add((byte) (0xBC)); // newarray
        code.add((byte) (typeCode));
        return this;
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

    public CodeBuilder pop() {
        code.add((byte) (0x57)); // pop
        return this;
    }

    public CodeBuilder pop2() {
        code.add((byte) (0x58)); // pop2
        return this;
    }

    public CodeBuilder returnVoid() {
        code.add((byte) (0xB1)); // return
        return this;
    }

    public CodeBuilder saload() {
        code.add((byte) (0x35)); // saload
        return this;
    }

    public CodeBuilder sastore() {
        code.add((byte) (0x56)); // sastore
        return this;
    }

    public void setLineNumber(int lineNumber) {
        if (lineNumber != currentLine && lineNumber > 0) {
            currentLine = lineNumber;
            lineNumbers.add(new ClassWriter.LineNumberEntry(getCurrentOffset(), lineNumber));
        }
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[code.size()];
        for (int i = 0; i < code.size(); i++) {
            bytes[i] = code.get(i);
        }
        return bytes;
    }

    private void writeShort(int value) {
        code.add((byte) ((value >> 8) & 0xFF));
        code.add((byte) (value & 0xFF));
    }
}
