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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstMemberProp;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstOptChainBase;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstSimpleAssignTarget;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.Swc4jSpan;

public class Swc4jAstMemberExpr
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstOptChainBase, ISwc4jAstSimpleAssignTarget {
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr obj;
    protected final ISwc4jAstMemberProp prop;

    public Swc4jAstMemberExpr(
            ISwc4jAstExpr obj,
            ISwc4jAstMemberProp prop,
            Swc4jSpan span) {
        super(span);
        this.prop = AssertionUtils.notNull(prop, "Prop");
        this.obj = AssertionUtils.notNull(obj, "Obj");
        childNodes = SimpleList.immutableOf(obj, prop);
        updateParent();
    }

    public ISwc4jAstExpr getObj() {
        return obj;
    }

    public ISwc4jAstMemberProp getProp() {
        return prop;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.MemberExpr;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitMemberExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
