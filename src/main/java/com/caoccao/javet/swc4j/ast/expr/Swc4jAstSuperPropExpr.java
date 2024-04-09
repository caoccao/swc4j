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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstSuper;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstSuperProp;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstSuperPropExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final Swc4jAstSuper obj;
    protected final ISwc4jAstSuperProp prop;

    public Swc4jAstSuperPropExpr(
            Swc4jAstSuper obj,
            ISwc4jAstSuperProp prop,
            Swc4jAstSpan span) {
        super(span);
        this.prop = AssertionUtils.notNull(prop, "Prop");
        this.obj = AssertionUtils.notNull(obj, "Obj");
        childNodes = SimpleList.immutableOf(obj, prop);
        updateParent();
    }

    public Swc4jAstSuper getObj() {
        return obj;
    }

    public ISwc4jAstSuperProp getProp() {
        return prop;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.SuperPropExpr;
    }
}
