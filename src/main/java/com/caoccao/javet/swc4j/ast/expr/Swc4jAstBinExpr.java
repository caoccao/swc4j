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

package com.caoccao.javet.swc4j.ast.expr;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCoercionPrimitive;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustClass;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustField;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustFilePath;
import com.caoccao.javet.swc4j.jni2rust.Jni2RustMethod;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstBinExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr left;
    protected Swc4jAstBinaryOp op;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr right;

    @Jni2RustMethod
    public Swc4jAstBinExpr(
            Swc4jAstBinaryOp op,
            ISwc4jAstExpr left,
            ISwc4jAstExpr right,
            Swc4jSpan span) {
        super(span);
        setLeft(left);
        setOp(op);
        setRight(right);
    }

    @Override
    public Optional<ISwc4jAst> eval() {
        ISwc4jAstExpr left = this.left.unParenExpr();
        ISwc4jAstExpr right = this.right.unParenExpr();
        Swc4jAstType leftType = left.getType();
        Swc4jAstType rightType = right.getType();
        switch (op) {
            case Add:
                if (leftType.isArrayLit() && !left.as(Swc4jAstArrayLit.class).isAllPrimitive()) {
                    return super.eval();
                }
                if (rightType.isArrayLit() && !right.as(Swc4jAstArrayLit.class).isAllPrimitive()) {
                    return super.eval();
                }
                if ((leftType.isBool() && rightType.isNumber()) ||
                        (leftType.isBool() && rightType.isBool()) ||
                        (leftType.isNumber() && rightType.isBool()) ||
                        (leftType.isNumber() && rightType.isNumber())) {
                    double value = left.as(ISwc4jAstCoercionPrimitive.class).asDouble()
                            + right.as(ISwc4jAstCoercionPrimitive.class).asDouble();
                    return Optional.of(Swc4jAstNumber.create(value));
                } else if ((leftType.isBool() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isBool()) ||
                        (leftType.isArrayLit() && rightType.isArrayLit()) ||
                        (leftType.isStr() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isStr()) ||
                        (leftType.isStr() && rightType.isStr()) ||
                        (leftType.isStr() && rightType.isNumber()) ||
                        (leftType.isStr() && rightType.isBool()) ||
                        (leftType.isBool() && rightType.isStr()) ||
                        (leftType.isArrayLit() && rightType.isNumber()) ||
                        (leftType.isNumber() && rightType.isStr()) ||
                        (leftType.isNumber() && rightType.isArrayLit())) {
                    String value = left.as(ISwc4jAstCoercionPrimitive.class).asString()
                            + right.as(ISwc4jAstCoercionPrimitive.class).asString();
                    return Optional.of(Swc4jAstStr.create(value));
                } else if ((leftType.isArrayLit() && rightType.isIdent()) ||
                        (leftType.isIdent() && rightType.isArrayLit())) {
                    String value = left.toString() + right;
                    return Optional.of(Swc4jAstStr.create(value));
                } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isCallExpr()) {
                    Swc4jAstCallExpr callExpr = right.as(Swc4jAstCallExpr.class);
                    if (callExpr.getCallee() instanceof Swc4jAstMemberExpr && callExpr.getArgs().isEmpty()) {
                        Swc4jAstMemberExpr memberExpr = callExpr.getCallee().as(Swc4jAstMemberExpr.class);
                        ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                        if (obj instanceof Swc4jAstArrayLit && memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
                            Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                            ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                            if (expr instanceof Swc4jAstStr) {
                                Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                                String leftString = left.as(ISwc4jAstCoercionPrimitive.class).asString();
                                String rightString = Swc4jAstArrayLit.ARRAY_FUNCTION_STRING_MAP.getOrDefault(str.getValue(), Swc4jAstIdent.UNDEFINED);
                                return Optional.of(Swc4jAstStr.create(leftString + rightString));
                            }
                        }
                    }
                } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isCallExpr()) {
                    Swc4jAstCallExpr callExpr = left.as(Swc4jAstCallExpr.class);
                    if (callExpr.getCallee() instanceof Swc4jAstMemberExpr && callExpr.getArgs().isEmpty()) {
                        Swc4jAstMemberExpr memberExpr = callExpr.getCallee().as(Swc4jAstMemberExpr.class);
                        ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                        if (obj instanceof Swc4jAstArrayLit && memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
                            Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                            ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                            if (expr instanceof Swc4jAstStr) {
                                Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                                String leftString = Swc4jAstArrayLit.ARRAY_FUNCTION_STRING_MAP.getOrDefault(str.getValue(), Swc4jAstIdent.UNDEFINED);
                                String rightString = right.as(ISwc4jAstCoercionPrimitive.class).asString();
                                return Optional.of(Swc4jAstStr.create(leftString + rightString));
                            }
                        }
                    }
                } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isMemberExpr()) {
                    Swc4jAstMemberExpr memberExpr = right.as(Swc4jAstMemberExpr.class);
                    ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                    if (obj instanceof Swc4jAstArrayLit && memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
                        Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                        ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                        if (expr instanceof Swc4jAstStr) {
                            Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                            String leftString = left.as(ISwc4jAstCoercionPrimitive.class).asString();
                            String rightString = "function " + str.getValue() + "() { [native code] }";
                            return Optional.of(Swc4jAstStr.create(leftString + rightString));
                        }
                    }
                } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isMemberExpr()) {
                    Swc4jAstMemberExpr memberExpr = left.as(Swc4jAstMemberExpr.class);
                    ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                    if (obj instanceof Swc4jAstArrayLit && memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
                        Swc4jAstComputedPropName computedPropName = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                        ISwc4jAstExpr expr = computedPropName.getExpr().unParenExpr();
                        if (expr instanceof Swc4jAstStr) {
                            Swc4jAstStr str = expr.as(Swc4jAstStr.class);
                            String leftString = "function " + str.getValue() + "() { [native code] }";
                            String rightString = right.as(ISwc4jAstCoercionPrimitive.class).asString();
                            return Optional.of(Swc4jAstStr.create(leftString + rightString));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.eval();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(left, right);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getLeft() {
        return left;
    }

    @Jni2RustMethod
    public Swc4jAstBinaryOp getOp() {
        return op;
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getRight() {
        return right;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.BinExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (left == oldNode && newNode instanceof ISwc4jAstExpr) {
            setLeft((ISwc4jAstExpr) newNode);
            return true;
        }
        if (right == oldNode && newNode instanceof ISwc4jAstExpr) {
            setRight((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstBinExpr setLeft(ISwc4jAstExpr left) {
        this.left = AssertionUtils.notNull(left, "Left");
        this.left.setParent(this);
        return this;
    }

    public Swc4jAstBinExpr setOp(Swc4jAstBinaryOp op) {
        this.op = AssertionUtils.notNull(op, "Op");
        return this;
    }

    public Swc4jAstBinExpr setRight(ISwc4jAstExpr right) {
        this.right = AssertionUtils.notNull(right, "Right");
        this.right.setParent(this);
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitBinExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
