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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropOrSpread;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

public class Swc4jAstObjectLit
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected final List<ISwc4jAstPropOrSpread> props;

    public Swc4jAstObjectLit(
            List<ISwc4jAstPropOrSpread> props,
            Swc4jAstSpan span) {
        super(span);
        this.props = SimpleList.immutableCopyOf(AssertionUtils.notNull(props, "Props"));
        childNodes = SimpleList.immutableCopyOf(props);
        updateParent();
    }

    public List<ISwc4jAstPropOrSpread> getProps() {
        return props;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ObjectLit;
    }
}
