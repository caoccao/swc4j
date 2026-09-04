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

package com.caoccao.javet.swc4j.compiler.ast.expr.ident;

import com.caoccao.javet.swc4j.compiler.BaseTestCompileSuite;
import com.caoccao.javet.swc4j.compiler.JdkVersion;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Test suite for identifiers that cannot be resolved.
 * Such an identifier is a compile error rather than an expression that emits no bytecode.
 */
public class TestCompileAstIdentUnresolved extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testUnresolvedIdentifier(JdkVersion jdkVersion) {
        assertThatThrownBy(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      return notDefined
                    }
                  }
                }"""))
                .isInstanceOf(Swc4jByteCodeCompilerException.class)
                .hasMessage("Unresolved identifier: notDefined");
    }
}
