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

package com.caoccao.javet.swc4j.compiler;

import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Test suite for the errors the compiler reports.
 * The error is the one the compiler raised, and it carries the location it was raised at.
 */
public class TestByteCodeCompilerError extends BaseTestCompileSuite {

    @ParameterizedTest
    @EnumSource(JdkVersion.class)
    public void testErrorCarriesLineAndColumn(JdkVersion jdkVersion) {
        Throwable throwable = catchThrowable(() -> getCompiler(jdkVersion).compile("""
                namespace com {
                  export class A {
                    test(): int {
                      await using x = null
                      return 1
                    }
                  }
                }"""));
        assertThat(throwable).isInstanceOf(Swc4jByteCodeCompilerException.class);
        Swc4jByteCodeCompilerException exception = (Swc4jByteCodeCompilerException) throwable;
        assertThat(exception).hasMessage("await using is not supported");
        assertThat(exception.getDetailedMessage())
                .contains("await using is not supported")
                .contains("line 4")
                .contains("column 7");
    }
}
