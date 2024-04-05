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

package com.caoccao.javet.swc4j.ast.miscs;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstOptChainBase;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

public class Swc4jAstOptCall
        extends Swc4jAst
        implements ISwc4jAstOptChainBase {
    protected final List<Swc4jAstExprOrSpread> args;
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr callee;
    protected final Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    public Swc4jAstOptCall(
            ISwc4jAstExpr callee,
            List<Swc4jAstExprOrSpread> args,
            Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jAstSpan span) {
        super(span);
        this.args = SimpleList.immutableCopyOf(AssertionUtils.notNull(args, "Args"));
        this.callee = AssertionUtils.notNull(callee, "Callee");
        this.typeArgs = Optional.ofNullable(typeArgs);
        children = SimpleList.copyOf(args);
        children.add(callee);
        children.add(typeArgs);
        children = SimpleList.immutable(children);
    }

    public List<Swc4jAstExprOrSpread> getArgs() {
        return args;
    }

    public ISwc4jAstExpr getCallee() {
        return callee;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.OptCall;
    }

    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }
}
