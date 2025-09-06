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

import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

/**
 * The type Swc4j jsx runtime option classic.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public final class Swc4jJsxRuntimeOptionClassic extends Swc4jJsxRuntimeOption {
    /**
     * The constant DEFAULT_FACTORY.
     *
     * @since 1.7.0
     */
    public static final String DEFAULT_FACTORY = "React.createElement";
    /**
     * The constant DEFAULT_FRAGMENT_FACTORY.
     *
     * @since 1.7.0
     */
    public static final String DEFAULT_FRAGMENT_FACTORY = "React.Fragment";
    /**
     * When transforming JSX, what value should be used for the JSX factory.
     * Defaults to `React.createElement`.
     *
     * @since 1.7.0
     */
    private String factory;
    /**
     * When transforming JSX, what value should be used for the JSX fragment factory.
     * Defaults to `React.Fragment`.
     *
     * @since 1.7.0
     */
    private String fragmentFactory;

    /**
     * Instantiates a new Swc4j jsx runtime option classic.
     *
     * @since 1.7.0
     */
    Swc4jJsxRuntimeOptionClassic() {
        this(DEFAULT_FACTORY, DEFAULT_FRAGMENT_FACTORY);
    }

    /**
     * Instantiates a new Swc4j jsx runtime option classic.
     *
     * @param factory         the factory
     * @param fragmentFactory the fragment factory
     * @since 1.7.0
     */
    Swc4jJsxRuntimeOptionClassic(String factory, String fragmentFactory) {
        setFactory(factory);
        setFragmentFactory(fragmentFactory);
    }

    /**
     * Gets factory.
     *
     * @return the factory
     * @since 1.7.0
     */
    @Jni2RustMethod
    public String getFactory() {
        return factory;
    }

    /**
     * Gets fragment factory.
     *
     * @return the fragment factory
     * @since 1.7.0
     */
    @Jni2RustMethod
    public String getFragmentFactory() {
        return fragmentFactory;
    }

    /**
     * Sets factory.
     *
     * @param factory the factory
     * @return the self
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionClassic setFactory(String factory) {
        this.factory = AssertionUtils.notNull(factory, "factory");
        return this;
    }

    /**
     * Sets fragment factory.
     *
     * @param fragmentFactory the fragment factory
     * @return the self
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionClassic setFragmentFactory(String fragmentFactory) {
        this.fragmentFactory = AssertionUtils.notNull(fragmentFactory, "fragment factory");
        return this;
    }
}
