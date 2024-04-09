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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementName;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

@Jni2RustClass(name = "JSXNamespacedName", span = false)
public class Swc4jAstJsxNamespacedName
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstJsxElementName, ISwc4jAstJsxAttrName {
    protected final Swc4jAstIdent name;
    protected final Swc4jAstIdent ns;

    public Swc4jAstJsxNamespacedName(
            Swc4jAstIdent ns,
            Swc4jAstIdent name,
            Swc4jAstSpan span) {
        super(span);
        this.name = AssertionUtils.notNull(name, "Name");
        this.ns = AssertionUtils.notNull(ns, "Ns");
        childNodes = SimpleList.immutableOf(ns, name);
        updateParent();
    }

    public Swc4jAstIdent getName() {
        return name;
    }

    public Swc4jAstIdent getNs() {
        return ns;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxNamespacedName;
    }
}
