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

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.Swc4jSpan;

import java.util.List;
import java.util.Optional;

/**
 * The type Swc4j ast program.
 *
 * @param <AST> the type parameter
 * @since 0.2.0
 */
public abstract class Swc4jAstProgram<AST extends ISwc4jAst>
        extends Swc4jAst
        implements ISwc4jAstProgram<AST> {
    /**
     * The Body.
     *
     * @since 0.2.0
     */
    protected final List<AST> body;
    /**
     * The Shebang.
     *
     * @since 0.2.0
     */
    protected final Optional<String> shebang;

    /**
     * Instantiates a new Swc4j ast program.
     *
     * @param body    the body
     * @param shebang the shebang
     * @param span    the span
     * @since 0.2.0
     */
    protected Swc4jAstProgram(
            List<AST> body,
            String shebang,
            Swc4jSpan span) {
        super(span);
        this.body = SimpleList.immutableCopyOf(AssertionUtils.notNull(body, "Body"));
        this.shebang = Optional.ofNullable(shebang);
        childNodes = SimpleList.immutableCopyOf(body);
        updateParent();
    }

    @Override
    public List<AST> getBody() {
        return body;
    }

    @Override
    public Optional<String> getShebang() {
        return shebang;
    }
}
