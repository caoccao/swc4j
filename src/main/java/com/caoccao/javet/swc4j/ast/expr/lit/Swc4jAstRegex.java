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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

public class Swc4jAstRegex
        extends Swc4jAst
        implements ISwc4jAstLit {
    protected final String exp;
    protected final String flags;

    public Swc4jAstRegex(
            String exp,
            String flags,
            Swc4jAstSpan span) {
        super(span);
        this.flags = AssertionUtils.notNull(flags, "Flags");
        this.exp = AssertionUtils.notNull(exp, "Exp");
    }

    public String getExp() {
        return exp;
    }

    public String getFlags() {
        return flags;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Regex;
    }
}
