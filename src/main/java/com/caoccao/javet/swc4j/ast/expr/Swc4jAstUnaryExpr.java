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
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstUnaryOp;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstBool;
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
public class Swc4jAstUnaryExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr arg;
    protected Swc4jAstUnaryOp op;

    @Jni2RustMethod
    public Swc4jAstUnaryExpr(
            Swc4jAstUnaryOp op,
            ISwc4jAstExpr arg,
            Swc4jSpan span) {
        super(span);
        setArg(arg);
        setOp(op);
    }

    @Override
    public Optional<ISwc4jAst> eval() {
        ISwc4jAstExpr arg = this.arg.unParenExpr();
        switch (op) {
            case Bang:
                switch (arg.getType()) {
                    case ArrayLit:
                    case ObjectLit:
                        return Optional.of(Swc4jAstBool.create(false));
                    case Bool:
                    case Number:
                    case Str:
                        return Optional.of(Swc4jAstBool.create(!arg.as(ISwc4jAstCoercionPrimitive.class).asBoolean()));
                    default:
                        break;
                }
                break;
            case Minus:
                switch (arg.getType()) {
                    case ArrayLit:
                        return Optional.of(Swc4jAstNumber.create(-arg.as(Swc4jAstArrayLit.class).asDouble()));
                    case Bool:
                        return Optional.of(Swc4jAstNumber.create(-arg.as(ISwc4jAstCoercionPrimitive.class).asInt()));
                    case Ident:
                        Swc4jAstIdent ident = arg.as(Swc4jAstIdent.class);
                        if (ISwc4jConstants.NAN.equals(ident.getSym())) {
                            return Optional.of(Swc4jAstNumber.createNaN());
                        } else if (ISwc4jConstants.INFINITY.equals(ident.getSym())) {
                            return Optional.of(Swc4jAstNumber.createInfinity(false));
                        }
                        break;
                    case Number:
                        Swc4jAstNumber number = arg.as(Swc4jAstNumber.class);
                        return Optional.of(Swc4jAstNumber.create(-number.getValue(), number.getRaw().map(n -> "-" + n).orElse(null)));
                    case ObjectLit:
                        return Optional.of(Swc4jAstNumber.createNaN());
                    case Str: {
                        try {
                            return Optional.of(Swc4jAstNumber.create(
                                    -Double.parseDouble(arg.as(Swc4jAstStr.class).getValue())));
                        } catch (Throwable t) {
                            return Optional.of(Swc4jAstNumber.createNaN());
                        }
                    }
                    default:
                        break;
                }
                break;
            case Plus:
                switch (arg.getType()) {
                    case ArrayLit:
                        return Optional.of(Swc4jAstNumber.create(arg.as(Swc4jAstArrayLit.class).asDouble()));
                    case Bool:
                        return Optional.of(Swc4jAstNumber.create(arg.as(ISwc4jAstCoercionPrimitive.class).asInt()));
                    case Ident:
                        Swc4jAstIdent ident = arg.as(Swc4jAstIdent.class);
                        if (ISwc4jConstants.NAN.equals(ident.getSym())) {
                            return Optional.of(Swc4jAstNumber.createNaN());
                        } else if (ISwc4jConstants.INFINITY.equals(ident.getSym())) {
                            return Optional.of(Swc4jAstNumber.createInfinity(true));
                        }
                        break;
                    case Number:
                        Swc4jAstNumber number = arg.as(Swc4jAstNumber.class);
                        return Optional.of(Swc4jAstNumber.create(number.getValue(), number.getRaw().orElse(null)));
                    case ObjectLit:
                        return Optional.of(Swc4jAstNumber.createNaN());
                    case Str: {
                        try {
                            return Optional.of(Swc4jAstNumber.create(
                                    Double.parseDouble(arg.as(Swc4jAstStr.class).getValue())));
                        } catch (Throwable t) {
                            return Optional.of(Swc4jAstNumber.createNaN());
                        }
                    }
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return super.eval();
    }

    @Jni2RustMethod
    public ISwc4jAstExpr getArg() {
        return arg;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        return SimpleList.of(arg);
    }

    @Jni2RustMethod
    public Swc4jAstUnaryOp getOp() {
        return op;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.UnaryExpr;
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (arg == oldNode && newNode instanceof ISwc4jAstExpr) {
            setArg((ISwc4jAstExpr) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstUnaryExpr setArg(ISwc4jAstExpr arg) {
        this.arg = AssertionUtils.notNull(arg, "Arg");
        this.arg.setParent(this);
        return this;
    }

    public Swc4jAstUnaryExpr setOp(Swc4jAstUnaryOp op) {
        this.op = AssertionUtils.notNull(op, "Op");
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitUnaryExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
