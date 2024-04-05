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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxElementName;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(name = "JSXOpeningElement")
public class Swc4jAstJsxOpeningElement
        extends Swc4jAst {
    protected final List<ISwc4jAstJsxAttrOrSpread> attrs;
    protected final ISwc4jAstJsxElementName name;
    protected final boolean selfClosing;
    protected final Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    public Swc4jAstJsxOpeningElement(
            ISwc4jAstJsxElementName name,
            List<ISwc4jAstJsxAttrOrSpread> attrs,
            boolean selfClosing,
            Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jAstSpan span) {
        super(span);
        this.attrs = SimpleList.immutableCopyOf(AssertionUtils.notNull(attrs, "Attrs"));
        this.name = AssertionUtils.notNull(name, "Name");
        this.selfClosing = selfClosing;
        this.typeArgs = Optional.ofNullable(typeArgs);
        childNodes = SimpleList.copyOf(attrs);
        childNodes.add(name);
        childNodes.add(typeArgs);
        childNodes = SimpleList.immutable(childNodes);
        updateParent();
    }

    public List<ISwc4jAstJsxAttrOrSpread> getAttrs() {
        return attrs;
    }

    public ISwc4jAstJsxElementName getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxOpeningElement;
    }

    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    public boolean isSelfClosing() {
        return selfClosing;
    }
}