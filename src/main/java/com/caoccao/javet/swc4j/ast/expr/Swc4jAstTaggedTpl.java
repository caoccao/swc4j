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

import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTaggedTpl
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr tag;
    @Jni2RustField(box = true)
    protected final Swc4jAstTpl tpl;
    protected final Optional<Swc4jAstTsTypeParamInstantiation> typeParams;

    @Jni2RustMethod
    public Swc4jAstTaggedTpl(
            ISwc4jAstExpr tag,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeParams,
            Swc4jAstTpl tpl,
            Swc4jSpan span) {
        super(span);
        this.tag = AssertionUtils.notNull(tag, "Tag");
        this.tpl = tpl;
        this.typeParams = Optional.ofNullable(typeParams);
        childNodes = SimpleList.immutableOf(tag, typeParams, tpl);
        updateParent();
    }

    public ISwc4jAstExpr getTag() {
        return tag;
    }

    public Swc4jAstTpl getTpl() {
        return tpl;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TaggedTpl;
    }

    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeParams() {
        return typeParams;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTaggedTpl(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
