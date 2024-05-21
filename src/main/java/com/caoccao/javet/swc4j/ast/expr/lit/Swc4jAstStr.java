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
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstStr
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstModuleExportName, ISwc4jAstPropName, ISwc4jAstTsModuleName, ISwc4jAstTsLit,
        ISwc4jAstTsEnumMemberId, ISwc4jAstCoercionPrimitive {
    public static final String CONSTRUCTOR = "String";
    public static final String FONTCOLOR = "fontcolor";
    public static final String ITALICS = "italics";
    public static final String SLICE = "slice";
    public static final String SPLIT = "split";
    @Jni2RustField(componentAtom = true)
    protected Optional<String> raw;
    @Jni2RustField(atom = true)
    protected String value;

    @Jni2RustMethod
    public Swc4jAstStr(
            String value,
            @Jni2RustParam(optional = true) String raw,
            Swc4jSpan span) {
        super(span);
        setRaw(raw);
        setValue(value);
    }

    public static Swc4jAstStr create(String value) {
        return new Swc4jAstStr(value, "\"" + value + "\"", Swc4jSpan.DUMMY);
    }

    @Override
    public boolean asBoolean() {
        return StringUtils.isNotEmpty(value);
    }

    @Override
    public byte asByte() {
        return Double.valueOf(asDouble()).byteValue();
    }

    @Override
    public double asDouble() {
        try {
            return Double.parseDouble(value);
        } catch (Throwable t) {
            return Double.NaN;
        }
    }

    @Override
    public float asFloat() {
        return Double.valueOf(asDouble()).floatValue();
    }

    @Override
    public int asInt() {
        return Double.valueOf(asDouble()).intValue();
    }

    @Override
    public long asLong() {
        return Double.valueOf(asDouble()).longValue();
    }

    @Override
    public short asShort() {
        return Double.valueOf(asDouble()).shortValue();
    }

    @Override
    public String asString() {
        return toString();
    }

    public String fontcolor(String arg) {
        String escapeArg = arg.replace("\"", "&quot;");
        return "<font color=\"" + escapeArg + "\">" + value + "</font>";
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
        return Swc4jAstType.Str;
    }

    @Jni2RustMethod
    public String getValue() {
        return value;
    }

    public String italics() {
        return "<i>" + value + "</i>";
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        return false;
    }

    public Swc4jAstStr setRaw(String raw) {
        this.raw = Optional.ofNullable(raw);
        return this;
    }

    public Swc4jAstStr setValue(String value) {
        this.value = AssertionUtils.notNull(value, "Value");
        return this;
    }

    public String slice() {
        return slice(0);
    }

    public String slice(int indexStart) {
        return slice(indexStart, value.length());
    }

    public String slice(int indexStart, int indexEnd) {
        return StringUtils.slice(value, indexStart, indexEnd);
    }

    public List<String> split() {
        return split(null, 0);
    }

    public List<String> split(String separator) {
        return split(separator, 0);
    }

    public List<String> split(String separator, int limit) {
        return StringUtils.split(value, separator, limit);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitStr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
