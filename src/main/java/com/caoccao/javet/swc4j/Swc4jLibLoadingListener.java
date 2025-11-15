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

package com.caoccao.javet.swc4j;

import com.caoccao.javet.swc4j.interfaces.ISwc4jLibLoadingListener;
import com.caoccao.javet.swc4j.utils.OSUtils;

import java.io.File;

/**
 * The type Swc4j lib loading listener is the default one.
 *
 * @since 2.0.0
 */
public final class Swc4jLibLoadingListener implements ISwc4jLibLoadingListener {
    /**
     * The constant SWC4J_LIB_LOADING_TYPE_DEFAULT.
     *
     * @since 2.0.0
     */
    public static final String SWC4J_LIB_LOADING_TYPE_DEFAULT = "default";
    /**
     * The constant SWC4J_LIB_LOADING_TYPE_CUSTOM.
     *
     * @since 2.0.0
     */
    public static final String SWC4J_LIB_LOADING_TYPE_CUSTOM = "custom";
    /**
     * The constant SWC4J_LIB_LOADING_TYPE_SYSTEM.
     *
     * @since 2.0.0
     */
    public static final String SWC4J_LIB_LOADING_TYPE_SYSTEM = "system";
    /**
     * The constant PROPERTY_KEY_SWC4J_LIB_LOADING_PATH.
     *
     * @since 2.0.0
     */
    public static final String PROPERTY_KEY_SWC4J_LIB_LOADING_PATH = "swc4j.lib.loading.path";
    /**
     * The constant PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE.
     *
     * @since 2.0.0
     */
    public static final String PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE = "swc4j.lib.loading.type";
    /**
     * The constant PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR.
     *
     * @since 2.0.0
     */
    public static final String PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR = "swc4j.lib.loading.suppress.error";
    private static final String LIB_NAME = "swc4j";
    private final String swc4jLibLoadingPath;
    private final String swc4jLibLoadingSuppressError;
    private final String swc4jLibLoadingType;

    /**
     * Instantiates a new Swc4j lib loading listener.
     *
     * @since 2.0.0
     */
    public Swc4jLibLoadingListener() {
        swc4jLibLoadingPath = System.getProperty(PROPERTY_KEY_SWC4J_LIB_LOADING_PATH);
        swc4jLibLoadingSuppressError = System.getProperty(PROPERTY_KEY_SWC4J_LIB_LOADING_SUPPRESS_ERROR, null);
        swc4jLibLoadingType = System.getProperty(PROPERTY_KEY_SWC4J_LIB_LOADING_TYPE, SWC4J_LIB_LOADING_TYPE_DEFAULT);
    }

    @Override
    public File getLibPath() {
        if (swc4jLibLoadingPath == null) {
            return new File(OSUtils.TEMP_DIRECTORY, LIB_NAME);
        }
        return new File(swc4jLibLoadingPath);
    }

    @Override
    public boolean isDeploy() {
        if (OSUtils.IS_ANDROID) {
            return false;
        }
        if (SWC4J_LIB_LOADING_TYPE_SYSTEM.equals(swc4jLibLoadingType)) {
            return false;
        }
        if (SWC4J_LIB_LOADING_TYPE_CUSTOM.equals(swc4jLibLoadingType)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isLibInSystemPath() {
        if (OSUtils.IS_ANDROID) {
            return true;
        }
        if (SWC4J_LIB_LOADING_TYPE_SYSTEM.equals(swc4jLibLoadingType)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSuppressingError() {
        if (OSUtils.IS_ANDROID) {
            return true;
        }
        return swc4jLibLoadingSuppressError != null;
    }
}
