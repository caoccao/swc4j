/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.outputs;

/**
 * The type Swc4j parse output.
 *
 * @since 0.2.0
 */
public class Swc4jParseOutput {
    /**
     * The Module.
     *
     * @since 0.2.0
     */
    protected boolean module;
    /**
     * The Script.
     *
     * @since 0.2.0
     */
    protected boolean script;

    /**
     * Instantiates a new Swc4j parse output.
     *
     * @param module the module
     * @param script the script
     * @since 0.2.0
     */
    public Swc4jParseOutput(boolean module, boolean script) {
        setModule(module);
        setScript(script);
    }

    /**
     * Gets if this source is a module.
     *
     * @return true : module, false : not module
     * @since 0.2.0
     */
    public boolean isModule() {
        return module;
    }

    /**
     * Gets if this source is a script.
     *
     * @return true : script, false : not script
     * @since 0.2.0
     */
    public boolean isScript() {
        return script;
    }

    /**
     * Sets module.
     *
     * @param module the module
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOutput setModule(boolean module) {
        this.module = module;
        return this;
    }

    /**
     * Sets script.
     *
     * @param script the script
     * @return the self
     * @since 0.2.0
     */
    public Swc4jParseOutput setScript(boolean script) {
        this.script = script;
        return this;
    }
}
