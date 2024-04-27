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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstDecorator;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstAccessibility;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsParamPropParam;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstTsParamProp
        extends Swc4jAst
        implements ISwc4jAstParamOrTsParamProp {
    protected final List<Swc4jAstDecorator> decorators;
    @Jni2RustField(name = "is_override")
    protected boolean _override;
    protected Optional<Swc4jAstAccessibility> accessibility;
    protected ISwc4jAstTsParamPropParam param;
    protected boolean readonly;

    @Jni2RustMethod
    public Swc4jAstTsParamProp(
            List<Swc4jAstDecorator> decorators,
            @Jni2RustParam(optional = true) Swc4jAstAccessibility accessibility,
            @Jni2RustParam(name = "is_override") boolean _override,
            boolean readonly,
            ISwc4jAstTsParamPropParam param,
            Swc4jSpan span) {
        super(span);
        setAccessibility(accessibility);
        setOverride(_override);
        setParam(param);
        setReadonly(readonly);
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        updateParent();
    }

    @Jni2RustMethod
    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(decorators);
        childNodes.add(param);
        return childNodes;
    }

    @Jni2RustMethod
    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    @Jni2RustMethod
    public ISwc4jAstTsParamPropParam getParam() {
        return param;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsParamProp;
    }

    @Jni2RustMethod
    public boolean isOverride() {
        return _override;
    }

    @Jni2RustMethod
    public boolean isReadonly() {
        return readonly;
    }

    public Swc4jAstTsParamProp setAccessibility(Swc4jAstAccessibility accessibility) {
        this.accessibility = Optional.ofNullable(accessibility);
        return this;
    }

    public Swc4jAstTsParamProp setOverride(boolean _override) {
        this._override = _override;
        return this;
    }

    public Swc4jAstTsParamProp setParam(ISwc4jAstTsParamPropParam param) {
        this.param = AssertionUtils.notNull(param, "Param");
        return this;
    }

    public Swc4jAstTsParamProp setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitTsParamProp(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
