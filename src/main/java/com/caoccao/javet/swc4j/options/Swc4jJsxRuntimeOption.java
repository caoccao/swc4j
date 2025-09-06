/*
 * Copyright (c) 2025. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.options;

import java.util.List;

/**
 * The interface Swc4j jsx runtime option.
 *
 * @since 1.7.0
 */
public abstract class Swc4jJsxRuntimeOption {
    /**
     * Automatic swc4j jsx runtime option automatic.
     *
     * @return the swc4j jsx runtime option automatic
     * @since 1.7.0
     */
    public static Swc4jJsxRuntimeOptionAutomatic Automatic() {
        return new Swc4jJsxRuntimeOptionAutomatic();
    }

    /**
     * Automatic swc4j jsx runtime option automatic.
     *
     * @param development  the development
     * @param importSource the import source
     * @return the swc4j jsx runtime option automatic
     * @since 1.7.0
     */
    public static Swc4jJsxRuntimeOptionAutomatic Automatic(boolean development, String importSource) {
        return new Swc4jJsxRuntimeOptionAutomatic(development, importSource);
    }

    /**
     * Classic swc4j jsx runtime option classic.
     *
     * @return the swc4j jsx runtime option classic
     * @since 1.7.0
     */
    public static Swc4jJsxRuntimeOptionClassic Classic() {
        return new Swc4jJsxRuntimeOptionClassic();
    }

    /**
     * Classic swc4j jsx runtime option classic.
     *
     * @param factory         the factory
     * @param fragmentFactory the fragment factory
     * @return the swc4j jsx runtime option classic
     * @since 1.7.0
     */
    public static Swc4jJsxRuntimeOptionClassic Classic(String factory, String fragmentFactory) {
        return new Swc4jJsxRuntimeOptionClassic(factory, fragmentFactory);
    }

    /**
     * Precompile swc4j jsx runtime option precompile.
     *
     * @param automatic    the automatic
     * @param dynamicProps the dynamic props
     * @param skipElements the skip elements
     * @return the swc4j jsx runtime option precompile
     * @since 1.7.0
     */
    public static Swc4jJsxRuntimeOptionPrecompile Precompile(
            Swc4jJsxRuntimeOptionAutomatic automatic,
            List<String> dynamicProps,
            List<String> skipElements) {
        return new Swc4jJsxRuntimeOptionPrecompile(automatic, dynamicProps, skipElements);
    }
}
