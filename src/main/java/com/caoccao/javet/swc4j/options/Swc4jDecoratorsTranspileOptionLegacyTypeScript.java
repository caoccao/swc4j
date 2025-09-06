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

/**
 * The interface Swc4j decorators transpile option None.
 * TypeScript experimental decorators.
 *
 * @since 1.7.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public final class Swc4jDecoratorsTranspileOptionLegacyTypeScript extends Swc4jDecoratorsTranspileOption {
    /**
     * Also emit experimental decorator meta data.
     * Defaults to `false`.
     *
     * @since 1.7.0
     */
    private boolean emitMetadata;

    /**
     * Instantiates a new Swc4j decorators transpile option legacy type script.
     *
     * @since 1.7.0
     */
    Swc4jDecoratorsTranspileOptionLegacyTypeScript() {
        this(false);
    }

    /**
     * Instantiates a new Swc4j decorators transpile option legacy type script.
     *
     * @param emitMetadata the emit metadata
     * @since 1.7.0
     */
    Swc4jDecoratorsTranspileOptionLegacyTypeScript(boolean emitMetadata) {
        this.emitMetadata = emitMetadata;
    }

    /**
     * Is emit metadata.
     *
     * @return true: yes, false: no
     * @since 1.7.0
     */
    @Jni2RustMethod
    public boolean isEmitMetadata() {
        return emitMetadata;
    }

    /**
     * Sets emit metadata.
     *
     * @param emitMetadata the emit metadata
     */
    public void setEmitMetadata(boolean emitMetadata) {
        this.emitMetadata = emitMetadata;
    }
}
