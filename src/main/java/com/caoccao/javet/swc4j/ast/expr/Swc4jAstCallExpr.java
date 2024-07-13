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

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstArrayLit;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstNumber;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstRegex;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCoercionPrimitive;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.constants.ISwc4jConstants;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstCallExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    protected static final Swc4jParseOptions PARSE_OPTIONS = new Swc4jParseOptions()
            .setCaptureAst(true)
            .setMediaType(Swc4jMediaType.JavaScript);
    /**
     * The constant swc4j is for evaluating the expression.
     *
     * @since 0.10.0
     */
    protected static Swc4j swc4j;
    protected final List<Swc4jAstExprOrSpread> args;
    protected ISwc4jAstCallee callee;
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    @Jni2RustMethod
    public Swc4jAstCallExpr(
            ISwc4jAstCallee callee,
            List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jSpan span) {
        super(span);
        setCallee(callee);
        setTypeArgs(typeArgs);
        this.args = AssertionUtils.notNull(args, "Args");
        this.args.forEach(node -> node.setParent(this));
    }

    public static Swc4jAstCallExpr create(ISwc4jAstCallee callee) {
        return create(callee, SimpleList.of());
    }

    public static Swc4jAstCallExpr create(ISwc4jAstCallee callee, List<Swc4jAstExprOrSpread> args) {
        return create(callee, args, null);
    }

    public static Swc4jAstCallExpr create(
            ISwc4jAstCallee callee,
            List<Swc4jAstExprOrSpread> args,
            Swc4jAstTsTypeParamInstantiation typeArgs) {
        return new Swc4jAstCallExpr(callee, args, typeArgs, Swc4jSpan.DUMMY);
    }

    protected static Swc4j getSwc4j() {
        if (swc4j == null) {
            swc4j = new Swc4j();
        }
        return swc4j;
    }

    @Override
    public Optional<ISwc4jAst> eval() {
        switch (callee.getType()) {
            case MemberExpr:
                Swc4jAstMemberExpr memberExpr = callee.as(Swc4jAstMemberExpr.class);
                Optional<String> call = memberExpr.evalAsCall();
                if (call.isPresent()) {
                    ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                    switch (obj.getType()) {
                        case Str:
                            Swc4jAstStr objString = obj.as(Swc4jAstStr.class);
                            if (ISwc4jConstants.FONTCOLOR.equals(call.get())) {
                                String argString = ISwc4jConstants.UNDEFINED;
                                if (!args.isEmpty()) {
                                    Swc4jAstExprOrSpread exprOrSpread = args.get(0);
                                    ISwc4jAstExpr expr = exprOrSpread.getExpr().unParenExpr();
                                    if (expr instanceof Swc4jAstStr || expr instanceof Swc4jAstNumber) {
                                        argString = expr.as(ISwc4jAstCoercionPrimitive.class).asString();
                                    } else if (expr.isNaN()) {
                                        argString = ISwc4jConstants.NAN;
                                    } else if (expr.isInfinity()) {
                                        argString = ISwc4jConstants.INFINITY;
                                    } else {
                                        return Optional.empty();
                                    }
                                }
                                return Optional.of(Swc4jAstStr.create(objString.fontcolor(argString)));
                            } else if (ISwc4jConstants.ITALICS.equals(call.get())) {
                                return Optional.of(Swc4jAstStr.create(objString.italics()));
                            } else if (ISwc4jConstants.SPLIT.equals(call.get())) {
                                switch (args.size()) {
                                    case 0:
                                        return Optional.of(Swc4jAstArrayLit.create(objString.split().stream()
                                                .map(str -> str == null ? null : Swc4jAstExprOrSpread.create(Swc4jAstStr.create(str)))
                                                .collect(Collectors.toList())));
                                    case 1:
                                        ISwc4jAstExpr arg = args.get(0).getExpr().unParenExpr();
                                        if (arg instanceof ISwc4jAstCoercionPrimitive) {
                                            String separator = arg.as(ISwc4jAstCoercionPrimitive.class).asString();
                                            return Optional.of(Swc4jAstArrayLit.create(objString.split(separator).stream()
                                                    .map(str -> str == null ? null : Swc4jAstExprOrSpread.create(Swc4jAstStr.create(str)))
                                                    .collect(Collectors.toList())));
                                        }
                                        break;
                                    default:
                                        ISwc4jAstExpr arg1 = args.get(0).getExpr().unParenExpr();
                                        ISwc4jAstExpr arg2 = args.get(1).getExpr().unParenExpr();
                                        if (arg1 instanceof ISwc4jAstCoercionPrimitive &&
                                                arg2 instanceof ISwc4jAstCoercionPrimitive) {
                                            String separator = arg1.as(ISwc4jAstCoercionPrimitive.class).asString();
                                            int limit = arg2.as(ISwc4jAstCoercionPrimitive.class).asInt();
                                            return Optional.of(Swc4jAstArrayLit.create(objString.split(separator, limit).stream()
                                                    .map(str -> str == null ? null : Swc4jAstExprOrSpread.create(Swc4jAstStr.create(str)))
                                                    .collect(Collectors.toList())));
                                        }
                                        break;
                                }
                            } else if (ISwc4jConstants.SLICE.equals(call.get())) {
                                switch (args.size()) {
                                    case 0:
                                        return Optional.of(Swc4jAstStr.create(objString.slice()));
                                    case 1:
                                        ISwc4jAstExpr arg = args.get(0).getExpr().unParenExpr();
                                        if (arg instanceof ISwc4jAstCoercionPrimitive) {
                                            int indexStart = arg.as(ISwc4jAstCoercionPrimitive.class).asInt();
                                            return Optional.of(Swc4jAstStr.create(objString.slice(indexStart)));
                                        }
                                        break;
                                    default:
                                        ISwc4jAstExpr arg1 = args.get(0).getExpr().unParenExpr();
                                        ISwc4jAstExpr arg2 = args.get(1).getExpr().unParenExpr();
                                        if (arg1 instanceof ISwc4jAstCoercionPrimitive &&
                                                arg2 instanceof ISwc4jAstCoercionPrimitive) {
                                            int indexStart = arg1.as(ISwc4jAstCoercionPrimitive.class).asInt();
                                            int indexEnd = arg2.as(ISwc4jAstCoercionPrimitive.class).asInt();
                                            return Optional.of(Swc4jAstStr.create(objString.slice(indexStart, indexEnd)));
                                        }
                                        break;
                                }
                            }
                            break;
                        case ArrayLit:
                            Swc4jAstArrayLit objArrayLit = obj.as(Swc4jAstArrayLit.class);
                            if (ISwc4jConstants.CONCAT.equals(call.get())) {
                                if (!args.isEmpty()) {
                                    ISwc4jAstExpr expr = args.get(0).getExpr().unParenExpr();
                                    if (expr instanceof Swc4jAstArrayLit) {
                                        Swc4jAstArrayLit rightArrayLit = expr.as(Swc4jAstArrayLit.class);
                                        objArrayLit.concat(rightArrayLit);
                                        return Optional.of(objArrayLit);
                                    }
                                }
                            } else if (ISwc4jConstants.JOIN.equals(call.get())) {
                                if (objArrayLit.isAllPrimitive()) {
                                    String separator = null;
                                    if (!args.isEmpty()) {
                                        ISwc4jAstExpr expr = args.get(0).getExpr().unParenExpr();
                                        if (!expr.isUndefined() && expr instanceof ISwc4jAstCoercionPrimitive) {
                                            separator = expr.as(ISwc4jAstCoercionPrimitive.class).asString();
                                        }
                                    }
                                    return Optional.of(Swc4jAstStr.create(objArrayLit.join(separator)));
                                }
                            } else {
                                return Optional.of(Swc4jAstStr.create(Swc4jAstArrayLit.ARRAY_FUNCTION_STRING_MAP
                                        .getOrDefault(call.get(), ISwc4jConstants.UNDEFINED)));
                            }
                            break;
                        case Regex:
                            if (ISwc4jConstants.CONSTRUCTOR.equals(call.get())) {
                                switch (args.size()) {
                                    case 0:
                                        return Optional.of(Swc4jAstRegex.create());
                                    case 1:
                                        ISwc4jAstExpr arg = args.get(0).getExpr().unParenExpr();
                                        if (arg instanceof Swc4jAstStr) {
                                            return Optional.of(Swc4jAstRegex.create(
                                                    Swc4jAstRegex.escape(arg.as(Swc4jAstStr.class).getValue())));
                                        }
                                        break;
                                    default:
                                        ISwc4jAstExpr arg1 = args.get(0).getExpr().unParenExpr();
                                        ISwc4jAstExpr arg2 = args.get(1).getExpr().unParenExpr();
                                        if (arg1 instanceof Swc4jAstStr && arg2 instanceof Swc4jAstStr) {
                                            return Optional.of(Swc4jAstRegex.create(
                                                    Swc4jAstRegex.escape(arg1.as(Swc4jAstStr.class).getValue()),
                                                    arg2.as(Swc4jAstStr.class).getValue()));
                                        }
                                        break;
                                }
                            }
                            break;
                        case Number:
                            if (ISwc4jConstants.TO_STRING.equals(call.get())) {
                                int radix = 10;
                                if (!args.isEmpty()) {
                                    ISwc4jAstExpr arg = args.get(0).getExpr().unParenExpr();
                                    if (arg instanceof Swc4jAstNumber || arg instanceof Swc4jAstStr) {
                                        radix = arg.as(ISwc4jAstCoercionPrimitive.class).asInt();
                                    }
                                }
                                return Optional.of(Swc4jAstStr.create(obj.as(Swc4jAstNumber.class).toString(radix)));
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            case CallExpr:
                Swc4jAstCallExpr callExpr = callee.as(Swc4jAstCallExpr.class);
                if (callExpr.getCallee() instanceof Swc4jAstIdent && args.isEmpty()) {
                    Swc4jAstIdent ident = callExpr.getCallee().as(Swc4jAstIdent.class);
                    if (ISwc4jConstants.FUNCTION.equals(ident.getSym()) && callExpr.getArgs().size() == 1) {
                        ISwc4jAstExpr expr = callExpr.getArgs().get(0).getExpr().unParenExpr();
                        if (expr instanceof Swc4jAstStr) {
                            String code = expr.as(Swc4jAstStr.class).getValue();
                            try {
                                Swc4jParseOutput output = getSwc4j().parse(code, PARSE_OPTIONS);
                                List<? extends ISwc4jAst> body = output.getProgram().getBody();
                                if (body.size() == 1 && body.get(0) instanceof Swc4jAstReturnStmt) {
                                    Swc4jAstReturnStmt returnStmt = body.get(0).as(Swc4jAstReturnStmt.class);
                                    return Optional.of(returnStmt.getArg().orElse(Swc4jAstIdent.createUndefined()));
                                }
                            } catch (Swc4jCoreException ignored) {
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.eval();
    }

    @Jni2RustMethod
    public List<Swc4jAstExprOrSpread> getArgs() {
        return args;
    }

    @Jni2RustMethod
    public ISwc4jAstCallee getCallee() {
        return callee;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(args);
        childNodes.add(callee);
        typeArgs.ifPresent(childNodes::add);
        return childNodes;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.CallExpr;
    }

    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    public boolean isSpreadPresent() {
        return args.stream().anyMatch(arg -> arg.getSpread().isPresent());
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!args.isEmpty() && newNode instanceof Swc4jAstExprOrSpread) {
            final int size = args.size();
            for (int i = 0; i < size; i++) {
                if (args.get(i) == oldNode) {
                    args.set(i, (Swc4jAstExprOrSpread) newNode);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (callee == oldNode && newNode instanceof ISwc4jAstCallee) {
            setCallee((ISwc4jAstCallee) newNode);
            return true;
        }
        if (typeArgs.isPresent() && typeArgs.get() == oldNode && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setTypeArgs((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        return false;
    }

    public Swc4jAstCallExpr setCallee(ISwc4jAstCallee callee) {
        this.callee = AssertionUtils.notNull(callee, "Callee");
        this.callee.setParent(this);
        return this;
    }

    public Swc4jAstCallExpr setTypeArgs(Swc4jAstTsTypeParamInstantiation typeArgs) {
        this.typeArgs = Optional.ofNullable(typeArgs);
        this.typeArgs.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        switch (visitor.visitCallExpr(this)) {
            case Error:
                return Swc4jAstVisitorResponse.Error;
            case OkAndBreak:
                return Swc4jAstVisitorResponse.OkAndContinue;
            default:
                return super.visit(visitor);
        }
    }
}
