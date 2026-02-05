/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.compiler;

/**
 * Enum representing supported JDK versions for bytecode compilation.
 */
public enum JdkVersion {
    /**
     * JDK 17 version
     */
    JDK_17(17);

    private final int version;

    /**
     * Constructs a JdkVersion with the given version number.
     *
     * @param version the version number
     */
    JdkVersion(int version) {
        this.version = version;
    }

    /**
     * Gets the version number.
     *
     * @return the version number
     */
    public int getVersion() {
        return version;
    }
}
