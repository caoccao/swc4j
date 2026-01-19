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

package com.caoccao.javet.swc4j.plugins;

import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.utils.SimpleList;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BaseTestSuiteSwc4jPlugin extends BaseTestSuite {
    protected void assertEvalAsString(String code, String expectedCode) {
        assertEquals(
                evalAsString(code),
                evalAsString(expectedCode),
                "Failed to evaluate.\nFrom:\n" + code + "\nTo:\n" + expectedCode);
    }

    protected void assertTransform(String code, String expectedCode) {
        try {
            assertEquals(expectedCode, swc4j.transform(code, jsScriptTransformOptions).getCode());
        } catch (Throwable t) {
            fail("Failed to transform.\nFrom:\n" + code + "\nTo:\n" + expectedCode, t);
        }
    }

    protected void assertTransformAndEvalAsString(Map<String, String> testCaseMap) {
        testCaseMap.forEach((code, expectedCode) -> {
            assertTransform(code, expectedCode);
            assertEvalAsString(code, expectedCode);
        });
    }

    @BeforeEach
    @Override
    protected void beforeEach() {
        super.beforeEach();
        SimpleList.of(jsModuleTransformOptions,
                        jsProgramTransformOptions,
                        jsScriptTransformOptions,
                        jsxModuleTransformOptions,
                        jsxProgramTransformOptions,
                        jsxScriptTransformOptions,
                        tsModuleTransformOptions,
                        tsProgramTransformOptions,
                        tsScriptTransformOptions)
                .forEach(options -> options.setSourceMap(Swc4jSourceMapOption.None));
    }

    protected String evalAsString(String code) {
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            return v8Runtime.getExecutor(code).executeString();
        } catch (Throwable t) {
            fail("Failed to execute " + code, t);
        }
        return null;
    }
}
