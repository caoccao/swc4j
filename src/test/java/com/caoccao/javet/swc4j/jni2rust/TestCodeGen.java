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

package com.caoccao.javet.swc4j.jni2rust;

import com.caoccao.javet.swc4j.ast.Swc4jAstFactory;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.caoccao.javet.swc4j.tokens.Swc4jTokenFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestCodeGen {
    @Test
    public void testSwc4jAstFactory() throws IOException {
        Jni2Rust<Swc4jAstFactory> jni2Rust = new Jni2Rust<>(Swc4jAstFactory.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jParseOptions() throws IOException {
        Jni2Rust<Swc4jParseOptions> jni2Rust = new Jni2Rust<>(Swc4jParseOptions.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jParseOutput() throws IOException {
        Jni2Rust<Swc4jParseOutput> jni2Rust = new Jni2Rust<>(Swc4jParseOutput.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTokenFactory() throws IOException {
        Jni2Rust<Swc4jTokenFactory> jni2Rust = new Jni2Rust<>(Swc4jTokenFactory.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTranspileOptions() throws IOException {
        Jni2Rust<Swc4jTranspileOptions> jni2Rust = new Jni2Rust<>(Swc4jTranspileOptions.class);
        jni2Rust.updateFile();
    }

    @Test
    public void testSwc4jTranspileOutput() throws IOException {
        Jni2Rust<Swc4jTranspileOutput> jni2Rust = new Jni2Rust<>(Swc4jTranspileOutput.class);
        jni2Rust.updateFile();
    }
}
