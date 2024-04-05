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
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstTsSatisfiesExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr expr;
    @Jni2RustField(box = true, value = "Box::new(TsType::TsThisType(TsThisType { span: DUMMY_SP }))")
    protected final ISwc4jAstTsType typeAnn;

    public Swc4jAstTsSatisfiesExpr(
            ISwc4jAstExpr expr,
            ISwc4jAstTsType typeAnn,
            Swc4jAstSpan span) {
        super(span);
        this.expr = AssertionUtils.notNull(expr, "Expr");
        this.typeAnn = AssertionUtils.notNull(typeAnn, "TypeAnn");
        childNodes = SimpleList.immutableOf(expr, typeAnn);
        updateParent();
    }

    public ISwc4jAstExpr getExpr() {
        return expr;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsSatisfiesExpr;
    }

    public ISwc4jAstTsType getTypeAnn() {
        return typeAnn;
    }
}