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

package com.caoccao.javet.swc4j.compiler.constants;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Java type constants for bytecode generation.
 * <p>
 * This class contains constants for:
 * <ul>
 * <li>Primitive type abbreviations (ABBR_*)</li>
 * <li>Array type descriptors (ARRAY_*)</li>
 * <li>Java class internal names without L prefix (JAVA_LANG_*, JAVA_UTIL_*, JAVA_MATH_*)</li>
 * <li>Java type descriptors with L prefix and semicolon (LJAVA_LANG_*, LJAVA_UTIL_*, LJAVA_MATH_*)</li>
 * <li>JavaScript typeof operator result strings (TYPEOF_*)</li>
 * </ul>
 */
public final class ConstantJavaType {
    /**
     * The constant ABBR_BOOLEAN - primitive boolean type descriptor.
     */
    public static final String ABBR_BOOLEAN = "Z";
    /**
     * The constant ABBR_BYTE - primitive byte type descriptor.
     */
    public static final String ABBR_BYTE = "B";
    /**
     * The constant ABBR_CHARACTER - primitive char type descriptor.
     */
    public static final String ABBR_CHARACTER = "C";
    /**
     * The constant ABBR_DOUBLE - primitive double type descriptor.
     */
    public static final String ABBR_DOUBLE = "D";
    /**
     * The constant ABBR_FLOAT - primitive float type descriptor.
     */
    public static final String ABBR_FLOAT = "F";
    /**
     * The constant ABBR_INTEGER - primitive int type descriptor.
     */
    public static final String ABBR_INTEGER = "I";
    /**
     * The constant ABBR_LONG - primitive long type descriptor.
     */
    public static final String ABBR_LONG = "J";
    /**
     * The constant ABBR_SHORT - primitive short type descriptor.
     */
    public static final String ABBR_SHORT = "S";
    /**
     * The constant ABBR_VOID - void type descriptor.
     */
    public static final String ABBR_VOID = "V";
    /**
     * The constant ARRAY_ARRAY_LJAVA_LANG_STRING - 2D String array type descriptor.
     */
    public static final String ARRAY_ARRAY_LJAVA_LANG_STRING = "[[Ljava/lang/String;";
    /**
     * The constant ARRAY_I - int array type descriptor.
     */
    public static final String ARRAY_I = "[I";
    /**
     * The constant ARRAY_J - long array type descriptor.
     */
    public static final String ARRAY_J = "[J";
    /**
     * The constant ARRAY_LJAVA_LANG_OBJECT - Object array type descriptor.
     */
    public static final String ARRAY_LJAVA_LANG_OBJECT = "[Ljava/lang/Object;";
    /**
     * The constant ARRAY_LJAVA_LANG_STRING - String array type descriptor.
     */
    public static final String ARRAY_LJAVA_LANG_STRING = "[Ljava/lang/String;";
    /**
     * The constant ARRAY_PREFIX - array type prefix.
     */
    public static final String ARRAY_PREFIX = "[";
    /**
     * The constant CHAR_ARRAY - array type descriptor character.
     */
    public static final char CHAR_ARRAY = '[';
    /**
     * The constant CHAR_BOOLEAN - primitive boolean type descriptor character.
     */
    public static final char CHAR_BOOLEAN = 'Z';
    /**
     * The constant CHAR_BYTE - primitive byte type descriptor character.
     */
    public static final char CHAR_BYTE = 'B';
    /**
     * The constant CHAR_CHARACTER - primitive char type descriptor character.
     */
    public static final char CHAR_CHARACTER = 'C';
    /**
     * The constant CHAR_DOUBLE - primitive double type descriptor character.
     */
    public static final char CHAR_DOUBLE = 'D';
    /**
     * The constant CHAR_FLOAT - primitive float type descriptor character.
     */
    public static final char CHAR_FLOAT = 'F';
    /**
     * The constant CHAR_INTEGER - primitive int type descriptor character.
     */
    public static final char CHAR_INTEGER = 'I';
    /**
     * The constant CHAR_LONG - primitive long type descriptor character.
     */
    public static final char CHAR_LONG = 'J';
    /**
     * The constant CHAR_REFERENCE - reference type descriptor character.
     */
    public static final char CHAR_REFERENCE = 'L';
    /**
     * The constant CHAR_SHORT - primitive short type descriptor character.
     */
    public static final char CHAR_SHORT = 'S';
    /**
     * The constant CHAR_VOID - void type descriptor character.
     */
    public static final char CHAR_VOID = 'V';
    /**
     * The constant CLASS_JAVA_LANG_BOOLEAN - Boolean fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_BOOLEAN = Boolean.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_BYTE - Byte fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_BYTE = Byte.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_CHARACTER - Character fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_CHARACTER = Character.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_DOUBLE - Double fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_DOUBLE = Double.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_FLOAT - Float fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_FLOAT = Float.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_INTEGER - Integer fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_INTEGER = Integer.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_LONG - Long fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_LONG = Long.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_NUMBER - Number fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_NUMBER = Number.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_OBJECT - Object fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_OBJECT = Object.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_SHORT - Short fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_SHORT = Short.class.getName();
    /**
     * The constant CLASS_JAVA_LANG_STRING - String fully qualified class name.
     */
    public static final String CLASS_JAVA_LANG_STRING = String.class.getName();
    /**
     * The constant CLASS_JAVA_MATH_BIGINTEGER - BigInteger fully qualified class name.
     */
    public static final String CLASS_JAVA_MATH_BIGINTEGER = BigInteger.class.getName();
    /**
     * The constant CLASS_JAVA_UTIL_REGEX_PATTERN - Pattern fully qualified class name.
     */
    public static final String CLASS_JAVA_UTIL_REGEX_PATTERN = Pattern.class.getName();
    /**
     * The constant JAVA_LANG_AUTOCLOSEABLE - AutoCloseable class internal name.
     */
    public static final String JAVA_LANG_AUTOCLOSEABLE = AutoCloseable.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_BOOLEAN - Boolean class internal name.
     */
    public static final String JAVA_LANG_BOOLEAN = Boolean.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_BYTE - Byte class internal name.
     */
    public static final String JAVA_LANG_BYTE = Byte.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_CHARACTER - Character class internal name.
     */
    public static final String JAVA_LANG_CHARACTER = Character.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_CLASS - Class class internal name.
     */
    public static final String JAVA_LANG_CLASS = Class.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_DOUBLE - Double class internal name.
     */
    public static final String JAVA_LANG_DOUBLE = Double.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_ENUM - Enum class internal name.
     */
    public static final String JAVA_LANG_ENUM = Enum.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_FLOAT - Float class internal name.
     */
    public static final String JAVA_LANG_FLOAT = Float.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_ILLEGALARGUMENTEXCEPTION - IllegalArgumentException class internal name.
     */
    public static final String JAVA_LANG_ILLEGALARGUMENTEXCEPTION = IllegalArgumentException.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_INTEGER - Integer class internal name.
     */
    public static final String JAVA_LANG_INTEGER = Integer.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_ITERABLE - Iterable class internal name.
     */
    public static final String JAVA_LANG_ITERABLE = Iterable.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_LONG - Long class internal name.
     */
    public static final String JAVA_LANG_LONG = Long.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_MATH - Math class internal name.
     */
    public static final String JAVA_LANG_MATH = Math.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_NUMBER - Number class internal name.
     */
    public static final String JAVA_LANG_NUMBER = Number.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_NUMBERFORMATEXCEPTION - NumberFormatException class internal name.
     */
    public static final String JAVA_LANG_NUMBERFORMATEXCEPTION = NumberFormatException.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_OBJECT - Object class internal name.
     */
    public static final String JAVA_LANG_OBJECT = Object.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_RUNNABLE - Runnable class internal name.
     */
    public static final String JAVA_LANG_RUNNABLE = Runnable.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_SHORT - Short class internal name.
     */
    public static final String JAVA_LANG_SHORT = Short.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_STRING - String class internal name.
     */
    public static final String JAVA_LANG_STRING = String.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_STRINGBUILDER - StringBuilder class internal name.
     */
    public static final String JAVA_LANG_STRINGBUILDER = StringBuilder.class.getName().replace('.', '/');
    /**
     * The constant JAVA_LANG_THROWABLE - Throwable class internal name.
     */
    public static final String JAVA_LANG_THROWABLE = Throwable.class.getName().replace('.', '/');
    /**
     * The constant JAVA_MATH_BIGINTEGER - BigInteger class internal name.
     */
    public static final String JAVA_MATH_BIGINTEGER = BigInteger.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_ARRAYLIST - ArrayList class internal name.
     */
    public static final String JAVA_UTIL_ARRAYLIST = ArrayList.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_ARRAYS - Arrays class internal name.
     */
    public static final String JAVA_UTIL_ARRAYS = Arrays.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_COLLECTIONS - Collections class internal name.
     */
    public static final String JAVA_UTIL_COLLECTIONS = Collections.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_HASHMAP - HashMap class internal name.
     */
    public static final String JAVA_UTIL_HASHMAP = HashMap.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_ITERATOR - Iterator class internal name.
     */
    public static final String JAVA_UTIL_ITERATOR = Iterator.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_LINKEDHASHMAP - LinkedHashMap class internal name.
     */
    public static final String JAVA_UTIL_LINKEDHASHMAP = LinkedHashMap.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_LINKEDLIST - LinkedList class internal name.
     */
    public static final String JAVA_UTIL_LINKEDLIST = LinkedList.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_LIST - List class internal name.
     */
    public static final String JAVA_UTIL_LIST = List.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_MAP - Map class internal name.
     */
    public static final String JAVA_UTIL_MAP = Map.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_MAP_ENTRY - Map.Entry class internal name.
     */
    public static final String JAVA_UTIL_MAP_ENTRY = Map.Entry.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_OBJECTS - Objects class internal name.
     */
    public static final String JAVA_UTIL_OBJECTS = Objects.class.getName().replace('.', '/');
    /**
     * The constant JAVA_UTIL_SET - Set class internal name.
     */
    public static final String JAVA_UTIL_SET = Set.class.getName().replace('.', '/');
    /**
     * The constant LJAVA_LANG_ - common prefix for java.lang type descriptors.
     */
    public static final String LJAVA_LANG_ = "Ljava/lang/";
    /**
     * The constant LJAVA_LANG_AUTOCLOSEABLE - AutoCloseable type descriptor.
     */
    public static final String LJAVA_LANG_AUTOCLOSEABLE = "Ljava/lang/AutoCloseable;";
    /**
     * The constant LJAVA_LANG_BOOLEAN - Boolean type descriptor.
     */
    public static final String LJAVA_LANG_BOOLEAN = "Ljava/lang/Boolean;";
    /**
     * The constant LJAVA_LANG_BYTE - Byte type descriptor.
     */
    public static final String LJAVA_LANG_BYTE = "Ljava/lang/Byte;";
    /**
     * The constant LJAVA_LANG_CHARACTER - Character type descriptor.
     */
    public static final String LJAVA_LANG_CHARACTER = "Ljava/lang/Character;";
    /**
     * The constant LJAVA_LANG_CLASS - Class type descriptor.
     */
    public static final String LJAVA_LANG_CLASS = "Ljava/lang/Class;";
    /**
     * The constant LJAVA_LANG_DOUBLE - Double type descriptor.
     */
    public static final String LJAVA_LANG_DOUBLE = "Ljava/lang/Double;";
    /**
     * The constant LJAVA_LANG_FLOAT - Float type descriptor.
     */
    public static final String LJAVA_LANG_FLOAT = "Ljava/lang/Float;";
    /**
     * The constant LJAVA_LANG_INTEGER - Integer type descriptor.
     */
    public static final String LJAVA_LANG_INTEGER = "Ljava/lang/Integer;";
    /**
     * The constant LJAVA_LANG_LONG - Long type descriptor.
     */
    public static final String LJAVA_LANG_LONG = "Ljava/lang/Long;";
    /**
     * The constant LJAVA_LANG_NUMBER - Number type descriptor.
     */
    public static final String LJAVA_LANG_NUMBER = "Ljava/lang/Number;";
    /**
     * The constant LJAVA_LANG_OBJECT - Object type descriptor.
     */
    public static final String LJAVA_LANG_OBJECT = "Ljava/lang/Object;";
    /**
     * The constant LJAVA_LANG_SHORT - Short type descriptor.
     */
    public static final String LJAVA_LANG_SHORT = "Ljava/lang/Short;";
    /**
     * The constant LJAVA_LANG_STRING - String type descriptor.
     */
    public static final String LJAVA_LANG_STRING = "Ljava/lang/String;";
    /**
     * The constant LJAVA_LANG_THROWABLE - Throwable type descriptor.
     */
    public static final String LJAVA_LANG_THROWABLE = "Ljava/lang/Throwable;";
    /**
     * The constant LJAVA_LANG_VOID - Void type descriptor.
     */
    public static final String LJAVA_LANG_VOID = "Ljava/lang/Void;";
    /**
     * The constant LJAVA_MATH_BIGINTEGER - BigInteger type descriptor.
     */
    public static final String LJAVA_MATH_BIGINTEGER = "Ljava/math/BigInteger;";
    /**
     * The constant LJAVA_UTIL_ARRAYLIST - ArrayList type descriptor.
     */
    public static final String LJAVA_UTIL_ARRAYLIST = "Ljava/util/ArrayList;";
    /**
     * The constant LJAVA_UTIL_HASHMAP - HashMap type descriptor.
     */
    public static final String LJAVA_UTIL_HASHMAP = "Ljava/util/HashMap;";
    /**
     * The constant LJAVA_UTIL_ITERATOR - Iterator type descriptor.
     */
    public static final String LJAVA_UTIL_ITERATOR = "Ljava/util/Iterator;";
    /**
     * The constant LJAVA_UTIL_LINKEDHASHMAP - LinkedHashMap type descriptor.
     */
    public static final String LJAVA_UTIL_LINKEDHASHMAP = "Ljava/util/LinkedHashMap;";
    /**
     * The constant LJAVA_UTIL_LINKEDLIST - LinkedList type descriptor.
     */
    public static final String LJAVA_UTIL_LINKEDLIST = "Ljava/util/LinkedList;";
    /**
     * The constant LJAVA_UTIL_LIST - List type descriptor.
     */
    public static final String LJAVA_UTIL_LIST = "Ljava/util/List;";
    /**
     * The constant LJAVA_UTIL_MAP - Map type descriptor.
     */
    public static final String LJAVA_UTIL_MAP = "Ljava/util/Map;";
    /**
     * The constant LJAVA_UTIL_MAP_ENTRY - Map.Entry type descriptor.
     */
    public static final String LJAVA_UTIL_MAP_ENTRY = "Ljava/util/Map$Entry;";
    /**
     * The constant LJAVA_UTIL_SET - Set type descriptor.
     */
    public static final String LJAVA_UTIL_SET = "Ljava/util/Set;";
    /**
     * The constant PRIMITIVE_BOOLEAN - Java primitive type name for boolean.
     */
    public static final String PRIMITIVE_BOOLEAN = "boolean";
    /**
     * The constant PRIMITIVE_BYTE - Java primitive type name for byte.
     */
    public static final String PRIMITIVE_BYTE = "byte";
    /**
     * The constant PRIMITIVE_CHAR - Java primitive type name for char.
     */
    public static final String PRIMITIVE_CHAR = "char";
    /**
     * The constant PRIMITIVE_DOUBLE - Java primitive type name for double.
     */
    public static final String PRIMITIVE_DOUBLE = "double";
    /**
     * The constant PRIMITIVE_FLOAT - Java primitive type name for float.
     */
    public static final String PRIMITIVE_FLOAT = "float";
    /**
     * The constant PRIMITIVE_INT - Java primitive type name for int.
     */
    public static final String PRIMITIVE_INT = "int";
    /**
     * The constant PRIMITIVE_LONG - Java primitive type name for long.
     */
    public static final String PRIMITIVE_LONG = "long";
    /**
     * The constant PRIMITIVE_SHORT - Java primitive type name for short.
     */
    public static final String PRIMITIVE_SHORT = "short";
    /**
     * The constant PRIMITIVE_VOID - Java primitive type name for void.
     */
    public static final String PRIMITIVE_VOID = "void";
    /**
     * The constant SIMPLE_BIGINTEGER - BigInteger simple class name.
     */
    public static final String SIMPLE_BIGINTEGER = BigInteger.class.getSimpleName();
    /**
     * The constant SIMPLE_BOOLEAN - Boolean simple class name.
     */
    public static final String SIMPLE_BOOLEAN = Boolean.class.getSimpleName();
    /**
     * The constant SIMPLE_BYTE - Byte simple class name.
     */
    public static final String SIMPLE_BYTE = Byte.class.getSimpleName();
    /**
     * The constant SIMPLE_CHARACTER - Character simple class name.
     */
    public static final String SIMPLE_CHARACTER = Character.class.getSimpleName();
    /**
     * The constant SIMPLE_DOUBLE - Double simple class name.
     */
    public static final String SIMPLE_DOUBLE = Double.class.getSimpleName();
    /**
     * The constant SIMPLE_FLOAT - Float simple class name.
     */
    public static final String SIMPLE_FLOAT = Float.class.getSimpleName();
    /**
     * The constant SIMPLE_INTEGER - Integer simple class name.
     */
    public static final String SIMPLE_INTEGER = Integer.class.getSimpleName();
    /**
     * The constant SIMPLE_LONG - Long simple class name.
     */
    public static final String SIMPLE_LONG = Long.class.getSimpleName();
    /**
     * The constant SIMPLE_NUMBER - Number simple class name.
     */
    public static final String SIMPLE_NUMBER = Number.class.getSimpleName();
    /**
     * The constant SIMPLE_OBJECT - Object simple class name.
     */
    public static final String SIMPLE_OBJECT = Object.class.getSimpleName();
    /**
     * The constant SIMPLE_PATTERN - Pattern simple class name.
     */
    public static final String SIMPLE_PATTERN = Pattern.class.getSimpleName();
    /**
     * The constant SIMPLE_SHORT - Short simple class name.
     */
    public static final String SIMPLE_SHORT = Short.class.getSimpleName();
    /**
     * The constant SIMPLE_STRING - String simple class name.
     */
    public static final String SIMPLE_STRING = String.class.getSimpleName();
    /**
     * The constant TYPEOF_BOOLEAN - JavaScript typeof result for boolean values.
     */
    public static final String TYPEOF_BOOLEAN = "boolean";
    /**
     * The constant TYPEOF_NUMBER - JavaScript typeof result for numeric values.
     */
    public static final String TYPEOF_NUMBER = "number";
    /**
     * The constant TYPEOF_OBJECT - JavaScript typeof result for object values.
     */
    public static final String TYPEOF_OBJECT = "object";
    /**
     * The constant TYPEOF_STRING - JavaScript typeof result for string values.
     */
    public static final String TYPEOF_STRING = "string";
    /**
     * The constant TYPEOF_UNDEFINED - JavaScript typeof result for undefined values.
     */
    public static final String TYPEOF_UNDEFINED = "undefined";
    /**
     * The constant TYPE_ALIAS_ANY - TypeScript 'any' type alias.
     */
    public static final String TYPE_ALIAS_ANY = "any";
    /**
     * The constant TYPE_ALIAS_NUMBER - TypeScript 'number' type alias (lowercase).
     */
    public static final String TYPE_ALIAS_NUMBER = "number";
    /**
     * The constant TYPE_ALIAS_UNKNOWN - TypeScript 'unknown' type alias.
     */
    public static final String TYPE_ALIAS_UNKNOWN = "unknown";

    private ConstantJavaType() {
    }
}
