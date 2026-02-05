/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.swc4j.ast.miscs;

import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.enums.Swc4jAstType;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstExprOrSpread;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstOptChainBase;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeParamInstantiation;
import com.caoccao.javet.swc4j.ast.visitors.ISwc4jAstVisitor;
import com.caoccao.javet.swc4j.ast.visitors.Swc4jAstVisitorResponse;
import com.caoccao.javet.swc4j.jni2rust.*;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.swc4j.utils.AssertionUtils;
import com.caoccao.javet.swc4j.utils.SimpleList;

import java.util.List;
import java.util.Optional;

/**
 * The type swc4j ast opt call.
 */
@Jni2RustClass(filePath = Jni2RustFilePath.AstUtils)
public class Swc4jAstOptCall
        extends Swc4jAst
        implements ISwc4jAstOptChainBase {
    /**
     * The Args.
     */
    protected final List<Swc4jAstExprOrSpread> args;
    /**
     * The Callee.
     */
    @Jni2RustField(box = true)
    protected ISwc4jAstExpr callee;
    /**
     * The Ctxt.
     */
    @Jni2RustField(syntaxContext = true)
    protected int ctxt;
    /**
     * The Type args.
     */
    @Jni2RustField(componentBox = true)
    protected Optional<Swc4jAstTsTypeParamInstantiation> typeArgs;

    /**
     * Instantiates a new swc4j ast opt call.
     *
     * @param ctxt     the ctxt
     * @param callee   the callee
     * @param args     the args
     * @param typeArgs the type args
     * @param span     the span
     */
    @Jni2RustMethod
    public Swc4jAstOptCall(
            @Jni2RustParam(syntaxContext = true) int ctxt,
            ISwc4jAstExpr callee,
            List<Swc4jAstExprOrSpread> args,
            @Jni2RustParam(optional = true) Swc4jAstTsTypeParamInstantiation typeArgs,
            Swc4jSpan span) {
        super(span);
        setCallee(callee);
        setCtxt(ctxt);
        setTypeArgs(typeArgs);
        this.args = AssertionUtils.notNull(args, "Args");
        this.args.forEach(node -> node.setParent(this));
    }

    /**
     * Create swc4j ast opt call.
     *
     * @param callee the callee
     * @return the swc4j ast opt call
     */
    public static Swc4jAstOptCall create(ISwc4jAstExpr callee) {
        return create(callee, SimpleList.of());
    }

    /**
     * Create swc4j ast opt call.
     *
     * @param callee the callee
     * @param args   the args
     * @return the swc4j ast opt call
     */
    public static Swc4jAstOptCall create(ISwc4jAstExpr callee, List<Swc4jAstExprOrSpread> args) {
        return create(callee, args, null);
    }

    /**
     * Create swc4j ast opt call.
     *
     * @param callee   the callee
     * @param args     the args
     * @param typeArgs the type args
     * @return the swc4j ast opt call
     */
    public static Swc4jAstOptCall create(
            ISwc4jAstExpr callee,
            List<Swc4jAstExprOrSpread> args,
            Swc4jAstTsTypeParamInstantiation typeArgs) {
        return create(0, callee, args, typeArgs);
    }

    /**
     * Create swc4j ast opt call.
     *
     * @param ctxt     the ctxt
     * @param callee   the callee
     * @param args     the args
     * @param typeArgs the type args
     * @return the swc4j ast opt call
     */
    public static Swc4jAstOptCall create(
            int ctxt,
            ISwc4jAstExpr callee,
            List<Swc4jAstExprOrSpread> args,
            Swc4jAstTsTypeParamInstantiation typeArgs) {
        return new Swc4jAstOptCall(ctxt, callee, args, typeArgs, Swc4jSpan.DUMMY);
    }

    /**
     * Gets args.
     *
     * @return the args
     */
    @Jni2RustMethod
    public List<Swc4jAstExprOrSpread> getArgs() {
        return args;
    }

    /**
     * Gets callee.
     *
     * @return the callee
     */
    @Jni2RustMethod
    public ISwc4jAstExpr getCallee() {
        return callee;
    }

    @Override
    public List<ISwc4jAst> getChildNodes() {
        List<ISwc4jAst> childNodes = SimpleList.copyOf(args);
        childNodes.add(callee);
        typeArgs.ifPresent(childNodes::add);
        return childNodes;
    }

    /**
     * Gets ctxt.
     *
     * @return the ctxt
     */
    @Jni2RustMethod
    public int getCtxt() {
        return ctxt;
    }

    @Override
    public Swc4jAstType getType() {
        return Swc4jAstType.OptCall;
    }

    /**
     * Gets type args.
     *
     * @return the type args
     */
    @Jni2RustMethod
    public Optional<Swc4jAstTsTypeParamInstantiation> getTypeArgs() {
        return typeArgs;
    }

    /**
     * Is spread present boolean.
     *
     * @return the boolean
     */
    public boolean isSpreadPresent() {
        return args.stream().anyMatch(arg -> arg.getSpread().isPresent());
    }

    @Override
    public boolean replaceNode(ISwc4jAst oldNode, ISwc4jAst newNode) {
        if (!args.isEmpty() && newNode instanceof Swc4jAstExprOrSpread newArg) {
            final int size = args.size();
            for (int i = 0; i < size; i++) {
                if (args.get(i) == oldNode) {
                    args.set(i, newArg);
                    newNode.setParent(this);
                    return true;
                }
            }
        }
        if (callee == oldNode && newNode instanceof ISwc4jAstExpr newCallee) {
            setCallee(newCallee);
            return true;
        }
        if (typeArgs.map(node -> node == oldNode).orElse(oldNode == null)
                && (newNode == null || newNode instanceof Swc4jAstTsTypeParamInstantiation)) {
            setTypeArgs((Swc4jAstTsTypeParamInstantiation) newNode);
            return true;
        }
        return false;
    }

    /**
     * Sets callee.
     *
     * @param callee the callee
     * @return the callee
     */
    public Swc4jAstOptCall setCallee(ISwc4jAstExpr callee) {
        this.callee = AssertionUtils.notNull(callee, "Callee");
        this.callee.setParent(this);
        return this;
    }

    /**
     * Sets ctxt.
     *
     * @param ctxt the ctxt
     * @return the ctxt
     */
    public Swc4jAstOptCall setCtxt(int ctxt) {
        this.ctxt = ctxt;
        return this;
    }

    /**
     * Sets type args.
     *
     * @param typeArgs the type args
     * @return the type args
     */
    public Swc4jAstOptCall setTypeArgs(Swc4jAstTsTypeParamInstantiation typeArgs) {
        this.typeArgs = Optional.ofNullable(typeArgs);
        this.typeArgs.ifPresent(node -> node.setParent(this));
        return this;
    }

    @Override
    public Swc4jAstVisitorResponse visit(ISwc4jAstVisitor visitor) {
        return switch (visitor.visitOptCall(this)) {
            case Error -> Swc4jAstVisitorResponse.Error;
            case OkAndBreak -> Swc4jAstVisitorResponse.OkAndContinue;
            default -> super.visit(visitor);
        };
    }
}
