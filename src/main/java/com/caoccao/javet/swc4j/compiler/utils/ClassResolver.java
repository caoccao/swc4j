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

package com.caoccao.javet.swc4j.compiler.utils;

/**
 * Resolves Java classes for the compiler through the class loader configured in the options.
 * <p>
 * {@code Class.forName(String)} resolves through the class loader of the caller, which is the one
 * that loaded the compiler itself. A class that is only visible through a user supplied class
 * loader would not be found that way, even though the generated code is later loaded through
 * exactly that loader.
 */
public record ClassResolver(ClassLoader classLoader) {
    /**
     * Instantiates a new class resolver.
     *
     * @param classLoader the class loader to resolve through, null for the compiler's own
     */
    public ClassResolver(ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : ClassResolver.class.getClassLoader();
    }

    /**
     * Gets the class loader used for resolution.
     *
     * @return the class loader
     */
    @Override
    public ClassLoader classLoader() {
        return classLoader;
    }

    /**
     * Resolves a class by its qualified name.
     *
     * @param qualifiedName the qualified class name
     * @return the class
     * @throws ClassNotFoundException if the class cannot be resolved
     */
    public Class<?> resolve(String qualifiedName) throws ClassNotFoundException {
        // The class is only inspected, never used, so it does not need to be initialized
        return Class.forName(qualifiedName, false, classLoader);
    }

    /**
     * Resolves a class by its qualified name, returning null when it cannot be resolved.
     *
     * @param qualifiedName the qualified class name
     * @return the class, or null
     */
    public Class<?> resolveOrNull(String qualifiedName) {
        try {
            return resolve(qualifiedName);
        } catch (ClassNotFoundException | LinkageError e) {
            return null;
        }
    }
}
