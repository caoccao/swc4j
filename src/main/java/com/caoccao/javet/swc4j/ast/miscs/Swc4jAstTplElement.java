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
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.Optional;

public class Swc4jAstTplElement
        extends Swc4jAst {
    protected final Optional<String> cooked;
    protected final String raw;
    protected final boolean tail;

    public Swc4jAstTplElement(
            boolean tail,
            String cooked,
            String raw,
            Swc4jAstSpan span) {
        super(span);
        this.cooked = Optional.ofNullable(cooked);
        this.raw = AssertionUtils.notNull(raw, "Raw");
        this.tail = tail;
        children = SimpleList.immutableOf();
        updateParent();
    }

    public Optional<String> getCooked() {
        return cooked;
    }

    public String getRaw() {
        return raw;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.TplElement;
    }

    public boolean isTail() {
        return tail;
    }
}