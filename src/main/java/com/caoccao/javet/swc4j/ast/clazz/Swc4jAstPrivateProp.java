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
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstKey;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstPrivateProp
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    protected final boolean _override;
    protected final boolean _static;
    protected final Optional<Swc4jAstAccessibility> accessibility;
    protected final List<Swc4jAstDecorator> decorators;
    protected final boolean definite;
    protected final ISwc4jAstKey key;
    protected final boolean optional;
    protected final boolean readonly;
    protected final Optional<Swc4jAstTsTypeAnn> typeAnn;
    protected final Optional<ISwc4jAstExpr> value;

    public Swc4jAstPrivateProp(
            ISwc4jAstKey key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean optional,
            boolean _override,
            boolean readonly,
            boolean definite,
            Swc4jAstSpan span) {
        super(span);
        this._override = _override;
        this._static = _static;
        this.accessibility = Optional.ofNullable(accessibility);
        this.decorators = SimpleList.immutableCopyOf(AssertionUtils.notNull(decorators, "Decorators"));
        this.definite = definite;
        this.key = AssertionUtils.notNull(key, "Key");
        this.optional = optional;
        this.readonly = readonly;
        this.typeAnn = Optional.ofNullable(typeAnn);
        this.value = Optional.ofNullable(value);
        children = SimpleList.copyOf(decorators);
        children.add(key);
        children.add(value);
        children.add(typeAnn);
        children = SimpleList.immutable(children);
        updateParent();
    }

    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    public ISwc4jAstKey getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.PrivateProp;
    }

    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    public Optional<ISwc4jAstExpr> getValue() {
        return value;
    }

    public boolean isDefinite() {
        return definite;
    }

    public boolean isOverride() {
        return _override;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isStatic() {
        return _static;
    }
}