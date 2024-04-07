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

public class Swc4jAstTsConditionalType
        extends Swc4jAst
        implements ISwc4jAstTsType {
    @Jni2RustField(box = true)
    protected final ISwc4jAstTsType checkType;
    @Jni2RustField(box = true)
    protected final ISwc4jAstTsType extendsType;
    @Jni2RustField(box = true)
    protected final ISwc4jAstTsType falseType;
    @Jni2RustField(box = true)
    protected final ISwc4jAstTsType trueType;

    public Swc4jAstTsConditionalType(
            ISwc4jAstTsType checkType,
            ISwc4jAstTsType extendsType,
            ISwc4jAstTsType trueType,
            ISwc4jAstTsType falseType,
            Swc4jAstSpan span) {
        super(span);
        this.checkType = AssertionUtils.notNull(checkType, "CheckType");
        this.extendsType = AssertionUtils.notNull(extendsType, "ExtendsType");
        this.trueType = AssertionUtils.notNull(trueType, "TrueType");
        this.falseType = AssertionUtils.notNull(falseType, "FalseType");
        childNodes = SimpleList.immutableOf(checkType, extendsType, trueType, falseType);
        updateParent();
    }

    public ISwc4jAstTsType getCheckType() {
        return checkType;
    }

    public ISwc4jAstTsType getExtendsType() {
        return extendsType;
    }

    public ISwc4jAstTsType getFalseType() {
        return falseType;
    }

    public ISwc4jAstTsType getTrueType() {
        return trueType;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsConditionalType;
    }
}
