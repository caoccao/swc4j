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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstIdent;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsEntityName;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

@Jni2RustClass(span = false)
public class Swc4jAstTsQualifiedName
        extends Swc4jAst
        implements ISwc4jAstTsEntityName {
    protected final ISwc4jAstTsEntityName left;
    protected final Swc4jAstIdent right;

    public Swc4jAstTsQualifiedName(
            ISwc4jAstTsEntityName left,
            Swc4jAstIdent right,
            Swc4jAstSpan span) {
        super(span);
        this.left = AssertionUtils.notNull(left, "Left");
        this.right = AssertionUtils.notNull(right, "Right");
        childNodes = SimpleList.immutableOf(left, right);
        updateParent();
    }

    public ISwc4jAstTsEntityName getLeft() {
        return left;
    }

    public Swc4jAstIdent getRight() {
        return right;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsQualifiedName;
    }
}
