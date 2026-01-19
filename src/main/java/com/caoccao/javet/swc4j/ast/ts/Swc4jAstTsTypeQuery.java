/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsTypeQueryExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsTypeQuery
        extends Swc4jAst
        implements ISwc4jAstTsType {
    protected ISwc4jAstTsTypeQueryExpr exprName;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    @Jni2RustMethod
    public Swc4jAstTsTypeQuery(
            ISwc4jAstTsTypeQueryExpr exprName,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jSpan span) {
        super(span);
        setExprName(exprName);
        setTypeArgs(typeArgs);
    }

    public static Swc4jAstTsTypeQuery create(ISwc4jAstTsTypeQueryExpr exprName) {
        return create(exprName, null);
    }

    public static Swc4jAstTsTypeQuery create(
            ISwc4jAstTsTypeQueryExpr exprName,
            Swc4jAstTsTypeParamInstantiation typeArgs) {
        return new Swc4jAstTsTypeQuery(exprName, typeArgs, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(exprName);
        typeArgs.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstTsTypeQueryExpr getExprName() {
        return exprName;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeQuery;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (exprName == oldNode && newNode instanceof ISwc4jAstTsTypeQueryExpr newExprName) {
            setExprName(newExprName);
            return true;
        }
        if (typeArgs.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setTypeArgs((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsTypeQuery setExprName(ISwc4jAstTsTypeQueryExpr exprName) {
        this.exprName = AssertionUtils.notNull(exprName, "Expr name");
        this.exprName.setParent(this);
        return this;
    }

    public Swc4jAstTsTypeQuery setTypeArgs(Swc4jAstTsTypeParamInstantiation typeArgs) {
        this.typeArgs = Optional.ofNullable(typeArgs);
        this.typeArgs.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitTsTypeQuery(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
