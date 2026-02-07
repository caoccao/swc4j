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

package com.caoccao.javet.swc4j.compiler.jdk17.ast.utils;

import com.caoccao.javet.swc4j.compiler.asm.ClassWriter;
import com.caoccao.javet.swc4j.compiler.asm.CodeBuilder;

import java.util.Set;

/**
 * The type Type conversion utils.
 */
public final class TypeConversionUtils {
    /**
     * The constant ABBR_BOOLEAN.
     */
    public static final String ABBR_BOOLEAN = "Z";
    /**
     * The constant ABBR_BYTE.
     */
    public static final String ABBR_BYTE = "B";
    /**
     * The constant ABBR_CHARACTER.
     */
    public static final String ABBR_CHARACTER = "C";
    /**
     * The constant ABBR_DOUBLE.
     */
    public static final String ABBR_DOUBLE = "D";
    /**
     * The constant ABBR_FLOAT.
     */
    public static final String ABBR_FLOAT = "F";
    /**
     * The constant ABBR_INTEGER.
     */
    public static final String ABBR_INTEGER = "I";
    /**
     * The constant ABBR_LONG.
     */
    public static final String ABBR_LONG = "J";
    /**
     * The constant ABBR_SHORT.
     */
    public static final String ABBR_SHORT = "S";
    /**
     * The constant ABBR_VOID.
     */
    public static final String ABBR_VOID = "V";
    /**
     * The constant ARRAY_I.
     */
    public static final String ARRAY_I = "[I";
    /**
     * The constant ARRAY_LJAVA_LANG_OBJECT.
     */
    public static final String ARRAY_LJAVA_LANG_OBJECT = "[Ljava/lang/Object;";
    /**
     * The constant ARRAY_J.
     */
    public static final String ARRAY_J = "[J";
    /**
     * The constant ARRAY_LJAVA_LANG_STRING.
     */
    public static final String ARRAY_LJAVA_LANG_STRING = "[Ljava/lang/String;";
    /**
     * The constant ARRAY_ARRAY_LJAVA_LANG_STRING.
     */
    public static final String ARRAY_ARRAY_LJAVA_LANG_STRING = "[[Ljava/lang/String;";
    /**
     * The constant ARRAY_PREFIX.
     */
    public static final String ARRAY_PREFIX = "[";
    /**
     * The constant DESCRIPTER_B__LJAVA_LANG_BYTE.
     */
    public static final String DESCRIPTER_B__LJAVA_LANG_BYTE = "(B)Ljava/lang/Byte;";
    /**
     * The constant DESCRIPTER_C__LJAVA_LANG_CHARACTER.
     */
    public static final String DESCRIPTER_C__LJAVA_LANG_CHARACTER = "(C)Ljava/lang/Character;";
    /**
     * The constant DESCRIPTER_D__LJAVA_LANG_DOUBLE.
     */
    public static final String DESCRIPTER_D__LJAVA_LANG_DOUBLE = "(D)Ljava/lang/Double;";
    /**
     * The constant DESCRIPTER_F__LJAVA_LANG_FLOAT.
     */
    public static final String DESCRIPTER_F__LJAVA_LANG_FLOAT = "(F)Ljava/lang/Float;";
    /**
     * The constant DESCRIPTER_I__LJAVA_LANG_INTEGER.
     */
    public static final String DESCRIPTER_I__LJAVA_LANG_INTEGER = "(I)Ljava/lang/Integer;";
    /**
     * The constant DESCRIPTER_J__LJAVA_LANG_LONG.
     */
    public static final String DESCRIPTER_J__LJAVA_LANG_LONG = "(J)Ljava/lang/Long;";
    /**
     * The constant DESCRIPTER_S__LJAVA_LANG_SHORT.
     */
    public static final String DESCRIPTER_S__LJAVA_LANG_SHORT = "(S)Ljava/lang/Short;";
    /**
     * The constant DESCRIPTER_Z__LJAVA_LANG_BOOLEAN.
     */
    public static final String DESCRIPTER_Z__LJAVA_LANG_BOOLEAN = "(Z)Ljava/lang/Boolean;";
    /**
     * The constant DESCRIPTOR_I_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT.
     */
    public static final String DESCRIPTOR_I_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT = "(ILjava/lang/Object;)Ljava/lang/Object;";
    /**
     * The constant DESCRIPTOR_I__LJAVA_LANG_OBJECT.
     */
    public static final String DESCRIPTOR_I__LJAVA_LANG_OBJECT = "(I)Ljava/lang/Object;";
    /**
     * The constant DESCRIPTOR_I_I__LJAVA_UTIL_LIST.
     */
    public static final String DESCRIPTOR_I_I__LJAVA_UTIL_LIST = "(II)Ljava/util/List;";
    /**
     * The constant DESCRIPTOR_I__C.
     */
    public static final String DESCRIPTOR_I__C = "(I)C";
    /**
     * The constant DESCRIPTOR_I__I.
     */
    public static final String DESCRIPTOR_I__I = "(I)I";
    /**
     * The constant DESCRIPTOR_I__LJAVA_LANG_STRINGBUILDER.
     */
    public static final String DESCRIPTOR_I__LJAVA_LANG_STRINGBUILDER = "(I)Ljava/lang/StringBuilder;";
    /**
     * The constant DESCRIPTOR_I__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_I__LJAVA_LANG_STRING = "(I)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_I__LJAVA_MATH_BIGINTEGER.
     */
    public static final String DESCRIPTOR_I__LJAVA_MATH_BIGINTEGER = "(I)Ljava/math/BigInteger;";
    /**
     * The constant DESCRIPTOR_I__V.
     */
    public static final String DESCRIPTOR_I__V = "(I)V";
    /**
     * The constant DESCRIPTOR_I_D__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_I_D__LJAVA_LANG_STRING = "(ID)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_I_D__V.
     */
    public static final String DESCRIPTOR_I_D__V = "(ID)V";
    /**
     * The constant DESCRIPTOR_LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT.
     */
    public static final String DESCRIPTOR_LJAVA_LANG_OBJECT_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
    /**
     * The constant DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT.
     */
    public static final String DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_OBJECT = "(Ljava/lang/Object;)Ljava/lang/Object;";
    /**
     * The constant DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_LJAVA_LANG_OBJECT__LJAVA_LANG_STRING = "(Ljava/lang/Object;)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_LJAVA_LANG_OBJECT__Z.
     */
    public static final String DESCRIPTOR_LJAVA_LANG_OBJECT__Z = "(Ljava/lang/Object;)Z";
    /**
     * The constant DESCRIPTOR_LJAVA_LANG_STRING__I.
     */
    public static final String DESCRIPTOR_LJAVA_LANG_STRING__I = "(Ljava/lang/String;)I";
    /**
     * The constant DESCRIPTOR_LJAVA_LANG_STRING__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_LJAVA_LANG_STRING__LJAVA_LANG_STRING = "(Ljava/lang/String;)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_B__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_B__LJAVA_LANG_STRING = "(B)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_C__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_C__LJAVA_LANG_STRING = "(C)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_C__LJAVA_LANG_STRINGBUILDER.
     */
    public static final String DESCRIPTOR_C__LJAVA_LANG_STRINGBUILDER = "(C)Ljava/lang/StringBuilder;";
    /**
     * The constant DESCRIPTOR_D__LJAVA_LANG_OBJECT.
     */
    public static final String DESCRIPTOR_D__LJAVA_LANG_OBJECT = "(D)Ljava/lang/Object;";
    /**
     * The constant DESCRIPTOR_D__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_D__LJAVA_LANG_STRING = "(D)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_D__LJAVA_LANG_STRINGBUILDER.
     */
    public static final String DESCRIPTOR_D__LJAVA_LANG_STRINGBUILDER = "(D)Ljava/lang/StringBuilder;";
    /**
     * The constant DESCRIPTOR_D_D__D.
     */
    public static final String DESCRIPTOR_D_D__D = "(DD)D";
    /**
     * The constant DESCRIPTOR_F__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_F__LJAVA_LANG_STRING = "(F)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_F__LJAVA_LANG_STRINGBUILDER.
     */
    public static final String DESCRIPTOR_F__LJAVA_LANG_STRINGBUILDER = "(F)Ljava/lang/StringBuilder;";
    /**
     * The constant DESCRIPTOR_J__LJAVA_LANG_OBJECT.
     */
    public static final String DESCRIPTOR_J__LJAVA_LANG_OBJECT = "(J)Ljava/lang/Object;";
    /**
     * The constant DESCRIPTOR_J__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_J__LJAVA_LANG_STRING = "(J)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_J__LJAVA_LANG_STRINGBUILDER.
     */
    public static final String DESCRIPTOR_J__LJAVA_LANG_STRINGBUILDER = "(J)Ljava/lang/StringBuilder;";
    /**
     * The constant DESCRIPTOR_J__LJAVA_MATH_BIGINTEGER.
     */
    public static final String DESCRIPTOR_J__LJAVA_MATH_BIGINTEGER = "(J)Ljava/math/BigInteger;";
    /**
     * The constant DESCRIPTOR_S__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_S__LJAVA_LANG_STRING = "(S)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_Z__LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR_Z__LJAVA_LANG_STRING = "(Z)Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR_Z__LJAVA_LANG_STRINGBUILDER.
     */
    public static final String DESCRIPTOR_Z__LJAVA_LANG_STRINGBUILDER = "(Z)Ljava/lang/StringBuilder;";
    /**
     * The constant DESCRIPTOR_LJAVA_UTIL_MAP__V.
     */
    public static final String DESCRIPTOR_LJAVA_UTIL_MAP__V = "(Ljava/util/Map;)V";
    /**
     * The constant DESCRIPTOR___LJAVA_LANG_OBJECT.
     */
    public static final String DESCRIPTOR___LJAVA_LANG_OBJECT = "()Ljava/lang/Object;";
    /**
     * The constant DESCRIPTOR___LJAVA_LANG_STRING.
     */
    public static final String DESCRIPTOR___LJAVA_LANG_STRING = "()Ljava/lang/String;";
    /**
     * The constant DESCRIPTOR___LJAVA_LANG_CLASS.
     */
    public static final String DESCRIPTOR___LJAVA_LANG_CLASS = "()Ljava/lang/Class;";
    /**
     * The constant DESCRIPTOR___LJAVA_LANG_THROWABLE.
     */
    public static final String DESCRIPTOR___LJAVA_LANG_THROWABLE = "()Ljava/lang/Throwable;";
    /**
     * The constant DESCRIPTOR___LJAVA_MATH_BIGINTEGER.
     */
    public static final String DESCRIPTOR___LJAVA_MATH_BIGINTEGER = "()Ljava/math/BigInteger;";
    /**
     * The constant DESCRIPTOR___LJAVA_UTIL_ITERATOR.
     */
    public static final String DESCRIPTOR___LJAVA_UTIL_ITERATOR = "()Ljava/util/Iterator;";
    /**
     * The constant DESCRIPTOR___LJAVA_UTIL_SET.
     */
    public static final String DESCRIPTOR___LJAVA_UTIL_SET = "()Ljava/util/Set;";
    /**
     * The constant DESCRIPTOR___ARRAY_LJAVA_LANG_STACKTRACEELEMENT.
     */
    public static final String DESCRIPTOR___ARRAY_LJAVA_LANG_STACKTRACEELEMENT = "()[Ljava/lang/StackTraceElement;";
    /**
     * The constant DESCRIPTOR___V.
     */
    public static final String DESCRIPTOR___V = "()V";
    /**
     * The constant DESCRIPTER___B.
     */
    public static final String DESCRIPTER___B = "()B";
    /**
     * The constant DESCRIPTER___C.
     */
    public static final String DESCRIPTER___C = "()C";
    /**
     * The constant DESCRIPTER___D.
     */
    public static final String DESCRIPTER___D = "()D";
    /**
     * The constant DESCRIPTER___F.
     */
    public static final String DESCRIPTER___F = "()F";
    /**
     * The constant DESCRIPTER___I.
     */
    public static final String DESCRIPTER___I = "()I";
    /**
     * The constant DESCRIPTER___J.
     */
    public static final String DESCRIPTER___J = "()J";
    /**
     * The constant DESCRIPTER___S.
     */
    public static final String DESCRIPTER___S = "()S";
    /**
     * The constant DESCRIPTER___Z.
     */
    public static final String DESCRIPTER___Z = "()Z";
    /**
     * The constant JAVA_LANG_AUTOCLOSEABLE.
     */
    public static final String JAVA_LANG_AUTOCLOSEABLE = "java/lang/AutoCloseable";
    /**
     * The constant JAVA_LANG_BOOLEAN.
     */
    public static final String JAVA_LANG_BOOLEAN = "java/lang/Boolean";
    /**
     * The constant JAVA_LANG_BYTE.
     */
    public static final String JAVA_LANG_BYTE = "java/lang/Byte";
    /**
     * The constant JAVA_LANG_CHARACTER.
     */
    public static final String JAVA_LANG_CHARACTER = "java/lang/Character";
    /**
     * The constant JAVA_LANG_CLASS.
     */
    public static final String JAVA_LANG_CLASS = "java/lang/Class";
    /**
     * The constant JAVA_LANG_DOUBLE.
     */
    public static final String JAVA_LANG_DOUBLE = "java/lang/Double";
    /**
     * The constant JAVA_LANG_ENUM.
     */
    public static final String JAVA_LANG_ENUM = "java/lang/Enum";
    /**
     * The constant JAVA_LANG_FLOAT.
     */
    public static final String JAVA_LANG_FLOAT = "java/lang/Float";
    /**
     * The constant JAVA_LANG_ILLEGALARGUMENTEXCEPTION.
     */
    public static final String JAVA_LANG_ILLEGALARGUMENTEXCEPTION = "java/lang/IllegalArgumentException";
    /**
     * The constant JAVA_LANG_INTEGER.
     */
    public static final String JAVA_LANG_INTEGER = "java/lang/Integer";
    /**
     * The constant JAVA_LANG_ITERABLE.
     */
    public static final String JAVA_LANG_ITERABLE = "java/lang/Iterable";
    /**
     * The constant JAVA_LANG_LONG.
     */
    public static final String JAVA_LANG_LONG = "java/lang/Long";
    /**
     * The constant JAVA_LANG_MATH.
     */
    public static final String JAVA_LANG_MATH = "java/lang/Math";
    /**
     * The constant JAVA_LANG_NUMBER.
     */
    public static final String JAVA_LANG_NUMBER = "java/lang/Number";
    /**
     * The constant JAVA_LANG_NUMBERFORMATEXCEPTION.
     */
    public static final String JAVA_LANG_NUMBERFORMATEXCEPTION = "java/lang/NumberFormatException";
    /**
     * The constant JAVA_LANG_OBJECT.
     */
    public static final String JAVA_LANG_OBJECT = "java/lang/Object";
    /**
     * The constant JAVA_LANG_RUNNABLE.
     */
    public static final String JAVA_LANG_RUNNABLE = "java/lang/Runnable";
    /**
     * The constant JAVA_LANG_SHORT.
     */
    public static final String JAVA_LANG_SHORT = "java/lang/Short";
    /**
     * The constant JAVA_LANG_STRING.
     */
    public static final String JAVA_LANG_STRING = "java/lang/String";
    /**
     * The constant JAVA_LANG_STRINGBUILDER.
     */
    public static final String JAVA_LANG_STRINGBUILDER = "java/lang/StringBuilder";
    /**
     * The constant JAVA_LANG_THROWABLE.
     */
    public static final String JAVA_LANG_THROWABLE = "java/lang/Throwable";
    /**
     * The constant JAVA_MATH_BIGINTEGER.
     */
    public static final String JAVA_MATH_BIGINTEGER = "java/math/BigInteger";
    /**
     * The constant JAVA_UTIL_ARRAYLIST.
     */
    public static final String JAVA_UTIL_ARRAYLIST = "java/util/ArrayList";
    /**
     * The constant JAVA_UTIL_ARRAYS.
     */
    public static final String JAVA_UTIL_ARRAYS = "java/util/Arrays";
    /**
     * The constant JAVA_UTIL_COLLECTIONS.
     */
    public static final String JAVA_UTIL_COLLECTIONS = "java/util/Collections";
    /**
     * The constant JAVA_UTIL_HASHMAP.
     */
    public static final String JAVA_UTIL_HASHMAP = "java/util/HashMap";
    /**
     * The constant JAVA_UTIL_ITERATOR.
     */
    public static final String JAVA_UTIL_ITERATOR = "java/util/Iterator";
    /**
     * The constant JAVA_UTIL_LINKEDLIST.
     */
    public static final String JAVA_UTIL_LINKEDLIST = "java/util/LinkedList";
    /**
     * The constant JAVA_UTIL_LINKEDHASHMAP.
     */
    public static final String JAVA_UTIL_LINKEDHASHMAP = "java/util/LinkedHashMap";
    /**
     * The constant JAVA_UTIL_LIST.
     */
    public static final String JAVA_UTIL_LIST = "java/util/List";
    /**
     * The constant JAVA_UTIL_MAP.
     */
    public static final String JAVA_UTIL_MAP = "java/util/Map";
    /**
     * The constant JAVA_UTIL_MAP_ENTRY.
     */
    public static final String JAVA_UTIL_MAP_ENTRY = "java/util/Map$Entry";
    /**
     * The constant JAVA_UTIL_OBJECTS.
     */
    public static final String JAVA_UTIL_OBJECTS = "java/util/Objects";
    /**
     * The constant JAVA_UTIL_SET.
     */
    public static final String JAVA_UTIL_SET = "java/util/Set";
    /**
     * The constant LJAVA_LANG_.
     */
    public static final String LJAVA_LANG_ = "Ljava/lang/";
    /**
     * The constant LJAVA_LANG_AUTOCLOSEABLE.
     */
    public static final String LJAVA_LANG_AUTOCLOSEABLE = "Ljava/lang/AutoCloseable;";
    /**
     * The constant LJAVA_LANG_BOOLEAN.
     */
    public static final String LJAVA_LANG_BOOLEAN = "Ljava/lang/Boolean;";
    /**
     * The constant LJAVA_LANG_BYTE.
     */
    public static final String LJAVA_LANG_BYTE = "Ljava/lang/Byte;";
    /**
     * The constant LJAVA_LANG_CHARACTER.
     */
    public static final String LJAVA_LANG_CHARACTER = "Ljava/lang/Character;";
    /**
     * The constant LJAVA_LANG_CLASS.
     */
    public static final String LJAVA_LANG_CLASS = "Ljava/lang/Class;";
    /**
     * The constant LJAVA_LANG_DOUBLE.
     */
    public static final String LJAVA_LANG_DOUBLE = "Ljava/lang/Double;";
    /**
     * The constant LJAVA_LANG_FLOAT.
     */
    public static final String LJAVA_LANG_FLOAT = "Ljava/lang/Float;";
    /**
     * The constant LJAVA_LANG_INTEGER.
     */
    public static final String LJAVA_LANG_INTEGER = "Ljava/lang/Integer;";
    /**
     * The constant LJAVA_LANG_LONG.
     */
    public static final String LJAVA_LANG_LONG = "Ljava/lang/Long;";
    /**
     * The constant LJAVA_LANG_NUMBER.
     */
    public static final String LJAVA_LANG_NUMBER = "Ljava/lang/Number;";
    /**
     * The constant LJAVA_LANG_OBJECT.
     */
    public static final String LJAVA_LANG_OBJECT = "Ljava/lang/Object;";
    /**
     * The constant LJAVA_LANG_SHORT.
     */
    public static final String LJAVA_LANG_SHORT = "Ljava/lang/Short;";
    /**
     * The constant LJAVA_LANG_STRING.
     */
    public static final String LJAVA_LANG_STRING = "Ljava/lang/String;";
    /**
     * The constant LJAVA_LANG_VOID.
     */
    public static final String LJAVA_LANG_VOID = "Ljava/lang/Void;";
    /**
     * The constant LJAVA_LANG_THROWABLE.
     */
    public static final String LJAVA_LANG_THROWABLE = "Ljava/lang/Throwable;";
    /**
     * The constant LJAVA_MATH_BIGINTEGER.
     */
    public static final String LJAVA_MATH_BIGINTEGER = "Ljava/math/BigInteger;";
    /**
     * The constant LJAVA_UTIL_ARRAYLIST.
     */
    public static final String LJAVA_UTIL_ARRAYLIST = "Ljava/util/ArrayList;";
    /**
     * The constant LJAVA_UTIL_HASHMAP.
     */
    public static final String LJAVA_UTIL_HASHMAP = "Ljava/util/HashMap;";
    /**
     * The constant LJAVA_UTIL_ITERATOR.
     */
    public static final String LJAVA_UTIL_ITERATOR = "Ljava/util/Iterator;";
    /**
     * The constant LJAVA_UTIL_LINKEDHASHMAP.
     */
    public static final String LJAVA_UTIL_LINKEDHASHMAP = "Ljava/util/LinkedHashMap;";
    /**
     * The constant LJAVA_UTIL_LINKEDLIST.
     */
    public static final String LJAVA_UTIL_LINKEDLIST = "Ljava/util/LinkedList;";
    /**
     * The constant LJAVA_UTIL_MAP_ENTRY.
     */
    public static final String LJAVA_UTIL_MAP_ENTRY = "Ljava/util/Map$Entry;";
    /**
     * The constant LJAVA_UTIL_SET.
     */
    public static final String LJAVA_UTIL_SET = "Ljava/util/Set;";
    /**
     * The constant LJAVA_UTIL_LIST.
     */
    public static final String LJAVA_UTIL_LIST = "Ljava/util/List;";
    /**
     * The constant LJAVA_UTIL_MAP.
     */
    public static final String LJAVA_UTIL_MAP = "Ljava/util/Map;";
    /**
     * The constant TYPEOF_BOOLEAN.
     */
    public static final String TYPEOF_BOOLEAN = "boolean";
    /**
     * The constant TYPEOF_NUMBER.
     */
    public static final String TYPEOF_NUMBER = "number";
    /**
     * The constant TYPEOF_OBJECT.
     */
    public static final String TYPEOF_OBJECT = "object";
    /**
     * The constant TYPEOF_STRING.
     */
    public static final String TYPEOF_STRING = "string";
    /**
     * The constant TYPEOF_UNDEFINED.
     */
    public static final String TYPEOF_UNDEFINED = "undefined";
    /**
     * The constant METHOD_ADD.
     */
    public static final String METHOD_ADD = "add";
    /**
     * The constant METHOD_AND.
     */
    public static final String METHOD_AND = "and";
    /**
     * The constant METHOD_APPEND.
     */
    public static final String METHOD_APPEND = "append";
    /**
     * The constant METHOD_BOOLEAN_VALUE.
     */
    public static final String METHOD_BOOLEAN_VALUE = "booleanValue";
    /**
     * The constant METHOD_BYTE_VALUE.
     */
    public static final String METHOD_BYTE_VALUE = "byteValue";
    /**
     * The constant METHOD_CHAR_VALUE.
     */
    public static final String METHOD_CHAR_VALUE = "charValue";
    /**
     * The constant METHOD_COMPARE_TO.
     */
    public static final String METHOD_COMPARE_TO = "compareTo";
    /**
     * The constant METHOD_CONCAT.
     */
    public static final String METHOD_CONCAT = "concat";
    /**
     * The constant METHOD_DIVIDE.
     */
    public static final String METHOD_DIVIDE = "divide";
    /**
     * The constant METHOD_DOUBLE_VALUE.
     */
    public static final String METHOD_DOUBLE_VALUE = "doubleValue";
    /**
     * The constant METHOD_ENTRY_SET.
     */
    public static final String METHOD_ENTRY_SET = "entrySet";
    /**
     * The constant METHOD_EQUALS.
     */
    public static final String METHOD_EQUALS = "equals";
    /**
     * The constant METHOD_FLOAT_VALUE.
     */
    public static final String METHOD_FLOAT_VALUE = "floatValue";
    /**
     * The constant METHOD_GET.
     */
    public static final String METHOD_GET = "get";
    /**
     * The constant METHOD_GET_KEY.
     */
    public static final String METHOD_GET_KEY = "getKey";
    /**
     * The constant METHOD_GET_VALUE.
     */
    public static final String METHOD_GET_VALUE = "getValue";
    /**
     * The constant METHOD_HAS_NEXT.
     */
    public static final String METHOD_HAS_NEXT = "hasNext";
    /**
     * The constant METHOD_INDEX_OF.
     */
    public static final String METHOD_INDEX_OF = "indexOf";
    /**
     * The constant METHOD_INT_VALUE.
     */
    public static final String METHOD_INT_VALUE = "intValue";
    /**
     * The constant METHOD_ITERATOR.
     */
    public static final String METHOD_ITERATOR = "iterator";
    /**
     * The constant METHOD_INIT.
     */
    public static final String METHOD_INIT = "<init>";
    /**
     * The constant METHOD_LAST_INDEX_OF.
     */
    public static final String METHOD_LAST_INDEX_OF = "lastIndexOf";
    /**
     * The constant METHOD_LENGTH.
     */
    public static final String METHOD_LENGTH = "length";
    /**
     * The constant METHOD_LONG_VALUE.
     */
    public static final String METHOD_LONG_VALUE = "longValue";
    /**
     * The constant METHOD_MULTIPLY.
     */
    public static final String METHOD_MULTIPLY = "multiply";
    /**
     * The constant METHOD_NEXT.
     */
    public static final String METHOD_NEXT = "next";
    /**
     * The constant METHOD_OR.
     */
    public static final String METHOD_OR = "or";
    /**
     * The constant METHOD_PARSE_INT.
     */
    public static final String METHOD_PARSE_INT = "parseInt";
    /**
     * The constant METHOD_POW.
     */
    public static final String METHOD_POW = "pow";
    /**
     * The constant METHOD_PUT.
     */
    public static final String METHOD_PUT = "put";
    /**
     * The constant METHOD_PUT_ALL.
     */
    public static final String METHOD_PUT_ALL = "putAll";
    /**
     * The constant METHOD_REMAINDER.
     */
    public static final String METHOD_REMAINDER = "remainder";
    /**
     * The constant METHOD_REMOVE.
     */
    public static final String METHOD_REMOVE = "remove";
    /**
     * The constant METHOD_SET.
     */
    public static final String METHOD_SET = "set";
    /**
     * The constant METHOD_SHIFT_LEFT.
     */
    public static final String METHOD_SHIFT_LEFT = "shiftLeft";
    /**
     * The constant METHOD_SHIFT_RIGHT.
     */
    public static final String METHOD_SHIFT_RIGHT = "shiftRight";
    /**
     * The constant METHOD_SHORT_VALUE.
     */
    public static final String METHOD_SHORT_VALUE = "shortValue";
    /**
     * The constant METHOD_SIZE.
     */
    public static final String METHOD_SIZE = "size";
    /**
     * The constant METHOD_SUBTRACT.
     */
    public static final String METHOD_SUBTRACT = "subtract";
    /**
     * The constant METHOD_TO_STRING.
     */
    public static final String METHOD_TO_STRING = "toString";
    /**
     * The constant METHOD_VALUE_OF.
     */
    public static final String METHOD_VALUE_OF = "valueOf";
    /**
     * The constant METHOD_XOR.
     */
    public static final String METHOD_XOR = "xor";
    private static final Set<String> INTEGER_PRIMITIVES = Set.of(ABBR_INTEGER, ABBR_LONG, ABBR_BYTE, ABBR_SHORT, ABBR_CHARACTER);
    private static final Set<String> NUMERIC_PRIMITIVES = Set.of(ABBR_INTEGER, ABBR_LONG, ABBR_FLOAT, ABBR_DOUBLE, ABBR_BYTE, ABBR_SHORT, ABBR_CHARACTER);
    private static final Set<String> PRIMITIVE_TYPES = Set.of(ABBR_INTEGER, ABBR_BOOLEAN, ABBR_BYTE, ABBR_CHARACTER, ABBR_SHORT, ABBR_LONG, ABBR_FLOAT, ABBR_DOUBLE);

    private TypeConversionUtils() {
    }

    /**
     * Box primitive type.
     *
     * @param code          the code
     * @param classWriter   the class writer
     * @param primitiveType the primitive type
     * @param targetType    the target type
     */
    public static void boxPrimitiveType(CodeBuilder code, ClassWriter classWriter, String primitiveType, String targetType) {
        var cp = classWriter.getConstantPool();
        // Only box if targetType is a wrapper
        if (!targetType.startsWith(LJAVA_LANG_)) {
            return; // Target is primitive, no boxing needed
        }

        switch (primitiveType) {
            case ABBR_INTEGER -> {
                if (LJAVA_LANG_INTEGER.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_INTEGER, METHOD_VALUE_OF, DESCRIPTER_I__LJAVA_LANG_INTEGER);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_BOOLEAN -> {
                if (LJAVA_LANG_BOOLEAN.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_BOOLEAN, METHOD_VALUE_OF, DESCRIPTER_Z__LJAVA_LANG_BOOLEAN);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_BYTE -> {
                if (LJAVA_LANG_BYTE.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_BYTE, METHOD_VALUE_OF, DESCRIPTER_B__LJAVA_LANG_BYTE);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_CHARACTER -> {
                if (LJAVA_LANG_CHARACTER.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_CHARACTER, METHOD_VALUE_OF, DESCRIPTER_C__LJAVA_LANG_CHARACTER);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_SHORT -> {
                if (LJAVA_LANG_SHORT.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_SHORT, METHOD_VALUE_OF, DESCRIPTER_S__LJAVA_LANG_SHORT);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_LONG -> {
                if (LJAVA_LANG_LONG.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_LONG, METHOD_VALUE_OF, DESCRIPTER_J__LJAVA_LANG_LONG);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_FLOAT -> {
                if (LJAVA_LANG_FLOAT.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_FLOAT, METHOD_VALUE_OF, DESCRIPTER_F__LJAVA_LANG_FLOAT);
                    code.invokestatic(valueOfRef);
                }
            }
            case ABBR_DOUBLE -> {
                if (LJAVA_LANG_DOUBLE.equals(targetType)) {
                    int valueOfRef = cp.addMethodRef(JAVA_LANG_DOUBLE, METHOD_VALUE_OF, DESCRIPTER_D__LJAVA_LANG_DOUBLE);
                    code.invokestatic(valueOfRef);
                }
            }
        }
    }

    /**
     * Convert primitive type.
     *
     * @param code     the code
     * @param fromType the from type
     * @param toType   the to type
     */
    public static void convertPrimitiveType(CodeBuilder code, String fromType, String toType) {
        if (fromType.equals(toType)) {
            return; // No conversion needed
        }

        // byte, short, char are stored as int on the stack, so they start as "I" after unboxing
        String stackFromType = switch (fromType) {
            case ABBR_BYTE, ABBR_SHORT, ABBR_CHARACTER -> ABBR_INTEGER;
            default -> fromType;
        };

        // Convert from stack type to target type
        switch (stackFromType) {
            case ABBR_INTEGER -> {
                switch (toType) {
                    case ABBR_BYTE -> code.i2b();
                    case ABBR_CHARACTER -> code.i2c();
                    case ABBR_SHORT -> code.i2s();
                    case ABBR_LONG -> code.i2l();
                    case ABBR_FLOAT -> code.i2f();
                    case ABBR_DOUBLE -> code.i2d();
                }
            }
            case ABBR_LONG -> {
                switch (toType) {
                    case ABBR_INTEGER -> code.l2i();
                    case ABBR_FLOAT -> code.l2f();
                    case ABBR_DOUBLE -> code.l2d();
                }
            }
            case ABBR_FLOAT -> {
                switch (toType) {
                    case ABBR_INTEGER -> code.f2i();
                    case ABBR_LONG -> code.f2l();
                    case ABBR_DOUBLE -> code.f2d();
                }
            }
            case ABBR_DOUBLE -> {
                switch (toType) {
                    case ABBR_INTEGER -> code.d2i();
                    case ABBR_LONG -> code.d2l();
                    case ABBR_FLOAT -> code.d2f();
                }
            }
        }
    }

    /**
     * Gets primitive type.
     *
     * @param type the type
     * @return the primitive type
     */
    public static String getPrimitiveType(String type) {
        return switch (type) {
            case LJAVA_LANG_BYTE -> ABBR_BYTE;
            case LJAVA_LANG_SHORT -> ABBR_SHORT;
            case LJAVA_LANG_INTEGER -> ABBR_INTEGER;
            case LJAVA_LANG_LONG -> ABBR_LONG;
            case LJAVA_LANG_FLOAT -> ABBR_FLOAT;
            case LJAVA_LANG_DOUBLE -> ABBR_DOUBLE;
            case LJAVA_LANG_CHARACTER -> ABBR_CHARACTER;
            default -> type;
        };
    }

    /**
     * Gets the result of the {@code typeof} operator for a statically-known type.
     * Returns {@code null} if the type requires runtime checking (e.g., generic Object).
     *
     * @param type the JVM type descriptor
     * @return the typeof result string, or null if runtime check is needed
     */
    public static String getTypeOfResult(String type) {
        if (type == null) return null;
        if (ABBR_VOID.equals(type)) return TYPEOF_UNDEFINED;
        if (LJAVA_LANG_STRING.equals(type)) return TYPEOF_STRING;
        if (LJAVA_MATH_BIGINTEGER.equals(type)) return TYPEOF_NUMBER;
        if (type.startsWith(ARRAY_PREFIX)) return TYPEOF_OBJECT;
        String primitiveType = getPrimitiveType(type);
        if (ABBR_BOOLEAN.equals(primitiveType)) return TYPEOF_BOOLEAN;
        if (isNumericPrimitive(primitiveType)) return TYPEOF_NUMBER;
        return null;
    }

    /**
     * Gets wrapper type.
     *
     * @param primitiveType the primitive type
     * @return the wrapper type
     */
    public static String getWrapperType(String primitiveType) {
        return switch (primitiveType) {
            case ABBR_BYTE -> LJAVA_LANG_BYTE;
            case ABBR_SHORT -> LJAVA_LANG_SHORT;
            case ABBR_INTEGER -> LJAVA_LANG_INTEGER;
            case ABBR_LONG -> LJAVA_LANG_LONG;
            case ABBR_FLOAT -> LJAVA_LANG_FLOAT;
            case ABBR_DOUBLE -> LJAVA_LANG_DOUBLE;
            case ABBR_CHARACTER -> LJAVA_LANG_CHARACTER;
            case ABBR_BOOLEAN -> LJAVA_LANG_BOOLEAN;
            default -> primitiveType; // Already a wrapper or reference type
        };
    }

    /**
     * Checks if the given primitive type descriptor is an integer-category type (I, J, B, S, C).
     *
     * @param primitiveType the primitive type descriptor
     * @return true if integer-category
     */
    public static boolean isIntegerPrimitive(String primitiveType) {
        return INTEGER_PRIMITIVES.contains(primitiveType);
    }

    /**
     * Checks if the given primitive type descriptor is a numeric type (I, J, F, D, B, S, C).
     *
     * @param primitiveType the primitive type descriptor
     * @return true if numeric
     */
    public static boolean isNumericPrimitive(String primitiveType) {
        return NUMERIC_PRIMITIVES.contains(primitiveType);
    }

    /**
     * Is primitive type boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isPrimitiveType(String type) {
        return PRIMITIVE_TYPES.contains(type);
    }

    /**
     * Pops a value from the stack based on its type descriptor.
     * No-op for void, {@code pop2} for long/double, {@code pop} for everything else.
     *
     * @param code the code builder
     * @param type the JVM type descriptor
     */
    public static void popByType(CodeBuilder code, String type) {
        if (ABBR_VOID.equals(type)) return;
        if (ABBR_LONG.equals(type) || ABBR_DOUBLE.equals(type)) {
            code.pop2();
        } else {
            code.pop();
        }
    }

    /**
     * Unbox wrapper type.
     *
     * @param code        the code
     * @param classWriter the class writer
     * @param type        the type
     */
    public static void unboxWrapperType(CodeBuilder code, ClassWriter classWriter, String type) {
        var cp = classWriter.getConstantPool();
        switch (type) {
            case LJAVA_LANG_BOOLEAN -> {
                int booleanValueRef = cp.addMethodRef(JAVA_LANG_BOOLEAN, METHOD_BOOLEAN_VALUE, DESCRIPTER___Z);
                code.invokevirtual(booleanValueRef);
            }
            case LJAVA_LANG_INTEGER -> {
                int intValueRef = cp.addMethodRef(JAVA_LANG_INTEGER, METHOD_INT_VALUE, DESCRIPTER___I);
                code.invokevirtual(intValueRef);
            }
            case LJAVA_LANG_CHARACTER -> {
                int charValueRef = cp.addMethodRef(JAVA_LANG_CHARACTER, METHOD_CHAR_VALUE, DESCRIPTER___C);
                code.invokevirtual(charValueRef);
            }
            case LJAVA_LANG_BYTE -> {
                int byteValueRef = cp.addMethodRef(JAVA_LANG_BYTE, METHOD_BYTE_VALUE, DESCRIPTER___B);
                code.invokevirtual(byteValueRef);
            }
            case LJAVA_LANG_LONG -> {
                int longValueRef = cp.addMethodRef(JAVA_LANG_LONG, METHOD_LONG_VALUE, DESCRIPTER___J);
                code.invokevirtual(longValueRef);
            }
            case LJAVA_LANG_SHORT -> {
                int shortValueRef = cp.addMethodRef(JAVA_LANG_SHORT, METHOD_SHORT_VALUE, DESCRIPTER___S);
                code.invokevirtual(shortValueRef);
            }
            case LJAVA_LANG_FLOAT -> {
                int floatValueRef = cp.addMethodRef(JAVA_LANG_FLOAT, METHOD_FLOAT_VALUE, DESCRIPTER___F);
                code.invokevirtual(floatValueRef);
            }
            case LJAVA_LANG_DOUBLE -> {
                int doubleValueRef = cp.addMethodRef(JAVA_LANG_DOUBLE, METHOD_DOUBLE_VALUE, DESCRIPTER___D);
                code.invokevirtual(doubleValueRef);
            }
        }
    }
}
