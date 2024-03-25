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
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSwc4jAst extends BaseTestSuite {
    @Test
    public void testParseTypeScriptAsScriptWithCaptureAst() throws Swc4jCoreException {
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setMediaType(Swc4jMediaType.TypeScript)
                .setParseMode(Swc4jParseMode.Script)
                .setCaptureAst(true);
        String code = "let a";
        Swc4jParseOutput output = swc4j.parse(code, options);
        assertNotNull(output);
        assertFalse(output.isModule());
        assertTrue(output.isScript());
        assertNotNull(output.getProgram());
        Swc4jAstScript script = (Swc4jAstScript) output.getProgram();
        assertEquals(0, script.getStartPosition());
        assertEquals(code.length(), script.getEndPosition());
        assertNotNull(script.getBody());
    }
}
