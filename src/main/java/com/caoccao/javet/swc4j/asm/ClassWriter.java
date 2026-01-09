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
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple bytecode class writer for generating JVM class files.
 * This is a minimal implementation to support basic class generation.
 */
public class ClassWriter {
    private static final int MAGIC = 0xCAFEBABE;
    private static final int MAJOR_VERSION = 61; // Java 17
    private static final int MINOR_VERSION = 0;
    private final String className;
    private final ConstantPool constantPool;
    private final List<MethodInfo> methods = new ArrayList<>();
    private final String superClassName;

    public ClassWriter(String className) {
        this(className, "java/lang/Object");
    }

    public ClassWriter(String className, String superClassName) {
        this.className = className;
        this.superClassName = superClassName;
        this.constantPool = new ConstantPool();
    }

    public void addMethod(int accessFlags, String name, String descriptor, byte[] code, int maxStack, int maxLocals) {
        methods.add(new MethodInfo(accessFlags, name, descriptor, code, maxStack, maxLocals));
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        // Magic number
        out.writeInt(MAGIC);

        // Version
        out.writeShort(MINOR_VERSION);
        out.writeShort(MAJOR_VERSION);

        // Build constant pool
        int thisClassIndex = constantPool.addClass(className);
        int superClassIndex = constantPool.addClass(superClassName);

        // Add method references to constant pool
        for (MethodInfo method : methods) {
            constantPool.addUtf8(method.name);
            constantPool.addUtf8(method.descriptor);

            if (method.code != null) {
                constantPool.addUtf8("Code");
            }
        }

        // Write constant pool
        constantPool.write(out);

        // Access flags (public class)
        out.writeShort(0x0021); // ACC_PUBLIC | ACC_SUPER

        // This class
        out.writeShort(thisClassIndex);

        // Super class
        out.writeShort(superClassIndex);

        // Interfaces count
        out.writeShort(0);

        // Fields count
        out.writeShort(0);

        // Methods count
        out.writeShort(methods.size());

        // Methods
        for (MethodInfo method : methods) {
            writeMethod(out, method);
        }

        // Attributes count
        out.writeShort(0);

        return baos.toByteArray();
    }

    private void writeMethod(DataOutputStream out, MethodInfo method) throws IOException {
        // Access flags
        out.writeShort(method.accessFlags);

        // Name index
        out.writeShort(constantPool.getUtf8Index(method.name));

        // Descriptor index
        out.writeShort(constantPool.getUtf8Index(method.descriptor));

        // Attributes count
        if (method.code == null) {
            out.writeShort(0);
        } else {
            out.writeShort(1); // Code attribute

            // Code attribute
            out.writeShort(constantPool.getUtf8Index("Code"));

            // Attribute length
            int attributeLength = 12 + method.code.length;
            out.writeInt(attributeLength);

            // Max stack
            out.writeShort(method.maxStack);

            // Max locals
            out.writeShort(method.maxLocals);

            // Code length
            out.writeInt(method.code.length);

            // Code
            out.write(method.code);

            // Exception table length
            out.writeShort(0);

            // Code attributes count
            out.writeShort(0);
        }
    }

    public static class ConstantPool {
        private final Map<String, Integer> classCache = new HashMap<>();
        private final List<Object> constants = new ArrayList<>();
        private final Map<Double, Integer> doubleCache = new HashMap<>();
        private final Map<Float, Integer> floatCache = new HashMap<>();
        private final Map<String, Integer> methodRefCache = new HashMap<>();
        private final Map<String, Integer> nameAndTypeCache = new HashMap<>();
        private final Map<String, Integer> stringCache = new HashMap<>();
        private final Map<String, Integer> utf8Cache = new HashMap<>();

        public ConstantPool() {
            // Index 0 is reserved
            constants.add(null);
        }

        public int addClass(String className) {
            return classCache.computeIfAbsent(className, c -> {
                int nameIndex = addUtf8(c);
                int index = constants.size();
                constants.add(new ClassInfo(nameIndex));
                return index;
            });
        }

        public int addDouble(double value) {
            return doubleCache.computeIfAbsent(value, v -> {
                int index = constants.size();
                constants.add(new DoubleInfo(v));
                // Double and Long constants take two slots in the constant pool
                constants.add(null);
                return index;
            });
        }

        public int addFloat(float value) {
            return floatCache.computeIfAbsent(value, v -> {
                int index = constants.size();
                constants.add(new FloatInfo(v));
                return index;
            });
        }

        public int addMethodRef(String className, String methodName, String descriptor) {
            String key = className + "." + methodName + ":" + descriptor;
            return methodRefCache.computeIfAbsent(key, k -> {
                int classIndex = addClass(className);
                int nameAndTypeIndex = addNameAndType(methodName, descriptor);
                int index = constants.size();
                constants.add(new MethodRefInfo(classIndex, nameAndTypeIndex));
                return index;
            });
        }

        public int addNameAndType(String name, String descriptor) {
            String key = name + ":" + descriptor;
            return nameAndTypeCache.computeIfAbsent(key, k -> {
                int nameIndex = addUtf8(name);
                int descriptorIndex = addUtf8(descriptor);
                int index = constants.size();
                constants.add(new NameAndTypeInfo(nameIndex, descriptorIndex));
                return index;
            });
        }

        public int addString(String value) {
            return stringCache.computeIfAbsent(value, v -> {
                int utf8Index = addUtf8(v);
                int index = constants.size();
                constants.add(new StringInfo(utf8Index));
                return index;
            });
        }

        public int addUtf8(String value) {
            return utf8Cache.computeIfAbsent(value, v -> {
                int index = constants.size();
                constants.add(new Utf8Info(v));
                return index;
            });
        }

        public int getUtf8Index(String value) {
            Integer index = utf8Cache.get(value);
            return index != null ? index : addUtf8(value);
        }

        void write(DataOutputStream out) throws IOException {
            out.writeShort(constants.size());
            for (int i = 1; i < constants.size(); i++) {
                Object constant = constants.get(i);
                if (constant instanceof Utf8Info utf8) {
                    out.writeByte(1); // CONSTANT_Utf8
                    out.writeUTF(utf8.value);
                } else if (constant instanceof ClassInfo classInfo) {
                    out.writeByte(7); // CONSTANT_Class
                    out.writeShort(classInfo.nameIndex);
                } else if (constant instanceof StringInfo stringInfo) {
                    out.writeByte(8); // CONSTANT_String
                    out.writeShort(stringInfo.utf8Index);
                } else if (constant instanceof NameAndTypeInfo nameAndType) {
                    out.writeByte(12); // CONSTANT_NameAndType
                    out.writeShort(nameAndType.nameIndex);
                    out.writeShort(nameAndType.descriptorIndex);
                } else if (constant instanceof MethodRefInfo methodRef) {
                    out.writeByte(10); // CONSTANT_Methodref
                    out.writeShort(methodRef.classIndex);
                    out.writeShort(methodRef.nameAndTypeIndex);
                } else if (constant instanceof FloatInfo floatInfo) {
                    out.writeByte(4); // CONSTANT_Float
                    out.writeFloat(floatInfo.value);
                } else if (constant instanceof DoubleInfo doubleInfo) {
                    out.writeByte(6); // CONSTANT_Double
                    out.writeDouble(doubleInfo.value);
                    i++; // Skip the next slot as double takes 2 slots
                } else if (constant == null) {
                    // Skip null entries (used for double/long second slot)
                    continue;
                }
            }
        }

        private record ClassInfo(int nameIndex) {
        }

        private record DoubleInfo(double value) {
        }

        private record FloatInfo(float value) {
        }

        private record MethodRefInfo(int classIndex, int nameAndTypeIndex) {
        }

        private record NameAndTypeInfo(int nameIndex, int descriptorIndex) {
        }

        private record StringInfo(int utf8Index) {
        }

        private record Utf8Info(String value) {
        }
    }

    private record MethodInfo(int accessFlags, String name, String descriptor, byte[] code, int maxStack,
                              int maxLocals) {
    }
}
