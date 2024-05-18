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
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCoercionPrimitive;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstMemberProp;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.utils.SimpleMap;

import java.util.Map;

/**
 * The type Swc4j plugin visitor jsfuck decoder.
 * It partially implements the features.
 *
 * @since 0.8.0
 */
public class Swc4jPluginVisitorJsFuckDecoder extends Swc4jAstVisitor {
    protected static final Map<String, String> ARRAY_FUNCTION_STRING_MAP = SimpleMap.immutableOf(
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
                boolean ignore = false;
                if (leftType == Swc4jAstType.ArrayLit) {
                    ignore = !left.as(Swc4jAstArrayLit.class).isAllPrimitive();
                } else if (rightType == Swc4jAstType.ArrayLit) {
                    ignore = !right.as(Swc4jAstArrayLit.class).isAllPrimitive();
                }
                if (!ignore) {
                    if ((leftType == Swc4jAstType.Bool && rightType == Swc4jAstType.Number) ||
                            (leftType == Swc4jAstType.Bool && rightType == Swc4jAstType.Bool) ||
                            (leftType == Swc4jAstType.Number && rightType == Swc4jAstType.Bool) ||
                            (leftType == Swc4jAstType.Number && rightType == Swc4jAstType.Number)) {
                        double value = left.as(ISwc4jAstCoercionPrimitive.class).asDouble()
                                + right.as(ISwc4jAstCoercionPrimitive.class).asDouble();
                        newNode = Swc4jAstNumber.create(value);
                    } else if ((leftType == Swc4jAstType.Bool && rightType == Swc4jAstType.ArrayLit) ||
                            (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.Bool) ||
                            (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.ArrayLit) ||
                            (leftType == Swc4jAstType.Str && rightType == Swc4jAstType.ArrayLit) ||
                            (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.Str) ||
                            (leftType == Swc4jAstType.Str && rightType == Swc4jAstType.Str) ||
                            (leftType == Swc4jAstType.Str && rightType == Swc4jAstType.Number) ||
                            (leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.Number) ||
                            (leftType == Swc4jAstType.Number && rightType == Swc4jAstType.Str) ||
                            (leftType == Swc4jAstType.Number && rightType == Swc4jAstType.ArrayLit)) {
                        String value = left.as(ISwc4jAstCoercionPrimitive.class).asString()
                                + right.as(ISwc4jAstCoercionPrimitive.class).asString();
                        newNode = Swc4jAstStr.create(value);
                    } else if ((leftType == Swc4jAstType.ArrayLit && rightType == Swc4jAstType.Ident) ||
                            (leftType == Swc4jAstType.Ident && rightType == Swc4jAstType.ArrayLit)) {
                        String value = left.toString() + right;
                        newNode = Swc4jAstStr.create(value);
                    } else if ((leftType.isPrimitive() || leftType == Swc4jAstType.ArrayLit)
                            && rightType == Swc4jAstType.CallExpr) {
                        Swc4jAstCallExpr callExpr = right.as(Swc4jAstCallExpr.class);
                        if (callExpr.getCallee().getType() == Swc4jAstType.MemberExpr && callExpr.getArgs().isEmpty()) {
                            Swc4jAstMemberExpr memberExpr = callExpr.getCallee().as(Swc4jAstMemberExpr.class);
                            ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                            if (obj.getType() == Swc4jAstType.ArrayLit && memberExpr.getProp().getType() == Swc4jAstType.ComputedPropName) {
                                Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                                ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                                if (expr.getType() == Swc4jAstType.Str) {
                                    Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                                    String leftString = left.as(ISwc4jAstCoercionPrimitive.class).asString();
                                    String rightString = ARRAY_FUNCTION_STRING_MAP.getOrDefault(str.getValue(), Swc4jAstIdent.UNDEFINED);
                                    newNode = Swc4jAstStr.create(leftString + rightString);
                                }
                            }
                        }
                    } else if ((rightType.isPrimitive() || rightType == Swc4jAstType.ArrayLit)
                            && leftType == Swc4jAstType.CallExpr) {
                        Swc4jAstCallExpr callExpr = left.as(Swc4jAstCallExpr.class);
                        if (callExpr.getCallee().getType() == Swc4jAstType.MemberExpr && callExpr.getArgs().isEmpty()) {
                            Swc4jAstMemberExpr memberExpr = callExpr.getCallee().as(Swc4jAstMemberExpr.class);
                            ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                            if (obj.getType() == Swc4jAstType.ArrayLit && memberExpr.getProp().getType() == Swc4jAstType.ComputedPropName) {
                                Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                                ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                                if (expr.getType() == Swc4jAstType.Str) {
                                    Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                                    String leftString = ARRAY_FUNCTION_STRING_MAP.getOrDefault(str.getValue(), Swc4jAstIdent.UNDEFINED);
                                    String rightString = right.as(ISwc4jAstCoercionPrimitive.class).asString();
                                    newNode = Swc4jAstStr.create(leftString + rightString);
                                }
                            }
                        }
                    } else if ((leftType.isPrimitive() || leftType == Swc4jAstType.ArrayLit)
                            && rightType == Swc4jAstType.MemberExpr) {
                        Swc4jAstMemberExpr memberExpr = right.as(Swc4jAstMemberExpr.class);
                        ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                        if (obj.getType() == Swc4jAstType.ArrayLit && memberExpr.getProp().getType() == Swc4jAstType.ComputedPropName) {
                            Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                            ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                            if (expr.getType() == Swc4jAstType.Str) {
                                Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                                String leftString = left.as(ISwc4jAstCoercionPrimitive.class).asString();
                                String rightString = "function " + str.getValue() + "() { [native code] }";
                                newNode = Swc4jAstStr.create(leftString + rightString);
                            }
                        }
                    } else if ((rightType.isPrimitive() || rightType == Swc4jAstType.ArrayLit)
                            && leftType == Swc4jAstType.MemberExpr) {
                        Swc4jAstMemberExpr memberExpr = left.as(Swc4jAstMemberExpr.class);
                        ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                        if (obj.getType() == Swc4jAstType.ArrayLit && memberExpr.getProp().getType() == Swc4jAstType.ComputedPropName) {
                            Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                            ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                            if (expr.getType() == Swc4jAstType.Str) {
                                Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                                String leftString = "function " + str.getValue() + "() { [native code] }";
                                String rightString = right.as(ISwc4jAstCoercionPrimitive.class).asString();
                                newNode = Swc4jAstStr.create(leftString + rightString);
                            }
                        }
                    }
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
        ISwc4jAst newNode = null;
        ISwc4jAstExpr obj = node.getObj().unParenExpr();
        ISwc4jAstMemberProp prop = node.getProp();
        switch (obj.getType()) {
            case ArrayLit:
                Swc4jAstArrayLit arrayLit = obj.as(Swc4jAstArrayLit.class);
                boolean ignore = false;
                if (prop.getType() == Swc4jAstType.ComputedPropName) {
                    Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
                    ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                    switch (expr.getType()) {
                        case BinExpr:
                        case Str:
                            ignore = true;
                            break;
                    }
                }
                if (!ignore) {
                    if (arrayLit.getElems().isEmpty()) {
                        newNode = Swc4jAstIdent.createUndefined();
                    }
                }
                break;
            case Str:
                if (prop.getType() == Swc4jAstType.ComputedPropName) {
                    Swc4jAstComputedPropName computedPropName = prop.as(Swc4jAstComputedPropName.class);
                    ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                    String value = obj.as(Swc4jAstStr.class).getValue();
                    switch (expr.getType()) {
                        case Number:
                        case Str:
                            int index = expr.as(ISwc4jAstCoercionPrimitive.class).asInt();
                            if (index >= 0 && index < value.length()) {
                                value = value.substring(index, index + 1);
                                newNode = Swc4jAstStr.create(value);
                            }
                            break;
                    }
                }
                break;
            default:
                break;
        }
        if (newNode != null) {
            ++count;
            node.getParent().replaceNode(node, newNode);
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
                    case ArrayLit: {
                        newNode = Swc4jAstNumber.create(arg.as(Swc4jAstArrayLit.class).asDouble());
                        break;
                    }
                    case Bool:
                        newNode = Swc4jAstNumber.create(arg.as(ISwc4jAstCoercionPrimitive.class).asInt());
                        break;
                    case Number: {
                        Swc4jAstNumber number = arg.as(Swc4jAstNumber.class);
                        newNode = Swc4jAstNumber.create(number.getValue(), number.getRaw().orElse(null));
                        break;
                    }
                    case Str: {
                        double value;
                        try {
                            value = Double.parseDouble(arg.as(Swc4jAstStr.class).getValue());
                        } catch (Throwable t) {
                            value = Double.NaN;
                        }
                        newNode = Swc4jAstNumber.create(value);
                        break;
                    }
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
