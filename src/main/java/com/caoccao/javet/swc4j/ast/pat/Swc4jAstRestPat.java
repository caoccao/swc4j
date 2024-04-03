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

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstObjectPatProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeAnn;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstRestPat
        extends Swc4jAst
        implements ISwc4jAstPat, ISwc4jAstObjectPatProp {
    protected final ISwc4jAstPat arg;
    protected final Swc4jAstSpan dot3Token;
    @Nullable
    protected final Swc4jAstTsTypeAnn typeAnn;

    public Swc4jAstRestPat(
            Swc4jAstSpan dot3Token,
            ISwc4jAstPat arg,
            Swc4jAstTsTypeAnn typeAnn,
            Swc4jAstSpan span) {
        super(span);
        this.dot3Token = AssertionUtils.notNull(dot3Token, "Dot3 token");
        this.arg = AssertionUtils.notNull(arg, "Expr");
        this.typeAnn = typeAnn;
        children = SimpleList.immutableOf(arg, typeAnn);
        updateParent();
    }

    public ISwc4jAstPat getArg() {
        return arg;
    }

    public Swc4jAstSpan getDot3Token() {
        return dot3Token;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.RestPat;
    }

    public Swc4jAstTsTypeAnn getTypeAnn() {
        return typeAnn;
    }
}
