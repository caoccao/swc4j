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
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCoercionPrimitive;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.*;

import java.util.*;
import java.util.stream.Collectors;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstArrayLit
        extends Swc4jAst
        implements ISwc4jAstExpr, ISwc4jAstCoercionPrimitive {
    public static final Set<String> ARRAY_FUNCTION_SET = SimpleSet.immutableOf(
            "at",
            "concat",
            "copyWithin",
            "entries",
            "every",
            "fill",
            "filter",
            "find",
            "findIndex",
            "findLast",
            "findLastIndex",
            "flat",
            "flatMap",
            "forEach",
            "includes",
            "indexOf",
            "join",
            "keys",
            "lastIndexOf",
            "map",
            "pop",
            "push",
            "reduce",
            "reduceRight",
            "reverse",
            "shift",
            "slice",
            "some",
            "sort",
            "splice",
            "toLocaleString",
            "toReversed",
            "toSorted",
            "toSpliced",
            "toString",
            "unshift",
            "values",
            "with");
    public static final Map<String, String> ARRAY_FUNCTION_STRING_MAP = SimpleMap.immutableOf(
            "concat", "",
            "copyWithin", "",
            "entries", "[object Array Iterator]",
            "fill", "",
            "flat", "",
            "indexOf", "-1",
            "includes", "false",
            "join", "",
            "keys", "[object Array Iterator]",
            "lastIndexOf", "-1",
            "push", "0",
            "reverse", "",
            "slice", "",
            "sort", "",
            "splice", "",
            "toReversed", "",
            "toSorted", "",
            "toSpliced", "",
            "toString", "",
            "unshift", "0",
            "values", "[object Array Iterator]");
    public static final String CONCAT = "concat";
    public static final String CONSTRUCTOR = "Array";
    public static final String JOIN = "join";
    protected final List<Optional<Swc4jAstExprOrSpread>> elems;

    @Jni2RustMethod
    public Swc4jAstArrayLit(
            List<Swc4jAstExprOrSpread> elems,
            Swc4jSpan span) {
        super(span);
        this.elems = AssertionUtils.notNull(elems, "Elems").stream()
                .map(Optional::ofNullable)
                .collect(Collectors.toList());
        this.elems.stream().filter(Optional::isPresent).map(Optional::get).forEach(node -> node.setParent(this));
    }

    public static Swc4jAstArrayLit create() {
        return new Swc4jAstArrayLit(SimpleList.of(), Swc4jSpan.DUMMY);
    }

    public static Swc4jAstArrayLit create(List<String> list) {
        List<Swc4jAstExprOrSpread> elems = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            list.forEach(str -> elems.add(str == null ? null : Swc4jAstExprOrSpread.create(Swc4jAstStr.create(str))));
        }
        return new Swc4jAstArrayLit(elems, Swc4jSpan.DUMMY);
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    @Override
    public byte asByte() {
        return Double.valueOf(asDouble()).byteValue();
    }

    @Override
    public double asDouble() {
        switch (elems.size()) {
            case 0:
                return 0;
            case 1:
                return elems.get(0)
                        .map(Swc4jAstExprOrSpread::getExpr)
                        .filter(n -> n instanceof Swc4jAstNumber)
                        .map(n -> (Swc4jAstNumber) n)
                        .map(Swc4jAstNumber::getValue)
                        .orElse(Double.NaN);
            default:
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

    public void concat(Swc4jAstArrayLit... arrayLits) {
        if (ArrayUtils.isNotEmpty(AssertionUtils.notNull(arrayLits, "Array lits"))) {
            for (Swc4jAstArrayLit arrayLit : arrayLits) {
                AssertionUtils.notNull(arrayLit, "Array lit").getElems().forEach(elem -> {
                    elem.ifPresent(e -> e.setParent(this));
                    elems.add(elem);
                });
            }
        }
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.of();
        elems.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(childNodes::add);
        return childNodes;
    }

    @Jni2RustMethod
    public List<Optional<Swc4jAstExprOrSpread>> getElems() {
        return elems;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.ArrayLit;
    }

    public int indexOf(Swc4jAstExprOrSpread node) {
        if (!elems.isEmpty()) {
            final int length = elems.size();
            for (int i = 0; i < length; i++) {
                if (elems.get(i).map(elem -> elem == node).orElse(false)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean isAllPrimitive() {
        return elems.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Swc4jAstExprOrSpread::getExpr)
                .map(ISwc4jAstExpr::unParenExpr)
                .allMatch(elem -> {
                    if (elem.getType().isPrimitive() || elem.isUndefined() || elem.isInfinity() || elem.isNaN()) {
                        return true;
                    }
                    if (elem instanceof Swc4jAstArrayLit) {
                        return elem.as(Swc4jAstArrayLit.class).isAllPrimitive();
                    }
                    return false;
                });
    }

    public boolean isSpreadPresent() {
        return elems.stream().anyMatch(elem -> elem.map(e -> e.getSpread().isPresent()).orElse(false));
    }

    public String join(String separator) {
        if (separator == null) {
            separator = ",";
        }
        return elems.stream()
                .map(elem -> elem
                        .map(e -> e.getExpr().unParenExpr())
                        .map(e -> e.as(ISwc4jAstCoercionPrimitive.class))
                        .map(ISwc4jAstCoercionPrimitive::toString)
                        .orElse(""))
                .collect(Collectors.joining(separator));
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!elems.isEmpty() && (newNode == null || newNode instanceof Swc4jAstExprOrSpread)) {
            final int size = elems.size();
            for (int i = 0; i < size; i++) {
                Optional<Swc4jAstExprOrSpread> optionalOldElem = elems.get(i);
                if (optionalOldElem.isPresent() && optionalOldElem.get() == oldNode) {
                    Optional<Swc4jAstExprOrSpread> optionalNewElem = Optional.ofNullable((Swc4jAstExprOrSpread) newNode);
                    optionalNewElem.ifPresent(node -> node.setParent(this));
                    elems.set(i, optionalNewElem);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return elems.stream()
                .map(optionalElem -> optionalElem
                        .map(elem -> {
                            ISwc4jAstExpr expr = elem.as(Swc4jAstExprOrSpread.class).getExpr().unParenExpr();
                            if (expr.isUndefined()) {
                                return "";
                            } else {
                                return expr.toString();
                            }
                        })
                        .orElse(""))
                .collect(Collectors.joining(","));
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitArrayLit(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
