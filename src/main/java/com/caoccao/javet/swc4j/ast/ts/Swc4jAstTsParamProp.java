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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstDecorator;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsParamPropParam;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstTsParamProp
        extends Swc4jAst
        implements ISwc4jAstParamOrTsParamProp {
    @Jni2RustField(name = "is_override")
    protected final boolean _override;
    protected final Optional<Swc4jAstAccessibility> accessibility;
    protected final List<Swc4jAstDecorator> decorators;
    protected final ISwc4jAstTsParamPropParam param;
    protected final boolean readonly;

    public Swc4jAstTsParamProp(
            List<Swc4jAstDecorator> decorators,
            Swc4jAstAccessibility accessibility,
            boolean _override,
            boolean readonly,
            ISwc4jAstTsParamPropParam param,
            Swc4jAstSpan span) {
        super(span);
        this._override = _override;
        this.accessibility = Optional.ofNullable(accessibility);
        this.decorators = SimpleList.immutableCopyOf(AssertionUtils.notNull(decorators, "Decorators"));
        this.param = AssertionUtils.notNull(param, "Param");
        this.readonly = readonly;
        childNodes = SimpleList.copyOf(decorators);
        childNodes.add(param);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    public ISwc4jAstTsParamPropParam getParam() {
        return param;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsParamProp;
    }

    public boolean isOverride() {
        return _override;
    }

    public boolean isReadonly() {
        return readonly;
    }
}
