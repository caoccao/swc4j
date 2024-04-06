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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstJsxAttrValue;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

@Jni2RustClass(name = "JSXAttr")
public class Swc4jAstJsxAttr
        extends Swc4jAst
        implements ISwc4jAstJsxAttrOrSpread {
    @Jni2RustField(value = "JSXAttrName::Ident(Ident::dummy())")
    protected final ISwc4jAstJsxAttrName name;
    protected final Optional<ISwc4jAstJsxAttrValue> value;

    public Swc4jAstJsxAttr(
            ISwc4jAstJsxAttrName name,
            ISwc4jAstJsxAttrValue value,
            Swc4jAstSpan span) {
        super(span);
        this.name = AssertionUtils.notNull(name, "Name");
        this.value = Optional.ofNullable(value);
        childNodes = SimpleList.immutableOf(name, value);
        updateParent();
    }

    public ISwc4jAstJsxAttrName getName() {
        return name;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.JsxAttr;
    }

    public Optional<ISwc4jAstJsxAttrValue> getValue() {
        return value;
    }
}
