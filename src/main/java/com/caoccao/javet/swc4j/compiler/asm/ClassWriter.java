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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

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
    private final List<FieldInfo> fields = new ArrayList<>();
    private final List<String> interfaces = new ArrayList<>();
    private final List<MethodInfo> methods = new ArrayList<>();
    private final String superClassName;
    private int accessFlags = 0x0021; // ACC_PUBLIC | ACC_SUPER (default)
    private String classSignature; // Generic signature for the class

    public ClassWriter(String className) {
        this(className, "java/lang/Object");
    }

    public ClassWriter(String className, String superClassName) {
        this.className = className;
        this.superClassName = superClassName;
        this.constantPool = new ConstantPool();
    }

    public ClassWriter(String className, String superClassName, String[] interfaces) {
        this.className = className;
        this.superClassName = superClassName;
        this.constantPool = new ConstantPool();
        if (interfaces != null) {
            Collections.addAll(this.interfaces, interfaces);
        }
    }

    public void addField(int accessFlags, String name, String descriptor) {
        fields.add(new FieldInfo(accessFlags, name, descriptor));
    }

    public void addInterface(String interfaceInternalName) {
        interfaces.add(interfaceInternalName);
    }

    public void addMethod(int accessFlags, String name, String descriptor, byte[] code, int maxStack, int maxLocals) {
        addMethod(accessFlags, name, descriptor, code, maxStack, maxLocals, null, null, null);
    }

    public void addMethod(int accessFlags, String name, String descriptor, byte[] code, int maxStack, int maxLocals,
                          List<LineNumberEntry> lineNumberTable, List<LocalVariableEntry> localVariableTable) {
        addMethod(accessFlags, name, descriptor, code, maxStack, maxLocals, lineNumberTable, localVariableTable, null, null);
    }

    public void addMethod(int accessFlags, String name, String descriptor, byte[] code, int maxStack, int maxLocals,
                          List<LineNumberEntry> lineNumberTable, List<LocalVariableEntry> localVariableTable,
                          List<StackMapEntry> stackMapTable) {
        addMethod(accessFlags, name, descriptor, code, maxStack, maxLocals, lineNumberTable, localVariableTable, stackMapTable, null);
    }

    public void addMethod(int accessFlags, String name, String descriptor, byte[] code, int maxStack, int maxLocals,
                          List<LineNumberEntry> lineNumberTable, List<LocalVariableEntry> localVariableTable,
                          List<StackMapEntry> stackMapTable, List<ExceptionTableEntry> exceptionTable) {
        methods.add(new MethodInfo(accessFlags, name, descriptor, code, maxStack, maxLocals, lineNumberTable, localVariableTable, stackMapTable, exceptionTable));
    }

    public String getClassName() {
        return className;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    /**
     * Sets the generic signature for the class.
     * For generic interfaces, this would be something like: {@code <T:Ljava/lang/Object;>Ljava/lang/Object;}
     *
     * @param signature the generic signature
     */
    public void setClassSignature(String signature) {
        this.classSignature = signature;
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

        // Pre-add interface classes to constant pool
        int[] interfaceIndexes = new int[interfaces.size()];
        for (int i = 0; i < interfaces.size(); i++) {
            interfaceIndexes[i] = constantPool.addClass(interfaces.get(i));
        }

        // Add class signature to constant pool if present
        if (classSignature != null) {
            constantPool.addUtf8("Signature");
            constantPool.addUtf8(classSignature);
        }

        // Add method references to constant pool
        for (MethodInfo method : methods) {
            constantPool.addUtf8(method.name);
            constantPool.addUtf8(method.descriptor);

            if (method.code != null) {
                constantPool.addUtf8("Code");

                // Add debug attribute names if present
                if (method.stackMapTable != null && !method.stackMapTable.isEmpty()) {
                    constantPool.addUtf8("StackMapTable");
                    // Pre-add class names from StackMapTable entries to constant pool
                    for (StackMapEntry entry : method.stackMapTable) {
                        if (entry.localClassNames != null) {
                            for (String className : entry.localClassNames) {
                                if (className != null) {
                                    constantPool.addClass(className);
                                }
                            }
                        }
                        if (entry.stackClassNames != null) {
                            for (String className : entry.stackClassNames) {
                                if (className != null) {
                                    constantPool.addClass(className);
                                }
                            }
                        }
                    }
                }
                if (method.lineNumberTable != null && !method.lineNumberTable.isEmpty()) {
                    constantPool.addUtf8("LineNumberTable");
                }
                if (method.localVariableTable != null && !method.localVariableTable.isEmpty()) {
                    constantPool.addUtf8("LocalVariableTable");
                    // Add variable names and descriptors to constant pool
                    for (LocalVariableEntry entry : method.localVariableTable) {
                        constantPool.addUtf8(entry.name);
                        constantPool.addUtf8(entry.descriptor);
                    }
                }
            }
        }

        // Add field names and descriptors to constant pool
        for (FieldInfo field : fields) {
            constantPool.addUtf8(field.name);
            constantPool.addUtf8(field.descriptor);
        }

        // Write constant pool
        constantPool.write(out);

        // Access flags (use configured access flags)
        out.writeShort(accessFlags);

        // This class
        out.writeShort(thisClassIndex);

        // Super class
        out.writeShort(superClassIndex);

        // Interfaces count and interfaces
        out.writeShort(interfaces.size());
        for (int interfaceIndex : interfaceIndexes) {
            out.writeShort(interfaceIndex);
        }

        // Fields count
        out.writeShort(fields.size());

        // Fields
        for (FieldInfo field : fields) {
            writeField(out, field);
        }

        // Methods count
        out.writeShort(methods.size());

        // Methods
        for (MethodInfo method : methods) {
            writeMethod(out, method);
        }

        // Class attributes count and attributes
        int classAttributeCount = (classSignature != null) ? 1 : 0;
        out.writeShort(classAttributeCount);

        // Write Signature attribute if present
        if (classSignature != null) {
            out.writeShort(constantPool.getUtf8Index("Signature"));
            out.writeInt(2); // Signature attribute length is always 2 (1 u2 for signature_index)
            out.writeShort(constantPool.getUtf8Index(classSignature));
        }

        return baos.toByteArray();
    }

    private void writeField(DataOutputStream out, FieldInfo field) throws IOException {
        // Access flags
        out.writeShort(field.accessFlags);

        // Name index
        out.writeShort(constantPool.getUtf8Index(field.name));

        // Descriptor index
        out.writeShort(constantPool.getUtf8Index(field.descriptor));

        // Attributes count (no attributes for basic fields)
        out.writeShort(0);
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

            // Calculate attribute length including sub-attributes
            int exceptionTableSize = (method.exceptionTable != null) ? method.exceptionTable.size() * 8 : 0;
            int attributeLength = 12 + method.code.length + exceptionTableSize; // Base: max_stack + max_locals + code_length + code + exception_table_length + exception_table + attributes_count

            // Add StackMapTable size if present
            if (method.stackMapTable != null && !method.stackMapTable.isEmpty()) {
                int stackMapSize = 2; // number_of_entries
                for (StackMapEntry entry : method.stackMapTable) {
                    stackMapSize += 1; // frame_type
                    if (entry.frameType == 255) { // FULL_FRAME
                        stackMapSize += 2; // offset_delta
                        stackMapSize += 2; // number_of_locals
                        if (entry.locals != null) {
                            for (int local : entry.locals) {
                                stackMapSize += (local == 7) ? 3 : 1; // OBJECT type needs 1 byte + 2 byte index
                            }
                        }
                        stackMapSize += 2; // number_of_stack_items
                        if (entry.stack != null) {
                            for (int stackItem : entry.stack) {
                                stackMapSize += (stackItem == 7) ? 3 : 1; // OBJECT type needs 1 byte + 2 byte index
                            }
                        }
                    }
                }
                attributeLength += 6 + stackMapSize; // attribute_name_index + attribute_length + stackMapSize
            }

            // Add LineNumberTable size if present
            if (method.lineNumberTable != null && !method.lineNumberTable.isEmpty()) {
                attributeLength += 8 + method.lineNumberTable.size() * 4; // attribute_name_index + attribute_length + line_number_table_length + entries
            }

            // Add LocalVariableTable size if present
            if (method.localVariableTable != null && !method.localVariableTable.isEmpty()) {
                attributeLength += 8 + method.localVariableTable.size() * 10; // attribute_name_index + attribute_length + local_variable_table_length + entries
            }

            out.writeInt(attributeLength);

            // Max stack
            out.writeShort(method.maxStack);

            // Max locals
            out.writeShort(method.maxLocals);

            // Code length
            out.writeInt(method.code.length);

            // Code
            out.write(method.code);

            // Exception table
            if (method.exceptionTable != null && !method.exceptionTable.isEmpty()) {
                out.writeShort(method.exceptionTable.size());
                for (ExceptionTableEntry entry : method.exceptionTable) {
                    out.writeShort(entry.startPc);
                    out.writeShort(entry.endPc);
                    out.writeShort(entry.handlerPc);
                    out.writeShort(entry.catchType);
                }
            } else {
                out.writeShort(0);
            }

            // Code attributes count (StackMapTable + LineNumberTable + LocalVariableTable if present)
            int codeAttributeCount = 0;
            if (method.stackMapTable != null && !method.stackMapTable.isEmpty()) {
                codeAttributeCount++;
            }
            if (method.lineNumberTable != null && !method.lineNumberTable.isEmpty()) {
                codeAttributeCount++;
            }
            if (method.localVariableTable != null && !method.localVariableTable.isEmpty()) {
                codeAttributeCount++;
            }
            out.writeShort(codeAttributeCount);

            // Write StackMapTable attribute
            if (method.stackMapTable != null && !method.stackMapTable.isEmpty()) {
                out.writeShort(constantPool.addUtf8("StackMapTable"));
                // Calculate size
                ByteArrayOutputStream stackMapBaos = new ByteArrayOutputStream();
                DataOutputStream stackMapOut = new DataOutputStream(stackMapBaos);
                stackMapOut.writeShort(method.stackMapTable.size());
                for (StackMapEntry entry : method.stackMapTable) {
                    stackMapOut.writeByte(entry.frameType);
                    if (entry.frameType == 255) { // FULL_FRAME
                        stackMapOut.writeShort(entry.offset);
                        stackMapOut.writeShort(entry.locals != null ? entry.locals.size() : 0);
                        if (entry.locals != null) {
                            int classNameIndex = 0;
                            for (int local : entry.locals) {
                                String specificClass = null;
                                if (local == 7 && entry.localClassNames != null && classNameIndex < entry.localClassNames.size()) {
                                    specificClass = entry.localClassNames.get(classNameIndex++);
                                }
                                writeVerificationType(stackMapOut, local, specificClass);
                            }
                        }
                        stackMapOut.writeShort(entry.stack != null ? entry.stack.size() : 0);
                        if (entry.stack != null) {
                            int classNameIndex = 0;
                            for (int stackItem : entry.stack) {
                                String specificClass = null;
                                if (stackItem == 7 && entry.stackClassNames != null && classNameIndex < entry.stackClassNames.size()) {
                                    specificClass = entry.stackClassNames.get(classNameIndex++);
                                }
                                writeVerificationType(stackMapOut, stackItem, specificClass);
                            }
                        }
                    }
                }
                byte[] stackMapData = stackMapBaos.toByteArray();
                out.writeInt(stackMapData.length);
                out.write(stackMapData);
            }

            // Write LineNumberTable attribute
            if (method.lineNumberTable != null && !method.lineNumberTable.isEmpty()) {
                out.writeShort(constantPool.addUtf8("LineNumberTable"));
                int lineNumberTableLength = 2 + method.lineNumberTable.size() * 4;
                out.writeInt(lineNumberTableLength);
                out.writeShort(method.lineNumberTable.size());
                for (LineNumberEntry entry : method.lineNumberTable) {
                    out.writeShort(entry.startPc);
                    out.writeShort(entry.lineNumber);
                }
            }

            // Write LocalVariableTable attribute
            if (method.localVariableTable != null && !method.localVariableTable.isEmpty()) {
                out.writeShort(constantPool.addUtf8("LocalVariableTable"));
                int localVariableTableLength = 2 + method.localVariableTable.size() * 10;
                out.writeInt(localVariableTableLength);
                out.writeShort(method.localVariableTable.size());
                for (LocalVariableEntry entry : method.localVariableTable) {
                    out.writeShort(entry.startPc);
                    out.writeShort(entry.length);
                    out.writeShort(constantPool.addUtf8(entry.name));
                    out.writeShort(constantPool.addUtf8(entry.descriptor));
                    out.writeShort(entry.index);
                }
            }
        }
    }

    private void writeVerificationType(DataOutputStream out, int type, String classNameForObject) throws IOException {
        out.writeByte(type);
        if (type == 7) { // OBJECT - use specified class or java/lang/Object for compatibility
            String classToUse = (classNameForObject != null) ? classNameForObject : "java/lang/Object";
            int classIndex = constantPool.addClass(classToUse);
            out.writeShort(classIndex);
        }
        // Other types (INTEGER, LONG, FLOAT, DOUBLE, etc.) don't need additional data
    }

    public static class ConstantPool {
        private final Map<String, Integer> classCache = new HashMap<>();
        private final List<Object> constants = new ArrayList<>();
        private final Map<Double, Integer> doubleCache = new HashMap<>();
        private final Map<Float, Integer> floatCache = new HashMap<>();
        private final Map<Integer, Integer> integerCache = new HashMap<>();
        private final Map<Long, Integer> longCache = new HashMap<>();
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

        public int addFieldRef(String className, String fieldName, String descriptor) {
            String key = "field:" + className + "." + fieldName + ":" + descriptor;
            return methodRefCache.computeIfAbsent(key, k -> {
                int classIndex = addClass(className);
                int nameAndTypeIndex = addNameAndType(fieldName, descriptor);
                int index = constants.size();
                constants.add(new FieldRefInfo(classIndex, nameAndTypeIndex));
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

        public int addInteger(int value) {
            return integerCache.computeIfAbsent(value, v -> {
                int index = constants.size();
                constants.add(new IntegerInfo(v));
                return index;
            });
        }

        public int addInterfaceMethodRef(String interfaceName, String methodName, String descriptor) {
            String key = "interface:" + interfaceName + "." + methodName + ":" + descriptor;
            return methodRefCache.computeIfAbsent(key, k -> {
                int classIndex = addClass(interfaceName);
                int nameAndTypeIndex = addNameAndType(methodName, descriptor);
                int index = constants.size();
                constants.add(new InterfaceMethodRefInfo(classIndex, nameAndTypeIndex));
                return index;
            });
        }

        public int addLong(long value) {
            return longCache.computeIfAbsent(value, v -> {
                int index = constants.size();
                constants.add(new LongInfo(v));
                // Double and Long constants take two slots in the constant pool
                constants.add(null);
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

        /**
         * Get the class name for a class reference at the given constant pool index.
         * This looks up the class info, then the UTF8 name.
         *
         * @param index the constant pool index of the class ref
         * @return the class name string, or null if not found or not a class ref
         */
        public String getClassName(int index) {
            if (index <= 0 || index >= constants.size()) {
                return null;
            }
            Object constant = constants.get(index);
            if (constant instanceof ClassInfo classInfo) {
                int nameIndex = classInfo.nameIndex;
                if (nameIndex <= 0 || nameIndex >= constants.size()) {
                    return null;
                }
                Object name = constants.get(nameIndex);
                if (name instanceof Utf8Info utf8) {
                    return utf8.value;
                }
            }
            return null;
        }

        /**
         * Get the type of constant at the given index for stack map verification.
         * Returns: 'I' for Integer, 'F' for Float, 'J' for Long, 'D' for Double,
         * 'S' for String, 'C' for Class, 'O' for other/unknown.
         *
         * @param index the constant pool index
         * @return a character indicating the constant type
         */
        public char getConstantType(int index) {
            if (index <= 0 || index >= constants.size()) {
                return 'O'; // Unknown
            }
            Object constant = constants.get(index);
            if (constant instanceof IntegerInfo) {
                return 'I';
            } else if (constant instanceof FloatInfo) {
                return 'F';
            } else if (constant instanceof LongInfo) {
                return 'J';
            } else if (constant instanceof DoubleInfo) {
                return 'D';
            } else if (constant instanceof StringInfo) {
                return 'S';
            } else if (constant instanceof ClassInfo) {
                return 'C';
            }
            return 'O'; // Other/unknown
        }

        /**
         * Get the field descriptor for a field reference at the given constant pool index.
         * This looks up the field ref, then the name and type, then the UTF8 descriptor.
         *
         * @param index the constant pool index of the field ref
         * @return the field descriptor string, or null if not found or not a field ref
         */
        public String getFieldDescriptor(int index) {
            if (index <= 0 || index >= constants.size()) {
                return null;
            }
            Object constant = constants.get(index);
            int nameAndTypeIndex;
            if (constant instanceof FieldRefInfo fieldRef) {
                nameAndTypeIndex = fieldRef.nameAndTypeIndex;
            } else {
                return null;
            }
            if (nameAndTypeIndex <= 0 || nameAndTypeIndex >= constants.size()) {
                return null;
            }
            Object nameAndType = constants.get(nameAndTypeIndex);
            if (nameAndType instanceof NameAndTypeInfo natInfo) {
                int descriptorIndex = natInfo.descriptorIndex;
                if (descriptorIndex <= 0 || descriptorIndex >= constants.size()) {
                    return null;
                }
                Object descriptor = constants.get(descriptorIndex);
                if (descriptor instanceof Utf8Info utf8) {
                    return utf8.value;
                }
            }
            return null;
        }

        /**
         * Get the method descriptor for a method reference at the given constant pool index.
         * This looks up the method/interface method ref, then the name and type, then the UTF8 descriptor.
         *
         * @param index the constant pool index of the method ref
         * @return the method descriptor string, or null if not found or not a method ref
         */
        public String getMethodDescriptor(int index) {
            if (index <= 0 || index >= constants.size()) {
                return null;
            }
            Object constant = constants.get(index);
            int nameAndTypeIndex;
            if (constant instanceof MethodRefInfo methodRef) {
                nameAndTypeIndex = methodRef.nameAndTypeIndex;
            } else if (constant instanceof InterfaceMethodRefInfo interfaceMethodRef) {
                nameAndTypeIndex = interfaceMethodRef.nameAndTypeIndex;
            } else {
                return null;
            }
            if (nameAndTypeIndex <= 0 || nameAndTypeIndex >= constants.size()) {
                return null;
            }
            Object nameAndType = constants.get(nameAndTypeIndex);
            if (nameAndType instanceof NameAndTypeInfo natInfo) {
                int descriptorIndex = natInfo.descriptorIndex;
                if (descriptorIndex <= 0 || descriptorIndex >= constants.size()) {
                    return null;
                }
                Object descriptor = constants.get(descriptorIndex);
                if (descriptor instanceof Utf8Info utf8) {
                    return utf8.value;
                }
            }
            return null;
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
                } else if (constant instanceof FieldRefInfo fieldRef) {
                    out.writeByte(9); // CONSTANT_Fieldref
                    out.writeShort(fieldRef.classIndex);
                    out.writeShort(fieldRef.nameAndTypeIndex);
                } else if (constant instanceof MethodRefInfo methodRef) {
                    out.writeByte(10); // CONSTANT_Methodref
                    out.writeShort(methodRef.classIndex);
                    out.writeShort(methodRef.nameAndTypeIndex);
                } else if (constant instanceof InterfaceMethodRefInfo interfaceMethodRef) {
                    out.writeByte(11); // CONSTANT_InterfaceMethodref
                    out.writeShort(interfaceMethodRef.classIndex);
                    out.writeShort(interfaceMethodRef.nameAndTypeIndex);
                } else if (constant instanceof IntegerInfo integerInfo) {
                    out.writeByte(3); // CONSTANT_Integer
                    out.writeInt(integerInfo.value);
                } else if (constant instanceof FloatInfo floatInfo) {
                    out.writeByte(4); // CONSTANT_Float
                    out.writeFloat(floatInfo.value);
                } else if (constant instanceof DoubleInfo doubleInfo) {
                    out.writeByte(6); // CONSTANT_Double
                    out.writeDouble(doubleInfo.value);
                    i++; // Skip the next slot as double takes 2 slots
                } else if (constant instanceof LongInfo longInfo) {
                    out.writeByte(5); // CONSTANT_Long
                    out.writeLong(longInfo.value);
                    i++; // Skip the next slot as long takes 2 slots
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

        private record FieldRefInfo(int classIndex, int nameAndTypeIndex) {
        }

        private record FloatInfo(float value) {
        }

        private record IntegerInfo(int value) {
        }

        private record InterfaceMethodRefInfo(int classIndex, int nameAndTypeIndex) {
        }

        private record LongInfo(long value) {
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

    /**
     * Represents an entry in the exception table of a method's Code attribute.
     *
     * @param startPc   start of the try block (inclusive)
     * @param endPc     end of the try block (exclusive)
     * @param handlerPc start of the exception handler
     * @param catchType constant pool index of the exception class, or 0 for any exception
     */
    public record ExceptionTableEntry(int startPc, int endPc, int handlerPc, int catchType) {
    }

    private record FieldInfo(int accessFlags, String name, String descriptor) {
    }

    public record LineNumberEntry(int startPc, int lineNumber) {
    }

    public record LocalVariableEntry(int startPc, int length, String name, String descriptor, int index) {
    }

    private record MethodInfo(int accessFlags, String name, String descriptor, byte[] code, int maxStack,
                              int maxLocals, List<LineNumberEntry> lineNumberTable,
                              List<LocalVariableEntry> localVariableTable, List<StackMapEntry> stackMapTable,
                              List<ExceptionTableEntry> exceptionTable) {
    }

    public record StackMapEntry(int offset, int frameType, List<Integer> locals, List<Integer> stack,
                                List<String> localClassNames, List<String> stackClassNames) {
        // Backwards-compatible constructor
        public StackMapEntry(int offset, int frameType, List<Integer> locals, List<Integer> stack) {
            this(offset, frameType, locals, stack, null, null);
        }
    }
}
