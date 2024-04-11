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

package com.caoccao.javet.swc4j.ast.program;

import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstModuleItem;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;

import java.util.List;

/**
 * The type Swc4j ast module.
 *
 * @since 0.2.0
 */
public class Swc4jAstModule extends Swc4jAstProgram<ISwc4jAstModuleItem> {
    /**
     * Instantiates a new Swc4j ast module.
     *
     * @param body    the body
     * @param shebang the shebang
     * @param span    the span
     * @since 0.2.0
     */
    public Swc4jAstModule(
            List<ISwc4jAstModuleItem> body,
            String shebang,
            Swc4jAstSpan span) {
        super(body, shebang, span);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Module;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitModule(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
