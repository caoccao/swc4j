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

package com.caoccao.javet.swc4j.ast.stmt;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstDefaultDecl;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsExprWithTypeArgs;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsInterfaceBody;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParam;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstTsInterfaceDecl
        extends Swc4jAst
        implements ISwc4jAstDefaultDecl {
    protected final List<Swc4jAstTsExprWithTypeArgs> _extends;
    protected final Swc4jAstTsInterfaceBody body;
    protected final boolean declare;
    protected final Swc4jAstIdent id;
    protected final Optional<Swc4jAstTsTypeParam> typeParams;

    public Swc4jAstTsInterfaceDecl(
            Swc4jAstIdent id,
            boolean declare,
            Swc4jAstTsTypeParam typeParams,
            List<Swc4jAstTsExprWithTypeArgs> _extends,
            Swc4jAstTsInterfaceBody body,
            Swc4jAstSpan span) {
        super(span);
        this._extends = SimpleList.immutableCopyOf(AssertionUtils.notNull(_extends, "Extends"));
        this.body = AssertionUtils.notNull(body, "Body");
        this.declare = declare;
        this.id = AssertionUtils.notNull(id, "Id");
        this.typeParams = Optional.ofNullable(typeParams);
        children = SimpleList.immutableOf(id, typeParams);
        updateParent();
    }

    public Swc4jAstTsInterfaceBody getBody() {
        return body;
    }

    public List<Swc4jAstTsExprWithTypeArgs> getExtends() {
        return _extends;
    }

    public Swc4jAstIdent getId() {
        return id;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsInterfaceDecl;
    }

    public Optional<Swc4jAstTsTypeParam> getTypeParams() {
        return typeParams;
    }

    public boolean isDeclare() {
        return declare;
    }
}
