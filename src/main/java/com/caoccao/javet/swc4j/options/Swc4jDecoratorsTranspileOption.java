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

/**
 * The interface Swc4j decorators transpile option.
 *
 * @since 1.7.0
 */
public abstract class Swc4jDecoratorsTranspileOption {
    /**
     * Ecma.
     *
     * @return the Ecma
     */
    public static Swc4jDecoratorsTranspileOptionEcma Ecma() {
        return Swc4jDecoratorsTranspileOptionEcma.INSTANCE;
    }

    /**
     * Default LegacyTypeScript
     *
     * @return the default LegacyTypeScript
     */
    public static Swc4jDecoratorsTranspileOptionLegacyTypeScript LegacyTypeScript() {
        return new Swc4jDecoratorsTranspileOptionLegacyTypeScript();
    }

    /**
     * LegacyTypeScript
     *
     * @param emitMetadata the emit metadata
     * @return the LegacyTypeScript
     */
    public static Swc4jDecoratorsTranspileOptionLegacyTypeScript LegacyTypeScript(boolean emitMetadata) {
        return new Swc4jDecoratorsTranspileOptionLegacyTypeScript(emitMetadata);
    }

    /**
     * None
     *
     * @return the None
     */
    public static Swc4jDecoratorsTranspileOptionNone None() {
        return Swc4jDecoratorsTranspileOptionNone.INSTANCE;
    }
}
