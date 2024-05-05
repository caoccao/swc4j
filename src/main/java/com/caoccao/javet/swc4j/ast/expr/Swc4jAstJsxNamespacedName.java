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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementName;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils, name = "JSXNamespacedName", span = false)
public class Swc4jAstJsxNamespacedName
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstJsxElementName, ISwc4jAstJsxAttrName {
    protected Swc4jAstIdent name;
    protected Swc4jAstIdent ns;

    @Jni2RustMethod
    public Swc4jAstJsxNamespacedName(
            Swc4jAstIdent ns,
            Swc4jAstIdent name,
            Swc4jSpan span) {
        super(span);
        setName(name);
        setNs(ns);
        updateParent();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(ns, name);
    }

    @Jni2RustMethod
    public Swc4jAstIdent getName() {
        return name;
    }

    @Jni2RustMethod
    public Swc4jAstIdent getNs() {
        return ns;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxNamespacedName;
    }

    public Swc4jAstJsxNamespacedName setName(Swc4jAstIdent name) {
        this.name = AssertionUtils.notNull(name, "Name");
        return this;
    }

    public Swc4jAstJsxNamespacedName setNs(Swc4jAstIdent ns) {
        this.ns = AssertionUtils.notNull(ns, "Ns");
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitJsxNamespacedName(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
