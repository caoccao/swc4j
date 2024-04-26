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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustParam;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type Swc4j ast script.
 *
 * @since 0.2.0
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstScript
        extends Swc4jAst
        implements ISwc4jAstProgram<ISwc4jAstStmt> {
    /**
     * The Body.
     *
     * @since 0.2.0
     */
    protected final List<ISwc4jAstStmt> body;
    /**
     * The Shebang.
     *
     * @since 0.2.0
     */
    protected final Optional<String> shebang;

    /**
     * Instantiates a new Swc4j ast script.
     *
     * @param body    the body
     * @param shebang the shebang
     * @param span    the span
     * @since 0.2.0
     */
    @Jni2RustMethod
    public Swc4jAstScript(
            List<ISwc4jAstStmt> body,
            @Jni2RustParam(optional = true) String shebang,
            Swc4jSpan span) {
        super(span);
        this.body = AssertionUtils.notNull(body, "Body");
        this.shebang = Optional.ofNullable(shebang);
        updateParent();
    }

    @Override
    public List<ISwc4jAstStmt> getBody() {
        return body;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.copyOf(body);
    }

    @Override
    public Optional<String> getShebang() {
        return shebang;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Script;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitScript(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
