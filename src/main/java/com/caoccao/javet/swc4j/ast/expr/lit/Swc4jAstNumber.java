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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.constants.ISwc4jConstants;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstNumber
        extends Swc4jAst
        implements ISwc4jAstLit, ISwc4jAstPropName, ISwc4jAstTsLit, ISwc4jAstCoercionPrimitive {
    protected static final int MAX_EXPONENT = 308;
    protected static final Pattern PATTERN_DECIMAL_ZEROS =
            Pattern.compile("^([\\+\\-]?)(\\d+)\\.0*$", Pattern.CASE_INSENSITIVE);
    protected static final Pattern PATTERN_SCIENTIFIC_NOTATION_WITHOUT_FRACTION =
            Pattern.compile("^([\\+\\-]?)(\\d+)e([\\+\\-]?)(\\d+)$", Pattern.CASE_INSENSITIVE);
    protected static final Pattern PATTERN_SCIENTIFIC_NOTATION_WITH_FRACTION =
            Pattern.compile("^([\\+\\-]?)(\\d+)\\.(\\d*)e([\\+\\-]?)(\\d+)$", Pattern.CASE_INSENSITIVE);
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

    public static Swc4jAstNumber create(int value) {
        return create(value, Integer.toString(value));
    }

    public static Swc4jAstNumber create(double value) {
        return create(value, null);
    }

    public static Swc4jAstNumber create(double value, String raw) {
        return new Swc4jAstNumber(value, raw, Swc4jSpan.DUMMY);
    }

    public static Swc4jAstNumber createInfinity(boolean positive) {
        return create(positive ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY, null);
    }

    public static Swc4jAstNumber createNaN() {
        return create(Double.NaN, null);
    }

    protected static String normalize(String raw) {
        Matcher matcher = PATTERN_SCIENTIFIC_NOTATION_WITH_FRACTION.matcher(raw);
        if (matcher.matches()) {
            String sign = "-".equals(matcher.group(1)) ? "-" : "";
            String exponentSign = StringUtils.isEmpty(matcher.group(4)) ? "+" : matcher.group(4);
            String integer = matcher.group(2);
            String fraction = matcher.group(3);
            int additionalExponent = 0;
            while (fraction.endsWith("0")) {
                fraction = fraction.substring(0, fraction.length() - 1);
            }
            if (integer.length() > 1) {
                additionalExponent += integer.length() - 1;
                fraction = integer.substring(1) + fraction;
                integer = integer.substring(0, 1);
            }
            if (StringUtils.isNotEmpty(fraction)) {
                fraction = "." + fraction;
            }
            long exponent = Long.parseLong(matcher.group(5)) + additionalExponent;
            if (exponent > MAX_EXPONENT) {
                return sign + ISwc4jConstants.INFINITY;
            }
            return sign + integer + fraction + "e" + exponentSign + exponent;
        }
        matcher = PATTERN_SCIENTIFIC_NOTATION_WITHOUT_FRACTION.matcher(raw);
        if (matcher.matches()) {
            String sign = "-".equals(matcher.group(1)) ? "-" : "";
            String exponentSign = StringUtils.isEmpty(matcher.group(3)) ? "+" : matcher.group(3);
            String integer = matcher.group(2);
            String fraction = "";
            int additionalExponent = 0;
            while (integer.endsWith("0")) {
                ++additionalExponent;
                integer = integer.substring(0, integer.length() - 1);
            }
            if (integer.length() > 1) {
                additionalExponent += integer.length() - 1;
                fraction = "." + integer.substring(1);
                integer = integer.substring(0, 1);
            }
            long exponent = Long.parseLong(matcher.group(4)) + additionalExponent;
            if (exponent > MAX_EXPONENT) {
                return sign + ISwc4jConstants.INFINITY;
            }
            return sign + integer + fraction + "e" + exponentSign + exponent;
        }
        matcher = PATTERN_DECIMAL_ZEROS.matcher(raw);
        if (matcher.matches()) {
            String sign = "-".equals(matcher.group(1)) ? "-" : "";
            return sign + matcher.group(2);
        }
        return raw;
    }

    @Override
    public boolean asBoolean() {
        return value != 0;
    }

    @Override
    public byte asByte() {
        return ((Double) value).byteValue();
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public float asFloat() {
        return ((Double) value).floatValue();
    }

    @Override
    public int asInt() {
        return ((Double) value).intValue();
    }

    @Override
    public long asLong() {
        return ((Double) value).longValue();
    }

    @Override
    public short asShort() {
        return ((Double) value).shortValue();
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return EMPTY_CHILD_NODES;
    }

    protected int getMinusCount(ISwc4jAst ast) {
        switch (ast.getType()) {
            case ParenExpr:
                return getMinusCount(ast.getParent());
            case UnaryExpr:
                if (ast.as(Swc4jAstUnaryExpr.class).getOp() == Swc4jAstUnaryOp.Minus) {
                    return getMinusCount(ast.getParent()) + 1;
                }
                return 0;
            default:
                return 0;
        }
    }

    public int getMinusCount() {
        return getMinusCount(getParent());
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

    @Override
    public boolean isInfinity() {
        return Double.isInfinite(value);
    }

    @Override
    public boolean isNaN() {
        return Double.isNaN(value);
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
        return normalize(raw.orElse(Double.toString(value)));
    }

    public String toString(int radix) {
        return Integer.toString(asInt(), radix);
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
