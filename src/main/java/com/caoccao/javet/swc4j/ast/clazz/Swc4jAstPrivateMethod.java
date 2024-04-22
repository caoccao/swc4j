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

package com.caoccao.javet.swc4j.ast.clazz;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstMethodKind;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstPrivateMethod
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    @Jni2RustField(name = "is_abstract")
    protected final boolean _abstract;
    @Jni2RustField(name = "is_override")
    protected final boolean _override;
    @Jni2RustField(name = "is_static")
    protected final boolean _static;
    protected final Optional<Swc4jAstAccessibility> accessibility;
    @Jni2RustField(box = true)
    protected final Swc4jAstFunction function;
    protected final Swc4jAstPrivateName key;
    protected final Swc4jAstMethodKind kind;
    @Jni2RustField(name = "is_optional")
    protected final boolean optional;

    public Swc4jAstPrivateMethod(
            Swc4jAstPrivateName key,
            Swc4jAstFunction function,
            Swc4jAstMethodKind kind,
            boolean _static,
            Swc4jAstAccessibility accessibility,
            boolean _abstract,
            boolean optional,
            boolean _override,
            Swc4jSpan span) {
        super(span);
        this._abstract = _abstract;
        this._override = _override;
        this._static = _static;
        this.accessibility = Optional.ofNullable(accessibility);
        this.function = function;
        this.key = AssertionUtils.notNull(key, "Key");
        this.kind = AssertionUtils.notNull(kind, "Kind");
        this.optional = optional;
        childNodes = SimpleList.immutableOf(key, function);
        updateParent();
    }

    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    public Swc4jAstFunction getFunction() {
        return function;
    }

    public Swc4jAstPrivateName getKey() {
        return key;
    }

    public Swc4jAstMethodKind getKind() {
        return kind;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.PrivateMethod;
    }

    public boolean isAbstract() {
        return _abstract;
    }

    public boolean isOverride() {
        return _override;
    }

    public boolean isStatic() {
        return _static;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitPrivateMethod(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
