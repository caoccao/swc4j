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

import com.caoccao.javet.swc4j.annotations.Nullable;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.Swc4jAstSpan;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBigIntSign;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.math.BigInteger;

public class Swc4jAstBigInt
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstPropName {
    @Nullable
    protected final String raw;
    protected final Swc4jAstBigIntSign sign;
    protected final BigInteger value;

    public Swc4jAstBigInt(
            Swc4jAstBigIntSign sign,
            String raw,
            Swc4jAstSpan span) {
        super(span);
        this.sign = AssertionUtils.notNull(sign, "Sign");
        this.raw = raw;
        this.value = StringUtils.isEmpty(raw) ? BigInteger.ZERO : new BigInteger(raw.substring(0, raw.length() - 1));
    }

    public String getRaw() {
        return raw;
    }

    public Swc4jAstBigIntSign getSign() {
        return sign;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BigInt;
    }

    public BigInteger getValue() {
        return value;
    }
}
