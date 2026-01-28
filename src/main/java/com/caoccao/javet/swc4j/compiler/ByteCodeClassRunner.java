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

import com.caoccao.javet.swc4j.compiler.utils.ScoreUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class ByteCodeClassRunner {
    private final Class<?> clazz;
    private final Object instance;
    private final Map<String, Method[]> methodNameToMethodsMap;
    private final Method[] methods;

    private ByteCodeClassRunner(Class<?> clazz, Object instance) {
        this.clazz = clazz;
        this.instance = instance;
        methods = clazz.getMethods();
        methodNameToMethodsMap = new HashMap<>();
    }

    public static ByteCodeClassRunner createInstance(Class<?> clazz, Object... constructorArgs) {
        Constructor<?> constructor = ScoreUtils.findBestConstructor(clazz.getConstructors(), constructorArgs);
        if (constructor == null) {
            throw new IllegalArgumentException("No matching constructor found for " + clazz.getName());
        }
        try {
            return new ByteCodeClassRunner(clazz, constructor.newInstance(constructorArgs));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to instantiate " + clazz.getName(), e);
        }
    }

    public static ByteCodeClassRunner createStatic(Class<?> clazz) {
        return new ByteCodeClassRunner(clazz, null);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getInstance() {
        return instance;
    }

    private Method[] getMethods(String methodName) {
        return methodNameToMethodsMap.computeIfAbsent(methodName, k -> Stream.of(methods)
                .filter(method -> method.getName().equals(methodName))
                .toArray(Method[]::new));
    }

    public <T> T invoke(String methodName, Object... args) throws InvocationTargetException, IllegalAccessException {
        Method method = ScoreUtils.findBestMethod(getMethods(methodName), args);
        if (method == null) {
            throw new IllegalArgumentException("No matching method found: " + methodName);
        }
        @SuppressWarnings("unchecked")
        T result = (T) method.invoke(instance, args);
        return result;
    }
}
