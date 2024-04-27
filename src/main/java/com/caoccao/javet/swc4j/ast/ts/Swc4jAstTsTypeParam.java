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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
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
    protected Optional<ISwc4jAstTsType> _default;
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
        updateParent();
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

    public Swc4jAstTsTypeParam setConst(boolean _const) {
        this._const = _const;
        return this;
    }

    public Swc4jAstTsTypeParam setConstraint(ISwc4jAstTsType constraint) {
        this.constraint = Optional.ofNullable(constraint);
        return this;
    }

    public Swc4jAstTsTypeParam setDefault(ISwc4jAstTsType _default) {
        this._default = Optional.ofNullable(_default);
        return this;
    }

    public Swc4jAstTsTypeParam setIn(boolean in) {
        this.in = in;
        return this;
    }

    public Swc4jAstTsTypeParam setName(Swc4jAstIdent name) {
        this.name = AssertionUtils.notNull(name, "Name");
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
