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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Collections;
import java.util.List;

public class Swc4jAstConstructor
        extends Swc4jAst
        implements ISwc4jAstClassMember {
    protected final Swc4jAstAccessibility accessibility;
    @Nullable
    protected final Swc4jAstBlockStmt body;
    protected final ISwc4jAstPropName key;
    protected final boolean optional;
    protected final List<ISwc4jAstParamOrTsParamProp> params;

    public Swc4jAstConstructor(
            ISwc4jAstPropName key,
            List<ISwc4jAstParamOrTsParamProp> params,
            Swc4jAstBlockStmt body,
            Swc4jAstAccessibility accessibility,
            boolean optional,
            int startPosition,
            int endPosition) {
        super(startPosition, endPosition);
        this.accessibility = AssertionUtils.notNull(accessibility, "Accessibility");
        this.body = body;
        this.key = AssertionUtils.notNull(key, "Key");
        this.optional = optional;
        this.params = AssertionUtils.notNull(params, "Params");
        children = SimpleList.copyOf(params);
        children.add(body);
        children.add(key);
        children = Collections.unmodifiableList(children);
        updateParent();
    }

    public Swc4jAstAccessibility getAccessibility() {
        return accessibility;
    }

    public Swc4jAstBlockStmt getBody() {
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
        return Swc4jAstType.Decorator;
    }

    public boolean isOptional() {
        return optional;
    }
}
