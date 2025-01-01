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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
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
public class Swc4jAstTsTupleElement
        extends Swc4jAst {
    protected Optional<ISwc4jAstPat> label;
    @Jni2RustField(box = true)
    protected ISwc4jAstTsType ty;

    @Jni2RustMethod
    public Swc4jAstTsTupleElement(
            @Jni2RustParam(optional = true) ISwc4jAstPat label,
            ISwc4jAstTsType ty,
            Swc4jSpan span) {
        super(span);
        setLabel(label);
        setTy(ty);
    }

    public static Swc4jAstTsTupleElement create(ISwc4jAstTsType ty) {
        return create(null, ty);
    }

    public static Swc4jAstTsTupleElement create(ISwc4jAstPat label, ISwc4jAstTsType ty) {
        return new Swc4jAstTsTupleElement(label, ty, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(ty);
        label.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstPat> getLabel() {
        return label;
    }

    @Jni2RustMethod
    public ISwc4jAstTsType getTy() {
        return ty;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTupleElement;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (label.isPresent() && label.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstPat)) {
            setLabel((ISwc4jAstPat) newNode);
            return true;
        }
        if (ty == oldNode && newNode instanceof ISwc4jAstTsType) {
            setTy((ISwc4jAstTsType) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsTupleElement setLabel(ISwc4jAstPat label) {
        this.label = Optional.ofNullable(label);
        this.label.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstTsTupleElement setTy(ISwc4jAstTsType ty) {
        this.ty = AssertionUtils.notNull(ty, "Ty");
        this.ty.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsTupleElement(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
