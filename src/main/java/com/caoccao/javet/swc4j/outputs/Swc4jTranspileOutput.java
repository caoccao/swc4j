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

public class Swc4jTranspileOutput {
    protected String code;
    protected boolean module;

    public Swc4jTranspileOutput() {
        this(null);
    }

    public Swc4jTranspileOutput(String code) {
        this(code, false);
    }

    public Swc4jTranspileOutput(String code, boolean module) {
        setCode(code);
        setModule(module);
    }

    public String getCode() {
        return code;
    }

    public boolean isModule() {
        return module;
    }

    public Swc4jTranspileOutput setCode(String code) {
        this.code = code;
        return this;
    }

    public Swc4jTranspileOutput setModule(boolean module) {
        this.module = module;
        return this;
    }
}
