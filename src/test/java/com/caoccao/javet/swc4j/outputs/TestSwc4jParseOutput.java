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

package com.caoccao.javet.swc4j.outputs;

import com.caoccao.javet.swc4j.jni2rust.Jni2Rust;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestSwc4jParseOutput {
    @Test
    public void testCodeGen() throws IOException {
        Jni2Rust<Swc4jParseOutput> jni2Rust = new Jni2Rust<>(Swc4jParseOutput.class);
        jni2Rust.updateFile();
    }
}
