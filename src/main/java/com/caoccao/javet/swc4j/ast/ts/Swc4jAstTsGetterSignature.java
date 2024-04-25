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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeElement;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsGetterSignature
        extends Swc4jAst
        implements ISwc4jAstTsTypeElement {
    protected final boolean computed;
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr key;
    protected final boolean optional;
    protected final boolean readonly;
    protected final Optional<Swc4jAstTsTypeAnn> typeAnn;

    @Jni2RustMethod
    public Swc4jAstTsGetterSignature(
            boolean readonly,
            ISwc4jAstExpr key,
            boolean computed,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            Swc4jSpan span) {
        super(span);
        this.computed = computed;
        this.key = AssertionUtils.notNull(key, "Key");
        this.optional = optional;
        this.readonly = readonly;
        this.typeAnn = Optional.ofNullable(typeAnn);
        childNodes = SimpleList.immutableOf(typeAnn);
        updateParent();
    }

    public ISwc4jAstExpr getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsGetterSignature;
    }

    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    public boolean isComputed() {
        return computed;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsGetterSignature(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
