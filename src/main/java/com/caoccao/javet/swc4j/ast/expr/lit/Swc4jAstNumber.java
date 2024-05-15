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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstPropName;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstTsLit;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstNumber
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstPropName, ISwc4jAstTsLit {
    @Jni2RustField(componentAtom = true)
    protected Optional<String> raw;
    protected double value;

    @Jni2RustMethod
    public Swc4jAstNumber(
            double value,
            @Jni2RustParam(optional = true) String raw,
            Swc4jSpan span) {
        super(span);
        setRaw(raw);
        setValue(value);
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    @Jni2RustMethod
    public Optional<String> getRaw() {
        return raw;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Number;
    }

    @Jni2RustMethod
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

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    public Swc4jAstNumber setRaw(String raw) {
        this.raw = Optional.ofNullable(raw);
        return this;
    }

    public Swc4jAstNumber setValue(double value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return raw.orElse(null);
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitNumber(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
