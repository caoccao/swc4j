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

package com.caoccao.javet.swc4j.plugins.jsfuck;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstMemberExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstUnaryExpr;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCoercionPrimitive;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstMemberProp;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Swc4jPluginVisitorJsFuckDecoder extends Swc4jAstVisitor {
    protected static final Pattern PATTERN_SCIENTIFIC_NOTATION =
            Pattern.compile("^(\\d+)e(\\d+)$", Pattern.CASE_INSENSITIVE);
    protected int count;

    public Swc4jPluginVisitorJsFuckDecoder() {
        count = 0;
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }

    @Override
    public Swc4jAstVisitorResponse visitBinExpr(Swc4jAstBinExpr node) {
        ISwc4jAst newNode = null;
        ISwc4jAstExpr left = node.getLeft().unParenExpr();
        ISwc4jAstExpr right = node.getRight().unParenExpr();
        Swc4jAstType leftType = left.getType();
        Swc4jAstType rightType = right.getType();
        switch (node.getOp()) {
            case Add:
                if ((leftType == Swc4jAstType.Bool || leftType == Swc4jAstType.Number) &&
                        (rightType == Swc4jAstType.Bool || rightType == Swc4jAstType.Number)) {
                    int value = left.as(ISwc4jAstCoercionPrimitive.class).asInt() + right.as(ISwc4jAstCoercionPrimitive.class).asInt();
                    newNode = Swc4jAstNumber.create(value);
                } else if ((leftType == Swc4jAstType.Bool && rightType == Swc4jAstType.ArrayLit) ||
                        (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.Bool) ||
                        (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.ArrayLit) ||
                        (leftType == Swc4jAstType.Str && rightType == Swc4jAstType.ArrayLit) ||
                        (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.Str) ||
                        (leftType == Swc4jAstType.Number && rightType == Swc4jAstType.ArrayLit) ||
                        (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.Number) ||
                        (leftType == Swc4jAstType.Str && rightType == Swc4jAstType.Str) ||
                        (leftType == Swc4jAstType.Number && rightType == Swc4jAstType.Str) ||
                        (leftType == Swc4jAstType.Str && rightType == Swc4jAstType.Number)) {
                    String value = left.as(ISwc4jAstCoercionPrimitive.class).asString() + right.as(ISwc4jAstCoercionPrimitive.class).asString();
                    newNode = Swc4jAstStr.create(value);
                }
                break;
            default:
                break;
        }
        if (newNode != null) {
            ++count;
            node.getParent().replaceNode(node, newNode);
        }
        return super.visitBinExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitMemberExpr(Swc4jAstMemberExpr node) {
        ISwc4jAstExpr obj = node.getObj().unParenExpr();
        ISwc4jAstMemberProp prop = node.getProp();
        if (obj.getType() == Swc4jAstType.Str) {
            if (prop.getType() == Swc4jAstType.ComputedPropName) {
                Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
                ISwc4jAstExpr expr = computedPropName.getExpr();
                if (expr.getType() == Swc4jAstType.Number) {
                    String value = obj.as(Swc4jAstStr.class).getValue();
                    int index = expr.as(Swc4jAstNumber.class).asInt();
                    if (index >= 0 && index < value.length()) {
                        value = value.substring(index, index + 1);
                        ++count;
                        node.getParent().replaceNode(node, Swc4jAstStr.create(value));
                    }
                }
            }
        }
        return super.visitMemberExpr(node);
    }

    @Override
    public Swc4jAstVisitorResponse visitUnaryExpr(Swc4jAstUnaryExpr node) {
        ISwc4jAst newNode = null;
        ISwc4jAstExpr arg = node.getArg().unParenExpr();
        switch (node.getOp()) {
            case Bang:
                switch (arg.getType()) {
                    case ArrayLit:
                        newNode = Swc4jAstBool.create(false);
                        break;
                    case Bool:
                    case Number:
                        newNode = Swc4jAstBool.create(!arg.as(ISwc4jAstCoercionPrimitive.class).asBoolean());
                        break;
                    default:
                        break;
                }
                break;
            case Plus:
                switch (arg.getType()) {
                    case ArrayLit:
                    case Bool:
                        newNode = Swc4jAstNumber.create(arg.as(ISwc4jAstCoercionPrimitive.class).asInt());
                        break;
                    case Number: {
                        Swc4jAstNumber number = arg.as(Swc4jAstNumber.class);
                        newNode = Swc4jAstNumber.create(number.getValue(), number.getRaw().orElse(null));
                    }
                    break;
                    case Str: {
                        Swc4jAstStr str = arg.as(Swc4jAstStr.class);
                        try {
                            double value = Double.parseDouble(str.getValue());
                            String raw = str.getValue();
                            Matcher matcher = PATTERN_SCIENTIFIC_NOTATION.matcher(raw);
                            if (matcher.matches()) {
                                raw = matcher.group(1) + "e+" + matcher.group(2);
                            }
                            newNode = Swc4jAstNumber.create(value, raw);
                        } catch (Throwable t) {
                            newNode = Swc4jAstNumber.create(Double.NaN);
                        }
                    }
                    break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        if (newNode != null) {
            ++count;
            node.getParent().replaceNode(node, newNode);
        }
        return super.visitUnaryExpr(node);
    }
}
