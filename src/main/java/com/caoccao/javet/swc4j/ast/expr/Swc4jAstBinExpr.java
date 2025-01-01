/*
 * Copyright (c) 2024-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstBinaryOp;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCoercionPrimitive;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.constants.ISwc4jConstants;
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
    /**
     * The Bang count is a local cache of the bang count through the AST.
     *
     * @since 1.3.0
     */
    @Jni2RustField(ignore = true)
    protected Optional<Integer> bangCount;
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr left;
    /**
     * The Logical operator count is a local cache of the logical operator count through the AST.
     *
     * @since 1.3.0
     */
    @Jni2RustField(ignore = true)
    protected Optional<Integer> logicalOperatorCount;
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
        resetBangCount();
        resetLogicalOperatorCount();
        setLeft(left);
        setOp(op);
        setRight(right);
    }

    public static Swc4jAstBinExpr create(Swc4jAstBinaryOp op, ISwc4jAstExpr left, ISwc4jAstExpr right) {
        return new Swc4jAstBinExpr(op, left, right, Swc4jSpan.DUMMY);
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
                    // true+0 => 1, 0+false => 0, true+true => 2, 1+1 => 2
                    double value = left.as(ISwc4jAstCoercionPrimitive.class).asDouble()
                            + right.as(ISwc4jAstCoercionPrimitive.class).asDouble();
                    return Optional.of(Swc4jAstNumber.create(value));
                } else if ((leftType.isBool() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isBool()) ||
                        (leftType.isArrayLit() && rightType.isArrayLit()) ||
                        (leftType.isStr() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isStr()) ||
                        (leftType.isStr() && rightType.isStr()) ||
                        (leftType.isRegex() && rightType.isArrayLit()) ||
                        (leftType.isArrayLit() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isStr()) ||
                        (leftType.isStr() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isBool()) ||
                        (leftType.isBool() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isNumber()) ||
                        (leftType.isNumber() && rightType.isRegex()) ||
                        (leftType.isRegex() && rightType.isRegex()) ||
                        (leftType.isStr() && rightType.isNumber()) ||
                        (leftType.isStr() && rightType.isBool()) ||
                        (leftType.isBool() && rightType.isStr()) ||
                        (leftType.isArrayLit() && rightType.isNumber()) ||
                        (leftType.isNumber() && rightType.isStr()) ||
                        (leftType.isNumber() && rightType.isArrayLit())) {
                    // true+[] => 'true', true+'' => 'true', [0]+/a/i => '0/a/i'
                    String value = left.as(ISwc4jAstCoercionPrimitive.class).asString()
                            + right.as(ISwc4jAstCoercionPrimitive.class).asString();
                    return Optional.of(Swc4jAstStr.create(value));
                } else if (left.isNaN()) {
                    if (rightType.isNumber() || rightType.isBool()) {
                        // NaN+0 => NaN, NaN+true => NaN
                        return Optional.of(Swc4jAstNumber.createNaN());
                    } else if (rightType.isStr() || rightType.isArrayLit()) {
                        // NaN+'a' => 'NaNa', NaN+['a','b'] => 'NaNa,b'
                        return Optional.of(Swc4jAstStr.create(ISwc4jConstants.NAN + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                    } else if (rightType.isMemberExpr()) {
                        return right.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(rightString -> Swc4jAstStr.create(ISwc4jConstants.NAN + rightString));
                    } else if (right.isNaN() || right.isUndefined() || right.isInfinity()) {
                        // NaN+NaN => NaN, NaN+undefined => NaN, NaN+Infinity => NaN
                        return Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (left.isInfinity()) {
                    if (rightType.isNumber() || rightType.isBool()) {
                        // Infinity+0 => Infinity, Infinity+true => Infinity
                        return Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (rightType.isStr() || rightType.isArrayLit()) {
                        // Infinity+'a' => 'Infinitya', Infinity+[0] => 'Infinity0'
                        return Optional.of(Swc4jAstStr.create(ISwc4jConstants.INFINITY + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                    } else if (rightType.isMemberExpr()) {
                        return right.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(rightString -> Swc4jAstStr.create(ISwc4jConstants.INFINITY + rightString));
                    } else if (right.isInfinity()) {
                        // Infinity+Infinity => Infinity
                        return Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (right.isNaN() || right.isUndefined()) {
                        // Infinity+NaN => NaN, Infinity+undefined => NaN
                        return Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (left.isUndefined()) {
                    if (rightType.isNumber() || rightType.isBool()) {
                        // undefined+0 => NaN, undefined+true => NaN
                        return Optional.of(Swc4jAstNumber.createNaN());
                    } else if (rightType.isStr() || rightType.isArrayLit()) {
                        // undefined+'a' = 'undefineda', undefined+[0] => 'undefined0'
                        return Optional.of(Swc4jAstStr.create(ISwc4jConstants.UNDEFINED + right.as(ISwc4jAstCoercionPrimitive.class).asString()));
                    } else if (right.isNaN() || right.isUndefined() || right.isInfinity()) {
                        // undefined+NaN => NaN, undefined+undefined => NaN, undefined+Infinity => NaN
                        return Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (right.isNaN()) {
                    if (leftType.isNumber() || leftType.isBool()) {
                        return Optional.of(Swc4jAstNumber.createNaN());
                    } else if (leftType.isStr() || leftType.isArrayLit()) {
                        return Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.NAN));
                    } else if (leftType.isMemberExpr()) {
                        return left.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(leftString -> Swc4jAstStr.create(leftString + ISwc4jConstants.NAN));
                    } else if (left.isNaN() || left.isUndefined() || left.isInfinity()) {
                        return Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (right.isInfinity()) {
                    if (leftType.isNumber() || leftType.isBool()) {
                        return Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (leftType.isStr() || leftType.isArrayLit()) {
                        return Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.INFINITY));
                    } else if (leftType.isMemberExpr()) {
                        return left.as(Swc4jAstMemberExpr.class).evalAsString()
                                .map(leftString -> Swc4jAstStr.create(leftString + ISwc4jConstants.INFINITY));
                    } else if (left.isInfinity()) {
                        return Optional.of(Swc4jAstNumber.createInfinity(true));
                    } else if (left.isNaN() || left.isUndefined()) {
                        return Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if (right.isUndefined()) {
                    if (leftType.isNumber() || leftType.isBool()) {
                        return Optional.of(Swc4jAstNumber.createNaN());
                    } else if (leftType.isStr() || leftType.isArrayLit()) {
                        return Optional.of(Swc4jAstStr.create(left.as(ISwc4jAstCoercionPrimitive.class).asString() + ISwc4jConstants.UNDEFINED));
                    } else if (left.isNaN() || left.isUndefined() || left.isInfinity()) {
                        return Optional.of(Swc4jAstNumber.createNaN());
                    }
                } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isCallExpr()) {
                    return right.as(Swc4jAstCallExpr.class).eval()
                            .map(n -> left.as(ISwc4jAstCoercionPrimitive.class).asString() + n)
                            .map(Swc4jAstStr::create);
                } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isCallExpr()) {
                    return left.as(Swc4jAstCallExpr.class).eval()
                            .map(n -> n + right.as(ISwc4jAstCoercionPrimitive.class).asString())
                            .map(Swc4jAstStr::create);
                } else if ((leftType.isPrimitive() || leftType.isArrayLit()) && rightType.isMemberExpr()) {
                    return right.as(Swc4jAstMemberExpr.class).evalAsString()
                            .map(rightString -> left.as(ISwc4jAstCoercionPrimitive.class).asString() + rightString)
                            .map(Swc4jAstStr::create);
                } else if ((rightType.isPrimitive() || rightType.isArrayLit()) && leftType.isMemberExpr()) {
                    return left.as(Swc4jAstMemberExpr.class).evalAsString()
                            .map(leftString -> leftString + right.as(ISwc4jAstCoercionPrimitive.class).asString())
                            .map(Swc4jAstStr::create);
                }
                break;
            default:
                break;
        }
        return super.eval();
    }

    protected int getBangCount(ISwc4jAst ast) {
        switch (ast.getType()) {
            case BinExpr:
                Swc4jAstBinExpr binExpr = ast.as(Swc4jAstBinExpr.class);
                if (binExpr.getOp().isLogicalOperator()) {
                    return binExpr.getBangCount();
                }
                return 0;
            case ParenExpr:
                return getBangCount(ast.getParent());
            case UnaryExpr:
                if (ast.as(Swc4jAstUnaryExpr.class).getOp() == Swc4jAstUnaryOp.Bang) {
                    return getBangCount(ast.getParent()) + 1;
                }
                return 0;
            default:
                return 0;
        }
    }

    public int getBangCount() {
        if (!bangCount.isPresent()) {
            bangCount = Optional.of(getBangCount(getParent()));
        }
        return bangCount.get();
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(left, right);
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getLeft() {
        return left;
    }

    public int getLogicalOperatorCount() {
        if (!logicalOperatorCount.isPresent()) {
            logicalOperatorCount = Optional.of(getLogicalOperatorCount(getParent()));
        }
        return logicalOperatorCount.get();
    }

    protected int getLogicalOperatorCount(ISwc4jAst ast) {
        switch (ast.getType()) {
            case BinExpr:
                Swc4jAstBinExpr binExpr = ast.as(Swc4jAstBinExpr.class);
                if (binExpr.getOp().isLogicalOperator()) {
                    return binExpr.getLogicalOperatorCount() + 1;
                }
                return 0;
            case ParenExpr:
                return getLogicalOperatorCount(ast.getParent());
            default:
                return 0;
        }
    }

    @Jni2RustMethod
    public Swc4jAstBinaryOp getOp() {
        return op;
    }

    public Swc4jAstBinExpr getParentBinExpr() {
        return getParentBinExpr(getParent());
    }

    protected Swc4jAstBinExpr getParentBinExpr(ISwc4jAst ast) {
        switch (ast.getType()) {
            case BinExpr:
                return ast.as(Swc4jAstBinExpr.class);
            case ParenExpr:
                return getParentBinExpr(ast.getParent());
            default:
                return null;
        }
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

    public Swc4jAstBinExpr resetBangCount() {
        bangCount = Optional.empty();
        return this;
    }

    public Swc4jAstBinExpr resetLogicalOperatorCount() {
        logicalOperatorCount = Optional.empty();
        return this;
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
