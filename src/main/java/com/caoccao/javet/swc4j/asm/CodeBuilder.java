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
import java.io.IOException;

/**
 * Helper class to build method bytecode.
 */
public class CodeBuilder {
    private final ByteArrayOutputStream code = new ByteArrayOutputStream();

    public CodeBuilder aload(int index) {
        if (index == 0) {
            code.write(0x2A); // aload_0
        } else if (index == 1) {
            code.write(0x2B); // aload_1
        } else if (index == 2) {
            code.write(0x2C); // aload_2
        } else if (index == 3) {
            code.write(0x2D); // aload_3
        } else {
            code.write(0x19); // aload
            code.write(index);
        }
        return this;
    }

    public CodeBuilder invokespecial(int methodRefIndex) {
        code.write(0xB7); // invokespecial
        writeShort(methodRefIndex);
        return this;
    }

    public CodeBuilder invokevirtual(int methodRefIndex) {
        code.write(0xB6); // invokevirtual
        writeShort(methodRefIndex);
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

    public CodeBuilder returnVoid() {
        code.write(0xB1); // return
        return this;
    }

    public CodeBuilder areturn() {
        code.write(0xB0); // areturn
        return this;
    }

    public CodeBuilder ireturn() {
        code.write(0xAC); // ireturn
        return this;
    }

    public CodeBuilder dreturn() {
        code.write(0xAF); // dreturn
        return this;
    }

    public CodeBuilder iconst(int value) {
        switch (value) {
            case -1: code.write(0x02); break; // iconst_m1
            case 0:  code.write(0x03); break; // iconst_0
            case 1:  code.write(0x04); break; // iconst_1
            case 2:  code.write(0x05); break; // iconst_2
            case 3:  code.write(0x06); break; // iconst_3
            case 4:  code.write(0x07); break; // iconst_4
            case 5:  code.write(0x08); break; // iconst_5
            default:
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
        return this;
    }

    public CodeBuilder dconst(double value) {
        if (value == 0.0) {
            code.write(0x0E); // dconst_0
        } else if (value == 1.0) {
            code.write(0x0F); // dconst_1
        } else {
            throw new IllegalArgumentException("Double value " + value + " requires ldc2_w");
        }
        return this;
    }

    public CodeBuilder istore(int index) {
        switch (index) {
            case 0: code.write(0x3B); break; // istore_0
            case 1: code.write(0x3C); break; // istore_1
            case 2: code.write(0x3D); break; // istore_2
            case 3: code.write(0x3E); break; // istore_3
            default:
                code.write(0x36); // istore
                code.write(index);
        }
        return this;
    }

    public CodeBuilder iload(int index) {
        switch (index) {
            case 0: code.write(0x1A); break; // iload_0
            case 1: code.write(0x1B); break; // iload_1
            case 2: code.write(0x1C); break; // iload_2
            case 3: code.write(0x1D); break; // iload_3
            default:
                code.write(0x15); // iload
                code.write(index);
        }
        return this;
    }

    public CodeBuilder astore(int index) {
        switch (index) {
            case 0: code.write(0x4B); break; // astore_0
            case 1: code.write(0x4C); break; // astore_1
            case 2: code.write(0x4D); break; // astore_2
            case 3: code.write(0x4E); break; // astore_3
            default:
                code.write(0x3A); // astore
                code.write(index);
        }
        return this;
    }

    public CodeBuilder iadd() {
        code.write(0x60); // iadd
        return this;
    }

    public CodeBuilder dadd() {
        code.write(0x63); // dadd
        return this;
    }

    public CodeBuilder newInstance(int classRefIndex) {
        code.write(0xBB); // new
        writeShort(classRefIndex);
        return this;
    }

    public CodeBuilder dup() {
        code.write(0x59); // dup
        return this;
    }

    public byte[] toByteArray() {
        return code.toByteArray();
    }

    private void writeShort(int value) {
        code.write((value >> 8) & 0xFF);
        code.write(value & 0xFF);
    }
}
