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

package com.caoccao.javet.swc4j.ast.ts;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstTsRestType
        extends Swc4jAst
        implements ISwc4jAstTsType {
    @Jni2RustField(box = true)
    protected final ISwc4jAstTsType typeAnn;

    public Swc4jAstTsRestType(
            ISwc4jAstTsType typeAnn,
            Swc4jAstSpan span) {
        super(span);
        this.typeAnn = AssertionUtils.notNull(typeAnn, "TypeAnn");
        childNodes = SimpleList.immutableOf(typeAnn);
        updateParent();
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsRestType;
    }

    public ISwc4jAstTsType getTypeAnn() {
        return typeAnn;
    }
}