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
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsThisTypeOrIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTsTypePredicate
        extends Swc4jAst
        implements ISwc4jAstTsType {
    protected final boolean asserts;
    protected final ISwc4jAstTsThisTypeOrIdent paramName;
    protected final Optional<ISwc4jAstTsType> typeAnn;

    public Swc4jAstTsTypePredicate(
            boolean asserts,
            ISwc4jAstTsThisTypeOrIdent paramName,
            ISwc4jAstTsType typeAnn,
            Swc4jAstSpan span) {
        super(span);
        this.asserts = asserts;
        this.paramName = AssertionUtils.notNull(paramName, "ParamName");
        this.typeAnn = Optional.ofNullable(typeAnn);
        childNodes = SimpleList.immutableOf(paramName, typeAnn);
        updateParent();
    }

    public ISwc4jAstTsThisTypeOrIdent getParamName() {
        return paramName;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypePredicate;
    }

    public Optional<ISwc4jAstTsType> getTypeAnn() {
        return typeAnn;
    }

    public boolean isAsserts() {
        return asserts;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsTypePredicate(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
