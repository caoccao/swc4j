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
import com.caoccao.javet.swc4j.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;

/**
 * The type Swc4j ast ts type ann.
 *
 * @since 0.2.0
 */
public class Swc4jAstTsTypeAnn extends Swc4jAst {
    // TODO

    /**
     * Instantiates a new Swc4j ast ts type ann.
     *
     * @param type          the type
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.2.0
     */
    protected Swc4jAstTsTypeAnn(Swc4jAstType type, int startPosition, int endPosition) {
        super(type, startPosition, endPosition);
    }

    @Override
    public List<Swc4jAst> getChildren() {
        return SimpleList.of();
    }
}
