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

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstKey;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Collections;
import java.util.List;

public class Swc4jAstAutoAccessor
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    protected final boolean _static;
    @Nullable
    protected final Swc4jAstAccessibility accessibility;
    protected final List<Swc4jAstDecorator> decorators;
    protected final ISwc4jAstKey key;
    @Nullable
    protected final Swc4jAstTsTypeAnn typeAnn;
    @Nullable
    protected final ISwc4jAstExpr value;

    public Swc4jAstAutoAccessor(
            ISwc4jAstKey key,
            ISwc4jAstExpr value,
            Swc4jAstTsTypeAnn typeAnn,
            boolean _static,
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this._static = _static;
        this.accessibility = accessibility;
        this.decorators = SimpleList.immutableCopyOf(AssertionUtils.notNull(decorators, "Decorators"));
        this.key = AssertionUtils.notNull(key, "Key");
        this.typeAnn = typeAnn;
        this.value = value;
        children = SimpleList.copyOf(decorators);
        children.add(key);
        children.add(value);
        children.add(typeAnn);
        children = Collections.unmodifiableList(children);
        updateParent();
    }

    public Swc4jAstAccessibility getAccessibility() {
        return accessibility;
    }

    public ISwc4jAstKey getKey() {
        return key;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.AutoAccessor;
    }

    public Swc4jAstTsTypeAnn getTypeAnn() {
        return typeAnn;
    }

    public ISwc4jAstExpr getValue() {
        return value;
    }

    public boolean is_static() {
        return _static;
    }
}
