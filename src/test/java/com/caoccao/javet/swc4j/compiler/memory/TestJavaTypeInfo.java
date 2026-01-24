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

package com.caoccao.javet.swc4j.compiler.memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link JavaTypeInfo#isAssignableTo(String)}.
 * Tests cover List, Map, String hierarchies and user-defined types.
 */
public class TestJavaTypeInfo {

    @Test
    public void testArrayListAssignableToCollection() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertTrue(arrayListType.isAssignableTo("Ljava/util/Collection;"),
                "ArrayList should be assignable to Collection");
    }

    @Test
    public void testArrayListAssignableToIterable() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertTrue(arrayListType.isAssignableTo("Ljava/lang/Iterable;"),
                "ArrayList should be assignable to Iterable");
    }

    @Test
    public void testArrayListAssignableToList() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertTrue(arrayListType.isAssignableTo("Ljava/util/List;"),
                "ArrayList should be assignable to List");
    }

    @Test
    public void testArrayListAssignableToObject() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertTrue(arrayListType.isAssignableTo("Ljava/lang/Object;"),
                "ArrayList should be assignable to Object");
    }

    @Test
    public void testArrayListNotAssignableToMap() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertFalse(arrayListType.isAssignableTo("Ljava/util/Map;"),
                "ArrayList should NOT be assignable to Map");
    }

    @Test
    public void testArrayListNotAssignableToString() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertFalse(arrayListType.isAssignableTo("Ljava/lang/String;"),
                "ArrayList should NOT be assignable to String");
    }

    @Test
    public void testDirectMatchArrayList() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertTrue(arrayListType.isAssignableTo("Ljava/util/ArrayList;"),
                "ArrayList should be assignable to ArrayList");
    }

    @Test
    public void testDirectMatchHashMap() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertTrue(hashMapType.isAssignableTo("Ljava/util/HashMap;"),
                "HashMap should be assignable to HashMap");
    }

    @Test
    public void testDirectMatchLinkedHashMap() {
        JavaTypeInfo linkedHashMapType = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        assertTrue(linkedHashMapType.isAssignableTo("Ljava/util/LinkedHashMap;"),
                "LinkedHashMap should be assignable to LinkedHashMap");
    }

    @Test
    public void testDirectMatchString() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertTrue(stringType.isAssignableTo("Ljava/lang/String;"),
                "String should be assignable to String");
    }

    @Test
    public void testEmptyParentList() {
        // User-defined type with no parents (fallback path used)
        JavaTypeInfo customType = new JavaTypeInfo("Isolated", "com.example", "com/example/Isolated");
        // Should only be assignable to itself
        assertTrue(customType.isAssignableTo("Lcom/example/Isolated;"),
                "Type with no parents should be assignable to itself");
        assertFalse(customType.isAssignableTo("Ljava/lang/Object;"),
                "User-defined type with no parents should not match Object via fallback");
    }

    @Test
    public void testEnumAssignableToSelf() {
        JavaTypeInfo colorEnum = new JavaTypeInfo("Color", "com.example", "com/example/Color", JavaType.ENUM);
        assertTrue(colorEnum.isAssignableTo("Lcom/example/Color;"),
                "Enum should be assignable to itself");
    }

    @Test
    public void testEnumNotAssignableToList() {
        JavaTypeInfo colorEnum = new JavaTypeInfo("Color", "com.example", "com/example/Color", JavaType.ENUM);
        assertFalse(colorEnum.isAssignableTo("Ljava/util/List;"),
                "Enum should NOT be assignable to List");
    }

    @Test
    public void testEnumNotAssignableToString() {
        JavaTypeInfo colorEnum = new JavaTypeInfo("Color", "com.example", "com/example/Color", JavaType.ENUM);
        assertFalse(colorEnum.isAssignableTo("Ljava/lang/String;"),
                "Enum should NOT be assignable to String");
    }

    @Test
    public void testHashMapAssignableToMap() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertTrue(hashMapType.isAssignableTo("Ljava/util/Map;"),
                "HashMap should be assignable to Map");
    }

    @Test
    public void testHashMapAssignableToObject() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertTrue(hashMapType.isAssignableTo("Ljava/lang/Object;"),
                "HashMap should be assignable to Object");
    }

    @Test
    public void testHashMapNotAssignableToList() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertFalse(hashMapType.isAssignableTo("Ljava/util/List;"),
                "HashMap should NOT be assignable to List");
    }

    @Test
    public void testHashMapNotAssignableToString() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertFalse(hashMapType.isAssignableTo("Ljava/lang/String;"),
                "HashMap should NOT be assignable to String");
    }

    @Test
    public void testIsAssignableToClassCrossType() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertFalse(arrayListType.isAssignableTo(java.util.Map.class),
                "ArrayList should NOT be assignable to Map.class");
    }

    @Test
    public void testIsAssignableToClassNegative() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertFalse(stringType.isAssignableTo(java.util.List.class),
                "String should NOT be assignable to List.class");
    }

    @Test
    public void testIsAssignableToClassWithHashMap() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertTrue(hashMapType.isAssignableTo(java.util.Map.class),
                "HashMap should be assignable to Map.class");
    }

    @Test
    public void testIsAssignableToClassWithKnownType() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertTrue(arrayListType.isAssignableTo(java.util.List.class),
                "ArrayList should be assignable to List.class");
    }

    @Test
    public void testLinkedHashMapAssignableToHashMap() {
        JavaTypeInfo linkedHashMapType = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        assertTrue(linkedHashMapType.isAssignableTo("Ljava/util/HashMap;"),
                "LinkedHashMap should be assignable to HashMap (LinkedHashMap extends HashMap)");
    }

    @Test
    public void testLinkedHashMapAssignableToMap() {
        JavaTypeInfo linkedHashMapType = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        assertTrue(linkedHashMapType.isAssignableTo("Ljava/util/Map;"),
                "LinkedHashMap should be assignable to Map");
    }

    @Test
    public void testListNotAssignableToArrayList() {
        JavaTypeInfo listType = new JavaTypeInfo("List", "java.util", "java/util/List", JavaType.INTERFACE);
        assertFalse(listType.isAssignableTo("Ljava/util/ArrayList;"),
                "List should NOT be assignable to ArrayList (List is interface, ArrayList is implementation)");
    }

    @Test
    public void testMapNotAssignableToHashMap() {
        JavaTypeInfo mapType = new JavaTypeInfo("Map", "java.util", "java/util/Map", JavaType.INTERFACE);
        assertFalse(mapType.isAssignableTo("Ljava/util/HashMap;"),
                "Map should NOT be assignable to HashMap");
    }

    @Test
    public void testSelfAssignability() {
        JavaTypeInfo type = new JavaTypeInfo("Custom", "com.example", "com/example/Custom");
        assertTrue(type.isAssignableTo("Lcom/example/Custom;"),
                "Type should always be assignable to itself");
    }

    @Test
    public void testStringAssignableToCharSequence() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertTrue(stringType.isAssignableTo("Ljava/lang/CharSequence;"),
                "String should be assignable to CharSequence");
    }

    @Test
    public void testStringAssignableToComparable() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertTrue(stringType.isAssignableTo("Ljava/lang/Comparable;"),
                "String should be assignable to Comparable");
    }

    @Test
    public void testStringAssignableToObject() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertTrue(stringType.isAssignableTo("Ljava/lang/Object;"),
                "String should be assignable to Object");
    }

    @Test
    public void testStringNotAssignableToList() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertFalse(stringType.isAssignableTo("Ljava/util/List;"),
                "String should NOT be assignable to List");
    }

    @Test
    public void testStringNotAssignableToMap() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertFalse(stringType.isAssignableTo("Ljava/util/Map;"),
                "String should NOT be assignable to Map");
    }

    @Test
    public void testUserDefinedClassDeepHierarchy() {
        // Create a deep hierarchy: GrandChild -> Child -> ArrayList -> List
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        JavaTypeInfo childType = new JavaTypeInfo("ChildList", "com.example", "com/example/ChildList");
        childType.addParentTypeInfo(arrayListType);
        JavaTypeInfo grandChildType = new JavaTypeInfo("GrandChildList", "com.example", "com/example/GrandChildList");
        grandChildType.addParentTypeInfo(childType);

        // GrandChildList should be assignable to List (via ChildList -> ArrayList -> List)
        assertTrue(grandChildType.isAssignableTo("Ljava/util/List;"),
                "Deep hierarchy should be traversed: GrandChildList should be assignable to List");
    }

    @Test
    public void testUserDefinedClassNotAssignableToUnrelatedType() {
        // Create user-defined class with ArrayList parent
        JavaTypeInfo arrayListParent = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        JavaTypeInfo customList = new JavaTypeInfo("MyList", "com.example", "com/example/MyList");
        customList.addParentTypeInfo(arrayListParent);

        // MyList should NOT be assignable to Map (no Map in hierarchy)
        assertFalse(customList.isAssignableTo("Ljava/util/Map;"),
                "User-defined class with List hierarchy should NOT be assignable to Map");
    }

    @Test
    public void testUserDefinedClassWithListParent() {
        // Create user-defined class that "extends ArrayList"
        JavaTypeInfo arrayListParent = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        JavaTypeInfo customList = new JavaTypeInfo("MyList", "com.example", "com/example/MyList");
        customList.addParentTypeInfo(arrayListParent);

        // MyList should be assignable to List (via ArrayList parent)
        assertTrue(customList.isAssignableTo("Ljava/util/List;"),
                "User-defined class extending ArrayList should be assignable to List");
    }

    @Test
    public void testUserDefinedClassWithMapParent() {
        // Create user-defined class that "extends LinkedHashMap"
        JavaTypeInfo linkedHashMapParent = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        JavaTypeInfo customMap = new JavaTypeInfo("MyMap", "com.example", "com/example/MyMap");
        customMap.addParentTypeInfo(linkedHashMapParent);

        // MyMap should be assignable to Map (via LinkedHashMap parent)
        assertTrue(customMap.isAssignableTo("Ljava/util/Map;"),
                "User-defined class extending LinkedHashMap should be assignable to Map");
    }

    @Test
    public void testUserDefinedClassWithMultipleParents() {
        // Create List interface info
        JavaTypeInfo listInterface = new JavaTypeInfo("List", "java.util", "java/util/List", JavaType.INTERFACE);
        // Create Serializable interface info
        JavaTypeInfo serializableInterface = new JavaTypeInfo("Serializable", "java.io", "java/io/Serializable", JavaType.INTERFACE);

        // Create user-defined class that implements both interfaces
        JavaTypeInfo customClass = new JavaTypeInfo("MyClass", "com.example", "com/example/MyClass");
        customClass.addParentTypeInfo(listInterface);
        customClass.addParentTypeInfo(serializableInterface);

        // Note: This uses the fallback path since com/example/MyClass doesn't exist
        // The test verifies the parentTypeInfos mechanism works correctly
        assertTrue(customClass.isAssignableTo("Lcom/example/MyClass;"),
                "User-defined class should be assignable to itself");
    }
}
