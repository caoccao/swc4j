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

package com.caoccao.javet.swc4j;

import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.interfaces.ISwc4jLogger;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.Swc4jDefaultLogger;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestSuite {
    protected Swc4jParseOptions jsModuleParseOptions;
    protected Swc4jTransformOptions jsModuleTransformOptions;
    protected Swc4jTranspileOptions jsModuleTranspileOptions;
    protected Swc4jParseOptions jsScriptParseOptions;
    protected Swc4jTransformOptions jsScriptTransformOptions;
    protected Swc4jTranspileOptions jsScriptTranspileOptions;
    protected Swc4jParseOptions jsxModuleParseOptions;
    protected Swc4jTransformOptions jsxModuleTransformOptions;
    protected Swc4jTranspileOptions jsxModuleTranspileOptions;
    protected Swc4jParseOptions jsxScriptParseOptions;
    protected Swc4jTransformOptions jsxScriptTransformOptions;
    protected Swc4jTranspileOptions jsxScriptTranspileOptions;
    protected ISwc4jLogger logger;
    protected Swc4j swc4j;
    protected Swc4jParseOptions tsModuleParseOptions;
    protected Swc4jTransformOptions tsModuleTransformOptions;
    protected Swc4jTranspileOptions tsModuleTranspileOptions;
    protected Swc4jParseOptions tsScriptParseOptions;
    protected Swc4jTransformOptions tsScriptTransformOptions;
    protected Swc4jTranspileOptions tsScriptTranspileOptions;

    public BaseTestSuite() {
        logger = new Swc4jDefaultLogger(getClass().getName());
        swc4j = new Swc4j();
    }

    @BeforeEach
    protected void beforeEach() {
        jsModuleParseOptions = new Swc4jParseOptions();
        jsModuleTransformOptions = new Swc4jTransformOptions();
        jsModuleTranspileOptions = new Swc4jTranspileOptions();
        jsScriptParseOptions = new Swc4jParseOptions();
        jsScriptTransformOptions = new Swc4jTransformOptions();
        jsScriptTranspileOptions = new Swc4jTranspileOptions();
        jsxModuleParseOptions = new Swc4jParseOptions();
        jsxModuleTransformOptions = new Swc4jTransformOptions();
        jsxModuleTranspileOptions = new Swc4jTranspileOptions();
        jsxScriptParseOptions = new Swc4jParseOptions();
        jsxScriptTransformOptions = new Swc4jTransformOptions();
        jsxScriptTranspileOptions = new Swc4jTranspileOptions();
        tsModuleParseOptions = new Swc4jParseOptions();
        tsModuleTransformOptions = new Swc4jTransformOptions();
        tsModuleTranspileOptions = new Swc4jTranspileOptions();
        tsScriptParseOptions = new Swc4jParseOptions();
        tsScriptTransformOptions = new Swc4jTransformOptions();
        tsScriptTranspileOptions = new Swc4jTranspileOptions();
        SimpleList.of(
                jsModuleParseOptions,
                jsModuleTransformOptions,
                jsModuleTranspileOptions,
                jsScriptParseOptions,
                jsScriptTransformOptions,
                jsScriptTranspileOptions
        ).forEach(options -> options.setMediaType(Swc4jMediaType.JavaScript));
        SimpleList.of(
                jsxModuleParseOptions,
                jsxModuleTransformOptions,
                jsxModuleTranspileOptions,
                jsxScriptParseOptions,
                jsxScriptTransformOptions,
                jsxScriptTranspileOptions
        ).forEach(options -> options.setMediaType(Swc4jMediaType.Jsx));
        SimpleList.of(
                tsModuleParseOptions,
                tsModuleTransformOptions,
                tsModuleTranspileOptions,
                tsScriptParseOptions,
                tsScriptTransformOptions,
                tsScriptTranspileOptions
        ).forEach(options -> options.setMediaType(Swc4jMediaType.TypeScript));
        SimpleList.of(
                jsModuleParseOptions,
                jsModuleTransformOptions,
                jsModuleTranspileOptions,
                jsxModuleParseOptions,
                jsxModuleTransformOptions,
                jsxModuleTranspileOptions,
                tsModuleParseOptions,
                tsModuleTransformOptions,
                tsModuleTranspileOptions
        ).forEach(options -> options.setParseMode(Swc4jParseMode.Module));
        SimpleList.of(
                jsScriptParseOptions,
                jsScriptTransformOptions,
                jsScriptTranspileOptions,
                jsxScriptParseOptions,
                jsxScriptTransformOptions,
                jsxScriptTranspileOptions,
                tsScriptParseOptions,
                tsScriptTransformOptions,
                tsScriptTranspileOptions
        ).forEach(options -> options.setParseMode(Swc4jParseMode.Script));
    }
}
