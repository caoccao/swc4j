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
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCoercionPrimitive;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstLit;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;

import java.util.List;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstRegex
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstCoercionPrimitive {
    @Jni2RustField(atom = true)
    protected String exp;
    @Jni2RustField(atom = true)
    protected String flags;

    @Jni2RustMethod
    public Swc4jAstRegex(
            String exp,
            String flags,
            Swc4jSpan span) {
        super(span);
        setExp(exp);
        setFlags(flags);
    }

    public static Swc4jAstRegex create() {
        return create("(?:)");
    }

    public static Swc4jAstRegex create(String exp) {
        return create(exp, "");
    }

    public static Swc4jAstRegex create(String exp, String flags) {
        return new Swc4jAstRegex(exp, flags, Swc4jSpan.DUMMY);
    }

    public static String escape(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    @Override
    public byte asByte() {
        return 0;
    }

    @Override
    public double asDouble() {
        return Double.NaN;
    }

    @Override
    public float asFloat() {
        return Float.NaN;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public short asShort() {
        return 0;
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    @Jni2RustMethod
    public String getExp() {
        return exp;
    }

    @Jni2RustMethod
    public String getFlags() {
        return flags;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.Regex;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    public Swc4jAstRegex setExp(String exp) {
        this.exp = AssertionUtils.notNull(exp, "Exp");
        return this;
    }

    public Swc4jAstRegex setFlags(String flags) {
        this.flags = AssertionUtils.notNull(flags, "Flags");
        return this;
    }

    @Override
    public String toString() {
        return "/" + exp + "/" + flags;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitRegex(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
