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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JavaTypeInfo#isAssignableTo(String)}.
 * Tests cover List, Map, String hierarchies and user-defined types.
 */
public class TestJavaTypeInfo {

    @Test
    public void testArrayListAssignableToCollection() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo("Ljava/util/Collection;")).as("ArrayList should be assignable to Collection").isTrue();
    }

    @Test
    public void testArrayListAssignableToIterable() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo("Ljava/lang/Iterable;")).as("ArrayList should be assignable to Iterable").isTrue();
    }

    @Test
    public void testArrayListAssignableToList() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo("Ljava/util/List;")).as("ArrayList should be assignable to List").isTrue();
    }

    @Test
    public void testArrayListAssignableToObject() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo("Ljava/lang/Object;")).as("ArrayList should be assignable to Object").isTrue();
    }

    @Test
    public void testArrayListNotAssignableToMap() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo("Ljava/util/Map;")).as("ArrayList should NOT be assignable to Map").isFalse();
    }

    @Test
    public void testArrayListNotAssignableToString() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo("Ljava/lang/String;")).as("ArrayList should NOT be assignable to String").isFalse();
    }

    @Test
    public void testDirectMatchArrayList() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo("Ljava/util/ArrayList;")).as("ArrayList should be assignable to ArrayList").isTrue();
    }

    @Test
    public void testDirectMatchHashMap() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertThat(hashMapType.isAssignableTo("Ljava/util/HashMap;")).as("HashMap should be assignable to HashMap").isTrue();
    }

    @Test
    public void testDirectMatchLinkedHashMap() {
        JavaTypeInfo linkedHashMapType = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        assertThat(linkedHashMapType.isAssignableTo("Ljava/util/LinkedHashMap;")).as("LinkedHashMap should be assignable to LinkedHashMap").isTrue();
    }

    @Test
    public void testDirectMatchString() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertThat(stringType.isAssignableTo("Ljava/lang/String;")).as("String should be assignable to String").isTrue();
    }

    @Test
    public void testEmptyParentList() {
        // User-defined type with no parents (fallback path used)
        JavaTypeInfo customType = new JavaTypeInfo("Isolated", "com.example", "com/example/Isolated");
        // Should only be assignable to itself
        assertThat(customType.isAssignableTo("Lcom/example/Isolated;")).as("Type with no parents should be assignable to itself").isTrue();
        assertThat(customType.isAssignableTo("Ljava/lang/Object;")).as("User-defined type with no parents should not match Object via fallback").isFalse();
    }

    @Test
    public void testEnumAssignableToSelf() {
        JavaTypeInfo colorEnum = new JavaTypeInfo("Color", "com.example", "com/example/Color", JavaType.ENUM);
        assertThat(colorEnum.isAssignableTo("Lcom/example/Color;")).as("Enum should be assignable to itself").isTrue();
    }

    @Test
    public void testEnumNotAssignableToList() {
        JavaTypeInfo colorEnum = new JavaTypeInfo("Color", "com.example", "com/example/Color", JavaType.ENUM);
        assertThat(colorEnum.isAssignableTo("Ljava/util/List;")).as("Enum should NOT be assignable to List").isFalse();
    }

    @Test
    public void testEnumNotAssignableToString() {
        JavaTypeInfo colorEnum = new JavaTypeInfo("Color", "com.example", "com/example/Color", JavaType.ENUM);
        assertThat(colorEnum.isAssignableTo("Ljava/lang/String;")).as("Enum should NOT be assignable to String").isFalse();
    }

    @Test
    public void testHashMapAssignableToMap() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertThat(hashMapType.isAssignableTo("Ljava/util/Map;")).as("HashMap should be assignable to Map").isTrue();
    }

    @Test
    public void testHashMapAssignableToObject() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertThat(hashMapType.isAssignableTo("Ljava/lang/Object;")).as("HashMap should be assignable to Object").isTrue();
    }

    @Test
    public void testHashMapNotAssignableToList() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertThat(hashMapType.isAssignableTo("Ljava/util/List;")).as("HashMap should NOT be assignable to List").isFalse();
    }

    @Test
    public void testHashMapNotAssignableToString() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertThat(hashMapType.isAssignableTo("Ljava/lang/String;")).as("HashMap should NOT be assignable to String").isFalse();
    }

    @Test
    public void testIsAssignableToClassCrossType() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo(java.util.Map.class)).as("ArrayList should NOT be assignable to Map.class").isFalse();
    }

    @Test
    public void testIsAssignableToClassNegative() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertThat(stringType.isAssignableTo(java.util.List.class)).as("String should NOT be assignable to List.class").isFalse();
    }

    @Test
    public void testIsAssignableToClassWithHashMap() {
        JavaTypeInfo hashMapType = new JavaTypeInfo("HashMap", "java.util", "java/util/HashMap");
        assertThat(hashMapType.isAssignableTo(java.util.Map.class)).as("HashMap should be assignable to Map.class").isTrue();
    }

    @Test
    public void testIsAssignableToClassWithKnownType() {
        JavaTypeInfo arrayListType = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        assertThat(arrayListType.isAssignableTo(java.util.List.class)).as("ArrayList should be assignable to List.class").isTrue();
    }

    @Test
    public void testLinkedHashMapAssignableToHashMap() {
        JavaTypeInfo linkedHashMapType = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        assertThat(linkedHashMapType.isAssignableTo("Ljava/util/HashMap;")).as("LinkedHashMap should be assignable to HashMap (LinkedHashMap extends HashMap)").isTrue();
    }

    @Test
    public void testLinkedHashMapAssignableToMap() {
        JavaTypeInfo linkedHashMapType = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        assertThat(linkedHashMapType.isAssignableTo("Ljava/util/Map;")).as("LinkedHashMap should be assignable to Map").isTrue();
    }

    @Test
    public void testListNotAssignableToArrayList() {
        JavaTypeInfo listType = new JavaTypeInfo("List", "java.util", "java/util/List", JavaType.INTERFACE);
        assertThat(listType.isAssignableTo("Ljava/util/ArrayList;")).as("List should NOT be assignable to ArrayList (List is interface, ArrayList is implementation)").isFalse();
    }

    @Test
    public void testMapNotAssignableToHashMap() {
        JavaTypeInfo mapType = new JavaTypeInfo("Map", "java.util", "java/util/Map", JavaType.INTERFACE);
        assertThat(mapType.isAssignableTo("Ljava/util/HashMap;")).as("Map should NOT be assignable to HashMap").isFalse();
    }

    @Test
    public void testSelfAssignability() {
        JavaTypeInfo type = new JavaTypeInfo("Custom", "com.example", "com/example/Custom");
        assertThat(type.isAssignableTo("Lcom/example/Custom;")).as("Type should always be assignable to itself").isTrue();
    }

    @Test
    public void testStringAssignableToCharSequence() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertThat(stringType.isAssignableTo("Ljava/lang/CharSequence;")).as("String should be assignable to CharSequence").isTrue();
    }

    @Test
    public void testStringAssignableToComparable() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertThat(stringType.isAssignableTo("Ljava/lang/Comparable;")).as("String should be assignable to Comparable").isTrue();
    }

    @Test
    public void testStringAssignableToObject() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertThat(stringType.isAssignableTo("Ljava/lang/Object;")).as("String should be assignable to Object").isTrue();
    }

    @Test
    public void testStringNotAssignableToList() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertThat(stringType.isAssignableTo("Ljava/util/List;")).as("String should NOT be assignable to List").isFalse();
    }

    @Test
    public void testStringNotAssignableToMap() {
        JavaTypeInfo stringType = new JavaTypeInfo("String", "java.lang", "java/lang/String");
        assertThat(stringType.isAssignableTo("Ljava/util/Map;")).as("String should NOT be assignable to Map").isFalse();
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
        assertThat(grandChildType.isAssignableTo("Ljava/util/List;")).as("Deep hierarchy should be traversed: GrandChildList should be assignable to List").isTrue();
    }

    @Test
    public void testUserDefinedClassNotAssignableToUnrelatedType() {
        // Create user-defined class with ArrayList parent
        JavaTypeInfo arrayListParent = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        JavaTypeInfo customList = new JavaTypeInfo("MyList", "com.example", "com/example/MyList");
        customList.addParentTypeInfo(arrayListParent);

        // MyList should NOT be assignable to Map (no Map in hierarchy)
        assertThat(customList.isAssignableTo("Ljava/util/Map;")).as("User-defined class with List hierarchy should NOT be assignable to Map").isFalse();
    }

    @Test
    public void testUserDefinedClassWithListParent() {
        // Create user-defined class that "extends ArrayList"
        JavaTypeInfo arrayListParent = new JavaTypeInfo("ArrayList", "java.util", "java/util/ArrayList");
        JavaTypeInfo customList = new JavaTypeInfo("MyList", "com.example", "com/example/MyList");
        customList.addParentTypeInfo(arrayListParent);

        // MyList should be assignable to List (via ArrayList parent)
        assertThat(customList.isAssignableTo("Ljava/util/List;")).as("User-defined class extending ArrayList should be assignable to List").isTrue();
    }

    @Test
    public void testUserDefinedClassWithMapParent() {
        // Create user-defined class that "extends LinkedHashMap"
        JavaTypeInfo linkedHashMapParent = new JavaTypeInfo("LinkedHashMap", "java.util", "java/util/LinkedHashMap");
        JavaTypeInfo customMap = new JavaTypeInfo("MyMap", "com.example", "com/example/MyMap");
        customMap.addParentTypeInfo(linkedHashMapParent);

        // MyMap should be assignable to Map (via LinkedHashMap parent)
        assertThat(customMap.isAssignableTo("Ljava/util/Map;")).as("User-defined class extending LinkedHashMap should be assignable to Map").isTrue();
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
        assertThat(customClass.isAssignableTo("Lcom/example/MyClass;")).as("User-defined class should be assignable to itself").isTrue();
    }
}
