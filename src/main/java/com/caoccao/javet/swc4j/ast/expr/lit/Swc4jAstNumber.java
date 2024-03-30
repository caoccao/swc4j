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

package com.caoccao.javet.swc4j.ast.expr.lit;

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;

public class Swc4jAstNumber
        extends Swc4jAst
        implements ISwc4jAstLit {
    @Nullable
    protected final String raw;
    protected final double value;

    public Swc4jAstNumber(double value, String raw, int startPosition, int endPosition) {
        super(startPosition, endPosition);
        this.value = value;
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Number;
    }

    public double getValue() {
        return value;
    }

    public float getValueAsFloat() {
        return ((Double) value).floatValue();
    }

    public int getValueAsInt() {
        return ((Double) value).intValue();
    }

    public long getValueAsLong() {
        return ((Double) value).longValue();
    }
}
