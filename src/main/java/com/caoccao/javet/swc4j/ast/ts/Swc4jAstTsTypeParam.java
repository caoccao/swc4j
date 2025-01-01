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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
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
public class Swc4jAstTsTypeParam
        extends Swc4jAst {
    @Jni2RustField(name = "is_const")
    protected boolean _const;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstTsType> _default;
    @Jni2RustField(componentBox = true)
    protected Optional<ISwc4jAstTsType> constraint;
    @Jni2RustField(name = "is_in")
    protected boolean in;
    protected Swc4jAstIdent name;
    @Jni2RustField(name = "is_out")
    protected boolean out;

    @Jni2RustMethod
    public Swc4jAstTsTypeParam(
            Swc4jAstIdent name,
            @Jni2RustParam(name = "is_in") boolean in,
            @Jni2RustParam(name = "is_out") boolean out,
            @Jni2RustParam(name = "is_count") boolean _const,
            @Jni2RustParam(optional = true) ISwc4jAstTsType constraint,
            @Jni2RustParam(optional = true) ISwc4jAstTsType _default,
            Swc4jSpan span) {
        super(span);
        setConst(_const);
        setConstraint(constraint);
        setDefault(_default);
        setIn(in);
        setName(name);
        setOut(out);
    }

    public static Swc4jAstTsTypeParam create(Swc4jAstIdent name) {
        return create(name, false);
    }

    public static Swc4jAstTsTypeParam create(Swc4jAstIdent name, boolean in) {
        return create(name, in, false);
    }

    public static Swc4jAstTsTypeParam create(Swc4jAstIdent name, boolean in, boolean out) {
        return create(name, in, out, false);
    }

    public static Swc4jAstTsTypeParam create(Swc4jAstIdent name, boolean in, boolean out, boolean _const) {
        return create(name, in, out, _const, null);
    }

    public static Swc4jAstTsTypeParam create(
            Swc4jAstIdent name,
            boolean in,
            boolean out,
            boolean _const,
            ISwc4jAstTsType constraint) {
        return create(name, in, out, _const, constraint, null);
    }

    public static Swc4jAstTsTypeParam create(
            Swc4jAstIdent name,
            boolean in,
            boolean out,
            boolean _const,
            ISwc4jAstTsType constraint,
            ISwc4jAstTsType _default) {
        return new Swc4jAstTsTypeParam(name, in, out, _const, constraint, _default, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of(name);
        _default.ifPresent(childNodes::add);
        constraint.ifPresent(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstTsType> getConstraint() {
        return constraint;
    }

    @Jni2RustMethod
    public Optional<ISwc4jAstTsType> getDefault() {
        return _default;
    }

    @Jni2RustMethod
    public Swc4jAstIdent getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeParam;
    }

    @Jni2RustMethod
    public boolean isConst() {
        return _const;
    }

    @Jni2RustMethod
    public boolean isIn() {
        return in;
    }

    @Jni2RustMethod
    public boolean isOut() {
        return out;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (_default.isPresent() && _default.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstTsType)) {
            setDefault((ISwc4jAstTsType) newNode);
            return true;
        }
        if (constraint.isPresent() && constraint.get() == oldNode && (newNode == null || newNode instanceof ISwc4jAstTsType)) {
            setConstraint((ISwc4jAstTsType) newNode);
            return true;
        }
        if (name == oldNode && newNode instanceof Swc4jAstIdent) {
            setName((Swc4jAstIdent) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstTsTypeParam setConst(boolean _const) {
        this._const = _const;
        return this;
    }

    public Swc4jAstTsTypeParam setConstraint(ISwc4jAstTsType constraint) {
        this.constraint = Optional.ofNullable(constraint);
        this.constraint.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstTsTypeParam setDefault(ISwc4jAstTsType _default) {
        this._default = Optional.ofNullable(_default);
        this._default.ifPresent(node -> node.setParent(this));
        return this;
    }

    public Swc4jAstTsTypeParam setIn(boolean in) {
        this.in = in;
        return this;
    }

    public Swc4jAstTsTypeParam setName(Swc4jAstIdent name) {
        this.name = AssertionUtils.notNull(name, "Name");
        this.name.setParent(this);
        return this;
    }

    public Swc4jAstTsTypeParam setOut(boolean out) {
        this.out = out;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsTypeParam(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
