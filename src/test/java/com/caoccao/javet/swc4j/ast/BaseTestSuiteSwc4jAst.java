/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.ast;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;

public abstract class BaseTestSuiteSwc4jAst extends BaseTestSuite {
    protected Swc4jParseOptions tsModuleOptions;
    protected Swc4jParseOptions tsScriptOptions;

    public BaseTestSuiteSwc4jAst() {
        super();
        this.tsModuleOptions = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setParseMode(Swc4jParseMode.Module)
                .setCaptureAst(true);
        this.tsScriptOptions = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setParseMode(Swc4jParseMode.Script)
                .setCaptureAst(true);
    }
}
