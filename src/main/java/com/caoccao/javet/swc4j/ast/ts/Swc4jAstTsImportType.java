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
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEntityName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeQueryExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.span.Swc4jSpan;

import java.util.Optional;

public class Swc4jAstTsImportType
        extends Swc4jAst
        implements ISwc4jAstTsType, ISwc4jAstTsTypeQueryExpr {
    protected final Swc4jAstStr arg;
    protected final Optional<ISwc4jAstTsEntityName> qualifier;
    protected final Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    public Swc4jAstTsImportType(
            Swc4jAstStr arg,
            ISwc4jAstTsEntityName qualifier,
            Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jSpan span) {
        super(span);
        this.arg = AssertionUtils.notNull(arg, "Arg");
        this.qualifier = Optional.ofNullable(qualifier);
        this.typeArgs = Optional.ofNullable(typeArgs);
        childNodes = SimpleList.immutableOf(arg, qualifier, typeArgs);
        updateParent();
    }

    public Swc4jAstStr getArg() {
        return arg;
    }

    public Optional<ISwc4jAstTsEntityName> getQualifier() {
        return qualifier;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsImportType;
    }

    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsImportType(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
