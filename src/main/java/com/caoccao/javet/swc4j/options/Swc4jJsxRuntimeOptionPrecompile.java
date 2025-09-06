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

import java.util.List;

/**
 * The type Swc4j jsx runtime option precompile.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public final class Swc4jJsxRuntimeOptionPrecompile extends Swc4jJsxRuntimeOption {
    /**
     * Automatic.
     *
     * @since 1.7.0
     */
    private Swc4jJsxRuntimeOptionAutomatic automatic;
    /**
     * List of properties/attributes that should always be treated as dynamic.
     *
     * @since 1.7.0
     */
    private List<String> dynamicProps;
    /**
     * List of elements that should not be precompiled when the JSX precompile transform is used.
     *
     * @since 1.7.0
     */
    private List<String> skipElements;

    /**
     * Instantiates a new Swc4j jsx runtime option precompile.
     *
     * @param automatic    the automatic
     * @param dynamicProps the dynamic props
     * @param skipElements the skip elements
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionPrecompile(
            Swc4jJsxRuntimeOptionAutomatic automatic,
            List<String> dynamicProps,
            List<String> skipElements) {
        setAutomatic(automatic);
        setDynamicProps(dynamicProps);
        setSkipElements(skipElements);
    }

    /**
     * Gets automatic.
     *
     * @return the automatic
     * @since 1.7.0
     */
    @Jni2RustMethod()
    public Swc4jJsxRuntimeOptionAutomatic getAutomatic() {
        return automatic;
    }

    /**
     * Gets dynamic props.
     *
     * @return the dynamic props
     * @since 1.7.0
     */
    @Jni2RustMethod(optional = true)
    public List<String> getDynamicProps() {
        return dynamicProps;
    }

    /**
     * Gets skip elements.
     *
     * @return the skip elements
     * @since 1.7.0
     */
    @Jni2RustMethod(optional = true)
    public List<String> getSkipElements() {
        return skipElements;
    }

    /**
     * Sets automatic.
     *
     * @param automatic the automatic
     * @return the self
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionPrecompile setAutomatic(Swc4jJsxRuntimeOptionAutomatic automatic) {
        this.automatic = AssertionUtils.notNull(automatic, "automatic");
        return this;
    }

    /**
     * Sets dynamic props.
     *
     * @param dynamicProps the dynamic props
     * @return the self
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionPrecompile setDynamicProps(List<String> dynamicProps) {
        this.dynamicProps = dynamicProps;
        return this;
    }

    /**
     * Sets skip elements.
     *
     * @param skipElements the skip elements
     * @return the self
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionPrecompile setSkipElements(List<String> skipElements) {
        this.skipElements = skipElements;
        return this;
    }
}
