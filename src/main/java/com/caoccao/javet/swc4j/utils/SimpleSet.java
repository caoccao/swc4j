/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Simple set is a polyfill because Set.of() is not available at JDK 8 .
 *
 * @since 0.2.0
 */
public final class SimpleSet {
    private SimpleSet() {
    }

    /**
     * Immutable set.
     *
     * @param <T> the type parameter
     * @param set the set
     * @return the set
     * @since 0.7.0
     */
    public static <T> Set<T> immutable(Set<T> set) {
        return Collections.unmodifiableSet(set);
    }

    /**
     * Immutable of set.
     *
     * @param <T> the type parameter
     * @return the set
     * @since 0.7.0
     */
    public static <T> Set<T> immutableOf() {
        return immutable(of());
    }

    /**
     * Immutable of set.
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the set
     * @since 0.7.0
     */
    @SafeVarargs
    public static <T> Set<T> immutableOf(T... objects) {
        return immutable(of(objects));
    }

    /**
     * Of set.
     *
     * @param <T> the type parameter
     * @return the set
     * @since 0.2.0
     */
    public static <T> Set<T> of() {
        return new HashSet<>();
    }

    /**
     * Of set.
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the set
     * @since 0.2.0
     */
    @SafeVarargs
    public static <T> Set<T> of(T... objects) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, objects);
        return set;
    }
}
