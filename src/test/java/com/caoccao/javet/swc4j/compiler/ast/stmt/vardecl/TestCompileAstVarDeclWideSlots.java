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

package com.caoccao.javet.swc4j.compiler.ast.stmt.vardecl;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for local variable slots that do not fit a byte.
 * Loads and stores of such a slot need the wide form of the instruction.
 */
public class TestCompileAstVarDeclWideSlots extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testManyLocalVariables(JdkVersion jdkVersion) throws Exception {
        StringBuilder code = new StringBuilder("namespace com {\n  export class A {\n    test(): int {\n");
        for (int i = 0; i < 300; i++) {
            code.append("      let v").append(i).append(": int = ").append(i).append("\n");
        }
        code.append("      return v0 + v299\n    }\n  }\n}");
        var runner = getCompiler(jdkVersion).compile(code.toString());
        assertThat((int) runner.createInstanceRunner("com.A").invoke("test")).isEqualTo(299);
    }
}
