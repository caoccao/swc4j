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

import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstParamOrTsParamProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;

import java.util.List;

public class Swc4jAstParam
        extends Swc4jAst
        implements ISwc4jAstParamOrTsParamProp {
    protected final List<Swc4jAstDecorator> decorators;
    protected final ISwc4jAstPat pat;

    public Swc4jAstParam(
            List<Swc4jAstDecorator> decorators,
            ISwc4jAstPat pat,
            Swc4jAstSpan span) {
        super(span);
        this.decorators = AssertionUtils.notNull(decorators, "Decorators");
        this.pat = AssertionUtils.notNull(pat, "Pat");
        childNodes = SimpleList.copyOf(decorators);
        childNodes.add(pat);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public List<Swc4jAstDecorator> getDecorators() {
        return decorators;
    }

    public ISwc4jAstPat getPat() {
        return pat;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Param;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitParam(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
