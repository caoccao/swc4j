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

package com.caoccao.javet.swc4j.ast.interfaces;

import com.caoccao.javet.swc4j.ast.BaseTestSuiteSwc4jAst;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestISwc4jAst extends BaseTestSuiteSwc4jAst {
    @Test
    public void testFind() throws Swc4jCoreException {
        String code = "+(-(+(-1)))";
        Swc4jParseOutput output = swc4j.parse(code, tsScriptParseOptions);
        IntStream.range(0, 7).forEach(i ->
                assertTrue(output.getProgram().find(Swc4jAstNumber.class, i).isEmpty()));
        assertEquals(1, output.getProgram().find(Swc4jAstNumber.class, 8).size());
        assertEquals(1, output.getProgram().find(Swc4jAstNumber.class, -1).size());
        assertEquals(1, output.getProgram().find(Swc4jAstNumber.class).size());
    }
}
