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

package com.caoccao.javet.swc4j.plugins.jsfuck;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.plugins.ISwc4jPluginHost;

public class Swc4jPluginHostJsFuckDecoder implements ISwc4jPluginHost {
    @Override
    public boolean process(ISwc4jAstProgram<?> program) {
        Swc4jPluginVisitorJsFuckDecoder jsFuckDecoder = new Swc4jPluginVisitorJsFuckDecoder();
        do {
            jsFuckDecoder.reset();
            program.visit(jsFuckDecoder);
        } while (jsFuckDecoder.getCount() > 0);
        return true;
    }
}
