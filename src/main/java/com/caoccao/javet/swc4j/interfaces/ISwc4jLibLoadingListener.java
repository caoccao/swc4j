/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.interfaces;

import java.io.File;

/**
 * The interface Swc4j lib loading listener.
 *
 * @since 2.0.0
 */
public interface ISwc4jLibLoadingListener {
    /**
     * Gets lib path.
     * If the lib is in system path, this function will not be called.
     * <p>
     * Note: lib file name is decided by Swc4j.
     *
     * @return the lib path
     * @since 2.0.0
     */
    default File getLibPath() {
        return null;
    }

    /**
     * Is deploy.
     *
     * @return true : yes, false : no
     * @since 2.0.0
     */
    default boolean isDeploy() {
        return true;
    }

    /**
     * Is lib in system path.
     *
     * @return true : yes, false : no
     * @since 2.0.0
     */
    default boolean isLibInSystemPath() {
        return false;
    }

    /**
     * Is suppressing error.
     *
     * @return true : yes, false : no
     * @since 2.0.0
     */
    default boolean isSuppressingError() {
        return false;
    }
}
