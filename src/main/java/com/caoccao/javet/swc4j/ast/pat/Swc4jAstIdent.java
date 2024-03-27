/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.enums.Swc4jAstType;

public class Swc4jAstIdent extends Swc4jAst {
    protected final boolean optional;
    protected final String sym;

    public Swc4jAstIdent(String sym, boolean optional, int startPosition, int endPosition) {
        super(Swc4jAstType.Ident, startPosition, endPosition);
        this.optional = optional;
        this.sym = sym;
    }

    public String getSym() {
        return sym;
    }

    public boolean isOptional() {
        return optional;
    }
}
