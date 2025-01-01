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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstObjectPat
        extends Swc4jAst
        implements ISwc4jAstPat, ISwc4jAstAssignTargetPat, ISwc4jAstTsFnParam, ISwc4jAstSimpleAssignTarget {
    protected final List<ISwc4jAstObjectPatProp> props;
    protected boolean optional;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeAnn> typeAnn;

    @Jni2RustMethod
    public Swc4jAstObjectPat(
            List<ISwc4jAstObjectPatProp> props,
            boolean optional,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeAnn typeAnn,
            Swc4jSpan span) {
        super(span);
        setOptional(optional);
        setTypeAnn(typeAnn);
        this.props = AssertionUtils.notNull(props, "Props");
        this.props.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstObjectPat create() {
        return create(SimpleList.of());
    }

    public static Swc4jAstObjectPat create(List<ISwc4jAstObjectPatProp> props) {
        return create(props, false);
    }

    public static Swc4jAstObjectPat create(List<ISwc4jAstObjectPatProp> props, boolean optional) {
        return create(props, optional, null);
    }

    public static Swc4jAstObjectPat create(
            List<ISwc4jAstObjectPatProp> props,
            boolean optional,
            Swc4jAstTsTypeAnn typeAnn) {
        return new Swc4jAstObjectPat(props, optional, typeAnn, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(props);
        typeAnn.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<ISwc4jAstObjectPatProp> getProps() {
        return props;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ObjectPat;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    @Jni2RustMethod
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!props.isEmpty() && newNode instanceof ISwc4jAstObjectPatProp) {
            final int size = props.size();
            for (int i = 0; i < size; i++) {
                if (props.get(i) == oldNode) {
                    props.set(i, (ISwc4jAstObjectPatProp) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (typeAnn.isPresent() && typeAnn.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeAnn)) {
            setTypeAnn((Swc4jAstTsTypeAnn) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstObjectPat setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public Swc4jAstObjectPat setTypeAnn(Swc4jAstTsTypeAnn typeAnn) {
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.typeAnn.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitObjectPat(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
