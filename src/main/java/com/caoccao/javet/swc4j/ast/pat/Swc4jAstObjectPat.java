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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsFnParam;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstObjectPat
        extends Swc4jAst
        implements ISwc4jAstPat, ISwc4jAstTsFnParam {
    protected final boolean optional;
    protected final List<ISwc4jAstObjectPatProp> props;
    protected final Optional<Swc4jAstTsTypeAnn> typeAnn;

    public Swc4jAstObjectPat(
            List<ISwc4jAstObjectPatProp> props,
            boolean optional,
            Swc4jAstTsTypeAnn typeAnn,
            Swc4jAstSpan span) {
        super(span);
        this.props = SimpleList.immutableCopyOf(AssertionUtils.notNull(props, "Props"));
        this.optional = optional;
        this.typeAnn = Optional.ofNullable(typeAnn);
        children = SimpleList.copyOf(props);
        children.add(typeAnn);
        children = SimpleList.immutable(children);
        updateParent();
    }

    public List<ISwc4jAstObjectPatProp> getProps() {
        return props;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ObjectPat;
    }

    public Optional<Swc4jAstTsTypeAnn> getTypeAnn() {
        return typeAnn;
    }

    public boolean isOptional() {
        return optional;
    }
}
