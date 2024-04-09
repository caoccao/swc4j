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

package com.caoccao.javet.swc4j.ast.pat;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPat;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsParamPropParam;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

public class Swc4jAstAssignPat
        extends Swc4jAst
        implements ISwc4jAstPat, ISwc4jAstTsParamPropParam {
    @Jni2RustField(box = true)
    protected final ISwc4jAstPat left;
    @Jni2RustField(box = true)
    protected final ISwc4jAstExpr right;

    public Swc4jAstAssignPat(
            ISwc4jAstPat left,
            ISwc4jAstExpr right,
            Swc4jAstSpan span) {
        super(span);
        this.left = AssertionUtils.notNull(left, "Left");
        this.right = AssertionUtils.notNull(right, "Right");
        childNodes = SimpleList.immutableOf(left, right);
        updateParent();
    }

    public ISwc4jAstPat getLeft() {
        return left;
    }

    public ISwc4jAstExpr getRight() {
        return right;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.AssignPat;
    }
}
