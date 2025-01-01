/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsThisTypeOrIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsTypePredicate
        extends Swc4jAst
        implements ISwc4jAstTsType {
    protected boolean asserts;
    protected ISwc4jAstTsThisTypeOrIdent paramName;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;

    @Jni2RustMethod
    public Swc4jAstTsTypePredicate(
            boolean asserts,
            ISwc4jAstTsThisTypeOrIdent paramName,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            Swc4jSpan span) {
        super(span);
        setAsserts(asserts);
        setParamName(paramName);
        setTypeAnn(typeAnn);
    }

    public static Swc4jAstTsTypePredicate create(ISwc4jAstTsThisTypeOrIdent paramName) {
        return create(false, paramName);
    }

    public static Swc4jAstTsTypePredicate create(boolean asserts, ISwc4jAstTsThisTypeOrIdent paramName) {
        return create(asserts, paramName, null);
    }

    public static Swc4jAstTsTypePredicate create(
            boolean asserts,
            ISwc4jAstTsThisTypeOrIdent paramName,
            Swc4jAstTsTypeAnn typeAnn) {
        return new Swc4jAstTsTypePredicate(asserts, paramName, typeAnn, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(paramName);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public ISwc4jAstTsThisTypeOrIdent getParamName() {
        return paramName;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypePredicate;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    @Jni2RustMethod
    public boolean isAsserts() {
        return asserts;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (paramName == oldNode && newNode instanceof ISwc4jAstTsThisTypeOrIdent) {
            setParamName((ISwc4jAstTsThisTypeOrIdent) newNode);
            return true;
        }
        if (typeAnn.isPresent() && typeAnn.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeAnn)) {
            setTypeAnn((Swc4jAstTsTypeAnn) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsTypePredicate setAsserts(boolean asserts) {
        this.asserts = asserts;
        return this;
    }

    public Swc4jAstTsTypePredicate setParamName(ISwc4jAstTsThisTypeOrIdent paramName) {
        this.paramName = AssertionUtils.notNull(paramName, "Param name");
        this.paramName.setParent(this);
        return this;
    }

    public Swc4jAstTsTypePredicate setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeAnn.ifPresent(node -> node.setParent(this));
        return this;
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
