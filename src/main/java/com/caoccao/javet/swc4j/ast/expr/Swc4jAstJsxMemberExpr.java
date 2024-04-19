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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxObject;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.span.Swc4jSpan;

@Jni2RustClass(name = "JSXMemberExpr", span = false)
public class Swc4jAstJsxMemberExpr
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstJsxObject, ISwc4jAstJsxElementName {
    protected final ISwc4jAstJsxObject obj;
    protected final Swc4jAstIdent prop;

    public Swc4jAstJsxMemberExpr(
            ISwc4jAstJsxObject obj,
            Swc4jAstIdent prop,
            Swc4jSpan span) {
        super(span);
        this.obj = AssertionUtils.notNull(obj, "Obj");
        this.prop = AssertionUtils.notNull(prop, "Prop");
        childNodes = SimpleList.immutableOf(obj, prop);
        updateParent();
    }

    public ISwc4jAstJsxObject getObj() {
        return obj;
    }

    public Swc4jAstIdent getProp() {
        return prop;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxMemberExpr;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitJsxMemberExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
