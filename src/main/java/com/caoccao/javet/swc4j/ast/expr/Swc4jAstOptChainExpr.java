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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstOptChainBase;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstSimpleAssignTarget;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstOptChainExpr
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstSimpleAssignTarget {
    @Jni2RustField(box = true)
    protected final ISwc4jAstOptChainBase base;
    protected final boolean optional;

    @Jni2RustMethod
    public Swc4jAstOptChainExpr(
            boolean optional,
            ISwc4jAstOptChainBase base,
            Swc4jSpan span) {
        super(span);
        this.base = AssertionUtils.notNull(base, "Base");
        this.optional = optional;
        updateParent();
    }

    public ISwc4jAstOptChainBase getBase() {
        return base;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(base);
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.OptChainExpr;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitOptChainExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
