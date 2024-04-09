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
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTsTypeParam
        extends Swc4jAst {
    @Jni2RustField(name = "is_const")
    protected final boolean _const;
    protected final Optional<ISwc4jAstTsType> _default;
    protected final Optional<ISwc4jAstTsType> constraint;
    @Jni2RustField(name = "is_in")
    protected final boolean in;
    protected final Swc4jAstIdent name;
    @Jni2RustField(name = "is_out")
    protected final boolean out;

    public Swc4jAstTsTypeParam(
            Swc4jAstIdent name,
            boolean in,
            boolean out,
            boolean _const,
            ISwc4jAstTsType constraint,
            ISwc4jAstTsType _default,
            Swc4jAstSpan span) {
        super(span);
        this._const = _const;
        this._default = Optional.ofNullable(_default);
        this.constraint = Optional.ofNullable(constraint);
        this.in = in;
        this.name = AssertionUtils.notNull(name, "Name");
        this.out = out;
        childNodes = SimpleList.immutableOf(name, constraint, _default);
        updateParent();
    }

    public Optional<ISwc4jAstTsType> getConstraint() {
        return constraint;
    }

    public Optional<ISwc4jAstTsType> getDefault() {
        return _default;
    }

    public Swc4jAstIdent getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeParam;
    }

    public boolean isConst() {
        return _const;
    }

    public boolean isIn() {
        return in;
    }

    public boolean isOut() {
        return out;
    }
}
