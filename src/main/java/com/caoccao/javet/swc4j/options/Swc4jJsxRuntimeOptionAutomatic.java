/*
 * Copyright (c) 2025-2026. caoccao.com Sam Cao
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

/**
 * The type Swc4j jsx runtime option automatic.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public final class Swc4jJsxRuntimeOptionAutomatic extends Swc4jJsxRuntimeOption {
    /**
     * If JSX is automatic, if it is in development mode, meaning that it should
     * import `jsx-dev-runtime` and transform JSX using `jsxDEV` import from the
     * JSX import source as well as provide additional debug information to the
     * JSX factory.
     *
     * @since 1.7.0
     */
    private boolean development;

    /**
     * The string module specifier to implicitly import JSX factories from when transpiling JSX.
     *
     * @since 1.7.0
     */
    private String importSource;

    /**
     * Instantiates a new Swc4j jsx runtime option automatic.
     *
     * @since 1.7.0
     */
    Swc4jJsxRuntimeOptionAutomatic() {
        this(false, null);
    }

    /**
     * Instantiates a new Swc4j jsx runtime option automatic.
     *
     * @param development  the development
     * @param importSource the import source
     * @since 1.7.0
     */
    Swc4jJsxRuntimeOptionAutomatic(boolean development, String importSource) {
        setDevelopment(development);
        setImportSource(importSource);
    }

    /**
     * Gets import source.
     *
     * @return the import source
     * @since 1.7.0
     */
    @Jni2RustMethod(optional = true)
    public String getImportSource() {
        return importSource;
    }

    /**
     * Is development.
     *
     * @return true : yes, false : no
     * @since 1.7.0
     */
    @Jni2RustMethod
    public boolean isDevelopment() {
        return development;
    }

    /**
     * Sets development.
     *
     * @param development the development
     * @return the self
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionAutomatic setDevelopment(boolean development) {
        this.development = development;
        return this;
    }

    /**
     * Sets import source.
     *
     * @param importSource the import source
     * @return the self
     * @since 1.7.0
     */
    public Swc4jJsxRuntimeOptionAutomatic setImportSource(String importSource) {
        this.importSource = importSource;
        return this;
    }
}
