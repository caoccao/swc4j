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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestByteCodeCompilerOptions {

    /**
     * Check if the class file's constant pool contains a specific UTF-8 string
     */
    private boolean containsUtf8(byte[] classBytes, String target) throws Exception {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(classBytes));

        // Skip magic and version
        dis.skipBytes(8);

        // Read constant pool count
        int constantPoolCount = dis.readUnsignedShort();

        // Scan constant pool
        for (int i = 1; i < constantPoolCount; i++) {
            int tag = dis.readUnsignedByte();
            switch (tag) {
                case 1: // UTF8
                    String value = dis.readUTF();
                    if (value.equals(target)) {
                        return true;
                    }
                    break;
                case 3: // Integer
                case 4: // Float
                    dis.skipBytes(4);
                    break;
                case 5: // Long
                case 6: // Double
                    dis.skipBytes(8);
                    i++; // Double and Long take 2 slots
                    break;
                case 7: // Class
                case 8: // String
                case 16: // MethodType
                case 19: // Module
                case 20: // Package
                    dis.skipBytes(2);
                    break;
                case 9: // Fieldref
                case 10: // Methodref
                case 11: // InterfaceMethodref
                case 12: // NameAndType
                case 17: // Dynamic
                case 18: // InvokeDynamic
                    dis.skipBytes(4);
                    break;
                case 15: // MethodHandle
                    dis.skipBytes(3);
                    break;
                default:
                    throw new IllegalStateException("Unknown constant pool tag: " + tag);
            }
        }

        return false;
    }

    @Test
    public void testDebugInfoDisabled() throws Exception {
        ByteCodeCompilerOptions options = ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .debug(false)
                .build();

        ByteCodeCompiler compiler = ByteCodeCompiler.of(options);
        compiler.compile("""
                namespace com {
                  export class A {
                    test(): Integer {
                      const x: Integer = 42
                      const y: Integer = 10
                      return x
                    }
                  }
                }""");

        byte[] classBytes = compiler.getMemory().getByteCodeMap().get("com.A");
        assertNotNull(classBytes);

        // Verify the class file does NOT contain debug info
        assertFalse(containsUtf8(classBytes, "LineNumberTable"), "Class should NOT contain LineNumberTable attribute");
        assertFalse(containsUtf8(classBytes, "LocalVariableTable"), "Class should NOT contain LocalVariableTable attribute");
    }

    @Test
    public void testDebugInfoEnabled() throws Exception {
        ByteCodeCompilerOptions options = ByteCodeCompilerOptions.builder()
                .jdkVersion(JdkVersion.JDK_17)
                .debug(true)
                .build();

        ByteCodeCompiler compiler = ByteCodeCompiler.of(options);
        compiler.compile("""
                namespace com {
                  export class A {
                    test(): Integer {
                      const x: Integer = 42
                      const y: Integer = 10
                      return x
                    }
                  }
                }""");

        byte[] classBytes = compiler.getMemory().getByteCodeMap().get("com.A");
        assertNotNull(classBytes);

        // Verify the class file contains debug info
        // We'll check for the presence of LineNumberTable and LocalVariableTable attribute names in the constant pool
        assertTrue(containsUtf8(classBytes, "LineNumberTable"), "Class should contain LineNumberTable attribute");
        assertTrue(containsUtf8(classBytes, "LocalVariableTable"), "Class should contain LocalVariableTable attribute");
    }
}
