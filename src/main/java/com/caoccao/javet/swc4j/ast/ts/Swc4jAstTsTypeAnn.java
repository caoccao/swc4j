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

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsType;
import com.caoccao.javet.swc4j.utils.SimpleList;

/**
 * The type Swc4j ast ts type ann.
 *
 * @since 0.2.0
 */
public class Swc4jAstTsTypeAnn extends Swc4jAst {
    @Nullable
    protected final ISwc4jAstTsType typeAnn;

    /**
     * Instantiates a new Swc4j ast ts type ann.
     *
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    protected Swc4jAstTsTypeAnn(ISwc4jAstTsType typeAnn, int startPosition, int endPosition) {
        super(startPosition, endPosition);
        this.typeAnn = typeAnn;
        children = SimpleList.immutableOf(typeAnn);
        updateParent();
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TsTypeAnn;
    }

    public ISwc4jAstTsType getTypeAnn() {
        return typeAnn;
    }
}
