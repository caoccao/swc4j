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

package com.caoccao.javet.swc4j.options;

import com.caoccao.javet.swc4j.BaseTestSuite;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jTransformOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSwc4JTransformOptions extends BaseTestSuite {

    @Test
    public void testTypeScriptWithDefaultOptions() throws Swc4jCoreException {
        String code = "function add(a:number, b:number) { return a+b; }";
        String expectedCode = "function add(a:number,b:number){return a+b;}\n";
        String expectedSourceMapPrefix = "//# sourceMappingURL=data:application/json;base64,";
        Swc4jTransformOutput output = swc4j.transform(code, tsModuleTransformOptions);
        assertNotNull(output);
        assertEquals(Swc4jParseMode.Module, output.getParseMode());
        assertEquals(expectedCode, output.getCode().substring(0, expectedCode.length()));
        assertEquals(Swc4jMediaType.TypeScript, output.getMediaType());
        assertEquals(
                expectedSourceMapPrefix,
                output.getCode().substring(
                        expectedCode.length(),
                        expectedCode.length() + expectedSourceMapPrefix.length()));
    }
}
