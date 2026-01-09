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

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.exceptions.Swc4jByteCodeCompilerException;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.Map;

public sealed abstract class ByteCodeCompiler permits
        ByteCodeCompiler17 {
    protected final ByteCodeCompilerOptions options;
    protected final Swc4j swc4j;
    protected final Swc4jParseOptions parseOptions;

    ByteCodeCompiler(ByteCodeCompilerOptions options) {
        this.options = AssertionUtils.notNull(options, "options");
        parseOptions = new Swc4jParseOptions()
                .setCaptureAst(true);
        swc4j = new Swc4j();
    }

    public static ByteCodeCompiler of(ByteCodeCompilerOptions options) {
        AssertionUtils.notNull(options, "options");
        return switch (options.jdkVersion()) {
            case JDK_17 -> new ByteCodeCompiler17(options);
        };
    }

    public Map<String, byte[]> compile(String code) throws Swc4jCoreException, Swc4jByteCodeCompilerException {
        Swc4jParseOutput output = swc4j.parse(code, parseOptions);
        return compileProgram(output.getProgram());
    }

    abstract Map<String, byte[]> compileProgram(ISwc4jAstProgram<?> program) throws Swc4jByteCodeCompilerException;
}
