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

/**
 * The interface Swc4j decorators transpile option Ecma.
 * TC39 Decorators Proposal - https://github.com/tc39/proposal-decorators
 *
 * @since 1.7.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.Options)
public final class Swc4jDecoratorsTranspileOptionEcma extends Swc4jDecoratorsTranspileOption {
    /**
     * The Instance.
     *
     * @since 1.7.0
     */
    static final Swc4jDecoratorsTranspileOptionEcma INSTANCE = new Swc4jDecoratorsTranspileOptionEcma();

    /**
     * Instantiates a new Swc4j decorators transpile option ecma.
     *
     * @since 1.7.0
     */
    Swc4jDecoratorsTranspileOptionEcma() {
    }
}
