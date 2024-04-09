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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstIfStmt
        extends Swc4jAst
        implements ISwc4jAstStmt {
    protected final Optional<ISwc4jAstStmt> alt;
    @Jni2RustField(box = true)
    protected final ISwc4jAstStmt cons;
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr test;

    public Swc4jAstIfStmt(
            ISwc4jAstExpr test,
            ISwc4jAstStmt cons,
            ISwc4jAstStmt alt,
            Swc4jAstSpan span) {
        super(span);
        this.cons = AssertionUtils.notNull(cons, "Body");
        this.alt = Optional.ofNullable(alt);
        this.test = AssertionUtils.notNull(test, "Right");
        childNodes = SimpleList.immutableOf(test, cons, alt);
        updateParent();
    }

    public Optional<ISwc4jAstStmt> getAlt() {
        return alt;
    }

    public ISwc4jAstStmt getCons() {
        return cons;
    }

    public ISwc4jAstExpr getTest() {
        return test;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.IfStmt;
    }
}
