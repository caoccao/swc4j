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

package com.caoccao.javet.swc4j.ast.plugins;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

public class Swc4jAstPluginVisitors implements ISwc4jAstPlugin {
    protected final List<ISwc4jAstVisitor> visitors;

    public Swc4jAstPluginVisitors(List<ISwc4jAstVisitor> visitors) {
        this.visitors = AssertionUtils.notNull(visitors, "Visitors");
    }

    @Override
    public Swc4jAstPluginResponse process(ISwc4jAstProgram<?> program) {
        for (ISwc4jAstVisitor visitor : visitors) {
            switch (program.visit(visitor)) {
                case Error:
                    return Swc4jAstPluginResponse.Error;
                case OkAndBreak:
                    return Swc4jAstPluginResponse.OkAndBreak;
                default:
                    break;
            }
        }
        return Swc4jAstPluginResponse.OkAndContinue;
    }
}
