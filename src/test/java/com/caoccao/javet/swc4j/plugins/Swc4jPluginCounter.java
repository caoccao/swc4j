/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstModule;
import com.caoccao.javet.swc4j.ast.program.Swc4jAstScript;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class Swc4jPluginCounter implements ISwc4jPlugin {
    protected int moduleCount;
    protected int scriptCount;

    public Swc4jPluginCounter() {
        moduleCount = 0;
        scriptCount = 0;
    }

    public int getModuleCount() {
        return moduleCount;
    }

    public int getScriptCount() {
        return scriptCount;
    }

    @Override
    public Swc4jPluginResponse process(ISwc4jAstProgram<?> program) {
        assertNotNull(program);
        if (program instanceof Swc4jAstModule) {
            ++moduleCount;
        } else if (program instanceof Swc4jAstScript) {
            ++scriptCount;
        } else {
            fail("It's impossible to process program: " + program.getClass().getName());
        }
        return Swc4jPluginResponse.OkAndContinue;
    }
}
