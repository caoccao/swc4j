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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBigIntSign;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsLit;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;
import com.caoccao.javet.swc4j.utils.Swc4jAstSpan;

import java.math.BigInteger;
import java.util.Optional;

public class Swc4jAstBigInt
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstPropName, ISwc4jAstTsLit {
    protected final Optional<String> raw;
    @Jni2RustField(ignore = true)
    protected final Swc4jAstBigIntSign sign;
    protected final BigInteger value;

    public Swc4jAstBigInt(
            Swc4jAstBigIntSign sign,
            String raw,
            Swc4jAstSpan span) {
        super(span);
        this.sign = AssertionUtils.notNull(sign, "Sign");
        this.raw = Optional.ofNullable(raw);
        this.value = StringUtils.isEmpty(raw) ? BigInteger.ZERO : new BigInteger(raw.substring(0, raw.length() - 1));
    }

    public Optional<String> getRaw() {
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

    @Override
    public String toString() {
        return raw.orElse(null);
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitBigInt(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
