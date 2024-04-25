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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstNewExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final Optional<List<Swc4jAstExprOrSpread>> args;
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr callee;
    protected final Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    @Jni2RustMethod
    public Swc4jAstNewExpr(
            ISwc4jAstExpr callee,
            @Jni2RustParam(optional = true) List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jSpan span) {
        super(span);
        this.args = Optional.ofNullable(args).map(SimpleList::immutableCopyOf);
        this.callee = AssertionUtils.notNull(callee, "Callee");
        this.typeArgs = Optional.ofNullable(typeArgs);
        childNodes = args == null ? SimpleList.of() : SimpleList.copyOf(args);
        childNodes.add(callee);
        childNodes.add(typeArgs);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public Optional<List<Swc4jAstExprOrSpread>> getArgs() {
        return args;
    }

    public ISwc4jAstExpr getCallee() {
        return callee;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.NewExpr;
    }

    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitNewExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
