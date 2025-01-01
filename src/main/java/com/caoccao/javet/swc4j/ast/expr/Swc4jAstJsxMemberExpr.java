/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxObject;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, name = "JSXMemberExpr")
public class Swc4jAstJsxMemberExpr
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstJsxObject, ISwc4jAstJsxElementName {
    protected ISwc4jAstJsxObject obj;
    protected Swc4jAstIdentName prop;

    @Jni2RustMethod
    public Swc4jAstJsxMemberExpr(
            ISwc4jAstJsxObject obj,
            Swc4jAstIdentName prop,
            Swc4jSpan span) {
        super(span);
        setObj(obj);
        setProp(prop);
    }

    public static Swc4jAstJsxMemberExpr create(ISwc4jAstJsxObject obj, Swc4jAstIdentName prop) {
        return new Swc4jAstJsxMemberExpr(obj, prop, Swc4jSpan.DUMMY);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(obj, prop);
    }

    @Jni2RustMethod
    public ISwc4jAstJsxObject getObj() {
        return obj;
    }

    @Jni2RustMethod
    public Swc4jAstIdentName getProp() {
        return prop;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxMemberExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (obj == oldNode && newNode instanceof ISwc4jAstJsxObject) {
            setObj((ISwc4jAstJsxObject) newNode);
            return true;
        }
        if (prop == oldNode && newNode instanceof Swc4jAstIdentName) {
            setProp((Swc4jAstIdentName) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstJsxMemberExpr setObj(ISwc4jAstJsxObject obj) {
        this.obj = AssertionUtils.notNull(obj, "Obj");
        this.obj.setParent(this);
        return this;
    }

    public Swc4jAstJsxMemberExpr setProp(Swc4jAstIdentName prop) {
        this.prop = AssertionUtils.notNull(prop, "Prop");
        this.prop.setParent(this);
        return this;
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
