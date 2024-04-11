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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstClassMember;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstConstructor
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    protected final Optional<Swc4jAstAccessibility> accessibility;
    protected final Optional<Swc4jAstBlockStmt> body;
    protected final ISwc4jAstPropName key;
    @Jni2RustField(name = "is_optional")
    protected final boolean optional;
    protected final List<ISwc4jAstParamOrTsParamProp> params;

    public Swc4jAstConstructor(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            Swc4jAstBlockStmt body,
            Swc4jAstAccessibility accessibility,
            boolean optional,
            Swc4jAstSpan span) {
        super(span);
        this.accessibility = Optional.ofNullable(accessibility);
        this.body = Optional.ofNullable(body);
        this.key = AssertionUtils.notNull(key, "Key");
        this.optional = optional;
        this.params = AssertionUtils.notNull(params, "Params");
        childNodes = SimpleList.copyOf(params);
        childNodes.add(body);
        childNodes.add(key);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public Optional<Swc4jAstAccessibility> getAccessibility() {
        return accessibility;
    }

    public Optional<Swc4jAstBlockStmt> getBody() {
        return body;
    }

    public ISwc4jAstPropName getKey() {
        return key;
    }

    public List<ISwc4jAstParamOrTsParamProp> getParams() {
        return params;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Constructor;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitConstructor(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
