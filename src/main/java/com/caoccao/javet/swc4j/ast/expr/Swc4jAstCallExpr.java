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
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstRegex;
import com.caoccao.javet.swc4j.ast.expr.lit.Swc4jAstStr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstCallee;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;
import com.caoccao.javet.swc4j.utils.SimpleSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstCallExpr
        extends Swc4jAst
        implements ISwc4jAstExpr {
    public static final String FONTCOLOR = "fontcolor";
    public static final String ITALICS = "italics";
    public static final Set<String> BUILT_IN_FUNCTION_SET = SimpleSet.immutableOf(FONTCOLOR, ITALICS);
    protected static final Swc4jParseOptions PARSE_OPTIONS = new Swc4jParseOptions()
            .setCaptureAst(true)
            .setMediaType(Swc4jMediaType.JavaScript);
    protected static final Swc4j SWC4J = new Swc4j();
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

    @Override
    public Optional<ISwc4jAst> eval() {
        switch (callee.getType()) {
            case MemberExpr:
                String specialCall = null;
                Swc4jAstMemberExpr memberExpr = callee.as(Swc4jAstMemberExpr.class);
                if (memberExpr.getProp() instanceof Swc4jAstComputedPropName) {
                    Swc4jAstComputedPropName prop = memberExpr.getProp().as(Swc4jAstComputedPropName.class);
                    ISwc4jAstExpr expr = prop.getExpr().unParenExpr();
                    if (expr instanceof Swc4jAstStr) {
                        specialCall = expr.as(Swc4jAstStr.class).getValue();
                    }
                } else if (memberExpr.getProp() instanceof Swc4jAstIdent) {
                    specialCall = memberExpr.getProp().as(Swc4jAstIdent.class).getSym();
                }
                if (specialCall != null) {
                    if (BUILT_IN_FUNCTION_SET.contains(specialCall)) {
                        ISwc4jAstExpr obj = memberExpr.getObj().unParenExpr();
                        if (obj instanceof Swc4jAstStr) {
                            String objString = obj.as(Swc4jAstStr.class).getValue();
                            if (FONTCOLOR.equals(specialCall)) {
                                String argString = args.isEmpty() ? Swc4jAstIdent.UNDEFINED : args.get(0).toString();
                                return Optional.of(Swc4jAstStr.create("<font color=\"" + argString + "\">" + objString + "</font>"));
                            } else if (ITALICS.equals(specialCall)) {
                                return Optional.of(Swc4jAstStr.create("<i>" + objString + "</i>"));
                            }
                        }
                    } else if (Swc4jAstMemberExpr.CONSTRUCTOR.equals(specialCall)) {
                        if (memberExpr.getObj() instanceof Swc4jAstRegex) {
                            switch (args.size()) {
                                case 0:
                                    return Optional.of(Swc4jAstRegex.create());
                                case 1:
                                    ISwc4jAstExpr arg = args.get(0).getExpr().unParenExpr();
                                    if (arg instanceof Swc4jAstStr) {
                                        return Optional.of(Swc4jAstRegex.create(arg.as(Swc4jAstStr.class).getValue()));
                                    }
                                    break;
                                default:
                                    ISwc4jAstExpr arg1 = args.get(0).getExpr().unParenExpr();
                                    ISwc4jAstExpr arg2 = args.get(1).getExpr().unParenExpr();
                                    if (arg1 instanceof Swc4jAstStr && arg2 instanceof Swc4jAstStr) {
                                        return Optional.of(Swc4jAstRegex.create(
                                                arg1.as(Swc4jAstStr.class).getValue(),
                                                arg2.as(Swc4jAstStr.class).getValue()));
                                    }
                                    break;
                            }
                        }
                    }
                }
                break;
            case CallExpr:
                Swc4jAstCallExpr callExpr = callee.as(Swc4jAstCallExpr.class);
                if (callExpr.getCallee() instanceof Swc4jAstIdent && args.isEmpty()) {
                    Swc4jAstIdent ident = callExpr.getCallee().as(Swc4jAstIdent.class);
                    if (Swc4jAstFunction.CONSTRUCTOR.equals(ident.getSym()) && callExpr.getArgs().size() == 1) {
                        ISwc4jAstExpr expr = callExpr.getArgs().get(0).getExpr().unParenExpr();
                        if (expr instanceof Swc4jAstStr) {
                            String code = expr.as(Swc4jAstStr.class).getValue();
                            try {
                                Swc4jParseOutput output = SWC4J.parse(code, PARSE_OPTIONS);
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
