/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The type Simple list.
 *
 * @since 0.2.0
 */
public final class SimpleList {
    private SimpleList() {
    }

    /**
     * Copy of list.
     *
     * @param <R>        the type parameter
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the copied list
     * @since 0.2.0
     */
    public static <R, T extends R> List<R> copyOf(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    /**
     * Get an immutable list.
     *
     * @param <T>  the type parameter
     * @param list the list
     * @return the list
     * @since 0.2.0
     */
    public static <T> List<T> immutable(List<T> list) {
        return Collections.unmodifiableList(list);
    }

    /**
     * Immutable copy of list.
     *
     * @param <R>        the type parameter
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the immutable copied list
     * @since 0.2.0
     */
    public static <R, T extends R> List<R> immutableCopyOf(Collection<T> collection) {
        return immutable(copyOf(collection));
    }

    /**
     * Immutable of list.
     *
     * @param <T> the type parameter
     * @return the immutable list
     * @since 0.2.0
     */
    public static <T> List<T> immutableOf() {
        return immutable(of());
    }

    /**
     * Immutable of list.
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the immutable list
     * @since 0.2.0
     */
    @SafeVarargs
    public static <T> List<T> immutableOf(T... objects) {
        return immutable(of(objects));
    }

    /**
     * Of list.
     *
     * @param <T> the type parameter
     * @return the list
     * @since 0.2.0
     */
    public static <T> List<T> of() {
        return new ArrayList<>();
    }

    /**
     * Of list.
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the list
     * @since 0.2.0
     */
    @SafeVarargs
    public static <T> List<T> of(T... objects) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, objects);
        return list;
    }
}
